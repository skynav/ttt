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
import java.io.InputStream;

import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Result;
import com.xfsi.xav.validation.util.AbstractLoggingValidator;
import com.xfsi.xav.validation.util.BitInputStream;
import com.xfsi.xav.validation.util.Util.AppType;

/**
 * Parses and validates MPEG audio streams
 */
public final class MpegValidator extends AbstractLoggingValidator {
    static enum MsgCode
    {
        MPA01I001,
        MPA01I002,
        MPA01I003,
        MPA01I004,
        MPA01I005,
        MPA01I006,
        MPA01I007,
        MPA01I008,
        MPA01I009,
        MPA01I010,
        MPA01I011,
        MPA01I012,
        MPA01I013,
        MPA01I014,
        MPA01I015,
        MPA01I016,
        MPA01X001,
        MPA01X002,
        MPA01X003,
        MPA01W001,
        MPA01W002,
        MPA01E001,
        MPA01E002,
        MPA01E003,
        MPA01E004,
        MPA01E005,
        MPA01E006,
        MPA01E007,
        MPA01F001,
        MPA01F002,
        MPA01F003,
        MPA01F004,
        MPA01F005,
        MPA01F006,
    }
    private BitInputStream bis = null;
    private State s = null;

    public MpegValidator() {
        super(Error.TestType.STATIC, Error.ContentType.AUDIO_MP3);
    }

    public Result run(TestManager tm, TestInfo ti) throws Exception
    {
        initState(tm,ti);
        validate();
        return getErrorReported() ? Result.FAIL : Result.PASS;
    }
        
    public String getVersion() 
    {
        return "1.0.0";
    }

    public void validate()
    {
        this.s = new State();
        try {
            InputStream is = getTestInfo().getResourceStream();
            assert(is != null) : this.msgFormatterNV(MsgCode.MPA01X002.toString());
            logAll(MsgCode.MPA01I001);
            this.bis = new BitInputStream(is);
            parseFrames();
            performOverallFinalCheck();
            assertEOF();
        } catch (CannotContinueValidationException e) {
            performOverallFinalCheck(); // TODO: once we properly parse file, this call should no longer be needed
            // Nothing to do, assumes fatal error already generated and that validation cannot continue
        } catch (AssertionError e) {
            logProgress(MsgCode.MPA01X001, e.getMessage(), this.bis.getTotalBytesRead());
        }
        logAll(MsgCode.MPA01I002);
    }

    private void parseFrames() throws CannotContinueValidationException
    {
        boolean done = false;
        while (!done)
            done = parseFrame();
    }

    private void performOverallFinalCheck()
    {
        if (getAppType() == AppType.JSR242 && this.s.getLayer3FrameCount() > 0)
            logResult(MsgCode.MPA01W001);
    }

    private boolean parseFrame() throws CannotContinueValidationException
    {
        HeaderParser hp = new HeaderParser(this.bis, this.s, this);
        boolean tagFound = false;
        int syncword = 0;
        do {
            syncword = hp.readSyncword();
            TagParser tp = new TagParser(this.bis, this.s, this);
            tagFound = tp.parse(syncword);
        } while (tagFound);
        hp.parse(syncword);
        this.s.incrementFrameCount();
        logAll(MsgCode.MPA01I016, this.s.getFrameCount());
        ErrorCheckParser ecp = new ErrorCheckParser(this.bis, this.s, this);
        ecp.parse();
        boolean done;
        switch (this.s.getLayer()) {
        case LAYER_I:
            Layer1AudioDataParser l1adp = new Layer1AudioDataParser(this.bis, this.s, this);
            done = l1adp.parse();
            break;
        case LAYER_II:
            Layer2AudioDataParser l2adp = new Layer2AudioDataParser(this.bis, this.s, this);
            done = l2adp.parse();
            break;
        case LAYER_III:
            Layer3AudioDataParser l3adp = new Layer3AudioDataParser(this.bis, this.s, this);
            done = l3adp.parse();
            break;
        default:
            done = true;
            // TODO: add internal error msg: logResult("X");
        }
        return done;
    }

    private void assertEOF()
    {
        try {
            this.bis.readBits(8);
        } catch (EOFException e) {
            return;
        } catch (IOException e) {
            assert(false) :
              this.msgFormatterNV(MsgCode.MPA01X003.toString(), Thread.currentThread().getStackTrace()[2].getMethodName(), e.getMessage());
        }
        // TODO: add msg: logResult(MsgCode.MPA01E00);
    }
}
