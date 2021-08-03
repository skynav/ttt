/*
 * Copyright 2014-21 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.skynav.ttpe.fonts.Combination;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.fonts.FontFeature;
import com.skynav.ttpe.fonts.FontKerning;
import com.skynav.ttpe.fonts.FontSelectionStrategy;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.fonts.Orientation;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.model.value.FontVariant;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Measure;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.verifier.util.Fonts;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.Measures;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.QuotedGenericFontFamilyTreatment;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.geometry.Dimension.*;
import static com.skynav.ttpe.style.Constants.*;
import static com.skynav.ttpe.text.Constants.*;

public class StyleCollector {

    private StyleCollector parent;                      // parent style collector (or null if no parent)
    private TransformerContext context;                 // transformation context
    private FontCache fontCache;                        // font state cache
    private Defaults defaults;                          // style defaults state
    private Extent extBounds;                           // extent of outer (canvas) viewport context, remains constant during collection
    private Extent refBounds;                           // extent of nearest viewport context, remains constant during collection
    private Extent cellResolution;                      // cell resolution of collector context, remains constant during collection
    private WritingMode writingMode;                    // writing mode of collector context, remains constant during collection
    private String language;                            // language of collector context, remains constant during collection
    private Font[] fonts;                               // fonts of current element being collected
    private int synthesizedStylesIndex;                 // index of next synthesized style identifier
    private Map<String,StyleSet> styles;                // map of all isd:css style sets, identified by id string
    private List<StyleAttributeInterval> attributes;    // text attributes being collected
    private BidiLevelIterator bidiIterator;             // bidi level iterator, reused as needed
    private FontRunIterator fontIterator;               // font run iterator, reused as needed

    public StyleCollector(StyleCollector sc) {
        this(sc, sc.context, sc.fontCache, sc.defaults, sc.extBounds, sc.refBounds, sc.cellResolution, sc.writingMode, sc.language, sc.fonts, sc.styles);
    }

    public StyleCollector
        (StyleCollector parent, TransformerContext context, FontCache fontCache, Defaults defaults, Extent extBounds, Extent refBounds, Extent cellResolution, WritingMode writingMode, String language, Font[] fonts, Map<String,StyleSet> styles) {
        this.parent = parent;
        this.context = context;
        this.defaults = defaults;
        this.fontCache = fontCache;
        this.extBounds = extBounds;
        this.refBounds = refBounds;
        this.cellResolution = cellResolution;
        this.writingMode = writingMode;
        this.language = language;
        this.fonts = (fonts != null) ? Arrays.copyOf(fonts, fonts.length) : null;
        this.styles = styles;
        this.bidiIterator = new BidiLevelIterator();
        this.fontIterator = new FontRunIterator(fontCache);
    }

    public StyleCollector getParent() {
        return parent;
    }

    public TransformerContext getContext() {
        for (StyleCollector sc = this; sc != null; sc = sc.getParent()) {
            if (sc.context != null)
                return sc.context;
        }
        return null;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    public Defaults getDefaults() {
        return defaults;
    }

    protected void setFonts(Font[] fonts) {
        assert fonts != null;
        this.fonts = (fonts != null) ? Arrays.copyOf(fonts, fonts.length) : null;
    }

    public Font[] getFonts() {
        if (this.fonts != null)
            return Arrays.copyOf(this.fonts, this.fonts.length);
        else
            return null;
    }

    public Font getFirstAvailableFont() {
        Font[] fonts = getFonts();
        if ((fonts != null) && (fonts.length > 0))
            return fonts[0];
        else
            return null;
    }

    public Extent getFirstAvailableFontSize() {
        Font font = getFirstAvailableFont();
        if (font != null)
            return font.getSize();
        else
            return Extent.UNIT;
    }

    public Extent getExternalBounds() {
        return extBounds;
    }

    public Extent getReferenceBounds() {
        return refBounds;
    }

    public Extent getCellResolution() {
        return cellResolution;
    }

    public void clear() {
        if (attributes != null)
            attributes.clear();
    }

    public boolean isDisplayed(Element e) {
        Display display;
        StyleSpecification s = getStyles(e).get(ttsDisplayAttrName);
        if (s != null) {
            String v = s.getValue();
            display = Display.valueOf(v.toUpperCase());
        } else
            display = getDefaults().getDisplay();
        return display != Display.NONE;
    }

    public boolean generatesAnnotationBlock(Element e) {
        if (Documents.isElement(e, ttSpanElementName)) {
            Annotation r = getAnnotation(e);
            return (r != null) && ((r == Annotation.CONTAINER) || (r == Annotation.EMPHASIS));
        } else
            return false;
    }

    public Annotation getAnnotation(Element e) {
        StyleSet styles = getStyles(e);
        StyleSpecification s;
        s = styles.get(ttsRubyAttrName);
        if (s != null)
            return Annotation.fromValue(s.getValue());
        s = styles.get(ttsTextEmphasisAttrName);
        if (s != null)
            return Annotation.EMPHASIS;
        return null;
    }

    public boolean generatesInlineBlock(Element e) {
        if (!Documents.isElement(e, ttSpanElementName))
            return false;
        else {
            StyleSet styles = getStyles(e);
            if (styles.get(ttsTextAlignAttrName) != null)
                return true;
            else if (styles.get(ttsIPDAttrName) != null)
                return true;
            else if (styles.get(ttsBPDAttrName) != null)
                return true;
            else
                return false;
        }
    }

    public void collectParagraphStyles(Element e) {
        StyleSet styles = getStyles(e);
        int begin = -1;
        int end = -1;

        // collect common styles
        collectCommonStyles(e, begin, end, styles);

        // collect paragraph styles
        StyleSpecification s;
        Object v;

        // ANNOTATION_RESERVE
        s = styles.get(ttsRubyReserveAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.TextReserve[] retReserve = new com.skynav.ttv.model.value.TextReserve[1];
            if (com.skynav.ttv.verifier.util.Reserve.isReserve(s.getValue(), new Location(), context, retReserve)) {
                com.skynav.ttv.model.value.TextReserve ar = retReserve[0];
                Extent fs = getFirstAvailableFontSize();
                Length reserve = ar.getReserve();
                double r = (reserve != null) ? Helpers.resolveLength(e, reserve, Axis.VERTICAL, extBounds, refBounds, fs, cellResolution) : -1;
                v = new AnnotationReserve(ar.getPosition().name(), r);
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_RESERVE, v, begin, end);

        // BLOCK_ALIGNMENT
        s = styles.get(ttsDisplayAlignAttrName);
        v = null;
        if (s != null)
            v = BlockAlignment.valueOf(s.getValue().toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.BLOCK_ALIGNMENT, v, begin, end);

        // LINE_SHEAR
        s = styles.get(ttsLineShearAttrName);
        if (s != null) {
            Integer[] minMax = new Integer[] { 1, 1 };
            Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
                assert lengths.size() == 1;
                Length length = lengths.get(0);
                if (length.getUnits() == Length.Unit.Percentage) {
                    v = Double.valueOf(length.getValue() / 100.0);
                    addAttribute(StyleAttribute.LINE_SHEAR, v, begin, end);
                }
            }
        }

        // SHEAR
        s = styles.get(ttsShearAttrName);
        if (s != null) {
            Integer[] minMax = new Integer[] { 1, 1 };
            Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
                assert lengths.size() == 1;
                Length length = lengths.get(0);
                if (length.getUnits() == Length.Unit.Percentage) {
                    v = Double.valueOf(length.getValue() / 100.0);
                    addAttribute(StyleAttribute.SHEAR, v, begin, end);
                }
            }
        }

    }

    public void collectSpanStyles(Element e, int begin, int end) {
        assert (begin < 0) || (end - begin) > 0;
        StyleSet styles = getStyles(e);

        // collect common styles
        collectCommonStyles(e, begin, end);

        // collect non-common span styles
        StyleSpecification s;
        Object v;

        // BACKGROUND_COLOR (paragraph is handled as block presentation state, not outer text style state)
        s = styles.get(ttsBackgroundColorAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (com.skynav.ttv.verifier.util.Colors.isColor(s.getValue(), new Location(), context, retColor))
                v = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
        }
        if (v != null)
            addAttribute(StyleAttribute.BACKGROUND_COLOR, v, begin, end);

        // LANGUAGE
        String xmlLang = Documents.getAttribute(e, xmlLanguageAttrName, null);
        v = null;
        if (xmlLang != null)
            v = xmlLang;
        if (v != null)
            addAttribute(StyleAttribute.LANGUAGE, v, begin, end);

        // WHITESPACE
        String xmlSpace = Documents.getAttribute(e, xmlSpaceAttrName, null);
        v = null;
        if (xmlSpace != null)
            v = Whitespace.valueOf(xmlSpace.toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.WHITESPACE, v, begin, end);

    }

    public void maybeWrapWithBidiControls(Element e) {
        StyleSet styles = getStyles(e);

        // style values
        StyleSpecification s;
        String v;

        // direction
        Direction d = null;
        s = styles.get(ttsDirectionAttrName);
        if (s != null) {
            v = s.getValue().toLowerCase();
            if (v.equals("ltr"))
                d = Direction.LR;
            else if (v.equals("rtl"))
                d = Direction.RL;
        }

        // unicode bidi
        UnicodeBidi u = null;
        s = styles.get(ttsUnicodeBidiAttrName);
        if (s != null) {
            v = s.getValue().toLowerCase();
            if (v.equals("normal"))
                u = UnicodeBidi.NORMAL;
            else if (v.equals("embed"))
                u = UnicodeBidi.EMBED;
            else if (v.equals("bidioverride"))
                u = UnicodeBidi.OVERRIDE;
        }

        int c = 0;
        if ((u != null) && (u != UnicodeBidi.NORMAL) && (d != null)) {
            if (u == UnicodeBidi.EMBED) {
                if (d == Direction.RL)
                    c = Characters.UC_RLE;
                else if (d == Direction.LR)
                    c = Characters.UC_LRE;
            } else if (u == UnicodeBidi.OVERRIDE) {
                if (d == Direction.RL)
                    c = Characters.UC_RLO;
                else if (d == Direction.LR)
                    c = Characters.UC_LRO;
            }
        }
        if (c != 0) {
            NodeList children = e.getChildNodes();
            if (children.getLength() == 1) {
                Node n = children.item(0);
                if (n instanceof Text) {
                    Text t = (Text) n;
                    StringBuffer sb = new StringBuffer();
                    sb.append((char) c);
                    sb.append(t.getWholeText());
                    sb.append((char) Characters.UC_PDF);
                    t.replaceWholeText(sb.toString());
                }
            } else {
                // [TBD] handle multiple children
            }
        }
    }

    public void collectContentStyles(Element e, String content, int begin, int end) {
        int contentLength = content.length();
        if (begin < 0)
            begin = 0;
        if (begin > contentLength)
            begin = contentLength;
        if ((end < 0) || (end > contentLength))
            end = contentLength;
        if (begin > end)
            begin = end;
        collectContentOrientation(e, content, begin, end);
        collectContentBidiLevels(e, content, begin, end);
        collectContentFontRuns(e, content, begin, end);
    }

    public void collectContentOrientation(Element e, String content, int begin, int end) {
        if (isVertical()) {
            StyleSet styles = getStyles(e);
            StyleSpecification s = styles.get(ttsTextOrientationAttrName);
            Orientation eOrientation = null;
            if (s != null)
                eOrientation = Orientation.fromTextOrientation(s.getValue().toUpperCase());
            if (eOrientation == null) { // if null, then element orientation is mixed, so content orientation applies
                int lastBegin = begin;
                int lastEnd = lastBegin;
                Orientation lastOrientation = null;
                for (int i = begin, n = end; i < n; ++i) {
                    int c = content.charAt(i);
                    Orientation o = Orientation.fromCharacter(c);
                    if (o != lastOrientation) {
                        if ((lastOrientation != null) && (lastOrientation != getDefaults().getOrientation()) && (lastEnd > lastBegin))
                            addAttribute(StyleAttribute.ORIENTATION, lastOrientation, lastBegin, i);
                        lastOrientation = o;
                        lastBegin = i;
                    }
                    lastEnd = i + 1;
                }
                if (lastBegin < end) {
                    if ((lastOrientation != null) && (lastOrientation != getDefaults().getOrientation()) && (lastEnd > lastBegin))
                        addAttribute(StyleAttribute.ORIENTATION, lastOrientation, lastBegin, lastEnd);
                }
            }
        }
    }

    private boolean isVertical() {
        return writingMode.getAxis(IPD) == Axis.VERTICAL;
    }

    protected void collectContentBidiLevels(Element e, String content, int begin, int end) {
        int defaultLevel = getDefaultBidiLevel();
        int index = 0;
        for (int limit : getBidiRunLimits(begin, end)) {
            if (index >= end)
                break;
            else if (limit > index)
                collectContentBidiLevels(content, index, limit, defaultLevel);
            index = limit;
        }
    }

    private int getDefaultBidiLevel() {
        if (writingMode.getAxis(IPD) == Axis.VERTICAL)
            return 0;
        else
            return (writingMode.getDirection(IPD) == Direction.RL) ? 1 : 0;
    }

    private static final Set<StyleAttribute> bidiRunBreakingAttributes;
    static {
        Set<StyleAttribute> s = new java.util.HashSet<StyleAttribute>();
        s.add(StyleAttribute.ORIENTATION);
        bidiRunBreakingAttributes = Collections.unmodifiableSet(s);
    }

    private int[] getBidiRunLimits(int begin, int end) {
        Set<Integer> limits = new java.util.TreeSet<Integer>();
        limits.add(begin);
        for (StyleAttributeInterval i : getIntervals(bidiRunBreakingAttributes)) {
            int b = i.getBegin();
            if ((b >= begin) && (b < end))
                limits.add(b);
            int e = i.getEnd();
            if ((e > begin) && (e <= end))
                limits.add(e);
        }
        limits.add(end);
        int[] la = new int[limits.size()];
        int i = 0;
        for (int l : limits)
            la[i++] = l;
        return la;
    }

    protected void collectContentBidiLevels(String content, int begin, int end, int defaultLevel) {
        BidiLevelIterator bi = bidiIterator.setParagraph(content.substring(begin, end), defaultLevel);
        int lastBegin = begin;
        int lastLevel = -1;
        int maxLevel = lastLevel;
        for (int i = bi.first(); i != BidiLevelIterator.DONE; i = bi.next()) {
            int level = bi.level();
            if (level != lastLevel) {
                if (lastLevel >= 0) {
                    addAttribute(StyleAttribute.BIDI, Integer.valueOf(lastLevel), lastBegin, i);
                }
                lastLevel = level;
                lastBegin = i;
            }
            if (level > maxLevel)
                maxLevel = level;
        }
        if (lastBegin < end) {
            if ((lastLevel >= 0) && (maxLevel > 0))
                addAttribute(StyleAttribute.BIDI, Integer.valueOf(lastLevel), lastBegin, end);
        }
    }

    protected void collectContentFontRuns(Element e, String content, int begin, int end) {
        collectContentFontRuns(content, begin, end, getFonts(), getFontSelectionStrategy());
    }

    protected FontSelectionStrategy getFontSelectionStrategy() {
        List<StyleAttributeInterval> intervals = getIntervals(StyleAttribute.FONT_SELECTION_STRATEGY);
        FontSelectionStrategy fontSelectionStrategy = null;
        if (!intervals.isEmpty())
            fontSelectionStrategy = (FontSelectionStrategy) intervals.get(0).getValue();
        if (fontSelectionStrategy == null)
            fontSelectionStrategy = FontSelectionStrategy.getDefaultAutoTreatment();
        return fontSelectionStrategy;
    }

    protected void collectContentFontRuns(String content, int begin, int end, Font[] fonts, FontSelectionStrategy fontSelectionStrategy) {
        FontRunIterator fi = fontIterator.setParagraph(content.substring(begin, end), fonts, fontSelectionStrategy);
        int lastBegin = begin;
        Font lastFont = null;
        for (int i = fi.first(); i != FontRunIterator.DONE; i = fi.next()) {
            Font font = fi.font();
            if (font != lastFont) {
                if (lastFont != null) {
                    addAttribute(StyleAttribute.FONT, lastFont, lastBegin, i);
                }
                lastFont = font;
                lastBegin = i;
            }
        }
        if (lastBegin < end) {
            if (lastFont != null)
                addAttribute(StyleAttribute.FONT, lastFont, lastBegin, end);
        }
    }
    
    public void addEmbedding(Object object, int begin, int end) {
        addAttribute(StyleAttribute.EMBEDDING, object, begin, end);
    }

    public List<StyleAttributeInterval> extract() {
        List<StyleAttributeInterval> attributes = this.attributes;
        this.attributes = null;
        return attributes;
    }

    public StyleSet addStyles(Element e) {
        StyleSet styles = getStyles(e);
        if (styles == StyleSet.EMPTY)
            styles = newStyles(e);
        Documents.setAttribute(e, isdCSSAttrName, styles.getId());
        return styles;
    }

    protected void collectCommonStyles(Element e, int begin, int end) {
        collectCommonStyles(e, begin, end, getStyles(e));
    }

    protected void collectCommonStyles(Element e, int begin, int end, StyleSet styles) {
        assert (begin < 0) || (end - begin) > 0;

        StyleSpecification s;
        Object v;

        // BPD
        s = styles.get(ttsBPDAttrName);
        v = null;
        if (s != null) {
            if (Keywords.isAuto(s.getValue())) {
                v = Double.valueOf(-1);
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
                List<Measure> measures = new java.util.ArrayList<Measure>();
                if (Measures.isMeasures(s.getValue(), new Location(), context, minMax, treatments, measures)) {
                    assert measures.size() == 1;
                    Measure m = measures.get(0);
                    Axis axis = writingMode.getAxis(BPD);
                    Extent fs = getFirstAvailableFontSize();
                    v = Double.valueOf(Helpers.resolveMeasure(e, m, axis, extBounds, refBounds, fs, cellResolution, null));
                }
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.BPD, v, begin, end);

        // COLOR
        Color color = null;
        s = styles.get(ttsColorAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (com.skynav.ttv.verifier.util.Colors.isColor(s.getValue(), new Location(), context, retColor))
                v = color = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
        }
        if (v != null)
            addAttribute(StyleAttribute.COLOR, v, begin, end);

        // FONT
        collectCommonFontStyles(e, begin, end, styles);

        // IPD
        s = styles.get(ttsIPDAttrName);
        v = null;
        if (s != null) {
            if (Keywords.isAuto(s.getValue())) {
                v = Double.valueOf(-1);
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
                List<Measure> measures = new java.util.ArrayList<Measure>();
                if (Measures.isMeasures(s.getValue(), new Location(), context, minMax, treatments, measures)) {
                    assert measures.size() == 1;
                    Measure m = measures.get(0);
                    Axis axis = writingMode.getAxis(IPD);
                    Extent fs = getFirstAvailableFontSize();
                    v = Double.valueOf(Helpers.resolveMeasure(e, m, axis, extBounds, refBounds, fs, cellResolution, null));
                }
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.IPD, v, begin, end);

        // PADDING
        s = styles.get(ttsPaddingAttrName);
        v = null;
        if (s != null) {
            double[] padding = null;
            if (!Keywords.isAuto(s.getValue())) {
                Integer[] minMax = new Integer[] { 1, 4 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
                    Length[] la = lengths.toArray(new Length[lengths.size()]);
                    Extent fs = getFirstAvailableFontSize();
                    padding = Helpers.resolvePadding(e, la, writingMode, extBounds, refBounds, fs, cellResolution);
                }
            }
            if (padding != null)
                v = new Padding(padding);
        }
        if (v != null)
            addAttribute(StyleAttribute.PADDING, v, begin, end);

        // TEXT_ALIGN
        s = styles.get(ttsTextAlignAttrName);
        v = null;
        if (s != null)
            v = InlineAlignment.fromValue(s.getValue());
        if (v != null)
            addAttribute(StyleAttribute.INLINE_ALIGNMENT, v, begin, end);

        // TEXT_COMBINE
        s = styles.get(ttsTextCombineAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.TextCombine[] retCombine = new com.skynav.ttv.model.value.TextCombine[1];
            if (com.skynav.ttv.verifier.util.Combine.isCombine(s.getValue(), new Location(), context, retCombine)) {
                com.skynav.ttv.model.value.TextCombine tc = retCombine[0];
                v = new Combination(tc.getStyle().name(), tc.getCount());
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.COMBINATION, v, begin, end);

        // TEXT_EMPHASIS
        s = styles.get(ttsTextEmphasisAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.TextEmphasis[] retEmphasis = new com.skynav.ttv.model.value.TextEmphasis[1];
            if (com.skynav.ttv.verifier.util.Emphasis.isEmphasis(s.getValue(), new Location(), context, retEmphasis)) {
                com.skynav.ttv.model.value.TextEmphasis te = retEmphasis[0];
                com.skynav.ttv.model.value.Color teColor = te.getColor();
                Color c;
                if ((teColor == null) || teColor.isCurrent())
                    c = null;
                else
                    c = new Color(teColor.getRed(), teColor.getGreen(), teColor.getBlue(), teColor.getAlpha());
                v = new Emphasis(te.getStyle().name(), te.getText(), te.getPosition().name(), c);
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.EMPHASIS, v, begin, end);

        // TEXT_OUTLINE
        s = styles.get(ttsTextOutlineAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.TextOutline[] retOutline = new com.skynav.ttv.model.value.TextOutline[1];
            if (com.skynav.ttv.verifier.util.Outlines.isOutline(s.getValue(), new Location(), context, retOutline)) {
                com.skynav.ttv.model.value.TextOutline to = retOutline[0];
                com.skynav.ttv.model.value.Color toColor = to.getColor();
                Color c = (toColor != null) ? new Color(toColor.getRed(), toColor.getGreen(), toColor.getBlue(), toColor.getAlpha()) : color;
                Extent fs = getFirstAvailableFontSize();
                Length thickness = to.getThickness();
                double t = Helpers.resolveLength(e, thickness, Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
                Length blur = to.getBlur();
                double b = Helpers.resolveLength(e, blur, Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
                v = new Outline(c, t, b);
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.OUTLINE, v, begin, end);

        // TEXT_ORIENTATION
        s = styles.get(ttsTextOrientationAttrName);
        v = null;
        if (s != null)
            v = Orientation.fromTextOrientation(s.getValue().toUpperCase());
        if ((v != null) && isVertical())
            addAttribute(StyleAttribute.ORIENTATION, v, begin, end);

        // VISIBILITY
        s = styles.get(ttsVisibilityAttrName);
        v = null;
        if (s != null)
            v = Visibility.valueOf(s.getValue().toUpperCase());
        else
            v = getDefaultVisibility(e, styles);
        if (v != null)
            addAttribute(StyleAttribute.VISIBILITY, v, begin, end);

        // WRAP
        s = styles.get(ttsWrapOptionAttrName);
        v = null;
        if (s != null)
            v = Wrap.valueOf(s.getValue().toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.WRAP, v, begin, end);

    }

    protected void collectCommonFontStyles(Element e, int begin, int end, StyleSet styles) {
        collectFontStyle(e, begin, end, styles);
        collectFontSelectionStrategyStyle(e, begin, end, styles);
        collectLineHeightStyle(e, begin, end, styles);
    }

    protected void collectFontStyle(Element e, int begin, int end, StyleSet styles) {
        Font[] fonts = getFontsFromStyles(e, styles);
        if (fonts != null) {
            addAttribute(StyleAttribute.FONTS, fonts, begin, end);
            setFonts(fonts);
        }
    }

    protected void collectFontSelectionStrategyStyle(Element e, int begin, int end, StyleSet styles) {
        StyleSpecification s = styles.get(ttsFontSelectionStrategyAttrName);
        Object v;
        if (s != null)
            v = FontSelectionStrategy.valueOf(s.getValue().toUpperCase());
        else
            v = getDefaultFontSelectionStrategy(e, styles);
        if (v.equals(FontSelectionStrategy.AUTO)) {
            FontSelectionStrategy treatFontSelectionStrategyAutoAs =
                (FontSelectionStrategy) getContext().getExternalParameters().getParameter("treatFontSelectionStrategyAutoAs");
            v = treatFontSelectionStrategyAutoAs;
        }
        if (v != null)
            addAttribute(StyleAttribute.FONT_SELECTION_STRATEGY, v, begin, end);
    }

    protected void collectLineHeightStyle(Element e, int begin, int end, StyleSet styles) {
        StyleSpecification s = styles.get(ttsLineHeightAttrName);
        Object v = null;
        if (s != null) {
            Extent fs = getFirstAvailableFontSize();
            if (Keywords.isNormal(s.getValue())) {
                v = Double.valueOf(fs.getDimension(Axis.VERTICAL) * 1.25);
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
                    assert lengths.size() == 1;
                    v = Double.valueOf(Helpers.resolveLength(e, lengths.get(0), Axis.VERTICAL, extBounds, refBounds, fs, cellResolution));
                }
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.LINE_HEIGHT, v, begin, end);
    }

    private Font[] getFontsFromStyles(Element e, StyleSet styles) {
        StyleSpecification s;
        // families
        List<String> fontFamilies = new java.util.ArrayList<String>();
        s = styles.get(ttsFontFamilyAttrName);
        if (s != null) {
            List<FontFamily> families = new java.util.ArrayList<FontFamily>();
            Object[] treatments = new Object[] { QuotedGenericFontFamilyTreatment.Allow };
            if (Fonts.isFontFamilies(s.getValue(), new Location(), context, treatments, families)) {
                if (!families.isEmpty()) {
                    List<String> familyNames =  new java.util.ArrayList<String>();
                    for (FontFamily family : families)
                        familyNames.add(family.toString());
                    fontFamilies = familyNames;
                }
            }
        }
        for (String familyName : getDefaultFontFamilies(e, styles)) {
            if (!fontFamilies.contains(familyName))
                fontFamilies.add(familyName);
        }
        // style
        FontStyle fontStyle = null;
        s = styles.get(ttsFontStyleAttrName);
        if (s != null)
            fontStyle = FontStyle.valueOf(s.getValue().toUpperCase());
        if (fontStyle == null)
            fontStyle = getDefaultFontStyle(e, styles);
        // weight
        FontWeight fontWeight = null;
        s = styles.get(ttsFontWeightAttrName);
        if (s != null)
            fontWeight = FontWeight.valueOf(s.getValue().toUpperCase());
        if (fontWeight == null)
            fontWeight = getDefaultFontWeight(e, styles);
        // size
        Extent fontSize = null;
        s = styles.get(ttsFontSizeAttrName);
        if (s != null) {
            Extent fs = parseFontSize(e, s);
            if (fs != null)
                fontSize = fs;
        }
        if (fontSize == null)
            fontSize = getDefaultFontSize(e, styles);
        // features
        Set<FontFeature> fontFeatures = new java.util.HashSet<FontFeature>();
        // variant features
        s = styles.get(ttsFontVariantAttrName);
        if (s != null) {
            Set<FontVariant> variants = new java.util.HashSet<FontVariant>();
            if (Fonts.isFontVariants(s.getValue(), new Location(), context, variants)) {
                if (!variants.isEmpty()) {
                    for (FontVariant fv : variants) {
                        FontFeature f = FontFeature.fromVariant(fv);
                        if (f != null)
                            fontFeatures.add(f);
                    }
                }
            }
        }
        // shear feature
        s = styles.get(ttsFontShearAttrName);
        if (s != null) {
            Integer[] minMax = new Integer[] { 1, 1 };
            Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
                assert lengths.size() == 1;
                Length length = lengths.get(0);
                if (length.getUnits() == Length.Unit.Percentage)
                    fontFeatures.add(new FontFeature("oblq", new Object[]{Double.valueOf(length.getValue() / 100.0)}));
            }
        }
        // kerning feature
        s = styles.get(ttsFontKerningAttrName);
        if (s != null) {
            FontKerning k = FontKerning.valueOf(s.getValue().toUpperCase());
            fontFeatures.add(new FontFeature("kern", new Object[]{k}));
        }
        if (fontFeatures.isEmpty())
            fontFeatures = getDefaultFontFeatures(e, styles);
        List<Font> mappedFonts = new java.util.ArrayList<Font>();
        if (!fontFamilies.isEmpty()) {
            List<String> ff =  new java.util.ArrayList<String>(1);
            for (String family : fontFamilies) {
                ff.clear();
                ff.add(family);
                Font f = getFontCache().mapFont(ff, fontStyle, fontWeight, language, writingMode.getAxis(IPD), fontSize, fontFeatures);
                if (f != null)
                    mappedFonts.add(f);
            }
        } else {
            Font f = getFontCache().mapFont(fontFamilies, fontStyle, fontWeight, language, writingMode.getAxis(IPD), fontSize, fontFeatures);
            if (f != null)
                mappedFonts.add(f);
        }
        if (!mappedFonts.isEmpty())
            return mappedFonts.toArray(new Font[mappedFonts.size()]);
        else
            return null;
    }

    protected StyleSet getStyles(Element e) {
        String css = Documents.getAttribute(e, isdCSSAttrName, null);
        return (css != null) ? getStyles(css) : StyleSet.EMPTY;
    }

    protected StyleSet getStyles(String css) {
        assert css != null;
        String[] ids = css.trim().split("\\s+");
        if (ids.length < 1)
            return StyleSet.EMPTY;
        else if (ids.length < 2)
            return this.styles.get(ids[0]);
        else
            return mergeStyles(ids);
    }

    private StyleSet mergeStyles(String[] ids) {
        StyleSet styles = new StyleSet();
        for (String id : ids)
            styles.merge(getStyles(id), getContext().getConditionEvaluatorState());
        return styles;
    }

    protected StyleSet newStyles(Element e) {
        StyleSet styles = new StyleSet();
        String id = generateSynthesizedStylesId();
        styles.setId(id);
        this.styles.put(id, styles);
        return styles;
    }

    private String generateSynthesizedStylesId() {
        StringBuffer sb = new StringBuffer();
        sb.append('_');
        sb.append(synthesizedStylesIndex++);
        return sb.toString();
    }

    protected void addAttribute(StyleAttribute attribute, Object value, int begin, int end) {
        assert (begin < 0) || (end - begin) > 0;
        if (attributes == null)
            attributes = new java.util.ArrayList<StyleAttributeInterval>();
        attributes.add(new StyleAttributeInterval(attribute, value, begin, end));
    }

    protected List<StyleAttributeInterval> getIntervals(Set<StyleAttribute> attributes) {
        List<StyleAttributeInterval> intervals = new java.util.ArrayList<StyleAttributeInterval>();
        if (this.attributes != null) {
            for (StyleAttributeInterval i : this.attributes) {
                if (attributes.contains(i.getAttribute()))
                    intervals.add(i);
            }
        }
        return intervals;
    }

    protected List<StyleAttributeInterval> getIntervals(StyleAttribute attribute) {
        List<StyleAttributeInterval> intervals = new java.util.ArrayList<StyleAttributeInterval>();
        if (this.attributes != null) {
            for (StyleAttributeInterval i : this.attributes) {
                if (i.getAttribute().equals(attribute))
                    intervals.add(i);
            }
        }
        return intervals;
    }

    protected List<String> getDefaultFontFamilies(Element e, StyleSet styles) {
        return getDefaults().getFontFamilies();
    }

    protected FontSelectionStrategy getDefaultFontSelectionStrategy(Element e, StyleSet styles) {
        return getDefaults().getFontSelectionStrategy();
    }

    protected FontStyle getDefaultFontStyle(Element e, StyleSet styles) {
        return getDefaults().getFontStyle();
    }

    protected FontWeight getDefaultFontWeight(Element e, StyleSet styles) {
        return getDefaults().getFontWeight();
    }

    protected Extent getDefaultFontSize(Element e, StyleSet styles) {
        return getDefaults().getFontSize();
    }

    protected Set<FontFeature> getDefaultFontFeatures(Element e, StyleSet styles) {
        return getDefaults().getFontFeatures();
    }

    protected Visibility getDefaultVisibility(Element e, StyleSet styles) {
        Visibility v = null;
        boolean forcedDisplay = (Boolean) getContext().getExternalParameters().getParameter("forcedDisplay");
        if (forcedDisplay) {
            if (Documents.isElement(e, ttSpanElementName)) {
                for (Node n = e.getParentNode(); n != null; n = n.getParentNode()) {
                    if (n instanceof Element) {
                        Element p = (Element) n;
                        StyleSet pStyles = getStyles(p);
                        StyleSpecification s = pStyles.get(ttsVisibilityAttrName);
                        if (s != null) {
                            v = Visibility.valueOf(s.getValue().toUpperCase());
                            break;
                        } else if (Documents.isElement(p, ttSpanElementName)) {
                            continue;
                        } else {
                            break;
                        }
                    }
                }
            }
        }
        if (v == null)
            v = getDefaults().getVisibility();
        return v;
    }

    protected Extent parseFontSize(Element e, StyleSpecification s) {
        Integer[] minMax = new Integer[] { 1, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
            assert lengths.size() > 0;
            Extent fs = getFirstAvailableFontSize();
            Extent refBounds = this.refBounds;
            if (!Documents.isElement(e, isdRegionElementName))
                refBounds = fs;
            double w, h;
            if (lengths.size() == 1) {
                h = Helpers.resolveLength(e, lengths.get(0), Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
                w = h;
            } else {
                w = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, extBounds, refBounds, fs, cellResolution);
                h = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
            }
            return new Extent(w, h);
        } else
            return null;
    }

}
