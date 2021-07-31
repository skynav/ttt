/*
 * Copyright 2014-21 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import com.skynav.ttpe.fonts.Font;

public class FontRunIterator {

    public static final int DONE = -1;

    private FontRunGenerator generator;
    private FontRun run;

    public FontRunIterator() {
        this.generator = new FontRunGenerator();
    }

    public FontRunIterator setParagraph(String text) {
        generator.setPara(text);
        setIndex(0);
        return this;
    }

    public int current() {
        return (run != null) ? run.getStart() : DONE;
    }

    public int limit() {
        return (run != null) ? run.getLimit() : DONE;
    }

    public Font font() {
        return (run != null) ? run.getFont() : null;
    }

    public int first() {
        return setIndex(0);
    }

    public int next() {
        return setIndex(limit());
    }

    public int setIndex(int index) {
        if (index < 0)
            return DONE;
        else if (index < generator.getProcessedLength()) {
            run = generator.getLogicalRun(index);
            return current();
        } else
            return DONE;
    }

    static class FontRun {
        private int start;
        private int limit;
        private Font font;
        FontRun(int start, int limit, Font font) {
            this.start = start;
            this.limit = limit;
            this.font  = font;
        }
        public int getStart() {
            return start;
        }

        public int getLimit() {
            return limit;
        }

        public Font getFont() {
            return font;
        }
    }

    static class FontRunGenerator {
        public void setPara(String text) {
        }

        public int getProcessedLength() {
            return 0;
        }

        public FontRun getLogicalRun(int index) {
            return null;
        }
    }

}
