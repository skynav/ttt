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

import com.skynav.ttv.model.value.TextCombine;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Combine {

    public static boolean isCombine(String value, Location location, VerifierContext context, TextCombine[] outputCombine) {
        String[] components = splitComponents(value);
        int numComponents = components.length;
        if (numComponents < 1)
            return false;
        int index = 0;
        String c1 = components[index++];
        TextCombine.Style style;
        if (c1 != null) {
            try {
                style = TextCombine.Style.fromValue(c1);
            } catch (IllegalArgumentException e) {
                return false;
            }
        } else
            style = null;
        String c2 = (index < numComponents) ? components[index] : null;
        int count;
        if (c2 != null) {
            try {
                count = Integer.parseInt(c2);
            } catch (NumberFormatException e) {
                return false;
            }
        } else
            count = 0;
        if (style == null)
            return false;
        if (count < 0)
            return false;
        if (outputCombine != null) {
            assert outputCombine.length >= 1;
            outputCombine[0] = new TextCombine(style, count);
        }
        return true;
    }

    private static String[] splitComponents(String value) {
        return value.split("[ \t\r\n]+");
    }

}
