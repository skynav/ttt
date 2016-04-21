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
import java.io.InputStream;

import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Result;
import com.xfsi.xav.validation.util.AbstractLoggingValidator;

/**
 * Parses and validates JPEG files.
 */
public final class JpegValidator extends AbstractLoggingValidator {
    static enum MsgCode
    {
        JPG01I001,
        JPG01I002,
        JPG01I003,
        JPG01I004,
        JPG01I005,
        JPG01I006,
        JPG01I007,
        JPG01I008,
        JPG01I009,
        JPG01I010,
        JPG01I011,
        JPG01I012,
        JPG01I013,
        JPG01I014,
        JPG01I015,
        JPG01I016,
        JPG01I017,
        JPG01I018,
        JPG01I019,
        JPG01I020,
        JPG01I021,
        JPG01I022,
        JPG01I023,
        JPG01I024,
        JPG01I025,
        JPG01I026,
        JPG01I027,
        JPG01I028,
        JPG01I029,
        JPG01I030,
        JPG01I031,
        JPG01I032,
        JPG01I033,
        JPG01I034,
        JPG01I035,
        JPG01I036,
        JPG01I037,
        JPG01I038,
        JPG01I039,
        JPG01X001,
        JPG01X002,
        JPG01X003,
        JPG01X004,
        JPG01X005,
        JPG01X006,
        JPG01W001,
        JPG01W002,
        JPG01W003,
        JPG01W004,
        JPG01W005,
        JPG01W006,
        JPG01W007,
        JPG01W008,
        JPG01W009,
        JPG01W010,
        JPG01W011,
        JPG01W012,
        JPG01W013,
        JPG01W014,
        JPG01W015,
        JPG01W016,
        JPG01E001,
        JPG01E002,
        JPG01F001,
        JPG01F002,
        JPG01F003,
        JPG01E004,
        JPG01E005,
        JPG01E006,
        JPG01E007,
        JPG01E009,
        JPG01E010,
        JPG01E011,
        JPG01E012,
        JPG01E013,
        JPG01E014,
        JPG01E015,
        JPG01E016,
        JPG01E017,
        JPG01E018,
        JPG01E019,
        JPG01E020,
        JPG01E021,
        JPG01E022,
        JPG01E024,
        JPG01E025,
        JPG01E026,
        JPG01E027,
        JPG01E028,
        JPG01E029,
        JPG01E030,
        JPG01E031,
        JPG01E032,
        JPG01E033,
        JPG01E034,
        JPG01E035,
        JPG01E036,
        JPG01E037,
        JPG01E038,
        JPG01E039,
        JPG01E040,
        JPG01E041,
        JPG01E042,
        JPG01E043,
        JPG01E044,
        JPG01E045,
        JPG01E046,
        JPG01E047,
        JPG01E048,
    }
    private JpegInputStream inputStream = null;
    private JpegState state = null;

    public JpegValidator() throws Exception
    {
        super(Error.TestType.STATIC, Error.ContentType.IMAGE_JPG);
    }

    public Result run(TestManager tm, TestInfo ti) throws Exception
    {
        initState(tm,ti);
        validate();
        return getErrorReported() ? Result.FAIL : Result.PASS;
    }

    public String getVersion()
    {
        // TODO: implement versioning
        return "1.0.0";
    }

    public void validate()
    {
        this.state = new JpegState();
        try
            {
                InputStream is = getTestInfo().getResourceStream();
                assert(is != null) : msgFormatterNV(MsgCode.JPG01X002.toString());
                logAll(MsgCode.JPG01I001);
                this.inputStream = new JpegInputStream(is);
                if (dispatchSegmentParser())
                    assertEOF();
            }
        catch (EOFException e)
            {
                // Nothing to do, terminating early due to EOF
            }
        catch (AssertionError e)
            {
                logProgress(MsgCode.JPG01X001, e.getMessage(), this.inputStream.getTotalBytesRead());
            }
        logAll(MsgCode.JPG01I002);
    }

    private boolean dispatchSegmentParser() throws EOFException
    {
        while (!this.state.isEoiFound())
            {
                if (!findParserClassNameForSegment())
                    return false;
                if (!parseSegment())
                    return false;
                if (!assertSoiSegmentIsFirst())
                    return false;
            }
        return performFinalChecks();
    }

    private boolean performFinalChecks()
    {
        checkAbbreviatedFormat();
        assertAtLeastOneFrameSegmentFound();
        assertOneOrMoreSosSegmentFound();
        assertDnlSegmentFoundIfRequired();
        assertApp0SegmentFound();
        return assertEoiSegmentIsLast();
    }

    private boolean parseSegment() throws EOFException
    {
        Class c = null;
        String className = this.state.getCurrentSegmentParserName();
        try
            {
                c = Class.forName(className);
                SegmentParser mp = (SegmentParser) c.newInstance();
                return mp.validate(this.inputStream, this.state, this);
            }
        catch (ClassNotFoundException e)
            {
                Integer code = this.state.getCurrentCode();
                if (!isReservedApplicationSegment(code))
                    {
                        String symbol;
                        if ((symbol = findReservedJpegExtensionSymbol(code)) != null)
                            logResult(MsgCode.JPG01E006, code, symbol, this.inputStream.getTotalBytesRead());
                        else if (code == 0xff01)
                            logResult(MsgCode.JPG01E007, this.inputStream.getTotalBytesRead());
                        else if (code >= 0xff02 && code <= 0xffbf)
                            logResult(MsgCode.JPG01E002, code, this.inputStream.getTotalBytesRead());
                        else
                            {
                                logResult(MsgCode.JPG01E014, code, this.inputStream.getTotalBytesRead());
                                return false;
                            }
                        skipToNextSegment();
                    }
                return true;
            }
        catch (IllegalAccessException e)
            {
                logProgress(MsgCode.JPG01X005, Thread.currentThread().getStackTrace()[2].getMethodName(), c.getName(), e.getMessage(), this.inputStream.getTotalBytesRead());
            }
        catch (InstantiationException e)
            {
                logProgress(MsgCode.JPG01X006, Thread.currentThread().getStackTrace()[2].getMethodName(), c.getName(), e.getMessage(), this.inputStream.getTotalBytesRead());
            }
        return false;
    }

    private boolean findParserClassNameForSegment() throws EOFException
    {
        try
            {
                byte b;
                // get rid of 0xff optionally preceding the code
                do
                    {
                        b = this.inputStream.readByte();
                    } while ((b & 0xff) == 0xff);
                short code = (short) (0xff00 | b);
                String markerClassBasePathName = SegmentParser.class.getName();
                String segmentParserName = String.format("%1$s%2$X", markerClassBasePathName, code);
                this.state.setCurrentCode(code);
                this.state.setCurrentSegmentParserName(segmentParserName);
                logAll(MsgCode.JPG01I004, this.state.getCurrentCode(), this.state.getSegmentCount());
                return true;
            }
        catch (EOFException e)
            {
                logResult(MsgCode.JPG01F001, this.inputStream.getTotalBytesRead());
                throw e;
            }
        catch (IOException e)
            {
                assert(false) : msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
            }
        return false;
    }

    private boolean assertSoiSegmentIsFirst()
    {
        if (!this.state.isSoiFound())
            {
                logResult(MsgCode.JPG01E004, this.state.getCurrentCode(), this.inputStream.getTotalBytesRead());
                return false;
            }
        return true;
    }

    private boolean assertEoiSegmentIsLast()
    {
        if (this.state.getCurrentCode() != 0xffd9)
            {
                logResult(MsgCode.JPG01E005, this.state.getCurrentCode(), this.inputStream.getTotalBytesRead());
                return false;
            }
        return true;
    }

    private void checkAbbreviatedFormat()
    {
        if (this.state.getInitialFrameCode() == null && this.state.getTableMiscSegmentCount() > 0)
            logResult(MsgCode.JPG01W005);
    }

    private void assertAtLeastOneFrameSegmentFound()
    {
        if (this.state.getInitialFrameCode() == null)
            logResult(MsgCode.JPG01E010, this.inputStream.getTotalBytesRead());
    }

    private void assertOneOrMoreSosSegmentFound()
    {
        if (this.state.getSosSegmentCount() == 0)
            logResult(MsgCode.JPG01E012, this.inputStream.getTotalBytesRead());
    }

    private void assertDnlSegmentFoundIfRequired()
    {
        if (this.state.isDnlSegmentRequired() && this.state.getDnlSegmentCount() == 0)
            logResult(MsgCode.JPG01E016, this.state.getInitialFrameCode());
    }

    private void assertApp0SegmentFound()
    {
        if (this.state.getApp0SegmentCount() == 0)
            logResult(MsgCode.JPG01E018, this.state.getSegmentCount());
    }

    private void skipToNextSegment() throws EOFException
    {
        try
            {
                byte b;
                boolean isMarkerStart = false;
                while (true)
                    {
                        b = this.inputStream.readByte();
                        if ((b & 0xff) == 0xff && !isMarkerStart)
                            isMarkerStart = true;
                        else if (isMarkerStart && ((b & 0xff) != 0 || (b & 0xff) != 0xff))
                            {
                                // next segment marker code found
                                this.inputStream.putBack((byte) 0xff);
                                this.inputStream.putBack(b);
                                break;
                            }
                    }
            }
        catch (EOFException e)
            {
                logResult(MsgCode.JPG01F001, this.inputStream.getTotalBytesRead());
                throw e;
            }
        catch (IOException e)
            {
                assert(false) : msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
            }
    }

    private boolean isReservedApplicationSegment(Integer code) throws EOFException
    {
        if (code >= 0xffe0 && code <= 0xffef)
            {
                this.state.incrementTablesMiscSegmentCount();
                String symbol = null;
                try
                    {
                        short size = this.inputStream.readShort();
                        symbol = String.format("APP%1$d", code & 0xf);
                        logAll(MsgCode.JPG01I005, code, symbol, size);
                        size -= 2; // already read segment size
                        this.inputStream.skipBytes(size);
                        return true;
                    }
                catch (EOFException e)
                    {
                        logResult(MsgCode.JPG01F002, code, symbol, this.inputStream.getTotalBytesRead());
                        throw e;
                    }
                catch (IOException e)
                    {
                        assert(false) : msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
                    }
            }
        return false;
    }

    private String findReservedJpegExtensionSymbol(Integer code)
    {
        if (code == 0xffc8)
            return "JPG";
        if (code >= 0xfff0 && code <= 0xfffd)
            return "JPG" + (code & 0xf);
        return null;
    }

    private void assertEOF()
    {
        try
            {
                this.inputStream.readByte();
            }
        catch (EOFException e)
            {
                logAll(MsgCode.JPG01I003);
                return;
            }
        catch (IOException e)
            {
                assert(false) : msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
            }
        logResult(MsgCode.JPG01E001);
    }
}
