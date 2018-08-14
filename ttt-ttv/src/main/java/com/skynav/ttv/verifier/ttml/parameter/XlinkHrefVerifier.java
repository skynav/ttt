/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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

import javax.xml.bind.JAXBElement;import javax.xml.bind.JAXBElement;

import org.xml.sax.Locator;

import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class XlinkHrefVerifier implements ParameterValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        assert value instanceof String;
        String href = (String) value;
        try {
            URI hrefUri = new URI(href);
            Object content = location.getContent();
            do {
                Object parent = context.getBindingElementParent(content);
                if (parent instanceof JAXBElement<?>)
                    parent = ((JAXBElement<?>)parent).getValue();
                if ((parent != null) && (parent instanceof Span)) {
                    String hrefParent = ((Span) parent).getHref();
                    if (hrefParent != null) {
                        Reporter reporter = context.getReporter();
                        reporter.logInfo(reporter.message(location.getLocator(), "*KEY*",
                            "Nested {0} not permitted.", location.getAttributeName()));
                        failed = true;
                        break;
                    }
                } else {
                    content = parent;
                }
            } while (content != null);
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
            failed = true;
        }
        return !failed;
    }

}
