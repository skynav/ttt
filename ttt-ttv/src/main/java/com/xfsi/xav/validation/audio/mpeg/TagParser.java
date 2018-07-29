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

class TagParser {
    private static enum TagType {
        ID3V1,
        ID3V2,
        ID3V2_FOOTER,
    };
    private BitInputStream bis = null;
    private AbstractLoggingValidator mh = null;
    private State s = null;
    private TagType tt = null;
    @SuppressWarnings("unused")
    private Integer flags = null;
    private Integer size = null;

    TagParser(BitInputStream bis, State s, AbstractLoggingValidator mh) {
        this.bis = bis;
        this.s = s;
        this.mh = mh;
    }

    boolean parse(int maybeSyncword) throws CannotContinueValidationException {
        try {
            findTagType(maybeSyncword);
            if (this.tt != null) {
                this.mh.logResult(MPA01E007, this.tt.toString(), this.bis.getTotalBytesRead());
                switch (this.tt) {
                case ID3V1:
                    parseId3v1();
                    break;
                case ID3V2:
                    parseId3v2();
                    break;
                case ID3V2_FOOTER:
                    parseId3v2Footer();
                    break;
                }
                return true;
            }
        } catch (EOFException e) {
            this.mh.logResult(MPA01F003, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        } catch (IOException e) {
            assert(false) :
              mh.msgFormatterNV(MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
        return false;
    }
        
    private void findTagType(int syncword) throws IOException {
        if (syncword == 0x544 &&                                // 'T' && (('A' & 0xf0) >>> 4)
            this.bis.readBits(12) == 0x147) {                   // ('A' & 0xf) && 'G')
            this.size = 128 - 3;                                // 128 - already read tag
            this.tt = TagType.ID3V1;
        }
        int sizeMsb0 = 0;
        int sizeMsb1 = 0;
        int sizeMsb2 = 0;
        int sizeMsb3 = 0;
        if (((syncword == 0x494 &&                              // 'I' && (('D' & 0xf0) >>> 4)
              this.bis.readBits(12) == 0x433) ||                // ('D' & 0xf) && '3'
             (syncword == 0x334 &&                              // '3' && (('D' & 0xf0) >>> 4)
              this.bis.readBits(12) == 0x449)) &&               // ('D' & 0xf) && 'I'
            this.bis.readBits(8) < 0xff &&                      // version byte 1
            this.bis.readBits(8) < 0xff &&                      // version byte 2
            (this.flags = this.bis.readBits(8)) > -1 &&         // flags, don't care value
            (sizeMsb0 = this.bis.readBits(8)) < 0x80 &&
            (sizeMsb1 = this.bis.readBits(8)) < 0x80 &&
            (sizeMsb2 = this.bis.readBits(8)) < 0x80 &&
            (sizeMsb3 = this.bis.readBits(8)) < 0x80) {
            this.size = sizeMsb0 << 21 | sizeMsb1 << 14 | sizeMsb2 << 7 | sizeMsb3;
            this.tt = (syncword & 0xff0) == 0x490 ? TagType.ID3V2 : TagType.ID3V2_FOOTER;
        }
    }
        
    private void parseId3v2() throws IOException {
        for (int i = 0; i < this.size; i++)
            this.bis.readBits(8);
    }
        
    private void parseId3v1() throws IOException {
        for (int i = 0; i < this.size; i++)
            this.bis.readBits(8);
    }
        
    private void parseId3v2Footer() {
        // TODO: implement: throws IOException
    }
}
