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

package com.skynav.ttv.verifier.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Profiles {

    public static boolean isFeatureDesignation(String value, Location location, VerifierContext context, String base) {
        try {
            URI featureUri = new URI(base).resolve(value);
            String uriString = featureUri.toString();
            String uriFragment = featureUri.getFragment();
            String featureNamespace = (uriFragment != null) ? uriString.substring(0, uriString.indexOf("#" + uriFragment)) : uriString;
            String ttFeatureNamespace = context.getModel().getTTFeatureNamespaceUri().toString();
            if (!featureNamespace.equals(ttFeatureNamespace))
                return false;
            else if (uriFragment == null)
                return false;
            else if (uriFragment.length() == 0)
                return false;
            else if (!context.getModel().isStandardFeatureDesignation(featureUri))
                return false;
            else
                return true;
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
            return false;
        }
    }

    public static void badFeatureDesignation(String value, Location location, VerifierContext context, String base) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Message message = null;
        try {
            URI featureUri = new URI(base).resolve(value);
            String uriString = featureUri.toString();
            String uriFragment = featureUri.getFragment();
            String featureNamespace = (uriFragment != null) ? uriString.substring(0, uriString.indexOf("#" + uriFragment)) : uriString;
            String ttFeatureNamespace = context.getModel().getTTFeatureNamespaceUri().toString();
            if (!featureNamespace.equals(ttFeatureNamespace)) {
                message = reporter.message(locator, "*KEY*",
                    "Unknown namespace in feature designation ''{0}'', expect TT Feature Namespace ''{1}''.", featureUri, ttFeatureNamespace);
            } else if (uriFragment == null) {
                message = reporter.message(locator, "*KEY*",
                    "Missing designation in feature designation ''{0}''.", featureUri);
            } else if (uriFragment.length() == 0) {
                message =reporter.message(locator, "*KEY*",
                    "Empty designation token in feature designation ''{0}''.", featureUri);
            } else if (!context.getModel().isStandardFeatureDesignation(featureUri)) {
                message =reporter.message(locator, "*KEY*",
                    "Unknown designation token in feature designation ''{0}''.", featureUri);
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
        }
        if (message != null)
            reporter.logInfo(message);
    }

    public static boolean isExtensionDesignation(String value, Location location, VerifierContext context, String base) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        try {
            URI extensionUri = new URI(base).resolve(value);
            String uriString = extensionUri.toString();
            String uriFragment = extensionUri.getFragment();
            String extensionNamespace = (uriFragment != null) ? uriString.substring(0, uriString.indexOf("#" + uriFragment)) : uriString;
            String ttExtensionNamespace = context.getModel().getTTExtensionNamespaceUri().toString();
            String modelExtensionNamespace = context.getModel().getExtensionNamespaceUri().toString();
            if (uriFragment == null)
                return false;
            else if (uriFragment.length() == 0)
                return false;
            else if (extensionNamespace.equals(ttExtensionNamespace))
                return context.getModel().isStandardExtensionDesignation(extensionUri);
            else if (extensionNamespace.equals(modelExtensionNamespace))
                return context.getModel().isStandardExtensionDesignation(extensionUri);
            else {
                if (reporter.isWarningEnabled("references-other-extension-namespace")) {
                    if (reporter.logWarning(reporter.message(locator, "*KEY*", "Other namespace in extension designation ''{0}''.", extensionUri)))
                        return false;
                }
                if (reporter.isWarningEnabled("references-non-standard-extension")) {
                    if (reporter.logWarning(reporter.message(locator, "*KEY*", "Non-standard extension designation ''{0}'' in an Other Extension Namespace.", extensionUri)))
                        return false;
                }
                return true;
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
            return false;
        }
    }

    public static void badExtensionDesignation(String value, Location location, VerifierContext context, String base) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Message message = null;
        try {
            URI extensionUri = new URI(base).resolve(value);
            String uriString = extensionUri.toString();
            String uriFragment = extensionUri.getFragment();
            String extensionNamespace = (uriFragment != null) ? uriString.substring(0, uriString.indexOf("#" + uriFragment)) : uriString;
            Model model = context.getModel();
            String ttExtensionNamespace = model.getTTExtensionNamespaceUri().toString();
            String modelExtensionNamespace = model.getExtensionNamespaceUri().toString();
            String modelName = model.getName();
            if (uriFragment == null) {
                message = reporter.message(locator, "*KEY*",
                        "Missing designation in extension designation ''{0}''.", extensionUri);
            } else if (uriFragment.length() == 0) {
                message = reporter.message(locator, "*KEY*",
                        "Empty designation token in extension designation ''{0}''.", extensionUri);
            } else if (extensionNamespace.equals(ttExtensionNamespace)) {
                if (!model.isStandardExtensionDesignation(extensionUri)) {
                    message = reporter.message(locator, "*KEY*",
                        "Unknown designation token in extension designation ''{0}'' in TT Extension Namespace.", extensionUri);
                }
            } else if (extensionNamespace.equals(modelExtensionNamespace)) {
                if (!model.isStandardExtensionDesignation(extensionUri)) {
                    message = reporter.message(locator, "*KEY*",
                        "Unknown designation token in extension designation ''{0}'' in Model ({1}) Extension Namespace.", extensionUri, modelName);
                }
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
        }
        if (message != null)
            reporter.logInfo(message);
    }

    public static boolean isProfileDesignator(String value, Location location, VerifierContext context, URI ttmlProfileNamespaceUri, Set<URI> designators) {
        try {
            URI uri = new URI(value);
            if (!uri.isAbsolute())
                uri = ttmlProfileNamespaceUri.resolve(uri);
            if (designators.contains(uri))
                return true;
            else if (uri.toString().indexOf(ttmlProfileNamespaceUri.toString()) == 0) {
                // error - unknown designator in TTML profile namespace
                return false;
            } else {
                Reporter reporter = context.getReporter();
                if (reporter.isWarningEnabled("references-non-standard-profile")) {
                    if (reporter.logWarning(reporter.message(location.getLocator(), "*KEY*", "Non-standard profile designator ''{0}''.", uri)))
                        return false;
                }
                return true;
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
            return false;
        }
    }

    public static void badProfileDesignator(String value, Location location, VerifierContext context, URI ttmlProfileNamespaceUri, Set<URI> designators) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        try {
            URI uri = new URI(value);
            if (!uri.isAbsolute())
                uri = ttmlProfileNamespaceUri.resolve(uri);
            if (!designators.contains(uri)) {
                if (uri.toString().indexOf(ttmlProfileNamespaceUri.toString()) == 0) {
                    reporter.logInfo(reporter.message(locator, "*KEY*", "Bad profile designator, unrecognized designator ''{0}'' in TT Profile Namespace.", value));
                } else {
                    reporter.logInfo(reporter.message(locator, "*KEY*", "Bad profile designator, unrecognized designator ''{0}'' in Other Profile Namespace.", value));
                }
            }
        } catch (URISyntaxException e) {
            // Phase 3 will have already reported that value doesn't correspond with xs:anyURI.
        }
    }

}
