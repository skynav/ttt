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
 
package com.skynav.ttv.imsc11.invalid.text;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidIMSC11BadEBUTTSLinePadding() throws Exception {
        performInvalidityTest("imsc11-invld-bad-ebutts-line-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSLinePaddingUsageContext() throws Exception {
        performInvalidityTest("imsc11-invld-bad-ebutts-line-padding-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSMultiRowAlign() throws Exception {
        performInvalidityTest("imsc11-invld-bad-ebutts-multirow-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEBUTTSMultiRowAlignUsageContext() throws Exception {
        performInvalidityTest("imsc11-invld-bad-ebutts-multirow-align-usage-context.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadEncoding() throws Exception {
        performInvalidityTest("imsc11-invld-bad-encoding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11BadProfileAttribute() throws Exception {
        performInvalidityTest("imsc11-invld-bad-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11MissingRegionExtent() throws Exception {
        performInvalidityTest("imsc11-invld-missing-region-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedClockMode() throws Exception {
        performInvalidityTest("imsc11-invld-not-permitted-clock-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedDropMode() throws Exception {
        performInvalidityTest("imsc11-invld-not-permitted-drop-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedMarkerMode() throws Exception {
        performInvalidityTest("imsc11-invld-not-permitted-marker-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedPixelAspectRatio() throws Exception {
        performInvalidityTest("imsc11-invld-not-permitted-pixel-aspect-ratio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11NotPermittedSubFrameRate() throws Exception {
        performInvalidityTest("imsc11-invld-not-permitted-sub-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedAudio() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-audio.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundClip() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-clip.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundExtent() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundImage() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundOrigin() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-origin.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundPosition() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBackgroundRepeat() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-background-repeat.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBorder() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-border.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedBPD() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-bpd.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInExtent() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInFontSize() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-font-size.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInLineHeight() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-line-height.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInOrigin() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-origin.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInPadding() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedCellUnitInTextOutline() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-cell-unit-in-text-outline.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedChunk() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-chunk.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedData() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-data.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedDisplayAlign() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-display-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedExtentLengthUnit() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-extent-length-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFont() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontKerning() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-kerning.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSelectionStrategy() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-selection-strategy.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontShear() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontSizeAnamorphic() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-size-anamorphic.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontVariant() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-variant.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedIPD() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-ipd.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLetterSpacing() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-letter-spacing.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineShear() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-line-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedOriginLengthUnit() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-origin-length-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedResources() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-resources.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageHorizontal() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-smpte-background-image-horizontal.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImage() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-smpte-background-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageVertical() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-smpte-background-image-vertical.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEImage() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-smpte-image.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSource() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-source.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOrientation() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-orientation.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOutlineBlur() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-outline-blur.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTimeBaseClock() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-time-base-clock.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTimeBaseSMPTE() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-time-base-smpte.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11RegionNotInRootContainer() throws Exception {
        performInvalidityTest("imsc11-invld-region-not-in-root-container.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesFramesComponentWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc11-invld-uses-frames-component-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesFramesMetricWithoutFrameRate() throws Exception {
        performInvalidityTest("imsc11-invld-uses-frames-metric-without-frame-rate.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesNegativeLength() throws Exception {
        performInvalidityTest("imsc11-invld-uses-negative-length.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesPixelUnitWithoutRootExtent() throws Exception {
        performInvalidityTest("imsc11-invld-uses-pixel-unit-without-root-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11UsesTicksMetricWithoutTickRate() throws Exception {
        performInvalidityTest("imsc11-invld-uses-ticks-metric-without-tick-rate.xml", -1, -1);
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
