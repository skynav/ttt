/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.ttml2;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidTTML2BadBackgroundPosition() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-background-position.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundRepeatUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-background-repeat-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDNegative() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-bpd-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-bpd-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDNegative() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-ipd-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-ipd-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2LetterSpacingAllSpace() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-letter-spacing-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2LetterSpacingEmpty() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-letter-spacing-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2LetterSpacingExtraLength() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-letter-spacing-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2LetterSpacingMissingUnit() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-letter-spacing-missing-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2LetterSpacingUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invalid-bad-letter-spacing-unknown-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDesignatorDelimiterSyntax() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-designator-delimiter-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDuplicateDesignatorAbsolutized() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-duplicate-designator-absolutized.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDuplicateDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-duplicate-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifiedNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-quantified-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifierSyntax() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-quantifier-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifierUnknown() throws Exception {
        performInvalidityTest("ttml2-invalid-content-profiles-quantifier-unknown.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInBlockSimpleSourcePng() throws Exception {
        performInvalidityTest("ttml2-invalid-image-in-block-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInBlockSimpleSourceJpg() throws Exception {
        performInvalidityTest("ttml2-invalid-image-in-block-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDesignatorDelimiterSyntax() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-designator-delimiter-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDuplicateDesignatorAbsolutized() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-duplicate-designator-absolutized.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDuplicateDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-duplicate-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifiedNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-quantified-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifierSyntax() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-quantifier-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifierUnknown() throws Exception {
        performInvalidityTest("ttml2-invalid-processor-profiles-quantifier-unknown.xml", -1, -1);
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
