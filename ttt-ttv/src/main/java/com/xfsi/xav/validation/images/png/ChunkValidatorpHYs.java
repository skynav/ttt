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

public final class ChunkValidatorpHYs extends ChunkValidator {

    public static final class Spec {
        public static final String section = "4.2.5";
        static final byte[] header =
            ChunkValidatorpHYs.class.getName().substring(ChunkValidatorpHYs.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatorpHYs.class.getName().length()).getBytes(Utils.getCharset());
    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        validateBeforeChunk(ChunkValidatorIDAT.Spec.header);
        validateUnitSpecifier();
    }

    void validateUnitSpecifier() throws PngValidationException {
        byte actual = data[8];
        if (actual != 0 && actual != 1)
            png.logMsg(PngValidator.MsgCode.PNG01E021, Spec.section, actual);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I025, Spec.section, actual);
    }

}
