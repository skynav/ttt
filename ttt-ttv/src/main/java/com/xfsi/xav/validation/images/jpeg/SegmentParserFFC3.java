/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 * Portions Copyright 2009 Extensible Formatting Systems, Inc (XFSI).
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

package com.xfsi.xav.validation.images.jpeg;

import java.util.List;

import com.xfsi.xav.validation.util.AbstractLoggingValidator;

/**
 * validates SOF3 (non-differential Huffman coding, Spatial (sequential) lossless) segment
 */
class SegmentParserFFC3 extends SofSegmentParser {
    protected void performSegmentSpecificChecks(JpegState js, AbstractLoggingValidator mh)
    {
        mh.logResult(JpegValidator.MsgCode.JPG01W004, js.getCurrentCode(), js.getSegmentCount());
    }

    protected void loadValidSamplePrecisions(List<Integer> sp)
    {
        sp.add(2);
        sp.add(3);
        sp.add(4);
        sp.add(5);
        sp.add(6);
        sp.add(7);
        sp.add(8);
        sp.add(9);
        sp.add(10);
        sp.add(11);
        sp.add(12);
        sp.add(13);
        sp.add(14);
        sp.add(15);
        sp.add(16);
    }
    protected short getMinImageComponentNumber()
    {
        return 1;
    }

    protected short getMaxImageComponentNumber()
    {
        return 255;
    }

    protected byte getMinQuantizationTableDestinationSelector()
    {
        return 0;
    }

    protected byte getMaxQuantizationTableDestinationSelector()
    {
        return 0;
    }
}
