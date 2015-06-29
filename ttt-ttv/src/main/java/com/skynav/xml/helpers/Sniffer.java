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

package com.skynav.xml.helpers;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.IllegalCharsetNameException;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Collections;
import java.util.List;

public class Sniffer {

    private static final List<Object[]> bomList;

    static {
        List<Object[]> l = new java.util.ArrayList<Object[]>();
        try {
            l.add(new Object[] { new int[] { 0x00, 0x00, 0xFE, 0xFF }, Charset.forName("UTF-32BE") });
            l.add(new Object[] { new int[] { 0xFF, 0xFE, 0x00, 0x00 }, Charset.forName("UTF-32LE") });
            l.add(new Object[] { new int[] { 0xEF, 0xBB, 0xBF }, Charset.forName("UTF-8") });
            l.add(new Object[] { new int[] { 0xFE, 0xFF }, Charset.forName("UTF-16BE") });
            l.add(new Object[] { new int[] { 0xFF, 0xFE }, Charset.forName("UTF-16LE") });
        } catch (RuntimeException e) {
        }
        bomList = Collections.unmodifiableList(l);
    }

    private Sniffer() {
    }

    public static Charset sniff(ByteBuffer bb, Charset defaultCharset) {
        return sniff(bb, defaultCharset, null);
    }

    public static Charset sniff(ByteBuffer bb, Charset defaultCharset, Object[] outputParameters) {
        int restore = bb.position();
        Charset sniffedCharset;
        Charset bomCharset = checkForBOMCharset(bb, outputParameters);
        Charset encodingCharset = checkForXMLEncodingCharset(bb, bomCharset, outputParameters);
        if (bomCharset != null) {
            if (encodingCharset != null) {
                if (encodingCharset.equals(bomCharset))
                    sniffedCharset = encodingCharset;
                else
                    sniffedCharset = bomCharset;
            } else
                sniffedCharset = bomCharset;
        } else {
            if (encodingCharset != null)
                sniffedCharset = encodingCharset;
            else
                sniffedCharset = defaultCharset;
        }
        bb.position(restore);
        return sniffedCharset;
    }

    public static Charset checkForBOMCharset(ByteBuffer bb, Object[] outputParameters) {
        for (Object[] bomEntry : bomList) {
            int[] bom = (int[]) bomEntry[0];
            if (bom.length > bb.limit())
                return null;
        }
        int restore = bb.position();
        for (Object[] bomEntry : bomList) {
            int[] bom = (int[]) bomEntry[0];
            bb.rewind();
            assert bb.limit() >= bom.length;
            int i = 0;
            while (i < bom.length)
                if ((bb.get() & 0xFF) != bom[i++])
                    break;
            if (i == bom.length) {
                if (outputParameters != null) {
                    if (outputParameters.length > 0)
                        outputParameters[0] = Integer.valueOf(bom.length);
                }
                return (Charset) bomEntry[1];
            }
        }
        bb.position(restore);
        return null;
    }

    private static boolean isXMLEncodingNameInitial(int c) {
        return ((c >= (int) 'A') && (c <= (int) 'Z')) || ((c >= (int) 'a') && (c <= (int) 'z'));
    }

    private static boolean isXMLEncodingNameFollowing(int c) {
        return isXMLEncodingNameInitial(c) || ((c >= (int) '0') && (c <= (int) '9')) || (c == '.') || (c == '_') || (c == '-');
    }

    private static String checkXMLEncodingName(String encoding) {
        if (encoding.length() == 0)
            return null;
        else {
            if (!isXMLEncodingNameInitial(encoding.charAt(0)))
                return null;
            for (int i = 1; i < encoding.length(); ++i) {
                if (!isXMLEncodingNameFollowing(encoding.charAt(i)))
                    return null;
            }
            return encoding;
        }
    }

    private static String extractXMLEncoding(CharBuffer cb, int start, int end) {
        StringBuffer sb = new StringBuffer();
        cb.position(start);
        while (cb.position() < end)
            sb.append(cb.get());
        return checkXMLEncodingName(sb.toString());
    }

    private static String parseXMLEncoding(CharBuffer cb) {
        int restore = cb.position();
        skipSpace(cb);
        if (!match(cb, "=")) {
            cb.position(restore);
            return null;
        }
        skipSpace(cb);
        char quote;
        if (match(cb, "\""))
            quote = '"';
        else if (match(cb, "\'"))
            quote = '\'';
        else {
            cb.position(restore);
            return null;
        }
        int encodingStart = cb.position();
        if (!find(cb, new String(new char[]{quote}))) {
            cb.position(restore);
            return null;
        }
        int encodingEnd = cb.position();
        return extractXMLEncoding(cb, encodingStart, encodingEnd);
    }

    private static String findXMLEncoding(CharBuffer cb) {
        String encoding;
        int restore = cb.position();
        if (find(cb, "encoding"))
            encoding = parseXMLEncoding(cb);
        else
            encoding = null;
        if (encoding == null)
            cb.position(restore);
        return encoding;
    }

    private static final int[] encoding8 = new int[] {
        0x65, // 'e'
        0x6E, // 'n'
        0x63, // 'c'
        0x6F, // 'o'
        0x64, // 'd'
        0x69, // 'i'
        0x6E, // 'n'
        0x67  // 'g'
    };

    private static final int[] encoding16be = new int[] {
        0x00,
        0x65, // 'e'
        0x00,
        0x6E, // 'n'
        0x00,
        0x63, // 'c'
        0x00,
        0x6F, // 'o'
        0x00,
        0x64, // 'd'
        0x00,
        0x69, // 'i'
        0x00,
        0x6E, // 'n'
        0x00,
        0x67  // 'g'
    };

    private static final int[] encoding16le = new int[] {
        0x65, // 'e'
        0x00,
        0x6E, // 'n'
        0x00,
        0x63, // 'c'
        0x00,
        0x6F, // 'o'
        0x00,
        0x64, // 'd'
        0x00,
        0x69, // 'i'
        0x00,
        0x6E, // 'n'
        0x00,
        0x67, // 'g'
        0x00,
    };

    private static final int[] encoding32be = new int[] {
        0x00,
        0x00,
        0x00,
        0x65, // 'e'
        0x00,
        0x00,
        0x00,
        0x6E, // 'n'
        0x00,
        0x00,
        0x00,
        0x63, // 'c'
        0x00,
        0x00,
        0x00,
        0x6F, // 'o'
        0x00,
        0x00,
        0x00,
        0x64, // 'd'
        0x00,
        0x00,
        0x00,
        0x69, // 'i'
        0x00,
        0x00,
        0x00,
        0x6E, // 'n'
        0x00,
        0x00,
        0x00,
        0x67  // 'g'
    };

    private static final int[] encoding32le = new int[] {
        0x65, // 'e'
        0x00,
        0x00,
        0x00,
        0x6E, // 'n'
        0x00,
        0x00,
        0x00,
        0x63, // 'c'
        0x00,
        0x00,
        0x00,
        0x6F, // 'o'
        0x00,
        0x00,
        0x00,
        0x64, // 'd'
        0x00,
        0x00,
        0x00,
        0x69, // 'i'
        0x00,
        0x00,
        0x00,
        0x6E, // 'n'
        0x00,
        0x00,
        0x00,
        0x67, // 'g'
        0x00,
        0x00,
        0x00
    };

    private static String extractXMLEncoding(ByteBuffer bb, int start, int end, int codeLength, boolean bigEndian) {
        StringBuffer sb = new StringBuffer();
        int restore = bb.position();
        bb.position(start);
        while (bb.position() < end) {
            if (bigEndian)
                if (!matchByte(bb, 0, codeLength - 1))
                    break;
            sb.append((char) bb.get());
            if (!bigEndian)
                if (!matchByte(bb, 0, codeLength - 1))
                    break;
        }
        if (bb.position() == end)
            return checkXMLEncodingName(sb.toString());
        else {
            bb.position(restore);
            return null;
        }
    }

    private static String parseXMLEncoding(ByteBuffer bb, int codeLength, boolean bigEndian) {
        int restore = bb.position();
        skipSpace(bb, codeLength, bigEndian);
        if (!matchExtended(bb, '=', codeLength, bigEndian)) {
            bb.position(restore);
            return null;
        }
        skipSpace(bb, codeLength, bigEndian);
        char quote;
        if (matchExtended(bb, '"', codeLength, bigEndian))
            quote = '"';
        else if (matchExtended(bb, '\'', codeLength, bigEndian))
            quote = '\'';
        else {
            bb.position(restore);
            return null;
        }
        int encodingStart = bb.position();
        if (!findExtended(bb, quote, codeLength, bigEndian)) {
            bb.position(restore);
            return null;
        }
        int encodingEnd = bb.position() - codeLength;
        return extractXMLEncoding(bb, encodingStart, encodingEnd, codeLength, bigEndian);
    }

    // Either no BOM is present or it wasn't recognized.
    private static String findXMLEncoding(ByteBuffer bb) {
        String encoding;
        int restore = bb.position();
        if (findFrom(bb, 0, encoding8))
            encoding = parseXMLEncoding(bb, 1, true);
        else {
            byte[] bytes;
            if (bb.hasArray())
                bytes = bb.array();
            else {
                bytes = new byte[4];
                bb.position(0);
                bb.get(bytes);
                bb.position(0);
            }
            if (bytes.length < 4) {
                encoding = null;
            } else if ((bytes[0] == '<') && (bytes[1] == 0) && (bytes[2] == 0) && (bytes[3] == 0)) {
                if (findFrom(bb, 0, encoding32le))
                    encoding = parseXMLEncoding(bb, 4, false);
                else
                    encoding = null;
            } else if ((bytes[0] == '<') && (bytes[1] == 0)) {
                if (findFrom(bb, 0, encoding16le))
                    encoding = parseXMLEncoding(bb, 2, false);
                else
                    encoding = null;
            } else if ((bytes[0] == 0) && (bytes[1] == 0) && (bytes[2] == 0) && (bytes[3] == '<')) {
                if (findFrom(bb, 0, encoding32be))
                    encoding = parseXMLEncoding(bb, 4, true);
                else
                    encoding = null;
            } else if ((bytes[0] == 0) && (bytes[1] == '<')) {
                if (findFrom(bb, 0, encoding16be))
                    encoding = parseXMLEncoding(bb, 2, true);
                else
                    encoding = null;
            } else
                encoding = null;
        }
        if (encoding == null)
            bb.position(restore);
        return encoding;
    }

    private static Charset checkForXMLEncodingCharset(ByteBuffer bb, Charset bomCharset, Object[] outputParameters) {
        String encoding = null;
        if (bomCharset != null) {
            // Decode using bomCharset, then search for encoding.
            // If a BOM was present and recognized, then bb.position() should be immediately
            // following the BOM. We will create a new ByteBuffer containing the bytes that
            // follow the BOM, if present, up to a maximum of 256 bytes from which we scan
            // for the XML encoding.
            int limitOld = bb.limit();
            int limitNew = Math.min(limitOld,256);
            ByteBuffer bbNew = ByteBuffer.allocate(limitNew - bb.position());
            if (limitNew < bb.limit())
                bb.limit(limitNew);
            bbNew.put(bb);
            bbNew.rewind();
            CharBuffer cb = bomCharset.decode(bbNew);
            encoding = findXMLEncoding(cb);
            // Restore prior limit.
            bb.limit(limitOld);
        } else
            encoding = findXMLEncoding(bb);
        if (encoding != null) {
            try {
                return Charset.forName(encoding);
            } catch (IllegalCharsetNameException e) {
            } catch (UnsupportedCharsetException e) {
            }
        }
        return null;
    }

    private static boolean match(ByteBuffer bb, int[] matchBytes) {
        int restore = bb.position();
        int i = 0;
        int n = matchBytes.length;
        for (; i < n; ++i) {
            if (bb.get() != matchBytes[i])
                break;
        }
        if (i == n)
            return true;
        else {
            bb.position(restore);
            return false;
        }
    }

    private static boolean matchExtended(ByteBuffer bb, int byteValue, int codeLength, boolean bigEndian) {
        int[] matchBytes = new int[codeLength];
        if (bigEndian)
            matchBytes[matchBytes.length - 1] = byteValue;
        else
            matchBytes[0] = byteValue;
        return match(bb, matchBytes);
    }

    private static boolean find(ByteBuffer bb, int[] matchBytes) {
        if (matchBytes.length == 0)
            return true;
        for (; bb.position() < bb.limit(); bb.get()) {
            if (match(bb, matchBytes))
                return true;
        }
        return false;
    }

    private static boolean findFrom(ByteBuffer bb, int offset, int[] matchBytes) {
        if (offset < 0)
            offset = 0;
        if (offset + matchBytes.length > bb.limit())
            return false;
        bb.position(offset);
        return find(bb, matchBytes);
    }

    private static boolean findExtended(ByteBuffer bb, int byteValue, int codeLength, boolean bigEndian) {
        int[] matchBytes = new int[codeLength];
        if (bigEndian)
            matchBytes[matchBytes.length - 1] = byteValue;
        else
            matchBytes[0] = byteValue;
        return find(bb, matchBytes);
    }

    private static boolean match(CharBuffer cb, String matchString) {
        int restore = cb.position();
        int i = 0;
        int n = matchString.length();
        for (; i < n; ++i) {
            if (cb.get() != matchString.charAt(i))
                break;
        }
        if (i == n)
            return true;
        else {
            cb.position(restore);
            return false;
        }
    }

    private static boolean find(CharBuffer cb, String matchString) {
        if (matchString.length() == 0)
            return true;
        for (; cb.position() < cb.limit(); cb.get()) {
            if (match(cb, matchString))
                return true;
        }
        return false;
    }

    private static boolean isXMLSpace(char c) {
        return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
    }

    private static boolean isXMLSpace(byte b) {
        return (b == 0x20) || (b == 0x09) || (b == 0x0A) || (b == 0x0D);
    }

    private static boolean matchByte(ByteBuffer bb, int byteValue, int count) {
        if (count < 1)
            return true;
        int restore = bb.position();
        int i = 0;
        int n = count;
        for (; i < n; ++i) {
            if (bb.get() != byteValue)
                break;
        }
        if (i == n)
            return true;
        else {
            bb.position(restore);
            return false;
        }
    }

    private static void skipSpace(ByteBuffer bb, int codeLength, boolean bigEndian) {
        while (bb.position() < bb.limit()) {
            int restore = bb.position();
            if (bigEndian && !matchByte(bb, 0, codeLength - 1)) {
                bb.position(restore);
                break;
            }
            if (!isXMLSpace(bb.get())) {
                bb.position(restore);
                break;
            }
            if (!bigEndian && !matchByte(bb, 0, codeLength - 1)) {
                bb.position(restore);
                break;
            }
        }
    }

    private static void skipSpace(CharBuffer cb) {
        while (cb.position() < cb.limit()) {
            int restore = cb.position();
            char c = cb.get();
            if (!isXMLSpace(c)) {
                cb.position(restore);
                break;
            }
        }
    }

}
