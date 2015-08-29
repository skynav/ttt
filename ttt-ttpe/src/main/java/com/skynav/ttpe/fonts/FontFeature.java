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

public class FontFeature implements Comparable<FontFeature> {

    public static final FontFeature     HWID            = new FontFeature("hwid");              // half width variant
    public static final FontFeature     FWID            = new FontFeature("fwid");              // full width variant
    public static final FontFeature     KERN            = new FontFeature("kern");              // kerning
    public static final FontFeature     RUBY            = new FontFeature("ruby");              // ruby variant
    public static final FontFeature     VERT            = new FontFeature("vert");              // vertical variant

    // private (non-standard) pseudo-features
    public static final FontFeature     BIDI            = new FontFeature("BIDI");              // bidi level           (Integer level)
    public static final FontFeature     COMB            = new FontFeature("COMB");              // tate-chu-yoko        (Combine combination)
    public static final FontFeature     LANG            = new FontFeature("LANG");              // resolved language    (String language)
    public static final FontFeature     MIRR            = new FontFeature("MIRR");              // mirrored glyphs      (Boolean mirrored)
    public static final FontFeature     ORNT            = new FontFeature("ORNT");              // orientation          (Orientation orientation)
    public static final FontFeature     REVS            = new FontFeature("REVS");              // reversed glyphs      (Boolean reversed)
    public static final FontFeature     SCPT            = new FontFeature("SCPT");              // resolved script      (String script)

    private final String feature;
    private final Object[] arguments;

    public FontFeature(String feature) {
        this(feature, null);
    }

    public FontFeature(String feature, Object[] arguments) {
        if ((feature == null) || feature.isEmpty())
            throw new IllegalArgumentException();
        this.feature = feature;
        if (arguments != null) {
            int na = arguments.length;
            Object[] aa = new Object[na];
            System.arraycopy(arguments, 0, aa, 0, na);
            this.arguments = aa;
        } else
            this.arguments = null;
    }

    public FontFeature parameterize(Object ... arguments) {
        return new FontFeature(this.feature, arguments);
    }

    public String getFeature() {
        return feature;
    }

    public int getArgumentCount() {
        return (arguments != null) ? arguments.length : 0;
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

    public int compareTo(FontFeature other) {
        int d = feature.compareTo(other.feature);
        if (d != 0)
            return d;
        return compareArguments(arguments, other.arguments);
    }

    private static int compareArguments(Object[] args1, Object[] args2) {
        if (isEmpty(args1))
            return isEmpty(args2) ? 0 : -1;
        else if (isEmpty(args2))
            return 1;
        else
            return compareArgumentsPiecewise(args1, args2);
    }

    private static boolean isEmpty(Object[] args) {
        return (args == null) || (args.length == 0);
    }

    private static int compareArgumentsPiecewise(Object[] args1, Object[] args2) {
        assert args1 != null;
        int n1 = args1.length;
        assert n1 > 0;
        assert args2 != null;
        int n2 = args2.length;
        assert n2 > 0;
        for (int i = 0, n = (n1 < n2) ? n1 : n2; i < n; ++i) {
            if (i == n1)
                return -1;
            if (i == n2)
                return 1;
            int d = compareArguments(args1[i], args2[i]);
            if (d != 0)
                return d;
        }
        return 0;
    }

    private static int compareArguments(Object a1, Object a2) {
        if (a1 == null)
            return (a2 == null) ? 0 : -1;
        else if (a2 == null)
            return 1;
        else {
            Class<?> c1 = a1.getClass();
            Class<?> c2 = a2.getClass();
            if (!c1.isInstance(a2) || !c2.isInstance(a1))
                return c1.getName().compareTo(c2.getName());
            else
                return compareArgumentsOfCompatibleType(a1, a2);
        }
    }

    private static int compareArgumentsOfCompatibleType(Object a1, Object a2) {
        if (a1 instanceof String) {
            if (!(a2 instanceof String))
                throw new IllegalArgumentException();
            return ((String) a1).compareTo((String) a2);
        } else if (a1 instanceof Boolean) {
            if (!(a2 instanceof Boolean))
                throw new IllegalArgumentException();
            return ((Boolean) a1).compareTo((Boolean) a2);
        } else if (a1 instanceof Integer) {
            if (!(a2 instanceof Integer))
                throw new IllegalArgumentException();
            return ((Integer) a1).compareTo((Integer) a2);
        } else if (a1 instanceof Double) {
            if (!(a2 instanceof Double))
                throw new IllegalArgumentException();
            return ((Double) a1).compareTo((Double) a2);
        } else
            return a1.toString().compareTo(a2.toString());
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
