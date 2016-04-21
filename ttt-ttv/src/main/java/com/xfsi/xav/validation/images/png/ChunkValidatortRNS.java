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

public final class ChunkValidatortRNS extends ChunkValidator {

    public static final class Spec {
        public static final String section = "4.2.9";
        public static final byte[] header =
            ChunkValidatortRNS.class.getName().substring(ChunkValidatortRNS.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatortRNS.class.getName().length()).getBytes();
    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        validateBeforeChunk(ChunkValidatorIDAT.Spec.header);
        validateTransparency();
    }

    private void validateTransparency() throws PngValidationException {
        ChunkState trns = png.getChunkState(ChunkValidatortRNS.Spec.header);
        ChunkState plte = png.getChunkState(ChunkValidatorPLTE.Spec.header);
        byte[] PLTEData = plte.getData();
        ChunkState ihdr = png.getChunkState(ChunkValidatorIHDR.Spec.header);
        byte[] IHDRData = ihdr.getData();
        byte colorType = IHDRData[9]; // TODO: make this better

        switch (colorType) {
        case 0:
            // TODO: verify this
            break;
        case 2:
            // TODO: verify this
            break;
        case 3:
            validateAfterChunk(ChunkValidatorPLTE.Spec.header);
            if (PLTEData == null) {
                png.logMsg(PngValidator.MsgCode.PNG01E016, null, new String(ChunkValidatorPLTE.Spec.header));
                break;
            }
            int tRNSEntries = data.length;
            int PLTEEntries = PLTEData.length/3;
            if (tRNSEntries > PLTEEntries)
                png.logMsg(PngValidator.MsgCode.PNG01E020, Spec.section, PLTEEntries, tRNSEntries);
            else
                png.logMsg(PngValidator.MsgCode.PNG01I033, Spec.section, PLTEEntries, tRNSEntries);
            break;
        case 4:
        case 6:
            png.logMsg(PngValidator.MsgCode.PNG01W002, null, new String(Spec.header));
            trns.setStateType(ChunkState.StateType.mustNotExist);
            break;
        }
    }

}
