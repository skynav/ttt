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
 
package com.skynav.ttv.w3c.ttml2.validation.invalid;

import java.net.URL;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidTTML2AudioEmbeddingBadResource() throws Exception {
        performInvalidityTest("ttml2-invld-audio-embedding-bad-resource.xml", -1, -1);
    }

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
    public void testInvalidTTML2BadAnimateNonAnimatable() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-non-animatable.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateNonAnimatableDiscreteDefault() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-non-animatable-discrete-default.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateNonAnimatableDiscreteLinear() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-non-animatable-discrete-linear.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateNonAnimatableDiscretePaced() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-non-animatable-discrete-paced.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadAnimateNonAnimatableDiscreteSpline() throws Exception {
        performInvalidityTest("ttml2-invld-bad-animate-non-animatable-discrete-spline.xml", -1, -1);
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
    public void testInvalidTTML2BadBackgroundPositionUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-background-position-unknown-keyword.xml", -1, -1);
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

    @Test
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
    public void testInvalidTTML2BadDisplayAlignAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-align-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayAlignEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-align-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayAlignUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-align-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayInlineBlockNonSpan() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-inline-block-non-span.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadDisplayUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-display-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadExtentAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-all-space.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentColonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-colon-delimiter-sans-whitespace.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-comma-delimiter-with-whitespace.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-empty.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-extra-length.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentMissingLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-missing-length.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentMissingUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-missing-unit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentNegativeHeight() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-height.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentNegativeHeightRootRelative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-height-root-relative.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentNegativeWidth() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-width.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadExtentNegativeWidthRootRelative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-negative-width-root-relative.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentRootCover() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-cover.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentRootHeight() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-height.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentRootMeasureAvailable() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-measure-available.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentRootMeasureFitContent() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-measure-fit-content.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentRootWidth() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-root-width.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-keyword.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentUnknownMeasureKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-measure-keyword.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadExtentUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-extent-unknown-unit.xml", -1, -1);
    }
    
    @Test
    public void testInvalidTTML2BadFontKerningAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-kerning-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontKerningEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-kerning-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontKerningUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-kerning-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontSelectionStrategyAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-selection-strategy-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontSelectionStrategyEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-selection-strategy-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontSelectionStrategyUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-selection-strategy-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontShearAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-shear-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontShearEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-shear-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontShearRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-shear-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontShearUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-shear-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantMixedNormal() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-mixed-normal.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantMixedScript() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-mixed-script.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantMixedWidth() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-mixed-width.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadFontVariantUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-font-variant-unknown-keyword.xml", -1, -1);
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
    public void testInvalidTTML2BadLetterSpacingUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLetterSpacingUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-letter-spacing-unknown-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLineShearAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-line-shear-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLineShearEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-line-shear-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLineShearRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-line-shear-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLineShearUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-line-shear-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainSignNegative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-sign-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainSignPositive() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-sign-positive.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadLuminanceGainUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-luminance-gain-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadOpacityAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-opacity-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadOpacityEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-opacity-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadOpacityUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-opacity-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingColonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-colon-delimiter-sans-whitespace.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingCommaDelimiterWitWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-comma-delimiter-with-whitespace.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingMissingUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-missing-unit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingNegativeBeforeAndAfter() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-negative-before-and-after.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingNegativeStartAndEnd() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-negative-start-and-end.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPaddingUnknownUnit() throws Exception {
        performInvalidityTest("ttml2-invld-bad-padding-unknown-unit.xml", -1, -1);
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
    public void testInvalidTTML2BadPosition() throws Exception {
        performInvalidityTest("ttml2-invld-bad-position.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPositionAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-position-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPositionEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-position-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadPositionUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-position-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyAlignAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-align-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyAlignEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-align-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyAlignUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-align-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyPositionAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-position-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyPositionEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-position-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyPositionUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-position-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyReserveAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-reserve-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyReserveEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-reserve-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyReserveNegative() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-reserve-negative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyReserveUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-reserve-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadRubyUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-ruby-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSetAnimationStyleValue() throws Exception {
        performInvalidityTest("ttml2-invld-bad-set-animation-style-value.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSetNegativeRepeatCount() throws Exception {
        performInvalidityTest("ttml2-invld-bad-set-negative-repeat-count.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSetNonAnimatable() throws Exception {
        performInvalidityTest("ttml2-invld-bad-set-non-animatable.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSetUnknownFill() throws Exception {
        performInvalidityTest("ttml2-invld-bad-set-unknown-fill.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadSetUnknownRepeatCount() throws Exception {
        performInvalidityTest("ttml2-invld-bad-set-unknown-repeat-count.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadShearAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-shear-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadShearEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-shear-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadShearRealSyntax() throws Exception {
        performInvalidityTest("ttml2-invld-bad-shear-real-syntax.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadShearUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-shear-unknown-keyword.xml", -1, -1);
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
    public void testInvalidTTML2BadTextAlignAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-align-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextAlignEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-align-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextAlignUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-align-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextCombineAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-combine-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextCombineEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-combine-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextCombineUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-combine-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextEmphasisAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-emphasis-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextEmphasisEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-emphasis-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextEmphasisUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-emphasis-unknown-keyword.xml", -1, -1);
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
    public void testInvalidTTML2BadTextShadowAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowExtraLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-extra-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowMisplacedColor() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-misplaced-color.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowMissingLength() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-missing-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowNegativeBlur() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-negative-blur.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadTextShadowUnknownColor() throws Exception {
        performInvalidityTest("ttml2-invld-bad-text-shadow-unknown-color.xml", -1, -1);
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
    public void testInvalidTTML2BadVisibilityAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-bad-visibility-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadVisibilityEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-bad-visibility-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BadVisibilityUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-bad-visibility-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-base-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2BaseGeneralAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-base-general-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ConditionMedia() throws Exception {
        performInvalidityTest("ttml2-invld-condition-media.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ConditionParameter() throws Exception {
        performInvalidityTest("ttml2-invld-condition-parameter.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ConditionPrimary() throws Exception {
        performInvalidityTest("ttml2-invld-condition-primary.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ConditionSupports() throws Exception {
        performInvalidityTest("ttml2-invld-condition-supports.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfileCombinationAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-content-profile-combination-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfileCombinationEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-content-profile-combination-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfileCombinationUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-content-profile-combination-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfileExtentRootVersion2() throws Exception {
        performInvalidityTest("ttml2-invld-content-profile-extent-root-version-2.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ContentProfilesAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-all-space.xml", -1, -1);
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
    public void testInvalidTTML2ContentProfilesEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-content-profiles-empty.xml", -1, -1);
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
    public void testInvalidTTML2DataChunkedBadLength() throws Exception {
        performInvalidityTest("ttml2-invld-data-chunked-bad-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataChunkedMissingType() throws Exception {
        performInvalidityTest("ttml2-invld-data-chunked-missing-type.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataChunkedWithBase() throws Exception {
        performInvalidityTest("ttml2-invld-data-chunked-with-base.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataChunkedWithCondition() throws Exception {
        performInvalidityTest("ttml2-invld-data-chunked-with-condition.xml", -1, -1);
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
    public void testInvalidTTML2DataSimpleBadLength() throws Exception {
        performInvalidityTest("ttml2-invld-data-simple-bad-length.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DataSimpleNonAlphabet() throws Exception {
        performInvalidityTest("ttml2-invld-data-simple-non-alphabet.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioColonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-colon-delimiter-sans-whitespace.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioCommaDelimiterWIthWhitespace() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-comma-delimiter-with-whitespace.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioExtraComponent() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-extra-component.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioMissingComponent() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-missing-component.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioNegativeDenominator() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-negative-denominator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioNegativeNumerator() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-negative-numerator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioZeroDenominator() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-zero-denominator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2DisplayAspectRatioZeroNumerator() throws Exception {
        performInvalidityTest("ttml2-invld-display-aspect-ratio-zero-numerator.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingFamily() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-family.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingRangeIntervalExtraDigit() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-range-interval-extra-digit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingRangeSingleExtraDigit() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-range-single-extra-digit.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingRangeUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-range-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingStyleUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-style-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2FontEmbeddingWeightUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-font-embedding-weight-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ImageEmbeddingBadResource() throws Exception {
        performInvalidityTest("ttml2-invld-image-embedding-bad-resource.xml", -1, -1);
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
    public void testInvalidTTML2ImageUnresolvableFragmentUri() throws Exception {
        performInvalidityTest("ttml2-invld-image-unresolvable-fragment-uri.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileMethodAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-method-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileMethodEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-method-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileMethodUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-method-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileSourceAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-source-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileSourceEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-source-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InferProcessorProfileSourceUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-infer-processor-profile-source-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2InitialNestedStyle() throws Exception {
        performInvalidityTest("ttml2-invld-initial-nested-style.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadSize0Minus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-size-0-minus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadSize0Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-size-0-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadSize1Minus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-size-1-minus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadSize1Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-size-1-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadVersion0() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-version-0.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadVersion0Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-version-0-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadVersion1() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-version-1.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadVersion1Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-version-1-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSequenceBadVersion2Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-sequence-bad-version-2-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleBadVersion0() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-bad-version-0.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleBadVersion0Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-bad-version-0-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleBadVersion1() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-bad-version-1.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleBadVersion1Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-bad-version-1-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleBadVersion2Plus() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-bad-version-2-plus.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2IsdSingleNestedBadVersion() throws Exception {
        performInvalidityTest("ttml2-invld-isd-single-nested-bad-version.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2Length2RootContainerRelative() throws Exception {
        performInvalidityTest("ttml2-invld-length-2-root-container-relative.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2MetadataItemAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-metadata-item-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2MetadataItemEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-metadata-item-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2MetadataItemQualifiedMissingBinding() throws Exception {
        performInvalidityTest("ttml2-invld-metadata-item-qualified-missing-binding.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureNarrowingAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-narrowing-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureNarrowingEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-narrowing-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureNarrowingUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-narrowing-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureWideningAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-widening-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureWideningEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-widening-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2PermitFeatureWideningUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-permit-feature-widening-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfileCombinationAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profile-combination-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfileCombinationEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profile-combination-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfileCombinationUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profile-combination-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProcessorProfilesAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-all-space.xml", -1, -1);
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
    public void testInvalidTTML2ProcessorProfilesEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-processor-profiles-empty.xml", -1, -1);
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

    @Test
    public void testInvalidTTML2ProfileCombineAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-profile-combine-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileCombineEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-profile-combine-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileCombineUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-profile-combine-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileDesignatorFragment() throws Exception {
        performInvalidityTest("ttml2-invld-profile-designator-fragment.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileDesignatorFragmentNested() throws Exception {
        performInvalidityTest("ttml2-invld-profile-designator-fragment-nested.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileDesignatorRegistryUndefined() throws Exception {
        performInvalidityTest("ttml2-invld-profile-designator-registry-undefined.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileNestedMixed() throws Exception {
        performInvalidityTest("ttml2-invld-profile-nested-mixed.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileTypeAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-profile-type-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileTypeEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-profile-type-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileTypeUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-profile-type-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ProfileUseFragment() throws Exception {
        performInvalidityTest("ttml2-invld-profile-use-fragment.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2RegionInlineDivMultiple() throws Exception {
        performInvalidityTest("ttml2-invld-region-inline-div-multiple.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2RegionInlineImage() throws Exception {
        performInvalidityTest("ttml2-invld-region-inline-image.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2RegionInlineParagraphMultiple() throws Exception {
        performInvalidityTest("ttml2-invld-region-inline-p-multiple.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2RegionInlineSpan() throws Exception {
        performInvalidityTest("ttml2-invld-region-inline-span.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2RegionInlineTiming() throws Exception {
        performInvalidityTest("ttml2-invld-region-inline-timing.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationActionAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-validation-action-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationActionEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-validation-action-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationActionUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-validation-action-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-validation-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-validation-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2ValidationUnknownKeyword() throws Exception {
        performInvalidityTest("ttml2-invld-validation-unknown-keyword.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2WallClockDateTime() throws Exception {
        performInvalidityTest("ttml2-invld-wall-clock-date-time.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2WallClockDate() throws Exception {
        performInvalidityTest("ttml2-invld-wall-clock-date.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2WallClockWallTime() throws Exception {
        performInvalidityTest("ttml2-invld-wall-clock-wall-time.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2XlinkHrefAllSpace() throws Exception {
        performInvalidityTest("ttml2-invld-xlink-href-all-space.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2XlinkHrefEmpty() throws Exception {
        performInvalidityTest("ttml2-invld-xlink-href-empty.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2XlinkHrefNestedImage() throws Exception {
        performInvalidityTest("ttml2-invld-xlink-href-nested-image.xml", -1, -1);
    }

    @Test
    public void testInvalidTTML2XlinkHrefNestedSpan() throws Exception {
        performInvalidityTest("ttml2-invld-xlink-href-nested-span.xml", -1, -1);
    }

    private void performInvalidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performInvalidityTest(resourceName, expectedErrors, expectedWarnings, null);
    }
    
    private void performInvalidityTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
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
