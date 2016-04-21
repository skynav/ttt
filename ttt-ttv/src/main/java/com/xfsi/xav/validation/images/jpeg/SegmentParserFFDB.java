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
 * validates DQT segment
 */
class SegmentParserFFDB extends SegmentParser {
    boolean validate(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                js.incrementTablesMiscSegmentCount();
                int segmentBytesLeft = jis.readShort() & 0xffff;
                mh.logResult(JpegValidator.MsgCode.JPG01I024, js.getCurrentCode(), segmentBytesLeft);
                segmentBytesLeft -= 2;
                int tableCount = 0;
                List<Integer> validPq = new LinkedList<Integer>();
                validPq.add(0);
                validPq.add(1);
                List<Integer> validTq = new LinkedList<Integer>();
                validTq.add(0);
                validTq.add(1);
                validTq.add(2);
                validTq.add(3);
                while (segmentBytesLeft > 0)
                    {
                        byte b = jis.readByte();
                        segmentBytesLeft--;
                        int pQ = (b & 0xf0) >>> 4;
                        int tQ = (b & 0xf);
                        // TODO: further constraint pQ and tQ values by SOFn type (baseline, lossless, etc)
                        if (!validPq.contains(pQ))
                            mh.logResult(JpegValidator.MsgCode.JPG01E042, tableCount, pQ, validPq);
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I025, tableCount, pQ);
                        if (!validTq.contains(tQ))
                            mh.logResult(JpegValidator.MsgCode.JPG01E043, tableCount, tQ, validTq);
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I026, tableCount, tQ);
                        if (pQ == 0)
                            {
                                for (int k = 0; k < 64; k++)
                                    {
                                        if (jis.readByte() == 0)
                                            mh.logResult(JpegValidator.MsgCode.JPG01E044, tableCount, k);
                                        segmentBytesLeft--;
                                    }
                            }
                        else
                            {
                                for (int k = 0; k < 64; k ++)
                                    {
                                        if (jis.readShort() == 0)
                                            mh.logResult(JpegValidator.MsgCode.JPG01E044, tableCount, k);
                                        segmentBytesLeft -= 2;
                                    }
                            }
                        tableCount++;
                    }
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
