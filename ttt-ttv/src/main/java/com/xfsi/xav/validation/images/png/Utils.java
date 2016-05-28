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

import java.nio.charset.Charset;

final public class Utils {

    private static final Charset charset;
    static {
        Charset cs;
        try {
            cs = Charset.forName("ISO-8859-1");
        } catch (IllegalArgumentException e) {
            cs = Charset.defaultCharset();
        }
        charset = cs;
    }

    static Charset getCharset() {
        return charset;
    }

    static private int[] crcTable;

    static public int updateCrc(int crc, byte buf[], int len) {
        return updateCrc(crc, buf, 0, len);
    }

    static public int updateCrc(int crc, byte buf[], int index, int len) {
        if (crcTable == null)
            makeCrcTable();
        int c = crc;
        int l = index + len;
        for (int n = index; n < l; n++)
            c = crcTable[(c ^ buf[n]) & 0xff] ^ (c >>> 8);
        return c;
    }

    static boolean compareEqual(byte[] b1, byte[] b2, int length) {
        return findMismatchIndex(b1, b2, length) < 0;
    }

    static int convertToInt(byte[] sequence, int numBytes) {
        return convertToInt(sequence, 0, numBytes);
    }

    static int convertToInt(byte[] sequence, int index, int numBytes) {
        int r = 0;
        int shift = 0;
        for (int i = (index + numBytes) - 1; i >= index; i--) {
            r += ((sequence[i] & 0xFF) << shift);
            shift += 8;
        }
        return r;
    }

    static int findMismatchIndex(byte[] b1, byte[] b2, int length) {
        for (int i = 0; i < length; i++) {
            if (b1[i] != b2[i])
                return i;
        }
        return -1;
    }

    static int findValidatedNameSize(byte[] data, PngValidator png, boolean checkLatin1Enc, String section) throws PngValidationException {
        byte latin1space = 32;
        int maxLength = 79;
        int i;
        boolean errorFound = false;
        for (i = 0; i < 80 && i < data.length; i++) {
            if (data[i] == 0) {
                if (i == 0) {
                    png.logMsg(PngValidator.MsgCode.PNG01E041, section, maxLength, i);
                    errorFound = true;
                }
                i++; // skip null
                break;
            }
            if (checkLatin1Enc) {
                if (!Utils.isAllowedLatin1Char(data[i])) {
                    png.logMsg(PngValidator.MsgCode.PNG01E038, section, i);
                    errorFound = true;
                }
                if (data[i] == latin1space) {
                    if (i == 0) {
                        png.logMsg(PngValidator.MsgCode.PNG01E039, section, i);
                        errorFound = true;
                    }
                    if (data[i+1] == latin1space) {
                        png.logMsg(PngValidator.MsgCode.PNG01E040, section, i);
                        errorFound = true;
                    }
                }
            }
            if (i == maxLength) {
                png.logMsg(PngValidator.MsgCode.PNG01E041, section, maxLength, i);
                errorFound = true;
            }
        }
        if (i == data.length) {
            png.logMsg(PngValidator.MsgCode.PNG01E029, section, maxLength, i);
            errorFound = true;
        }
        if (!errorFound)
            png.logMsg(PngValidator.MsgCode.PNG01I014, section);
        return i;
    }

    static boolean isAllowedLatin1Char(byte c) {
        int i = (c & 0xFF);
        return (i >= 32 && i <= 126) || (i >= 161 && i <= 255);
    }

    private static void makeCrcTable() {
        crcTable = new int[256];
        for (int n = 0; n < 256; n++) {
            int c = n;
            for (int k = 0; k < 8; k++) {
                if ((c & 1) != 0)
                    c = 0xedb88320 ^ (c >>> 1);
                else
                    c = c >>> 1;
            }
            crcTable[n] = c;
        }
    }

}
