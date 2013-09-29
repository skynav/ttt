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
 
package com.skynav.ttv.model.smpte;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.smpte.tt.rel2013.Information;
import com.skynav.ttv.model.smpte.tt.rel2013.m708.Service;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.smpte.ST20522013SemanticsVerifier;

public class ST20522013 {

    public static class Constants extends ST20522010.Constants {

        public static final String MODEL_NAME = "st2052-2013";

        public static final String NAMESPACE_2013 = "http://www.smpte-ra.org/schemas/2052-1/2013/smpte-tt";
        public static final String NAMESPACE_2013_PROFILE = "http://www.smpte-ra.org/schemas/2052-1/2013/profiles/";
        public static final String NAMESPACE_2013_EXTENSION = "http://www.smpte-ra.org/23b/smpte-tt/extension/";
        public static final String XSD_2013 = "xsd/smpte/2013/smpte-tt.xsd";

        public static final String NAMESPACE_2013_CEA608 = "http://www.smpte-ra.org/schemas/2052-1/2013/smpte-tt#cea608";
        public static final String XSD_2013_CEA608 = "xsd/smpte/2013/smpte-tt-608.xsd";

        public static final String NAMESPACE_2013_CEA708 = "http://www.smpte-ra.org/schemas/2052-1/2013/smpte-tt#cea708";
        public static final String XSD_2013_CEA708 = "xsd/smpte/2013/smpte-tt-708.xsd";

        public static final String PROFILE_2013_FULL = "smpte-tt-full";
        public static final String PROFILE_2013_FULL_ABSOLUTE = NAMESPACE_2013_PROFILE + PROFILE_2010_FULL;

        public static final String DATA_TYPE_608 = NAMESPACE_2013_CEA608;
        public static final String DATA_TYPE_708 = NAMESPACE_2013_CEA708;

        public static final String ELT_SERVICE = "service";

        public static final String ATTR_ASPECT_RATIO = "aspectRatio";
        public static final String ATTR_EASY_READER = "easyReader";
        public static final String ATTR_FCC_MINIMUM = "fccMinimum";
        public static final String ATTR_NUMBER = "number";

    }

    public static final String MODEL_NAME = "st2052-2013";
    public static final Model MODEL = new ST20522013Model();

    public static boolean inSMPTEPrimaryNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_2013);
    }

    public static boolean inSMPTESecondaryNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_2013_CEA608) || nsUri.equals(Constants.NAMESPACE_2013_CEA708);
    }

    public static boolean inSMPTENamespace(QName name) {
        return inSMPTEPrimaryNamespace(name) || inSMPTESecondaryNamespace(name);
    }

    public static class ST20522013Model extends ST20522010.ST20522010Model {
        private String[] schemaResourceNames;
        private URI[] namespaceURIs;
        private URI profileNamespaceUri;
        private URI extensionNamespaceUri;
        ST20522013Model() {
            populate();
        }
        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }
        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getTTSchemaResourceNames()));
            resourceNames.add(Constants.XSD_2013);
            resourceNames.add(Constants.XSD_2013_CEA608);
            resourceNames.add(Constants.XSD_2013_CEA708);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }
        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            namespaceURIs.addAll(Arrays.asList(super.getTTNamespaceURIs()));
            try {
                namespaceURIs.add(new URI(Constants.NAMESPACE_2013));
                namespaceURIs.add(new URI(Constants.NAMESPACE_2013_CEA608));
                namespaceURIs.add(new URI(Constants.NAMESPACE_2013_CEA708));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_2013_PROFILE);
                this.extensionNamespaceUri = new URI(Constants.NAMESPACE_2013_EXTENSION);
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
            return this.extensionNamespaceUri;
        }
        private static Map<URI,Class<?>> profileSpecificationClasses;
        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>(super.getProfileSpecificationClasses());
                profileSpecificationClasses.put(profileNamespaceUri.resolve(Constants.PROFILE_2013_FULL), ST20522013FullProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }
        Profile.StandardDesignations standardDesignations;
        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null) {
                standardDesignations = ST20522013StandardDesignations.getInstance();
            }
            return standardDesignations;
        }
        public boolean isGlobalAttribute(QName name) {
            if (super.isGlobalAttribute(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inSMPTEPrimaryNamespace(name)) {
                    if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE))
                        return true;
                    else if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE_HORIZONTAL))
                        return true;
                    else if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE_VERTICAL))
                        return true;
                } else if (inSMPTESecondaryNamespace(name)) {
                    String nsUri = name.getNamespaceURI();
                    if (nsUri.equals(Constants.NAMESPACE_2013_CEA608)) {
                        if (ln.equals(Constants.ATTR_CHANNEL))
                            return true;
                        else if (ln.equals(Constants.ATTR_FIELD_START))
                            return true;
                        else if (ln.equals(Constants.ATTR_PROGRAM_NAME))
                            return true;
                        else if (ln.equals(Constants.ATTR_PROGRAM_TYPE))
                            return true;
                        else if (ln.equals(Constants.ATTR_CONTENT_ADVISORY))
                            return true;
                        else if (ln.equals(Constants.ATTR_CAPTION_SERVICE))
                            return true;
                        else if (ln.equals(Constants.ATTR_COPY_AND_REDISTRIBUTION_CONTROL))
                            return true;
                    } else if (nsUri.equals(Constants.NAMESPACE_2013_CEA708)) {
                        if (ln.equals(Constants.ATTR_NUMBER))
                            return true;
                        else if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                            return true;
                        else if (ln.equals(Constants.ATTR_EASY_READER))
                            return true;
                        else if (ln.equals(Constants.ATTR_FCC_MINIMUM))
                            return true;
                    }
                }
            }
            return false;
        }
        private static final QName informationElementName = new com.skynav.ttv.model.smpte.tt.rel2013.ObjectFactory().createInformation(new Information()).getName();
        public boolean isSMPTEInformationElement(QName name) {
            return name.equals(informationElementName);
        }
        private static final QName serviceElementName = new com.skynav.ttv.model.smpte.tt.rel2013.m708.ObjectFactory().createService(new Service()).getName();
        public boolean isSMPTEServiceElement(QName name) {
            return name.equals(serviceElementName);
        }
        public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
            if (super.isGlobalAttributePermitted(attributeName, elementName))
                return true;
            else {
                String ln = attributeName.getLocalPart();
                if (inSMPTEPrimaryNamespace(attributeName)) {
                    if (isTTDivElement(elementName)) {
                        if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE))
                            return true;
                        else if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE_HORIZONTAL))
                            return true;
                        else if (ln.equals(Constants.ATTR_BACKGROUND_IMAGE_VERTICAL))
                            return true;
                    }
                } else if (inSMPTESecondaryNamespace(attributeName)) {
                    String nsUri = attributeName.getNamespaceURI();
                    if (nsUri.equals(Constants.NAMESPACE_2013_CEA608)) {
                        if (isSMPTEInformationElement(elementName)) {
                            if (ln.equals(Constants.ATTR_CHANNEL))
                                return true;
                            else if (ln.equals(Constants.ATTR_FIELD_START))
                                return true;
                            else if (ln.equals(Constants.ATTR_PROGRAM_NAME))
                                return true;
                            else if (ln.equals(Constants.ATTR_PROGRAM_TYPE))
                                return true;
                            else if (ln.equals(Constants.ATTR_CONTENT_ADVISORY))
                                return true;
                            else if (ln.equals(Constants.ATTR_CAPTION_SERVICE))
                                return true;
                            else if (ln.equals(Constants.ATTR_COPY_AND_REDISTRIBUTION_CONTROL))
                                return true;
                        }
                    } else if (nsUri.equals(Constants.NAMESPACE_2013_CEA708)) {
                        if (isSMPTEInformationElement(elementName) || isSMPTEServiceElement(elementName) ) {
                            if (ln.equals(Constants.ATTR_NUMBER))
                                return true;
                            else if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                                return true;
                            else if (ln.equals(Constants.ATTR_EASY_READER))
                                return true;
                            else if (ln.equals(Constants.ATTR_FCC_MINIMUM))
                                return true;
                        }
                    }
                }
            }
            return false;
        }
        public boolean isElement(QName name) {
            if (super.isElement(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inSMPTEPrimaryNamespace(name)) {
                    if (ln.equals(Constants.ELT_DATA))
                        return true;
                    else if (ln.equals(Constants.ELT_IMAGE))
                        return true;
                    else if (ln.equals(Constants.ELT_INFORMATION))
                        return true;
                } else if (name.getNamespaceURI().equals(Constants.NAMESPACE_2013_CEA708)) {
                    if (ln.equals(Constants.ELT_SERVICE))
                        return true;
                }
            }
            return false;
        }
        private static final String[] contextPaths = new String[] {
            "com.skynav.ttv.model.smpte.tt.rel2013",
            "com.skynav.ttv.model.smpte.tt.rel2013.m708",
        };
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
            if (permissibleAncestors == null) {
                permissibleAncestors = new java.util.ArrayList<List<QName>>();
                String localName = elementName.getLocalPart();
                if ((localName.equals(Constants.ELT_DATA) || localName.equals(Constants.ELT_IMAGE)) && inSMPTEPrimaryNamespace(elementName)) {
                    List<QName> ancestors = new java.util.ArrayList<QName>();
                    ancestors.add(metadataElementName);
                    permissibleAncestors.add(ancestors);
                } else if (localName.equals(Constants.ELT_INFORMATION) && inSMPTEPrimaryNamespace(elementName)) {
                    List<QName> ancestors = new java.util.ArrayList<QName>();
                    ancestors.add(metadataElementName);
                    ancestors.add(headElementName);
                    permissibleAncestors.add(ancestors);
                } else if (localName.equals(Constants.ELT_SERVICE) && elementName.getNamespaceURI().equals(Constants.NAMESPACE_2013_CEA708)) {
                    List<QName> ancestors = new java.util.ArrayList<QName>();
                    ancestors.add(informationElementName);
                    ancestors.add(metadataElementName);
                    permissibleAncestors.add(ancestors);
                }
            }
            return (permissibleAncestors.size() > 0) ? permissibleAncestors : null;
        }
        private SemanticsVerifier semanticsVerifier;
        public SemanticsVerifier getSemanticsVerifier() {
            synchronized (this) {
                if (semanticsVerifier == null) {
                    semanticsVerifier = new ST20522013SemanticsVerifier(this);
                }
            }
            return semanticsVerifier;
        }
    }
}
