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

import java.io.EOFException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import com.xfsi.xav.validation.images.jpeg.JpegValidator.MsgCode;
import com.xfsi.xav.validation.util.AbstractLoggingValidator;

/**
 * validates DHT segment
 */
class SegmentParserFFC4 extends SegmentParser {
    boolean validate(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                js.incrementTablesMiscSegmentCount();
                if (js.getInitialFrameCode() != null && !js.usesHuffmanCoding())
                    mh.logResult(JpegValidator.MsgCode.JPG01W001, js.getSegmentCount(), js.getInitialFrameCode());
                int segmentBytesLeft = jis.readShort() & 0xffff;
                segmentBytesLeft -= 2;
                byte b = jis.readByte();
                mh.logResult(JpegValidator.MsgCode.JPG01I019, js.getCurrentCode(), segmentBytesLeft);
                segmentBytesLeft--;
                int tC = (b & 0xf0) >>> 4;
                int tH = (b & 0xf);
                // TODO: further constraint Tc and Th values by SOFn type (baseline, lossless, etc)
                List<Integer> valid = new LinkedList<Integer>();
                valid.add(0);
                valid.add(1);
                if (!valid.contains(tC))
                    mh.logResult(JpegValidator.MsgCode.JPG01E040, tC, valid);
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01I020, tC);
                valid.clear();
                valid.add(0);
                valid.add(1);
                valid.add(2);
                valid.add(3);
                if (!valid.contains(tH))
                    mh.logResult(JpegValidator.MsgCode.JPG01E041, tH, valid);
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01I021, tH);
                jis.skipBytes(segmentBytesLeft);
            }
        catch (EOFException e)
            {
                mh.logResult(JpegValidator.MsgCode.JPG01F003, js.getCurrentCode(), js.getSegmentCount(), jis.getTotalBytesRead());
                throw e;
            }
        catch (IOException e)
            {
                assert(false) : mh.msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
            }
        return true;
    }
}
