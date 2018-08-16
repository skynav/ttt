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
 
package com.skynav.ttv.ttml2.validation.valid;

import java.net.URL;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {

    @Test
    public void testValidTTML2AnimateDiscrete() throws Exception {
        performValidityTest("ttml2-valid-animate-discrete.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimateFill() throws Exception {
        performValidityTest("ttml2-valid-animate-fill.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimateLinear() throws Exception {
        performValidityTest("ttml2-valid-animate-linear.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimatePaced() throws Exception {
        performValidityTest("ttml2-valid-animate-paced.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimateRepeatCount() throws Exception {
        performValidityTest("ttml2-valid-animate-repeat-count.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimateSpline() throws Exception {
        performValidityTest("ttml2-valid-animate-spline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AnimateOutOfLine() throws Exception {
        performValidityTest("ttml2-valid-animate-out-of-line.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AudioEmbedding() throws Exception {
        performValidityTest("ttml2-valid-audio-embedding.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AudioInBlock() throws Exception {
        performValidityTest("ttml2-valid-audio-in-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2AudioInInline() throws Exception {
        performValidityTest("ttml2-valid-audio-in-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundClip() throws Exception {
        performValidityTest("ttml2-valid-background-clip.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundColor() throws Exception {
        performValidityTest("ttml2-valid-background-color.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundExtent() throws Exception {
        performValidityTest("ttml2-valid-background-extent.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundImage() throws Exception {
        performValidityTest("ttml2-valid-background-image.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundOrigin() throws Exception {
        performValidityTest("ttml2-valid-background-origin.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundPosition() throws Exception {
        performValidityTest("ttml2-valid-background-position.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BackgroundRepeat() throws Exception {
        performValidityTest("ttml2-valid-background-repeat.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Base() throws Exception {
        performValidityTest("ttml2-valid-base.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BaseGeneral() throws Exception {
        performValidityTest("ttml2-valid-base-general.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Border() throws Exception {
        performValidityTest("ttml2-valid-border.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BorderBlock() throws Exception {
        performValidityTest("ttml2-valid-border-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BorderInline() throws Exception {
        performValidityTest("ttml2-valid-border-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BorderRadii1() throws Exception {
        performValidityTest("ttml2-valid-border-radii-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BorderRadii2() throws Exception {
        performValidityTest("ttml2-valid-border-radii-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BorderRegion() throws Exception {
        performValidityTest("ttml2-valid-border-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BPD() throws Exception {
        performValidityTest("ttml2-valid-bpd.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BPDBlock() throws Exception {
        performValidityTest("ttml2-valid-bpd-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BPDInline() throws Exception {
        performValidityTest("ttml2-valid-bpd-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2BPDInlineBlock() throws Exception {
        performValidityTest("ttml2-valid-bpd-inline-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileCombinationIgnore() throws Exception {
        performValidityTest("ttml2-valid-content-profile-combination-ignore.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileCombinationLeastRestrictive() throws Exception {
        performValidityTest("ttml2-valid-content-profile-combination-least-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileCombinationMostRestrictive() throws Exception {
        performValidityTest("ttml2-valid-content-profile-combination-most-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileCombinationReplace() throws Exception {
        performValidityTest("ttml2-valid-content-profile-combination-replace.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileMultiple() throws Exception {
        performValidityTest("ttml2-valid-content-profile-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfileNested() throws Exception {
        performValidityTest("ttml2-valid-content-profile-nested.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesQuantifiedAllMultiple() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-quantified-all-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesQuantifiedAllSingle() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-quantified-all-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesUnquantifiedMultiple() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-unquantified-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ContentProfilesUnquantifiedSingle() throws Exception {
        performValidityTest("ttml2-valid-content-profiles-unquantified-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DataEmbeddingChunked() throws Exception {
        performValidityTest("ttml2-valid-data-embedding-chunked.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DataEmbeddingSimple() throws Exception {
        performValidityTest("ttml2-valid-data-embedding-simple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DataEmbeddingSourced() throws Exception {
        performValidityTest("ttml2-valid-data-embedding-sourced.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DirectionP() throws Exception {
        performValidityTest("ttml2-valid-direction-p.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DirectionSpan() throws Exception {
        performValidityTest("ttml2-valid-direction-span.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Disparity() throws Exception {
        performValidityTest("ttml2-valid-disparity.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Display1() throws Exception {
        performValidityTest("ttml2-valid-display-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Display2() throws Exception {
        performValidityTest("ttml2-valid-display-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlign1() throws Exception {
        performValidityTest("ttml2-valid-display-align-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlign1Region() throws Exception {
        performValidityTest("ttml2-valid-display-align-1-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlign2() throws Exception {
        performValidityTest("ttml2-valid-display-align-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlign2Block() throws Exception {
        performValidityTest("ttml2-valid-display-align-2-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlign2Region() throws Exception {
        performValidityTest("ttml2-valid-display-align-2-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAlignJustify() throws Exception {
        performValidityTest("ttml2-valid-display-align-justify.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAspectRatio169() throws Exception {
        performValidityTest("ttml2-valid-display-aspect-ratio-16-9.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayAspectRatio43() throws Exception {
        performValidityTest("ttml2-valid-display-aspect-ratio-4-3.xml", -1, -1);
    }

    @Test
    public void testValidTTML2DisplayInlineBlock() throws Exception {
        performValidityTest("ttml2-valid-display-inline-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent1() throws Exception {
        performValidityTest("ttml2-valid-extent-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent1Region() throws Exception {
        performValidityTest("ttml2-valid-extent-1-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent1RootAuto() throws Exception {
        performValidityTest("ttml2-valid-extent-1-root-auto.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent1RootDefinite() throws Exception {
        performValidityTest("ttml2-valid-extent-1-root-definite.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2() throws Exception {
        performValidityTest("ttml2-valid-extent-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2Image() throws Exception {
        performValidityTest("ttml2-valid-extent-2-image.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2Length() throws Exception {
        performValidityTest("ttml2-valid-extent-2-length.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2Measure() throws Exception {
        performValidityTest("ttml2-valid-extent-2-measure.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2Region() throws Exception {
        performValidityTest("ttml2-valid-extent-2-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2RegionContain() throws Exception {
        performValidityTest("ttml2-valid-extent-2-region-contain.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2RegionCover() throws Exception {
        performValidityTest("ttml2-valid-extent-2-region-cover.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2RootAuto() throws Exception {
        performValidityTest("ttml2-valid-extent-2-root-auto.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Extent2RootContain() throws Exception {
        performValidityTest("ttml2-valid-extent-2-root-contain.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontEmbedding() throws Exception {
        performValidityTest("ttml2-valid-font-embedding.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontKerning() throws Exception {
        performValidityTest("ttml2-valid-font-kerning.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontShear() throws Exception {
        performValidityTest("ttml2-valid-font-shear.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontSelectionStrategy() throws Exception {
        performValidityTest("ttml2-valid-font-selection-strategy.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontSelectionStrategyCharacter() throws Exception {
        performValidityTest("ttml2-valid-font-selection-strategy-character.xml", -1, -1);
    }

    @Test
    public void testValidTTML2FontVariant() throws Exception {
        performValidityTest("ttml2-valid-font-variant.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Gain() throws Exception {
        performValidityTest("ttml2-valid-gain.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageEmbedding() throws Exception {
        performValidityTest("ttml2-valid-image-embedding.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageExtent() throws Exception {
        performValidityTest("ttml2-valid-image-extent.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInBlockPng() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-png.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInBlockSimpleSourceJpg() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInBlockSimpleSourcePng() throws Exception {
        performValidityTest("ttml2-valid-image-in-block-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInInlinePng() throws Exception {
        performValidityTest("ttml2-valid-image-in-inline-png.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInInlineSimpleSourceJpg() throws Exception {
        performValidityTest("ttml2-valid-image-in-inline-simple-source-jpg.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ImageInInlineSimpleSourcePng() throws Exception {
        performValidityTest("ttml2-valid-image-in-inline-simple-source-png.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InferProcessorProfileMethodLoose() throws Exception {
        performValidityTest("ttml2-valid-infer-processor-profile-method-loose.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InferProcessorProfileMethodStrict() throws Exception {
        performValidityTest("ttml2-valid-infer-processor-profile-method-strict.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InferProcessorProfileSourceFirst() throws Exception {
        performValidityTest("ttml2-valid-infer-processor-profile-source-first.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InferProcessorProfileSourceCombined() throws Exception {
        performValidityTest("ttml2-valid-infer-processor-profile-source-combined.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InitialDuplicate() throws Exception {
        performValidityTest("ttml2-valid-initial-duplicate.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InitialMultiple() throws Exception {
        performValidityTest("ttml2-valid-initial-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InitialNone() throws Exception {
        performValidityTest("ttml2-valid-initial-none.xml", -1, -1);
    }

    @Test
    public void testValidTTML2InitialSingle() throws Exception {
        performValidityTest("ttml2-valid-initial-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2IPD() throws Exception {
        performValidityTest("ttml2-valid-ipd.xml", -1, -1);
    }

    @Test
    public void testValidTTML2IPDBlock() throws Exception {
        performValidityTest("ttml2-valid-ipd-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2IPDInline() throws Exception {
        performValidityTest("ttml2-valid-ipd-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2IPDInlineBlock() throws Exception {
        performValidityTest("ttml2-valid-ipd-inline-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Length1() throws Exception {
        performValidityTest("ttml2-valid-length-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Length2() throws Exception {
        performValidityTest("ttml2-valid-length-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Length2RootContainerRelative() throws Exception {
        performValidityTest("ttml2-valid-length-2-root-container-relative.xml", -1, -1);
    }

    @Test
    public void testValidTTML2LetterSpacing() throws Exception {
        performValidityTest("ttml2-valid-letter-spacing.xml", -1, -1);
    }

    @Test
    public void testValidTTML2LineShear() throws Exception {
        performValidityTest("ttml2-valid-line-shear.xml", -1, -1);
    }

    @Test
    public void testValidTTML2LuminanceGain() throws Exception {
        performValidityTest("ttml2-valid-luminance-gain.xml", -1, -1);
    }

    @Test
    public void testValidTTML2MetadataItemNamed() throws Exception {
        performValidityTest("ttml2-valid-metadata-item-named.xml", -1, -1);
    }

    @Test
    public void testValidTTML2MetadataItemQualified() throws Exception {
        performValidityTest("ttml2-valid-metadata-item-qualified.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Opacity() throws Exception {
        performValidityTest("ttml2-valid-opacity.xml", -1, -1);
    }

    @Test
    public void testValidTTML2OpacityBlock() throws Exception {
        performValidityTest("ttml2-valid-opacity-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2OpacityInline() throws Exception {
        performValidityTest("ttml2-valid-opacity-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2OpacityOutOfRange() throws Exception {
        performValidityTest("ttml2-valid-opacity-out-of-range.xml", -1, -1);
    }

    @Test
    public void testValidTTML2OpacityRegion() throws Exception {
        performValidityTest("ttml2-valid-opacity-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Padding() throws Exception {
        performValidityTest("ttml2-valid-padding.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PaddingBlock() throws Exception {
        performValidityTest("ttml2-valid-padding-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PaddingInline() throws Exception {
        performValidityTest("ttml2-valid-padding-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PaddingRegion() throws Exception {
        performValidityTest("ttml2-valid-padding-region.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Pan() throws Exception {
        performValidityTest("ttml2-valid-pan.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PermitFeatureNarrowingAllow() throws Exception {
        performValidityTest("ttml2-valid-permit-feature-narrowing-allow.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PermitFeatureNarrowingDisallow() throws Exception {
        performValidityTest("ttml2-valid-permit-feature-narrowing-disallow.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PermitFeatureWideningAllow() throws Exception {
        performValidityTest("ttml2-valid-permit-feature-widening-allow.xml", -1, -1);
    }

    @Test
    public void testValidTTML2PermitFeatureWideningDisallow() throws Exception {
        performValidityTest("ttml2-valid-permit-feature-widening-disallow.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Pitch() throws Exception {
        performValidityTest("ttml2-valid-pitch.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Position() throws Exception {
        performValidityTest("ttml2-valid-position.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Presentation1() throws Exception {
        performValidityTest("ttml2-valid-presentation-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Presentation2() throws Exception {
        performValidityTest("ttml2-valid-presentation-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileCombinationIgnore() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-combination-ignore.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileCombinationLeastRestrictive() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-combination-least-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileCombinationMostRestrictive() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-combination-most-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileCombinationReplace() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-combination-replace.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfileNested() throws Exception {
        performValidityTest("ttml2-valid-processor-profile-nested.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAllMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-all-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAllSingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-all-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAnyMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-any-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesQuantifiedAnySingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-quantified-any-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesUnquantifiedMultiple() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-unquantified-multiple.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProcessorProfilesUnquantifiedSingle() throws Exception {
        performValidityTest("ttml2-valid-processor-profiles-unquantified-single.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileCombineLeastRestrictive() throws Exception {
        performValidityTest("ttml2-valid-profile-combine-least-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileCombineMostRestrictive() throws Exception {
        performValidityTest("ttml2-valid-profile-combine-most-restrictive.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileCombineReplace() throws Exception {
        performValidityTest("ttml2-valid-profile-combine-replace.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileDesignator() throws Exception {
        performValidityTest("ttml2-valid-profile-designator.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileTypeContent() throws Exception {
        performValidityTest("ttml2-valid-profile-type-content.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileTypeProcessor() throws Exception {
        performValidityTest("ttml2-valid-profile-type-processor.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ProfileUse() throws Exception {
        performValidityTest("ttml2-valid-profile-use.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RegionInline() throws Exception {
        performValidityTest("ttml2-valid-region-inline.xml", -1, -1, new String[] { "--debug-exceptions" });
    }

    @Test
    public void testValidTTML2RegionImpliedAnimation() throws Exception {
        performValidityTest("ttml2-valid-region-implied-animation.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RegionTiming() throws Exception {
        performValidityTest("ttml2-valid-region-timing.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ResourcesAll() throws Exception {
        performValidityTest("ttml2-valid-resources-all.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Ruby() throws Exception {
        performValidityTest("ttml2-valid-ruby.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubyAlign() throws Exception {
        performValidityTest("ttml2-valid-ruby-align.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubyAlignMinimal() throws Exception {
        performValidityTest("ttml2-valid-ruby-align-minimal.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubyAlignWithBase() throws Exception {
        performValidityTest("ttml2-valid-ruby-align-with-base.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubyPosition() throws Exception {
        performValidityTest("ttml2-valid-ruby-position.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubyReserve() throws Exception {
        performValidityTest("ttml2-valid-ruby-reserve.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubySpan() throws Exception {
        performValidityTest("ttml2-valid-ruby-span.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubySpanDelimiters() throws Exception {
        performValidityTest("ttml2-valid-ruby-span-delimiters.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubySpanShortcuts() throws Exception {
        performValidityTest("ttml2-valid-ruby-span-shortcuts.xml", -1, -1);
    }

    @Test
    public void testValidTTML2RubySpanShortcutsDelimiters() throws Exception {
        performValidityTest("ttml2-valid-ruby-span-shortcuts-delimiters.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Set() throws Exception {
        performValidityTest("ttml2-valid-set.xml", -1, -1);
    }

    @Test
    public void testValidTTML2SetFill() throws Exception {
        performValidityTest("ttml2-valid-set-fill.xml", -1, -1);
    }

    @Test
    public void testValidTTML2SetMultipleStyles() throws Exception {
        performValidityTest("ttml2-valid-set-multiple-styles.xml", -1, -1);
    }

    @Test
    public void testValidTTML2SetRepeatCount() throws Exception {
        performValidityTest("ttml2-valid-set-repeat-count.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Shear() throws Exception {
        performValidityTest("ttml2-valid-shear.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Speak() throws Exception {
        performValidityTest("ttml2-valid-speak.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Speech() throws Exception {
        performValidityTest("ttml2-valid-speech.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextAlignAbsolute() throws Exception {
        performValidityTest("ttml2-valid-text-align-absolute.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextAlignJustify() throws Exception {
        performValidityTest("ttml2-valid-text-align-justify.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextAlignRelative() throws Exception {
        performValidityTest("ttml2-valid-text-align-relative.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextCombine() throws Exception {
        performValidityTest("ttml2-valid-text-combine.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextEmphasisColor() throws Exception {
        performValidityTest("ttml2-valid-text-emphasis-color.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextEmphasisMinimal() throws Exception {
        performValidityTest("ttml2-valid-text-emphasis-minimal.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextEmphasisQuotedString() throws Exception {
        performValidityTest("ttml2-valid-text-emphasis-quoted-string.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextOrientation() throws Exception {
        performValidityTest("ttml2-valid-text-orientation.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextOrientationSideways() throws Exception {
        performValidityTest("ttml2-valid-text-orientation-sideways.xml", -1, -1);
    }

    @Test
    public void testValidTTML2TextShadow() throws Exception {
        performValidityTest("ttml2-valid-text-shadow.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Transformation1() throws Exception {
        performValidityTest("ttml2-valid-transformation-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Transformation2() throws Exception {
        performValidityTest("ttml2-valid-transformation-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2UnicodeBidi1() throws Exception {
        performValidityTest("ttml2-valid-unicode-bidi-1.xml", -1, -1);
    }

    @Test
    public void testValidTTML2UnicodeBidi2() throws Exception {
        performValidityTest("ttml2-valid-unicode-bidi-2.xml", -1, -1);
    }

    @Test
    public void testValidTTML2UnicodeBidiIsolate() throws Exception {
        performValidityTest("ttml2-valid-unicode-bidi-isolate.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationActionAbort() throws Exception {
        performValidityTest("ttml2-valid-validation-action-abort.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationActionIgnore() throws Exception {
        performValidityTest("ttml2-valid-validation-action-ignore.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationActionWarn() throws Exception {
        performValidityTest("ttml2-valid-validation-action-warn.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationOptional() throws Exception {
        performValidityTest("ttml2-valid-validation-optional.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationProhibited() throws Exception {
        performValidityTest("ttml2-valid-validation-prohibited.xml", -1, -1);
    }

    @Test
    public void testValidTTML2ValidationRequired() throws Exception {
        performValidityTest("ttml2-valid-validation-required.xml", -1, -1);
    }

    @Test
    public void testValidTTML2Visibility() throws Exception {
        performValidityTest("ttml2-valid-visibility.xml", -1, -1);
    }

    @Test
    public void testValidTTML2VisibilityImage() throws Exception {
        performValidityTest("ttml2-valid-visibility-image.xml", -1, -1);
    }

    @Test
    public void testValidTTML2WallClockDate() throws Exception {
        performValidityTest("ttml2-valid-wall-clock-date.xml", -1, -1);
    }

    @Test
    public void testValidTTML2WallClockDateTime() throws Exception {
        performValidityTest("ttml2-valid-wall-clock-date-time.xml", -1, -1);
    }

    @Test
    public void testValidTTML2WallClockWallTime() throws Exception {
        performValidityTest("ttml2-valid-wall-clock-wall-time.xml", -1, -1);
    }

    @Test
    public void testValidTTML2XlinkHrefImageInBlock() throws Exception {
        performValidityTest("ttml2-valid-xlink-href-image-in-block.xml", -1, -1);
    }

    @Test
    public void testValidTTML2XlinkHrefImageInInline() throws Exception {
        performValidityTest("ttml2-valid-xlink-href-image-in-inline.xml", -1, -1);
    }

    @Test
    public void testValidTTML2XlinkHrefSpan() throws Exception {
        performValidityTest("ttml2-valid-xlink-href-span.xml", -1, -1);
    }

    private void performValidityTest(String resourceName, int expectedErrors, int expectedWarnings) {
        performValidityTest(resourceName, expectedErrors, expectedWarnings, null);
    }

    private void performValidityTest(String resourceName, int expectedErrors, int expectedWarnings, String[] additionalOptions) {
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
        args.add(urlString);
        TimedTextVerifier ttv = new TimedTextVerifier();
        ttv.run(args.toArray(new String[args.size()]));
        int resultCode = ttv.getResultCode(urlString);
        int resultFlags = ttv.getResultFlags(urlString);
        if (resultCode == TimedTextVerifier.RV_PASS) {
            if ((resultFlags & TimedTextVerifier.RV_FLAG_ERROR_EXPECTED_MATCH) != 0) {
                fail("Unexpected success with expected error(s) match.");
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

