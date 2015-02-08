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

package com.skynav.ttpe.render.svg;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.style.Color;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;

public class SVGRenderProcessor extends RenderProcessor {

    public static final RenderProcessor PROCESSOR               = new SVGRenderProcessor();

    private static final String PROCESSOR_NAME                  = "svg";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // miscellaneous statics
    public static final MessageFormat doubleFormatter          = new MessageFormat("{0,number,#.####}");
    public static final MessageFormat transformFormatter1      = new MessageFormat("translate({0,number,#.####},{1,number,#.####})");

    // render state
    private double xCurrent;
    private double yCurrent;

    public SVGRenderProcessor() {
    }

    @Override
    public String getName() {
        return PROCESSOR_NAME;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        return longOptions.values();
    }

    @Override
    public List<Frame> render(List<Area> areas, TransformerContext context) {
        List<Frame> frames = new java.util.ArrayList<Frame>();
        for (Area a : areas) {
            if (a instanceof CanvasArea) {
                Frame f = renderCanvas((CanvasArea) a, context);
                if (f != null)
                    frames.add(f);
            }
        }
        return frames;
    }

    private Frame renderCanvas(CanvasArea a, TransformerContext context) {
        Reporter reporter = context.getReporter();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.appendChild(renderCanvas(null, a, d, context));
            Namespaces.normalize(d, SVGDocumentFrame.prefixes);
            return new SVGDocumentFrame(a.getBegin(), a.getEnd(), a.getExtent(), d);
        } catch (Exception e) {
            reporter.logError(e);
        }
        return null;
    }

    private Element renderCanvas(Element parent, CanvasArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgSVGEltName);
        return renderChildren(e, a, d, context);
    }

    private Element renderViewport(Element parent, ViewportArea a, Document d, TransformerContext context) {
        Element e = parent;
        return renderChildren(e, a, d, context);
    }

    private Element renderReference(Element parent, ReferenceArea a, Document d, TransformerContext context) {
        Element eSVG;
        boolean root = isRootReference(a);
        if (root)
            eSVG = parent;
        else
            eSVG = Documents.createElement(d, SVGDocumentFrame.svgSVGEltName);
        Extent extent = a.getExtent();
        if (extent != null) {
            Documents.setAttribute(eSVG, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
            Documents.setAttribute(eSVG, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
        }
        if (root)  {
            Element eBackground = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
            Documents.setAttribute(eBackground, SVGDocumentFrame.xAttrName, "0");
            Documents.setAttribute(eBackground, SVGDocumentFrame.yAttrName, "0");
            Documents.setAttribute(eBackground, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
            Documents.setAttribute(eBackground, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
            Documents.setAttribute(eBackground, SVGDocumentFrame.fillAttrName, "black");
            eSVG.appendChild(eBackground);
            return renderChildren(eSVG, a, d, context);
        } else {
            Element eGroup = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
            Point origin = a.getOrigin();
            if (origin != null)
                Documents.setAttribute(eGroup, SVGDocumentFrame.transformAttrName, transformFormatter1.format(new Object[] {origin.getX(),origin.getY()}));
            eGroup.appendChild(renderChildren(eSVG, a, d, context));
            return eGroup;
        }
    }

    private boolean isRootReference(AreaNode a) {
        for (AreaNode p = a.getParent(); p != null; p = p.getParent()) {
            if (p instanceof ReferenceArea)
                return false;
        }
        return true;
    }

    private Element renderBlock(Element parent, BlockArea a, Document d, TransformerContext context) {
        Element e = parent;
        double ySaved = yCurrent;
        Element eBlockGroup = renderChildren(e, a, d, context);
        yCurrent = ySaved;
        return eBlockGroup;
    }

    private Element renderLine(Element parent, LineArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        Color color = a.getColor();
        Documents.setAttribute(e, SVGDocumentFrame.fillAttrName, color.toRGBString());
        Font font = a.getFont();
        String fontFamily = font.getPreferredFamilyName();
        Documents.setAttribute(e, SVGDocumentFrame.fontFamilyAttrName, fontFamily);
        Extent fontSize = font.getSize();
        Documents.setAttribute(e, SVGDocumentFrame.fontSizeAttrName, doubleFormatter.format(new Object[] {fontSize.getHeight()}));
        FontStyle fontStyle = font.getStyle();
        if (fontStyle != FontStyle.NORMAL)
            Documents.setAttribute(e, SVGDocumentFrame.fontStyleAttrName, fontStyle.name().toLowerCase());
        FontWeight fontWeight = font.getWeight();
        if (fontWeight != FontWeight.NORMAL)
            Documents.setAttribute(e, SVGDocumentFrame.fontWeightAttrName, fontWeight.name().toLowerCase());
        Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, transformFormatter1.format(new Object[] {0, yCurrent + fontSize.getHeight()}));
        double xSaved = xCurrent;
        e = renderChildren(e, a, d, context);
        xCurrent = xSaved;
        yCurrent += a.getBPD();
        return e;
    }

    private Element renderGlyph(Element parent, GlyphArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
        Documents.setAttribute(e, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {xCurrent}));
        xCurrent += a.getIPD();
        e.appendChild(d.createTextNode(a.getText()));
        return e;
    }

    private Element renderSpace(Element parent, SpaceArea a, Document d, TransformerContext context) {
        xCurrent += a.getIPD();
        return null;
    }

    private Element renderFiller(Element parent, InlineFillerArea a, Document d, TransformerContext context) {
        xCurrent += a.getIPD();
        return null;
    }

    private Element renderChildren(Element parent, Area a, Document d, TransformerContext context) {
        if (a instanceof NonLeafAreaNode) {
            for (Area c : ((NonLeafAreaNode) a).getChildren()) {
                Element e = renderArea(parent, c, d, context);
                if (e != null) {
                    if (e != parent) {
                        parent.appendChild(e);
                    }
                }
            }
        }
        return parent;
    }

    private Element renderArea(Element parent, Area a, Document d, TransformerContext context) {
        if (a instanceof GlyphArea)
            return renderGlyph(parent, (GlyphArea) a, d, context);
        else if (a instanceof SpaceArea)
            return renderSpace(parent, (SpaceArea) a, d, context);
        else if (a instanceof InlineFillerArea)
            return renderFiller(parent, (InlineFillerArea) a, d, context);
        else if (a instanceof LineArea)
            return renderLine(parent, (LineArea) a, d, context);
        else if (a instanceof ReferenceArea)
            return renderReference(parent, (ReferenceArea) a, d, context);
        else if (a instanceof ViewportArea)
            return renderViewport(parent, (ViewportArea) a, d, context);
        else if (a instanceof BlockArea)
            return renderBlock(parent, (BlockArea) a, d, context);
        else
            throw new IllegalArgumentException();
    }

}
