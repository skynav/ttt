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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.fonts.FontFeature;
import com.skynav.ttpe.fonts.FontKerning;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.model.value.FontVariant;
import com.skynav.ttv.model.value.Length;
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
    protected Extent extBounds;
    protected Extent refBounds;
    protected WritingMode writingMode;
    protected String language;
    protected Font font;
    private Map<String,StyleSet> styles;
    private List<StyleAttributeInterval> attributes;

    public StyleCollector(StyleCollector sc) {
        this(sc.context, sc.fontCache, sc.extBounds, sc.refBounds, sc.writingMode, sc.language, sc.font, sc.styles);
    }

    public StyleCollector
        (TransformerContext context, FontCache fontCache, Extent extBounds, Extent refBounds, WritingMode writingMode, String language, Font font, Map<String,StyleSet> styles) {
        this.context = context;
        this.fontCache = fontCache;
        this.extBounds = extBounds;
        this.refBounds = refBounds;
        this.writingMode = writingMode;
        this.language = language;
        this.font = font;
        this.styles = styles;
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
            return (r != null) && (r == Annotation.CONTAINER);
        } else
            return false;
    }

    public Annotation getAnnotation(Element e) {
        StyleSet styles = getStyles(e);
        StyleSpecification s = styles.get(ttsRubyAttrName);
        if (s != null)
            return Annotation.fromValue(s.getValue());
        else
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

    public void addEmbedding(Object object, int begin, int end) {
        addAttribute(StyleAttribute.EMBEDDING, object, begin, end);
    }

    public List<StyleAttributeInterval> extract() {
        List<StyleAttributeInterval> attributes = this.attributes;
        this.attributes = null;
        return attributes;
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
            if (com.skynav.ttv.verifier.util.Colors.isColor(s.getValue(), null, null, retColor))
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

        // TEXT_EMPHASIS
        s = styles.get(ttsTextEmphasisAttrName);
        v = null;
        if (s != null) {
            com.skynav.ttv.model.value.TextEmphasis[] retEmphasis = new com.skynav.ttv.model.value.TextEmphasis[1];
            if (com.skynav.ttv.verifier.util.Emphasis.isEmphasis(s.getValue(), null, null, retEmphasis)) {
                com.skynav.ttv.model.value.TextEmphasis te = retEmphasis[0];
                com.skynav.ttv.model.value.Color teColor = te.getColor();
                Color c = (teColor != null) ? new Color(teColor.getRed(), teColor.getGreen(), teColor.getBlue(), teColor.getAlpha()) : color;
                v = new Emphasis(te.getStyle().name(), te.getText(), te.getPosition().name(), c);
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.EMPHASIS, v, begin, end);

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
            if (Keywords.isNormal(s.getValue())) {
                v = Double.valueOf(font.getSize(Axis.VERTICAL) * 1.25);
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(s.getValue(), null, context, minMax, treatments, lengths)) {
                    assert lengths.size() == 1;
                    v = Double.valueOf(Helpers.resolveLength(e, lengths.get(0), Axis.VERTICAL, extBounds, refBounds, font.getSize()));
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
            if (Fonts.isFontFamilies(s.getValue(), null, context, treatments, families)) {
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
            if (Fonts.isFontVariants(s.getValue(), null, context, variants)) {
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
            if (Lengths.isLengths(s.getValue(), null, context, minMax, treatments, lengths)) {
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
        if ((fontFeatures == null) || fontFeatures.isEmpty())
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

    protected void addAttribute(StyleAttribute attribute, Object value, int begin, int end) {
        if (attributes == null)
            attributes = new java.util.ArrayList<StyleAttributeInterval>();
        attributes.add(new StyleAttributeInterval(attribute, value, begin, end));
    }

    protected List<String> getDefaultFontFamilies(Element e, StyleSet styles) {
        return defaultFontFamilies;
    }

    protected FontStyle getDefaultFontStyle(Element e, StyleSet styles) {
        return defaultFontStyle;
    }

    protected FontWeight getDefaultFontWeight(Element e, StyleSet styles) {
        return defaultFontWeight;
    }

    protected Extent getDefaultFontSize(Element e, StyleSet styles) {
        return defaultFontSize;
    }

    protected Set<FontFeature> getDefaultFontFeatures(Element e, StyleSet styles) {
        return defaultFontFeatures;
    }

    protected Extent parseFontSize(Element e, StyleSpecification s) {
        Integer[] minMax = new Integer[] { 1, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(s.getValue(), null, context, minMax, treatments, lengths)) {
            assert lengths.size() > 0;
            double h = Helpers.resolveLength(e, lengths.get(0), Axis.VERTICAL, extBounds, refBounds, font.getSize());
            if (lengths.size() == 1)
                lengths.add(lengths.get(0));
            double w = Helpers.resolveLength(e, lengths.get(1), Axis.HORIZONTAL, extBounds, refBounds, font.getSize());
            return new Extent(w, h);
        } else
            return null;
    }

}
