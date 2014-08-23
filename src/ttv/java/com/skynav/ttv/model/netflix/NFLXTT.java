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
 
package com.skynav.ttv.model.netflix;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.smpte.ST20522010.ST20522010Model;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.netflix.NFLXTTParameterVerifier;
import com.skynav.ttv.verifier.netflix.NFLXTTSemanticsVerifier;
import com.skynav.ttv.verifier.netflix.NFLXTTStyleVerifier;

public class NFLXTT {

    public static class Constants {
        public static final String NAMESPACE_PROFILE = "http://www.netflix.com/ns/ttml/profile/";

        public static final String PROFILE_CC = "nflx-cc";
        public static final String PROFILE_SDH = "nflx-sdh";

        public static final String PROFILE_CC_ABSOLUTE = NAMESPACE_PROFILE + PROFILE_CC;
        public static final String PROFILE_SDH_ABSOLUTE = NAMESPACE_PROFILE + PROFILE_SDH;

        public static final String CHARSET_REQUIRED = "UTF-8";
    }

    public static final String MODEL_NAME = "nflxtt";
    public static final Model MODEL = new NFLXTTModel();

    public static class NFLXTTModel extends ST20522010Model {
        private String[] schemaResourceNames;
        private URI[] namespaceURIs;
        private URI profileNamespaceUri;
        NFLXTTModel() {
            populate();
        }
        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }
        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getST20522010SchemaResourceNames()));
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }
        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            namespaceURIs.addAll(Arrays.asList(super.getST20522010NamespaceURIs()));
            try {
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_PROFILE);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        public String getName() {
            return MODEL_NAME;
        }
        public String[] getSchemaResourceNames() {
            return this.schemaResourceNames;
        }
        public URI[] getNamespaceURIs() {
            return this.namespaceURIs;
        }
        public URI getProfileNamespaceUri() {
            return this.profileNamespaceUri;
        }
        public URI getExtensionNamespaceUri() {
            return super.getExtensionNamespaceUri();
        }
        private static Map<URI,Class<?>> profileSpecificationClasses;
        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>();
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_CC), NFLXCCProfileSpecification.class);
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_SDH), NFLXSDHProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }
        public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
            if (!super.isGlobalAttributePermitted(attributeName, elementName))
                return false;
            else {
                String ln = attributeName.getLocalPart();
                if (ST20522010.inSMPTEPrimaryNamespace(attributeName)) {
                    if (ln.equals(ST20522010.Constants.ATTR_BACKGROUND_IMAGE))
                        return false;
                    else if (ln.equals(ST20522010.Constants.ATTR_BACKGROUND_IMAGE_HORIZONTAL))
                        return false;
                    else if (ln.equals(ST20522010.Constants.ATTR_BACKGROUND_IMAGE_VERTICAL))
                        return false;
                }
            }
            return true;
        }
        private SemanticsVerifier semanticsVerifier;
        public SemanticsVerifier getSemanticsVerifier() {
            synchronized (this) {
                if (semanticsVerifier == null) {
                    semanticsVerifier = new NFLXTTSemanticsVerifier(this);
                }
            }
            return semanticsVerifier;
        }
        private ParameterVerifier parameterVerifier;
        public ParameterVerifier getParameterVerifier() {
            synchronized (this) {
                if (parameterVerifier == null) {
                    parameterVerifier = new NFLXTTParameterVerifier(this);
                }
            }
            return parameterVerifier;
        }
        private StyleVerifier styleVerifier;
        public StyleVerifier getStyleVerifier() {
            synchronized (this) {
                if (styleVerifier == null) {
                    styleVerifier = new NFLXTTStyleVerifier(this);
                }
            }
            return styleVerifier;
        }
    }
}
