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

public final class ChunkValidatorIHDR extends ChunkValidator {

    public static final class Spec {

        public static final String section = "4.1.1";
        public static final byte[] header =
            ChunkValidatorIHDR.class.getName().substring(ChunkValidatorIHDR.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatorIHDR.class.getName().length()).getBytes();
        public static class Offset {
            public static final byte width                      = 0;
            public static final byte height                     = 4;
            public static final byte bitDepth                   = 8;
            public static final byte colorType                  = 9;
            public static final byte compressionMethod          = 10;
            public static final byte filterMethod               = 11;
            public static final byte interlaceMethod            = 12;
        }

        public static class Size {
            public static final byte width                      = 4;
            public static final byte height                     = 4;
            public static final byte bitDepth                   = 1;
            public static final byte colorType                  = 1;
            public static final byte compressionMethod          = 1;
            public static final byte filterMethod               = 1;
            public static final byte interlaceMethod            = 1;
        }

    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        validatePosition();
        validateWidth();
        validateHeight();
        validateBitDepthAndColorType();
        validateCompressionMethod(Spec.Offset.compressionMethod, 0, Spec.section);
        validateFilterMethod();
        validateInterlaceMethod();
    }

    private void validatePosition() throws PngValidationException {
        if (png.getCurrentChunkIndex() != 0)
            png.logMsg(PngValidator.MsgCode.PNG01E003, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I016, null);
    }

    private void validateWidth() throws PngValidationException {
        int width = Utils.convertToInt(data, Spec.Offset.width, Spec.Size.width);
        int invalid = 0;
        if (width == invalid)
            png.logMsg(PngValidator.MsgCode.PNG01E005, Spec.section, width);
        else {
            png.logMsg(PngValidator.MsgCode.PNG01I017, Spec.section, width);
            resultState.put("width", Integer.valueOf(width));
        }
    }

    private void validateHeight() throws PngValidationException {
        int height = Utils.convertToInt(data, Spec.Offset.height, Spec.Size.height);
        int invalid = 0;
        if (height == invalid)
            png.logMsg(PngValidator.MsgCode.PNG01E006, Spec.section, height);
        else {
            png.logMsg(PngValidator.MsgCode.PNG01I018, Spec.section, height);
            resultState.put("height", Integer.valueOf(height));
        }
    }

    private void validateBitDepthAndColorType() throws PngValidationException {
        byte bitDepth = data[Spec.Offset.bitDepth];
        byte colorType = data[Spec.Offset.colorType];
        png.setColorType(colorType);
        png.setBitDepth(bitDepth);
        switch (colorType) {
        case 0:
            png.logMsg(PngValidator.MsgCode.PNG01I020, Spec.section, colorType);
            switch (bitDepth) {
            case 1:
            case 2:
            case 4:
            case 8:
            case 16:
                png.logMsg(PngValidator.MsgCode.PNG01I019, Spec.section, bitDepth);
                break;
            default:
                png.logMsg(PngValidator.MsgCode.PNG01E042, Spec.section, colorType, bitDepth);
            }
            break;
        case 3:
            png.logMsg(PngValidator.MsgCode.PNG01I020, Spec.section, colorType);
            switch (bitDepth) {
            case 1:
            case 2:
            case 4:
            case 8:
                png.logMsg(PngValidator.MsgCode.PNG01I019, Spec.section, bitDepth);
                break;
            default:
                png.logMsg(PngValidator.MsgCode.PNG01E042, Spec.section, colorType, bitDepth);
            }
            break;
        case 2:
        case 4:
        case 6:
            png.logMsg(PngValidator.MsgCode.PNG01I020, Spec.section, colorType);
            switch (bitDepth) {
            case 8:
            case 16:
                png.logMsg(PngValidator.MsgCode.PNG01I019, Spec.section, bitDepth);
                break;
            default:
                png.logMsg(PngValidator.MsgCode.PNG01E042, Spec.section, colorType, bitDepth);
            }
            break;
        default:
            png.logMsg(PngValidator.MsgCode.PNG01E008, Spec.section, colorType);
        }
        ChunkState plte = png.getChunkState(ChunkValidatorPLTE.Spec.header);
        switch (colorType) {
        case 3:
            plte.setStateType(ChunkState.StateType.mustExist);
            break;
        case 0:
        case 4:
            plte.setStateType(ChunkState.StateType.mustNotExist);
            break;
        }
    }

    private void validateFilterMethod() throws PngValidationException {
        byte actual = data[Spec.Offset.filterMethod];
        int required = 0;
        if (actual != required)
            png.logMsg(PngValidator.MsgCode.PNG01E009, Spec.section, actual);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I021, Spec.section, actual);
    }

    private void validateInterlaceMethod() throws PngValidationException {
        byte actual = data[Spec.Offset.interlaceMethod];
        if (actual != 0 && actual != 1)
            png.logMsg(PngValidator.MsgCode.PNG01E010, Spec.section, actual);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I022, Spec.section, actual);
    }

}
