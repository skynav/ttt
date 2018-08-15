/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.ttml1.ttd.TimeContainer;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.AbstractVerifier;
import com.skynav.ttv.verifier.TimingValueVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.VerificationParameters;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.timing.TimeContainerVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimeCoordinateVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimeDurationVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters1;
import com.skynav.ttv.verifier.util.Strings;

public class TTML1TimingVerifier extends AbstractVerifier implements TimingVerifier {

    public static final QName beginAttributeName                        = new QName("", "begin");
    public static final QName durAttributeName                          = new QName("", "dur");
    public static final QName endAttributeName                          = new QName("", "end");
    public static final QName timeContainerAttributeName                = new QName("", "timeContainer");

    private static final Object[][] timingAccessorMap                   = new Object[][] {
        {
            beginAttributeName,                                         // attribute name
            "Begin",                                                    // accessor method name suffix
            String.class,                                               // accessor method value type
            TimeCoordinateVerifier.class,                               // specialized verifier
            Boolean.FALSE,                                              // padding permitted
            null,                                                       // default value
        },
        {
            durAttributeName,
            "Dur",
            String.class,
            TimeDurationVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            endAttributeName,
            "End",
            String.class,
            TimeCoordinateVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            timeContainerAttributeName,
            "TimeContainer",
            TimeContainer.class,
            TimeContainerVerifier.class,
            Boolean.FALSE,
            TimeContainer.PAR,
        },
    };

    private Map<QName, TimingAccessor> accessors;
    private VerificationParameters verificationParameters;

    public TTML1TimingVerifier(Model model) {
        super(model);
        populate();
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        setState(content, context);
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    protected boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (isTimedText(content))
            verificationParameters = makeTimingVerificationParameters(content, context);
        for (TimingAccessor ta : accessors.values()) {
            if (!verifyAttributeItem(content, locator, ta, context))
                failed = true;
        }
        return !failed;
    }

    protected boolean verifyAttributeItem(Object content, Locator locator, TimingAccessor ta, VerifierContext context) {
        return ta.verify(getModel(), content, locator, context);
    }

    protected boolean isTimedText(Object content) {
        return content instanceof TimedText;
    }

    protected TimingVerificationParameters makeTimingVerificationParameters(Object content, VerifierContext context) {
        return new TimingVerificationParameters1(content, context != null ? context.getExternalParameters() : null);
    }

    private void populate() {
        this.accessors = makeAccessors();
    }

    private Map<QName, TimingAccessor> makeAccessors() {
        Map<QName, TimingAccessor> accessors = new java.util.HashMap<QName, TimingAccessor>();
        populateAccessors(accessors);
        return accessors;
    }

    protected void populateAccessors(Map<QName, TimingAccessor> accessors) {
        populateAccessors(accessors, timingAccessorMap);
    }

    protected void populateAccessors(Map<QName, TimingAccessor> accessors, Object[][] accessorMap) {
        for (Object[] timingAccessorEntry : accessorMap) {
            assert timingAccessorEntry.length >= 5;
            QName timingName = (QName) timingAccessorEntry[0];
            String accessorName = (String) timingAccessorEntry[1];
            Class<?> valueClass = (Class<?>) timingAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) timingAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) timingAccessorEntry[4]).booleanValue();
            accessors.put(timingName, new TimingAccessor(timingName, accessorName, valueClass, verifierClass, paddingPermitted));
        }
    }

    protected String getTimingValueAsString(Object content, QName timingName) {
        assert content instanceof TimedText;
        return ((TimedText)content).getOtherAttributes().get(timingName);
    }

    protected class TimingAccessor {

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

        public QName getTimingName() {
            return timingName;
        }

        public boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getTimingValue(content);
            if (value != null) {
                Location location = new Location(content, context.getBindingElementName(content), timingName, locator);
                if (value instanceof String)
                    success = verify((String) value, location, context);
                else
                    success = verifier.verify(value, location, context, verificationParameters);
            }
            if (!success) {
                if (value != null) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Invalid {0} value ''{1}''.", timingName, value));
                }
            }
            return success;
        }

        private boolean verify(String value, Location location, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            Locator locator = location.getLocator();
            if (value.length() == 0) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Empty {0} not permitted on {1}, got ''{2}''.", timingName, location.getElementName(), value));
            } else if (Strings.isAllXMLSpace(value)) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "The value of {0} on {1} is entirely XML space characters, got ''{2}''.", timingName, location.getElementName(), value));
            } else if (!paddingPermitted && !value.equals(value.trim())) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "XML space padding of {0} not permitted on {1}, got ''{2}''.", timingName, location.getElementName(), value));
            } else
                success = verifier.verify(value, location, context, verificationParameters);
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

        protected Object getTimingValue(Object content) {
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
                if (isTimedText(content))
                    return convertType(getTimingValueAsString(content, timingName), valueClass);
                else
                    return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
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
