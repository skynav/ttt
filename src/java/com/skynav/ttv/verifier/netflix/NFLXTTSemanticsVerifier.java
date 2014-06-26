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
import java.util.Set;

import org.xml.sax.Locator;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML1.TTML1Model;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.smpte.tt.rel2010.Image;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010SemanticsVerifier;

public class NFLXTTSemanticsVerifier extends ST20522010SemanticsVerifier {

    public NFLXTTSemanticsVerifier(Model model) {
        super(model);
    }

    protected static final QName ttElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createTt(new TimedText()).getName();
    public boolean verify(Object root, VerifierContext context) {
        if (!super.verify(root, context))
            return false;
        else if (root instanceof TimedText)
            return true;
        else {
            QName rootName = context.getBindingElementName(root);
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(getLocator(root), "*KEY*", "Root element must be ''{0}'', got ''{1}''.", ttElementName, rootName));
            return false;
        }
    }

    protected boolean verify(TimedText tt) {
        if (!super.verify(tt))
            return false;
        else {
            boolean failed = false;
            Reporter reporter = getContext().getReporter();
            if (tt.getProfile() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' attribute, but it is missing.",
                    ttElementName, TTML1ProfileVerifier.profileAttributeName));
                failed = true;
            } else {
                try {
                    Set<URI> designators = getModel().getProfileDesignators();
                    String value = tt.getProfile();
                    URI uri = new URI(value);
                    if (!designators.contains(uri)) {
                        reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' has a ''{1}'' attribute with value ''{2}'', but must have a value that matches one of following: {3}.",
                            ttElementName, TTML1ProfileVerifier.profileAttributeName, value, designators));
                        failed = true;
                    }
                } catch (URISyntaxException e) {
                    // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
                }
            }
            if (tt.getHead() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    ttElementName, TTML1Model.headElementName));
                failed = true;
            }
            if (tt.getBody() == null) {
                reporter.logError(reporter.message(getLocator(tt), "*KEY*", "Root element ''{0}'' must have a ''{1}'' child element, but it is missing.",
                    ttElementName, TTML1Model.bodyElementName));
                failed = true;
            }
            return !failed;
        }
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
