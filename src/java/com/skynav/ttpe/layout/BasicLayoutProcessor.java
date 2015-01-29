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
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.text.LineBreaker;
import com.skynav.ttpe.text.LineBreakIterator;
import com.skynav.ttpe.text.Paragraph;
import com.skynav.ttpe.text.ParagraphCollector;
import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttv.model.ttml.TTML.Constants.*;

public class BasicLayoutProcessor extends LayoutProcessor {

    public static final String PROCESSOR_NAME                   = "basic";
    public static final LayoutProcessor PROCESSOR               = new BasicLayoutProcessor();

    public static QName isdSequenceElementName                  = new QName(NAMESPACE_TT_ISD, "sequence");
    public static QName isdInstanceElementName                  = new QName(NAMESPACE_TT_ISD, "isd");
    public static QName isdComputedStyleSetElementName          = new QName(NAMESPACE_TT_ISD, "css");
    public static QName isdRegionElementName                    = new QName(NAMESPACE_TT_ISD, "region");

    public static QName ttBodyElementName                       = new QName(NAMESPACE_TT, "body");
    public static QName ttDivisionElementName                   = new QName(NAMESPACE_TT, "div");
    public static QName ttParagraphElementName                  = new QName(NAMESPACE_TT, "p");
    public static QName ttSpanElementName                       = new QName(NAMESPACE_TT, "span");
    public static QName ttBreakElementName                      = new QName(NAMESPACE_TT, "br");

    public static final String defaultLineBreakerName           = "uax14";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "line-breaker",               "NAME",     "specify line breaker name (default: " + defaultLineBreakerName + ")" },
        { "font",                       "FILE",     "specify font configuration file" },
        { "font-directory",             "DIRECTORY","specify path to directory where font configuration files are located" },
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

    // derived state
    private FontCache fontCache;
    private LineBreakIterator breakIterator;

    protected BasicLayoutProcessor() {
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
    public int parseLongOption(String args[], int index) {
            String option = args[index];
            assert option.length() > 2;
            option = option.substring(2);
            if (option.equals("font")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                if (fontSpecificationFileNames == null)
                    fontSpecificationFileNames = new java.util.ArrayList<String>();
                fontSpecificationFileNames.add(args[++index]);
            } else if (option.equals("font-directory")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                fontSpecificationDirectoryPath = args[++index];
            } else if (option.equals("line-breaker")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                lineBreakerName = args[++index];
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
        this.fontCache = new FontCache(fontSpecificationDirectory, fontSpecificationFiles);
        if (lineBreakerName == null)
            lineBreakerName = defaultLineBreakerName;
        LineBreaker lb = LineBreaker.getInstance(lineBreakerName);
        this.breakIterator = (lb != null) ? lb.getIterator() : null;
    }

    @Override
    public List<Area> layout(Document d) {
        if (d != null) {
            Element root = d.getDocumentElement();
            if (root != null) {
                LayoutState ls = makeLayoutState();
                if (isElement(root, isdSequenceElementName))
                    return layoutISDSequence(root, ls);
                else if (isElement(root, isdInstanceElementName))
                    return layoutISDInstance(root, ls);
            }
        }
        return new java.util.ArrayList<Area>();
    }

    protected LayoutState makeLayoutState() {
        return initializeLayoutState(createLayoutState());
    }

    protected LayoutState createLayoutState() {
        return new BasicLayoutState();
    }

    protected LayoutState initializeLayoutState(LayoutState ls) {
        return ls.initialize(fontCache, breakIterator);
    }

    protected List<Area> layoutISDSequence(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, isdInstanceElementName)) {
                areas.addAll(layoutISDInstance(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutISDInstance(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        ls.pushBlock(e, 0, 0, 1280, 720);
        for (Element c : getChildElements(e)) {
            if (isElement(c, isdRegionElementName)) {
                ls.pushBlock(c, 540, 630, 200, 60);
                areas.addAll(layoutRegion(c, ls));
                ls.pop();
            }
        }
        ls.pop();
        return areas;
    }

    protected List<Area> layoutRegion(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttBodyElementName)) {
                ls.pushBlock(c);
                areas.addAll(layoutBody(c, ls));
                ls.pop();
            }
        }
        return areas;
    }

    protected List<Area> layoutBody(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName)) {
                ls.pushBlock(c);
                areas.addAll(layoutDivision(c, ls));
                ls.pop();
            }
        }
        return areas;
    }

    protected List<Area> layoutDivision(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName)) {
                ls.pushBlock(c);
                areas.addAll(layoutDivision(c, ls));
                ls.pop();
            } else if (isElement(c, ttParagraphElementName)) {
                areas.addAll(layoutParagraph(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutParagraph(Element e, LayoutState ls) {
        return layoutParagraphs(new ParagraphCollector().collect(e), ls);
    }

    protected List<Area> layoutParagraphs(List<Paragraph> paragraphs, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Paragraph p : paragraphs) {
            areas.addAll(layoutParagraph(p, ls));
        }
        return areas;
    }

    protected List<Area> layoutParagraph(Paragraph p, LayoutState ls) {
        return new ParagraphLayout(p, ls).layout();
    }

    protected List<Element> getChildElements(Element e) {
        return Documents.getChildElements(e);
    }

    protected boolean isElement(Element e, QName qn) {
        return Documents.isElement(e, qn);
    }

}