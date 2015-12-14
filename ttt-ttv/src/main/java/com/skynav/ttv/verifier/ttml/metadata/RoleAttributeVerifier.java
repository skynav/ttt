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

package com.skynav.ttv.verifier.ttml.metadata;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.MetadataValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class RoleAttributeVerifier implements MetadataValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        assert value instanceof List<?>;
        List<?> roles = (List<?>) value;
        Reporter reporter = context.getReporter();
        QName name = location.getAttributeName();
        Locator locator = location.getLocator();
        if (roles.size() > 0) {
            Set<String> tokens = new java.util.HashSet<String>();
            for (Object role : roles) {
                assert role instanceof String;
                String token = (String) role;
                if (token.indexOf("x-") == 0) {
                    if (reporter.isWarningEnabled("references-extension-role")) {
                        if (reporter.logWarning(reporter.message(locator, "*KEY*", "Extension role token ''{0}'' in ''{1}''.", token, name)))
                            failed = true;
                    }
                }
                if (tokens.contains(token)) {
                    if (reporter.isWarningEnabled("duplicate-role")) {
                        if (reporter.logWarning(reporter.message(locator, "*KEY*", "Duplicate role token ''{0}'' in ''{1}''.", token, name)))
                            failed = true;
                    }
                } else
                    tokens.add(token);
            }
        }
        return !failed;
    }

}
