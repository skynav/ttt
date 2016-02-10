/*
 * Copyright 2015-16 Skynav, Inc. All rights reserved.
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

import java.text.MessageFormat;
import java.util.Locale;

public class Outline {

    public static final Outline NONE = new Outline(null, 0, 0);

    private Color color;
    private double thickness;
    private double blur;

    public Outline(Color color, double thickness, double blur) {
        this.color = color;
        this.thickness = thickness;
        this.blur = blur;
    }

    public boolean isNone() {
        return (this == NONE) || !(thickness != 0);
    }

    public Color getColor() {
        return color;
    }

    public double getThickness() {
        return thickness;
    }

    public double getBlur() {
        return blur;
    }

    @Override
    public int hashCode() {
        int hc = 23;
        if (color != null)
            hc = hc * 31 + color.hashCode();
        hc = hc * 31 + Double.valueOf(thickness).hashCode();
        hc = hc * 31 + Double.valueOf(blur).hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Outline) {
            Outline other = (Outline) o;
            if ((other.color == null) ^ (color == null))
                return false;
            else if ((other.color != null) && !other.color.equals(color))
                return false;
            else if (other.thickness != thickness)
                return false;
            else if (other.blur != blur)
                return false;
            else
                return true;
        } else
            return false;
    }

    private static final MessageFormat doubleFormatter = new MessageFormat("{0,number,#.####}", Locale.US);
    public String toTextOutlineString() {
        StringBuffer sb = new StringBuffer();
        if (color != null) {
            if (color.getAlpha() < 1)
                sb.append(color.toRGBAString(false));
            else
                sb.append(color.toRGBString());
        }
        if (sb.length() > 0)
            sb.append(' ');
        sb.append(doubleFormatter.format(new Object[] {thickness}));
        sb.append("px");
        if (blur != 0) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(doubleFormatter.format(new Object[] {blur}));
            sb.append("px");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        if (color != null) {
            sb.append(color);
            sb.append(',');
        }
        sb.append(doubleFormatter.format(new Object[] {thickness}));
        sb.append(',');
        sb.append(doubleFormatter.format(new Object[] {blur}));
        sb.append(']');
        return sb.toString();
    }

}
