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
import com.skynav.ttpe.geometry.Point;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.impl.LengthImpl;

import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.text.Constants.*;

public class Helpers {

    private Helpers() {}

    public static Point resolvePosition(Element e, Length[] lengths, Extent external, Extent reference, Extent cellResolution) {

        assert external != null;
        assert reference != null;
        assert lengths != null;
        assert lengths.length >= 4;

        Extent referencePercent = new Extent(external.getWidth() - reference.getWidth(), external.getHeight() - reference.getHeight());

        Length lx = lengths[0];
        if (lx == null)
            lx = LengthImpl.PCT_0;
        double x;
        if (lx.getUnits() == Length.Unit.Percentage)
            x = (lx.getValue() / 100) * referencePercent.getWidth();
        else
            x = resolveLength(e, lx, Axis.HORIZONTAL, external, reference, null, cellResolution);

        Length ly = lengths[1];
        if (ly == null)
            ly = LengthImpl.PCT_0;
        double y;
        if (ly.getUnits() == Length.Unit.Percentage)
            y = (ly.getValue() / 100) * referencePercent.getHeight();
        else
            y = resolveLength(e, ly, Axis.VERTICAL, external, reference, null, cellResolution);

        Length ox = lengths[2];
        if (ox == null)
            ox = LengthImpl.PCT_0;
        double xOffset;
        if (ox.getUnits() == Length.Unit.Percentage)
            xOffset = (ox.getValue() / 100) * referencePercent.getWidth();
        else
            xOffset = resolveLength(e, ox, Axis.HORIZONTAL, external, reference, null, cellResolution);

        Length oy = lengths[3];
        if (oy == null)
            oy = LengthImpl.PCT_0;
        double yOffset;
        if (oy.getUnits() == Length.Unit.Percentage)
            yOffset = (oy.getValue() / 100) * referencePercent.getHeight();
        else
            yOffset = resolveLength(e, oy, Axis.VERTICAL, external, reference, null, cellResolution);

        x -= xOffset;
        y -= yOffset;

        if ((x == 0) && (y == 0))
            return Point.ZERO;
        else
            return new Point(x, y);
    }

    public static double resolveLength(Element e, Length l, Axis axis, Extent external, Extent reference, Extent font, Extent cellResolution) {
        return (l != null) ? l.getValue() * getLengthReference(e, l.getUnits(), axis, external, reference, font, cellResolution) : 0;
    }

    private static double getLengthReference(Element e, Length.Unit units, Axis axis, Extent external, Extent reference, Extent font, Extent cellResolution) {
        if (units == Length.Unit.Pixel)
            return 1;
        else if (units == Length.Unit.Percentage)
            return getPercentageReference(e, axis, external, reference);
        else if (units == Length.Unit.Em)
            return getFontSizeReference(e, axis, font);
        else if (units == Length.Unit.Cell)
            return getCellSizeReference(axis, external, cellResolution);
        else if (units == Length.Unit.ViewportHeight)
            return getViewportReference(Axis.VERTICAL, external);
        else if (units == Length.Unit.ViewportWidth)
            return getViewportReference(Axis.HORIZONTAL, external);
        else
            return 1;
    }

    private static double getPercentageReference(Element e, Axis axis, Extent external, Extent reference) {
        if (Documents.isElement(e, isdRegionElementName)) {
            return getReference(axis, external) / 100;
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

    private static double getCellSizeReference(Axis axis, Extent reference, Extent cellResolution) {
        if (axis == Axis.VERTICAL)
            return getReference(axis, reference) / cellResolution.getHeight();
        else
            return getReference(axis, reference) / cellResolution.getWidth();
    }

    private static double getViewportReference(Axis axis, Extent reference) {
        return getReference(axis, reference) / 100;
    }

    private static double getReference(Axis axis, Extent reference) {
        if (axis == Axis.VERTICAL)
            return reference.getHeight();
        else
            return reference.getWidth();
    }

}
