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

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.TextReserve;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Reserve {

    public static boolean isReserve(String value, Location location, VerifierContext context, TextReserve[] outputReserve) {
        String[] components = splitComponents(value);
        if (components.length < 1)
            return false;
        int index = 0;
        // position
        TextReserve.Position[] position = new TextReserve.Position[1];
        if ((index + 1) <= components.length) {
            if (!isPosition(components, index, location, context, position))
                return false;
            else
                ++index;
        }
        // reserve
        Length[] reserve = new Length[1];
        if ((index + 1) <= components.length) {
            if (!isLength(components, index, location, context, reserve))
                return false;
            else
                ++index;
        }
        // unparsed components
        if (index < components.length)
            return false;
        if (outputReserve != null) {
            assert outputReserve.length >= 1;
            outputReserve[0] = new TextReserve(position[0], reserve[0]);
        }
        return true;
    }

    private static String[] splitComponents(String value) {
        return value.split("[ \t\r\n]+");
    }

    private static boolean isPosition(String[] components, int index, Location location, VerifierContext context, TextReserve.Position[] outputPosition) {
        String p = null;
        if (index < 0)
            return false;
        String c = (index < components.length) ? components[index] : null;
        if (c != null) {
            if (Keywords.isNone(c))
                p = c;
            else if (Keywords.isAuto(c))
                p = c;
            else if (isPositionKeyword(c))
                p = c;
        }
        if (p == null)
            return false;
        if (outputPosition != null) {
            assert outputPosition.length >= 1;
            outputPosition[0] = TextReserve.Position.fromValue(p);
        }
        return true;
    }

    private static boolean isPositionKeyword(String s) {
        if (s.equals("before"))
            return true;
        else if (s.equals("after"))
            return true;
        else if (s.equals("both"))
            return true;
        else if (s.equals("outside"))
            return true;
        else if (s.equals("around"))
            return true;
        else if (s.equals("between"))
            return true;
        else
            return false;
    }

    private static boolean isLength(String[] components, int index, Location location, VerifierContext context, Length[] outputLength) {
        Length l = null;
        if (index < 0)
            return false;
        String c = (index < components.length) ? components[index] : null;
        if (c != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            Length[] length = new Length[1];
            if (!Lengths.isLength(c, location, context, treatments, length))
                return false;
            else
                l = length[0];
        }
        if (l == null)
            return false;
        if (outputLength != null) {
            assert outputLength.length >= 1;
            outputLength[0] = l;
        }
        return true;
    }

}
