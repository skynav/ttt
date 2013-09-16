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
 
package com.skynav.ttv.verifier.ttml.parameter;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class BaseVerifier implements ParameterValueVerifier {

    public boolean verify(Model model, Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        String base = (String) valueObject;
        try {
            URI baseUri = new URI(base);
            if (content instanceof Features) {
                URI featureNamespaceUri = model.getFeatureNamespaceUri();
                if (!baseUri.isAbsolute()) {
                    reporter.logInfo(locator, "Non-absolute feature namespace '" + baseUri + "'.");
                    failed = true;
                } else if (!baseUri.equals(featureNamespaceUri)) {
                    reporter.logInfo(locator, "Unknown feature namespace '" + base + "', expected '" + featureNamespaceUri + "'.");
                    failed = true;
                }
            } else if (content instanceof Extensions) {
                URI extensionNamespaceUri = model.getExtensionNamespaceUri();
                if (!baseUri.equals(extensionNamespaceUri)) {
                    if (!baseUri.isAbsolute()) {
                        reporter.logInfo(locator, "Non-absolute extension namespace '" + baseUri + "'.");
                        failed = true;
                    } else if (reporter.isWarningEnabled("references-other-extension-namespace")) {
                        if (reporter.logWarning(locator, "Other extension namespace '" + baseUri + "'."))
                            failed = true;
                    }
                }
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
            failed = true;
        }
        return !failed;
    }

}
