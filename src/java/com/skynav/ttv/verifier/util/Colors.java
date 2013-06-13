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

import java.util.Map;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.impl.ColorImpl;
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

    public static boolean isColor(String value, Locator locator, VerifierContext context, Color[] outputColor) {
        if (isRGBHash(value, outputColor))
            return true;
        else if (isRGBFunction(value, outputColor))
            return true;
        else if (isNamedColor(value, outputColor))
            return true;
        else
            return false;
    }

    public static void badColor(String value, Locator locator, VerifierContext context) {
        if (value.charAt(0) == '#')
            badRGBHash(value, locator, context);
        else if (value.indexOf("rgb") == 0)
            badRGBFunction(value, locator, context);
        else if (Strings.isLetters(value))
            badNamedColor(value, locator, context);
        else {
            context.getReporter().logInfo(locator,
                "Bad <color> expression, got '" + value + "', but expected <#rrggbb>, #<rrggbbaa>, <rgb(...)>, <rgba(...)>, or <named color>.");
        }
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

    private static void badNamedColor(String value, Locator locator, VerifierContext context) {
        assert Strings.isLetters(value);
        context.getReporter().logInfo(locator, "Unknown named color, got '" + value + "'.");
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

    private static void badRGBComponent(String value, Locator locator, VerifierContext context, int minValue, int maxValue) {
        Reporter reporter = context.getReporter();
        try {
            int componentValue = Integer.parseInt(value);
            if ((componentValue < minValue) || (componentValue > maxValue) ) {
                reporter.logInfo(locator,
                    "Component out of range [" + minValue + "," + maxValue +
                    "] in <rgb(...)> or <rgba(...)> color expression, got " + componentValue + ".");
            }
        } catch (NumberFormatException e) {
            if (Strings.containsXMLSpace(value)) {
                String trimmedComponent = value.trim();
                try {
                    Integer.parseInt(trimmedComponent);
                    reporter.logInfo(locator,
                        "XML space padding not permitted in <rgb(...)> or <rgba(...)> color expression component, got '" + value + "'.");
                } catch (NumberFormatException ee) {
                    reporter.logInfo(locator,
                        "Component in <rgb(...)> or <rgba(...)> color expression is not a non-negative integer, got '" + value + "'.");
                }
            } else {
                reporter.logInfo(locator,
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

    private static void badRGBComponents(String components, Locator locator, VerifierContext context, int[][] valueLimits) {
        String[] colorComponents = components.split(",");
        int numComponents = valueLimits.length;
        if (colorComponents.length != numComponents) {
            context.getReporter().logInfo(locator,
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
                badRGBComponent(component, locator, context, minValue, maxValue);
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

    private static void badRGBFunction(String value, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        assert value.indexOf("rgb") == 0;
        int opIndex = value.indexOf("(");
        if (opIndex < 0) {
            reporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', missing opening parenthesis of argument list.");
        } else {
            String functionName = value.substring(0, opIndex);
            if (!functionName.equals("rgb") && !functionName.equals("rgba")) {
                reporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', incorrect function name, expect 'rgb' or 'rgba'.");
            }
        }
        int cpIndex = value.indexOf(")");
        if (cpIndex < 0)
            reporter.logInfo(locator, "Bad RGB function syntax in <color> expression, got '" + value + "', missing closing parenthesis of argument list.");
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
                    badRGBComponents(value.substring(componentsStart, componentsEnd), locator, context, valueLimits);
            }
        }
    }

    private static boolean isRGBHash(String value, Color[] outputColor) {
        int numDigits = value.length() - 1;
        if ((numDigits != 6) || (numDigits != 8))
            return false;
        else if (!Strings.isHexDigits(value.substring(1)))
            return false;
        else {
            if (outputColor != null)
                outputColor[0] = ColorImpl.fromRGBHash(value);
            return true;
        }
    }

    private static void badRGBHash(String value, Locator locator, VerifierContext context) {

        Reporter reporter = context.getReporter();
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
                reporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted before '#'.");
            }

            // hash sign
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c != '#') {
                reporter.logInfo(locator,
                    "Bad <#...> color expression, expected '#', got '" + c + "'.");
                break;
            } else
                valueIndex++;

            // whitespace after hash sign
            if (valueIndex == valueLength) {
                reporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
                break;
            }
            c = value.charAt(valueIndex);
            if (Characters.isXMLSpace(c)) {
                while (Characters.isXMLSpace(c)) {
                    if (++valueIndex >= valueLength)
                        break;
                    c = value.charAt(valueIndex);
                }
                reporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted after '#'.");
            }

            // hex digits
            if (valueIndex == valueLength) {
                reporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
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
                    reporter.logInfo(locator,
                        "Bad <#...> color expression, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits, got " + numDigits + " digits.");
                }
            } else {
                reporter.logInfo(locator,
                    "Bad <#...> color expression, no hexadecimal digits, expect 6 ('rrggbb') or 8 ('rrggbbaa') hexadecimal digits after '#'.");
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
                reporter.logInfo(locator,
                    "Bad <#...> color expression, XML space padding not permitted after hexadecimal digits.");
            }

            // garbage after (hexDigit+ S*)
            if (valueIndex < valueLength) {
                StringBuffer sb = new StringBuffer();
                while (valueIndex < valueLength) {
                    sb.append(value.charAt(valueIndex++));
                }
                reporter.logInfo(locator,
                    "Bad <#...> color expression, unrecognized characters not permitted after " +
                    ((hexDigits ==  null) ? "sign" :  "hexadecimal digits") + ", got '" + sb + "'.");
            }

        } while (false);

    }

}
