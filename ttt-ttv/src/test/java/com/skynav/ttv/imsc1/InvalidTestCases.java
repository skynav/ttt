/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.imsc1;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidIMSC1BadEBUTTSLinePadding() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-ebutts-line-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1BadEBUTTSLinePaddingUsageContext() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-ebutts-line-padding-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1BadEBUTTSMultiRowAlign() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-ebutts-multirow-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1BadEBUTTSMultiRowAlignUsageContext() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-ebutts-multirow-align-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1BadEncoding() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-encoding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1BadProfileAttribute() throws Exception {
        performInvalidityTest("imsc1-invalid-bad-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1MissingRegionExtent() throws Exception {
        performInvalidityTest("imsc1-invalid-missing-region-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1NotPermittedClockMode() throws Exception {
        performInvalidityTest("imsc1-invalid-not-permitted-clock-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1NotPermittedDropMode() throws Exception {
        performInvalidityTest("imsc1-invalid-not-permitted-drop-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1NotPermittedMarkerMode() throws Exception {
        performInvalidityTest("imsc1-invalid-not-permitted-marker-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1NotPermittedPixelAspectRatio() throws Exception {
        performInvalidityTest("imsc1-invalid-not-permitted-pixel-aspect-ratio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1NotPermittedSubFrameRate() throws Exception {
        performInvalidityTest("imsc1-invalid-not-permitted-sub-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedBreakInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-break-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInExtentInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-extent-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInFontSizeInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-font-size-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInLineHeightInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-line-height-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInOriginInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-origin-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInPaddingInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-padding-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedCellUnitInTextOutlineInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-cell-unit-in-text-outline-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedColorInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-color-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedDirectionInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-direction-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedDisplayAlignInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-display-align-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedEmUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-em-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedExtentLengthUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-extent-length-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedExtentLengthUnitInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-extent-length-unit-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedFontFamilyInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-font-family-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedFontSizeAnamorphic() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-font-size-anamorphic.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedFontSizeInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-font-size-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedFontStyleInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-font-style-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedFontWeightInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-font-weight-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedLineHeightInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-line-height-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedNestedDivisionInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-nested-division-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedNestedSpanInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-nested-span-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedOriginLengthUnitInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-origin-length-unit-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedOriginLengthUnitInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-origin-length-unit-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedPaddingInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-padding-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedParagraphInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-paragraph-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEBackgroundImageHorizontalInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-background-image-horizontal-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEBackgroundImageHorizontalInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-background-image-horizontal-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEBackgroundImageInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-background-image-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEBackgroundImageVerticalInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-background-image-vertical-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEBackgroundImageVerticalInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-background-image-vertical-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEImageInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-image-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSMPTEImageInTextProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-smpte-image-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedSpanInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-span-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTextAlignInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-text-align-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTextDecorationInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-text-decoration-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTextOutlineBlur() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-text-outline-blur.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTextOutlineInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-text-outline-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTimeBaseClock() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-time-base-clock.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedTimeBaseSMPTE() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-time-base-smpte.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedUnicodeBidiInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-unicode-bidi-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedWrapOptionInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-wrap-option-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1ProhibitedWritingModeInImageProfile() throws Exception {
        performInvalidityTest("imsc1-invalid-prohibited-writing-mode-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1RegionNotInRootContainer() throws Exception {
        performInvalidityTest("imsc1-invalid-region-not-in-root-container.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1UsesFramesComponentWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc1-invalid-uses-frames-component-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1UsesFramesMetricWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc1-invalid-uses-frames-metric-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1UsesNegativeLength() throws Exception {
        performInvalidityTest("imsc1-invalid-uses-negative-length.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1UsesPixelUnitWithoutRootExtent() throws Exception {
        performInvalidityTest("imsc1-invalid-uses-pixel-unit-without-root-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC1UsesTicksMetricWithoutTickRate() throws Exception {
        performInvalidityTest("imsc1-invalid-uses-ticks-metric-without-tick-rate.xml", -1, -1);
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
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}
