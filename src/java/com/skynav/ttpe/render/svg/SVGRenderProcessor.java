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

import com.skynav.ttpe.area.AnnotationArea;
import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.Inline;
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
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.Emphasis;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttpe.util.Strings;
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
        { "svg-decorate-all",           "",         "decorate regions, lines, glyphs, etc., for debugging purposes" },
        { "svg-decorate-glyphs",        "",         "decorate glyphs with bounding box, etc., for debugging purposes" },
        { "svg-decorate-lines",         "",         "decorate lines with bounding box, etc., for debugging purposes" },
        { "svg-decorate-regions",       "",         "decorate regions with bounding box, etc., for debugging purposes" },
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
    private boolean decorateLines;
    private boolean decorateRegions;
    private String decorationOption;
    private String outputPattern;

    // derived options state
    private Color background;
    private Color decoration;

    // render state
    private double xCurrent;
    private double yCurrent;
    private List<Rectangle> regions;
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
    public int parseLongOption(String args[], int index) {
        String option = args[index];
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("output-pattern")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            outputPattern = args[++index];
        } else if (option.equals("svg-background")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            backgroundOption = args[++index];
        } else if (option.equals("svg-decorate-all")) {
            decorateGlyphs = true;
            decorateLines = true;
            decorateRegions = true;
        } else if (option.equals("svg-decorate-glyphs")) {
            decorateGlyphs = true;
        } else if (option.equals("svg-decorate-lines")) {
            decorateLines = true;
        } else if (option.equals("svg-decorate-regions")) {
            decorateRegions = true;
        } else if (option.equals("svg-decoration")) {
            if (index + 1 > args.length)
                throw new MissingOptionArgumentException("--" + option);
            decorationOption = args[++index];
        } else {
            return super.parseLongOption(args, index);
        }
        return index + 1;
    }

    @Override
    public void processDerivedOptions() {
        super.processDerivedOptions();
        // background
        Color background;
        if (backgroundOption != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(backgroundOption, null, context, retColor)) {
                background = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                throw new InvalidOptionUsageException("svg-background", "invalid color: " + backgroundOption);
        } else
            background = null;
        this.background = background;
        // decoration
        Color decoration;
        if (decorationOption != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (Colors.isColor(decorationOption, null, context, retColor)) {
                decoration = new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha());
            } else
                throw new InvalidOptionUsageException("svg-decoration", "invalid color: " + decorationOption);
        } else
            decoration = (background != null) ? background.contrast() : Color.BLACK;
        this.decoration = decoration;
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
        } catch (Exception e) {
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
            if (background != null) {
                Element eBackground = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eBackground, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eBackground, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eBackground, SVGDocumentFrame.fillAttrName, background.toRGBString());
                if (background.getAlpha() < 1)
                    Documents.setAttribute(eBackground, SVGDocumentFrame.opacityAttrName, doubleFormatter.format(new Object[] {background.getAlpha()}));
                eSVG.appendChild(eBackground);
            }
            return renderChildren(eSVG, a, d);
        } else {
            Element eGroup = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
            Point origin = a.getOrigin();
            if (origin != null) {
                Documents.setAttribute(eGroup, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {origin.getX(),origin.getY()}));
                if (extent != null)
                    addRegion(origin, extent);
            }
            xCurrent = yCurrent = 0;
            WritingMode wm = a.getWritingMode();
            if (wm.isVertical()) {
                if (wm.getDirection(Dimension.BPD) == RL)
                    xCurrent += extent.getWidth();
            } else {
                if (wm.getDirection(Dimension.IPD) == RL)
                    xCurrent += extent.getWidth();
            }
            BlockAlignment align = a.getBlockAlignment();
            if (align != null) {
                double available = a.getBPD();
                double consumed = computeChildrenBPD(a);
                double free = available - consumed;
                double adjust = 0;
                if (free < 0)
                    free = 0;
                if (align == BlockAlignment.BEFORE) {
                    // no-op
                } else if (align == BlockAlignment.AFTER) {
                    adjust = free;
                } else if (align == BlockAlignment.CENTER) {
                    adjust = free / 2;
                } else if (align == BlockAlignment.JUSTIFY) {
                    // [TBD] - IMPLEMENT ME
                }
                if (adjust > 0) {
                    if (wm.isVertical()) {
                        if (wm.getDirection(Dimension.BPD) == RL)
                            adjust *= -1;
                        xCurrent += adjust;
                    } else {
                        yCurrent += adjust;
                    }
                }
            }
            if (decorateRegions) {
                Element eDecoration = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eDecoration, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.fillAttrName, "none");
                Documents.setAttribute(eDecoration, SVGDocumentFrame.strokeAttrName, decoration.toRGBString());
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

    private void addRegion(Point origin, Extent extent) {
        if (regions == null)
            regions = new java.util.ArrayList<Rectangle>();
        regions.add(new Rectangle(origin, extent));
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
            else {
                Direction ipdDirection = wm.getDirection(Dimension.IPD);
                xCurrent = xSaved + ipd * ((ipdDirection == RL) ? -1 : 1);
            }
        }
        double bpd = a.getBPD();
        if (a.isVertical()) {
            Direction bpdDirection = wm.getDirection(Dimension.BPD);
            xCurrent = ySaved + bpd * ((bpdDirection == RL) ? -1 : 1);
        } else
            yCurrent = ySaved + bpd;
        // update decoration indices
        if (Documents.isElement(a.getElement(), ttParagraphElementName)) {
            ++paragraphGenerationIndex;
            lineGenerationIndex = 0;
        }
        return eBlockGroup;
    }

    private Element renderAnnotation(Element parent, AnnotationArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        LineArea l = getLine(a);
        assert l != null;
        WritingMode wm = l.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        AnnotationPosition position = a.getPosition();
        if (position == AnnotationPosition.BEFORE) {
            double bpdOffset = a.getBPD();
            if (vertical) {
                if (bpdDirection == LR)
                    xCurrent -= bpdOffset;
                else
                    xCurrent += bpdOffset;
            } else {
               yCurrent -= bpdOffset;
            }
        } else if (position == AnnotationPosition.AFTER) {
            double bpdOffset = l.getBPD() + a.getLeadingBefore();
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
        if (decorateLines)
            decorateLine(e, a, d, vertical, bpdDirection, true);
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
        return e;
    }

    private LineArea getLine(AreaNode a) {
        while (a != null) {
            if (a instanceof LineArea)
                return (LineArea) a;
            else
                a = a.getParent();
        }
        return null;
    }

    private Element renderLine(Element parent, LineArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        WritingMode wm = a.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        double bpdAnnotationBefore = a.getAnnotationBPD(AnnotationPosition.BEFORE);
        if (vertical) {
            if (bpdDirection == LR)
                xCurrent += bpdAnnotationBefore;
            else
                xCurrent -= bpdAnnotationBefore;
        } else {
           yCurrent += bpdAnnotationBefore;
        }
        if ((xCurrent != 0) || (yCurrent != 0))
            Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Double[] {xCurrent, yCurrent}));
        xCurrent = 0;
        yCurrent = 0;
        if (decorateLines)
            decorateLine(e, a, d, vertical, bpdDirection, false);
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

    private void decorateLine(Element e, LineArea a, Document d, boolean vertical, Direction bpdDirection, boolean annotation) {
        boolean showBoundingBox = true;
        boolean showLabel = !annotation;
        double w, h;
        if (vertical) {
            h = a.getIPD();
            w = a.getBPD();
        } else {
            h = a.getBPD();
            w = a.getIPD();
        }
        double x, y;
        if (vertical) {
            if (bpdDirection == LR) {
                x = xCurrent;
                y = yCurrent;
            } else {
                x = xCurrent - w;
                y = yCurrent;
            }
        } else {
            x = xCurrent;
            y = yCurrent;
        }
        // bounding box
        if (showBoundingBox) {
            Element eDecoration = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
            Documents.setAttribute(eDecoration, SVGDocumentFrame.fillAttrName, "none");
            Documents.setAttribute(eDecoration, SVGDocumentFrame.strokeAttrName, decoration.toRGBString());
            Documents.setAttribute(eDecoration, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Double[] {w}));
            Documents.setAttribute(eDecoration, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Double[] {h}));
            if (x != 0)
                Documents.setAttribute(eDecoration, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x}));
            if (y != 0)
                Documents.setAttribute(eDecoration, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y}));
            e.appendChild(eDecoration);
        }
        // crop marks
        // label
        if (showLabel) {
            Element eDecorationLabel = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
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
                Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Double[] {x + 2}));
                Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Double[] {y + 8}));
            }
            Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.fillAttrName, decoration.toRGBString());
            if (vertical)
                Documents.setAttribute(eDecorationLabel, SVGDocumentFrame.writingModeAttrName, "tb");
            eDecorationLabel.appendChild(d.createTextNode("P" + (paragraphGenerationIndex + 1) + "L" + (lineGenerationIndex + 1)));
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
            TransformMatrix fontMatrix = font.getTransform();
            if (fontMatrix != null)
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
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
        double ipd = a.getIPD();
        double bpd = a.getBPD();
        if (a.isVertical()) {
            double baselineOffset = (bpd / 2) * ((a.getWritingMode().getDirection(Dimension.BPD) == LR) ? 1 : -1);
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {baselineOffset, yCurrent}));
        } else {
            double baselineOffset = a.getFont().getHeight();
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, baselineOffset}));
        }
        List<Decoration> decorations = a.getDecorations();
        Element e;
        if ((e = renderGlyphText(g, a, d, getColorDecorations(decorations))) != null)
            g.appendChild(e);
        if ((e = renderGlyphEmphases(g, a, d, getEmphasisDecorations(decorations))) != null)
            g.appendChild(e);
        if (a.isVertical())
            yCurrent += ipd;
        else
            xCurrent += ipd;
        return g;
    }

    private List<Decoration> getColorDecorations(List<Decoration> decorations) {
        List<Decoration> colors = new java.util.ArrayList<Decoration>();
        for (Decoration d : decorations) {
            if (d.isColor())
                colors.add(d);
        }
        return !colors.isEmpty() ? colors : null;
    }

    private List<Decoration> getEmphasisDecorations(List<Decoration> decorations) {
        List<Decoration> emphases = new java.util.ArrayList<Decoration>();
        for (Decoration d : decorations) {
            if (d.isEmphasis())
                emphases.add(d);
        }
        return !emphases.isEmpty() ? emphases : null;
    }

    private Element renderGlyphText(Element parent, GlyphArea a, Document d, List<Decoration> colors) {
        String text = a.getText();
        Font font = a.getFont();
        Element e = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
        if (a.isVertical()) {
            Documents.setAttribute(e, SVGDocumentFrame.writingModeAttrName, "tb");
        } else {
            double[] kerning = font.getKerning(text);
            if (kerning != null) {
                StringBuffer sb = new StringBuffer();
                sb.append("0");
                for (int i = 0; i < kerning.length - 1; ++i) {
                    sb.append(',');
                    sb.append(doubleFormatter.format(new Object[] {kerning[i]}));
                }
                Documents.setAttribute(e, SVGDocumentFrame.dxAttrName, sb.toString());
            }
        }
        TransformMatrix fontMatrix = font.getTransform();
        if (fontMatrix != null)
            Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
        e.appendChild(d.createTextNode(text));
        return e;
    }

    private Element renderGlyphEmphases(Element parent, GlyphArea a, Document d, List<Decoration> emphases) {
        if ((emphases != null) && !emphases.isEmpty()) {
            String text = a.getText();
            int numLines = a.getContainingBlock().getLineCount();
            boolean firstLine = a.getLine().isFirstLine();
            Element eBefore = renderGlyphEmphasis(parent, a, d, getEmphasisText(a, text, emphases, Emphasis.Position.BEFORE, numLines, firstLine), Emphasis.Position.BEFORE);
            Element eAfter = renderGlyphEmphasis(parent, a, d, getEmphasisText(a, text, emphases, Emphasis.Position.AFTER, numLines,firstLine), Emphasis.Position.AFTER);
            if ((eBefore == null) && (eAfter == null))
                return null;
            else if ((eBefore != null) && (eAfter == null))
                return eBefore;
            else if ((eBefore == null) && (eAfter != null))
                return eAfter;
            else {
                Element g = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                g.appendChild(eBefore);
                g.appendChild(eAfter);
                return g;
            }
        } else
            return null;
    }

    private String getEmphasisText(GlyphArea a, String text, List<Decoration> emphases, Emphasis.Position position, int numLines, boolean firstLine) {
        char[] ea = new char[text.length()];
        for (Decoration decoration : emphases) {
            assert decoration.isEmphasis();                
            Emphasis emphasis = decoration.getEmphasis();
            if (emphasis.isNone())
                continue;
            String et = emphasis.resolveText(a.getWritingMode().getAxis(Dimension.IPD));
            if ((et == null) || et.isEmpty())
                continue;
            char ec = et.charAt(0);
            if (emphasis.resolvePosition(numLines, firstLine) != position)
                continue;
            if ((ec >= 0xD800) && (ec < 0xE000))
                throw new UnsupportedOperationException("non-BMP emphasis characters not yet supported");
            for (int i = decoration.getBegin(), e = decoration.getEnd(); i < e; ++i) {
                char c = text.charAt(i);
                if ((c >= 0xD800) && (c < 0xE000))
                    throw new UnsupportedOperationException("emphasis on non-BMP characters not yet supported");
                if (decoration.intersects(i, i + 1))
                    ea[i] = ec;
            }
        }
        for (int i = 0, n = ea.length; i < n; ++i) {
            if (ea[i] == 0)
                ea[i] = Characters.UC_SPACE;
        }
        return new String(ea);
    }

    private Element renderGlyphEmphasis(Element parent, GlyphArea a, Document d, String emphasis, Emphasis.Position position) {
        String text = a.getText();
        if ((text == null) || text.isEmpty() || Strings.isWhitespace(text))
            return null;
        if ((emphasis == null) || emphasis.isEmpty())
            return null;
        Font font = a.getFont();
        Rectangle[] bounds = font.getGlyphBounds(text);
        if (bounds == null)
            return null;
        Element e = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
        double[] advances = font.getAdvances(text);
        int[] glyphs = font.getGlyphs(text);
        Font fontEmphasis = font.getScaledFont(0.5);
        double[] advancesEmphasis = fontEmphasis.getAdvances(emphasis);
        Rectangle[] boundsEmphasis = fontEmphasis.getGlyphBounds(emphasis);
        double dxLast = 0;
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = glyphs.length; i < n; ++i) {
            Rectangle bt = bounds[i];
            double ct = bt.getX() + bt.getWidth()/2;
            Rectangle be = boundsEmphasis[i];
            double ce = be.getX() + be.getWidth()/2;
            double dc = ct - ce;
            if (sb.length() > 0)
                sb.append(',');
            sb.append(doubleFormatter.format(new Object[] {dxLast + dc}));
            dxLast = advances[i] - advancesEmphasis[i] - dc;
        }
        if (sb.length() > 0)
            Documents.setAttribute(e, SVGDocumentFrame.dxAttrName, sb.toString());
        if (position == Emphasis.Position.AFTER)
            Documents.setAttribute(e, SVGDocumentFrame.dyAttrName, doubleFormatter.format(new Object[] {fontEmphasis.getHeight() + fontEmphasis.getLeading()/2}));
        else
            Documents.setAttribute(e, SVGDocumentFrame.dyAttrName, doubleFormatter.format(new Object[] {-font.getHeight()}));
        Documents.setAttribute(e, SVGDocumentFrame.fontSizeAttrName, doubleFormatter.format(new Object[] {fontEmphasis.getHeight()}));
        e.appendChild(d.createTextNode(emphasis));
        return e;
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
        else if (a instanceof BlockArea)
            return renderBlock(parent, (BlockArea) a, d);
        else
            throw new IllegalArgumentException();
    }

    private double computeChildrenBPD(Area a) {
        double bpd = 0;
        if (a instanceof NonLeafAreaNode) {
            for (Area c : ((NonLeafAreaNode) a).getChildren()) {
                if (c instanceof BlockArea) {
                    bpd += ((BlockArea) c).getBPD();
                }
            }
        }
        return bpd;
    }

}
