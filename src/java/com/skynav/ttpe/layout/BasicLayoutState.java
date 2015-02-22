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
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Overflow;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.Helpers;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.text.LineBreakIterator;

import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
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
        String language = null;
        if (!areas.empty())
            language = areas.peek().getLanguage();
        if (language == null)
            language = defaultLanguage;
        return language;
    }

    public Whitespace getWhitespace() {
        Whitespace ws = null;
        if (!areas.empty())
            ws = areas.peek().getWhitespace();
        if (ws == null)
            ws = defaultWhitespace;
        return ws;
    }

    public WritingMode getWritingMode() {
        WritingMode wm = null;
        if (!areas.empty())
            wm = areas.peek().getWritingMode();
        if (wm == null)
            wm = defaultWritingMode;
        return wm;
    }

    public double getAvailable(Dimension dimension) {
        if (!areas.empty())
            return areas.peek().getAvailable(dimension);
        else
            return 0;
    }

    public Extent getReferenceExtent() {
        return getReferenceArea().getExtent();
    }

    public Extent getReferenceFontSize() {
        return defaultFontSize;                                 // [TBD] get from area stack context
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
            else
                return StyleSet.EMPTY;
        } else
            return StyleSet.EMPTY;
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
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = Extent.EMPTY;
                    double w = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize);
                    double h = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize);
                    return new Extent(w, h);
                } else
                    return defaultExtent;
            }
        } else
            return defaultExtent;
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
                    Extent externalExtent = getExternalExtent();
                    Extent referenceExtent = externalExtent;
                    Extent fontSize = Extent.EMPTY;
                    double x = Helpers.resolveLength(e, lengths.get(0), Axis.HORIZONTAL, externalExtent, referenceExtent, fontSize);
                    double y = Helpers.resolveLength(e, lengths.get(1), Axis.VERTICAL, externalExtent, referenceExtent, fontSize);
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

    public TransformMatrix getTransform(Element e) {
        return defaultTransform;
    }

    public WritingMode getWritingMode(Element e) {
        StyleSpecification s = getStyles(e).get(ttsWritingModeAttrName);
        if (s != null) {
            String v = s.getValue();
            return WritingMode.valueOf(v.toUpperCase());
        } else
            return defaultWritingMode;
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

}