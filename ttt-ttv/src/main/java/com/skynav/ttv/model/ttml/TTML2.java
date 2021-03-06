/*
 * Copyright 2015-2020 Skynav, Inc. All rights reserved.
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
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.ttml2.tt.Animate;
import com.skynav.ttv.model.ttml2.tt.Animation;
import com.skynav.ttv.model.ttml2.tt.Audio;
import com.skynav.ttv.model.ttml2.tt.Chunk;
import com.skynav.ttv.model.ttml2.tt.Data;
import com.skynav.ttv.model.ttml2.tt.Font;
import com.skynav.ttv.model.ttml2.tt.Image;
import com.skynav.ttv.model.ttml2.tt.Initial;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Resources;
import com.skynav.ttv.model.ttml2.tt.Style;
import com.skynav.ttv.model.ttml2.ttm.Agent;
import com.skynav.ttv.verifier.AudioVerifier;
import com.skynav.ttv.verifier.DataVerifier;
import com.skynav.ttv.verifier.FontVerifier;
import com.skynav.ttv.verifier.ImageVerifier;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.ttml.TTML2AudioVerifier;
import com.skynav.ttv.verifier.ttml.TTML2DataVerifier;
import com.skynav.ttv.verifier.ttml.TTML2FontVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ImageVerifier;
import com.skynav.ttv.verifier.ttml.TTML2MetadataVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ParameterVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ProfileVerifier;
import com.skynav.ttv.verifier.ttml.TTML2SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML2StyleVerifier;
import com.skynav.ttv.verifier.ttml.TTML2TimingVerifier;
import com.skynav.xml.helpers.XML;

public class TTML2 {

    public static class Constants extends TTML1.Constants {
        public static final String XSD_TTML2 = "com/skynav/ttv/xsd/ttml2/ttml2.xsd";

        public static final String PROFILE_TTML2_PRESENTATION = "ttml2-presentation";
        public static final String PROFILE_TTML2_TRANSFORMATION = "ttml2-transformation";
        public static final String PROFILE_TTML2_FULL = "ttml2-full";

        public static final String PROFILE_TTML2_PRESENTATION_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TTML2_PRESENTATION;
        public static final String PROFILE_TTML2_TRANSFORMATION_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TTML2_TRANSFORMATION;
        public static final String PROFILE_TTML2_FULL_ABSOLUTE = NAMESPACE_TT_PROFILE + PROFILE_TTML2_FULL;

        public static final String NAMESPACE_XLINK = "http://www.w3.org/1999/xlink";

        public static final String ATTR_FORMAT = "format";
        public static final String ATTR_SRC = "src";
        public static final String ATTR_TYPE = "type";

    }

    public static final String MODEL_NAME = "ttml2";
    public static final int MODEL_VERSION = 2;

    public static class TTML2Model extends TTML1.TTML1Model {

        public static final QName animationElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createAnimation(new Animation()).getName();
        public static final QName animateElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createAnimate(new Animate()).getName();
        public static final QName audioElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createAudio(new Audio()).getName();
        public static final QName chunkElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createChunk(new Chunk()).getName();
        public static final QName dataElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createData(new Data()).getName();
        public static final QName fontElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createFont(new Font()).getName();
        public static final QName imageElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createImage(new Image()).getName();
        public static final QName initialElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createInitial(new Initial()).getName();
        public static final QName resourcesElementName = new com.skynav.ttv.model.ttml2.tt.ObjectFactory().createResources(new Resources()).getName();

        private String[] schemaResourceNames;
        private URI[] namespaceURIs;
        private Map<String,String> normalizedPrefixes2;
        private Map<URI,Class<?>> profileSpecificationClasses;
        private Profile.StandardDesignations standardDesignations;
        private Map<Class<?>,String> rootClasses;
        private SemanticsVerifier semanticsVerifier;
        private ParameterVerifier parameterVerifier;
        private ProfileVerifier profileVerifier;
        private StyleVerifier styleVerifier;
        private TimingVerifier timingVerifier;
        private MetadataVerifier metadataVerifier;
        private AudioVerifier audioVerifier;
        private DataVerifier dataVerifier;
        private FontVerifier fontVerifier;
        private ImageVerifier imageVerifier;

        public TTML2Model() {
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.add(Constants.XSD_TTML2);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            List<URI> namespaceURIs = new java.util.ArrayList<URI>();
            try {
                namespaceURIs.addAll(Arrays.asList(super.getTTNamespaceURIs()));
                namespaceURIs.add(new URI(XML.getXlinkNamespaceUri()));
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT_AUDIO));
                namespaceURIs.add(new URI(Constants.NAMESPACE_TT_ISD));
                this.namespaceURIs = namespaceURIs.toArray(new URI[namespaceURIs.size()]);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return MODEL_NAME;
        }

        public int getTTMLVersion() {
            return MODEL_VERSION;
        }

        public String[] getTTSchemaResourceNames() {
            return schemaResourceNames;
        }

        public URI[] getTTNamespaceURIs() {
            return namespaceURIs;
        }

        public Map<String,String> getNormalizedPrefixes() {
            if (normalizedPrefixes2 == null) {
                normalizedPrefixes2 = new java.util.HashMap<String,String>(super.getNormalizedPrefixes());
                normalizedPrefixes2.put(Constants.NAMESPACE_TT_AUDIO, "tta");
                normalizedPrefixes2.put(Constants.NAMESPACE_XLINK, "xlink");
            }
            return normalizedPrefixes2;
        }

        protected Map<URI,Class<?>> getProfileSpecificationClasses() {
            if (profileSpecificationClasses == null) {
                profileSpecificationClasses = new java.util.HashMap<URI,Class<?>>(super.getProfileSpecificationClasses());
                profileSpecificationClasses.put(getTTProfileNamespaceUri().resolve(Constants.PROFILE_TTML2_TRANSFORMATION), TTML2TransformationProfileSpecification.class);
                profileSpecificationClasses.put(getTTProfileNamespaceUri().resolve(Constants.PROFILE_TTML2_PRESENTATION), TTML2PresentationProfileSpecification.class);
                profileSpecificationClasses.put(getTTProfileNamespaceUri().resolve(Constants.PROFILE_TTML2_FULL), TTML2FullProfileSpecification.class);
            }
            return profileSpecificationClasses;
        }

        public Profile.StandardDesignations getStandardDesignations() {
            if (standardDesignations == null)
                standardDesignations = TTML2StandardDesignations.getInstance();
            return standardDesignations;
        }

        public String getJAXBContextPath() {
            return "com.skynav.ttv.model.ttml2.tt:com.skynav.ttv.model.ttml2.ttm:com.skynav.ttv.model.ttml2.ttp:com.skynav.ttv.model.ttml2.isd";
        }

        public Map<Class<?>,String> getRootClasses() {
            if (rootClasses == null) {
                rootClasses = new java.util.HashMap<Class<?>,String>();
                rootClasses.put(com.skynav.ttv.model.ttml2.tt.TimedText.class, "createTt");
                rootClasses.put(com.skynav.ttv.model.ttml2.ttp.Profile.class, "createProfile");
                rootClasses.put(com.skynav.ttv.model.ttml2.isd.ISDSequence.class, "createISDSequence");
                rootClasses.put(com.skynav.ttv.model.ttml2.isd.ISD.class, "createISD");
            }
            return rootClasses;
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
            } else if (namespaceUri.equals(Constants.NAMESPACE_TT_METADATA)) {
                if (localName.equals(Constants.ATTR_AGENT))
                    return Agent.class;
            }
            return Object.class;
        }

        static public boolean isTTInitialElement(QName name) {
            return name.equals(timedTextElementName);
        }

        public SemanticsVerifier getSemanticsVerifier() {
            if (semanticsVerifier == null) {
                semanticsVerifier = (SemanticsVerifier) new TTML2SemanticsVerifier(this);
            }
            return semanticsVerifier;
        }

        public ParameterVerifier getParameterVerifier() {
            if (parameterVerifier == null) {
                parameterVerifier = new TTML2ParameterVerifier(this);
            }
            return parameterVerifier;
        }

        public ProfileVerifier getProfileVerifier() {
            if (profileVerifier == null) {
                profileVerifier = new TTML2ProfileVerifier(this);
            }
            return profileVerifier;
        }

        public StyleVerifier getStyleVerifier() {
            if (styleVerifier == null) {
                styleVerifier = new TTML2StyleVerifier(this);
            }
            return styleVerifier;
        }

        public TimingVerifier getTimingVerifier() {
            if (timingVerifier == null) {
                timingVerifier = new TTML2TimingVerifier(this);
            }
            return timingVerifier;
        }

        public MetadataVerifier getMetadataVerifier() {
            if (metadataVerifier == null) {
                metadataVerifier = new TTML2MetadataVerifier(this);
            }
            return metadataVerifier;
        }

        public AudioVerifier getAudioVerifier() {
            if (audioVerifier == null) {
                audioVerifier = new TTML2AudioVerifier(this);
            }
            return audioVerifier;
        }

        public DataVerifier getDataVerifier() {
            if (dataVerifier == null) {
                dataVerifier = new TTML2DataVerifier(this);
            }
            return dataVerifier;
        }

        public FontVerifier getFontVerifier() {
            if (fontVerifier == null) {
                fontVerifier = new TTML2FontVerifier(this);
            }
            return fontVerifier;
        }

        public ImageVerifier getImageVerifier() {
            if (imageVerifier == null) {
                imageVerifier = new TTML2ImageVerifier(this);
            }
            return imageVerifier;
        }

        public boolean isSupportedResourceType(String type, String parameters) {
            if (type == null)
                return false;
            else if (type.equals("audio/mpeg"))
                return true;
            else if (type.equals("audio/vnd.wave"))
                return true;
            else if (type.equals("audio/vnd.skynav.speech"))
                return true;
            else if (type.equals("font/collection"))
                return true;
            else if (type.equals("font/otf"))
                return true;
            else if (type.equals("font/ttf"))
                return true;
            else if (type.equals("font/woff"))
                return true;
            else if (type.equals("image/png"))
                return true;
            else if (type.equals("image/jpeg"))
                return true;
            else if (type.equals("application/octet-stream"))
                return true;
            else
                return false;
        }

    }

}
