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
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class InvalidTestCases {
    @Test
    public void testInvalidRootHtml() throws Exception {
        performInvalidityTest("ttml1-invalid-root-html.xml", 1, 1);
    }

    @Test
    public void testInvalidRootDiv() throws Exception {
        performInvalidityTest("ttml1-invalid-root-div.xml", 1, 0);
    }

    @Test
    public void testInvalidMissingLanguage() throws Exception {
        performInvalidityTest("ttml1-invalid-missing-lang.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLanguage() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-lang.xml", 2, 0);
    }

    @Test
    public void testInvalidNonUniqueId() throws Exception {
        performInvalidityTest("ttml1-invalid-non-unique-id.xml", 4, 0);
    }

    @Test
    public void testInvalidBadActorAgentIdrefMultiple() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-actor-agent-idref-multiple.xml", 2, 0);
    }

    @Test
    public void testInvalidBadActorAgentIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-actor-agent-idref-non-significant.xml", 1, 0);
    }

    @Test
    public void testInvalidBadActorAgentIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-actor-agent-idref-wrong-eltype.xml", 1, 0);
    }

    @Test
    public void testInvalidBadAgentIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-agent-idref-non-significant.xml", 1, 0);
    }

    @Test
    public void testInvalidBadAgentIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-agent-idref-wrong-eltype.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeExtraPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-extra-part-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeExtraPart() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-extra-part.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesExceedsFrameRate() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-exceeds-frame-rate.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesExtraSubPart() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-extra-sub-part.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesInClockMode() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-in-clock-mode.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameExceedsSubFrameRate() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-sub-frame-exceeds-sub-frame-rate.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-sub-frame-sub-part-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesSubFrameSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-sub-frame-sub-part-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-whole-sub-part-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-whole-sub-part-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeFramesWholeSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-frames-whole-sub-part-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursEmptyWithMinutesAndSeconds() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-hours-empty-with-minutes-and-seconds.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursEmptyWithMinutesOnly() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-hours-empty-with-minutes-only.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-hours-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeHoursNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-hours-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeInternalWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-internal-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesEmptyWithSeconds() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-empty-with-seconds.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesEmptyWithoutSeconds() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-empty-without-seconds.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesExceeds59() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-exceeds-59.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-extra-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeMinutesNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-minutes-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExceeds60() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-exceeds-60.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExceeds60WithFraction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-exceeds-60-with-fraction.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-extra-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsExtraSubPart() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-extra-sub-part.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsFractionSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-fraction-sub-part-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsFractionSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-fraction-sub-part-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsMissing() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-missing.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-whole-sub-part-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-whole-sub-part-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginClockTimeSecondsWholeSubPartNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-clock-time-seconds-whole-sub-part-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFractionPartEmptyWithMetric() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-fraction-part-empty-with-metric.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFractionPartEmptyWithoutMetric() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-fraction-part-empty-without-metric.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeFramesInClockMode() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-frames-in-clock-mode.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeGarbageAfterMetric() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-garbage-after-metric.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMetricNonLetterWithFraction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-metric-non-letter-with-fraction.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMetricNonLetterWithoutFraction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-metric-non-letter-without-fraction.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMissingMetricWithFraction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-missing-metric-with-fraction.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeMissingMetricWithoutFraction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-missing-metric-without-fraction.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeUnknownMetricWrongCase() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-unknown-metric-wrong-case.xml", 1, 0);
    }

    @Test
    public void testInvalidBadBeginOffsetTimeUnknownMetric() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-begin-offset-time-unknown-metric.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionDelimiter() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-delimiter.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-extra-integer.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-missing-integer.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-negative.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionWithUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-with-units.xml", 1, 0);
    }

    @Test
    public void testInvalidBadCellResolutionZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-cell-resolution-zero.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-bad-component-syntax.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-extra-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-missing-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-negative-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-out-of-range-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-function-padded-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-extra-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashGarbageAfterDigits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-garbage-after-digits.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsGarbageAfterHash() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-missing-all-digits-garbage-after-hash.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashMissingAllDigitsHashOnly() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-missing-all-digits-hash-only.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBHashSpaceAfterHash() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgb-hash-space-after-hash.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionBadComponentSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-bad-component-syntax.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionExtraComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-extra-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionMissingComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-missing-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionNegativeComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-negative-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionOutOfRangeComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-out-of-range-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAFunctionPaddedComponent() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-function-padded-component.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAHashExtraDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-hash-extra-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAHashMissingDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-hash-missing-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorRGBAHashNonDigit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-rgba-hash-non-digit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorSpacePaddedNamedColor() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-space-padded-named-color.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBFunction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-space-padded-rgb-function.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBHash() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-space-padded-rgb-hash.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAFunction() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-space-padded-rgba-function.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorSpacePaddedRGBAHash() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-space-padded-rgba-hash.xml", 1, 0);
    }

    @Test
    public void testInvalidBadColorUnknownNamedColor() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-color-unknown-named-color.xml", 1, 0);
    }

    @Test
    public void testInvalidBadDurDisallowedSmpteDiscontinuous() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-dur-disallowed-smpte-discontinuous.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionEmptyDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extension-empty-designation-token.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extension-missing-designation.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionUnknownDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extension-unknown-designation-token.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionsBaseEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extensions-base-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionsBaseNonAbsolute() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extensions-base-non-absolute.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtensionsBaseSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extensions-base-syntax.xml", 2, 0);
    }

    @Test
    public void testInvalidBadExtentAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-comma-delimiter-with-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentExtraLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-extra-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentMissingLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-missing-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-missing-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-negative-height.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentNegativeWidth() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-negative-width.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentRootHeight() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-root-height.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentRootWidth() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-root-width.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-semicolon-delimiter-sans-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadExtentUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-extent-unknown-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeatureEmptyDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-feature-empty-designation-token.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeatureMissingDesignation() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-feature-missing-designation.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeatureUnknownDesignationToken() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-feature-unknown-designation-token.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeatureUnknownNamespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-feature-unknown-namespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeaturesBaseEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-features-base-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeaturesBaseNonAbsolute() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-features-base-non-absolute.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeaturesBaseUnknownNamespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-features-base-unknown-namespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFeaturesBaseSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-features-base-syntax.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFontFamilyEmptyItemFinal() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-empty-item-final.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyEmptyItemMedial() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-empty-item-medial.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyQuotedEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-quoted-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyQuotedExtraUnrecognized() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-quoted-extra-unrecognized.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyQuotedIncompleteEscape() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-quoted-incomplete-escape.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyQuotedUnterminated() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-quoted-unterminated.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedBadFollowing() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-bad-following.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedBadInitial() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-bad-initial.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedDoubleHyphen() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-double-hyphen.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedIncompleteEscape() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-incomplete-escape.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedMissingInitial() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-missing-initial.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedReservedInherit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-reserved-inherit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontFamilyUnquotedReservedInitial() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-family-unquoted-reserved-initial.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-comma-delimiter-with-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeExtraLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-extra-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-missing-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeMixedUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-mixed-units.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-negative-height.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeNegativeWidth() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-negative-width.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-semicolon-delimiter-sans-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFontSizeUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-font-size-unknown-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadFrameRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-negative.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-zero.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierDelimiter() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-delimiter.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-extra-integer.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-missing-integer.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-negative.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierWithUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-with-units.xml", 2, 0);
    }

    @Test
    public void testInvalidBadFrameRateMultiplierZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-frame-rate-multiplier-zero.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-comma-delimiter-with-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightExtraLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-extra-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-missing-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightNegativeHeight() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-negative-height.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-semicolon-delimiter-sans-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadLineHeightUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-line-height-unknown-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-comma-delimiter-with-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginExtraLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-extra-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginMissingLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-missing-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-missing-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-semicolon-delimiter-sans-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadOriginUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-origin-unknown-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingCommaDelimiterWithWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-comma-delimiter-with-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingExtraLength() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-extra-length.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingMissingUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-missing-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingNegativeBeforeAndAfter() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-negative-before-and-after.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingNegativeStartAndEnd() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-negative-start-and-end.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingSemicolonDelimiterSansWhitespace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-semicolon-delimiter-sans-whitespace.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPaddingUnknownUnit() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-padding-unknown-unit.xml", 1, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioDelimiter() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-delimiter.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-extra-integer.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioMissingInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-missing-integer.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-negative.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioWithUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-with-units.xml", 2, 0);
    }

    @Test
    public void testInvalidBadPixelAspectRatioZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-pixel-aspect-ratio-zero.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileAttributeEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-attribute-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileAttributeSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-attribute-syntax.xml", 2, 0);
    }

    @Test
    public void testInvalidBadProfileAttributeUnknownAbsoluteDesignator() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-attribute-unknown-absolute-designator.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileAttributeUnknownRelativeDesignator() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-attribute-unknown-relative-designator.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileUseAttributeEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-use-attribute-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileUseAttributeSyntax() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-use-attribute-syntax.xml", 2, 0);
    }

    @Test
    public void testInvalidBadProfileUseAttributeUnknownAbsoluteDesignator() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-use-attribute-unknown-absolute-designator.xml", 1, 0);
    }

    @Test
    public void testInvalidBadProfileUseAttributeUnknownRelativeDesignator() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-profile-use-attribute-unknown-relative-designator.xml", 1, 0);
    }

    @Test
    public void testInvalidBadRegionAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-region-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadRegionEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-region-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadRegionIdrefUnresolvable() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-region-idref-unresolvable.xml", 1, 0);
    }

    @Test
    public void testInvalidBadRegionIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-region-idref-wrong-eltype.xml", 1, 0);
    }

    @Test
    public void testInvalidBadStyleAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadStyleEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength1() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-loop-length-1.xml", 1, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength2() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-loop-length-2.xml", 2, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefLoopLength4() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-loop-length-4.xml", 4, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefNonSignificant() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-non-significant.xml", 1, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefUnresolvable() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-unresolvable.xml", 1, 0);
    }

    @Test
    public void testInvalidBadStyleIdrefWrongEltype() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-style-idref-wrong-eltype.xml", 1, 0);
    }

    @Test
    public void testInvalidBadSubFrameRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-sub-frame-rate-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadSubFrameRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-sub-frame-rate-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadSubFrameRateNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-sub-frame-rate-negative.xml", 2, 0);
    }

    @Test
    public void testInvalidBadSubFrameRateZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-sub-frame-rate-zero.xml", 2, 0);
    }

    @Test
    public void testInvalidBadTextOutlineAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurColor() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-blur-color.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurMissingUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-blur-missing-units.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBadBlurNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-blur-negative.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBackThicknessColor() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-thickness-color.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessMissingUnits() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-thickness-missing-units.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineBadThicknessNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-bad-thickness-negative.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTextOutlineMissingThickness() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-text-outline-missing-thickness.xml", 1, 0);
    }

    @Test
    public void testInvalidBadTickRateAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-tick-rate-all-space.xml", 2, 0);
    }

    @Test
    public void testInvalidBadTickRateEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-tick-rate-empty.xml", 2, 0);
    }

    @Test
    public void testInvalidBadTickRateNegative() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-tick-rate-negative.xml", 2, 0);
    }

    @Test
    public void testInvalidBadTickRateZero() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-tick-rate-zero.xml", 2, 0);
    }

    @Test
    public void testInvalidBadZIndexAllSpace() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-z-index-all-space.xml", 1, 0);
    }

    @Test
    public void testInvalidBadZIndexEmpty() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-z-index-empty.xml", 1, 0);
    }

    @Test
    public void testInvalidBadZIndexExtraInteger() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-z-index-extra-integer.xml", 1, 0);
    }

    @Test
    public void testInvalidBadZIndexNonIntegerReal() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-z-index-non-integer-real.xml", 1, 0);
    }

    @Test
    public void testInvalidBadZIndexNonIntegerToken() throws Exception {
        performInvalidityTest("ttml1-invalid-bad-z-index-non-integer-token.xml", 1, 0);
    }

    @Test
    public void testInvalidMetadataDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-metadata-disallowed-attributes.xml", 18, 0);
    }

    @Test
    public void testInvalidMetadataUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-metadata-unknown-attributes.xml", 2, 0);
    }

    @Test
    public void testInvalidMetadataNonElementContent() throws Exception {
        performInvalidityTest("ttml1-invalid-metadata-non-element-content.xml", 1, 0);
    }

    @Test
    public void testInvalidParameterDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-parameter-disallowed-attributes.xml", 18, 0);
    }

    @Test
    public void testInvalidParameterUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-parameter-unknown-attributes.xml", 2, 0);
    }

    @Test
    public void testInvalidSetMaximumStyleCountExceeded() throws Exception {
        performInvalidityTest("ttml1-invalid-set-maximum-style-count-exceeded.xml", 2, 0);
    }

    @Test
    public void testInvalidStyleDisallowedAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-style-disallowed-attributes.xml", 15, 0);
    }

    @Test
    public void testInvalidStyleUnknownAttributes() throws Exception {
        performInvalidityTest("ttml1-invalid-style-unknown-attributes.xml", 3, 0);
    }

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
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) == 0) {
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
