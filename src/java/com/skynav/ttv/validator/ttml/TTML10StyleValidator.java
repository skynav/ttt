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
 
package com.skynav.ttv.validator.ttml;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml10.tt.TimedText;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.validator.StyleValidator;
import com.skynav.ttv.validator.StyleValueValidator;
import com.skynav.ttv.validator.ttml.style.ColorValidator;
import com.skynav.ttv.validator.ttml.style.ExtentValidator;
import com.skynav.ttv.validator.ttml.style.FontFamilyValidator;
import com.skynav.ttv.validator.ttml.style.FontSizeValidator;
import com.skynav.ttv.validator.ttml.style.LineHeightValidator;
import com.skynav.ttv.validator.ttml.style.OpacityValidator;
import com.skynav.ttv.validator.ttml.style.OriginValidator;
import com.skynav.ttv.validator.ttml.style.PaddingValidator;
import com.skynav.ttv.validator.ttml.style.TextOutlineValidator;
import com.skynav.ttv.validator.ttml.style.ZIndexValidator;

public class TTML10StyleValidator implements StyleValidator {

    public static final String getStyleNamespaceUri() {
        return "http://www.w3.org/ns/ttml#styling";
    }

    private static Object[][] styleAccessorMap = new Object[][] {
        // property name         accessor method        specialized validator
        { "backgroundColor",    "getBackgroundColor",   String.class,   ColorValidator.class            },
        { "color",              "getColor",             String.class,   ColorValidator.class            },
        { "extent",             "getExtent",            String.class,   ExtentValidator.class           },
        { "fontFamily",         "getFontFamily",        String.class,   FontFamilyValidator.class       },
        { "fontSize",           "getFontSize",          String.class,   FontSizeValidator.class         },
        { "lineHeight",         "getLineHeight",        String.class,   LineHeightValidator.class       },
        { "opacity",            "getOpacity",           Float.class,    OpacityValidator.class          },
        { "origin",             "getOrigin",            String.class,   OriginValidator.class           },
        { "padding",            "getPadding",           String.class,   PaddingValidator.class          },
        { "textOutline",        "getTextOutline",       String.class,   TextOutlineValidator.class      },
        { "zIndex",             "getZIndex",            String.class,   ZIndexValidator.class           },
    };

    private Model model;
    private Map<String, StyleAccessor> accessors;

    public TTML10StyleValidator(Model model) {
        populate(model);
    }

    public boolean validate(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (String name : accessors.keySet()) {
            StyleAccessor sa = accessors.get(name);
            if (!sa.validate(model, content, locator, errorReporter))
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
            Class<?> validatorClass = (Class<?>) styleAccessorEntry[3];
            accessors.put(styleName, new StyleAccessor(styleName, accessorName, valueClass, validatorClass));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private static class StyleAccessor {

        private String styleName;
        private String accessorName;
        private Class<?> valueClass;
        private StyleValueValidator validator;

        public StyleAccessor(String styleName, String accessorName, Class<?> valueClass, Class<?> validatorClass) {
            populate(styleName, accessorName, valueClass, validatorClass);
        }

        public boolean validate(Model model, Object content, Locator locator, ErrorReporter errorReporter) {
            Object value = getStyleValue(content);
            if (value != null)
                return validator.validate(model, styleName, value, locator, errorReporter);
            else
                return true;
        }

        private StyleValueValidator createStyleValueValidator(Class<?> validatorClass) {
            try {
                return (StyleValueValidator) validatorClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(String styleName, String accessorName, Class<?> valueClass, Class<?> validatorClass) {
            this.styleName = styleName;
            this.accessorName = accessorName;
            this.valueClass = valueClass;
            this.validator = createStyleValueValidator(validatorClass);
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
