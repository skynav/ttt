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

package com.skynav.ttv.model.imsc;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.imsc10.ittm.AltText;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.smpte.ST20522010.ST20522010Model;
import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ImageVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.imsc.IMSC10ImageVerifier;
import com.skynav.ttv.verifier.imsc.IMSC10ParameterVerifier;
import com.skynav.ttv.verifier.imsc.IMSC10SemanticsVerifier;
import com.skynav.ttv.verifier.imsc.IMSC10StyleVerifier;
import com.skynav.ttv.verifier.imsc.IMSC10TimingVerifier;

public class IMSC10 {

    public static class Constants {

        public static final String NAMESPACE_IMSC_PREFIX = TTML1.Constants.NAMESPACE_TT + "/profile/imsc1";
        public static final String NAMESPACE_IMSC_METADATA = NAMESPACE_IMSC_PREFIX + "#metadata";
        public static final String NAMESPACE_IMSC_PARAMETER = NAMESPACE_IMSC_PREFIX + "#parameter";
        public static final String NAMESPACE_IMSC_STYLING = NAMESPACE_IMSC_PREFIX + "#styling";
        public static final String NAMESPACE_IMSC_PROFILE = NAMESPACE_IMSC_PREFIX + "/";
        public static final String NAMESPACE_IMSC_EXTENSION = NAMESPACE_IMSC_PREFIX + "/extension/";

        public static final String NAMESPACE_EBUTT_PREFIX = "urn:ebu:tt";
        public static final String NAMESPACE_EBUTT_STYLING = NAMESPACE_EBUTT_PREFIX + ":style";

        public static final String XSD_IMSC10 = "com/skynav/ttv/xsd/imsc10/imsc10.xsd";

        public static final String PROFILE_TEXT = "text";
        public static final String PROFILE_TEXT_ABSOLUTE = NAMESPACE_IMSC_PROFILE + PROFILE_TEXT;
        public static final String PROFILE_IMAGE = "image";
        public static final String PROFILE_IMAGE_ABSOLUTE = NAMESPACE_IMSC_PROFILE + PROFILE_IMAGE;

        public static final String ELT_ALT_TEXT = "altText";

        public static final String ATTR_ASPECT_RATIO = "aspectRatio";
        public static final String ATTR_FORCED_DISPLAY = "forcedDisplay";
        public static final String ATTR_LINE_PADDING = "linePadding";
        public static final String ATTR_MULTIROW_ALIGN = "multiRowAlign";
        public static final String ATTR_PROGRESSIVELY_DECODABLE = "progressivelyDecodable";

        public static final String CHARSET_REQUIRED = "UTF-8";
        public static final int    MAX_REGIONS_PER_ISD = 4;

    }

    public static final String MODEL_NAME = "imsc10";

    public static boolean inIMSC10MetadataNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC_METADATA);
    }

    public static boolean inIMSC10ParameterNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC_PARAMETER);
    }

    public static boolean inIMSC10StylingNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC_STYLING);
    }

    public static boolean maybeInIMSC10Namespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.startsWith(Constants.NAMESPACE_IMSC_PREFIX);
    }

    public static boolean inIMSC10Namespace(QName name) {
        if (!maybeInIMSC10Namespace(name))
            return false;
        else if (inIMSC10MetadataNamespace(name))
            return true;
        else if (inIMSC10ParameterNamespace(name))
            return true;
        else if (inIMSC10StylingNamespace(name))
            return true;
        else
            return false;
    }

    public static boolean inIMSCNamespace(QName name) {
        return inIMSC10Namespace(name);
    }

    public static boolean inEBUTTStylingNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_EBUTT_STYLING);
    }

    public static boolean maybeInEBUTTNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.startsWith(Constants.NAMESPACE_EBUTT_PREFIX);
    }

    public static boolean inEBUTTNamespace(QName name) {
        if (!maybeInEBUTTNamespace(name))
            return false;
        else if (inEBUTTStylingNamespace(name))
            return true;
        else
            return false;
    }

    public static class IMSC10Model extends ST20522010Model {

        private static final QName altTextElementName = new com.skynav.ttv.model.imsc10.ittm.ObjectFactory().createAltText(new AltText()).getName();
        private static final String[] contextPaths = new String[] { "com.skynav.ttv.model.imsc10.ittm" };

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
        private ImageVerifier imageVerifier;

        public IMSC10Model() {
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getTTSchemaResourceNames()));
            resourceNames.add(ST20522010.Constants.XSD_2010);
            resourceNames.add(Constants.XSD_IMSC10);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            namespaceURIs.addAll(Arrays.asList(super.getTTNamespaceURIs()));
            try {
                namespaceURIs.add(new URI(ST20522010.Constants.NAMESPACE_2010));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC_METADATA));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC_PARAMETER));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC_STYLING));
                namespaceURIs.add(new URI(Constants.NAMESPACE_EBUTT_STYLING));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_IMSC_PROFILE);
                this.extensionNamespaceUri = new URI(Constants.NAMESPACE_IMSC_EXTENSION);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return MODEL_NAME;
        }

        public String[] getIMSC10SchemaResourceNames() {
            return this.schemaResourceNames;
        }

        public String[] getSchemaResourceNames() {
            return getIMSC10SchemaResourceNames();
        }

        public URI[] getIMSC10NamespaceURIs() {
            return this.namespaceURIs;
        }

        public URI[] getNamespaceURIs() {
            return getIMSC10NamespaceURIs();
        }

        public URI getProfileNamespaceUri() {
            return this.profileNamespaceUri;
        }

        public URI getExtensionNamespaceUri() {
            return this.extensionNamespaceUri;
        }

        public Map<String,String> getNormalizedPrefixes() {
            Map<String,String> normalizedPrefixes = super.getNormalizedPrefixes();
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC_METADATA, "ittm");
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC_PARAMETER, "ittp");
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC_STYLING, "itts");
            normalizedPrefixes.put(Constants.NAMESPACE_EBUTT_STYLING, "ebutts");
            return normalizedPrefixes;
        }

        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>();
                profileSpecificationClasses.put(getProfileNamespaceUri().resolve(Constants.PROFILE_TEXT), IMSC10TextProfileSpecification.class);
                profileSpecificationClasses.put(getProfileNamespaceUri().resolve(Constants.PROFILE_IMAGE), IMSC10ImageProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }

        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null) {
                standardDesignations = IMSC10StandardDesignations.getInstance();
            }
            return standardDesignations;
        }

        public boolean isGlobalAttribute(QName name) {
            if (super.isGlobalAttribute(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inIMSC10ParameterNamespace(name)) {
                    if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                        return true;
                    else if (ln.equals(Constants.ATTR_PROGRESSIVELY_DECODABLE))
                        return true;
                } else if (inIMSC10StylingNamespace(name)) {
                    if (ln.equals(Constants.ATTR_FORCED_DISPLAY))
                        return true;
                } else if (inEBUTTStylingNamespace(name)) {
                    if (ln.equals(Constants.ATTR_LINE_PADDING))
                        return true;
                    else if (ln.equals(Constants.ATTR_MULTIROW_ALIGN))
                        return true;
                }
            }
            return false;
        }

        public boolean isIMSC10AltTextElement(QName name) {
            return name.equals(altTextElementName);
        }

        public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
            if (super.isGlobalAttributePermitted(attributeName, elementName))
                return true;
            else {
                String ln = attributeName.getLocalPart();
                if (inIMSC10ParameterNamespace(attributeName)) {
                    if (isTTElement(elementName)) {
                        if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                            return true;
                        else if (ln.equals(Constants.ATTR_PROGRESSIVELY_DECODABLE))
                            return true;
                    }
                } else if (inIMSC10StylingNamespace(attributeName)) {
                    if (ln.equals(Constants.ATTR_FORCED_DISPLAY))
                        return isTTContentOrRegionElement(elementName);
                } else if (inEBUTTStylingNamespace(attributeName)) {
                    if (ln.equals(Constants.ATTR_LINE_PADDING))
                        return isEBUTTStylingUsageContext(elementName);
                    else if (ln.equals(Constants.ATTR_MULTIROW_ALIGN))
                        return isEBUTTStylingUsageContext(elementName);
                }
            }
            return false;
        }

        protected boolean isEBUTTStylingUsageContext(QName name) {
            if (isTTBodyElement(name))
                return true;
            else if (isTTDivElement(name))
                return true;
            else if (isTTParagraphElement(name))
                return true;
            else if (isTTRegionElement(name))
                return true;
            else if (isTTStyleElement(name))
                return true;
            else
                return false;
        }

        public boolean isElement(QName name) {
            if (super.isElement(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inIMSC10MetadataNamespace(name)) {
                    if (ln.equals(Constants.ELT_ALT_TEXT))
                        return true;
                }
            }
            return false;
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
            if (permissibleAncestors == null) {
                permissibleAncestors = new java.util.ArrayList<List<QName>>();
                if (inIMSC10MetadataNamespace(elementName)) {
                    String localName = elementName.getLocalPart();
                    if (localName.equals(Constants.ELT_ALT_TEXT)) {
                        List<QName> ancestors = new java.util.ArrayList<QName>();
                        ancestors.add(metadataElementName);
                        permissibleAncestors.add(ancestors);
                    }
                }
            }
            return (permissibleAncestors.size() > 0) ? permissibleAncestors : null;
        }

        public SemanticsVerifier getSemanticsVerifier() {
            if (semanticsVerifier == null) {
                semanticsVerifier = new IMSC10SemanticsVerifier(this);
            }
            return semanticsVerifier;
        }

        public ParameterVerifier getParameterVerifier() {
            if (parameterVerifier == null) {
                parameterVerifier = new IMSC10ParameterVerifier(this);
            }
            return parameterVerifier;
        }

        public StyleVerifier getStyleVerifier() {
            if (styleVerifier == null) {
                styleVerifier = new IMSC10StyleVerifier(this);
            }
            return styleVerifier;
        }

        public TimingVerifier getTimingVerifier() {
            if (timingVerifier == null) {
                timingVerifier = new IMSC10TimingVerifier(this);
            }
            return timingVerifier;
        }


        public ImageVerifier getImageVerifier() {
            if (imageVerifier == null) {
                imageVerifier = new IMSC10ImageVerifier(this);
            }
            return imageVerifier;
        }

        public boolean isSupportedResourceType(String type, String parameters) {
            if (type == null)
                return false;
            else if (type.equals("image/png"))
                return true;
            else
                return false;
        }

        public void configureReporter(Reporter reporter) {
            if (!reporter.hasDisabledWarning("missing-timing"))
                reporter.enableWarning("missing-timing");
            if (!reporter.hasEnabledWarning("references-external-image"))
                reporter.disableWarning("references-external-image");
            if (!reporter.hasDisabledWarning("uses-line-height-normal"))
                reporter.enableWarning("uses-line-height-normal");
            if (!reporter.hasDisabledWarning("uses-non-recommended-font-family"))
                reporter.enableWarning("uses-non-recommended-font-family");
        }

    }

}
