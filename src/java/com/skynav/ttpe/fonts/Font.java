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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.Map;

import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.util.BoundingBox;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.util.Reporter;

public class Font {

    private static final int                    GLYPHS_CACHE_SIZE               = 16;

    private FontCache cache;
    private FontKey key;
    private String source;
    private Reporter reporter;
    private boolean otfLoadFailed;
    // loaded table state
    private OpenTypeFont otf;
    private NamingTable nameTable;
    private OS2WindowsMetricsTable os2Table;
    private CmapSubtable cmapSubtable;
    private KerningSubtable kerningSubtable;
    private GlyphTable glyphTable;
    // caching
    private Deque<GlyphMapping> glyphs = new java.util.ArrayDeque<GlyphMapping>(GLYPHS_CACHE_SIZE);
    private Map<Integer,Integer> mappedGlyphs = new java.util.HashMap<Integer,Integer>();

    public Font(FontCache cache, FontKey key, String source, Reporter reporter) {
        this.cache = cache;
        this.key = key;
        this.source = source;
        this.reporter = reporter;
    }

    @Override
    public String toString() {
        if (key != null)
            return key.toString();
        else
            return super.toString();
    }

    public FontKey getKey() {
        return key;
    }

    public FontSpecification getSpecification() {
        return key.getSpecification();
    }

    public String getSource() {
        return source;
    }

    public String getPreferredFamilyName() {
        if (maybeLoad()) {
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

    public FontStyle getStyle() {
        return key.style;
    }

    public FontWeight getWeight() {
        return key.weight;
    }

    public Collection<FontFeature> getFeatures() {
        return key.getFeatures();
    }

    public FontFeature getFeature(String feature) {
        return key.getFeature(feature);
    }

    public boolean isKerningEnabled() {
        FontFeature f = getFeature("kern");
        if (f != null) {
            Object arg = f.getArgument(0);
            if ((arg instanceof FontKerning) && (arg == FontKerning.NONE))
                return false;
        }
        return true;
    }

    public boolean isSheared() {
        FontFeature f = getFeature("oblq");
        if (f != null) {
            Object arg = f.getArgument(0);
            if (arg instanceof Double) {
                double shear = (Double) arg;
                if (shear != 0)
                    return true;
            }
        }
        return false;
    }

    public double getShear() {
        FontFeature f = getFeature("oblq");
        if (f != null) {
            Object arg = f.getArgument(0);
            if (arg instanceof Double)
                return (Double) arg;
        }
        return 0;
    }

    public Axis getAxis() {
        return key.axis;
    }

    public Extent getSize() {
        return key.size;
    }

    public double getSize(Axis axis) {
        return key.size.getDimension(axis);
    }

    public double getWidth() {
        return getSize(Axis.HORIZONTAL);
    }

    public double getHeight() {
        return getSize(Axis.VERTICAL);
    }

    public boolean isAnamorphic() {
        return getSize(Axis.HORIZONTAL) != getSize(Axis.VERTICAL);
    }

    public Double getDefaultLineHeight() {
        return key.size.getDimension(key.axis) * 1.25;
    }

    public double getLeading() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoLineGap());
        else
            return 0;
    }

    public double getAscent() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoAscender());
        else
            return 0;
    }

    public double getDescent() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoDescender());
        else
            return 0;
    }

    public double getAdvance(String text) {
        return getAdvance(text, true);
    }

    public double getAdvance(String text, boolean adjustForKerning) {
        double advance = 0;
        for (double a : getAdvances(text))
            advance += a;
        if (adjustForKerning)
            advance += getKerningAdvance(text);
        return advance;
    }

    public double[] getAdvances(String text) {
        double[] advances = new double[text.length()];
        if (maybeLoad()) {
            int[] glyphs = getGlyphs(text);
            for (int i = 0, n = glyphs.length; i < n; ++i) {
                int g = glyphs[i];
                int c = mappedGlyphs.get(g);
                if (!Characters.isZeroWidthWhitespace(c)) {
                    try {
                        advances[i] = scaleFontUnits((double) otf.getAdvanceWidth(g));
                    } catch (IOException e) {
                    }
                }
            }
        }
        return advances;
    }

    public double getKerningAdvance(String text) {
        double[] kerning = getKerning(text);
        if (kerning != null) {
            double advance = 0;
            for (double k : kerning)
                advance += k;
            return advance;
        } else
            return 0;
    }

    public double[] getKerning(String text) {
        if (maybeLoad()) {
            if (kerningSubtable != null) {
                int[] glyphs = getGlyphs(text);
                int[] kerning = kerningSubtable.getKerning(glyphs);
                assert kerning.length == glyphs.length;
                double[] kerningScaled = new double[kerning.length];
                boolean hasNonZeroKerning = false;
                for (int i = 0; i < kerning.length; ++i) {
                    double k = scaleFontUnits((double) kerning[i]);
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

    public TransformMatrix getTransform() {
        TransformMatrix t = TransformMatrix.IDENTITY;
        if (isSheared())
            t = applyShear(t, getShear());
        if (isAnamorphic())
            t = applyAnamorphic(t, getSize());
        return !t.isIdentity() ? t : null;
    }

    private TransformMatrix applyShear(TransformMatrix t0, double shear) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double sx = -Math.tan(Math.toRadians(shear * 90));
        double sy = 0;
        t.shear(sx, sy);
        return t;
    }

    private TransformMatrix applyAnamorphic(TransformMatrix t0, Extent size) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double sx = size.getDimension(Axis.HORIZONTAL) / size.getDimension(Axis.VERTICAL);
        double sy = 1;
        t.scale(sx, sy);
        return t;
    }

    public Rectangle[] getGlyphBounds(String text) {
        if (maybeLoad()) {
            if (glyphTable != null) {
                Rectangle[] bounds = new Rectangle[text.length()];
                int[] glyphs = getGlyphs(text);
                for (int i = 0, n = glyphs.length; i < n; ++i) {
                    int g = glyphs[i];
                    if (g >= 0) {
                        try {
                            GlyphData d = glyphTable.getGlyph(g);
                            if (d != null) {
                                BoundingBox b = d.getBoundingBox();
                                double x = scaleFontUnits(b.getLowerLeftX());
                                double y = scaleFontUnits(b.getLowerLeftY());
                                double w = scaleFontUnits(b.getWidth());
                                double h = scaleFontUnits(b.getHeight());
                                bounds[i] = new Rectangle(x, y, w, h);
                            } else
                                bounds[i] = Rectangle.EMPTY;
                        } catch (IOException e) {
                        }
                    }
                }
                return bounds;
            }
        }
        return null;
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

    public int[] mapGlyphs(String text, int substitution) {
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
            int g = cmapSubtable.getGlyphId(c);
            mappedGlyphs.put(g, c);
            glyphs[i] = g;
            if ((k - i) == 2)
                glyphs[i + 1] = -1;
        }
        return glyphs;
    }

    public Font getScaledFont(double scale) {
        return cache.getScaledFont(this, scale);
    }

    private boolean maybeLoad() {
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
                    if (cmap != null)
                        cmapSubtable = cmap.getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_BMP);
                    if (isKerningEnabled()) {
                        KerningTable kerning = otf.getKerning();
                        if (kerning != null)
                            kerningSubtable = kerning.getHorizontalKerningSubtable();
                    }
                    glyphTable = otf.getGlyph();
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

    private double scaleFontUnits(double v) {
        try {
            return (v / (double) otf.getUnitsPerEm()) * key.size.getDimension(key.axis);
        } catch (Exception e) {
            return v;
        }
    }

    public static class GlyphMapping {
        String text;
        int[] glyphs;
        GlyphMapping(String text, int[] glyphs) {
            this.text = text;
            this.glyphs = glyphs;
        }
    }

}
