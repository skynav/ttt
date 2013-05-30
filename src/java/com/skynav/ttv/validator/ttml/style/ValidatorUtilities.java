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
 
package com.skynav.ttv.validator.ttml.style;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.impl.ColorImpl;
import com.skynav.ttv.model.value.impl.LengthImpl;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.util.NullErrorReporter;

public class ValidatorUtilities {

    public enum MixedUnitsTreatment {
        Error,
        Warning,
        Info,
        Allow
    }

    public enum NegativeTreatment {
        Error,
        Warning,
        Info,
        Allow,
    }

    public enum ZeroTreatment {
        Error,
        Warning,
        Info,
        Allow,
    }

    // { isX [, badX] } pairs, in alphabetical order by X

    public static boolean isAuto(String value) {
        return value.equals("auto");
    }

    public static boolean maybeColor(String value) {
        value = value.trim();
        if (value.length() == 0)
            return false;
        else if (value.charAt(0) == '#')
            return true;
        else if (value.indexOf("rgb") == 0)
            return true;
        else if (isLetters(value))
            return true;
        else
            return false;
    }

    public static boolean isColor(String value, Locator locator, ErrorReporter errorReporter, Color[] outputColor) {
        if (isRGBHash(value, outputColor))
            return true;
        else if (isRGBFunction(value, outputColor))
            return true;
        else if (isNamedColor(value, outputColor))
            return true;
        else
            return false;
    }

    public static void badColor(String value, Locator locator, ErrorReporter errorReporter) {
        if (value.charAt(0) == '#')
            badRGBHash(value, locator, errorReporter);
        else if (value.indexOf("rgb") == 0)
            badRGBFunction(value, locator, errorReporter);
        else if (isLetters(value))
            badNamedColor(value, locator, errorReporter);
        else {
            errorReporter.logInfo(locator,
                "Bad <color> expression, got '" + value + "', but expected <#rrggbb>, #<rrggbbaa>, <rgb(...)>, <rgba(...)>, or <named color>.");
        }
    }

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
                    if (errorReporter.logWarning(locator, "Negative <integer> expression " + normalize(numberValue) + " should not be used."))
                        return false;
                } else if (negativeTreatment == NegativeTreatment.Info)
                    errorReporter.logInfo(locator, "Negative <integer> expression " + normalize(numberValue) + " used.");
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, XML space padding not permitted before integer");
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <integer> expression, XML space padding not permitted between sign and non-negative-integer");
            }

            // non-negative-number (integral part only)
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (isDigit(c)) {
                StringBuffer sb = new StringBuffer();
                while (isDigit(c)) {
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
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
            errorReporter.logInfo(locator, "Bad <integer> expression, negative value " + normalize(numberValue) + " not permitted.");
        else if ((numberValue == 0) && (zeroTreatment == ZeroTreatment.Error))
            errorReporter.logInfo(locator, "Bad <integer> expression, zero value not permitted.");
    }

    public static boolean isIntegers(String value, Locator locator, ErrorReporter errorReporter, int minComponents, int maxComponents, NegativeTreatment negativeTreatment, ZeroTreatment zeroTreatment, List<Integer> outputIntegers) {
        List<Integer> integers = new java.util.Vector<Integer>();
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
        List<Integer> integers = new java.util.Vector<Integer>();
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

    public static boolean maybeLength(String value) {
        value = value.trim();
        if (value.length() == 0)
            return false;
        char c1 = value.charAt(0);
        char c2 = (value.length() > 1) ? value.charAt(1) : 0;
        char c3 = (value.length() > 2) ? value.charAt(2) : 0;
        if ((c1 == '+') || (c1 == '-')) {
            if (isDigit(c2))
                return true;
            else if ((c2 == '.') && isDigit(c3))
                return true;
            else
                return false;
        } else if (isDigit(c1)) {
            return true;
        } else if ((c1 == '.') && isDigit(c2)) {
            return true;
        } else {
            return false;
        }
    }

    private static Pattern lengthPattern = Pattern.compile("([\\+\\-]?(?:\\d*.\\d+|\\d+))(\\w+|%)?");
    public static boolean isLength(String value, Locator locator, ErrorReporter errorReporter, NegativeTreatment negativeTreatment, Length[] outputLength) {
        Matcher m = lengthPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() > 0;
            String number = m.group(1);
            if (number.charAt(0) == '+')
                number = number.substring(1);
            double numberValue;
            if (!containsDecimalSeparator(number)) {
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
            if (numberValue < 0) {
                if (negativeTreatment == NegativeTreatment.Error)
                    return false;
                else if (negativeTreatment == NegativeTreatment.Warning) {
                    if (errorReporter.logWarning(locator, "Negative <length> expression " + normalize(numberValue) + " should not be used."))
                        return false;
                } else if (negativeTreatment == NegativeTreatment.Info)
                    errorReporter.logInfo(locator, "Negative <length> expression " + normalize(numberValue) + " used.");
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

    public static void badLength(String value, Locator locator, ErrorReporter errorReporter, NegativeTreatment negativeTreatment) {
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted before number");
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted between sign and non-negative-number");
            }

            // non-negative-number (integral part)
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (isDigit(c)) {
                StringBuffer sb = new StringBuffer();
                while (isDigit(c)) {
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
                if (isDigit(c)) {
                    StringBuffer sb = new StringBuffer();
                    while (isDigit(c)) {
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

            // non-negative-number
            if (integralPart == null) {
                if (fractionalPart == null) {
                    errorReporter.logInfo(locator,
                        "Bad <length> expression, missing non-negative number after optional sign.");
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
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted between number and units.");
            }

            // units
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '%')
                units = Length.Unit.Percentage;
            else if (isLetter(c)) {
                StringBuffer sb = new StringBuffer();
                while (isLetter(c)) {
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
                        errorReporter.logInfo(locator,
                            "Bad <length> expression, units is not expressed with correct case, got '" + unitsAsString + "', expected " + units.shorthand() + "'.");
                    } catch (IllegalArgumentException ee) {
                        errorReporter.logInfo(locator,
                            "Bad <length> expression, unknown units '" + unitsAsString + "'.");
                    }
                }
            }

            // whitespace after units
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <length> expression, XML space padding not permitted after units.");
            }

            // garbage after (units S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                errorReporter.logInfo(locator,
                    "Bad <length> expression, unrecognized characters not permitted after units, got '" + sb + "'.");
            }

        } while (false);

        if (negative)
            numberValue = -numberValue;
        if ((numberValue < 0) && (negativeTreatment == NegativeTreatment.Error))
            errorReporter.logInfo(locator, "Bad <length> expression, negative value " + normalize(numberValue) + " not permitted.");
        if (units == null)
            errorReporter.logInfo(locator, "Bad <length> expression, missing or unknown units, expected one of " + Length.Unit.shorthands() + ".");
    }

    public static boolean isLengths(String value, Locator locator, ErrorReporter errorReporter, int minComponents, int maxComponents, NegativeTreatment negativeTreatment, MixedUnitsTreatment mixedUnitsTreatment, List<Length> outputLengths) {
        List<Length> lengths = new java.util.Vector<Length>();
        String [] lengthComponents = value.split("[ \t\r\n]+");
        int numComponents = lengthComponents.length;
        for (String component : lengthComponents) {
            Length[] length = new Length[1];
            if (isLength(component, locator, errorReporter, negativeTreatment, length))
                lengths.add(length[0]);
            else
                return false;
        }
        if (numComponents < minComponents)
            return false;
        else if (numComponents > maxComponents)
            return false;
        if (!sameUnits(lengths)) {
            Set<Length.Unit> units = units(lengths);
            if (mixedUnitsTreatment == MixedUnitsTreatment.Error)
                return false;
            else if (mixedUnitsTreatment == MixedUnitsTreatment.Warning) {
                if (errorReporter.logWarning(locator, "Mixed units " +  Length.Unit.shorthands(units) + " should not be used in <length> expressions."))
                    return false;
            } else if (mixedUnitsTreatment == MixedUnitsTreatment.Info)
                errorReporter.logInfo(locator, "Mixed units " + Length.Unit.shorthands(units) + " used in <length> expressions.");
        }
        if (outputLengths != null) {
            outputLengths.clear();
            outputLengths.addAll(lengths);
        }
        return true;
    }

    public static void badLengths(String value, Locator locator, ErrorReporter errorReporter, int minComponents, int maxComponents,
        NegativeTreatment negativeTreatment, MixedUnitsTreatment mixedUnitsTreatment) {
        List<Length> lengths = new java.util.Vector<Length>();
        String [] lengthComponents = value.split("[ \t\r\n]+");
        int numComponents = lengthComponents.length;
        for (String component : lengthComponents) {
            Length[] length = new Length[1];
            if (isLength(component, locator, NullErrorReporter.Reporter, negativeTreatment, length))
                lengths.add(length[0]);
            else
                badLength(component, locator, errorReporter, negativeTreatment);
        }
        if (numComponents < minComponents) {
            errorReporter.logInfo(locator,
                "Missing <length> expression, got " + numComponents + ", but expected at least " + minComponents + " <length> expressions.");
        } else if (numComponents > maxComponents) {
            errorReporter.logInfo(locator,
                "Extra <length> expression, got " + numComponents + ", but expected no more than " + maxComponents + " <length> expressions.");
        }
        if (!sameUnits(lengths) && (mixedUnitsTreatment == MixedUnitsTreatment.Error))
            errorReporter.logInfo(locator, "Mixed units " + Length.Unit.shorthands(units(lengths)) + " not permitted.");
    }

    private static Map<String,Color> namedColors;
    private static final double cc80 = 128.0 / 255.0;
    private static final double ccC0 = 192.0 / 255.0;
    private static final Object[][] namedColorValues = new Object[][] {
        { "transparent", new ColorImpl(0,0,0,0)          },
        { "black",       new ColorImpl(0,0,0,1)          },
        { "silver",      new ColorImpl(ccC0,ccC0,ccC0,1) },
        { "gray",        new ColorImpl(cc80,cc80,cc80,1) },
        { "white",       new ColorImpl(1,1,1,1)          },
        { "maroon",      new ColorImpl(cc80,0,0,1)       },
        { "red",         new ColorImpl(1,0,0,1)          },
        { "purple",      new ColorImpl(cc80,0,cc80,1)    },
        { "fuchsia",     new ColorImpl(1,0,1,1)          },
        { "magenta",     new ColorImpl(1,0,1,1)          },
        { "green",       new ColorImpl(0,cc80,0,1)       },
        { "lime",        new ColorImpl(0,1,0,1)          },
        { "olive",       new ColorImpl(cc80,cc80,0,1)    },
        { "yellow",      new ColorImpl(1,1,0,1)          },
        { "navy",        new ColorImpl(0,0,cc80,1)       },
        { "blue",        new ColorImpl(0,0,1,1)          },
        { "teal",        new ColorImpl(0,cc80,cc80,1)    },
        { "aqua",        new ColorImpl(0,1,1,1)          },
        { "cyan",        new ColorImpl(0,1,1,1)          },
    };
    static {
        namedColors = new java.util.HashMap<String,Color>();
        for (Object[] namedColor : namedColorValues) {
            namedColors.put((String) namedColor[0], (Color) namedColor[1]);
        }
    }

    private static boolean isNamedColor(String value, Color[] outputColor) {
        if (namedColors.containsKey(value)) {
            if (outputColor != null)
                outputColor[0] = namedColors.get(value);
            return true;
        } else
            return false;
    }

    private static void badNamedColor(String value, Locator locator, ErrorReporter errorReporter) {
        assert isLetters(value);
        errorReporter.logInfo(locator, "Unknown named color, got '" + value + "'.");
    }

    public static boolean isNormal(String value) {
        return value.equals("normal");
    }

    private static boolean isRGBComponent(String value, int minValue, int maxValue, Double[] outputValue) {
        try {
            int componentValue = Integer.parseInt(value);
            if ((componentValue < minValue) || (componentValue > maxValue))
                return false;
            else {
                if (outputValue != null)
                    outputValue[0] = Double.valueOf((double) componentValue / 255.0);
                return true;
            }
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static void badRGBComponent(String value, Locator locator, ErrorReporter errorReporter, int minValue, int maxValue) {
        try {
            int componentValue = Integer.parseInt(value);
            if ((componentValue < minValue) || (componentValue > maxValue) ) {
                errorReporter.logInfo(locator,
                    "Component out of range [" + minValue + "," + maxValue +
                    "] in <rgb(...)> or <rgba(...)> color expression, got " + componentValue + ".");
            }
        } catch (NumberFormatException e) {
            if (containsXMLSpace(value)) {
                String trimmedComponent = value.trim();
                try {
                    Integer.parseInt(trimmedComponent);
                    errorReporter.logInfo(locator,
                        "XML space padding not permitted in <rgb(...)> or <rgba(...)> color expression component, got '" + value + "'.");
                } catch (NumberFormatException ee) {
                    errorReporter.logInfo(locator,
                        "Component in <rgb(...)> or <rgba(...)> color expression is not a non-negative integer, got '" + value + "'.");
                }
            } else {
                errorReporter.logInfo(locator,
                    "Component in <rgb(...)> or <rgba(...)> color expression is not a non-negative integer, got '" + value + "'.");
            }
        }
    }

    private static boolean isRGBComponents(String components, int[][] valueLimits, Color[] outputColor) {
        String[] colorComponents = components.split(",");
        int numComponents = colorComponents.length;
        if (numComponents != valueLimits.length)
            return false;
        double r = 0;
        double g = 0;
        double b = 0;
        double a = 1;
        int componentIndex = 0;
        for (String component : colorComponents) {
            int[] limits = valueLimits[componentIndex++];
            int minValue = limits[0];
            int maxValue = limits[1];
            Double[] outputValue = new Double[1];
            if (!isRGBComponent(component, minValue, maxValue, outputValue))
                return false;
            if (componentIndex == 0)
                r = outputValue[0].doubleValue();
            if (componentIndex == 1)
                g = outputValue[0].doubleValue();
            if (componentIndex == 2)
                b = outputValue[0].doubleValue();
            if (componentIndex == 3)
                a = outputValue[0].doubleValue();
        }
        if (outputColor != null)
            outputColor[0] = new ColorImpl(r, g, b, a);
        return true;
    }

    private static void badRGBComponents(String components, Locator locator, ErrorReporter errorReporter, int[][] valueLimits) {
        String[] colorComponents = components.split(",");
        int numComponents = valueLimits.length;
        if (colorComponents.length != numComponents) {
            errorReporter.logInfo(locator,
                ((colorComponents.length < numComponents) ? "Missing" : "Extra" ) +
                " component in <rgb(...)> or <rgba(...)> color expression, got " + colorComponents.length +
                ", expected " + numComponents + " components.");
        }
        int componentIndex = 0;
        for (String component : colorComponents) {
            // if extra component, then use last limits
            int[] limits = (componentIndex < numComponents) ? valueLimits[componentIndex++] : valueLimits[numComponents - 1];
            int minValue = limits[0];
            int maxValue = limits[1];
            if (!isRGBComponent(component, minValue, maxValue, null))
                badRGBComponent(component, locator, errorReporter, minValue, maxValue);
        }
    }

    private static final int[][] rgbComponentLimits = new int[][] { { 0, 255 }, { 0, 255 }, { 0, 255 } };
    private static final int[][] rgbaComponentLimits = new int[][] { { 0, 255 }, { 0, 255 }, { 0, 255 }, { 0, 255 } };
    private static boolean isRGBFunction(String value, Color[] outputColor) {
        int componentsStart;
        int[][] valueLimits;
        if (value.indexOf("rgb(") == 0) {
            componentsStart = 4;
            valueLimits = rgbComponentLimits;
        } else if (value.indexOf("rgba(") == 0) {
            componentsStart = 5;
            valueLimits = rgbaComponentLimits;
        } else
            return false;
        if (value.charAt(value.length() - 1) != ')')
            return false;
        return isRGBComponents(value.substring(componentsStart, value.length() - 1), valueLimits, outputColor);
    }

    private static void badRGBFunction(String value, Locator locator, ErrorReporter errorReporter) {
        assert value.indexOf("rgb") == 0;
        int opIndex = value.indexOf("(");
        if (opIndex < 0) {
            errorReporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', missing opening parenthesis of argument list.");
        } else {
            String functionName = value.substring(0, opIndex);
            if (!functionName.equals("rgb") && !functionName.equals("rgba")) {
                errorReporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', incorrect function name, expect 'rgb' or 'rgba'.");
            }
        }
        int cpIndex = value.indexOf(")");
        if (cpIndex < 0)
            errorReporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', missing closing parenthesis of argument list.");
        if ((opIndex >= 0) && (cpIndex >= 0)) {
            int componentsStart = opIndex + 1;
            int componentsEnd = cpIndex;
            if (componentsStart <= componentsEnd) {
                int[][] valueLimits;
                if (value.indexOf("rgb(") == 0)
                    valueLimits = rgbComponentLimits;
                else if (value.indexOf("rgba(") == 0)
                    valueLimits = rgbaComponentLimits;
                else
                    valueLimits = null;
                if (valueLimits != null)
                    badRGBComponents(value.substring(componentsStart, componentsEnd), locator, errorReporter, valueLimits);
            }
        }
    }

    private static boolean isRGBHash(String value, Color[] outputColor) {
        int numDigits = value.length() - 1;
        if ((numDigits != 6) || (numDigits != 8))
            return false;
        else if (!isHexDigits(value.substring(1)))
            return false;
        else {
            if (outputColor != null)
                outputColor[0] = ColorImpl.fromRGBHash(value);
            return true;
        }
    }

    private static void badRGBHash(String value, Locator locator, ErrorReporter errorReporter) {

        String hexDigits = null;

        do {
            int valueIndex = 0;
            int valueLength = value.length();
            char c;

            // whitespace before hash sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted before '#'.");
            }

            // hash sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c != '#') {
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, expected '#', got '" + c + "'.");
                break;
            } else
                valueIndex++;

            // whitespace after hash sign
            if (valueIndex == valueLength) {
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
                break;
            }
            c = value.charAt(valueIndex);
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted after '#'.");
            }

            // hex digits
            if (valueIndex == valueLength) {
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
                break;
            }
            c = value.charAt(valueIndex);
            if (isHexDigit(c)) {
                StringBuffer sb = new StringBuffer();
                while (isHexDigit(c)) {
                    sb.append(c);
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                hexDigits = sb.toString();
            }
            
            // hex digits count
            if (hexDigits != null) {
                int numDigits = hexDigits.length();
                if ((numDigits != 6) && (numDigits != 8)) {
                    errorReporter.logInfo(locator,
                        "Bad <#...> color expression, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits, got " + numDigits + " digits.");
                }
            } else {
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
            }

            // whitespace after hex digits
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (isXMLSpace(c)) {
                while (isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted after hexadecimal digits.");
            }

            // garbage after (hexDigit+ S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                errorReporter.logInfo(locator,
                    "Bad <#...> color expression, unrecognized characters not permitted after " +
                    ((hexDigits ==  null) ? "sign" :  "hexadecimal digits") + ", got '" + sb + "'.");
            }

        } while (false);

    }

    // other value utilities

    private static boolean containsXMLSpace(String value) {
        for (int i = 0, n = value.length(); i < n; ++i) {
            if (isXMLSpace(value.charAt(i)))
                return true;
        }
        return false;
    }

    public static boolean isAllXMLSpace(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!isXMLSpace(value.charAt(i)))
                return false;
        }
        return true;
    }

    private static boolean isXMLSpace(char c) {
        return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
    }

    private static boolean isHexDigits(String value) {
        for (int i = 0, n = value.length(); i < n; ++i) {
            if (!isHexDigit(value.charAt(i)))
                return false;
        }
        return true;
    }

    private static boolean isHexDigit(char c) {
        return ((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')) || ((c >= 'a') && (c <= 'f'));
    }

    private static boolean isDigit(char c) {
        return ((c >= '0') && (c <= '9'));
    }

    private static boolean isLetters(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!isLetter(value.charAt(i)))
                return false;
        }
        return true;
    }

    private static boolean isLetter(char c) {
        return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
    }

    private static boolean containsDecimalSeparator(String number) {
        for (int i = 0, n = number.length(); i < n; ++i) {
            if (number.charAt(i) == '.')
                return true;
        }
        return false;
    }

    private static String normalize(double number) {
        if (Math.floor(number) == number)
            return Long.toString((long) number);
        else
            return Double.toString(number);
    }

    public static boolean sameUnits(List<Length> lengths) {
        return units(lengths).size() < 2;
    }

    private static Set<Length.Unit> units(List<Length> lengths) {
        Set<Length.Unit> units = new java.util.HashSet<Length.Unit>();
        for (Length l : lengths) {
            units.add(l.getUnits());
        }
        return units;
    }

}
