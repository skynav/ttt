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

package com.skynav.ttv.verifier.util;

import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.TextEmphasis;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Emphasis {

    public static boolean isEmphasis(String value, Location location, VerifierContext context, TextEmphasis[] outputEmphasis) {
        String[] components = splitComponents(value);
        if (components.length < 1)
            return false;
        TextEmphasis.Style[] style = new TextEmphasis.Style[1];
        String[] text = new String[1];
        TextEmphasis.Position[] position = new TextEmphasis.Position[1];
        Color[] color = new Color[1];
        int numAuto = 0;
        for (int i = 0, k, n = components.length; i < n; ) {
            if (Keywords.isAuto(components[i]))
                ++numAuto;
            else if ((k = isStyle(components, i, location, context, style, text)) > i)
                i = k;
            else if ((k = isPosition(components, i, location, context, position)) > i)
                i = k;
            else if ((k = isColor(components, i, location, context, color)) > i)
                i = k;
            else
                return false;
        }
        if (numAuto == 1) {
            if (style[0] == null)
                style[0] = TextEmphasis.Style.AUTO;
            else if (position[0] == null)
                position[0] = TextEmphasis.Position.AUTO;
            else
                return false;
        } else if (numAuto == 2) {
            if ((style[0] == null) && (position[0] == null)) {
                style[0] = TextEmphasis.Style.AUTO;
                position[0] = TextEmphasis.Position.AUTO;
            } else
                return false;
        }
        if (outputEmphasis != null) {
            assert outputEmphasis.length >= 1;
            outputEmphasis[0] = new TextEmphasis(style[0], text[0], position[0], color[0]);
        }
        return true;
    }

    private static String[] splitComponents(String value) {
        return value.split("[ \t\r\n]+");
    }

    private static int isStyle(String[] components, int index, Location location, VerifierContext context, TextEmphasis.Style[] outputStyle, String[] outputText) {
        String s = null;
        String f = null;
        if (index < 0)
            return -1;
        String c1 = (index < components.length) ? components[index] : null;
        if (c1 != null) {
            if (Keywords.isNone(c1)) {
                return isStyleContinuation(c1, null, ++index, outputStyle, outputText);
            } else if (isStyleKeyword(c1)) {
                s = c1;
                ++index;
            } else if (isFillKeyword(c1)) {
                f = c1;
                ++index;
            } else
                index = -1;
        } else
            index = -1;
        if (index < 0)
            return -1;
        String c2 = (index < components.length) ? components[index] : null;
        if (c2 != null) {
            if (isStyleKeyword(c2)) {
                if (s == null) {
                    s = c2;
                    ++index;
                }
            } else if (isFillKeyword(c2)) {
                if (f == null) {
                    f = c2;
                    ++index;
                }
            }
        }
        if ((s == null) && (f == null))
            return -1;
        if (s == null)
            s = "circle";
        if (!Keywords.isNone(s) && (f == null))
            f = "filled";
        return isStyleContinuation(s, f, index, outputStyle, outputText);
    }

    private static int isStyleContinuation(String style, String fill, int index, TextEmphasis.Style[] outputStyle, String[] outputText) {
        String text = TextEmphasis.getTextFromStyleSpecification(style, fill);
        if (outputStyle != null) {
            assert outputStyle.length >= 1;
            outputStyle[0] = (text != null) ? TextEmphasis.Style.TEXT : TextEmphasis.Style.NONE;
        }
        if (outputText != null) {
            assert outputText.length >= 1;
            outputText[0] = text;
        }
        return index;
    }

    private static boolean isStyleKeyword(String s) {
        if (s.equals("circle"))
            return true;
        else if (s.equals("dot"))
            return true;
        else if (s.equals("sesame"))
            return true;
        else
            return false;
    }

    private static boolean isFillKeyword(String s) {
        if (s.equals("filled"))
            return true;
        else if (s.equals("open"))
            return true;
        else
            return false;
    }

    private static int isColor(String[] components, int index, Location location, VerifierContext context, Color[] outputColor) {
        if (index < 0)
            return index;
        String c = (index < components.length) ? components[index] : null;
        if (c != null) {
            if (Colors.maybeColor(c)) {
                if (Colors.isColor(c, location, context, outputColor))
                    return ++index;
            }
        }
        return -1;
    }

    private static int isPosition(String[] components, int index, Location location, VerifierContext context, TextEmphasis.Position[] outputPosition) {
        String p = null;
        if (index < 0)
            return -1;
        String c = (index < components.length) ? components[index] : null;
        if (c != null) {
            if (Keywords.isAuto(c))
                p = c;
            else if (isPositionKeyword(c))
                p = c;
        }
        if (p == null)
            return -1;
        if (outputPosition != null) {
            assert outputPosition.length >= 1;
            outputPosition[0] = TextEmphasis.Position.fromValue(p);
        }
        return ++index;
    }

    private static boolean isPositionKeyword(String s) {
        if (s.equals("before"))
            return true;
        else if (s.equals("after"))
            return true;
        else if (s.equals("outside"))
            return true;
        else
            return false;
    }

}
