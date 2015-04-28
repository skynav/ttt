/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttpe.app;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttpe.app.Presenter;

public class SampleTestCases {

    @Test
    public void testSample1() throws Exception {
        performPresentationTest("sample-001.xml", -1, -1);
    }

    @Test
    public void testSample2() throws Exception {
        performPresentationTest("sample-002.xml", -1, -1);
    }

    @Test
    public void testSample3() throws Exception {
        performPresentationTest("sample-003.xml", -1, -1);
    }

    @Test
    public void testSample4() throws Exception {
        performPresentationTest("sample-004.xml", -1, -1);
    }

    @Test
    public void testSample5() throws Exception {
        performPresentationTest("sample-005.xml", -1, -1);
    }

    @Test
    public void testSample6() throws Exception {
        performPresentationTest("sample-006.xml", -1, -1);
    }

    @Test
    public void testSample7() throws Exception {
        performPresentationTest("sample-007.xml", -1, -1);
    }

    @Test
    public void testSample8() throws Exception {
        performPresentationTest("sample-008.xml", -1, -1);
    }

    @Test
    public void testSample9() throws Exception {
        performPresentationTest("sample-009.xml", -1, -1);
    }

    @Test
    public void testSample10() throws Exception {
        performPresentationTest("sample-010.xml", -1, -1);
    }

    @Test
    public void testSample11() throws Exception {
        performPresentationTest("sample-011.xml", -1, -1);
    }

    @Test
    public void testSample12() throws Exception {
        performPresentationTest("sample-012.xml", -1, -1);
    }

    @Test
    public void testSample13() throws Exception {
        performPresentationTest("sample-013.xml", -1, -1);
    }

    @Test
    public void testSample14() throws Exception {
        performPresentationTest("sample-014.xml", -1, -1);
    }

    private void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performPresentationTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    private void performPresentationTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String urlString = url.toString();
        List<String> args = new java.util.ArrayList<String>();
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
        Presenter ttpe = new Presenter();
        ttpe.run(args.toArray(new String[args.size()]));
    }

}
