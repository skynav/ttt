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

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.skynav.ttv.model.ttml2.tt.Animate;
import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Break;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.Layout;
import com.skynav.ttv.model.ttml2.tt.ObjectFactory;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Set;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.TimeContainer;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.ttml.TTML2StyleVerifier;
import com.skynav.ttx.transformer.TransformerContext;

public class TTML2Helper extends TTMLHelper {

    private static final ObjectFactory spanFactory = new ObjectFactory();

    @Override
    public int getVersion() {
        return 2;
    }

    @Override
    public void traverse(Object root, Visitor v) throws Exception {
        assert root instanceof TimedText;
        traverse((TimedText) root, v);
    }

    private static void traverse(TimedText tt, Visitor v) throws Exception {
        if (!v.preVisit(tt, null))
            return;
        Head head = tt.getHead();
        if (head != null)
            traverse(head, tt, v);
        Body body = tt.getBody();
        if (body != null)
            traverse(body, tt, v);
        if (!v.postVisit(tt, null))
            return;
    }

    private static void traverse(Head head, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(head, parent))
            return;
        Layout layout = head.getLayout();
        if (layout != null)
            traverse(layout, head, v);
        if (!v.postVisit(head, parent))
            return;
    }

    private static void traverse(Layout layout, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(layout, parent))
            return;
        for (Region r : layout.getRegion())
            traverse(r, layout, v);
        if (!v.postVisit(layout, parent))
            return;
    }

    private static void traverse(Region region, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(region, parent))
            return;
        if (!v.postVisit(region, parent))
            return;
    }

    private static void traverse(Body body, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(body, parent))
            return;
        traverseAnimations(body.getAnimationClass(), body, v);
        for (Division d : body.getDiv())
            traverse(d, body, v);
        if (!v.postVisit(body, parent))
            return;
    }

    private static void traverse(Division division, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(division, parent))
            return;
        traverseAnimations(division.getAnimationClass(), division, v);
        for (Object b : division.getBlockOrEmbeddedClass())
            traverseBlock(b, division, v);
        if (!v.postVisit(division, parent))
            return;
    }

    private static void traverse(Paragraph paragraph, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(paragraph, parent))
            return;
        for (Serializable s : paragraph.getContent())
            traverseContent(s, paragraph, v);
        if (!v.postVisit(paragraph, parent))
            return;
    }

    private static void traverse(Span span, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(span, parent))
            return;
        for (Serializable s : span.getContent())
            traverseContent(s, span, v);
        if(!v.postVisit(span, parent))
            return;
    }

    private static void traverse(Break br, Object parent, Visitor v) throws Exception {
        if(!v.preVisit(br, parent))
            return;
        traverseAnimations(br.getAnimationClass(), br, v);
        if (!v.postVisit(br, parent))
            return;
    }

    private static void traverse(Animate animate, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(animate, parent))
            return;
        if (!v.postVisit(animate, parent))
            return;
    }

    private static void traverse(Set set, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(set, parent))
            return;
        if (!v.postVisit(set, parent))
            return;
    }

    private static void traverse(String string, Object parent, Visitor v) throws Exception {
        if (!v.preVisit(string, parent))
            return;
        if (!v.postVisit(string, parent))
            return;
    }

    private static void traverseBlock(Object block, Object parent, Visitor v) throws Exception {
        if (block instanceof Division)
            traverse((Division) block, parent, v);
        else if (block instanceof Paragraph)
            traverse((Paragraph) block, parent, v);
    }

    private static void traverseContent(Serializable content, Object parent, Visitor v) throws Exception {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (element instanceof Animate)
                traverse((Animate) element, parent, v);
            else if (element instanceof Set)
                traverse((Set) element, parent, v);
            else if (element instanceof Span)
                traverse((Span) element, parent, v);
            else if (element instanceof Break)
                traverse((Break) element, parent, v);
        } else if (content instanceof String) {
            traverse((String) content, parent, v);
        }
    }

    private static void traverseAnimations(List<Object> animations, Object parent, Visitor v) throws Exception {
        for (Object a : animations) {
            if (a instanceof Animate)
                traverse((Animate) a, parent, v);
            else if (a instanceof Set)
                traverse((Set) a, parent, v);
        }
    }

    @Override
    public void generateAnonymousSpans(Object root, final TransformerContext context) {
        try {
            traverse(root, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    if (content instanceof String) {
                        List<Serializable> contentChildren;
                        if (parent instanceof Paragraph)
                            contentChildren = ((Paragraph) parent).getContent();
                        else if (parent instanceof Span)
                            contentChildren = ((Span) parent).getContent();
                        else
                            contentChildren = null;
                        if (contentChildren != null) {
                            if (!(parent instanceof Span) || (contentChildren.size() > 1))
                                contentChildren.set(contentChildren.indexOf(content), wrapInAnonymousSpan(content, context));
                        }
                    }
                    return true;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private JAXBElement<Span> wrapInAnonymousSpan(Object content, TransformerContext context) {
        assert content instanceof String;
        Span span = spanFactory.createSpan();
        span.setId(generateAnonymousSpanId(context));
        span.getContent().add((Serializable) content);
        return spanFactory.createSpan(span);
    }

    @Override
    @SuppressWarnings({"rawtypes","unchecked"})
    public List getChildren(Object content) {
        List children = null;
        if (content instanceof TimedText) {
            children = new java.util.ArrayList<Object>();
            Head head = ((TimedText) content).getHead();
            if (head != null)
                children.add(head);
            Body body = ((TimedText) content).getBody();
            if (body != null)
                children.add(body);
        } else if (content instanceof Head) {
            children = new java.util.ArrayList<Object>();
            Layout layout = ((Head) content).getLayout();
            if (layout != null)
                children.add(layout);
        } else if (content instanceof Layout) {
            children = ((Layout) content).getRegion();
        } else if (content instanceof Body) {
            children = ((Body) content).getDiv();
        } else if (content instanceof Division) {
            children = ((Division) content).getBlockOrEmbeddedClass();
        } else if (content instanceof Paragraph) {
            children = dereferenceAsContent(((Paragraph) content).getContent());
        } else if (content instanceof Span) {
            children = dereferenceAsContent(((Span) content).getContent());
        } else {
            children = new java.util.ArrayList<Object>();
        }
        return children;
    }

    private List<Object> dereferenceAsContent(List<Serializable> content) {
        List<Object> dereferencedContent = new java.util.ArrayList<Object>(content.size());
        for (Serializable s: content) {
            if (s instanceof JAXBElement<?>) {
                Object element = ((JAXBElement<?>)s).getValue();
                if (element instanceof Span)
                    dereferencedContent.add(element);
                else if (element instanceof Break)
                    dereferencedContent.add(element);
            }
        }
        return dereferencedContent;
    }

    @Override
    public boolean isTimedText(Object content) {
        return content instanceof TimedText;
    }

    @Override
    public boolean isAnonymousSpan(Object content) {
        if (content instanceof Span) {
            String value = getStringValuedAttribute(content, "id");
            return (value != null) && (value.indexOf("ttxSpan") == 0);
        } else
            return false;
    }

    @Override
    public boolean isTimed(Object content) {
        if (content instanceof TimedText)
            return true;
        else if (content instanceof Head)
            return true;
        else if (content instanceof Layout)
            return true;
        else if (content instanceof Region)
            return true;
        else if (content instanceof Body)
            return true;
        else if (content instanceof Division)
            return true;
        else if (content instanceof Paragraph)
            return true;
        else if (content instanceof Span)
            return true;
        else if (content instanceof Break)
            return true;
        else if (content instanceof Animate)
            return true;
        else if (content instanceof Set)
            return true;
        else
            return false;
    }

    @Override
    public boolean isTimedContainer(Object content) {
        if (content instanceof TimedText)
            return true;
        else if (content instanceof Head)
            return true;
        else if (content instanceof Layout)
            return true;
        else if (content instanceof Body)
            return true;
        else if (content instanceof Division)
            return true;
        else if (content instanceof Paragraph)
            return true;
        else if (content instanceof Span)
            return true;
        else
            return false;
    }

    @Override
    public boolean isSequenceContainer(Object content) {
        TimeContainer container = getTimeContainer(content);
        return (container != null ) ? container.value().equals("seq") : false;
    }

    private TimeContainer getTimeContainer(Object content) {
        if (isTimedContainer(content)) {
            TimeContainer container = null;
            if (content instanceof TimedText)
                container = TimeContainer.PAR;
            else if (content instanceof Head)
                container = TimeContainer.PAR;
            else if (content instanceof Layout)
                container = TimeContainer.PAR;
            else if (content instanceof Body)
                container = ((Body) content).getTimeContainer();
            else if (content instanceof Division)
                container = ((Division) content).getTimeContainer();
            else if (content instanceof Paragraph)
                container = ((Paragraph) content).getTimeContainer();
            else if (content instanceof Span)
                container = ((Span) content).getTimeContainer();
            else
                unexpectedContent(content);
            return container;
        } else
            return null;
    }

    private static boolean unexpectedContent(Object content) throws IllegalStateException {
        throw new IllegalStateException("Unexpected JAXB content object of type '" + content.getClass().getName() +  "'.");
    }

    @Override
    public String getClassString(Object content) {
        String cls = content.getClass().toString();
        cls = cls.substring(cls.lastIndexOf('.') + 1);
        if (isAnonymousSpan(content)) {
            List<Serializable> spanContent = ((Span) content).getContent();
            if (spanContent.size() == 1) {
                Serializable spanContentFirst = spanContent.get(0);
                if (spanContentFirst instanceof String) {
                    String spanText = (String) spanContentFirst;
                    cls = "AnonSpan(" + escapeControls(spanText) + ")";
                }
            }
        }
        return cls;
    }

    @Override
    public boolean specialStyleInheritance(Element elt, QName styleName, StyleSet sss, TransformerContext context) {
        if (hasSpecialInheritance(styleName)) {
            if (isRubyTextContainer(elt, sss) || isRubyText(elt, sss))
                return true;
        }
        return false;
    }

    private static boolean hasSpecialInheritance(QName styleName) {
        if (styleName.equals(TTML2StyleVerifier.fontSizeAttributeName))
            return true;
        else if (styleName.equals(TTML2StyleVerifier.lineHeightAttributeName))
            return true;
        else
            return false;
    }

    private static boolean isRubyTextContainer(Element elt, StyleSet sss) {
        return isRuby(elt, sss, "textContainer");
    }

    private static boolean isRubyText(Element elt, StyleSet sss) {
        return isRuby(elt, sss, "text");
    }

    private static boolean isRuby(Element elt, StyleSet sss, String ruby) {
        if (isSpanElement(elt)) {
            String r = null;
            if (elt.hasAttributeNS(TTMLHelper.NAMESPACE_TT_STYLE, "ruby"))
                r = elt.getAttributeNS(TTMLHelper.NAMESPACE_TT_STYLE, "ruby");
            if (r == null)
                r = getStyleValue(sss, TTML2StyleVerifier.rubyAttributeName);
            if (r != null) {
                if (r.equals(ruby))
                    return true;
            }
        }
        return false;
    }

    private static boolean isSpanElement(Element elt) {
        return isTimedTextElement(elt, "span");
    }

    private static boolean isTimedTextElement(Element elt, String localName) {
        if (elt != null) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri != null) && nsUri.equals(TTMLHelper.NAMESPACE_TT) && elt.getLocalName().equals(localName))
                return true;
        }
        return false;
    }

    @Override
    public StyleSpecification getSpecialInheritedStyle(Element elt, QName styleName, StyleSet sss, Map<Element, StyleSet> specifiedStyleSets, TransformerContext context) {
        if (isRubyText(elt, sss)) {
            Node n = elt.getParentNode();
            if (n instanceof Element) {
                Element p = (Element) n;
                sss = specifiedStyleSets.get(p);
                if (sss != null) {
                    if (isRubyTextContainer(p, sss))
                        return getStyle(sss, styleName);
                }
            }
        }
        return null;
    }

    private static String getStyleValue(StyleSet sss, QName styleName) {
        StyleSpecification s = getStyle(sss, styleName);
        if (s != null)
            return s.getValue();
        else
            return null;
    }

    private static StyleSpecification getStyle(StyleSet sss, QName styleName) {
        if (sss.containsKey(styleName))
            return sss.get(styleName);
        else
            return null;
    }

}
