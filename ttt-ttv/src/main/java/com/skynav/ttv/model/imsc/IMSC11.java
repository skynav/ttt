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
import com.skynav.ttv.model.imsc11.ittm.AltText;
import com.skynav.ttv.model.smpte.ST20522010TTML2;
import com.skynav.ttv.model.smpte.ST20522010TTML2.ST20522010TTML2Model;
import com.skynav.ttv.model.ttml.TTML2;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ImageVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.imsc.IMSC11ImageVerifier;
import com.skynav.ttv.verifier.imsc.IMSC11ParameterVerifier;
import com.skynav.ttv.verifier.imsc.IMSC11SemanticsVerifier;
import com.skynav.ttv.verifier.imsc.IMSC11StyleVerifier;
import com.skynav.ttv.verifier.imsc.IMSC11TimingVerifier;

public class IMSC11 {

    public static class Constants {

        public static final String NAMESPACE_IMSC10_PREFIX = TTML2.Constants.NAMESPACE_TT + "/profile/imsc1";
        public static final String NAMESPACE_IMSC11_PREFIX = TTML2.Constants.NAMESPACE_TT + "/profile/imsc1.1";
        public static final String NAMESPACE_IMSC11_METADATA = NAMESPACE_IMSC10_PREFIX + "#metadata";
        public static final String NAMESPACE_IMSC11_PARAMETER = NAMESPACE_IMSC10_PREFIX + "#parameter";
        public static final String NAMESPACE_IMSC11_STYLING = NAMESPACE_IMSC10_PREFIX + "#styling";
        public static final String NAMESPACE_IMSC11_PROFILE = NAMESPACE_IMSC11_PREFIX + "/";
        public static final String NAMESPACE_IMSC11_EXTENSION = NAMESPACE_IMSC10_PREFIX + "/extension/";

        public static final String NAMESPACE_EBUTT_PREFIX = "urn:ebu:tt";
        public static final String NAMESPACE_EBUTT_STYLING = NAMESPACE_EBUTT_PREFIX + ":style";

        public static final String XSD_IMSC11 = "com/skynav/ttv/xsd/imsc11/imsc11.xsd";

        public static final String PROFILE_TEXT = "text";
        public static final String PROFILE_TEXT_ABSOLUTE = NAMESPACE_IMSC11_PROFILE + PROFILE_TEXT;
        public static final String PROFILE_IMAGE = "image";
        public static final String PROFILE_IMAGE_ABSOLUTE = NAMESPACE_IMSC11_PROFILE + PROFILE_IMAGE;

        public static final String ELT_ALT_TEXT = "altText";

        public static final String ATTR_ACTIVE_AREA = "activeArea";
        public static final String ATTR_ASPECT_RATIO = "aspectRatio";
        public static final String ATTR_FILL_LINE_GAP = "fillLineGap";
        public static final String ATTR_FORCED_DISPLAY = "forcedDisplay";
        public static final String ATTR_LINE_PADDING = "linePadding";
        public static final String ATTR_MULTI_ROW_ALIGN = "multiRowAlign";
        public static final String ATTR_PROGRESSIVELY_DECODABLE = "progressivelyDecodable";

        public static final String CHARSET_REQUIRED = "UTF-8";
        public static final int    MAX_REGIONS_PER_ISD = 4;

    }

    public static final String MODEL_NAME = "imsc11";
    public static final String MODEL_NAME_ALIAS_1 = "imsc1";

    public static boolean inIMSC11MetadataNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC11_METADATA);
    }

    public static boolean inIMSC11ParameterNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC11_PARAMETER);
    }

    public static boolean inIMSC11StylingNamespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.equals(Constants.NAMESPACE_IMSC11_STYLING);
    }

    public static boolean maybeInIMSC11Namespace(QName name) {
        String nsUri = name.getNamespaceURI();
        return nsUri.startsWith(Constants.NAMESPACE_IMSC11_PREFIX);
    }

    public static boolean inIMSC11Namespace(QName name) {
        if (!maybeInIMSC11Namespace(name))
            return false;
        else if (inIMSC11MetadataNamespace(name))
            return true;
        else if (inIMSC11ParameterNamespace(name))
            return true;
        else if (inIMSC11StylingNamespace(name))
            return true;
        else
            return false;
    }

    public static boolean inIMSCNamespace(QName name) {
        return inIMSC11Namespace(name);
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

    public static class IMSC11Model extends ST20522010TTML2Model {

        private static final QName altTextElementName = new com.skynav.ttv.model.imsc11.ittm.ObjectFactory().createAltText(new AltText()).getName();
        private static final String[] contextPaths = new String[] { "com.skynav.ttv.model.imsc11.ittm" };

        private String[] nameAliases;
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

        public IMSC11Model() {
            populate();
        }

        private void populate() {
            populateNameAliases();
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateNameAliases() {
            List<String> nameAliases = new java.util.ArrayList<String>();
            nameAliases.add(MODEL_NAME_ALIAS_1);
            this.nameAliases = nameAliases.toArray(new String[nameAliases.size()]);
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getTTSchemaResourceNames()));
            resourceNames.add(ST20522010TTML2.Constants.XSD_2010);
            resourceNames.add(Constants.XSD_IMSC11);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            namespaceURIs.addAll(Arrays.asList(super.getTTNamespaceURIs()));
            try {
                namespaceURIs.add(new URI(ST20522010TTML2.Constants.NAMESPACE_2010));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC11_METADATA));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC11_PARAMETER));
                namespaceURIs.add(new URI(Constants.NAMESPACE_IMSC11_STYLING));
                namespaceURIs.add(new URI(Constants.NAMESPACE_EBUTT_STYLING));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_IMSC11_PROFILE);
                this.extensionNamespaceUri = new URI(Constants.NAMESPACE_IMSC11_EXTENSION);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return MODEL_NAME;
        }

        public String[] getNameAliases() {
            return this.nameAliases;
        }

        public String[] getIMSC11SchemaResourceNames() {
            return this.schemaResourceNames;
        }

        public String[] getSchemaResourceNames() {
            return getIMSC11SchemaResourceNames();
        }

        public URI[] getIMSC11NamespaceURIs() {
            return this.namespaceURIs;
        }

        public URI[] getNamespaceURIs() {
            return getIMSC11NamespaceURIs();
        }

        public URI getProfileNamespaceUri() {
            return this.profileNamespaceUri;
        }

        public URI getExtensionNamespaceUri() {
            return this.extensionNamespaceUri;
        }

        public Map<String,String> getNormalizedPrefixes() {
            Map<String,String> normalizedPrefixes = super.getNormalizedPrefixes();
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC11_METADATA, "ittm");
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC11_PARAMETER, "ittp");
            normalizedPrefixes.put(Constants.NAMESPACE_IMSC11_STYLING, "itts");
            normalizedPrefixes.put(Constants.NAMESPACE_EBUTT_STYLING, "ebutts");
            return normalizedPrefixes;
        }

        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>();
                profileSpecificationClasses.put(getProfileNamespaceUri().resolve(Constants.PROFILE_TEXT), IMSC11TextProfileSpecification.class);
                profileSpecificationClasses.put(getProfileNamespaceUri().resolve(Constants.PROFILE_IMAGE), IMSC11ImageProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }

        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null) {
                standardDesignations = IMSC11StandardDesignations.getInstance();
            }
            return standardDesignations;
        }

        public boolean isGlobalAttribute(QName name) {
            if (super.isGlobalAttribute(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inIMSC11ParameterNamespace(name)) {
                    if (ln.equals(Constants.ATTR_ACTIVE_AREA))
                        return true;
                    else if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                        return true;
                    else if (ln.equals(Constants.ATTR_PROGRESSIVELY_DECODABLE))
                        return true;
                } else if (inIMSC11StylingNamespace(name)) {
                    if (ln.equals(Constants.ATTR_FORCED_DISPLAY))
                        return true;
                    else if (ln.equals(Constants.ATTR_FILL_LINE_GAP))
                        return true;
                } else if (inEBUTTStylingNamespace(name)) {
                    if (ln.equals(Constants.ATTR_LINE_PADDING))
                        return true;
                    else if (ln.equals(Constants.ATTR_MULTI_ROW_ALIGN))
                        return true;
                }
            }
            return false;
        }

        public boolean isIMSC11AltTextElement(QName name) {
            return name.equals(altTextElementName);
        }

        public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
            if (super.isGlobalAttributePermitted(attributeName, elementName))
                return true;
            else {
                String ln = attributeName.getLocalPart();
                if (inIMSC11ParameterNamespace(attributeName)) {
                    if (isTTElement(elementName)) {
                        if (ln.equals(Constants.ATTR_ACTIVE_AREA))
                            return true;
                        else if (ln.equals(Constants.ATTR_ASPECT_RATIO))
                            return true;
                        else if (ln.equals(Constants.ATTR_PROGRESSIVELY_DECODABLE))
                            return true;
                    }
                } else if (inIMSC11StylingNamespace(attributeName)) {
                    if (ln.equals(Constants.ATTR_FORCED_DISPLAY))
                        return isIMSCStylingUsageContext(elementName) | isTTSpanElement(elementName);
                    if (ln.equals(Constants.ATTR_FILL_LINE_GAP))
                        return isIMSCStylingUsageContext(elementName);
                } else if (inEBUTTStylingNamespace(attributeName)) {
                    if (ln.equals(Constants.ATTR_LINE_PADDING))
                        return isIMSCStylingUsageContext(elementName);
                    else if (ln.equals(Constants.ATTR_MULTI_ROW_ALIGN))
                        return isIMSCStylingUsageContext(elementName);
                }
            }
            return false;
        }

        protected boolean isIMSCStylingUsageContext(QName name) {
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
            else if (isTTInitialElement(name))
                return true;
            else
                return false;
        }

        public boolean isElement(QName name) {
            if (super.isElement(name))
                return true;
            else {
                String ln = name.getLocalPart();
                if (inIMSC11MetadataNamespace(name)) {
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
                if (inIMSC11MetadataNamespace(elementName)) {
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
                semanticsVerifier = new IMSC11SemanticsVerifier(this);
            }
            return semanticsVerifier;
        }

        public ParameterVerifier getParameterVerifier() {
            if (parameterVerifier == null) {
                parameterVerifier = new IMSC11ParameterVerifier(this);
            }
            return parameterVerifier;
        }

        public StyleVerifier getStyleVerifier() {
            if (styleVerifier == null) {
                styleVerifier = new IMSC11StyleVerifier(this);
            }
            return styleVerifier;
        }

        public TimingVerifier getTimingVerifier() {
            if (timingVerifier == null) {
                timingVerifier = new IMSC11TimingVerifier(this);
            }
            return timingVerifier;
        }


        public ImageVerifier getImageVerifier() {
            if (imageVerifier == null) {
                imageVerifier = new IMSC11ImageVerifier(this);
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
