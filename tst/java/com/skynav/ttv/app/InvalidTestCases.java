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

import com.skynav.ttv.app.TimedTextVerifier;

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
    public void testInvalidBadColorRGBHashGarbageAfterDigits() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-garbage-after-digits.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsGarbageAfterHash() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-missing-all-digits-garbage-after-hash.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsHashOnly() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-missing-all-digits-hash-only.xml");
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
    public void testInvalidBadColorRGBHashSpaceAfterHash() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-color-rgb-hash-space-after-hash.xml");
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

    @Test
    public void testInvalidBadExtentAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-all-space.xml");
    }

    @Test
    public void testInvalidBadExtentCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadExtentEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-empty.xml");
    }

    @Test
    public void testInvalidBadExtentExtraLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-extra-length.xml");
    }

    @Test
    public void testInvalidBadExtentMissingLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-missing-length.xml");
    }

    @Test
    public void testInvalidBadExtentMissingUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-missing-unit.xml");
    }

    @Test
    public void testInvalidBadExtentNegativeHeight() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-negative-height.xml");
    }

    @Test
    public void testInvalidBadExtentNegativeWidth() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-negative-width.xml");
    }

    @Test
    public void testInvalidBadExtentSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadExtentUnknownUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-extent-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadFontSizeAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-all-space.xml");
    }

    @Test
    public void testInvalidBadFontSizeCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadFontSizeEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-empty.xml");
    }

    @Test
    public void testInvalidBadFontSizeExtraLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-extra-length.xml");
    }

    @Test
    public void testInvalidBadFontSizeMissingUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-missing-unit.xml");
    }

    @Test
    public void testInvalidBadFontSizeMixedUnits() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-mixed-units.xml");
    }

    @Test
    public void testInvalidBadFontSizeNegativeHeight() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-negative-height.xml");
    }

    @Test
    public void testInvalidBadFontSizeNegativeWidth() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-negative-width.xml");
    }

    @Test
    public void testInvalidBadFontSizeSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadFontSizeUnknownUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-font-size-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadLineHeightAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-all-space.xml");
    }

    @Test
    public void testInvalidBadLineHeightCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadLineHeightEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-empty.xml");
    }

    @Test
    public void testInvalidBadLineHeightExtraLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-extra-length.xml");
    }

    @Test
    public void testInvalidBadLineHeightMissingUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-missing-unit.xml");
    }

    @Test
    public void testInvalidBadLineHeightNegativeHeight() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-negative-height.xml");
    }

    @Test
    public void testInvalidBadLineHeightSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadLineHeightUnknownUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-line-height-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadOriginAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-all-space.xml");
    }

    @Test
    public void testInvalidBadOriginCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadOriginEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-empty.xml");
    }

    @Test
    public void testInvalidBadOriginExtraLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-extra-length.xml");
    }

    @Test
    public void testInvalidBadOriginMissingLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-missing-length.xml");
    }

    @Test
    public void testInvalidBadOriginMissingUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-missing-unit.xml");
    }

    @Test
    public void testInvalidBadOriginSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadOriginUnknownUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-origin-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadPaddingAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-all-space.xml");
    }

    @Test
    public void testInvalidBadPaddingCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadPaddingEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-empty.xml");
    }

    @Test
    public void testInvalidBadPaddingExtraLength() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-extra-length.xml");
    }

    @Test
    public void testInvalidBadPaddingMissingUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-missing-unit.xml");
    }

    @Test
    public void testInvalidBadPaddingNegativeBeforeAndAfter() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-negative-before-and-after.xml");
    }

    @Test
    public void testInvalidBadPaddingNegativeStartAndEnd() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-negative-start-and-end.xml");
    }

    @Test
    public void testInvalidBadPaddingSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadPaddingUnknownUnit() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-padding-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadTextOutlineAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-all-space.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurColor() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-blur-color.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurMissingUnits() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-blur-missing-units.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurNegative() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-blur-negative.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBackThicknessColor() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-thickness-color.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessMissingUnits() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-thickness-missing-units.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessNegative() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-bad-thickness-negative.xml");
    }

    @Test
    public void testInvalidBadTextOutlineEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-empty.xml");
    }

    @Test
    public void testInvalidBadTextOutlineMissingThickness() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-text-outline-missing-thickness.xml");
    }

    @Test
    public void testInvalidBadZIndexAllSpace() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-z-index-all-space.xml");
    }

    @Test
    public void testInvalidBadZIndexEmpty() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-z-index-empty.xml");
    }

    @Test
    public void testInvalidBadZIndexExtraInteger() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-z-index-extra-integer.xml");
    }

    @Test
    public void testInvalidBadZIndexNonIntegerReal() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-z-index-non-integer-real.xml");
    }

    @Test
    public void testInvalidBadZIndexNonIntegerToken() throws Exception {
        performInvalidityTest("ttml10-invalid-bad-z-index-non-integer-token.xml");
    }

    private void performInvalidityTest(String resourceName) {
        URL url = getClass().getResource(resourceName);
        if (url == null)
            fail("Can't find test resource: " + resourceName + ".");
        String[] args = { "-q", "-v", url.toString() };
        int rv = new TimedTextVerifier().run(args);
        if (rv == 0)
            fail("Unexpected success.");
        else if (rv != 1)
            fail("Unexpected failure code: expected 1, got " + rv + ".");
    }
}

