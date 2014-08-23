/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.verifier.util;

import java.nio.ByteBuffer;

public class Base64 {

    private static byte[] decodeMap;
    private static byte[] getDecodeMap() {
        if (decodeMap == null)
            decodeMap = makeDecodeMap();
        return decodeMap;
    }
    private static final byte PAD = 127;
    private static byte[] makeDecodeMap() {
        byte[] map = new byte[128];
        for (int i = 0; i < 128; ++i)
            map[i] = -1;
        for (int i = 'A'; i <= 'Z'; ++i)
            map[i] = (byte) (i - 'A');
        for (int i = 'a'; i <= 'z'; ++i)
            map[i] = (byte) (i - 'a' + 26);
        for (int i = '0'; i <= '9'; ++i)
            map[i] = (byte) (i - '0' + 52);
        map['+'] = 62;
        map['/'] = 63;
        map['='] = PAD;
        return map;
    }

    public static byte[] decode(String data) {
        final ByteBuffer bb = ByteBuffer.allocate(data.length());
        final byte[] map = getDecodeMap();
        final byte[] grp = new byte[4];
        for (int i = 0, n = data.length(); i < n; ) {
            int k = 0;
            while (k < 4) {
                if (i < n) {
                    char c = data.charAt(i++);
                    if (Characters.isXMLSpace(c))
                        continue;
                    else if (c < 128) {
                        byte bits = map[c];
                        if (bits == -1) {
                            throw new IllegalArgumentException("Character '" + Characters.maybeEscapeAsNCRef(c) + "' is not in Base64 alphabet.");
                        } else if ((bits == PAD) && (k == 0)) {
                            throw new IllegalArgumentException("Padding not permitted in first position of group.");
                        } else if ((bits == PAD) && (k == 1)) {
                            throw new IllegalArgumentException("Padding not permitted in second position of group.");
                        } else {
                            grp[k++] = bits;
                        }
                    } else {
                        throw new IllegalArgumentException("Character '" + Characters.maybeEscapeAsNCRef(c) + "' is not in Base64 alphabet.");
                    }
                } else if (k > 0) {
                    throw new IllegalArgumentException("Input data underflow, got " + k + " of four (4) Base64 alphabet characters.");
                } else {
                    break;
                }
            }
            if (k == 4) {
                bb.put((byte)((grp[0] << 2) | (grp[1] >> 4)));
                if (grp[2] != PAD)
                    bb.put((byte)((grp[1] << 4) | (grp[2] >> 2)));
                if (grp[3] != PAD)
                    bb.put((byte)((grp[2] << 6) | grp[3]));
            }
        }
        byte[] out = new byte[bb.position()];
        bb.rewind();
        bb.get(out);
        return out;
    }

    public static boolean isBase64(String data) {
        try {
            decode(data);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
