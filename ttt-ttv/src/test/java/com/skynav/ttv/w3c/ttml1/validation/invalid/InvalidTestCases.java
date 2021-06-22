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
 
package com.skynav.ttv.w3c.ttml1.validation.invalid;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {

    @Test
    public void testInvalidBadActorAgentIdrefMultiple() throws Exception {
        performInvalidityTest("ttml1-invld-bad-actor-agent-idref-multiple.xml");
    }

    @Test
    public void testInvalidBadActorAgentIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invld-bad-actor-agent-idref-non-significant.xml");
    }

    @Test
    public void testInvalidBadActorAgentIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invld-bad-actor-agent-idref-wrong-eltype.xml");
    }

    @Test
    public void testInvalidBadAgentIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invld-bad-agent-idref-non-significant.xml");
    }

    @Test
    public void testInvalidBadAgentIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invld-bad-agent-idref-wrong-eltype.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeExtraPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-extra-part-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeExtraPart() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-extra-part.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesExceedsFrameRate() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-exceeds-frame-rate.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesExtraSubPart() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-extra-sub-part.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesInClockMode() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-in-clock-mode.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameExceedsSubFrameRate() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-sub-frame-exceeds-sub-frame-rate.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-sub-frame-sub-part-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-sub-frame-sub-part-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-whole-sub-part-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-whole-sub-part-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-frames-whole-sub-part-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursEmptyWithMinutesAndSeconds() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-hours-empty-with-minutes-and-seconds.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursEmptyWithMinutesOnly() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-hours-empty-with-minutes-only.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-hours-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-hours-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeInternalWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-internal-whitespace.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesEmptyWithSeconds() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-empty-with-seconds.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesEmptyWithoutSeconds() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-empty-without-seconds.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesExceeds59() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-exceeds-59.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-extra-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-minutes-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExceeds60() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-exceeds-60.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExceeds60WithFraction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-exceeds-60-with-fraction.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-extra-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExtraSubPart() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-extra-sub-part.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsFractionSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-fraction-sub-part-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsFractionSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-fraction-sub-part-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsMissing() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-missing.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-whole-sub-part-empty.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-whole-sub-part-missing-digit.xml");
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-clock-time-seconds-whole-sub-part-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-empty.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFractionPartEmptyWithMetric() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-fraction-part-empty-with-metric.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFractionPartEmptyWithoutMetric() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-fraction-part-empty-without-metric.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFramesInClockMode() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-frames-in-clock-mode.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeGarbageAfterMetric() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-garbage-after-metric.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMetricNonLetterWithFraction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-metric-non-letter-with-fraction.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMetricNonLetterWithoutFraction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-metric-non-letter-without-fraction.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMissingMetricWithFraction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-missing-metric-with-fraction.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMissingMetricWithoutFraction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-missing-metric-without-fraction.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-non-digit.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeUnknownMetricWrongCase() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-unknown-metric-wrong-case.xml");
    }

    @Test
    public void testInvalidBadBeginOffsetTimeUnknownMetric() throws Exception {
        performInvalidityTest("ttml1-invld-bad-begin-offset-time-unknown-metric.xml");
    }

    @Test
    public void testInvalidBadCellResolutionAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-all-space.xml");
    }

    @Test
    public void testInvalidBadCellResolutionDelimiter() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-delimiter.xml");
    }

    @Test
    public void testInvalidBadCellResolutionEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-empty.xml");
    }

    @Test
    public void testInvalidBadCellResolutionExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-extra-integer.xml");
    }

    @Test
    public void testInvalidBadCellResolutionMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-missing-integer.xml");
    }

    @Test
    public void testInvalidBadCellResolutionNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-negative.xml");
    }

    @Test
    public void testInvalidBadCellResolutionWithUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-with-units.xml");
    }

    @Test
    public void testInvalidBadCellResolutionZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-cell-resolution-zero.xml");
    }

    @Test
    public void testInvalidBadColorAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-all-space.xml");
    }

    @Test
    public void testInvalidBadColorEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-empty.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-bad-component-syntax.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-extra-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-missing-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-negative-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-out-of-range-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-function-padded-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-extra-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashGarbageAfterDigits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-garbage-after-digits.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsGarbageAfterHash() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-missing-all-digits-garbage-after-hash.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsHashOnly() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-missing-all-digits-hash-only.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-missing-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-non-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBHashSpaceAfterHash() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgb-hash-space-after-hash.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-bad-component-syntax.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-extra-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-missing-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-negative-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-out-of-range-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-function-padded-component.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-hash-extra-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-hash-missing-digit.xml");
    }

    @Test
    public void testInvalidBadColorRGBAHashNonDigit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-rgba-hash-non-digit.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedNamedColor() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-space-padded-named-color.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBFunction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-space-padded-rgb-function.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBHash() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-space-padded-rgb-hash.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAFunction() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-space-padded-rgba-function.xml");
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAHash() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-space-padded-rgba-hash.xml");
    }

    @Test
    public void testInvalidBadColorUnknownNamedColor() throws Exception {
        performInvalidityTest("ttml1-invld-bad-color-unknown-named-color.xml");
    }

    @Test
    public void testInvalidBadDurDisallowedSmpteDiscontinuous() throws Exception {
        performInvalidityTest("ttml1-invld-bad-dur-disallowed-smpte-discontinuous.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionEmptyDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extension-empty-designation-token.xml");
    }

    @Test
    public void testInvalidBadExtensionMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extension-missing-designation.xml");
    }

    @Test
    public void testInvalidBadExtensionUnknownDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extension-unknown-designation-token.xml");
    }

    @Test
    public void testInvalidBadExtensionsBaseEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extensions-base-empty.xml");
    }

    @Test
    public void testInvalidBadExtensionsBaseNonAbsolute() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extensions-base-non-absolute.xml");
    }

    @Test
    public void testInvalidBadExtensionsBaseSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extensions-base-syntax.xml");
    }

    @Test
    public void testInvalidBadExtentAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-all-space.xml");
    }

    @Test
    public void testInvalidBadExtentCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadExtentEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-empty.xml");
    }

    @Test
    public void testInvalidBadExtentExtraLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-extra-length.xml");
    }

    @Test
    public void testInvalidBadExtentMissingLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-missing-length.xml");
    }

    @Test
    public void testInvalidBadExtentMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-missing-unit.xml");
    }

    @Test
    public void testInvalidBadExtentNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-negative-height.xml");
    }

    @Test
    public void testInvalidBadExtentNegativeWidth() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-negative-width.xml");
    }

    @Test
    public void testInvalidBadExtentRootHeight() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-root-height.xml");
    }

    @Test
    public void testInvalidBadExtentRootWidth() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-root-width.xml");
    }

    @Test
    public void testInvalidBadExtentSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadExtentUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-extent-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadFeatureEmptyDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invld-bad-feature-empty-designation-token.xml");
    }

    @Test
    public void testInvalidBadFeatureMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-feature-missing-designation.xml");
    }

    @Test
    public void testInvalidBadFeatureUnknownDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invld-bad-feature-unknown-designation-token.xml");
    }

    @Test
    public void testInvalidBadFeatureUnknownNamespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-feature-unknown-namespace.xml");
    }

    @Test
    public void testInvalidBadFeaturesBaseEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-features-base-empty.xml");
    }

    @Test
    public void testInvalidBadFeaturesBaseNonAbsolute() throws Exception {
        performInvalidityTest("ttml1-invld-bad-features-base-non-absolute.xml");
    }

    @Test
    public void testInvalidBadFeaturesBaseUnknownNamespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-features-base-unknown-namespace.xml");
    }

    @Test
    public void testInvalidBadFeaturesBaseSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-features-base-syntax.xml");
    }

    @Test
    public void testInvalidBadFontFamilyEmptyItemFinal() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-empty-item-final.xml");
    }

    @Test
    public void testInvalidBadFontFamilyEmptyItemMedial() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-empty-item-medial.xml");
    }

    @Test
    public void testInvalidBadFontFamilyEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-empty.xml");
    }

    @Test
    public void testInvalidBadFontFamilyQuotedEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-quoted-empty.xml");
    }

    @Test
    public void testInvalidBadFontFamilyQuotedExtraUnrecognized() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-quoted-extra-unrecognized.xml");
    }

    @Test
    public void testInvalidBadFontFamilyQuotedIncompleteEscape() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-quoted-incomplete-escape.xml");
    }

    @Test
    public void testInvalidBadFontFamilyQuotedUnterminated() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-quoted-unterminated.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedBadFollowing() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-bad-following.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedBadInitial() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-bad-initial.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedDoubleHyphen() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-double-hyphen.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedIncompleteEscape() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-incomplete-escape.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedMissingInitial() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-missing-initial.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedReservedInherit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-reserved-inherit.xml");
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedReservedInitial() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-family-unquoted-reserved-initial.xml");
    }

    @Test
    public void testInvalidBadFontSizeAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-all-space.xml");
    }

    @Test
    public void testInvalidBadFontSizeCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadFontSizeEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-empty.xml");
    }

    @Test
    public void testInvalidBadFontSizeExtraLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-extra-length.xml");
    }

    @Test
    public void testInvalidBadFontSizeMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-missing-unit.xml");
    }

    @Test
    public void testInvalidBadFontSizeMixedUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-mixed-units.xml");
    }

    @Test
    public void testInvalidBadFontSizeNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-negative-height.xml");
    }

    @Test
    public void testInvalidBadFontSizeNegativeWidth() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-negative-width.xml");
    }

    @Test
    public void testInvalidBadFontSizeSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadFontSizeUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-font-size-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadFrameRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-all-space.xml");
    }

    @Test
    public void testInvalidBadFrameRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-empty.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-all-space.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierDelimiter() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-delimiter.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-empty.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-extra-integer.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-missing-integer.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-negative.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierWithUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-with-units.xml");
    }

    @Test
    public void testInvalidBadFrameRateMultiplierZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-multiplier-zero.xml");
    }

    @Test
    public void testInvalidBadFrameRateNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-negative.xml");
    }

    @Test
    public void testInvalidBadFrameRateZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-frame-rate-zero.xml");
    }

    @Test
    public void testInvalidBadLanguage() throws Exception {
        performInvalidityTest("ttml1-invld-bad-lang.xml");
    }

    @Test
    public void testInvalidBadLineHeightAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-all-space.xml");
    }

    @Test
    public void testInvalidBadLineHeightEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-empty.xml");
    }

    @Test
    public void testInvalidBadLineHeightExtraLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-extra-length.xml");
    }

    @Test
    public void testInvalidBadLineHeightMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-missing-unit.xml");
    }

    @Test
    public void testInvalidBadLineHeightNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-negative-height.xml");
    }

    @Test
    public void testInvalidBadLineHeightUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-line-height-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadOriginAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-all-space.xml");
    }

    @Test
    public void testInvalidBadOriginCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadOriginEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-empty.xml");
    }

    @Test
    public void testInvalidBadOriginExtraLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-extra-length.xml");
    }

    @Test
    public void testInvalidBadOriginMissingLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-missing-length.xml");
    }

    @Test
    public void testInvalidBadOriginMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-missing-unit.xml");
    }

    @Test
    public void testInvalidBadOriginSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadOriginUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-origin-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadPaddingAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-all-space.xml");
    }

    @Test
    public void testInvalidBadPaddingCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-comma-delimiter-with-whitespace.xml");
    }

    @Test
    public void testInvalidBadPaddingEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-empty.xml");
    }

    @Test
    public void testInvalidBadPaddingExtraLength() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-extra-length.xml");
    }

    @Test
    public void testInvalidBadPaddingMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-missing-unit.xml");
    }

    @Test
    public void testInvalidBadPaddingNegativeBeforeAndAfter() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-negative-before-and-after.xml");
    }

    @Test
    public void testInvalidBadPaddingNegativeStartAndEnd() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-negative-start-and-end.xml");
    }

    @Test
    public void testInvalidBadPaddingSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-semicolon-delimiter-sans-whitespace.xml");
    }

    @Test
    public void testInvalidBadPaddingUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invld-bad-padding-unknown-unit.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-all-space.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioDelimiter() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-delimiter.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-empty.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-extra-integer.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-missing-integer.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-negative.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioWithUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-with-units.xml");
    }

    @Test
    public void testInvalidBadPixelAspectRatioZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-pixel-aspect-ratio-zero.xml");
    }

    @Test
    public void testInvalidBadProfileAttributeEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-attribute-empty.xml");
    }

    @Test
    public void testInvalidBadProfileAttributeSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-attribute-syntax.xml");
    }

    @Test
    public void testInvalidBadProfileAttributeUnknownAbsoluteDesignator() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-attribute-unknown-absolute-designator.xml");
    }

    @Test
    public void testInvalidBadProfileAttributeUnknownRelativeDesignator() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-attribute-unknown-relative-designator.xml");
    }

    @Test
    public void testInvalidBadProfileExtensionEmptyDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-extension-empty-designation.xml");
    }

    @Test
    public void testInvalidBadProfileExtensionMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-extension-missing-designation.xml");
    }

    @Test
    public void testInvalidBadProfileExtensionUnknownDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-extension-unknown-designation.xml");
    }

    @Test
    public void testInvalidBadProfileExtensionUnknownValue() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-extension-unknown-value.xml");
    }

    @Test
    public void testInvalidBadProfileFeatureEmptyDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-feature-empty-designation.xml");
    }

    @Test
    public void testInvalidBadProfileFeatureMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-feature-missing-designation.xml");
    }

    @Test
    public void testInvalidBadProfileFeatureUnknownDesignation() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-feature-unknown-designation.xml");
    }

    @Test
    public void testInvalidBadProfileFeatureUnknownValue() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-feature-unknown-value.xml");
    }

    @Test
    public void testInvalidBadProfileUseAttributeEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-use-attribute-empty.xml");
    }

    @Test
    public void testInvalidBadProfileUseAttributeSyntax() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-use-attribute-syntax.xml");
    }

    @Test
    public void testInvalidBadProfileUseAttributeUnknownAbsoluteDesignator() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-use-attribute-unknown-absolute-designator.xml");
    }

    @Test
    public void testInvalidBadProfileUseAttributeUnknownRelativeDesignator() throws Exception {
        performInvalidityTest("ttml1-invld-bad-profile-use-attribute-unknown-relative-designator.xml");
    }

    @Test
    public void testInvalidBadRegionAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-region-all-space.xml");
    }

    @Test
    public void testInvalidBadRegionEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-region-empty.xml");
    }

    @Test
    public void testInvalidBadRegionIdrefUnresolvable() throws Exception {
        performInvalidityTest("ttml1-invld-bad-region-idref-unresolvable.xml");
    }

    @Test
    public void testInvalidBadRegionIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invld-bad-region-idref-wrong-eltype.xml");
    }

    @Test
    public void testInvalidBadStyleAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-all-space.xml");
    }

    @Test
    public void testInvalidBadStyleEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-empty.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength1() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-loop-length-1.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength2() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-loop-length-2.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength4() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-loop-length-4.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-non-significant.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefUnresolvable() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-unresolvable.xml");
    }

    @Test
    public void testInvalidBadStyleIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invld-bad-style-idref-wrong-eltype.xml");
    }

    @Test
    public void testInvalidBadSubFrameRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-sub-frame-rate-all-space.xml");
    }

    @Test
    public void testInvalidBadSubFrameRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-sub-frame-rate-empty.xml");
    }

    @Test
    public void testInvalidBadSubFrameRateNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-sub-frame-rate-negative.xml");
    }

    @Test
    public void testInvalidBadSubFrameRateZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-sub-frame-rate-zero.xml");
    }

    @Test
    public void testInvalidBadTextOutlineAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-all-space.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurColor() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-blur-color.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurMissingUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-blur-missing-units.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-blur-negative.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBackThicknessColor() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-thickness-color.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessMissingUnits() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-thickness-missing-units.xml");
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-bad-thickness-negative.xml");
    }

    @Test
    public void testInvalidBadTextOutlineEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-empty.xml");
    }

    @Test
    public void testInvalidBadTextOutlineMissingThickness() throws Exception {
        performInvalidityTest("ttml1-invld-bad-text-outline-missing-thickness.xml");
    }

    @Test
    public void testInvalidBadTickRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-tick-rate-all-space.xml");
    }

    @Test
    public void testInvalidBadTickRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-tick-rate-empty.xml");
    }

    @Test
    public void testInvalidBadTickRateNegative() throws Exception {
        performInvalidityTest("ttml1-invld-bad-tick-rate-negative.xml");
    }

    @Test
    public void testInvalidBadTickRateZero() throws Exception {
        performInvalidityTest("ttml1-invld-bad-tick-rate-zero.xml");
    }

    @Test
    public void testInvalidBadUnicodeBidiAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-unicode-bidi-all-space.xml");
    }

    @Test
    public void testInvalidBadUnicodeBidiEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-unicode-bidi-empty.xml");
    }

    @Test
    public void testInvalidBadUnicodeBidiUnknownKeyword() throws Exception {
        performInvalidityTest("ttml1-invld-bad-unicode-bidi-unknown-keyword.xml");
    }

    @Test
    public void testInvalidBadZIndexAllSpace() throws Exception {
        performInvalidityTest("ttml1-invld-bad-z-index-all-space.xml");
    }

    @Test
    public void testInvalidBadZIndexEmpty() throws Exception {
        performInvalidityTest("ttml1-invld-bad-z-index-empty.xml");
    }

    @Test
    public void testInvalidBadZIndexExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invld-bad-z-index-extra-integer.xml");
    }

    @Test
    public void testInvalidBadZIndexNonIntegerReal() throws Exception {
        performInvalidityTest("ttml1-invld-bad-z-index-non-integer-real.xml");
    }

    @Test
    public void testInvalidBadZIndexNonIntegerToken() throws Exception {
        performInvalidityTest("ttml1-invld-bad-z-index-non-integer-token.xml");
    }

    @Test
    public void testInvalidMetadataDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-metadata-disallowed-attributes.xml");
    }

    @Test
    public void testInvalidMetadataUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-metadata-unknown-attributes.xml");
    }

    @Test
    public void testInvalidMetadataNonElementContent() throws Exception {
        performInvalidityTest("ttml1-invld-metadata-non-element-content.xml");
    }

    @Test
    public void testInvalidMissingLanguage() throws Exception {
        performInvalidityTest("ttml1-invld-missing-lang.xml");
    }

    @Test
    public void testInvalidNonUniqueId() throws Exception {
        performInvalidityTest("ttml1-invld-non-unique-id.xml");
    }

    @Test
    public void testNonWellFormedXMLDeclaration() throws Exception {
        performInvalidityTest("ttml1-invld-non-well-formed-xml-declaration.xml", 1, 0);
    }

    @Test
    public void testNonWellFormedStartTag() throws Exception {
        performInvalidityTest("ttml1-invld-non-well-formed-start-tag.xml", 1, 0);
    }

    @Test
    public void testInvalidParameterDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-parameter-disallowed-attributes.xml");
    }

    @Test
    public void testInvalidParameterUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-parameter-unknown-attributes.xml");
    }

    @Test
    public void testInvalidRootHtml() throws Exception {
        performInvalidityTest("ttml1-invld-root-html.xml", 1, 1);
    }

    @Test
    public void testInvalidRootDiv() throws Exception {
        performInvalidityTest("ttml1-invld-root-div.xml", 1, 0);
    }

    @Test
    public void testInvalidSetMaximumStyleCountExceeded() throws Exception {
        performInvalidityTest("ttml1-invld-set-maximum-style-count-exceeded.xml");
    }

    @Test
    public void testInvalidStyleDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-style-disallowed-attributes.xml");
    }

    @Test
    public void testInvalidStyleUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invld-style-unknown-attributes.xml");
    }

    /* -------------------------------------------------------------------------------- */

    private void performInvalidityTest(String resourceName) {
        performInvalidityTest(resourceName, -1, -1, null);
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
