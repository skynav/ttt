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
 
package com.skynav.ttv.netflix;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidNFLXCCBadEncoding() throws Exception {
        performInvalidityTest("nflxcc-invalid-bad-encoding.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCBadProfileAttribute() throws Exception {
        performInvalidityTest("nflxcc-invalid-bad-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCMissingBody() throws Exception {
        performInvalidityTest("nflxcc-invalid-missing-body.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCMissingHead() throws Exception {
        performInvalidityTest("nflxcc-invalid-missing-head.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCMissingProfileAttribute() throws Exception {
        performInvalidityTest("nflxcc-invalid-missing-profile-attribute.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedBackgroundImageHorizontal() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-background-image-horizontal.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedBackgroundImageVertical() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-background-image-vertical.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedBackgroundImage() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-background-image.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedImage() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-image.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedMarkerMode() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-marker-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCNotPermittedOverflow() throws Exception {
        performInvalidityTest("nflxcc-invalid-not-permitted-overflow.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCRegionNotInRootContainer() throws Exception {
        performInvalidityTest("nflxcc-invalid-region-not-in-root-container.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCRootProfile() throws Exception {
        performInvalidityTest("nflxcc-invalid-root-profile.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCUsesCellUnitWithoutCellResolution() throws Exception {
        performInvalidityTest("nflxcc-invalid-uses-cell-unit-without-cell-resolution.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCUsesEmUnit() throws Exception {
        performInvalidityTest("nflxcc-invalid-uses-em-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXCCUsesPixelUnitWithoutRootExtent() throws Exception {
        performInvalidityTest("nflxcc-invalid-uses-pixel-unit-without-root-extent.xml", -1, -1);
    }

    @Test
    public void testInvalidNFLXSDHBadRegionGeometry() throws Exception {
        performInvalidityTest("nflxsdh-invalid-bad-region-geometry.xml", -1, -1);
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
