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

package com.skynav.ttpe.style;

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
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.verifier.util.Fonts;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.QuotedGenericFontFamilyTreatment;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.geometry.Dimension.*;
import static com.skynav.ttpe.style.Constants.*;
import static com.skynav.ttpe.text.Constants.*;

public class StyleCollector {

    protected TransformerContext context;
    protected FontCache fontCache;
    protected Defaults defaults;
    protected Extent extBounds;
    protected Extent refBounds;
    protected Extent cellResolution;
    protected WritingMode writingMode;
    protected String language;
    protected Font font;
    private int synthesizedStylesIndex;
    private Map<String,StyleSet> styles;
    private List<StyleAttributeInterval> attributes;
    private BidiLevelIterator bidi;

    public StyleCollector(StyleCollector sc) {
        this(sc.context, sc.fontCache, sc.defaults, sc.extBounds, sc.refBounds, sc.cellResolution, sc.writingMode, sc.language, sc.font, sc.styles);
    }

    public StyleCollector
        (TransformerContext context, FontCache fontCache, Defaults defaults, Extent extBounds, Extent refBounds, Extent cellResolution, WritingMode writingMode, String language, Font font, Map<String,StyleSet> styles) {
        this.context = context;
        this.fontCache = fontCache;
        this.defaults = defaults;
        this.extBounds = extBounds;
        this.refBounds = refBounds;
        this.cellResolution = cellResolution;
        this.writingMode = writingMode;
        this.language = language;
        this.font = font;
        this.styles = styles;
        this.bidi = new BidiLevelIterator();
    }

    public Defaults getDefaults() {
        return defaults;
    }

    protected void setFont(Font font) {
        this.font = font;
    }

    public void clear() {
        if (attributes != null)
            attributes.clear();
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
                Extent fs = (font != null) ? font.getSize() : Extent.UNIT;
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
    }

    public void collectSpanStyles(Element e, int begin, int end) {
        collectCommonStyles(e, begin, end);
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
            }
        }
    }

    public void collectContentStyles(String content, int begin, int end) {
        int contentLength = content.length();
        if (begin < 0)
            begin = 0;
        if (begin > contentLength)
            begin = contentLength;
        if ((end < 0) || (end > contentLength))
            end = contentLength;
        if (begin > end)
            begin = end;
        collectContentOrientation(content, begin, end);
        collectContentBidiLevels(content, begin, end);
    }

    public void collectContentOrientation(String content, int begin, int end) {
        if (isVertical()) {
            int lastBegin = begin;
            int lastEnd = lastBegin;
            Orientation lastOrientation = null;
            for (int i = begin, n = end; i < n; ++i) {
                int c = content.charAt(i);
                Orientation o = Orientation.fromCharacter(c);
                if (o != lastOrientation) {
                    if ((lastOrientation != null) && (lastOrientation != defaults.getOrientation()) && (lastEnd > lastBegin))
                        addAttribute(StyleAttribute.ORIENTATION, lastOrientation, lastBegin, i);
                    lastOrientation = o;
                    lastBegin = i;
                }
                lastEnd = i + 1;
            }
            if (lastBegin < end) {
                if ((lastOrientation != null) && (lastOrientation != defaults.getOrientation()) && (lastEnd > lastBegin))
                    addAttribute(StyleAttribute.ORIENTATION, lastOrientation, lastBegin, lastEnd);
            }
        }
    }

    private boolean isVertical() {
        return writingMode.getAxis(IPD) == Axis.VERTICAL;
    }

    protected void collectContentBidiLevels(String content, int begin, int end) {
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
        BidiLevelIterator bi = bidi.setParagraph(content.substring(begin, end), defaultLevel);
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

        StyleSpecification s;
        Object v;

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
                Color c = (teColor != null) ? new Color(teColor.getRed(), teColor.getGreen(), teColor.getBlue(), teColor.getAlpha()) : color;
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
            if (com.skynav.ttv.verifier.util.Outline.isOutline(s.getValue(), new Location(), context, retOutline)) {
                com.skynav.ttv.model.value.TextOutline to = retOutline[0];
                com.skynav.ttv.model.value.Color toColor = to.getColor();
                Color c = (toColor != null) ? new Color(toColor.getRed(), toColor.getGreen(), toColor.getBlue(), toColor.getAlpha()) : color;
                Extent fs = (font != null) ? font.getSize() : Extent.UNIT;
                Length thickness = to.getThickness();
                double t = Helpers.resolveLength(e, thickness, Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
                Length blur = to.getBlur();
                double b = Helpers.resolveLength(e, blur, Axis.VERTICAL, extBounds, refBounds, fs, cellResolution);
                v = new Outline(c, t, b);
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.OUTLINE, v, begin, end);

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
        collectLineHeightStyle(e, begin, end, styles);
    }

    protected void collectFontStyle(Element e, int begin, int end, StyleSet styles) {
        Font f = getFontFromStyles(e, styles);
        if (f != null) {
            addAttribute(StyleAttribute.FONT, f, begin, end);
            setFont(f);
        }
    }

    protected void collectLineHeightStyle(Element e, int begin, int end, StyleSet styles) {
        StyleSpecification s = styles.get(ttsLineHeightAttrName);
        Object v = null;
        if (s != null) {
            Extent fs = (font != null) ? font.getSize() : Extent.UNIT;
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

    private Font getFontFromStyles(Element e, StyleSet styles) {
        StyleSpecification s;
        // families
        List<String> fontFamilies = null;
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
        if (fontFamilies == null)
            fontFamilies = getDefaultFontFamilies(e, styles);
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
        return fontCache.mapFont(fontFamilies, fontStyle, fontWeight, language, writingMode.getAxis(IPD), fontSize, fontFeatures);
    }

    protected StyleSet getStyles(Element e) {
        String style = Documents.getAttribute(e, isdCSSAttrName, null);
        if (style != null) {
            StyleSet styles = this.styles.get(style);
            if (styles != null)
                return styles;
            else
                return StyleSet.EMPTY;
        } else
            return StyleSet.EMPTY;
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
        return defaults.getFontFamilies();
    }

    protected FontStyle getDefaultFontStyle(Element e, StyleSet styles) {
        return defaults.getFontStyle();
    }

    protected FontWeight getDefaultFontWeight(Element e, StyleSet styles) {
        return defaults.getFontWeight();
    }

    protected Extent getDefaultFontSize(Element e, StyleSet styles) {
        return defaults.getFontSize();
    }

    protected Set<FontFeature> getDefaultFontFeatures(Element e, StyleSet styles) {
        return defaults.getFontFeatures();
    }

    protected Extent parseFontSize(Element e, StyleSpecification s) {
        Integer[] minMax = new Integer[] { 1, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(s.getValue(), new Location(), context, minMax, treatments, lengths)) {
            assert lengths.size() > 0;
            Extent fs = (font != null) ? font.getSize() : Extent.UNIT;
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
