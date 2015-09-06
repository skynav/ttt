/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttx.transformer.isd;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.skynav.ttv.app.InvalidOptionUsageException;
import com.skynav.ttv.app.MissingOptionArgumentException;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttv.app.UnknownOptionException;
import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Annotations;
import com.skynav.ttv.util.ComparableQName;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Namespaces;
import com.skynav.ttv.util.PostVisitor;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;

import com.skynav.ttx.transformer.AbstractTransformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerException;
import com.skynav.ttx.util.DirectedGraph;
import com.skynav.ttx.util.TimeCoordinate;
import com.skynav.ttx.util.TimeInterval;
import com.skynav.ttx.util.TopologicalSort;

import com.skynav.xml.helpers.XML;

public class ISD {

    public static final String TRANSFORMER_NAME                 = "isd";

    private static final String DEFAULT_OUTPUT_ENCODING         = AbstractTransformer.DEFAULT_OUTPUT_ENCODING;
    private static final Charset defaultOutputEncoding;
    private static final String defaultOutputFileNamePattern    = "isdi{0,number,000000}.xml";

    static {
        Charset de;
        try {
            de = Charset.forName(DEFAULT_OUTPUT_ENCODING);
        } catch (RuntimeException e) {
            de = Charset.defaultCharset();
        }
        defaultOutputEncoding = de;
    }

    // option and usage info
    private static final String[][] longOptionSpecifications    = new String[][] {
        { "isd-output-clean",           "",         "clean (remove) all files in output directory prior to writing ISD output" },
        { "isd-output-directory",       "DIRECTORY","specify path to directory where ISD output is to be written" },
        { "isd-output-encoding",        "ENCODING", "specify character encoding of ISD output (default: " + defaultOutputEncoding.name() + ")" },
        { "isd-output-indent",          "",         "indent ISD output (default: no indent)" },
        { "isd-output-pattern",         "PATTERN",  "specify ISD output file name pattern (default: 'isd00000')" },
    };
    private static final Collection<OptionSpecification> longOptions;
    static {
        Set<OptionSpecification> s = new java.util.TreeSet<OptionSpecification>();
        for (String[] spec : longOptionSpecifications) {
            s.add(new OptionSpecification(spec[0], spec[1], spec[2]));
        }
        longOptions = Collections.unmodifiableSet(s);
    }

    public static TTMLHelper getHelper(TransformerContext context) {
        return (TTMLHelper) context.getResourceState(ResourceState.isdHelper.name());
    }

    @SuppressWarnings("unchecked")
    public static Map<Object,Object> getParents(TransformerContext context) {
        return (Map<Object,Object>) context.getResourceState(ResourceState.isdParents.name());
    }

    @SuppressWarnings("unchecked")
    public static Map<Object,TimingState> getTimingStates(TransformerContext context) {
        return (Map<Object,TimingState>) context.getResourceState(ResourceState.isdTimingStates.name());
    }

    public static class ISDTransformer extends AbstractTransformer {

        // options state
        private boolean outputDirectoryClean;
        private String outputDirectoryPath;
        private String outputEncodingName;
        private boolean outputIndent;
        private String outputPattern;

        // derived option state
        private File outputDirectory;
        private Charset outputEncoding;

        public ISDTransformer() {
        }

        public String getName() {
            return TRANSFORMER_NAME;
        }

        @Override
        public Collection<OptionSpecification> getShortOptionSpecs() {
            return null;
        }

        @Override
        public Collection<OptionSpecification> getLongOptionSpecs() {
            return longOptions;
        }

        @Override
        public int parseLongOption(List<String> args, int index) {
            String arg = args.get(index);
            int numArgs = args.size();
            String option = arg;
            assert option.length() > 2;
            option = option.substring(2);
            if (option.equals("isd-output-clean")) {
                outputDirectoryClean = true;
            } else if (option.equals("isd-output-directory")) {
                if (index + 1 > numArgs)
                    throw new MissingOptionArgumentException("--" + option);
                outputDirectoryPath = args.get(++index);
            } else if (option.equals("isd-output-encoding")) {
                if (index + 1 > numArgs)
                    throw new MissingOptionArgumentException("--" + option);
                outputEncodingName = args.get(++index);
            } else if (option.equals("isd-output-indent")) {
                outputIndent = true;
            } else if (option.equals("isd-output-pattern")) {
                if (index + 1 > numArgs)
                    throw new MissingOptionArgumentException("--" + option);
                outputPattern = args.get(++index);
            } else
                index = index - 1;
            return index + 1;
        }

        @Override
        public int parseShortOption(List<String> args, int index) {
            String arg = args.get(index);
            String option = arg;
            assert option.length() == 2;
            option = option.substring(1);
            throw new UnknownOptionException("-" + option);
        }

        @Override
        public void processDerivedOptions() {
            // output directory
            File outputDirectory;
            if (outputDirectoryPath != null) {
                outputDirectory = new File(outputDirectoryPath);
                if (!outputDirectory.exists())
                    throw new InvalidOptionUsageException("isd-output-directory", "directory does not exist: " + outputDirectoryPath);
                else if (!outputDirectory.isDirectory())
                    throw new InvalidOptionUsageException("isd-output-directory", "not a directory: " + outputDirectoryPath);
            } else
                outputDirectory = new File(".");
            this.outputDirectory = outputDirectory;
            // output encoding
            Charset outputEncoding;
            if (outputEncodingName != null) {
                try {
                    outputEncoding = Charset.forName(outputEncodingName);
                } catch (Exception e) {
                    outputEncoding = null;
                }
                if (outputEncoding == null)
                    throw new InvalidOptionUsageException("isd-output-encoding", "unknown encoding: " + outputEncodingName);
            } else
                outputEncoding = null;
            if (outputEncoding == null)
                outputEncoding = defaultOutputEncoding;
            this.outputEncoding = outputEncoding;
            // output pattern
            String outputPattern = this.outputPattern;
            if (outputPattern == null)
                outputPattern = defaultOutputFileNamePattern;
            this.outputPattern = outputPattern;
        }

        @Override
        public void transform(List<String> args, Object root, TransformerContext context, OutputStream out) {
            assert root != null;
            assert context != null;
            populateContext(context);
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message("*KEY*", "Transforming result using ''{0}'' transformer...", getName()));
            // extract significant time intervals
            Set<TimeInterval> intervals = extractISDIntervals(root, context);
            if (reporter.getDebugLevel() > 0) {
                StringBuffer sb = new StringBuffer();
                for (TimeInterval interval : intervals) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append(interval);
                }
                reporter.logDebug(reporter.message("*KEY*", "Resolved active intervals: {0}.", "{" + sb + "}"));
            }
            writeISDSequence(root, context, intervals, out);
        }

        private void populateContext(TransformerContext context) {
            context.setResourceState(ResourceState.isdHelper.name(), TTMLHelper.makeInstance(context.getModel().getTTMLVersion()));
            context.setResourceState(ResourceState.isdParents.name(), new java.util.HashMap<Object, Object>());
            context.setResourceState(ResourceState.isdTimingStates.name(), new java.util.HashMap<Object, TimingState>());
            context.setResourceState(ResourceState.isdGenerationIndices.name(), new int[GenerationIndex.values().length]);
        }

        private Set<TimeInterval> extractISDIntervals(Object root, TransformerContext context) {
            Reporter reporter = context.getReporter();
            getHelper(context).generateAnonymousSpans(root, context);
            recordParents(root, context);
            resolveTiming(root, context);
            Set<TimeCoordinate> coordinates = new java.util.TreeSet<TimeCoordinate>();
            for (TimeInterval interval : extractActiveIntervals(root, context)) {
                coordinates.add(interval.getBegin());
                coordinates.add(interval.getEnd());
            }
            if (reporter.getDebugLevel() > 0) {
                StringBuffer sb = new StringBuffer();
                for (TimeCoordinate coordinate : coordinates) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append(coordinate);
                }
                reporter.logDebug(reporter.message("*KEY*", "Resolved active coordinates: {0}.", "{" + sb + "}"));
            }
            Set<TimeInterval> intervals = new java.util.TreeSet<TimeInterval>();
            TimeCoordinate lastCoordinate = null;
            for (TimeCoordinate coordinate : coordinates) {
                if (lastCoordinate != null)
                    intervals.add(new TimeInterval(lastCoordinate, coordinate));
                lastCoordinate = coordinate;
            }
            return intervals;
        }

        private void recordParents(Object root, final TransformerContext context) {
            try {
                getHelper(context).traverse(root, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        Map<Object,Object> parents = getParents(context);
                        if (!parents.containsKey(content))
                            parents.put(content, parent);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private void resolveTiming(Object root, TransformerContext context) {
            resolveExplicit(root, context);
            resolveImplicit(root, context);
            resolveActive(root, context);
        }

        private void resolveExplicit(Object root, final TransformerContext context) {
            try {
                final TimeParameters timeParameters = TimingVerificationParameters.makeInstance(root, context.getExternalParameters()).getTimeParameters();
                getHelper(context).traverse(root, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        if (getHelper(context).isTimed(content)) {
                            TimingState ts = getTimingState(content, context, timeParameters);
                            ts.resolveExplicit();
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private void resolveImplicit(Object root, final TransformerContext context) {
            try {
                final TimeParameters timeParameters = TimingVerificationParameters.makeInstance(root, context.getExternalParameters()).getTimeParameters();
                getHelper(context).traverse(root, new PostVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        if (getHelper(context).isTimed(content)) {
                            TimingState ts = getTimingState(content, context, timeParameters);
                            ts.resolveImplicit();
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private void resolveActive(Object root, final TransformerContext context) {
            try {
                final TimeParameters timeParameters = TimingVerificationParameters.makeInstance(root, context.getExternalParameters()).getTimeParameters();
                getHelper(context).traverse(root, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        if (getHelper(context).isTimed(content)) {
                            TimingState ts = getTimingState(content, context, timeParameters);
                            ts.resolveActive();
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private java.util.Set<TimeInterval> extractActiveIntervals(Object root, final TransformerContext context) {
            final TimeParameters timeParameters = TimingVerificationParameters.makeInstance(root, context.getExternalParameters()).getTimeParameters();
            final java.util.Set<TimeInterval> intervals = new java.util.TreeSet<TimeInterval>();
            try {
                getHelper(context).traverse(root, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        if (getHelper(context).isTimed(content)) {
                            TimingState ts = getTimingState(content,context, timeParameters);
                            ts.extractActiveInterval(intervals);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return intervals;
        }

        private TimingState getTimingState(Object content, TransformerContext context, TimeParameters timeParameters) {
            if (content == null)
                return null;
            Map<Object,TimingState> timingStates = getTimingStates(context);
            if (!timingStates.containsKey(content))
                timingStates.put(content, new TimingState(context, content, timeParameters));
            return getTimingStates(context).get(content);
        }

        private void writeISDSequence(Object root, TransformerContext context, Set<TimeInterval> intervals, OutputStream out) {
            Reporter reporter = context.getReporter();
            boolean suppressOutput = (Boolean) context.getResourceState(TransformerContext.ResourceState.ttxSuppressOutputSerialization.name());
            if (!suppressOutput && outputDirectoryClean)
                cleanOutputDirectory(outputDirectory, context);
            try {
                Model model = context.getModel();
                JAXBContext jc = JAXBContext.newInstance(model.getJAXBContextPath());
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.newDocument();
                Marshaller m = jc.createMarshaller();
                m.marshal(context.getBindingElement(context.getXMLNode(root)), doc);
                List<Object> isdSequence = new java.util.ArrayList<Object>();
                for (TimeInterval interval : intervals) {
                    Document docCopy = copyDocument(doc, db);
                    markIdAttributes(docCopy, context);
                    pruneIntervals(docCopy, context, interval);
                    pruneRegions(docCopy, context);
                    pruneTimingAndRegionAttributes(docCopy, context);
                    generateISDWrapper(docCopy, interval, context);
                    pruneISDExclusions(docCopy, context);
                    Namespaces.normalize(docCopy, model.getNormalizedPrefixes());
                    if (hasUsableContent(docCopy, context)) {
                        Object isd = writeISD(docCopy, isdSequence.size() + 1, suppressOutput, context);
                        if (isd != null)
                            isdSequence.add(isd);
                    }
                }
                if (suppressOutput) {
                    reporter.logInfo(reporter.message("*KEY*", "Suppressing ''{0}'' transformer output serialization.", getName()));
                    context.setResourceState(TransformerContext.ResourceState.ttxOutput.name(), isdSequence);
                }
            } catch (JAXBException e) {
                reporter.logError(e);
            } catch (ParserConfigurationException e) {
                reporter.logError(e);
            }
        }

        private static Document copyDocument(Document doc, DocumentBuilder db) {
            Document docCopy = db.newDocument();
            Node rootCopy = doc.getDocumentElement().cloneNode(true);
            docCopy.appendChild(docCopy.adoptNode(rootCopy));
            return docCopy;
        }

        private static void markIdAttributes(Document doc, TransformerContext context) {
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (elt.hasAttributeNS(XML.xmlNamespace, "id"))
                            elt.setIdAttributeNS(XML.xmlNamespace, "id", true);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static void pruneIntervals(Document doc, TransformerContext context, final TimeInterval interval) {
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isTimedElement(elt)) {
                            TimeInterval eltInterval = getActiveInterval(elt);
                            if (eltInterval.isEmpty() || !eltInterval.intersects(interval)) {
                                assert parent instanceof Element;
                                Element eltParent = (Element) parent;
                                pruneElement(elt, eltParent);
                            }
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static boolean isTimedElement(Element elt) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(TTMLHelper.NAMESPACE_TT))
                return false;
            else {
                String localName = elt.getLocalName();
                if (localName.equals("animate"))
                    return true;
                else if (localName.equals("body"))
                    return true;
                else if (localName.equals("div"))
                    return true;
                else if (localName.equals("p"))
                    return true;
                else if (localName.equals("span"))
                    return true;
                else if (localName.equals("br"))
                    return true;
                else if (localName.equals("region"))
                    return true;
                else if (localName.equals("set"))
                    return true;
                else
                    return false;
            }
        }

        private static TimeInterval getActiveInterval(Element elt) {
            String begin;
            if (elt.hasAttributeNS(TTMLHelper.NAMESPACE_ISD, "begin"))
                begin = elt.getAttributeNS(TTMLHelper.NAMESPACE_ISD, "begin");
            else
                begin = null;
            String end;
            if (elt.hasAttributeNS(TTMLHelper.NAMESPACE_ISD, "end"))
                end = elt.getAttributeNS(TTMLHelper.NAMESPACE_ISD, "end");
            else
                end = null;
            return new TimeInterval(begin, end);
        }

        private static void pruneElement(Element elt, Element parent) {
            assert elt != null;
            if (parent != null) {
                assert elt.getParentNode() == parent;
                parent.removeChild(elt);
            }
        }

        private static void pruneRegions(Document doc, TransformerContext context) {
            List<Element> regions = getRegionElements(doc, context);
            regions = maybeImplyDefaultRegion(doc, context, regions);
            for (Element region : regions) {
                try {
                    Element body = copyBodyElement(doc, context);
                    if (body != null) {
                        pruneUnselectedContent(body, context, region);
                        if (hasUsableContent(body, context))
                            region.appendChild(body);
                    }
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
            removeBodyElement(doc, context);
        }

        private static boolean hasUsableContent(Element elt, TransformerContext context) {
            final boolean returnUsable[] = new boolean[1];
            try {
                Traverse.traverseElements(elt, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isParagraphElement(elt)) {
                            if (hasUsableContentInParagraph(elt)) {
                                returnUsable[0] = true;
                                return false;
                            } else
                                return true;
                        } else
                            return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return returnUsable[0];
        }

        private static boolean hasUsableContentInParagraph(Element elt) {
            String content = elt.getTextContent();
            for (int i = 0, n = content.length(); i < n; ++i) {
                if (!Character.isWhitespace(content.charAt(i)))
                    return true;
            }
            return false;
        }

        private static void pruneUnselectedContent(final Element body, TransformerContext context, final Element region) {
            try {
                Traverse.traverseElements(body, null, new PostVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (!isSelectedContent(elt, region)) {
                            if (parent != null) {
                                assert parent instanceof Element;
                                Element eltParent = (Element) parent;
                                pruneElement(elt, eltParent);
                            } else if (elt != body) {
                                assert false;
                            }
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static boolean isSelectedContent(Element elt, Element region) {
            String id = getAssociatedRegionIdentifier(elt);
            if (id != null) {
                String idRegion = getXmlIdentifier(region);
                if (idRegion != null)
                    return id.equals(idRegion);
                else
                    return false;
            } else
                return isAnonymousRegionElement(region);
        }

        private static String getAssociatedRegionIdentifier(Element elt) {
            String id;
            id = getRegionIdentifier(elt);
            if (id != null)
                return id;
            // use descendant before ancestor since we are doing post-traversal visit
            id = getNearestDescendantRegionIdentifier(elt);
            if (id != null)
                return id;
            // use ancestor after descendant since we are doing post-traversal visit
            id = getNearestAncestorRegionIdentifier(elt);
            if (id != null)
                return id;
            return null;
        }

        private static String getNearestAncestorRegionIdentifier(Element elt) {
            for (Node a = elt.getParentNode(); a != null; a = a.getParentNode()) {
                if (a instanceof Element) {
                    String id = getRegionIdentifier((Element) a);
                    if (id != null)
                        return id;
                } else
                    break;
            }
            return null;
        }

        private static String getNearestDescendantRegionIdentifier(Element elt) {
            Queue<Element> descendants = new java.util.LinkedList<Element>();
            descendants.add(elt);
            while (!descendants.isEmpty()) {
                Element e = descendants.remove();
                for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
                    if (n instanceof Element) {
                        Element c = (Element) n;
                        String id = getRegionIdentifier(c);
                        if (id != null)
                            return id;
                        descendants.add(c);
                    }
                }
            }
            return null;
        }

        private static String getRegionIdentifier(Element elt) {
            if (elt.hasAttributeNS(null, "region"))
                return elt.getAttributeNS(null, "region");
            else
                return null;
        }

        private static String getXmlIdentifier(Element elt) {
            if (elt.hasAttributeNS(XML.xmlNamespace, "id"))
                return elt.getAttributeNS(XML.xmlNamespace, "id");
            else
                return null;
        }

        private static List<Element> getRegionElements(Document doc, TransformerContext context) {
            final List<Element> regions = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isOutOfLineRegionElement(elt))
                            regions.add(elt);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return regions;
        }

        private static boolean isOutOfLineRegionElement(Element elt) {
            return isRegionElement(elt) && isLayoutElement((Element) elt.getParentNode());
        }

        private static Element maybeImplyHeadElement(Document document, TransformerContext context) {
            Element head = getHeadElement(document, context);
            if (head == null) {
                Element defaultHead = document.createElementNS(TTMLHelper.NAMESPACE_TT, "head");
                Element body = getBodyElement(document, context);
                try {
                    Element root = document.getDocumentElement();
                    if (body != null)
                        root.insertBefore(defaultHead, body);
                    else
                        root.appendChild(defaultHead);
                    head = defaultHead;
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
            return head;
        }

        private static Element maybeImplyLayoutElement(Document document, TransformerContext context) {
            Element layout = getLayoutElement(document, context);
            if (layout == null) {
                Element defaultLayout = document.createElementNS(TTMLHelper.NAMESPACE_TT, "layout");
                Element head = maybeImplyHeadElement(document, context);
                assert head != null;
                try {
                    head.appendChild(defaultLayout);
                    layout = defaultLayout;
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
            return layout;
        }

        private static List<Element> maybeImplyDefaultRegion(Document document, TransformerContext context, List<Element> regions) {
            if (regions.isEmpty()) {
                try {
                    Element defaultRegion = document.createElementNS(TTMLHelper.NAMESPACE_TT, "region");
                    defaultRegion.setAttributeNS(XML.xmlNamespace, "id", getHelper(context).generateAnonymousRegionId(context));
                    Element layout = maybeImplyLayoutElement(document, context);
                    assert layout != null;
                    layout.appendChild(defaultRegion);
                    regions.add(defaultRegion);
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
            return regions;
        }

        private static Element copyBodyElement(Document document, TransformerContext context) {
            Element body = getBodyElement(document, context);
            if (body != null)
                return (Element) body.cloneNode(true);
            else
                return null;
        }

        private static Element removeBodyElement(Document document, TransformerContext context) {
            try {
                Element body = getBodyElement(document, context);
                if (body != null)
                    return (Element) body.getParentNode().removeChild(body);
            } catch (DOMException e) {
                context.getReporter().logError(e);
            }
            return null;
        }

        private static boolean isTimedTextElement(Element elt, String localName) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri != null) && nsUri.equals(TTMLHelper.NAMESPACE_TT) && elt.getLocalName().equals(localName))
                    return true;
            }
            return false;
        }

        private static boolean isRootElement(Element elt) {
            return isTimedTextElement(elt, "tt");
        }

        private static Element getHeadElement(Document document, TransformerContext context) {
            final Element[] retHead = new Element[1];
            try {
                Traverse.traverseElements(document, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isHeadElement(elt)) {
                            retHead[0] = elt;
                            return false;
                        } else
                            return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return retHead[0];
        }

        private static boolean isHeadElement(Element elt) {
            return isTimedTextElement(elt, "head");
        }

        private static Element getBodyElement(Document document, TransformerContext context) {
            final Element[] retBody = new Element[1];
            try {
                Traverse.traverseElements(document, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        Element eltParent = (Element) parent;
                        if (isBodyElement(elt) && isRootElement(eltParent)) {
                            retBody[0] = elt;
                            return false;
                        } else
                            return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return retBody[0];
        }

        private static boolean isBodyElement(Element elt) {
            return isTimedTextElement(elt, "body");
        }

        private static Element getLayoutElement(Document document, TransformerContext context) {
            final Element[] retLayout = new Element[1];
            try {
                Traverse.traverseElements(document, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isLayoutElement(elt)) {
                            retLayout[0] = elt;
                            return false;
                        } else
                            return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return retLayout[0];
        }

        private static boolean isLayoutElement(Element elt) {
            return isTimedTextElement(elt, "layout");
        }

        private static boolean isRegionElement(Element elt) {
            return isTimedTextElement(elt, "region");
        }

        private static boolean isAnonymousRegionElement(Element elt) {
            if (!isRegionElement(elt))
                return false;
            else {
                String id = getXmlIdentifier(elt);
                return (id != null) && (id.indexOf("isdRegion") == 0);
            }
        }

        private static boolean isParagraphElement(Element elt) {
            return isTimedTextElement(elt, "p");
        }

        private static boolean isSpanElement(Element elt) {
            return isTimedTextElement(elt, "span");
        }

        private static boolean isAnonymousSpanElement(Element elt) {
            if (!isSpanElement(elt))
                return false;
            else {
                String id = getXmlIdentifier(elt);
                return (id != null) && (id.indexOf("isdSpan") == 0);
            }
        }

        private static boolean isStyleElement(Element elt) {
            return isTimedTextElement(elt, "style");
        }

        private static boolean isAnimationElement(Element elt) {
            return isTimedTextElement(elt, "set");
        }

        private static boolean isContentElement(Element elt) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(TTMLHelper.NAMESPACE_TT))
                return false;
            else {
                String localName = elt.getLocalName();
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

        private static boolean isRegionOrContentElement(Element elt) {
            return isRegionElement(elt) || isContentElement(elt);
        }

        private static void pruneTimingAndRegionAttributes(Document document, TransformerContext context) {
            try {
                Traverse.traverseElements(document, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isTimedElement(elt)) {
                            pruneTimingAttributes(elt);
                            pruneRegionAttributes(elt);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static void pruneTimingAttributes(Element elt) {
            elt.removeAttributeNS(null, "begin");
            elt.removeAttributeNS(null, "end");
            elt.removeAttributeNS(null, "dur");
            elt.removeAttributeNS(null, "timeContainer");
        }

        private static void pruneRegionAttributes(Element elt) {
            elt.removeAttributeNS(null, "region");
        }

        private static void generateISDWrapper(Document document, TimeInterval interval, TransformerContext context) {
            Element root = document.getDocumentElement();
            Element isd = document.createElementNS(TTMLHelper.NAMESPACE_ISD, "isd");
            isd.setAttributeNS(null, "begin", interval.getBegin().toString());
            isd.setAttributeNS(null, "end", interval.getEnd().toString());
            copyWrapperAttributes(isd, root);
            generateISDComputedStyleSets(isd, root, context);
            generateISDRegions(isd, root, context);
            unwrapRedundantAnonymousSpans(isd, root, context);
            document.removeChild(root);
            document.appendChild(isd);
        }

        private static void copyWrapperAttributes(Element isd, Element root) {
            NamedNodeMap attrs = root.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    String nsUri = a.getNamespaceURI();
                    if (nsUri == null) {
                        continue;
                    } else if (nsUri.equals(TTMLHelper.NAMESPACE_TT_PARAMETER)) {
                        if (inISDParameterAttributeSet(a) && !isDefaultParameterValue(a)) {
                            isd.setAttributeNS(nsUri, a.getLocalName(), a.getValue());
                        }
                    } else if (nsUri.equals(XML.xmlNamespace)) {
                        String ln = a.getLocalName();
                        if (ln.equals("lang") || ln.equals("space"))
                            isd.setAttributeNS(nsUri, ln, a.getValue());
                    }
                }
            }
        }

        private static boolean inISDParameterAttributeSet(Attr attr) {
            String localName = attr.getLocalName();
            String value = attr.getValue();
            if ((value == null) || value.isEmpty())
                return false;
            else if (localName.equals("cellResolution"))
                return true;
            else if (localName.equals("frameRate"))
                return true;
            else if (localName.equals("frameRateMultiplier"))
                return true;
            else if (localName.equals("pixelAspectRatio"))
                return true;
            else if (localName.equals("subFrameRate"))
                return true;
            else if (localName.equals("tickRate"))
                return true;
            else
                return false;
        }

        private static void generateISDComputedStyleSets(final Element isd, Element root, TransformerContext context) {
            try {
                final Map<Element, StyleSet> computedStyleSets = resolveComputedStyles(root, context);
                final Set<String> styleIds = new java.util.HashSet<String>();
                final Document document = isd.getOwnerDocument();
                Traverse.traverseElements(root, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (computedStyleSets.containsKey(elt)) {
                            StyleSet css = computedStyleSets.get(elt);
                            String id = css.getId();
                            if (!id.isEmpty()) {
                                elt.setAttributeNS(TTMLHelper.NAMESPACE_ISD, "css", id);
                                if (!styleIds.contains(id)) {
                                    Element isdStyle = document.createElementNS(TTMLHelper.NAMESPACE_ISD, "css");
                                    generateAttributes(css, isdStyle);
                                    isd.appendChild(isdStyle);
                                    styleIds.add(id);
                                }
                            }
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static void generateAttributes(StyleSet ss, Element elt) {
            String id = ss.getId();
            if ((id != null) && !id.isEmpty()) {
                for (StyleSpecification s : ss.getStyles().values()) {
                    ComparableQName n = s.getName();
                    elt.setAttributeNS(n.getNamespaceURI(), n.getLocalPart(), s.getValue());
                }
                elt.setAttributeNS(XML.xmlNamespace, "id", id);
            }
        }

        private static Map<Element, StyleSet> resolveComputedStyles(Element root, TransformerContext context) {
            // compute initial value overrides
            StyleSet overrides = computeInitialStyleOverrides(root.getOwnerDocument(), context);

            // resolve specified style sets
            Map<Element, StyleSet> specifiedStyleSets = resolveSpecifiedStyles(root, context, overrides);

            // derive {CSS(E)} from {SSS(E)}
            Map<Element, StyleSet> computedStyleSets = new java.util.HashMap<Element, StyleSet>();
            for (Map.Entry<Element, StyleSet> e : specifiedStyleSets.entrySet()) {
                Element elt = e.getKey();
                if (isRegionElement(elt)) {
                    if (findChildElement(elt, TTMLHelper.NAMESPACE_TT, "body") != null)
                        computedStyleSets.put(elt, applicableStyles(e.getValue(), elt, context));
                } else if (isContentElement(elt)) {
                    computedStyleSets.put(elt, applicableStyles(e.getValue(), elt, context));
                }
            }

            // elide initial values
            for (Map.Entry<Element, StyleSet> e : computedStyleSets.entrySet())
                elideInitialValues(e.getValue(), e.getKey(), context);

            // compute set of unique CSSs
            Set<StyleSet> uniqueComputedStyleSets = new java.util.TreeSet<StyleSet>(StyleSet.getValuesComparator());
            for (StyleSet css : computedStyleSets.values()) {
                if (!css.isEmpty())
                    uniqueComputedStyleSets.add(css);
            }

            // obtain ordered list of unique CSSs
            List<StyleSet> uniqueStyles = new java.util.ArrayList<StyleSet>(uniqueComputedStyleSets);

            // assign identifiers to unique CSSs
            int uniqueStyleIndex = 0;
            for (StyleSet css : uniqueStyles)
                css.setId("s" + uniqueStyleIndex++);

            // remap CSS map entries to unique CSSs
            for (Map.Entry<Element,StyleSet> e : computedStyleSets.entrySet()) {
                StyleSet css = e.getValue();
                int index = uniqueStyles.indexOf(css);
                if (index >= 0) {
                    StyleSet cssUnique = uniqueStyles.get(index);
                    if (css != cssUnique) // N.B. must not use equals() here
                        e.setValue(cssUnique);
                }
            }

            return computedStyleSets;
        }

        private static StyleSet computeInitialStyleOverrides(Document doc, TransformerContext context) {
            StyleSet overrides = new StyleSet();
            for (Element initial : getInitialElements(doc, context)) {
                overrides.merge(getInlineStyles(initial));
            }
            return overrides;
        }

        private static List<Element> getInitialElements(Document doc, TransformerContext context) {
            final List<Element> initials = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(doc, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isInitialElement(elt))
                            initials.add(elt);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return initials;
        }

        private static boolean isInitialElement(Element elt) {
            return isTimedTextElement(elt, "initial");
        }

        private static Map<Element, StyleSet> resolveSpecifiedStyles(Element root, TransformerContext context, StyleSet overrides) {
            Map<Element, StyleSet> specifiedStyleSets = new java.util.HashMap<Element, StyleSet>();
            specifiedStyleSets = resolveSpecifiedStyles(getStyleElements(root, context), specifiedStyleSets, context, overrides);
            specifiedStyleSets = resolveSpecifiedStyles(getAnimationElements(root, context), specifiedStyleSets, context, overrides);
            specifiedStyleSets = resolveSpecifiedStyles(getRegionOrContentElements(root, context), specifiedStyleSets, context, overrides);
            return specifiedStyleSets;
        }

        private static Collection<Element> getStyleElements(Element root, TransformerContext context) {
            final Collection<Element> elts = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(root, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isStyleElement(elt)) {
                            elts.add(elt);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return topoSortByStyleReferences(elts, context);
        }

        private static Collection<Element> topoSortByStyleReferences(Collection<Element> elts, TransformerContext context) {
            DirectedGraph<Element> graph = new DirectedGraph<Element>();
            for (Element elt : elts)
                graph.addNode(elt);
            for (Element elt : elts) {
                for (Element refStyle : getReferencedStyleElements(elt)) {
                    graph.addEdge(elt, refStyle);
                }
            }
            try {
                List<Element> eltsSorted = TopologicalSort.sort(graph);
                // topo sort ensures all forward refs, but we want all reverse refs, so reverse sorted results
                Collections.reverse(eltsSorted);
                return eltsSorted;
            } catch (IllegalArgumentException e) {
                Reporter reporter = context.getReporter();
                reporter.logError(reporter.message("*KEY*", "Cycle in style chain, unable to resolve styles."));
                return new java.util.ArrayList<Element>();
            }
        }

        private static Collection<Element> getAnimationElements(Element root, TransformerContext context) {
            final Collection<Element> elts = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(root, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isAnimationElement(elt)) {
                            elts.add(elt);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return elts;
        }

        private static Collection<Element> getRegionOrContentElements(Element root, TransformerContext context) {
            final Collection<Element> elts = new java.util.ArrayList<Element>();
            try {
                Traverse.traverseElements(root, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isRegionOrContentElement(elt))
                            elts.add(elt);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
            return elts;
        }

        private static Map<Element, StyleSet> resolveSpecifiedStyles(Collection<Element> elts, Map<Element, StyleSet> specifiedStyleSets, TransformerContext context, StyleSet overrides) {
            for (Element elt : elts) {
                assert !specifiedStyleSets.containsKey(elt);
                specifiedStyleSets.put(elt, computeSpecifiedStyleSet(elt, specifiedStyleSets, context, overrides));
            }
            return specifiedStyleSets;
        }

        private static StyleSet applicableStyles(StyleSet ss, Element elt, TransformerContext context) {
            boolean containsInapplicableStyle = false;
            QName eltName = new QName(elt.getNamespaceURI(), elt.getLocalName());
            for (StyleSpecification s : ss.getStyles().values()) {
                if (!doesStyleApply(eltName, s.getName(), context)) {
                    containsInapplicableStyle = true;
                    break;
                }
            }
            if (!containsInapplicableStyle)
                return ss;
            else {
                StyleSet ssNew = new StyleSet(ss.getGeneration());
                for (StyleSpecification s : ss.getStyles().values()) {
                    if (doesStyleApply(eltName, s.getName(), context)) {
                        ssNew.merge(s);
                    }
                }
                return ssNew;
            }
        }

        private static boolean doesStyleApply(Element elt, QName styleName, TransformerContext context) {
            QName eltName = new QName(elt.getNamespaceURI(), elt.getLocalName());
            return doesStyleApply(eltName, styleName, context);
        }

        private static boolean doesStyleApply(QName eltName, QName styleName, TransformerContext context) {
            return context.getModel().doesStyleApply(eltName, styleName);
        }

        private static void elideInitialValues(StyleSet ss, Element elt, TransformerContext context) {
            QName eltName = new QName(elt.getNamespaceURI(), elt.getLocalName());
            List<StyleSpecification> elisions = new java.util.ArrayList<StyleSpecification>();
            for (StyleSpecification s : ss.getStyles().values()) {
                StyleSpecification initial = getInitialStyle(eltName, s.getName(), context, null);
                if (initial != null) {
                    String value = initial.getValue();
                    if ((value != null) && value.equals(s.getValue()))
                        elisions.add(s);
                }
            }
            for (StyleSpecification s : elisions) {
                ss.remove(s.getName());
            }
        }

        private static StyleSpecification getInitialStyle(Element elt, QName styleName, TransformerContext context, StyleSet overrides) {
            QName eltName = new QName(elt.getNamespaceURI(), elt.getLocalName());
            return getInitialStyle(eltName, styleName, context, overrides);
        }

        private static StyleSpecification getInitialStyle(QName eltName, QName styleName, TransformerContext context, StyleSet overrides) {
            String value = context.getModel().getInitialStyleValue(eltName, styleName);
            if (value != null) {
                if (overrides != null) {
                    StyleSpecification override = overrides.get(styleName);
                    if (override != null)
                        value = override.getValue();
                }
                return new StyleSpecification(new ComparableQName(styleName), value);
            } else
                return null;
        }

        private static StyleSet computeSpecifiedStyleSet(Element elt, Map<Element, StyleSet> specifiedStyleSets, TransformerContext context, StyleSet overrides) {
            // See TTML2, Section 8.4.4.2
            // 1. initialization
            StyleSet sss = new StyleSet(getHelper(context).generateStyleSetIndex(context));
            // 2. referential and chained referential styling
            for (StyleSet ss : getSpecifiedStyleSets(getReferencedStyleElements(elt), specifiedStyleSets))
                sss.merge(ss);
            // 3. nested styling
            for (StyleSet ss : getSpecifiedStyleSets(getChildStyleElements(elt), specifiedStyleSets))
                sss.merge(ss);
            // 4. inline styling
            sss.merge(getInlineStyles(elt));
            // 5. animation styling
            if (!isAnimationElement(elt)) {
                for (StyleSet ss : getSpecifiedStyleSets(getChildAnimationElements(elt), specifiedStyleSets))
                    sss.merge(ss);
            }
            // 6. implicit inheritance and initial value fallback
            if (!isAnimationElement(elt) && !isStyleElement(elt)) {
                for (QName name : getDefinedStyleNames(context)) {
                    if (!sss.containsKey(name)) {
                        StyleSpecification s;
                        if (!isInheritableStyle(elt, name, context) || isRootStylingElement(elt))
                            s = getInitialStyle(elt, name, context, overrides);
                        else if (specialStyleInheritance(elt, name, sss, context))
                            s = getSpecialInheritedStyle(elt, name, sss, specifiedStyleSets, context);
                        else
                            s = getNearestAncestorStyle(elt, name, specifiedStyleSets);
                        if ((s != null) && (doesStyleApply(elt, name, context) || isRootStylingElement(elt)))
                            sss.merge(s);
                    }
                }
            }
            return sss;
        }

        private static Collection<Element> getReferencedStyleElements(Element elt) {
            Collection<Element> elts = new java.util.ArrayList<Element>();
            if (elt.hasAttribute("style")) {
                String style = elt.getAttribute("style");
                if (!style.isEmpty()) {
                    Document document = elt.getOwnerDocument();
                    String[] idrefs = style.split("\\s+");
                    for (String idref : idrefs) {
                        Element refStyle = document.getElementById(idref);
                        if (refStyle != null)
                            elts.add(refStyle);
                    }
                }
            }
            return elts;
        }

        private static Collection<Element> getChildStyleElements(Element elt) {
            Collection<Element> elts = new java.util.ArrayList<Element>();
            for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Element) {
                    Element c = (Element) n;
                    if (isStyleElement(c))
                        elts.add(c);
                }
            }
            return elts;
        }

        private static Collection<Element> getChildAnimationElements(Element elt) {
            Collection<Element> elts = new java.util.ArrayList<Element>();
            for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Element) {
                    Element c = (Element) n;
                    if (isAnimationElement(c))
                        elts.add(c);
                }
            }
            return elts;
        }

        private static Collection<StyleSet> getSpecifiedStyleSets(Collection<Element> elts, Map<Element, StyleSet> specifiedStyleSets) {
            Collection<StyleSet> styleSets = new java.util.ArrayList<StyleSet>();
            for (Element elt : elts) {
                if (specifiedStyleSets.containsKey(elt))
                    styleSets.add(specifiedStyleSets.get(elt));
            }
            return styleSets;
        }

        private static StyleSet getInlineStyles(Element elt) {
            StyleSet styles = new StyleSet();
            NamedNodeMap attrs = elt.getAttributes();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    String nsUri = a.getNamespaceURI();
                    if ((nsUri != null) && nsUri.equals(TTMLHelper.NAMESPACE_TT_STYLE)) {
                        styles.merge(new StyleSpecification(new ComparableQName(a.getNamespaceURI(), a.getLocalName()), a.getValue()));
                    }
                }
            }
            return styles;
        }

        private static Collection<QName> getDefinedStyleNames(TransformerContext context) {
            return context.getModel().getDefinedStyleNames();
        }

        private static boolean isInheritableStyle(Element elt, QName styleName, TransformerContext context) {
            return isInheritableStyle(new QName(elt.getNamespaceURI(), elt.getLocalName()), styleName, context);
        }

        private static boolean isInheritableStyle(QName eltName, QName styleName, TransformerContext context) {
            return context.getModel().isInheritableStyle(eltName, styleName);
        }

        private static boolean isRootStylingElement(Element elt) {
            /* TBD - MIGRATE TO ROOT in TTML2
            int version = getHelper(context).getVersion();
            if (version == 1)
                return isRegionElement(elt);
            else
                return isRootElement(elt);
            */
            return isRegionElement(elt);
        }

        private static boolean specialStyleInheritance(Element elt, QName styleName, StyleSet sss, TransformerContext context) {
            return getHelper(context).specialStyleInheritance(elt, styleName, sss, context);
        }

        private static StyleSpecification getSpecialInheritedStyle(Element elt, QName styleName, StyleSet sss, Map<Element, StyleSet> specifiedStyleSets, TransformerContext context) {
            return getHelper(context).getSpecialInheritedStyle(elt, styleName, sss, specifiedStyleSets, context);
        }

        private static StyleSpecification getNearestAncestorStyle(Element elt, QName styleName, Map<Element, StyleSet> specifiedStyleSets) {
            for (Node a = elt.getParentNode(); a != null; a = a.getParentNode()) {
                if (a instanceof Element) {
                    Element p = (Element) a;
                    StyleSet ss = specifiedStyleSets.get(p);
                    if (ss != null) {
                        if (ss.containsKey(styleName))
                            return ss.get(styleName);
                    }
                }
            }
            return null;
        }

        private static void generateISDRegions(final Element isd, Element root, final TransformerContext context) {
            try {
                Traverse.traverseElements(root, null, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isRegionElement(elt)) {
                            generateISDRegion(isd, elt, context);
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static void generateISDRegion(Element isd, Element region, TransformerContext context) {
            Element body = detachBody(region);
            if (body != null) {
                Document document = isd.getOwnerDocument();
                Element isdRegion = document.createElementNS(TTMLHelper.NAMESPACE_ISD, "region");
                copyRegionAttributes(isdRegion, region, context);
                isdRegion.appendChild(body);
                isd.appendChild(isdRegion);
            }
        }

        private static void copyRegionAttributes(Element isdRegion, Element region, TransformerContext context) {
            boolean retainLocations = (Boolean) context.getResourceState(TransformerContext.ResourceState.ttxRetainLocations.name());
            NamedNodeMap attrs = region.getAttributes();
            List<Attr> attrsNew = new java.util.ArrayList<Attr>();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                Attr aNew = null;
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    String nsUri = a.getNamespaceURI();
                    String localName = a.getLocalName();
                    if (nsUri != null) {
                        if (nsUri.equals(XML.xmlNamespace)) {
                            if (localName.equals("id"))
                                aNew = a;
                        } else if (nsUri.equals(TTMLHelper.NAMESPACE_ISD)) {
                            if (localName.equals("css"))
                                aNew = a;
                        } else if (nsUri.equals(Annotations.getNamespace())) {
                            if (retainLocations) {
                                if (localName.equals("loc"))
                                    aNew = a;
                            }
                        }
                    }
                }
                if (aNew != null)
                    attrsNew.add(aNew);
            }
            for (Attr a : attrsNew)
                isdRegion.setAttributeNS(a.getNamespaceURI(), a.getLocalName(), a.getValue());
        }

        private static Element detachBody(Element region) {
            Element body = findChildElement(region, TTMLHelper.NAMESPACE_TT, "body");
            if (body != null) {
                assert body.getParentNode() == region;
                region.removeChild(body);
                return body;
            } else
                return null;
        }

        private static void unwrapRedundantAnonymousSpans(final Element isd, Element root, TransformerContext context) {
            try {
                Traverse.traverseElements(isd, null, new PostVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (isAnonymousSpanElement(elt)) {
                            if (parent instanceof Element) {
                                if (hasSameComputedStyleSet(elt, (Element) parent))
                                    unwrapAnonymousSpan(elt, (Element) parent);
                            }
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static boolean hasSameComputedStyleSet(Element e1, Element e2) {
            if (!e1.hasAttributeNS(TTMLHelper.NAMESPACE_ISD, "css"))
                return false;
            else if (!e2.hasAttributeNS(TTMLHelper.NAMESPACE_ISD, "css"))
                return false;
            else {
                String s1 = e1.getAttributeNS(TTMLHelper.NAMESPACE_ISD, "css");
                String s2 = e2.getAttributeNS(TTMLHelper.NAMESPACE_ISD, "css");
                return s1.equals(s2);
            }
        }

        private static void unwrapAnonymousSpan(Element elt, Element parent) {
            Node fc = elt.getFirstChild();
            assert fc != null;
            Node lc = elt.getLastChild();
            assert lc == fc;
            if (fc instanceof Text) {
                Text text = (Text) elt.removeChild(fc);
                assert text != null;
                parent.replaceChild(text, elt);
            }
        }

        private static Element findChildElement(Element elt, String namespace, String name) {
            for (Node n = elt.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Element) {
                    Element c = (Element) n;
                    String nsUri = c.getNamespaceURI();
                    String localName = c.getLocalName();
                    if ((namespace == null) ^ (nsUri == null))
                        continue;
                    else if (!localName.equals(name))
                        continue;
                    else
                        return c;
                }
            }
            return null;
        }

        private void pruneISDExclusions(Document document, final TransformerContext context) {
            try {
                Traverse.traverseElements(document, new PreVisitor() {
                    public boolean visit(Object content, Object parent, Visitor.Order order) {
                        assert content instanceof Element;
                        Element elt = (Element) content;
                        if (!maybeExcludeElement(elt, context))
                            maybeExcludeAttributes(elt, context);
                        return true;
                    }
                });
            } catch (Exception e) {
                context.getReporter().logError(e);
            }
        }

        private static boolean maybeExcludeElement(Element elt, TransformerContext context) {
            boolean retainMetadata = (Boolean) context.getResourceState(TransformerContext.ResourceState.ttxRetainMetadata.name());
            String nsUri = elt.getNamespaceURI();
            boolean exclude = false;
            if (nsUri.equals(TTMLHelper.NAMESPACE_TT_METADATA)) {
                if (!retainMetadata)
                    exclude = true;
            }
            if (exclude) {
                excludeElement(elt);
                return true;
            } else
                return false;
        }

        private static void excludeElement(Element elt) {
            Node parent = elt.getParentNode();
            if (parent != null) {
                parent.removeChild(elt);
            }
        }

        private static boolean maybeExcludeAttributes(Element elt, TransformerContext context) {
            boolean retainLocations = (Boolean) context.getResourceState(TransformerContext.ResourceState.ttxRetainLocations.name());
            String nsUriElt = elt.getNamespaceURI();
            NamedNodeMap attrs = elt.getAttributes();
            List<Attr> exclusions = new java.util.ArrayList<Attr>();
            for (int i = 0, n = attrs.getLength(); i < n; ++i) {
                Node node = attrs.item(i);
                if (node instanceof Attr) {
                    Attr a = (Attr) node;
                    String nsUri = a.getNamespaceURI();
                    String localName = a.getLocalName();
                    String value = a.getValue();
                    if (nsUri == null) {
                        if (localName.equals("style") && value.isEmpty())
                            exclusions.add(a);
                        else
                            continue;
                    } else if (nsUri.equals(TTMLHelper.NAMESPACE_TT_METADATA)) {
                        exclusions.add(a);
                    } else if (nsUri.equals(TTMLHelper.NAMESPACE_TT_PARAMETER)) {
                        if (isDefaultParameterValue(a))
                            exclusions.add(a);
                    } else if (nsUri.equals(TTMLHelper.NAMESPACE_TT_STYLE)) {
                        if ((nsUriElt == null) || !nsUriElt.equals(TTMLHelper.NAMESPACE_ISD))
                            exclusions.add(a);
                    } else if (nsUri.equals(TTMLHelper.NAMESPACE_ISD)) {
                        if (!localName.equals("css"))
                            exclusions.add(a);
                    } else if (nsUri.equals(Annotations.getNamespace())) {
                        if (!retainLocations)
                            exclusions.add(a);
                    } else if (nsUri.equals(XML.xmlnsNamespace)) {
                        if (value != null) {
                            if (value.equals(TTMLHelper.NAMESPACE_ISD)) {
                                if ((nsUriElt != null) && nsUriElt.equals(TTMLHelper.NAMESPACE_TT))
                                    exclusions.add(a);
                            } else if (value.equals(TTMLHelper.NAMESPACE_TT))
                                exclusions.add(a);
                            else if (value.equals(TTMLHelper.NAMESPACE_TT_METADATA))
                                exclusions.add(a);
                            else if (value.equals(Annotations.getNamespace()))
                                exclusions.add(a);
                        }
                    }
                }
            }
            for (Attr a : exclusions)
                elt.removeAttributeNode(a);
            return exclusions.size() > 0;
        }

        // TBD - use defaulting data from TTML1ParameterVerifier.parameterAccessorMap
        private static boolean isDefaultParameterValue(Attr attr) {
            String localName = attr.getLocalName();
            String value = attr.getValue();
            if ((value == null) || value.isEmpty())
                return false;
            else if (localName.equals("cellResolution"))
                return value.equals("32 15");
            else if (localName.equals("clockMode"))
                return value.equals("utc");
            else if (localName.equals("dropMode"))
                return value.equals("nonDrop");
            else if (localName.equals("frameRateMultiplier"))
                return value.equals("1 1");
            else if (localName.equals("markerMode"))
                return value.equals("discontinuous");
            else if (localName.equals("pixelAspectRatio"))
                return value.equals("1 1");
            else if (localName.equals("subFrameRate"))
                return value.equals("1");
            else if (localName.equals("tickRate"))
                return value.equals("1");
            else if (localName.equals("timeBase"))
                return value.equals("media");
            else
                return false;
        }

        private static boolean hasUsableContent(Document document, TransformerContext context) {
            Element root = document.getDocumentElement();
            return (root != null) && hasUsableContent(root, context);
        }

        private static void cleanOutputDirectory(File directory, TransformerContext context) {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message("*KEY*", "Cleaning ISD artifacts from output directory ''{0}''...", directory.getPath()));
            File[] files = directory.listFiles();
            if (files != null) {
                for (File f : files) {
                    String name = f.getName();
                    if (name.indexOf("isd") != 0)
                        continue;
                    else if (name.indexOf(".xml") != (name.length() - 4))
                        continue;
                    else if (!f.delete())
                        throw new TransformerException("unable to clean output directory: can't delete: '" + name + "'");
                }
            }
        }

        private Object writeISD(Document document, int sequenceIndex, boolean suppressOutput, TransformerContext context) {
            if (suppressOutput)
                return writeISDAsByteArray(document, sequenceIndex, context);
            else
                return writeISDAsFile(document, sequenceIndex, context);
        }

        private Object writeISDAsFile(Document document, int sequenceIndex, TransformerContext context) {
           Reporter reporter = context.getReporter();
           FileOutputStream fos = null;
           BufferedOutputStream bos = null;
           try {
               String outputFileName = MessageFormat.format(outputPattern, sequenceIndex);
               File outputFile = new File(outputDirectory, outputFileName);
               fos = new FileOutputStream(outputFile);
               bos = new BufferedOutputStream(fos);
               writeISD(document, bos, context);
               reporter.logInfo(reporter.message("*KEY*", "Wrote ISD ''{0}''.", outputFile.getAbsolutePath()));
               return outputFile;
           } catch (FileNotFoundException e) {
               reporter.logError(e);
               return null;
           } finally {
               IOUtil.closeSafely(bos);
               IOUtil.closeSafely(fos);
           }
        }

        private Object writeISDAsByteArray(Document document, int sequenceIndex, TransformerContext context) {
           Reporter reporter = context.getReporter();
           ByteArrayOutputStream bas = null;
           BufferedOutputStream bos = null;
           try {
               bas = new ByteArrayOutputStream();
               bos = new BufferedOutputStream(bas);
               writeISD(document, bos, context);
               return bas.toByteArray();
           } catch (Exception e) {
               reporter.logError(e);
               return null;
           } finally {
               IOUtil.closeSafely(bos);
               IOUtil.closeSafely(bas);
           }
        }

        private void writeISD(Document document, OutputStream os, TransformerContext context) throws TransformerException {
           BufferedWriter bw = null;
           try {
               TransformerFactory tf = TransformerFactory.newInstance();
               DOMSource source = new DOMSource(document);
               bw = new BufferedWriter(new OutputStreamWriter(os, outputEncoding));
               StreamResult result = new StreamResult(bw);
               javax.xml.transform.Transformer t = tf.newTransformer();
               t.setOutputProperty(OutputKeys.INDENT, outputIndent ? "yes" : "no");
               t.transform(source, result);
           } catch (TransformerConfigurationException e) {
               throw new RuntimeException(e);
           } catch (javax.xml.transform.TransformerException e) {
               throw new RuntimeException(e);
           } finally {
               if (bw != null) {
                   try { bw.close(); } catch (IOException e) {}
               }
           }
        }
    }

}
