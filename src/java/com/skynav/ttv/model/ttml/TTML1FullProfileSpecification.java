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

public class TTML1FullProfileSpecification extends Specification {
    
    private static Object[][] featureMapEntries = new Object[][] {
        { "#animation", Usage.REQUIRED },
        { "#backgroundColor-block", Usage.REQUIRED },
        { "#backgroundColor-inline", Usage.REQUIRED },
        { "#backgroundColor-region", Usage.REQUIRED },
        { "#backgroundColor", Usage.REQUIRED },
        { "#bidi", Usage.REQUIRED },
        { "#cellResolution", Usage.REQUIRED },
        { "#clockMode-gps", Usage.REQUIRED },
        { "#clockMode-local", Usage.REQUIRED },
        { "#clockMode-utc", Usage.REQUIRED },
        { "#clockMode", Usage.REQUIRED },
        { "#color", Usage.REQUIRED },
        { "#content", Usage.REQUIRED },
        { "#core", Usage.REQUIRED },
        { "#direction", Usage.REQUIRED },
        { "#display-block", Usage.REQUIRED },
        { "#display-inline", Usage.REQUIRED },
        { "#display-region", Usage.REQUIRED },
        { "#display", Usage.REQUIRED },
        { "#displayAlign", Usage.REQUIRED },
        { "#dropMode-dropNTSC", Usage.REQUIRED },
        { "#dropMode-dropPAL", Usage.REQUIRED },
        { "#dropMode-nonDrop", Usage.REQUIRED },
        { "#dropMode", Usage.REQUIRED },
        { "#extent-region", Usage.REQUIRED },
        { "#extent-root", Usage.REQUIRED },
        { "#extent", Usage.REQUIRED },
        { "#fontFamily-generic", Usage.REQUIRED },
        { "#fontFamily-non-generic", Usage.REQUIRED },
        { "#fontFamily", Usage.REQUIRED },
        { "#fontSize-anamorphic", Usage.REQUIRED },
        { "#fontSize-isomorphic", Usage.REQUIRED },
        { "#fontSize", Usage.REQUIRED },
        { "#fontStyle-italic", Usage.REQUIRED },
        { "#fontStyle-oblique", Usage.REQUIRED },
        { "#fontStyle", Usage.REQUIRED },
        { "#fontWeight-bold", Usage.REQUIRED },
        { "#fontWeight", Usage.REQUIRED },
        { "#frameRate", Usage.REQUIRED },
        { "#frameRateMultiplier", Usage.REQUIRED },
        { "#layout", Usage.REQUIRED },
        { "#length-cell", Usage.REQUIRED },
        { "#length-em", Usage.REQUIRED },
        { "#length-integer", Usage.REQUIRED },
        { "#length-negative", Usage.REQUIRED },
        { "#length-percentage", Usage.REQUIRED },
        { "#length-pixel", Usage.REQUIRED },
        { "#length-positive", Usage.REQUIRED },
        { "#length-real", Usage.REQUIRED },
        { "#length", Usage.REQUIRED },
        { "#lineBreak-uax14", Usage.REQUIRED },
        { "#lineHeight", Usage.REQUIRED },
        { "#markerMode-continuous", Usage.REQUIRED },
        { "#markerMode-discontinuous", Usage.REQUIRED },
        { "#markerMode", Usage.REQUIRED },
        { "#metadata", Usage.REQUIRED },
        { "#nested-div", Usage.REQUIRED },
        { "#nested-span", Usage.REQUIRED },
        { "#opacity", Usage.REQUIRED },
        { "#origin", Usage.REQUIRED },
        { "#overflow-visible", Usage.REQUIRED },
        { "#overflow", Usage.REQUIRED },
        { "#padding-1", Usage.REQUIRED },
        { "#padding-2", Usage.REQUIRED },
        { "#padding-3", Usage.REQUIRED },
        { "#padding-4", Usage.REQUIRED },
        { "#padding", Usage.REQUIRED },
        { "#pixelAspectRatio", Usage.REQUIRED },
        { "#presentation", Usage.REQUIRED },
        { "#profile", Usage.REQUIRED },
        { "#showBackground", Usage.REQUIRED },
        { "#structure", Usage.REQUIRED },
        { "#styling-chained", Usage.REQUIRED },
        { "#styling-inheritance-content", Usage.REQUIRED },
        { "#styling-inheritance-region", Usage.REQUIRED },
        { "#styling-inline", Usage.REQUIRED },
        { "#styling-nested", Usage.REQUIRED },
        { "#styling-referential", Usage.REQUIRED },
        { "#styling", Usage.REQUIRED },
        { "#subFrameRate", Usage.REQUIRED },
        { "#textAlign-absolute", Usage.REQUIRED },
        { "#textAlign-relative", Usage.REQUIRED },
        { "#textAlign", Usage.REQUIRED },
        { "#textDecoration-over", Usage.REQUIRED },
        { "#textDecoration-through", Usage.REQUIRED },
        { "#textDecoration-under", Usage.REQUIRED },
        { "#textDecoration", Usage.REQUIRED },
        { "#textOutline-blurred", Usage.REQUIRED },
        { "#textOutline-unblurred", Usage.REQUIRED },
        { "#textOutline", Usage.REQUIRED },
        { "#tickRate", Usage.REQUIRED },
        { "#time-clock-with-frames", Usage.REQUIRED },
        { "#time-clock", Usage.REQUIRED },
        { "#time-offset-with-frames", Usage.REQUIRED },
        { "#time-offset-with-ticks", Usage.REQUIRED },
        { "#time-offset", Usage.REQUIRED },
        { "#timeBase-clock", Usage.REQUIRED },
        { "#timeBase-media", Usage.REQUIRED },
        { "#timeBase-smpte", Usage.REQUIRED },
        { "#timeContainer", Usage.REQUIRED },
        { "#timing", Usage.REQUIRED },
        { "#transformation", Usage.REQUIRED },
        { "#unicodeBidi", Usage.REQUIRED },
        { "#visibility-block", Usage.REQUIRED },
        { "#visibility-inline", Usage.REQUIRED },
        { "#visibility-region", Usage.REQUIRED },
        { "#visibility", Usage.REQUIRED },
        { "#wrapOption", Usage.REQUIRED },
        { "#writingMode-horizontal-lr", Usage.REQUIRED },
        { "#writingMode-horizontal-rl", Usage.REQUIRED },
        { "#writingMode-horizontal", Usage.REQUIRED },
        { "#writingMode-vertical", Usage.REQUIRED },
        { "#writingMode", Usage.REQUIRED },
        { "#zIndex", Usage.REQUIRED },
    };

    private static Object[][] extensionMapEntries = new Object[][] {
    };

    public TTML1FullProfileSpecification(URI profileUri) {
        super(profileUri, null, featuresMap(NAMESPACE_TT_FEATURE, featureMapEntries), extensionsMap(NAMESPACE_TT_EXTENSION, extensionMapEntries));
    }

}
