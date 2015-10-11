/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.imsc;

import java.nio.charset.Charset;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.imsc.IMSC1;
import com.skynav.ttv.model.imsc1.ittm.AltText;
import com.skynav.ttv.model.ttml.TTML1.TTML1Model;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.util.Strings;
import com.skynav.xml.helpers.Nodes;

import static com.skynav.ttv.model.imsc.IMSC1.Constants.*;

public class IMSC1SemanticsVerifier extends ST20522010SemanticsVerifier {

    public IMSC1SemanticsVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean verify(Object root, VerifierContext context) {
        boolean failed = false;
        if (!super.verify(root, context))
            failed = true;
        if (root instanceof TimedText) {
            TimedText tt = (TimedText) root;
            if (!verifyCharset(tt))
                failed = true;
        } else {
            QName rootName = context.getBindingElementName(root);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root), "*KEY*", "Root element must be ''{0}'', got ''{1}''.", TTML1Model.timedTextElementName, rootName));
            failed = true;
        }
        return !failed;
    }

    @Override
    protected boolean verifyTimedText(Object root) {
        if (!super.verifyTimedText(root))
            return false;
        else {
            boolean failed = false;
            assert root instanceof TimedText;
            TimedText tt = (TimedText) root;
            Reporter reporter = getContext().getReporter();
            String profile = tt.getProfile();
            if (profile == null) {
                Message message = reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' should have a ''{1}'' attribute, but it is missing.",
                    TTML1Model.timedTextElementName, TTML1ProfileVerifier.profileAttributeName);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
            return !failed;
        }
    }

    protected boolean verifyCharset(TimedText tt) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        try {
            Charset charsetRequired = Charset.forName(IMSC1.Constants.CHARSET_REQUIRED);
            String charsetRequiredName = charsetRequired.name();
            Charset charset = (Charset) getContext().getResourceState("encoding");
            String charsetName = (charset != null) ? charset.name() : "unknown";
            if (!charsetName.equals(charsetRequiredName)) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Document encoding uses ''{0}'', but requires ''{1}''.", charsetName, charsetRequiredName));
                failed = true;
            }
        } catch (Exception e) {
            reporter.logError(e);
            failed = true;
        }
        return !failed;
    }

    public boolean inIMSCNamespace(QName name) {
        return IMSC1.inIMSCNamespace(name);
    }

    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        assert context == getContext();
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
            if (inIMSCNamespace(name)) {
                if (!model.isElement(name)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator, "*KEY", "Unknown element in IMSC namespace ''{0}''.", name));
                    failed = true;
                } else if (isIMSCAltTextElement(content)) {
                    failed = !verifyIMSCAltText(content, locator, context);
                } else {
                    return unexpectedContent(content);
                }
            }
        }
        return !failed;
    }

    protected boolean isIMSCAltTextElement(Object content) {
        return content instanceof AltText;
    }

    protected boolean verifyIMSCAltText(Object image, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyOtherAttributes(image))
            failed = true;
        if (!verifyAncestry(image, locator, context))
            failed = true;
        return !failed;
    }

    private boolean verifyAncestry(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Node node = context.getXMLNode(content);
        if (node == null) {
            if (content instanceof Element)
                node = (Element) content;
        }
        if (node != null) {
            QName name = context.getBindingElementName(content);
            List<List<QName>> ancestors = getModel().getElementPermissibleAncestors(name);
            if (ancestors != null) {
                if (!Nodes.hasAncestors(node, ancestors)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator, "*KEY*", "IMSC element ''{0}'' must have ancestors {1}.", name, ancestors));
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
            boolean failedAttribute = false;
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
                    Reporter reporter = context.getReporter();
                    String value = attribute.getValue();
                    if (!model.isGlobalAttribute(name)) {
                        reporter.logError(reporter.message(locator, "*KEY*",
                            "Unknown attribute in IMSC namespace ''{0}'' not permitted on ''{1}''.", name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!model.isGlobalAttributePermitted(name, context.getBindingElementName(content))) {
                        reporter.logError(reporter.message(locator, "*KEY*",
                            "IMSC attribute ''{0}'' not permitted on ''{1}''.", name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!verifyNonEmptyOrPadded(content, name, value, locator, context)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                        failedAttribute = true;
                    } else if (!verifyIMSCAttribute(content, locator, context, name, value)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                        failedAttribute = true;
                    }
                }
            }
            if (failedAttribute)
                failed = failedAttribute;
        }
        return !failed;
    }

    private boolean verifyNonEmptyOrPadded(Object content, QName name, String value, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        if (value.length() == 0) {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Empty {0} not permitted, got ''{1}''.", name, value));
            return false;
        } else if (Strings.isAllXMLSpace(value)) {
            reporter.logInfo(reporter.message(locator, "*KEY*", "The value of {0} is entirely XML space characters, got ''{1}''.", name, value));
            return false;
        } else if (!value.equals(value.trim())) {
            reporter.logInfo(reporter.message(locator, "*KEY*", "XML space padding not permitted on {0}, got ''{1}''.", name, value));
            return false;
        } else
            return true;
    }

    protected boolean verifyIMSCAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        /*
        if (isAspectRatioAttribute(name)) {
            if (!verifyAspectRatio(content, name, value, locator, context))
                failed = true;
        } else if (isForcedDisplayAttribute(name)) {
            if (!verifyForcedDisplay(content, name, value, locator, context))
                failed = true;
        } else if (isProgressivelyDecodableAttribute(name)) {
            if (!verifyProgressivelyDecodable(content, name, value, locator, context))
                failed = true;
        }
        */
        return !failed;
    }

    /*
    private boolean isAspectRatioAttribute(QName name) {
        String ln = name.getLocalPart();
        return inIMSCStylingNamespace(name) && ln.equals(ATTR_ASPECT_RATIO);
    }

    private boolean isForcedDisplayAttribute(QName name) {
        String ln = name.getLocalPart();
        return inIMSCStyleNamespace(name) && ln.equals(ATTR_FORCED_DISPLAY);
    }

    protected boolean verifyEBUTTAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        if (isLinePaddingAttribute(name)) {
            if (!verifyLinePadding(content, name, value, locator, context))
                failed = true;
        } else if (isMultiRowAlignAttribute(name)) {
            if (!verifyMultiRowAlign(content, name, value, locator, context))
                failed = true;
        }
        return !failed;
    }

    protected boolean verifySMPTEAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        return !failed;
    }
    */

}
