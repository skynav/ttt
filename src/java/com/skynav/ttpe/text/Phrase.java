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
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.Wrap;
import com.skynav.ttpe.util.AttributedStrings;

import static com.skynav.ttpe.style.Constants.*;

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
        content = AttributedStrings.concat(new AttributedCharacterIterator[] {getIterator(), p.getIterator()});
    }

    public void append(List<Phrase> phrases) {
        if ((phrases != null) && !phrases.isEmpty()) {
            AttributedCharacterIterator[] acis = new AttributedCharacterIterator[1 + phrases.size()];
            int ni = 0;
            acis[ni++] = getIterator();
            for (Phrase p : phrases)
                acis[ni++] = p.getIterator();
            content = AttributedStrings.concat(acis);
        }
    }

    public AttributedCharacterIterator getIterator() {
        return content.getIterator();
    }

    public int length() {
        AttributedCharacterIterator aci = getIterator();
        return aci.getEndIndex() - aci.getBeginIndex();
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

    public Wrap getWrapOption(int index) {
        return defaultWrap;
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
