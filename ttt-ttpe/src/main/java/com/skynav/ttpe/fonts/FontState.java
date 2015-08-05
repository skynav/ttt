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
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.apache.fontbox.ttf.CFFTable;
import org.apache.fontbox.ttf.CmapSubtable;
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
    @SuppressWarnings("unused")
    private GlyphTable glyphTable;
    @SuppressWarnings("unused")
    private CFFTable cffTable;
    private boolean useLayoutTables;
    private boolean useLayoutTablesFailed;
    private GlyphDefinitionTable gdef;
    private GlyphSubstitutionTable gsub;
    private GlyphPositioningTable gpos;
    private Map<GlyphMapping.Key,GlyphMapping> mappings;
    private Map<Integer,Integer> mappedGlyphs;
    private Set<Integer> mappingFailures;
    private Set<Integer> glyphMappingFailures;
    private int[] widths;
    private int[] heights;

    public FontState(String source, Reporter reporter) {
        this.source = source;
        this.reporter = reporter;
        this.mappings = new java.util.HashMap<GlyphMapping.Key,GlyphMapping>();
        this.mappedGlyphs = new java.util.HashMap<Integer,Integer>();
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

    public GlyphMapping getGlyphMapping(FontKey key, String text, SortedSet<FontFeature> features) {
        if (maybeLoad(key)) {
            GlyphMapping.Key gmk = GlyphMapping.makeKey(text, features);
            GlyphMapping gm = getCachedMapping(key, gmk);
            if (gm == null)
                gm = putCachedMapping(key, gmk, mapGlyphs(key, gmk));
            return gm;
        } else
            return null;
    }

    public int getAdvance(FontKey key, GlyphMapping gm) {
        int[] advances = gm.getAdvances();
        int advance = 0;
        for (int i = 0, n = advances.length; i < n; ++i) {
            advance += advances[i];
        }
        return advance;
    }

    public int[] getAdvances(FontKey key, GlyphMapping gm) {
        return gm.getAdvances();
    }

    public double getScaledAdvance(FontKey key, GlyphMapping gm) {
        double[] scaledAdvances = getScaledAdvances(key, gm);
        double scaledAdvance = 0;
        for (int i = 0, n = scaledAdvances.length; i < n; ++i)
            scaledAdvance += scaledAdvances[i];
        return scaledAdvance;
    }

    public double[] getScaledAdvances(FontKey key, GlyphMapping gm) {
        int[] advances = gm.getAdvances();
        double size = key.size.getDimension(gm.getKey().getAdvanceAxis(key));
        double[] scaledAdvances = new double[advances.length];
        for (int i = 0, n = scaledAdvances.length; i < n; ++i)
            scaledAdvances[i] = scaleFontUnits(size, (double) advances[i]);
        return scaledAdvances;
    }

    private boolean maybeLoad(FontKey key) {
        if ((otf == null) && !otfLoadFailed) {
            OpenTypeFont otf = null;
            NamingTable nameTable = null;
            OS2WindowsMetricsTable os2Table = null;
            CmapSubtable cmapSubtable = null;
            KerningSubtable kerningSubtable = null;
            GlyphTable glyphTable = null;
            CFFTable cffTable = null;
            int[] widths = null;
            int[] heights = null;
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
                    widths = otf.getAdvanceWidths();
                    heights = otf.getAdvanceHeights();
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
                this.cffTable = cffTable;
                this.widths = widths;
                this.heights = heights;
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
            } else
                useLayoutTablesFailed = true;
            return !useLayoutTablesFailed;
        }
        return false;
    }

    private GlyphMapping getCachedMapping(FontKey key, GlyphMapping.Key gmk) {
        return this.mappings.get(gmk);
    }

    private GlyphMapping putCachedMapping(FontKey key, GlyphMapping.Key gmk, GlyphMapping gm) {
        this.mappings.put(gmk, gm);
        return gm;
    }

    private GlyphMapping mapGlyphs(FontKey key, GlyphMapping.Key gmk) {
        if (!useLayoutTables)
            return mapGlyphsSimple(key, gmk);
        else
            return mapGlyphsComplex(key, gmk);
    }

    private GlyphMapping mapGlyphsSimple(FontKey key, GlyphMapping.Key gmk) {
        GlyphSequence ogs = mapCharsToGlyphs(gmk.getText(), null);
        return new GlyphMapping(gmk, ogs, getAdvances(key, gmk, ogs), null);
    }

    private GlyphMapping mapGlyphsComplex(FontKey key, GlyphMapping.Key gmk) {

        // extract font info, text, etc.
        String text = gmk.getText();

        // if script is not specified or it is specified as 'auto', then compute dominant script
        String script = gmk.getScript();
        if ((script == null) || script.isEmpty() || "auto".equals(script))
            script = CharScript.scriptTagFromCode(CharScript.dominantScript(text));

        // if language is not specified or it is specified as 'none', then assign default language
        String language = gmk.getLanguage();
        if ((language == null) || language.isEmpty() || "none".equals(language))
            language = "dflt";

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

        // construct final output glyph sequence and mapping
        GlyphSequence ogs = mapCharsToGlyphs(mcs, associations);
        GlyphMapping gm =  new GlyphMapping(gmk, ogs, getAdvances(key, gmk, ogs), gpa);
        gm.setResolvedScript(script);
        gm.setResolvedLanguage(language);
        return gm;
    }

    private int[] getAdvances(FontKey key, GlyphMapping.Key gmk, GlyphSequence gs) {
        boolean vertical = gmk.getAdvanceAxis(key).isVertical();
        int[] glyphs = getGlyphs(gs);
        int[] kerning = gmk.isKerningEnabled() ? ((kerningSubtable != null) ? kerningSubtable.getKerning(glyphs) : null) : null;
        int[] advances = new int[glyphs.length];
        for (int i = 0, n = advances.length; i < n; ++i) {
            int g = glyphs[i];
            int c = mappedGlyphs.get(g);
            if (!Characters.isZeroWidthWhitespace(c)) {
                int a = vertical ? getAdvanceHeight(g) : getAdvanceWidth(g);
                int k = (kerning != null) ? kerning[i] : 0;
                advances[i] = a + k;
            }
        }
        return advances;
    }

    private int[] getGlyphs(GlyphSequence gs) {
        int[] glyphs = new int[gs.getGlyphCount()];
        for (int i = 0, n = glyphs.length; i < n; ++i)
            glyphs[i] = gs.getGlyph(i);
        return glyphs;
    }

    private int getAdvanceHeight(int g) {
        if (heights != null) {
            if (g >= heights.length)
                g = heights.length - 1;
            return heights[g];
        } else
            return 0;
    }

    private int getAdvanceWidth(int g) {
        if (widths != null) {
            if (g >= widths.length)
                g = widths.length - 1;
            return widths[g];
        } else
            return 0;
    }

    private double scaleFontUnits(FontKey key, double v) {
        return scaleFontUnits(key.size.getDimension(key.axis), v);
    }

    private double scaleFontUnits(double size, double v) {
        try {
            return (v / (double) otf.getUnitsPerEm()) * size;
        } catch (Exception e) {
            return v;
        }
    }

    // advanced typographic table support

    private boolean performsSubstitution() {
        return gsub != null;
    }

    private CharSequence performSubstitution(FontKey key, CharSequence cs, String script, String language, List associations, boolean retainControls) {
        if (gsub != null) {
            CharSequence  ncs = normalize(cs, associations);
            GlyphSequence igs = mapCharsToGlyphs(ncs, associations);
            GlyphSequence ogs = gsub.substitute(igs, script, language);
            if (associations != null) {
                associations.clear();
                associations.addAll(ogs.getAssociations());
            }
            if (!retainControls) {
                ogs = elideControls(ogs);
            }
            CharSequence ocs = mapGlyphsToChars(ogs);
            return ocs;
        } else {
            return cs;
        }
    }

    private CharSequence reorderCombiningMarks(FontKey key, CharSequence cs, int[][] gpa, String script, String language, List associations) {
        if (gdef != null) {
            GlyphSequence igs = mapCharsToGlyphs(cs, associations);
            GlyphSequence ogs = gdef.reorderCombiningMarks(igs, getUnscaledWidths(igs), gpa, script, language);
            if (associations != null) {
                associations.clear();
                associations.addAll(ogs.getAssociations());
            }
            CharSequence ocs = mapGlyphsToChars(ogs);
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
            GlyphSequence gs = mapCharsToGlyphs(cs, null);
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

    private GlyphSequence mapCharsToGlyphs(CharSequence cs, List associations) {
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

    private int getGlyphId(int c) {
        assert cmapSubtable != null;
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

    private CharSequence mapGlyphsToChars(GlyphSequence gs) {
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

    private void reportGlyphMappingFailure(int g) {
        if (glyphMappingFailures == null)
            glyphMappingFailures = new java.util.HashSet<Integer>();
        Integer value = Integer.valueOf(g);
        if (!glyphMappingFailures.contains(value)) {
            reporter.logWarning(reporter.message("*KEY*", "No character mapping for glyph {0} in font resource ''{1}''.", String.format("0x%04X", g), source));
            glyphMappingFailures.add(value);
        }
    }

}
