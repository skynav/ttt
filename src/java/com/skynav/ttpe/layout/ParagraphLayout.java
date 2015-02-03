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

import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LeafInlineArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.util.Characters;

import static com.skynav.ttpe.geometry.Dimension.*;

public class ParagraphLayout {

    // paragraph content
    private Paragraph paragraph;
    private AttributedCharacterIterator iterator;
    // layout state
    private LayoutState state;
    // style related state
    private boolean allowOverflow;
    private Font font;
    private double fontSize;
    private double lineHeight;
    private InlineAlignment textAlign;
    private WritingMode writingMode;

    public ParagraphLayout(Paragraph paragraph, LayoutState state) {
        this.paragraph = paragraph;
        this.iterator = paragraph.getIterator();
        this.state = state;
        this.writingMode = state.getWritingMode();
        // [TBD] initialize following states from paragraph styles
        this.allowOverflow = false;
        this.fontSize = 24;
        this.font = state.getFontCache().getDefaultFont(writingMode.getAxis(IPD), fontSize);
        this.lineHeight = fontSize * 1.25;
        this.textAlign = InlineAlignment.CENTER;
    }

    public List<LineArea> layout() {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        double available = state.getAvailable(IPD);
        if (available > 0) {
            double consumed = 0;
            List<InlineBreakOpportunity> breaks = new java.util.ArrayList<InlineBreakOpportunity>();
            LineBreakIterator lbi = state.getBreakIterator();
            LineBreakIterator lci = state.getCharacterIterator();
            LineBreakIterator bi;
            for (TextRun r = getNextTextRun(); r != null;) {
                bi = updateIterator(lbi, r);
                for (InlineBreakOpportunity b = getNextBreakOpportunity(bi, r); b != null; ) {
                    double advance = b.advance;
                    if ((consumed + advance) > available) {
                        if (!breaks.isEmpty()) {
                            lines.add(emit(available, consumed, breaks));
                            consumed = 0;
                            continue;
                        } else if (!allowOverflow) {
                            if (bi != lci)
                                bi = updateIterator(lci, b);
                            b = getNextBreakOpportunity(bi, r);
                            continue;
                        }
                    }
                    breaks.add(b);
                    consumed += advance;
                    b = getNextBreakOpportunity(bi, r);
                }
                r = getNextTextRun();
            }
            if (!breaks.isEmpty())
                lines.add(emit(available, consumed, breaks));
        }
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

    private LineBreakIterator updateIterator(LineBreakIterator bi, TextRun r) {
        bi.setText(r.getText());
        bi.first();
        return bi;
    }

    private LineBreakIterator updateIterator(LineBreakIterator bi, InlineBreakOpportunity b) {
        bi.setText(b.run.getText(b.start));
        bi.first();
        return bi;
    }

    private InlineBreakOpportunity getNextBreakOpportunity(LineBreakIterator bi, TextRun r) {
        if (bi != null) {
            int start = bi.current();
            int limit = bi.next();
            if (limit != LineBreakIterator.DONE)
                return new InlineBreakOpportunity(r, r.getInlineBreak(limit), start, limit, r.getAdvance(start, limit));
        }
        return null;
    }

    private LineArea emit(double available, double consumed, List<InlineBreakOpportunity> breaks) {
        LineArea l = new LineArea(paragraph.getElement(), available, lineHeight, textAlign);
        return alignTextAreas(addTextAreas(l, breaks));
    }

    private LineArea addTextAreas(LineArea l, List<InlineBreakOpportunity> breaks) {
        removeLeadingWhitespace(breaks);
        removeTrailingWhitespace(breaks);
        if (!breaks.isEmpty()) {
            int savedIndex = iterator.getIndex();
            StringBuffer sb = new StringBuffer();
            TextRun lastRun = null;
            double advance = 0;
            for (InlineBreakOpportunity b : breaks) {
                TextRun r = b.run;
                if ((lastRun != null) && (r != lastRun)) {
                    addTextArea(l, sb.toString(), font, advance, lineHeight, lastRun instanceof WhitespaceRun);
                    sb.setLength(0);
                    advance = 0;
                }
                int s = r.start + b.start;
                int e = r.start + b.index;
                for (int i = s; i < e; ++i)
                    sb.append(iterator.setIndex(i));
                advance += b.advance;
                lastRun = r;
            }
            if (sb.length() > 0)
                addTextArea(l, sb.toString(), font, advance, lineHeight, lastRun instanceof WhitespaceRun);
            iterator.setIndex(savedIndex);
            breaks.clear();
        }
        return l;
    }

    private void removeLeadingWhitespace(List<InlineBreakOpportunity> breaks) {
        while (!breaks.isEmpty()) {
            int i = 0;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.run instanceof WhitespaceRun)
                breaks.remove(i);
            else
                break;
        }
    }

    private void removeTrailingWhitespace(List<InlineBreakOpportunity> breaks) {
        while (!breaks.isEmpty()) {
            int i = breaks.size() - 1;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.run instanceof WhitespaceRun)
                breaks.remove(i);
            else
                break;
        }
    }

    private void addTextArea(LineArea l, String text, Font font, double advance, double lineHeight, boolean isWhitespace) {
        LeafInlineArea a;
        if (isWhitespace)
            a = new SpaceArea(paragraph.getElement(), advance, lineHeight, text, font);
        else
            a = new GlyphArea(paragraph.getElement(), advance, lineHeight, text, font);
        l.addChild(a);
    }

    private LineArea alignTextAreas(LineArea l) {
        double measure = l.getIPD();
        double consumed = 0;
        for (AreaNode c : l.getChildren())
            consumed += c.getIPD();
        double available = measure - consumed;
        if (available > 0) {
            if (textAlign == InlineAlignment.START) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, lineHeight);
                l.addChild(a);
            } else if (textAlign == InlineAlignment.END) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, lineHeight);
                l.insertChild(a, l.firstChild());
            } else if (textAlign == InlineAlignment.CENTER) {
                double half = available / 2;
                AreaNode a1 = new InlineFillerArea(l.getElement(), half, lineHeight);
                AreaNode a2 = new InlineFillerArea(l.getElement(), half, lineHeight);
                l.insertChild(a1, l.firstChild());
                l.insertChild(a2, null);
            } else if (textAlign == InlineAlignment.JUSTIFY) {
                l = justifyTextAreas(l);
            }
        } else
            l.setOverflow(-available);
        return l;
    }

    private LineArea justifyTextAreas(LineArea l) {
        return l;
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
        int start;              // start index within text run
        int index;              // index (of break) within text run
        double advance;         // advance (in IPD) within text run
        InlineBreakOpportunity(TextRun run, InlineBreak type, int start, int index, double advance) {
            this.run = run;
            this.type = type;
            this.start = start;
            this.index = index;
            this.advance = advance;
        }
    }

    private class TextRun {
        int start;              // start index in outer iterator
        int end;                // end index in outer iterator
        String text;            // cached text over complete run interval
        TextRun(int start, int end) {
            this.start = start;
            this.end = end;
        }
        String getText() {
            if (text == null)
                text = getText(start);
            return text;
        }
        String getText(int from) {
            return getText(from, end);
        }
        String getText(int from, int to) {
            StringBuffer sb = new StringBuffer();
            int savedIndex = iterator.getIndex();
            for (int i = from; i < to; ++i) {
                sb.append(iterator.setIndex(i));
            }
            iterator.setIndex(savedIndex);
            return sb.toString();
        }
        InlineBreak getInlineBreak(int index) {
            return InlineBreak.UNKNOWN;
        }
        double getAdvance(int start, int limit) {
            return font.getAdvance(getText().substring(start, limit));
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
