/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Style;
import com.skynav.ttv.model.ttml2.ttm.Agent;
import com.skynav.ttv.verifier.ImageVerifier;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ImageVerifier;
import com.skynav.ttv.verifier.ttml.TTML2MetadataVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ParameterVerifier;
import com.skynav.ttv.verifier.ttml.TTML2ProfileVerifier;
import com.skynav.ttv.verifier.ttml.TTML2SemanticsVerifier;
import com.skynav.ttv.verifier.ttml.TTML2StyleVerifier;
import com.skynav.ttv.verifier.ttml.TTML2TimingVerifier;


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
    }

    public static final String MODEL_NAME = "ttml2";
    public static final int MODEL_VERSION = 2;

    public static class TTML2Model extends TTML1.TTML1Model {

        private String[] schemaResourceNames;
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
        private ImageVerifier imageVerifier;

        public TTML2Model() {
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.add(Constants.XSD_TTML2);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        public String getName() {
            return MODEL_NAME;
        }

        public int getTTMLVersion() {
            return MODEL_VERSION;
        }

        public boolean isTTMLVersion(int version) {
            return getTTMLVersion() == MODEL_VERSION;
        }

        public String[] getTTSchemaResourceNames() {
            return schemaResourceNames;
        }

        public Map<String,String> getNormalizedPrefixes() {
            if (normalizedPrefixes2 == null) {
                normalizedPrefixes2 = new java.util.HashMap<String,String>(super.getNormalizedPrefixes());
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
            return "com.skynav.ttv.model.ttml2.tt:com.skynav.ttv.model.ttml2.ttm:com.skynav.ttv.model.ttml2.ttp";
        }

        public Map<Class<?>,String> getRootClasses() {
            if (rootClasses == null) {
                rootClasses = new java.util.HashMap<Class<?>,String>();
                rootClasses.put(com.skynav.ttv.model.ttml2.tt.TimedText.class, "createTt");
                rootClasses.put(com.skynav.ttv.model.ttml2.ttp.Profile.class, "createProfile");
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

        public SemanticsVerifier getSemanticsVerifier() {
            if (semanticsVerifier == null) {
                semanticsVerifier = new TTML2SemanticsVerifier(this);
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

        public ImageVerifier getImageVerifier() {
            if (imageVerifier == null) {
                imageVerifier = new TTML2ImageVerifier(this);
            }
            return imageVerifier;
        }

    }

}
