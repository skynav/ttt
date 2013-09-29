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
 
package com.skynav.ttv.model.ttml;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.AbstractModel;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Layout;
import com.skynav.ttv.model.ttml1.tt.Metadata;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Style;
import com.skynav.ttv.model.ttml1.tt.Styling;
import com.skynav.ttv.model.ttml1.ttm.Agent;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.ttml.TTML1MetadataVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ParameterVerifier;
import com.skynav.ttv.verifier.ttml.TTML1ProfileVerifier;
import com.skynav.ttv.verifier.ttml.TTML1SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML1StyleVerifier;
import com.skynav.ttv.verifier.ttml.TTML1TimingVerifier;
import com.skynav.xml.helpers.XML;


public class TTML1 {

    public static class Constants {

        public static final String NAMESPACE_PREFIX = "http://www.w3.org/ns/ttml";

        public static final String NAMESPACE_TT = "http://www.w3.org/ns/ttml";
        public static final String NAMESPACE_TT_METADATA = "http://www.w3.org/ns/ttml#metadata";
        public static final String NAMESPACE_TT_PARAMETER = "http://www.w3.org/ns/ttml#parameter";
        public static final String NAMESPACE_TT_STYLE = "http://www.w3.org/ns/ttml#styling";
        public static final String NAMESPACE_TT_PROFILE = "http://www.w3.org/ns/ttml/profile/";
        public static final String NAMESPACE_TT_FEATURE = "http://www.w3.org/ns/ttml/feature/";
        public static final String NAMESPACE_TT_EXTENSION = "http://www.w3.org/ns/ttml/extension/";
        public static final String XSD_TT = "xsd/ttml1/ttml1.xsd";

        public static final String PROFILE_TT_PRESENTATION = "dfxp-presentation";
        public static final String PROFILE_TT_TRANSFORMATION = "dfxp-transformation";
        public static final String PROFILE_TT_FULL = "dfxp-full";

        public static final String PROFILE_TT_PRESENTATION_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TT_PRESENTATION;
        public static final String PROFILE_TT_TRANSFORMATION_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TT_TRANSFORMATION;
        public static final String PROFILE_TT_FULL_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TT_FULL;

        public static final String ATTR_AGENT = "agent";
        public static final String ATTR_REGION = "region";
        public static final String ATTR_STYLE = "style";

    }

    public static final String MODEL_NAME = "ttml1";
    public static final Model MODEL = new TTML1Model();
    public static class TTML1Model extends AbstractModel {
        private String[] schemaResourceNames;
        private URI[] namespaceURIs;
        private URI profileNamespaceUri;
        private URI featureNamespaceUri;
        private URI extensionNamespaceUri;
        protected TTML1Model() {
            populate();
        }
        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }
        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.add(Constants.XSD_TT);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }
        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            try {
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT));
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT_METADATA));
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT_PARAMETER));
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT_STYLE));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_TT_PROFILE);
                this.featureNamespaceUri = new URI(Constants.NAMESPACE_TT_FEATURE);
                this.extensionNamespaceUri = new URI(Constants.NAMESPACE_TT_EXTENSION);
            } catch (URISyntaxException e) {
            }
        }
        public String getName() {
            return MODEL_NAME;
        }
        public String[] getTTSchemaResourceNames() {
            return schemaResourceNames;
        }
        public String[] getSchemaResourceNames() {
            return getTTSchemaResourceNames();
        }
        public URI[] getTTNamespaceURIs() {
            return namespaceURIs;
        }
        public URI[] getNamespaceURIs() {
            return getTTNamespaceURIs();
        }
        public final URI getTTProfileNamespaceUri() {
            return profileNamespaceUri;
        }
        public URI getProfileNamespaceUri() {
            return getTTProfileNamespaceUri();
        }
        public final URI getTTFeatureNamespaceUri() {
            return featureNamespaceUri;
        }
        public URI getFeatureNamespaceUri() {
            return getTTFeatureNamespaceUri();
        }
        public final URI getTTExtensionNamespaceUri() {
            return extensionNamespaceUri;
        }
        public URI getExtensionNamespaceUri() {
            return getTTExtensionNamespaceUri();
        }
        private static Map<URI,Class<?>> profileSpecificationClasses;
        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>();
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_TT_TRANSFORMATION), TTML1TransformationProfileSpecification.class);
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_TT_PRESENTATION), TTML1PresentationProfileSpecification.class);
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_TT_FULL), TTML1FullProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }
        public Set<URI> getProfileDesignators() {
            return getProfileSpecificationClasses().keySet();
        }
        private static final Map<URI,Profile.Specification> profileSpecifications = new java.util.HashMap<URI,Profile.Specification>();
        public Profile.Specification getProfileSpecification(URI uri) {
            if (profileSpecifications.containsKey(uri))
                return profileSpecifications.get(uri);
            else if (!getProfileSpecificationClasses().containsKey(uri))
                return null;
            else {
                Profile.Specification ps = createProfileSpecification(getProfileSpecificationClasses().get(uri), uri);
                profileSpecifications.put(uri, ps);
                return ps;
            }
        }
        private static final Class<?>[] profileSpecificationConstructorParameterTypes = new Class<?>[] { URI.class };
        protected Profile.Specification createProfileSpecification(Class<?> psc, URI uri) {
            try {
                Object[] parameters = new Object[] { uri };
                return (Profile.Specification) psc.getDeclaredConstructor(profileSpecificationConstructorParameterTypes).newInstance(parameters);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Profile.StandardDesignations standardDesignations;
        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null)
                standardDesignations = TTML1StandardDesignations.getInstance();
            return standardDesignations;
        }
        public boolean isStandardFeatureDesignation(URI uri) {
            return getStandardDesignations().isStandardFeatureDesignation(uri);
        }
        public boolean isStandardExtensionDesignation(URI uri) {
            return getStandardDesignations().isStandardExtensionDesignation(uri);
        }
        public String getJAXBContextPath() {
            return "com.skynav.ttv.model.ttml1.tt:com.skynav.ttv.model.ttml1.ttm:com.skynav.ttv.model.ttml1.ttp";
        }
        private static final List<QName> idAttributes;
        static {
            idAttributes = new java.util.ArrayList<QName>();
            idAttributes.add(XML.getIdAttributeName());
        }
        public List<QName> getIdAttributes() {
            return idAttributes;
        }
        private static final Map<Class<?>,String> rootClasses;
        static {
            rootClasses = new java.util.HashMap<Class<?>,String>();
            rootClasses.put(com.skynav.ttv.model.ttml1.tt.TimedText.class, "createTt");
            rootClasses.put(com.skynav.ttv.model.ttml1.ttp.Profile.class, "createProfile");
        }
        public Map<Class<?>,String> getRootClasses() {
            return rootClasses;
        }
        protected static final QName agentElementName = new com.skynav.ttv.model.ttml1.ttm.ObjectFactory().createAgent(new Agent()).getName();
        protected static final QName regionElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createRegion(new Region()).getName();
        protected static final QName styleElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createStyle(new Style()).getName();
        private static final String metadataNamespaceUri = com.skynav.ttv.verifier.ttml.TTML1MetadataVerifier.getMetadataNamespaceUri();
        public QName getIdReferenceTargetName(QName attributeName) {
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if (isEmptyNamespace(namespaceUri)) {
                if (localName.equals(Constants.ATTR_AGENT))
                    return agentElementName;
                else if (localName.equals(Constants.ATTR_REGION))
                    return regionElementName;
                else if (localName.equals(Constants.ATTR_STYLE))
                    return styleElementName;
            } else if (namespaceUri.equals(metadataNamespaceUri)) {
                if (localName.equals(Constants.ATTR_AGENT))
                    return agentElementName;
            }
            return null;
        }
        public Class<?> getIdReferenceTargetClass(QName attributeName) {
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if (isEmptyNamespace(namespaceUri)) {
                if (localName.equals(Constants.ATTR_AGENT))
                    return Agent.class;
                else if (localName.equals(Constants.ATTR_REGION))
                    return Region.class;
                else if (localName.equals(Constants.ATTR_STYLE))
                    return Style.class;
            } else if (namespaceUri.equals(metadataNamespaceUri)) {
                if (localName.equals(Constants.ATTR_AGENT))
                    return Agent.class;
            }
            return Object.class;
        }
        protected static final QName headElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createHead(new Head()).getName();
        protected static final QName metadataElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createMetadata(new Metadata()).getName();
        protected static final QName stylingElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createStyling(new Styling()).getName();
        protected static final QName layoutElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createLayout(new Layout()).getName();
        public List<List<QName>> getIdReferencePermissibleAncestors(QName attributeName) {
            List<List<QName>> permissibleAncestors = new java.util.ArrayList<List<QName>>();
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if (localName.equals(Constants.ATTR_STYLE) && isEmptyNamespace(namespaceUri)) {
                List<QName> ancestors = new java.util.ArrayList<QName>();
                ancestors.add(stylingElementName);
                ancestors.add(headElementName);
                permissibleAncestors.add(ancestors);
            } else if (localName.equals(Constants.ATTR_REGION) && isEmptyNamespace(namespaceUri)) {
                List<QName> ancestors = new java.util.ArrayList<QName>();
                ancestors.add(layoutElementName);
                ancestors.add(headElementName);
                permissibleAncestors.add(ancestors);
            } else if (localName.equals(Constants.ATTR_AGENT) && (isEmptyNamespace(namespaceUri) || namespaceUri.equals(metadataNamespaceUri))) {
                List<QName> ancestors1 = new java.util.ArrayList<QName>();
                ancestors1.add(metadataElementName);
                ancestors1.add(headElementName);
                permissibleAncestors.add(ancestors1);
                List<QName> ancestors2 = new java.util.ArrayList<QName>();
                ancestors2.add(headElementName);
                permissibleAncestors.add(ancestors2);
            }
            return (permissibleAncestors.size() > 0) ? permissibleAncestors : null;
        }
        private boolean isEmptyNamespace(String namespaceUri) {
            return (namespaceUri == null) || (namespaceUri.length() == 0);
        }
        protected static final QName divElementName = new com.skynav.ttv.model.ttml1.tt.ObjectFactory().createDiv(new Division()).getName();
        protected boolean isTTDivElement(QName name) {
            return name.equals(divElementName);
        }
        private SemanticsVerifier semanticsVerifier;
        public SemanticsVerifier getSemanticsVerifier() {
            synchronized (this) {
                if (semanticsVerifier == null) {
                    semanticsVerifier = new TTML1SemanticsVerifier(this);
                }
            }
            return semanticsVerifier;
        }
        private ParameterVerifier parameterVerifier;
        public ParameterVerifier getParameterVerifier() {
            synchronized (this) {
                if (parameterVerifier == null) {
                    parameterVerifier = new TTML1ParameterVerifier(this);
                }
            }
            return parameterVerifier;
        }
        private ProfileVerifier profileVerifier;
        public ProfileVerifier getProfileVerifier() {
            synchronized (this) {
                if (profileVerifier == null) {
                    profileVerifier = new TTML1ProfileVerifier(this);
                }
            }
            return profileVerifier;
        }
        private StyleVerifier styleVerifier;
        public StyleVerifier getStyleVerifier() {
            synchronized (this) {
                if (styleVerifier == null) {
                    styleVerifier = new TTML1StyleVerifier(this);
                }
            }
            return styleVerifier;
        }
        private TimingVerifier timingVerifier;
        public TimingVerifier getTimingVerifier() {
            synchronized (this) {
                if (timingVerifier == null) {
                    timingVerifier = new TTML1TimingVerifier(this);
                }
            }
            return timingVerifier;
        }
        private MetadataVerifier metadataVerifier;
        public MetadataVerifier getMetadataVerifier() {
            synchronized (this) {
                if (metadataVerifier == null) {
                    metadataVerifier = new TTML1MetadataVerifier(this);
                }
            }
            return metadataVerifier;
        }
    }
}
