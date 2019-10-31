/*
 * Copyright 2015-2019 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.ProfileCombination;
import com.skynav.ttv.model.ttml2.ttd.ProfileType;
import com.skynav.ttv.model.ttml2.ttp.Extension;
import com.skynav.ttv.model.ttml2.ttp.Extensions;
import com.skynav.ttv.model.ttml2.ttp.Feature;
import com.skynav.ttv.model.ttml2.ttp.Features;
import com.skynav.ttv.model.ttml2.ttp.Profile;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.URIs;
import com.skynav.ttv.verifier.AbstractVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Profiles;

import static com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier.*;

public class TTML2ProfileVerifier extends AbstractVerifier implements ProfileVerifier {

    public static final QName contentProfilesAttributeName              = new QName(TTML1ParameterVerifier.NAMESPACE, "contentProfiles");
    public static final QName designatorAttributeName                   = new QName("", "designator");
    public static final QName processorProfilesAttributeName            = new QName(TTML1ParameterVerifier.NAMESPACE, "processorProfiles");
    public static final QName useAttributeName                          = new QName("", "use");
    private static final List<QName> allProfileAttributeNames;

    static {
        List<QName> names = new java.util.ArrayList<QName>();
        names.add(contentProfilesAttributeName);
        names.add(processorProfilesAttributeName);
        names.add(profileAttributeName);
        allProfileAttributeNames = names;
    }

    public TTML2ProfileVerifier(Model model) {
        super(model);
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        setState(content, context);
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

    protected boolean verify(TimedText content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        boolean specifiesSomeProfileAttribute = false;
        // @contentProfiles
        String contentProfilesAttribute = content.getContentProfiles();
        if ((contentProfilesAttribute != null) && !contentProfilesAttribute.isEmpty())
            specifiesSomeProfileAttribute = true;
        // @processorProfiles
        String processorProfilesAttribute = content.getProcessorProfiles();
        if ((processorProfilesAttribute != null) && !processorProfilesAttribute.isEmpty())
            specifiesSomeProfileAttribute = true;
        // @profile
        String profileAttribute = content.getProfile();
        if ((profileAttribute != null) && !profileAttribute.isEmpty())
            specifiesSomeProfileAttribute = true;
        // warn if profile attribute is ignored when internal profile (element) is present, unless the former references the latter
        List<Profile> profileElements = (content.getHead() != null) ? content.getHead().getParametersClass() : null;
        if ((profileElements != null) && (profileElements.size() > 0) && specifiesSomeProfileAttribute) {
            // [TBD] do not emit warning when a profile attribute supplies the profile and that profile is an internal profile
            if (reporter.isWarningEnabled("ignored-profile-attribute")) {
                Message message = reporter.message(locator, "*KEY*",
                    "When {0} element is present, the {1} attribute is ignored.", profileElementName, allProfileAttributeNames);
                if (reporter.logWarning(message))
                    failed = true;
            }
        }
        // warn if no profile attribute or internal profile is present
        if ((profileElements == null) || (profileElements.size() == 0)) {
            if (!specifiesSomeProfileAttribute) {
                if (reporter.isWarningEnabled("missing-profile")) {
                    Message message = reporter.message(locator, "*KEY*",
                        "No profile specified, expected either (1) one of the {1} attributes or (2) a {0} element.",
                        profileElementName, profileAttributeName);
                    if (reporter.logWarning(message))
                        failed = true;
                }
            }
        }
        return !failed;
    }

    protected boolean verify(Profile content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        // @combine     - schema validation only, but handle defaulting here
        ProfileCombination combine = content.getCombine();
        if (combine == null) {
            combine = ProfileCombination.IGNORE;        // N.B. spec currently say REPLACE, but should say IGNORE!!!
            content.setCombine(combine);
        }
        // @designator  - must be absolute
        String designator = content.getDesignator();
        if ((designator != null) && !URIs.isAbsolute(designator)) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Invalid {0} attribute, reference to non-absolute URI ''{1}'' not permitted", designatorAttributeName, designator));
            failed = true;
        }
        // @type        - schema validation only, but handle defaulting here
        ProfileType type = content.getType();
        if (type == null) {
            type = ProfileType.PROCESSOR;
            content.setType(type);
        }
        // @use         - must not be local fragment
        String use = content.getUse();
        if ((use != null) && URIs.isLocalFragment(use)) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Invalid {0} attribute, reference to local fragment ''{1}'' not permitted", useAttributeName, use));
            failed = true;
        }
        // check nesting constraints
        Object parent = context.getBindingElementParent(content);
        if (parent instanceof Profile) {
            ProfileType tThis   = content.getType();
            ProfileType tParent = ((Profile) parent).getType();
            if (tParent == null)
                tParent = type = ProfileType.PROCESSOR;
            if (tThis != tParent) {
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Invalid nested profile, this profile''s type ''{0}'' must match parent''s profile type ''{1}''", tThis.value(), tParent.value()));
                failed = true;
            }
        }
        return !failed;
    }

    protected boolean verify(Feature content, Locator locator, VerifierContext context) {
        boolean failed = false;
        Features features = (Features) context.getBindingElementParent(content);
        if (features != null) {
            String base = features.getBase();
            String designation = content.getValue();
            Location location = new Location(content, context.getBindingElementName(content), null, locator);
            if (base == null) {
                base = context.getModel().getTTFeatureNamespaceUri().toString();
                features.setBase(base);
            }
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
            if (base == null) {
                base = context.getModel().getTTExtensionNamespaceUri().toString();
                extensions.setBase(base);
            }
            if (!Profiles.isExtensionDesignation(designation, location, context, base)) {
                Profiles.badExtensionDesignation(designation, location, context, base);
                failed = true;
            }
        } else
            throw new IllegalStateException("missing parent");
        return !failed;
    }
}
