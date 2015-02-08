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
 
package com.skynav.ttpe.fonts;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;

public class FontKey {

    public static final String                  DEFAULT_FAMILY     = "Noto Sans";
    public static final FontStyle               DEFAULT_STYLE      = FontStyle.NORMAL;
    public static final FontWeight              DEFAULT_WEIGHT     = FontWeight.NORMAL;
    public static final Extent                  DEFAULT_SIZE       = new Extent(24, 24);
    public static final String                  DEFAULT_LANGUAGE   = "";
    public static final FontKey                 DEFAULT_HORIZONTAL =
        new FontKey(DEFAULT_FAMILY, DEFAULT_STYLE, DEFAULT_WEIGHT, Axis.HORIZONTAL, DEFAULT_LANGUAGE, DEFAULT_SIZE);
    public static final FontKey                 DEFAULT_VERTICAL   =
        new FontKey(DEFAULT_FAMILY, DEFAULT_STYLE, DEFAULT_WEIGHT, Axis.VERTICAL, DEFAULT_LANGUAGE, DEFAULT_SIZE);

    public String family;
    public FontStyle style;
    public FontWeight weight;
    public Axis axis;
    public String language;
    public Extent size;

    public FontKey(FontKey key, Extent size) {
        this(key.family, key.style, key.weight, key.axis, key.language, size);
    }

    public FontKey(String family, FontStyle style, FontWeight weight, Axis axis, String language, Extent size) {
        this.family = family.toLowerCase();
        this.style = style;
        this.weight = weight;
        this.axis = axis;
        this.language = language.toLowerCase();
        this.size = size;
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + family.hashCode();
        hc = hc * 31 + style.hashCode();
        hc = hc * 31 + weight.hashCode();
        hc = hc * 31 + axis.hashCode();
        hc = hc * 31 + language.hashCode();
        hc = hc * 31 + size.hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FontKey) {
            FontKey other = (FontKey) o;
            if (!family.equals(other.family))
                return false;
            else if (style != other.style)
                return false;
            else if (weight != other.weight)
                return false;
            else if (axis != other.axis)
                return false;
            else if (!language.equals(other.language))
                return false;
            else if (!size.equals(other.size))
                return false;
            else
                return true;
        } else
            return false;
    }

}
