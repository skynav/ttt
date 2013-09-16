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

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttd.ClockMode;
import com.skynav.ttv.model.ttml1.ttd.DropMode;
import com.skynav.ttv.model.ttml1.ttd.MarkerMode;
import com.skynav.ttv.model.ttml1.ttd.TimeBase;
import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.util.Enums;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.parameter.BaseVerifier;
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
import com.skynav.xml.helpers.XML;

import static com.skynav.ttv.model.ttml.TTML1.Constants.*;

public class TTML1ParameterVerifier implements ParameterVerifier {

    public static final String NAMESPACE = NAMESPACE_TT_PARAMETER;

    public static final String getParameterNamespaceUri() {
        return NAMESPACE;
    }

    private static Object[][] parameterAccessorMap = new Object[][] {
        {
            new QName(NAMESPACE,"cellResolution"),     // attribute name
            "CellResolution",                                   // accessor method name suffix
            String.class,                                       // value type
            CellResolutionVerifier.class,                       // specialized verifier
            Boolean.FALSE,                                      // padding permitted
            "32 15",                                            // default value
        },
        {
            new QName(NAMESPACE,"clockMode"),
            "ClockMode",
            ClockMode.class,
            ClockModeVerifier.class,
            Boolean.FALSE,
            ClockMode.UTC,
        },
        {
            new QName(NAMESPACE,"dropMode"),
            "DropMode",
            DropMode.class,
            DropModeVerifier.class,
            Boolean.FALSE,
            DropMode.NON_DROP,
        },
        {
            new QName(NAMESPACE,"frameRate"),
            "FrameRate",
            BigInteger.class,
            FrameRateVerifier.class,
            Boolean.TRUE,
            BigInteger.valueOf(30),
        },
        {
            new QName(NAMESPACE,"frameRateMultiplier"),
            "FrameRateMultiplier",
            String.class,
            FrameRateMultiplierVerifier.class,
            Boolean.FALSE,
            "1 1",
        },
        {
            new QName(NAMESPACE,"markerMode"),
            "MarkerMode",
            MarkerMode.class,
            MarkerModeVerifier.class,
            Boolean.FALSE,
            MarkerMode.DISCONTINUOUS,
        },
        {
            new QName(NAMESPACE,"pixelAspectRatio"),
            "PixelAspectRatio",
            String.class,
            PixelAspectRatioVerifier.class,
            Boolean.FALSE,
            "1 1",
        },
        {
            new QName(NAMESPACE,"profile"),
            "Profile",
            String.class,
            ProfileVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            new QName(NAMESPACE,"subFrameRate"),
            "SubFrameRate",
            BigInteger.class,
            SubFrameRateVerifier.class,
            Boolean.FALSE,
            BigInteger.valueOf(1),
        },
        {
            new QName(NAMESPACE,"tickRate"),
            "TickRate",
            BigInteger.class,
            TickRateVerifier.class,
            Boolean.FALSE,
            BigInteger.valueOf(1),
        },
        {
            new QName(NAMESPACE,"timeBase"),
            "TimeBase",
            TimeBase.class,
            TimeBaseVerifier.class,
            Boolean.FALSE,
            TimeBase.MEDIA,
        },
        // 'use' attribute applies only to ttp:profile element
        {
            new QName("","use"),
            "Use",
            String.class,
            ProfileVerifier.class,
            Boolean.FALSE,
            null,
        },
        // 'xml:base' attribute applies only to ttp:features and ttp:extensions elements
        {
            XML.getBaseAttributeName(),
            "Base",
            String.class,
            BaseVerifier.class,
            Boolean.FALSE,
            null,
        },
    };

    private Model model;
    private Map<QName, ParameterAccessor> accessors;

    public TTML1ParameterVerifier(Model model) {
        populate(model);
    }

    public QName getParameterAttributeName(String parameterName) {
        // assumes that parameter name is same as local part of qualified attribute name, which
        // is presently true in TTML1
        for (QName name : accessors.keySet()) {
            if (parameterName.equals(name.getLocalPart()))
                return name;
        }
        return null;
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else if (type == ItemType.Other)
            return verifyOtherAttributes(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    private boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        for (QName name : accessors.keySet()) {
            ParameterAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, context))
                failed = true;
        }
        return !failed;
    }

    private boolean verifyOtherAttributes(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        NamedNodeMap attributes = context.getXMLNode(content).getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            Node item = attributes.item(i);
            if (!(item instanceof Attr))
                continue;
            Attr attribute = (Attr) item;
            String nsUri = attribute.getNamespaceURI();
            String localName = attribute.getLocalName();
            if (localName == null)
                localName = attribute.getName();
            if (localName.indexOf("xmlns") == 0)
                continue;
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            if (name.getNamespaceURI().equals(NAMESPACE)) {
                if (!isParameterAttribute(name)) {
                    context.getReporter().logError(locator, "Unknown attribute in TT Parameter namespace '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                } else if (!permitsParameterAttribute(content)) {
                    context.getReporter().logError(locator, "TT Parameter attribute '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean permitsParameterAttribute(Object content) {
        if (content instanceof TimedText)
            return true;
        else
            return false;
    }

    private boolean isParameterAttribute(QName name) {
        return name.getNamespaceURI().equals(NAMESPACE) && accessors.containsKey(name);
    }

    private void populate(Model model) {
        Map<QName, ParameterAccessor> accessors = new java.util.HashMap<QName, ParameterAccessor>();
        for (Object[] parameterAccessorEntry : parameterAccessorMap) {
            assert parameterAccessorEntry.length >= 6;
            QName parameterName = (QName) parameterAccessorEntry[0];
            String accessorName = (String) parameterAccessorEntry[1];
            Class<?> valueClass = (Class<?>) parameterAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) parameterAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) parameterAccessorEntry[4]).booleanValue();
            Object defaultValue = parameterAccessorEntry[5];
            accessors.put(parameterName, new ParameterAccessor(parameterName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private class ParameterAccessor {

        private QName parameterName;
        private String getterName;
        private String setterName;
        private Class<?> valueClass;
        private ParameterValueVerifier verifier;
        private boolean paddingPermitted;
        private Object defaultValue;

        public ParameterAccessor(QName parameterName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            populate(parameterName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue);
        }

        public boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getParameterValue(content);
            if (value != null) {
                if (value instanceof String)
                    success = verify(model, content, (String) value, locator, context);
                else
                    success = verifier.verify(model, content, parameterName, value, locator, context);
            } else
                setParameterDefaultValue(content);
            if (!success)
                context.getReporter().logError(locator, "Invalid " + parameterName + " value '" + value + "'.");
            return success;
        }

        private boolean verify(Model model, Object content, String value, Locator locator, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            if (value.length() == 0)
                reporter.logInfo(locator, "Empty " + parameterName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                reporter.logInfo(locator, "The value of " + parameterName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                reporter.logInfo(locator, "XML space padding not permitted on " + parameterName + ", got '" + value + "'.");
            else
                success = verifier.verify(model, content, parameterName, value, locator, context);
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

        private void populate(QName parameterName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            this.parameterName = parameterName;
            this.getterName = "get" + accessorName;
            this.setterName = "set" + accessorName;
            this.valueClass = valueClass;
            this.verifier = createParameterValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
            this.defaultValue = defaultValue;
        }

        private Object getParameterValue(Object content) {
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
                return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setParameterDefaultValue(Object content) {
            Object defaultValue = this.defaultValue;
            if (content instanceof TimedText) {
                if (defaultValue != null)
                    setParameterValue(content, defaultValue);
            } else if ((content instanceof Features) || (content instanceof Extensions)) {
                if (parameterName.equals(XML.getBaseAttributeName())) {
                    if (content instanceof Features)
                        defaultValue = model.getFeatureNamespaceUri().toString();
                    else if (content instanceof Extensions)
                        defaultValue = model.getExtensionNamespaceUri().toString();
                    if (defaultValue != null)
                        setParameterValue(content, defaultValue);
                }
            }
        }

        private void setParameterValue(Object content, Object value) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(setterName, new Class<?>[]{ valueClass });
                m.invoke(content, new Object[]{ value });
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
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
            } else if (value.getClass() == BigInteger.class) {
                if (targetClass == String.class)
                    return ((BigInteger) value).toString();
                else
                    return null;
            } else if (value instanceof Enum<?>) {
                if (targetClass == String.class)
                    return Enums.getValue((Enum<?>) value);
                else
                    return null;
            } else
                return null;
        }

    }

}
