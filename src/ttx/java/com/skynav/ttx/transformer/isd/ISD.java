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
 
package com.skynav.ttx.transformer.isd;

import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.ObjectFactory;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;

import com.skynav.ttx.transformer.AbstractTransformer;
import com.skynav.ttx.transformer.Transformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.util.PostVisitor;
import com.skynav.ttx.util.PreVisitor;
import com.skynav.ttx.util.TimeCoordinate;
import com.skynav.ttx.util.TimeInterval;
import com.skynav.ttx.util.Visitor;

import com.skynav.xml.helpers.XML;

public class ISD {

    public static final String TRANSFORMER_NAME = "isd";
    public static final Transformer TRANSFORMER = new ISDTransformer();
    public static final String NAMESPACE_ISD = "http://www.w3.org/ns/ttml#isd";
    public static final String NAMESPACE_TT = TTML1.Constants.NAMESPACE_TT;

    enum GenerationIndex {
        isdInstanceIndex,
        isdAnonymousSpanIndex,
        isdAnonymousRegionIndex;
    };

    enum ResourceState {
        isdParents,
        isdTimingStates,
        isdGenerationIndices;
    };

    @SuppressWarnings("unchecked")
    public static Map<Object,Object> getParents(TransformerContext context) {
        return (Map<Object,Object>) context.getResourceState(ResourceState.isdParents.name());
    }

    @SuppressWarnings("unchecked")
    public static Map<Object,TimingState> getTimingStates(TransformerContext context) {
        return (Map<Object,TimingState>) context.getResourceState(ResourceState.isdTimingStates.name());
    }

    public static int[] getGenerationIndices(TransformerContext context) {
        return (int[]) context.getResourceState(ResourceState.isdGenerationIndices.name());
    }

    public static class ISDTransformer extends AbstractTransformer {

        protected ISDTransformer() {
        }

        public String getName() {
            return TRANSFORMER_NAME;
        }

        public void transform(Object root, TransformerContext context, OutputStream out) {
            assert root != null;
            assert context != null;
            populateContext(context);
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message("*KEY*", "Transforming result using ''{0}'' transformer...", getName()));
            TimedText tt = (TimedText) root;
            // extract significant time intervals
            Set<TimeInterval> intervals = extractISDIntervals(tt, context);
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
            context.setResourceState(ResourceState.isdParents.name(), new java.util.HashMap<Object, Object>());
            context.setResourceState(ResourceState.isdTimingStates.name(), new java.util.HashMap<Object, TimingState>());
            context.setResourceState(ResourceState.isdGenerationIndices.name(), new int[GenerationIndex.values().length]);
        }

        private Set<TimeInterval> extractISDIntervals(TimedText tt, TransformerContext context) {
            Reporter reporter = context.getReporter();
            generateAnonymousSpans(tt, context);
            recordParents(tt, context);
            resolveTiming(tt, context);
            Set<TimeCoordinate> coordinates = new java.util.TreeSet<TimeCoordinate>();
            for (TimeInterval interval : extractActiveIntervals(tt, context)) {
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

        private void generateAnonymousSpans(TimedText tt, final TransformerContext context) {
            traverse(tt, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (content instanceof String) {
                        List<Serializable> contentChildren;
                        if (parent instanceof Paragraph)
                            contentChildren = ((Paragraph) parent).getContent();
                        else if (parent instanceof Span)
                            contentChildren = ((Span) parent).getContent();
                        else
                            contentChildren = null;
                        if (contentChildren != null)
                            contentChildren.set(contentChildren.indexOf(content), wrapInAnonymousSpan(content, context));
                    }
                    return true;
                }
            });
        }

        private static final ObjectFactory spanFactory = new ObjectFactory();
        private JAXBElement<Span> wrapInAnonymousSpan(Object content, TransformerContext context) {
            assert content instanceof String;
            Span span = spanFactory.createSpan();
            span.setId(generateAnonymousSpanId(context));
            span.getContent().add((Serializable) content);
            return spanFactory.createSpan(span);
        }

        private String generateAnonymousSpanId(TransformerContext context) {
            int[] indices = getGenerationIndices(context);
            return "isdSpan" + pad(indices[GenerationIndex.isdAnonymousSpanIndex.ordinal()]++, 6);
        }

        private static String digits = "0123456789";
        private static String pad(int value, int width) {
            assert value >= 0;
            StringBuffer sb = new StringBuffer(width);
            while (value > 0) {
                sb.append(digits.charAt(value % 10));
                value /= 10;
            }
            while (sb.length() < width) {
                sb.append('0');
            }
            return sb.reverse().toString();
        }

        private void recordParents(TimedText tt, final TransformerContext context) {
            traverse(tt, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    Map<Object,Object> parents = getParents(context);
                    if (!parents.containsKey(content))
                        parents.put(content, parent);
                    return true;
                }
            });
        }

        private void resolveTiming(TimedText tt, TransformerContext context) {
            resolveExplicit(tt, context);
            resolveImplicit(tt, context);
            resolveActive(tt, context);
        }

        private void resolveExplicit(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveExplicit();
                    }
                    return true;
                }
            });
        }

        private void resolveImplicit(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PostVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveImplicit();
                    }
                    return true;
                }
            });
        }

        private void resolveActive(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveActive();
                    }
                    return true;
                }
            });
        }

        private java.util.Set<TimeInterval> extractActiveIntervals(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            final java.util.Set<TimeInterval> intervals = new java.util.TreeSet<TimeInterval>();
            traverse(tt, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content,context, timeParameters);
                        ts.extractActiveInterval(intervals);
                    }
                    return true;
                }
            });
            return intervals;
        }

        private static void traverse(TimedText tt, Visitor v) {
            Body body = tt.getBody();
            if (body != null) {
                if (!v.preVisit(tt, null))
                    return;
                traverse(body, tt, v);
                if (!v.postVisit(tt, null))
                    return;
            }
        }

        private static void traverse(Body body, Object parent, Visitor v) {
            if (!v.preVisit(body, parent))
                return;
            for (com.skynav.ttv.model.ttml1.tt.Set a : body.getAnimationClass())
                traverse(a, body, v);
            for (Division d : body.getDiv())
                traverse(d, body, v);
            if (!v.postVisit(body, parent))
                return;
        }

        private static void traverse(Division division, Object parent, Visitor v) {
            if (!v.preVisit(division, parent))
                return;
            for (com.skynav.ttv.model.ttml1.tt.Set a : division.getAnimationClass())
                traverse(a, division, v);
            for (Object b : division.getBlockClass())
                traverseBlock(b, division, v);
            if (!v.postVisit(division, parent))
                return;
        }

        private static void traverse(Paragraph paragraph, Object parent, Visitor v) {
            if (!v.preVisit(paragraph, parent))
                return;
            for (Serializable s : paragraph.getContent())
                traverseContent(s, paragraph, v);
            if (!v.postVisit(paragraph, parent))
                return;
        }

        private static void traverse(Span span, Object parent, Visitor v) {
            if (!v.preVisit(span, parent))
                return;
            for (Serializable s : span.getContent())
                traverseContent(s, span, v);
            if(!v.postVisit(span, parent))
                return;
        }

        private static void traverse(Break br, Object parent, Visitor v) {
            if(!v.preVisit(br, parent))
                return;
            for (com.skynav.ttv.model.ttml1.tt.Set a : br.getAnimationClass())
                traverse(a, br, v);
            if (!v.postVisit(br, parent))
                return;
        }

        private static void traverse(com.skynav.ttv.model.ttml1.tt.Set set, Object parent, Visitor v) {
            if (!v.preVisit(set, parent))
                return;
            if (!v.postVisit(set, parent))
                return;
        }

        private static void traverse(String string, Object parent, Visitor v) {
            if (!v.preVisit(string, parent))
                return;
            if (!v.postVisit(string, parent))
                return;
        }

        private static void traverseBlock(Object block, Object parent, Visitor v) {
            if (block instanceof Division)
                traverse((Division) block, parent, v);
            else if (block instanceof Paragraph)
                traverse((Paragraph) block, parent, v);
        }

        private static void traverseContent(Serializable content, Object parent, Visitor v) {
            if (content instanceof JAXBElement<?>) {
                Object element = ((JAXBElement<?>)content).getValue();
                if (element instanceof com.skynav.ttv.model.ttml1.tt.Set)
                    traverse((com.skynav.ttv.model.ttml1.tt.Set) element, parent, v);
                else if (element instanceof Span)
                    traverse((Span) element, parent, v);
                else if (element instanceof Break)
                    traverse((Break) element, parent, v);
            } else if (content instanceof String) {
                traverse((String) content, parent, v);
            }
        }

        private TimingState getTimingState(Object content, TransformerContext context, TimeParameters timeParameters) {
            if (content == null)
                return null;
            Map<Object,TimingState> timingStates = getTimingStates(context);
            if (!timingStates.containsKey(content))
                timingStates.put(content, new TimingState(context, content, timeParameters));
            return getTimingStates(context).get(content);
        }

        private static void writeISDSequence(Object root, TransformerContext context, Set<TimeInterval> intervals, OutputStream out) {
            Reporter reporter = context.getReporter();
            try {
                JAXBContext jc = JAXBContext.newInstance(context.getModel().getJAXBContextPath());
                Marshaller m = jc.createMarshaller();
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                List<Document> isdDocuments = new java.util.ArrayList<Document>();
                for (TimeInterval interval : intervals) {
                    Document doc = db.newDocument();
                    m.marshal(context.getBindingElement(context.getXMLNode(root)), doc);
                    pruneIntervals(doc, context, interval);
                    pruneRegions(doc, context);
                    pruneTimingAndRegionAttributes(doc, context);
                    if (hasUsableContent(doc, context)) {
                        isdDocuments.add(doc);
                    }
                }
                writeISDSequence(isdDocuments, context, out);
            } catch (JAXBException e) {
                reporter.logError(e);
            } catch (ParserConfigurationException e) {
                reporter.logError(e);
            }
        }

        private static void pruneIntervals(Document doc, TransformerContext context, final TimeInterval interval) {
            traverseElements(doc, new PreVisitor() {
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
        }

        private static boolean isTimedElement(Element elt) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
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
                else if (localName.equals("retion"))
                    return true;
                else if (localName.equals("set"))
                    return true;
                else
                    return false;
            }
        }

        private static TimeInterval getActiveInterval(Element elt) {
            String begin;
            if (elt.hasAttributeNS(NAMESPACE_ISD, "begin"))
                begin = elt.getAttributeNS(NAMESPACE_ISD, "begin");
            else
                begin = null;
            String end;
            if (elt.hasAttributeNS(NAMESPACE_ISD, "end"))
                end = elt.getAttributeNS(NAMESPACE_ISD, "end");
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
            List<Element> regions = getRegionElements(doc);
            regions = maybeImplyDefaultRegion(doc, context, regions);
            for (Element region : regions) {
                try {
                    Element body = extractBodyElement(doc, context);
                    if (body != null) {
                        pruneUnselectedContent(body, context, region);
                        if (hasUsableContent(body, context))
                            region.appendChild(body);
                    }
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
        }

        private static boolean hasUsableContent(Element elt, TransformerContext context) {
            final boolean returnUsable[] = new boolean[1];
            traverseElements(elt, null, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isAnonymousSpanElement(elt)) {
                        returnUsable[0] = true;
                        return false;
                    } else
                        return true;
                }
            });
            return returnUsable[0];
        }

        private static void pruneUnselectedContent(final Element body, TransformerContext context, final Element region) {
            traverseElements(body, null, new PostVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (!isSelectedContent(elt, region)) {
                        if (parent != null)
                            ((Node) parent).removeChild(elt);
                        else if (elt != body)
                            assert parent != null;
                    }
                    return true;
                }
            });
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
            id = getNearestAncestorRegionIdentifier(elt);
            if (id != null)
                return id;
            id = getNearestDescendantRegionIdentifier(elt);
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

        private static List<Element> getRegionElements(Document doc) {
            final List<Element> regions = new java.util.ArrayList<Element>();
            traverseElements(doc, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isOutOfLineRegionElement(elt))
                        regions.add(elt);
                    return true;
                }
            });
            return regions;
        }

        private static boolean isOutOfLineRegionElement(Element elt) {
            return isRegionElement(elt) && isLayoutElement((Element) elt.getParentNode());
        }

        private static Element maybeImplyHeadElement(Document doc, TransformerContext context) {
            Element head = getHeadElement(doc);
            if (head == null) {
                Element defaultHead = doc.createElementNS(NAMESPACE_TT, "head");
                Element body = getBodyElement(doc);
                try {
                    Element root = doc.getDocumentElement();
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

        private static Element maybeImplyLayoutElement(Document doc, TransformerContext context) {
            Element layout = getLayoutElement(doc);
            if (layout == null) {
                Element defaultLayout = doc.createElementNS(NAMESPACE_TT, "layout");
                Element head = maybeImplyHeadElement(doc, context);
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

        private static List<Element> maybeImplyDefaultRegion(Document doc, TransformerContext context, List<Element> regions) {
            if (regions.isEmpty()) {
                try {
                    Element defaultRegion = doc.createElementNS(NAMESPACE_TT, "region");
                    defaultRegion.setAttributeNS(XML.xmlNamespace, "id", generateAnonymousRegionId(context));
                    Element layout = maybeImplyLayoutElement(doc, context);
                    assert layout != null;
                    layout.appendChild(defaultRegion);
                    regions.add(defaultRegion);
                } catch (DOMException e) {
                    context.getReporter().logError(e);
                }
            }
            return regions;
        }

        private static String generateAnonymousRegionId(TransformerContext context) {
            int[] indices = getGenerationIndices(context);
            return "isdRegion" + pad(indices[GenerationIndex.isdAnonymousRegionIndex.ordinal()]++, 6);
        }

        private static Element extractBodyElement(Document doc, TransformerContext context) {
            try {
                Element body = getBodyElement(doc);
                return (Element) body.getParentNode().removeChild(body);
            } catch (DOMException e) {
                context.getReporter().logError(e);
                return null;
            }
        }

        private static Element getHeadElement(Document doc) {
            final Element[] retHead = new Element[1];
            traverseElements(doc, new PreVisitor() {
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
            return retHead[0];
        }

        private static boolean isHeadElement(Element elt) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
                    return false;
                else {
                    String localName = elt.getLocalName();
                    if (localName.equals("head"))
                        return true;
                    else
                        return false;
                }
            } else
                return false;
        }

        private static Element getBodyElement(Document doc) {
            final Element[] retBody = new Element[1];
            traverseElements(doc, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isBodyElement(elt)) {
                        retBody[0] = elt;
                        return false;
                    } else
                        return true;
                }
            });
            return retBody[0];
        }

        private static boolean isBodyElement(Element elt) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
                    return false;
                else {
                    String localName = elt.getLocalName();
                    if (localName.equals("body"))
                        return true;
                    else
                        return false;
                }
            } else
                return false;
        }

        private static Element getLayoutElement(Document doc) {
            final Element[] retLayout = new Element[1];
            traverseElements(doc, new PreVisitor() {
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
            return retLayout[0];
        }

        private static boolean isLayoutElement(Element elt) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
                    return false;
                else {
                    String localName = elt.getLocalName();
                    if (localName.equals("layout"))
                        return true;
                    else
                        return false;
                }
            } else
                return false;
        }

        private static boolean isRegionElement(Element elt) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
                    return false;
                else {
                    String localName = elt.getLocalName();
                    if (localName.equals("region"))
                        return true;
                    else
                        return false;
                }
            } else
                return false;
        }

        private static boolean isAnonymousRegionElement(Element elt) {
            if (!isRegionElement(elt))
                return false;
            else {
                String id = getXmlIdentifier(elt);
                return (id != null) && (id.indexOf("isdRegion") == 0);
            }
        }

        private static boolean isSpanElement(Element elt) {
            if (elt != null) {
                String nsUri = elt.getNamespaceURI();
                if ((nsUri == null) || !nsUri.equals(NAMESPACE_TT))
                    return false;
                else {
                    String localName = elt.getLocalName();
                    if (localName.equals("span"))
                        return true;
                    else
                        return false;
                }
            } else
                return false;
        }

        private static boolean isAnonymousSpanElement(Element elt) {
            if (!isSpanElement(elt))
                return false;
            else {
                String id = getXmlIdentifier(elt);
                return (id != null) && (id.indexOf("isdSpan") == 0);
            }
        }

        private static void pruneTimingAndRegionAttributes(Document doc, TransformerContext context) {
            traverseElements(doc, new PreVisitor() {
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

        private static boolean hasUsableContent(Document doc, TransformerContext context) {
            Element root = doc.getDocumentElement();
            return (root != null) && hasUsableContent(root, context);
        }

        private static void writeISDSequence(List<Document> isdDocuments, TransformerContext context, OutputStream out) {
        }

        private static void traverseElements(Document doc, Visitor v) {
            Element root = doc.getDocumentElement();
            if (root != null)
                traverseElements(root, null, v);
        }

        private static void traverseElements(Element elt, Element parent, Visitor v) {
            if (!v.preVisit(elt, parent))
                return;
            // extract stable (non-live) list of children in case visitors mutate child list
            List<Element> children = new java.util.ArrayList<Element>();
            for (Node node = elt.getFirstChild(); node != null; node = node.getNextSibling()) {
                if (node instanceof Element)
                    children.add((Element) node);
            }
            // traverse stable (non-live) list of children
            for (Element child : children) {
                traverseElements(child, elt, v);
            }
            if (!v.postVisit(elt, parent))
                return;
        }

    }

}
