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

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.area.Area;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;

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

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
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
    String fontDirectoryPath;
    List<String> fontConfigurationFileNames;

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
                if (fontConfigurationFileNames == null)
                    fontConfigurationFileNames = new java.util.ArrayList<String>();
                fontConfigurationFileNames.add(args[++index]);
            } else if (option.equals("font-directory")) {
                if (index + 1 > args.length)
                    throw new MissingOptionArgumentException("--" + option);
                fontDirectoryPath = args[++index];
            } else
                index = index - 1;
            return index + 1;
    }

    @Override
    public List<Area> layout(Document d) {
        if (d != null) {
            Element root = d.getDocumentElement();
            if (root != null) {
                LayoutState ls = new BasicLayoutState();
                if (isElement(root, isdSequenceElementName))
                    return layoutISDSequence(root, ls);
                else if (isElement(root, isdInstanceElementName))
                    return layoutISDInstance(root, ls);
            }
        }
        return new java.util.ArrayList<Area>();
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
        for (Element c : getChildElements(e)) {
            if (isElement(c, isdRegionElementName)) {
                areas.addAll(layoutRegion(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutRegion(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttBodyElementName)) {
                areas.addAll(layoutRegion(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutBody(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName)) {
                areas.addAll(layoutDivision(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutDivision(Element e, LayoutState ls) {
        List<Area> areas = new java.util.ArrayList<Area>();
        for (Element c : getChildElements(e)) {
            if (isElement(c, ttDivisionElementName)) {
                areas.addAll(layoutDivision(c, ls));
            } else if (isElement(c, ttParagraphElementName)) {
                areas.addAll(layoutParagraph(c, ls));
            }
        }
        return areas;
    }

    protected List<Area> layoutParagraph(Element e, LayoutState ls) {
        // collect delimited annotated text runs
        return new java.util.ArrayList<Area>();
    }

    protected List<Element> getChildElements(Element e) {
        return new java.util.ArrayList<Element>();
    }

    protected boolean isElement(Element e, QName qn) {
        return false;
    }

}