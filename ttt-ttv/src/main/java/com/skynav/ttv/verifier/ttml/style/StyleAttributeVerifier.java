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

package com.skynav.ttv.verifier.ttml.style;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.IdReferences;
import com.skynav.ttv.verifier.util.Styles;

public class StyleAttributeVerifier implements StyleValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        assert value instanceof List<?>;
        List<?> styles = (List<?>) value;
        if (styles.size() > 0) {
            Model model = context.getModel();
            QName name = location.getAttributeName();
            QName targetName = model.getIdReferenceTargetName(name);
            Class<?> targetClass = model.getIdReferenceTargetClass(name);
            List<List<QName>> ancestors = model.getIdReferencePermissibleAncestors(name);
            Object styleLast = null;
            Set<String> styleIdentifiers = new java.util.HashSet<String>();
            for (Object style : styles) {
                Node node = context.getXMLNode(style);
                if (!Styles.isStyleReference(node, style, location, context, targetClass, ancestors)) {
                    Styles.badStyleReference(node, style, location, context, name, targetName, targetClass, ancestors);
                    failed = true;
                }
                String id = IdReferences.getId(style);
                Reporter reporter = context.getReporter();
                Locator locator = location.getLocator();
                if (styleIdentifiers.contains(id)) {
                    if (reporter.isWarningEnabled("duplicate-idref-in-style")) {
                        if (reporter.logWarning(reporter.message(locator, "*KEY*",
                            "Duplicate IDREF ''{0}'' should not appear in {1}.", IdReferences.getId(style), name))) {
                            failed = true;
                        }
                    }
                } else
                    styleIdentifiers.add(id);
                if ((styleLast != null) && referencesSameStyle(style, styleLast)) {
                    if (reporter.isWarningEnabled("duplicate-idref-in-style-no-intervening")) {
                        if (reporter.logWarning(reporter.message(locator, "*KEY*",
                            "Duplicate IDREF ''{0}'' should not appear in {1} without distinct intervening IDREF.", IdReferences.getId(style), name))) {
                            failed = true;
                        }
                    }
                }
                styleLast = style;
            }
        }
        return !failed;
    }

    private static boolean referencesSameStyle(Object s1, Object s2) {
        String id1 = IdReferences.getId(s1);
        String id2 = IdReferences.getId(s2);
        return id1.equals(id2);
    }

}
