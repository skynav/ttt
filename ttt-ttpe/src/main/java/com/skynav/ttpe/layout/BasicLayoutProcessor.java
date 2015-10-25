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

import java.io.File;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.BlockFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.StyleCollector;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.LineBreaker;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.text.ParagraphCollector;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.util.Integers;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.ZeroTreatment;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.parameter.Constants.*;
import static com.skynav.ttpe.text.Constants.*;

public class BasicLayoutProcessor extends LayoutProcessor {

    public static final String NAME                             = "basic";

    public static final String defaultLineBreakerName           = "uax14";
    public static final String defaultCharacterBreakerName      = "scalar";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "font",                       "FILE",     "specify font configuration file" },
        { "font-directory",             "DIRECTORY","specify path to directory where font configuration files are located" },
        { "default-background-color",   "COLOR",    "default background color (default: \"" + Defaults.defaultBackgroundColor + "\")" },
        { "default-color",              "COLOR",    "default foreground color (default: " + Defaults.defaultColor + "\")" },
        { "default-font-families",      "FAMILIES", "default font families (default: \"" + Defaults.defaultFontFamilies + "\")" },
        { "default-whitespace",         "SPACE",    "default xml space treatment (\"default\"|\"preserve\"; default: \"" +
            Defaults.defaultWhitespace.toString().toLowerCase() + "\")" },
        { "line-breaker",               "NAME",     "specify line breaker name (default: \"" + defaultLineBreakerName + "\")" },
        { "max-regions",                "COUNT",    "maximum number of regions in canvas (default: no limit)" },
        { "max-lines",                  "COUNT",    "maximum number of lines in canvas (default: no limit)" },
        { "max-lines-per-region",       "COUNT",    "maximum number of lines in a region (default: no limit)" },
        { "max-chars",                  "COUNT",    "maximum number of characters in canvas (default: no limit)" },
        { "max-chars-per-region",       "COUNT",    "maximum number of characters in a region (default: no limit)" },
        { "max-chars-per-line",         "COUNT",    "maximum number of characters in a line (default: no limit)" },
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // options state
    private String fontSpecificationDirectoryPath;
    private List<String> fontSpecificationFileNames;
    private String lineBreakerName;
    private String charBreakerName;
    private String defaultBackgroundColor;
    private String defaultColor;
    private String defaultFontFamilies;
    private String defaultWhitespace;
    private int maxRegions = -1;
    private int maxLines = -1;
    private int maxLinesPerRegion = -1;
    private int maxChars = -1;
    private int maxCharsPerRegion = -1;
    private int maxCharsPerLine = -1;

    // derived state
    private FontCache fontCache;
    private LineBreaker lineBreaker;
    private LineBreaker charBreaker;
    private Defaults defaults;

    protected BasicLayoutProcessor(TransformerContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public Collection<OptionSpecification> getLongOptionSpecs() {
        return longOptions.values();
    }

    @Override
    public int parseLongOption(List<String> args, int index) {
        Reporter reporter = context.getReporter();
        String arg = args.get(index);
        int numArgs = args.size();
        String option = arg;
        assert option.length() > 2;
        option = option.substring(2);
        if (option.equals("default-background-color")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultBackgroundColor = args.get(++index);
        } else if (option.equals("default-color")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultColor = args.get(++index);
        } else if (option.equals("default-font-families")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultFontFamilies = args.get(++index);
        } else if (option.equals("default-whitespace")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            defaultWhitespace = args.get(++index);
        } else if (option.equals("font")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            if (fontSpecificationFileNames == null)
                fontSpecificationFileNames = new java.util.ArrayList<String>();
            fontSpecificationFileNames.add(args.get(++index));
        } else if (option.equals("font-directory")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            fontSpecificationDirectoryPath = args.get(++index);
        } else if (option.equals("line-breaker")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            lineBreakerName = args.get(++index);
        } else if (option.equals("max-regions")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxRegions = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else if (option.equals("max-lines")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxLines = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else if (option.equals("max-lines-per-region")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxLinesPerRegion = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else if (option.equals("max-chars")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxChars = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else if (option.equals("max-chars-per-region")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxCharsPerRegion = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else if (option.equals("max-chars-per-line")) {
            if (index + 1 > numArgs)
                throw new MissingOptionArgumentException("--" + option);
            String count = args.get(++index);
            try {
                maxCharsPerLine = Integer.parseInt(count);
            } catch (NumberFormatException e) {
                throw new InvalidOptionUsageException(option, reporter.message("*KEY*", "bad count syntax: {0}", count));
            }
        } else
            index = index - 1;
        return index + 1;
    }

    @Override
    public void processDerivedOptions() {
        File fontSpecificationDirectory = null;
        if (fontSpecificationDirectoryPath != null) {
            fontSpecificationDirectory = new File(fontSpecificationDirectoryPath);
            if (!fontSpecificationDirectory.exists())
                throw new InvalidOptionUsageException("font-directory", "directory does not exist: " + fontSpecificationDirectoryPath);
            else if (!fontSpecificationDirectory.isDirectory())
                throw new InvalidOptionUsageException("font-directory", "not a directory: " + fontSpecificationDirectoryPath);
        }
        List<File> fontSpecificationFiles = null;
        if ((fontSpecificationFileNames != null) && !fontSpecificationFileNames.isEmpty()) {
            for (String name : fontSpecificationFileNames) {
                File fontSpecificationFile = new File(name);
                if (!fontSpecificationFile.exists())
                    throw new InvalidOptionUsageException("font", "file does not exist: " + name);
                else if (!fontSpecificationFile.isFile())
                    throw new InvalidOptionUsageException("font", "not a file: " + name);
                else {
                    if (fontSpecificationFiles == null)
                        fontSpecificationFiles = new java.util.ArrayList<File>();
                    fontSpecificationFiles.add(fontSpecificationFile);
                }
            }
        }
        Reporter reporter = context.getReporter();
        this.fontCache = new FontCache(fontSpecificationDirectory, fontSpecificationFiles, reporter);
        if (lineBreakerName == null)
            lineBreakerName = defaultLineBreakerName;
        LineBreaker lb = LineBreaker.getInstance(lineBreakerName);
        this.lineBreaker = lb;
        if (charBreakerName == null)
            charBreakerName = defaultCharacterBreakerName;
        LineBreaker cb = LineBreaker.getInstance(charBreakerName);
        this.charBreaker = cb;
        this.defaults = new Defaults();
        if (defaultBackgroundColor != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (com.skynav.ttv.verifier.util.Colors.isColor(defaultBackgroundColor, null, null, retColor))
                defaults.setBackgroundColor(new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha()));
            else
                throw new InvalidOptionUsageException("default-background-color", "invalid color syntax:  " + defaultBackgroundColor);
        }
        if (defaultColor != null) {
            com.skynav.ttv.model.value.Color[] retColor = new com.skynav.ttv.model.value.Color[1];
            if (com.skynav.ttv.verifier.util.Colors.isColor(defaultColor, null, null, retColor))
                defaults.setColor(new Color(retColor[0].getRed(), retColor[0].getGreen(), retColor[0].getBlue(), retColor[0].getAlpha()));
            else
                throw new InvalidOptionUsageException("default-color", "invalid color syntax:  " + defaultColor);
        }
        if (defaultFontFamilies != null) {
            List<com.skynav.ttv.model.value.FontFamily> families = new java.util.ArrayList<com.skynav.ttv.model.value.FontFamily>();
            Object[] treatments = new Object[] { com.skynav.ttv.verifier.util.QuotedGenericFontFamilyTreatment.Allow };
            if (com.skynav.ttv.verifier.util.Fonts.isFontFamilies(defaultFontFamilies, null, context, treatments, families)) {
                List<String> fontFamilies = new java.util.ArrayList<String>(families.size());
                for (com.skynav.ttv.model.value.FontFamily f : families)
                    fontFamilies.add(f.toString());
                defaults.setFontFamilies(fontFamilies);
            } else
                throw new InvalidOptionUsageException("default-font-families", "invalid font families syntax:  " + defaultFontFamilies);
        }
        if (defaultWhitespace != null) {
            try {
                defaults.setWhitespace(Whitespace.valueOf(defaultWhitespace.toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new InvalidOptionUsageException("default-whitespace", "invalid whitespace syntax:  " + defaultWhitespace);
            }
        }
    }

    @Override
    public List<Area> layout(Document d) {
        List<Area> areas = null;
        if (d != null) {
            Element root = d.getDocumentElement();
            if (root != null) {
                LayoutState ls = makeLayoutState();
                if (isElement(root, isdSequenceElementName))
                    areas = layoutISDSequence(root, ls);
                else if (isElement(root, isdInstanceElementName))
                    areas = layoutISDInstance(root, ls);
                warnOnCounterViolations(ls);
            }
        }
        return (areas != null) ? areas : new java.util.ArrayList<Area>();
    }

    @Override
    public void clear(boolean all) {
        if (all) {
            if (fontCache != null)
                fontCache.clear();
            if (lineBreaker != null)
                lineBreaker.clear();
            if (charBreaker != null)
                 charBreaker.clear();
        }
    }


    protected LayoutState makeLayoutState() {
        return initializeLayoutState(createLayoutState());
    }

    protected LayoutState createLayoutState() {
        return new BasicLayoutState(context);
    }

    protected LayoutState initializeLayoutState(LayoutState ls) {
        return ls.initialize(fontCache, getLineBreakIterator(), getCharacterBreakIterator(), getDefaults());
    }

    protected LineBreakIterator getLineBreakIterator() {
        LineBreaker lb = lineBreaker;
        return (lb != null) ? lb.getIterator(context.getReporter()) : null;
    }

    protected LineBreakIterator getCharacterBreakIterator() {
        LineBreaker cb = charBreaker;
        return (cb != null) ? cb.getIterator(context.getReporter()) : null;
    }

    protected Defaults getDefaults() {
        return defaults;
    }

    protected List<Area> layoutISDSequence(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, isdInstanceElementName))
                areas.addAll(layoutISDInstance(c, ls));
        }
        return areas;
    }

    protected List<Area> layoutISDInstance(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        try {
            double begin = Double.parseDouble(e.getAttribute("begin"));
            double end = Double.parseDouble(e.getAttribute("end"));
            Extent cellResolution = parseCellResolution(Documents.getAttribute(e, ttpCellResolutionAttrName, null));
            if (cellResolution == null)
                cellResolution = defaults.getCellResolution();
            ls.pushCanvas(e, begin, end, cellResolution);
            Extent extent = ls.getExternalExtent();
            double w = extent.getWidth();
            double h = extent.getHeight();
            boolean clip = ls.getExternalOverflow().clips();
            ls.pushViewport(e, w, h, clip);
            WritingMode wm = ls.getExternalWritingMode();
            TransformMatrix ctm = ls.getExternalTransform();
            ls.pushReference(e, 0, 0, w, h, wm, ctm);
            for (Element c : getChildElements(e)) {
                if (isElement(c, isdRegionElementName))
                    layoutRegion(c, ls);
                else if (isElement(c, isdComputedStyleSetElementName))
                    ls.saveStyles(c);
            }
            ls.pop();
            ls.pop();
            areas.add(ls.pop());
        } catch (NumberFormatException x) {
        }
        return areas;
    }

    private Extent parseCellResolution(String value) {
        if (value != null) {
            Integer[] minMax = new Integer[] { 2, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Error, ZeroTreatment.Error };
            List<Integer> integers = new java.util.ArrayList<Integer>();
            if (Integers.isIntegers(value, null, context, minMax, treatments, integers))
                return new Extent(integers.get(0), integers.get(1));
        }
        return null;
    }

    protected void layoutRegion(Element e, LayoutState ls) {
        Extent extent = ls.getExtent(e);
        double w = extent.getWidth();
        double h = extent.getHeight();
        Point origin = ls.getPosition(e, extent);
        double x = origin.getX();
        double y = origin.getY();
        boolean clip = ls.getOverflow(e).clips();
        ls.pushViewport(e, w, h, clip);
        WritingMode wm = ls.getWritingMode(e);
        TransformMatrix ctm = ls.getTransform(e);
        ls.pushReference(e, x, y, w, h, wm, ctm);
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttBodyElementName))
                layoutBody(c, ls);
        }
        AreaNode r = ls.peek();
        if (r instanceof ReferenceArea)
            alignBlockAreas((ReferenceArea) r, ls.getReferenceAlignment());
        ls.pop();
        ls.pop();
    }

    protected void layoutBody(Element e, LayoutState ls) {
        ls.pushBlock(e);
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName))
                layoutDivision(c, ls);
        }
        ls.pop();
    }

    protected void layoutDivision(Element e, LayoutState ls) {
        ls.pushBlock(e);
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName)) {
                layoutDivision(c, ls);
            } else if (isElement(c, ttParagraphElementName)) {
                layoutParagraph(c, ls);
            }
        }
        ls.pop();
    }

    protected void layoutParagraph(Element e, LayoutState ls) {
        layoutParagraphs(e, new ParagraphCollector(newStyleCollector(ls)).collect(e), ls);
    }

    private StyleCollector newStyleCollector(LayoutState ls) {
        return new StyleCollector(context, ls.getFontCache(), defaults, ls.getExternalExtent(), ls.getReferenceExtent(), ls.getCellResolution(), ls.getWritingMode(), ls.getLanguage(), ls.getFont(), ls.getStyles());
    }

    protected void layoutParagraphs(Element e, List<Paragraph> paragraphs, LayoutState ls) {
        for (Paragraph p : paragraphs) {
            layoutParagraph(p, ls);
        }
    }

    protected void layoutParagraph(Paragraph p, LayoutState ls) {
        ls.pushBlock(p.getElement());
        for (LineArea l : new ParagraphLayout(p, ls).layout()) {
            ls.addLine(l);
        }
        AreaNode b = ls.peek();
        if (b instanceof BlockArea)
            alignLineAreas((BlockArea) b, ls);
        ls.pop();
    }

    protected static List<Element> getChildElements(Element e) {
        return Documents.getChildElements(e);
    }

    protected static boolean isElement(Element e, QName qn) {
        return Documents.isElement(e, qn);
    }

    private void alignBlockAreas(ReferenceArea r, BlockAlignment alignment) {
        double measure = r.isVertical() ? r.getWidth() : r.getHeight();
        double consumed = 0;
        for (AreaNode c : r.getChildren()) {
            consumed += c.getBPD();
        }
        double available = measure - consumed;
        if (available > 0) {
            if (alignment == BlockAlignment.BEFORE) {
                AreaNode a = new BlockFillerArea(r.getElement(), 0, available);
                r.addChild(a, null);
            } else if (alignment == BlockAlignment.AFTER) {
                AreaNode a = new BlockFillerArea(r.getElement(), 0, available);
                r.insertChild(a, r.firstChild(), null);
            } else if (alignment == BlockAlignment.CENTER) {
                double half = available / 2;
                AreaNode a1 = new BlockFillerArea(r.getElement(), 0, half);
                AreaNode a2 = new BlockFillerArea(r.getElement(), 0, half);
                r.insertChild(a1, r.firstChild(), null);
                r.insertChild(a2, null, null);
            } else {
                // no-op
            }
        } else if (available < 0) {
            r.setOverflow(-available);
        }
    }

    private void alignLineAreas(BlockArea b, LayoutState ls) {
        BlockAlignment alignment = ls.getReferenceAlignment();
        ReferenceArea r = ls.getReferenceArea();
        double measure = r.isVertical() ? r.getWidth() : r.getHeight();
        double consumed = 0;
        int numChildren = 0;
        for (AreaNode c : b.getChildren()) {
            consumed += c.getBPD();
            ++numChildren;
        }
        double available = measure - consumed;
        if (available > 0) {
            if (alignment == BlockAlignment.BEFORE) {
                // no-op
            } else if (alignment == BlockAlignment.AFTER) {
                // no-op
            } else if (alignment == BlockAlignment.CENTER) {
                // no-op
            } else {
                justifyLineAreas(b, measure, consumed, numChildren, alignment);
            }
        } else if (available < 0) {
            b.setOverflow(-available);
        }
    }

    private void justifyLineAreas(BlockArea b, double measure, double consumed, int numChildren, BlockAlignment alignment) {
        double available = measure - consumed;
        if (alignment == BlockAlignment.JUSTIFY)
            alignment = BlockAlignment.SPACE_BETWEEN;
        int numFillers;
        if (alignment == BlockAlignment.SPACE_AROUND) {
            numFillers = numChildren + 1;
        } else if (alignment == BlockAlignment.SPACE_BETWEEN) {
            numFillers = numChildren - 1;
        } else
            numFillers = 0;
        double fill;
        if (numFillers > 0)
            fill = available / numFillers;
        else
            fill = 0;
        if (fill > 0) {
            List<AreaNode> children = new java.util.ArrayList<AreaNode>(b.getChildren());
            for (AreaNode c : children) {
                AreaNode f = new BlockFillerArea(b.getElement(), 0, fill);
                if ((c == children.get(0)) && (alignment == BlockAlignment.SPACE_BETWEEN))
                    continue;
                else
                    b.insertChild(f, c, null);
            }
            if (alignment == BlockAlignment.SPACE_AROUND) {
                AreaNode f = new BlockFillerArea(b.getElement(), 0, fill);
                b.insertChild(f, null, null);
            }
        }
    }

    private void warnOnCounterViolations(LayoutState ls) {
        Reporter reporter = context.getReporter();
        ls.finalizeCounters();
        int regions = ls.getCounter(LayoutState.Counter.REGIONS_IN_CANVAS);
        if ((maxRegions >= 0) && (regions > maxRegions))
            reporter.logWarning(reporter.message("*KEY*", "Regions per canvas limit exceeded, {0} present, must not exceed {1}.", regions, maxRegions));
        int lines = ls.getCounter(LayoutState.Counter.LINES_IN_CANVAS);
        if ((maxLines >= 0) && (lines > maxLines))
            reporter.logWarning(reporter.message("*KEY*", "Lines per canvas limit exceeded, {0} present, must not exceed {1}.", lines, maxLines));
        int linesPerRegion = ls.getCounter(LayoutState.Counter.MAX_LINES_IN_REGION);
        if ((maxLinesPerRegion >= 0) && (linesPerRegion > maxLinesPerRegion))
            reporter.logWarning(reporter.message("*KEY*", "Lines per region limit exceeded, {0} present, must not exceed {1}.", linesPerRegion, maxLinesPerRegion));
        int chars = ls.getCounter(LayoutState.Counter.CHARS_IN_CANVAS);
        if ((maxChars >= 0) && (chars > maxChars))
            reporter.logWarning(reporter.message("*KEY*", "Characters per canvas limit exceeded, {0} present, must not exceed {1}.", chars, maxChars));
        int charsPerRegion = ls.getCounter(LayoutState.Counter.MAX_CHARS_IN_REGION);
        if ((maxCharsPerRegion >= 0) && (charsPerRegion > maxCharsPerRegion))
            reporter.logWarning(reporter.message("*KEY*", "Characters per region limit exceeded, {0} present, must not exceed {1}.", charsPerRegion, maxCharsPerRegion));
        int charsPerLine = ls.getCounter(LayoutState.Counter.MAX_CHARS_IN_LINE);
        if ((maxCharsPerLine >= 0) && (charsPerLine > maxCharsPerLine))
            reporter.logWarning(reporter.message("*KEY*", "Characters per line limit exceeded, {0} present, must not exceed {1}.", charsPerLine, maxCharsPerLine));
    }

}
