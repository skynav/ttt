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
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.GlyphArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.RenderProcessor;
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
    };
    private static final Map<String,OptionSpecification> longOptions;
    static {
        longOptions = new java.util.TreeMap<String,OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            longOptions.put(spec[0], new OptionSpecification(spec[0], spec[1], spec[2]));
        }
    }

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
    public List<Frame> render(List<Area> areas, TransformerContext context) {
        List<Frame> frames = new java.util.ArrayList<Frame>();
        for (Area a : areas) {
            Frame f = renderRoot(a, context);
            if (f != null)
                frames.add(f);
        }
        return frames;
    }

    private Frame renderRoot(Area a, TransformerContext context) {
        Reporter reporter = context.getReporter();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            Element e = Documents.createElement(d, XMLDocumentFrame.ttpeCanvasEltName);
            e.setAttributeNS(XML.xmlnsNamespace, "xmlns:ttpe", Namespace.NAMESPACE);
            e.appendChild(renderArea(a, d, context));
            d.appendChild(e);
            Namespaces.normalize(d, XMLDocumentFrame.prefixes);
            return new XMLDocumentFrame(d);
        } catch (Exception e) {
            reporter.logError(e);
        }
        return null;
    }

    private Element renderArea(Area a, Document d, TransformerContext context) {
        if (a instanceof GlyphArea)
            return renderGlyph((GlyphArea) a, d, context);
        else if (a instanceof LineArea)
            return renderLine((LineArea) a, d, context);
        else if (a instanceof BlockArea)
            return renderBlock((BlockArea) a, d, context);
        else
            throw new IllegalArgumentException();
    }

    private Element renderGlyph(GlyphArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeGlyphsEltName);
        renderCommonAreaAttributes(a, e, context);
        Documents.setAttribute(e, XMLDocumentFrame.textAttrName, a.getText());
        return e;
    }

    private Element renderLine(LineArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeLineEltName);
        renderCommonAreaAttributes(a, e, context);
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

    private Element renderBlock(BlockArea a, Document d, TransformerContext context) {
        Element e = Documents.createElement(d, XMLDocumentFrame.ttpeBlockEltName);
        renderCommonAreaAttributes(a, e, context);
        for (Area c : a.getChildren()) {
            e.appendChild(renderArea(c, d, context));
        }
        return e;
    }

   private static final String doubleFormat = "{0,number,#.####}";
   private Element renderCommonAreaAttributes(Area a, Element e, TransformerContext context) {
        Documents.setAttribute(e, XMLDocumentFrame.ipdAttrName, MessageFormat.format(doubleFormat, a.getIPD()));
        Documents.setAttribute(e, XMLDocumentFrame.bpdAttrName, MessageFormat.format(doubleFormat, a.getBPD()));
        return e;
    }

}