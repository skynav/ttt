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
 
package com.skynav.ttv.model;

import java.net.URI;
import java.util.Map;

import com.skynav.ttv.util.URIs;

public class Profile {

    public enum Usage {
        NONE,
        OPTIONAL,
        REQUIRED,
        USE;
    }

    private Specification specification;
    private Profile baseline;

    public Profile(Specification specification, Profile baseline) {
        assert specification != null;
        this.specification = specification;
        this.baseline = baseline;
    }

    @Override
    public int hashCode() {
        return specification.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Profile) {
            return specification.equals(((Profile) o).specification);
        } else
            return false;
    }

    /**
     * Obtain profile designator URI.
     * @return profile designator URI
     */
    public URI getURI() {
        return specification.getURI();
    }

    /**
     * Obtain baseline profile.
     * @return baseline profile or null
     */
    public Profile getBaseline() {
        return baseline;
    }

    /**
     * Obtain feature usage.
     * @return feature usage
     */
    public Usage getFeature(URI uri) {
        if (specification.hasFeature(uri))
            return specification.getFeature(uri);
        else if (baseline != null)
            return baseline.getFeature(uri);
        else
            return Usage.NONE;
    }

    /**
     * Obtain extension usage.
     * @return extension usage
     */
    public Usage getExtension(URI uri) {
        if (specification.hasExtension(uri))
            return specification.getExtension(uri);
        else if (baseline != null)
            return baseline.getExtension(uri);
        else
            return Usage.NONE;
    }

    public static class StandardDesignations {

        public boolean isStandardFeatureDesignation(URI uri) {
            return false;
        }
        
        public boolean isStandardExtensionDesignation(URI uri) {
            return false;
        }
        
    }

    public static class Specification {

        private URI uri;
        private URI baselineUri;
        private Map<URI,Usage> features;
        private Map<URI,Usage> extensions;

        protected Specification(URI uri, String baseline, Map<URI,Usage> features, Map<URI,Usage> extensions) {
            assert uri != null;
            this.uri = uri;
            this.baselineUri = URIs.makeURISafely(baseline);
            this.features = (features != null) ? features : new java.util.HashMap<URI,Usage>();
            this.extensions = (extensions != null) ? extensions : new java.util.HashMap<URI,Usage>();
        }

        @Override
        public int hashCode() {
            int hash = uri.hashCode();
            if (baselineUri != null)
                hash ^= baselineUri.hashCode();
            return hash;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Specification) {
                Specification ps = (Specification) o;
                if (!uri.equals(ps.uri))
                    return false;
                else if ((baselineUri != null) && (ps.baselineUri != null))
                    return baselineUri.equals(ps.baselineUri);
                else
                    return true;
            } else
                return false;
        }

        /**
         * Obtain profile designator URI.
         * @return profile designator URI
         */
        public URI getURI() {
            return uri;
        }

        /**
         * Obtain baseline profile designator URI, i.e., URI specified
         * by 'use' attribute on ttp:profile element.
         * @return baseline profile designator URI or null if empty baseline
         */
        public URI getBaselineURI() {
            return baselineUri;
        }

        /**
         * Determine if feature is defined.
         * @return true if feature defined
         */
        public boolean hasFeature(URI uri) {
            return features.containsKey(uri);
        }

        /**
         * Obtain feature usage if defined, otherwise Usage.NONE.
         * @return feature usage if defined, otherwise Usage.NONE
         */
        public Usage getFeature(URI uri) {
            Usage usage = features.get(uri);
            if (usage == null)
                usage = Usage.NONE;
            return usage;
        }

        /**
         * Determine if extension is defined.
         * @return true if extension defined
         */
        public boolean hasExtension(URI uri) {
            return extensions.containsKey(uri);
        }

        /**
         * Obtain extension usage if defined, otherwise Usage.NONE.
         * @return extension usage if defined, otherwise Usage.NONE
         */
        public Usage getExtension(URI uri) {
            Usage usage = extensions.get(uri);
            if (usage == null)
                usage = Usage.NONE;
            return usage;
        }

        protected static Map<URI,Usage> featuresMap(String featureNamespace, Object[][] featureMapEntries) {
            if ((featureNamespace == null) || (featureNamespace.length() == 0) || (featureMapEntries == null))
                return null;
            else {
                URI featureNamespaceUri = URIs.makeURISafely(featureNamespace);
                if (featureNamespaceUri != null) {
                    Map<URI,Usage> map = new java.util.HashMap<URI,Usage>(featureMapEntries.length);
                    for (Object[] entry : featureMapEntries) {
                        String designator = (String) entry[0];
                        Usage usage = (Usage) entry[1];
                        map.put(featureNamespaceUri.resolve(designator), usage);
                    }
                    return map;
                } else
                    return null;
            }
        }

        protected static Map<URI,Usage> extensionsMap(String extensionNamespace, Object[][] extensionMapEntries) {
            if ((extensionNamespace == null) || (extensionNamespace.length() == 0) || (extensionMapEntries == null))
                return null;
            else {
                URI extensionNamespaceUri = URIs.makeURISafely(extensionNamespace);
                if (extensionNamespaceUri != null) {
                    Map<URI,Usage> map = new java.util.HashMap<URI,Usage>(extensionMapEntries.length);
                    for (Object[] entry : extensionMapEntries) {
                        String designator = (String) entry[0];
                        Usage usage = (Usage) entry[1];
                        map.put(extensionNamespaceUri.resolve(designator), usage);
                    }
                    return map;
                } else
                    return null;
            }
        }

    }

}