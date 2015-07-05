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

import com.skynav.ttv.model.value.FontVariant;

public class FontFeature {

    public static final FontFeature     HWID            = new FontFeature("hwid");
    public static final FontFeature     FWID            = new FontFeature("fwid");
    public static final FontFeature     RUBY            = new FontFeature("ruby");
    public static final FontFeature     VERT            = new FontFeature("vert");

    private final String feature;
    private final Object[] arguments;

    public FontFeature(String feature) {
        this(feature, null);
    }

    public FontFeature(String feature, Object[] arguments) {
        assert (feature != null) && !feature.isEmpty();
        this.feature = feature;
        if (arguments != null) {
            int na = arguments.length;
            Object[] a = new Object[na];
            System.arraycopy(arguments, 0, a, 0, na);
            this.arguments = a;
        } else
            this.arguments = null;
    }

    public String getFeature() {
        return feature;
    }

    public Object getArgument(int index) {
        if ((arguments != null) && (arguments.length > index))
            return arguments[index];
        else
            return null;
    }

    public static FontFeature fromVariant(FontVariant variant) {
        String feature;
        if (variant == FontVariant.SUPER)
            feature = "sups";
        else if (variant == FontVariant.SUB)
            feature = "subs";
        else if (variant == FontVariant.HALF)
            feature = "hwid";
        else if (variant == FontVariant.FULL)
            feature = "fwid";
        else if (variant == FontVariant.RUBY)
            feature = "ruby";
        else
            feature = null;
        return (feature != null) ? new FontFeature(feature) : null;
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + feature.hashCode();
        hc = hc * 31 + hashCode(arguments);
        return hc;
    }

    private int hashCode(Object[] arguments) {
        int hc = 23;
        if (arguments != null) {
            for (int i = 0, n = arguments.length; i < n; ++i) {
                Object a = arguments[i];
                if (a != null)
                    hc = hc * 31 + a.hashCode();
            }
        }
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof FontFeature) {
            FontFeature other = (FontFeature) o;
            if (!feature.equals(other.feature))
                return false;
            else if (!equals(arguments, other.arguments))
                return false;
            else
                return true;
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append('\'');
        sb.append(feature);
        sb.append('\'');
        if (arguments != null) {
            boolean first = true;
            sb.append(',');
            for (Object a : arguments) {
                if (!first)
                    sb.append(',');
                else
                    first = false;
                sb.append(a);
            }
        }
        sb.append(']');
        return sb.toString();
    }

    private boolean equals(Object[] aa1, Object[] aa2) {
        if (aa1 == null) {
            return aa2 == null;
        } else if (aa2 == null) {
            return false;
        } else if (aa1.length != aa2.length) {
            return false;
        } else {
            assert aa1.length == aa2.length;
            for (int i = 0, n = aa1.length; i < n; ++i) {
                Object a1 = aa1[i];
                Object a2 = aa2[i];
                if (a1 == null) {
                    if (a2 != null)
                        return false;
                } else if (a2 == null) {
                    return false;
                } else if (!a1.equals(a2))
                    return false;
            }
            return true;
        }
    }

}
