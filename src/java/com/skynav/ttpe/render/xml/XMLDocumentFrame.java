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

import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.skynav.ttpe.app.Namespace;
import com.skynav.ttpe.render.DocumentFrame;
import com.skynav.xml.helpers.XML;

public class XMLDocumentFrame implements DocumentFrame {

    // element name constants
    public static final QName ttpeBlockEltName                 = new QName(Namespace.NAMESPACE, "block");
    public static final QName ttpeCanvasEltName                = new QName(Namespace.NAMESPACE, "canvas");
    public static final QName ttpeGlyphsEltName                = new QName(Namespace.NAMESPACE, "glyphs");
    public static final QName ttpeLineEltName                  = new QName(Namespace.NAMESPACE, "line");

    // attribute name constants
    public static final QName bpdAttrName                      = new QName("", "bpd");
    public static final QName ipdAttrName                      = new QName("", "ipd");
    public static final QName textAttrName                     = new QName("", "text");

    private Document document;

    public XMLDocumentFrame(Document d) {
        this.document = d;
    }

    public Document getDocument() {
        return document;
    }

    public static Map<String, String> prefixes;
    static {
        prefixes = new java.util.HashMap<String,String>();
        prefixes.put(XML.xmlNamespace, "xml");
        prefixes.put(XML.xmlnsNamespace, "xmlns");
        prefixes.put(Namespace.NAMESPACE, "");
    }
    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    private static Set<QName> startTagIndentExclusions;
    static {
        startTagIndentExclusions = new java.util.HashSet<QName>();
        startTagIndentExclusions.add(ttpeGlyphsEltName);
    }

    public Set<QName> getStartExclusions() {
        return startTagIndentExclusions;
    }

    public Set<QName> getEndExclusions() {
        return null;
    }

}