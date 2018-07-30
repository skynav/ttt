/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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

import org.xml.sax.Locator;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Numbers {

    private static final Pattern numberPattern = Pattern.compile("([\\+\\-]?(?:\\d*\\.\\d+|\\d+))");
    public static boolean isNumber(String value, Location location, VerifierContext context, Double[] outputNumber) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Matcher m = numberPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() > 0;
            String number = m.group(1);
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
            if (outputNumber != null) {
                outputNumber[0] = Double.valueOf(numberValue);
            }
            return true;
        } else
            return false;
    }

    public static String normalize(double number) {
        if (Math.floor(number) == number)
            return Long.toString((long) number);
        else
            return Double.toString(number);
    }

}
