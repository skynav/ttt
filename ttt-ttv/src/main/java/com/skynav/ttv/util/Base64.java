/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.util;

public class Base64 {

    private static final char[] map1 = new char[64];
    static {
        int i = 0;
        for (char c = 'A'; c <= 'Z'; c++)
            map1[i++] = c;
        for (char c = 'a'; c <= 'z'; c++)
            map1[i++] = c;
        for (char c = '0'; c <= '9'; c++)
            map1[i++] = c;
        map1[i++] = '+';
        map1[i++] = '/';
    }

    private static final byte[] map2 = new byte[128];
    static {
        for (int i = 0; i < map2.length; i++)
            map2[i] = -1;
        for (int i = 0; i < 64; i++)
            map2[map1[i]] = (byte) i;
    }

    public static char[] encode(byte[] in) {
        return encode(in, 0, in.length);
    }

    public static char[] encode(byte[] in, int iOffset, int iLength) {
        int oDataLen = (4 * iLength + 2) / 3;
        int oLength = ((iLength + 2) / 3) * 4;
        char[] out = new char[oLength];
        int iIndex = iOffset;
        int iEnd = iOffset + iLength;
        int oIndex = 0;
        while (iIndex < iEnd) {
            int i0 = in[iIndex++] & 0xff;
            int i1 = iIndex < iEnd ? in[iIndex++] & 0xff : 0;
            int i2 = iIndex < iEnd ? in[iIndex++] & 0xff : 0;
            int o0 = i0 >>> 2;
            int o1 = ((i0 &   3) << 4) | (i1 >>> 4);
            int o2 = ((i1 & 0xf) << 2) | (i2 >>> 6);
            int o3 =   i2 & 0x3f;
            out[oIndex] = map1[o0];
            ++oIndex;
            out[oIndex] = map1[o1];
            ++oIndex;
            out[oIndex] = oIndex < oDataLen ? map1[o2] : '=';
            ++oIndex;
            out[oIndex] = oIndex < oDataLen ? map1[o3] : '=';
            ++oIndex;
        }
        return out;
    }

    public static byte[] decode (char[] in) {
        return decode(in, 0, in.length);
    }

    public static byte[] decode (char[] in, int iOffset, int iLength) {
        if ((iLength % 4) != 0)
            throw new IllegalArgumentException("length of base64 encoded input string is not a multiple of 4.");
        while ((iLength > 0) && (in[iOffset + iLength - 1] == '='))
            iLength--;
        int oLength = (3 * iLength) / 4;
        byte[] out = new byte[oLength];
        int iIndex = iOffset;
        int iEnd = iOffset + iLength;
        int oIndex = 0;
        while (iIndex < iEnd) {
            int i0 = in[iIndex++];
            int i1 = in[iIndex++];
            int i2 = iIndex < iEnd ? in[iIndex++] : 'A';
            int i3 = iIndex < iEnd ? in[iIndex++] : 'A';
            if ((i0 > 127) || (i1 > 127) || (i2 > 127) || (i3 > 127))
                throw new IllegalArgumentException("illegal character in base64 encoded data.");
            int b0 = map2[i0];
            int b1 = map2[i1];
            int b2 = map2[i2];
            int b3 = map2[i3];
            if ((b0 < 0) || (b1 < 0) || (b2 < 0) || (b3 < 0))
                throw new IllegalArgumentException("illegal character in base64 encoded data.");
            int o0 = ( b0        << 2) | (b1 >>> 4);
            int o1 = ((b1 & 0xf) << 4) | (b2 >>> 2);
            int o2 = ((b2 &   3) << 6) |  b3;
            out[oIndex++] = (byte) o0;
            if (oIndex < oLength) {
                out[oIndex++] = (byte) o1;
                out[oIndex++] = (byte) o2;
            }
        }
        return out;
    }

}
