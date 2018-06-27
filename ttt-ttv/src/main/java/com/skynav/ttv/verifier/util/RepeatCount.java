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

import com.skynav.ttv.model.value.Repeat;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class RepeatCount {

    private static final Pattern countPattern = Pattern.compile("\\d*\\.\\d+|\\d+");

    public static boolean isRepeatCount(String value, Location location, VerifierContext context, Repeat[] outputRepeat) {
        Repeat repeat = null;
        if (isIndefinite(value))
            repeat = Repeat.INDEFINITE;
        else {
            Matcher m = countPattern.matcher(value);
            if (m.matches()) {
                String count = m.group(0);
                double countValue;
                if (!Strings.containsDecimalSeparator(count)) {
                    try {
                        countValue = new BigInteger(count).doubleValue();
                    } catch (NumberFormatException e) {
                        return false;
                    }
                } else {
                    try {
                        countValue = new BigDecimal(count).doubleValue();
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                repeat = new Repeat(Repeat.Type.DEFINITE, countValue);
            } else
                return false;
        }
        if (outputRepeat != null) {
            assert outputRepeat.length >= 1;
            outputRepeat[0] = repeat;
        }
        return true;
    }

    private static boolean isIndefinite(String s) {
        if (s.equals("indefinite"))
            return true;
        else
            return false;
    }

}
