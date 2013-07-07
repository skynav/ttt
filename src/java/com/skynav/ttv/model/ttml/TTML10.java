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

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.ttml10.tt.Layout;
import com.skynav.ttv.model.ttml10.tt.Region;
import com.skynav.ttv.model.ttml10.tt.Style;
import com.skynav.ttv.model.ttml10.tt.Styling;
import com.skynav.ttv.model.ttml10.ttm.Agent;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.xml.helpers.XML;

public class TTML10 {
    public static final Model MODEL = new TTML10Model();
    private static class TTML10Model implements Model {
        public String getName() {
            return "ttml10";
        }
        public String getSchemaResourceName() {
            return "xsd/ttml10/ttaf1-dfxp.xsd";
        }
        private static final URI namespaceUri;
        private static final URI profileNamespaceUri;
        private static final URI featureNamespaceUri;
        private static final URI extensionNamespaceUri;
        static {
            try {
                namespaceUri = new URI("http://www.w3.org/ns/ttml");
                profileNamespaceUri = new URI("http://www.w3.org/ns/ttml/profile/");
                featureNamespaceUri = new URI("http://www.w3.org/ns/ttml/feature/");
                extensionNamespaceUri = new URI("http://www.w3.org/ns/ttml/extension/");
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
        public URI getNamespaceUri() {
            return namespaceUri;
        }
        public URI getProfileNamespaceUri() {
            return profileNamespaceUri;
        }
        public URI getFeatureNamespaceUri() {
            return featureNamespaceUri;
        }
        public URI getExtensionNamespaceUri() {
            return extensionNamespaceUri;
        }
        private static final Map<URI,Class<?>> standardProfileSpecificationClasses;
        static {
            standardProfileSpecificationClasses = new java.util.HashMap<URI,Class<?>>();
            standardProfileSpecificationClasses.put(profileNamespaceUri.resolve("dfxp-transformation"), TTML10TransformationProfileSpecification.class);
            standardProfileSpecificationClasses.put(profileNamespaceUri.resolve("dfxp-presentation"), TTML10PresentationProfileSpecification.class);
            standardProfileSpecificationClasses.put(profileNamespaceUri.resolve("dfxp-full"), TTML10FullProfileSpecification.class);
            standardProfileSpecificationClasses.put(profileNamespaceUri.resolve("sdp-us"), TTML10SDPUSProfileSpecification.class);
        }
        public Set<URI> getStandardProfileURIs() {
            return standardProfileSpecificationClasses.keySet();
        }
        private static final Map<URI,Profile.Specification> standardProfileSpecifications = new java.util.HashMap<URI,Profile.Specification>();
        private static final Class<?>[] profileSpecificationConstructorParameterTypes = new Class<?>[] { URI.class, URI.class, URI.class };
        public Profile.Specification getStandardProfileSpecification(URI uri) {
            if (standardProfileSpecifications.containsKey(uri))
                return standardProfileSpecifications.get(uri);
            else if (!standardProfileSpecificationClasses.containsKey(uri))
                return null;
            else {
                Profile.Specification ps = createProfileSpecification(standardProfileSpecificationClasses.get(uri), uri);
                standardProfileSpecifications.put(uri, ps);
                return ps;
            }
        }
        private Profile.Specification createProfileSpecification(Class<?> psc, URI uri) {
            try {
                Object[] parameters = new Object[] { uri, getFeatureNamespaceUri(), getExtensionNamespaceUri() };
                return (Profile.Specification) psc.getDeclaredConstructor(profileSpecificationConstructorParameterTypes).newInstance(parameters);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        Profile.StandardDesignations standardDesignations;
        public boolean isStandardFeatureDesignation(URI uri) {
            if (standardDesignations == null)
                standardDesignations = TTML10StandardDesignations.getInstance(getFeatureNamespaceUri(), getExtensionNamespaceUri());
            return standardDesignations.isStandardFeatureDesignation(uri);
        }
        public boolean isStandardExtensionDesignation(URI uri) {
            if (standardDesignations == null)
                standardDesignations = TTML10StandardDesignations.getInstance(getFeatureNamespaceUri(), getExtensionNamespaceUri());
            return standardDesignations.isStandardExtensionDesignation(uri);
        }
        public String getJAXBContextPath() {
            return "com.skynav.ttv.model.ttml10.tt:com.skynav.ttv.model.ttml10.ttm:com.skynav.ttv.model.ttml10.ttp";
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
            rootClasses.put(com.skynav.ttv.model.ttml10.tt.TimedText.class, "createTt");
            rootClasses.put(com.skynav.ttv.model.ttml10.ttp.Profile.class, "createProfile");
        }
        public Map<Class<?>,String> getRootClasses() {
            return rootClasses;
        }
        private static final QName agentElementName = new com.skynav.ttv.model.ttml10.ttm.ObjectFactory().createAgent(new Agent()).getName();
        private static final QName regionElementName = new com.skynav.ttv.model.ttml10.tt.ObjectFactory().createRegion(new Region()).getName();
        private static final QName styleElementName = new com.skynav.ttv.model.ttml10.tt.ObjectFactory().createStyle(new Style()).getName();
        private static final String metadataNamespaceUri = com.skynav.ttv.verifier.ttml.TTML10MetadataVerifier.getMetadataNamespaceUri();
        public QName getIdReferenceTargetName(QName attributeName) {
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if ((namespaceUri == null) || (namespaceUri.length() == 0)) {
                if (localName.equals("agent"))
                    return agentElementName;
                else if (localName.equals("region"))
                    return regionElementName;
                else if (localName.equals("style"))
                    return styleElementName;
            } else if (namespaceUri.equals(metadataNamespaceUri)) {
                if (localName.equals("agent"))
                    return agentElementName;
            }
            return null;
        }
        public Class<?> getIdReferenceTargetClass(QName attributeName) {
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if ((namespaceUri == null) || (namespaceUri.length() == 0)) {
                if (localName.equals("agent"))
                    return Agent.class;
                else if (localName.equals("region"))
                    return Region.class;
                else if (localName.equals("style"))
                    return Style.class;
            } else if (namespaceUri.equals(metadataNamespaceUri)) {
                if (localName.equals("agent"))
                    return Agent.class;
            }
            return Object.class;
        }
        private static final QName stylingElementName = new com.skynav.ttv.model.ttml10.tt.ObjectFactory().createStyling(new Styling()).getName();
        private static final QName layoutElementName = new com.skynav.ttv.model.ttml10.tt.ObjectFactory().createLayout(new Layout()).getName();
        public Set<QName> getIdReferenceAncestorNames(QName attributeName) {
            Set<QName> ancestorNames = new java.util.HashSet<QName>();
            String namespaceUri = attributeName.getNamespaceURI();
            String localName = attributeName.getLocalPart();
            if ((namespaceUri == null) || (namespaceUri.length() == 0)) {
                if (localName.equals("style"))
                    ancestorNames.add(stylingElementName);
                else if (localName.equals("region"))
                    ancestorNames.add(layoutElementName);
            }
            return (ancestorNames.size() > 0) ? ancestorNames : null;
        }
        private SemanticsVerifier semanticsVerifier;
        public SemanticsVerifier getSemanticsVerifier() {
            synchronized (this) {
                if (semanticsVerifier == null) {
                    semanticsVerifier = new com.skynav.ttv.verifier.ttml.TTML10SemanticsVerifier(this);
                }
            }
            return semanticsVerifier;
        }
        private ParameterVerifier parameterVerifier;
        public ParameterVerifier getParameterVerifier() {
            synchronized (this) {
                if (parameterVerifier == null) {
                    parameterVerifier = new com.skynav.ttv.verifier.ttml.TTML10ParameterVerifier(this);
                }
            }
            return parameterVerifier;
        }
        private ProfileVerifier profileVerifier;
        public ProfileVerifier getProfileVerifier() {
            synchronized (this) {
                if (profileVerifier == null) {
                    profileVerifier = new com.skynav.ttv.verifier.ttml.TTML10ProfileVerifier(this);
                }
            }
            return profileVerifier;
        }
        private StyleVerifier styleVerifier;
        public StyleVerifier getStyleVerifier() {
            synchronized (this) {
                if (styleVerifier == null) {
                    styleVerifier = new com.skynav.ttv.verifier.ttml.TTML10StyleVerifier(this);
                }
            }
            return styleVerifier;
        }
        private TimingVerifier timingVerifier;
        public TimingVerifier getTimingVerifier() {
            synchronized (this) {
                if (timingVerifier == null) {
                    timingVerifier = new com.skynav.ttv.verifier.ttml.TTML10TimingVerifier(this);
                }
            }
            return timingVerifier;
        }
        private MetadataVerifier metadataVerifier;
        public MetadataVerifier getMetadataVerifier() {
            synchronized (this) {
                if (metadataVerifier == null) {
                    metadataVerifier = new com.skynav.ttv.verifier.ttml.TTML10MetadataVerifier(this);
                }
            }
            return metadataVerifier;
        }
    }
}
