/*
 * Copyright 2018 Skynav, Inc. All rights reserved.
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

package com.xfsi.xav.validation.audio.mpeg;

import java.io.EOFException;
import java.io.IOException;

import com.xfsi.xav.validation.util.AbstractLoggingValidator;
import com.xfsi.xav.validation.util.BitInputStream;

import static com.xfsi.xav.validation.audio.mpeg.MpegValidator.MsgCode.*;

/**
 * Parses and validates MPEG audio layer 1 audio_data() block
 */
class Layer1AudioDataParser {
    private BitInputStream bis = null;
    private AbstractLoggingValidator mh = null;
    private State s = null;

    Layer1AudioDataParser(BitInputStream bis, State s, AbstractLoggingValidator mh) {
        this.bis = bis;
        this.s = s;
        this.mh = mh;
    }

    boolean parse() throws CannotContinueValidationException {
        try {
            // TODO: remove: parse audio data properly and remove skipping
            if (this.s != null) { // always true, enter skipping code
                for (int i = 0; i < this.s.getBytesToNextFrameBeginning() - (this.s.hasAddedRedundancy() ? 6 : 4); i++)
                    this.bis.readBits(8);
                return false;
            }
            // TODO: end remove
        } catch (EOFException e) {
            this.mh.logResult(MPA01F004, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        } catch (IOException e) {
            assert(false) : mh.msgFormatterNV(MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
        return false;
    }
}
