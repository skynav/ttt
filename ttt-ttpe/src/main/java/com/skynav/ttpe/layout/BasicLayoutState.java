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

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import org.xml.sax.Locator;

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
import com.skynav.ttpe.geometry.Shear;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.Display;
import com.skynav.ttpe.style.Helpers;
import com.skynav.ttpe.style.Image;
import com.skynav.ttpe.style.Visibility;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.text.LineBreakIterator;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Condition;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Locators;
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
    private Locator baseLocator;

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
        this.baseLocator = makeBaseLocator(context);
        return this;
    }

    private static Locator makeBaseLocator(TransformerContext context) {
        URI uri = (URI) context.getResourceState("sysid");
        return Locators.getLocator(uri != null ? uri.toString() : null);
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
        } else {
            updateDefaultFontSizeWithCellResolution(width, height);
        }
        return a;
    }

    private void updateDefaultFontSizeWithCellResolution(double width, double height) {
        double h = height / getCellResolution().getHeight();
        defaults.setFontSize(new Extent(h, h));
    }

    public NonLeafAreaNode pushReference(Element e, double x, double y, double width, double height, WritingMode wm, TransformMatrix ctm, Visibility visibility) {
        ReferenceArea ra = new ReferenceArea(e, x, y, width, height, wm, ctm, visibility);
        processBlockTraits(ra, e);
        return push(ra);
    }

    public NonLeafAreaNode pushBlock(Element e, double shearAngle, Visibility visibility) {
        ReferenceArea ra = getReferenceArea();
        if (ra != null) {
            BlockArea ba = new BlockArea(e, ra.getIPD(), ra.getBPD(), getBidiLevel(), shearAngle, visibility);
            processBlockTraits(ba, e);
            return push(ba);
        } else
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

    public Font[] getFonts() {
        Font font = null;
        if (!areas.empty())
            font = areas.peek().getFont();
        if (font == null)
            font = fontCache.getDefaultFont(getWritingMode().getAxis(Dimension.IPD), defaults.getFontSize());
        return new Font[] { font };
    }

    public Font getFirstFont() {
        Font[] fonts = getFonts();
        if ((fonts != null) && (fonts.length > 0) )
            return fonts[0];
        else
            return null;
    }

    public Extent getFontSize() {
        Font f = getFirstFont();
        if (f != null)
            return f.getSize();
        else
            return Extent.UNIT;
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
            return Extent.UNIT;
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

    public boolean isExcluded(Element e) {
        Condition condition = getCondition(e);
        if (condition != null) {
            try {
                if (!condition.evaluate(context.getConditionEvaluatorState()))
                    return true;
            } catch (Condition.EvaluatorException x) {
            } catch (IllegalStateException x) {
            }
        }
        return false;
    }

    private Condition getCondition(Element e) {
        String condition = Documents.getAttribute(e, conditionAttrName, null);
        if (condition != null) {
            try {
                return Condition.valueOf(condition);
            } catch (Condition.ParserException x) {
            }
        }
        return null;
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

    public Color getBackgroundColor(Element e) {
        StyleSpecification s = getStyles(e).get(ttsBackgroundColorAttrName);
        if (s != null) {
            String v = s.getValue();
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (com.skynav.ttv.verifier.util.Colors.isColor(v, getLocation(e, ttsBackgroundColorAttrName), context, retColor))
                return new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
        }
        return defaults.getBackgroundColor();
    }

    public Image getBackgroundImage(Element e) {
        QName an = ttsBackgroundImageAttrName;
        StyleSpecification s = getStyles(e).get(an);
        if (s != null) {
            Image image;
            if ((image = getImage(e, an, s.getValue())) != null)
                return image;
        }
        return defaults.getBackgroundImage();
    }

    public Image getForegroundImage(Element e) {
        // [TBD] - implement support for all source categories, at present only support 'src' attribute
        QName an = sourceAttrName;
        String s = Documents.getAttribute(e, an);
        if (s != null) {
            Image image;
            if ((image = getImage(e, an, s)) != null)
                return image;
        }
        return Image.NONE;
    }

    private Image getImage(Element e, QName attrName, String attrValue) {
        if ((attrValue != null) && !attrValue.isEmpty()) {
            com.skynav.ttv.model.value.Image[] retImage = new com.skynav.ttv.model.value.Image[1];
            if (com.skynav.ttv.verifier.util.Images.isImage(attrValue, getLocation(e, attrName), context, retImage)) {
                com.skynav.ttv.model.value.Image i = retImage[0];
                return new Image(i.getURI(), i.getVerifiedType(), i.getVerifiedFormat(), i.getWidth(), i.getHeight());
            }
        }
        return null;
    }

    public Display getDisplay(Element e) {
        StyleSpecification s = getStyles(e).get(ttsDisplayAttrName);
        if (s != null) {
            String v = s.getValue();
            return Display.valueOf(v.toUpperCase());
        } else
            return defaults.getDisplay();
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
        if (Documents.isElement(e, isdInstanceElementName))
            return getISDExtent(e);
        else
            return getTTSExtent(e);
    }

    private Extent getISDExtent(Element e) {
        assert Documents.isElement(e, isdInstanceElementName);
        String v = e.getAttribute("extent");
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(v, getLocation(e, ttsExtentAttrName), context, minMax, treatments, lengths)) {
            assert lengths.size() == 2;
            double w = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, null, null, null, null);
            double h = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, null, null, null, null);
            return new Extent(w, h);
        }
        return getExternalExtent();
    }

    private Extent getTTSExtent(Element e) {
        StyleSpecification s = getStyles(e).get(ttsExtentAttrName);
        if (s != null) {
            String v = s.getValue();
            if (Keywords.isAuto(v)) {
                return getExternalExtent();
            } else {
                Integer[] minMax = new Integer[] { 2, 2 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, getLocation(e, ttsExtentAttrName), context, minMax, treatments, lengths)) {
                    assert lengths.size() == 2;
                    Extent cellResolution = getCellResolution();
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = getFontSize();
                    double w = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    double h = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    return new Extent(w, h);
                }
            }
        }
        return getExternalExtent();
    }

    public double getOpacity(Element e) {
        StyleSpecification s = getStyles(e).get(ttsOpacityAttrName);
        if (s != null) {
            String v = s.getValue();
            try {
                Double d = Double.valueOf(v);
                double opacity = d.doubleValue();
                if (d.isNaN()) {
                    opacity = 1;
                } else if (d.isInfinite()) {
                    if (opacity < 0) {
                        opacity = 0;
                    } else {
                        opacity = 1;
                    }
                } else if (opacity < 0) {
                    opacity = 0;
                } else if (opacity > 1) {
                    opacity = 1;
                }
                return opacity;
            } catch (NumberFormatException x) {
                return defaults.getOpacity();
            }
        } else
            return defaults.getOpacity();
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
                if (Lengths.isLengths(v, getLocation(e, ttsOriginAttrName), context, minMax, treatments, lengths)) {
                    assert lengths.size() == 2;
                    Extent cellResolution = getCellResolution();
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = getFontSize();
                    double x = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    double y = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize, cellResolution);
                    return new Point(x, y);
                }
            }
        }
        return defaults.getOrigin();
    }

    private static final double[] zeroPadding = new double[4];
    public double[] getPadding(Element e) {
        StyleSpecification s = getStyles(e).get(ttsPaddingAttrName);
        if (s != null) {
            String v = s.getValue();
            if (Keywords.isAuto(v)) {
                return Arrays.copyOf(zeroPadding, zeroPadding.length);
            } else {
                Integer[] minMax = new Integer[] { 1, 4 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, getLocation(e, ttsPaddingAttrName), context, minMax, treatments, lengths)) {
                    Length[] la = lengths.toArray(new Length[lengths.size()]);
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = getReferenceExtent();
                    if ((referenceExtent == null) || referenceExtent.isEmpty())
                        referenceExtent = externalExtent;
                    return Helpers.resolvePadding(e, la, getWritingMode(e), externalExtent, referenceExtent, getFontSize(), getCellResolution());
                }
            }
        }
        return defaults.getPadding();
    }

    public Point getPosition(Element e, Extent extent) {
        StyleSpecification s = getStyles(e).get(ttsPositionAttrName);
        if ((s == null) && (getStyles(e).get(ttsOriginAttrName) == null) && (context.getModel().getTTMLVersion() >= 2))
            s = new StyleSpecification(ttsPositionAttrName, defaults.getPositionComponents());
        if (s != null) {
            String v = s.getValue();
            String [] components = v.split("[ \t\r\n]+");
            Length[] lengths = new Length[4];
            if (Positions.isPosition(components, getLocation(e, ttsPositionAttrName), context, lengths)) {
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

    public double getShear(Element e) {
        StyleSpecification s = getStyles(e).get(ttsShearAttrName);
        if (s != null) {
            String v = s.getValue();
            if (v != null) {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Error };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, getLocation(e, ttsShearAttrName), context, minMax, treatments, lengths)) {
                    assert lengths.size() == 1;
                    Length length = lengths.get(0);
                    if (length.getUnits() == Length.Unit.Percentage) {
                        return length.getValue() / 100.0;
                    }
                }
            }
        }
        return defaults.getShear();
    }

    public Visibility getVisibility(Element e) {
        StyleSpecification s = getStyles(e).get(ttsVisibilityAttrName);
        if (s != null) {
            String v = s.getValue();
            return Visibility.valueOf(v.toUpperCase());
        } else
            return defaults.getVisibility();
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
        String condition = Documents.getAttribute(e, conditionAttrName, null);
        if (condition != null) {
            try {
                styles.setCondition(Condition.valueOf(condition));
            } catch (Condition.ParserException x) {
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

    private Location getLocation(Element e, QName attributeName) {
        return new Location(e, Documents.getName(e), attributeName, baseLocator);
    }

    /**
     * Assign block traits to block area A from styles on element E
     * These include:
     *
     * 1. background color
     * 2. background image and related properties
     * 3. border properties
     * 4. padding properties
     */
    private void processBlockTraits(BlockArea a, Element e) {
        // background color
        Color c = getBackgroundColor(e);
        if ((c != null) && !c.isTransparent())
            a.setBackgroundColor(c);
        // background image
        Image i = getBackgroundImage(e);
        if ((i != null) && !i.isNone())
            a.setBackgroundImage(i);
        // border [TBD]
        // padding [TBD]
        double[] p = getPadding(e);
        if (!Arrays.equals(p,zeroPadding))
            a.setPadding(p);
    }

}
