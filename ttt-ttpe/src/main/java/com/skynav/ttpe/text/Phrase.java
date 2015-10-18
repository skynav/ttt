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

package com.skynav.ttpe.text;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.style.AnnotationOverflow;
import com.skynav.ttpe.style.AnnotationOverhang;
import com.skynav.ttpe.style.AnnotationOverhangClass;
import com.skynav.ttpe.style.AnnotationPosition;
import com.skynav.ttpe.style.AnnotationReserve;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.Emphasis;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Outline;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.Wrap;
import com.skynav.ttpe.util.AttributedStrings;
import com.skynav.ttpe.util.Characters;

public class Phrase {

    protected Element element;                                  // element that generated content
    protected Map<StyleAttribute,Object> attributes;            // attributes that apply to content as a whole
    protected AttributedString content;                         // attributed content

    public Phrase(Element e, List<Phrase> phrases, List<StyleAttributeInterval> attributes) {
        this(e, (String) null, attributes);
        append(phrases);
    }

    public Phrase(Element e, String text, List<StyleAttributeInterval> attributes) {
        this.element = e;
        if (text == null)
            text = "";
        this.content = new AttributedString(text);
        this.attributes = new java.util.HashMap<StyleAttribute,Object>();
        if (attributes != null)
            add(attributes, text.length());
    }

    public Element getElement() {
        return element;
    }

    public void append(Phrase p) {
        List<Phrase> phrases = new java.util.ArrayList<Phrase>(1);
        phrases.add(p);
        append(phrases);
    }

    public void append(List<Phrase> phrases) {
        if ((phrases != null) && !phrases.isEmpty()) {
            int np = 1 + phrases.size();
            AttributedCharacterIterator[] acis = new AttributedCharacterIterator[np];
            int ni = 0;
            acis[ni++] = getIterator();
            for (Phrase p : phrases)
                acis[ni++] = p.getIterator();
            int[] contentIndices = new int[np + 1];
            content = AttributedStrings.concat(acis, contentIndices);
            for (int i = 0; i < np; ++i) {
                Phrase p = (i == 0) ? this : phrases.get(i - 1);
                int b = contentIndices[i + 0];
                int e = contentIndices[i + 1];
                if (e > b) {
                    Map<StyleAttribute,Object> attributes = p.attributes;
                    if ((attributes != null) && (attributes.size() > 0)) {
                        for (Map.Entry<StyleAttribute,Object> entry : attributes.entrySet()) {
                            StyleAttribute a = entry.getKey();
                            // FIXME - should not special case OUTLINE here, but handle all attributes; however,
                            // using addAttributes() to add all attributes causes a regression due to apparent
                            // overwrite of FONT attribute; need to selectively add attribute only if it is not
                            // already present
                            if (a.equals(StyleAttribute.OUTLINE)) {
                                content.addAttribute(a, entry.getValue(), b, e);
                            }
                        }
                    }
                }
            }
        }
    }

    public AttributedCharacterIterator getIterator() {
        return content.getIterator();
    }

    public int length() {
        AttributedCharacterIterator aci = getIterator();
        return aci.getEndIndex() - aci.getBeginIndex();
    }

    public boolean isWhitespace() {
        boolean rv = true;
        AttributedCharacterIterator aci = getIterator();
        int savedIndex = aci.getIndex();
        for (int i = aci.getBeginIndex(), n = aci.getEndIndex(); rv && (i < n); ++i) {
            char c = aci.setIndex(i);
            if (!Characters.isWhitespace(c))
                rv = false;
        }
        aci.setIndex(savedIndex);
        return rv;
    }

    public boolean isEmbedding() {
        boolean rv = true;
        AttributedCharacterIterator aci = getIterator();
        int savedIndex = aci.getIndex();
        int b = aci.getBeginIndex();
        int e = aci.getEndIndex();
        if ((e - b) != 1)
            rv = false;
        else if (aci.setIndex(b) != Characters.UC_OBJECT)
            rv = false;
        aci.setIndex(savedIndex);
        return rv;
    }

    private static final StyleAttribute[] annotationAttr = new StyleAttribute[] { StyleAttribute.ANNOTATIONS };
    public Phrase[] getAnnotations(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationAttr[0]);
        else
            v = content.getIterator(annotationAttr, index, index + 1).getAttribute(annotationAttr[0]);
        if (v == null)
            v = defaults.getAnnotation();
        if (v instanceof Phrase[])
            return (Phrase[]) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationAlignAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_ALIGNMENT };
    public InlineAlignment getAnnotationAlign(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationAlignAttr[0]);
        else
            v = content.getIterator(annotationAlignAttr, index, index + 1).getAttribute(annotationAlignAttr[0]);
        if (v == null)
            v = defaults.getAnnotationAlign();
        if (v instanceof InlineAlignment)
            return (InlineAlignment) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationOffsetAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_OFFSET };
    public Double getAnnotationOffset(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationOffsetAttr[0]);
        else
            v = content.getIterator(annotationOffsetAttr, index, index + 1).getAttribute(annotationOffsetAttr[0]);
        if (v == null)
            v = defaults.getAnnotationOffset();
        if (v instanceof Double)
            return (Double) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationOverflowAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_OVERFLOW };
    public AnnotationOverflow getAnnotationOverflow(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationOverflowAttr[0]);
        else
            v = content.getIterator(annotationOverflowAttr, index, index + 1).getAttribute(annotationOverflowAttr[0]);
        if (v == null)
            v = defaults.getAnnotationOverflow();
        if (v instanceof AnnotationOverflow)
            return (AnnotationOverflow) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationOverhangAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_OVERHANG };
    public AnnotationOverhang getAnnotationOverhang(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationOverhangAttr[0]);
        else
            v = content.getIterator(annotationOverhangAttr, index, index + 1).getAttribute(annotationOverhangAttr[0]);
        if (v == null)
            v = defaults.getAnnotationOverhang();
        if (v instanceof AnnotationOverhang)
            return (AnnotationOverhang) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationOverhangClassAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_OVERHANG_CLASS };
    public AnnotationOverhangClass getAnnotationOverhangClass(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationOverhangClassAttr[0]);
        else
            v = content.getIterator(annotationOverhangClassAttr, index, index + 1).getAttribute(annotationOverhangClassAttr[0]);
        if (v == null)
            v = defaults.getAnnotationOverhangClass();
        if (v instanceof AnnotationOverhangClass)
            return (AnnotationOverhangClass) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationPositionAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_POSITION };
    public AnnotationPosition getAnnotationPosition(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationPositionAttr[0]);
        else
            v = content.getIterator(annotationPositionAttr, index, index + 1).getAttribute(annotationPositionAttr[0]);
        if (v == null)
            v = defaults.getAnnotationPosition();
        if (v instanceof AnnotationPosition)
            return (AnnotationPosition) v;
        else
            return null;
    }

    private static final StyleAttribute[] annotationReserveAttr = new StyleAttribute[] { StyleAttribute.ANNOTATION_RESERVE };
    public AnnotationReserve getAnnotationReserve(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(annotationReserveAttr[0]);
        else
            v = content.getIterator(annotationReserveAttr, index, index + 1).getAttribute(annotationReserveAttr[0]);
        if (v == null)
            v = defaults.getAnnotationReserve();
        if (v instanceof AnnotationReserve)
            return (AnnotationReserve) v;
        else
            return null;
    }

    private static final StyleAttribute[] colorAttr = new StyleAttribute[] { StyleAttribute.COLOR };
    public Color getColor(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(colorAttr[0]);
        else
            v = content.getIterator(colorAttr, index, index + 1).getAttribute(colorAttr[0]);
        if (v == null)
            v = defaults.getColor();
        if (v instanceof Color)
            return (Color) v;
        else
            return null;
    }

    private static final StyleAttribute[] emphasisAttr = new StyleAttribute[] { StyleAttribute.EMPHASIS };
    public Emphasis getEmphasis(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(emphasisAttr[0]);
        else
            v = content.getIterator(emphasisAttr, index, index + 1).getAttribute(emphasisAttr[0]);
        if (v instanceof Emphasis)
            return (Emphasis) v;
        else
            return null;
    }

    private static final StyleAttribute[] fontAttr = new StyleAttribute[] { StyleAttribute.FONT };
    public Font getFont(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(fontAttr[0]);
        else
            v = content.getIterator(fontAttr, index, index + 1).getAttribute(fontAttr[0]);
        if (v instanceof Font)
            return (Font) v;
        else
            return null;
    }

    private static final StyleAttribute[] languageAttr = new StyleAttribute[] { StyleAttribute.LANGUAGE };
    public String getLanguage(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(languageAttr[0]);
        else
            v = content.getIterator(languageAttr, index, index + 1).getAttribute(languageAttr[0]);
        if (v == null)
            v = defaults.getLanguage();
        if (v instanceof String)
            return (String) v;
        else
            return null;
    }

    private static final StyleAttribute[] lineHeightAttr = new StyleAttribute[] { StyleAttribute.LINE_HEIGHT };
    public Double getLineHeight(int index, Defaults defaults, Font font) {
        Object v;
        if (index < 0)
            v = attributes.get(lineHeightAttr[0]);
        else
            v = content.getIterator(lineHeightAttr, index, index + 1).getAttribute(lineHeightAttr[0]);
        if (v == null) {
            if (font != null)
                v = font.getDefaultLineHeight();
            else
                v = defaults.getLineHeight();
        }
        if (v instanceof Double)
            return (Double) v;
        else
            return null;
    }

    private static final StyleAttribute[] outlineAttr = new StyleAttribute[] { StyleAttribute.OUTLINE };
    public Outline getOutline(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(outlineAttr[0]);
        else
            v = content.getIterator(outlineAttr, index, index + 1).getAttribute(outlineAttr[0]);
        if (v == null)
            v = defaults.getOutline();
        if (v instanceof Outline)
            return (Outline) v;
        else
            return null;
    }

    private static final StyleAttribute[] scriptAttr = new StyleAttribute[] { StyleAttribute.SCRIPT };
    public String getScript(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(scriptAttr[0]);
        else
            v = content.getIterator(scriptAttr, index, index + 1).getAttribute(scriptAttr[0]);
        if (v == null)
            v = defaults.getScript();
        if (v instanceof String)
            return (String) v;
        else
            return null;
    }

    private static final StyleAttribute[] textAlignAttr = new StyleAttribute[] { StyleAttribute.INLINE_ALIGNMENT };
    public InlineAlignment getTextAlign(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(textAlignAttr[0]);
        else
            v = content.getIterator(textAlignAttr, index, index + 1).getAttribute(textAlignAttr[0]);
        if (v == null)
            v = defaults.getTextAlign();
        if (v instanceof InlineAlignment)
            return (InlineAlignment) v;
        else
            return null;
    }

    private static final StyleAttribute[] wrapOptionAttr = new StyleAttribute[] { StyleAttribute.WRAP };
    public Wrap getWrapOption(int index, Defaults defaults) {
        Object v;
        if (index < 0)
            v = attributes.get(wrapOptionAttr[0]);
        else
            v = content.getIterator(wrapOptionAttr, index, index + 1).getAttribute(wrapOptionAttr[0]);
        if (v == null)
            v = defaults.getWrap();
        if (v instanceof Wrap)
            return (Wrap) v;
        else
            return null;
    }

    public void add(List<StyleAttributeInterval> attributes, int maxIndex) {
        for (StyleAttributeInterval tai : attributes)
            add(tai, maxIndex);
    }

    public void add(StyleAttributeInterval tai, int maxIndex) {
        if (tai.getEnd() > maxIndex)
            throw new IllegalArgumentException();
        else if (!tai.isOuterScope())
            add(tai.getAttribute(), tai.getValue(), tai.getBegin(), tai.getEnd());
        else
            add(tai.getAttribute(), tai.getValue());
    }

    public void add(StyleAttribute attribute, Object value) {
        attributes.put(attribute, value);
    }

    public void add(StyleAttribute attribute, Object value, int begin, int end) {
        content.addAttribute(attribute, value, begin, end);
    }

}
