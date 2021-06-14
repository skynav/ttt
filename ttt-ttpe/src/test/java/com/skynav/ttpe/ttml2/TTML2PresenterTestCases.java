/*
 * Copyright 2013-16 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttpe.ttml2;

//import org.junit.Ignore;
import org.junit.Test;

import com.skynav.ttpe.app.PresenterTestDriver;

public class TTML2PresenterTestCases extends PresenterTestDriver {

    @Test
    public void testTTML2Mixed() throws Exception {
        performPresentationTest("ttml2-mixed.xml");
    }

    @Test
    public void testTTML2SpanTextAlignEN() throws Exception {
        performPresentationTest("ttml2-span-text-align-en.xml");
    }

    @Test
    public void testTTML2SpanTextAlignJA() throws Exception {
        performPresentationTest("ttml2-span-text-align-ja.xml");
    }

    @Test
    public void testTTML2Ruby() throws Exception {
        performPresentationTest("ttml2-ruby.xml");
    }

    @Test
    public void testTTML2SpanFontVariantWidth() throws Exception {
        performPresentationTest("ttml2-span-font-variant-width.xml");
    }

    @Test
    public void testTTML2RegionDisplayAlignLRTB() throws Exception {
        performPresentationTest("ttml2-region-display-align-lrtb.xml");
    }

    @Test
    public void testTTML2RegionDisplayAlignTBLR() throws Exception {
        performPresentationTest("ttml2-region-display-align-tblr.xml");
    }

    @Test
    public void testTTML2RegionDisplayAlignTBRL() throws Exception {
        performPresentationTest("ttml2-region-display-align-tbrl.xml");
    }

    @Test
    public void testTTML2SpanFontKerningLRTB() throws Exception {
        performPresentationTest("ttml2-span-font-kerning-lrtb.xml");
    }

    @Test
    public void testTTML2SpanFontShearLRTB() throws Exception {
        performPresentationTest("ttml2-span-font-shear-lrtb.xml");
    }

    @Test
    public void testTTML2SpanFontSizeAnomorphicLRTB() throws Exception {
        performPresentationTest("ttml2-span-font-size-anomorphic-lrtb.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisArialLRTB() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-arial-lrtb.xml");
    }

    @Test
    public void testTTML2SpanTextAlignJustifyEN() throws Exception {
        performPresentationTest("ttml2-span-text-align-justify-en.xml");
    }

    @Test
    public void testTTML2StylingIssue179() throws Exception {
        performPresentationTest("ttml2-styling-issue-179.xml");
    }

    @Test
    public void testTTML2RegionDisplayAlignJustifyLRTB() throws Exception {
        performPresentationTest("ttml2-region-display-align-justify-lrtb.xml");
    }

    @Test
    public void testTTML2RegionPositionInitialValue() throws Exception {
        performPresentationTest("ttml2-region-position-initial-value.xml");
    }

    @Test
    public void testTTML2SpanTextCombine() throws Exception {
        performPresentationTest("ttml2-span-text-combine.xml");
    }

    @Test
    public void testTTML2Break() throws Exception {
        performPresentationTest("ttml2-break.xml");
    }

    @Test
    public void testTTML2BreakFontShear() throws Exception {
        performPresentationTest("ttml2-break-font-shear.xml");
    }

    @Test
    public void testTTML2PlacementAndAlignmentDefault() throws Exception {
        performPresentationTest("ttml2-placement-and-alignment-default.xml");
    }

    @Test
    public void testTTML2RubyDisplayAlignLRTB() throws Exception {
        performPresentationTest("ttml2-ruby-display-align-lrtb.xml");
    }

    @Test
    public void testTTML2RubyDisplayAlignTBRL() throws Exception {
        performPresentationTest("ttml2-ruby-display-align-tbrl.xml");
    }

    @Test
    public void testTTML2RubyDisplayAlignTBLR() throws Exception {
        performPresentationTest("ttml2-ruby-display-align-tblr.xml");
    }

    @Test
    public void testTTML2SpanFontShearTBRL() throws Exception {
        performPresentationTest("ttml2-span-font-shear-tbrl.xml");
    }

    @Test
    public void testTTML2RubyMultiLineLRTB() throws Exception {
        performPresentationTest("ttml2-ruby-multi-line-lrtb.xml");
    }

    @Test
    public void testTTML2RubyMultiLineTBRL() throws Exception {
        performPresentationTest("ttml2-ruby-multi-line-tbrl.xml");
    }

    @Test
    public void testTTML2RubyMultiLineTBLR() throws Exception {
        performPresentationTest("ttml2-ruby-multi-line-tblr.xml");
    }

    @Test
    public void testTTML2InlineBlockRubyMultiLineLRTB() throws Exception {
        performPresentationTest("ttml2-inline-block-ruby-multi-line-lrtb.xml");
    }

    @Test
    public void testTTML2InlineBlockRubyMultiLineTBRL() throws Exception {
        performPresentationTest("ttml2-inline-block-ruby-multi-line-tbrl.xml");
    }

    @Test
    public void testTTML2InlineBlockRubyMultiLineTBLR() throws Exception {
        performPresentationTest("ttml2-inline-block-ruby-multi-line-tblr.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisMixed() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-mixed.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisNotoLRTB() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-noto-lrtb.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisNotoBeforeMixedOrientation() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-noto-before-mixed-orientation.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisNotoAfterMixedOrientation() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-noto-after-mixed-orientation.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisSegmented() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-segmented.xml");
    }

    @Test
    public void testTTML2BreakBetweenRubyContainers() throws Exception {
        performPresentationTest("ttml2-break-between-ruby-containers.xml");
    }

    @Test
    public void testTTML2SpanTextEmphasisAutoPosition() throws Exception {
        performPresentationTest("ttml2-span-text-emphasis-auto-position.xml");
    }

    @Test
    public void testTTML2ParagraphMultiplePerRegionTBRL() throws Exception {
        performPresentationTest("ttml2-p-multiple-per-region-tbrl.xml");
    }

    @Test
    public void testTTML2ParagraphTextAlignHebrewRLTB() throws Exception {
        performPresentationTest("ttml2-p-text-align-iw-rltb.xml");
    }

    @Test
    public void testTTML2SpanBidiLROInRLTB() throws Exception {
        performPresentationTest("ttml2-span-bidi-rlo-rltb.xml");
    }

    @Test
    public void testTTML2MaxRegionsExceeded() throws Exception {
        performPresentationTest("ttml2-max-regions-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2MaxLinesExceeded() throws Exception {
        performPresentationTest("ttml2-max-lines-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2MaxLinesPerRegionExceeded() throws Exception {
        performPresentationTest("ttml2-max-lines-per-region-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2MaxCharsExceeded() throws Exception {
        performPresentationTest("ttml2-max-chars-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2MaxCharsPerRegionExceeded() throws Exception {
        performPresentationTest("ttml2-max-chars-per-region-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2MaxCharsPerLineExceeded() throws Exception {
        performPresentationTest("ttml2-max-chars-per-line-exceeded.xml", 0, 1);
    }

    @Test
    public void testTTML2OptionDefaultColorRed() throws Exception {
        performPresentationTest("ttml2-option-default-color-red.xml");
    }

    @Test
    public void testTTML2VerticalVariants() throws Exception {
        performPresentationTest("ttml2-vertical-variants.xml");
    }

    @Test
    public void testTTML2SpanTextOutline() throws Exception {
        performPresentationTest("ttml2-span-text-outline.xml");
    }

    @Test
    public void testTTML2RubyAlignAutoLRTB() throws Exception {
        performPresentationTest("ttml2-ruby-align-auto-lrtb.xml");
    }

    @Test
    public void testTTML2RubyAlignAutoTBRL() throws Exception {
        performPresentationTest("ttml2-ruby-align-auto-tbrl.xml");
    }

    @Test
    public void testTTML2TextCombineMixed() throws Exception {
        performPresentationTest("ttml2-text-combine-mixed.xml");
    }

    @Test
    public void testTTML2TextOutlineOnRubyMixed() throws Exception {
        performPresentationTest("ttml2-text-outline-on-ruby-mixed.xml");
    }

    @Test
    public void testTTML2TextOutlineOnEmphasisMixed() throws Exception {
        performPresentationTest("ttml2-text-outline-on-emphasis-mixed.xml");
    }

    @Test
    public void testTTML2FontShearAdvanceTBRL() throws Exception {
        performPresentationTest("ttml2-font-shear-advance-tbrl.xml");
    }

    @Test
    public void testTTML2RubyOnMixedOrientations() throws Exception {
        performPresentationTest("ttml2-ruby-on-mixed-orientations.xml");
    }

    @Test
    public void testTTML2ParagraphTextAlignArabicRLTB() throws Exception {
        performPresentationTest("ttml2-p-text-align-ar-rltb.xml");
    }

    @Test
    public void testTTML2XMLSpace() throws Exception {
        performPresentationTest("ttml2-xml-space.xml");
    }

    @Test
    public void testTTML2OptionDefaultWhitespacePreserve() throws Exception {
        performPresentationTest("ttml2-option-default-whitespace-preserve.xml");
    }

    @Test
    public void testTTML230A1RotationIssue118() throws Exception {
        performPresentationTest("ttml2-30a1-rotation-issue-118.xml");
    }

    @Test
    public void testTTML2ExpandedFontWidthInVerticalIssue119() throws Exception {
        performPresentationTest("ttml2-expanded-font-width-in-vertical-issue-119.xml");
    }

    @Test
    public void testTTML2VerticalMappedRotations() throws Exception {
        performPresentationTest("ttml2-vertical-mapped-rotations.xml");
    }

    @Test
    public void testTTML2TextCombineWithShearIssue125() throws Exception {
        performPresentationTest("ttml2-text-combine-with-shear-issue-125.xml");
    }

    @Test
    public void testTTML2AnnotationBreakingIssue124Ruby33() throws Exception {
        performPresentationTest("ttml2-annotation-breaking-issue-124-ruby33.xml");
    }

    @Test
    public void testTTML2AnnotationBreakingIssue124Ruby34() throws Exception {
        performPresentationTest("ttml2-annotation-breaking-issue-124-ruby34.xml");
    }

    @Test
    public void testTTML2AmbiguousGlyphToCharMappingIssue128() throws Exception {
        performPresentationTest("ttml2-ambiguous-glyph-to-char-mapping-issue-128.xml");
    }

    @Test
    public void testTTML2AnnotationOverflowsLineAreaIssue120Ruby29() throws Exception {
        performPresentationTest("ttml2-annotation-overflows-line-area-issue-120-ruby29.xml");
    }

    @Test
    public void testTTML2RubyReserveIssue97Ruby13() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-issue-97-ruby13.xml");
    }

    @Test
    public void testTTML2RubyReserveIssue97Ruby16() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-issue-97-ruby16.xml");
    }

    @Test
    public void testTTML2BidiReorderWithIsolates() throws Exception {
        performPresentationTest("ttml2-bidi-reorder-with-isolates.xml");
    }

    @Test
    public void testTTML2KoranicArabic() throws Exception {
        performPresentationTest("ttml2-koranic-arabic.xml");
    }

    @Test
    public void testTTML2BidiMirror() throws Exception {
        performPresentationTest("ttml2-bidi-mirror.xml");
    }

    @Test
    public void testTTML2RubyReserveIssue147() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-issue-147.xml");
    }

    @Test
    public void testTTML2RubyReserveOutside3Lines() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-outside-3-lines.xml");
    }

    @Test
    public void testTTML2RubyReserveAround3Lines() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-around-3-lines.xml");
    }

    @Test
    public void testTTML2RubyReserveBetween3Lines() throws Exception {
        performPresentationTest("ttml2-ruby-reserve-between-3-lines.xml");
    }

    @Test
    public void testTTML2SpanTextAlignKO() throws Exception {
        performPresentationTest("ttml2-span-text-align-ko.xml");
    }

    @Test
    public void testTTML2SpanTextAlignZHS() throws Exception {
        performPresentationTest("ttml2-span-text-align-zhs.xml");
    }

    @Test
    public void testTTML2SpanTextAlignZHT() throws Exception {
        performPresentationTest("ttml2-span-text-align-zht.xml");
    }

    @Test
    public void testTTML2SpanTextAlignRU() throws Exception {
        performPresentationTest("ttml2-span-text-align-ru.xml");
    }

    @Test
    public void testTTML2OffsetTimeMetricTicks() throws Exception {
        performPresentationTest("ttml2-offset-time-metric-ticks.xml");
    }

    @Test
    public void testTTML2SMPTE30FPSDropNTSC() throws Exception {
        performPresentationTest("ttml2-smpte-30fps-drop-ntsc.xml");
    }

    @Test
    public void testTTML2SMPTE30FPSDropPAL() throws Exception {
        performPresentationTest("ttml2-smpte-30fps-drop-pal.xml");
    }

    @Test
    public void testTTML2SMPTE30FPSNonDrop() throws Exception {
        performPresentationTest("ttml2-smpte-30fps-non-drop.xml");
    }

    @Test
    public void testTTML2CellResolution1Part() throws Exception {
        performPresentationTest("ttml2-cell-resolution-1-part.xml");
    }

    @Test
    public void testTTML2CellResolution2Part() throws Exception {
        performPresentationTest("ttml2-cell-resolution-2-part.xml");
    }

    @Test
    public void testTTML2InitialFontSize1() throws Exception {
        performPresentationTest("ttml2-initial-font-size-1.xml");
    }

    @Test
    public void testTTML2InitialFontSize2() throws Exception {
        performPresentationTest("ttml2-initial-font-size-2.xml");
    }

    @Test
    public void testTTML2InitialFontSize3() throws Exception {
        performPresentationTest("ttml2-initial-font-size-3.xml");
    }

    @Test
    public void testTTML2SpanXMLSpace() throws Exception {
        performPresentationTest("ttml2-span-xml-space.xml");
    }

    @Test
    public void testTTML2DivisionBackgroundImage1() throws Exception {
        performPresentationTest("ttml2-div-background-image-1.xml");
    }

    @Test
    public void testTTML2DivisionImage1() throws Exception {
        performPresentationTest("ttml2-div-image-1.xml");
    }

    @Test
    public void testTTML2PaddingInlineLRTB() throws Exception {
        performPresentationTest("ttml2-padding-inline-lrtb.xml");
    }

    @Test
    public void testTTML2LineShearLR() throws Exception {
        performPresentationTest("ttml2-line-shear-lr.xml");
    }

    @Test
    public void testTTML2LineShearRL() throws Exception {
        performPresentationTest("ttml2-line-shear-rl.xml");
    }

    @Test
    public void testTTML2ShearLRWithBPD() throws Exception {
        performPresentationTest("ttml2-shear-lr-with-bpd.xml");
    }

    @Test
    public void testTTML2ShearRLWithBPD() throws Exception {
        performPresentationTest("ttml2-shear-rl-with-bpd.xml");
    }

}
