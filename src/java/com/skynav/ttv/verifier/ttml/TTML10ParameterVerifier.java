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
import java.math.BigInteger;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml10.tt.TimedText;
import com.skynav.ttv.model.ttml10.ttd.ClockMode;
import com.skynav.ttv.model.ttml10.ttd.DropMode;
import com.skynav.ttv.model.ttml10.ttd.MarkerMode;
import com.skynav.ttv.model.ttml10.ttd.TimeBase;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.ttml.parameter.CellResolutionVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ClockModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.DropModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.FrameRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.FrameRateMultiplierVerifier;
import com.skynav.ttv.verifier.ttml.parameter.MarkerModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.PixelAspectRatioVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ProfileVerifier;
import com.skynav.ttv.verifier.ttml.parameter.SubFrameRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TickRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TimeBaseVerifier;
import com.skynav.ttv.verifier.util.Strings;

public class TTML10ParameterVerifier implements ParameterVerifier {

    public static final String getParameterNamespaceUri() {
        return "http://www.w3.org/ns/ttml#parameter";
    }

    private static Object[][] parameterAccessorMap = new Object[][] {
        // property name            accessor method             value type          specialized verifier                padding permitted
        { "cellResolution",         "getCellResolution",        String.class,       CellResolutionVerifier.class,       Boolean.FALSE },
        { "clockMode",              "getClockMode",             ClockMode.class,    ClockModeVerifier.class,            Boolean.FALSE },
        { "dropMode",               "getDropMode",              DropMode.class,     DropModeVerifier.class,             Boolean.FALSE },
        { "frameRate",              "getFrameRate",             BigInteger.class,   FrameRateVerifier.class,            Boolean.TRUE  },
        { "frameRateMultiplier",    "getFrameRateMultiplier",   String.class,       FrameRateMultiplierVerifier.class,  Boolean.FALSE },
        { "markerMode",             "getMarkerMode",            MarkerMode.class,   MarkerModeVerifier.class,           Boolean.FALSE },
        { "pixelAspectRatio",       "getPixelAspectRatio",      String.class,       PixelAspectRatioVerifier.class,     Boolean.FALSE },
        { "profile",                "getProfile",               String.class,       ProfileVerifier.class,              Boolean.FALSE },
        { "subFrameRate",           "getSubFrameRate",          BigInteger.class,   SubFrameRateVerifier.class,         Boolean.FALSE },
        { "tickRate",               "getTickRate",              BigInteger.class,   TickRateVerifier.class,             Boolean.FALSE },
        { "timeBase",               "getTimeBase",              TimeBase.class,     TimeBaseVerifier.class,             Boolean.FALSE },
    };

    private Model model;
    private Map<String, ParameterAccessor> accessors;

    public TTML10ParameterVerifier(Model model) {
        populate(model);
    }

    public boolean verify(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (String name : accessors.keySet()) {
            ParameterAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, errorReporter))
                failed = true;
        }
        return !failed;
    }

    private void populate(Model model) {
        Map<String, ParameterAccessor> accessors = new java.util.HashMap<String, ParameterAccessor>();
        for (Object[] parameterAccessorEntry : parameterAccessorMap) {
            assert parameterAccessorEntry.length >= 5;
            String parameterName = (String) parameterAccessorEntry[0];
            String accessorName = (String) parameterAccessorEntry[1];
            Class<?> valueClass = (Class<?>) parameterAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) parameterAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) parameterAccessorEntry[4]).booleanValue();
            accessors.put(parameterName, new ParameterAccessor(parameterName, accessorName, valueClass, verifierClass, paddingPermitted));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private static class ParameterAccessor {

        private String parameterName;
        private String accessorName;
        private Class<?> valueClass;
        private ParameterValueVerifier verifier;
        private boolean paddingPermitted;

        public ParameterAccessor(String parameterName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            populate(parameterName, accessorName, valueClass, verifierClass, paddingPermitted);
        }

        public boolean verify(Model model, Object content, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            Object value = getParameterValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, (String) value, locator, errorReporter);
                else if (!verifier.verify(model, parameterName, value, locator, errorReporter))
                    errorReporter.logError(locator, "Invalid " + parameterName + " value '" + value + "'.");
                else
                    success = true;
            } else
                success = true;
            return success;
        }

        private boolean verify(Model model, String value, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            if (value.length() == 0)
                errorReporter.logError(locator, "Empty " + parameterName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                errorReporter.logError(locator, "The value of " + parameterName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                errorReporter.logError(locator, "XML space padding not permitted on " + parameterName + ", got '" + value + "'.");
            else if (!verifier.verify(model, parameterName, value, locator, errorReporter))
                errorReporter.logError(locator, "Invalid " + parameterName + " value '" + value + "'.");
            else
                success = true;
            return success;
        }

        private ParameterValueVerifier createParameterValueVerifier(Class<?> verifierClass) {
            try {
                return (ParameterValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(String parameterName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            this.parameterName = parameterName;
            this.accessorName = accessorName;
            this.valueClass = valueClass;
            this.verifier = createParameterValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
        }

        private Object getParameterValue(Object content) {
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
                    return convertType(getParameterValueAsString((TimedText) content), valueClass);
                else
                    throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private String getParameterValueAsString(TimedText content) {
            return content.getOtherAttributes().get(new QName(getParameterNamespaceUri(), parameterName));
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
            } else if (value.getClass() == BigInteger.class) {
                if (targetClass == String.class)
                    return ((BigInteger) value).toString();
                else
                    return null;
            } else
                return null;
        }

    }

}
