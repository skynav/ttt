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
 * Parses and validates MPEG audio header() block
 */
class HeaderParser {
    private static final int bitrateTable[][] = {
        { 0,  32000,  64000,  96000, 128000, 160000, 192000, 224000, 256000, 288000, 320000, 352000, 384000, 416000, 448000 }, // MPEG-1 Layer I
        { 0,  32000,  48000,  56000,  64000,  80000,  96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000, 384000 }, // MPEG-1 Layer II
        { 0,  32000,  40000,  48000,  56000,  64000,  80000,  96000, 112000, 128000, 160000, 192000, 224000, 256000, 320000 }, // MPEG-1 Layer III
        { 0,  32000,  48000,  56000,  64000,  80000,  96000, 112000, 128000, 144000, 160000, 176000, 192000, 224000, 256000 }, // MPEG-2 Layer I
        { 0,   8000,  16000,  24000,  32000,  40000,  48000,  56000,  64000,  80000,  96000, 112000, 128000, 144000, 160000 }, // MPEG-2 Layers II and III
    };
    private static final int samplingFrequency[] = { 44100, 48000, 32000 };
    private static final int syncword = 0xfff;
    private BitInputStream bis = null;
    private AbstractLoggingValidator mh = null;
    private State s = null;

    HeaderParser(BitInputStream bis, State s, AbstractLoggingValidator mh) {
        this.bis = bis;
        this.s = s;
        this.mh = mh;
    }

    void parse(int syncword) throws CannotContinueValidationException {
        try {
            validateSyncword(syncword);
            validateId(this.bis.readBits(1));
            validateLayer(this.bis.readBits(2));
            validateProtectionBit(this.bis.readBits(1));
            validateBitRateIndex(this.bis.readBits(4));
            validateSamplingFrequency(this.bis.readBits(2));
            validatePaddingBit(this.bis.readBits(1));
            this.bis.readBits(1); // TODO: validate private_bit
            validateMode(this.bis.readBits(2));
            validateModeExtension(this.bis.readBits(2));
            this.mh.logAll(MPA01I012, this.bis.readBits(1) == 1);
            this.mh.logAll(MPA01I013, this.bis.readBits(1) == 1);
            validateEmphasis(this.bis.readBits(2));
        } catch (EOFException e) {
            this.mh.logResult(MPA01F002, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        } catch (IOException e) {
            assert(false) :
              mh.msgFormatterNV(MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
    }

    int readSyncword() throws CannotContinueValidationException {
        int syncword = 0;
        try {
            syncword = this.bis.readBits(12);
        } catch (EOFException e) {
            if (this.s.getFrameCount() == 0)
                this.mh.logResult(MPA01W002);
            throw new CannotContinueValidationException(); // no more syncwords -> so no more frames -> we're done, end validation without error
        } catch (IOException e) {
            assert(false) :
              mh.msgFormatterNV(MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
        return syncword;
    }

    private void validateSyncword(int syncword) throws IOException {
        if (syncword != HeaderParser.syncword) {
            //this.mh.logResult(MPA01E001, syncword, HeaderParser.syncword, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            do {
                syncword <<= 1;
                syncword |= this.bis.readBits(1);
                continue;
            } while (syncword != HeaderParser.syncword);
        }
        this.mh.logAll(MPA01I003, syncword, this.bis.getTotalBytesRead());
    }

    private void validateId(int id) {
        this.s.setAlgorithmBit(id == 1);
        if (this.s.isAlgorithmBit())
            this.mh.logAll(MPA01I004);
        else
            this.mh.logResult(MPA01E002, this.s.getFrameCount(), this.bis.getTotalBytesRead());         
    }
        
    private void validateLayer(int layer) throws CannotContinueValidationException {
        switch (layer) {
        case 3:
            this.s.setLayer(State.Layer.LAYER_I);
            this.mh.logAll(MPA01I005, 1);
            break;
        case 2:
            this.s.setLayer(State.Layer.LAYER_II);
            this.mh.logAll(MPA01I005, 2);
            break;
        case 1:
            this.s.setLayer(State.Layer.LAYER_III);
            this.mh.logAll(MPA01I005, 3);
            break;
        default:
            this.mh.logResult(MPA01E003, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        }
    }

    private void validateProtectionBit(int protectionBit) {
        this.s.setAddedRedundancy(protectionBit == 0);
        this.mh.logAll(MPA01I006, this.s.hasAddedRedundancy());         
    }

    private void validateBitRateIndex(int bitrateIndex) throws CannotContinueValidationException {
        this.s.setBitRateIndex(bitrateIndex);
        if (this.s.getBitRateIndex() == 0xf) {
            this.mh.logResult(MPA01E004, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        }
        this.mh.logAll(MPA01I007, this.s.getBitRateIndex());
        if (this.s.isAlgorithmBit())
            this.s.setBitRate(bitrateTable[this.s.getLayer().ordinal() - 1][bitrateIndex]);
        else
            this.s.setBitRate(bitrateTable[3 + (this.s.getLayer().ordinal() >> 1)][bitrateIndex]);
        this.mh.logAll(MPA01I015, this.s.getBitRate()/1000);
    }

    private void validateSamplingFrequency(int samplingFrequency) throws CannotContinueValidationException {
        if (samplingFrequency == 3) {
            this.mh.logResult(MPA01E005, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            throw new CannotContinueValidationException();
        }
        int sampleRate = HeaderParser.samplingFrequency[samplingFrequency];
        if (!this.s.isAlgorithmBit())
            sampleRate /= 2;
        this.s.setSamplingFrequency(sampleRate);
        this.mh.logAll(MPA01I008, this.s.getSamplingFrequency());
    }
        
    private void validatePaddingBit(int paddingBit) {
        this.s.setPaddingBit(paddingBit != 0);
    }

    private void validateMode(int mode) {
        switch (mode) {
        case 0:
            this.s.setMode(State.Mode.STEREO);
            break;
        case 1:
            this.s.setMode(State.Mode.JOINT_STEREO);
            break;
        case 2:
            this.s.setMode(State.Mode.DUAL_CHANNEL);
            break;
        case 3:
            this.s.setMode(State.Mode.SINGLE_CHANNEL);
            break;
        }
        this.mh.logAll(MPA01I009, this.s.getMode().toString().toLowerCase());           
    }

    private void validateModeExtension(int modeExtension) {
        if (this.s.getLayer() == State.Layer.LAYER_I || this.s.getLayer() == State.Layer.LAYER_II) {
            switch (modeExtension) {
            case 0:
                this.mh.logAll(MPA01I010, 4);
                break;
            case 1:
                this.mh.logAll(MPA01I010, 8);
                break;
            case 2:
                this.mh.logAll(MPA01I010, 12);
                break;
            case 3:
                this.mh.logAll(MPA01I010, 16);
                break;
            }
        } else if (this.s.getLayer() == State.Layer.LAYER_III) {
            switch (modeExtension) {
            case 0:
                this.mh.logAll(MPA01I011, false, false);
                break;
            case 1:
                this.mh.logAll(MPA01I011, true, false);
                break;
            case 2:
                this.mh.logAll(MPA01I011, false, true);
                break;
            case 3:
                this.mh.logAll(MPA01I011, true, true);
                break;
            }
        }
    }

    private void validateEmphasis(int emphasis) {
        switch (emphasis) {
        case 0:
            this.mh.logAll(MPA01I014, "none");
            break;
        case 1:
            this.mh.logAll(MPA01I014, "50/15 microseconds");
            break;
        case 2:
            this.mh.logResult(MPA01E006, this.s.getFrameCount(), this.bis.getTotalBytesRead());
            break;
        case 3:
            this.mh.logAll(MPA01I014, "CCITT J.17");
            break;
        }
    }
}
