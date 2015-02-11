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

package com.skynav.ttpe.render.svg;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;

import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.render.AbstractDocumentFrame;
import com.skynav.xml.helpers.XML;

public class SVGDocumentFrame extends AbstractDocumentFrame {

    private static final String NAMESPACE                       = "http://www.w3.org/2000/svg";

    // element name constants
    public static final QName svgSVGEltName                     = new QName(NAMESPACE, "svg");
    public static final QName svgGroupEltName                   = new QName(NAMESPACE, "g");
    public static final QName svgRectEltName                    = new QName(NAMESPACE, "rect");
    public static final QName svgTextEltName                    = new QName(NAMESPACE, "text");

    // attribute name constants
    public static final QName fillAttrName                      = new QName("", "fill");
    public static final QName fontFamilyAttrName                = new QName("", "font-family");
    public static final QName fontSizeAttrName                  = new QName("", "font-size");
    public static final QName fontStyleAttrName                 = new QName("", "font-style");
    public static final QName fontWeightAttrName                = new QName("", "font-weight");
    public static final QName heightAttrName                    = new QName("", "height");
    public static final QName opacityAttrName                   = new QName("", "opacity");
    public static final QName strokeAttrName                    = new QName("", "stroke");
    public static final QName strokeWidthAttrName               = new QName("", "stroke-width");
    public static final QName transformAttrName                 = new QName("", "transform");
    public static final QName viewBoxAttrName                   = new QName("", "viewBox");
    public static final QName widthAttrName                     = new QName("", "width");
    public static final QName wmAttrName                        = new QName("", "writing-mode");
    public static final QName xAttrName                         = new QName("", "x");
    public static final QName yAttrName                         = new QName("", "y");

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
        startTagIndentExclusions.add(svgTextEltName);
    }

    private List<Rectangle> regions;

    public SVGDocumentFrame(double begin, double end, Extent extent, Document d, List<Rectangle> regions) {
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

}