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

package com.skynav.ttv.model.value;

import java.util.BitSet;

public class CharacterClass {

    public static final CharacterClass EMPTY = new CharacterClass();

    public static final char OPEN_DELIMITER    = '[';
    public static final char CLOSE_DELIMITER   = ']';
    public static final char RANGE_DELIMITER   = '-';
    public static final char ESCAPE_DELIMITER  = '\\';
    public static final char UNICODE_DELIMITER = 'u';

    private BitSet members;

    private CharacterClass() {
        this.members = new BitSet();
    }

    public CharacterClass(CharacterClass c) {
        this.members = new BitSet();
        this.add(c);
    }

    public boolean isEmpty() {
        return members.isEmpty();
    }

    public boolean inClass(int c) {
        return members.get(c);
    }

    /**
     * Add code point range [C1,C2] to members.
     * @param c1 start code point
     * @param c2 end code point (inclusive)
     */
    public void add(int c1, int c2) {
        if (this == EMPTY)
            throw new IllegalArgumentException();
        members.set(c2, c2 + 1);
    }

    /**
     * Add members of class C to this character class.
     * @param c class from which members are to be added
     */
    public void add(CharacterClass c) {
        if (this == EMPTY)
            throw new IllegalArgumentException();
        members.or(c.members);
    }

    @Override
    public int hashCode() {
        return members.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof CharacterClass) {
            CharacterClass other = (CharacterClass) o;
            return other.members.equals(members);
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(OPEN_DELIMITER);
        for (int i = members.nextSetBit(0); i >= 0; i = members.nextSetBit(i + 1))
            sb.append(toUnicodeEscape(i));
        sb.append(CLOSE_DELIMITER);
        return sb.toString();
    }

    static public CharacterClass parse(String s) {
        CharacterClass cc = new CharacterClass();
        int[] c = new int[2];
        int i = 0, k = parseDelimiter(s, i, OPEN_DELIMITER);
        if (k <= 0)
            throw new IllegalArgumentException();
        else
            i = k;
        while ((k = parseClassCharacter(s, i, c, 0)) > i) {
            i = k;
            k = parseDelimiter(s, i, RANGE_DELIMITER);
            if (k < 0)
                throw new IllegalArgumentException();
            else if (k == i)
                c[1] = c[0];
            else {
                i = k;
                if ((k = parseClassCharacter(s, i, c, 1)) <= 0)
                    throw new IllegalArgumentException();
                i = k;
            }
            cc.add(c[0], c[1]);
        }
        if (parseDelimiter(s, i, CLOSE_DELIMITER) <= 0)
            throw new IllegalArgumentException();
        return cc.isEmpty() ? EMPTY : cc;
    }

    static private int parseClassCharacter(String s, int i, int[] ca, int ci) {
        int j = parseDelimiter(s, i, ESCAPE_DELIMITER);
        if (j < 0)
            return -1;
        else if (j > i)
            return parseEscape(s, j, ca, ci);
        else
            return parseNonSpecial(s, j, ca, ci);
    }

    static private int parseEscape(String s, int i, int[] ca, int ci) {
        int j = parseDelimiter(s, i, UNICODE_DELIMITER);
        if (j < 0)
            return -1;
        else if (j > i)
            return parseUnicodeEscape(s, j, ca, ci);
        else
            return parseNonUnicodeEscape(s, j, ca, ci);
    }

    static private int parseUnicodeEscape(String s, int i, int[] ca, int ci) {
        int n = countHexDigits(s, i);
        if ((n == 4) || (n == 6))
            return parseHexDigits(s, i, n, ca, ci);
        else
            return -1;
    }

    static private String toUnicodeEscape(int c) {
        StringBuffer sb = new StringBuffer();
        sb.append(ESCAPE_DELIMITER);
        sb.append(ESCAPE_DELIMITER);
        sb.append(UNICODE_DELIMITER);
        sb.append(toHexDigits(c, c > 65535 ? 6 : 4, '0', true));
        return sb.toString();
    }

    static private int countHexDigits(String s, int i) {
        int d = 0;
        for (int j = i, n = s.length(); j < n; ++j) {
            int c = s.charAt(j);
            if (isHexDigit(c))
                ++d;
            else
                break;
        }
        return d;
    }

    static private int parseHexDigits(String s, int i, int d, int[] ca, int ci) {
        int j = i, v = 0;
        for (int k = i + d, n = s.length(); j < k; ++j) {
            if (j < n)
                v = (v << 4) | hexDigitValue(s, j);
        }
        ca[ci] = v;
        return j;
    }

    static private String toHexDigits(int c, int n, char p, boolean upper) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < n; ++i, c >>>= 4) {
            int d = c & 0xF;
            int b = (d < 10) ? '0' : ((upper ? 'A' : 'a') - 10);
            int h = b + d;
            sb.append((char) h);
        }
        for (int i = 0, m = n - sb.length(); (m > 0) && (i < m); ++i)
            sb.append(p);
        sb.reverse();
        return sb.toString();
    }

    static private boolean isHexDigit(int c) {
        if ((c >= '0') && (c <= '9'))
            return true;
        else if ((c >= 'a') && (c <= 'f'))
            return true;
        else if ((c >= 'A') && (c <= 'F'))
            return true;
        else
            return false;
    }

    static private int hexDigitValue(String s, int i) {
        int c = s.charAt(i);
        if ((c >= '0') && (c <= '9'))
            return c - '0';
        else if ((c >= 'a') && (c <= 'f'))
            return (c - 'a') + 10;
        else if ((c >= 'A') && (c <= 'F'))
            return (c - 'A') + 10;
        else
            throw new IllegalArgumentException();
    }

    static private int parseNonUnicodeEscape(String s, int i, int[] ca, int ci) {
        int n = s.length();
        if (i >= n)
            return -1;
        else {
            int c = s.charAt(i);
            ca[ci] = c;
            return i + 1;
        }
    }

    static private int parseNonSpecial(String s, int i, int[] ca, int ci) {
        int n = s.length();
        if (i >= n)
            return -1;
        else {
            int c = s.charAt(i);
            if (isSpecial(c))
                return -1;
            else {
                ca[ci] = c;
                return i + 1;
            }
        }
    }

    static private int parseDelimiter(String s, int i, int d) {
        int n = s.length();
        if (i >= n)
            return -1;
        else {
            int c = s.charAt(i);
            if (c == d)
                return i + 1;
            else
                return i;
        }
    }

    static private boolean isSpecial(int c) {
        if (c == '-')
            return true;
        else if (c == ']')
            return true;
        else if (c == '\'')
            return true;
        else if (c == '\"')
            return true;
        else if (c == ' ')
            return true;
        else if (c == '\t')
            return true;
        else if (c == '\r')
            return true;
        else if (c == '\n')
            return true;
        else
            return false;
    }

}
