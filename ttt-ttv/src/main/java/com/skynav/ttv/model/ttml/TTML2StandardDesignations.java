/*
 * Copyright 2018-2020 Skynav, Inc. All rights reserved.
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

public class TTML2StandardDesignations extends TTML1StandardDesignations {

    private static final String[] ttml2FeatureDesignationStrings = new String[] {
        "#animate",
        "#animate-fill",
        "#animate-minimal",
        "#animate-paced",
        "#animate-spline",
        "#animate-repeat",
        "#animation",
        "#animation-out-of-line",
        "#animation-version-2",
        "#audio",
        "#audio-description",
        "#audio-speech",
        "#background",
        "#background-image",
        "#backgroundClip",
        "#backgroundColor-block",
        "#backgroundColor-inline",
        "#backgroundColor-region",
        "#backgroundColor",
        "#backgroundExtent",
        "#backgroundImage",
        "#backgroundOrigin",
        "#backgroundPosition",
        "#backgroundRepeat",
        "#base",
        "#base-general",
        "#base-version-2",
        "#bidi",
        "#bidi-version-2",
        "#border",
        "#border-block",
        "#border-inline",
        "#border-radii",
        "#border-radii-1",
        "#border-radii-2",
        "#border-region",
        "#bpd",
        "#cellResolution",
        "#chunk",
        "#clockMode",
        "#clockMode-gps",
        "#clockMode-local",
        "#clockMode-utc",
        "#color",
        "#condition",
        "#condition-fn-media",
        "#condition-fn-parameter",
        "#condition-fn-supports",
        "#condition-primary",
        "#content",
        "#content-sizing",
        "#contentProfiles",
        "#contentProfiles-combined",
        "#core",
        "#data",
        "#direction",
        "#disparity",
        "#display",
        "#display-block",
        "#display-inline",
        "#display-inlineBlock",
        "#display-region",
        "#display-version-2",
        "#displayAlign",
        "#displayAlign-block",
        "#displayAlign-justify",
        "#displayAlign-region",
        "#displayAlign-relative",
        "#displayAlign-version-2",
        "#displayAspectRatio",
        "#dropMode",
        "#dropMode-dropNTSC",
        "#dropMode-dropPAL",
        "#dropMode-nonDrop",
        "#embedded-audio",
        "#embedded-content",
        "#embedded-data",
        "#embedded-font",
        "#embedded-image",
        "#extent",
        "#extent-auto",
        "#extent-auto-version-2",
        "#extent-contain",
        "#extent-cover",
        "#extent-full-version-2",
        "#extent-image",
        "#extent-length",
        "#extent-length-version-2",
        "#extent-measure",
        "#extent-region",
        "#extent-region-version-2",
        "#extent-root",
        "#extent-root-version-2",
        "#extent-version-2",
        "#font",
        "#fontFamily",
        "#fontFamily-generic",
        "#fontFamily-non-generic",
        "#fontKerning",
        "#fontSelectionStrategy",
        "#fontSelectionStrategy-character",
        "#fontShear",
        "#fontSize",
        "#fontSize-anamorphic",
        "#fontSize-isomorphic",
        "#fontStyle",
        "#fontStyle-italic",
        "#fontStyle-oblique",
        "#fontVariant",
        "#fontWeight",
        "#fontWeight-bold",
        "#frameRate",
        "#frameRateMultiplier",
        "#gain",
        "#image",
        "#image-png",
        "#initial",
        "#ipd",
        "#layout",
        "#length",
        "#length-cell",
        "#length-em",
        "#length-integer",
        "#length-negative",
        "#length-percentage",
        "#length-pixel",
        "#length-positive",
        "#length-real",
        "#length-root-container-relative",
        "#length-version-2",
        "#letterSpacing",
        "#lineBreak-uax14",
        "#lineHeight",
        "#lineShear",
        "#luminance",
        "#markerMode",
        "#markerMode-continuous",
        "#markerMode-discontinuous",
        "#metadata",
        "#metadata-item",
        "#metadata-version-2",
        "#nested-div",
        "#nested-span",
        "#opacity",
        "#opacity-block",
        "#opacity-inline",
        "#opacity-region",
        "#opacity-version-2",
        "#origin",
        "#overflow",
        "#overflow-visible",
        "#padding",
        "#padding-1",
        "#padding-2",
        "#padding-3",
        "#padding-4",
        "#padding-block",
        "#padding-inline",
        "#padding-region",
        "#padding-version-2",
        "#pan",
        "#permitFeatureNarrowing",
        "#permitFeatureWidening",
        "#pitch",
        "#pixelAspectRatio",
        "#position",
        "#presentation",
        "#presentation-audio",
        "#presentation-version-2",
        "#processorProfiles",
        "#processorProfiles-combined",
        "#profile",
        "#profile-full-version-2",
        "#profile-nesting",
        "#profile-version-2",
        "#region-implied-animation",
        "#region-inline",
        "#region-timing",
        "#resources",
        "#ruby",
        "#ruby-full",
        "#rubyAlign",
        "#rubyAlign-minimal",
        "#rubyAlign-withBase",
        "#rubyPosition",
        "#rubyReserve",
        "#set",
        "#set-fill",
        "#set-multiple-styles",
        "#set-repeat",
        "#shear",
        "#showBackground",
        "#source",
        "#speak",
        "#speech",
        "#structure",
        "#styling",
        "#styling-chained",
        "#styling-inheritance-content",
        "#styling-inheritance-region",
        "#styling-inline",
        "#styling-nested",
        "#styling-referential",
        "#subFrameRate",
        "#textAlign",
        "#textAlign-absolute",
        "#textAlign-justify",
        "#textAlign-relative",
        "#textAlign-version-2",
        "#textCombine",
        "#textDecoration",
        "#textDecoration-over",
        "#textDecoration-through",
        "#textDecoration-under",
        "#textEmphasis",
        "#textEmphasis-color",
        "#textEmphasis-minimal",
        "#textEmphasis-quoted-string",
        "#textOrientation",
        "#textOrientation-sideways-LR",
        "#textOutline",
        "#textOutline-blurred",
        "#textOutline-unblurred",
        "#textShadow",
        "#tickRate",
        "#time-clock-with-frames",
        "#time-clock",
        "#time-offset-with-frames",
        "#time-offset-with-ticks",
        "#time-offset",
        "#time-wall-clock",
        "#timeBase-clock",
        "#timeBase-media",
        "#timeBase-smpte",
        "#timeContainer",
        "#timing",
        "#transformation",
        "#transformation-version-2",
        "#unicodeBidi",
        "#unicodeBidi-isolate",
        "#unicodeBidi-version-2",
        "#validation",
        "#visibility",
        "#visibility-block",
        "#visibility-image",
        "#visibility-inline",
        "#visibility-region",
        "#visibility-version-2",
        "#wrapOption",
        "#writingMode",
        "#writingMode-horizontal-lr",
        "#writingMode-horizontal-rl",
        "#writingMode-horizontal",
        "#writingMode-vertical",
        "#xlink",
        "#zIndex"
    };

    private static final String[] ttml2ExtensionDesignationStrings = new String[] {
        "#supported",                           // used for testing purposes
        "#unsupported"                          // used for testing purposes
    };

    public static TTML2StandardDesignations getInstance() {
        return new TTML2StandardDesignations();
    }

    @Override
    public String[] getFeatureDesignationStrings() {
        return ttml2FeatureDesignationStrings;
    }

    @Override
    public String[] getExtensionDesignationStrings() {
        return ttml2ExtensionDesignationStrings;
    }

}
