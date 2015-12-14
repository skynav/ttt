/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.ZeroTreatment;

public class Integers {

    private static final Pattern integerPattern = Pattern.compile("([\\+\\-]?)(\\d+)");
    private static boolean isInteger(String value, Location location, VerifierContext context, Object[] treatments, Integer[] outputInteger) {
        Reporter reporter = (context != null) ? context.getReporter() : null;
        Locator locator = location.getLocator();
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
            if (treatments != null) {
                if (numberValue < 0) {
                    NegativeTreatment negativeTreatment = (NegativeTreatment) treatments[0];
                    if (negativeTreatment == NegativeTreatment.Error)
                        return false;
                    else if (reporter != null) {
                        if (negativeTreatment == NegativeTreatment.Warning) {
                            if (reporter.logWarning(reporter.message(locator, "*KEY*",
                                "Negative <integer> expression {0} should not be used.", Numbers.normalize(numberValue)))) {
                                treatments[0] = NegativeTreatment.Allow;                        // suppress second warning
                                return false;
                            }
                        } else if (negativeTreatment == NegativeTreatment.Info) {
                            reporter.logInfo(reporter.message(locator,
                                "*KEY*", "Negative <integer> expression {0} used.", Numbers.normalize(numberValue)));
                        }
                    }
                } else if (numberValue == 0) {
                    assert treatments.length > 1;
                    ZeroTreatment zeroTreatment = (ZeroTreatment) treatments[1];
                    if (zeroTreatment == ZeroTreatment.Error)
                        return false;
                    else if (reporter != null) {
                        if (zeroTreatment == ZeroTreatment.Warning) {
                            if (reporter.logWarning(reporter.message(locator,
                                "*KEY*", "Zero <integer> expression {0} should not be used.", Numbers.normalize(numberValue)))) {
                                treatments[1] = ZeroTreatment.Allow;                            // suppress second warning
                                return false;
                            }
                        } else if (zeroTreatment == ZeroTreatment.Info) {
                            reporter.logInfo(reporter.message(locator,
                                "*KEY*", "Zero <integer> expression {0} used.", Numbers.normalize(numberValue)));
                        }
                    }
                }
            }
            if (outputInteger != null)
                outputInteger[0] = Integer.valueOf(numberValue);
            return true;
        } else
            return false;
    }

    private static void badInteger(String value, Location location, VerifierContext context, Object[] treatments) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
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
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad <integer> expression, XML space padding not permitted before integer."));
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
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad <integer> expression, XML space padding not permitted between sign and non-negative-integer."));
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
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad <integer> expression, missing non-negative integer after optional sign."));
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
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad <integer> expression, XML space padding not permitted after integer."));
            }

            // garbage after (number S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <integer> expression, unrecognized characters not permitted after integer, got ''{0}''.", sb.toString()));
            }

        } while (false);

        if (negative)
            numberValue = -numberValue;
        if (treatments != null) {
            if ((numberValue < 0) && (((NegativeTreatment)treatments[0]) == NegativeTreatment.Error)) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <integer> expression, negative value {0} not permitted.", Numbers.normalize(numberValue)));
            } else if ((numberValue == 0) && (((ZeroTreatment)treatments[1]) == ZeroTreatment.Error)) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <integer> expression, zero value not permitted."));
            }
        }
    }

    public static boolean isIntegers(String value, Location location, VerifierContext context, Integer[] minMax, Object[] treatments, List<Integer> outputIntegers) {
        List<Integer> integers = new java.util.ArrayList<Integer>();
        String [] integerComponents = value.split("[ \t\r\n]+");
        int numComponents = integerComponents.length;
        for (String component : integerComponents) {
            Integer[] integer = new Integer[1];
            if (isInteger(component, location, context, treatments, integer))
                integers.add(integer[0]);
            else
                return false;
        }
        if (numComponents < minMax[0])
            return false;
        else if (numComponents > minMax[1])
            return false;
        if (outputIntegers != null) {
            outputIntegers.clear();
            outputIntegers.addAll(integers);
        }
        return true;
    }

    public static void badIntegers(String value, Location location, VerifierContext context, Integer[] minMax, Object[] treatments) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        String [] integerComponents = value.split("[ \t\r\n]+");
        int numComponents = integerComponents.length;
        Object[] treatmentsInner = (treatments != null) ? new Object[] { treatments[0], treatments[1] } : null;
        for (String component : integerComponents) {
            if (!isInteger(component, location, context, treatmentsInner, null))
                badInteger(component, location, context, treatmentsInner);
        }
        if (minMax != null) {
            if (numComponents < minMax[0]) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Missing <integer> expression, got {0}, but expected at least {1} <integer> expressions.", numComponents, minMax[0]));
            } else if (numComponents > minMax[1]) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Extra <integer> expression, got {0}, but expected no more than {1} <integer> expressions.", numComponents, minMax[1]));
            }
        }
    }

}
