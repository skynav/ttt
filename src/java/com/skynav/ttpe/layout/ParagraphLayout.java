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

import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.util.Characters;

import static com.skynav.ttpe.geometry.Axis.*;
import static com.skynav.ttpe.geometry.Dimension.*;

public class ParagraphLayout {

    // paragraph content
    private Paragraph paragraph;
    private AttributedCharacterIterator iterator;
    // layout state
    private LayoutState state;
    private double xCurrent;
    private double yCurrent;
    // style related state
    private Font font;
    private double fontSize;
    private double lineHeight;
    private WritingMode writingMode;

    public ParagraphLayout(Paragraph paragraph, LayoutState state) {
        this.paragraph = paragraph;
        this.iterator = paragraph.getIterator();
        this.state = state;
        this.writingMode = state.getWritingMode();
        this.fontSize = 24;                                     // [TBD] TEMPORARY - REMOVE ME
        this.lineHeight = fontSize * 1.25;                      // [TBD] TEMPORARY - REMOVE ME
        this.font = state.getFontCache().getDefaultFont(writingMode.getAxis(IPD), fontSize);
    }

    public List<LineArea> layout() {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        double available = state.getAvailable(IPD);
        double consumed = 0;
        List<InlineBreakOpportunity> breaks = new java.util.ArrayList<InlineBreakOpportunity>();
        LineBreakIterator bi = state.getBreakIterator();
        for (TextRun r = getNextTextRun(); r != null;) {
            updateIterator(bi, r);
            for (InlineBreakOpportunity b = getNextBreakOpportunity(bi, r); b != null; b = getNextBreakOpportunity(bi, r)) {
                double advance = b.advance;
                if ((consumed + advance) > available) {
                    lines.add(emit(available, consumed, breaks));
                    breaks.clear();
                    consumed = 0;
                } else {
                    breaks.add(b);
                    consumed += advance;
                }
            }
            r = getNextTextRun();
        }
        if (!breaks.isEmpty())
            lines.add(emit(available, consumed, breaks));
        return lines;
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
                return new InlineBreakOpportunity(r, r.getInlineBreak(limit), limit, r.getAdvance(limit));
        }
        return null;
    }

    private LineArea emit(double available, double consumed, List<InlineBreakOpportunity> breaks) {
        double          x       = xCurrent;
        double          y       = yCurrent;
        double          w       = available;
        double          h       = lineHeight;
        LineArea        l       = new LineArea(paragraph.getElement(), writingMode, x, y, w, h);
        if (writingMode.getAxis(IPD) == HORIZONTAL) {
            yCurrent += h;
        } else {
            throw new UnsupportedOperationException();          // [TBD] - VERTICAL WMs
        }
        populate(l, breaks);
        return l;
    }

    private void populate(LineArea l, List<InlineBreakOpportunity> breaks) {
        if (!breaks.isEmpty()) {
            int nb = breaks.size();
            InlineBreakOpportunity fb = breaks.get(0);
            InlineBreakOpportunity lb = breaks.get(nb - 1);
            while ((lb.run instanceof WhitespaceRun) && (nb > 0)) {
                lb = breaks.get(--nb - 1);
            }
            int s = fb.run.start;
            int e = lb.run.start + lb.index;
            StringBuffer sb = new StringBuffer();
            int savedIndex = iterator.getIndex();
            for (int i = s; i < e; ++i) {
                sb.append(iterator.setIndex(i));
            }
            iterator.setIndex(savedIndex);
            String text = sb.toString();
            l.addChild(new GlyphArea(paragraph.getElement(), writingMode, 0, 0, font.getAdvance(text), l.getHeight(), text, font));
        }
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
        TextRun run;            // associated text run
        @SuppressWarnings("unused")
        InlineBreak type;
        int index;              // index within text run
        double advance;         // advance (in IPD) within text run
        InlineBreakOpportunity(TextRun run, InlineBreak type, int index, double advance) {
            this.run = run;
            this.type = type;
            this.index = index;
            this.advance = advance;
        }
    }

    private class TextRun {
        int start;              // start index in outer iterator
        int end;                // end index in outer iterator
        String text;
        TextRun(int start, int end) {
            this.start = start;
            this.end = end;
        }
        String getText() {
            if (text == null) {
                StringBuffer sb = new StringBuffer();
                int savedIndex = iterator.getIndex();
                for (int i = start; i < end; ++i) {
                    sb.append(iterator.setIndex(i));
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
