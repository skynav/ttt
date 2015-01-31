/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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
 
package com.skynav.xml.helpers;

import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Documents {

    private Documents() {
    }

    public static void assignIdAttributes(Document document, List<QName> idAttrs) {
        assignIdAttributes(document.getDocumentElement(), idAttrs);
    }

    public static void assignIdAttributes(Element elt, List<QName> idAttrs) {
        for (QName idAttr : idAttrs) {
            String ns = idAttr.getNamespaceURI();
            String ln = idAttr.getLocalPart();
            if (ns.length() == 0)
                ns = null;
            Attr a = elt.getAttributeNodeNS(ns, ln);
            if (a != null) {
                elt.setIdAttributeNode(a, true);
                break;
            }
        }
        for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                assignIdAttributes((Element) n, idAttrs);
            }
        }
    }

    public static List<Element> findElementsByName(Document d, QName qn) {
        String ns = qn.getNamespaceURI();
        NodeList nodes;
        if ((ns == null) || (ns.length() == 0))
            nodes = d.getElementsByTagName(qn.getLocalPart());
        else
            nodes = d.getElementsByTagNameNS(ns, qn.getLocalPart());
        List<Element> elts = new java.util.ArrayList<Element>();
        if (nodes != null) {
            for (int i = 0, n = nodes.getLength(); i < n; ++i) {
                elts.add((Element) nodes.item(i));
            }
        }
        return elts;
    }

    public static List<Element> findElementsByName(Element e, QName qn) {
        String ns = qn.getNamespaceURI();
        NodeList nodes;
        if ((ns == null) || (ns.length() == 0))
            nodes = e.getElementsByTagName(qn.getLocalPart());
        else
            nodes = e.getElementsByTagNameNS(ns, qn.getLocalPart());
        List<Element> elts = new java.util.ArrayList<Element>();
        if (nodes != null) {
            for (int i = 0, n = nodes.getLength(); i < n; ++i) {
                elts.add((Element) nodes.item(i));
            }
        }
        return elts;
    }

    public static Element findElementByName(Document d, QName qn) {
        List<Element> elts = findElementsByName(d, qn);
        if (!elts.isEmpty())
            return elts.get(0);
        else
            return null;
    }

    public static List<Element> getChildElements(Element e) {
        List<Element> elts = new java.util.ArrayList<Element>();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Element) {
                elts.add((Element) n);
            }
        }
        return elts;
    }

    public static boolean isElement(Element e, QName qn) {
        String nsElt    = e.getNamespaceURI();
        String lnElt    = e.getLocalName();
        String ns       = qn.getNamespaceURI();
        String ln       = qn.getLocalPart();
        if ((ns == null) || (ns.length() == 0))
            return lnElt.equals(ln);
        else if (nsElt != null)
            return nsElt.equals(ns) && lnElt.equals(ln);
        else
            return false;
    }

    public static Element createElement(Document d, QName qn) {
        String ns       = qn.getNamespaceURI();
        String ln       = qn.getLocalPart();
        if ((ns == null) || (ns.length() == 0))
            return d.createElement(ln);
        else
            return d.createElementNS(ns, ln);
    }

    public static void addChildren(Element e, List<Element> children) {
        for (Element c : children) {
            e.appendChild(c);
        }
    }

    public static void setAttribute(Element e, QName qn, String value) {
        String ns       = qn.getNamespaceURI();
        String ln       = qn.getLocalPart();
        if ((ns == null) || (ns.length() == 0))
            e.setAttribute(ln, value);
        else
             e.setAttributeNS(ns, ln, value);
    }

}
