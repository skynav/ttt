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

import java.util.Collections;
import java.util.Map;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.impl.ColorImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Colors {

    public static boolean maybeColor(String value) {
        value = value.trim();
        if (value.length() == 0)
            return false;
        else if (value.charAt(0) == '#')
            return true;
        else if (value.indexOf("rgb") == 0)
            return true;
        else if (Strings.isLetters(value))
            return true;
        else
            return false;
    }

    public static boolean isColor(String value, Location location, VerifierContext context, Color[] outputColor) {
        if (isRGBHash(value, outputColor))
            return true;
        else if (isRGBFunction(value, outputColor))
            return true;
        else if (isNamedColor(value, outputColor))
            return true;
        else
            return false;
    }

    public static void badColor(String value, Location location, VerifierContext context) {
        if (value.charAt(0) == '#')
            badRGBHash(value, location, context);
        else if (value.indexOf("rgb") == 0)
            badRGBFunction(value, location, context);
        else if (Strings.isLetters(value))
            badNamedColor(value, location, context);
        else {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(location.getLocator(), "*KEY*",
                "Bad <color> expression, got ''{0}'', but expected <#rrggbb>, #<rrggbbaa>, <rgb(...)>, <rgba(...)>, or <named color>.", value));
        }
    }

    private static final Map<String,Color> namedColors;
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
        Map<String,Color> m = new java.util.HashMap<String,Color>();
        for (Object[] namedColor : namedColorValues) {
            m.put((String) namedColor[0], (Color) namedColor[1]);
        }
        namedColors = Collections.unmodifiableMap(m);
    }

    private static boolean isNamedColor(String value, Color[] outputColor) {
        if (namedColors.containsKey(value)) {
            if (outputColor != null)
                outputColor[0] = namedColors.get(value);
            return true;
        } else
            return false;
    }

    private static void badNamedColor(String value, Location location, VerifierContext context) {
        assert Strings.isLetters(value);
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(location.getLocator(), "*KEY*", "Unknown named color, got ''{0}''.", value));
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

    private static void badRGBComponent(String value, Location location, VerifierContext context, int minValue, int maxValue) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        try {
            int componentValue = Integer.parseInt(value);
            if ((componentValue < minValue) || (componentValue > maxValue) ) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Component out of range [{0},{1}] in <rgb(...)> or <rgba(...)> color expression, got {2}.",
                    minValue, maxValue, componentValue));
            }
        } catch (NumberFormatException e) {
            if (Strings.containsXMLSpace(value)) {
                String trimmedComponent = value.trim();
                try {
                    Integer.parseInt(trimmedComponent);
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "XML space padding not permitted in <rgb(...)> or <rgba(...)> color expression component, got ''{0}''.", value));
                } catch (NumberFormatException ee) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Component in <rgb(...)> or <rgba(...)> color expression is not a non-negative integer, got ''{0}''.", value));
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Component in <rgb(...)> or <rgba(...)> color expression is not a non-negative integer, got ''{0}''.", value));
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

    private static void badRGBComponents(String components, Location location, VerifierContext context, int[][] valueLimits) {
        String[] colorComponents = components.split(",");
        int numComponents = valueLimits.length;
        if (colorComponents.length != numComponents) {
            Reporter reporter = context.getReporter();
            Locator locator = location.getLocator();
            Message message;
            if (colorComponents.length < numComponents) {
                message = reporter.message(locator, "*KEY*",
                    "Missing component in <rgb(...)> or <rgba(...)> color expression, got {0}, expected {1} components.",
                    colorComponents.length, numComponents);
            } else {
                message = reporter.message(locator, "*KEY*",
                    "Extra component in <rgb(...)> or <rgba(...)> color expression, got {0}, expected {1} components.",
                    colorComponents.length, numComponents);
            }
            reporter.logInfo(message);
        }
        int componentIndex = 0;
        for (String component : colorComponents) {
            // if extra component, then use last limits
            int[] limits = (componentIndex < numComponents) ? valueLimits[componentIndex++] : valueLimits[numComponents - 1];
            int minValue = limits[0];
            int maxValue = limits[1];
            if (!isRGBComponent(component, minValue, maxValue, null))
                badRGBComponent(component, location, context, minValue, maxValue);
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

    private static void badRGBFunction(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        assert value.indexOf("rgb") == 0;
        int opIndex = value.indexOf("(");
        if (opIndex < 0) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Bad RGB function syntax in <color> expression, got ''{0}'', missing opening parenthesis of argument list.", value));
        } else {
            String functionName = value.substring(0, opIndex);
            if (!functionName.equals("rgb") && !functionName.equals("rgba")) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad RGB function syntax in <color> expression, got ''{0}'', incorrect function name, expect 'rgb' or 'rgba'.", value));
            }
        }
        int cpIndex = value.indexOf(")");
        if (cpIndex < 0) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Bad RGB function syntax in <color> expression, got ''{0}'', missing closing parenthesis of argument list.", value));
        }
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
                    badRGBComponents(value.substring(componentsStart, componentsEnd), location, context, valueLimits);
            }
        }
    }

    private static boolean isRGBHash(String value, Color[] outputColor) {
        if (value.length() < 7)
            return false;
        else if (value.charAt(0) != '#')
            return false;
        else {
            String digits = value.substring(1);
            int numDigits = digits.length();
            if ((numDigits != 6) && (numDigits != 8))
                return false;
            else if (!Strings.isHexDigits(digits))
                return false;
            else {
                if (outputColor != null)
                    outputColor[0] = ColorImpl.fromRGBHash(digits);
                return true;
            }
        }
    }

    private static void badRGBHash(String value, Location location, VerifierContext context) {

        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        String hexDigits = null;

        do {
            int valueIndex = 0;
            int valueLength = value.length();
            char c;

            // whitespace before hash sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, XML space padding not permitted before '#'."));
            }

            // hash sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c != '#') {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, expected '#', got '" + c + "'."));
                break;
            } else
                valueIndex++;

            // whitespace after hash sign
            if (valueIndex == valueLength) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'."));
                break;
            }
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, XML space padding not permitted after '#'."));
            }

            // hex digits
            if (valueIndex == valueLength) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'."));
                break;
            }
            c = value.charAt(valueIndex);
            if (Characters.isHexDigit(c)) {
                StringBuffer sb = new StringBuffer();
                while (Characters.isHexDigit(c)) {
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
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad <#...> color expression, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits, got {0} digits.", numDigits));
                }
            } else {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'."));
            }

            // whitespace after hex digits
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Bad <#...> color expression, XML space padding not permitted after hexadecimal digits."));
            }

            // garbage after (hexDigit+ S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                Message message;
                if (hexDigits  == null) {
                    message = reporter.message(locator, "*KEY*",
                        "Bad <#...> color expression, unrecognized characters not permitted after sign, got ''{0}''.", sb.toString());
                } else {
                    message = reporter.message(locator, "*KEY*",
                        "Bad <#...> color expression, unrecognized characters not permitted after hexadecimal digits, got ''{0}''.", sb.toString());
                }
                reporter.logInfo(message);
            }

        } while (false);

    }

}
