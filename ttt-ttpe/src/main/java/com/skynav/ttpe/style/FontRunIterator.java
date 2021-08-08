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
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.fonts.FontSelectionStrategy;

public class FontRunIterator {

    public static final int DONE = -1;

    private FontRunGenerator generator;
    private FontRun run;

    public FontRunIterator(FontCache fontCache) {
        this.generator = new FontRunGenerator(fontCache);
    }

    public FontRunIterator setParagraph(String text, Font[] fonts, FontSelectionStrategy fontSelectionStrategy) {
        generator.setPara(text, fonts, fontSelectionStrategy);
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
        private FontCache fontCache;
        private String text;
        private int processedLength;
        private Font[] fonts;
        private FontSelectionStrategy fontSelectionStrategy;
        public FontRunGenerator(FontCache fontCache) {
            this.fontCache = fontCache;
            this.text = null;
            this.fonts = null;
        }
        public void setPara(String text, Font[] fonts, FontSelectionStrategy fontSelectionStrategy) {
            this.text = text;
            this.processedLength = text.length();
            this.fonts = fonts;
            this.fontSelectionStrategy = fontSelectionStrategy;
        }

        public int getProcessedLength() {
            return processedLength;
        }

        public FontRun getLogicalRun(int index) {
            Font fLast  = null;
            int  iPrev  = -1;
            int  iFirst = -1;
            int  iLast  = -1;
            for (int i = index, iEnd = getProcessedLength(); i < iEnd;) {
                int iNext = getNextIndex(iPrev, i, iEnd);
                for (Font f : fonts) {
                    Font lastResortFont = getLastResortFont(f);
                    if (f.hasGlyphMapping(text, iPrev, i, iNext) || (f == lastResortFont)) {
                        if (f != fLast) {
                            if (fLast != null) {
                                return new FontRun(iFirst, iLast, fLast);
                            }
                            fLast = f;
                            iFirst = i;
                        }
                        iPrev = i;
                        i = iNext;
                        iLast = iNext;
                        break;
                    }
                }
            }
            if (fLast != null)
                return new FontRun(iFirst, iLast, fLast);
            else
                return null;
        }

        private int getNextIndex(int previous, int from, int to) {
            int next;
            if (from >= to) {
                next = to;
            } else {
                FontSelectionStrategy strategy = fontSelectionStrategy;
                if (strategy == FontSelectionStrategy.CHARACTER) {
                    next = getNextIndexChar(previous, from, to);
                } else if (strategy == FontSelectionStrategy.CCS) {
                    next = getNextIndexCCS(previous, from, to);
                } else if (strategy == FontSelectionStrategy.GC) {
                    next = getNextIndexGC(previous, from, to);
                } else if (strategy == FontSelectionStrategy.CONTEXT) {
                    int nextCCS = getNextIndexCCS(previous, from, to);
                    int nextGC  = getNextIndexGC(previous, from, to);
                    if (nextGC > nextCCS)
                        next = nextGC;
                    else
                        next = nextCCS;
                } else {
                    next = getNextIndexChar(previous, from, to);
                }
            }
            return next;
        }

        private int getNextIndexChar(int previous, int from, int to) {
            if ((from + 1) <= to)
                return from + 1;
            else
                return from;
        }

        private int getNextIndexCCS(int previous, int from, int to) {
            assert from >= 0;
            assert from <  to;
            int next = from;
            int c = text.codePointAt(next);
            if (Character.getType(c) != Character.NON_SPACING_MARK) {
                if (c < 0x10000)
                    next += 1;
                else
                    next += 2;
                while (next < to) {
                    c = text.codePointAt(next);
                    if (Character.getType(c) != Character.NON_SPACING_MARK)
                        return next;
                    if (c < 0x10000)
                        next += 1;
                    else
                        next += 2;
                }
            } else {
                while (next < to) {
                    c = text.codePointAt(next);
                    if (Character.getType(c) != Character.NON_SPACING_MARK)
                        return next;
                    if (c < 0x10000)
                        next += 1;
                    else
                        next += 2;
                }
            }
            return next;
        }

        private int getNextIndexGC(int previous, int from, int to) {
            // FIXME - IMPLEMENT fully; for now, reduce to CCS
            return getNextIndexCCS(previous, from, to);
        }

        private Font getLastResortFont(Font referenceFont) {
            return fontCache.getLastResortFont(referenceFont.getAxis(), referenceFont.getSize());
        }
    }

}
