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

package com.xfsi.xav.validation.images.png;

import com.xfsi.xav.validation.util.Util;

public abstract class ChunkValidator {

    protected PngValidator png = null;
    protected byte[] data = null;

    public static final class Spec {
        public static final class Props {
            // All in bytes
            public static final byte lengthSize = 4;
            public static final byte typeSize = 4;
            public static final byte crcSize = 4;
        }
    }

    void initialize(PngValidator caller, byte[] chunkData) {
        png = caller;
        data = chunkData;
    }

    protected void validateMultipleAllowed(byte[] type) throws PngValidationException {
        ChunkState ce = png.getChunkState(type);
        if (!ce.getMultipleAllowed() && ce.getCount() > 0)
            png.logMsg(PngValidator.MsgCode.PNG01E023, null, new String(type));
        else
            png.logMsg(PngValidator.MsgCode.PNG01I008, null);
        ce.incrementCount();
        ce.setData(this.data);
    }

    // TODO: make sure whoever calls this REQUIRES the chunktype to be present
    protected void validateAfterChunk(byte[] chunkType) throws PngValidationException {
        ChunkState ac = png.getChunkState(chunkType);
        if (ac.mustExist()) {
            if (ac.getCount() == 0)
                png.logMsg(PngValidator.MsgCode.PNG01E035, null, new String(chunkType));
            else
                png.logMsg(PngValidator.MsgCode.PNG01I009, null, new String(chunkType));
        }
    }

    protected void validateBeforeChunk(byte[] chunkType) throws PngValidationException {
        ChunkState bc = png.getChunkState(chunkType);
        if (bc.getCount() > 0)
            png.logMsg(PngValidator.MsgCode.PNG01E022, null, new String(chunkType));
        else
            png.logMsg(PngValidator.MsgCode.PNG01I010, null, new String(chunkType));
    }

    protected boolean validateDataSize(int required, String section) throws PngValidationException {
        boolean valid = data.length == required;
        if (!valid)
            png.logMsg(PngValidator.MsgCode.PNG01E036, section, data.length);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I011, section, required);
        return valid;
    }

    protected void validateCompressionMethod(int dataOffset, int required, String section) throws PngValidationException {
        if (dataOffset < data.length) {
            int actual = data[dataOffset];
            if (actual != required)
                png.logMsg(PngValidator.MsgCode.PNG01E017, section, actual);
            else
                png.logMsg(PngValidator.MsgCode.PNG01I012, section, required);
        } else
            png.logMsg(PngValidator.MsgCode.PNG01E043, section, required);
    }

    protected void validateDataBytes(int index, int length, int minVal, int maxVal, PngValidator.MsgCode msgType, String section) throws PngValidationException {
        int actual;
        for (int i = index; i < length; i++) {
            actual = data[i];
            if (actual < minVal || actual > maxVal)
                png.logMsg(msgType, section, minVal, maxVal, actual);
        }
    }

    abstract void validate() throws PngValidationException;
}
