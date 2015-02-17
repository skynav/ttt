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

import org.w3c.dom.Element;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;

import com.skynav.ttv.model.value.Length;

import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.text.Constants.*;

public class Helpers {

    private Helpers() {}

    public static double resolveLength(Element e, Length l, Axis axis, Extent external, Extent reference, Extent font) {
        return l.getValue() * getLengthReference(e, l.getUnits(), axis, external, reference, font);
    }

    private static double getLengthReference(Element e, Length.Unit units, Axis axis, Extent external, Extent reference, Extent font) {
        if (units == Length.Unit.Pixel)
            return 1;
        else if (units == Length.Unit.Percentage)
            return getPercentageReference(e, axis, external, reference);
        else if (units == Length.Unit.Em)
            return getFontSizeReference(e, axis, font);
        else if (units == Length.Unit.Cell)
            return getCellSizeReference(axis, external);
        else if (units == Length.Unit.ViewportHeight)
            return getReference(axis, external);
        else if (units == Length.Unit.ViewportWidth)
            return getReference(axis, external);
        else
            return 1;
    }

    private static double getPercentageReference(Element e, Axis axis, Extent external, Extent reference) {
        if (Documents.isElement(e, isdRegionElementName)) {
            return getReference(axis, external) * 0.01;
        } else {
            return 0.01;                                        // [TBD] FIX ME - use element specific definition
        }
    }

    private static double getFontSizeReference(Element e, Axis axis, Extent font) {
        if (axis == Axis.VERTICAL)
            return font.getHeight();
        else
            return font.getWidth();
    }

    private static double getCellSizeReference(Axis axis, Extent reference) {
        if (axis == Axis.VERTICAL)
            return getReference(axis, reference) / 15;          // [TBD] FIX ME - use ttp:cellResolution
        else
            return getReference(axis, reference) / 32;          // [TBD] FIX ME - use ttp:cellResolution
    }

    private static double getReference(Axis axis, Extent reference) {
        if (axis == Axis.VERTICAL)
            return reference.getHeight();
        else
            return reference.getWidth();
    }

}
