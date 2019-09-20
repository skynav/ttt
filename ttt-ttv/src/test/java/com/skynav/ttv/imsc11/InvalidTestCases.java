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
 
package com.skynav.ttv.imsc11;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidIMSC11BadEBUTTSLinePadding() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-ebutts-line-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSLinePaddingUsageContext() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-ebutts-line-padding-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSMultiRowAlign() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-ebutts-multirow-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSMultiRowAlignUsageContext() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-ebutts-multirow-align-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEncoding() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-encoding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadProfileAttribute() throws Exception {
        performInvalidityTest("imsc11-invalid-bad-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11MissingRegionExtent() throws Exception {
        performInvalidityTest("imsc11-invalid-missing-region-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedClockMode() throws Exception {
        performInvalidityTest("imsc11-invalid-not-permitted-clock-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedDropMode() throws Exception {
        performInvalidityTest("imsc11-invalid-not-permitted-drop-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedMarkerMode() throws Exception {
        performInvalidityTest("imsc11-invalid-not-permitted-marker-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedPixelAspectRatio() throws Exception {
        performInvalidityTest("imsc11-invalid-not-permitted-pixel-aspect-ratio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedSubFrameRate() throws Exception {
        performInvalidityTest("imsc11-invalid-not-permitted-sub-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundClipInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-clip-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundClipInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-clip-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundExtentInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-extent-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundExtentInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-extent-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundImageInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-image-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundImageInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-image-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundOriginInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-origin-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundOriginInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-origin-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundPositionInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-position-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundPositionInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-position-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundRepeatInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-repeat-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundRepeatInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-background-repeat-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBorderInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-border-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBorderInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-border-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBPDInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-bpd-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBPDInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-bpd-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBreakInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-break-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInExtentInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-extent-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInFontSizeInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-font-size-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInLineHeightInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-line-height-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInOriginInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-origin-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInPaddingInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-padding-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInTextOutlineInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-cell-unit-in-text-outline-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedColorInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-color-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedDirectionInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-direction-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedDisplayAlignInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-display-align-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedEmUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-em-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedExtentLengthUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-extent-length-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedExtentLengthUnitInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-extent-length-unit-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontFamilyInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-family-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontKerningInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-kerning-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontKerningInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-kerning-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSelectionStrategyInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-selection-strategy-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSelectionStrategyInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-selection-strategy-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontShearInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-shear-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontShearInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-shear-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSizeAnamorphic() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-size-anamorphic.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSizeInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-size-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontStyleInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-style-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontVariantInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-variant-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontVariantInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-variant-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontWeightInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-weight-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedIPDInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ipd-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedIPDInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ipd-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLetterSpacingInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-letter-spacing-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLetterSpacingInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-letter-spacing-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineHeightInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-line-height-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineShearInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-line-shear-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineShearInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-line-shear-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedNestedDivisionInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-nested-division-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedNestedSpanInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-nested-span-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedOriginLengthUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-origin-length-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedOriginLengthUnitInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-origin-length-unit-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedPaddingInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-padding-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedParagraphInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-paragraph-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ruby-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyAlignInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ruby-align-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyPositionInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ruby-position-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyReserveInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-ruby-reserve-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageHorizontalInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-background-image-horizontal-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageHorizontalInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-background-image-horizontal-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-background-image-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageVerticalInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-background-image-vertical-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageVerticalInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-background-image-vertical-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEImageInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-image-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEImageInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-smpte-image-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSpanInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-span-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextAlignInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-align-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextCombineInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-combine-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextDecorationInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-decoration-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextEmphasisInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-emphasis-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOrientationInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-orientation-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOrientationInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-orientation-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOutlineBlur() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-outline-blur.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOutlineInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-outline-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextShadowInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-shadow-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTimeBaseClock() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-time-base-clock.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTimeBaseSMPTE() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-time-base-smpte.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedUnicodeBidiInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-unicode-bidi-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedWrapOptionInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-wrap-option-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedWritingModeInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-writing-mode-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11RegionNotInRootContainer() throws Exception {
        performInvalidityTest("imsc11-invalid-region-not-in-root-container.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesFramesComponentWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc11-invalid-uses-frames-component-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesFramesMetricWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc11-invalid-uses-frames-metric-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesNegativeLength() throws Exception {
        performInvalidityTest("imsc11-invalid-uses-negative-length.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesPixelUnitWithoutRootExtent() throws Exception {
        performInvalidityTest("imsc11-invalid-uses-pixel-unit-without-root-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesTicksMetricWithoutTickRate() throws Exception {
        performInvalidityTest("imsc11-invalid-uses-ticks-metric-without-tick-rate.xml", -1, -1);
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
