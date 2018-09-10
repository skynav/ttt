/*
 * Copyright 2013-18 Skynav, Inc. All rights reserved.
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
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateFill() throws Exception {
        performPresentationTest("ttml2-prstn-animate-fill.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateLinearColor() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-color.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateLinearOpacity() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-opacity.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateLinearPosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-linear-position.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimatePacedPosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-paced-position.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-animate-repeat.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2AnimateSplinePosition() throws Exception {
        performPresentationTest("ttml2-prstn-animate-spline-position.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/216
    public void testTTML2AnimationOutOfLine() throws Exception {
        performPresentationTest("ttml2-prstn-animation-out-of-line.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/204
    public void testTTML2BackgroundClipBorder() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-border.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/204
    public void testTTML2BackgroundClipContent() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-content.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/204
    public void testTTML2BackgroundClipPadding() throws Exception {
        performPresentationTest("ttml2-prstn-background-clip-padding.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/202
    public void testTTML2BackgroundExtent() throws Exception {
        performPresentationTest("ttml2-prstn-background-extent.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/201
    public void testTTML2BackgroundImage() throws Exception {
        performPresentationTest("ttml2-prstn-background-image.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/205
    public void testTTML2BackgroundOriginBorder() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-border.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/205
    public void testTTML2BackgroundOriginContent() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-content.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/205
    public void testTTML2BackgroundOriginPadding() throws Exception {
        performPresentationTest("ttml2-prstn-background-origin-padding.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/206
    public void testTTML2BackgroundPositionBottomRight() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-bottom-right.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/206
    public void testTTML2BackgroundPositionTopLeft() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-top-left.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/206
    public void testTTML2BackgroundPositionCenter() throws Exception {
        performPresentationTest("ttml2-prstn-background-position-center.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/203
    public void testTTML2BackgroundRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/203
    public void testTTML2BackgroundRepeatNone() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-none.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/203
    public void testTTML2BackgroundRepeatX() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-x.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/203
    public void testTTML2BackgroundRepeatY() throws Exception {
        performPresentationTest("ttml2-prstn-background-repeat-y.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderBlock() throws Exception {
        performPresentationTest("ttml2-prstn-border-block.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderBlockRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-block-radii-1.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderInline() throws Exception {
        performPresentationTest("ttml2-prstn-border-inline.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderInlineRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-inline-radii-1.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderRegion() throws Exception {
        performPresentationTest("ttml2-prstn-border-region.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderRegionRadii1() throws Exception {
        performPresentationTest("ttml2-prstn-border-region-radii-1.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/207
    public void testTTML2BorderRegionRadii2() throws Exception {
        performPresentationTest("ttml2-prstn-border-region-radii-2.xml", 0, 0);
    }

    @Test
    public void testTTML2ConditionMedia() throws Exception {
        performPresentationTest("ttml2-prstn-condition-media.xml", 0, 0);
    }

    @Test
    public void testTTML2ConditionParameter() throws Exception {
        performPresentationTest("ttml2-prstn-condition-parameter.xml", 0, 0);
    }

    @Test
    public void testTTML2ConditionPrimary() throws Exception {
        performPresentationTest("ttml2-prstn-condition-primary.xml", 0, 0);
    }

    @Test
    public void testTTML2ConditionSupports() throws Exception {
        performPresentationTest("ttml2-prstn-condition-supports.xml", 0, 0);
    }

    @Test
    public void testTTML2ContentProfiles() throws Exception {
        performPresentationTest("ttml2-prstn-content-profiles.xml", 0, 0);
    }

    @Test
    public void testTTML2ContentProfilesCombined() throws Exception {
        performPresentationTest("ttml2-prstn-content-profiles-combined.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/214
    public void testTTML2DisplayAlignBlock() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-block.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAlignJustify() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-justify.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAlignRegion() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-region.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAlignRelative() throws Exception {
        performPresentationTest("ttml2-prstn-display-align-relative.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2DisplayAspectRatioIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-display-aspect-ratio-imsc11-4.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/221
    public void testTTML2ExtentImage() throws Exception {
        performPresentationTest("ttml2-prstn-extent-image.xml", 0, 0);
    }

    @Test
    public void testTTML2FontKerning() throws Exception {
        performPresentationTest("ttml2-prstn-font-kerning.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/211
    public void testTTML2FontSelectionStrategy() throws Exception {
        performPresentationTest("ttml2-prstn-font-selection-strategy.xml", 0, 0);
    }

    @Test
    public void testTTML2FontShearNoRuby() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-no-ruby.xml", 0, 0);
    }

    @Test
    public void testTTML2FontShearRubyMultiple() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-ruby-multiple.xml", 0, 0);
    }

    @Test
    public void testTTML2FontShearRubySingle() throws Exception {
        performPresentationTest("ttml2-prstn-font-shear-ruby-single.xml", 0, 0);
    }

    @Test
    public void testTTML2FontVariantWidth() throws Exception {
        performPresentationTest("ttml2-prstn-font-variant-width.xml", 0, 0);
    }

    @Test
    public void testTTML2ImageIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-image-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2InitialIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-initial-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2InitialIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-initial-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-4.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test5() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-5.xml", 0, 0);
    }

    @Test
    public void testTTML2LengthRootContainerRelativeIMSC11Test6() throws Exception {
        performPresentationTest("ttml2-prstn-length-root-container-relative-imsc11-6.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/212
    public void testTTML2LetterSpacing() throws Exception {
        performPresentationTest("ttml2-prstn-letter-spacing.xml", 0, 0);
    }

    @Test
    public void testTTML2LineShearNoRuby() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-no-ruby.xml", 0, 0);
    }

    @Test
    public void testTTML2LineShearRubyMultiple() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-ruby-multiple.xml", 0, 0);
    }

    @Test
    public void testTTML2LineShearRubySingle() throws Exception {
        performPresentationTest("ttml2-prstn-line-shear-ruby-single.xml", 0, 0);
    }

    @Test
    public void testTTML2MetadataItem() throws Exception {
        performPresentationTest("ttml2-prstn-metadata-item.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/208
    public void testTTML2OpacityBlock() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-block.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/208
    public void testTTML2OpacityInline() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-inline.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/208
    public void testTTML2OpacityRegion() throws Exception {
        performPresentationTest("ttml2-prstn-opacity-region.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/210
    public void testTTML2PaddingBlock() throws Exception {
        performPresentationTest("ttml2-prstn-padding-block.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/210
    public void testTTML2PaddingInline() throws Exception {
        performPresentationTest("ttml2-prstn-padding-inline.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/210
    public void testTTML2PaddingRegion() throws Exception {
        performPresentationTest("ttml2-prstn-padding-region.xml", 0, 0);
    }

    @Test
    public void testTTML2PermitFeatureNarrowing() throws Exception {
        performPresentationTest("ttml2-prstn-permit-feature-narrowing.xml", 0, 0);
    }

    @Test
    public void testTTML2PermitFeatureWidening() throws Exception {
        performPresentationTest("ttml2-prstn-permit-feature-widening.xml", 0, 0);
    }

    @Test
    public void testTTML2PositionIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2PositionIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2PositionIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-position-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2ProcessorProfiles() throws Exception {
        performPresentationTest("ttml2-prstn-processor-profiles.xml", 0, 0);
    }

    @Test
    public void testTTML2ProcessorProfilesCombined() throws Exception {
        performPresentationTest("ttml2-prstn-processor-profiles-combined.xml", 0, 0);
    }

    @Test
    public void testTTML2ProfileNesting() throws Exception {
        performPresentationTest("ttml2-prstn-profile-nesting.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-4.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyIMSC11Test5() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-imsc11-5.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyAlignIMSC11Test4() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-imsc11-4.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyAlignWithBase() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-align-with-base.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyPositionAfter() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-after.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyPositionBefore() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-before.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyPositionOutside() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-position-outside.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2RubyReserveIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-ruby-reserve-imsc11-3.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2SetFill() throws Exception {
        performPresentationTest("ttml2-prstn-set-fill.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2SetMultipleStyles() throws Exception {
        performPresentationTest("ttml2-prstn-set-multiple-styles.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/215
    public void testTTML2SetRepeat() throws Exception {
        performPresentationTest("ttml2-prstn-set-repeat.xml", 0, 0);
    }

    @Test
    public void testTTML2ShearIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2ShearIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2ShearIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-shear-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2TextAlignAbsolute() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-absolute.xml", 0, 0);
    }

    @Test
    public void testTTML2TextAlignJustify() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-justify.xml", 0, 0);
    }

    @Test
    public void testTTML2TextAlignRelative() throws Exception {
        performPresentationTest("ttml2-prstn-text-align-relative.xml", 0, 0);
    }

    @Test
    public void testTTML2TextCombineIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-combine-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2TextEmphasisColor() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-color.xml", 0, 0);
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test2() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-2.xml", 0, 0);
    }

    @Test
    public void testTTML2TextEmphasisIMSC11Test3() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-imsc11-3.xml", 0, 0);
    }

    @Test
    public void testTTML2TextEmphasisQuotedString() throws Exception {
        performPresentationTest("ttml2-prstn-text-emphasis-quoted-string.xml", 0, 0);
    }

    @Test
    public void testTTML2TextOrientation() throws Exception {
        performPresentationTest("ttml2-prstn-text-orientation.xml", 0, 0);
    }

    @Test
    public void testTTML2TextShadowIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-shadow-imsc11-1.xml", 0, 0);
    }

    @Test
    public void testTTML2ValidationProhibitedValid() throws Exception {
        performPresentationTest("ttml2-prstn-validation-prohibited-valid.xml", 0, 0);
    }

    @Test
    public void testTTML2VisibilityImage() throws Exception {
        performPresentationTest("ttml2-prstn-visibility-image.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/220
    public void testTTML2XLinkImage() throws Exception {
        performPresentationTest("ttml2-prstn-xlink-image.xml", 0, 0);
    }

    @Test
    @Ignore // pending https://github.com/skynav/ttt/issues/220
    public void testTTML2XLinkSpan() throws Exception {
        performPresentationTest("ttml2-prstn-xlink-span.xml", 0, 0);
    }

}
