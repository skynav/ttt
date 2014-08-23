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

import com.skynav.ttv.model.Profile.Specification;
import com.skynav.ttv.model.Profile.Usage;

import static com.skynav.ttv.model.ttml.TTML1.Constants.*;

public class TTML1TransformationProfileSpecification extends Specification {
    
    private static Object[][] featureMapEntries = new Object[][] {
        { "#content", Usage.REQUIRED },
        { "#core", Usage.REQUIRED },
        { "#profile", Usage.REQUIRED },
        { "#structure", Usage.REQUIRED },
        { "#time-offset", Usage.REQUIRED },
        { "#timing", Usage.REQUIRED },
        { "#transformation", Usage.REQUIRED },
        { "#animation", Usage.OPTIONAL },
        { "#backgroundColor-block", Usage.OPTIONAL },
        { "#backgroundColor-inline", Usage.OPTIONAL },
        { "#backgroundColor-region", Usage.OPTIONAL },
        { "#backgroundColor", Usage.OPTIONAL },
        { "#bidi", Usage.OPTIONAL },
        { "#cellResolution", Usage.OPTIONAL },
        { "#clockMode-gps", Usage.OPTIONAL },
        { "#clockMode-local", Usage.OPTIONAL },
        { "#clockMode-utc", Usage.OPTIONAL },
        { "#clockMode", Usage.OPTIONAL },
        { "#color", Usage.OPTIONAL },
        { "#direction", Usage.OPTIONAL },
        { "#display-block", Usage.OPTIONAL },
        { "#display-inline", Usage.OPTIONAL },
        { "#display-region", Usage.OPTIONAL },
        { "#display", Usage.OPTIONAL },
        { "#displayAlign", Usage.OPTIONAL },
        { "#dropMode-dropNTSC", Usage.OPTIONAL },
        { "#dropMode-dropPAL", Usage.OPTIONAL },
        { "#dropMode-nonDrop", Usage.OPTIONAL },
        { "#dropMode", Usage.OPTIONAL },
        { "#extent-region", Usage.OPTIONAL },
        { "#extent-root", Usage.OPTIONAL },
        { "#extent", Usage.OPTIONAL },
        { "#fontFamily-generic", Usage.OPTIONAL },
        { "#fontFamily-non-generic", Usage.OPTIONAL },
        { "#fontFamily", Usage.OPTIONAL },
        { "#fontSize-anamorphic", Usage.OPTIONAL },
        { "#fontSize-isomorphic", Usage.OPTIONAL },
        { "#fontSize", Usage.OPTIONAL },
        { "#fontStyle-italic", Usage.OPTIONAL },
        { "#fontStyle-oblique", Usage.OPTIONAL },
        { "#fontStyle", Usage.OPTIONAL },
        { "#fontWeight-bold", Usage.OPTIONAL },
        { "#fontWeight", Usage.OPTIONAL },
        { "#frameRate", Usage.OPTIONAL },
        { "#frameRateMultiplier", Usage.OPTIONAL },
        { "#layout", Usage.OPTIONAL },
        { "#length-cell", Usage.OPTIONAL },
        { "#length-em", Usage.OPTIONAL },
        { "#length-integer", Usage.OPTIONAL },
        { "#length-negative", Usage.OPTIONAL },
        { "#length-percentage", Usage.OPTIONAL },
        { "#length-pixel", Usage.OPTIONAL },
        { "#length-positive", Usage.OPTIONAL },
        { "#length-real", Usage.OPTIONAL },
        { "#length", Usage.OPTIONAL },
        { "#lineBreak-uax14", Usage.OPTIONAL },
        { "#lineHeight", Usage.OPTIONAL },
        { "#markerMode-continuous", Usage.OPTIONAL },
        { "#markerMode-discontinuous", Usage.OPTIONAL },
        { "#markerMode", Usage.OPTIONAL },
        { "#metadata", Usage.OPTIONAL },
        { "#nested-div", Usage.OPTIONAL },
        { "#nested-span", Usage.OPTIONAL },
        { "#opacity", Usage.OPTIONAL },
        { "#origin", Usage.OPTIONAL },
        { "#overflow-visible", Usage.OPTIONAL },
        { "#overflow", Usage.OPTIONAL },
        { "#padding-1", Usage.OPTIONAL },
        { "#padding-2", Usage.OPTIONAL },
        { "#padding-3", Usage.OPTIONAL },
        { "#padding-4", Usage.OPTIONAL },
        { "#padding", Usage.OPTIONAL },
        { "#pixelAspectRatio", Usage.OPTIONAL },
        { "#presentation", Usage.OPTIONAL },
        { "#showBackground", Usage.OPTIONAL },
        { "#styling-chained", Usage.OPTIONAL },
        { "#styling-inheritance-content", Usage.OPTIONAL },
        { "#styling-inheritance-region", Usage.OPTIONAL },
        { "#styling-inline", Usage.OPTIONAL },
        { "#styling-nested", Usage.OPTIONAL },
        { "#styling-referential", Usage.OPTIONAL },
        { "#styling", Usage.OPTIONAL },
        { "#subFrameRate", Usage.OPTIONAL },
        { "#textAlign-absolute", Usage.OPTIONAL },
        { "#textAlign-relative", Usage.OPTIONAL },
        { "#textAlign", Usage.OPTIONAL },
        { "#textDecoration-over", Usage.OPTIONAL },
        { "#textDecoration-through", Usage.OPTIONAL },
        { "#textDecoration-under", Usage.OPTIONAL },
        { "#textDecoration", Usage.OPTIONAL },
        { "#textOutline-blurred", Usage.OPTIONAL },
        { "#textOutline-unblurred", Usage.OPTIONAL },
        { "#textOutline", Usage.OPTIONAL },
        { "#tickRate", Usage.OPTIONAL },
        { "#time-clock-with-frames", Usage.OPTIONAL },
        { "#time-clock", Usage.OPTIONAL },
        { "#time-offset-with-frames", Usage.OPTIONAL },
        { "#time-offset-with-ticks", Usage.OPTIONAL },
        { "#timeBase-clock", Usage.OPTIONAL },
        { "#timeBase-media", Usage.OPTIONAL },
        { "#timeBase-smpte", Usage.OPTIONAL },
        { "#timeContainer", Usage.OPTIONAL },
        { "#unicodeBidi", Usage.OPTIONAL },
        { "#visibility-block", Usage.OPTIONAL },
        { "#visibility-inline", Usage.OPTIONAL },
        { "#visibility-region", Usage.OPTIONAL },
        { "#visibility", Usage.OPTIONAL },
        { "#wrapOption", Usage.OPTIONAL },
        { "#writingMode-horizontal-lr", Usage.OPTIONAL },
        { "#writingMode-horizontal-rl", Usage.OPTIONAL },
        { "#writingMode-horizontal", Usage.OPTIONAL },
        { "#writingMode-vertical", Usage.OPTIONAL },
        { "#writingMode", Usage.OPTIONAL },
        { "#zIndex", Usage.OPTIONAL },
    };

    private static Object[][] extensionMapEntries = new Object[][] {
    };

    public TTML1TransformationProfileSpecification(URI profileUri) {
        super(profileUri, null, featuresMap(NAMESPACE_TT_FEATURE, featureMapEntries), extensionsMap(NAMESPACE_TT_EXTENSION, extensionMapEntries));
    }

}
