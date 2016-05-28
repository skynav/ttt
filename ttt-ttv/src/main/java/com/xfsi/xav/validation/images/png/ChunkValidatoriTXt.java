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

public final class ChunkValidatoriTXt extends ChunkValidator {

    public static final class Spec {
        public static final String section = "11.3.4.5";
        static final byte[] header =
            ChunkValidatoriTXt.class.getName().substring(ChunkValidatoriTXt.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatoriTXt.class.getName().length()).getBytes(Utils.getCharset());
    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        int index = validateKeyword();
        validateCompressionFlag(index++);
        validateCompressionMethod(index++, 0, Spec.section);
        index = validateLanguageTag(index);
        index = validateUTF8Keyword(index);
        validateText(index);
    }

    private int validateKeyword() throws PngValidationException {
        return Utils.findValidatedNameSize(data, png, false, Spec.section);
    }

    protected void validateCompressionFlag(int dataOffset) throws PngValidationException {
        int[] required = { 0, 1 };
        if (dataOffset < data.length) {
            int actual = data[dataOffset];
            if (actual != required[0] && actual != required[1])
                png.logMsg(PngValidator.MsgCode.PNG01E030, Spec.section, required[0], required[1], actual);
            else
                png.logMsg(PngValidator.MsgCode.PNG01I023, Spec.section, actual);
        } else
            png.logMsg(PngValidator.MsgCode.PNG01E045, Spec.section, required[0], required[1]);
    }

    private int validateLanguageTag(int dataOffset) throws PngValidationException {
        return findDelimiter(dataOffset, PngValidator.MsgCode.PNG01E031);
    }

    private int validateUTF8Keyword(int dataOffset) throws PngValidationException {
        return findDelimiter(dataOffset, PngValidator.MsgCode.PNG01E029);
    }

    private void validateText(int dataOffset) throws PngValidationException {
        boolean errFound = false;
        for (int i = dataOffset; i < data.length; i++) {
            if (data[i] == 0) {
                png.logMsg(PngValidator.MsgCode.PNG01E032, Spec.section, 0);
                errFound = true;
            }
        }
        if (!errFound)
            png.logMsg(PngValidator.MsgCode.PNG01I024, null);
    }

    private int findDelimiter(int dataOffset, PngValidator.MsgCode msgIndex) throws PngValidationException {
        int i;
        for (i = dataOffset; data[i] != 0 && i < data.length; i++)
            continue;
        if (i == data.length)
            png.logMsg(msgIndex, Spec.section);
        else
            i++; // skip null delimiter
        return i;
    }

}
