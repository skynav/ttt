/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import java.math.BigInteger;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.util.NullErrorReporter;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.ZeroTreatment;

public class Integers {

    private static Pattern integerPattern = Pattern.compile("([\\+\\-]?)(\\d+)");
    private static boolean isInteger(String value, Locator locator, ErrorReporter errorReporter,
        NegativeTreatment negativeTreatment, ZeroTreatment zeroTreatment, Integer[] outputInteger) {
        Matcher m = integerPattern.matcher(value);
        if (m.matches()) {
            String number = m.group(0);
            if (number.charAt(0) == '+')
                number = number.substring(1);
            int numberValue;
            try {
                numberValue = new BigInteger(number).intValue();
            } catch (NumberFormatException e) {
                return false;
            }
            if (numberValue < 0) {
                if (negativeTreatment == NegativeTreatment.Error)
                    return false;
                else if (negativeTreatment == NegativeTreatment.Warning) {
                    if (errorReporter.logWarning(locator, "Negative <integer> expression " + Numbers.normalize(numberValue) + " should not be used."))
                        return false;
                } else if (negativeTreatment == NegativeTreatment.Info)
                    errorReporter.logInfo(locator, "Negative <integer> expression " + Numbers.normalize(numberValue) + " used.");
            } else if (numberValue == 0) {
                if (zeroTreatment == ZeroTreatment.Error)
                    return false;
                else if (zeroTreatment == ZeroTreatment.Warning) {
                    if (errorReporter.logWarning(locator, "Zero <integer> expression " + Numbers.normalize(numberValue) + " should not be used."))
                        return false;
                } else if (zeroTreatment == ZeroTreatment.Info)
                    errorReporter.logInfo(locator, "Zero <integer> expression " + Numbers.normalize(numberValue) + " used.");
            }
            if (outputInteger != null)
                outputInteger[0] = Integer.valueOf(numberValue);
            return true;
        } else
            return false;
    }

    private static void badInteger(String value, Locator locator, ErrorReporter errorReporter, NegativeTreatment negativeTreatment, ZeroTreatment zeroTreatment) {
        boolean negative = false;
        int numberValue = 0;

        do {
            int valueIndex = 0;
            int valueLength = value.length();
            BigInteger integralPart = null;
            char c;

            // whitespace before optional sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, XML space padding not permitted before integer.");
            }

            // optional sign before non-negative-integer
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '+')
                ++valueIndex;
            else if (c == '-') {
                negative = true;
                ++valueIndex;
            }

            // whitespace before non-negative-number
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, XML space padding not permitted between sign and non-negative-integer.");
            }

            // non-negative-number (integral part only)
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isDigit(c)) {
                StringBuffer sb = new StringBuffer();
                while (Characters.isDigit(c)) {
                    sb.append(c);
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                if (sb.length() > 0) {
                    try {
                        integralPart = new BigInteger(sb.toString());
                    } catch (NumberFormatException e) {
                    }
                }
            }

            // non-negative-number
            if (integralPart == null) {
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, missing non-negative integer after optional sign.");
            } else {
                numberValue = integralPart.intValue();
            }

            // whitespace after number
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, XML space padding not permitted after integer.");
            }

            // garbage after (number S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, unrecognized characters not permitted after integer, got '" + sb + "'.");
            }

        } while (false);

        if (negative)
            numberValue = -numberValue;
        if ((numberValue < 0) && (negativeTreatment == NegativeTreatment.Error))
            errorReporter.logInfo(locator, "Bad <integer> expression, negative value " + Numbers.normalize(numberValue) + " not permitted.");
        else if ((numberValue == 0) && (zeroTreatment == ZeroTreatment.Error))
            errorReporter.logInfo(locator, "Bad <integer> expression, zero value not permitted.");
    }

    public static boolean isIntegers(String value, Locator locator, ErrorReporter errorReporter, int minComponents, int maxComponents, NegativeTreatment negativeTreatment, ZeroTreatment zeroTreatment, List<Integer> outputIntegers) {
        List<Integer> integers = new java.util.ArrayList<Integer>();
        String [] integerComponents = value.split("[ \t\r\n]+");
        int numComponents = integerComponents.length;
        for (String component : integerComponents) {
            Integer[] integer = new Integer[1];
            if (isInteger(component, locator, errorReporter, negativeTreatment, zeroTreatment, integer))
                integers.add(integer[0]);
            else
                return false;
        }
        if (numComponents < minComponents)
            return false;
        else if (numComponents > maxComponents)
            return false;
        if (outputIntegers != null) {
            outputIntegers.clear();
            outputIntegers.addAll(integers);
        }
        return true;
    }

    public static void badIntegers(String value, Locator locator, ErrorReporter errorReporter, int minComponents, int maxComponents, NegativeTreatment negativeTreatment, ZeroTreatment zeroTreatment) {
        List<Integer> integers = new java.util.ArrayList<Integer>();
        String [] integerComponents = value.split("[ \t\r\n]+");
        int numComponents = integerComponents.length;
        for (String component : integerComponents) {
            Integer[] integer = new Integer[1];
            if (isInteger(component, locator, NullErrorReporter.Reporter, negativeTreatment, zeroTreatment, integer))
                integers.add(integer[0]);
            else
                badInteger(component, locator, errorReporter, negativeTreatment, zeroTreatment);
        }
        if (numComponents < minComponents) {
            errorReporter.logInfo(locator,
                "Missing <integer> expression, got " + numComponents + ", but expected at least " + minComponents + " <integer> expressions.");
        } else if (numComponents > maxComponents) {
            errorReporter.logInfo(locator,
                "Extra <integer> expression, got " + numComponents + ", but expected no more than " + maxComponents + " <integer> expressions.");
        }
    }

}
