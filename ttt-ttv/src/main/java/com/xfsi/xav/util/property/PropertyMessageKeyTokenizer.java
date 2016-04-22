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

package com.xfsi.xav.util.property;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;

/**
 * Tokenizer class for XML message keys
 */

public class PropertyMessageKeyTokenizer {

    private static class TableEntry {
        public boolean isNumeric = false;
        public boolean isAlphabetic = false;
        public boolean isFraction = false;
    }

    final public static int TT_EOF = -1;
    final public static int TT_NUMBER = -2;
    final public static int TT_WORD = -3;
    final public static int TT_FRACTION = -4;

    public int ttype;
    public String sval;
    public int nval;
    public int tokenLen;

    private PushbackReader pushIn = null;
    private TableEntry[] lookup = new TableEntry[257];
    private boolean pushBack = false;

    public PropertyMessageKeyTokenizer() {
        for (int pos = 0; pos < lookup.length; pos++) {
            lookup[pos] = new TableEntry();
        }
        reset();
    }

    public void setReader(Reader r) {
        pushIn = new PushbackReader(r);
    }

    private int chrRead() throws IOException {
        int chr = pushIn.read();
        if (chr == -1)
            chr = 256; /* EOF as invalid char */
        return chr;
    }

    public int nextToken() throws IOException {
        if (pushBack == true) {
            /* Do nothing */
            pushBack = false;
            return (ttype);
        } else {
            return (nextTokenType());
        }
    }

    private int nextTokenType() throws IOException {
        int chr = chrRead();
        sval = null; // Reset
        tokenLen = 1;

        if (lookup[chr].isFraction) {
            // Return fraction token
            ttype = TT_FRACTION;
        } else if (lookup[chr].isNumeric) {
            /* Parse the number and return */
            StringBuffer buffer = new StringBuffer(4);
            while (lookup[chr].isNumeric) {
                buffer.append((char) (chr & 0xFF));
                chr = chrRead();
            }
            /* For next token */
            pushIn.unread(chr);
            ttype = TT_NUMBER;
            try {
                tokenLen = buffer.length();
                nval = Integer.parseInt(buffer.toString());
            } catch (NumberFormatException e) {
                nval = 0;
            }
        } else if (lookup[chr].isAlphabetic) {
            /* Parse the word and return */
            StringBuffer buffer = new StringBuffer(4);
            // Words may NOT contain numeric chars
            while (lookup[chr].isAlphabetic) {
                buffer.append((char) (chr & 0xFF));
                chr = chrRead();
            }
            /* For next token */
            pushIn.unread(chr);
            ttype = TT_WORD;
            tokenLen = buffer.length();
            sval = buffer.toString();
        } else {
            /* Just return it as a token */
            if (chr == 256)
                ttype = TT_EOF;
            else
                ttype = chr & 0xFF;
        }
        return (ttype);
    }

    private void parseNumbers() {
        for (int letter = '0'; letter <= '9'; letter++) {
            lookup[letter].isNumeric = true;
        }
    }

    public void invalidChar(int ch) {
        lookup[ch].isAlphabetic = false;
        lookup[ch].isNumeric = false;
        lookup[ch].isFraction = false;
    }

    public void fractionChar(int ch) {
        lookup[ch].isAlphabetic = false;
        lookup[ch].isNumeric = false;
        lookup[ch].isFraction = true;
    }

    public void invalidChars(int low, int hi) {
        for (int letter = low; letter <= hi; letter++) {
            invalidChar(letter);
        }
    }

    public void pushBack() {
        pushBack = true;
    }

    public void reset() {
        wordChars('A', 'Z');
        wordChars('a', 'z');
        invalidChar(256); /* EOF */
        fractionChar('.');
        parseNumbers();
    }

    public String toString() {
        if (ttype == TT_EOF) {
            return ("EOF");
        } else if (ttype == TT_NUMBER) {
            return ("Token[n=" + nval + "]");
        } else {
            return ("Token[s=" + sval + "]");
        }
    }

    public void wordChars(int low, int hi) {
        for (int letter = low; letter <= hi; letter++) {
            lookup[letter].isAlphabetic = true;
        }
    }

}
