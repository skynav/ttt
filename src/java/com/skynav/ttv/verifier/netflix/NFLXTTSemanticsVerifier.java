/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.verifier.netflix;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import org.xml.sax.Locator;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.netflix.NFLXTT;
import com.skynav.ttv.model.ttml.TTML1.TTML1Model;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.smpte.tt.rel2010.Image;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1ParameterVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.ttml.TTML1StyleVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010SemanticsVerifier;

public class NFLXTTSemanticsVerifier extends ST20522010SemanticsVerifier {

    public NFLXTTSemanticsVerifier(Model model) {
        super(model);
    }

    public boolean verify(Object root, VerifierContext context) {
        boolean failed = false;
        if (!super.verify(root, context))
            failed = true;
        if (root instanceof TimedText) {
            TimedText tt = (TimedText) root;
            if (!verifyCharset(tt))
                failed = true;
            if (!verifyCellResolutionIfCellUnitUsed(tt))
                failed = true;
            if (!verifyExtentIfPixelUnitUsed(tt))
                failed = true;
            if (!verifyLengthAvoidsEmUnit(tt))
                failed = true;
            if (!verifyRegionContainment(tt))
                failed = true;
            if (!verifyRegionNonOverlap(tt))
                failed = true;
        } else {
            QName rootName = context.getBindingElementName(root);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root), "*KEY*", "Root element must be ''{0}'', got ''{1}''.", TTML1Model.ttElementName, rootName));
            failed = true;
        }
        return !failed;
    }

    protected boolean verify(TimedText tt) {
        if (!super.verify(tt))
            return false;
        else {
            boolean failed = false;
            Reporter reporter = getContext().getReporter();
            if (tt.getProfile() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' attribute, but it is missing.",
                    TTML1Model.ttElementName, TTML1ProfileVerifier.profileAttributeName));
                failed = true;
            } else {
                try {
                    Set<URI> designators = getModel().getProfileDesignators();
                    String value = tt.getProfile();
                    URI uri = new URI(value);
                    if (!designators.contains(uri)) {
                        reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' has a ''{1}'' attribute with value ''{2}'', but must have a value that matches one of following: {3}.",
                            TTML1Model.ttElementName, TTML1ProfileVerifier.profileAttributeName, value, designators));
                        failed = true;
                    }
                } catch (URISyntaxException e) {
                    // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
                }
            }
            if (tt.getHead() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    TTML1Model.ttElementName, TTML1Model.headElementName));
                failed = true;
            }
            if (tt.getBody() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    TTML1Model.ttElementName, TTML1Model.bodyElementName));
                failed = true;
            }
            return !failed;
        }
    }

    protected boolean verifyCharset(TimedText tt) {
        boolean failed = false;
        Reporter reporter = getContext().getReporter();
        try {
            Charset charsetRequired = Charset.forName(NFLXTT.Constants.CHARSET_REQUIRED);
            String charsetRequiredName = charsetRequired.name();
            Charset charset = (Charset) getContext().getResourceState("charset");
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

    protected boolean verifyCellResolutionIfCellUnitUsed(TimedText tt) {
        boolean failed = false;
        List<Locator> usage = (List<Locator>) getContext().getResourceState("usageCell");
        if ((usage != null) && (usage.size() > 0)) {
            Element ttElement = (Element) getContext().getXMLNode(tt);
            QName cellResolutionName = TTML1ParameterVerifier.cellResolutionAttributeName;
            String cellResolution = ttElement.getAttributeNS(cellResolutionName.getNamespaceURI(), cellResolutionName.getLocalPart());
            System.err.println(cellResolution);
            if ((cellResolution == null) || (cellResolution.length() == 0)) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Uses ''c'' unit, but does not specify ''{0}'' attribute on ''{1}'' element.",
                                                       cellResolutionName, TTML1Model.ttElementName));
                }
                failed = true;
            }
        }
        return true;
    }

    protected boolean verifyExtentIfPixelUnitUsed(TimedText tt) {
        boolean failed = false;
        List<Locator> usage = (List<Locator>) getContext().getResourceState("usagePixel");
        if ((usage != null) && (usage.size() > 0)) {
            String extent = tt.getExtent();
            if ((extent == null) || (extent.length() == 0)) {
                Reporter reporter = getContext().getReporter();
                for (Locator locator : usage) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Uses ''px'' unit, but does not specify ''{0}'' attribute on ''{1}'' element.",
                                                       TTML1StyleVerifier.extentAttributeName, TTML1Model.ttElementName));
                }
                failed = true;
            }
        }
        return true;
    }

    protected boolean verifyLengthAvoidsEmUnit(TimedText tt) {
        boolean failed = false;
        List<Locator> usage = (List<Locator>) getContext().getResourceState("usageEm");
        if ((usage != null) && (usage.size() > 0)) {
            Reporter reporter = getContext().getReporter();
            for (Locator locator : usage)
                reporter.logError(reporter.message(locator, "*KEY*", "Uses disallowed ''em'' unit in length expression."));
            failed = true;
        }
        return !failed;
    }

    protected boolean verifyRegionContainment(TimedText tt) {
        return true;
    }

    protected boolean verifyRegionNonOverlap(TimedText tt) {
        return true;
    }

    protected static final QName smpteImageElementName = new com.skynav.ttv.model.smpte.tt.rel2010.ObjectFactory().createImage(new Image()).getName();
    protected boolean verifySMPTEImage(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (isSMPTEImageElement(content)) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Element type ''{0}'' is not permitted.", smpteImageElementName));
            failed = true;
        } else {
            failed = super.verifySMPTEElement(content, locator, context);
        }
        return !failed;
    }


}
