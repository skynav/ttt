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
import com.skynav.ttv.model.ttml10.tt.Body;
import com.skynav.ttv.model.ttml10.tt.Break;
import com.skynav.ttv.model.ttml10.tt.Division;
import com.skynav.ttv.model.ttml10.tt.Paragraph;
import com.skynav.ttv.model.ttml10.tt.Span;
import com.skynav.ttv.model.ttml10.tt.Metadata;
import com.skynav.ttv.model.ttml10.ttm.Actor;
import com.skynav.ttv.model.ttml10.ttm.Agent;
import com.skynav.ttv.model.ttml10.ttm.Name;
import com.skynav.ttv.util.Enums;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.MetadataValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.metadata.AgentVerifier;
import com.skynav.ttv.verifier.ttml.metadata.RoleVerifier;
import com.skynav.ttv.verifier.util.Strings;

public class TTML10MetadataVerifier implements MetadataVerifier {

    private static final String metadataNamespace = "http://www.w3.org/ns/ttml#metadata";

    public static final String getMetadataNamespaceUri() {
        return metadataNamespace;
    }

    private static Object[][] metadataAccessorMap = new Object[][] {
        {
            new QName(metadataNamespace,"agent"),               // attribute name
            "Agent",                                            // accessor method name suffix
            String.class,                                       // value type
            AgentVerifier.class,                                // specialized verifier
            Boolean.FALSE,                                      // padding permitted
            null,                                               // default value
        },
        {
            new QName(metadataNamespace,"role"),
            "Role",
            String.class,
            RoleVerifier.class,
            Boolean.FALSE,
            null,
        },
    };

    private Model model;
    private Map<QName, MetadataAccessor> accessors;

    public TTML10MetadataVerifier(Model model) {
        populate(model);
    }

    public QName getMetadataAttributeName(String metadataName) {
        // assumes that metadata name is same as local part of qualified attribute name, which
        // is presently true in TTML10
        for (QName name : accessors.keySet()) {
            if (metadataName.equals(name.getLocalPart()))
                return name;
        }
        return null;
    }

    public boolean verify(Object content, Locator locator, VerifierContext context) {
        boolean failedAttributeItem = false;
        for (QName name : accessors.keySet()) {
            MetadataAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, context))
                failedAttributeItem = true;
        }
        boolean failedElementItem = false;
        if (content instanceof Actor)
            failedElementItem = !verify((Actor) content, locator, context);
        else if (content instanceof Agent)
            failedElementItem = !verify((Agent) content, locator, context);
        else if (content instanceof Metadata)
            failedElementItem = !verify((Metadata) content, locator, context);
        else if (content instanceof Name)
            failedElementItem = !verify((Name) content, locator, context);
        if (failedElementItem)
            context.getReporter().logError(locator, "Invalid '" + context.getBindingElementName(content) + "' metadata item.");
        return !failedAttributeItem && !failedElementItem;
    }

    public boolean verify(Actor content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verify(Agent content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verify(Metadata content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verify(Name content, Locator locator, VerifierContext context) {
        return true;
    }

    private void populate(Model model) {
        Map<QName, MetadataAccessor> accessors = new java.util.HashMap<QName, MetadataAccessor>();
        for (Object[] metadataAccessorEntry : metadataAccessorMap) {
            assert metadataAccessorEntry.length >= 6;
            QName metadataName = (QName) metadataAccessorEntry[0];
            String accessorName = (String) metadataAccessorEntry[1];
            Class<?> valueClass = (Class<?>) metadataAccessorEntry[2];
            Class<?> verifierClass = (Class<?>) metadataAccessorEntry[3];
            boolean paddingPermitted = ((Boolean) metadataAccessorEntry[4]).booleanValue();
            Object defaultValue = metadataAccessorEntry[5];
            accessors.put(metadataName, new MetadataAccessor(metadataName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue));
        }
        this.model = model;
        this.accessors = accessors;
    }

    private class MetadataAccessor {

        private QName metadataName;
        private String getterName;
        @SuppressWarnings("unused")
        private String setterName;
        private Class<?> valueClass;
        private MetadataValueVerifier verifier;
        private boolean paddingPermitted;
        private Object defaultValue;

        public MetadataAccessor(QName metadataName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            populate(metadataName, accessorName, valueClass, verifierClass, paddingPermitted, defaultValue);
        }

        public boolean verify(Model model, Object content, Locator locator, VerifierContext context) {
            boolean success = true;
            Object value = getMetadataValue(content);
            if (value != null) {
                if (!permitsMetadataAttribute(content)) {
                    context.getReporter().logInfo(locator, "TT Metadata attribute '" + metadataName + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    success = false;
                } else if (value instanceof String)
                    success = verify(model, content, (String) value, locator, context);
                else
                    success = verifier.verify(model, content, metadataName, value, locator, context);
            } else
                setMetadataDefaultValue(content);
            if (!success)
                context.getReporter().logError(locator, "Invalid " + metadataName + " value '" + value + "'.");
            return success;
        }

        private boolean permitsMetadataAttribute(Object content) {
            if (content instanceof Body)
                return true;
            else if (content instanceof Division)
                return true;
            else if (content instanceof Paragraph)
                return true;
            else if (content instanceof Span)
                return true;
            else if (content instanceof Break)
                return true;
            else if (content instanceof Metadata)
                return true;
            else
                return false;
        }

        private boolean verify(Model model, Object content, String value, Locator locator, VerifierContext context) {
            boolean success = false;
            Reporter reporter = context.getReporter();
            if (value.length() == 0)
                reporter.logInfo(locator, "Empty " + metadataName + " not permitted, got '" + value + "'.");
            else if (Strings.isAllXMLSpace(value))
                reporter.logInfo(locator, "The value of " + metadataName + " is entirely XML space characters, got '" + value + "'.");
            else if (!paddingPermitted && !value.equals(value.trim()))
                reporter.logInfo(locator, "XML space padding not permitted on " + metadataName + ", got '" + value + "'.");
            else
                success = verifier.verify(model, content, metadataName, value, locator, context);
            return success;
        }

        private MetadataValueVerifier createMetadataValueVerifier(Class<?> verifierClass) {
            try {
                return (MetadataValueVerifier) verifierClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        private void populate(QName metadataName, String accessorName, Class<?> valueClass, Class<?> verifierClass, boolean paddingPermitted, Object defaultValue) {
            this.metadataName = metadataName;
            this.getterName = "get" + accessorName;
            this.setterName = "set" + accessorName;
            this.valueClass = valueClass;
            this.verifier = createMetadataValueVerifier(verifierClass);
            this.paddingPermitted = paddingPermitted;
            this.defaultValue = defaultValue;
        }

        private Object getMetadataValue(Object content) {
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
                return convertType(getMetadataValueAsString(content), valueClass);
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setMetadataDefaultValue(Object content) {
            if (defaultValue != null)
                throw new UnsupportedOperationException();
        }

        private String getMetadataValueAsString(Object content) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod("getOtherAttributes", new Class<?>[]{});
                Object otherAttributes = m.invoke(content, new Object[]{});
                if ((otherAttributes != null) && (otherAttributes instanceof Map<?,?>)) {
                    return (String) (((Map<?,?>)otherAttributes).get(metadataName));
                }
                return null;
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

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (value.getClass() == targetClass)
                return value;
            else if (value instanceof Enum<?>) {
                if (targetClass == String.class)
                    return Enums.getValue((Enum<?>) value);
                else
                    return null;
            } else
                return null;
        }

    }

}
