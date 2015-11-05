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

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.PathIterator;
import java.io.File;
import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.IntBuffer;
import java.text.MessageFormat;
import java.util.BitSet;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

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

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.util.Reporter;

@SuppressWarnings({"unchecked","rawtypes"})
public class FontState {

    private static final MessageFormat doubleFormatter          = new MessageFormat("{0,number,#.####}");

    private static final int MISSING_GLYPH_CHAR = '#';          // character for missing glyph
    private static final int PUA_LOWER_LIMIT    = 0xE000;       // lower limit (inclusive) of bmp pua
    private static final int PUA_UPPER_LIMIT    = 0xF8FF;       // upper limit (inclusive) of bmp pua

    private static final SortedSet<FontFeature> RVS_M1;         // feature set (reverse, with mirror)
    private static final SortedSet<FontFeature> RVS_M0;         // feature set (reverse, without mirror)

    static {
        SortedSet<FontFeature> ss;
        ss = new java.util.TreeSet<FontFeature>();
        ss.add(FontFeature.REVS.parameterize(true));
        ss.add(FontFeature.MIRR.parameterize(true));
        RVS_M1 = Collections.unmodifiableSortedSet(ss);
        ss = new java.util.TreeSet<FontFeature>();
        ss.add(FontFeature.REVS.parameterize(true));
        ss.add(FontFeature.MIRR.parameterize(false));
        RVS_M0 = Collections.unmodifiableSortedSet(ss);
    }

    private String source;                                      // font file source path
    private BitSet forcePath;                                   // force use of path for specified glyph indices represented by bit set
    private Reporter reporter;                                  // reporter for warnings, errors, etc
    private OpenTypeFont otf;                                   // open type font instance (from fontbox)
    private boolean otfLoadFailed;                              // true if load of open type font failed
    private NamingTable nameTable;                              // name table
    private OS2WindowsMetricsTable os2Table;                    // os2 table
    private CmapSubtable cmapSubtable;                          // cmap subtable for character to glyph mapping
    private KerningSubtable kerningSubtable;                    // kerning subtable
    private GlyphTable glyphTable;                              // glyph table
    private CFFTable cffTable;                                  // cff table (postscript fonts)
    private boolean useLayoutTables;                            // true if using advanced typographic (layout) tables
    private boolean useLayoutTablesFailed;                      // true if load of advanced typographic (layout) tables failed
    private GlyphDefinitionTable gdef;                          // glyph definition table, if available
    private GlyphSubstitutionTable gsub;                        // glyph substitution table, if available
    private GlyphPositioningTable gpos;                         // glyph positioning table, if available
    private Map<GlyphMapping.Key,GlyphMapping> mappings;        // glyph mappings cache
    private Map<Integer,Integer> gidMappings;                   // map from glyph indices (gids) to input character codes
    private Set<Integer> gidMappingFailures;                    // set of input character codes that don't map to any glyph index
    private int puaMappingNext;                                 // next pua assignment for glyphs with no corresponding character code
    private Map<Integer,Integer> puaMappings;                   // map from unmappable glyph indices to pua character codes
    private Map<Integer,Integer> puaGlyphMappings;              // map from pua character codes to unmappable glyph indices
    private Set<Integer> puaMappingFailures;                    // set of glyph indices for which no pua character code could be assigned
    private double upem;                                        // units per em
    private int[] widths;                                       // array of glyph advances in horizontal axis
    private int[] heights;                                      // array of glyph advances in vertical axis
    private Map<PathCacheKey,String> pathCache;                 // map from {font key, glyph id, advance} to glyph path string

    public FontState(String source, BitSet forcePath, Reporter reporter) {
        this.source = source;
        this.forcePath = forcePath;
        this.reporter = reporter;
        this.mappings = new java.util.HashMap<GlyphMapping.Key,GlyphMapping>();
        this.gidMappings = new java.util.HashMap<Integer,Integer>();
        this.puaMappingNext = PUA_LOWER_LIMIT;
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
            return scaleFontUnits(key, getLeading());
        else
            return 0;
    }

    public int getLeading() {
        int l = os2Table.getTypoLineGap();
        if (l == 0) {
            int wa = os2Table.getWinAscent();
            int wd = os2Table.getWinDescent();
            int wl = (wa + wd) - (int) upem;
            if (wl > 0)
                l = wl;
        }
        return l;
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

    public GlyphMapping maybeReverse(FontKey key, GlyphMapping mapping, boolean mirror) {
        assert mapping != null;
        GlyphMapping gm = mapping;
        if (!gm.isReversed()) {
            GlyphMapping.Key gmk = GlyphMapping.makeKey(mapping.getKey(), makeReversingFeatures(mirror));
            gm = getCachedMapping(key, gmk);
            if (gm == null)
                gm = putCachedMapping(key, gmk, reverseGlyphs(key, gmk, mapping));
        }
        return gm;
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
            scaledAdvances[i] = scaleFontUnits(size, advances[i]);
        return scaledAdvances;
    }

    public double[][] getScaledAdjustments(FontKey key, GlyphMapping gm) {
        int[][] adjustments = gm.getAdjustments();
        if (adjustments != null) {
            double size = key.size.getDimension(gm.getKey().getAdvanceAxis(key));
            double[][] scaledAdjustments = new double[adjustments.length][];
            for (int i = 0, n = scaledAdjustments.length; i < n; ++i) {
                int[] ia = adjustments[i];
                if (ia != null)
                    scaledAdjustments[i] = scaleFontUnits(size, ia);
            }
            return scaledAdjustments;
        } else
            return null;
    }

    public boolean containsPUAMapping(FontKey key, String glyphsAsText) {
        for (int i = 0, n = glyphsAsText.length(); i < n; ++i) {
            int c = glyphsAsText.charAt(i);
            if (inActivePUARange(c))
                return true;
        }
        return false;
    }

    private boolean inActivePUARange(int c) {
        return (c >= PUA_LOWER_LIMIT) && (c < puaMappingNext);
    }

    public String getGlyphsPath(FontKey key, String glyphsAsText, Axis resolvedAxis, double[] advances) {
        if (glyphTable != null)
            return getGlyphsPathContours(key, glyphsAsText, advances, glyphTable);
        else if (cffTable != null)
            return getGlyphsPathContours(key, glyphsAsText, advances, cffTable);
        else
            return "";
    }

    private String getGlyphsPathContours(FontKey key, String glyphsAsText, double[] advances, GlyphTable glyphs) {
        StringBuffer sb = new StringBuffer();
        int[] retNext = new int[1];
        for (int i = 0, n = glyphsAsText.length(); i < n; i = retNext[0]) {
            int gi = getGlyphId(glyphsAsText, i, false, null, retNext);
            if (gi > 0) {
                String p = getGlyphPath(key, gi, advances[i], glyphs);
                if (p != null)
                    sb.append(p);
            }
        }
        return sb.toString();
    }

    private String getGlyphPath(FontKey key, int gi, double advance, GlyphTable glyphs) {
        PathCacheKey pck = new PathCacheKey(key, gi, advance);
        if (hasCachedGlyphPath(pck))
            return getCachedGlyphPath(pck);
        else
            return putCachedGlyphPath(pck, getGlyphPath(pck, glyphs));
    }

    private String getGlyphPath(PathCacheKey pck, GlyphTable glyphs) {
        try {
            GlyphData gd = glyphs.getGlyph(pck.getGlyph());
            if (gd != null) {
                GeneralPath p = gd.getPath();
                if (p != null)
                    return getGlyphPath(pck.getFontKey(), p, pck.getAdvance());
            }
        } catch (IOException e) {
        }
        return null;
    }

    private String getGlyphsPathContours(FontKey key, String glyphsAsText, double[] advances, CFFTable glyphs) {
        StringBuffer sb = new StringBuffer();
        CFFFont cff = glyphs.getFont();
        if ((cff != null) && (cff instanceof CFFCIDFont)) {
            CFFCharset charset = ((CFFCIDFont) cff).getCharset();
            if (charset != null) {
                int[] retNext = new int[1];
                for (int i = 0, n = glyphsAsText.length(); i < n; i = retNext[0]) {
                    int gi = getGlyphId(glyphsAsText, i, false, null, retNext);
                    if (gi > 0) {
                        String p = getGlyphPath(key, gi, advances[i], cff, charset);
                        if (p != null)
                            sb.append(p);
                    }
                }
            }
        }
        return sb.toString();
    }

    private String getGlyphPath(FontKey key, int gi, double advance, CFFFont cff, CFFCharset charset) {
        PathCacheKey pck = new PathCacheKey(key, gi, advance);
        if (hasCachedGlyphPath(pck))
            return getCachedGlyphPath(pck);
        else
            return putCachedGlyphPath(pck, getGlyphPath(pck, cff, charset));
    }

    private String getGlyphPath(PathCacheKey pck, CFFFont cff, CFFCharset charset) {
        try {
            int cid = charset.getCIDForGID(pck.getGlyph());
            Type1CharString cs = cff.getType2CharString(cid);
            if (cs != null) {
                GeneralPath p = cs.getPath();
                if (p != null)
                    return getGlyphPath(pck.getFontKey(), p, pck.getAdvance());
            }
        } catch (IOException e) {
        }
        return null;
    }

    private String getGlyphPath(FontKey key, GeneralPath p, double advance) {
        StringBuffer sb = new StringBuffer();
        double size = key.size.getHeight();
        double s = size / this.upem;
        AffineTransform ctm = AffineTransform.getScaleInstance(s, -s);
        double[] coordinates = new double[6];
        for (PathIterator pi = p.getPathIterator(ctm); !pi.isDone(); pi.next()) {
            int op = pi.currentSegment(coordinates);
            if (op == PathIterator.SEG_CLOSE) {
                sb.append("Z ");
            } else if (op == PathIterator.SEG_CUBICTO) {
                sb.append("C ");
                appendCoordinates(sb, coordinates, 6);
            } else if (op == PathIterator.SEG_LINETO) {
                sb.append("L ");
                appendCoordinates(sb, coordinates, 2);
            } else if (op == PathIterator.SEG_MOVETO) {
                sb.append("M ");
                appendCoordinates(sb, coordinates, 2);
            } else if (op == PathIterator.SEG_QUADTO) {
                sb.append("Q ");
                appendCoordinates(sb, coordinates, 4);
            } else {
            }
        }
        return sb.toString().trim();
    }

    private boolean hasCachedGlyphPath(PathCacheKey pck) {
        return (pathCache != null) && pathCache.containsKey(pck);
    }

    private String getCachedGlyphPath(PathCacheKey pck) {
        return pathCache.get(pck);
    }

    private String putCachedGlyphPath(PathCacheKey pck, String p) {
        if (pathCache == null)
            pathCache = new java.util.HashMap<PathCacheKey, String>();
        pathCache.put(pck, p);
        return p;
    }

    private void appendCoordinates(StringBuffer sb, double[] coordinates, int numCoordinates) {
        for (int i = 0, n = numCoordinates; i < n; ++i) {
            sb.append(doubleFormatter.format(new Object[] {coordinates[i]}));
            sb.append(' ');
        }
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
            double upem = 1000;
            int[] widths = null;
            int[] heights = null;
            boolean useLayoutTables = false;
            File f = new File(source);
            try {
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
                    upem = (double) otf.getUnitsPerEm();
                    widths = otf.getAdvanceWidths();
                    heights = otf.getAdvanceHeights();
                    useLayoutTables = useLayoutTables(key, otf);
                    reporter.logInfo(reporter.message("*KEY*", "Loaded font instance ''{0}''", f.getAbsolutePath()));
                } else {
                    reporter.logError(reporter.message("*KEY*", "Font instance ''{0}'' does not exist", f.getAbsolutePath()));
                }
            } catch (IOException e) {
                reporter.logError(reporter.message("*KEY*", "Failed to load font instance ''{0}'': {1}", f.getAbsolutePath(), e.getMessage()));
            }
            if ((nameTable != null) && (os2Table != null) && (cmapSubtable != null)) {
                this.otf = otf;
                this.nameTable = nameTable;
                this.os2Table = os2Table;
                this.cmapSubtable = cmapSubtable;
                this.kerningSubtable = kerningSubtable;
                this.glyphTable = glyphTable;
                this.cffTable = cffTable;
                this.upem = upem;
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
            gdef = otf.getGDEF();
            gsub = otf.getGSUB();
            gpos = otf.getGPOS();
            if ((gsub == null) && (gpos == null))
                useLayoutTablesFailed = true;
            return !useLayoutTablesFailed;
        } else
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
        GlyphSequence ogs = mapCharsToGlyphs(gmk.getText(), false, null);
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

        // prepare mapping features
        Object[][] mappingFeatures = getMappingFeatures(new java.util.TreeSet<FontFeature>(gmk.getFeatures()));

        // perform substitutions
        CharSequence mcs;
        List associations = new java.util.ArrayList();
        boolean retainControls = false;
        if (performsSubstitution())
            mcs = performSubstitution(key, text, script, language, mappingFeatures, associations, retainControls);
        else
            mcs = text;

        // perform positioning
        int[][] gpa = null;
        if (performsPositioning())
            gpa = performPositioning(key, mcs, script, language, mappingFeatures, (int) Math.floor(key.size.getDimension(key.axis) * 1000));

        // reorder combining marks
        mcs = reorderCombiningMarks(key, mcs, gpa, script, language, mappingFeatures, associations);

        // construct final output glyph sequence and mapping
        GlyphSequence ogs = mapCharsToGlyphs(mcs, false, associations);
        GlyphMapping gm =  new GlyphMapping(gmk, ogs, getAdvances(key, gmk, ogs), gpa);
        gm.setResolvedScript(script);
        gm.setResolvedLanguage(language);
        return gm;
    }

    private Object[][] getMappingFeatures(SortedSet<FontFeature> features) {
        int nf = features.size();
        Object[][] mappingFeatures = new Object[nf][];
        int k = 0;
        for (FontFeature f : features) {
            int na = f.getArgumentCount();
            Object[] fa = new Object[na + 1];
            fa[0] = f.getFeature();
            for (int i = 0, n = na; i < n; ++i)
                fa[i + 1] = f.getArgument(i);
            mappingFeatures[k++] = fa;
        }
        return mappingFeatures;
    }

    private boolean requiresMirror(Object[][] features) {
        for (Object[] f : features) {
            if (requiresMirror(f))
                return true;
        }
        return false;
    }

    private boolean requiresMirror(Object[] feature) {
        if ((feature != null) && (feature.length > 1)) {
            if (feature[0] instanceof String) {
                String n = (String) feature[0];
                if (n.equals(FontFeature.BIDI.getFeature())) {
                    if (feature[1] instanceof Integer) {
                        Integer i = (Integer) feature[1];
                        return (i & 1) == 1;
                    }
                }
            }
        }
        return false;
    }

    private SortedSet<FontFeature> makeReversingFeatures(boolean mirror) {
        return new java.util.TreeSet<FontFeature>(mirror ? RVS_M1 : RVS_M0);
    }

    private GlyphMapping reverseGlyphs(FontKey key, GlyphMapping.Key gmk, GlyphMapping gm) {
        return gm.reverse(gmk);
    }

    private int[] getAdvances(FontKey key, GlyphMapping.Key gmk, GlyphSequence gs) {
        boolean vertical = gmk.getAdvanceAxis(key).isVertical();
        int[] glyphs = getGlyphs(gs);
        int[] kerning = gmk.isKerningEnabled() ? ((kerningSubtable != null) ? kerningSubtable.getKerning(glyphs) : null) : null;
        int[] advances = new int[glyphs.length];
        for (int i = 0, n = advances.length; i < n; ++i) {
            int gi = glyphs[i];
            if (gi != 0) {
                int c = gidMappings.get(gi);
                if (!Characters.isZeroWidthWhitespace(c)) {
                    int a = vertical ? getAdvanceHeight(gi) : getAdvanceWidth(gi);
                    int k = (kerning != null) ? kerning[i] : 0;
                    advances[i] = a + k;
                }
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

    private int getAdvanceHeight(int gi) {
        if (heights != null) {
            if (gi >= heights.length)
                gi = heights.length - 1;
            return heights[gi];
        } else
            return 0;
    }

    private int getAdvanceWidth(int gi) {
        if (widths != null) {
            if (gi >= widths.length)
                gi = widths.length - 1;
            return widths[gi];
        } else
            return 0;
    }

    private double scaleFontUnits(FontKey key, int v) {
        return scaleFontUnits(key.size.getDimension(key.axis), (double) v);
    }

    private double[] scaleFontUnits(double size, int[] va) {
        double[] sa = new double[va.length];
        for (int i = 0, n = va.length; i < n; ++i)
            sa[i] = scaleFontUnits(size, (double) va[i]);
        return sa;
    }

    private double scaleFontUnits(double size, int v) {
        return scaleFontUnits(size, (double) v);
    }

    private double scaleFontUnits(double size, double v) {
        return (v / this.upem) * size;
    }

    // advanced typographic table support

    private boolean performsSubstitution() {
        return gsub != null;
    }

    private CharSequence performSubstitution(FontKey key, CharSequence cs, String script, String language, Object[][] features, List associations, boolean retainControls) {
        if (gsub != null) {
            CharSequence  ncs = normalize(cs, associations);
            GlyphSequence igs = mapCharsToGlyphs(ncs, requiresMirror(features), associations);
            GlyphSequence ogs = gsub.substitute(igs, script, language, features);
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

    private CharSequence reorderCombiningMarks(FontKey key, CharSequence cs, int[][] gpa, String script, String language, Object[][] features, List associations) {
        if (gdef != null) {
            GlyphSequence igs = mapCharsToGlyphs(cs, false, associations);
            GlyphSequence ogs = gdef.reorderCombiningMarks(igs, getUnscaledWidths(igs), gpa, script, language, features);
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
            } else if ((ch >= 0x2060) && (ch <= 0x2064)) {
                return true;
            } else if (ch >= 0x2066) {
                return true;
            } else {
                return false;
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

    private int[][] performPositioning(FontKey key, CharSequence cs, String script, String language, Object[][] features, int fontSize) {
        if (gpos != null) {
            GlyphSequence gs = mapCharsToGlyphs(cs, false, null);
            int[][] adjustments = new int [ gs.getGlyphCount() ] [ 4 ];
            if (gpos.position(gs, script, language, features, fontSize, this.widths, adjustments)) {
                return adjustments;
            } else
                return null;
        } else
            return null;
    }

    private GlyphSequence mapCharsToGlyphs(CharSequence cs, boolean mirror, List associations) {
        IntBuffer cb = IntBuffer.allocate(cs.length());
        IntBuffer gb = IntBuffer.allocate(cs.length());
        int gi;
        int giMissing = getGlyphId(MISSING_GLYPH_CHAR, false);
        int[] retChar = new int[1];
        int[] retNext = new int[1];
        for (int i = 0, n = cs.length(); i < n; i = retNext[0]) {
            gi = getGlyphId(cs, i, mirror, retChar, retNext);
            if (gi <= 0) {
                maybeReportMappingFailure(retChar[0]);
                gi = giMissing;
            }
            cb.put(retChar[0]);
            gb.put(gi);
            if (gi != 0)
                gidMappings.put(gi, retChar[0]);
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

    private int getGlyphId(CharSequence cs, int index, boolean mirror, int[] retChar, int[] retNext) {
        int c = cs.charAt(index++);
        if ((c >= 0xD800) && (c < 0xDC00)) {
            int n = cs.length();
            if (index < n) {
                int sh = c;
                int sl = cs.charAt(index++);
                if ((sl >= 0xDC00) && (sl < 0xE000)) {
                    c = 0x10000 + ((sh - 0xD800) << 10) + ((sl - 0xDC00) << 0);
                } else {
                    throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated high surrogate at index " + (index - 1));
                }
            } else {
                throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated high surrogate at end of sequence");
            }
        } else if ((c >= 0xDC00) && (c < 0xE000)) {
            throw new IllegalArgumentException("ill-formed UTF-16 sequence, contains isolated low surrogate at index " + (index - 1));
        }
        if (mirror && Characters.hasMirror(c))
            c = Characters.toMirror(c);
        if (retChar != null)
            retChar[0] = c;
        if (retNext != null)
            retNext[0] = index;
        return getGlyphId(c, true);
    }

    private int getGlyphId(int c, boolean reportMappingFailure) {
        assert cmapSubtable != null;
        int gid = cmapSubtable.getGlyphId(c);
        if ((gid == 0) && inActivePUARange(c))
            gid = getPUAGlyph(c);
        if ((gid == 0) && reportMappingFailure)
            maybeReportMappingFailure(c);
        return gid;
    }

    private void maybeReportMappingFailure(int c) {
        if (dontReportMappingFailure(c))
            return;
        if (gidMappingFailures == null)
            gidMappingFailures = new java.util.HashSet<Integer>();
        Integer value = Integer.valueOf(c);
        if (!gidMappingFailures.contains(value)) {
            reporter.logWarning(reporter.message("*KEY*", "No glyph mapping for character {0} in font resource ''{1}''.", Characters.formatCharacter(c), source));
            gidMappingFailures.add(value);
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
        CharBuffer cb = CharBuffer.allocate(ng * 2);
        for (int i = 0, n = ng; i < n; i++) {
            int gi = gs.getGlyph(i);
            int cc = findCharacterFromGlyphIndex(gi);
            if ((cc == 0) || (cc > 0x10FFFF))
                cc = getPUACharacter(gi);
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

    private int findCharacterFromGlyphIndex(int gi) {
        if ((forcePath != null) && forcePath.get(gi))
            return 0;
        else if (gidMappings.containsKey(Integer.valueOf(gi)))
            return gidMappings.get(gi);
        else if (cmapSubtable != null) {
            Integer c = cmapSubtable.getCharacterCode(gi);
            return (c != null) ? (int) c : 0;
        } else
            return 0;
    }

    private int getPUACharacter(int gi) {
        Integer v = (puaMappings != null) ? puaMappings.get(gi) : null;
        if (v == null) {
            int cc;
            if (puaMappingNext < PUA_UPPER_LIMIT) {
                cc = puaMappingNext++;
                if (puaMappings == null) {
                    puaMappings = new java.util.HashMap<Integer,Integer>();
                    puaGlyphMappings = new java.util.HashMap<Integer,Integer>();
                }
                puaMappings.put(gi, cc);
                puaGlyphMappings.put(cc, gi);
            } else {
                reportPUAMappingExhausted(gi);
                cc = MISSING_GLYPH_CHAR;
            }
            return cc;
        } else
            return (int) v;
    }

    private int getPUAGlyph(int cc) {
        Integer v = (puaGlyphMappings != null) ? puaGlyphMappings.get(cc) : null;
        return (v != null) ? (int) v : 0;
    }

    private void reportPUAMappingExhausted(int gi) {
        if (puaMappingFailures == null)
            puaMappingFailures = new java.util.HashSet<Integer>();
        Integer value = Integer.valueOf(gi);
        if (!puaMappingFailures.contains(value)) {
            reporter.logWarning(reporter.message("*KEY*", "No PUA mapping available for glyph {0} in font resource ''{1}''.", String.format("0x%04X", gi), source));
            puaMappingFailures.add(value);
        }
    }

    private static class PathCacheKey {
        private FontKey key;
        private int glyph;
        private double advance;
        public PathCacheKey(FontKey key, int glyph, double advance) {
            this.glyph = glyph;
            this.key = key;
            this.advance = advance;
        }
        public FontKey getFontKey() {
            return key;
        }
        public int getGlyph() {
            return glyph;
        }
        public double getAdvance() {
            return advance;
        }
        @Override
        public int hashCode() {
            int hc = 23;
            hc = hc * 31 + key.hashCode();
            hc = hc * 31 + Integer.valueOf(glyph).hashCode();
            hc = hc * 31 + Double.valueOf(advance).hashCode();
            return hc;
        }
        @Override
        public boolean equals(Object o) {
            if (o instanceof PathCacheKey) {
                PathCacheKey other = (PathCacheKey) o;
                if (glyph != other.glyph)
                    return false;
                else if (advance != other.advance)
                    return false;
                else if (!key.equals(other.key))
                    return false;
                else
                    return true;
            } else
                return false;
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append(key);
            sb.append(',');
            sb.append(glyph);
            sb.append(',');
            sb.append(advance);
            sb.append(']');
            return sb.toString();
        }
    }

}
