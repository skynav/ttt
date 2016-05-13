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

public class Decoration implements Cloneable {

    public enum Type {
        BACKGROUND_COLOR,
        COLOR,
        EMPHASIS,
        HIGHLIGHT,
        LINING,
        OUTLINE,
        VISIBILITY;
    }

    private int begin;
    private int end;
    private Type type;
    private Object value;

    public Decoration(int begin, int end, Type type, Object value) {
        this.begin = begin;
        this.end = end;
        this.type = type;
        this.value = value;
    }

    public int[] getInterval() {
        return new int[]{begin, end};
    }

    public int getBegin() {
        return begin;
    }

    public int getEnd() {
        return end;
    }

    public Type getType() {
        return type;
    }

    public boolean isType(Type type) {
        return this.type == type;
    }

    public boolean isBackgroundColor() {
        return getType() == Type.BACKGROUND_COLOR;
    }

    public BackgroundColor getBackgroundColor() {
        return isBackgroundColor() ? (BackgroundColor) getValue() : null;
    }

    public boolean isColor() {
        return getType() == Type.COLOR;
    }

    public Color getColor() {
        return isColor() ? (Color) getValue() : null;
    }

    public boolean isEmphasis() {
        return getType() == Type.EMPHASIS;
    }

    public Emphasis getEmphasis() {
        return isEmphasis() ? (Emphasis) getValue() : null;
    }

    public boolean isHighlight() {
        return getType() == Type.HIGHLIGHT;
    }

    public boolean isLining() {
        return getType() == Type.LINING;
    }

    public boolean isOutline() {
        return getType() == Type.OUTLINE;
    }

    public Outline getOutline() {
        return isOutline() ? (Outline) getValue() : null;
    }

    public boolean isVisibility() {
        return getType() == Type.VISIBILITY;
    }

    public Visibility getVisibility() {
        return isVisibility() ? (Visibility) getValue() : null;
    }

    public Object getValue() {
        return value;
    }

    public boolean intersects(int from, int to) {
        if (this.end <= from)
            return false;
        else if (this.begin >= to)
            return false;
        else
            return true;
    }

    public Decoration adjustInterval(int adjust) {
        try {
            Decoration d = (Decoration) this.clone();
            d.begin += adjust;
            d.end += adjust;
            if (d.begin < 0) {
                int l = d.end - d.begin;
                d.begin = 0;
                d.end = l;
            }
            return d;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
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
        sb.append(type);
        sb.append(',');
        sb.append(value);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
