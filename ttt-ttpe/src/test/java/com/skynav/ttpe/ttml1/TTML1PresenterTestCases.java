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
 
package com.skynav.ttpe.ttml1;

import org.junit.Test;

import com.skynav.ttpe.app.PresenterTestDriver;

public class TTML1PresenterTestCases extends PresenterTestDriver {

    @Test
    public void testTTML1BackgroundColorBlock() throws Exception {
        performPresentationTest("ttml1-background-color-block.xml", 0, 0);
    }

    @Test
    public void testTTML1BackgroundColorRegion() throws Exception {
        performPresentationTest("ttml1-background-color-region.xml", 0, 0);
    }

    @Test
    public void testTTML1Display1() throws Exception {
        performPresentationTest("ttml1-display-1.xml", 0, 0);
    }

    @Test
    public void testTTML1Display2() throws Exception {
        performPresentationTest("ttml1-display-2.xml", 0, 0);
    }

    @Test
    public void testTTML1ForegroundColor() throws Exception {
        performPresentationTest("ttml1-foreground-color.xml", 0, 0);
    }

    @Test
    public void testTTML1Visibility1() throws Exception {
        performPresentationTest("ttml1-visibility-1.xml", 0, 0);
    }

    @Test
    public void testTTML1Visibility2() throws Exception {
        performPresentationTest("ttml1-visibility-2.xml", 0, 0);
    }

}
