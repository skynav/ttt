/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttxv.imsc1.w3c;

import java.net.URL;
import java.util.List;

import org.junit.Test;
import static org.junit.Assert.fail;

import com.skynav.ttv.app.TimedTextVerifier;

public class ValidTestCases {

    @Test
    public void testValidIMSC1W3CAltText1() throws Exception {
        performValidityTest("altText/altText1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio1() throws Exception {
        performValidityTest("aspectRatio/aspectRatio1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio2() throws Exception {
        performValidityTest("aspectRatio/aspectRatio2.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio3() throws Exception {
        performValidityTest("aspectRatio/aspectRatio3.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio4() throws Exception {
        performValidityTest("aspectRatio/aspectRatio4.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio5() throws Exception {
        performValidityTest("aspectRatio/aspectRatio5.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CAspectRatio6() throws Exception {
        performValidityTest("aspectRatio/aspectRatio6.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment0() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag0-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment1() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag1-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment2() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag2-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment3() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag3-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment4() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag4-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track5Fragment5() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track5-frag5-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE22Track6Fragment0() throws Exception {
        performValidityTest("dece/Solekai022_854_29_640x75_MaxSdSubtitle_v7_subtitles/track6-frag0-sample1-subs0.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CDECE23Track5Fragment0() throws Exception {
        performValidityTest("dece/Solekai023_1920_23_1x1_MaxHdSubtitle_v7_subtitles/track5-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE23Track5Fragment1() throws Exception {
        performValidityTest("dece/Solekai023_1920_23_1x1_MaxHdSubtitle_v7_subtitles/track5-frag1-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE23Track5Fragment2() throws Exception {
        performValidityTest("dece/Solekai023_1920_23_1x1_MaxHdSubtitle_v7_subtitles/track5-frag2-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE23Track6Fragment0() throws Exception {
        performValidityTest("dece/Solekai023_1920_23_1x1_MaxHdSubtitle_v7_subtitles/track6-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE23Track3Fragment0SD() throws Exception {
        performValidityTest("dece/Solekai044_640_23_1x1_Sync_Subs_Txt_SD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE23Track3Fragment0HD() throws Exception {
        performValidityTest("dece/Solekai045_1920_23_1x1_Sync_Subs_Txt_HD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE46Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai046_640_23_1x1_Sync_Subs_Img_SD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE46Track4Fragment0() throws Exception {
        performValidityTest("dece/Solekai046_640_23_1x1_Sync_Subs_Img_SD_v7_subtitles/track4-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE47Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai047_1920_23_1x1_Sync_Subs_Img_HD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE47Track4Fragment0() throws Exception {
        performValidityTest("dece/Solekai047_1920_23_1x1_Sync_Subs_Img_HD_v7_subtitles/track4-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE49Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai049_854_23_426x75_Sync_Subs_Txt_SD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE50Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai050_854_23_426x75_Sync_Subs_Img_SD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE50Track4Fragment0() throws Exception {
        performValidityTest("dece/Solekai050_854_23_426x75_Sync_Subs_Img_SD_v7_subtitles/track4-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE51Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai051_1920_23_5x75_Sync_Subs_Txt_HD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE52Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai052_1920_23_5x75_Sync_Subs_Img_HD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE52Track4Fragment0() throws Exception {
        performValidityTest("dece/Solekai052_1920_23_5x75_Sync_Subs_Img_HD_v7_subtitles/track4-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track3Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track3-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track4Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track4-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track5Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track5-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track6Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track6-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track7Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track7-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CDECE55Track8Fragment0() throws Exception {
        performValidityTest("dece/Solekai055_640_23_1x1_TimeReps_SD_v7_subtitles/track8-frag0-sample1-subs0.ttml", -1, -1);
    }
    
    @Test
    public void testValidIMSC1W3CForcedDisplay1() throws Exception {
        performValidityTest("forcedDisplay/forcedDisplay1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CFrameSync1() throws Exception {
        performValidityTest("frameSync/frameSync1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CLinePadding1() throws Exception {
        performValidityTest("linePadding/linePadding1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CMultiRowAlign1() throws Exception {
        performValidityTest("multiRowAlign/multiRowAlign1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CProgressivelyDecodable1() throws Exception {
        performValidityTest("progressivelyDecodable/progressivelyDecodable1.ttml", -1, -1);
    }

    @Test
    public void testValidIMSC1W3CReferenceFonts1() throws Exception {
        performValidityTest("referenceFonts/referenceFonts1.ttml", -1, -1);
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
        args.add("--model");
        args.add("imsc1");
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
        } else
            fail("Unexpected result code " + resultCode + ".");
    }

}

