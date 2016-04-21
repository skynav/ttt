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
 * validates SOS segment
 */
class SegmentParserFFDA extends SegmentParser {
    boolean validate(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                js.incrementSosSegmentCount();
                if (js.getInitialFrameCode() == null)
                    mh.logResult(JpegValidator.MsgCode.JPG01E024, js.getSegmentCount());
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01I031, js.getSegmentCount(), js.getInitialFrameCode());
                short segmentBytesLeft = jis.readShort();
                int nS = jis.readByte() & 0xff;
                int expectedNs = 6 + (2*nS);
                if (segmentBytesLeft != expectedNs)
                    mh.logResult(JpegValidator.MsgCode.JPG01E025, segmentBytesLeft, expectedNs, js.getSegmentCount());
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01I032, segmentBytesLeft, js.getSegmentCount());
                List<Integer> valid = getExpectedNsValues(js);
                if (valid != null)
                    {
                        if(!valid.contains(nS))
                            mh.logResult(JpegValidator.MsgCode.JPG01E026, nS, valid, js.getSegmentCount());
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I033, nS, js.getSegmentCount());
                    }
                List<Integer> validTd = getExpectedTdValues(js);
                List<Integer> validTa = getExpectedTaValues(js);
                int value;
                for (int j = 0; j < nS; j++)
                    {
                        jis.readByte(); // Cs(j) can range from 0-255, nothing to check //TODO: check that it's a member of Ci specified in SOFn
                        value  = jis.readByte() & 0xff;
                        int tD = (value & 0xf) >>> 4;
                        int tA = (value & 0xf);
                        if (validTd != null)
                            {
                                if (!validTd.contains(tD))
                                    mh.logResult(JpegValidator.MsgCode.JPG01E027, j, tD, validTd, js.getFrameType().toString(), js.getSegmentCount());
                                else
                                    mh.logResult(JpegValidator.MsgCode.JPG01I034, j, tD, js.getFrameType().toString(), js.getSegmentCount());
                            }
                        if (validTa != null)
                            {
                                if (!validTa.contains(tA))
                                    mh.logResult(JpegValidator.MsgCode.JPG01E028, j, tA, validTa, js.getFrameType().toString(), js.getSegmentCount());
                                else
                                    mh.logResult(JpegValidator.MsgCode.JPG01I035, j, tA, js.getFrameType().toString(), js.getSegmentCount());
                            }
                    }
                int sS = jis.readByte() & 0xff;
                valid = getExpectedSsValues(js);
                if (valid != null)
                    {
                        if (!valid.contains(sS))
                            mh.logResult(JpegValidator.MsgCode.JPG01E029, sS, valid, js.getFrameType().toString(), js.getSegmentCount());
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I036, sS, js.getFrameType().toString(), js.getSegmentCount());
                    }
                int sE = jis.readByte() & 0xff;
                valid = getExpectedSeValues(js, sS);
                if (valid != null)
                    {
                        if (!valid.contains(sE))
                            mh.logResult(JpegValidator.MsgCode.JPG01E030, sE, valid, js.getFrameType().toString(), js.getSegmentCount());
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I037, sE, js.getFrameType().toString(), js.getSegmentCount());
                    }
                value  = jis.readByte() & 0xff;
                int aH = (value & 0xf) >>> 4;
                int aL = (value & 0xf);
                valid = getExpectedAhValues(js);
                if (valid != null)
                    {
                        if (!valid.contains(aH))
                            mh.logResult(JpegValidator.MsgCode.JPG01E031, aH, valid, js.getFrameType().toString(), js.getSegmentCount());
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I038, aH, js.getFrameType().toString(), js.getSegmentCount());
                    }
                valid = getExpectedAlValues(js);
                if (valid != null)
                    {
                        if (!valid.contains(aL))
                            mh.logResult(JpegValidator.MsgCode.JPG01E032, aL, valid, js.getFrameType().toString(), js.getSegmentCount());
                        else
                            mh.logResult(JpegValidator.MsgCode.JPG01I039, aL, js.getFrameType().toString(), js.getSegmentCount());
                    }
                parseScanData(jis, js, mh);
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

    private void parseScanData(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException, IOException
    {
        int count = 0;
        while (true)
            {
                int b = jis.readByte() & 0xff;
                if (b == 0xff)
                    {
                        do
                            {
                                b = jis.readByte() & 0xff;
                            } while (b == 0xff);
                        if (b != 0)
                            {
                                if (b >= 0xd0 && b <= 0xd7)
                                    {
                                        js.setCurrentCode((short) (0xff << 8 | b));
                                        js.incrementTablesMiscSegmentCount();
                                        mh.logResult(JpegValidator.MsgCode.JPG01I030, js.getCurrentCode());
                                    }
                                else
                                    {
                                        if (count == 0)
                                            mh.logResult(JpegValidator.MsgCode.JPG01E013, js.getSegmentCount(), jis.getTotalBytesRead());
                                        if (js.getSosSegmentCount() == 1 && b == 0xdc)
                                            js.allowDnlSegment();
                                        jis.putBack((byte) 0xff);
                                        jis.putBack((byte) b);
                                        break; // next marker tag found
                                    }
                            }
                        count++;
                    }
                count++;
            }
    }

    private List<Integer> getExpectedNsValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                for (int i = 1; i <= 4; i++)
                    values.add(i);
            }
        return values;
    }

    private List<Integer> getExpectedTdValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                        for (int i = 0; i <= 1; i++)
                            values.add(i);
                        break;
                    case EXTENDED:
                    case PROGRESSIVE:
                    case LOSSLESS:
                        for (int i = 0; i <= 3; i++)
                            values.add(i);
                        break;
                    }
            }
        return values;
    }

    private List<Integer> getExpectedTaValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                        for (int i = 0; i <= 1; i++)
                            values.add(i);
                        break;
                    case EXTENDED:
                    case PROGRESSIVE:
                        for (int i = 0; i <= 3; i++)
                            values.add(i);
                        break;
                    case LOSSLESS:
                        values.add(0);
                        break;
                    }
            }
        return values;
    }

    private List<Integer> getExpectedSsValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                    case EXTENDED:
                        values.add(0);
                        break;
                    case PROGRESSIVE:
                        for (int i = 0; i <= 63; i++)
                            values.add(i);
                        break;
                    case LOSSLESS:
                        for (int i = 1; i < 8; i++)
                            values.add(i);
                        break;
                    }
            }
        return values;
    }

    private List<Integer> getExpectedSeValues(JpegState js, int sS)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                    case EXTENDED:
                        values.add(63);
                        break;
                    case PROGRESSIVE:
                        if (sS > 63)
                            break; // error already detected above
                        for (int i = sS; i <= 63; i++)
                            values.add(i);
                        break;
                    case LOSSLESS:
                        values.add(0);
                        break;
                    }
            }
        return values;
    }

    private List<Integer> getExpectedAhValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                    case EXTENDED:
                    case LOSSLESS:
                        values.add(0);
                        break;
                    case PROGRESSIVE:
                        for (int i = 0; i <= 13; i++)
                            values.add(i);
                        break;
                    }
            }
        return values;
    }

    private List<Integer> getExpectedAlValues(JpegState js)
    {
        List<Integer> values = null;
        if (js.getFrameType() != null)
            {
                values = new LinkedList<Integer>();
                switch (js.getFrameType())
                    {
                    case BASELINE:
                    case EXTENDED:
                        values.add(0);
                        break;
                    case PROGRESSIVE:
                        for (int i = 0; i <= 13; i++)
                            values.add(i);
                        break;
                    case LOSSLESS:
                        for (int i = 0; i <= 15; i++)
                            values.add(i);
                        break;
                    }
            }
        return values;
    }
}
