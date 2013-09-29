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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.impl.LengthImpl;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class Lengths {

    public static boolean maybeLength(String value) {
        value = value.trim();
        if (value.length() == 0)
            return false;
        char c1 = value.charAt(0);
        char c2 = (value.length() > 1) ? value.charAt(1) : 0;
        char c3 = (value.length() > 2) ? value.charAt(2) : 0;
        if ((c1 == '+') || (c1 == '-')) {
            if (Characters.isDigit(c2))
                return true;
            else if ((c2 == '.') && Characters.isDigit(c3))
                return true;
            else
                return false;
        } else if (Characters.isDigit(c1)) {
            return true;
        } else if ((c1 == '.') && Characters.isDigit(c2)) {
            return true;
        } else {
            return false;
        }
    }

    private static Pattern lengthPattern = Pattern.compile("([\\+\\-]?(?:\\d*.\\d+|\\d+))(\\w+|%)?");
    public static boolean isLength(String value, Locator locator, VerifierContext context, Object[] treatments, Length[] outputLength) {
        Reporter reporter = context.getReporter();
        Matcher m = lengthPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() > 0;
            String number = m.group(1);
            if (number.charAt(0) == '+')
                number = number.substring(1);
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
            if (treatments != null) {
                if (numberValue < 0) {
                    NegativeTreatment negativeTreatment = (NegativeTreatment) treatments[0];
                    if (negativeTreatment == NegativeTreatment.Error)
                        return false;
                    else if (negativeTreatment == NegativeTreatment.Warning) {
                        if (reporter.logWarning(locator, "Negative <length> expression " + Numbers.normalize(numberValue) + " should not be used.")) {
                            treatments[0] = NegativeTreatment.Allow;                        // suppress second warning
                            return false;
                        }
                    } else if (negativeTreatment == NegativeTreatment.Info)
                        reporter.logInfo(locator, "Negative <length> expression " + Numbers.normalize(numberValue) + " used.");
                }
            }
            Length.Unit unitsValue;
            if (m.groupCount() > 1) {
                String units = m.group(2);
                try {
                    unitsValue = Length.Unit.valueOfShorthand(units);
                } catch (IllegalArgumentException e) {
                    return false;
                }
            } else
                return false;
            if (outputLength != null)
                outputLength[0] = new LengthImpl(numberValue, unitsValue);
            return true;
        } else
            return false;
    }

    public static void badLength(String value, Locator locator, VerifierContext context, Object[] treatments) {
        Reporter reporter = context.getReporter();
        boolean negative = false;
        double numberValue = 0;
        Length.Unit units = null;

        do {
            int valueIndex = 0;
            int valueLength = value.length();
            BigDecimal integralPart = null;
            BigDecimal fractionalPart = null;
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
                reporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted before number.");
            }

            // optional sign before non-negative-number
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
                reporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted between sign and non-negative-number.");
            }

            // non-negative-number (integral part)
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
                        integralPart = new BigDecimal(new BigInteger(sb.toString()));
                    } catch (NumberFormatException e) {
                    }
                }
            }

            // non-negative-number (fractional part)
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '.') {
                c = value.charAt(++valueIndex);
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
                            fractionalPart = new BigDecimal(new BigInteger(sb.toString()),sb.length());
                        } catch (NumberFormatException e) {
                        }
                    }
                }
            }

            // number
            if (integralPart == null) {
                if (fractionalPart == null) {
                    reporter.logInfo(locator,
                        "Bad <length> expression, missing number after optional sign.");
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

            // whitespace before units
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted between number and units.");
            }

            // units
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '%') {
                units = Length.Unit.Percentage;
                ++valueIndex;
            } else if (Characters.isLetter(c)) {
                StringBuffer sb = new StringBuffer();
                while (Characters.isLetter(c)) {
                    sb.append(c);
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                String unitsAsString = sb.toString();
                try {
                    units = Length.Unit.valueOfShorthand(unitsAsString);
                } catch (IllegalArgumentException e) {
                    try {
                        units = Length.Unit.valueOfShorthandIgnoringCase(unitsAsString);
                        reporter.logInfo(locator,
                            "Bad <length> expression, units is not expressed with correct case, got '" + unitsAsString + "', expected " + units.shorthand() + "'.");
                    } catch (IllegalArgumentException ee) {
                        reporter.logInfo(locator,
                            "Bad <length> expression, unknown units '" + unitsAsString + "'.");
                    }
                }
            }

            // whitespace after units
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted after units.");
            }

            // garbage after (units S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                reporter.logInfo(locator,
                    "Bad <length> expression, unrecognized characters not permitted after units, got '" + sb + "'.");
            }

        } while (false);

        if (negative)
            numberValue = -numberValue;
        if (treatments != null) {
            NegativeTreatment negativeTreatment = (NegativeTreatment) treatments[0];
            if ((numberValue < 0) && (negativeTreatment == NegativeTreatment.Error))
                reporter.logInfo(locator, "Bad <length> expression, negative value " + Numbers.normalize(numberValue) + " not permitted.");
            if (units == null)
                reporter.logInfo(locator, "Bad <length> expression, missing or unknown units, expected one of " + Length.Unit.shorthands() + ".");
        }
    }

    public static boolean isLengths(String value, Locator locator, VerifierContext context, Integer[] minMax, Object[] treatments, List<Length> outputLengths) {
        Reporter reporter = context.getReporter();
        List<Length> lengths = new java.util.ArrayList<Length>();
        String [] lengthComponents = value.split("[ \t\r\n]+");
        int numComponents = lengthComponents.length;
        for (String component : lengthComponents) {
            Length[] length = new Length[1];
            if (isLength(component, locator, context, treatments, length))
                lengths.add(length[0]);
            else
                return false;
        }
        if (minMax != null) {
            if (numComponents < minMax[0])
                return false;
            else if (numComponents > minMax[1])
                return false;
        }
        if (treatments != null) {
            if (!Units.sameUnits(lengths)) {
                assert treatments.length > 1;
                MixedUnitsTreatment mixedUnitsTreatment = (MixedUnitsTreatment) treatments[1];
                Set<Length.Unit> units = Units.units(lengths);
                if (mixedUnitsTreatment == MixedUnitsTreatment.Error)
                    return false;
                else if (mixedUnitsTreatment == MixedUnitsTreatment.Warning) {
                    if (reporter.logWarning(locator, "Mixed units " +  Length.Unit.shorthands(units) + " should not be used in <length> expressions.")) {
                        treatments[1] = MixedUnitsTreatment.Allow;                          // suppress second warning
                        return false;
                    }
                } else if (mixedUnitsTreatment == MixedUnitsTreatment.Info)
                    reporter.logInfo(locator, "Mixed units " + Length.Unit.shorthands(units) + " used in <length> expressions.");
            }
        }
        if (outputLengths != null) {
            outputLengths.clear();
            outputLengths.addAll(lengths);
        }
        return true;
    }

    public static void badLengths(String value, Locator locator, VerifierContext context, Integer[] minMax, Object[] treatments) {
        Reporter reporter = context.getReporter();
        List<Length> lengths = new java.util.ArrayList<Length>();
        String [] lengthComponents = value.split("[ \t\r\n]+");
        int numComponents = lengthComponents.length;
        for (String component : lengthComponents) {
            Object[] treatmentsInner = (treatments != null) ? new Object[] { treatments[0], treatments[1] } : treatments;
            Length[] length = new Length[1];
            if (isLength(component, locator, context, treatmentsInner, length))
                lengths.add(length[0]);
            else
                badLength(component, locator, context, treatmentsInner);
        }
        if (numComponents < minMax[0]) {
            reporter.logInfo(locator,
                "Missing <length> expression, got " + numComponents + ", but expected at least " + minMax[0] + " <length> expressions.");
        } else if (numComponents > minMax[1]) {
            reporter.logInfo(locator,
                "Extra <length> expression, got " + numComponents + ", but expected no more than " + minMax[1] + " <length> expressions.");
        }
        if (treatments != null) {
            MixedUnitsTreatment mixedUnitsTreatment = (MixedUnitsTreatment) treatments[1];
            if (!Units.sameUnits(lengths) && (mixedUnitsTreatment == MixedUnitsTreatment.Error))
                reporter.logInfo(locator, "Mixed units " + Length.Unit.shorthands(Units.units(lengths)) + " not permitted.");
        }
    }

}
