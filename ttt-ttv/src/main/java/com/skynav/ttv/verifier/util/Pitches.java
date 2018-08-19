/*
 * Copyright 2018 Skynav, Inc. All rights reserved.
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skynav.ttv.model.value.Pitch;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Pitches {

    private static final Pattern pitchPattern = Pattern.compile("([\\+\\-]?(?:\\d*\\.\\d+|\\d+))(\\w+|%)?");
    public static boolean isPitch(String value, Location location, VerifierContext context, Pitch[] outputPitch) {
        Matcher m = pitchPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() > 0;
            String number = m.group(1);
            boolean numberHasSign = (number.charAt(0) == '+') || (number.charAt(0) == '-');
            double numberValue;
            if (!Strings.containsDecimalSeparator(number)) {
                try {
                    numberValue = new BigInteger(number).doubleValue();
                } catch (NumberFormatException e) {
                    return false;
                }
            } else {
                try {
                    numberValue = new BigDecimal(number).doubleValue();
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            Pitch.Unit unitsValue = null;
            if (m.groupCount() > 1) {
                String units = m.group(2);
                if (units != null) {
                    try {
                        unitsValue = Pitch.Unit.valueOfShorthand(units);
                    } catch (IllegalArgumentException e) {
                        return false;
                    }
                }
            }
            if (unitsValue == null)
                unitsValue = Pitch.Unit.Hertz;
            if (outputPitch != null) {
                Pitch.Type type;
                if ((unitsValue == Pitch.Unit.Percentage) || numberHasSign)
                    type = Pitch.Type.RELATIVE;
                else
                    type = Pitch.Type.ABSOLUTE;
                outputPitch[0] = new Pitch(type, numberValue, unitsValue);
            }
            return true;
        } else
            return false;
    }

}
