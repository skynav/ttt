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

public class StyleAttributeInterval implements Comparable<StyleAttributeInterval> {

    private StyleAttribute attribute;
    private Object value;
    private int begin;
    private int end;

    public StyleAttributeInterval(StyleAttribute attribute, Object value, int begin, int end) {
        this.attribute = attribute;
        this.value = value;
        assert begin > -2;
        this.begin = begin;
        assert end > -2;
        assert end >= begin;
        this.end = end;
    }

    public StyleAttribute getAttribute() {
        return attribute;
    }

    public Object getValue() {
        return value;
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public int getLength() {
        return end - begin;
    }

    public boolean isOuterScope() {
        return begin < 0;
    }

    public boolean intersects(int b, int e) {
        if (b >= end)
            return false;
        else if (e <= begin)
            return false;
        else
            return true;
    }

    public int[] intersection(int b, int e) {
        if (intersects(b, e)) {
            if (begin > b)
                b = begin;
            if (end < e)
                e = end;
            return new int[] { b, e };
        } else {
            return null;
        }
    }

    public int compareTo(StyleAttributeInterval other) {
        int b1 = begin;
        int b2 = other.begin;
        if (b1 < b2)
            return -1;
        else if (b1 > b2)
            return 1;
        int e1 = end;
        int e2 = other.end;
        if (e1 < e2)
            return -1;
        else if (e1 > e2)
            return 1;
        int d = attribute.compareTo(other.attribute);
        if ((d < 0) || (d > 0))
            return d;
        int h1 = value.hashCode();
        int h2 = other.value.hashCode();
        if (h1 < h2)
            return -1;
        else if (h1 > h2)
            return 1;
        if (value == other.value)
            return 0;
        else
            return -1;
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + attribute.hashCode();
        hc = hc * 31 + value.hashCode();
        hc = hc * 31 + Integer.valueOf(begin).hashCode();
        hc = hc * 31 + Integer.valueOf(end).hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StyleAttributeInterval) {
            StyleAttributeInterval other = (StyleAttributeInterval) o;
            return compareTo(other) == 0;
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append('[');
        sb.append(begin);
        sb.append(',');
        sb.append(end);
        sb.append(']');
        sb.append(',');
        sb.append(attribute);
        sb.append(',');
        sb.append(value);
        sb.append(']');
        return sb.toString();
    }

}
