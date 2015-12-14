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

package com.skynav.ttv.verifier.ttml.parameter;

import java.net.URI;
import java.net.URISyntaxException;

import org.xml.sax.Locator;

import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class BaseVerifier implements ParameterValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        assert value instanceof String;
        String base = (String) value;
        try {
            URI baseUri = new URI(base);
            Reporter reporter = context.getReporter();
            Locator locator = location.getLocator();
            if (location.getContent() instanceof Features) {
                URI featureNamespaceUri = context.getModel().getFeatureNamespaceUri();
                if (!baseUri.isAbsolute()) {
                    reporter.logInfo(reporter.message(locator,
                        "*KEY*", "Non-absolute feature namespace ''{0}''.", baseUri));
                    failed = true;
                } else if (!baseUri.equals(featureNamespaceUri)) {
                    reporter.logInfo(reporter.message(locator,
                        "*KEY*", "Unknown feature namespace ''{0}'', expected ''{1}''.", base, featureNamespaceUri));
                    failed = true;
                }
            } else if (location.getContent() instanceof Extensions) {
                URI extensionNamespaceUri = context.getModel().getExtensionNamespaceUri();
                if (!baseUri.equals(extensionNamespaceUri)) {
                    if (!baseUri.isAbsolute()) {
                        reporter.logInfo(reporter.message(locator,
                            "*KEY*", "Non-absolute extension namespace ''{0}''.", baseUri));
                        failed = true;
                    } else if (reporter.isWarningEnabled("references-other-extension-namespace")) {
                        if (reporter.logWarning(reporter.message(locator,
                            "*KEY*", "Other extension namespace ''{0}''.", baseUri))) {
                            failed = true;
                        }
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
