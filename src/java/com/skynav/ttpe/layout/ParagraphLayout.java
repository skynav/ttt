/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.layout;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.List;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.util.Characters;

import static com.skynav.ttpe.geometry.Dimension.*;

public class ParagraphLayout {

    private AttributedCharacterIterator iterator;
    private LayoutState state;
    private double fontSize;
    @SuppressWarnings("unused")
    private double lineHeight;
    private Font font;

    public ParagraphLayout(Paragraph paragraph, LayoutState state) {
        this.iterator = paragraph.getIterator();
        this.state = state;
        this.fontSize = 24; // [TBD] TEMPORARY - REMOVE ME
        this.font = state.getFontCache().getDefaultFont(state.getWritingMode().getAxis(IPD), fontSize);
    }

    public List<Area> layout() {
        List<Area> areas = new java.util.ArrayList<Area>();
        double available = state.getAvailable(IPD);
        double consumed = 0;
        @SuppressWarnings("unused")
        InlineBreakOpportunity bLast = null;
        LineBreakIterator bi = state.getBreakIterator();
        for (TextRun r = getNextTextRun(); r != null;) {
            updateIterator(bi, r);
            for (InlineBreakOpportunity b = getNextBreakOpportunity(bi, r); b != null; b = getNextBreakOpportunity(bi, r)) {
                double advance = b.getAdvance();
                if ((consumed + advance) > available)
                    break;
                else {
                    consumed += advance;
                    bLast = b;
                }
            }
            if (consumed > available) {
                // [TBD ] EMIT line area (remove final whitespace run if present)
            }
            r = getNextTextRun();
        }
        return areas;
    }

    private TextRun getNextTextRun() {
        int s = iterator.getIndex();
        char c = iterator.current();
        if (c == CharacterIterator.DONE)
            return null;
        boolean inBreakingWhitespace = Characters.isBreakingWhitespace(c);
        while ((c = iterator.next()) != CharacterIterator.DONE) {
            if (inBreakingWhitespace ^ Characters.isBreakingWhitespace(c))
                break;
        }
        int e = iterator.getIndex();
        return inBreakingWhitespace ? new WhitespaceRun(s, e) : new NonWhitespaceRun(s, e);
    }

    private void updateIterator(LineBreakIterator bi, TextRun r) {
        bi.setText(r.getText());
        bi.first();
    }

    private InlineBreakOpportunity getNextBreakOpportunity(LineBreakIterator bi, TextRun r) {
        if (bi != null) {
            int limit = bi.next();
            if (limit != LineBreakIterator.DONE)
                return new InlineBreakOpportunity(r.getInlineBreak(limit), limit, r.getAdvance(limit));
        }
        return null;
    }

    private enum InlineBreak {
        HARD,
        SOFT_IDEOGRAPH,
        SOFT_HYPHENATION_POINT,
        SOFT_WHITESPACE,
        UNKNOWN;
        boolean isHard() {
            return this == HARD;
        }
        @SuppressWarnings("unused")
        boolean isSoft() {
            return !isHard();
        }
    }

    private static class InlineBreakOpportunity {
        private InlineBreak type;
        private int index;
        private double advance;
        InlineBreakOpportunity(InlineBreak type, int index, double advance) {
            this.type = type;
            this.index = index;
            this.advance = advance;
        }
        @SuppressWarnings("unused")
        InlineBreak getType() {
            return type;
        }
        @SuppressWarnings("unused")
        int getIndex() {
            return index;
        }
        double getAdvance() {
            return advance;
        }
    }

    private class TextRun {
        private int start;
        private int end;
        private String text;
        TextRun(int start, int end) {
            this.start = start;
            this.end = end;
        }
        int getStart() {
            return start;
        }
        int getEnd() {
            return end;
        }
        String getText() {
            if (text == null) {
                StringBuffer sb = new StringBuffer();
                int savedIndex = iterator.getIndex();
                for (int s = getStart(), e = getEnd(); s < e; ++s) {
                    sb.append(iterator.setIndex(s));
                }
                iterator.setIndex(savedIndex);
                text = sb.toString();
            }
            return text;
        }
        InlineBreak getInlineBreak(int index) {
            return InlineBreak.UNKNOWN;
        }
        double getAdvance(int limit) {
            return font.getAdvance(getText().substring(0, limit));
        }
    }

    private class WhitespaceRun extends TextRun {
        WhitespaceRun(int start, int end) {
            super(start, end);
        }
    }

    private class NonWhitespaceRun extends TextRun {
        NonWhitespaceRun(int start, int end) {
            super(start, end);
        }
    }

}
