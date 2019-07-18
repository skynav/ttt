/*
 * Copyright 2019 Skynav, Inc. All rights reserved.
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

import com.skynav.ttv.model.Profile;
import com.skynav.ttv.model.smpte.ST20522010TTML1;
import com.skynav.ttv.model.ttml.TTML1;

public class IMSC11 {

    public static class Constants extends IMSC10.Constants {

        public static final String NAMESPACE_IMSC11_PREFIX = TTML1.Constants.NAMESPACE_TT + "/profile/imsc1.1";
        public static final String NAMESPACE_IMSC11_PROFILE = NAMESPACE_IMSC11_PREFIX + "/";

        public static final String XSD_IMSC11 = "com/skynav/ttv/xsd/imsc11/imsc11.xsd";

        public static final String PROFILE_TEXT_ABSOLUTE = NAMESPACE_IMSC11_PROFILE + IMSC10.Constants.PROFILE_TEXT;
        public static final String PROFILE_IMAGE_ABSOLUTE = NAMESPACE_IMSC11_PROFILE + IMSC10.Constants.PROFILE_IMAGE;

    }

    public static final String MODEL_NAME = "imsc11";

    public static class IMSC11Model extends IMSC10.IMSC10Model {

        private String[] schemaResourceNames;
        private URI profileNamespaceUri;
        private Map<URI,Class<?>> profileSpecificationClasses;
        private Profile.StandardDesignations standardDesignations;

        public IMSC11Model() {
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<String>();
            resourceNames.addAll(Arrays.asList(super.getTTSchemaResourceNames()));
            resourceNames.add(ST20522010TTML1.Constants.XSD_2010);
            resourceNames.add(Constants.XSD_IMSC11);
            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            try {
                this.profileNamespaceUri = new URI(Constants.NAMESPACE_IMSC11_PROFILE);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        public String getName() {
            return MODEL_NAME;
        }

        public String[] getIMSC11SchemaResourceNames() {
            return this.schemaResourceNames;
        }

        public String[] getSchemaResourceNames() {
            return getIMSC11SchemaResourceNames();
        }

        public URI getProfileNamespaceUri() {
            return this.profileNamespaceUri;
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

    }

}
