/*
 * Copyright 2016-2018 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.model.value.impl;

import java.util.Arrays;

import com.skynav.ttv.model.value.Border;
import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.Length;

public class BorderImpl implements Border {
    private Style style;
    private Color color;
    private Length thickness;
    private Length[] radii;
    public BorderImpl(Border.Style style, Color color, Length thickness, Length[] radii) {
        assert style != null;
        this.style = style;
        assert color != null;
        this.color = color;
        assert thickness != null;
        this.thickness = thickness;
        assert (radii == null) || ((radii.length > 1) && (radii[0] != null) && (radii[1] != null));
        this.radii = (radii != null) ? Arrays.copyOf(radii, radii.length) : null;
    }
    public Border.Style getStyle() {
        return style;
    }
    public Color getColor() {
        return color;
    }
    public Length getThickness() {
        return thickness;
    }
    public Length[] getRadii() {
        return (radii != null) ? Arrays.copyOf(radii, radii.length) : null;
    }
    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + getStyle().hashCode();
        hc = hc * 31 + getColor().hashCode();
        hc = hc * 31 + getThickness().hashCode();
        if (radii != null)
            hc = hc * 31 + Arrays.hashCode(radii);
        return hc;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof BorderImpl) {
            BorderImpl o = (BorderImpl) other;
            if (o.getStyle() != getStyle())
                return false;
            else if (o.getColor().equals(getColor()))
                return false;
            else if (o.getThickness().equals(getThickness()))
                return false;
            else if ((o.radii != null) ^ (radii != null))
                return false;
            else if (o.radii != null) {
                assert radii != null;
                return Arrays.equals(o.radii, radii);
            } else
                return true;
        } else
            return false;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(getStyle());
        sb.append(',');
        sb.append(getColor());
        sb.append(',');
        sb.append(getThickness());
        if (radii != null) {
            sb.append(',');
            sb.append('[');
            sb.append(radii[0]);
            sb.append(',');
            sb.append(radii[1]);
            sb.append(']');
        }
        sb.append(']');
        return sb.toString();
    }
    public static Length getLengthFromThicknessKeyword(Border.Thickness thickness) {
        if (thickness == Border.Thickness.THIN)
            return new LengthImpl(1.0, Length.Unit.Pixel);
        else if (thickness == Border.Thickness.MEDIUM)
            return new LengthImpl(2.0, Length.Unit.Pixel);
        else if (thickness == Border.Thickness.THICK)
            return new LengthImpl(5.0, Length.Unit.Pixel);
        else
            return LengthImpl.PXL_0;
    }

}

