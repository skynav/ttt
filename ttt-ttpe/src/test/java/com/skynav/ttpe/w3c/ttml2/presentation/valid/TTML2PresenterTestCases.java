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

//import org.junit.Ignore;
import org.junit.Test;

import com.skynav.ttpe.app.PresenterTestDriver;

public class TTML2PresenterTestCases extends PresenterTestDriver {

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
    public void testTTML2TextCombineIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-combine-imsc11-1.xml", 0, 0);
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
    public void testTTML2TextShadowIMSC11Test1() throws Exception {
        performPresentationTest("ttml2-prstn-text-shadow-imsc11-1.xml", 0, 0);
    }

}
