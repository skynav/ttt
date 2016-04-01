/*
 * Copyright 2014-16 Skynav, Inc. All rights reserved.
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
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.render.Frame;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.TextTransformer;
import com.skynav.ttx.transformer.TransformerContext;

public abstract class Manifest {

    // miscellaneous statics
    public static final MessageFormat timeFormatter             = new MessageFormat("{0,number,00}:{1,number,00}:{2,number,00.###}", Locale.US);

    protected Manifest() {
    }

    public abstract String getName();

    public abstract Map<String,String> getPrefixes();

    public void write(OutputStream os, List<Frame> frames, String format, Charset encoding, boolean indent, TransformerContext context) throws IOException {
        Reporter reporter = context.getReporter();
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            d.appendChild(addFrames(d, frames, format));
            Namespaces.normalize(d, getPrefixes());
            write(os, d, encoding, indent, context);
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        }
    }    

    protected abstract Element addFrames(Document d, List<Frame> frames, String format);
    
    protected String formatTime(double time) {
        int hh = (int) (time / 3600);
        int mm = (int) ((time - (hh * 3600)) / 60);
        double ss = time - ((hh * 60) + mm) * 60;
        return timeFormatter.format(new Object[] {hh, mm, ss});
    }

    private void write(OutputStream os, Document d, Charset encoding, boolean indent, TransformerContext context) throws IOException {
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
            Transformer t = new TextTransformer(encoding.name(), indent, getPrefixes(), null, null);
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

    public static String getDefaultManifestFormat() {
        return SimpleManifest.getManifestFormat();
    }
    
    public static boolean isManifestFormat(String manifestFormat) {
        if (manifestFormat == null)
            return false;
        else if (manifestFormat.equals(SimpleManifest.getManifestFormat()))
            return true;
        else if (manifestFormat.equals(TTMLManifest.getManifestFormat()))
            return true;
        else
            return false;
    }

    public static Manifest createManifest(String manifestFormat) {
        if ((manifestFormat == null) || manifestFormat.equals(SimpleManifest.getManifestFormat()))
            return new SimpleManifest();
        else if (manifestFormat.equals(TTMLManifest.getManifestFormat()))
            return new TTMLManifest();
        else
            throw new IllegalArgumentException();
    }

}
