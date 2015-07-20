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
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.fontbox.cff.CFFCIDFont;
import org.apache.fontbox.cff.CFFCharset;
import org.apache.fontbox.cff.CFFFont;
import org.apache.fontbox.cff.Type1CharString;
import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.GlyphData;
import org.apache.fontbox.ttf.GlyphTable;
import org.apache.fontbox.ttf.KerningSubtable;
import org.apache.fontbox.ttf.KerningTable;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OTFParser;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.advanced.GlyphDefinitionTable;
import org.apache.fontbox.ttf.advanced.GlyphPositioningTable;
import org.apache.fontbox.ttf.advanced.GlyphSubstitutionTable;
import org.apache.fontbox.ttf.advanced.util.CharAssociation;
import org.apache.fontbox.ttf.advanced.util.CharNormalize;
import org.apache.fontbox.ttf.advanced.util.CharScript;
import org.apache.fontbox.ttf.advanced.util.GlyphSequence;
import org.apache.fontbox.util.BoundingBox;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.util.Reporter;

@SuppressWarnings({"unchecked","rawtypes"})
public class FontState {

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
    private boolean useLayoutTables;
    private boolean useLayoutTablesFailed;
    private GlyphDefinitionTable gdef;
    private GlyphSubstitutionTable gsub;
    private GlyphPositioningTable gpos;
    private Map<String,GlyphMapping> glyphs = new java.util.HashMap<String,GlyphMapping>();
    private Map<Integer,Integer> mappedGlyphs = new java.util.HashMap<Integer,Integer>();
    private Set<Integer> mappingFailures;
    private Set<Integer> glyphMappingFailures;
    private int[] widths;
    // private int[] heights;

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

    public int[] getGlyphs(FontKey key, String text, String script, String language) {
        return getGlyphs(key, text, script, language, Characters.UC_REPLACEMENT);
    }

    public int[] getGlyphs(FontKey key, String text, String script, String language, int substitution) {
        int[] glyphs;
        if ((glyphs = getCachedGlyphs(text)) != null)
            return glyphs;
        return putCachedGlyphs(text, mapGlyphs(key, text, script, language, substitution));
    }

    public String getMappedText(FontKey key, String text) {
        GlyphMapping m = this.glyphs.get(text);
        return (m != null) ? m.getMappedText() : text;
    }

    public double getAdvance(FontKey key, String text, String script, String language, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        double advance = 0;
        for (double a : getAdvances(key, text, script, language, adjustForKerning, rotatedOrientation, cross))
            advance += a;
        return advance;
    }

    public double[] getAdvances(FontKey key, String text, String script, String language, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        boolean vertical = ((key.axis == Axis.VERTICAL) && !cross) || ((key.axis == Axis.HORIZONTAL) && cross);
        double[] advances = null;
        if (maybeLoad(key)) {
            int[] glyphs = getGlyphs(key, text, script, language);
            int[] kerning = (adjustForKerning && (kerningSubtable != null)) ? kerningSubtable.getKerning(glyphs) : null;
            advances = new double[glyphs.length];
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

    public double getKerningAdvance(FontKey key, String text, String script, String language) {
        double[] kerning = getKerning(key, text, script, language);
        if (kerning != null) {
            double advance = 0;
            for (double k : kerning)
                advance += k;
            return advance;
        } else
            return 0;
    }

    public double[] getKerning(FontKey key, String text, String script, String language) {
        if (maybeLoad(key)) {
            if (kerningSubtable != null) {
                int[] glyphs = getGlyphs(key, text, script, language);
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

    public Rectangle[] getGlyphBounds(FontKey key, String text, String script, String language) {
        Rectangle[] bounds = null;
        if (maybeLoad(key)) {
            if (glyphTable != null)
                bounds = getGlyphBounds(key, glyphTable, text, script, language);
            else if (cffTable != null)
                bounds = getGlyphBounds(key, cffTable, text, script, language);
        }
        if (bounds != null) {
            for (int i = 0, n = bounds.length; i < n; ++i) {
                if (bounds[i] == null)
                    bounds[i] = Rectangle.EMPTY;
            }
        }
        return bounds;
    }

    public Rectangle[] getGlyphBounds(FontKey key, GlyphTable glyphTable, String text, String script, String language) {
        assert glyphTable != null;
        Rectangle[] bounds = new Rectangle[text.length()];
        int[] glyphs = getGlyphs(key, text, script, language);
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

    public Rectangle[] getGlyphBounds(FontKey key, CFFTable cffTable, String text, String script, String language) {
        assert cffTable != null;
        CFFFont cff = cffTable.getFont();
        if ((cff != null) && (cff instanceof CFFCIDFont))
            return getGlyphBounds(key, (CFFCIDFont) cff, text, script, language);
        else
            return null;
    }

    public Rectangle[] getGlyphBounds(FontKey key, CFFCIDFont cff, String text, String script, String language) {
        assert cff != null;
        CFFCharset charset = cff.getCharset();
        if (charset == null)
            return null;
        Rectangle[] bounds = new Rectangle[text.length()];
        int[] glyphs = getGlyphs(key, text, script, language);
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
            boolean useLayoutTables = false;
            try {
                File f = new File(source);
                if (f.exists()) {
                    otf = new OTFParser(false, true).parse(f);
                    nameTable = otf.getNaming();
                    os2Table = otf.getOS2Windows();
                    cmapSubtable = otf.getUnicodeCmap();
                    KerningTable kerning = otf.getKerning();
                    if (kerning != null)
                        kerningSubtable = kerning.getHorizontalKerningSubtable();
                    if (!otf.isPostScript())
                        glyphTable = otf.getGlyph();
                    else
                        cffTable = otf.getCFF();
                    useLayoutTables = useLayoutTables(key, otf);
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
                this.useLayoutTables = useLayoutTables;
            } else
                otfLoadFailed = true;
        }
        return !otfLoadFailed;
    }

    private boolean useLayoutTables(FontKey key, OpenTypeFont otf) throws IOException
    {
        if (otf == null)
            return false;
        else if (useLayoutTables)
            return true;
        else if (otf.hasLayoutTables() && !useLayoutTablesFailed) {
            if ((gdef = otf.getGDEF()) != null) {
                gsub = otf.getGSUB();
                gpos = otf.getGPOS();
                if ((gsub == null) && (gpos == null))
                    useLayoutTablesFailed = true;
                widths = otf.getAdvanceWidths();
                // heights = otf.getAdvanceHeights();
            } else
                useLayoutTablesFailed = true;
            return !useLayoutTablesFailed;
        }
        return false;
    }

    private int[] getCachedGlyphs(String text) {
        GlyphMapping m = this.glyphs.get(text);
        return (m != null) ? m.getGlyphs() : null;
    }

    private int[] putCachedGlyphs(String text, GlyphMapping m) {
        this.glyphs.put(text, m);
        return m.getGlyphs();
    }

    private GlyphMapping mapGlyphs(FontKey key, String text, String script, String language, int substitution) {
        if (!useLayoutTables)
            return mapGlyphsSimple(key, text, substitution);
        else
            return mapGlyphsComplex(key, text, script, language, substitution);
    }

    private GlyphMapping mapGlyphsSimple(FontKey key, String text, int substitution) {
        return new GlyphMapping(text, mapCharsToGlyphs(key, text, null), null);
    }

    private GlyphMapping mapGlyphsComplex(FontKey key, String text, String script, String language, int substitution) {

        // if script is not specified or it is specified as 'auto', then compute dominant script
        if ((script == null) || script.isEmpty() || "auto".equals(script)) {
            script = CharScript.scriptTagFromCode(CharScript.dominantScript(text));
        }
        if ((language == null) || language.isEmpty() || "none".equals(language)) {
            language = "dflt";
        }

        // perform substitutions
        CharSequence mcs;
        List associations = new java.util.ArrayList();
        boolean retainControls = false;
        if (performsSubstitution())
            mcs = performSubstitution(key, text, script, language, associations, retainControls);
        else
            mcs = text;

        // perform positioning
        int[][] gpa = null;
        if (performsPositioning())
            gpa = performPositioning(key, mcs, script, language, (int) Math.floor(key.size.getDimension(key.axis) * 1000));

        // reorder combining marks
        mcs = reorderCombiningMarks(key, mcs, gpa, script, language, associations);

        return new GlyphMapping(text, mapCharsToGlyphs(key, mcs, associations), gpa);
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

    private void reportGlyphMappingFailure(int g) {
        if (glyphMappingFailures == null)
            glyphMappingFailures = new java.util.HashSet<Integer>();
        Integer value = Integer.valueOf(g);
        if (!glyphMappingFailures.contains(value)) {
            reporter.logWarning(reporter.message("*KEY*", "No character mapping for glyph {0} in font resource ''{1}''.", String.format("0x%04X", g), source));
            glyphMappingFailures.add(value);
        }
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
        String mappedText;
        int[] glyphs;
        List<CharAssociation> associations;
        int[][] adjustments;
        GlyphMapping(String text, GlyphSequence gs, int[][] adjustments) {
            this.text = text;
            this.mappedText = getText(gs);
            this.glyphs = getGlyphs(gs);
            this.associations = getAssociations(gs);
            this.adjustments = adjustments;
        }
        public String getText() {
            return text;
        }
        public String getMappedText() {
            return mappedText;
        }
        public int[] getGlyphs() {
            return glyphs;
        }
        public List getAssociations() {
            return associations;
        }
        public int[][] getAdjustments() {
            return adjustments;
        }
        private static String getText(GlyphSequence gs) {
            IntBuffer cb = gs.getCharacters();
            cb.rewind();
            StringBuffer sb = new StringBuffer(cb.limit() - cb.position());
            while (cb.position() < cb.limit()) {
                int c = cb.get();
                if (c < 0x10000)
                    sb.append((char) c);
                else {
                    c -= 0x10000;
                    int sh = ((c >> 10) & 0x03FF) + 0xD800;
                    int sl = ((c >>  0) & 0x03FF) + 0xDC00;
                    sb.append((char) sh);
                    sb.append((char) sl);
                }
            }
            return sb.toString();
        }
        private static int[] getGlyphs(GlyphSequence gs) {
            IntBuffer gb = gs.getGlyphs();
            gb.rewind();
            int[] glyphs = new int[gb.limit() - gb.position()];
            for (int i = 0; gb.position() < gb.limit(); ) {
                glyphs[i++] = gb.get();
            }
            return glyphs;
        }
        private static List<CharAssociation> getAssociations(GlyphSequence gs) {
            List associations = gs.getAssociations();
            if (associations != null) {
                List<CharAssociation> cal = new java.util.ArrayList<CharAssociation>(associations.size());
                for (Object a : associations) {
                    if (a instanceof CharAssociation)
                        cal.add((CharAssociation) a);
                    else
                        cal.add(null);
                }
                return cal;
            } else
                return null;
        }
    }

    // advanced typographic table support

    private boolean performsSubstitution() {
        return gsub != null;
    }

    private CharSequence performSubstitution(FontKey key, CharSequence cs, String script, String language, List associations, boolean retainControls) {
        if (gsub != null) {
            CharSequence  ncs = normalize(cs, associations);
            GlyphSequence igs = mapCharsToGlyphs(key, ncs, associations);
            GlyphSequence ogs = gsub.substitute(igs, script, language);
            if (associations != null) {
                associations.clear();
                associations.addAll(ogs.getAssociations());
            }
            if (!retainControls) {
                ogs = elideControls(ogs);
            }
            CharSequence ocs = mapGlyphsToChars(key, ogs);
            return ocs;
        } else {
            return cs;
        }
    }

    private CharSequence reorderCombiningMarks(FontKey key, CharSequence cs, int[][] gpa, String script, String language, List associations) {
        if (gdef != null) {
            GlyphSequence igs = mapCharsToGlyphs(key, cs, associations);
            GlyphSequence ogs = gdef.reorderCombiningMarks(igs, getUnscaledWidths(igs), gpa, script, language);
            if (associations != null) {
                associations.clear();
                associations.addAll(ogs.getAssociations());
            }
            CharSequence ocs = mapGlyphsToChars(key, ogs);
            return ocs;
        } else {
            return cs;
        }
    }

    private CharSequence normalize(CharSequence cs, List associations) {
        return hasDecomposable(cs) ? decompose(cs, associations) : cs;
    }

    private boolean hasDecomposable(CharSequence cs) {
        for (int i = 0, n = cs.length(); i < n; i++) {
            int cc = cs.charAt(i);
            if (CharNormalize.isDecomposable(cc)) {
                return true;
            }
        }
        return false;
    }

    private CharSequence decompose(CharSequence cs, List associations) {
        StringBuffer sb = new StringBuffer(cs.length());
        int[] daBuffer = new int[CharNormalize.maximumDecompositionLength()];
        for (int i = 0, n = cs.length(); i < n; i++) {
            int cc = cs.charAt(i);
            int[] da = CharNormalize.decompose(cc, daBuffer);
            for (int j = 0; j < da.length; j++) {
                if (da[j] > 0) {
                    sb.append((char) da[j]);
                } else {
                    break;
                }
            }
        }
        return sb;
    }

    private static GlyphSequence elideControls(GlyphSequence gs) {
        if (hasElidableControl(gs)) {
            int[] ca = gs.getCharacterArray(false);
            IntBuffer ngb = IntBuffer.allocate(gs.getGlyphCount());
            List nal = new java.util.ArrayList(gs.getGlyphCount());
            for (int i = 0, n = gs.getGlyphCount(); i < n; ++i) {
                CharAssociation a = gs.getAssociation(i);
                int s = a.getStart();
                int e = a.getEnd();
                while (s < e) {
                    int ch = ca [ s ];
                    if (isElidableControl(ch)) {
                        break;
                    } else {
                        ++s;
                    }
                }
                if (s == e) {
                    ngb.put(gs.getGlyph(i));
                    nal.add(a);
                }
            }
            ngb.flip();
            return new GlyphSequence(gs.getCharacters(), ngb, nal, gs.getPredications());
        } else {
            return gs;
        }
    }

    private static boolean hasElidableControl(GlyphSequence gs) {
        int[] ca = gs.getCharacterArray(false);
        for (int i = 0, n = ca.length; i < n; ++i) {
            int ch = ca [ i ];
            if (isElidableControl(ch)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isElidableControl(int ch) {
        if (ch < 0x0020) {
            return true;
        } else if ((ch >= 0x80) && (ch < 0x00A0)) {
            return true;
        } else if ((ch >= 0x2000) && (ch <= 0x206F)) {
            if ((ch >= 0x200B) && (ch <= 0x200F)) {
                return true;
            } else if ((ch >= 0x2028) && (ch <= 0x202E)) {
                return true;
            } else if (ch >= 0x2066) {
                return true;
            } else {
                return ch == 0x2060;
            }
        } else {
            return false;
        }
    }

    protected int[] getUnscaledWidths(GlyphSequence gs) {
        int[] widths = new int[gs.getGlyphCount()];
        for (int i = 0, n = widths.length; i < n; ++i) {
            int g = gs.getGlyph(i);
            if (g >= this.widths.length)
                g = this.widths.length - 1;
            widths[i] = this.widths[g];
        }
        return widths;
    }

    private boolean performsPositioning() {
        return gpos != null;
    }

    private int[][] performPositioning(FontKey key, CharSequence cs, String script, String language, int fontSize) {
        if (gpos != null) {
            GlyphSequence gs = mapCharsToGlyphs(key, cs, null);
            int[][] adjustments = new int [ gs.getGlyphCount() ] [ 4 ];
            if (gpos.position(gs, script, language, fontSize, this.widths, adjustments)) {
                return scaleAdjustments(adjustments, fontSize);
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    private int[][] scaleAdjustments(int[][] adjustments, int fontSize) {
        if (adjustments != null) {
            for (int i = 0, n = adjustments.length; i < n; i++) {
                int[] gpa = adjustments [ i ];
                for (int k = 0; k < 4; k++) {
                    gpa [ k ] = (gpa [ k ] * fontSize) / 1000;
                }
            }
            return adjustments;
        } else {
            return null;
        }
    }

    private GlyphSequence mapCharsToGlyphs(FontKey key, CharSequence cs, List associations) {
        IntBuffer cb = IntBuffer.allocate(cs.length());
        IntBuffer gb = IntBuffer.allocate(cs.length());
        int gi;
        int giMissing = getGlyphId(/*Typeface.NOT_FOUND*/ '#');
        for (int i = 0, n = cs.length(); i < n; i++) {
            int cc = cs.charAt(i);
            if ((cc >= 0xD800) && (cc < 0xDC00)) {
                if ((i + 1) < n) {
                    int sh = cc;
                    int sl = cs.charAt(++i);
                    if ((sl >= 0xDC00) && (sl < 0xE000)) {
                        cc = 0x10000 + ((sh - 0xD800) << 10) + ((sl - 0xDC00) << 0);
                    } else {
                        throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated high surrogate at index " + i);
                    }
                } else {
                    throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated high surrogate at end of sequence");
                }
            } else if ((cc >= 0xDC00) && (cc < 0xE000)) {
                throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated low surrogate at index " + i);
            }
            gi = getGlyphId(cc);
            if (gi <= 0) {
                maybeReportMappingFailure((char) cc);
                gi = giMissing;
            }
            cb.put(cc);
            gb.put(gi);
            mappedGlyphs.put(gi, cc);
        }
        cb.flip();
        gb.flip();
        if ((associations != null) && (associations.size() == cs.length())) {
            associations = new java.util.ArrayList(associations);
        } else {
            associations = null;
        }
        return new GlyphSequence(cb, gb, associations);
    }

    private CharSequence mapGlyphsToChars(FontKey key, GlyphSequence gs) {
        int ng = gs.getGlyphCount();
        CharBuffer cb = CharBuffer.allocate(ng);
        int ccMissing = '#';
        for (int i = 0, n = ng; i < n; i++) {
            int gi = gs.getGlyph(i);
            int cc = findCharacterFromGlyphIndex(gi);
            if ((cc == 0) || (cc > 0x10FFFF)) {
                cc = ccMissing;
                reportGlyphMappingFailure(gi);
            }
            if (cc > 0x00FFFF) {
                int sh;
                int sl;
                cc -= 0x10000;
                sh = ((cc >> 10) & 0x3FF) + 0xD800;
                sl = ((cc >>  0) & 0x3FF) + 0xDC00;
                cb.put((char) sh);
                cb.put((char) sl);
            } else {
                cb.put((char) cc);
            }
        }
        cb.flip();
        return cb;
    }

    private int findCharacterFromGlyphIndex(int g) {
        if (cmapSubtable != null) {
            Integer c = cmapSubtable.getCharacterCode(g);
            return (c != null) ? (int) c : 0;
        } else
            return 0;
    }

}
