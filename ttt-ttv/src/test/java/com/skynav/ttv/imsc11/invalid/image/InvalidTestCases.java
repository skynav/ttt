/*
 * Copyright 2013-2019 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.imsc11.invalid.image;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidIMSC11ActiveAreaBadUnits() throws Exception {
        performInvalidityTest("imsc11-invld-active-area-bad-units.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ActiveAreaMissingComponent() throws Exception {
        performInvalidityTest("imsc11-invld-active-area-missing-component.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ActiveAreaMixedUnits() throws Exception {
        performInvalidityTest("imsc11-invld-active-area-mixed-units.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11AspectRatioMissingComponent() throws Exception {
        performInvalidityTest("imsc11-invld-aspect-ratio-missing-component.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11AspectRatioNegativeDenominator() throws Exception {
        performInvalidityTest("imsc11-invld-aspect-ratio-negative-denominator.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11AspectRatioNegativeNumerator() throws Exception {
        performInvalidityTest("imsc11-invld-aspect-ratio-negative-numerator.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11AspectRatioZeroDenominator() throws Exception {
        performInvalidityTest("imsc11-invld-aspect-ratio-zero-denominator.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11AspectRatioZeroNumerator() throws Exception {
        performInvalidityTest("imsc11-invld-aspect-ratio-zero-numerator.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Audio() throws Exception {
        performInvalidityTest("imsc11-invld-audio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundClip() throws Exception {
        performInvalidityTest("imsc11-invld-background-clip.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundExtent() throws Exception {
        performInvalidityTest("imsc11-invld-background-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundImage() throws Exception {
        performInvalidityTest("imsc11-invld-background-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundOrigin() throws Exception {
        performInvalidityTest("imsc11-invld-background-origin.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundPosition() throws Exception {
        performInvalidityTest("imsc11-invld-background-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BackgroundRepeat() throws Exception {
        performInvalidityTest("imsc11-invld-background-repeat.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Border() throws Exception {
        performInvalidityTest("imsc11-invld-border.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BPD() throws Exception {
        performInvalidityTest("imsc11-invld-bpd.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Break() throws Exception {
        performInvalidityTest("imsc11-invld-break.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Chunk() throws Exception {
        performInvalidityTest("imsc11-invld-chunk.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ClockModeLocal() throws Exception {
        performInvalidityTest("imsc11-invld-clock-mode-local.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ClockModeUTC() throws Exception {
        performInvalidityTest("imsc11-invld-clock-mode-utc.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Color() throws Exception {
        performInvalidityTest("imsc11-invld-color.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ConditionMedia() throws Exception {
        performInvalidityTest("imsc11-invld-condition-media.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ConditionParameter() throws Exception {
        performInvalidityTest("imsc11-invld-condition-parameter.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ConditionPrimary() throws Exception {
        performInvalidityTest("imsc11-invld-condition-primary.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ConditionSupports() throws Exception {
        performInvalidityTest("imsc11-invld-condition-supports.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ContentProfileCombination() throws Exception {
        performInvalidityTest("imsc11-invld-content-profile-combination.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Data() throws Exception {
        performInvalidityTest("imsc11-invld-data.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Direction() throws Exception {
        performInvalidityTest("imsc11-invld-direction.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11DisplayAlign() throws Exception {
        performInvalidityTest("imsc11-invld-display-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11DropModeNone() throws Exception {
        performInvalidityTest("imsc11-invld-drop-mode-none.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11DropModeNTSC() throws Exception {
        performInvalidityTest("imsc11-invld-drop-mode-ntsc.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11DropModePAL() throws Exception {
        performInvalidityTest("imsc11-invld-drop-mode-pal.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11EmUnit() throws Exception {
        performInvalidityTest("imsc11-invld-em-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ExtentLengthUnit() throws Exception {
        performInvalidityTest("imsc11-invld-extent-length-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Font() throws Exception {
        performInvalidityTest("imsc11-invld-font.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontFamily() throws Exception {
        performInvalidityTest("imsc11-invld-font-family.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontKerning() throws Exception {
        performInvalidityTest("imsc11-invld-font-kerning.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontSelectionStrategy() throws Exception {
        performInvalidityTest("imsc11-invld-font-selection-strategy.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontShear() throws Exception {
        performInvalidityTest("imsc11-invld-font-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontSize() throws Exception {
        performInvalidityTest("imsc11-invld-font-size.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontStyle() throws Exception {
        performInvalidityTest("imsc11-invld-font-style.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontVariant() throws Exception {
        performInvalidityTest("imsc11-invld-font-variant.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11FontWeight() throws Exception {
        performInvalidityTest("imsc11-invld-font-weight.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Gain() throws Exception {
        performInvalidityTest("imsc11-invld-gain.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Image() throws Exception {
        performInvalidityTest("imsc11-invld-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11InferProcessorProfileMethod() throws Exception {
        performInvalidityTest("imsc11-invld-infer-processor-profile-method.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11InferProcessorProfileSource() throws Exception {
        performInvalidityTest("imsc11-invld-infer-processor-profile-source.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Initial() throws Exception {
        performInvalidityTest("imsc11-invld-initial.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11IPD() throws Exception {
        performInvalidityTest("imsc11-invld-ipd.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11LetterSpacing() throws Exception {
        performInvalidityTest("imsc11-invld-letter-spacing.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11LineHeight() throws Exception {
        performInvalidityTest("imsc11-invld-line-height.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11LineShear() throws Exception {
        performInvalidityTest("imsc11-invld-line-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11MarkerModeContinuous() throws Exception {
        performInvalidityTest("imsc11-invld-marker-mode-continuous.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11MarkerModeDiscontinuous() throws Exception {
        performInvalidityTest("imsc11-invld-marker-mode-discontinuous.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NestedDivision() throws Exception {
        performInvalidityTest("imsc11-invld-nested-division.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NestedSpan() throws Exception {
        performInvalidityTest("imsc11-invld-nested-span.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11OriginLengthUnit() throws Exception {
        performInvalidityTest("imsc11-invld-origin-length-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Padding() throws Exception {
        performInvalidityTest("imsc11-invld-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Pan() throws Exception {
        performInvalidityTest("imsc11-invld-pan.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Paragraph() throws Exception {
        performInvalidityTest("imsc11-invld-paragraph.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11PermitFeatureNarrowing() throws Exception {
        performInvalidityTest("imsc11-invld-permit-feature-narrowing.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11PermitFeatureWidening() throws Exception {
        performInvalidityTest("imsc11-invld-permit-feature-widening.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Pitch() throws Exception {
        performInvalidityTest("imsc11-invld-pitch.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11PixelAspectRatio() throws Exception {
        performInvalidityTest("imsc11-invld-pixel-aspect-ratio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Position() throws Exception {
        performInvalidityTest("imsc11-invld-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProcessorProfiles() throws Exception {
        performInvalidityTest("imsc11-invld-processor-profiles.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProcessorProfileCombination() throws Exception {
        performInvalidityTest("imsc11-invld-processor-profile-combination.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProgressivelyDecodableCase() throws Exception {
        performInvalidityTest("imsc11-invld-progressively-decodable-case.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProgressivelyDecodableEmpty() throws Exception {
        performInvalidityTest("imsc11-invld-progressively-decodable-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProgressivelyDecodableUnknown() throws Exception {
        performInvalidityTest("imsc11-invld-progressively-decodable-unknown.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Resources() throws Exception {
        performInvalidityTest("imsc11-invld-resources.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Ruby() throws Exception {
        performInvalidityTest("imsc11-invld-ruby.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11RubyAlign() throws Exception {
        performInvalidityTest("imsc11-invld-ruby-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11RubyPosition() throws Exception {
        performInvalidityTest("imsc11-invld-ruby-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11RubyReserve() throws Exception {
        performInvalidityTest("imsc11-invld-ruby-reserve.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Shear() throws Exception {
        performInvalidityTest("imsc11-invld-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11SMPTEBackgroundImageHorizontal() throws Exception {
        performInvalidityTest("imsc11-invld-smpte-background-image-horizontal.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11SMPTEBackgroundImageVertical() throws Exception {
        performInvalidityTest("imsc11-invld-smpte-background-image-vertical.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11SMPTEImage() throws Exception {
        performInvalidityTest("imsc11-invld-smpte-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Source() throws Exception {
        performInvalidityTest("imsc11-invld-source.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Span() throws Exception {
        performInvalidityTest("imsc11-invld-span.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Speak() throws Exception {
        performInvalidityTest("imsc11-invld-speak.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11SubFrameRate() throws Exception {
        performInvalidityTest("imsc11-invld-sub-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextAlign() throws Exception {
        performInvalidityTest("imsc11-invld-text-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextCombine() throws Exception {
        performInvalidityTest("imsc11-invld-text-combine.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextDecoration() throws Exception {
        performInvalidityTest("imsc11-invld-text-decoration.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextEmphasis() throws Exception {
        performInvalidityTest("imsc11-invld-text-emphasis.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextOrientation() throws Exception {
        performInvalidityTest("imsc11-invld-text-orientation.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextOutline() throws Exception {
        performInvalidityTest("imsc11-invld-text-outline.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11TextShadow() throws Exception {
        performInvalidityTest("imsc11-invld-text-shadow.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UnicodeBidi() throws Exception {
        performInvalidityTest("imsc11-invld-unicode-bidi.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11Validation() throws Exception {
        performInvalidityTest("imsc11-invld-validation.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ValidationAction() throws Exception {
        performInvalidityTest("imsc11-invld-validation-action.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11WrapOption() throws Exception {
        performInvalidityTest("imsc11-invld-wrap-option.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11WritingMode() throws Exception {
        performInvalidityTest("imsc11-invld-writing-mode.xml", -1, -1);
    }

    private void performInvalidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
        args.add("-q");
        args.add("-v");
        if (expectedErrors >= 0) {
            args.add("--expect-errors");
            args.add(Integer.toString(expectedErrors));
        }
        if (expectedWarnings >= 0) {
            args.add("--expect-warnings");
            args.add(Integer.toString(expectedWarnings));
        }
        args.add(urlString);
        TimedTextVerifier ttv = new TimedTextVerifier();
        ttv.run(args.toArray(new String[args.size()]));
        int resultCode = ttv.getResultCode(urlString);
        int resultFlags = ttv.getResultFlags(urlString);
        if (resultCode == TimedTextVerifier.RV_PASS) {
            if (((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) == 0) && (expectedErrors >= 0)) {
                fail("Unexpected success without expected error(s) match.");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected success with unexpected warning(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected success with expected warning(s) mismatch.");
            }
        } else if (resultCode == TimedTextVerifier.RV_FAIL) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected error(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected error(s) mismatch.");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_UNEXPECTED) != 0) {
                fail("Unexpected failure with unexpected warning(s).");
            }
            if ((resultFlags & TimedTextVerifier.RV_FLAG_WARNING_EXPECTED_MISMATCH) != 0) {
                fail("Unexpected failure with expected warning(s) mismatch.");
            }
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}
