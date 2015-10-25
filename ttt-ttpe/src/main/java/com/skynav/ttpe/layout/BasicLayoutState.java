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

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Overflow;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.Helpers;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.text.LineBreakIterator;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Positions;
import com.skynav.ttx.transformer.TransformerContext;

import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.style.Constants.*;
import static com.skynav.ttpe.text.Constants.*;

public class BasicLayoutState implements LayoutState {

    // initialized state
    private TransformerContext context;
    private FontCache fontCache;
    private LineBreakIterator breakIterator;
    private LineBreakIterator characterIterator;
    private Defaults defaults;
    private Stack<NonLeafAreaNode> areas;
    private Map<String, StyleSet> styles;
    private int[] counters;

    public BasicLayoutState(TransformerContext context) {
        this.context = context;
        this.counters = new int[Counter.values().length];
    }

    public LayoutState initialize(FontCache fontCache, LineBreakIterator breakIterator, LineBreakIterator characterIterator, Defaults defaults) {
        this.fontCache = fontCache.maybeLoad();
        this.breakIterator = breakIterator;
        this.characterIterator = characterIterator;
        this.defaults = defaults;
        this.areas = new java.util.Stack<NonLeafAreaNode>();
        this.styles = new java.util.HashMap<String,StyleSet>();
        return this;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    public LineBreakIterator getBreakIterator() {
        return breakIterator;
    }

    public LineBreakIterator getCharacterIterator() {
        return characterIterator;
    }

    public Defaults getDefaults() {
        return defaults;
    }

    public NonLeafAreaNode pushCanvas(Element e, double begin, double end, Extent cellResolution) {
        return push(new CanvasArea(e, begin, end, cellResolution));
    }

    public NonLeafAreaNode pushViewport(Element e, double width, double height, boolean clip) {
        NonLeafAreaNode a = push(new ViewportArea(e, width, height, clip));
        if (areas.size() > 2) {
            incrementCounters(CounterEvent.ADD_REGION, a);
            ((ViewportArea) a).setId(generateRegionIdentifier());
        }
        return a;
    }

    public NonLeafAreaNode pushReference(Element e, double x, double y, double width, double height, WritingMode wm, TransformMatrix ctm) {
        return push(new ReferenceArea(e, x, y, width, height, wm, ctm));
    }

    public NonLeafAreaNode pushBlock(Element e) {
        ReferenceArea ra = getReferenceArea();
        if (ra != null)
            return push(new BlockArea(e, ra.getIPD(), ra.getBPD(), getBidiLevel()));
        else
            throw new IllegalStateException();
    }

    public NonLeafAreaNode push(NonLeafAreaNode a) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        if (p != null)
            p.addChild(a);
        return (NonLeafAreaNode) areas.push(a);
    }

    public NonLeafAreaNode addLine(LineArea l) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        if ((p == null) || !(p instanceof BlockArea))
            throw new IllegalStateException();
        else
            p.addChild(l);
        incrementCounters(CounterEvent.ADD_LINE, l);
        return l;
    }

    public NonLeafAreaNode pop() {
        return pop(true);
    }

    public NonLeafAreaNode pop(boolean collapse) {
        NonLeafAreaNode a = areas.pop();
        if (collapse)
            collapse(a, Dimension.BPD);
        return a;
    }

    public NonLeafAreaNode peek() {
        return areas.peek();
    }

    public CanvasArea getCanvasArea() {
        if (!areas.empty()) {
            for (int i = 0, n = areas.size(); i < n; ++i) {
                int k = n - i - 1;
                NonLeafAreaNode a = areas.get(k);
                if (a instanceof CanvasArea)
                    return (CanvasArea) a;
            }
        }
        return null;
    }

    public ReferenceArea getReferenceArea() {
        if (!areas.empty()) {
            for (int i = 0, n = areas.size(); i < n; ++i) {
                int k = n - i - 1;
                NonLeafAreaNode a = areas.get(k);
                if (a instanceof ReferenceArea)
                    return (ReferenceArea) a;
            }
        }
        return null;
    }

    public String getLanguage() {
        String language = null;
        if (!areas.empty())
            language = areas.peek().getLanguage();
        if (language == null)
            language = defaults.getLanguage();
        return language;
    }

    public Whitespace getWhitespace() {
        Whitespace ws = null;
        if (!areas.empty())
            ws = areas.peek().getWhitespace();
        if (ws == null)
            ws = defaults.getWhitespace();
        return ws;
    }

    public WritingMode getWritingMode() {
        WritingMode wm = null;
        if (!areas.empty())
            wm = areas.peek().getWritingMode();
        if (wm == null)
            wm = defaults.getWritingMode();
        return wm;
    }

    public int getBidiLevel() {
        return !areas.empty() ? areas.peek().getBidiLevel() : -1;
    }

    public Font getFont() {
        Font f = null;
        if (!areas.empty())
            f = areas.peek().getFont();
        if (f == null)
            f = fontCache.getDefaultFont(getWritingMode().getAxis(Dimension.BPD), defaults.getFontSize());
        return f;
    }

    public double getAvailable(Dimension dimension) {
        if (!areas.empty())
            return areas.peek().getAvailable(dimension);
        else
            return 0;
    }

    public Extent getCellResolution() {
        CanvasArea ca = getCanvasArea();
        if (ca != null)
            return ca.getCellResolution();
        else
            return defaults.getCellResolution();
    }

    public Extent getReferenceExtent() {
        ReferenceArea ra = getReferenceArea();
        if (ra != null)
            return ra.getExtent();
        else
            return Extent.EMPTY;
    }

    public BlockAlignment getReferenceAlignment() {
        ReferenceArea ra = getReferenceArea();
        if (ra != null)
            return getDisplayAlign(ra.getElement());
        else
            return defaults.getDisplayAlign();
    }

    public Extent getExternalExtent() {
        Object value = context.getExternalParameters().getParameter("externalExtent");
        if (value instanceof double[]) {
            double[] parsedExternalExtent = (double[]) value;
            return new Extent(parsedExternalExtent[0], parsedExternalExtent[1]);
        } else
            return defaults.getExternalExtent();
    }

    public Point getExternalOrigin() {
        return Point.ZERO;                                      // [TBD] get from context
    }

    public Overflow getExternalOverflow() {
        return Overflow.HIDDEN;                                 // [TBD] get from context
    }

    public TransformMatrix getExternalTransform() {
        return TransformMatrix.IDENTITY;                        // [TBD] get from context
    }

    public WritingMode getExternalWritingMode() {
        return WritingMode.LRTB;                                // [TBD] get from context
    }

    public void saveStyles(Element e) {
        assert Documents.isElement(e, isdComputedStyleSetElementName);
        String id = Documents.getAttribute(e, xmlIdAttrName, null);
        if (id != null) {
            styles.put(id, parseStyle(e, id));
        }
    }

    public Map<String,StyleSet> getStyles() {
        return styles;
    }

    public StyleSet getStyles(Element e) {
        String style = Documents.getAttribute(e, isdCSSAttrName, null);
        if (style != null) {
            StyleSet styles = this.styles.get(style);
            if (styles != null)
                return styles;
        }
        return StyleSet.EMPTY;
    }

    public BlockAlignment getDisplayAlign(Element e) {
        StyleSpecification s = getStyles(e).get(ttsDisplayAlignAttrName);
        if (s != null) {
            String v = s.getValue();
            return BlockAlignment.valueOf(v.toUpperCase());
        } else
            return defaults.getDisplayAlign();
    }

    public Extent getExtent(Element e) {
        StyleSpecification s = getStyles(e).get(ttsExtentAttrName);
        if (s != null) {
            String v = s.getValue();
            if (Keywords.isAuto(v)) {
                return getExternalExtent();
            } else {
                Integer[] minMax = new Integer[] { 2, 2 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, null, context, minMax, treatments, lengths)) {
                    assert lengths.size() == 2;
                    Extent cellResolution = getCellResolution();
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = Extent.EMPTY;
                    double w = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    double h = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    return new Extent(w, h);
                }
            }
        }
        return getExternalExtent();
    }

    public Point getOrigin(Element e) {
        StyleSpecification s = getStyles(e).get(ttsOriginAttrName);
        if (s != null) {
            String v = s.getValue();
            if (Keywords.isAuto(v)) {
                return getExternalOrigin();
            } else {
                Integer[] minMax = new Integer[] { 2, 2 };
                Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, null, context, minMax, treatments, lengths)) {
                    assert lengths.size() == 2;
                    Extent cellResolution = getCellResolution();
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = Extent.EMPTY;
                    double x = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    double y = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    return new Point(x, y);
                }
            }
        }
        return defaults.getOrigin();
    }

    public Point getPosition(Element e, Extent extent) {
        StyleSpecification s = getStyles(e).get(ttsPositionAttrName);
        if ((s == null) && (getStyles(e).get(ttsOriginAttrName) == null) && (context.getModel().getTTMLVersion() >= 2))
            s = new StyleSpecification(ttsPositionAttrName, defaults.getPositionComponents());
        if (s != null) {
            String v = s.getValue();
            String [] components = v.split("[ \t\r\n]+");
            Length[] lengths = new Length[4];
            if (Positions.isPosition(components, null, context, lengths)) {
                Extent cellResolution = getCellResolution();
                return Helpers.resolvePosition(e, lengths, getExternalExtent(), extent, cellResolution);
            }
        }
        return getOrigin(e);
    }


    public Overflow getOverflow(Element e) {
        StyleSpecification s = getStyles(e).get(ttsOverflowAttrName);
        if (s != null) {
            String v = s.getValue();
            return Overflow.valueOf(v.toUpperCase());
        } else
            return defaults.getOverflow();
    }

    public TransformMatrix getTransform(Element e) {
        return defaults.getTransform();
    }

    public WritingMode getWritingMode(Element e) {
        StyleSpecification s = getStyles(e).get(ttsWritingModeAttrName);
        if (s != null) {
            String v = s.getValue();
            return WritingMode.valueOf(v.toUpperCase());
        } else
            return defaults.getWritingMode();
    }

    public void incrementCounters(CounterEvent event, Area a) {
        if (event == CounterEvent.ADD_REGION) {
            updateCharCounters(a);
            updateLineCounters(a);
            int nr = 1;
            counters[Counter.REGIONS_IN_CANVAS.ordinal()] += nr;
        } else if (event == CounterEvent.ADD_LINE) {
            updateCharCounters(a);
            int nc = countSpacingGlyphs(a);
            counters[Counter.CHARS_IN_LINE.ordinal()] += nc;
            counters[Counter.CHARS_IN_REGION.ordinal()] += nc;
            counters[Counter.CHARS_IN_CANVAS.ordinal()] += nc;
            int nl = 1;
            counters[Counter.LINES_IN_REGION.ordinal()] += nl;
            counters[Counter.LINES_IN_CANVAS.ordinal()] += nl;
        } else if (event == CounterEvent.RESET) {
            Arrays.fill(counters, 0);
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void finalizeCounters() {
        updateCharCounters(null);
        updateLineCounters(null);
    }

    public int getCounter(Counter counter) {
        return counters[counter.ordinal()];
    }

    private void updateLineCounters(Area a) {
        int n, m;
        if ((a == null) || (a instanceof ViewportArea)) {
            n = counters[Counter.LINES_IN_REGION.ordinal()];
            m = counters[Counter.MAX_LINES_IN_REGION.ordinal()];
            if (n > m)
                counters[Counter.MAX_LINES_IN_REGION.ordinal()] = n;
            resetCounter(Counter.LINES_IN_REGION);
        }
    }

    private void updateCharCounters(Area a) {
        int n, m;
        if ((a == null) || (a instanceof LineArea)) {
            n = counters[Counter.CHARS_IN_LINE.ordinal()];
            m = counters[Counter.MAX_CHARS_IN_LINE.ordinal()];
            if (n > m)
                counters[Counter.MAX_CHARS_IN_LINE.ordinal()] = n;
            resetCounter(Counter.CHARS_IN_LINE);
        }
        if ((a == null) || (a instanceof ViewportArea)) {
            n = counters[Counter.CHARS_IN_REGION.ordinal()];
            m = counters[Counter.MAX_CHARS_IN_REGION.ordinal()];
            if (n > m)
                counters[Counter.MAX_CHARS_IN_REGION.ordinal()] = n;
            resetCounter(Counter.CHARS_IN_REGION);
        }
    }

    private int countSpacingGlyphs(Area a) {
        if (a instanceof LineArea)
            return ((LineArea) a).getSpacingGlyphsCount();
        else
            return 0;
    }

    private void resetCounter(Counter counter) {
        counters[counter.ordinal()] = 0;
    }

    private String generateRegionIdentifier() {
        StringBuffer sb = new StringBuffer();
        sb.append('r');
        sb.append(Integer.toString(getCounter(Counter.REGIONS_IN_CANVAS)));
        return sb.toString();
    }

    private StyleSet parseStyle(Element e, String id) {
        assert Documents.isElement(e, isdComputedStyleSetElementName);
        StyleSet styles = new StyleSet(id);
        for (Map.Entry<QName,String> a : Documents.getAttributes(e).entrySet()) {
            QName qn = a.getKey();
            String ns = qn.getNamespaceURI();
            if (ns != null) {
                if (ns.equals(ttsNamespace) || qn.equals(xmlLanguageAttrName))
                    styles.merge(new StyleSpecification(ns, qn.getLocalPart(), a.getValue()));
            }
        }
        return styles;
    }

    private void collapse(NonLeafAreaNode a, Dimension dimension) {
        if ((dimension == Dimension.BOTH) || (dimension == Dimension.BPD))
            collapseBPD(a);
        if ((dimension == Dimension.BOTH) || (dimension == Dimension.IPD))
            collapseIPD(a);
    }

    private void collapseIPD(NonLeafAreaNode a) {
        a.setIPD(computeConsumed(a, Dimension.IPD));
    }

    private void collapseBPD(NonLeafAreaNode a) {
        a.setBPD(computeConsumed(a, Dimension.BPD));
    }

    private double computeConsumed(NonLeafAreaNode a, Dimension dimension) {
        double consumed = 0;
        for (AreaNode c : a.getChildren()) {
            if (dimension == Dimension.IPD)
                consumed += c.getIPD();
            else if (dimension == Dimension.BPD)
                consumed += c.getBPD();
        }
        return consumed;
    }

}
