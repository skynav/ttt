/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import com.skynav.ttv.model.value.Color;

public class ColorImpl implements Color {
    private double red;
    private double green;
    private double blue;
    private double alpha;
    public ColorImpl(double red, double green, double blue, double alpha) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
        this.alpha = clamp(alpha);
    }
    private double clamp(double component) {
        if (Double.isNaN(component))
            component = 0;
        else if (component <= 0)
            component = 0;
        else if (component >= 1)
            component = 1;
        return component;
    }
    public double getRed() {
        return red;
    }
    public double getGreen() {
        return green;
    }
    public double getBlue() {
        return blue;
    }
    public double getAlpha() {
        return alpha;
    }
    public int hashCode() {
        return Double.valueOf(red).hashCode() ^ Double.valueOf(green).hashCode() ^ Double.valueOf(blue).hashCode() ^ Double.valueOf(alpha).hashCode();
    }
    public boolean equals(Object o) {
        if (o instanceof Color) {
            Color c = (Color) o;
            if (c.getRed() != red)
                return false;
            else if (c.getGreen() != green)
                return false;
            else if (c.getBlue() != blue)
                return false;
            else if (c.getAlpha() != alpha)
                return false;
            else
                return true;
        } else
            return false;
    }
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("rgb");
        if (alpha != 1)
            sb.append('a');
        sb.append('(');
        sb.append(getComponentAsString(red));
        sb.append(',');
        sb.append(getComponentAsString(green));
        sb.append(',');
        sb.append(getComponentAsString(blue));
        if (alpha != 1) {
            sb.append(',');
            sb.append(getComponentAsString(alpha));
        }
        sb.append(')');
        return sb.toString();
    }
    private String getComponentAsString(double component) {
        return Integer.toString((int) Math.floor(component * 255));
    }
    public static Color fromRGBHash(String hash) {
        assert (hash.length() == 6) || (hash.length() == 8);
        double r = fromHexColor(hash.substring(0,2));
        double g = fromHexColor(hash.substring(2,4));
        double b = fromHexColor(hash.substring(4,6));
        double a = (hash.length() == 8) ? fromHexColor(hash.substring(6,8)) : 1;
        return new ColorImpl(r,g,b,a);
    }
    private static double fromHexColor(String color) {
        try {
            return Integer.valueOf(color, 16).doubleValue() / 255.0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}

