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

public final class ChunkValidatorsPLT extends ChunkValidator {

    public static final class Spec {
        public static final String section = "11.3.5.4";
        public static final byte[] header =
            ChunkValidatorsPLT.class.getName().substring(ChunkValidatorsPLT.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatorsPLT.class.getName().length()).getBytes();
    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        validateBeforeChunk(ChunkValidatorIDAT.Spec.header);
        int size = validatePaletteName();
        validateEntries(size);
    }

    private int validatePaletteName() throws PngValidationException {
        // TODO: make sure all other sPLT names are different
        return Utils.findValidatedNameSize(data, png, true, Spec.section);
    }

    private void validateEntries(int index) throws PngValidationException {
        int[] required = {8, 16};
        if (index < data.length) {
            byte actual = data[index];
            int entriesBlobSize = data.length - index - 1; // index = name length, 1 = sample depth size
            int divisibleBy = 0;
            switch (actual) {
            case 8:
                divisibleBy = 6;
                break;
            case 16:
                divisibleBy = 10;
                break;
            default:
                png.logMsg(PngValidator.MsgCode.PNG01E044, Spec.section, required[0], required[1], actual);
            }
            if (divisibleBy > 0) {
                png.logMsg(PngValidator.MsgCode.PNG01I019, Spec.section, actual);
                if ((entriesBlobSize % divisibleBy) != 0)
                    png.logMsg(PngValidator.MsgCode.PNG01E047, Spec.section, divisibleBy);
                else
                    png.logMsg(PngValidator.MsgCode.PNG01I026, Spec.section, divisibleBy);
            }
        } else
            png.logMsg(PngValidator.MsgCode.PNG01E046, Spec.section, required[0], required[1]);
    }

}
