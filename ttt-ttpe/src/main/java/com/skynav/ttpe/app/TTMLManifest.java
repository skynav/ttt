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

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.render.Frame;
import com.skynav.ttpe.render.FrameImage;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

import static com.skynav.ttpe.parameter.Constants.*;
import static com.skynav.ttpe.style.Constants.*;
import static com.skynav.ttpe.text.Constants.*;
import static com.skynav.ttv.model.ttml.TTML.Constants.*;

public class TTMLManifest extends Manifest {

    private static final String MANIFEST_FORMAT                 = "ttml";
    private static final String NAME                            = "manifest.xml";

    private static final String TTML_DEFAULT_LANGUAGE           = "";
    private static final String TTML_CONTENT_PROFILE            = "http://skynav.com/ns/ttpe#manifest-ttpi";
    private static final int    TTML_VERSION                    = 2;

    // attribute name constants
    public static final QName   beginAttrName                   = new QName("", "begin");
    public static final QName   endAttrName                     = new QName("", "end");
    public static final QName   sourceAttrName                  = new QName("", "src");

    // namespace prefixes
    static final Map<String, String> prefixes;
    static {
        prefixes = new java.util.HashMap<String,String>();
        prefixes.put(XML.xmlNamespace, "xml");
        prefixes.put(XML.xmlnsNamespace, "xmlns");
        prefixes.put(NAMESPACE_TT, "");
        prefixes.put(NAMESPACE_TT_PARAMETER, "ttp");
        prefixes.put(NAMESPACE_TT_STYLE, "tts");
    }

    public String getName() {
        return NAME;
    }

    public Map<String,String> getPrefixes() {
        return prefixes;
    }

    protected Element addFrames(Document d, List<Frame> frames, String format) {
        Element r = Documents.createElement(d, ttRootElementName);
        Documents.setAttribute(r, xmlLanguageAttrName, TTML_DEFAULT_LANGUAGE);
        Documents.setAttribute(r, ttpContentProfileAttrName, TTML_CONTENT_PROFILE);
        Documents.setAttribute(r, ttpVersionAttrName, Integer.toString(TTML_VERSION));
        Element b = Documents.createElement(d, ttBodyElementName);
        for (Frame f : frames) {
            b.appendChild(addFrame(d, f));
        }
        r.appendChild(b);
        return r;
    }

    private Element addFrame(Document d, Frame f) {
        Element e = Documents.createElement(d, ttDivisionElementName);
        Documents.setAttribute(e, beginAttrName, formatTime(f.getBegin()));
        Documents.setAttribute(e, endAttrName, formatTime(f.getEnd()));
        Documents.setAttribute(e, ttsExtentAttrName, f.getExtent().toStringAsPixels());
        if (f.hasImages()) {
            for (FrameImage i : f.getImages())
                e.appendChild(addImage(d, i));
        } else {
            Element i = Documents.createElement(d, ttImageElementName);
            Documents.setAttribute(i, sourceAttrName, f.getFile().getName());
            e.appendChild(i);
        }
        return e;
    }

    private Element addImage(Document d, FrameImage i) {
        Element e = Documents.createElement(d, ttImageElementName);
        Documents.setAttribute(e, sourceAttrName, i.getFile().getName());
        Documents.setAttribute(e, ttsExtentAttrName, i.getExtent().toStringAsPixels());
        Documents.setAttribute(e, ttsOriginAttrName, i.getOrigin().toStringAsPixels());
        return e;
    }

    public static String getManifestFormat() {
        return MANIFEST_FORMAT;
    }

}
