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
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.TimingValueVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimeCoordinateVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimeDurationVerifier;
import com.skynav.ttv.verifier.util.Strings;

public class TTML10TimingVerifier implements TimingVerifier {

    public static final String getTimingNamespaceUri() {
        return null;
    }

    private static Object[][] timingAccessorMap = new Object[][] {
        // property name            accessor method             value type          specialized verifier                padding permitted
        { "begin",                  "getBegin",                 String.class,       TimeCoordinateVerifier.class,       Boolean.FALSE },
        { "dur",                    "getDur",                   String.class,       TimeDurationVerifier.class,         Boolean.FALSE },
        { "end",                    "getEnd",                   String.class,       TimeCoordinateVerifier.class,       Boolean.FALSE },
    };

    private Model model;
    private Map<String, TimingAccessor> accessors;

    public TTML10TimingVerifier(Model model) {
        populate(model);
    }

    public boolean verify(Object content, Locator locator, ErrorReporter errorReporter) {
        boolean failed = false;
        for (String name : accessors.keySet()) {
            TimingAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, errorReporter))
                failed = true;
        }
        return !failed;
    }

    private void populate(Model model) {
        Map<String, TimingAccessor> accessors = new java.util.HashMap<String, TimingAccessor>();
        for (Object[] timingAccessorEntry : timingAccessorMap) {
            assert timingAccessorEntry.length >= 5;
            String timingName = (String) timingAccessorEntry[0];
            String accessorName = (String) timingAccessorEntry[1];
            Class<?> valueClass = (Class<?>) timingAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) timingAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) timingAccessorEntry[4]).booleanValue();
            accessors.put(timingName, new TimingAccessor(timingName, accessorName, valueClass, verifierClass, paddingPermitted));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private static class TimingAccessor {

        private String timingName;
        private String accessorName;
        private Class<?> valueClass;
        private TimingValueVerifier verifier;
        private boolean paddingPermitted;

        public TimingAccessor(String timingName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            populate(timingName, accessorName, valueClass, verifierClass, paddingPermitted);
        }

        public boolean verify(Model model, Object content, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            Object value = getTimingValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, (String) value, locator, errorReporter);
                else if (!verifier.verify(model, timingName, value, locator, errorReporter))
                    errorReporter.logError(locator, "Invalid " + timingName + " value '" + value + "'.");
                else
                    success = true;
            } else
                success = true;
            return success;
        }

        private boolean verify(Model model, String value, Locator locator, ErrorReporter errorReporter) {
            boolean success = false;
            if (value.length() == 0)
                errorReporter.logError(locator, "Empty " + timingName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                errorReporter.logError(locator, "The value of " + timingName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                errorReporter.logError(locator, "XML space padding not permitted on " + timingName + ", got '" + value + "'.");
            else if (!verifier.verify(model, timingName, value, locator, errorReporter))
                errorReporter.logError(locator, "Invalid " + timingName + " value '" + value + "'.");
            else
                success = true;
            return success;
        }

        private TimingValueVerifier createTimingValueVerifier(Class<?> verifierClass) {
            try {
                return (TimingValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(String timingName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            this.timingName = timingName;
            this.accessorName = accessorName;
            this.valueClass = valueClass;
            this.verifier = createTimingValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
        }

        private Object getTimingValue(Object content) {
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
                    return convertType(getTimingValueAsString((TimedText) content), valueClass);
                else
                    throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private String getTimingValueAsString(TimedText content) {
            return content.getOtherAttributes().get(new QName(getTimingNamespaceUri(), timingName));
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (value.getClass() == targetClass)
                return value;
            else
                return null;
        }

    }

}
