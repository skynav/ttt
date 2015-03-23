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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.skynav.ttpe.app.Namespace;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.render.AbstractDocumentFrame;
import com.skynav.xml.helpers.XML;

public class XMLDocumentFrame extends AbstractDocumentFrame {

    private static final String NAMESPACE                       = Namespace.NAMESPACE + "#areas";

    // element name constants
    public static final QName ttpeAnnotationEltName             = new QName(NAMESPACE, "annotation");
    public static final QName ttpeBlockEltName                  = new QName(NAMESPACE, "block");
    public static final QName ttpeCanvasEltName                 = new QName(NAMESPACE, "canvas");
    public static final QName ttpeFillEltName                   = new QName(NAMESPACE, "fill");
    public static final QName ttpeGlyphsEltName                 = new QName(NAMESPACE, "glyphs");
    public static final QName ttpeLineEltName                   = new QName(NAMESPACE, "line");
    public static final QName ttpeReferenceEltName              = new QName(NAMESPACE, "reference");
    public static final QName ttpeSpaceEltName                  = new QName(NAMESPACE, "space");
    public static final QName ttpeViewportEltName               = new QName(NAMESPACE, "viewport");

    // attribute name constants
    public static final QName inlineAlignAttrName               = new QName("", "ia");
    public static final QName blockAlignAttrName                = new QName("", "ba");
    public static final QName bpdAttrName                       = new QName("", "bpd");
    public static final QName clipAttrName                      = new QName("", "clip");
    public static final QName ctmAttrName                       = new QName("", "ctm");
    public static final QName extentAttrName                    = new QName("", "extent");
    public static final QName fromAttrName                      = new QName("", "from");
    public static final QName ipdAttrName                       = new QName("", "ipd");
    public static final QName originAttrName                    = new QName("", "origin");
    public static final QName overflowAttrName                  = new QName("", "overflow");
    public static final QName textAttrName                      = new QName("", "text");
    public static final QName wmAttrName                        = new QName("", "wm");

    // namespace prefixes
    public static Map<String, String> prefixes;
    static {
        prefixes = new java.util.HashMap<String,String>();
        prefixes.put(XML.xmlNamespace, "xml");
        prefixes.put(XML.xmlnsNamespace, "xmlns");
        prefixes.put(NAMESPACE, "");
    }

    // serialization exclusions
    private static Set<QName> startTagIndentExclusions;
    static {
        startTagIndentExclusions = new java.util.HashSet<QName>();
        startTagIndentExclusions.add(ttpeGlyphsEltName);
        startTagIndentExclusions.add(ttpeSpaceEltName);
    }

    private List<Rectangle> regions;

    public XMLDocumentFrame(double begin, double end, Extent extent, Document d, List<Rectangle> regions) {
        super(begin, end, extent, d);
        this.regions = regions;
    }

    public List<Rectangle> getRegions() {
        return regions;
    }

    @Override
    public Map<String, String> getPrefixes() {
        return prefixes;
    }

    @Override
    public Set<QName> getStartExclusions() {
        return startTagIndentExclusions;
    }

    @Override
    public Set<QName> getEndExclusions() {
        return null;
    }

}