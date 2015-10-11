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

package com.skynav.ttv.model.imsc;

import java.net.URI;
import java.util.Map;

import com.skynav.ttv.model.Profile.Specification;
import com.skynav.ttv.model.Profile.Usage;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.ttml.TTML1;

import static com.skynav.ttv.model.imsc.IMSC1.Constants.NAMESPACE_EXTENSION;

public class IMSC1ImageProfileSpecification extends Specification {

    private static final Object[][] featureMapEntries = new Object[][] {
        { "#animation", Usage.REQUIRED },
        { "#backgroundColor", Usage.OPTIONAL },
        { "#backgroundColor-block", Usage.OPTIONAL },
        { "#backgroundColor-inline", Usage.OPTIONAL },
        { "#backgroundColor-region", Usage.OPTIONAL },
        { "#bidi", Usage.OPTIONAL },
        { "#cellResolution", Usage.REQUIRED },
        { "#clockMode", Usage.OPTIONAL },
        { "#clockMode-gps", Usage.OPTIONAL },
        { "#clockMode-local", Usage.OPTIONAL },
        { "#clockMode-utc", Usage.OPTIONAL },
        { "#color", Usage.OPTIONAL },
        { "#content", Usage.REQUIRED },
        { "#core", Usage.REQUIRED },
        { "#direction", Usage.OPTIONAL },
        { "#display", Usage.REQUIRED },
        { "#display-block", Usage.REQUIRED },
        { "#display-inline", Usage.REQUIRED },
        { "#display-region", Usage.REQUIRED },
        { "#displayAlign", Usage.OPTIONAL },
        { "#dropMode", Usage.OPTIONAL },
        { "#dropMode-dropNTSC", Usage.OPTIONAL },
        { "#dropMode-dropPAL", Usage.OPTIONAL },
        { "#dropMode-nonDrop", Usage.OPTIONAL },
        { "#extent", Usage.REQUIRED },
        { "#extent-region", Usage.REQUIRED },
        { "#extent-root", Usage.REQUIRED },
        { "#fontFamily", Usage.OPTIONAL },
        { "#fontFamily-generic", Usage.OPTIONAL },
        { "#fontFamily-non-generic", Usage.OPTIONAL },
        { "#fontSize", Usage.OPTIONAL },
        { "#fontSize-anamorphic", Usage.OPTIONAL },
        { "#fontSize-isomorphic", Usage.OPTIONAL },
        { "#fontStyle", Usage.OPTIONAL },
        { "#fontStyle-italic", Usage.OPTIONAL },
        { "#fontStyle-oblique", Usage.OPTIONAL },
        { "#fontWeight", Usage.OPTIONAL },
        { "#fontWeight-bold", Usage.OPTIONAL },
        { "#frameRate", Usage.REQUIRED },
        { "#frameRateMultiplier", Usage.REQUIRED },
        { "#layout", Usage.REQUIRED },
        { "#length", Usage.REQUIRED },
        { "#length-cell", Usage.REQUIRED },
        { "#length-em", Usage.REQUIRED },
        { "#length-integer", Usage.REQUIRED },
        { "#length-negative", Usage.REQUIRED },
        { "#length-percentage", Usage.REQUIRED },
        { "#length-pixel", Usage.REQUIRED },
        { "#length-positive", Usage.REQUIRED },
        { "#length-real", Usage.REQUIRED },
        { "#lineBreak-uax14", Usage.OPTIONAL },
        { "#lineHeight", Usage.OPTIONAL },
        { "#markerMode", Usage.OPTIONAL },
        { "#markerMode-continuous", Usage.OPTIONAL },
        { "#markerMode-discontinuous", Usage.OPTIONAL },
        { "#metadata", Usage.REQUIRED },
        { "#nested-div", Usage.OPTIONAL },
        { "#nested-span", Usage.OPTIONAL },
        { "#opacity", Usage.REQUIRED },
        { "#origin", Usage.REQUIRED },
        { "#overflow", Usage.REQUIRED },
        { "#overflow-visible", Usage.REQUIRED },
        { "#padding", Usage.OPTIONAL },
        { "#padding-1", Usage.OPTIONAL },
        { "#padding-2", Usage.OPTIONAL },
        { "#padding-3", Usage.OPTIONAL },
        { "#padding-4", Usage.OPTIONAL },
        { "#pixelAspectRatio", Usage.OPTIONAL },
        { "#presentation", Usage.REQUIRED },
        { "#profile", Usage.REQUIRED },
        { "#showBackground", Usage.REQUIRED },
        { "#structure", Usage.REQUIRED },
        { "#styling", Usage.REQUIRED },
        { "#styling-chained", Usage.REQUIRED },
        { "#styling-inheritance-content", Usage.REQUIRED },
        { "#styling-inheritance-region", Usage.REQUIRED },
        { "#styling-inline", Usage.REQUIRED },
        { "#styling-nested", Usage.REQUIRED },
        { "#styling-referential", Usage.REQUIRED },
        { "#subFrameRate", Usage.OPTIONAL },
        { "#textAlign", Usage.OPTIONAL },
        { "#textAlign-absolute", Usage.OPTIONAL },
        { "#textAlign-relative", Usage.OPTIONAL },
        { "#textDecoration", Usage.OPTIONAL },
        { "#textDecoration-over", Usage.OPTIONAL },
        { "#textDecoration-through", Usage.OPTIONAL },
        { "#textDecoration-under", Usage.OPTIONAL },
        { "#textOutline", Usage.OPTIONAL },
        { "#textOutline-blurred", Usage.OPTIONAL },
        { "#textOutline-unblurred", Usage.OPTIONAL },
        { "#tickRate", Usage.REQUIRED },
        { "#time-clock", Usage.REQUIRED },
        { "#time-clock-with-frames", Usage.REQUIRED },
        { "#time-offset", Usage.REQUIRED },
        { "#time-offset-with-frames", Usage.REQUIRED },
        { "#time-offset-with-ticks", Usage.REQUIRED },
        { "#timeBase-clock", Usage.OPTIONAL },
        { "#timeBase-media", Usage.REQUIRED },
        { "#timeBase-smpte", Usage.OPTIONAL },
        { "#timeContainer", Usage.REQUIRED },
        { "#timing", Usage.REQUIRED },
        { "#transformation", Usage.REQUIRED },
        { "#unicodeBidi", Usage.OPTIONAL },
        { "#visibility", Usage.REQUIRED },
        { "#visibility-block", Usage.REQUIRED },
        { "#visibility-inline", Usage.REQUIRED },
        { "#visibility-region", Usage.REQUIRED },
        { "#wrapOption", Usage.OPTIONAL },
        { "#writingMode", Usage.OPTIONAL },
        { "#writingMode-horizontal", Usage.REQUIRED },
        { "#writingMode-horizontal-lr", Usage.REQUIRED },
        { "#writingMode-horizontal-rl", Usage.REQUIRED },
        { "#writingMode-vertical", Usage.OPTIONAL },
        { "#zIndex", Usage.REQUIRED },
    };

    private static final Object[][] smpteExtensionMapEntries = new Object[][] {
        { "#image", Usage.REQUIRED },
    };

    private static final Object[][] imsc1ExtensionMapEntries = new Object[][] {
        { "#altText", Usage.REQUIRED },
        { "#aspectRatio", Usage.REQUIRED },
        { "#forcedDisplay", Usage.REQUIRED },
        { "#linePadding", Usage.OPTIONAL },
        { "#multiRowAlign", Usage.OPTIONAL },
        { "#progressivelyDecodable", Usage.REQUIRED },
    };

    public IMSC1ImageProfileSpecification(URI profileUri) {
        super(profileUri, null, featuresMap(TTML1.Constants.NAMESPACE_TT_FEATURE, featureMapEntries), makeExtensionsMap());
    }

    private static Map<URI,Usage> makeExtensionsMap() {
        Map<URI,Usage> fm = new java.util.HashMap<URI,Usage>();
        fm.putAll(featuresMap(ST20522010.Constants.NAMESPACE_2010_EXTENSION, smpteExtensionMapEntries));
        fm.putAll(featuresMap(NAMESPACE_EXTENSION, imsc1ExtensionMapEntries));
        return fm;
    }

}
