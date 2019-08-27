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
 
package com.skynav.ttpe.w3c.ttml1.presentation.valid;

import org.junit.Ignore;
import org.junit.Test;

import com.skynav.ttpe.app.PresenterTestDriver;

public class TTML1PresenterTestCases extends PresenterTestDriver {

    @Test
    public void testTTML1Animation() throws Exception {
        performPresentationTest("ttml1-prstn-animation.xml");
    }
    
    @Test
    public void testTTML1AnonymousSpanDurationImplicit() throws Exception {
        performPresentationTest("ttml1-prstn-anonymous-span-duration-implicit.xml");
    }
    
    @Test
    public void testTTML1BackgroundColorBlock() throws Exception {
        performPresentationTest("ttml1-prstn-background-color-block.xml");
    }

    @Test
    public void testTTML1BackgroundColorInlineLRTB() throws Exception {
        performPresentationTest("ttml1-prstn-background-color-inline-lrtb.xml");
    }

    @Test
    public void testTTML1BackgroundColorInlineSpacesLRTB() throws Exception {
        performPresentationTest("ttml1-prstn-background-color-inline-spaces-lrtb.xml");
    }

    @Test
    public void testTTML1BackgroundColorRegion() throws Exception {
        performPresentationTest("ttml1-prstn-background-color-region.xml");
    }

    @Test
    public void testTTML1Break() throws Exception {
        performPresentationTest("ttml1-prstn-break.xml");
    }
    
    @Test
    public void testTTML1BreakDurationImplicit() throws Exception {
        performPresentationTest("ttml1-prstn-break-duration-implicit.xml");
    }
    
    @Test
    public void testTTML1Color() throws Exception {
        performPresentationTest("ttml1-prstn-color.xml");
    }

    @Test
    @Ignore
    public void testTTML1ColorRGBWithLWSP() throws Exception {
        performPresentationTest("ttml1-prstn-color-rgb-with-lwsp.xml");
    }
    
    @Test
    public void testTTML1Direction() throws Exception {
        performPresentationTest("ttml1-prstn-direction.xml");
    }
    
    @Test
    public void testTTML1Display1() throws Exception {
        performPresentationTest("ttml1-prstn-display-1.xml");
    }

    @Test
    public void testTTML1Display2() throws Exception {
        performPresentationTest("ttml1-prstn-display-2.xml");
    }

    @Test
    public void testTTML1FontFamily() throws Exception {
        performPresentationTest("ttml1-prstn-font-family.xml");
    }
    
    @Test
    public void testTTML1FontSizeAnamorphicCell() throws Exception {
        performPresentationTest("ttml1-prstn-font-size-anamorphic-cell.xml");
    }
    
    @Test
    public void testTTML1FontSizeAnamorphicEm() throws Exception {
        performPresentationTest("ttml1-prstn-font-size-anamorphic-em.xml");
    }
    
    @Test
    public void testTTML1FontSizeInheritedEm() throws Exception {
        performPresentationTest("ttml1-prstn-font-size-inherited-em.xml");
    }
    
    @Test
    public void testTTML1FontSizeRegionEm() throws Exception {
        performPresentationTest("ttml1-prstn-font-size-region-em.xml");
    }
    
    @Test
    public void testTTML1InitialNonInherited() throws Exception {
        performPresentationTest("ttml1-prstn-initial-non-inherited.xml");
    }
    
    @Test
    public void testTTML1LineHeight() throws Exception {
        performPresentationTest("ttml1-prstn-line-height.xml");
    }
    
    @Test
    public void testTTML1PaddingRegion() throws Exception {
        performPresentationTest("ttml1-prstn-padding-region.xml");
    }

    @Test
    public void testTTML1SetDurationImplicit() throws Exception {
        performPresentationTest("ttml1-prstn-set-duration-implicit.xml");
    }
    
    @Test
    public void testTTML1ShowBackground() throws Exception {
        performPresentationTest("ttml1-prstn-show-background.xml");
    }

    @Test
    public void testTTML1SpanImplicitDuration() throws Exception {
        performPresentationTest("ttml1-prstn-span-duration-implicit.xml");
    }
    
    @Test
    public void testTTML1TextDecoration() throws Exception {
        performPresentationTest("ttml1-prstn-text-decoration.xml");
    }
    
    @Test
    public void testTTML1TextOutline() throws Exception {
        performPresentationTest("ttml1-prstn-text-outline.xml");
    }
    
    @Test
    @Ignore
    public void testTTML1UnknownAttribute() throws Exception {
        performPresentationTest("ttml1-prstn-unknown-attribute.xml");
    }
    
    @Test
    public void testTTML1Visibility() throws Exception {
        performPresentationTest("ttml1-prstn-visibility.xml");
    }
    
    @Test
    public void testTTML1Visibility1() throws Exception {
        performPresentationTest("ttml1-prstn-visibility-1.xml");
    }

    @Test
    public void testTTML1Visibility2() throws Exception {
        performPresentationTest("ttml1-prstn-visibility-2.xml");
    }

    @Test
    public void testTTML1ZIndex() throws Exception {
        performPresentationTest("ttml1-prstn-z-index.xml");
    }
    
}
