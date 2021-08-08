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

package com.skynav.ttpe.render.svg;

import java.net.URI;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
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
import com.skynav.ttpe.area.BlockImageArea;
import com.skynav.ttpe.area.BoundedBlockArea;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.Inline;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.InlineImageArea;
import com.skynav.ttpe.area.InlinePaddingArea;
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
import com.skynav.ttpe.render.FrameResource;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.style.AnnotationPosition;
import com.skynav.ttpe.style.BackgroundColor;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.Image;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Outline;
import com.skynav.ttpe.style.Visibility;
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

    public static final String NAME                                     = "svg";

    // static defaults
    private static final String defaultOutputFileNamePattern            = "ttps{0,number,000000}.svg";
    private static final String defaultOutputFileNamePatternResource    = "ttpr{0,number,000000}.dat";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "output-pattern-resource",    "PATTERN",  "specify output resource file name pattern" },
        { "svg-background",             "COLOR",    "paint background of specified color into root region (default: transparent)" },
        { "svg-decorate-all",           "",         "decorate regions, lines, glyphs" },
        { "svg-decorate-glyphs",        "",         "decorate glyphs with bounding box" },
        { "svg-decorate-line-baselines","",         "decorate line baselines" },
        { "svg-decorate-line-bounds",   "",         "decorate line bounding boxes" },
        { "svg-decorate-line-labels",   "",         "decorate line labels" },
        { "svg-decorate-lines",         "",         "decorate line features (bounding baselines, boxes, labels)" },
        { "svg-decorate-none",          "",         "disble decorations on regions, lines, glyphs" },
        { "svg-decorate-regions",       "",         "decorate regions with bounding box" },
        { "svg-decoration",             "COLOR",    "paint decorations using specified color (default: color contrasting with specified background or black)" },
        { "svg-mark-classes",           "",         "mark area classes" },
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // miscellaneous statics
    public static final MessageFormat doubleFormatter          = new MessageFormat("{0,number,#.####}", Locale.US);
    public static final MessageFormat matrixFormatter          = new MessageFormat("matrix({0})", Locale.US);
    public static final MessageFormat translateFormatter       = new MessageFormat("translate({0,number,#.####},{1,number,#.####})", Locale.US);
    public static final MessageFormat translateSkewXFormatter  = new MessageFormat("translate({0,number,#.####},{1,number,#.####}),skewX({2,number,#.####})", Locale.US);
    public static final MessageFormat translateSkewYFormatter  = new MessageFormat("translate({0,number,#.####},{1,number,#.####}),skewY({2,number,#.####})", Locale.US);
    public static final double        bgFuzz                   = 0.5;

    // options state
    private String backgroundOption;
    @SuppressWarnings("unused")
    private boolean decorateGlyphs;
    private boolean decorateLineBaselines;
    private boolean decorateLineBounds;
    private boolean decorateLineLabels;
    private boolean decorateNone;
    private boolean decorateRegions;
    private String decorationOption;
    private boolean markClasses;
    private String outputPatternResource;
    private String outputPattern;

    // derived options state
    private Color backgroundColor;
    private Color decorationColor;
    private MessageFormat outputPatternResourceFormatter;

    // render state
    private double xCurrent;
    private double yCurrent;
    private List<SVGFrameRegion> regions;
    private int paragraphGenerationIndex;
    private int lineGenerationIndex;
    private List<FrameResource> resources;
    private int resourceGenerationIndex;

    public SVGRenderProcessor(TransformerContext context) {
        super(context);
    }

    @Override
    public void resetAllState(boolean restart) {
        resetRenderState(restart);
        resetDerivedOptionsState(restart);
        resetOptionsState(restart);
    }

    private void resetRenderState(boolean restart) {
        xCurrent = 0;
        yCurrent = 0;
        regions = null;
        paragraphGenerationIndex = 0;
        lineGenerationIndex = 0;
        resources = null;
        resourceGenerationIndex = 0;
    }

    private void resetDerivedOptionsState(boolean restart) {
        backgroundColor = null;
        decorationColor = null;
        outputPatternResourceFormatter = null;
    }

    private void resetOptionsState(boolean restart) {
        backgroundOption = null;
        decorateGlyphs = false;
        decorateLineBaselines = false;
        decorateLineBounds = false;
        decorateLineLabels = false;
        decorateNone = false;
        decorateRegions = false;
        decorationOption = null;
        markClasses = false;
        outputPatternResource = null;
        outputPattern = null;
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
        } else if (option.equals("output-pattern-resource")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            outputPatternResource = args.get(++index);
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
        } else if (option.equals("svg-decorate-none")) {
            decorateNone = true;
        } else if (option.equals("svg-decorate-regions")) {
            decorateRegions = true;
        } else if (option.equals("svg-decoration")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            decorationOption = args.get(++index);
        } else if (option.equals("svg-mark-classes")) {
            markClasses = true;
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
        if (decorateNone) {
            decorateGlyphs = false;
            decorateLineBaselines = false;
            decorateLineBounds = false;
            decorateLineLabels = false;
            decorateRegions = false;
        }
        // output pattern
        String outputPattern = this.outputPattern;
        if (outputPattern == null)
            outputPattern = defaultOutputFileNamePattern;
        this.outputPattern = outputPattern;
        // output pattern for resources
        String outputPatternResource = this.outputPatternResource;
        if (outputPatternResource == null)
            outputPatternResource = defaultOutputFileNamePatternResource;
        this.outputPatternResource = outputPatternResource;
        this.outputPatternResourceFormatter = new MessageFormat(outputPatternResource, Locale.US);
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
        resources = null;
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
            return new SVGDocumentFrame(a.getBegin(), a.getEnd(), a.getExtent(), d, resources, regions);
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

    private void maybeMarkClasses(Element eTarget, Area a, String renderClass) {
        if (markClasses) {
            StringBuffer sb = new StringBuffer();
            sb.append(renderClass);
            Element eGenerator = a.getElement();
            if (eGenerator != null) {
                sb.append(' ');
                sb.append(eGenerator.getLocalName());
            }
            Documents.setAttribute(eTarget, SVGDocumentFrame.classAttrName, sb.toString());
        }
    }

    private Element renderReference(Element parent, ReferenceArea a, Document d) {
        Element eSVG;
        boolean root = isRootReference(a);
        if (root)
            eSVG = parent;
        else
            eSVG = Documents.createElement(d, SVGDocumentFrame.svgSVGEltName);
        maybeMarkClasses(eSVG, a, "viewport");
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
            maybeMarkClasses(eGroup, a, "reference");
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
            // render region presentation traits
            Color bColor = a.getBackgroundColor();
            if ((bColor != null) && !bColor.isTransparent()) {
                Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, bColor.toRGBString());
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
                eSVG.appendChild(eBackgroundColor);
            }
            if (decorateRegions) {
                Element eDecoration = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eDecoration, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
                Documents.setAttribute(eDecoration, SVGDocumentFrame.fillAttrName, "none");
                Documents.setAttribute(eDecoration, SVGDocumentFrame.strokeAttrName, decorationColor.toRGBString());
                eSVG.appendChild(eDecoration);
            }
            Point contentOrigin = a.getContentOrigin();
            xCurrent = contentOrigin.getX();
            yCurrent = contentOrigin.getY();
            WritingMode wm = a.getWritingMode();
            if (wm.isVertical()) {
                if (wm.getDirection(Dimension.BPD) == RL)
                    xCurrent += a.getContentExtent().getWidth();
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

    private FrameResource addResource(Image image) {
        return addResource(FrameResource.Type.IMAGE, generateResourceName(), image.getSource());
    }

    private String generateResourceName() {
        return outputPatternResourceFormatter.format(new Object[]{Integer.valueOf(++resourceGenerationIndex)});
    }

    private FrameResource addResource(FrameResource.Type type, String name, URI source) {
        if (resources == null)
            resources = new java.util.ArrayList<FrameResource>();
        FrameResource resource = new FrameResource(type, name, source);
        resources.add(resource);
        return resource;
    }

    private Element renderBlock(Element parent, BlockArea a, Document d) {
        Element e = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        WritingMode wm = a.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        Direction ipdDirection = wm.getDirection(Dimension.IPD);
        double shear = a.getShearAngle();
        if (Math.abs(shear) > 0.0001) {
            if (vertical) {
                double skewY = Math.abs(Math.tan(Math.toRadians(shear)));
                if (bpdDirection == Direction.LR) {
                    if (shear < 0) {
                        yCurrent += (skewY * a.getBPD());
                    }
                } else if (bpdDirection == Direction.RL) {
                    if (shear < 0) {
                        yCurrent += (skewY * a.getBPD());
                    }
                    shear *= -1;
                }
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateSkewYFormatter.format(new Double[] {xCurrent, yCurrent, shear}));
            } else {
                double skewX = Math.abs(Math.tan(Math.toRadians(shear)));
                if (ipdDirection == Direction.LR) {
                    if (shear > 0) {
                        xCurrent += (skewX * a.getBPD());
                    }
                    shear *= -1;
                } else if (ipdDirection == Direction.RL) {
                    if (shear < 0) {
                        xCurrent += (skewX * a.getBPD());
                    }
                }
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateSkewXFormatter.format(new Double[] {xCurrent, yCurrent, shear}));
            }
        } else {
            if ((xCurrent != 0) || (yCurrent != 0))
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Double[] {xCurrent, yCurrent}));
        }
        xCurrent = 0;
        yCurrent = 0;
        // render block presentation traits
        Extent extent = (a instanceof BoundedBlockArea) ? ((BoundedBlockArea) a).getBorderExtent() : null;
        if (extent == null)
            extent = new Extent(a.getIPD(), a.getBPD());
        Color bColor = a.getBackgroundColor();
        if ((bColor != null) && !bColor.isTransparent()) {
            Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, bColor.toRGBString());
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
            e.appendChild(eBackgroundColor);
        }
        Image bImage = a.getBackgroundImage();
        if ((bImage != null) && !bImage.isNone()) {
            FrameResource resource = addResource(bImage);
            Element eBackgroundImage = Documents.createElement(d, SVGDocumentFrame.svgImageEltName);
            Documents.setAttribute(eBackgroundImage, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {extent.getWidth()}));
            Documents.setAttribute(eBackgroundImage, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {extent.getHeight()}));
            Documents.setAttribute(eBackgroundImage, SVGDocumentFrame.preserveAspectRatioAttrName, "xMinYMin slice");
            Documents.setAttribute(eBackgroundImage, SVGDocumentFrame.xlinkHrefAttrName, resource.getName());
            e.appendChild(eBackgroundImage);
        }
        // render children
        Element eBlockGroup = renderChildren(e, a, d);
        // update current position
        if (a instanceof Inline) {
            double ipd = a.getIPD();
            if (a.isVertical())
                yCurrent = ySaved + ipd;
            else
                xCurrent = xSaved + ipd;
        }
        double bpd = a.getBPD();
        if (a.isVertical()) {
            xCurrent = xSaved + bpd * ((bpdDirection == RL) ? -1 : 1);
        } else
            yCurrent = ySaved + bpd;
        // update decoration indices
        if (Documents.isElement(a.getElement(), ttParagraphElementName)) {
            ++paragraphGenerationIndex;
            lineGenerationIndex = 0;
        }
        if (Documents.getAttribute(eBlockGroup, SVGDocumentFrame.classAttrName, null) == null)
            maybeMarkClasses(eBlockGroup, a, "block");
        return eBlockGroup;
    }

    private Element renderImage(Element parent, BlockImageArea a, Document d) {
        Element e = parent;
        if (a.isVisible()) {
            Image image = a.getImage();
            if ((image != null) && !image.isNone()) {
                FrameResource resource = addResource(image);
                Element eImage = Documents.createElement(d, SVGDocumentFrame.svgImageEltName);
                Documents.setAttribute(eImage, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {image.getWidth()}));
                Documents.setAttribute(eImage, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {image.getHeight()}));
                Documents.setAttribute(eImage, SVGDocumentFrame.preserveAspectRatioAttrName, "xMinYMin slice");
                Documents.setAttribute(eImage, SVGDocumentFrame.xlinkHrefAttrName, resource.getName());
                double opacity = a.getOpacity();
                if (opacity < 1) {
                    Documents.setAttribute(eImage, SVGDocumentFrame.opacityAttrName, doubleFormatter.format(new Object[] {opacity}));
                }
                e.appendChild(eImage);
            }
        }
        double bpd = a.getBPD();
        if (a.isVertical()) {
            if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                bpd = -bpd;
            xCurrent += bpd;
        } else
            yCurrent += bpd;
        return e;
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
        maybeMarkClasses(e, a, "annotation");
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
        maybeMarkClasses(e, a, "line");
        double xSaved = xCurrent;
        double ySaved = yCurrent;
        WritingMode wm = a.getWritingMode();
        boolean vertical = wm.isVertical();
        Direction bpdDirection = wm.getDirection(Dimension.BPD);
        Direction ipdDirection = wm.getDirection(Dimension.IPD);
        double shear = a.getShearAngle();
        if (Math.abs(shear) > 0.0001) {
            if (vertical) {
                double skewY = Math.abs(Math.tan(Math.toRadians(shear)));
                if (bpdDirection == Direction.LR) {
                    if (shear < 0) {
                        yCurrent += (skewY * a.getBPD());
                    }
                } else if (bpdDirection == Direction.RL) {
                    if (shear < 0) {
                        yCurrent += (skewY * a.getBPD());
                    }
                    shear *= -1;
                }
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateSkewYFormatter.format(new Double[] {xCurrent, yCurrent, shear}));
            } else {
                double skewX = Math.abs(Math.tan(Math.toRadians(shear)));
                if (ipdDirection == Direction.LR) {
                    if (shear > 0) {
                        xCurrent += (skewX * a.getBPD());
                    }
                    shear *= -1;
                } else if (ipdDirection == Direction.RL) {
                    if (shear < 0) {
                        xCurrent += (skewX * a.getBPD());
                    }
                }
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateSkewXFormatter.format(new Double[] {xCurrent, yCurrent, shear}));
            }
        } else {
            if ((xCurrent != 0) || (yCurrent != 0))
                Documents.setAttribute(e, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Double[] {xCurrent, yCurrent}));
        }
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
        maybeMarkClasses(g, a, "glyphs");
        LineArea l = a.getLine();
        double bpdLine = l.getBPD();
        double bpdLineAnnotationBefore = l.getAnnotationBPD(AnnotationPosition.BEFORE);
        double bpdLineSansAnnotationBefore = bpdLine - bpdLineAnnotationBefore;
        double bpdLineAnnotationAfter = l.getAnnotationBPD(AnnotationPosition.AFTER);
        double bpdLineSansAnnotation = bpdLine - (bpdLineAnnotationBefore + bpdLineAnnotationAfter);
        double bpdGlyphs = a.getBPD();
        double baselineOffset;
        double ipdGlyphs = a.getIPD();
        Font font = a.getFont();
        boolean combined = a.isCombined();
        if (a.isVertical()) {
            double yOffset = 0;
            boolean rotate = a.isRotatedOrientation();
            baselineOffset = bpdLineAnnotationBefore;
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
            baselineOffset = font.getHeight() + bpdLineAnnotationBefore;
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, baselineOffset}));
        }
        List<Decoration> decorations = a.getDecorations();
        Element e;
        maybeStyleGlyphGroup(g, a, l);
        e = renderGlyphText(g, a, d, decorations, bpdGlyphs, baselineOffset);
        assert e != null;
        g.appendChild(e);
        if (a.isVertical()) {
            yCurrent += combined ? bpdGlyphs : ipdGlyphs;
        } else {
            xCurrent += ipdGlyphs;
        }
        return g;
    }

    private void maybeStyleGlyphGroup(Element e, GlyphArea g, LineArea l) {
        Font gFont = g.getFont();
        Font lFont = l.getFont();
        String fontFamily = gFont.getPreferredFamilyName();
        if (!fontFamily.equals(lFont.getPreferredFamilyName()))
            Documents.setAttribute(e, SVGDocumentFrame.fontFamilyAttrName, fontFamily);
        Extent fontSize = gFont.getSize();
        if (!fontSize.equals(lFont.getSize()))
            Documents.setAttribute(e, SVGDocumentFrame.fontSizeAttrName, doubleFormatter.format(new Object[] {fontSize.getHeight()}));
        FontStyle fontStyle = gFont.getStyle();
        if (!fontStyle.equals(lFont.getStyle()))
            Documents.setAttribute(e, SVGDocumentFrame.fontStyleAttrName, fontStyle.name().toLowerCase());
        FontWeight fontWeight = gFont.getWeight();
        if (!fontWeight.equals(lFont.getWeight()))
            Documents.setAttribute(e, SVGDocumentFrame.fontWeightAttrName, fontWeight.name().toLowerCase());
    }

    private Element renderGlyphText(Element parent, GlyphArea a, Document d, List<Decoration> decorations, double bpdGlyphs, double baselineOffset) {
        if (a.isVertical() && !a.isRotatedOrientation() && !a.isCombined())
            return renderGlyphTextVertical(parent, a, d, decorations, bpdGlyphs, baselineOffset);
        else
            return renderGlyphTextHorizontal(parent, a, d, decorations, bpdGlyphs, baselineOffset);
    }

    private Element renderGlyphTextVertical(Element parent, GlyphArea a, Document d, List<Decoration> decorations, double bpdGlyphs, double baselineOffset) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(gOuter, a, "text-v");
        Documents.setAttribute(gOuter, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {-font.getWidth()/2,font.getAscent()}));
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        boolean combine = a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.VERTICAL, rotate);
        GlyphMapping gm = a.getGlyphMapping();
        String text = gm.getGlyphsAsText();
        double[] advances = font.getScaledAdvances(gm);
        boolean btt = false;
        double ySaved = yCurrent;
        yCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, combine);
        if (shearAdvance < 0)
            yCurrent += -shearAdvance;
        boolean areaVisible = a.isVisible();
        for (int i = 0, n = advances.length; i < n; ++i) {
            double ga = advances[i];
            double x = 0 /* [TBD] FIXME (bpdGlyphs - ga) / 2 */;
            double y = btt ? yCurrent - ga : yCurrent;
            int j = i + 1;
            String tGlyphs = text.substring(i, j);
            String tGlyphsPath;
            if (font.containsPUAMapping(tGlyphs))
                tGlyphsPath = font.getGlyphsPath(tGlyphs, Axis.VERTICAL, Arrays.copyOfRange(advances, i, j));
            else
                tGlyphsPath = null;
            // inline visibility
            boolean glyphVisible;
            Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, i, j);
            if (decorationVisibility != null)
                glyphVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
            else
                glyphVisible = areaVisible;
            // background color if required
            if (a.isVisible()) {
                Decoration decorationBackgroundColor = findDecoration(decorations, Decoration.Type.BACKGROUND_COLOR, i, j);
                if ((decorationBackgroundColor != null) && glyphVisible) {
                    BackgroundColor backgroundColor = decorationBackgroundColor.getBackgroundColor();
                    Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                    if (x != 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (baselineOffset > 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {-baselineOffset}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {ga + bgFuzz}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {bpdGlyphs}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
                    gOuter.appendChild(eBackgroundColor);
                }
            }
            // outline if required
            Element tOutline;
            Decoration decorationOutline = findDecoration(decorations, Decoration.Type.OUTLINE, i, j);
            if ((decorationOutline != null) && glyphVisible) {
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
            if (glyphVisible) {
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
            } else
                t = null;
            // group wrapper (gInner) if font transform required or using glyphs path
            if ((fontMatrix != null) || ((tGlyphsPath != null) && (y != 0))) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                maybeMarkClasses(gInner, a, "text-v-inner");
                if (y != 0)
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {0,y}));
                if (tOutline != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(tOutline);
                }
                if (t != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(t);
                }
                gOuter.appendChild(gInner);
            } else {
                if (tOutline != null) {
                    if (x != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(tOutline);
                }
                if (t != null) {
                    if (x != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(t);
                }
            }
            yCurrent += btt ? -ga : ga;
        }
        yCurrent = ySaved;
        return gOuter;
    }

    private Element renderGlyphTextHorizontal(Element parent, GlyphArea a, Document d, List<Decoration> decorations, double bpdGlyphs, double baselineOffset) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(gOuter, a, "text-h");
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        boolean combine = a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.HORIZONTAL, rotate);
        GlyphMapping gm = a.getGlyphMapping();
        String text = gm.getGlyphsAsText();
        double[] advances = font.getScaledAdvances(gm);
        double[][] adjustments = font.getScaledAdjustments(gm);
        boolean rtl = false;
        double xSaved = xCurrent;
        xCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, combine);
        if ((shearAdvance < 0) && rotate)
            xCurrent += -shearAdvance;
        boolean areaVisible = a.isVisible();
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
            // inline visibility
            boolean glyphVisible;
            Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, i, j);
            if (decorationVisibility != null)
                glyphVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
            else
                glyphVisible = areaVisible;
            // background color if required
            if (a.isVisible()) {
                Decoration decorationBackgroundColor = findDecoration(decorations, Decoration.Type.BACKGROUND_COLOR, i, j);
                if ((decorationBackgroundColor != null) && glyphVisible) {
                    BackgroundColor backgroundColor = decorationBackgroundColor.getBackgroundColor();
                    Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                    if (x != 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (baselineOffset > 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {-baselineOffset}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {ga + bgFuzz}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {bpdGlyphs}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
                    gOuter.appendChild(eBackgroundColor);
                }
            }
            // outline if required
            Element tOutline;
            Decoration decorationOutline = findDecoration(decorations, Decoration.Type.OUTLINE, i, j);
            if ((decorationOutline != null) && glyphVisible) {
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
            if (glyphVisible) {
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
            } else
                t = null;
            // group wrapper (gInner) if font transform required or using glyphs path
            if ((fontMatrix != null) || (tGlyphsPath != null) && ((x != 0) || (y != 0))) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                maybeMarkClasses(gInner, a, "text-h-inner");
                if ((x != 0) || (y != 0))
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {x,y}));
                if (tOutline != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(tOutline);
                }
                if (t != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(t);
                }
                gOuter.appendChild(gInner);
            } else {
                if (tOutline != null) {
                    if (x != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(tOutline, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(tOutline);
                }
                if (t != null) {
                    if (x != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(t);
                }
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
        Element g = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(g, a, "space");
        LineArea l = a.getLine();
        double bpdLine = l.getBPD();
        double bpdLineAnnotationBefore = l.getAnnotationBPD(AnnotationPosition.BEFORE);
        double bpdLineSansAnnotationBefore = bpdLine - bpdLineAnnotationBefore;
        double bpdLineAnnotationAfter = l.getAnnotationBPD(AnnotationPosition.AFTER);
        double bpdLineSansAnnotation = bpdLine - (bpdLineAnnotationBefore + bpdLineAnnotationAfter);
        double bpdSpace = a.getBPD();
        double baselineOffset;
        double ipdSpace = a.getIPD();
        Font font = a.getFont();
        boolean combined = /*a.isCombined()*/ false;
        if (a.isVertical()) {
            double yOffset = 0;
            boolean rotate = /*a.isRotatedOrientation()*/ false;
            baselineOffset = bpdLineAnnotationBefore;
            if (rotate && !combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotation - font.getHeight())/2 + font.getAscent();
                else
                    baselineOffset += (bpdLineSansAnnotation - bpdSpace)/2 + font.getLeading()/2;
            } else if (combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotationBefore + ipdSpace)/2;
                else
                    baselineOffset += (bpdLineSansAnnotationBefore - ipdSpace)/2;
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
            baselineOffset = font.getHeight() + bpdLineAnnotationBefore;
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, baselineOffset}));
        }
        // inline visibility
        boolean areaVisible = a.isVisible();
        boolean spaceVisible;
        List<Decoration> decorations = a.getDecorations();
        Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, 0, 1);
        if (decorationVisibility != null)
            spaceVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
        else
            spaceVisible = areaVisible;
        // background color if required
        Decoration decorationBackgroundColor = findDecoration(decorations, Decoration.Type.BACKGROUND_COLOR, 0, 1);
        if ((decorationBackgroundColor != null) && spaceVisible) {
            BackgroundColor backgroundColor = decorationBackgroundColor.getBackgroundColor();
            Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
            if (baselineOffset > 0)
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {-baselineOffset}));
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {ipdSpace + bgFuzz}));
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {bpdSpace}));
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
            Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
            g.appendChild(eBackgroundColor);
        }
        // update point
        if (a.isVertical())
            yCurrent += ipdSpace;
        else
            xCurrent += ipdSpace;
        return g.hasChildNodes() ? g : null;
    }

    private Element renderImage(Element parent, InlineImageArea a, Document d) {
        Element g = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(g, a, "image");
        LineArea l = a.getLine();
        double bpdLine = l.getBPD();
        double bpdLineAnnotationBefore = l.getAnnotationBPD(AnnotationPosition.BEFORE);
        double bpdLineSansAnnotationBefore = bpdLine - bpdLineAnnotationBefore;
        double bpdLineAnnotationAfter = l.getAnnotationBPD(AnnotationPosition.AFTER);
        double bpdLineSansAnnotation = bpdLine - (bpdLineAnnotationBefore + bpdLineAnnotationAfter);
        double bpdImage = a.getBPD();
        double baselineOffset;
        double ipdImage = a.getIPD();
        Font font = a.getFont();
        boolean combined = a.isCombined();
        if (a.isVertical()) {
            double yOffset = 0;
            boolean rotate = a.isRotatedOrientation();
            baselineOffset = bpdLineAnnotationBefore;
            if (rotate && !combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotation - font.getHeight())/2 + font.getAscent();
                else
                    baselineOffset += (bpdLineSansAnnotation - bpdImage)/2 + font.getLeading()/2;
            } else if (combined) {
                if (a.getWritingMode().getDirection(Dimension.BPD) == RL)
                    baselineOffset += (bpdLineSansAnnotationBefore + ipdImage)/2;
                else
                    baselineOffset += (bpdLineSansAnnotationBefore - ipdImage)/2;
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
            baselineOffset = font.getHeight() + bpdLineAnnotationBefore;
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, baselineOffset}));
        }
        List<Decoration> decorations = a.getDecorations();
        Element e;
        maybeStyleImageGroup(g, a, l);
        e = renderImageText(g, a, d, decorations, bpdImage, baselineOffset);
        assert e != null;
        g.appendChild(e);
        if (a.isVertical()) {
            yCurrent += combined ? bpdImage : ipdImage;
        } else {
            xCurrent += ipdImage;
        }
        return g;
    }

    private Element renderImageText(Element parent, InlineImageArea a, Document d, List<Decoration> decorations, double bpdImage, double baselineOffset) {
        if (a.isVertical() && !a.isRotatedOrientation() && !a.isCombined())
            return renderImageTextVertical(parent, a, d, decorations, bpdImage, baselineOffset);
        else
            return renderImageTextHorizontal(parent, a, d, decorations, bpdImage, baselineOffset);
    }

    private Element renderImageTextVertical(Element parent, InlineImageArea a, Document d, List<Decoration> decorations, double bpdImage, double baselineOffset) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(gOuter, a, "image-v");
        Documents.setAttribute(gOuter, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {-font.getWidth()/2,font.getAscent()}));
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        boolean combine = a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.VERTICAL, rotate);
        Image[] images = new Image[] { a.getImage() };
        double[] advances = new double[] { a.getIPD() };
        boolean btt = false;
        double ySaved = yCurrent;
        yCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, combine);
        if (shearAdvance < 0)
            yCurrent += -shearAdvance;
        boolean areaVisible = a.isVisible();
        for (int i = 0, n = advances.length; i < n; ++i) {
            Image tImage = images[i];
            double ia = advances[i];
            double x = 0 /* [TBD] FIXME (bpdImage - ia) / 2 */;
            double y = btt ? yCurrent - ia : yCurrent;
            int j = i + 1;
            // inline visibility
            boolean imageVisible;
            Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, i, j);
            if (decorationVisibility != null)
                imageVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
            else
                imageVisible = areaVisible;
            // text image
            Element t;
            if (imageVisible) {
                Double tOpacity;
                Double lOpacity = a.getLine().getOpacity();
                Decoration decorationOpacity = findDecoration(decorations, Decoration.Type.OPACITY, i, j);
                if (decorationOpacity != null)
                    tOpacity = decorationOpacity.getOpacity();
                else
                    tOpacity = lOpacity;
                FrameResource resource = addResource(tImage);
                t = Documents.createElement(d, SVGDocumentFrame.svgTextEltName);
                Documents.setAttribute(t, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {tImage.getWidth()}));
                Documents.setAttribute(t, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {tImage.getHeight()}));
                Documents.setAttribute(t, SVGDocumentFrame.preserveAspectRatioAttrName, "xMinYMin slice");
                Documents.setAttribute(t, SVGDocumentFrame.xlinkHrefAttrName, resource.getName());
                if (!tOpacity.equals(lOpacity) && (tOpacity < 1))
                    Documents.setAttribute(t, SVGDocumentFrame.opacityAttrName, tOpacity.toString());
            } else
                t = null;
            // group wrapper (gInner) if font transform required or using glyphs path
            if (fontMatrix != null) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                maybeMarkClasses(gInner, a, "image-v-inner");
                if (y != 0)
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {0,y}));
                if (t != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(t);
                }
                gOuter.appendChild(gInner);
            } else {
                if (t != null) {
                    if (x != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(t);
                }
            }
            yCurrent += btt ? -ia : ia;
        }
        yCurrent = ySaved;
        return gOuter;
    }

    private Element renderImageTextHorizontal(Element parent, InlineImageArea a, Document d, List<Decoration> decorations, double bpdImage, double baselineOffset) {
        Font font = a.getFont();
        Element gOuter = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
        maybeMarkClasses(gOuter, a, "image-h");
        boolean rotate = a.isRotatedOrientation() && !a.isCombined();
        boolean combine = a.isCombined();
        TransformMatrix fontMatrix = font.getTransform(Axis.HORIZONTAL, rotate);
        Image[] images = new Image[] { a.getImage() };
        double[] advances = new double[] { a.getIPD() };
        boolean rtl = false;
        double xSaved = xCurrent;
        xCurrent = 0;
        double shearAdvance = font.getShearAdvance(rotate, combine);
        if ((shearAdvance < 0) && rotate)
            xCurrent += -shearAdvance;
        boolean areaVisible = a.isVisible();
        for (int i = 0, n = advances.length; i < n; ++i) {
            Image tImage = images[i];
            double ia = advances[i];
            double x = rtl ? xCurrent - ia : xCurrent;
            double y = - font.getHeight() + getCrossShearAdjustment(a);
            int j = i + 1;
            // inline visibility
            boolean imageVisible;
            Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, i, j);
            if (decorationVisibility != null)
                imageVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
            else
                imageVisible = areaVisible;
            // text image
            Element t;
            if (imageVisible) {
                Double tOpacity;
                Double lOpacity = a.getLine().getOpacity();
                Decoration decorationOpacity = findDecoration(decorations, Decoration.Type.OPACITY, i, j);
                if (decorationOpacity != null)
                    tOpacity = decorationOpacity.getOpacity();
                else
                    tOpacity = lOpacity;
                FrameResource resource = addResource(tImage);
                t = Documents.createElement(d, SVGDocumentFrame.svgImageEltName);
                Documents.setAttribute(t, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {tImage.getWidth()}));
                Documents.setAttribute(t, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {tImage.getHeight()}));
                Documents.setAttribute(t, SVGDocumentFrame.preserveAspectRatioAttrName, "xMinYMin slice");
                Documents.setAttribute(t, SVGDocumentFrame.xlinkHrefAttrName, resource.getName());
                if (!tOpacity.equals(lOpacity) && (tOpacity < 1))
                    Documents.setAttribute(t, SVGDocumentFrame.opacityAttrName, tOpacity.toString());
            } else
                t = null;
            // background color if required
            if (a.isVisible()) {
                Decoration decorationBackgroundColor = findDecoration(decorations, Decoration.Type.BACKGROUND_COLOR, i, j);
                if ((decorationBackgroundColor != null) && imageVisible) {
                    BackgroundColor backgroundColor = decorationBackgroundColor.getBackgroundColor();
                    Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                    if (x != 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (baselineOffset > 0)
                        Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {-baselineOffset}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {ia + bgFuzz}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {bpdImage}));
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
                    Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
                    gOuter.appendChild(eBackgroundColor);
                }
            }
            // group wrapper (gInner) if font transform required
            if (fontMatrix != null) {
                Element gInner = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
                maybeMarkClasses(gInner, a, "image-h-inner");
                if ((x != 0) || (y != 0))
                    Documents.setAttribute(gInner, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {x,y}));
                if (t != null) {
                    if (fontMatrix != null)
                        Documents.setAttribute(t, SVGDocumentFrame.transformAttrName, matrixFormatter.format(new Object[] {fontMatrix.toString()}));
                    gInner.appendChild(t);
                }
                gOuter.appendChild(gInner);
            } else {
                if (t != null) {
                    if (x != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.xAttrName, doubleFormatter.format(new Object[] {x}));
                    if (y != 0)
                        Documents.setAttribute(t, SVGDocumentFrame.yAttrName, doubleFormatter.format(new Object[] {y}));
                    gOuter.appendChild(t);
                }
            }
            xCurrent += rtl ? -ia : ia;
        }
        xCurrent = xSaved;
        return gOuter;
    }

    private double getCrossShearAdjustment(InlineImageArea a) {
        if (a.isCombined()) {
            AreaNode aPrev = a.getPreviousSibling();
            if ((aPrev != null) && (aPrev instanceof InlineImageArea)) {
                InlineImageArea aPrevImages = (InlineImageArea) aPrev;
                if (!aPrevImages.isCombined())
                    return - aPrevImages.getFont().getShearAdvance(false, true)/2;
            }
        }
        return 0;
    }

    private void maybeStyleImageGroup(Element e, InlineImageArea g, LineArea l) {
    }

    private Element renderFiller(Element parent, InlineFillerArea a, Document d) {
        double ipd = a.getIPD();
        Element g;
        if (a instanceof InlinePaddingArea) {
            double bpd = a.getBPD();
            g = Documents.createElement(d, SVGDocumentFrame.svgGroupEltName);
            maybeMarkClasses(g, a, "padding");
            Documents.setAttribute(g, SVGDocumentFrame.transformAttrName, translateFormatter.format(new Object[] {xCurrent, 0}));
            // inline visibility
            boolean areaVisible = a.isVisible();
            boolean paddingVisible;
            List<Decoration> decorations = ((InlinePaddingArea) a).getDecorations();
            Decoration decorationVisibility = findDecoration(decorations, Decoration.Type.VISIBILITY, 0, 1);
            if (decorationVisibility != null)
                paddingVisible = (decorationVisibility.getVisibility() == Visibility.VISIBLE);
            else
                paddingVisible = areaVisible;
            // background color if required
            Decoration decorationBackgroundColor = findDecoration(decorations, Decoration.Type.BACKGROUND_COLOR, 0, 1);
            if ((decorationBackgroundColor != null) && paddingVisible) {
                BackgroundColor backgroundColor = decorationBackgroundColor.getBackgroundColor();
                Element eBackgroundColor = Documents.createElement(d, SVGDocumentFrame.svgRectEltName);
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.widthAttrName, doubleFormatter.format(new Object[] {ipd + bgFuzz}));
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.heightAttrName, doubleFormatter.format(new Object[] {bpd}));
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.fillAttrName, backgroundColor.toRGBString());
                Documents.setAttribute(eBackgroundColor, SVGDocumentFrame.strokeAttrName, "none");
                g.appendChild(eBackgroundColor);
            }
        } else
            g = null;
        // udpate point
        if (a.isVertical())
            yCurrent += ipd;
        else
            xCurrent += ipd;
        return ((g != null) && g.hasChildNodes()) ? g : null;
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
        else if (a instanceof InlineImageArea)
            return renderImage(parent, (InlineImageArea) a, d);
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
        else if (a instanceof BlockImageArea)
            return renderImage(parent, (BlockImageArea) a, d);
        else if (a instanceof BlockArea)
            return renderBlock(parent, (BlockArea) a, d);
        else
            throw new IllegalArgumentException();
    }

}
