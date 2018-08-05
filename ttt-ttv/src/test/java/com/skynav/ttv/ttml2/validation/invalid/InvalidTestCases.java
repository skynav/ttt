/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.ttml2.validation.invalid;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidTTML2AudioInBlockSimpleSourceMpeg() throws Exception {
        performInvalidityTest("ttml2-invld-audio-in-block-simple-source-mpeg.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2AudioInInlineSimpleSourceMpeg() throws Exception {
        performInvalidityTest("ttml2-invld-audio-in-inline-simple-source-mpeg.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateAnimationStyleValue() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-animation-style-value.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateNegativeRepeatCount() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-negative-repeat-count.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateUnknownCalculationMode() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-unknown-calculation-mode.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateUnknownFill() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-unknown-fill.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateUnknownRepeatCount() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-unknown-repeat-count.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundClipAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-clip-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundClipEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-clip-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundClipUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-clip-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundColorAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-all-space.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-empty.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-bad-component-syntax.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-extra-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-missing-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-negative-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-out-of-range-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-function-padded-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashExtraDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-extra-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashGarbageAfterDigits() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-garbage-after-digits.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashMissingAllDigitsGarbageAfterHash() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-missing-all-digits-garbage-after-hash.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashMissingAllDigitsHashOnly() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-missing-all-digits-hash-only.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashMissingDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-missing-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashNonDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-non-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBHashSpaceAfterHash() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgb-hash-space-after-hash.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-bad-component-syntax.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-extra-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-missing-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-negative-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-out-of-range-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-function-padded-component.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAHashExtraDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-hash-extra-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAHashMissingDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-hash-missing-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorRGBAHashNonDigit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-rgba-hash-non-digit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorSpacePaddedNamedColor() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-space-padded-named-color.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorSpacePaddedRGBFunction() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-space-padded-rgb-function.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorSpacePaddedRGBHash() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-space-padded-rgb-hash.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorSpacePaddedRGBAFunction() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-space-padded-rgba-function.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorSpacePaddedRGBAHash() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-space-padded-rgba-hash.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundColorUnknownNamedColor() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-color-unknown-named-color.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadBackgroundExtentAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-extent-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundExtentEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-extent-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundExtentUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-extent-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundImageAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-image-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundImageEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-image-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundImageUnresolvableFragmentUri() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-image-unresolvable-fragment-uri.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundImageUnresolvableExternalUri() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-image-unresolvable-external-uri.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundOriginAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-origin-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundOriginEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-origin-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundOriginUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-origin-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundPosition() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-position.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundPositionAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-position-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundPositionEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-position-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundRepeatAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-repeat-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundRepeatEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-repeat-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBackgroundRepeatUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-repeat-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderExtraColor() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-extra-color.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderExtraStyle() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-extra-style.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderExtraThickness() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-extra-thickness.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderExtraRadii() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-extra-radii.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderNegativeThickness() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-negative-thickness.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadii1Argument() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-1-argument.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadii1Negative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-1-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadii2Argument() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-2-argument.xml", -1, -1);
    }

    public void testInvalidTTML2BadBorderRadii2Delimiter() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-2-delimiter.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadii2Negative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-2-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadiiMissingLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-missing-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderRadiiExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-radii-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBorderUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-border-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-bpd-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-bpd-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDNegative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-bpd-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadBPDUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-bpd-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDirectionAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-direction-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDirectionEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-direction-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDirectionUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-direction-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisparityAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-disparity-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisparityEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-disparity-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisparityExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-disparity-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisparityUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-disparity-unknown-unit.xml", -1, -1);
    }

    @Test
    public void textInvalidTTML2BadExtentAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-all-space.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-comma-delimiter-with-whitespace.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-empty.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-extra-length.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentMissingLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-missing-length.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentMissingUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-missing-unit.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentNegativeHeight() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-height.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentNegativeWidth() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-width.xml", -1, -1);
    }

    @Test
    public void textInvalidTTML2BadExtentRootHeight() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-height.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentRootWidth() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-width.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-semicolon-delimiter-sans-whitespace.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-keyword.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentUnknownMeasureKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-measure-keyword.xml", -1, -1);
    }
    
    @Test
    public void textInvalidTTML2BadExtentUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-unit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadGainAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-gain-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadGainEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-gain-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadGainRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-gain-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadGainUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-gain-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ipd-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ipd-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDNegative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ipd-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadIPDUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ipd-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLetterSpacingAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLetterSpacingEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLetterSpacingExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLetterSpacingUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-unknown-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPanAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pan-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPanEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pan-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPanRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pan-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPanUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pan-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPitchAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pitch-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPitchEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pitch-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPitchRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pitch-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPitchUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pitch-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPitchUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-pitch-unknown-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSpeakAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-speak-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSpeakEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-speak-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSpeakUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-speak-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextOrientationAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-orientation-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextOrientationEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-orientation-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextOrientationUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-orientation-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadUnicodeBidiAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-unicode-bidi-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadUnicodeBidiEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-unicode-bidi-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadUnicodeBidiUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-unicode-bidi-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-base-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-base-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseGeneralAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-base-general-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseGeneralEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-base-general-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDesignatorDelimiterSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-designator-delimiter-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDuplicateDesignatorAbsolutized() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-duplicate-designator-absolutized.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesDuplicateDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-duplicate-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifiedNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-quantified-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifierSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-quantifier-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesQuantifierUnknown() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-quantifier-unknown.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataChunkedMissingType() throws Exception {
        performInvalidityTest("ttml2-invld-data-chunked-missing-type.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataReferenceExternalMissingType() throws Exception {
        performInvalidityTest("ttml2-invld-data-reference-external-missing-type.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataReferenceInternalWithType() throws Exception {
        performInvalidityTest("ttml2-invld-data-reference-internal-with-type.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataSimpleMissingType() throws Exception {
        performInvalidityTest("ttml2-invld-data-simple-missing-type.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInBlockSimpleSourcePng() throws Exception {
        performInvalidityTest("ttml2-invld-image-in-block-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInBlockSimpleSourceJpg() throws Exception {
        performInvalidityTest("ttml2-invld-image-in-block-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInInlineSimpleSourcePng() throws Exception {
        performInvalidityTest("ttml2-invld-image-in-inline-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageInInlineSimpleSourceJpg() throws Exception {
        performInvalidityTest("ttml2-invld-image-in-inline-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDesignatorDelimiterSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-designator-delimiter-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDuplicateDesignatorAbsolutized() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-duplicate-designator-absolutized.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesDuplicateDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-duplicate-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifiedNoDesignator() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-quantified-no-designator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifierSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-quantifier-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesQuantifierUnknown() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-quantifier-unknown.xml", -1, -1);
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
