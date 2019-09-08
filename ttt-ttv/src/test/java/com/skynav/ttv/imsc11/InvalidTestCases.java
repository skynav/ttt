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
    public void testInvalidIMSC11ProhibitedFontVariantInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-variant-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontVariantInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-font-variant-in-text-profile.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedLineShearInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-line-shear-in-image-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineShearInTextProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-line-shear-in-text-profile.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedTextCombineInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-combine-in-image-profile.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedTextShadowInImageProfile() throws Exception {
        performInvalidityTest("imsc11-invalid-prohibited-text-shadow-in-image-profile.xml", -1, -1);
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
