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

/* -*- indent-tabs-mode:nil;tab-width:4;coding:utf-8-unix -*- */

package com.xfsi.xav.validation.images.png;

public final class ChunkValidatorsBIT extends ChunkValidator {

    public static final class Spec {
        public static final String section = "4.2.6";
        public static final byte[] header =
            ChunkValidatorsBIT.class.getName().substring(ChunkValidatorsBIT.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatorsBIT.class.getName().length()).getBytes();
    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        validateBeforeChunk(ChunkValidatorPLTE.Spec.header);
        validateBeforeChunk(ChunkValidatorIDAT.Spec.header);
        validateBitDepths();
    }

    private void validateBitDepths() throws PngValidationException {
        // TODO: validate values are less than or equal to sample depth
        int colorType = png.getColorType();
        int bitDepth = png.getBitDepth();
        switch (colorType) {
        case 0:
            validateDataBytes(0, 1, 1, bitDepth, PngValidator.MsgCode.PNG01E007, Spec.section);
            break;
        case 2:
            validateDataBytes(0, 3, 1, bitDepth, PngValidator.MsgCode.PNG01E007, Spec.section);
            break;
        case 3:
            validateDataBytes(0, 3, 1, 8, PngValidator.MsgCode.PNG01E007, Spec.section);
            break;
        case 4:
            validateDataBytes(0, 2, 1, bitDepth, PngValidator.MsgCode.PNG01E007, Spec.section);
            break;
        case 6:
            validateDataBytes(0, 4, 1, bitDepth, PngValidator.MsgCode.PNG01E007, Spec.section);
            break;
        }
    }

}
