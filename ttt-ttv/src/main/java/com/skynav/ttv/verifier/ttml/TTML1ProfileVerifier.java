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

package com.skynav.ttv.verifier.ttml;

import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttp.Extension;
import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Feature;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.model.ttml1.ttp.Profile;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Profiles;

public class TTML1ProfileVerifier implements ProfileVerifier {

    public static final QName profileAttributeName = new QName(TTML1ParameterVerifier.getParameterNamespaceUri(), "profile");
    public static final QName profileElementName = new QName(TTML1ParameterVerifier.getParameterNamespaceUri(), "profile");

    @SuppressWarnings("unused")
    private Model model;

    public TTML1ProfileVerifier(Model model) {
        populate(model);
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Element)
            return verifyElementItem(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    protected boolean verifyElementItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof TimedText)
            failed = !verify((TimedText) content, locator, context);
        else if (content instanceof Profile)
            failed = !verify((Profile) content, locator, context);
        else if (content instanceof Feature)
            failed = !verify((Feature) content, locator, context);
        else if (content instanceof Extension)
            failed = !verify((Extension) content, locator, context);
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator,
                "*KEY*", "Invalid ''{0}'' profile related parameter item.", context.getBindingElementName(content)));
        }
        return !failed;
    }

    public boolean verify(TimedText content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        String profileAttribute = content.getProfile();
        List<Profile> profileElements = (content.getHead() != null) ? content.getHead().getParametersClass() : null;
        if ((profileElements != null) && (profileElements.size() > 0) && (profileAttribute != null)) {
            if (reporter.isWarningEnabled("ignored-profile-attribute")) {
                Message message = reporter.message(locator, "*KEY*",
                    "When {0} element is present, the {1} attribute is ignored.", profileElementName, profileAttributeName);
                if (reporter.logWarning(message))
                    failed = true;
            }
        }
        if (((profileElements == null) || (profileElements.size() == 0)) && ((profileAttribute == null) || (profileAttribute.length() == 0))) {
            if (reporter.isWarningEnabled("missing-profile")) {
                Message message = reporter.message(locator, "*KEY*",
                    "No profile specified, expected either {1} attribute or {0} element.", profileElementName, profileAttributeName);
                if (reporter.logWarning(message))
                    failed = true;
            }
        }
        return !failed;
    }

    protected boolean verify(Profile content, Locator locator, VerifierContext context) {
        boolean failed = false;
        // warn on duplicate features
        // warn on duplicate extensions
        return !failed;
    }

    protected boolean verify(Feature content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Features features = (Features) context.getBindingElementParent(content);
        if (features != null) {
            String base = features.getBase();
            String designation = content.getValue();
            Location location = new Location(content, context.getBindingElementName(content), null, locator);
            if (!Profiles.isFeatureDesignation(designation, location, context, base)) {
                Profiles.badFeatureDesignation(designation, location, context, base);
                failed = true;
            }
        } else
            throw new IllegalStateException("missing parent");
        return !failed;
    }

    protected boolean verify(Extension content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Extensions extensions = (Extensions) context.getBindingElementParent(content);
        if (extensions != null) {
            String base = extensions.getBase();
            String designation = content.getValue();
            Location location = new Location(content, context.getBindingElementName(content), null, locator);
            if (!Profiles.isExtensionDesignation(designation, location, context, base)) {
                Profiles.badExtensionDesignation(designation, location, context, base);
                failed = true;
            }
        } else
            throw new IllegalStateException("missing parent");
        return !failed;
    }

    private void populate(Model model) {
        this.model = model;
    }

}
