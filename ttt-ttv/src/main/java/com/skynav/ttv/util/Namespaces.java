/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.skynav.xml.helpers.XML;

public class Namespaces {

    private Namespaces() {}

    public static void normalize(Document document, final Map<String,String> normalizedPrefixes) {
        try {
            Set<String> namespaces = normalizedPrefixes.keySet();
            final String[] na = namespaces.toArray(new String[namespaces.size()]);  // array of namespaces
            Arrays.sort(na, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return s1.compareTo(s2);
                }
            });
            final int[] ca = new int[na.length];                                    // array of namespace usage counts
            Traverse.traverseElements(document, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    normalize(elt, normalizedPrefixes, na, ca);
                    return true;
                }
            });
            Element tt = document.getDocumentElement();
            for (int i = 0, n = na.length; i < n; ++i) {
                String ns = na[i];
                int c = ca[i];
                if (c > 0) {
                    String prefix = normalizedPrefixes.get(ns);
                    if (prefix.length() > 0) {
                        if (!tt.hasAttributeNS(XML.xmlnsNamespace, prefix)) {
                            if (!XML.isAnyXMLNamespace(ns))
                                tt.setAttributeNS(XML.xmlnsNamespace, "xmlns:" + prefix, ns);
                        }
                    } else {
                        if (!tt.hasAttribute("xmlns")) {
                            tt.setAttribute("xmlns", ns);
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private static void normalize(Element elt, Map<String,String> normalizedPrefixes, String[] na, int[] ca) {
        normalizeNode((Node) elt, normalizedPrefixes, na, ca);
        NamedNodeMap attrs = elt.getAttributes();
        List<Attr> xmlnsFixups = new java.util.ArrayList<Attr>();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                String nsUri = node.getNamespaceURI();
                if ((nsUri != null) && nsUri.equals(XML.xmlnsNamespace))
                    xmlnsFixups.add((Attr) node);
                else
                    normalizeNode(node, normalizedPrefixes, na, ca);
            }
        }
        for (Attr a : xmlnsFixups)
            normalizeDeclaration(a, elt, normalizedPrefixes);
    }

    private static void normalizeDeclaration(Attr attr, Element elt, Map<String,String> normalizedPrefixes) {
        String nsUri = attr.getValue();
        String normalizedPrefix = normalizedPrefixes.get(nsUri);
        if (normalizedPrefix != null) {
            elt.removeAttributeNode(attr);
        }
    }

    private static void normalizeNode(Node node, Map<String,String> normalizedPrefixes, String[] na, int[] ca) {
        String nsUri = node.getNamespaceURI();
        String normalizedPrefix = normalizedPrefixes.get(nsUri);
        if (normalizedPrefix != null) {
            if (normalizedPrefix.length() == 0)
                normalizedPrefix = null;
            node.setPrefix(normalizedPrefix);
        }
        if (nsUri != null) {
            int index = Arrays.binarySearch(na, nsUri, new Comparator<String>() {
                public int compare(String s1, String s2) {
                    return s1.compareTo(s2);
                }
            });
            if (index >= 0) {
                ca[index] += 1;
            }
        }
    }

}
