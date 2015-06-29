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

package com.skynav.ttv.model.value;

public class TextEmphasis {

    public enum Style {
        NONE,
        AUTO,
        TEXT;
        public static Style fromValue(String v) {
            return Style.valueOf(v.toUpperCase());
        }
    };

    public enum Position {
        AUTO,
        BEFORE,
        AFTER,
        OUTSIDE;
        public static Position fromValue(String v) {
            return Position.valueOf(v.toUpperCase());
        }
    };

    public static final TextEmphasis NONE       = new TextEmphasis(Style.NONE, null, null, null);
    public static final TextEmphasis AUTO       = new TextEmphasis(Style.AUTO, null, null, null);

    public static final String CIRCLE           = "circle";
    public static final String DOT              = "dot";
    public static final String SESAME           = "sesame";
    public static final String FILLED           = "filled";
    public static final String OPEN             = "open";


    private Style style;
    private String text;
    private Position position;
    private Color color;

    public TextEmphasis(Style style, String text, Position position, Color color) {
        if (style == null)
            style = Style.AUTO;
        this.style = style;
        this.text = text;
        if (position == null)
            position = Position.AUTO;
        this.position = position;
        this.color = color;
    }

    public Style getStyle() {
        return style;
    }

    public String getText() {
        return text;
    }

    public Position getPosition() {
        return position;
    }

    public Color getColor() {
        return color;
    }

    public static final int   UC_BULLET                         = '\u2022';
    public static final int   UC_WHITE_BULLET                   = '\u25E6';
    public static final int   UC_BLACK_CIRCLE                   = '\u25CF';
    public static final int   UC_WHITE_CIRCLE                   = '\u25CB';
    public static final int   UC_SESAME_DOT                     = '\uFE45';
    public static final int   UC_WHITE_SESAME_DOT               = '\uFE46';

    public static String getTextFromStyleSpecification(String style, String fill) {
        if (style == null)
            style = DOT;
        if (fill == null)
            fill = FILLED;
        int c;
        if (style.equals(DOT)) {
            c = isFilled(fill) ? UC_BULLET : UC_WHITE_BULLET;
        } else if (style.equals(CIRCLE)) {
            c = isFilled(fill) ? UC_BLACK_CIRCLE : UC_WHITE_CIRCLE;
        } else if (style.equals(SESAME)) {
            c = isFilled(fill) ? UC_SESAME_DOT : UC_WHITE_SESAME_DOT;
        } else
            c = 0;
        if (c != 0)
            return new String(new char[] {(char) c});
        else
            return null;
    }

    private static boolean isFilled(String fill) {
        return fill.equals(FILLED);
    }

}
