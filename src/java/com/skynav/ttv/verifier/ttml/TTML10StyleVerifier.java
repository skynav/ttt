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
import java.util.List;
import java.util.Map;

import javax.xml.bind.Binder;
import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml10.tt.Body;
import com.skynav.ttv.model.ttml10.tt.Break;
import com.skynav.ttv.model.ttml10.tt.Division;
import com.skynav.ttv.model.ttml10.tt.Paragraph;
import com.skynav.ttv.model.ttml10.tt.Region;
import com.skynav.ttv.model.ttml10.tt.Span;
import com.skynav.ttv.model.ttml10.tt.Style;
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
import com.skynav.ttv.verifier.ttml.style.StyleAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.RegionAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.TextOutlineVerifier;
import com.skynav.ttv.verifier.ttml.style.ZIndexVerifier;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Strings;

public class TTML10StyleVerifier implements StyleVerifier {

    private static final String styleNamespace = "http://www.w3.org/ns/ttml#styling";

    public static final String getStyleNamespaceUri() {
        return styleNamespace;
    }

    private static Object[][] styleAccessorMap = new Object[][] {
        // attribute name                               accessor method         value type              specialized verifier            padding permitted
        { new QName(styleNamespace,"backgroundColor"),  "getBackgroundColor",   String.class,           ColorVerifier.class,            Boolean.FALSE },
        { new QName(styleNamespace,"color"),            "getColor",             String.class,           ColorVerifier.class,            Boolean.FALSE },
        { new QName(styleNamespace,"extent"),           "getExtent",            String.class,           ExtentVerifier.class,           Boolean.FALSE },
        { new QName(styleNamespace,"fontFamily"),       "getFontFamily",        String.class,           FontFamilyVerifier.class,       Boolean.TRUE  },
        { new QName(styleNamespace,"fontSize"),         "getFontSize",          String.class,           FontSizeVerifier.class,         Boolean.FALSE },
        { new QName(styleNamespace,"lineHeight"),       "getLineHeight",        String.class,           LineHeightVerifier.class,       Boolean.FALSE },
        { new QName(styleNamespace,"opacity"),          "getOpacity",           Float.class,            OpacityVerifier.class,          Boolean.FALSE },
        { new QName(styleNamespace,"origin"),           "getOrigin",            String.class,           OriginVerifier.class,           Boolean.FALSE },
        { new QName(styleNamespace,"padding"),          "getPadding",           String.class,           PaddingVerifier.class,          Boolean.FALSE },
        // region is not a style property, but it is convenient to handle it here
        { new QName("","region"),                       "getRegion",            Object.class,           RegionAttributeVerifier.class,  Boolean.FALSE },
        // style is not a style property (as such), but it is convenient to handle it here
        { new QName("","style"),                        "getStyleAttribute",    List.class,             StyleAttributeVerifier.class,   Boolean.FALSE },
        { new QName(styleNamespace,"textOutline"),      "getTextOutline",       String.class,           TextOutlineVerifier.class,      Boolean.FALSE },
        { new QName(styleNamespace,"zIndex"),           "getZIndex",            String.class,           ZIndexVerifier.class,           Boolean.FALSE },
    };

    private Model model;
    @SuppressWarnings("unused")
    private Binder<?> binder;
    private Map<QName, StyleAccessor> accessors;

    public TTML10StyleVerifier(Model model, Binder<?> binder) {
        populate(model, binder);
    }

    public boolean verify(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (QName name : accessors.keySet()) {
            StyleAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, errorReporter))
                failed = true;
        }
        return !failed;
    }

    private void populate(Model model, Binder<?> binder) {
        Map<QName, StyleAccessor> accessors = new java.util.HashMap<QName, StyleAccessor>();
        for (Object[] styleAccessorEntry : styleAccessorMap) {
            assert styleAccessorEntry.length >= 5;
            QName styleName = (QName) styleAccessorEntry[0];
            String accessorName = (String) styleAccessorEntry[1];
            Class<?> valueClass = (Class<?>) styleAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) styleAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) styleAccessorEntry[4]).booleanValue();
            accessors.put(styleName, new StyleAccessor(styleName, accessorName, valueClass, verifierClass, paddingPermitted));
        }
        this.model = model;
        this.binder = binder;
        this.accessors = accessors;
    }

    private static class StyleAccessor {

        private QName styleName;
        private String accessorName;
        private Class<?> valueClass;
        private StyleValueVerifier verifier;
        private boolean paddingPermitted;

        public StyleAccessor(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            populate(styleName, accessorName, valueClass, verifierClass, paddingPermitted);
        }

        private static final QName styleAttributeName = new QName("", "style");
        private static final QName regionAttributeName = new QName("", "region");

        public boolean verify(Model model, Object content, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            Object value = getStyleValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, (String) value, locator, errorReporter);
                else if (!verifier.verify(model, styleName, value, locator, errorReporter)) {
                    if (styleName.equals(styleAttributeName)) {
                        value = IdReferences.getIdReferences(value);
                    } else if (styleName.equals(regionAttributeName)) {
                        value = IdReferences.getIdReference(value);
                    } else
                        value = value.toString();
                    errorReporter.logError(locator, "Invalid " + styleName + " value '" + value + "'.");
                } else
                    success = true;
            } else
                success = true;
            return success;
        }

        private boolean verify(Model model, String value, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            if (value.length() == 0)
                errorReporter.logError(locator, "Empty " + styleName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                errorReporter.logError(locator, "The value of " + styleName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
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

        private void populate(QName styleName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            this.styleName = styleName;
            this.accessorName = accessorName;
            this.valueClass = valueClass;
            this.verifier = createStyleValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
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
                else if (styleName.equals(regionAttributeName) && !takesRegionAttribute(content))
                    return null;
                else if (styleName.equals(styleAttributeName) && !takesStyleAttribute(content))
                    return null;
                else
                    throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private boolean takesRegionAttribute(Object content) {
            if (content instanceof Region)
                return false;
            else if (content instanceof Style)
                return false;
            else if (content instanceof Break) {
                // N.B. This may change in TTML.next; see https://www.w3.org/AudioVideo/TT/tracker/issues/254
                return false;
            } else
                return isContent(content);
        }

        private boolean takesStyleAttribute(Object content) {
            if (content instanceof Region)
                return true;
            else if (content instanceof Style)
                return true;
            else
                return isContent(content);
        }

        private boolean isContent(Object content) {
            if (content instanceof TimedText)
                return true;
            else if (content instanceof Body)
                return true;
            else if (content instanceof Division)
                return true;
            else if (content instanceof Paragraph)
                return true;
            else if (content instanceof Span)
                return true;
            else if (content instanceof Break)
                return true;
            else
                return false;
        }

        private String getStyleValueAsString(TimedText content) {
            return content.getOtherAttributes().get(styleName);
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (targetClass.isInstance(value))
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
