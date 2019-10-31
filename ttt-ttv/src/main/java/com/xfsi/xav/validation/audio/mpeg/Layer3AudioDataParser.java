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
 * Parses and validates MPEG audio layer 3 audio_data() block
 */
class Layer3AudioDataParser {
    private BitInputStream bis = null;
    private AbstractLoggingValidator mh = null;
    private State s = null;

    Layer3AudioDataParser(BitInputStream bis, State s, AbstractLoggingValidator mh) {
        this.bis = bis;
        this.s = s;
        this.mh = mh;
    }

    boolean parse() {
        try {
            // TODO: remove: parse audio data properly and remove skipping
            if (this.s != null) { // always true, enter skipping code
                for (int i = 0; i < this.s.getBytesToNextFrameBeginning() - (this.s.hasAddedRedundancy() ? 6 : 4); i++)
                    this.bis.readBits(8);
            }
            // TODO: end remove
            // if (this.s != null) {
            //     this.s.setMainDataBegin(this.bis.readBits(9));
            //     if (this.s.getMode() == State.Mode.SINGLE_CHANNEL)
            //         this.bis.readBits(5);
            //     else
            //         this.bis.readBits(3);
            //     for (int ch = 0; ch < this.s.getNumberOfChannels(); ch++) {
            //         for (int scfsiBand = 0; scfsiBand < 4; scfsiBand++)
            //             this.bis.readBits(1);
            //     }
            //     for (int gr=0; gr < 2; gr++) {
            //         for (int ch = 0; ch < this.s.getNumberOfChannels(); ch++) {
            //             this.bis.readBits(12);
            //             this.bis.readBits(9);
            //             this.bis.readBits(8);
            //             this.bis.readBits(4);
            //             if (this.bis.readBits(1) == 1) {
            //                 this.bis.readBits(2);
            //                 this.bis.readBits(1);
            //                 for (int region = 0; region < 2;  region++)
            //                     this.bis.readBits(5);
            //                 for (int window = 0; window < 2;  window++)
            //                     this.bis.readBits(3);
            //             } else {
            //                 for (int region = 0; region < 2;  region++)
            //                     this.bis.readBits(5);
            //                 this.bis.readBits(4);
            //                 this.bis.readBits(3);
            //             }
            //             this.bis.readBits(1);
            //             this.bis.readBits(1);
            //             this.bis.readBits(1);
            //         }
            //     }
            // }
            parseMainData();
        } catch (EOFException e) {
            return true;
            // TODO: this.mh.logResult(MPA01F006, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            // TODO: throw new CannotContinueValidationException();
        } catch (IOException e) {
            assert(false) :
              mh.msgFormatterNV(MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
        return false;
    }

    private void parseMainData() // TODO: implement: throws CannotContinueValidationException
    {
        // TODO: implement
    }
}
