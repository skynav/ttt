/*
 * Copyright 2013-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.model.smpte;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.smpte.tt.rel2010.Information;
import com.skynav.ttv.model.ttml.TTML2.TTML2Model;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010TTML2ParameterVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010TTML2SemanticsVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010TTML2StyleVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010TTML2TimingVerifier;

public class ST20522010TTML2 {

    public static class Constants extends ST20522010TTML1.Constants {
    }

    public static final String MODEL_NAME = "st2052-2010-ttml2";

    public static boolean inSMPTEPrimaryNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_2010);
    }

    public static boolean inSMPTESecondaryNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_2010_CEA608);
    }

    public static boolean inSMPTENamespace(QName name) {
        return inSMPTEPrimaryNamespace(name) || inSMPTESecondaryNamespace(name);
    }

    public static class ST20522010TTML2Model extends TTML2Model {

        private static final QName informationElementName = new com.skynav.ttv.model.smpte.tt.rel2010.ObjectFactory().createInformation(new Information()).getName();
        private static final String[] contextPaths = new String[] { "com.skynav.ttv.model.smpte.tt.rel2010" };

        private String[] schemaResourceNames;
        private URI[] namespaceURIs;
        private URI profileNamespaceUri;
        private URI extensionNamespaceUri;
        private Map<URI,Class<?>> profileSpecificationClasses;
        private Profile.StandardDesignations standardDesignations;
        private ParameterVerifier parameterVerifier;
        private SemanticsVerifier semanticsVerifier;
        private StyleVerifier styleVerifier;
        private TimingVerifier timingVerifier;

        public ST20522010TTML2Model() {
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getTTSchemaResourceNames()));
            resourceNames.add(Constants.XSD_2010);
            resourceNames.add(Constants.XSD_2010_CEA608);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            namespaceURIs.addAll(Arrays.asList(super.getTTNamespaceURIs()));
            try {
                namespaceURIs.add(new URI(Constants.NAMESPACE_2010));
                namespaceURIs.add(new URI(Constants.NAMESPACE_2010_CEA608));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_2010_PROFILE);
                this.extensionNamespaceUri = new URI(Constants.NAMESPACE_2010_EXTENSION);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return MODEL_NAME;
        }

        public String[] getST20522010SchemaResourceNames() {
            return this.schemaResourceNames;
        }

        public String[] getSchemaResourceNames() {
            return getST20522010SchemaResourceNames();
        }

        public URI[] getST20522010NamespaceURIs() {
            return this.namespaceURIs;
        }

        public URI[] getNamespaceURIs() {
            return getST20522010NamespaceURIs();
        }

        public URI getProfileNamespaceUri() {
            return this.profileNamespaceUri;
        }

        public URI getExtensionNamespaceUri() {
            return this.extensionNamespaceUri;
        }

        public Map<String,String> getNormalizedPrefixes() {
            Map<String,String> normalizedPrefixes = super.getNormalizedPrefixes();
            normalizedPrefixes.put(Constants.NAMESPACE_2010, "smpte");
            return normalizedPrefixes;
        }

        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>(super.getProfileSpecificationClasses());
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_2010_FULL), ST20522010TTML2FullProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }

        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null) {
                standardDesignations = ST20522010TTML2StandardDesignations.getInstance();
            }
            return standardDesignations;
        }

        public boolean isGlobalAttribute(QName name) {
            if (super.isGlobalAttribute(name))
                return true;
            else
                return ST20522010TTML1.ST20522010TTML1Model.isGlobalAttributeHelper(name);
        }

        public boolean isSMPTEInformationElement(QName name) {
            return name.equals(informationElementName);
        }

        public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
            if (super.isGlobalAttributePermitted(attributeName, elementName))
                return true;
            else
                return ST20522010TTML1.ST20522010TTML1Model.isGlobalAttributePermittedHelper(attributeName, elementName);
        }

        public boolean isElement(QName name) {
            if (super.isElement(name))
                return true;
            else
                return ST20522010TTML1.ST20522010TTML1Model.isElementHelper(name);
        }

        public String getJAXBContextPath() {
            StringBuffer sb = new StringBuffer(super.getJAXBContextPath());
            for (String path: contextPaths) {
                sb.append(':');
                sb.append(path);
            }
            return sb.toString();
        }

        public List<List<QName>> getElementPermissibleAncestors(QName elementName) {
            List<List<QName>> permissibleAncestors = super.getElementPermissibleAncestors(elementName);
            if (permissibleAncestors == null)
                permissibleAncestors =
                    ST20522010TTML1.ST20522010TTML1Model.getElementPermissibleAncestorsHelper(new java.util.ArrayList<List<QName>>(), elementName);
            return (permissibleAncestors.size() > 0) ? permissibleAncestors : null;
        }

        public ParameterVerifier getParameterVerifier() {
            if (parameterVerifier == null) {
                parameterVerifier = new ST20522010TTML2ParameterVerifier(this);
            }
            return parameterVerifier;
        }

        public SemanticsVerifier getSemanticsVerifier() {
            if (semanticsVerifier == null) {
                semanticsVerifier = new ST20522010TTML2SemanticsVerifier(this);
            }
            return semanticsVerifier;
        }

        public StyleVerifier getStyleVerifier() {
            if (styleVerifier == null) {
                styleVerifier = new ST20522010TTML2StyleVerifier(this);
            }
            return styleVerifier;
        }

        public TimingVerifier getTimingVerifier() {
            if (timingVerifier == null) {
                timingVerifier = new ST20522010TTML2TimingVerifier(this);
            }
            return timingVerifier;
        }

    }

}
