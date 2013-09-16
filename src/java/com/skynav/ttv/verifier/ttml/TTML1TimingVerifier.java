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
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.TimingValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.VerificationParameters;
import com.skynav.ttv.verifier.ttml.timing.TimeCoordinateVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimeDurationVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.util.Strings;

public class TTML1TimingVerifier implements TimingVerifier {

    private static final String timingNamespace = "";

    public static final String getTimingNamespaceUri() {
        return timingNamespace;
    }

    private static Object[][] timingAccessorMap = new Object[][] {
        {
            new QName(timingNamespace,"begin"),                 // attribute name
            "Begin",                                            // accessor method name suffix
            String.class,                                       // accessor method value type
            TimeCoordinateVerifier.class,                       // specialized verifier
            Boolean.FALSE,                                      // padding permitted
            null,                                               // default value
        },
        {
            new QName(timingNamespace,"dur"),
            "Dur",
            String.class,
            TimeDurationVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            new QName(timingNamespace,"end"),
            "End",
            String.class,
            TimeCoordinateVerifier.class,
            Boolean.FALSE,
            null,
        },
    };

    private Model model;
    private Map<QName, TimingAccessor> accessors;
    private VerificationParameters verificationParameters;

    public TTML1TimingVerifier(Model model) {
        populate(model);
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    private boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof TimedText) {
            verificationParameters = new TimingVerificationParameters((TimedText) content);
        } else {
            for (QName name : accessors.keySet()) {
                TimingAccessor sa = accessors.get(name);
                if (!sa.verify(model, content, locator, context))
                    failed = true;
            }
        }
        return !failed;
    }

    private void populate(Model model) {
        Map<QName, TimingAccessor> accessors = new java.util.HashMap<QName, TimingAccessor>();
        for (Object[] timingAccessorEntry : timingAccessorMap) {
            assert timingAccessorEntry.length >= 5;
            QName timingName = (QName) timingAccessorEntry[0];
            String accessorName = (String) timingAccessorEntry[1];
            Class<?> valueClass = (Class<?>) timingAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) timingAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) timingAccessorEntry[4]).booleanValue();
            accessors.put(timingName, new TimingAccessor(timingName, accessorName, valueClass, verifierClass, paddingPermitted));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private class TimingAccessor {

        private QName timingName;
        private String getterName;
        @SuppressWarnings("unused")
        private String setterName;
        private Class<?> valueClass;
        private TimingValueVerifier verifier;
        private boolean paddingPermitted;

        public TimingAccessor(QName timingName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            populate(timingName, accessorName, valueClass, verifierClass, paddingPermitted);
        }

        public boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getTimingValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, content, (String) value, locator, context);
                else
                    success = verifier.verify(model, content, timingName, value, locator, context, verificationParameters);
            }
            if (!success) {
                if (value != null) {
                    context.getReporter().logError(locator, "Invalid " + timingName + " value '" + value + "'.");
                }
            }
            return success;
        }

        private boolean verify(Model model, Object content, String value, Locator locator, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            if (value.length() == 0)
                reporter.logInfo(locator, "Empty " + timingName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                reporter.logInfo(locator, "The value of " + timingName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                reporter.logInfo(locator, "XML space padding not permitted on " + timingName + ", got '" + value + "'.");
            else
                success = verifier.verify(model, content, timingName, value, locator, context, verificationParameters);
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

        private void populate(QName timingName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted) {
            this.timingName = timingName;
            this.getterName = "get" + accessorName;
            this.setterName = "set" + accessorName;
            this.valueClass = valueClass;
            this.verifier = createTimingValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
        }

        private Object getTimingValue(Object content) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(getterName, new Class<?>[]{});
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
            return content.getOtherAttributes().get(timingName);
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (targetClass.isInstance(value))
                return value;
            else
                return null;
        }

    }

}
