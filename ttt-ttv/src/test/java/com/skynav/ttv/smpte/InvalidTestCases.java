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
 
package com.skynav.ttv.smpte;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalAllSpace() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalEmpty() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalInherit() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-inherit.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalNegativePercentage() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-negative-percentage.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalNegative() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalPadded() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-padded.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageHorizontalAllUnsupportedUnit() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-horizontal-unsupported-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadBackgroundImageIdrefWrongEltype() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-background-image-idref-wrong-eltype.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010UnknownAttributes() throws Exception {
        performInvalidityTest("st2052-2010-invalid-unknown-attributes.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010UnknownElements() throws Exception {
        performInvalidityTest("st2052-2010-invalid-unknown-elements.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadDataDatatype() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-data-datatype.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadDataBase64() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-data-base64.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadImageBase64() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-image-base64.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadInformationAncestry() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-information-ancestry.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadInformationDuplicate() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-information-duplicate.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadSMPTEGlobalAttributeDisallowed() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-smpte-global-attribute-disallowed.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522010BadM608GlobalAttributeDisallowed() throws Exception {
        performInvalidityTest("st2052-2010-invalid-bad-m608-global-attribute-disallowed.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522013BadSMPTEGlobalAttributeDisallowed() throws Exception {
        performInvalidityTest("st2052-2013-invalid-bad-smpte-global-attribute-disallowed.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522013BadM608GlobalAttributeDisallowed() throws Exception {
        performInvalidityTest("st2052-2013-invalid-bad-m608-global-attribute-disallowed.xml", -1, -1);
    }

    @Test
    public void testInvalidST20522013BadM708GlobalAttributeDisallowed() throws Exception {
        performInvalidityTest("st2052-2013-invalid-bad-m708-global-attribute-disallowed.xml", -1, -1);
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
