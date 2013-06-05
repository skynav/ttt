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
 
package com.skynav.ttv.verifier.ttml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml10.tt.TimedText;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.ttml.style.ColorVerifier;
import com.skynav.ttv.verifier.ttml.style.ExtentVerifier;
import com.skynav.ttv.verifier.ttml.style.FontFamilyVerifier;
import com.skynav.ttv.verifier.ttml.style.FontSizeVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OpacityVerifier;
import com.skynav.ttv.verifier.ttml.style.OriginVerifier;
import com.skynav.ttv.verifier.ttml.style.PaddingVerifier;
import com.skynav.ttv.verifier.ttml.style.TextOutlineVerifier;
import com.skynav.ttv.verifier.ttml.style.VerifierUtilities;
import com.skynav.ttv.verifier.ttml.style.ZIndexVerifier;

public class TTML10StyleVerifier implements StyleVerifier {

    public static final String getStyleNamespaceUri() {
        return "http://www.w3.org/ns/ttml#styling";
    }

    private static Object[][] styleAccessorMap = new Object[][] {
        // property name         accessor method        specialized verifier
        { "backgroundColor",    "getBackgroundColor",   String.class,   ColorVerifier.class            },
        { "color",              "getColor",             String.class,   ColorVerifier.class            },
        { "extent",             "getExtent",            String.class,   ExtentVerifier.class           },
        { "fontFamily",         "getFontFamily",        String.class,   FontFamilyVerifier.class       },
        { "fontSize",           "getFontSize",          String.class,   FontSizeVerifier.class         },
        { "lineHeight",         "getLineHeight",        String.class,   LineHeightVerifier.class       },
        { "opacity",            "getOpacity",           Float.class,    OpacityVerifier.class          },
        { "origin",             "getOrigin",            String.class,   OriginVerifier.class           },
        { "padding",            "getPadding",           String.class,   PaddingVerifier.class          },
        { "textOutline",        "getTextOutline",       String.class,   TextOutlineVerifier.class      },
        { "zIndex",             "getZIndex",            String.class,   ZIndexVerifier.class           },
    };

    private Model model;
    private Map<String, StyleAccessor> accessors;

    public TTML10StyleVerifier(Model model) {
        populate(model);
    }

    public boolean verify(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (String name : accessors.keySet()) {
            StyleAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, errorReporter))
                failed = true;
        }
        return !failed;
    }

    private void populate(Model model) {
        Map<String, StyleAccessor> accessors = new java.util.HashMap<String, StyleAccessor>();
        for (Object[] styleAccessorEntry : styleAccessorMap) {
            assert styleAccessorEntry.length >= 4;
            String styleName = (String) styleAccessorEntry[0];
            String accessorName = (String) styleAccessorEntry[1];
            Class<?> valueClass = (Class<?>) styleAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) styleAccessorEntry[3];
            accessors.put(styleName, new StyleAccessor(styleName, accessorName, valueClass, verifierClass));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private static class StyleAccessor {

        private String styleName;
        private String accessorName;
        private Class<?> valueClass;
        private StyleValueVerifier verifier;

        public StyleAccessor(String styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass) {
            populate(styleName, accessorName, valueClass, verifierClass);
        }

        public boolean verify(Model model, Object content, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            Object value = getStyleValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, (String) value, locator, errorReporter);
                else if (!verifier.verify(model, styleName, value, locator, errorReporter))
                    errorReporter.logError(locator, "Invalid " + styleName + " value '" + value + "'.");
                else
                    success = true;
            } else
                success = true;
            return success;
        }

        private boolean verify(Model model, String value, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            if (value.length() == 0)
                errorReporter.logError(locator, "Empty " + styleName + " not permitted, got '" + value + "'.");
            else if (VerifierUtilities.isAllXMLSpace(value))
                errorReporter.logError(locator, "The value of " + styleName + " is entirely XML space characters, got '" + value + "'.");
            else if (!value.equals(value.trim()))
                errorReporter.logError(locator, "XML space padding not permitted on " + styleName + ", got '" + value + "'.");
            else if (!verifier.verify(model, styleName, value, locator, errorReporter))
                errorReporter.logError(locator, "Invalid " + styleName + " value '" + value + "'.");
            else
                success = true;
            return success;
        }

        private StyleValueVerifier createStyleValueVerifier(Class<?> verifierClass) {
            try {
                return (StyleValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(String styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass) {
            this.styleName = styleName;
            this.accessorName = accessorName;
            this.valueClass = valueClass;
            this.verifier = createStyleValueVerifier(verifierClass);
        }

        private Object getStyleValue(Object content) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(accessorName, new Class<?>[]{});
                return convertType(m.invoke(content, new Object[]{}), valueClass);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                if (content instanceof TimedText)
                    return convertType(getStyleValueAsString((TimedText) content), valueClass);
                else
                    throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private String getStyleValueAsString(TimedText content) {
            return content.getOtherAttributes().get(new QName(getStyleNamespaceUri(), styleName));
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (value.getClass() == targetClass)
                return value;
            else if (value.getClass() == String.class) {
                if (targetClass == Float.class) {
                    try {
                        return Float.valueOf((String) value);
                    } catch (NumberFormatException e) {
                        return null;
                    }
                } else
                    return null;
            } else if (value.getClass() == Float.class) {
                if (targetClass == String.class)
                    return ((Float) value).toString();
                else
                    return null;
            } else
                return null;
        }

    }

}
