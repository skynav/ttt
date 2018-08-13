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

    private static final Pattern numberPattern = Pattern.compile("(([\\+\\-]?)(?:\\d*\\.\\d+|\\d+))");
    public static boolean isNumber(String value, Location location, VerifierContext context, Object[] treatments, Double[] outputNumber) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Matcher m = numberPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() > 0;
            // number
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
            // sign treatment
            SignTreatment signTreatment;
            if (treatments != null)
                signTreatment = (SignTreatment) Treatment.findTreatment(treatments, SignTreatment.class);
            else
                signTreatment = null;
            if (signTreatment != null) {
                String sign = m.group(2);
                if ((sign != null) && !sign.isEmpty()) {
                    if (signTreatment == SignTreatment.Error)
                        return false;
                    else if (reporter != null) {
                        if (signTreatment == SignTreatment.Warning) {
                            if (reporter.logWarning(reporter.message(locator, "*KEY*",
                                    "Signed <number> expression {0} should not be used.", Numbers.normalize(numberValue)))) {
                                return false;
                            }
                        } else if (signTreatment == SignTreatment.Info) {
                            reporter.logInfo(reporter.message(locator,
                                "*KEY*", "Signed <number> expression {0} used.", Numbers.normalize(numberValue)));
                        }
                    }
                }
            }
            // negative treatment
            NegativeTreatment negativeTreatment;
            if (treatments != null)
                negativeTreatment = (NegativeTreatment) Treatment.findTreatment(treatments, NegativeTreatment.class);
            else
                negativeTreatment = null;
            if (negativeTreatment != null) {
                if (numberValue < 0) {
                    if (negativeTreatment == NegativeTreatment.Error)
                        return false;
                    else if (reporter != null) {
                        if (negativeTreatment == NegativeTreatment.Warning) {
                            if (reporter.logWarning(reporter.message(locator, "*KEY*",
                                    "Negative <number> expression {0} should not be used.", Numbers.normalize(numberValue)))) {
                                return false;
                            }
                        } else if (negativeTreatment == NegativeTreatment.Info) {
                            reporter.logInfo(reporter.message(locator,
                                "*KEY*", "Negative <number> expression {0} used.", Numbers.normalize(numberValue)));
                        }
                    }
                }
            }
            // optionally return number
            if (outputNumber != null) {
                outputNumber[0] = Double.valueOf(numberValue);
            }
            return true;
        } else
            return false;
    }

    private static final String PLUS            = new String("+");
    private static final String MINUS           = new String("-");
    public static void badNumber(String value, Location location, VerifierContext context, Object[] treatments) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        String sign = null;
        boolean negative = false;
        double numberValue = 0;

        do {
            int valueIndex = 0;
            int valueLength = value.length();
            BigDecimal integralPart = null;
            BigDecimal fractionalPart = null;
            char c;

            // whitespace before optional sign
            if (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (Characters.isXMLSpace(c)) {
                    while (Characters.isXMLSpace(c)) {
                        if (++valueIndex >= valueLength)
                            break;
                        c = value.charAt(valueIndex);
                    }
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <number> expression, XML space padding not permitted before number."));
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <number> expression, expected optional sign or non-negative-number, got ''{0}''.", value.substring(valueIndex)));
                break;
            }

            // optional sign before non-negative-number
            if (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (c == '+') {
                    sign = PLUS;
                    ++valueIndex;
                } else if (c == '-') {
                    sign = MINUS;
                    negative = true;
                    ++valueIndex;
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <number> expression, expected optional sign or non-negative-number, got ''{0}''.", value.substring(valueIndex)));
                break;
            }

            // whitespace before non-negative-number
            if (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (Characters.isXMLSpace(c)) {
                    while (Characters.isXMLSpace(c)) {
                        if (++valueIndex >= valueLength)
                            break;
                        c = value.charAt(valueIndex);
                    }
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <number> expression, XML space padding not permitted between sign and non-negative-number."));
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <number> expression, expected non-negative-number, got ''{0}''.", value.substring(valueIndex)));
                break;
            }

            // non-negative-number (integral part)
            if (valueIndex < valueLength) {
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
                            integralPart = new BigDecimal(new BigInteger(sb.toString()));
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <number> expression, expected non-negative-number, got ''{0}''.", value.substring(valueIndex)));
                break;
            }

            // non-negative-number (fractional part)
            if (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (c == '.') {
                    if (++valueIndex < valueLength) {
                        c = value.charAt(valueIndex);
                        if (Characters.isDigit(c)) {
                            StringBuffer sb = new StringBuffer("");
                            while (Characters.isDigit(c)) {
                                sb.append(c);
                                if (++valueIndex >= valueLength)
                                    break;
                                c = value.charAt(valueIndex);
                            }
                            if (sb.length() > 0) {
                                try {
                                    fractionalPart = new BigDecimal(new BigInteger(sb.toString()),sb.length());
                                } catch (NumberFormatException e) {
                                }
                            }
                        }
                    } else {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <number> expression, expected fractional part of non-negative-number, got ''{0}''.", value.substring(valueIndex)));
                    }
                }
            }

            // number
            if (integralPart == null) {
                if (fractionalPart == null) {
                    reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <number> expression, missing number after optional sign."));
                } else {
                    numberValue = fractionalPart.doubleValue();
                }
            } else {
                if (fractionalPart == null) {
                    numberValue = integralPart.doubleValue();
                } else {
                    numberValue = integralPart.add(fractionalPart).doubleValue();
                }
            }

            // whitespace after number
            if (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (Characters.isXMLSpace(c)) {
                    while (Characters.isXMLSpace(c)) {
                        if (++valueIndex >= valueLength)
                            break;
                        c = value.charAt(valueIndex);
                    }
                    reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <number> expression, XML space padding not permitted after number."));
                }
            }

            // garbage after (number S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <number> expression, unrecognized characters not permitted after number, got ''{0}''.", sb.toString()));
            }

        } while (false);

        if (negative)
            numberValue = -numberValue;

        if (treatments != null) {
            if (sign != null) {
                SignTreatment signTreatment = (SignTreatment) Treatment.findTreatment(treatments, SignTreatment.class);
                if (signTreatment != null) {
                    if (signTreatment == SignTreatment.Error) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <number> expression, sign ''{0}'' not permitted.", sign));
                    }
                }
            }
            if (numberValue < 0) {
                NegativeTreatment negativeTreatment = (NegativeTreatment) Treatment.findTreatment(treatments, NegativeTreatment.class);
                if (negativeTreatment != null) {
                    if (negativeTreatment == NegativeTreatment.Error) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Bad <number> expression, negative value {0} not permitted.", Numbers.normalize(numberValue)));
                    }
                }
            }
        }
    }
    
    public static String normalize(double number) {
        if (Math.floor(number) == number)
            return Long.toString((long) number);
        else
            return Double.toString(number);
    }

}
