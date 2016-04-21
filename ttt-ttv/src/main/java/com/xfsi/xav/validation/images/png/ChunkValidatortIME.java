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

public final class ChunkValidatortIME extends ChunkValidator {

    public static final class Spec {

        public static final String section = "4.2.8";
        public static final byte[] header =
            ChunkValidatortIME.class.getName().substring(ChunkValidatortIME.class.getName().length() - ChunkValidator.Spec.Props.typeSize,
                                                         ChunkValidatortIME.class.getName().length()).getBytes();

        public static class Offset {
            static final byte data                              = 0;
            static final byte year                              = 0;
            static final byte month                             = 2;
            static final byte day                               = 3;
            static final byte hour                              = 4;
            static final byte minute                            = 5;
            static final byte second                            = 6;
        }

        public static class Size {
            static final byte data                              = 7;
            static final byte year                              = 2;
            static final byte month                             = 1;
            static final byte day                               = 1;
            static final byte hour                              = 1;
            static final byte minute                            = 1;
            static final byte second                            = 1;
        }

    }

    void validate() throws PngValidationException {
        validateMultipleAllowed(Spec.header);
        if (validateDataSize(Spec.Size.data, Spec.section)) {
            validateMonth();
            validateDay();
            validateHour();
            validateMinute();
            validateSecond();
        }
    }

    private void validateMonth() throws PngValidationException {
        byte month = data[Spec.Offset.month];
        if ( month < 1 || month > 12)
            png.logMsg(PngValidator.MsgCode.PNG01E024, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I028, null);
    }

    private void validateDay() throws PngValidationException {
        byte day = data[Spec.Offset.day];
        if ( day < 1 || day > 31)
            png.logMsg(PngValidator.MsgCode.PNG01E025, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I029, null);
    }

    private void validateHour() throws PngValidationException {
        byte hour = data[Spec.Offset.hour];
        if ( hour > 23)
            png.logMsg(PngValidator.MsgCode.PNG01E026, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I030, null);
    }

    private void validateMinute() throws PngValidationException {
        byte minute = data[Spec.Offset.minute];
        if ( minute > 59)
            png.logMsg(PngValidator.MsgCode.PNG01E027, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I031, null);
    }

    private void validateSecond() throws PngValidationException {
        byte second = data[Spec.Offset.second];
        if ( second > 60)
            png.logMsg(PngValidator.MsgCode.PNG01E028, Spec.section);
        else
            png.logMsg(PngValidator.MsgCode.PNG01I032, null);
    }

}
