/*
 * Copyright 2013-21 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.imsc10;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {

    @Test
    public void testValidIMSC10AllTTML1StylesInTextProfile() throws Exception {
        performValidityTest("imsc10-valid-all-ttml1-styles-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10DivisionWithBackgroundImageMissingTiming() throws Exception {
        performValidityTest("imsc10-valid-division-with-background-image-missing-timing.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10EBUTTSLinePadding() throws Exception {
        performValidityTest("imsc10-valid-ebutts-line-padding.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10EBUTTSMultiRowAlign() throws Exception {
        performValidityTest("imsc10-valid-ebutts-multirow-align.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10MissingProfileAttribute() throws Exception {
        performValidityTest("imsc10-valid-missing-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NestedDivisionInTextProfile() throws Exception {
        performValidityTest("imsc10-valid-nested-division-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NestedSpanInTextProfile() throws Exception {
        performValidityTest("imsc10-valid-nested-span-in-text-profile.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NonEmptyParagraphMissingTiming() throws Exception {
        performValidityTest("imsc10-valid-non-empty-paragraph-missing-timing.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NonEmptySpanMissingTiming() throws Exception {
        performValidityTest("imsc10-valid-non-empty-span-missing-timing.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NonEmptyTimeablesWithDurTiming() throws Exception {
        performValidityTest("imsc10-valid-non-empty-timeables-with-dur-timing.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10NonEmptyTimeablesWithTiming() throws Exception {
        performValidityTest("imsc10-valid-non-empty-timeables-with-timing.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10TimeablesWithBackgroundImageWithTimingOnAncestor() throws Exception {
        performValidityTest("imsc10-valid-timeables-with-background-image-with-timing-on-ancestor.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10TimeablesWithBackgroundImageWithTimingOnSelf() throws Exception {
        performValidityTest("imsc10-valid-timeables-with-background-image-with-timing-on-self.xml", -1, -1);
    }

    @Test
    public void testValidIMSC10WritingModeInTextProfile() throws Exception {
        performValidityTest("imsc10-valid-writing-mode-in-text-profile.xml", -1, -1);
    }

    private void performValidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performValidityTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    private void performValidityTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
        args.add("-q");
        args.add("-v");
        args.add("--warn-on");
        args.add("all");
        if (expectedErrors >= 0) {
            args.add("--expect-errors");
            args.add(Integer.toString(expectedErrors));
        }
        if (expectedWarnings >= 0) {
            args.add("--expect-warnings");
            args.add(Integer.toString(expectedWarnings));
        }
        if (additionalOptions != null) {
            args.addAll(java.util.Arrays.asList(additionalOptions));
        }
        args.add("--");
        args.add(urlString);
        TimedTextVerifier ttv = new TimedTextVerifier();
        ttv.run(args.toArray(new String[args.size()]));
        int resultCode = ttv.getResultCode(urlString);
        int resultFlags = ttv.getResultFlags(urlString);
        if (resultCode == TimedTextVerifier.RV_PASS) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
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

