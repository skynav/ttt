/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.smpte.tt.rel2010.Data;
import com.skynav.ttv.model.smpte.tt.rel2010.Image;
import com.skynav.ttv.model.smpte.tt.rel2010.Information;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.URIs;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1SemanticsVerifier;
import com.skynav.ttv.verifier.util.Base64;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Strings;
import com.skynav.xml.helpers.Nodes;

import static com.skynav.ttv.model.smpte.ST20522010.Constants.*;

public class ST20522010SemanticsVerifier extends TTML1SemanticsVerifier {

    public ST20522010SemanticsVerifier(Model model) {
        super(model);
    }

    public boolean inSMPTEPrimaryNamespace(QName name) {
        return ST20522010.inSMPTEPrimaryNamespace(name);
    }

    public boolean inSMPTESecondaryNamespace(QName name) {
        return ST20522010.inSMPTESecondaryNamespace(name);
    }

    public boolean inSMPTENamespace(QName name) {
        return inSMPTEPrimaryNamespace(name) || inSMPTESecondaryNamespace(name);
    }

    @Override
    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        if (!super.verifyNonTTOtherElement(content, locator, context))
            return false;
        else
            return verifySMPTENonTTOtherElement(content, locator, context);
    }

    protected boolean verifySMPTENonTTOtherElement(Object content, Locator locator, VerifierContext context) {
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
            if (inSMPTENamespace(name)) {
                if (!model.isElement(name)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator, "*KEY", "Unknown element in SMPTE namespace ''{0}''.", name));
                    failed = true;
                } else if (isSMPTEImageElement(content)) {
                    failed = !verifySMPTEImage(content, locator, context);
                } else if (isSMPTEInformationElement(content)) {
                    failed = !verifySMPTEInformation(content, locator, context);
                } else if (isSMPTEDataElement(content)) {
                    failed = !verifySMPTEData(content, locator, context);
                } else {
                    return unexpectedContent(content);
                }
            }
        }
        return !failed;
    }

    protected boolean verifySMPTEElement(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        assert context == getContext();
        if (isSMPTEImageElement(content)) {
            failed = !verifySMPTEImage(content, locator, context);
        } else if (isSMPTEInformationElement(content)) {
            failed = !verifySMPTEInformation(content, locator, context);
        } else if (isSMPTEDataElement(content)) {
            failed = !verifySMPTEData(content, locator, context);
        } else {
            return unexpectedContent(content);
        }
        return !failed;
    }

    protected boolean isSMPTEDataElement(Object content) {
        return content instanceof Data;
    }

    protected boolean isSMPTEImageElement(Object content) {
        return content instanceof Image;
    }

    protected boolean isSMPTEInformationElement(Object content) {
        return content instanceof Information;
    }

    protected boolean verifySMPTEImage(Object image, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyOtherAttributes(image))
            failed = true;
        // Unable to test the following since no TT|SMPTE element type other than tt:metadata accepts xs:any children,
        // consequently, phase 3 (schema validation) will already have reported error if smpte:image isn't a child of tt:metadata.
        if (!verifyAncestry(image, locator, context))
            failed = true;
        if (!verifyBase64Content(image, getImageValue(image), locator, context))
            failed = true;
        return !failed;
    }

    protected String getImageValue(Object image) {
        return ((Image) image).getValue();
    }

    protected boolean verifySMPTEInformation(Object information, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyOtherAttributes(information))
            failed = true;
        if (!verifyAncestry(information, locator, context))
            failed = true;
        if (!verifyDuplicate(information, locator, context))
            failed = true;
        return !failed;
    }

    private boolean verifyDuplicate(Object information, Locator locator, VerifierContext context) {
        boolean failed = false;
        String key = getModel().getName() + ".informationAlreadyPresent";
        Boolean informationAlreadyPresent = (Boolean) context.getResourceState(key);
        if ((informationAlreadyPresent != null) && informationAlreadyPresent) {
            QName name = context.getBindingElementName(information);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "SMPTE element '" + name + "' is already present, only one instance allowed.", name));
            failed = true;
        } else {
            context.setResourceState(key, Boolean.TRUE);
        }
        return !failed;
    }

    protected boolean verifySMPTEData(Object data, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyDataType(data, getDataDatatype(data), locator, context))
            failed = true;
        if (!verifyOtherAttributes(data))
            failed = true;
        // Unable to test the following since no TT|SMPTE element type other than tt:metadata accepts xs:any children,
        // consequently, phase 3 (schema validation) will already have reported error if smpte:image isn't a child of tt:metadata.
        if (!verifyAncestry(data, locator, context))
            failed = true;
        if (!verifyBase64Content(data, getDataValue(data), locator, context))
            failed = true;
        return !failed;
    }

    protected String getDataDatatype(Object data) {
        return ((Data) data).getDatatype();
    }

    protected String getDataValue(Object data) {
        return ((Data) data).getValue();
    }

    private final QName dataTypeAttributeName = new QName("", "datatype");
    private boolean verifyDataType(Object data, String value, Locator locator, VerifierContext context) {
        boolean failed = false;
        QName name = dataTypeAttributeName;
        if (!verifyNonEmptyOrPadded(data, name, value, locator, context))
            failed = true;
        else if (!verifyDataType(data, name, value, locator, context))
            failed = true;
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
        }
        return !failed;
    }

    private boolean verifyDataType(Object data, QName name, String value, Locator locator, VerifierContext context) {
        if (isStandardDataType(value))
            return true;
        else if (isPrivateDataType(value))
            return true;
        else {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(locator, "*KEY*", "Non-standard {0} must start with 'x-' prefix, got ''{1}''.", value));
            return false;
        }
    }

    protected boolean isStandardDataType(String dataType) {
        return dataType.equals(DATA_TYPE_608);
    }

    private boolean isPrivateDataType(String dataType) {
        return dataType.indexOf("x-") == 0;
    }

    protected boolean verifyAncestry(Object content, Locator locator, VerifierContext context) {
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
                    reporter.logError(reporter.message(locator, "*KEY*", "Element ''{0}'' must have ancestors {1}.", name, ancestors));
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean verifyBase64Content(Object content, String value, Locator locator, VerifierContext context) {
        boolean failed = false;
        QName name = context.getBindingElementName(content);
        try {
            Base64.decode(value);
        } catch (IllegalArgumentException e) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "SMPTE element ''{0}'' content does not conform to Base64 encoding: {1}", name, e.getMessage()));
            failed = true;
        }
        return !failed;
    }

    @Override
    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        if (!super.verifyNonTTOtherAttributes(content, locator, context))
            return false;
        else
            return verifySMPTENonTTOtherAttributes(content, locator, context);
    }

    protected boolean verifySMPTENonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
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
                            "Unknown attribute in SMPTE namespace ''{0}'' not permitted on ''{1}''.", name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!model.isGlobalAttributePermitted(name, context.getBindingElementName(content))) {
                        reporter.logError(reporter.message(locator, "*KEY*",
                            "SMPTE attribute ''{0}'' not permitted on ''{1}''.", name, context.getBindingElementName(content)));
                        failedAttribute = true;
                    } else if (!verifyNonEmptyOrPadded(content, name, value, locator, context)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
                        failedAttribute = true;
                    } else if (!verifySMPTEAttribute(content, locator, context, name, value)) {
                        reporter.logError(reporter.message(locator, "*KEY*", "Invalid or prohibited {0} attribute or attribute value ''{1}''.", name, value));
                        failedAttribute = true;
                    }
                }
            }
            if (failedAttribute)
                failed = failedAttribute;
        }
        return !failed;
    }

    protected boolean verifySMPTEAttribute(Object content, Locator locator, VerifierContext context, QName name, String value) {
        boolean failed = false;
        if (isBackgroundImageAttribute(name)) {
            if (!verifySMPTEBackgroundImage(content, name, value, locator, context))
                failed = true;
        } else if (isBackgroundImageHVAttribute(name)) {
            if (!verifySMPTEBackgroundImageHV(content, name, value, locator, context))
                failed = true;
        }
        return !failed;
    }

    protected boolean verifyNonEmptyOrPadded(Object content, QName name, String value, Locator locator, VerifierContext context) {
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

    private static final QName backgroundImageAttributeName = new QName(NAMESPACE_2010, "backgroundImage");
    protected QName getBackgroundImageAttributeName() {
        return backgroundImageAttributeName;
    }

    private boolean isBackgroundImageAttribute(QName name) {
        return name.equals(getBackgroundImageAttributeName());
    }

    protected boolean verifySMPTEBackgroundImage(Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        boolean failed = false;
        assert valueObject instanceof String;
        String value = (String) valueObject;
        Reporter reporter = context.getReporter();
        if (URIs.isLocalFragment(value)) {
            String id = URIs.getFragment(value);
            assert id != null;
            Node node = context.getXMLNode(content);
            if (node == null) {
                if (content instanceof Element)
                    node = (Element) content;
            }
            if (node != null) {
                Document document = node.getOwnerDocument();
                if (document != null) {
                    Element targetElement = document.getElementById(id);
                    if (targetElement != null) {
                        Object target = context.getBindingElement(targetElement);
                        if (target != null) {
                            QName targetName = context.getBindingElementName(target);
                            if (!isImageElement(targetName)) {
                                Location location = new Location(content, context.getBindingElementName(content), name, locator);
                                IdReferences.badReference(target, location, context, name, getImageElementName());
                                failed = true;
                            }
                        }
                    } else {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "SMPTE attribute {0} references local image element ''{1}'', but no corresponding element is present.", name, value));
                        failed = true;
                    }
                }
            }
        } else {
            if (reporter.isWarningEnabled("references-external-image")) {
                Message message = reporter.message(locator, "*KEY*", "SMPTE attribute {0} references external image at ''{1}''.", name, value);;
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        return !failed;
    }


    private static final QName imageElementName = new QName(NAMESPACE_2010, ELT_IMAGE);
    protected QName getImageElementName() {
        return imageElementName;
    }

    private boolean isImageElement(QName name) {
        return name.equals(getImageElementName());
    }

    private boolean isBackgroundImageHVAttribute(QName name) {
        String ln = name.getLocalPart();
        return inSMPTEPrimaryNamespace(name) && (ln.equals(ATTR_BACKGROUND_IMAGE_HORIZONTAL) || ln.equals(ATTR_BACKGROUND_IMAGE_VERTICAL));
    }

    protected boolean verifySMPTEBackgroundImageHV(Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        assert valueObject instanceof String;
        String value = (String) valueObject;
        Integer[] minMax = new Integer[] { 1, 1 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
        Location location = new Location(content, context.getBindingElementName(content), name, locator);
        if (Keywords.isKeyword(value)) {
            return isBackgroundImageHVKeyword(name, value);
        } else if (Lengths.isLengths(value, location, context, minMax, treatments, null)) {
            return true;
        } else {
            Lengths.badLengths(value, location, context, minMax, treatments);
            return false;
        }
    }

    private boolean isBackgroundImageHVKeyword(QName name, String value) {
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
