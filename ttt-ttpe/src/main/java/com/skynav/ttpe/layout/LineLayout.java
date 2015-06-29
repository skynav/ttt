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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.skynav.ttpe.area.AnnotationArea;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.InlineBlockArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontFeature;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.AnnotationPosition;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Combine;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.LineFeedTreatment;
import com.skynav.ttpe.style.Orientation;
import com.skynav.ttpe.style.Outline;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.SuppressAtLineBreakTreatment;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.style.WhitespaceTreatment;
import com.skynav.ttpe.style.Wrap;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.text.Phrase;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttpe.util.Integers;
import com.skynav.ttpe.util.Strings;

import static com.skynav.ttpe.geometry.Dimension.*;

public class LineLayout {

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

    // content state
    private Phrase content;
    private AttributedCharacterIterator iterator;

    // layout state
    private LayoutState state;
    private Defaults defaults;
    private int lineNumber;

    // style related state
    private Color color;
    private InlineAlignment textAlign;
    private Wrap wrap;
    private WritingMode writingMode;
    private int level;

    // derived style state
    private Font font;
    private double lineHeight;
    private WhitespaceState whitespace;

    public LineLayout(Phrase content, LayoutState state) {
        // content state
        this.content = content;
        this.iterator = content.getIterator();
        // layout state
        this.state = state;
        this.defaults = state.getDefaults();
        // area derived state
        this.writingMode = state.getWritingMode();
        this.level = state.getBidiLevel();
        // paragraph specified styles
        this.color = content.getColor(-1, defaults);
        this.textAlign = relativizeAlignment(content.getTextAlign(-1, defaults), this.writingMode);
        this.wrap = content.getWrapOption(-1, defaults);
        // derived styles
        this.font = content.getFont(-1, defaults);
        this.lineHeight = content.getLineHeight(-1, defaults, font);
        this.whitespace = new WhitespaceState(state.getWhitespace());
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

    public List<? extends LineArea> layout(double available, Consume consume) {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        if (available > 0) {
            double consumed = 0;
            List<InlineBreakOpportunity> breaks = new java.util.ArrayList<InlineBreakOpportunity>();
            LineBreakIterator lbi = state.getBreakIterator();
            LineBreakIterator lci = state.getCharacterIterator();
            LineBreakIterator bi;
            for (TextRun r = getNextTextRun(); r != null;) {
                if (!breaks.isEmpty() || !r.suppressAfterLineBreak()) {
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
            return new EmbeddingRun(s, embedding);
        }
        boolean inBreakingWhitespace = Characters.isBreakingWhitespace(c);
        while ((c = iterator.next()) != CharacterIterator.DONE) {
            if (c == Characters.UC_OBJECT)
                break;
            else if (inBreakingWhitespace ^ Characters.isBreakingWhitespace(c))
                break;
            else if (hasTextRunBreakingAttribute(iterator))
                break;
        }
        int e = iterator.getIndex();
        return inBreakingWhitespace ? new WhitespaceRun(s, e, whitespace) : new NonWhitespaceRun(s, e);
    }

    private static boolean hasTextRunBreakingAttribute(AttributedCharacterIterator iterator) {
        return hasTextRunBreakingAttribute(iterator, iterator.getIndex());
    }

    private static final Set<StyleAttribute> textRunBreakingAttributes;
    static {
        Set<StyleAttribute> s = new java.util.HashSet<StyleAttribute>();
        s.add(StyleAttribute.ANNOTATIONS);
        s.add(StyleAttribute.BIDI);
        s.add(StyleAttribute.COMBINE);
        s.add(StyleAttribute.ORIENTATION);
        textRunBreakingAttributes = Collections.unmodifiableSet(s);
    }

    private static boolean hasTextRunBreakingAttribute(AttributedCharacterIterator iterator, int index) {
        int s = iterator.getIndex();
        if (index != s)
            iterator.setIndex(index);
        int k = iterator.getRunStart(textRunBreakingAttributes);
        if (index != s)
            iterator.setIndex(s);
        return k == index;
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
        consumed = maybeRemoveLeading(breaks, consumed);
        consumed = maybeRemoveTrailing(breaks, consumed);
        return addTextAreas(newLine(content, consume == Consume.MAX ? available : consumed, lineHeight, level, textAlign, color, font), breaks);
    }

    protected LineArea newLine(Phrase p, double ipd, double bpd, int level, InlineAlignment textAlign, Color color, Font font) {
        return new LineArea(p.getElement(), ipd, bpd, level, textAlign, color, font, ++lineNumber);
    }

    protected int getNextLineNumber() {
        return ++lineNumber;
    }

    private LineArea addTextAreas(LineArea l, List<InlineBreakOpportunity> breaks) {
        if (!breaks.isEmpty()) {
            int savedIndex = iterator.getIndex();
            StringBuffer sb = new StringBuffer();
            List<Decoration> decorations = new java.util.ArrayList<Decoration>();
            TextRun lastRun = null;
            int lastRunStart = -1;
            double advance = 0;
            for (InlineBreakOpportunity b : breaks) {
                TextRun r = b.run;
                if ((lastRun != null) && ((r != lastRun) || (l instanceof AnnotationArea))) {
                    if (!(l instanceof AnnotationArea))
                        maybeAddAnnotationAreas(l, lastRunStart, r.start, font, advance, lineHeight);
                    addTextArea(l, sb.toString(), decorations, font, advance, lineHeight, lastRun);
                    sb.setLength(0);
                    decorations.clear();
                    advance = 0;
                    lastRunStart = -1;
                }
                sb.append(r.getText(b.start, b.index));
                decorations.addAll(r.getDecorations(b.start, b.index));
                advance += b.advance;
                lastRun = r;
                if (lastRunStart < 0)
                    lastRunStart = r.start + b.start;
            }
            if (sb.length() > 0) {
                if (!(l instanceof AnnotationArea))
                    maybeAddAnnotationAreas(l, lastRunStart, lastRun != null ? lastRun.end : -1, font, advance, lineHeight);
                addTextArea(l, sb.toString(), decorations, font, advance, lineHeight, lastRun);
            }
            iterator.setIndex(savedIndex);
            breaks.clear();
        }
        return l;
    }

    private double maybeRemoveLeading(List<InlineBreakOpportunity> breaks, double consumed) {
        while (!breaks.isEmpty()) {
            int i = 0;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.suppressAfterLineBreak()) {
                breaks.remove(i);
                consumed -= b.advance;
            } else
                break;
        }
        return consumed;
    }

    private double maybeRemoveTrailing(List<InlineBreakOpportunity> breaks, double consumed) {
        while (!breaks.isEmpty()) {
            int i = breaks.size() - 1;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.suppressBeforeLineBreak()) {
                breaks.remove(i);
                consumed -= b.advance;
            } else
                break;
        }
        return consumed;
    }

    private void addTextArea(LineArea l, String text, List<Decoration> decorations, Font font, double advance, double lineHeight, TextRun run) {
        if (run instanceof WhitespaceRun) {
            if (advance > 0)
                l.addChild(new SpaceArea(content.getElement(), advance, lineHeight, run.level, text, font), LineArea.ENCLOSE_ALL);
        } else if (run instanceof EmbeddingRun) {
            l.addChild(((EmbeddingRun) run).getArea(), LineArea.ENCLOSE_ALL);
        } else if (run instanceof NonWhitespaceRun) {
            int start = run.start;
            Orientation orientation = run.orientation;
            boolean rotated = orientation.isRotated();
            Combine combine = run.combine;
            boolean combined = !combine.isNone();
            for (StyleAttributeInterval fai : run.getFontIntervals()) {
                Font fo = font;
                double ipd = 0;
                double bpd = lineHeight;
                if (fai.isOuterScope()) {
                    boolean kerned = fo.isKerningEnabled();
                    ipd = !combined ? advance : fo.getAdvance(text, kerned, rotated, true);
                    GlyphArea a = new GlyphArea(content.getElement(), ipd, bpd, run.level, text, decorations, fo, orientation, combine);
                    l.addChild(a, combine.isNone() ? LineArea.ENCLOSE_ALL : LineArea.ENCLOSE_ALL_CROSS);
                    break;
                } else {
                    int i = fai.getBegin() - start;
                    assert i >= 0;
                    assert i < text.length();
                    int j = fai.getEnd() - start;
                    assert j >= 0;
                    assert j >= i;
                    if (j > text.length())
                        j = text.length();
                    String segText = text.substring(i, j);
                    Font fi;
                    if ((fi = (Font) fai.getValue()) != null) {
                        boolean kerned = fi.isKerningEnabled();
                        List<Decoration> segDecorations = getSegmentDecorations(decorations, i, j);
                        ipd = fi.getAdvance(segText, kerned, rotated, combined);
                        GlyphArea a = new GlyphArea(content.getElement(), ipd, bpd, run.level, segText, segDecorations, fi, orientation, combine);
                        l.addChild(a, combine.isNone() ? LineArea.ENCLOSE_ALL : LineArea.ENCLOSE_ALL_CROSS);
                    }
                }
            }
        }
    }

    private List<Decoration> getSegmentDecorations(List<Decoration> decorations, int from, int to) {
        List<Decoration> sd = new java.util.ArrayList<Decoration>();
        for (Decoration d : decorations) {
            if (d.intersects(from, to))
                sd.add(d.adjustInterval(-from));
        }
        return sd;
    }

    private void maybeAddAnnotationAreas(LineArea l, int start, int end, Font font, double advance, double lineHeight) {
        if (start >= 0) {
            iterator.setIndex(start);
            Phrase[] annotations = (Phrase[]) iterator.getAttribute(StyleAttribute.ANNOTATIONS);
            if (annotations != null) {
                String base = getAnnotationBase(start, end);
                Orientation[] baseOrientations = getAnnotationBaseOrientations(start, end);
                addAnnotationAreas(l, base, baseOrientations, annotations, font, advance, lineHeight);
            }
        }
    }

    private String getAnnotationBase(int start, int end) {
        StringBuffer sb = new StringBuffer();
        int savedIndex = iterator.getIndex();
        for (int i = start, j = end; i < j; ++i) {
            char c = iterator.setIndex(i);
            if (c == CharacterIterator.DONE)
                break;
            else
                sb.append(c);
        }
        iterator.setIndex(savedIndex);
        return sb.toString();
    }

    private Orientation[] getAnnotationBaseOrientations(int start, int end) {
        int savedIndex = iterator.getIndex();
        iterator.setIndex(start);
        Orientation orientation = (Orientation) iterator.getAttribute(StyleAttribute.ORIENTATION);
        iterator.setIndex(savedIndex);
        if ((orientation == null) || !orientation.isRotated())
            return null;
        else {
            int length = end - start;
            Orientation[] orientations = new Orientation[length];
            Arrays.fill(orientations, 0, length, orientation);
            return orientations;
        }
    }

    private void addAnnotationAreas(LineArea l, String base, Orientation[] baseOrientations, Phrase[] annotations, Font font, double advance, double lineHeight) {
        for (Phrase p : annotations) {
            InlineAlignment annotationAlign = p.getAnnotationAlign(-1, defaults);
            Double annotationOffset = p.getAnnotationOffset(-1, defaults);
            AnnotationPosition annotationPosition = p.getAnnotationPosition(-1, defaults);
            for (AnnotationArea a :  new AnnotationLayout(p, state).layout()) {
                a.setAlignment(annotationAlign);
                a.setOffset(annotationOffset);
                a.setPosition(annotationPosition);
                alignTextAreas(a, advance, annotationAlign, getBaseAdvances(base, baseOrientations, font));
                l.addChild(a, LineArea.ENCLOSE_ALL);
            }
        }
    }

    private double[] getBaseAdvances(String base, Orientation[] baseOrientations, Font font) {
        if (base == null)
            return null;
        else {
            int baseLength = base.length();
            double[] advances = new double[baseLength];
            boolean kerningEnabled = font.isKerningEnabled();
            for (int i = 0, n = baseLength; i < n; ++i) {
                int j = i + 1;
                Orientation orientation;
                if ((baseOrientations == null) || (i >= baseOrientations.length))
                    orientation = Orientation.ROTATE000;
                else
                    orientation = baseOrientations[i];
                advances[i] = font.getAdvance(base.substring(i, j), kerningEnabled, orientation.isRotated(), false);
            }
            return advances;
        }
    }

    private List<LineArea> align(List<LineArea> lines) {
        double maxMeasure = 0;
        for (LineArea l : lines) {
            double measure = l.getIPD();
            if (measure > maxMeasure)
                maxMeasure = measure;
        }
        for (LineArea l : lines) {
            alignTextAreas(l, maxMeasure, textAlign, null);
            l.setIPD(maxMeasure);
        }
        return lines;
    }

    private void alignTextAreas(LineArea l, double measure, InlineAlignment alignment, double[] baseAdvances) {
        double consumed = 0;
        int numNonAnnotationChildren = 0;
        for (AreaNode c : l.getChildren()) {
            if (c instanceof AnnotationArea)
                continue;
            else {
                consumed += c.getIPD();
                ++numNonAnnotationChildren;
            }
        }
        if ((l instanceof AnnotationArea) && (alignment == InlineAlignment.AUTO)) {
            int nb = (baseAdvances != null) ? baseAdvances.length : 0;
            int na = numNonAnnotationChildren;
            if (na == nb)
                alignment = InlineAlignment.WITH_BASE;
            else if (na < nb)
                alignment = (na > 1) ? InlineAlignment.SPACE_BETWEEN : InlineAlignment.SPACE_AROUND;
            else if (na > nb)
                alignment = InlineAlignment.CENTER;
            l.setAlignment(alignment);
        }
        double available = measure - consumed;
        int level = l.getBidiLevel();
        if (available > 0) {
            if (alignment == InlineAlignment.START) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, 0, level);
                l.addChild(a, LineArea.EXPAND_IPD);
            } else if (alignment == InlineAlignment.END) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, 0, level);
                l.insertChild(a, l.firstChild(), LineArea.EXPAND_IPD);
            } else if (alignment == InlineAlignment.CENTER) {
                double half = available / 2;
                AreaNode a1 = new InlineFillerArea(l.getElement(), half, 0, level);
                AreaNode a2 = new InlineFillerArea(l.getElement(), half, 0, level);
                l.insertChild(a1, l.firstChild(), LineArea.EXPAND_IPD);
                l.insertChild(a2, null, LineArea.EXPAND_IPD);
            } else {
                justifyTextAreas(l, measure, consumed, level, numNonAnnotationChildren, alignment, baseAdvances);
            }
        } else if (available < 0) {
            l.setOverflow(-available);
        }
    }

    private void justifyTextAreas(LineArea l, double measure, double consumed, int level, int numNonAnnotationChildren, InlineAlignment alignment, double[] baseAdvances) {
        double available = measure - consumed;
        if (alignment == InlineAlignment.JUSTIFY)
            alignment = InlineAlignment.SPACE_BETWEEN;
        int numFillers;
        if (alignment == InlineAlignment.SPACE_AROUND) {
            numFillers = numNonAnnotationChildren + 1;
        } else if (alignment == InlineAlignment.SPACE_BETWEEN) {
            numFillers = numNonAnnotationChildren - 1;
        } else if (alignment == InlineAlignment.WITH_BASE) {
            numFillers = justifyTextAreasWithBase(l, available, level, baseAdvances);
        } else
            numFillers = 0;
        double fill;
        if (numFillers > 0)
            fill = available / numFillers;
        else
            fill = 0;
        if (fill > 0) {
            List<AreaNode> children = new java.util.ArrayList<AreaNode>(l.getChildren());
            for (AreaNode c : children) {
                AreaNode f = new InlineFillerArea(l.getElement(), fill, 0, level);
                if ((c == children.get(0)) && (alignment == InlineAlignment.SPACE_BETWEEN))
                    continue;
                else
                    l.insertChild(f, c, LineArea.EXPAND_IPD);
            }
            if (alignment == InlineAlignment.SPACE_AROUND) {
                AreaNode f = new InlineFillerArea(l.getElement(), fill, 0, level);
                l.insertChild(f, null, LineArea.EXPAND_IPD);
            }
        }
    }

    private int justifyTextAreasWithBase(LineArea l, double available, int level, double[] baseAdvances) {
        List<AreaNode> leaves = new java.util.ArrayList<AreaNode>();
        for (AreaNode c : l.getChildren()) {
            if (c instanceof AnnotationArea)
                continue;
            else
                leaves.add(c);
        }
        if (leaves.size() != baseAdvances.length) {
            return 2;
        } else {
            for (int i = 0, n = baseAdvances.length; i < n; ++i)
                available -= baseAdvances[i];
            if (available < 0)
                available = 0;
            double s = available/2;
            int i = 0;
            for (AreaNode c : leaves) {
                double d = baseAdvances[i] - c.getIPD();
                s += d/2;
                if (s > 0) {
                    l.insertChild(new InlineFillerArea(l.getElement(), s, 0, level), c, LineArea.EXPAND_IPD);
                }
                s = d/2;
                ++i;
            }
            s += available/2;
            if (s > 0) {
                l.insertChild(new InlineFillerArea(l.getElement(), s, 0, level), null, LineArea.EXPAND_IPD);
            }
            return 0;
        }
    }

    private static class WhitespaceState {
        LineFeedTreatment lineFeedTreatment;
        SuppressAtLineBreakTreatment suppressAtLineBreakTreatment;
        boolean whitespaceCollapse;
        WhitespaceTreatment whitespaceTreatment;
        WhitespaceState(Whitespace whitespace) {
            LineFeedTreatment lineFeedTreatment;
            SuppressAtLineBreakTreatment suppressAtLineBreakTreatment;
            boolean whitespaceCollapse;
            WhitespaceTreatment whitespaceTreatment;
            if (whitespace == Whitespace.DEFAULT) {
                lineFeedTreatment = LineFeedTreatment.TREAT_AS_SPACE;
                suppressAtLineBreakTreatment = SuppressAtLineBreakTreatment.AUTO;
                whitespaceCollapse = true;
                whitespaceTreatment = WhitespaceTreatment.IGNORE_IF_SURROUNDING_LINEFEED;
            } else if (whitespace == Whitespace.PRESERVE) {
                lineFeedTreatment = LineFeedTreatment.PRESERVE;
                suppressAtLineBreakTreatment = SuppressAtLineBreakTreatment.RETAIN;
                whitespaceCollapse = false;
                whitespaceTreatment = WhitespaceTreatment.PRESERVE;
            } else
                throw new IllegalArgumentException();
            this.lineFeedTreatment = lineFeedTreatment;
            this.suppressAtLineBreakTreatment = suppressAtLineBreakTreatment;
            this.whitespaceCollapse = whitespaceCollapse;
            this.whitespaceTreatment = whitespaceTreatment;
        }
    }

    private static class InlineBreakOpportunity {
        TextRun run;            // associated text run
        InlineBreak type;       // type of break
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
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(run);
            sb.append('[');
            sb.append(start);
            sb.append(',');
            sb.append(index);
            sb.append(',');
            sb.append(Double.toString(advance));
            sb.append(']');
            return sb.toString();
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
        boolean suppressAfterLineBreak() {
            return run.suppressAfterLineBreak();
        }
        boolean suppressBeforeLineBreak() {
            return run.suppressBeforeLineBreak();
        }
    }

    private class TextRun {
        int start;                                              // start index in outer iterator
        int end;                                                // end index in outer iterator
        List<StyleAttributeInterval> fontIntervals;             // cached font sub-intervals over complete run interval
        Orientation orientation;                                // dominant glyph orientation for run
        Combine combine;                                        // dominant combine for run
        int level;                                              // bidirectional level
        String text;                                            // cached text over complete run interval
        TextRun(int start, int end) {
            this.start = start;
            this.end = end;
            int l = end - start;
            this.fontIntervals = getFontIntervals(0, l, font);
            this.orientation = getDominantOrientation(0, l, defaults.getOrientation());
            this.combine = getDominantCombine(0, l, defaults.getCombine());
            this.level = getDominantLevel(0, l);
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getText());
            return sb.toString();
        }
        // obtain all font intervals associated with run
        List<StyleAttributeInterval> getFontIntervals() {
            return fontIntervals;
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
        // obtain decorations starting at FROM to TO of run, where FROM and TO are indices into run, not outer iterator
        List<Decoration> getDecorations(int from, int to) {
            Set<StyleAttributeInterval> intervals = new java.util.TreeSet<StyleAttributeInterval>();
            intervals.addAll(getColorIntervals(from, to));
            intervals.addAll(getOutlineIntervals(from, to));
            List<Decoration> decorations = new java.util.ArrayList<Decoration>();
            for (StyleAttributeInterval i : intervals) {
                Decoration.Type t;
                Object v = i.getValue();
                if (v != null) {
                    if (v instanceof Color)
                        t = Decoration.Type.COLOR;
                    else if (v instanceof Outline)
                        t = Decoration.Type.OUTLINE;
                    else
                        t = null;
                    decorations.add(new Decoration(i.getBegin() - start, i.getEnd() - start, t, v));
                }
            }
            return decorations;
        }
        // obtain inline break type at INDEX of run, where INDEX is index into run, not outer iterator
        InlineBreak getInlineBreak(int index) {
            if (index > 0) {
                char c = getText(index - 1, index).charAt(0);
                if (Characters.isLineSeparator(c))
                    return InlineBreak.HARD;
                else if (Characters.isBreakingWhitespace(c))
                    return InlineBreak.SOFT_WHITESPACE;
                else if (Characters.isHyphenationPoint(c))
                    return InlineBreak.SOFT_HYPHENATION_POINT;
                else if (Characters.isCJKIdeograph(c))
                    return InlineBreak.SOFT_IDEOGRAPH;
            }
            return InlineBreak.UNKNOWN;
        }
        // obtain advance of text starting at FROM to TO of run, where FROM and TO are indices into run, not outer iterator
        double getAdvance(int from, int to, double available) {
            double advance = 0;
            for (StyleAttributeInterval fai : fontIntervals) {
                Font font = (Font) fai.getValue();
                if (font == null)
                    continue;
                else if (fai.isOuterScope()) {
                    advance += getAdvance(getText().substring(from, to), orientation, combine);
                    break;
                } else {
                    int[] intersection = fai.intersection(start + from, start + to);
                    if (intersection != null) {
                        int f = intersection[0] - start;
                        int t = intersection[1] - start;
                        advance += getAdvance(getText().substring(f,t), orientation, combine);
                    }
                }
            }
            return advance;
        }
        double getAdvance(String text, Orientation orientation, Combine combine) {
            if (font.isVertical() && (orientation == Orientation.ROTATE090)) {
                if (!combine.isNone())
                    return lineHeight;
                else
                    return font.getRotatedAdvance(text);
            } else
                return font.getAdvance(text);
        }
        // determine if content associate with break is suppressed after line break
        boolean suppressAfterLineBreak() {
            return false;
        }
        // determine if content associate with break is suppressed before line break
        boolean suppressBeforeLineBreak() {
            return false;
        }
        // obtain fonts for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getFontIntervals(int from, int to, Font defaultFont) {
            StyleAttribute fontAttr = StyleAttribute.FONT;
            List<StyleAttributeInterval> fonts = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, fontAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(fontAttr);
                if (v == null)
                    v = defaultFont;
                fonts.add(new StyleAttributeInterval(fontAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            if (fonts.isEmpty())
                fonts.add(new StyleAttributeInterval(fontAttr, defaultFont, -1, -1));
            return fonts;
        }
        // obtain colors for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getColorIntervals(int from, int to) {
            StyleAttribute colorAttr = StyleAttribute.COLOR;
            List<StyleAttributeInterval> colors = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, colorAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(colorAttr);
                if (v != null)
                    colors.add(new StyleAttributeInterval(colorAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return colors;
        }
        // obtain outline for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getOutlineIntervals(int from, int to) {
            StyleAttribute outlineAttr = StyleAttribute.OUTLINE;
            List<StyleAttributeInterval> outlines = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, outlineAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(outlineAttr);
                if (v != null)
                    outlines.add(new StyleAttributeInterval(outlineAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return outlines;
        }
        // obtain dominant orientation for specified interval FROM to TO of run
        private Orientation getDominantOrientation(int from, int to, Orientation defaultOrientation) {
            for (StyleAttributeInterval sai : getOrientationIntervals(from, to)) {
                Orientation orientation = (Orientation) sai.getValue();
                if (orientation.isRotated())
                    return orientation;
            }
            return defaultOrientation;
        }
        // obtain orientation for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getOrientationIntervals(int from, int to) {
            StyleAttribute orientationAttr = StyleAttribute.ORIENTATION;
            List<StyleAttributeInterval> orientations = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, orientationAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(orientationAttr);
                if (v != null)
                    orientations.add(new StyleAttributeInterval(orientationAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return orientations;
        }
        // obtain dominant combine for specified interval FROM to TO of run
        private Combine getDominantCombine(int from, int to, Combine defaultCombine) {
            if (orientation.isRotated()) {
                for (StyleAttributeInterval sai : getCombineIntervals(from, to)) {
                    Combine combine = (Combine) sai.getValue();
                    if (!combine.isNone())
                        return combine;
                }
            }
            return defaultCombine;
        }
        // obtain combine for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getCombineIntervals(int from, int to) {
            StyleAttribute combineAttr = StyleAttribute.COMBINE;
            List<StyleAttributeInterval> combines = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, combineAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(combineAttr);
                if (v != null)
                    combines.add(new StyleAttributeInterval(combineAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return combines;
        }
        // obtain dominant bidi level for run, which must be the same value from start to end in outer iterator
        private int getDominantLevel(int from, int to) {
            StyleAttribute bidiAttr = StyleAttribute.BIDI;
            int[] intervals = getAttributeIntervals(from, to, bidiAttr);
            if (intervals.length >= 2) {
                AttributedCharacterIterator aci = iterator;
                int savedIndex = aci.getIndex();
                int s = start + intervals[0];
                int e = start + intervals[1];
                assert s == start;
                assert e == end;
                aci.setIndex(s);
                Object v = aci.getAttribute(bidiAttr);
                int level = (v != null) ? (int) v : -1;
                aci.setIndex(savedIndex);
                return level;
            } else
                return -1;
        }
        // obtain intervals over [FROM,TO) for which ATTRIBUTE is defined
        private int[] getAttributeIntervals(int from, int to, StyleAttribute attribute) {
            List<Integer> indices = new java.util.ArrayList<Integer>();
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            int b = start;
            int e = end;
            aci.setIndex(b);
            while (aci.getIndex() < e) {
                int s = aci.getRunStart(attribute);
                int l = aci.getRunLimit(attribute);
                if (s < b)
                    s = b;
                indices.add(s - b);
                aci.setIndex(l);
                if (l > e)
                    l = e;
                indices.add(l - b);
            }
            aci.setIndex(savedIndex);
            return Integers.toArray(indices);
        }
    }

    private class WhitespaceRun extends TextRun {
        private WhitespaceState whitespace;
        WhitespaceRun(int start, int end, WhitespaceState whitespace) {
            super(start, end);
            this.whitespace = whitespace;
        }
        @Override
        String getText(int from, int to) {
            String t = super.getText(from, to);
            t = processLineFeedTreatment(t, whitespace.lineFeedTreatment);
            t = processWhitespaceCollapse(t, whitespace.whitespaceCollapse);
            t = processWhitespaceTreatment(t, whitespace.whitespaceTreatment);
            return t;
        }
        @Override
        boolean suppressAfterLineBreak() {
            return suppressAtLineBreak(whitespace.suppressAtLineBreakTreatment, false);
        }
        @Override
        boolean suppressBeforeLineBreak() {
            return suppressAtLineBreak(whitespace.suppressAtLineBreakTreatment, true);
        }
        private String processLineFeedTreatment(String t, LineFeedTreatment treatment) {
            if (treatment == LineFeedTreatment.PRESERVE)
                return t;
            else {
                StringBuffer sb = new StringBuffer();
                for (int i = 0, n = t.length(); i < n; ++i) {
                    char c = t.charAt(i);
                    if (c != Characters.UC_LF)
                        sb.append(c);
                    else if (treatment == LineFeedTreatment.IGNORE)
                        continue;
                    else if (treatment == LineFeedTreatment.TREAT_AS_SPACE)
                        sb.append((char) Characters.UC_SPACE);
                    else if (treatment == LineFeedTreatment.TREAT_AS_ZERO_WIDTH_SPACE)
                        sb.append((char) Characters.UC_SPACE_ZWSP);
                }
                return sb.toString();
            }
        }
        private String processWhitespaceCollapse(String t, boolean collapse) {
            if (!collapse)
                return t;
            else {
                StringBuffer sb = new StringBuffer();
                boolean inSpace = false;
                for (int i = 0, n = t.length(); i < n; ++i) {
                    char c = t.charAt(i);
                    if (c == Characters.UC_SPACE)
                        inSpace = true;
                    else {
                        if (inSpace)
                            sb.append((char) Characters.UC_SPACE);
                        sb.append(c);
                        inSpace = false;
                    }
                }
                if (inSpace)
                    sb.append((char) Characters.UC_SPACE);
                return sb.toString();
            }
        }
        private String processWhitespaceTreatment(String t, WhitespaceTreatment treatment) {
            return t;
        }
        private boolean suppressAtLineBreak(SuppressAtLineBreakTreatment suppressAtLineBreakTreatment, boolean before) {
            if (suppressAtLineBreakTreatment == SuppressAtLineBreakTreatment.RETAIN)
                return false;
            else if (suppressAtLineBreakTreatment == SuppressAtLineBreakTreatment.SUPPRESS)
                return true;
            else if (suppressAtLineBreakTreatment == SuppressAtLineBreakTreatment.AUTO)
                return suppressAtLineBreakAuto(before);
            else
                return false;
        }
        private boolean suppressAtLineBreakAuto(boolean before) {
            String t = getText();
            for (int i = 0, n = t.length(); i < n; ++i) {
                char c = t.charAt(i);
                if (c != Characters.UC_SPACE)
                    return false;
            }
            return true;
        }
    }

    private class NonWhitespaceRun extends TextRun {
        NonWhitespaceRun(int start, int end) {
            super(start, end);
        }
        @Override
        String getText(int from, int to) {
            String text = super.getText(from, to);
            StringBuffer sb = new StringBuffer();
            for (StyleAttributeInterval fai : fontIntervals) {
                if (fai.isOuterScope()) {
                    sb.append(processFeatures(text, (Font) fai.getValue()));
                    break;
                } else {
                    int[] intersection = fai.intersection(start + from, start + to);
                    if (intersection != null) {
                        int f = intersection[0] - start - from;
                        int t = intersection[1] - start - from;
                        sb.append(processFeatures(text.substring(f,t), (Font) fai.getValue()));
                    }
                }
            }
            return sb.toString();
        }
        private String processFeatures(String t, Font font) {
            Collection<FontFeature> features = (font != null) ? font.getFeatures() : null;
            if (features != null) {
                for (FontFeature feature : features) {
                    if (feature.getFeature().equals("hwid"))
                        t = processHalfWidth(t);
                    else if (feature.getFeature().equals("fwid"))
                        t = processFullWidth(t);
                    if (feature.getFeature().equals("vert"))
                        t = processVertical(t);
                }
            }
            return t;
        }
        private String processHalfWidth(String t) {
            return Strings.toHalfWidth(t);
        }
        private String processFullWidth(String t) {
            return Strings.toFullWidth(t);
        }
        private String processVertical(String t) {
            return Strings.toVertical(t);
        }
    }

    private class EmbeddingRun extends NonWhitespaceRun {
        private Object embedding;
        private InlineBlockArea area;
        EmbeddingRun(int index, Object embedding) {
            super(index, index + 1);
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
            area.addChildren(new ParagraphLayout(embedding, state).layout(available, Consume.FIT), LineArea.EXPAND_LINE);
            return area;
        }
    }

}
