/*
 * Copyright (c) 2015, Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 * Copyright 2015 Skynav, Inc.
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

package com.skynav.ttv.model.ebuttd;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.ebuttd.EBUTTDTimingVerifier;
import com.skynav.xml.helpers.XML;

/**
 * Class adding integration for EBU-TT-D (3380).
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class EBUTTD {

    public static class Constants extends TTML.Constants {

        public static final String XSD_EBUTTD = "com/skynav/ttv/xsd/ebuttd/ebutt_d.xsd";
        public static final String NAMESPACE_EBUTTD_METADATA = "urn:ebu:tt:metadata";
        public static final String NAMESPACE_EBUTTD_STYLING = "urn:ebu:tt:style";
        public static final String NAMESPACE_EBUTTD_DATATYPES = "urn:ebu:tt:datatypes";
        public static final String XJC_PACKAGE_EBUTTD_TT = "com.skynav.ttv.model.ebuttd.tt";
        public static final String XJC_PACKAGE_EBUTTD_METADATA = "com.skynav.ttv.model.ebuttd.ttm";
        public static final String XJC_PACKAGE_EBUTTD_DATATYPES = "com.skynav.ttv.model.ebuttd.ttd";
        public static final String EBUTTD_PROFILE_URI = "ttp://www.w3.org/ns/ttml/profile/ebuttd";
    }

    public static final String MODEL_NAME = "ebuttd";

    public static class EBUTTDModel extends TTML1.TTML1Model {

        private URI[] namespaceURIs;
        private String[] schemaResourceNames;
        private SemanticsVerifier semanticsVerifier;
        private ParameterVerifier parameterVerifier;
        private TimingVerifier timingVerifier;
        private StyleVerifier styleVerifier;

        public EBUTTDModel() {
            super();
            populate();
        }

        private void populate() {
            populateSchemaResourceNames();
            populateNamespaceURIs();
        }

        private void populateSchemaResourceNames() {
            List<String> resourceNames = new java.util.ArrayList<>();
            resourceNames.add(Constants.XSD_EBUTTD);

            this.schemaResourceNames = resourceNames.toArray(new String[resourceNames.size()]);
        }

        private void populateNamespaceURIs() {
            List<URI> namespaceUris = new java.util.ArrayList<>();

            try {
                namespaceUris.add(new URI(Constants.NAMESPACE_TT));
                namespaceUris.add(new URI(Constants.NAMESPACE_TT_METADATA));
                namespaceUris.add(new URI(Constants.NAMESPACE_TT_PARAMETER));
                namespaceUris.add(new URI(Constants.NAMESPACE_TT_STYLE));
                namespaceUris.add(new URI(Constants.NAMESPACE_EBUTTD_METADATA));
                namespaceUris.add(new URI(Constants.NAMESPACE_EBUTTD_DATATYPES));
                namespaceUris.add(new URI(Constants.NAMESPACE_EBUTTD_STYLING));
                namespaceUris.add(new URI(XML.xmlNamespace));
                namespaceUris.add(new URI(XML.xmlnsNamespace));
                namespaceUris.add(new URI(XML.xsiNamespace));
                namespaceUris.add(new URI(Annotations.getNamespace()));
                this.namespaceURIs = namespaceUris.toArray(new URI[namespaceUris.size()]);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public String getName() {
            return MODEL_NAME;
        }

        @Override
        public String[] getSchemaResourceNames() {
            return this.schemaResourceNames;
        }

        @Override
        public URI[] getNamespaceURIs() {
            return namespaceURIs;
        }

        @Override
        public String getJAXBContextPath() {
            String ebuttdContext = "ebu.tt.datatypes:ebu.tt.metadata";
            String ttmlContext = super.getJAXBContextPath();
            return ttmlContext + ":" + ebuttdContext;
        }

        @Override
        public Map<String, String> getNormalizedPrefixes() {
            Map<String, String> prefixes = new HashMap<>();
            prefixes.put(Constants.NAMESPACE_TT, "tt");
            prefixes.put(Constants.NAMESPACE_TT_METADATA, "ttm");
            prefixes.put(Constants.NAMESPACE_TT_PARAMETER, "ttp");
            prefixes.put(Constants.NAMESPACE_TT_STYLE, "tts");
            prefixes.put(Constants.NAMESPACE_EBUTTD_DATATYPES, "ebuttdt");
            prefixes.put(Constants.NAMESPACE_EBUTTD_METADATA, "ebuttm");
            prefixes.put(Constants.NAMESPACE_EBUTTD_STYLING, "ebutts");
            prefixes.put(XML.xmlnsNamespace, "xmlns");
            prefixes.put(XML.xmlNamespace, "xml");
            prefixes.put(XML.xsiNamespace, "xsi");
            return prefixes;
        }

        @Override
        public TimingVerifier getTimingVerifier() {
            if (timingVerifier == null) {
                timingVerifier = new EBUTTDTimingVerifier(this);
            }
            return timingVerifier;
        }
    }
}
