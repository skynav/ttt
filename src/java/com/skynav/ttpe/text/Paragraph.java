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
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.Wrap;

import static com.skynav.ttpe.style.Constants.*;

public class Paragraph {

    private Element element;                                    // element that generated paragraph
    private Map<StyleAttribute,Object> attributes;               // attributes that apply to paragraph as a whole
    private AttributedString content;                           // attributed content

    public Paragraph(Element e, String text, List<StyleAttributeInterval> attributes) {
        this.element = e;
        this.content = new AttributedString(text);
        add(attributes, text.length());
    }

    public Element getElement() {
        return element;
    }

    public AttributedCharacterIterator getIterator() {
        return content.getIterator();
    }

    private static final StyleAttribute[] colorAttr = new StyleAttribute[] { StyleAttribute.COLOR };
    public Color getColor(int index) {
        Object v;
        if (index < 0)
            v = attributes.get(colorAttr[0]);
        else
            v = content.getIterator(colorAttr, index, index + 1).getAttribute(colorAttr[0]);
        if (v == null)
            v = defaultColor;
        assert v instanceof Color;
        return (Color) v;
    }

    public BlockAlignment getDisplayAlign(int index) {
        return defaultDisplayAlign;
    }

    public String getFontFamily(int index) {
        return defaultFontFamily;
    }

    public Extent getFontSize(int index) {
        return defaultFontSize;
    }

    public FontStyle getFontStyle(int index) {
        return defaultFontStyle;
    }

    public FontWeight getFontWeight(int index) {
        return defaultFontWeight;
    }

    public double getLineHeight(int index, Font font) {
        return defaultLineHeight;
    }

    private static final StyleAttribute[] textAlignAttr = new StyleAttribute[] { StyleAttribute.INLINE_ALIGNMENT };
    public InlineAlignment getTextAlign(int index) {
        Object v;
        if (index < 0)
            v = attributes.get(textAlignAttr[0]);
        else
            v = content.getIterator(textAlignAttr, index, index + 1).getAttribute(textAlignAttr[0]);
        if (v == null)
            v = defaultTextAlign;
        assert v instanceof InlineAlignment;
        return (InlineAlignment) v;
    }

    public Wrap getWrapOption(int index) {
        return defaultWrap;
    }

    private void add(List<StyleAttributeInterval> attributes, int maxIndex) {
        for (StyleAttributeInterval tai : attributes)
            add(tai, maxIndex);
    }

    private void add(StyleAttributeInterval tai, int maxIndex) {
        if (tai.getEnd() > maxIndex)
            throw new IllegalArgumentException();
        else if (!tai.isOuterScope())
            content.addAttribute(tai.getAttribute(), tai.getValue(), tai.getBegin(), tai.getEnd());
        else {
            if (attributes == null)
                attributes = new java.util.HashMap<StyleAttribute,Object>();
            attributes.put(tai.getAttribute(), tai.getValue());
        }
    }

}
