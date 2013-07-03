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
import java.util.Map;

import com.skynav.ttv.model.Profile.Specification;
import com.skynav.ttv.model.Profile.Usage;

public class TTML10SDPUSProfileSpecification extends Specification {
    
    public TTML10SDPUSProfileSpecification(URI profileUri, URI featureNamespaceUri, URI extensionNamespaceUri) {
        super(profileUri, null, featuresMap(featureNamespaceUri), extensionsMap(extensionNamespaceUri));
    }

    private static Object[][] featureMapEntries = new Object[][] {
        { "#animation", Usage.REQUIRED },
        { "#backgroundColor-block", Usage.REQUIRED },
        { "#backgroundColor-inline", Usage.REQUIRED },
        { "#color", Usage.REQUIRED },
        { "#content", Usage.REQUIRED },
        { "#core", Usage.REQUIRED },
        { "#display-region", Usage.REQUIRED },
        { "#displayAlign", Usage.REQUIRED },
        { "#fontFamily-generic", Usage.REQUIRED },
        { "#fontSize", Usage.REQUIRED },
        { "#fontStyle-italic", Usage.REQUIRED },
        { "#frameRate", Usage.REQUIRED },
        { "#frameRateMultiplier", Usage.REQUIRED },
        { "#layout", Usage.REQUIRED },
        { "#length-percentage", Usage.REQUIRED },
        { "#length-positive", Usage.REQUIRED },
        { "#length-real", Usage.REQUIRED },
        { "#lineBreak-uax14", Usage.REQUIRED },
        { "#presentation", Usage.REQUIRED },
        { "#profile", Usage.REQUIRED },
        { "#structure", Usage.REQUIRED },
        { "#styling", Usage.REQUIRED },
        { "#styling-inheritance-content", Usage.REQUIRED },
        { "#styling-inheritance-region", Usage.REQUIRED },
        { "#styling-inline", Usage.REQUIRED },
        { "#styling-referential", Usage.REQUIRED },
        { "#textAlign-absolute", Usage.REQUIRED },
        { "#textDecoration-under", Usage.REQUIRED },
        { "#textOutline-unblurred", Usage.REQUIRED },
        { "#time-offset", Usage.REQUIRED },
        { "#timing", Usage.REQUIRED },
        { "#writingMode-horizontal-lr", Usage.REQUIRED },
    };
    private static Map<URI,Usage> featuresMap(URI featureNamespaceUri) {
        Map<URI,Usage> map = new java.util.HashMap<URI,Usage>(featureMapEntries.length);
        for (Object[] entry : featureMapEntries) {
            String designator = (String) entry[0];
            Usage usage = (Usage) entry[1];
            map.put(featureNamespaceUri.resolve(designator), usage);
        }
        return map;
    }

    private static Object[][] extensionMapEntries = new Object[][] {
    };
    private static Map<URI,Usage> extensionsMap(URI extensionNamespaceUri) {
        Map<URI,Usage> map = new java.util.HashMap<URI,Usage>(extensionMapEntries.length);
        for (Object[] entry : extensionMapEntries) {
            String designator = (String) entry[0];
            Usage usage = (Usage) entry[1];
            map.put(extensionNamespaceUri.resolve(designator), usage);
        }
        return map;
    }

}
