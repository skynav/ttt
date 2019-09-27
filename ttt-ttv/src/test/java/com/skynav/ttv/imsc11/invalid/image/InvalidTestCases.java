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

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

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
    public void testInvalidIMSC11ProhibitedBreak() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-break.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedChunk() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-chunk.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedColor() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-color.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedData() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-data.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedDirection() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-direction.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedDisplayAlign() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-display-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedEmUnit() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-em-unit.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedFontFamily() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-family.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedFontSize() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-size.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontStyle() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-style.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontVariant() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-variant.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedFontWeight() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-font-weight.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedInitial() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-initial.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedLineHeight() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-line-height.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedLineShear() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-line-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedNestedDivision() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-nested-division.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedNestedSpan() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-nested-span.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedOriginLengthUnit() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-origin-length-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedPadding() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-padding.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedParagraph() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-paragraph.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedPosition() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedResources() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-resources.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRuby() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-ruby.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyAlign() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-ruby-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyPosition() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-ruby-position.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedRubyReserve() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-ruby-reserve.xml", -1, -1);
    }

    @Ignore
    @Test
    public void testInvalidIMSC11ProhibitedShear() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-shear.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedSMPTEBackgroundImageHorizontal() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-smpte-background-image-horizontal.xml", -1, -1);
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
    public void testInvalidIMSC11ProhibitedSpan() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-span.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextAlign() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-align.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextCombine() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-combine.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextDecoration() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-decoration.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextEmphasis() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-emphasis.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOrientation() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-orientation.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextOutline() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-outline.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedTextShadow() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-text-shadow.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedUnicodeBidi() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-unicode-bidi.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedWrapOption() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-wrap-option.xml", -1, -1);
    }

    @Test
    public void testInvalidIMSC11ProhibitedWritingMode() throws Exception {
        performInvalidityTest("imsc11-invld-prohibited-writing-mode.xml", -1, -1);
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
