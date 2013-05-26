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
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xml.sax.Locator;

import com.skynav.ttv.util.ErrorReporter;

public class ValidatorUtilities {

    public static boolean isAuto(String value, Locator locator, ErrorReporter errorReporter) {
        return value.equals("auto");
    }

    public static boolean isColor(String value, Locator locator, ErrorReporter errorReporter) {
        if (isRGBHash(value, locator, errorReporter))
            return true;
        else if (isRGBFunction(value, locator, errorReporter))
            return true;
        else if (isRGBAFunction(value, locator, errorReporter))
            return true;
        else if (isNamedColor(value, locator, errorReporter))
            return true;
        else
            return false;
    }

    private static boolean isRGBHash(String value, Locator locator, ErrorReporter errorReporter) {
        int length = value.length();
        if (length == 0)
            return false;
        else if (value.charAt(0) != '#')
            return false;
        else if ((length != 7) && (length != 9)) {
            errorReporter.logInfo(locator, "Expected <#...> color expression to contain either 6 or 8 characters, got " + (length - 1) + " characters.");
            return false;
        } else if (!isHexDigits(value.substring(1))) {
            errorReporter.logInfo(locator, "Expected only hexadecimal digits in <#...> color expression, got '" + value.substring(1) + "'.");
            return false;
        } else
            return true;
    }

    private static boolean isRGBFunction(String value, Locator locator, ErrorReporter errorReporter) {
        int length = value.length();
        if (value.indexOf("rgb(") != 0)
            return false;
        else if (value.indexOf(")") != (length - 1)) {
            errorReporter.logInfo(locator, "Missing closing parenthesis in <rgb(...)> color expression, got '" + value + "'.");
            return false;
        } else if (!isRGBComponents(value.substring(4, length - 1), 3, locator, errorReporter))
            return false;
        else
            return true;
    }

    private static boolean isRGBAFunction(String value, Locator locator, ErrorReporter errorReporter) {
        int length = value.length();
        if (value.indexOf("rgba(") != 0)
            return false;
        else if (value.indexOf(")") != (length - 1)) {
            errorReporter.logInfo(locator, "Missing closing parenthesis in <rgba(...)> color expression, got '" + value + "'.");
            return false;
        } else if (!isRGBComponents(value.substring(5, length - 1), 4, locator, errorReporter))
            return false;
        else
            return true;
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

    private static boolean isRGBComponents(String value, int numComponents, Locator locator, ErrorReporter errorReporter) {
        String[] components = value.split(",");
        if (components.length != numComponents) {
            errorReporter.logInfo(locator, ((components.length < numComponents) ? "Missing" : "Extra" ) +
                " component in <rgb(...)> or <rgba(...)> color expression, got " + components.length + ", expected " + numComponents + " components.");
            return false;
        }
        for (String component : components) {
            try {
                int componentValue = Integer.parseInt(component);
                if ((componentValue < 0) || (componentValue > 255) ) {
                    errorReporter.logInfo(locator, "Component out of range [0,255] in <rgb(...)> or <rgba(...)> color expression, got " + componentValue + ".");
                    return false;
                }
            } catch (NumberFormatException e) {
                if (containsXMLSpace(component)) {
                    String trimmedComponent = component.trim();
                    try {
                        Integer.parseInt(trimmedComponent);
                        errorReporter.logInfo(locator, "XML space padding not permitted in <rgb(...)> or <rgba(...)> color expression component, got '" + component + "'.");
                    } catch (NumberFormatException ee) {
                        errorReporter.logInfo(locator, "Component in <rgb(...)> or <rgba(...)> color expression is not a number, got '" + component + "'.");
                    }
                } else
                    errorReporter.logInfo(locator, "Component in <rgb(...)> or <rgba(...)> color expression is not a number, got '" + component + "'.");
                return false;
            }
        }
        return true;
    }

    private static Set<String> namedColors;
    private static final String[] namedColorValues = new String[] {
        "transparent",
        "black",
        "silver",
        "gray",
        "white",
        "maroon",
        "red",
        "purple",
        "fuchsia",
        "magenta",
        "green",
        "lime",
        "olive",
        "yellow",
        "navy",
        "blue",
        "teal",
        "aqua",
        "cyan",
    };
    static {
        namedColors = new java.util.HashSet<String>();
        for (String namedColor : namedColorValues) {
            namedColors.add(namedColor);
        }
    }

    private static boolean isNamedColor(String value, Locator locator, ErrorReporter errorReporter) {
        if (!namedColors.contains(value)) {
            if (isLetters(value)) {
                errorReporter.logInfo(locator, "Unknown named color, got '" + value + "'.");
            }
            return false;
        } else
            return true;
    }

    public enum NegativeLengthTreatment {
        ErrorOnNegative,
        WarnOnNegative,
        InfoOnNegative,
    }

    public static boolean isLength(String value, Locator locator, ErrorReporter errorReporter, int minLengthCount, int maxLengthCount, NegativeLengthTreatment treatment) {
        String [] lengthComponents = value.split("[ \t\r\n]+");
        int numComponents = lengthComponents.length;
        for (String component : lengthComponents) {
            if (!isLength(component, locator, errorReporter, treatment))
                return false;
        }
        if (numComponents < minLengthCount) {
            errorReporter.logInfo(locator, "Missing <length> component, got " + numComponents + ", expected at least " + minLengthCount + " components.");
            return false;
        } else if (numComponents > maxLengthCount) {
            errorReporter.logInfo(locator, "Extra <length> component, got " + numComponents + ", expected no more than " + maxLengthCount + " components.");
            return false;
        } else
            return true;
    }

    private static Pattern lengthPattern = Pattern.compile("([\\+\\-]?(?:\\d*.\\d+|\\d+))(\\w+|%)");
    private static boolean isLength(String value, Locator locator, ErrorReporter errorReporter, NegativeLengthTreatment treatment) {
        Matcher m = lengthPattern.matcher(value);
        if (m.matches()) {
            assert m.groupCount() == 2;
            String number = m.group(1);
            if (number.charAt(0) == '+')
                number = number.substring(1);
            double numberValue;
            if (!containsDecimalSeparator(number)) {
                BigInteger integerValue;
                try {
                    integerValue = new BigInteger(number);
                    numberValue = integerValue.doubleValue();
                } catch (NumberFormatException e) {
                    errorReporter.logInfo(locator, "Bad integer '" + number + "'.");
                    return false;
                }
            } else {
                BigDecimal decimalValue;
                try {
                    decimalValue = new BigDecimal(number);
                    numberValue = decimalValue.doubleValue();
                } catch (NumberFormatException e) {
                    errorReporter.logInfo(locator, "Bad real '" + number + "'.");
                    return false;
                }
            }
            if (numberValue < 0) {
                if (treatment == NegativeLengthTreatment.ErrorOnNegative) {
                    errorReporter.logError(locator, "Negative length " + normalize(numberValue) + " not permitted.");
                    return false;
                } else if (treatment == NegativeLengthTreatment.WarnOnNegative) {
                    errorReporter.logWarning(locator, "Negative length " + normalize(numberValue) + " should not be used.");
                } else if (treatment == NegativeLengthTreatment.InfoOnNegative) {
                    errorReporter.logInfo(locator, "Negative length " + normalize(numberValue) + " used.");
                }
            }
            String units = m.group(2);
            if (!isLengthUnits(units)) {
                errorReporter.logInfo(locator, "Unknown units '" + units + "' in <length> component, expected one of {'px','em','c','%'}.");
                return false;
            }
            return true;
        } else {
            errorReporter.logInfo(locator, "Bad <length> component '" + value + "', neither a scalar nor percentage length.");
            return false;
        }
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

    private static boolean isLengthUnits(String value) {
        if (value.equals("px"))
            return true;
        else if (value.equals("em"))
            return true;
        else if (value.equals("c"))
            return true;
        else if (value.equals("%"))
            return true;
        else
            return false;
    }

    private static boolean isLetters(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!Character.isLetter(value.charAt(i)))
                return false;
        }
        return true;
    }

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

}
