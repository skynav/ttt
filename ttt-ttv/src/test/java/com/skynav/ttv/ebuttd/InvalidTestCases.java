/*
 * Copyright (c) 2015, Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 * Copyright 2015 Skynav, Inc.
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

package com.skynav.ttv.ebuttd;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

/**
 * Invalid test cases for EBU-TT-D.
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class InvalidTestCases {

    /**
     * Timing specified both on "p" and "span" elements.
     */
    @Test
    public void testTiming1() {
        performInvalidityTest("invalid-ebuttd_bad_timing_1.xml", 1, 0);
    }

    /**
     * Timing specified neither on "p" nor "span" element.
     */
    @Test
    public void testTiming2() {
        performInvalidityTest("invalid-ebuttd_bad_timing_2.xml", 1, 0);
    }

    /**
     * Timing specified on one "span" element only and not on parent "p" element. The another one is without timing
     * information.
     */
    @Test
    public void testTiming3() {
        performInvalidityTest("invalid-ebuttd_bad_timing_3.xml", 1, 0);
    }

    /**
     * Timing specified on parent "p" and one child "span" element. However, there is another child "span" element
     * without timing information.
     */
    @Test
    public void testTiming4() {
        performInvalidityTest("invalid-ebuttd_bad_timing_4.xml", 1, 0);
    }

    /**
     * Timing not specified on the "p" element. The textual payload is not wrapped inside "span" element.
     */
    @Test
    public void testTiming5() {
        performInvalidityTest("invalid-ebuttd_bad_timing_5.xml", 1, 0);
    }

    /**
     * Timing not specified on the "p" element, nor on the textual payload.
     */
    @Test
    public void testTiming6() {
        performInvalidityTest("invalid-ebuttd_bad_timing_6.xml", 1, 0);
    }

    private void performInvalidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        URL url = getClass().getResource(resourceName);
        if (url == null) {
            fail("Can't find test resource: " + resourceName + ".");
        }
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
        args.add("-q");
        args.add("-v");
        args.add("--model");
        args.add("ebuttd");
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
        } else {
            fail("Unexpected result code " + resultCode + ".");
        }
    }
}
