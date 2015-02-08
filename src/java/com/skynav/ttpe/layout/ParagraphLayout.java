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
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Wrap;
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
    private Color color;
    private String fontFamily;
    private Extent fontSize;
    private FontStyle fontStyle;
    private FontWeight fontWeight;
    private String language;
    private InlineAlignment textAlign;
    private Wrap wrap;
    private WritingMode writingMode;
    // derived style state
    private Font font;
    private double lineHeight;

    public ParagraphLayout(Paragraph paragraph, LayoutState state) {
        this.paragraph = paragraph;
        this.iterator = paragraph.getIterator();
        this.state = state;
        this.language = state.getLanguage();
        this.writingMode = state.getWritingMode();
        // paragraph specified styles
        org.w3c.dom.Element e = paragraph.getElement();
        this.color = state.getColor(e);
        this.fontFamily = state.getFontFamily(e);
        this.fontSize = state.getFontSize(e);
        this.fontStyle = state.getFontStyle(e);
        this.fontWeight = state.getFontWeight(e);
        this.textAlign = state.getTextAlign(e);
        this.wrap = state.getWrapOption(e);
        // derived styles
        this.font = state.getFontCache().mapFont(fontFamily, fontStyle, fontWeight, writingMode.getAxis(IPD), language, fontSize);
        this.lineHeight = state.getLineHeight(e, font);
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
                    if (b.isHard()) {
                        lines.add(emit(available, consumed, breaks));
                        consumed = 0;
                        break;
                    } else {
                        double advance = b.advance;
                        if ((consumed + advance) > available) {
                            if (wrap == Wrap.WRAP) {
                                if (!breaks.isEmpty()) {
                                    lines.add(emit(available, consumed, breaks));
                                    consumed = 0;
                                } else {
                                    if (bi != lci)
                                        bi = updateIterator(lci, b);
                                    b = getNextBreakOpportunity(bi, r);
                                }
                                continue;
                            }
                        }
                        breaks.add(b);
                        consumed += advance;
                        b = getNextBreakOpportunity(bi, r);
                    }
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
            int from = bi.current();
            int to = bi.next();
            if (to != LineBreakIterator.DONE)
                return new InlineBreakOpportunity(r, r.getInlineBreak(to), from, to, r.getAdvance(from, to));
        }
        return null;
    }

    private LineArea emit(double available, double consumed, List<InlineBreakOpportunity> breaks) {
        LineArea l = new LineArea(paragraph.getElement(), available, lineHeight, textAlign, color, font);
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
        boolean isHard() {
            if (type.isHard())
                return true;
            else if (run instanceof NonWhitespaceRun)
                return false;
            else {
                String lwsp = run.getText(start, index);
                return (lwsp.length() == 1) && (lwsp.charAt(0) == Characters.UC_LINE_SEPARATOR);
            }
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
        // obtain all of text associated with run
        String getText() {
            if (text == null)
                text = getText(0);
            return text;
        }
        // obtain text starting at FROM to END of run, where FROM is index into run, not outer iterator
        String getText(int from) {
            return getText(from, from + (end - start));
        }
        // obtain text starting at FROM to TO of run, where FROM and TO are indices into run, not outer iterator
        String getText(int from, int to) {
            StringBuffer sb = new StringBuffer();
            int savedIndex = iterator.getIndex();
            for (int i = start + from, e = start + to; i < e; ++i) {
                sb.append(iterator.setIndex(i));
            }
            iterator.setIndex(savedIndex);
            return sb.toString();
        }
        // obtain inline break type at INDEX of run, where INDEX is index into run, not outer iterator
        InlineBreak getInlineBreak(int index) {
            return InlineBreak.UNKNOWN;
        }
        // obtain advance of text starting at FROM to TO of run, where FROM and TO are indices into run, not outer iterator
        double getAdvance(int from, int to) {
            return font.getAdvance(getText().substring(from, to));
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
