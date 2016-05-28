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

import com.xfsi.xav.validation.images.jpeg.JpegValidator.MsgCode;
import com.xfsi.xav.validation.util.AbstractLoggingValidator;

/**
 * validates APP0 segment
 */
class SegmentParserFFE0 extends SegmentParser {
    private final static byte jfifIdentifierLength = 5;
    boolean validate(JpegInputStream jis, JpegState js, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                int l = jis.readShort() & 0xffff;
                mh.logAll(MsgCode.JPG01I005, 0xffe0, "APP0", l);
                l -= 2; // already read segment length
                js.incrementApp0SegmentCount();
                if (js.getApp0SegmentCount() == 1)
                    {
                        if (js.getSegmentCount() != 2 || js.getLastCode() != 0xffd8)
                            mh.logResult(JpegValidator.MsgCode.JPG01E017, js.getSegmentCount());
                        else
                            mh.logResult(MsgCode.JPG01I015);
                    }
                if (l > SegmentParserFFE0.jfifIdentifierLength)
                    {
                        if (isJfifSegment(jis, js, l, mh))
                            return true;
                        l -= SegmentParserFFE0.jfifIdentifierLength;
                    }
                if (js.getApp0SegmentCount() == 1)
                    mh.logResult(JpegValidator.MsgCode.JPG01E019, js.getSegmentCount());
                jis.skipBytes(l);
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

    private boolean isJfifSegment(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh)
    {
        try
            {
                byte[] identifier = new byte[SegmentParserFFE0.jfifIdentifierLength];
                for (int i = 0; i < SegmentParserFFE0.jfifIdentifierLength; i++)
                    identifier[i] = jis.readByte();
                segmentBytesLeft -= SegmentParserFFE0.jfifIdentifierLength;
                if (isFirstJfifSegment(identifier))
                    return checkFirstJfifSegment(jis, js, segmentBytesLeft, mh);
                if (isJfifExtensionSegment(identifier))
                    return checkJfifExtensionSegment(jis, js, segmentBytesLeft, mh);
            }
        catch (IOException e)
            {
                assert(false) : mh.msgFormatterNV(MsgCode.JPG01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
            }
        return false;
    }

    private boolean isFirstJfifSegment(byte[] identifier)
    {
        for (int i = 0; i < jfifIdentifierLength; i++)
            {
                byte b = identifier[i];
                if (i == 0) {
                    if (b != 0x4a)
                        return false;
                } else if (i == 1) {
                    if (b != 0x46)
                        return false;
                } else if (i == 2) {
                    if (b != 0x49)
                        return false;
                } else if (i == 3) {
                    if (b != 0x46)
                        return false;
                } else if (i == 4) {
                    if (b != 0x00)
                        return false;
                } else
                    break;
            }
        return true;
    }

    private boolean isJfifExtensionSegment(byte[] identifier)
    {
        for (int i = 0; i < jfifIdentifierLength; i++)
            {
                byte b = identifier[i];
                if (i == 0) {
                    if (b != 0x4a)
                        return false;
                } else if (i == 1) {
                    if (b != 0x46)
                        return false;
                } else if (i == 2) {
                    if (b != 0x58)
                        return false;
                } else if (i == 3) {
                    if (b != 0x58)
                        return false;
                } else if (i == 4) {
                    if (b != 0x00)
                        return false;
                } else
                    break;
            }
        return true;
    }

    private boolean checkFirstJfifSegment(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                js.markLastJfifApp0SegmentIndex();
                if (js.getSegmentCount() != 2)
                    mh.logResult(JpegValidator.MsgCode.JPG01E020, js.getSegmentCount());
                if (js.getApp0SegmentCount() == 1)
                    mh.logResult(MsgCode.JPG01I016);
                else
                    mh.logResult(JpegValidator.MsgCode.JPG01E021, js.getSegmentCount(), js.getApp0SegmentCount());
                int expectedMajor = 1;
                int expectedMinor = 2;
                js.setJfifMajorVersion(jis.readByte() & 0xff);
                segmentBytesLeft--;
                js.setJfifMinorVersion(jis.readByte() & 0xff);
                segmentBytesLeft--;
                if (js.getJfifMajorVersion() != expectedMajor || js.getJfifMinorVersion() != expectedMinor)
                    mh.logResult(JpegValidator.MsgCode.JPG01W006, js.getJfifMajorVersion(), js.getJfifMinorVersion(), expectedMajor, expectedMinor);
                int minUnits = 0;
                int maxUnits = 2;
                int units = jis.readByte() & 0xff;
                segmentBytesLeft--;
                if (units < minUnits || units > maxUnits)
                    mh.logResult(JpegValidator.MsgCode.JPG01W007, units, minUnits, maxUnits);
                int xDensity = jis.readShort() & 0xffff;
                segmentBytesLeft -= 2;
                if (xDensity == 0)
                    mh.logResult(JpegValidator.MsgCode.JPG01W009);
                int yDensity = jis.readShort() & 0xffff;
                segmentBytesLeft -= 2;
                if (yDensity == 0)
                    mh.logResult(JpegValidator.MsgCode.JPG01W010);
                int xt = jis.readByte() & 0xff;
                segmentBytesLeft--;
                int xy = jis.readByte() & 0xff;
                segmentBytesLeft --;
                int rgb3n = 3 * xt * xy;
                jis.skipBytes(rgb3n);
                segmentBytesLeft -= rgb3n;
                if (segmentBytesLeft != 0)
                    mh.logResult(JpegValidator.MsgCode.JPG01W013, segmentBytesLeft);
                mh.logResult(JpegValidator.MsgCode.JPG01I017);
                return true;
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
        return false;
    }

    private boolean checkJfifExtensionSegment(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                if (js.getLastJfifApp0SegmentIndex() != (js.getSegmentCount() - 1))
                    mh.logResult(JpegValidator.MsgCode.JPG01E022, js.getSegmentCount(), js.getLastJfifApp0SegmentIndex());
                js.markLastJfifApp0SegmentIndex();
                if (js.getJfifMajorVersion() < 1 || (js.getJfifMajorVersion() == 1 && js.getJfifMinorVersion() < 2))
                    mh.logResult(JpegValidator.MsgCode.JPG01W011, js.getJfifMajorVersion(), js.getJfifMinorVersion());
                int extensionCode = jis.readByte() & 0xff;
                segmentBytesLeft -= 1;
                switch (extensionCode)
                    {
                    case 0x10:
                        mh.logResult(JpegValidator.MsgCode.JPG01I018, extensionCode);
                        return checkThumbnailStoredAsJpeg(jis, js, segmentBytesLeft, mh);
                    case 0x11:
                        mh.logResult(JpegValidator.MsgCode.JPG01I018, extensionCode);
                        return checkThumbnailStored1BytePerPixel(jis, js, segmentBytesLeft, mh);
                    case 0x13:
                        mh.logResult(JpegValidator.MsgCode.JPG01I018, extensionCode);
                        return checkThumbnailStored3BytesPerPixel(jis, js, segmentBytesLeft, mh);
                    default:
                        mh.logResult(JpegValidator.MsgCode.JPG01W012, extensionCode);
                    }
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
        return false;
    }

    private boolean checkThumbnailStoredAsJpeg(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                // TODO: validate thumbnail JPEG
                jis.skipBytes(segmentBytesLeft);
                return true;
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
        return false;
    }

    private boolean checkThumbnailStored1BytePerPixel(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                int xThumbnail = jis.readByte() & 0xff;
                segmentBytesLeft--;
                int yThumbnail = jis.readByte() & 0xff;
                segmentBytesLeft--;
                jis.skipBytes(768);
                segmentBytesLeft -= 768;
                int thumbnailPixels = xThumbnail * yThumbnail;
                jis.skipBytes(thumbnailPixels);
                segmentBytesLeft -= thumbnailPixels;
                if (segmentBytesLeft != 0)
                    mh.logResult(JpegValidator.MsgCode.JPG01W014, segmentBytesLeft);
                return true;
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
        return false;
    }

    private boolean checkThumbnailStored3BytesPerPixel(JpegInputStream jis, JpegState js, int segmentBytesLeft, AbstractLoggingValidator mh) throws EOFException
    {
        try
            {
                int xThumbnail = jis.readByte() & 0xff;
                segmentBytesLeft--;
                int yThumbnail = jis.readByte() & 0xff;
                segmentBytesLeft--;
                int thumbnailPixels = 3 * xThumbnail * yThumbnail;
                jis.skipBytes(thumbnailPixels);
                segmentBytesLeft -= thumbnailPixels;
                if (segmentBytesLeft != 0)
                    mh.logResult(JpegValidator.MsgCode.JPG01W014, segmentBytesLeft);
                return true;
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
        return false;
    }
}
