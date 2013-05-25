/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.app;

import java.net.URL;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextValidator;

public class InvalidTestCases {
    @Test
    public void testInvalidRootHtml() throws Exception {
        performInvalidityTest("ttml10-invalid-root-html.xml");
    }

    @Test
    public void testInvalidRootDiv() throws Exception {
        performInvalidityTest("ttml10-invalid-root-div.xml");
    }

    @Test
    public void testInvalidMissingLanguage() throws Exception {
        performInvalidityTest("ttml10-invalid-missing-lang.xml");
    }

    @Test
    public void testInvalidBadLanguage() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-lang.xml");
    }

    @Test
    public void testInvalidNonUniqueId() throws Exception {
        performInvalidityTest("ttml10-invalid-non-unique-id.xml");
    }

    @Test
    public void testInvalidBadIdReferenceUnresolvable() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-id-reference-unresolvable.xml");
    }

    @Test
    public void testInvalidBadColorAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-all-space.xml");
    }

    @Test
    public void testInvalidBadColorEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-empty.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-bad-component-syntax.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-extra-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-missing-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-negative-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-out-of-range-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-function-padded-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashExtraDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-extra-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-missing-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashNonDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-non-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-bad-component-syntax.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-extra-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-missing-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-negative-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-out-of-range-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-function-padded-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashExtraDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-hash-extra-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashMissingDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-hash-missing-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashNonDigit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgba-hash-non-digit.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedNamedColor() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-space-padded-named-color.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBFunction() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-space-padded-rgb-function.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBHash() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-space-padded-rgb-hash.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAFunction() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-space-padded-rgba-function.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAHash() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-space-padded-rgba-hash.xml");
    }

    @Test
    public void testInvalidBadColorUnknownNamedColor() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-unknown-named-color.xml");
    }

    private void performInvalidityTest(String resourceName) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String[] args = { "-q", url.toString() };
        int rv = new TimedTextValidator().run(args);
        if (rv == 0)
            fail("Unexpected validation success.");
        else if (rv != 1)
            fail("Unexpected validation failure code: expected 1, got " + rv + ".");
    }
}

