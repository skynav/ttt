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

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {

    @Test
    public void testValidTTML2AllParameters() throws Exception {
        performValidityTest("ttml2-valid-all-parameters.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AllStyles() throws Exception {
        performValidityTest("ttml2-valid-all-styles.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesQuantifiedAllMultiple() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-quantified-all-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesQuantifiedAllSingle() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-quantified-all-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesUnquantifiedMultiple() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-unquantified-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesUnquantifiedSingle() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-unquantified-single.xml", -1, -1);
    }

    @Test
    @Ignore
    public void testValidTTML2ImageInBlockAll() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-all.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInBlockSimpleSourceJpg() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInBlockSimpleSourcePng() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAllMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-all-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAllSingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-all-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAnyMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-any-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAnySingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-any-single.xml", -1, -1);
    }

    public void testValidTTML2ProcessorProfilesUnquantifiedMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-unquantified-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesUnquantifiedSingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-unquantified-single.xml", -1, -1);
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
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}

