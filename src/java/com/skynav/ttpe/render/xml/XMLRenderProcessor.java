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

package com.skynav.ttpe.render.xml;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.app.Namespace;
import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.AreaNode;
import com.skynav.ttpe.area.CanvasArea;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.InlineFillerArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.SpaceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public class XMLRenderProcessor extends RenderProcessor {

    public static final RenderProcessor PROCESSOR               = new XMLRenderProcessor();

    private static final String PROCESSOR_NAME                  = "xml";

    // option and usage info
    private static final String[][] longOptionSpecifications = new String[][] {
        { "include-isd-generator",      "",         "include ISD generator information in output, i.e., information about source ISD instance" },
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

    // miscellaneous statics
    private static final MessageFormat doubleFormatter          = new MessageFormat("{0,number,#.####}");

    // options state
    private boolean includeISDGenerator;

    public XMLRenderProcessor() {
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
        if (option.equals("include-isd-generator")) {
            includeISDGenerator = true;
        } else {
            return super.parseLongOption(args, index);
        }
        return index + 1;
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
            d.appendChild(renderCanvas(a, d, context));
            Namespaces.normalize(d, XMLDocumentFrame.prefixes);
            return new XMLDocumentFrame(a.getBegin(), a.getEnd(), d);
        } catch (Exception e) {
            reporter.logError(e);
        }
        return null;
    }

    private Element renderCanvas(CanvasArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeCanvasEltName);
        e.setAttributeNS(XML.xmlnsNamespace, "xmlns:ttpe", Namespace.NAMESPACE);
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private Element renderViewport(ViewportArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeViewportEltName);
        renderCommonAreaAttributes(a, e, false, false, context);
        Extent extent = a.getExtent();
        if (extent != null)
            Documents.setAttribute(e, XMLDocumentFrame.extentAttrName, extent.toString());
        if (a.getClip())
            Documents.setAttribute(e, XMLDocumentFrame.clipAttrName, Boolean.valueOf(a.getClip()).toString().toLowerCase());
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private Element renderReference(ReferenceArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeReferenceEltName);
        renderCommonAreaAttributes(a, e, false, false, context);
        Extent extent = a.getExtent();
        if (extent != null)
            Documents.setAttribute(e, XMLDocumentFrame.extentAttrName, extent.toString());
        TransformMatrix ctm = a.getCTM();
        if (ctm != null)
            Documents.setAttribute(e, XMLDocumentFrame.ctmAttrName, ctm.toString());
        if (!isRootReferenceArea(a)) {
            Point origin = a.getOrigin();
            if (origin != null)
                Documents.setAttribute(e, XMLDocumentFrame.originAttrName, origin.toString());
            WritingMode wm = a.getWritingMode();
            if (wm != null)
                Documents.setAttribute(e, XMLDocumentFrame.wmAttrName, wm.toString().toLowerCase());
        }
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private boolean isRootReferenceArea(ReferenceArea a) {
        for (AreaNode p = a.getParent(); p != null; p = p.getParent()) {
            if (p instanceof ReferenceArea)
                return false;
        }
        return true;
    }

    private Element renderBlock(BlockArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeBlockEltName);
        renderCommonBlockAreaAttributes(a, e, context);
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private Element renderLine(LineArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeLineEltName);
        renderCommonBlockAreaAttributes(a, e, context);
        if (isOverflowed(a)) {
            InlineAlignment alignment = a.getAlignment();
            String align;
            if (alignment == InlineAlignment.START)
                align = null;
            else if (alignment == InlineAlignment.END)
                align = "end";
            else
                align = "center";
            if (align != null)
                Documents.setAttribute(e, XMLDocumentFrame.alignAttrName, align);
            Documents.setAttribute(e, XMLDocumentFrame.overflowAttrName, doubleFormatter.format(new Object[] {a.getOverflow()}));
        }
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private boolean isOverflowed(LineArea a) {
        return a.getOverflow() > 0;
    }

    private Element renderGlyph(GlyphArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeGlyphsEltName);
        renderCommonInlineAreaAttributes(a, e, false, context);
        Documents.setAttribute(e, XMLDocumentFrame.textAttrName, a.getText());
        return e;
    }

    private Element renderSpace(SpaceArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeSpaceEltName);
        renderCommonInlineAreaAttributes(a, e, false, context);
        Documents.setAttribute(e, XMLDocumentFrame.textAttrName, escapeWhitespace(a.getText()));
        return e;
    }

    private Element renderFiller(InlineFillerArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeFillEltName);
        renderCommonInlineAreaAttributes(a, e, false, context);
        return e;
    }

    private Element renderArea(Area a, Document d, TransformerContext context) {
        if (a instanceof GlyphArea)
            return renderGlyph((GlyphArea) a, d, context);
        else if (a instanceof SpaceArea)
            return renderSpace((SpaceArea) a, d, context);
        else if (a instanceof InlineFillerArea)
            return renderFiller((InlineFillerArea) a, d, context);
        else if (a instanceof LineArea)
            return renderLine((LineArea) a, d, context);
        else if (a instanceof ReferenceArea)
            return renderReference((ReferenceArea) a, d, context);
        else if (a instanceof ViewportArea)
            return renderViewport((ViewportArea) a, d, context);
        else if (a instanceof BlockArea)
            return renderBlock((BlockArea) a, d, context);
        else
            throw new IllegalArgumentException();
    }

    private Element renderCommonInlineAreaAttributes(Area a, Element e, boolean bpdInclude, TransformerContext context) {
        renderCommonAreaAttributes(a, e, bpdInclude, true, context);
        return e;
    }

    private Element renderCommonBlockAreaAttributes(Area a, Element e, TransformerContext context) {
        renderCommonAreaAttributes(a, e, true, true, context);
        return e;
    }

    private Element renderCommonAreaAttributes(Area a, Element e, boolean bpdInclude, boolean ipdInclude, TransformerContext context) {
        if (bpdInclude)
            Documents.setAttribute(e, XMLDocumentFrame.bpdAttrName, doubleFormatter.format(new Object[] {a.getBPD()}));
        if (ipdInclude)
            Documents.setAttribute(e, XMLDocumentFrame.ipdAttrName, doubleFormatter.format(new Object[] {a.getIPD()}));
        if (includeISDGenerator) {
            String ln = (a.getElement() != null) ? a.getElement().getLocalName() : null;
            if (ln != null)
                Documents.setAttribute(e, XMLDocumentFrame.fromAttrName, ln);
        }
        return e;
    }

    public static String escapeWhitespace(String s) {
        if (s == null)
            return null;
        else {
            StringBuffer sb = new StringBuffer(s.length());
            for (int i = 0, n = s.length(); i < n; ++i) {
                int c = s.codePointAt(i);
                if (Characters.isWhitespace(c))
                    appendNumericCharReference(sb, c);
                else
                    sb.append((char) c);
            }
            return sb.toString();
        }
    }

    private static void appendNumericCharReference(StringBuffer sb, int codepoint) {
        sb.append("\\u");
        sb.append(pad(codepoint, 16, (codepoint > 65535) ? 6 : 4, '0'));
    }

    private static String digits = "0123456789ABCDEF";
    private static String pad(int value, int radix, int width, char padding) {
        assert value >= 0;
        StringBuffer sb = new StringBuffer(width);
        while (value > 0) {
            sb.append(digits.charAt(value % radix));
            value /= radix;
        }
        while (sb.length() < width) {
            sb.append(padding);
        }
        return sb.reverse().toString();
    }

}