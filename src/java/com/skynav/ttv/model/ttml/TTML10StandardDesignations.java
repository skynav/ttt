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
import java.util.Set;

import com.skynav.ttv.model.Profile;

public class TTML10StandardDesignations extends Profile.StandardDesignations {
    
    private URI featureNamespaceUri;
    private Set<URI> featureDesignations;
    private static final String[] featureDesignationStrings = new String[] {
        "#animation",
        "#backgroundColor-block",
        "#backgroundColor-inline",
        "#backgroundColor-region",
        "#backgroundColor",
        "#bidi",
        "#cellResolution",
        "#clockMode-gps",
        "#clockMode-local",
        "#clockMode-utc",
        "#clockMode",
        "#color",
        "#content",
        "#core",
        "#direction",
        "#display-block",
        "#display-inline",
        "#display-region",
        "#display",
        "#displayAlign",
        "#dropMode-dropNTSC",
        "#dropMode-dropPAL",
        "#dropMode-nonDrop",
        "#dropMode",
        "#extent-region",
        "#extent-root",
        "#extent",
        "#fontFamily-generic",
        "#fontFamily-non-generic",
        "#fontFamily",
        "#fontSize-anamorphic",
        "#fontSize-isomorphic",
        "#fontSize",
        "#fontStyle-italic",
        "#fontStyle-oblique",
        "#fontStyle",
        "#fontWeight-bold",
        "#fontWeight",
        "#frameRate",
        "#frameRateMultiplier",
        "#layout",
        "#length-cell",
        "#length-em",
        "#length-integer",
        "#length-negative",
        "#length-percentage",
        "#length-pixel",
        "#length-positive",
        "#length-real",
        "#length",
        "#lineBreak-uax14",
        "#lineHeight",
        "#markerMode-continuous",
        "#markerMode-discontinuous",
        "#markerMode",
        "#metadata",
        "#nested-div",
        "#nested-span",
        "#opacity",
        "#origin",
        "#overflow-visible",
        "#overflow",
        "#padding-1",
        "#padding-2",
        "#padding-3",
        "#padding-4",
        "#padding",
        "#pixelAspectRatio",
        "#presentation",
        "#profile",
        "#showBackground",
        "#structure",
        "#styling-chained",
        "#styling-inheritance-content",
        "#styling-inheritance-region",
        "#styling-inline",
        "#styling-nested",
        "#styling-referential",
        "#styling",
        "#subFrameRate",
        "#textAlign-absolute",
        "#textAlign-relative",
        "#textAlign",
        "#textDecoration-over",
        "#textDecoration-through",
        "#textDecoration-under",
        "#textDecoration",
        "#textOutline-blurred",
        "#textOutline-unblurred",
        "#textOutline",
        "#tickRate",
        "#time-clock-with-frames",
        "#time-clock",
        "#time-offset-with-frames",
        "#time-offset-with-ticks",
        "#time-offset",
        "#timeBase-clock",
        "#timeBase-media",
        "#timeBase-smpte",
        "#timeContainer",
        "#timing",
        "#transformation",
        "#unicodeBidi",
        "#visibility-block",
        "#visibility-inline",
        "#visibility-region",
        "#visibility",
        "#wrapOption",
        "#writingMode-horizontal-lr",
        "#writingMode-horizontal-rl",
        "#writingMode-horizontal",
        "#writingMode-vertical",
        "#writingMode",
        "#zIndex",
    };

    private URI extensionNamespaceUri;
    private Set<URI> extensionDesignations;
    private static final String[] extensionDesignationStrings = new String[] {
    };

    private TTML10StandardDesignations(URI featureNamespaceUri, URI extensionNamespaceUri) {
        Set<URI> featureDesignations = new java.util.HashSet<URI>(featureDesignationStrings.length);
        for (String designation : featureDesignationStrings) {
            featureDesignations.add(featureNamespaceUri.resolve(designation));
        }
        this.featureDesignations = featureDesignations;
        this.featureNamespaceUri = featureNamespaceUri;

        Set<URI> extensionDesignations = new java.util.HashSet<URI>(extensionDesignationStrings.length);
        for (String designation : extensionDesignationStrings) {
            extensionDesignations.add(extensionNamespaceUri.resolve(designation));
        }
        this.extensionDesignations = extensionDesignations;
        this.extensionNamespaceUri = extensionNamespaceUri;
    }
    
    private static TTML10StandardDesignations instance;
    public static TTML10StandardDesignations getInstance(URI featureNamespaceUri, URI extensionNamespaceUri) {
        if (instance == null)
            instance = new TTML10StandardDesignations(featureNamespaceUri, extensionNamespaceUri);
        else {
            assert instance.featureNamespaceUri.equals(featureNamespaceUri);
            assert instance.extensionNamespaceUri.equals(extensionNamespaceUri);
        }
        return instance;
    }

    @Override
    public boolean isStandardFeatureDesignation(URI uri) {
        return featureDesignations.contains(uri);
    }

    @Override
    public boolean isStandardExtensionDesignation(URI uri) {
        return extensionDesignations.contains(uri);
    }

}
