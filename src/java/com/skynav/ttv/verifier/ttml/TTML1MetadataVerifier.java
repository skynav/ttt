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

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Metadata;
import com.skynav.ttv.model.ttml1.ttd.AgentType;
import com.skynav.ttv.model.ttml1.ttm.Actor;
import com.skynav.ttv.model.ttml1.ttm.Agent;
import com.skynav.ttv.model.ttml1.ttm.Name;
import com.skynav.ttv.util.Enums;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.MetadataValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.metadata.AgentAttributeVerifier;
import com.skynav.ttv.verifier.ttml.metadata.RoleAttributeVerifier;
import com.skynav.ttv.verifier.util.Agents;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Strings;

import static com.skynav.ttv.model.ttml.TTML1.Constants.*;

public class TTML1MetadataVerifier implements MetadataVerifier {

    public static final String NAMESPACE = NAMESPACE_TT_METADATA;

    public static final String getMetadataNamespaceUri() {
        return NAMESPACE;
    }

    private static QName agentAttributeName = new QName(NAMESPACE,"agent");
    private static QName roleAttributeName = new QName(NAMESPACE,"role");
    private static Object[][] metadataAccessorMap = new Object[][] {
        {
            agentAttributeName,                                 // attribute name
            "Agent",                                            // accessor method name suffix
            List.class,                                         // value type
            AgentAttributeVerifier.class,                       // specialized verifier
            Boolean.FALSE,                                      // padding permitted
            null,                                               // default value
        },
        {
            roleAttributeName,
            "Role",
            List.class,
            RoleAttributeVerifier.class,
            Boolean.FALSE,
            null,
        },
    };

    private Model model;
    private Map<QName, MetadataAccessor> accessors;

    public TTML1MetadataVerifier(Model model) {
        populate(model);
    }

    public QName getMetadataAttributeName(String metadataName) {
        // assumes that metadata name is same as local part of qualified attribute name, which
        // is presently true in TTML1
        for (QName name : accessors.keySet()) {
            if (metadataName.equals(name.getLocalPart()))
                return name;
        }
        return null;
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes)
            return verifyAttributeItems(content, locator, context);
        else if (type == ItemType.Element)
            return verifyElementItem(content, locator, context);
        else if (type == ItemType.Other)
            return verifyOtherAttributes(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    private boolean verifyAttributeItems(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        for (QName name : accessors.keySet()) {
            MetadataAccessor sa = accessors.get(name);
            if (!sa.verify(model, content, locator, context))
                failed = true;
        }
        return !failed;
    }

    private boolean verifyElementItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof Actor)
            failed = !verify((Actor) content, locator, context);
        else if (content instanceof Agent)
            failed = !verify((Agent) content, locator, context);
        else if (content instanceof Metadata)
            failed = !verify((Metadata) content, locator, context);
        else if (content instanceof Name)
            failed = !verify((Name) content, locator, context);
        if (failed)
            context.getReporter().logError(locator, "Invalid '" + context.getBindingElementName(content) + "' metadata item.");
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
                if (!isMetadataAttribute(name)) {
                    context.getReporter().logError(locator, "Unknown attribute in TT Metadata namespace '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                } else if (!permitsMetadataAttribute(content)) {
                    context.getReporter().logError(locator, "TT Metadata attribute '" + name + "' not permitted on '" +
                        context.getBindingElementName(content) + "'.");
                    failed = true;
                }
            }
        }
        return !failed;
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

    private boolean isMetadataAttribute(QName name) {
        return name.getNamespaceURI().equals(NAMESPACE) && accessors.containsKey(name);
    }

    private final QName actorAgentAttributeName = new QName("", "agent");
    public boolean verify(Actor content, Locator locator, VerifierContext context) {
        QName name = actorAgentAttributeName;
        QName targetName = model.getIdReferenceTargetName(name);
        Class<?> targetClass = model.getIdReferenceTargetClass(name);
        List<List<QName>> ancestors = model.getIdReferencePermissibleAncestors(name);
        Object agent = content.getAgent();
        Node node = context.getXMLNode(agent);
        if (Agents.isAgentReference(node, agent, locator, context, targetClass, ancestors))
            return true;
        else {
            Agents.badAgentReference(node, agent, locator, context, name, targetName, targetClass, ancestors);
            return false;
        }
    }

    private final QName actorElementName = new QName(NAMESPACE, "actor");
    private final QName agentElementName = new QName(NAMESPACE, "agent");
    private final QName nameElementName = new QName(NAMESPACE, "name");
    public boolean verify(Agent content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        if (content.getName().isEmpty()) {
            if (reporter.isWarningEnabled("missing-agent-name")) {
                if (reporter.logWarning(locator, "An '" + agentElementName + "' element should have at least one '" + nameElementName + "' child, but none is present."))
                    failed = true;
            }
        }
        if ((content.getType() == AgentType.CHARACTER) && (content.getActor() == null)) {
            if (reporter.isWarningEnabled("missing-agent-actor")) {
                if (reporter.logWarning(locator, "An '" + agentElementName + "' element of type 'character' should have an '" + actorElementName + "' child, but none is present."))
                    failed = true;
            }
        }
        return !failed;
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
                if (value instanceof String)
                    success = verify(model, content, (String) value, locator, context);
                else
                    success = verifier.verify(model, content, metadataName, value, locator, context);
            } else
                setMetadataDefaultValue(content);
            if (!success) {
                if (metadataName.equals(agentAttributeName)) {
                    value = IdReferences.getIdReferences(value);
                } else
                    value = value.toString();
                context.getReporter().logError(locator, "Invalid " + metadataName + " value '" + value + "'.");
            }
            return success;
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
                return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private void setMetadataDefaultValue(Object content) {
            if (defaultValue != null)
                throw new UnsupportedOperationException();
        }

        private Object convertType(Object value, Class<?> targetClass) {
            if (value == null)
                return null;
            else if (targetClass.isInstance(value))
                return value;
            else if (value instanceof String) {
                if (targetClass == List.class) {
                    String[] tokens = ((String)value).split("\\s+");
                    List<String> listOfTokens = new java.util.ArrayList<String>(tokens.length);
                    for (String token : tokens)
                        listOfTokens.add(token);
                    return listOfTokens;
                } else
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
