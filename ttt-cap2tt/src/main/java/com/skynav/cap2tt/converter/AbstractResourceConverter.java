/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import com.skynav.cap2tt.converter.AbstractResourceConverter;
import com.skynav.cap2tt.converter.AttributeSpecification;
import com.skynav.cap2tt.converter.ConverterContext;
import com.skynav.cap2tt.converter.Screen;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.ComparableQName;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.util.TextTransformer;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.Nodes;
import com.skynav.xml.helpers.XML;

import static com.skynav.cap2tt.app.Converter.*;
import static com.skynav.ttv.model.ttml.TTML2.Constants.*;

public abstract class AbstractResourceConverter implements ResourceConverter {

    // converter context state
    protected ConverterContext context;

    // converter options state
    protected String defaultLanguage;
    protected String defaultRegion;
    protected SimpleDateFormat gmtDateTimeFormat;
    protected boolean metadataCreation;
    protected boolean mergeStyles;
    protected Model model;
    protected File outputDirectory;
    protected boolean outputDisabled;
    protected Charset outputEncoding;
    protected File outputFile;
    protected boolean outputIndent;
    protected MessageFormat outputPatternFormatter;
    protected boolean retainDocument;
    protected MessageFormat styleIdPatternFormatter;
    protected int styleIdSequenceStart;

    // miscellaneous state
    protected int[] indices;
    protected int outputFileSequence;

    public enum GenerationIndex {
        styleSetIndex;
    };

    public AbstractResourceConverter(ConverterContext context) {
        // initialize global state context
        this.context = context;
        // initialize converter options state
        this.defaultLanguage = context.getOption("defaultLanguage");
        this.defaultRegion = context.getOption("defaultRegion");
        this.gmtDateTimeFormat = (SimpleDateFormat) context.getOptionObject("gmtDateTimeFormat");
        this.indices = (int[]) context.getOptionObject("indices");
        this.mergeStyles = context.getOptionBoolean("mergeStyles");
        this.metadataCreation = context.getOptionBoolean("metadataCreation");
        this.model = (Model) context.getOptionObject("model");
        this.outputDirectory = (File) context.getOptionObject("outputDirectory");
        this.outputDisabled = context.getOptionBoolean("outputDisabled");
        this.outputEncoding = (Charset) context.getOptionObject("outputEncoding");
        this.outputFile = (File) context.getOptionObject("outputFile");
        this.outputIndent = context.getOptionBoolean("outputIndent");
        this.outputPatternFormatter = (MessageFormat) context.getOptionObject("outputPatternFormatter");
        this.retainDocument = context.getOptionBoolean("retainDocument");
        this.styleIdPatternFormatter = (MessageFormat) context.getOptionObject("styleIdPatternFormatter");
        this.styleIdSequenceStart = context.getOptionInteger("styleIdSequenceStart");
        // miscellaneous state
        this.indices = new int[GenerationIndex.values().length];
    }

    public boolean convert(List<Screen> screens) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message("i.014", "Converting resource using {0} model ...", context.getModel().getName()));
        return true;
    }

    public String getOption(String name) {
        return context.getOption(name);
    }
    
    public Object getOptionObject(String name) {
        return context.getOptionObject(name);
    }
    
    public Map<String, AttributeSpecification> getKnownAttributes() {
        return context.getKnownAttributes();
    }

    public boolean useIMSCConstraints() {
        return context.useIMSCConstraints();
    }

    // concrete subclass support

    protected boolean convertResource(JAXBElement<?> tt) {
        boolean fail = false;
        Reporter reporter = context.getReporter();
        try {
            JAXBContext jc = JAXBContext.newInstance(model.getJAXBContextPath());
            Marshaller m = jc.createMarshaller();
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document d = db.newDocument();
            m.marshal(tt, d);
            mergeConfiguration(d);
            elideInitials(d, getInitials(d, model));
            if (mergeStyles)
                mergeStyles(d, indices);
            if (metadataCreation)
                addCreationMetadata(d);
            Map<String, String> prefixes = model.getNormalizedPrefixes();
            Namespaces.normalize(d, prefixes);
            if (!outputDisabled && !writeDocument(d, prefixes))
                fail = true;
            if (outputDisabled || retainDocument)
                context.setOptionObject("outputDocument", d);
        } catch (ParserConfigurationException e) {
            reporter.logError(e);
        } catch (JAXBException e) {
            reporter.logError(e);
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    // implementation support

    private void mergeConfiguration(Document d) {
        List<Attr> parameters = new java.util.ArrayList<Attr>(context.getConfigurationParameters());
        if (!parameters.isEmpty()) {
            Element e = Documents.findElementByName(d, ttTimedTextEltName);
            if (e != null) {
                for (Attr attr : parameters) {
                    String ns = attr.getNamespaceURI();
                    String qn = attr.getName();
                    String v = attr.getValue();
                    e.setAttributeNS(ns, qn, v);
                }
            }
        }
        List<Element> initials = new java.util.ArrayList<Element>(context.getConfigurationInitials());
        Collections.reverse(initials);
        if (!initials.isEmpty()) {
            Element e = Documents.findElementByName(d, ttStylingEltName);
            if (e != null) {
                for (Element initial : initials) {
                    e.insertBefore(d.importNode(initial, true), e.getFirstChild());
                }
            }
        }
        List<Element> regions = Documents.findElementsByName(d, ttRegionEltName);
        for (Element e : regions) {
            Element eConfig = context.getConfigurationRegion(e.getAttributeNS(XML.xmlNamespace, "id"));
            if (eConfig != null)
                mergeStyles(e, eConfig);
        }
    }

    private void mergeStyles(Element dst, Element src) {
        NamedNodeMap attrs = src.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                if (isMergedStyle(a))
                    dst.setAttributeNS(a.getNamespaceURI(), a.getLocalName(), a.getValue());
            }
        }
    }

    protected boolean isMergedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        return (ns != null) && ns.equals(NAMESPACE_TT_STYLE);
    }

    private void addCreationMetadata(Document d) {
        Element e;
        if ((e = Documents.findElementByName(d, ttHeadEltName)) != null) {
            Node m, f;
            m = createMetadataItemElement(d, "creationSystem", creationSystem);
            assert m != null;
            f = e.getFirstChild();
            e.insertBefore(m, f);
            m = createMetadataItemElement(d, "creationDate", getXSDDateString(new Date()));
            assert m != null;
            f = e.getFirstChild();
            e.insertBefore(m, f);
        }
    }

    private Element createMetadataItemElement(Document d, String name, String value) {
        QName qn = ttmItemEltName;
        Element e = d.createElementNS(qn.getNamespaceURI(), qn.getLocalPart());
        e.setAttribute("name", name);
        e.appendChild(d.createTextNode(value));
        return e;
    }

    private String getXSDDateString(Date date) {
        return gmtDateTimeFormat.format(date);
    }

    private void mergeStyles(final Document d, int[] indices) {
        try {
            final Map<Element,StyleSet> styleSets = getUniqueSpecifiedStyles(d, indices, styleIdPatternFormatter, styleIdSequenceStart);
            final Set<String> styleIds = new java.util.HashSet<String>();
            final Element styling = Documents.findElementByName(d, ttStylingEltName);
            if (styling != null) {
                Traverse.traverseElements(d, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element e = (Element) content;
                        if (styleSets.containsKey(e)) {
                            StyleSet ss = styleSets.get(e);
                            String id = ss.getId();
                            if (!id.isEmpty()) {
                                e.setAttribute("style", id);
                                if (!styleIds.contains(id)) {
                                    Element ttStyle = d.createElementNS(NAMESPACE_TT, "style");
                                    generateAttributes(ss, ttStyle);
                                    styling.appendChild(ttStyle);
                                    styleIds.add(id);
                                }
                            }
                            pruneStyles(e);
                        }
                        return true;
                    }
                });
            }
        } catch (Exception e) {
            context.getReporter().logError(e);
        }
    }

    private static void generateAttributes(StyleSet ss, Element e) {
        String id = ss.getId();
        if ((id != null) && !id.isEmpty()) {
            for (StyleSpecification s : ss.getStyles().values()) {
                ComparableQName n = s.getName();
                e.setAttributeNS(n.getNamespaceURI(), n.getLocalPart(), s.getValue());
            }
            e.setAttributeNS(XML.xmlNamespace, "id", id);
        }
    }

    private Map<QName,String> getInitials(Document d, Model model) {
        Map<QName,String> initials = new java.util.HashMap<QName,String>();
        // get initials from model
        for (QName qn : model.getDefinedStyleNames()) {
            initials.put(qn, model.getInitialStyleValue(null, qn));
        }
        // get initials from document
        for (Element e : Documents.findElementsByName(d, ttInitialEltName)) {
            NamedNodeMap attrs = e.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    if (isInitialStyle(a))
                        initials.put(new QName(a.getNamespaceURI(), a.getLocalName()), a.getValue());
                }
            }
        }
        return initials;
    }

    protected boolean isInitialStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        return (ns != null) && ns.equals(NAMESPACE_TT_STYLE);
    }

    private void elideInitials(Document d, final Map<QName,String> initials) {
        try {
            Traverse.traverseElements(d, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element e = (Element) content;
                    if (isRegionOrContentElement(e))
                        elideInitials(e, initials);
                    return true;
                }
            });
        } catch (Exception e) {
            context.getReporter().logError(e);
        }
    }

    private void elideInitials(Element e, final Map<QName,String> initials) {
        List<Attr> elide = new java.util.ArrayList<Attr>();
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                if (isInitialStyle(a)) {
                    String initial = initials.get(new QName(a.getNamespaceURI(), a.getLocalName()));
                    if ((initial != null) && a.getValue().equals(initial)) {
                        if (!isSpan(e) || !isTextAlign(a))
                            elide.add(a);
                    }
                }
            }
        }
        for (Attr a: elide) {
            e.removeAttributeNode(a);
        }
    }

    private static boolean isSpan(Element e) {
        return Nodes.matchesName(e, ttSpanEltName);
    }

    private static boolean isTextAlign(Attr a) {
        return Nodes.matchesName(a, ttsTextAlignAttrName);
    }

    private Map<Element, StyleSet> getUniqueSpecifiedStyles(Document d, int[] indices, MessageFormat styleIdPatternFormatter, int styleIdStart) {
        // get specified style sets
        Map<Element, StyleSet> specifiedStyleSets = getSpecifiedStyles(d, indices);

        // obtain ordered set of SSs, ordered by SS generation
        Set<StyleSet> orderedStyles = new java.util.TreeSet<StyleSet>(StyleSet.getGenerationComparator());
        orderedStyles.addAll(specifiedStyleSets.values());

        // obtain unique set of SSs
        Set<StyleSet> uniqueStyles = new java.util.TreeSet<StyleSet>(StyleSet.getValuesComparator());
        uniqueStyles.addAll(orderedStyles);

        // final reorder by generation
        orderedStyles.clear();
        orderedStyles.addAll(uniqueStyles);
        List<StyleSet> styles = new java.util.ArrayList<StyleSet>(orderedStyles);

        // assign identifiers to unique SSs
        int uniqueStyleIndex = styleIdStart;
        for (StyleSet ss : styles)
            ss.setId(styleIdPatternFormatter.format(new Object[]{Integer.valueOf(uniqueStyleIndex++)}));

        // remap SS map entries to unique SSs
        for (Map.Entry<Element,StyleSet> e : specifiedStyleSets.entrySet()) {
            StyleSet ss = e.getValue();
            int index = styles.indexOf(ss);
            if (index >= 0) {
                StyleSet ssUnique = styles.get(index);
                if (ss != ssUnique) // N.B. must not use equals() here
                    e.setValue(ssUnique);
            }
        }

        return specifiedStyleSets;
    }

    private Map<Element, StyleSet> getSpecifiedStyles(Document d, int[] indices) {
        Map<Element, StyleSet> specifiedStyleSets = new java.util.HashMap<Element, StyleSet>();
        specifiedStyleSets = getSpecifiedStyles(getContentElements(d), specifiedStyleSets, indices);
        return specifiedStyleSets;
    }

    private static List<Element> getContentElements(Document d) {
        final List<Element> elements = new java.util.ArrayList<Element>();
        try {
            Traverse.traverseElements(d, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element e = (Element) content;
                    if (isContentElement(e))
                        elements.add(e);
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return elements;
    }

    private static boolean isRegionOrContentElement(Element e) {
        return isRegionElement(e) || isContentElement(e);
    }

    private static boolean isRegionElement(Element e) {
        String ns = e.getNamespaceURI();
        if ((ns == null) || !ns.equals(NAMESPACE_TT))
            return false;
        else {
            String localName = e.getLocalName();
            if (localName.equals("region"))
                return true;
            else
                return false;
        }
    }

    private static boolean isContentElement(Element e) {
        String ns = e.getNamespaceURI();
        if ((ns == null) || !ns.equals(NAMESPACE_TT))
            return false;
        else {
            String localName = e.getLocalName();
            if (localName.equals("body"))
                return true;
            else if (localName.equals("div"))
                return true;
            else if (localName.equals("p"))
                return true;
            else if (localName.equals("span"))
                return true;
            else if (localName.equals("br"))
                return true;
            else
                return false;
        }
    }

    private Map<Element, StyleSet> getSpecifiedStyles(List<Element> elements, Map<Element, StyleSet> specifiedStyleSets, int[] indices) {
        for (Element e : elements) {
            assert !specifiedStyleSets.containsKey(e);
            StyleSet ss = getInlineStyles(e, indices);
            if (!ss.isEmpty())
                specifiedStyleSets.put(e, ss);
        }
        return specifiedStyleSets;
    }

    private StyleSet getInlineStyles(Element e, int[] indices) {
        StyleSet styles = new StyleSet(generateStyleSetIndex(indices));
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                String ns = a.getNamespaceURI();
                if (isInlinedStyle(a)) {
                    styles.merge(new StyleSpecification(new ComparableQName(ns, a.getLocalName()), a.getValue()));
                }
            }
        }
        return styles;
    }

    protected boolean isInlinedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        return (ns != null) && ns.equals(NAMESPACE_TT_STYLE);
    }

    private static int generateStyleSetIndex(int[] indices) {
        return indices[GenerationIndex.styleSetIndex.ordinal()]++;
    }

    private void pruneStyles(Element e) {
        List<Attr> prune = new java.util.ArrayList<Attr>();
        NamedNodeMap attrs = e.getAttributes();
        for (int i = 0, n = attrs.getLength(); i < n; ++i) {
            Node node = attrs.item(i);
            if (node instanceof Attr) {
                Attr a = (Attr) node;
                if (isPrunedStyle(a)) {
                    prune.add(a);
                }
            }
        }
        for (Attr a : prune) {
            e.removeAttributeNode(a);
        }
    }

    protected boolean isPrunedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        return (ns != null) && ns.equals(NAMESPACE_TT_STYLE);
    }

    private static Set<QName> startTagIndentExclusions;
    private static Set<QName> endTagIndentExclusions;
    static {
        startTagIndentExclusions = new java.util.HashSet<QName>();
        startTagIndentExclusions.add(ttParagraphEltName);
        startTagIndentExclusions.add(ttSpanEltName);
        startTagIndentExclusions.add(ttBreakEltName);
        startTagIndentExclusions.add(ttmItemEltName);
        endTagIndentExclusions = new java.util.HashSet<QName>();
        endTagIndentExclusions.add(ttSpanEltName);
        endTagIndentExclusions.add(ttBreakEltName);
    }

    private boolean writeDocument(Document d, Map<String, String> prefixes) {
        boolean fail = false;
        Reporter reporter = context.getReporter();
        BufferedWriter bw = null;
        try {
            DOMSource source = new DOMSource(d);
            File[] retOutputFile = new File[1];
            bw = new BufferedWriter(new OutputStreamWriter(getOutputStream(retOutputFile), outputEncoding));
            StreamResult result = new StreamResult(bw);
            Transformer t = new TextTransformer(outputEncoding.name(), outputIndent, prefixes, startTagIndentExclusions, endTagIndentExclusions);
            t.transform(source, result);
            File outputFile = retOutputFile[0];
            reporter.logInfo(reporter.message("i.015", "Wrote TTML ''{0}''.", (outputFile != null) ? outputFile.getAbsolutePath() : uriStandardOutput));
        } catch (IOException e) {
            reporter.logError(e);
        } catch (TransformerException e) {
            reporter.logError(e);
        } finally {
            if (bw != null) {
                try { bw.close(); } catch (IOException e) {}
            }
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    private OutputStream getOutputStream(File[] retOutputFile) throws IOException {
        URI resourceUri = context.getReporter().getResourceURI();
        StringBuffer sb = new StringBuffer();
        if (isFile(resourceUri)) {
            String path = resourceUri.getPath();
            int s = 0;
            int e = path.length();
            int lastPathSeparator = path.lastIndexOf('/');
            if (lastPathSeparator >= 0)
                s = lastPathSeparator + 1;
            int lastExtensionSeparator = path.lastIndexOf('.');
            if (lastExtensionSeparator >= 0)
                e = lastExtensionSeparator;
            sb.append(path.substring(s, e));
            sb.append(".xml");
        } else {
            sb.append(outputPatternFormatter.format(new Object[]{Integer.valueOf(++outputFileSequence)}));
        }
        String outputFileName = sb.toString();
        if (isStandardOutput(outputFileName))
            return System.out;
        else {
            File f = (outputFile != null) ? outputFile : new File(outputDirectory, outputFileName).getCanonicalFile();
            if (retOutputFile != null)
                retOutputFile[0] = f;
            return new FileOutputStream(f);
        }
    }

}

