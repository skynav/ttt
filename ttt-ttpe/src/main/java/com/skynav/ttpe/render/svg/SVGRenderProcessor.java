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
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.area.AnnotationArea;
import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.BlockFillerArea;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.Inline;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.fonts.GlyphMapping;
import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.style.AnnotationPosition;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Outline;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.util.Colors;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.geometry.Direction.*;
import static com.skynav.ttpe.text.Constants.*;

public class SVGRenderProcessor extends RenderProcessor {

    public static final String NAME                             = "svg";

    // static defaults
    private static final String defaultOutputFileNamePattern    = "ttps{0,number,000000}.svg";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "svg-background",             "COLOR",    "paint background of specified color into root region (default: transparent)" },
        { "svg-decorate-all",           "",         "decorate regions, lines, glyphs" },
        { "svg-decorate-glyphs",        "",         "decorate glyphs with bounding box" },
        { "svg-decorate-line-baselines","",         "decorate line baselines" },
        { "svg-decorate-line-bounds",   "",         "decorate line bounding boxes" },
        { "svg-decorate-line-labels",   "",         "decorate line labels" },
        { "svg-decorate-lines",         "",         "decorate line features (bounding baselines, boxes, labels)" },
        { "svg-decorate-regions",       "",         "decorate regions with bounding box" },
        { "svg-decoration",             "COLOR",    "paint decorations using specified color (default: color contrasting with specified background or black)" },
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
    public static final MessageFormat matrixFormatter          = new MessageFormat("matrix({0})");
    public static final MessageFormat translateFormatter       = new MessageFormat("translate({0,number,#.####},{1,number,#.####})");

    // options state
    private String backgroundOption;
    @SuppressWarnings("unused")
    private boolean decorateGlyphs;
    private boolean decorateLineBaselines;
    private boolean decorateLineBounds;
    private boolean decorateLineLabels;
    private boolean decorateRegions;
    private String decorationOption;
    private String outputPattern;

    // derived options state
    private Color backgroundColor;
    private Color decorationColor;

    // render state
    private double xCurrent;
    private double yCurrent;
    private List<SVGFrameRegion> regions;
    private int paragraphGenerationIndex;
    private int lineGenerationIndex;

    public SVGRenderProcessor(TransformerContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public String getOutputPattern() {
        return outputPattern;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        return longOptions.values();
    }

    @Override
    public int parseLongOption(List<String> args, int index) {
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("output-pattern")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputPattern = args.get(++index);
        } else if (option.equals("svg-background")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            backgroundOption = args.get(++index);
        } else if (option.equals("svg-decorate-all")) {
            // decorateGlyphs = true;
            decorateLineBaselines = true;
            decorateLineBounds = true;
            decorateLineLabels = true;
            decorateRegions = true;
        } else if (option.equals("svg-decorate-glyphs")) {
            decorateGlyphs = true;
        } else if (option.equals("svg-decorate-line-baselines")) {
            decorateLineBaselines = true;
        } else if (option.equals("svg-decorate-line-bounds")) {
            decorateLineBounds = true;
        } else if (option.equals("svg-decorate-line-labels")) {
            decorateLineLabels = true;
        } else if (option.equals("svg-decorate-lines")) {
            decorateLineBaselines = true;
            decorateLineBounds = true;
            decorateLineLabels = true;
        } else if (option.equals("svg-decorate-regions")) {
            decorateRegions = true;
        } else if (option.equals("svg-decoration")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            decorationOption = args.get(++index);
        } else {
            return super.parseLongOption(args, index);
        }
        return index + 1;
    }

    @Override
    public void processDerivedOptions() {
        super.processDerivedOptions();
        // backgroundColor
        Color backgroundColor;
        if (backgroundOption != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(backgroundOption, null, context, retColor)) {
                backgroundColor = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                throw new InvalidOptionUsageException("svg-background", "invalid color: " + backgroundOption);
        } else
            backgroundColor = null;
        this.backgroundColor = backgroundColor;
        // decoration
        Color decorationColor;
        if (decorationOption != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(decorationOption, null, context, retColor)) {
                decorationColor = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                throw new InvalidOptionUsageException("svg-decoration", "invalid color: " + decorationOption);
        } else
            decorationColor = (backgroundColor != null) ? backgroundColor.contrast() : Color.BLACK;
        this.decorationColor = decorationColor;
        // output pattern
        String outputPattern = this.outputPattern;
        if (outputPattern == null)
            outputPattern = defaultOutputFileNamePattern;
        this.outputPattern = outputPattern;
    }

    @Override
    public List<Frame> render(List<Area> areas) {
        List<Frame> frames = new java.util.ArrayList<Frame>();
        for (Area a : areas) {
            if (a instanceof CanvasArea) {
                Frame f = renderCanvas((CanvasArea) a);
                if (f != null)
                    frames.add(f);
            }
        }
        return frames;
    }

    @Override
    public void clear(boolean all) {
        xCurrent = 0;
        yCurrent = 0;
        regions = null;
    }

    protected Frame renderCanvas(CanvasArea a) {
        Reporter reporter = context.getReporter();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.appendChild(renderCanvas(null, a, d));
            Namespaces.normalize(d, SVGDocumentFrame.prefixes);
            return new SVGDocumentFrame(a.getBegin(), a.getEnd(), a.getExtent(), d, regions);
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        }
        return null;
    }

    private Element renderCanvas(Element parent, CanvasArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgSVGEltName);
        return renderChildren(e, a, d);
    }

    private Element renderViewport(Element parent, ViewportArea a, Document d) {
        Element e = parent;
        return renderChildren(e, a, d);
    }

    private Element renderReference(Element parent, ReferenceArea a, Document d) {
        Element eSVG;
        boolean root = isRootReference(a);
        if (root)
            eSVG = parent;
        else
            eSVG = Documents.createElement(d, SVGDocumentFrame.svgSVGEltName);
        Extent extent = a.getExtent();
        if (extent == null)
            extent = Extent.EMPTY;
        if (extent != null) {
            Documents.setAttribute(eSVG, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
            Documents.setAttribute(eSVG, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
        }
        if (root)  {
            if (backgroundColor != null) {
                Element eBackground = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eBackground, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eBackground, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eBackground, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
                if (backgroundColor.getAlpha() < 1)
                    Documents.setAttribute(eBackground, SVGDocumentFrame.opacityAttrName, doubleFormatter.format(new Object[] {backgroundColor.getAlpha()}));
                eSVG.appendChild(eBackground);
            }
            return renderChildren(eSVG, a, d);
        } else {
            Element eGroup = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
            Point origin = a.getOrigin();
            if (origin != null) {
                Documents.setAttribute(eGroup, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {origin.getX(),origin.getY()}));
                if (extent != null) {
                    String id = a.getParent().getId();
                    Documents.setAttribute(eSVG, SVGDocumentFrame.idAttrName, id);
                    Documents.setAttribute(eSVG, SVGDocumentFrame.classAttrName, "region");
                    addRegion(id, origin, extent);
                }
            }
            xCurrent = yCurrent = 0;
            WritingMode wm = a.getWritingMode();
            if (wm.isVertical()) {
                if (wm.getDirection(Dimension.BPD) == RL)
                    xCurrent += extent.getWidth();
            }
            if (decorateRegions) {
                Element eDecoration = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eDecoration, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.fillAttrName, "none");
                Documents.setAttribute(eDecoration, SVGDocumentFrame.strokeAttrName, decorationColor.toRGBString());
                eSVG.appendChild(eDecoration);
            }
            eGroup.appendChild(renderChildren(eSVG, a, d));
            paragraphGenerationIndex = 0;
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

    private void addRegion(String id, Point origin, Extent extent) {
        if (regions == null)
            regions = new java.util.ArrayList<SVGFrameRegion>();
        regions.add(new SVGFrameRegion(id, new Rectangle(origin, extent)));
    }

    private Element renderBlock(Element parent, BlockArea a, Document d) {
        Element e = parent;
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        // render children
        Element eBlockGroup = renderChildren(e, a, d);
        // update current position
        WritingMode wm = a.getWritingMode();
        if (a instanceof Inline) {
            double ipd = a.getIPD();
            if (a.isVertical())
                yCurrent = ySaved + ipd;
            else
                xCurrent = xSaved + ipd;
        }
        double bpd = a.getBPD();
        if (a.isVertical()) {
            Direction bpdDirection = wm.getDirection(Dimension.BPD);
            xCurrent = xSaved + bpd * ((bpdDirection == RL) ? -1 : 1);
        } else
            yCurrent = ySaved + bpd;
        // update decoration indices
        if (Documents.isElement(a.getElement(), ttParagraphElementName)) {
            ++paragraphGenerationIndex;
            lineGenerationIndex = 0;
        }
        return eBlockGroup;
    }

    private Element renderFiller(Element parent, BlockFillerArea a, Document d) {
        double bpd = a.getBPD();
        if (a.isVertical()) {
            if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                bpd = -bpd;
            xCurrent += bpd;
        } else
            yCurrent += bpd;
        return null;
    }

    private Element renderAnnotation(Element parent, AnnotationArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        LineArea l = a.getLine();
        WritingMode wm = l.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        Direction ipdDirection = wm.getDirection(Dimension.IPD);
        AnnotationPosition position = a.getPosition();
        double ipdOffset = (a.getAlignment() == InlineAlignment.CENTER) ? (a.getOverflow() / 2) : 0;
        if (ipdOffset > 0) {
            double lMeasure = l.getIPD();
            if (vertical) {
                yCurrent -= ipdOffset;
                if (yCurrent < 0)
                    yCurrent = 0;
                else if (yCurrent > lMeasure)
                    yCurrent = lMeasure - a.getIPD();
            } else {
                xCurrent -= ipdOffset;
                if (xCurrent < 0)
                    xCurrent = 0;
                else if (xCurrent > lMeasure)
                    xCurrent = lMeasure - a.getIPD();
            }
        }
        if (position == AnnotationPosition.AFTER) {
            double bpdOffset = l.getBPD() - a.getBPD();
            if (vertical) {
                if (bpdDirection == LR)
                    xCurrent += bpdOffset;
                else
                    xCurrent -= bpdOffset;
            } else {
               yCurrent += bpdOffset;
            }
        }
        if ((xCurrent != 0) || (yCurrent != 0))
            Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Double[] {xCurrent, yCurrent}));
        xCurrent = 0;
        yCurrent = 0;
        if (hasLineDecoration())
            decorateLine(e, a, d, vertical, bpdDirection, ipdDirection, true);
        maybeStyleLineGroup(e, a);
        e = renderChildren(e, a, d);
        xCurrent = xSaved;
        yCurrent = ySaved;
        return e;
    }

    private Element renderLine(Element parent, LineArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        WritingMode wm = a.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        Direction ipdDirection = wm.getDirection(Dimension.IPD);
        if ((xCurrent != 0) || (yCurrent != 0))
            Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Double[] {xCurrent, yCurrent}));
        xCurrent = 0;
        yCurrent = 0;
        if (hasLineDecoration())
            decorateLine(e, a, d, vertical, bpdDirection, ipdDirection, false);
        maybeStyleLineGroup(e, a);
        e = renderChildren(e, a, d);
        xCurrent = xSaved;
        yCurrent = ySaved;
        if (vertical) {
            if (bpdDirection == LR)
                xCurrent += a.getBPD();
            else
                xCurrent -= a.getBPD();
        } else {
            yCurrent += a.getBPD();
        }
        ++lineGenerationIndex;
        return e;
    }

    private boolean hasLineDecoration() {
        return decorateLineBaselines || decorateLineBounds || decorateLineLabels;
    }

    private void decorateLine(Element e, LineArea a, Document d, boolean vertical, Direction bpdDirection, Direction ipdDirection, boolean annotation) {
        boolean showBoundingBox = decorateLineBounds;
        boolean showLabel = decorateLineLabels && !annotation;
        double w, h;
        if (vertical) {
            h = a.getIPD();
            w = a.getBPD();
        } else {
            h = a.getBPD();
            w = a.getIPD();
        }
        double x, y;
        if (bpdDirection == RL) {
            x = xCurrent - w;
            y = yCurrent;
        } else {
            x = xCurrent;
            y = yCurrent;
        }
        // baseline [TBD]
        // bounding box
        if (showBoundingBox) {
            Element eDecoration = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
            Documents.setAttribute(eDecoration, SVGDocumentFrame.fillAttrName, "none");
            Documents.setAttribute(eDecoration, SVGDocumentFrame.strokeAttrName, decorationColor.toRGBString());
            Documents.setAttribute(eDecoration, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Double[] {w}));
            Documents.setAttribute(eDecoration, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Double[] {h}));
            if (x != 0)
                Documents.setAttribute(eDecoration, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x}));
            if (y != 0)
                Documents.setAttribute(eDecoration, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y}));
            e.appendChild(eDecoration);
        }
        // crop marks [TBD]
        // label
        if (showLabel) {
            Element eDecorationLabel = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
            String label = "P" + (paragraphGenerationIndex + 1) + "L" + (lineGenerationIndex + 1);
            Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.fontFamilyAttrName, "sans-serif");
            Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.fontSizeAttrName, "6");
            if (vertical) {
                if (bpdDirection == LR) {
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x + 6}));
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y + 3}));
                } else {
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x + w - 6}));
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y + 3}));
                }
            } else {
                if (ipdDirection == LR) {
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x + 2}));
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y + 8}));
                } else {
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x + w - (double) 4*label.length()}));
                    Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y + 8}));
                }
            }
            Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.fillAttrName, decorationColor.toRGBString());
            if (vertical)
                Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.writingModeAttrName, "tb");
            eDecorationLabel.appendChild(d.createTextNode(label));
            e.appendChild(eDecorationLabel);
        }
    }

    private void maybeStyleLineGroup(Element e, LineArea a) {
        if (hasGlyphChild(a)) {
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
        }
    }

    private boolean hasGlyphChild(LineArea l) {
        for (Area a : ((NonLeafAreaNode) l).getChildren()) {
            if (a instanceof GlyphArea)
                return true;
        }
        return false;
    }

    private Element renderGlyphs(Element parent, GlyphArea a, Document d) {
        Element g = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        LineArea l = a.getLine();
        double bpdLine = l.getBPD();
        double bpdLineAnnotationBefore = l.getAnnotationBPD(AnnotationPosition.BEFORE);
        double bpdLineSansAnnotationBefore = bpdLine - bpdLineAnnotationBefore;
        double bpdLineAnnotationAfter = l.getAnnotationBPD(AnnotationPosition.AFTER);
        double bpdLineSansAnnotation = bpdLine - (bpdLineAnnotationBefore + bpdLineAnnotationAfter);
        double bpdGlyphs = a.getBPD();
        double ipdGlyphs = a.getIPD();
        Font font = a.getFont();
        boolean combined = a.isCombined();
        if (a.isVertical()) {
            double baselineOffset = bpdLineAnnotationBefore;
            double yOffset = 0;
            boolean rotate = a.isRotatedOrientation();
            if (rotate && !combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotation - font.getHeight())/2 + font.getAscent();
                else
                    baselineOffset += (bpdLineSansAnnotation - bpdGlyphs)/2 + font.getLeading()/2;
            } else if (combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotationBefore + ipdGlyphs)/2;
                else
                    baselineOffset += (bpdLineSansAnnotationBefore - ipdGlyphs)/2;
                yOffset = font.getHeight();
            } else {
                baselineOffset += bpdLineSansAnnotation/2;
            }
            if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                baselineOffset *= -1;
            StringBuffer sb = new StringBuffer(translateFormatter.format(new Object[] {baselineOffset, yCurrent + yOffset}));
            if (rotate && !combined)
                sb.append(",rotate(90)");
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, sb.toString());
        } else {
            double baselineOffset = font.getHeight() + bpdLineAnnotationBefore;
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, baselineOffset}));
        }
        List<Decoration> decorations = a.getDecorations();
        Element e;
        e = renderGlyphText(g, a, d, decorations);
        assert e != null;
        g.appendChild(e);
        if (a.isVertical()) {
            yCurrent += combined ? bpdGlyphs : ipdGlyphs;
        } else {
            xCurrent += ipdGlyphs;
        }
        return g;
    }

    private Element renderGlyphText(Element parent, GlyphArea a, Document d, List<Decoration> decorations) {
        if (a.isVertical() && !a.isRotatedOrientation() && !a.isCombined())
            return renderGlyphTextVertical(parent, a, d, decorations);
        else
            return renderGlyphTextHorizontal(parent, a, d, decorations);
    }

    private Element renderGlyphTextVertical(Element parent, GlyphArea a, Document d, List<Decoration> decorations) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        Documents.setAttribute(gOuter, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {-font.getWidth()/2,font.getAscent()}));
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.VERTICAL, rotate);
        GlyphMapping gm = a.getGlyphMapping();
        String text = gm.getGlyphsAsText();
        double[] advances = font.getScaledAdvances(gm);
        boolean btt = false;
        double ySaved = yCurrent;
        yCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, a.isCombined());
        if (shearAdvance < 0)
            yCurrent += -shearAdvance;
        for (int i = 0, n = advances.length; i < n; ++i) {
            double ga = advances[i];
            double y = btt ? yCurrent - ga : yCurrent;
            int j = i + 1;
            String tGlyphs = text.substring(i, j);
            String tGlyphsPath;
            if (font.containsPUAMapping(tGlyphs))
                tGlyphsPath = font.getGlyphsPath(tGlyphs, Axis.VERTICAL, Arrays.copyOfRange(advances, i, j));
            else
                tGlyphsPath = null;
            // outline if required
            Element tOutline;
            Decoration decorationOutline = findDecoration(decorations, Decoration.Type.OUTLINE, i, j);
            if (decorationOutline != null) {
                Outline outline = decorationOutline.getOutline();
                if (tGlyphsPath != null)
                    tOutline = Documents.createElement(d, SVGDocumentFrame.svgPathEltName);
                else
                    tOutline = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
                Documents.setAttribute(tOutline, SVGDocumentFrame.strokeAttrName, outline.getColor().toRGBString());
                Documents.setAttribute(tOutline, SVGDocumentFrame.strokeWidthAttrName, doubleFormatter.format(new Object[] {outline.getThickness()}));
                Documents.setAttribute(tOutline, SVGDocumentFrame.fillAttrName, "none");
                if (tGlyphsPath != null) {
                    Documents.setAttribute(tOutline, SVGDocumentFrame.dAttrName, tGlyphsPath);
                } else
                    tOutline.appendChild(d.createTextNode(tGlyphs));
            } else
                tOutline = null;
            // text
            Element t;
            Color tColor;
            Color lColor = a.getLine().getColor();
            Decoration decorationColor = findDecoration(decorations, Decoration.Type.COLOR, i, j);
            if (decorationColor != null)
                tColor = decorationColor.getColor();
            else
                tColor = lColor;
            if (tGlyphsPath == null) {
                t = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
                t.appendChild(d.createTextNode(tGlyphs));
            } else {
                t = Documents.createElement(d, SVGDocumentFrame.svgPathEltName);
                Documents.setAttribute(t, SVGDocumentFrame.dAttrName, tGlyphsPath);
                Documents.setAttribute(t, SVGDocumentFrame.strokeAttrName, tColor.toRGBString());
                Documents.setAttribute(t, SVGDocumentFrame.strokeWidthAttrName, "0.5");
            }
            if (!tColor.equals(lColor))
                Documents.setAttribute(t, SVGDocumentFrame.fillAttrName, tColor.toRGBString());
            // group wrapper (gInner) if font transform required or using glyphs path
            if ((fontMatrix != null) || ((tGlyphsPath != null) && (y != 0))) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                if (y != 0)
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {0,y}));
                if (tOutline != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(tOutline);
                }
                if (fontMatrix != null)
                    Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                gInner.appendChild(t);
                gOuter.appendChild(gInner);
            } else {
                if (tOutline != null) {
                    if (y != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(tOutline);
                }
                if (y != 0)
                    Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                gOuter.appendChild(t);
            }
            yCurrent += btt ? -ga : ga;
        }
        yCurrent = ySaved;
        return gOuter;
    }

    private Element renderGlyphTextHorizontal(Element parent, GlyphArea a, Document d, List<Decoration> decorations) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.HORIZONTAL, rotate);
        GlyphMapping gm = a.getGlyphMapping();
        String text = gm.getGlyphsAsText();
        double[] advances = font.getScaledAdvances(gm);
        double[][] adjustments = font.getScaledAdjustments(gm);
        boolean rtl = false;
        double xSaved = xCurrent;
        xCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, a.isCombined());
        if ((shearAdvance < 0) && rotate)
            xCurrent += -shearAdvance;
        for (int i = 0, n = advances.length; i < n; ++i) {
            double ga = advances[i];
            double x = rtl ? xCurrent - ga : xCurrent;
            double y = getCrossShearAdjustment(a);
            if (adjustments != null) {
                double[] aa = adjustments[i];
                if (aa != null) {
                    x  += aa[0];
                    y  -= aa[1];
                    ga += aa[2];
                }
            }
            int j = i + 1;
            String tGlyphs = text.substring(i, j);
            String tGlyphsPath;
            if (font.containsPUAMapping(tGlyphs))
                tGlyphsPath = font.getGlyphsPath(tGlyphs, Axis.HORIZONTAL, Arrays.copyOfRange(advances, i, j));
            else
                tGlyphsPath = null;
            // outline if required
            Element tOutline;
            Decoration decorationOutline = findDecoration(decorations, Decoration.Type.OUTLINE, i, j);
            if (decorationOutline != null) {
                Outline outline = decorationOutline.getOutline();
                if (tGlyphsPath != null)
                    tOutline = Documents.createElement(d, SVGDocumentFrame.svgPathEltName);
                else
                    tOutline = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
                Documents.setAttribute(tOutline, SVGDocumentFrame.strokeAttrName, outline.getColor().toRGBString());
                Documents.setAttribute(tOutline, SVGDocumentFrame.strokeWidthAttrName, doubleFormatter.format(new Object[] {outline.getThickness()}));
                Documents.setAttribute(tOutline, SVGDocumentFrame.fillAttrName, "none");
                if (tGlyphsPath != null)
                    Documents.setAttribute(tOutline, SVGDocumentFrame.dAttrName, tGlyphsPath);
                else
                    tOutline.appendChild(d.createTextNode(tGlyphs));
            } else
                tOutline = null;
            // text
            Element t;
            Color tColor;
            Color lColor = a.getLine().getColor();
            Decoration decorationColor = findDecoration(decorations, Decoration.Type.COLOR, i, j);
            if (decorationColor != null)
                tColor = decorationColor.getColor();
            else
                tColor = lColor;
            if (tGlyphsPath == null) {
                t = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
                t.appendChild(d.createTextNode(tGlyphs));
            } else {
                t = Documents.createElement(d, SVGDocumentFrame.svgPathEltName);
                Documents.setAttribute(t, SVGDocumentFrame.dAttrName, tGlyphsPath);
                Documents.setAttribute(t, SVGDocumentFrame.strokeAttrName, tColor.toRGBString());
                Documents.setAttribute(t, SVGDocumentFrame.strokeWidthAttrName, "0.5");
            }
            if (!tColor.equals(lColor))
                Documents.setAttribute(t, SVGDocumentFrame.fillAttrName, tColor.toRGBString());
            // group wrapper (gInner) if font transform required or using glyphs path
            if ((fontMatrix != null) || (tGlyphsPath != null) && ((x != 0) || (y != 0))) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                if ((x != 0) || (y != 0))
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {x,y}));
                if (tOutline != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(tOutline);
                }
                if (fontMatrix != null)
                    Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                gInner.appendChild(t);
                gOuter.appendChild(gInner);
            } else {
                if (tOutline != null) {
                    if (x != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(tOutline);
                }
                if (x != 0)
                    Documents.setAttribute(t, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                if (y != 0)
                    Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                gOuter.appendChild(t);
            }
            xCurrent += rtl ? -ga : ga;
        }
        xCurrent = xSaved;
        return gOuter;
    }

    private double getCrossShearAdjustment(GlyphArea a) {
        if (a.isCombined()) {
            AreaNode aPrev = a.getPreviousSibling();
            if ((aPrev != null) && (aPrev instanceof GlyphArea)) {
                GlyphArea aPrevGlyphs = (GlyphArea) aPrev;
                if (!aPrevGlyphs.isCombined())
                    return - aPrevGlyphs.getFont().getShearAdvance(false, true)/2;
            }
        }
        return 0;
    }

    private Decoration findDecoration(List<Decoration> decorations, Decoration.Type type, int from, int to) {
        if (decorations != null) {
            for (Decoration d : decorations) {
                if (d.isType(type) && d.intersects(from, to))
                    return d;
            }
        }
        return null;
    }

    private Element renderSpace(Element parent, SpaceArea a, Document d) {
        double ipd = a.getIPD();
        if (a.isVertical())
            yCurrent += ipd;
        else
            xCurrent += ipd;
        return null;
    }

    private Element renderFiller(Element parent, InlineFillerArea a, Document d) {
        double ipd = a.getIPD();
        if (a.isVertical())
            yCurrent += ipd;
        else
            xCurrent += ipd;
        return null;
    }

    private Element renderChildren(Element parent, Area a, Document d) {
        if (a instanceof NonLeafAreaNode) {
            for (Area c : ((NonLeafAreaNode) a).getChildren()) {
                Element e = renderArea(parent, c, d);
                if (e != null) {
                    if (e != parent) {
                        parent.appendChild(e);
                    }
                }
            }
        }
        return parent;
    }

    private Element renderArea(Element parent, Area a, Document d) {
        if (a instanceof GlyphArea)
            return renderGlyphs(parent, (GlyphArea) a, d);
        else if (a instanceof SpaceArea)
            return renderSpace(parent, (SpaceArea) a, d);
        else if (a instanceof InlineFillerArea)
            return renderFiller(parent, (InlineFillerArea) a, d);
        else if (a instanceof AnnotationArea)
            return renderAnnotation(parent, (AnnotationArea) a, d);
        else if (a instanceof LineArea)
            return renderLine(parent, (LineArea) a, d);
        else if (a instanceof ReferenceArea)
            return renderReference(parent, (ReferenceArea) a, d);
        else if (a instanceof ViewportArea)
            return renderViewport(parent, (ViewportArea) a, d);
        else if (a instanceof BlockFillerArea)
            return renderFiller(parent, (BlockFillerArea) a, d);
        else if (a instanceof BlockArea)
            return renderBlock(parent, (BlockArea) a, d);
        else
            throw new IllegalArgumentException();
    }

}
