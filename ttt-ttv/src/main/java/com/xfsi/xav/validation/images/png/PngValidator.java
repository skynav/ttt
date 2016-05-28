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

import java.io.InputStream;
import java.util.Map;

import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Progress;
import com.xfsi.xav.util.Result;
import com.xfsi.xav.util.property.PropertyMessageKey;
import com.xfsi.xav.validation.validator.AbstractValidator;

public final class PngValidator extends AbstractValidator
{

    static enum MsgCode
    {
        PNG01F001,
        PNG01F002,
        PNG01F003,
        PNG01F004,
        PNG01F005,
        PNG01F006,
        PNG01E003,
        PNG01E004,
        PNG01E005,
        PNG01E006,
        PNG01E007,
        PNG01E008,
        PNG01E009,
        PNG01E010,
        PNG01E011,
        PNG01E016,
        PNG01E017,
        PNG01E018,
        PNG01E019,
        PNG01E020,
        PNG01E021,
        PNG01E022,
        PNG01E023,
        PNG01E024,
        PNG01E025,
        PNG01E026,
        PNG01E027,
        PNG01E028,
        PNG01E029,
        PNG01E030,
        PNG01E031,
        PNG01E032,
        PNG01E033,
        PNG01E034,
        PNG01E035,
        PNG01E036,
        PNG01E037,
        PNG01E038,
        PNG01E039,
        PNG01E040,
        PNG01E041,
        PNG01E042,
        PNG01E043,
        PNG01E044,
        PNG01E045,
        PNG01E046,
        PNG01E047,
        PNG01I001,
        PNG01I002,
        PNG01I003,
        PNG01I004,
        PNG01I005,
        PNG01I006,
        PNG01I007,
        PNG01I008,
        PNG01I009,
        PNG01I010,
        PNG01I011,
        PNG01I012,
        PNG01I013,
        PNG01I014,
        PNG01I015,
        PNG01I016,
        PNG01I017,
        PNG01I018,
        PNG01I019,
        PNG01I020,
        PNG01I021,
        PNG01I022,
        PNG01I023,
        PNG01I024,
        PNG01I025,
        PNG01I026,
        PNG01I027,
        PNG01I028,
        PNG01I029,
        PNG01I030,
        PNG01I031,
        PNG01I032,
        PNG01I033,
        PNG01W001,
        PNG01W002,
        PNG01W003,
        PNG01W004,
        PNG01W005,
        PNG01W006,
        PNG01W007,
        PNG01W009,
        PNG01W010,
        PNG01W011,
        PNG01W012,
        PNG01W013,
        PNG01W014,
        PNG01W015,
        PNG01W016,
        PNG01W017,
        PNG01X001,
        PNG01X002,
        PNG01X003,
    }

    public static final class Spec {
        public static final class Signature {
            public static final byte length = 8;
            static final byte[] data = {(byte)137, 80, 78, 71, 13, 10, 26, 10};
        }

        static ChunkState[] initializeChunkStates() {
            ChunkState[] cs = {
                new ChunkState(ChunkValidatorIHDR.Spec.header, ChunkState.StateType.mustExist,  false),
                new ChunkState(ChunkValidatorPLTE.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorIDAT.Spec.header, ChunkState.StateType.mustExist,  true),
                new ChunkState(ChunkValidatorIEND.Spec.header, ChunkState.StateType.mustExist,  false),
                new ChunkState(ChunkValidatorcHRM.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorgAMA.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatoriCCP.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorsBIT.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorsRGB.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorbKGD.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorhIST.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatortRNS.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorpHYs.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatorsPLT.Spec.header, ChunkState.StateType.mayExist,   true),
                new ChunkState(ChunkValidatortIME.Spec.header, ChunkState.StateType.mayExist,   false),
                new ChunkState(ChunkValidatoriTXt.Spec.header, ChunkState.StateType.mayExist,   true),
                new ChunkState(ChunkValidatortEXt.Spec.header, ChunkState.StateType.mayExist,   true),
                new ChunkState(ChunkValidatorzTXt.Spec.header, ChunkState.StateType.mayExist,   true)
            };
            return cs;
        }
    }

    private InputStream data;
    private byte[] currentChunkType;
    private int currentChunkIndex = -1;
    private int currentBytesRead = -1;
    private ChunkState[] chunkStates;
    private byte[] lastChunkType;
    private byte colorType = -1;
    private byte bitDepth = -1;
    private TestManager tm;
    private TestInfo ti;
    private int totalMsgs;
    private int totalErrorMsgs;

    public PngValidator() {
        chunkStates = new ChunkState[0];
        data = null;
    }

    public Result run(TestManager tm, TestInfo ti) {
        try {
            super.initState(tm, ti);
            InputStream is = ti.getResourceStream();
            Map<String,Object> resultState = new java.util.HashMap<String,Object>();
            Result r = validate(is, tm, ti, resultState) ? Result.PASS : Result.FAIL;
            return new Result(r, resultState);
        } catch (PngValidationException e) {
            tm.reportProgress(ti, new Progress(Error.Severity.INFO, "Unable to continue PNG validation: " + e.getMessage() + " issues detected"));
            return Result.FAIL;
        } catch (Exception e) {
            tm.reportProgress(ti, new Progress(Error.Severity.FATAL,"Internal PNG validation error: " + e));
            return Result.FAIL;
        }
    }

    public String getVersion() {
        return "1.0.0";
    }

    public boolean validate(InputStream is) throws PngValidationException {
        return validate(is, null, null, null);
    }

    public boolean validate(InputStream is, TestManager tm, TestInfo ti, Map<String,Object> resultState) throws PngValidationException {
        this.tm = tm;
        this.ti = ti;
        this.data = is;
        this.currentChunkType = null;
        this.currentChunkIndex = 0;
        this.currentBytesRead = 0;
        this.totalMsgs = 0;
        this.totalErrorMsgs = 0;
        this.lastChunkType = null;
        this.colorType = -1;
        this.bitDepth = -1;
        this.chunkStates = Spec.initializeChunkStates();
        if (this.data == null)
            logMsg(PngValidator.MsgCode.PNG01X003, null);
        validateSignature();
        readChunks(resultState);
        return this.totalErrorMsgs == 0;
    }

    ChunkState getChunkState(byte[] chunkType) throws PngValidationException {
        for (int i = 0; i < chunkStates.length; i++) {
            if (Utils.compareEqual(chunkStates[i].getType(), chunkType, ChunkValidator.Spec.Props.typeSize))
                return chunkStates[i];
        }
        logMsg(PngValidator.MsgCode.PNG01X001, null, new String(chunkType, Utils.getCharset()));
        throw new PngValidationException(String.valueOf(this.totalMsgs));
    }

    byte[] getLastChunkType() {
        return this.lastChunkType;
    }

    boolean assertStreamEnd() {
        return readBytes(1) == null;
    }

    void setColorType(byte colorType) {
        this.colorType = colorType;
    }

    byte getColorType() {
        return this.colorType;
    }

    void setBitDepth(byte bitDepth) {
        this.bitDepth = bitDepth;
    }

    byte getBitDepth() {
        return this.bitDepth;
    }

    int getCurrentChunkIndex() {
        return this.currentChunkIndex;
    }

    void logMsg(PngValidator.MsgCode code, String section, Object... params) throws PngValidationException {
        totalMsgs++;
        String msgKey = code.toString();
        Error.Severity severityType;
        try {
            PropertyMessageKey parsedKey = parseKey(msgKey);
            severityType = parsedKey.getSeverity();
        } catch (PropertyMessageKey.MalformedKeyException e) {
            // TBD: throw an internal error?
            severityType = Error.Severity.FATAL;
        }
        // TODO: check filtering before formatting message
        switch (severityType.getSeverity())
            {
            case Error.Severity.ERROR_SEVERITY:
            case Error.Severity.FATAL_SEVERITY:
                this.totalErrorMsgs++;
                break;
            default:
                break;
            }
        boolean consoleOutput = (tm == null || ti == null);
        if (! isResultFiltered(tm, msgKey, severityType)) {
            String msg = this.msg(msgKey);
            String ref = this.properties.getProperty(String.format("ref.%1$s", msgKey));
            String sec;
            if (section != null)
                sec = section;
            else
                sec = this.properties.getProperty(String.format("sec.%1$s", msgKey));
            if (ref == null)
                ref =  "";
            if (sec != null)
                ref += "; " + sec;
            String template = "";
            String currChunkType;
            if (this.currentChunkType != null) {
                currChunkType = new String(this.currentChunkType, Utils.getCharset());
                if (consoleOutput) {
                    template = "  Chunk: %1$s\n  %5$s: %2$s\n  Reference: %6$s\n  Bytes read: %3$d\n  Chunks processed: %4$d\n";
                } else {
                    if (severityType != Error.Severity.INFO)
                        template = this.properties.getProperty("ErrorLogTemplate");
                    else
                        template = this.properties.getProperty("LogTemplate");
                }
            } else {
                currChunkType = "";
                if (consoleOutput) {
                    template = "  %5$s: %2$s\n  Reference: %6$s\n  Bytes read: %3$d\n  Chunks processed: %4$d\n";
                } else {
                    if (severityType != Error.Severity.INFO)
                        template = this.properties.getProperty("ErrorLogTemplateNoChunk");
                    else
                        template = this.properties.getProperty("LogTemplateNoChunk");
                }
            }
            msg = String.format(msg, params);
            if (consoleOutput) {
                String out = String.format(template, currChunkType, msg, this.currentBytesRead, this.currentChunkIndex,
                                           severityType.toString().toUpperCase(), ref);
                System.out.println(out);
            } else {
                String out = String.format(template, currChunkType, msg, this.currentBytesRead, this.currentChunkIndex);
                String cat = this.properties.getProperty(String.format("cat.%1$s", msgKey));
                Error.Category category = Error.Category.VALIDITY; // default
                if (cat != null) {
                    if (cat.equalsIgnoreCase(Error.Category.COVERAGE.toString()))
                        category = Error.Category.COVERAGE;
                    else if (cat.equalsIgnoreCase(Error.Category.EFFICIENCY.toString()))
                        category = Error.Category.EFFICIENCY;
                    else if (cat.equalsIgnoreCase(Error.Category.INTERNAL.toString()))
                        category = Error.Category.INTERNAL;
                    else if (cat.equalsIgnoreCase(Error.Category.INTEROPERABILITY.toString()))
                        category = Error.Category.INTEROPERABILITY;
                    else if (cat.equalsIgnoreCase(Error.Category.OTHER.toString()))
                        category = Error.Category.OTHER;
                    else if (cat.equalsIgnoreCase(Error.Category.SECURITY.toString()))
                        category = Error.Category.SECURITY;
                }
                this.tm.reportError(this.ti, makeError (msgKey, category, ref, out));
            }
        } else {
            // Message was filtered
            if (consoleOutput)
                System.out.println("Message " + msgKey + " was filtered");
        }
        checkTermination(code);
    }

    private Error makeError (String messageKey, Error.Category category, String reference, String message) {
        return error(tm, Error.TestType.STATIC, category, Error.ContentType.IMAGE_PNG, Error.Reference.OTHER, reference, message, messageKey);
    }

    private void checkTermination(PngValidator.MsgCode msgType) throws PngValidationException {
        if (msgType == MsgCode.PNG01X003 ||
            msgType == MsgCode.PNG01F001 ||
            msgType == MsgCode.PNG01F002 ||
            msgType == MsgCode.PNG01F003 ||
            msgType == MsgCode.PNG01F004 ||
            msgType == MsgCode.PNG01F005 ||
            msgType == MsgCode.PNG01F006) {
            throw new PngValidationException(String.valueOf(this.totalErrorMsgs));
        }
    }

    private byte[] readBytes(int numBytes) {
        byte[] s = null;
        if (data != null) {
            try {
                s = new byte[numBytes];
                int bytesRead = data.read(s, 0, s.length);
                if (bytesRead != s.length)
                    s = null;
                if (bytesRead > 0)
                    currentBytesRead += bytesRead;
            } catch (OutOfMemoryError bounded) {
            } catch (Exception e) {
            }
        }
        return s;
    }

    private void validateSignature() throws PngValidationException {
        byte[] s = readBytes(Spec.Signature.length);
        if (s != null) {
            int i= Utils.findMismatchIndex(s, Spec.Signature.data, Spec.Signature.length);
            if (i >= 0)
                this.logMsg(PngValidator.MsgCode.PNG01F002, null, i, Spec.Signature.data[i], s[i]);
            else
                logMsg(PngValidator.MsgCode.PNG01I001, null);
        } else
            logMsg(PngValidator.MsgCode.PNG01F001, null);
    }

    private void readChunks(Map<String,Object> resultState) throws PngValidationException {
        boolean endFound = false;
        while (!endFound) {
            byte[] length = readChunkLength();
            byte[] type = readChunkType();
            validateType(type);
            byte[] data = readChunkData(length);
            byte[] crc = readChunkCrc();
            validateCrc(type, data, crc);
            endFound = dispatchChunkValidator(type, data, resultState);
            currentChunkType = null;
        }
        assertRequiredChunksFound();
    }

    private byte[] readChunkLength() throws PngValidationException {
        byte[] length = readBytes(ChunkValidator.Spec.Props.lengthSize);
        if (length == null)
            logMsg(PngValidator.MsgCode.PNG01F003, null);
        return length;
    }

    private byte[] readChunkType() throws PngValidationException {
        byte[] type = readBytes(ChunkValidator.Spec.Props.typeSize);
        if (type == null)
            logMsg(PngValidator.MsgCode.PNG01F004, null);
        return type;
    }

    private byte[] readChunkData(byte[] length) throws PngValidationException {
        int l = Utils.convertToInt(length, ChunkValidator.Spec.Props.lengthSize);
        byte[] data = readBytes(l);
        if (data == null)
            logMsg(PngValidator.MsgCode.PNG01F005, null, l);
        return data;
    }

    private byte[] readChunkCrc() throws PngValidationException {
        byte[] crc = readBytes(ChunkValidator.Spec.Props.crcSize);
        if (crc == null)
            logMsg(PngValidator.MsgCode.PNG01F006, null);
        return crc;
    }

    private boolean dispatchChunkValidator(byte[] type, byte[] data, Map<String,Object> resultState) throws PngValidationException {
        try {
            Class<?> c = Class.forName(ChunkValidator.class.getName() + new String(type, Utils.getCharset()));
            ChunkValidator cv = (ChunkValidator) c.newInstance();
            cv.initialize(this, data, resultState);
            cv.validate();
        } catch (ClassNotFoundException e) {
            processNonStandardChunk(type);
        } catch (InstantiationException e) {
            logMsg(PngValidator.MsgCode.PNG01X002, null, new String(type, Utils.getCharset()));
            throw new PngValidationException(String.valueOf(this.totalMsgs));
        } catch (IllegalAccessException e) {
            logMsg(PngValidator.MsgCode.PNG01X002, null, new String(type, Utils.getCharset()));
            throw new PngValidationException(String.valueOf(this.totalMsgs));
        }
        lastChunkType = type;
        currentChunkIndex++;
        return Utils.compareEqual(type, ChunkValidatorIEND.Spec.header, ChunkValidator.Spec.Props.typeSize);
    }

    private void assertRequiredChunksFound() throws PngValidationException {
        for (int i = 0; i < chunkStates.length; i++) {
            ChunkState cs = chunkStates[i];
            if (cs.mustExist()) {
                if (cs.getCount() == 0)
                    logMsg(PngValidator.MsgCode.PNG01E016, null, new String(cs.getType(), Utils.getCharset()));
                else
                    logMsg(PngValidator.MsgCode.PNG01I005, null, new String(cs.getType(), Utils.getCharset()));
            }
            if (cs.mustNotExist()) {
                if (cs.getCount() > 0)
                    logMsg(PngValidator.MsgCode.PNG01W002, null, new String(cs.getType(), Utils.getCharset()));
                else
                    logMsg(PngValidator.MsgCode.PNG01I006, null, new String(cs.getType(), Utils.getCharset()));
            }
        }
    }

    private void validateType(byte[] type) throws PngValidationException {
        boolean valid = true;
        char c;
        for (int i = 0; i < type.length; i++) {
            c = (char) type[i];
            if (!Character.isLetterOrDigit(c)) {
                logMsg(PngValidator.MsgCode.PNG01E034, null, c);
                valid = false;
            }
        }
        if (valid) {
            currentChunkType = type;
            logMsg(PngValidator.MsgCode.PNG01I002, null);
        }
    }

    private void validateCrc(byte[] type, byte[] data, byte[] crc) throws PngValidationException {
        int expected = Utils.updateCrc(0xffffffff, type, type.length);
        expected = Utils.updateCrc(expected, data, data.length) ^ 0xffffffff;
        int actual = Utils.convertToInt(crc, crc.length);
        if (actual != expected)
            logMsg(PngValidator.MsgCode.PNG01E019, null, expected, actual);
        else
            logMsg(PngValidator.MsgCode.PNG01I003, null, actual);
    }

    private void processNonStandardChunk(byte[] type) throws PngValidationException {
        logMsg(PngValidator.MsgCode.PNG01W003, null, new String(type, Utils.getCharset()));
        if (Character.isLowerCase((char) type[1]))
            logMsg(PngValidator.MsgCode.PNG01W001, null, new String(type, Utils.getCharset()));
        else
            logMsg(PngValidator.MsgCode.PNG01I004, null, new String(type, Utils.getCharset()));
    }

}
