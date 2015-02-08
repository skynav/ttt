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

import java.util.List;
import java.util.Map;
import java.util.Stack;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;

import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Overflow;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.style.Wrap;
import com.skynav.ttpe.text.LineBreakIterator;

import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.verifier.util.Colors;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttx.transformer.TransformerContext;

import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.layout.Constants.*;

public class BasicLayoutState implements LayoutState {

    // initialized state
    private TransformerContext context;
    private FontCache fontCache;
    private LineBreakIterator breakIterator;
    private LineBreakIterator characterIterator;
    private Stack<NonLeafAreaNode> areas;
    private Map<String, StyleSet> styles;

    public BasicLayoutState(TransformerContext context) {
        this.context = context;
    }

    public LayoutState initialize(FontCache fontCache, LineBreakIterator breakIterator, LineBreakIterator characterIterator) {
        this.fontCache = fontCache.maybeLoad();
        this.breakIterator = breakIterator;
        this.characterIterator = characterIterator;
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

    public NonLeafAreaNode pushCanvas(Element e, double begin, double end) {
        return push(new CanvasArea(e, begin, end));
    }

    public NonLeafAreaNode pushViewport(Element e, double width, double height, boolean clip) {
        return push(new ViewportArea(e, width, height, clip));
    }

    public NonLeafAreaNode pushReference(Element e, double x, double y, double width, double height, WritingMode wm, TransformMatrix ctm) {
        return push(new ReferenceArea(e, x, y, width, height, wm, ctm));
    }

    public NonLeafAreaNode pushBlock(Element e) {
        ReferenceArea ra = getReferenceArea();
        if (ra != null)
            return push(new BlockArea(e, ra.getIPD(), ra.getBPD()));
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
        return l;
    }

    public NonLeafAreaNode pop() {
        return areas.pop();
    }

    public NonLeafAreaNode peek() {
        return areas.peek();
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
        if (!areas.empty())
            return areas.peek().getLanguage();
        else
            return defaultLanguage;
    }

    public Whitespace getWhitespace() {
        if (!areas.empty())
            return areas.peek().getWhitespace();
        else
            return defaultWhitespace;
    }

    public WritingMode getWritingMode() {
        if (!areas.empty())
            return areas.peek().getWritingMode();
        else
            return defaultWritingMode;
    }

    public double getAvailable(Dimension dimension) {
        if (!areas.empty())
            return areas.peek().getAvailable(dimension);
        else
            return 0;
    }

    public Extent getExternalExtent() {
        return new Extent(1280, 720);                           // [TBD] get from context
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

    public void saveStyle(Element e) {
        assert isElement(e, isdComputedStyleSetElementName);
        String id = Documents.getAttribute(e, xmlIdAttrName, null);
        if (id != null) {
            styles.put(id, parseStyle(e, id));
        }
    }

    public BlockAlignment getDisplayAlign(Element e) {
        StyleSpecification s = getStyles(e).get(ttsDisplayAlignAttrName);
        if (s != null) {
            String v = s.getValue();
            return BlockAlignment.valueOf(v.toUpperCase());
        } else
            return defaultDisplayAlign;
    }

    public Color getColor(Element e) {
        StyleSpecification s = getStyles(e).get(ttsColorAttrName);
        if (s != null) {
            String v = s.getValue();
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(v, null, context, retColor)) {
                return new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                return defaultColor;
        } else
            return defaultColor;
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
                    double w = resolveLength(e, lengths.get(0), Axis.HORIZONTAL);
                    double h = resolveLength(e, lengths.get(1), Axis.VERTICAL);
                    return new Extent(w, h);
                } else
                    return defaultExtent;
            }
        } else
            return defaultExtent;
    }

    public String getFontFamily(Element e) {
        StyleSpecification s = getStyles(e).get(ttsFontFamilyAttrName);
        if (s != null)
            return s.getValue();                                // [TBD] FIX ME - handle quoted family name
        else
            return defaultFontFamily;
    }

    public Extent getFontSize(Element e) {
        StyleSpecification s = getStyles(e).get(ttsFontSizeAttrName);
        if (s != null) {
            String v = s.getValue();
            Integer[] minMax = new Integer[] { 1, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(v, null, context, minMax, treatments, lengths)) {
                assert lengths.size() > 0;
                double h = resolveLength(e, lengths.get(0), Axis.HORIZONTAL);
                if (lengths.size() == 1)
                    lengths.add(lengths.get(0));
                double w = resolveLength(e, lengths.get(1), Axis.VERTICAL);
                return new Extent(w, h);
            } else
                return defaultFontSize;
        } else
            return defaultFontSize;
    }

    public FontStyle getFontStyle(Element e) {
        return defaultFontStyle;
    }

    public FontWeight getFontWeight(Element e) {
        return defaultFontWeight;
    }

    public String getLanguage(Element e) {
        StyleSpecification s = getStyles(e).get(xmlLanguageAttrName);
        if (s != null)
            return s.getValue().toUpperCase();
        else
            return defaultLanguage;
    }

    public double getLineHeight(Element e, Font font) {
        double fontHeight = font.getSize().getHeight();
        StyleSpecification s = getStyles(e).get(ttsLineHeightAttrName);
        if (s != null) {
            String v = s.getValue();
            if (Keywords.isNormal(v)) {
                return fontHeight * 1.25;
            } else {
                Integer[] minMax = new Integer[] { 1, 1 };
                Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
                List<Length> lengths = new java.util.ArrayList<Length>();
                if (Lengths.isLengths(v, null, context, minMax, treatments, lengths)) {
                    assert lengths.size() == 1;
                    double h = resolveLength(e, lengths.get(0), Axis.VERTICAL);
                    return h;
                } else
                    return fontHeight * 1.25;
            }
        } else
            return fontHeight * 1.25;
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
                    double x = resolveLength(e, lengths.get(0), Axis.HORIZONTAL);
                    double y = resolveLength(e, lengths.get(1), Axis.VERTICAL);
                    return new Point(x, y);
                } else
                    return defaultOrigin;
            }
        } else
            return defaultOrigin;
    }

    public Overflow getOverflow(Element e) {
        StyleSpecification s = getStyles(e).get(ttsOverflowAttrName);
        if (s != null) {
            String v = s.getValue();
            return Overflow.valueOf(v.toUpperCase());
        } else
            return defaultOverflow;
    }

    public InlineAlignment getTextAlign(Element e) {
        StyleSpecification s = getStyles(e).get(ttsTextAlignAttrName);
        if (s != null) {
            String v = s.getValue();
            return InlineAlignment.valueOf(v.toUpperCase());
        } else
            return defaultTextAlign;
    }

    public TransformMatrix getTransform(Element e) {
        return defaultTransform;
    }

    public Whitespace getWhitespace(Element e) {
        StyleSpecification s = getStyles(e).get(xmlSpaceAttrName);
        if (s != null) {
            String v = s.getValue();
            return Whitespace.valueOf(v.toUpperCase());
        } else
            return defaultWhitespace;
    }

    public Wrap getWrapOption(Element e) {
        return defaultWrap;
    }

    public WritingMode getWritingMode(Element e) {
        StyleSpecification s = getStyles(e).get(ttsWritingModeAttrName);
        if (s != null) {
            String v = s.getValue();
            return WritingMode.valueOf(v.toUpperCase());
        } else
            return defaultWritingMode;
    }

    private StyleSet getStyles(Element e) {
        String style = Documents.getAttribute(e, isdCSSAttrName, null);
        if (style != null) {
            StyleSet styles = this.styles.get(style);
            if (styles != null)
                return styles;
            else
                return StyleSet.EMPTY;
        } else
            return StyleSet.EMPTY;
    }

    private StyleSet parseStyle(Element e, String id) {
        assert isElement(e, isdComputedStyleSetElementName);
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

    private double resolveLength(Element e, Length l, Axis axis) {
        return l.getValue() * getLengthReference(e, l.getUnits(), axis);
    }

    private double getLengthReference(Element e, Length.Unit units, Axis axis) {
        if (units == Length.Unit.Pixel)
            return 1;
        else if (units == Length.Unit.Percentage)
            return getPercentageReference(e, axis);
        else if (units == Length.Unit.Em)
            return getFontSizeReference(e, axis);
        else if (units == Length.Unit.Cell)
            return getCellSizeReference(axis);
        else if (units == Length.Unit.ViewportHeight)
            return getExternalReference(axis);
        else if (units == Length.Unit.ViewportWidth)
            return getExternalReference(axis);
        else
            return 1;
    }

    private double getPercentageReference(Element e, Axis axis) {
        if (isElement(e, isdRegionElementName)) {
            return getExternalReference(axis);
        } else {
            return 0.01;                                        // [TBD] FIX ME - use element specific definition
        }
    }

    private double getFontSizeReference(Element e, Axis axis) {
        Extent size = getFontSize(e);
        if (axis == Axis.VERTICAL)
            return size.getHeight();
        else
            return size.getWidth();
    }

    private double getCellSizeReference(Axis axis) {
        if (axis == Axis.VERTICAL)
            return getExternalExtent().getHeight() / 15;        // [TBD] FIX ME - use ttp:cellResolution
        else
            return getExternalExtent().getWidth() / 32;         // [TBD] FIX ME - use ttp:cellResolution
    }

    private double getExternalReference(Axis axis) {
        if (axis == Axis.VERTICAL)
            return getExternalExtent().getHeight();
        else
            return getExternalExtent().getWidth();
    }

    protected static boolean isElement(Element e, QName qn) {
        return Documents.isElement(e, qn);
    }

}