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
 
package com.skynav.ttv.verifier.smpte;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1SemanticsVerifier;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

import static com.skynav.ttv.model.smpte.ST20522010.Constants.*;
import static com.skynav.ttv.model.smpte.ST20522010.inSMPTEPrimaryNamespace;
import static com.skynav.ttv.model.smpte.ST20522010.inSMPTENamespace;

public class ST20522010SemanticsVerifier extends TTML1SemanticsVerifier {

    public ST20522010SemanticsVerifier(Model model) {
        super(model);
    }

    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Node node = context.getXMLNode(content);
        if (node == null) {
            if (content instanceof Element)
                node = (Element) content;
        }
        if (node != null) {
            String nsUri = node.getNamespaceURI();
            String localName = node.getLocalName();
            if (localName == null)
                localName = node.getNodeName();
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            Model model = getModel();
            if (inSMPTENamespace(name)) {
                if (!model.isElement(name)) {
                    context.getReporter().logError(locator, "Unknown element in SMPTE namespace '" + name + "'.");
                    failed = true;
                }
            }
        }
        return !failed;
    }

    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
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
            Model model = getModel();
            if (model.isNamespace(name.getNamespaceURI())) {
                if (name.getNamespaceURI().indexOf(NAMESPACE_PREFIX) == 0) {
                    String value = attribute.getValue();
                    if (!model.isGlobalAttribute(name)) {
                        context.getReporter().logError(locator, "Unknown attribute in SMPTE namespace '" + name + "' not permitted on '" +
                            context.getBindingElementName(content) + "'.");
                        failed = true;
                    } else if (!model.isGlobalAttributePermitted(name, content)) {
                        context.getReporter().logError(locator, "SMPTE attribute '" + name + "' not permitted on '" +
                            context.getBindingElementName(content) + "'.");
                        failed = true;
                    } else if (isBackgroundHVAttribute(name)) {
                        if (!verifyBackgroundHV(getModel(), content, name, value, locator, context)) {
                            context.getReporter().logError(locator, "Invalid " + name + " value '" + value + "'.");
                            failed = true;
                        }
                    }
                }
            }
        }
        return !failed;
    }

    private boolean isBackgroundHVAttribute(QName name) {
        String ln = name.getLocalPart();
        return inSMPTEPrimaryNamespace(name) && (ln.equals(ATTR_BACKGROUND_IMAGE_HORIZONTAL) || ln.equals(ATTR_BACKGROUND_IMAGE_VERTICAL));
    }

    private boolean verifyBackgroundHV(Model model, Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        assert valueObject instanceof String;
        String value = (String) valueObject;
        Integer[] minMax = new Integer[] { 1, 1 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
        if (Keywords.isKeyword(value)) {
            if (isBackgroundHVKeyword(name, value))
                return true;
            else
                return false;
        } else if (Lengths.isLengths(value, locator, context, minMax, treatments, null)) {
            return true;
        } else {
            Lengths.badLengths(value, locator, context, minMax, treatments);
            return false;
        }
    }

    private boolean isBackgroundHVKeyword(QName name, String value) {
        String ln = name.getLocalPart();
        if (ln.equals(ATTR_BACKGROUND_IMAGE_HORIZONTAL)) {
            if (value.equals("left"))
                return true;
            else if (value.equals("center"))
                return true;
            else if (value.equals("right"))
                return true;
            else
                return false;
        } else if (ln.equals(ATTR_BACKGROUND_IMAGE_VERTICAL)) {
            if (value.equals("top"))
                return true;
            else if (value.equals("center"))
                return true;
            else if (value.equals("bottom"))
                return true;
            else
                return false;
        } else {
            return false;
        }
    }

}
