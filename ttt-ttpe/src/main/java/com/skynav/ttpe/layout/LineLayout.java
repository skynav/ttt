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

package com.skynav.ttpe.layout;

import java.text.AttributedCharacterIterator;
import java.text.CharacterIterator;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.Stack;

import com.skynav.ttpe.area.AnnotationArea;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.Inline;
import com.skynav.ttpe.area.InlineBlockArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.InlinePaddingArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.fonts.Combination;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontFeature;
import com.skynav.ttpe.fonts.GlyphMapping;
import com.skynav.ttpe.fonts.Orientation;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.Shear;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.AnnotationPosition;
import com.skynav.ttpe.style.AnnotationReserve;
import com.skynav.ttpe.style.BackgroundColor;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.LineFeedTreatment;
import com.skynav.ttpe.style.Outline;
import com.skynav.ttpe.style.Padding;
import com.skynav.ttpe.style.StyleAttribute;
import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.SuppressAtLineBreakTreatment;
import com.skynav.ttpe.style.Visibility;
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
    private String script;
    private String language;
    private AttributedCharacterIterator iterator;

    // layout state
    private LayoutState state;
    private Defaults defaults;
    private int lineNumber;

    // style related state
    private AnnotationReserve annotationReserve;
    private int bidiLevel;
    private double bpd;
    private Color color;
    private double ipd;
    private double lineShear;
    private Outline outline;
    private double shear;
    private InlineAlignment textAlign;
    private Visibility visibility;
    private Wrap wrap;
    private WritingMode writingMode;

    // derived style state
    private Font firstAvailableFont;
    private double lineHeight;
    private double lineShearAngle;
    private double shearAngle;
    private WhitespaceState whitespace;

    // other style state
    private Stack<Padding> padding;

    public LineLayout(Phrase content, LayoutState state) {
        Defaults defaults = state.getDefaults();
        // content state
        this.content = content;
        this.script = content.getScript(-1, defaults);
        this.language = content.getLanguage(-1, defaults);
        this.iterator = content.getIterator();
        // layout state
        this.state = state;
        this.defaults = defaults;
        // area derived state
        this.bidiLevel = state.getBidiLevel();
        this.writingMode = state.getWritingMode();
        // outer context styles
        this.annotationReserve = content.getAnnotationReserve(-1, defaults);
        this.bpd = content.getBPD(-1, defaults);
        this.color = content.getColor(-1, defaults);
        this.ipd = content.getBPD(-1, defaults);
        this.lineShear = content.getLineShear(-1, defaults);
        this.outline = content.getOutline(-1, defaults);
        this.shear = content.getShear(-1, defaults);
        this.textAlign = relativizeAlignment(content.getTextAlign(-1, defaults), this.writingMode);
        this.visibility = content.getVisibility(-1, defaults);
        this.wrap = content.getWrapOption(-1, defaults);
        // derived styles
        this.firstAvailableFont = content.getFirstAvailableFont(-1, defaults);
        this.lineHeight = content.getLineHeight(-1, defaults, firstAvailableFont);
        this.lineShearAngle = Shear.toAngle(this.lineShear);
        this.shearAngle = Shear.toAngle(this.shear);
        this.whitespace = new WhitespaceState(state.getWhitespace());
        // other style state
        this.padding = new Stack<Padding>();
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

    public List<? extends LineArea> layout(double bpdAvailable, double ipdAvailable, Consume consume) {
        if (Math.abs(shearAngle) > 0.0001) {
            double bpd = this.bpd;
            if (bpd > 0) {
                ipdAvailable -= Math.abs(bpd * Math.tan(Math.toRadians(shearAngle)));
            }
        }
        return layout(ipdAvailable, consume);
    }
    
    public List<? extends LineArea> layout(double available, Consume consume) {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        if (Math.abs(lineShearAngle) > 0.0001) {
            available -= Math.abs(lineHeight * Math.tan(Math.toRadians(lineShearAngle)));
        }
        if (available > 0) {
            double consumed = 0;
            double sPadding = 0;
            double ePadding = 0;
            List<InlineBreakOpportunity> breaks = new java.util.ArrayList<InlineBreakOpportunity>();
            LineBreakIterator lbi = state.getBreakIterator();
            LineBreakIterator lci = state.getCharacterIterator();
            LineBreakIterator bi;
            InlineBreakOpportunity bPrev = null;
            for (TextRun r = getNextTextRun(); r != null;) {
                if (!r.ignore() && (!breaks.isEmpty() || !r.suppressAfterLineBreak())) {
                    bi = updateIterator(lbi, r);
                    for (InlineBreakOpportunity b = getNextBreakOpportunity(bi, r, available - consumed); b != null; ) {
                        if (b.isHard()) {
                            lines.add(emit(available, consumed, ePadding, consume, breaks));
                            consumed = 0;
                            ePadding = 0;
                            break;
                        } else {
                            double advance = b.getAdvance();
                            double advanceShear = b.getShearAdvance(bPrev);
                            if (advanceShear != 0)
                                advance += Math.abs(advanceShear);
                            Padding p = b.getPadding();
                            if (p != null) {
                                if (padding.empty() || (p != padding.peek())) {
                                    padding.push(p);
                                    sPadding += p.getStart();
                                    ePadding += p.getEnd();
                                } else if (breaks.isEmpty()) {
                                    sPadding += p.getStart();
                                    ePadding += p.getEnd();
                                }
                            } else if (!padding.empty()) {
                                padding.pop();
                            }
                            if ((consumed + advance) > (available - (sPadding + ePadding))) {
                                if (wrap == Wrap.WRAP) {
                                    if (!breaks.isEmpty()) {
                                        lines.add(emit(available, consumed, ePadding, consume, breaks));
                                        consumed = 0;
                                        ePadding = 0;
                                    } else {
                                        if (bi != lci)
                                            bi = updateIterator(lci, b);
                                        bPrev = b; b = getNextBreakOpportunity(bi, r, available - consumed);
                                    }
                                    continue;
                                }
                            }
                            consumed += advance;
                            if (sPadding > 0) {
                                b.startPadding(sPadding);
                                consumed += sPadding;
                                sPadding = 0;
                            }
                            breaks.add(b);
                            bPrev = b; b = getNextBreakOpportunity(bi, r, available - consumed);
                        }
                    }
                }
                r = getNextTextRun();
            }
            if (!breaks.isEmpty())
                lines.add(emit(available, consumed, ePadding, consume, breaks));
        }
        return reorder(align(lines));
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
        } else if (isIgnoredControl(c)) {
            iterator.setIndex(s + 1);
            return new IgnoredControlRun(s);
        }
        boolean inBreakingWhitespace = Characters.isBreakingWhitespace(c);
        while ((c = iterator.next()) != CharacterIterator.DONE) {
            if (c == Characters.UC_OBJECT)
                break;
            else if (isIgnoredControl(c))
                break;
            else if (inBreakingWhitespace ^ Characters.isBreakingWhitespace(c))
                break;
            else if (hasTextRunBreakingAttribute(iterator))
                break;
        }
        int e = iterator.getIndex();
        return inBreakingWhitespace ? new WhitespaceRun(s, e, getWhitespaceState(s, e)) : new NonWhitespaceRun(s, e);
    }

    private static boolean isIgnoredControl(int c) {
        return Characters.isBidiControl(c);
    }

    private static boolean hasTextRunBreakingAttribute(AttributedCharacterIterator iterator) {
        return hasTextRunBreakingAttribute(iterator, iterator.getIndex());
    }

    private static final Set<StyleAttribute> textRunBreakingAttributes;
    static {
        Set<StyleAttribute> s = new java.util.HashSet<StyleAttribute>();
        s.add(StyleAttribute.ANNOTATIONS);
        s.add(StyleAttribute.BIDI);
        s.add(StyleAttribute.COMBINATION);
        s.add(StyleAttribute.ORIENTATION);
        s.add(StyleAttribute.PADDING);
        s.add(StyleAttribute.WHITESPACE);
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

    private WhitespaceState getWhitespaceState(int s, int e) {
        WhitespaceState ws = this.whitespace;
        int savedIndex = iterator.getIndex();
        iterator.setIndex(s);
        Whitespace w = (Whitespace) iterator.getAttribute(StyleAttribute.WHITESPACE);
        if (w != null) {
            if (w.equals(Whitespace.DEFAULT))
                ws = WhitespaceState.DEFAULT;
            else if (w.equals(Whitespace.PRESERVE))
                ws = WhitespaceState.PRESERVE;
        }
        iterator.setIndex(savedIndex);
        return ws;
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
                return new InlineBreakOpportunity(r, r.getInlineBreak(to), from, to, r.getAdvance(from, to, script, language, available));
        }
        return null;
    }

    private LineArea emit(double available, double consumed, double endPadding, Consume consume, List<InlineBreakOpportunity> breaks) {
        consumed = maybeRemoveLeadingWhitespace(breaks, consumed);
        consumed = maybeRemoveTrailingWhitespace(breaks, consumed);
        if (!breaks.isEmpty()) {
            if (endPadding > 0) {
                InlineBreakOpportunity bLast = breaks.get(breaks.size() - 1);
                bLast.endPadding(endPadding);
                consumed += endPadding;
            }
        }
        LineArea la = newLine(content, consume == Consume.MAX ? available : consumed, lineHeight, bidiLevel, lineShearAngle, visibility, textAlign, color, firstAvailableFont);
        return addTextAreas(la, breaks);
    }

    protected LineArea newLine(Phrase p, double ipd, double bpd, int bidiLevel, double lineShearAngle, Visibility visibility, InlineAlignment textAlign, Color color, Font font) {
        return new LineArea(p.getElement(), ipd, bpd, bidiLevel, lineShearAngle, visibility, textAlign, color, font, ++lineNumber, p.isEmbedding());
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
            double sPadding = 0;
            double ePadding = 0;
            for (InlineBreakOpportunity b : breaks) {
                TextRun r = b.run;
                if ((lastRun != null) && ((r != lastRun) || (l instanceof AnnotationArea))) {
                    if (!(l instanceof AnnotationArea))
                        maybeAddAnnotationAreas(l, lastRunStart, r.start, firstAvailableFont, advance, lineHeight);
                    addTextArea(l, sb.toString(), decorations, firstAvailableFont, advance, sPadding, ePadding, lineHeight, lastRun, lastRunStart);
                    sb.setLength(0);
                    decorations.clear();
                    advance = 0;
                    sPadding = 0;
                    ePadding = 0;
                    lastRunStart = -1;
                }
                sb.append(r.getText(b.start, b.index));
                decorations.addAll(r.getDecorations(b.start, b.index));
                advance += b.getAdvance();
                sPadding = b.startPadding();
                ePadding = b.endPadding();
                lastRun = r;
                if (lastRunStart < 0)
                    lastRunStart = r.start + b.start;
            }
            if (sb.length() > 0) {
                if (!(l instanceof AnnotationArea))
                    maybeAddAnnotationAreas(l, lastRunStart, lastRun != null ? lastRun.end : -1, firstAvailableFont, advance, lineHeight);
                addTextArea(l, sb.toString(), decorations, firstAvailableFont, advance, sPadding, ePadding, lineHeight, lastRun, lastRunStart);
            }
            iterator.setIndex(savedIndex);
            breaks.clear();
        }
        return l;
    }

    private double maybeRemoveLeadingWhitespace(List<InlineBreakOpportunity> breaks, double consumed) {
        while (!breaks.isEmpty()) {
            int i = 0;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.suppressAfterLineBreak()) {
                breaks.remove(i);
                consumed -= b.getAdvance();
            } else
                break;
        }
        return consumed;
    }

    private double maybeRemoveTrailingWhitespace(List<InlineBreakOpportunity> breaks, double consumed) {
        while (!breaks.isEmpty()) {
            int i = breaks.size() - 1;
            InlineBreakOpportunity b = breaks.get(i);
            if (b.suppressBeforeLineBreak()) {
                breaks.remove(i);
                consumed -= b.getAdvance();
            } else
                break;
        }
        return consumed;
    }

    private void addTextArea(LineArea l, String text, List<Decoration> decorations, Font font, double advance, double startPadding, double endPadding, double lineHeight, TextRun run, int runStart) {
        if (startPadding > 0) {
            List<Decoration> d = getSegmentDecorations(decorations, 0, 1);
            l.addChild(new InlinePaddingArea(content.getElement(), startPadding, lineHeight, run.bidiLevel, d), LineArea.ENCLOSE_ALL);
        }
        if (run instanceof IgnoredControlRun) {
            return;
        } else if (run instanceof WhitespaceRun) {
            if (advance > 0) {
                List<Decoration> d = getSegmentDecorations(decorations, 0, text.length());
                l.addChild(new SpaceArea(content.getElement(), advance, lineHeight, run.bidiLevel, text, font, d), LineArea.ENCLOSE_ALL);
            }
        } else if (run instanceof EmbeddingRun) {
            l.addChild(((EmbeddingRun) run).getArea(), LineArea.ENCLOSE_ALL);
        } else if (run instanceof NonWhitespaceRun) {
            int start = run.start;
            for (StyleAttributeInterval fai : run.getFontIntervals()) {
                double bpd = lineHeight;
                String t;
                Font f;
                List<Decoration> d;
                if (fai.isOuterScope()) {
                    t = text;
                    f = font;
                    d = decorations;
                } else {
                    int i = fai.getBegin() - start;
                    assert i >= 0;
                    assert i < text.length();
                    int j = fai.getEnd() - start;
                    assert j >= 0;
                    assert j >= i;
                    if (j > text.length())
                        j = text.length();
                    t = text.substring(i, j);
                    if ((f = (Font) fai.getValue()) != null) {
                        int segOffset = runStart - start;
                        d = getSegmentDecorations(decorations, i + segOffset, j + segOffset);
                    } else
                        d = null;
                }
                if (f != null) {
                    GlyphMapping gm =
                        f.getGlyphMapping(t, makeGlyphMappingFeatures(script, language, run.bidiLevel, f.getAxis(), f.isKerningEnabled(), run.orientation, run.combination));
                    if (gm != null) {
                        double ipd = f.getScaledAdvance(gm);
                        boolean cross = !run.combination.isNone();
                        if (cross)
                            ipd += run.getShearAdvance();
                        GlyphArea a = new GlyphArea(content.getElement(), ipd, bpd, run.bidiLevel, f, gm, d);
                        l.addChild(a, !cross ? LineArea.ENCLOSE_ALL : LineArea.ENCLOSE_ALL_CROSS);
                    }
                }
                if (fai.isOuterScope())
                    break;
            }
        }
        if (endPadding > 0) {
            List<Decoration> d = getSegmentDecorations(decorations, text.length() - 1, text.length());
            l.addChild(new InlinePaddingArea(content.getElement(), endPadding, lineHeight, run.bidiLevel, d), LineArea.ENCLOSE_ALL);
        }
    }

    private SortedSet<FontFeature>
        makeGlyphMappingFeatures(String script, String language, int bidiLevel, Axis axis, boolean kerned, Orientation orientation, Combination combination) {
        SortedSet<FontFeature> features = new java.util.TreeSet<FontFeature>();
        if ((script != null) && !script.isEmpty())
            features.add(FontFeature.SCPT.parameterize(script));
        if ((language != null) && !language.isEmpty())
            features.add(FontFeature.LANG.parameterize(language));
        if (bidiLevel >= 0)
            features.add(FontFeature.BIDI.parameterize(bidiLevel));
        if (kerned)
            features.add(FontFeature.KERN.parameterize(Boolean.TRUE));
        else
            features.add(FontFeature.KERN.parameterize(Boolean.FALSE));
        if ((orientation != null) && orientation.isRotated())
            features.add(FontFeature.ORNT.parameterize(orientation));
        if ((combination != null) && !combination.isNone())
            features.add(FontFeature.COMB.parameterize(combination));
        if ((axis != null) && axis.cross(!combination.isNone()).isVertical() && !orientation.isRotated())
            features.add(FontFeature.VERT.parameterize(Boolean.TRUE));
        return features;
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
            int annoStart = iterator.getRunStart(StyleAttribute.ANNOTATIONS);
            int annoLimit = iterator.getRunLimit(StyleAttribute.ANNOTATIONS);
            if (annoStart == start) {
                Phrase[] annotations = (Phrase[]) iterator.getAttribute(StyleAttribute.ANNOTATIONS);
                if (annotations != null) {
                    Object[] baseInfo = new Object[3];
                    String base = getAnnotationBase(annoStart, annoLimit, baseInfo);
                    Orientation[] baseOrientations = getAnnotationBaseOrientations(annoStart, annoLimit);
                    addAnnotationAreas(l, base, (String) baseInfo[0], (String) baseInfo[1], (int) baseInfo[2], baseOrientations, annotations, font, advance, lineHeight);
                }
            }
        }
    }

    private String getAnnotationBase(int start, int end, Object[] baseInfo) {
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
        if (baseInfo != null) {
            baseInfo[0] = this.script;
            baseInfo[1] = this.language;
            baseInfo[2] = Integer.valueOf(this.bidiLevel);
        }
        return sb.toString();
    }

    private Orientation[] getAnnotationBaseOrientations(int start, int end) {
        int savedIndex = iterator.getIndex();
        Orientation[] orientations = new Orientation[end - start];
        for (int i = start; i < end;) {
            iterator.setIndex(i);
            Orientation o = (Orientation) iterator.getAttribute(StyleAttribute.ORIENTATION);
            if (o != null) {
                int j = i;
                int annoLimit = iterator.getRunLimit(StyleAttribute.ORIENTATION);
                int k = annoLimit;
                if (k > end)
                    k = end;
                while (j < k)
                    orientations[j++ - start] = o;
                i = annoLimit;
            } else
                ++i;
        }
        boolean found = false;
        for (int i = 0, n = orientations.length; !found && (i < n); ++i) {
            Orientation o = orientations[i];
            if ((o != null) && o.isRotated())
                found = true;
        }
        iterator.setIndex(savedIndex);
        return found ? orientations : null;
    }

    private void addAnnotationAreas(LineArea l, String base, String baseScript, String baseLanguage, int baseBidiLevel, Orientation[] baseOrientations, Phrase[] annotations, Font font, double advance, double lineHeight) {
        for (Phrase p : annotations) {
            InlineAlignment annotationAlign = p.getAnnotationAlign(-1, defaults);
            Double annotationOffset = p.getAnnotationOffset(-1, defaults);
            AnnotationPosition annotationPosition = p.getAnnotationPosition(-1, defaults);
            for (AnnotationArea a :  new AnnotationLayout(p, state).layout()) {
                a.setAlignment(annotationAlign);
                a.setOffset(annotationOffset);
                a.setPosition(annotationPosition);
                alignTextAreas(a, advance, annotationAlign, getBaseAdvances(base, baseScript, baseLanguage, baseBidiLevel, baseOrientations, font));
                l.addChild(a, LineArea.ENCLOSE_ALL);
            }
        }
    }

    private double[] getBaseAdvances(String base, String baseScript, String baseLanguage, int baseBidiLevel, Orientation[] baseOrientations, Font font) {
        if (base == null)
            return null;
        else {
            int baseLength = base.length();
            double[] advances = new double[baseLength];
            boolean kerningEnabled = font.isKerningEnabled();
            Axis axis = font.getAxis();
            for (int i = 0, n = baseLength; i < n; ++i) {
                int j = i + 1;
                Orientation orientation = null;
                if ((baseOrientations != null) && (i < baseOrientations.length))
                    orientation = baseOrientations[i];
                if (orientation == null)
                    orientation = Orientation.ROTATE000;
                String baseText = base.substring(i, j);
                GlyphMapping gm =
                    font.getGlyphMapping(baseText, makeGlyphMappingFeatures(baseScript, baseLanguage, baseBidiLevel, axis, kerningEnabled, orientation, Combination.NONE));
                if (gm != null)
                    advances[i] = font.getScaledAdvance(gm);
            }
            return advances;
        }
    }

    private List<LineArea> reorder(List<LineArea> lines) {
        for (LineArea l : lines)
            reorder(l);
        return lines;
    }

    private void reorder(LineArea l) {
        // 1. extract runs from children
        List<AreaNode> runs = l.getChildren();

        // 2. determine minimum and maximum levels
        int[] mm = computeMinMaxLevels(runs);

        // 3. reorder from maximum to minimum level
        int mn = mm[0];
        int mx = mm[1];
        if (mx > 0) {
            for (int l1 = mx, l2 = ((mn & 1) == 0) ? (mn + 1) : mn; l1 >= l2; --l1) {
                runs = reorderRuns(runs, l1);
            }
        } else
            return;

        // 4. reverse glyphs in runs while mirroring
        boolean mirror = true;
        maybeReverseGlyphs(runs, mirror);

        // 5. replace children with reordered runs
        l.setChildren(runs);
    }

    private int[] computeMinMaxLevels(List<AreaNode> runs) {
        int mn = -1;
        int mx = -1;
        for (AreaNode n : runs) {
            if ((n instanceof Inline) && !(n instanceof AnnotationArea)) {
                int bidiLevel = n.getBidiLevel();
                if (bidiLevel >= 0) {
                    if ((mn < 0) || (bidiLevel < mn))
                        mn = bidiLevel;
                    if ((mx < 0) || (bidiLevel > mx))
                        mx = bidiLevel;
                }
            }
        }
        return new int[] { mn, mx };
    }

    private List<AreaNode> reorderRuns(List<AreaNode> runs, int bidiLevel) {
        assert bidiLevel >= 0;
        List<AreaNode> runsNew = new java.util.ArrayList<AreaNode>();
        for (int i = 0, n = runs.size(); i < n; ++i) {
            AreaNode ni = runs.get(i);
            if (ni.getBidiLevel() < bidiLevel) {
                runsNew.add(ni);
            } else {
                int s = i;
                int e = s;
                while (e < n) {
                    AreaNode ne = runs.get(e);
                    if (ne.getBidiLevel() < bidiLevel) {
                        break;
                    } else {
                        e++;
                    }
                }
                if (s < e) {
                    runsNew.addAll(reverseRuns(runs, s, e));
                }
                i = e - 1;
            }
        }
        if (!runsNew.equals(runs)) {
            runs = runsNew;
        }
        return runs;
    }

    private static List<AreaNode> reverseRuns(List<AreaNode> runs, int s, int e) {
        int n = e - s;
        List<AreaNode> runsNew = new java.util.ArrayList<AreaNode>(n);
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                int k = (n - i - 1);
                AreaNode nk = runs.get(s + k);
                nk.reverse();
                runsNew.add(nk);
            }
        }
        return runsNew;
    }

    private void maybeReverseGlyphs(List<AreaNode> runs, boolean mirror) {
        for (AreaNode n : runs) {
            if (n instanceof GlyphArea)
                ((GlyphArea) n).maybeReverseGlyphs(mirror);
        }
    }

    private List<LineArea> align(List<LineArea> lines) {
        lines = alignIPD(lines);
        lines = alignBPD(lines);
        return lines;
    }

    private List<LineArea> alignIPD(List<LineArea> lines) {
        double maxMeasure = 0;
        for (LineArea l : lines) {
            double measure = l.getIPD();
            if (measure > maxMeasure)
                maxMeasure = measure;
        }
        for (LineArea l : lines) {
            if (!(l instanceof AnnotationArea))
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
            if (nb > 0) {
                double baseMeasure = 0;
                for (int i = 0; i < nb; ++i)
                    baseMeasure += baseAdvances[i];
                if (measure < baseMeasure)
                    measure = baseMeasure;
            }
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
        int bidiLevel = l.getBidiLevel();
        if (available > 0) {
            if (alignment == InlineAlignment.START) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, 0, bidiLevel);
                l.addChild(a, LineArea.EXPAND_IPD);
            } else if (alignment == InlineAlignment.END) {
                AreaNode a = new InlineFillerArea(l.getElement(), available, 0, bidiLevel);
                l.insertChild(a, l.firstChild(), LineArea.EXPAND_IPD);
            } else if (alignment == InlineAlignment.CENTER) {
                double half = available / 2;
                AreaNode a1 = new InlineFillerArea(l.getElement(), half, 0, bidiLevel);
                AreaNode a2 = new InlineFillerArea(l.getElement(), half, 0, bidiLevel);
                l.insertChild(a1, l.firstChild(), LineArea.EXPAND_IPD);
                l.insertChild(a2, null, LineArea.EXPAND_IPD);
            } else {
                justifyTextAreas(l, measure, consumed, bidiLevel, numNonAnnotationChildren, alignment, baseAdvances);
            }
        } else if (available < 0) {
            l.setOverflow(-available);
        }
    }

    private void justifyTextAreas(LineArea l, double measure, double consumed, int bidiLevel, int numNonAnnotationChildren, InlineAlignment alignment, double[] baseAdvances) {
        double available = measure - consumed;
        if (alignment == InlineAlignment.JUSTIFY)
            alignment = InlineAlignment.SPACE_BETWEEN;
        int numFillers;
        if (alignment == InlineAlignment.SPACE_AROUND) {
            numFillers = numNonAnnotationChildren + 1;
        } else if (alignment == InlineAlignment.SPACE_BETWEEN) {
            numFillers = numNonAnnotationChildren - 1;
        } else if (alignment == InlineAlignment.WITH_BASE) {
            numFillers = justifyTextAreasWithBase(l, available, bidiLevel, baseAdvances);
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
                AreaNode f = new InlineFillerArea(l.getElement(), fill, 0, bidiLevel);
                if ((c == children.get(0)) && (alignment == InlineAlignment.SPACE_BETWEEN))
                    continue;
                else
                    l.insertChild(f, c, LineArea.EXPAND_IPD);
            }
            if (alignment == InlineAlignment.SPACE_AROUND) {
                AreaNode f = new InlineFillerArea(l.getElement(), fill, 0, bidiLevel);
                l.insertChild(f, null, LineArea.EXPAND_IPD);
            }
        }
    }

    private int justifyTextAreasWithBase(LineArea l, double available, int bidiLevel, double[] baseAdvances) {
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
                    l.insertChild(new InlineFillerArea(l.getElement(), s, 0, bidiLevel), c, LineArea.EXPAND_IPD);
                }
                s = d/2;
                ++i;
            }
            s += available/2;
            if (s > 0) {
                l.insertChild(new InlineFillerArea(l.getElement(), s, 0, bidiLevel), null, LineArea.EXPAND_IPD);
            }
            return 0;
        }
    }

    private List<LineArea> alignBPD(List<LineArea> lines) {
        lines = maybeAddAnnotationReserve(lines);
        return lines;
    }

    private List<LineArea> maybeAddAnnotationReserve(List<LineArea> lines) {
        if (annotationReserve.isNone())
            return lines;
        else
            return maybeAddAnnotationReserve(lines, annotationReserve);
    }

    private List<LineArea> maybeAddAnnotationReserve(List<LineArea> lines, AnnotationReserve annotationReserve) {
        int numLines = lines.size();
        for (LineArea l : lines)
            maybeAddAnnotationReserve(l, annotationReserve, numLines, l.getLineNumber() == 1, l.getLineNumber() == numLines);
        return lines;
    }

    private LineArea maybeAddAnnotationReserve(LineArea line, AnnotationReserve annotationReserve, int numLines, boolean firstLine, boolean lastLine) {
        AnnotationReserve.Position p = annotationReserve.resolvePosition(numLines, firstLine, lastLine);
        double r = annotationReserve.getReserve();
        if (r < 0)
            r = lineHeight / 2;
        line.addAnnotationReserve(p, r);
        return line;
    }

    private static class WhitespaceState {
        static final WhitespaceState DEFAULT = new WhitespaceState(Whitespace.DEFAULT);
        static final WhitespaceState PRESERVE = new WhitespaceState(Whitespace.PRESERVE);
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
        double paddingStart;    // start padding
        double paddingEnd;      // end padding
        InlineBreakOpportunity(TextRun run, InlineBreak type, int start, int index, double advance) {
            this.run = run;
            this.type = type;
            this.start = start;
            this.index = index;
            this.advance = advance;
        }
        double getAdvance() {
            return advance;
        }
        double getShearAdvance(InlineBreakOpportunity previous) {
            if (previous == null)
                return run.getShearAdvance();
            else
                return 0;
        }
        Padding getPadding() {
            return run.getPadding(start, index);
        }
        void startPadding(double paddingStart) {
            this.paddingStart = paddingStart;
        }
        double startPadding() {
            return paddingStart;
        }
        void endPadding(double paddingEnd) {
            this.paddingEnd = paddingEnd;
        }
        double endPadding() {
            return paddingEnd;
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
            if (paddingStart > 0) {
                sb.append(',');
                sb.append('[');
                sb.append(Double.toString(paddingStart));
                sb.append(',');
                sb.append(Double.toString(paddingEnd));
                sb.append(']');
            }
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
        List<StyleAttributeInterval> paddingIntervals;          // cached padding sub-intervals over complete run interval
        Orientation orientation;                                // dominant glyph orientation for run
        Combination combination;                                // dominant combination for run
        int bidiLevel;                                          // bidirectional level
        String text;                                            // cached text over complete run interval
        TextRun(int start, int end) {
            this.start = start;
            this.end = end;
            int l = end - start;
            this.fontIntervals = getFontIntervals(0, l, firstAvailableFont);
            this.paddingIntervals = getPaddingIntervals(0, l);
            this.orientation = getDominantOrientation(0, l, defaults.getOrientation());
            this.combination = getDominantCombination(0, l, defaults.getCombination());
            this.bidiLevel = getDominantLevel(0, l);
        }
        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append(getText());
            return sb.toString();
        }
        boolean ignore() {
            return false;
        }
        // obtain all font intervals associated with run
        List<StyleAttributeInterval> getFontIntervals() {
            return fontIntervals;
        }
        // obtain padding where FROM and TO are indices into run, not outer iterator
        Padding getPadding(int from, int to) {
            Padding p = null;
            for (StyleAttributeInterval pai : paddingIntervals) {
                p = (Padding) pai.getValue();
                if (p == null)
                    continue;
                else if (pai.isOuterScope()) {
                    break;
                } else {
                    int[] intersection = pai.intersection(start + from, start + to);
                    if (intersection == null) {
                        p = null;
                        continue;
                    }
                }
            }
            return p;
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
            intervals.addAll(getBackgroundColorIntervals(from, to));
            intervals.addAll(getColorIntervals(from, to));
            intervals.addAll(getOutlineIntervals(from, to));
            intervals.addAll(getVisibilityIntervals(from, to));
            List<Decoration> decorations = new java.util.ArrayList<Decoration>();
            for (StyleAttributeInterval i : intervals) {
                Decoration.Type t;
                Object v = i.getValue();
                if (v != null) {
                    if (v instanceof BackgroundColor)
                        t = Decoration.Type.BACKGROUND_COLOR;
                    else if (v instanceof Color)
                        t = Decoration.Type.COLOR;
                    else if (v instanceof Outline)
                        t = Decoration.Type.OUTLINE;
                    else if (v instanceof Visibility)
                        t = Decoration.Type.VISIBILITY;
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
        double getAdvance(int from, int to, String script, String language, double available) {
            double advance = 0;
            for (StyleAttributeInterval fai : fontIntervals) {
                Font font = (Font) fai.getValue();
                if (font == null)
                    continue;
                else if (fai.isOuterScope()) {
                    advance += getAdvance(getText().substring(from, to), script, language, font, orientation, combination);
                    break;
                } else {
                    int[] intersection = fai.intersection(start + from, start + to);
                    if (intersection != null) {
                        int f = intersection[0] - start;
                        int t = intersection[1] - start;
                        advance += getAdvance(getText().substring(f,t), script, language, font, orientation, combination);
                    }
                }
            }
            return advance;
        }
        double getAdvance(String text, String script, String language, Font font, Orientation orientation, Combination combination) {
            if (font.isVertical() && (orientation == Orientation.ROTATE090) && !combination.isNone())
                return lineHeight;
            else {
                GlyphMapping gm =
                    font.getGlyphMapping(text, makeGlyphMappingFeatures(script, language, bidiLevel, font.getAxis(), font.isKerningEnabled(), orientation, combination));
                return (gm != null) ? font.getScaledAdvance(gm) : 0;
            }
        }
        // obtain shear advance starting at FROM to TO of run, where FROM and TO are indices into run, not outer iterator
        double getShearAdvance() {
            return getShearAdvance(0, end - start);
        }
        double getShearAdvance(int from, int to) {
            double advance = 0;
            for (StyleAttributeInterval fai : fontIntervals) {
                Font font = (Font) fai.getValue();
                if (font == null)
                    continue;
                else if (fai.isOuterScope()) {
                    advance += font.getShearAdvance(orientation.isRotated(), !combination.isNone());
                    break;
                } else if (fai.intersects(start + from, start + to))
                    advance += font.getShearAdvance(orientation.isRotated(), !combination.isNone());
            }
            return advance;
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
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
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
                if ((v != null) && (v instanceof Font))
                    ais.add(new StyleAttributeInterval(fontAttr, (Font) v, s, e));
            }
            aci.setIndex(savedIndex);
            if (ais.isEmpty())
                ais.add(new StyleAttributeInterval(fontAttr, defaultFont, -1, -1));
            return ais;
        }
        // obtain background colors for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getBackgroundColorIntervals(int from, int to) {
            StyleAttribute backgroundColorAttr = StyleAttribute.BACKGROUND_COLOR;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, backgroundColorAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(backgroundColorAttr);
                if ((v != null) && (v instanceof Color))
                    ais.add(new StyleAttributeInterval(backgroundColorAttr, new BackgroundColor((Color) v), s, e));
            }
            aci.setIndex(savedIndex);
            return ais;
        }
        // obtain colors for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getColorIntervals(int from, int to) {
            StyleAttribute colorAttr = StyleAttribute.COLOR;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, colorAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(colorAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(colorAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            if (ais.isEmpty() && (color != null) && !color.equals(defaults.getColor()))
                ais.add(new StyleAttributeInterval(colorAttr, color, start + from, start + to));
            return ais;
        }
        // obtain outline for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getOutlineIntervals(int from, int to) {
            StyleAttribute outlineAttr = StyleAttribute.OUTLINE;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, outlineAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(outlineAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(outlineAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            if (ais.isEmpty() && (outline != null) && !outline.equals(defaults.getOutline()))
                ais.add(new StyleAttributeInterval(outlineAttr, outline, start + from, start + to));
            return ais;
        }
        // obtain colors for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getPaddingIntervals(int from, int to) {
            StyleAttribute paddingAttr = StyleAttribute.PADDING;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, paddingAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(paddingAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(paddingAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return ais;
        }
        // obtain visibility for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getVisibilityIntervals(int from, int to) {
            StyleAttribute visibilityAttr = StyleAttribute.VISIBILITY;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, visibilityAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(visibilityAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(visibilityAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            if (ais.isEmpty() && (visibility != null) && !visibility.equals(defaults.getVisibility()))
                ais.add(new StyleAttributeInterval(visibilityAttr, visibility, start + from, start + to));
            return ais;
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
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, orientationAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(orientationAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(orientationAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return ais;
        }
        // obtain dominant combination for specified interval FROM to TO of run
        private Combination getDominantCombination(int from, int to, Combination defaultCombination) {
            if (orientation.isRotated()) {
                for (StyleAttributeInterval sai : getCombinationIntervals(from, to)) {
                    Combination combination = (Combination) sai.getValue();
                    if (!combination.isNone())
                        return combination;
                }
            }
            return defaultCombination;
        }
        // obtain combination for specified interval FROM to TO of run
        private List<StyleAttributeInterval> getCombinationIntervals(int from, int to) {
            StyleAttribute combinationAttr = StyleAttribute.COMBINATION;
            List<StyleAttributeInterval> ais = new java.util.ArrayList<StyleAttributeInterval>();
            int[] intervals = getAttributeIntervals(from, to, combinationAttr);
            AttributedCharacterIterator aci = iterator;
            int savedIndex = aci.getIndex();
            for (int i = 0, n = intervals.length / 2; i < n; ++i) {
                int s = start + intervals[i*2 + 0];
                int e = start + intervals[i*2 + 1];
                aci.setIndex(s);
                Object v = aci.getAttribute(combinationAttr);
                if (v != null)
                    ais.add(new StyleAttributeInterval(combinationAttr, v, s, e));
            }
            aci.setIndex(savedIndex);
            return ais;
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
                int bidiLevel = (v != null) ? (int) v : -1;
                aci.setIndex(savedIndex);
                return bidiLevel;
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

    private class IgnoredControlRun extends WhitespaceRun {
        IgnoredControlRun(int index) {
            super(index, index + 1, WhitespaceState.DEFAULT);
        }
        @Override
        boolean ignore() {
            return true;
        }
        @Override
        double getAdvance(int from, int to, String script, String language, double available) {
            return 0;
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
        double getAdvance(int from, int to, String script, String language, double available) {
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
            area.addChildren(new ParagraphLayout(embedding, state).layout(-1, available, Consume.FIT), LineArea.EXPAND_LINE);
            return area;
        }
    }

}
