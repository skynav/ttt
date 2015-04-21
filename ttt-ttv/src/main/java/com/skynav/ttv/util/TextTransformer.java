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

import java.io.IOException;
import java.io.Writer;
import java.util.Comparator;
import java.util.Set;
import java.util.Map;
import java.util.Properties;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public class TextTransformer extends Transformer {

    private String encoding;
    private boolean indent;
    private Map<String,String> prefixes;
    private Set<QName> startTagIndentExclusions;
    private Set<QName> endTagIndentExclusions;
    private int otherNamespaceSequence;

    public TextTransformer(String encoding, boolean indent, Map<String, String> prefixes, Set<QName> startTagIndentExclusions, Set<QName> endTagIndentExclusions) {
        this.encoding = encoding;
        this.indent = indent;
        this.prefixes = prefixes;
        this.startTagIndentExclusions = startTagIndentExclusions;
        this.endTagIndentExclusions = endTagIndentExclusions;
    }

    public ErrorListener getErrorListener() {
        return null;
    }

    public void setErrorListener(ErrorListener listener) {
    }

    public Properties getOutputProperties() {
        return null;
    }

    public void setOutputProperties(Properties properties) {
    }

    public String getOutputProperty(String name) {
        return null;
    }

    public void setOutputProperty(String name, String value) {
    }

    public Object getParameter(String name) {
        return null;
    }

    public void setParameter(String name, Object value) {
    }

    public void clearParameters() {
    }

    public URIResolver getURIResolver() {
        return null;
    }

    public void setURIResolver(URIResolver resolver) {
    }

    public void transform(Source source, Result result) throws TransformerException {
        if (!(source instanceof DOMSource) || !(result instanceof StreamResult)) {
            throw new IllegalArgumentException();
        }
        Node node = ((DOMSource) source).getNode();
        if (!(node instanceof Document)) {
            throw new IllegalArgumentException();
        }
        try {
            serialize(((StreamResult) result).getWriter(), (Document) node);
        } catch (IOException e) {
            throw new TransformerException(e);
        }
    }

    private void serialize(Writer w, Document d) throws IOException {
        w.write("<?xml version=\"1.0\"");
        if (encoding != null) {
            w.write(" encoding=\"");
            w.write(encoding.toLowerCase());
            w.write('"');
        }
        w.write("?>");
        if (indent)
            w.write('\n');
        Element e = d.getDocumentElement();
        if (e != null)
            serialize(w, e);
    }

    private void serialize(Writer w, Element e) throws IOException {
        String ns = e.getNamespaceURI();
        String ln = e.getLocalName();
        if (ln == null)
            ln = e.getNodeName();
        QName qn = new QName(ns, ln);
        w.write('<');
        serialize(w, qn);
        NamedNodeMap attrs = e.getAttributes();
        Set<Attr> attrsOrdered = new java.util.TreeSet<Attr>(new Comparator<Attr>() {
            public int compare(Attr a1, Attr a2) {
                String n1 = a1.getNodeName();
                String n2 = a2.getNodeName();
                if (n1.equals(n2))
                    return 0;
                else if (n1.equals("xml:id"))
                    return -1;
                else if (n2.equals("xml:id"))
                    return 1;
                else {
                    String p1 = a1.getPrefix();
                    String p2 = a2.getPrefix();
                    if (p1 == null) {
                        if (p2 == null) {
                            return n1.compareTo(n2);
                        } else
                            return -1;
                    } else if (p2 == null) {
                        return 1;
                    } else {
                        return n1.compareTo(n2);
                    }
                }
            }
        });
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr)
                attrsOrdered.add((Attr) node);
        }
        for (Attr a : attrsOrdered) {
            w.write(' ');
            serialize(w, a);
        }
        NodeList children = e.getChildNodes();
        int numChildren = children.getLength();
        if (numChildren == 0) {
            w.write("/>");
        } else {
            w.write('>');
            if (indent) {
                if ((startTagIndentExclusions == null) || !startTagIndentExclusions.contains(qn))
                    w.write('\n');
            }
            for (int i = 0, n = numChildren; i < n; ++i) {
                Node node = children.item(i);
                if (node instanceof Element) {
                    serialize(w, (Element) node);
                } else if (node instanceof Text) {
                    serialize(w, (Text) node);
                }
            }
            w.write("</");
            serialize(w, qn);
            w.write('>');
        }
        if (indent) {
            if ((endTagIndentExclusions == null) || !endTagIndentExclusions.contains(qn))
                w.write('\n');
        }
    }

    private void serialize(Writer w, Attr a) throws IOException {
        String ns = a.getNamespaceURI();
        String ln = a.getLocalName();
        if (ln == null)
            ln = a.getName();
        QName qn = new QName(ns, ln);
        serialize(w, qn);
        w.write('=');
        w.write('"');
        serialize(w, a.getValue());
        w.write('"');
    }

    private void serialize(Writer w, Text t) throws IOException {
        serialize(w, t.getWholeText());
    }

    private void serialize(Writer w, String s) throws IOException {
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (c == '<')
                w.write("&lt;");
            else if (c == '>')
                w.write("&gt;");
            else if (c == '"')
                w.write("&quot;");
            else if (c == '&')
                w.write("&amp;");
            else
                w.write(c);
        }
    }

    private void serialize(Writer w, QName qn) throws IOException {
        String ns = qn.getNamespaceURI();
        String ln = qn.getLocalPart();
        if ((ns != null) && (ns.length() > 0)) {
            String prefix = prefixes.get(ns);
            if (prefix == null) {
                prefix = "ns" + ++otherNamespaceSequence;
                prefixes.put(ns, prefix);
            }
            if (prefix.length() > 0) {
                w.write(prefix);
                w.write(':');
            }
        }
        w.write(ln);
    }

}
