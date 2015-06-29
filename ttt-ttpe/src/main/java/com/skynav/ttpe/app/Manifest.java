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

package com.skynav.ttpe.app;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.app.Namespace;
import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.FrameImage;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.TextTransformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public class Manifest {

    private static final String NAME                            = "manifest.xml";
    private static final String NAMESPACE                       = Namespace.NAMESPACE + "#manifest";
    private static final int VERSION                            = 1;

    // element name constants
    public static final QName framesEltName                     = new QName(NAMESPACE, "frames");
    public static final QName frameEltName                      = new QName(NAMESPACE, "frame");
    public static final QName imageEltName                      = new QName(NAMESPACE, "image");

    // attribute name constants
    public static final QName beginAttrName                     = new QName("", "b");
    public static final QName endAttrName                       = new QName("", "e");
    public static final QName extentAttrName                    = new QName("", "x");
    public static final QName formatAttrName                    = new QName("", "format");
    public static final QName originAttrName                    = new QName("", "o");
    public static final QName sourceAttrName                    = new QName("", "s");
    public static final QName versionAttrName                   = new QName("", "version");

    // namespace prefixes
    static final Map<String, String> prefixes;
    static {
        prefixes = new java.util.HashMap<String,String>();
        prefixes.put(XML.xmlNamespace, "xml");
        prefixes.put(XML.xmlnsNamespace, "xmlns");
        prefixes.put(NAMESPACE, "");
    }

    // miscellaneous statics
    public static final MessageFormat timeFormatter             = new MessageFormat("{0,number,00}:{1,number,00}:{2,number,00.###}");

    public Manifest() {
    }

    public String getName() {
        return NAME;
    }

    public void write(OutputStream os, List<Frame> frames, String format, Charset encoding, boolean indent, TransformerContext context) throws IOException {
        Reporter reporter = context.getReporter();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.appendChild(addFrames(d, frames, format));
            Namespaces.normalize(d, prefixes);
            write(os, d, encoding, indent, context);
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        }
    }

    private Element addFrames(Document d, List<Frame> frames, String format) {
        Element e = Documents.createElement(d, framesEltName);
        Documents.setAttribute(e, versionAttrName, Integer.toString(VERSION));
        Documents.setAttribute(e, formatAttrName, format);
        for (Frame f : frames) {
            e.appendChild(addFrame(d, f));
        }
        return e;
    }

    private Element addFrame(Document d, Frame f) {
        Element e = Documents.createElement(d, frameEltName);
        Documents.setAttribute(e, beginAttrName, formatTime(f.getBegin()));
        Documents.setAttribute(e, endAttrName, formatTime(f.getEnd()));
        Documents.setAttribute(e, extentAttrName, f.getExtent().toStringAsPixels());
        if (f.hasImages()) {
            for (FrameImage i : f.getImages())
                e.appendChild(addImage(d, i));
        } else {
            Documents.setAttribute(e, sourceAttrName, f.getFile().getName());
        }
        return e;
    }

    private Element addImage(Document d, FrameImage i) {
        Element e = Documents.createElement(d, imageEltName);
        Documents.setAttribute(e, sourceAttrName, i.getFile().getName());
        Documents.setAttribute(e, extentAttrName, i.getExtent().toStringAsPixels());
        Documents.setAttribute(e, originAttrName, i.getOrigin().toStringAsPixels());
        return e;
    }

    private String formatTime(double time) {
        int hh = (int) (time / 3600);
        int mm = (int) ((time - (hh * 3600)) / 60);
        double ss = time - ((hh * 60) + mm) * 60;
        return timeFormatter.format(new Object[] {hh, mm, ss});
    }

    public void write(OutputStream os, Document d, Charset encoding, boolean indent, TransformerContext context) throws IOException {
        Reporter reporter = context.getReporter();
        ByteArrayOutputStream bas = null;
        BufferedOutputStream bos = null;
        BufferedWriter bw = null;
        try {
            DOMSource source = new DOMSource(d);
            bas = new ByteArrayOutputStream();
            bos = new BufferedOutputStream(bas);
            bw = new BufferedWriter(new OutputStreamWriter(bos, encoding));
            StreamResult result = new StreamResult(bw);
            Transformer t = new TextTransformer(encoding.name(), indent, prefixes, null, null);
            t.transform(source, result);
            bw.close();
            byte[] bytes = bas.toByteArray();
            os.write(bytes, 0, bytes.length);
        } catch (TransformerException e) {
            reporter.logError(e);
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) {}
            }
        }
    }

}
