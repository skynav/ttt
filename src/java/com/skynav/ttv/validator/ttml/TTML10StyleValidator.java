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

    private static Object[][] styleMap = new Object[][] {
        // property name         accessor method        specialized validator
        { "backgroundColor",    "getBackgroundColor",   ColorValidator.class           },
        { "color",              "getColor",             ColorValidator.class           },
        { "extent",             "getExtent",            ExtentValidator.class          },
        { "fontFamily",         "getFontFamily",        FontFamilyValidator.class      },
        { "fontSize",           "getFontSize",          FontSizeValidator.class        },
        { "lineHeight",         "getLineHeight",        LineHeightValidator.class      },
        { "opacity",            "getOpacity",           OpacityValidator.class         },
        { "origin",             "getOrigin",            OriginValidator.class          },
        { "padding",            "getPadding",           PaddingValidator.class         },
        { "textOutline",        "getTextOutline",       TextOutlineValidator.class     },
        { "zIndex",             "getZIndex",            ZIndexValidator.class          },
    };

    private Map<String, String> accessors;
    private Map<String, StyleValueValidator> validators;

    public TTML10StyleValidator(Model model) {
        populate(model);
    }

    public boolean validate(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (String name : accessors.keySet()) {
            String value = getStyleValue(content, name, accessors.get(name));
            if (value != null) {
                StyleValueValidator svv = validators.get(name);
                if (!svv.validate(name, value, locator, errorReporter))
                    failed = true;
            }
        }
        return !failed;
    }

    @SuppressWarnings("rawtypes")
    private StyleValueValidator createStyleValueValidator(Class validatorClass, Model model) {
        try {
            StyleValueValidator svv = (StyleValueValidator) validatorClass.newInstance();
            svv.setModel(model);
            return svv;
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void populate(Model model) {
        Map<String, String> accessors = new java.util.HashMap<String, String>();
        Map<String, StyleValueValidator> validators = new java.util.HashMap<String, StyleValueValidator>();
        for (Object[] styleMapEntry : styleMap) {
            assert styleMapEntry.length >= 3;
            String styleName = (String) styleMapEntry[0];
            String accessor = (String) styleMapEntry[1];
            @SuppressWarnings("rawtypes")
            Class validatorClass = (Class) styleMapEntry[2];
            accessors.put(styleName, accessor);
            validators.put(styleName, createStyleValueValidator(validatorClass, model));
        }
        this.accessors = accessors;
        this.validators = validators;
    }

    private String getStyleValue(Object content, String name, String accessor) {
        try {
            @SuppressWarnings("rawtypes")
            Class contentClass = content.getClass();
            @SuppressWarnings("unchecked")
            Method m = contentClass.getMethod(accessor, new Class[]{});
            return (String) m.invoke(content, new Object[]{});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            if (content instanceof TimedText)
                return getStyleValue((TimedText) content, name);
            else
                throw new RuntimeException(e);
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private String getStyleValue(TimedText content, String name) {
        QName qn = new QName(getStyleNamespaceUri(), name);
        return content.getOtherAttributes().get(qn);
    }

    private static final String getStyleNamespaceUri() {
        return "http://www.w3.org/ns/ttml#styling";
    }

}
