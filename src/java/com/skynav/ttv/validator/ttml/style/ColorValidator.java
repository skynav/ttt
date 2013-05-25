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

import java.util.Set;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.util.NullErrorReporter;
import com.skynav.ttv.validator.StyleValueValidator;

public class ColorValidator implements StyleValueValidator {

    @SuppressWarnings("unused")
    private Model model;

    public void setModel(Model model) {
        this.model = model;
    }

    public boolean validate(String name, String value, Locator locator, ErrorReporter errorReporter) {
        if (isRGBHash(value, locator, errorReporter))
            return true;
        else if (isRGBFunction(value, locator, errorReporter))
            return true;
        else if (isRGBAFunction(value, locator, errorReporter))
            return true;
        else if (isNamedColor(value, locator, errorReporter))
            return true;
        else {
            if (value.length() == 0) {
                errorReporter.logInfo(locator, "Empty color expression not permitted, got '" + value + "'.");
            } else if (isAllXMLSpace(value)) {
                errorReporter.logInfo(locator, "Color expression is entirely XML space characters, got '" + value + "'.");
            } else if (!value.equals(value.trim())) {
                if (validate(name, value.trim(), locator, new NullErrorReporter()))
                    errorReporter.logInfo(locator, "XML space padding not permitted on color expression, got '" + value + "'.");
            }
            errorReporter.logError(locator, "Invalid color value '" + value + "'.");
            return false;
        }
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

    private static boolean isAllXMLSpace(String value) {
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
