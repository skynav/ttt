/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

import com.skynav.ttpe.geometry.Axis;

public class Emphasis {

    public enum Style {
        NONE,
        AUTO,
        TEXT;
    };

    public enum Position {
        AUTO,
        BEFORE,
        AFTER,
        OUTSIDE;
    };

    private Style style;
    private String text;
    private Position position;
    private Color color;

    public Emphasis(String style, String text, String position, Color color) {
        this.style = Style.valueOf(style);
        this.text = text;
        this.position = Position.valueOf(position);
        this.color = color;
    }

    public Style getStyle() {
        return style;
    }

    public boolean isNone() {
        return getStyle() == Style.NONE;
    }

    public String getText() {
        return text;
    }

    public static final String FILLED_DOT                       = "\u2022";
    public static final String SESAME_DOT                       = "\uFE45";

    public String resolveText(Axis axis) {
        if (style == Style.NONE)
            return null;
        else if (style == Style.TEXT)
            return getText();
        else if (style == Style.AUTO) {
            if (axis == Axis.VERTICAL)
                return FILLED_DOT;
            else
                return SESAME_DOT;
        } else
            throw new IllegalArgumentException();
    }

    public Position getPosition() {
        return position;
    }

    public Position resolvePosition(int numLines, boolean firstLine) {
        Position p = this.position;
        if (p == Position.AUTO)
            p = (numLines == 2) ? Position.OUTSIDE : Position.BEFORE;
        if (p == Position.OUTSIDE)
            p = firstLine ? Position.BEFORE : Position.AFTER;
        return p;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(style);
        sb.append(',');
        sb.append(text);
        sb.append(',');
        sb.append(position);
        if (color != null) {
            sb.append(',');
            sb.append(color);
        }
        sb.append(']');
        return sb.toString();
    }

}
