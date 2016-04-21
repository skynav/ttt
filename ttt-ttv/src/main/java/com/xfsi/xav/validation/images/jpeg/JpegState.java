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

/**
 * Maintains state during JPEG file validation.
 */
class JpegState {
    static enum FrameType
    {
        BASELINE,
        EXTENDED,
        PROGRESSIVE,
        LOSSLESS,
    }
    // There's no unsigned short in Java, so use an integer to store marker code, all of which are in the
    // 0xFFxx range
    private Integer currentCode;
    private Integer lastCode;
    private String currentSegmentParserName;
    private Integer initialFrameCode;
    private boolean soiFound;
    private boolean eoiFound;
    private int sosSegmentCount;
    private int dnlSegmentCount;
    private int app0SegmentCount;
    private int segmentCount;
    private boolean dnlSegmentAllowed;
    private boolean dnlRequired;
    private int tablesMiscCount;
    private int jfifMajorVersion;
    private int jfifMinorVersion;
    private int lastJfifApp0SegmentIndex;

    void setInitialFrameCode(Integer code)
    {
        this.initialFrameCode = code;
    }

    Integer getInitialFrameCode()
    {
        return this.initialFrameCode;
    }

    FrameType getFrameType()
    {
        if (this.initialFrameCode != null)
            {
                switch (this.initialFrameCode & 0xf)
                    {
                    case 0:
                        return FrameType.BASELINE;
                    case 1:
                    case 5:
                    case 9:
                    case 13:
                        return FrameType.EXTENDED;
                    case 2:
                    case 6:
                    case 10:
                    case 14:
                        return FrameType.PROGRESSIVE;
                    case 3:
                    case 7:
                    case 11:
                    case 15:
                        return FrameType.LOSSLESS;
                    }
            }
        return null;
    }

    Integer getCurrentCode()
    {
        return this.currentCode;
    }

    void setCurrentCode(short currentCode)
    {
        this.lastCode = this.currentCode;
        this.currentCode = (currentCode & 0xffff);
        this.segmentCount++;
    }

    Integer getLastCode()
    {
        return this.lastCode;
    }

    String getCurrentSegmentParserName()
    {
        return this.currentSegmentParserName;
    }

    void setCurrentSegmentParserName(String currentSegmentParserName)
    {
        this.currentSegmentParserName = currentSegmentParserName;
    }

    void setSoiFound()
    {
        this.soiFound = true;
    }

    boolean isSoiFound()
    {
        return this.soiFound;
    }

    void setEoiFound()
    {
        this.eoiFound = true;
    }

    boolean isEoiFound()
    {
        return this.eoiFound;
    }

    int getSegmentCount()
    {
        return this.segmentCount;
    }

    void incrementSosSegmentCount()
    {
        this.sosSegmentCount++;
    }

    int getSosSegmentCount()
    {
        return this.sosSegmentCount;
    }

    void incrementApp0SegmentCount()
    {
        this.app0SegmentCount++;
    }

    int getApp0SegmentCount()
    {
        return this.app0SegmentCount;
    }

    void incrementDnlSegmentCount()
    {
        this.dnlSegmentCount++;
    }

    int getDnlSegmentCount()
    {
        return this.dnlSegmentCount;
    }

    void allowDnlSegment()
    {
        this.dnlSegmentAllowed = true;
    }

    void disallowDnlSegment()
    {
        this.dnlSegmentAllowed = false;
    }

    boolean getDnlSegmentAllowed()
    {
        return this.dnlSegmentAllowed;
    }

    void requireDnlSegment()
    {
        this.dnlRequired = true;
    }

    boolean isDnlSegmentRequired()
    {
        return this.dnlRequired;
    }

    boolean usesHuffmanCoding()
    {
        if (this.initialFrameCode != null)
            {
                switch (this.initialFrameCode & 0xf)
                    {
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                    case 5:
                    case 6:
                    case 7:
                        return true;
                    }
            }
        return false;
    }

    boolean usesArithmeticCoding()
    {
        if (this.initialFrameCode != null)
            {
                switch (this.initialFrameCode & 0xf)
                    {
                    case 9:
                    case 10:
                    case 11:
                    case 13:
                    case 14:
                    case 15:
                        return true;
                    }
            }
        return false;
    }

    void setJfifMajorVersion(int version)
    {
        this.jfifMajorVersion = version;
    }

    int getJfifMajorVersion()
    {
        return this.jfifMajorVersion;
    }

    void setJfifMinorVersion(int version)
    {
        this.jfifMinorVersion = version;
    }

    int getJfifMinorVersion()
    {
        return this.jfifMinorVersion;
    }

    void markLastJfifApp0SegmentIndex()
    {
        this.lastJfifApp0SegmentIndex = this.segmentCount;
    }

    int getLastJfifApp0SegmentIndex()
    {
        return this.lastJfifApp0SegmentIndex;
    }

    void incrementTablesMiscSegmentCount()
    {
        this.tablesMiscCount++;
    }

    int getTableMiscSegmentCount()
    {
        return this.tablesMiscCount;
    }
}
