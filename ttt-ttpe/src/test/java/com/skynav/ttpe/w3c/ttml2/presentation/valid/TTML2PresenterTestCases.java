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
 
package com.skynav.ttpe.w3c.ttml2.presentation.valid;

import org.junit.Ignore;
import org.junit.Test;

import com.skynav.ttpe.app.PresenterTestDriver;

public class TTML2PresenterTestCases extends PresenterTestDriver {

    @Test
    public void testTTML2AnimateAudioGain() throws Exception {
        performPresentationTest("ttml2-prstn-animate-audio-gain.xml");
    }
    
    @Test
    public void testTTML2AnimateAudioPan() throws Exception {
        performPresentationTest("ttml2-prstn-animate-audio-pan.xml");
    }
    
    @Test
    public void testTTML2AnimateFill() throws Exception {
        performPresentationTest("ttml2-prstn-animate-fill.xml", true);
    }

    @Test
    public void testTTML2AnimateLinearColor() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-color.xml", true);
    }

    @Test
    public void testTTML2AnimateLinearOpacity() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-opacity.xml", true);
    }

    @Test
    public void testTTML2AnimateLinearPosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-position.xml", true);
    }

    @Test
    public void testTTML2AnimatePacedPosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-paced-position.xml", true);
    }

    @Test
    public void testTTML2AnimateRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-animate-repeat.xml", true);
    }

    @Test
    public void testTTML2AnimateSplinePosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-spline-position.xml", true);
    }

    @Test
    public void testTTML2AnimationOutOfLine() throws Exception {
        performPresentationTest("ttml2-prstn-animation-out-of-line.xml", true);
    }

    @Test
    public void testTTML2AudioClipBeginEnd() throws Exception {
        performPresentationTest("ttml2-prstn-audio-clip-begin-end.xml");
    }

    @Test
    public void testTTML2AudioEmbeddingChunked() throws Exception {
        performPresentationTest("ttml2-prstn-audio-embedding-chunked.xml");
    }

    @Test
    public void testTTML2AudioGainMix() throws Exception {
        performPresentationTest("ttml2-prstn-audio-gain-mix.xml");
    }
    
    @Test
    public void testTTML2AudioGainOnAudio() throws Exception {
        performPresentationTest("ttml2-prstn-audio-gain-on-audio.xml");
    }
    
    @Test
    public void testTTML2AudioGainOnParagraph() throws Exception {
        performPresentationTest("ttml2-prstn-audio-gain-on-p.xml");
    }
    
    @Test
    public void testTTML2AudioPanMix() throws Exception {
        performPresentationTest("ttml2-prstn-audio-pan-mix.xml");
    }
    
    @Test
    public void testTTML2AudioPanOnAudio() throws Exception {
        performPresentationTest("ttml2-prstn-audio-pan-on-audio.xml");
    }
    
    @Test
    public void testTTML2AudioPanOnParagraph() throws Exception {
        performPresentationTest("ttml2-prstn-audio-pan-on-p.xml");
    }
    
    @Test
    public void testTTML2AudioPitchOnParagraph() throws Exception {
        performPresentationTest("ttml2-prstn-audio-pitch-on-p.xml");
    }
    
    @Test
    public void testTTML2AudioPitchOnSpan() throws Exception {
        performPresentationTest("ttml2-prstn-audio-pitch-on-span.xml");
    }
    
    @Test
    public void testTTML2AudioSpeakOnParagraph() throws Exception {
        performPresentationTest("ttml2-prstn-audio-speak-on-p.xml");
    }
    
    @Test
    public void testTTML2AudioSpeakOnSpan() throws Exception {
        performPresentationTest("ttml2-prstn-audio-speak-on-span.xml");
    }
    
    @Test
    @Ignore
    public void testTTML2AudioEmbeddingSourced() throws Exception {
        performPresentationTest("ttml2-prstn-audio-embedding-sourced.xml", true);
    }

    @Test
    public void testTTML2BackgroundClipBorder() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-border.xml", true);
    }

    @Test
    public void testTTML2BackgroundClipContent() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-content.xml", true);
    }

    @Test
    public void testTTML2BackgroundClipPadding() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-padding.xml", true);
    }

    @Test
    public void testTTML2BackgroundExtent() throws Exception {
        performPresentationTest("ttml2-prstn-background-extent.xml", true);
    }

    @Test
    public void testTTML2BackgroundImage() throws Exception {
        performPresentationTest("ttml2-prstn-background-image.xml", true);
    }

    @Test
    public void testTTML2BackgroundOriginBorder() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-border.xml", true);
    }

    @Test
    public void testTTML2BackgroundOriginContent() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-content.xml", true);
    }

    @Test
    public void testTTML2BackgroundOriginPadding() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-padding.xml", true);
    }

    @Test
    public void testTTML2BackgroundPositionBottomRight() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-bottom-right.xml", true);
    }

    @Test
    public void testTTML2BackgroundPositionTopLeft() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-top-left.xml", true);
    }

    @Test
    public void testTTML2BackgroundPositionCenter() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-center.xml", true);
    }

    @Test
    public void testTTML2BackgroundRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat.xml", true);
    }

    @Test
    public void testTTML2BackgroundRepeatNone() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-none.xml", true);
    }

    @Test
    public void testTTML2BackgroundRepeatX() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-x.xml", true);
    }

    @Test
    public void testTTML2BackgroundRepeatY() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-y.xml", true);
    }

    @Test
    @Ignore
    public void testTTML2BaseImage() throws Exception {
        performPresentationTest("ttml2-prstn-base-image.xml", true);
    }

    @Test
    public void testTTML2BorderBlock() throws Exception {
        performPresentationTest("ttml2-prstn-border-block.xml", true);
    }

    @Test
    public void testTTML2BorderBlockRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-block-radii-1.xml", true);
    }

    @Test
    public void testTTML2BorderInline() throws Exception {
        performPresentationTest("ttml2-prstn-border-inline.xml", true);
    }

    @Test
    public void testTTML2BorderInlineRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-inline-radii-1.xml", true);
    }

    @Test
    public void testTTML2BorderRegion() throws Exception {
        performPresentationTest("ttml2-prstn-border-region.xml", true);
    }

    @Test
    public void testTTML2BorderRegionRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-region-radii-1.xml", true);
    }

    @Test
    public void testTTML2BorderRegionRadii2() throws Exception {
        performPresentationTest("ttml2-prstn-border-region-radii-2.xml", true);
    }

    @Test
    public void testTTML2BPDInlineBlock() throws Exception {
        performPresentationTest("ttml2-prstn-bpd-inline-block.xml", true);
    }

    @Test
    public void testTTML2ConditionMedia() throws Exception {
        performPresentationTest("ttml2-prstn-condition-media.xml");
    }

    @Test
    public void testTTML2ConditionParameter() throws Exception {
        performPresentationTest("ttml2-prstn-condition-parameter.xml");
    }

    @Test
    public void testTTML2ConditionPrimary() throws Exception {
        performPresentationTest("ttml2-prstn-condition-primary.xml");
    }

    @Test
    public void testTTML2ConditionSupports() throws Exception {
        performPresentationTest("ttml2-prstn-condition-supports.xml");
    }

    @Test
    public void testTTML2ContentProfiles() throws Exception {
        performPresentationTest("ttml2-prstn-content-profiles.xml");
    }

    @Test
    public void testTTML2ContentProfilesCombined() throws Exception {
        performPresentationTest("ttml2-prstn-content-profiles-combined.xml");
    }

    @Test
    public void testTTML2Disparity() throws Exception {
        performPresentationTest("ttml2-prstn-disparity.xml");
    }

    @Test
    public void testTTML2DisplayAlignBlock() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-block.xml", true);
    }

    @Test
    public void testTTML2DisplayAlignJustify() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-justify.xml");
    }

    @Test
    public void testTTML2DisplayAlignRegion() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-region.xml");
    }

    @Test
    public void testTTML2DisplayAlignRelative() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-relative.xml");
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-1.xml");
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-2.xml");
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-3.xml", false, 0, 0, new String[] { "--external-extent", "160px 120px" });
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-4.xml", false, 0, 0, new String[] { "--external-extent", "160px 90px" });
    }

    @Test
    public void testTTML2DisplayInlineBlock() throws Exception {
        performPresentationTest("ttml2-prstn-display-inline-block.xml");
    }

    @Test
    public void testTTML2ExtentImage() throws Exception {
        performPresentationTest("ttml2-prstn-extent-image.xml", true);
    }

    @Test
    public void testTTML2ExtentImageContain() throws Exception {
        performPresentationTest("ttml2-prstn-extent-image-contain.xml", true);
    }

    @Test
    public void testTTML2ExtentImageCover() throws Exception {
        performPresentationTest("ttml2-prstn-extent-image-cover.xml", true);
    }

    @Test
    public void testTTML2ExtentRegionMeasureAuto() throws Exception {
        performPresentationTest("ttml2-prstn-extent-region-measure-auto.xml", true);
    }

    @Test
    public void testTTML2ExtentRegionMeasureFitContent() throws Exception {
        performPresentationTest("ttml2-prstn-extent-region-measure-fit-content.xml", true);
    }

    @Test
    public void testTTML2ExtentRegionMeasureMinContent() throws Exception {
        performPresentationTest("ttml2-prstn-extent-region-measure-min-content.xml", true);
    }

    @Test
    public void testTTML2ExtentRegionMeasureMaxContent() throws Exception {
        performPresentationTest("ttml2-prstn-extent-region-measure-max-content.xml", true);
    }

    @Test
    public void testTTML2ExtentRegionRootContainerRelative() throws Exception {
        performPresentationTest("ttml2-prstn-extent-region-root-container-relative.xml");
    }

    @Test
    public void testTTML2ExtentRootAutoWithDAR() throws Exception {
        performPresentationTest("ttml2-prstn-extent-root-auto-with-dar.xml", false, 0, 0, new String[] { "--external-extent", "640px 480px" });
    }

    @Test
    public void testTTML2FontEmbedding() throws Exception {
        performPresentationTest("ttml2-prstn-font-embedding.xml", true);
    }

    @Test
    public void testTTML2FontKerning() throws Exception {
        performPresentationTest("ttml2-prstn-font-kerning.xml");
    }

    @Test
    public void testTTML2FontSelectionStrategy() throws Exception {
        performPresentationTest("ttml2-prstn-font-selection-strategy.xml");
    }

    @Test
    public void testTTML2FontSelectionStrategyLineHeightNormal() throws Exception {
        performPresentationTest("ttml2-prstn-font-selection-strategy-line-height-normal.xml");
    }

    @Test
    public void testTTML2FontShearLinearMapping() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-linear-mapping.xml");
    }

    @Test
    public void testTTML2FontShearNoRuby() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-no-ruby.xml");
    }

    @Test
    public void testTTML2FontShearRubyMultiple() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-ruby-multiple.xml");
    }

    @Test
    public void testTTML2FontShearRubySingle() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-ruby-single.xml");
    }

    @Test
    public void testTTML2FontVariantWidth() throws Exception {
        performPresentationTest("ttml2-prstn-font-variant-width.xml");
    }

    @Test
    public void testTTML2ImageEmbeddingChunked() throws Exception {
        performPresentationTest("ttml2-prstn-image-embedding-chunked.xml", true);
    }

    @Test
    @Ignore
    public void testTTML2ImageEmbeddingSourced() throws Exception {
        performPresentationTest("ttml2-prstn-image-embedding-sourced.xml", true);
    }

    @Test
    public void testTTML2ImageIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-image-imsc11-1.xml");
    }

    @Test
    public void testTTML2ImageInBody() throws Exception {
        performPresentationTest("ttml2-prstn-image-in-body.xml");
    }

    @Test
    public void testTTML2InitialIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-initial-imsc11-1.xml");
    }

    @Test
    public void testTTML2InitialIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-initial-imsc11-2.xml");
    }

    @Test
    public void testTTML2IPDInlineBlock() throws Exception {
        performPresentationTest("ttml2-prstn-ipd-inline-block.xml", true);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-1.xml");
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-2.xml");
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-3.xml");
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-4.xml");
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test5() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-5.xml");
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test6() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-6.xml");
    }

    @Test
    public void testTTML2LetterSpacing() throws Exception {
        performPresentationTest("ttml2-prstn-letter-spacing.xml", true);
    }

    @Test
    public void testTTML2LineShearNoRuby() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-no-ruby.xml", true);
    }

    @Test
    public void testTTML2LineShearLinearMapping() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-linear-mapping.xml");
    }

    @Test
    public void testTTML2LineShearRubyMultiple() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-ruby-multiple.xml", true);
    }

    @Test
    public void testTTML2LineShearRubySingle() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-ruby-single.xml", true);
    }

    @Test
    public void testTTML2LuminanceGain() throws Exception {
        performPresentationTest("ttml2-prstn-luminance-gain.xml");
    }

    @Test
    public void testTTML2MetadataItem() throws Exception {
        performPresentationTest("ttml2-prstn-metadata-item.xml");
    }

    @Test
    public void testTTML2OpacityBlock() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-block.xml", true);
    }

    @Test
    public void testTTML2OpacityImageBlock() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-image-block.xml");
    }

    @Test
    public void testTTML2OpacityImageInline() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-image-inline.xml");
    }

    @Test
    public void testTTML2OpacityInline() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-inline.xml", true);
    }

    @Test
    public void testTTML2OpacityRegion() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-region.xml", true);
    }

    @Test
    public void testTTML2PaddingBlock() throws Exception {
        performPresentationTest("ttml2-prstn-padding-block.xml", true);
    }

    @Test
    public void testTTML2PaddingInline() throws Exception {
        performPresentationTest("ttml2-prstn-padding-inline.xml", true);
    }

    @Test
    public void testTTML2PaddingRegion() throws Exception {
        performPresentationTest("ttml2-prstn-padding-region.xml", true);
    }

    @Test
    public void testTTML2PermitFeatureNarrowing() throws Exception {
        performPresentationTest("ttml2-prstn-permit-feature-narrowing.xml");
    }

    @Test
    public void testTTML2PermitFeatureWidening() throws Exception {
        performPresentationTest("ttml2-prstn-permit-feature-widening.xml");
    }

    @Test
    public void testTTML2PositionIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-1.xml");
    }

    @Test
    public void testTTML2PositionIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-2.xml", false, 0, 0, new String[] { "--external-extent", "640px 480px" });
    }

    @Test
    public void testTTML2PositionIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-3.xml");
    }

    @Test
    public void testTTML2ProcessorProfiles() throws Exception {
        performPresentationTest("ttml2-prstn-processor-profiles.xml");
    }

    @Test
    public void testTTML2ProcessorProfilesCombined() throws Exception {
        performPresentationTest("ttml2-prstn-processor-profiles-combined.xml");
    }

    @Test
    public void testTTML2ProfileNesting() throws Exception {
        performPresentationTest("ttml2-prstn-profile-nesting.xml");
    }

    @Test
    public void testTTML2RegionImpliedAnimationPosition() throws Exception {
        performPresentationTest("ttml2-prstn-region-implied-animation-position.xml", true);
    }

    @Test
    public void testTTML2RegionInline() throws Exception {
        performPresentationTest("ttml2-prstn-region-inline.xml", true);
    }

    // @Test
    // public void testTTML2RegionNoDefault() throws Exception {
    //     performPresentationTest("ttml2-prstn-region-no-default.xml");
    // }

    @Test
    public void testTTML2RubyIgnoredLWSPContainer() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-ignored-lwsp-container.xml");
    }

    @Test
    public void testTTML2RubyIgnoredLWSPContainerBase() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-ignored-lwsp-container-base.xml");
    }

    @Test
    public void testTTML2RubyIgnoredLWSPContainerText() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-ignored-lwsp-container-text.xml");
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-1.xml");
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-2.xml");
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-3.xml");
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-4.xml");
    }

    @Test
    public void testTTML2RubyAlignWithBase() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-with-base.xml");
    }

    @Test
    public void testTTML2RubyIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-1.xml");
    }

    @Test
    public void testTTML2RubyIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-2.xml");
    }

    @Test
    public void testTTML2RubyIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-3.xml");
    }

    @Test
    public void testTTML2RubyIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-4.xml");
    }

    @Test
    public void testTTML2RubyIMSC11Test5() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-5.xml");
    }

    @Test
    public void testTTML2RubyPositionAfter() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-after.xml");
    }

    @Test
    public void testTTML2RubyPositionBefore() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-before.xml");
    }

    @Test
    public void testTTML2RubyPositionOutside() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-outside.xml");
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-1.xml");
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-2.xml");
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-3.xml");
    }

    @Test
    public void testTTML2SetFill() throws Exception {
        performPresentationTest("ttml2-prstn-set-fill.xml", true);
    }

    @Test
    public void testTTML2SetMultipleStyles() throws Exception {
        performPresentationTest("ttml2-prstn-set-multiple-styles.xml", true);
    }

    @Test
    public void testTTML2SetRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-set-repeat.xml", true);
    }

    @Test
    public void testTTML2ShearIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-1.xml", true);
    }

    @Test
    public void testTTML2ShearIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-2.xml", true);
    }

    @Test
    public void testTTML2ShearIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-3.xml", true);
    }

    @Test
    public void testTTML2ShearLinearMapping() throws Exception {
        performPresentationTest("ttml2-prstn-shear-linear-mapping.xml");
    }

    @Test
    public void testTTML2TextAlignAbsolute() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-absolute.xml");
    }

    @Test
    public void testTTML2TextAlignJustify() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-justify.xml");
    }

    @Test
    public void testTTML2TextAlignRelative() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-relative.xml");
    }

    @Test
    public void testTTML2TextCombineIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-combine-imsc11-1.xml");
    }

    @Test
    public void testTTML2TextEmphasisColor() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-color.xml");
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-1.xml");
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-2.xml");
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-3.xml");
    }

    @Test
    public void testTTML2TextEmphasisQuotedString() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-quoted-string.xml");
    }

    @Test
    public void testTTML2TextOrientation() throws Exception {
        performPresentationTest("ttml2-prstn-text-orientation.xml");
    }

    @Test
    public void testTTML2TextShadowIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-shadow-imsc11-1.xml");
    }

    @Test
    public void testTTML2TimeWallClock() throws Exception {
        performPresentationTest("ttml2-prstn-time-wall-clock.xml", true);
    }

    @Test
    public void testTTML2UnicodeBidiIsolate() throws Exception {
        performPresentationTest("ttml2-prstn-unicode-bidi-isolate.xml", true);
    }

    @Test
    public void testTTML2ValidationProhibitedValid() throws Exception {
        performPresentationTest("ttml2-prstn-validation-prohibited-valid.xml");
    }

    @Test
    public void testTTML2VisibilityImage() throws Exception {
        performPresentationTest("ttml2-prstn-visibility-image.xml");
    }

    @Test
    public void testTTML2XLinkImage() throws Exception {
        performPresentationTest("ttml2-prstn-xlink-image.xml", true);
    }

    @Test
    public void testTTML2XLinkSpan() throws Exception {
        performPresentationTest("ttml2-prstn-xlink-span.xml", true);
    }

}
