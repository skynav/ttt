/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY SKYNAV, INC. AND ITS CONTRIBUTORS “AS IS” AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL SKYNAV, INC. OR ITS CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.skynav.ttpe.fonts;

import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.fontbox.cff.CFFCIDFont;
import org.apache.fontbox.cff.CFFCharset;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.Type1CharString;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.util.BoundingBox;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.util.Reporter;

public class FontState {

    private static final int                    GLYPHS_CACHE_SIZE               = 16;

    private String source;
    private Reporter reporter;
    private OpenTypeFont otf;
    private boolean otfLoadFailed;
    private NamingTable nameTable;
    private OS2WindowsMetricsTable os2Table;
    private CmapSubtable cmapSubtable;
    private KerningSubtable kerningSubtable;
    private GlyphTable glyphTable;
    private CFFTable cffTable;
    private Deque<GlyphMapping> glyphs = new java.util.ArrayDeque<GlyphMapping>(GLYPHS_CACHE_SIZE);
    private Map<Integer,Integer> mappedGlyphs = new java.util.HashMap<Integer,Integer>();
    private Set<Integer> mappingFailures;

    public FontState(String source, Reporter reporter) {
        this.source = source;
        this.reporter = reporter;
    }

    public String getPreferredFamilyName(FontKey key) {
        if (maybeLoad(key)) {
            if (nameTable != null) {
                String name = nameTable.getName(16, 1, 0, 0);
                if (name == null)
                    name = nameTable.getFontFamily();
                return name;
            } else
                return key.family;
        } else
            return "unknown";
    }

    public double getLeading(FontKey key) {
        if (maybeLoad(key))
            return scaleFontUnits(key, os2Table.getTypoLineGap());
        else
            return 0;
    }

    public double getAscent(FontKey key) {
        if (maybeLoad(key))
            return scaleFontUnits(key, os2Table.getTypoAscender());
        else
            return 0;
    }

    public double getDescent(FontKey key) {
        if (maybeLoad(key))
            return scaleFontUnits(key, os2Table.getTypoDescender());
        else
            return 0;
    }

    public int[] getGlyphs(String text) {
        return getGlyphs(text, Characters.UC_REPLACEMENT);
    }

    public int[] getGlyphs(String text, int substitution) {
        int[] glyphs;
        if ((glyphs = getCachedGlyphs(text)) != null)
            return glyphs;
        return putCachedGlyphs(text, mapGlyphs(text, substitution));
    }

    public double getAdvance(FontKey key, String text, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        double advance = 0;
        for (double a : getAdvances(key, text, adjustForKerning, rotatedOrientation, cross))
            advance += a;
        return advance;
    }

    public double[] getAdvances(FontKey key, String text, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        boolean vertical = ((key.axis == Axis.VERTICAL) && !cross) || ((key.axis == Axis.HORIZONTAL) && cross);
        double[] advances = new double[text.length()];
        if (maybeLoad(key)) {
            int[] glyphs = getGlyphs(text);
            int[] kerning = (adjustForKerning && (kerningSubtable != null)) ? kerningSubtable.getKerning(glyphs) : null;
            for (int i = 0, n = glyphs.length; i < n; ++i) {
                int g = glyphs[i];
                int c = mappedGlyphs.get(g);
                if (!Characters.isZeroWidthWhitespace(c)) {
                    try {
                        int k = kerning != null ? kerning[i] : 0;
                        advances[i] = scaleFontUnits(key, (double) (((vertical && !rotatedOrientation) ? otf.getAdvanceHeight(g) : otf.getAdvanceWidth(g)) + k));
                    } catch (IOException e) {
                    }
                }
            }
        }
        return advances;
    }

    public double getKerningAdvance(FontKey key, String text) {
        double[] kerning = getKerning(key, text);
        if (kerning != null) {
            double advance = 0;
            for (double k : kerning)
                advance += k;
            return advance;
        } else
            return 0;
    }

    public double[] getKerning(FontKey key, String text) {
        if (maybeLoad(key)) {
            if (kerningSubtable != null) {
                int[] glyphs = getGlyphs(text);
                int[] kerning = kerningSubtable.getKerning(glyphs);
                assert kerning.length == glyphs.length;
                double[] kerningScaled = new double[kerning.length];
                boolean hasNonZeroKerning = false;
                for (int i = 0; i < kerning.length; ++i) {
                    double k = scaleFontUnits(key, (double) kerning[i]);
                    if (k != 0) {
                        kerningScaled[i] = k;
                        hasNonZeroKerning = true;
                    }
                }
                return hasNonZeroKerning ? kerningScaled : null;
            }
        }
        return null;
    }

    public Rectangle[] getGlyphBounds(FontKey key, String text) {
        Rectangle[] bounds = null;
        if (maybeLoad(key)) {
            if (glyphTable != null)
                bounds = getGlyphBounds(key, glyphTable, text);
            else if (cffTable != null)
                bounds = getGlyphBounds(key, cffTable, text);
        }
        if (bounds != null) {
            for (int i = 0, n = bounds.length; i < n; ++i) {
                if (bounds[i] == null)
                    bounds[i] = Rectangle.EMPTY;
            }
        }
        return bounds;
    }

    public Rectangle[] getGlyphBounds(FontKey key, GlyphTable glyphTable, String text) {
        assert glyphTable != null;
        Rectangle[] bounds = new Rectangle[text.length()];
        int[] glyphs = getGlyphs(text);
        for (int i = 0, n = glyphs.length; i < n; ++i) {
            int g = glyphs[i];
            if (g >= 0) {
                try {
                    GlyphData d = glyphTable.getGlyph(g);
                    if (d != null) {
                        BoundingBox b = d.getBoundingBox();
                        double x = scaleFontUnits(key, b.getLowerLeftX());
                        double y = scaleFontUnits(key, b.getLowerLeftY());
                        double w = scaleFontUnits(key, b.getWidth());
                        double h = scaleFontUnits(key, b.getHeight());
                        bounds[i] = new Rectangle(x, y, w, h);
                    }
                } catch (IOException e) {
                }
            }
        }
        return bounds;
    }

    public Rectangle[] getGlyphBounds(FontKey key, CFFTable cffTable, String text) {
        assert cffTable != null;
        CFFFont cff = cffTable.getFont();
        if ((cff != null) && (cff instanceof CFFCIDFont))
            return getGlyphBounds(key, (CFFCIDFont) cff, text);
        else
            return null;
    }

    public Rectangle[] getGlyphBounds(FontKey key, CFFCIDFont cff, String text) {
        assert cff != null;
        CFFCharset charset = cff.getCharset();
        if (charset == null)
            return null;
        Rectangle[] bounds = new Rectangle[text.length()];
        int[] glyphs = getGlyphs(text);
        for (int i = 0, n = glyphs.length; i < n; ++i) {
            int g = glyphs[i];
            if (g >= 0) {
                try {
                    int cid = charset.getCIDForGID(g);
                    Type1CharString cs = cff.getType2CharString(cid);
                    if (cs != null) {
                        Rectangle2D r = cs.getBounds();
                        if (r != null) {
                            double x = scaleFontUnits(key, r.getX());
                            double y = scaleFontUnits(key, r.getY());
                            double w = scaleFontUnits(key, r.getWidth());
                            double h = scaleFontUnits(key, r.getHeight());
                            bounds[i] = new Rectangle(x, y, w, h);
                        }
                    }

                } catch (IOException e) {
                }
            }
        }
        return bounds;
    }

    private boolean maybeLoad(FontKey key) {
        if ((otf == null) && !otfLoadFailed) {
            OpenTypeFont otf = null;
            NamingTable nameTable = null;
            OS2WindowsMetricsTable os2Table = null;
            CmapSubtable cmapSubtable = null;
            KerningSubtable kerningSubtable = null;
            GlyphTable glyphTable = null;
            try {
                File f = new File(source);
                if (f.exists()) {
                    otf = new OTFParser(false, true).parse(f);
                    nameTable = otf.getNaming();
                    os2Table = otf.getOS2Windows();
                    CmapTable cmap = otf.getCmap();
                    if (cmap != null) {
                        cmapSubtable = cmap.getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_BMP);
                        if (cmapSubtable == null)
                            cmapSubtable = cmap.getSubtable(CmapTable.PLATFORM_WINDOWS, CmapTable.ENCODING_WIN_UNICODE_BMP);
                    }
                    KerningTable kerning = otf.getKerning();
                    if (kerning != null)
                        kerningSubtable = kerning.getHorizontalKerningSubtable();
                    glyphTable = otf.getGlyph();
                    cffTable = otf.getCFF();
                    reporter.logInfo(reporter.message("*KEY*", "Loaded font instance ''{0}''", f.getAbsolutePath()));
                }
            } catch (IOException e) {
            }
            if ((nameTable != null) && (os2Table != null) && (cmapSubtable != null)) {
                this.otf = otf;
                this.nameTable = nameTable;
                this.os2Table = os2Table;
                this.cmapSubtable = cmapSubtable;
                this.kerningSubtable = kerningSubtable;
                this.glyphTable = glyphTable;
            } else
                otfLoadFailed = true;
        }
        return !otfLoadFailed;
    }

    private int[] getCachedGlyphs(String text) {
        Iterator<GlyphMapping> it = this.glyphs.descendingIterator();
        while (it.hasNext()) {
            GlyphMapping m = it.next();
            if (m.text.equals(text))
                return m.glyphs;
        }
        return null;
    }

    private int[] putCachedGlyphs(String text, int[] glyphs) {
        if (this.glyphs.size() == GLYPHS_CACHE_SIZE)
            this.glyphs.remove();
        this.glyphs.push(new GlyphMapping(text, glyphs));
        return glyphs;
    }

    private int[] mapGlyphs(String text, int substitution) {
        int[] glyphs = new int[text.length()];
        for (int i = 0, k = i, n = text.length(); i < n; i = k) {
            int c = (int) text.charAt(i);
            ++k;
            if ((c >= 0xD800) && (c < 0xE000)) {
                int s1 = c;
                if (s1 < 0xDC00) {
                    if ((i + 1) < n) {
                        int s2 = (int) text.charAt(i + 1);
                        ++k;
                        if ((s2 >= 0xDC00) && (s2 < 0xE000))
                            c = ((s1 - 0xD800) << 10) + (s2 - 0xDC00) + 65536;
                        else
                            c = substitution;
                    } else
                        c = substitution;
                } else
                    c = substitution;
            }
            int g = getGlyphId(c);
            mappedGlyphs.put(g, c);
            glyphs[i] = g;
            if ((k - i) == 2)
                glyphs[i + 1] = -1;
        }
        return glyphs;
    }

    private int getGlyphId(int c) {
        int gid = cmapSubtable.getGlyphId(c);
        if (gid == 0)
            maybeReportMappingFailure(c);
        return gid;
    }

    private void maybeReportMappingFailure(int c) {
        if (dontReportMappingFailure(c))
            return;
        if (mappingFailures == null)
            mappingFailures = new java.util.HashSet<Integer>();
        Integer value = Integer.valueOf(c);
        if (!mappingFailures.contains(value)) {
            reporter.logWarning(reporter.message("*KEY*", "No glyph mapping for character {0} in font resource ''{1}''.", Characters.formatCharacter(c), source));
            mappingFailures.add(value);
        }
    }

    private boolean dontReportMappingFailure(int c) {
        if (Characters.isZeroWidthWhitespace(c))
            return true;
        else
            return false;
    }

    private double scaleFontUnits(FontKey key, double v) {
        try {
            return (v / (double) otf.getUnitsPerEm()) * key.size.getDimension(key.axis);
        } catch (Exception e) {
            return v;
        }
    }

    static class GlyphMapping {
        String text;
        int[] glyphs;
        GlyphMapping(String text, int[] glyphs) {
            this.text = text;
            this.glyphs = glyphs;
        }
    }

}
