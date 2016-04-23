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
 * base segment parser for any SOF segment (SOFn)
 */
abstract class SofSegmentParser extends SegmentParser {

    abstract void loadValidSamplePrecisions(List<Integer> sp);
    abstract short getMinImageComponentNumber();
    abstract short getMaxImageComponentNumber();
    abstract byte getMinQuantizationTableDestinationSelector();
    abstract byte getMaxQuantizationTableDestinationSelector();

    boolean validate(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException
    {
        String symbol;
        try
            {
                performSegmentSpecificChecks(js, mh);
                symbol = String.format("SOF%1$d", js.getCurrentCode() & 0xf);
                assertOnlySingleFrameSegmentAllowed(jis, js, mh);
                int lf = jis.readShort() & 0xffff;
                int p = jis.readByte() & 0xff;
                List<Integer> list = new LinkedList<Integer>();
                loadValidSamplePrecisions(list);
                validateSamplePrecision(symbol, p, list, mh);
                int y = jis.readShort() & 0xffff;
                validateNumberOfLines(symbol, y, js, mh);
                js.setResultState("height", Integer.valueOf(y));
                int x = jis.readShort() & 0xffff;
                validateNumberOfSamplesPerLine(symbol, x, mh);
                js.setResultState("width", Integer.valueOf(x));
                int nf = jis.readByte() & 0xff;
                short minNf = getMinImageComponentNumber();
                short maxNf = getMaxImageComponentNumber();
                validateImageComponentNumber(symbol, nf, minNf, maxNf, mh);
                validateFrameHeaderLength(symbol, lf, nf, mh);
                list.clear();
                int value;
                int minTqi = getMinQuantizationTableDestinationSelector();
                int maxTqi = getMaxQuantizationTableDestinationSelector();
                for (int i = 1; i <= nf; i++)
                    {
                        value = jis.readByte() & 0xff;
                        validateComponentIdentifier(symbol, i, value, list, mh);
                        value = jis.readByte() & 0xff;
                        validateSamplingFactors(symbol, i, (byte) value, mh);
                        value = jis.readByte() & 0xff;
                        validateQuantizationTableDestinationSelector(symbol, i, value, minTqi, maxTqi, mh);
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

    protected void performSegmentSpecificChecks(JpegState js, AbstractLoggingValidator mh)
    {
        // May be overriden for segment specific checks
    }

    private void assertOnlySingleFrameSegmentAllowed(JpegInputStream jv, JpegState js, AbstractLoggingValidator mh)
    {
        Integer c = js.getCurrentCode();
        Integer ifc = js.getInitialFrameCode();
        if (ifc != null)
            mh.logResult(JpegValidator.MsgCode.JPG01E009, ifc, String.format("SOF%1$d", ifc & 0xf), c, String.format("SOF%1$d", c & 0xf), jv.getTotalBytesRead());
        else
            js.setInitialFrameCode(c);
    }

    private void validateFrameHeaderLength(String symbol, int actualLf, int actualNf, AbstractLoggingValidator mh)
    {
        short expectedLf = (short) (8 + (3 * actualNf));
        if (actualLf != expectedLf)
            mh.logResult(JpegValidator.MsgCode.JPG01E033, symbol, actualLf, actualNf);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01I006, symbol, actualLf, actualNf);
    }

    private void validateSamplePrecision(String symbol, int actualPrecision, List<Integer> expectedPrecision, AbstractLoggingValidator mh)
    {
        if (expectedPrecision.contains(actualPrecision))
            mh.logResult(JpegValidator.MsgCode.JPG01I007, symbol, actualPrecision);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E034, symbol, actualPrecision, expectedPrecision);
    }

    private void validateNumberOfLines(String symbol, int y, JpegState js, AbstractLoggingValidator mh)
    {
        // Nothing to validate, all values allowed: 0-65535
        if (y == 0)
            js.requireDnlSegment();
        mh.logResult(JpegValidator.MsgCode.JPG01I008, symbol, y);
    }

    private void validateImageComponentNumber(String symbol, int nf, short expectedLow, short expectedHigh, AbstractLoggingValidator mh)
    {
        if (nf >= expectedLow && nf <= expectedHigh)
            {
                List<Integer> jfifRestrictions = new LinkedList<Integer>();
                jfifRestrictions.add(1);
                jfifRestrictions.add(3);
                if (!jfifRestrictions.contains(nf))
                    mh.logResult(JpegValidator.MsgCode.JPG01W008, symbol, nf, jfifRestrictions);
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01I009, symbol, nf);
            }
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E035, symbol, nf, expectedLow, expectedHigh);
    }

    private void validateNumberOfSamplesPerLine(String symbol, int numSamplesPerLine, AbstractLoggingValidator mh)
    {
        int expectedLow = 1;
        int expectedHigh = 65535;
        if (numSamplesPerLine >= expectedLow && numSamplesPerLine <= expectedHigh)
            mh.logResult(JpegValidator.MsgCode.JPG01I010, symbol, numSamplesPerLine);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E036, symbol, numSamplesPerLine, expectedLow, expectedHigh);
    }

    private void validateComponentIdentifier(String symbol, int index, int id, List<Integer> alreadyFound, AbstractLoggingValidator mh)
    {
        if (alreadyFound.contains(id))
            mh.logResult(JpegValidator.MsgCode.JPG01E015, symbol, index, id, alreadyFound);
        else
            {
                mh.logResult(JpegValidator.MsgCode.JPG01I011, symbol, index, id);
                alreadyFound.add(id);
            }
    }

    private void validateSamplingFactors(String symbol, int index, byte factors, AbstractLoggingValidator mh)
    {
        int expectedLow = 1;
        int expectedHigh = 4;
        int hi = (factors >>> 4) & 0xf;
        int vi = factors & 0xf;
        if (hi >= expectedLow && hi <= expectedHigh)
            mh.logResult(JpegValidator.MsgCode.JPG01I012, symbol, index, hi);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E037, symbol, index, hi, expectedLow, expectedHigh);
        if (vi >= expectedLow && vi <= expectedHigh)
            mh.logResult(JpegValidator.MsgCode.JPG01I013, symbol, index, vi);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E038, symbol, index, vi, expectedLow, expectedHigh);
    }

    private void validateQuantizationTableDestinationSelector(String symbol, int index, int tq, int expectedLow, int expectedHigh, AbstractLoggingValidator mh)
    {
        if (tq >= expectedLow && tq <= expectedHigh)
            mh.logResult(JpegValidator.MsgCode.JPG01I014, symbol, index, tq);
        else
            mh.logResult(JpegValidator.MsgCode.JPG01E039, symbol, index, tq, expectedLow, expectedHigh);
    }
}
