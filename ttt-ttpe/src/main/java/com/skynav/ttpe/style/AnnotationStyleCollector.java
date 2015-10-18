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

import org.w3c.dom.Element;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttv.model.value.CharacterClass;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.verifier.util.Characters;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

import static com.skynav.ttpe.style.Constants.*;

public class AnnotationStyleCollector extends StyleCollector {

    private Element base;

    public AnnotationStyleCollector(StyleCollector sc, Element base) {
        super(sc);
        this.base = base;
    }

    @Override
    protected void collectCommonStyles(Element e, int begin, int end, StyleSet styles) {
        super.collectCommonStyles(e, begin, end, styles);
        collectCommonAnnotationStyles(e, begin, end, styles);
    }

    @Override
    protected void collectFontStyle(Element e, int begin, int end, StyleSet styles) {
        super.collectFontStyle(e, begin, end, styles);
    }

    @Override
    protected Extent getDefaultFontSize(Element e, StyleSet styles) {
        Element b = findAssociatedBase(e, styles);
        if (b != null) {
            StyleSpecification s = getStyles(b).get(ttsFontSizeAttrName);
            if (s != null) {
                Extent fs = parseFontSize(b, s);
                if (fs != null)
                    return new Extent(fs.getWidth() * 0.5, fs.getHeight() * 0.5);
            }
        }
        return defaults.getFontSize();
    }

    private void collectCommonAnnotationStyles(Element e, int begin, int end, StyleSet styles) {
        StyleSpecification s;
        Object v;

        // ANNOTATION_ALIGNMENT
        s = styles.get(ttsRubyAlignAttrName);
        v = null;
        if (s != null)
            v = InlineAlignment.fromValue(s.getValue());
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_ALIGNMENT, v, begin, end);

        // ANNOTATION_OFFSET
        s = styles.get(ttsRubyOffsetAttrName);
        v = null;
        if (s != null) {
            if (Keywords.isAuto(s.getValue())) {
                v = Double.valueOf(0);
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(s.getValue(), null, context, minMax, treatments, lengths)) {
                    assert lengths.size() == 1;
                    v = Double.valueOf(Helpers.resolveLength(e, lengths.get(0), Axis.VERTICAL, extBounds, refBounds, font.getSize()));
                }
            }
        }
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_OFFSET, v, begin, end);

        // ANNOTATION_OVERFLOW
        s = styles.get(ttsRubyOverflowAttrName);
        v = null;
        if (s != null)
            v = AnnotationOverflow.valueOf(s.getValue().toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_OVERFLOW, v, begin, end);

        // ANNOTATION_OVERHANG
        s = styles.get(ttsRubyOverhangAttrName);
        v = null;
        if (s != null)
            v = AnnotationOverhang.valueOf(s.getValue().toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_OVERHANG, v, begin, end);

        // ANNOTATION_OVERHANG_CLASS
        s = styles.get(ttsRubyOverhangClassAttrName);
        v = null;
        if (s != null) {
            CharacterClass[] cc = new CharacterClass[1];
            if (Characters.isCharacterClass(s.getValue(), null, context, cc))
                v = new AnnotationOverhangClass(cc[0]);
        }
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_OVERHANG_CLASS, v, begin, end);

        // ANNOTATION_POSITION
        s = styles.get(ttsRubyPositionAttrName);
        v = null;
        if (s != null)
            v = AnnotationPosition.valueOf(s.getValue().toUpperCase());
        if (v != null)
            addAttribute(StyleAttribute.ANNOTATION_POSITION, v, begin, end);

    }

    private Element findAssociatedBase(Element e, StyleSet styles) {
        return base;
    }

}

