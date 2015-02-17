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
import com.skynav.ttpe.area.InlineBlockArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.Wrap;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.util.Characters;

import static com.skynav.ttpe.geometry.Dimension.*;

public class ParagraphLayout {

    private enum Consume {
        MIN,
        FIT,
        MAX;
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
        this.color = paragraph.getColor(-1);
        this.fontFamily = paragraph.getFontFamily(-1);
        this.fontSize = paragraph.getFontSize(-1);
        this.fontStyle = paragraph.getFontStyle(-1);
        this.fontWeight = paragraph.getFontWeight(-1);
        this.textAlign = relativizeAlignment(paragraph.getTextAlign(-1), this.writingMode);
        this.wrap = paragraph.getWrapOption(-1);
        // derived styles
        this.font = state.getFontCache().mapFont(fontFamily, fontStyle, fontWeight, writingMode.getAxis(IPD), language, fontSize);
        this.lineHeight = paragraph.getLineHeight(-1, font);
    }

    private static InlineAlignment relativizeAlignment(InlineAlignment alignment, WritingMode wm) {
        Direction direction = wm.getDirection(IPD);
        if (alignment == InlineAlignment.LEFT) {
            if (direction == Direction.LR)
                alignment = InlineAlignment.START;
            else if (direction == Direction.RL)
                alignment = InlineAlignment.END;
        } else if (alignment == InlineAlignment.RIGHT) {
            if (direction == Direction.RL)
                alignment = InlineAlignment.START;
            else if (direction == Direction.LR)
                alignment = InlineAlignment.END;
        }
        return alignment;
    }

    public List<LineArea> layout() {
        return layout(state.getAvailable(IPD), Consume.MAX);
    }

    public List<LineArea> layout(double available, Consume consume) {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        if (available > 0) {
            double consumed = 0;
            List<InlineBreakOpportunity> breaks = new java.util.ArrayList<InlineBreakOpportunity>();
            LineBreakIterator lbi = state.getBreakIterator();
            LineBreakIterator lci = state.getCharacterIterator();
            LineBreakIterator bi;
            for (TextRun r = getNextTextRun(); r != null;) {
                bi = updateIterator(lbi, r);
                for (InlineBreakOpportunity b = getNextBreakOpportunity(bi, r, available - consumed); b != null; ) {
                    if (b.isHard()) {
                        lines.add(emit(available, consumed, consume, breaks));
                        consumed = 0;
                        break;
                    } else {
                        double advance = b.advance;
                        if ((consumed + advance) > available) {
                            if (wrap == Wrap.WRAP) {
                                if (!breaks.isEmpty()) {
                                    lines.add(emit(available, consumed, consume, breaks));
                                    consumed = 0;
                                } else {
                                    if (bi != lci)
                                        bi = updateIterator(lci, b);
                                    b = getNextBreakOpportunity(bi, r, available - consumed);
                                }
                                continue;
                            }
                        }
                        breaks.add(b);
                        consumed += advance;
                        b = getNextBreakOpportunity(bi, r, available - consumed);
                    }
                }
                r = getNextTextRun();
            }
            if (!breaks.isEmpty())
                lines.add(emit(available, consumed, consume, breaks));
        }
        return align(lines);
    }

    private static final StyleAttribute[] embeddingAttr = new StyleAttribute[] { StyleAttribute.EMBEDDING };
    private TextRun getNextTextRun() {
        int s = iterator.getIndex();
        char c = iterator.current();
        if (c == CharacterIterator.DONE)
            return null;
        else if (c == Characters.UC_OBJECT) {
            Object embedding = iterator.getAttribute(embeddingAttr[0]);
            iterator.setIndex(s + 1);
            return new EmbeddingRun(s, s + 1, embedding);
        }
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

    private InlineBreakOpportunity getNextBreakOpportunity(LineBreakIterator bi, TextRun r, double available) {
        if (bi != null) {
            int from = bi.current();
            int to = bi.next();
            if (to != LineBreakIterator.DONE)
                return new InlineBreakOpportunity(r, r.getInlineBreak(to), from, to, r.getAdvance(from, to, available));
        }
        return null;
    }

    private LineArea emit(double available, double consumed, Consume consume, List<InlineBreakOpportunity> breaks) {
        LineArea l = new LineArea(paragraph.getElement(), consume == Consume.MAX ? available : consumed, lineHeight, textAlign, color, font);
        return addTextAreas(l, breaks);
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
                    addTextArea(l, sb.toString(), font, advance, lineHeight, lastRun);
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
                addTextArea(l, sb.toString(), font, advance, lineHeight, lastRun);
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

    private void addTextArea(LineArea l, String text, Font font, double advance, double lineHeight, TextRun run) {
        AreaNode a;
        if (run instanceof WhitespaceRun)
            a = new SpaceArea(paragraph.getElement(), advance, lineHeight, text, font);
        else if (run instanceof EmbeddingRun)
            a = ((EmbeddingRun) run).getArea();
        else if (run instanceof NonWhitespaceRun)
            a = new GlyphArea(paragraph.getElement(), advance, lineHeight, text, font);
        else
            a = null;
        if (a != null)
            l.addChild(a, true);
    }

    private List<LineArea> align(List<LineArea> lines) {
        double maxMeasure = 0;
        for (LineArea l : lines) {
            double measure = l.getIPD();
            if (measure > maxMeasure)
                maxMeasure = measure;
        }
        for (LineArea l : lines) {
            alignTextAreas(l, maxMeasure);
            l.setIPD(maxMeasure);
        }
        return lines;
    }

    private void alignTextAreas(LineArea l, double measure) {
        double consumed = 0;
        for (AreaNode c : l.getChildren())
            consumed += c.getIPD();
        double available = measure - consumed;
        if (available > 0) {
            if (textAlign == InlineAlignment.START) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, lineHeight);
                l.addChild(a, true);
            } else if (textAlign == InlineAlignment.END) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, lineHeight);
                l.insertChild(a, l.firstChild(), true);
            } else if (textAlign == InlineAlignment.CENTER) {
                double half = available / 2;
                AreaNode a1 = new InlineFillerArea(l.getElement(), half, lineHeight);
                AreaNode a2 = new InlineFillerArea(l.getElement(), half, lineHeight);
                l.insertChild(a1, l.firstChild(), true);
                l.insertChild(a2, null, true);
            } else if (textAlign == InlineAlignment.JUSTIFY) {
                l = justifyTextAreas(l);
            }
        } else if (available < 0) {
            l.setOverflow(-available);
        }
    }

    private LineArea justifyTextAreas(LineArea l) {
        return l;
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
        double getAdvance(int from, int to, double available) {
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

    private class EmbeddingRun extends NonWhitespaceRun {
        private Object embedding;
        private InlineBlockArea area;
        EmbeddingRun(int start, int end, Object embedding) {
            super(start, end);
            this.embedding = embedding;
        }
        double getAdvance(int from, int to, double available) {
            // format embedding using available width
            if (area == null)
                area = layoutEmbedding(available);
            if (area != null)
                return area.getIPD();
            else
                return 0;
        }
        InlineBlockArea getArea() {
            return area;
        }
        private InlineBlockArea layoutEmbedding(double available) {
            if (embedding instanceof Paragraph)
                return layoutEmbedding((Paragraph) embedding, available);
            else
                return null;
        }
        private InlineBlockArea layoutEmbedding(Paragraph embedding, double available) {
            InlineBlockArea area = new InlineBlockArea(embedding.getElement());
            area.addChildren(new ParagraphLayout(embedding, state).layout(available, Consume.FIT), true);
            return area;
        }
    }

}
