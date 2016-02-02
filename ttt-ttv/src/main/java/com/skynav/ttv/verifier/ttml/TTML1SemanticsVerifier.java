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

package com.skynav.ttv.verifier.ttml;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.InputSource;
import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Head;
import com.skynav.ttv.model.ttml1.tt.Layout;
import com.skynav.ttv.model.ttml1.tt.Metadata;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Set;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.Styling;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttm.Actor;
import com.skynav.ttv.model.ttml1.ttm.Agent;
import com.skynav.ttv.model.ttml1.ttm.Copyright;
import com.skynav.ttv.model.ttml1.ttm.Description;
import com.skynav.ttv.model.ttml1.ttm.Name;
import com.skynav.ttv.model.ttml1.ttm.Title;
import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.model.ttml1.ttp.Profile;
import com.skynav.ttv.util.IOUtil;
import com.skynav.ttv.util.Locators;
import com.skynav.ttv.util.PreVisitor;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.Traverse;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttv.verifier.ItemVerifier.ItemType;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public class TTML1SemanticsVerifier implements SemanticsVerifier {

    private Model model;
    private VerifierContext context;
    protected MetadataVerifier metadataVerifier;
    protected ParameterVerifier parameterVerifier;
    protected ProfileVerifier profileVerifier;
    protected StyleVerifier styleVerifier;
    protected TimingVerifier timingVerifier;

    public TTML1SemanticsVerifier(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Object findBindingElement(Object root, Node node) {
        if (root instanceof TimedText)
            return findTimedTextBindingElement(root, node);
        else if (root instanceof Profile)
            return findProfileBindingElement(root, node);
        else
            return null;
    }

    public boolean verify(Object root, VerifierContext context) {
        setState(root, context);
        return verifyRoot(root);
    }

    protected boolean verifyRoot(Object root) {
        if (root instanceof TimedText)
            return verifyTimedText(root);
        else if (root instanceof Profile)
            return verifyProfile(root);
        else
            return unexpectedContent(root);
    }

    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verifyPostTransform(Object root, Object contentTransformed, VerifierContext context) {
        boolean failed = false;
        if (contentTransformed != null) {
            if (contentTransformed instanceof List) {
                List<?> isdDocuments = (List<?>) contentTransformed;
                Reporter reporter = context.getReporter();
                reporter.logInfo(reporter.message("*KEY*", "Verifying post-transform semantics phase {0} using ''{1}'' model...", 5, getModel().getName()));
                for (Object isd : isdDocuments) {
                    Document doc = readISD(isd);
                    if (doc != null) {
                        if (!verifyPostTransform(root, doc, context))
                            failed = true;
                    }
                }
            }
        }
        return !failed;
    }

    private Document readISD(Object isd) {
        if (isd instanceof File)
            return readISDAsFile((File) isd);
        else if (isd instanceof byte[])
            return readISDAsByteArray((byte[]) isd);
        else
            return null;
    }

    private Document readISDAsFile(File data) {
        FileInputStream fis = null;
        BufferedInputStream bis = null;
        try {
            fis = new FileInputStream(data);
            bis = new BufferedInputStream(fis);
            return readISDFromStream(bis);
        } catch (IOException e) {
            getContext().getReporter().logError(e);
            return null;
        } finally {
            IOUtil.closeSafely(bis);
            IOUtil.closeSafely(fis);
        }
    }

    private Document readISDAsByteArray(byte[] data) {
        ByteArrayInputStream bas = null;
        BufferedInputStream bis = null;
        try {
            bas = new ByteArrayInputStream(data);
            bis = new BufferedInputStream(bas);
            return readISDFromStream(bis);
        } finally {
            IOUtil.closeSafely(bis);
            IOUtil.closeSafely(bas);
        }
    }

    private Document readISDFromStream(InputStream is) {
        Reporter reporter = getContext().getReporter();
        try {
            SAXSource source = new SAXSource(new InputSource(is));
            DOMResult result = new DOMResult();
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            return (Document) result.getNode();
        } catch (TransformerFactoryConfigurationError e) {
            reporter.logError(new Exception(e));
        } catch (TransformerException e) {
            reporter.logError(e);
        }
        return null;
    }

    protected boolean verifyPostTransform(Object root, Document isd, VerifierContext context) {
        return true;
    }

    protected static List<Element> getISDRegionElements(Document doc) {
        final List<Element> regions = new java.util.ArrayList<Element>();
        try {
            Traverse.traverseElements(doc, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isISDRegionElement(elt))
                        regions.add(elt);
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return regions;
    }

    protected static Map<String,StyleSet> getISDStyleSets(Document doc) {
        final Map<String,StyleSet> styleSets = new java.util.HashMap<String,StyleSet>();
        try {
            Traverse.traverseElements(doc, new PreVisitor() {
                public boolean visit(Object content, Object parent, Visitor.Order order) {
                    assert content instanceof Element;
                    Element elt = (Element) content;
                    if (isISDCSSElement(elt)) {
                        StyleSet css = getISDStyleSet(elt);
                        if (css != null)
                            styleSets.put(css.getId(), css);
                    }
                    return true;
                }
            });
        } catch (Exception e) {
        }
        return styleSets;
    }

    private static StyleSet getISDStyleSet(Element elt) {
        Map<QName,String> attrs = Documents.getAttributes(elt);
        QName qnId = XML.getIdAttributeName();
        String id = attrs.get(qnId);
        if ((id != null) && !id.isEmpty()) {
            StyleSet css = new StyleSet(id);
            for (Map.Entry<QName,String> e : attrs.entrySet()) {
                QName qn = e.getKey();
                if (!qn.equals(qnId))
                    css.merge(qn, e.getValue());
            }
            return css;
        } else
            return null;
    }

    protected static boolean isISDRegionElement(Element elt) {
        return isISDElement(elt, "region");
    }

    protected static boolean isISDCSSElement(Element elt) {
        return isISDElement(elt, "css");
    }

    private static boolean isISDElement(Element elt, String localName) {
        if (elt != null) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(TTML1.Constants.NAMESPACE_TT_ISD))
                return false;
            else {
                if (elt.getLocalName().equals(localName))
                    return true;
                else
                    return false;
            }
        } else
            return false;
    }

    protected static boolean isTTParagraphElement(Element elt) {
        return isTTElement(elt, "p");
    }

    protected static boolean isTTSpanElement(Element elt) {
        return isTTElement(elt, "span");
    }

    private static boolean isTTElement(Element elt, String localName) {
        if (elt != null) {
            String nsUri = elt.getNamespaceURI();
            if ((nsUri == null) || !nsUri.equals(TTML1.Constants.NAMESPACE_TT))
                return false;
            else {
                if (elt.getLocalName().equals(localName))
                    return true;
                else
                    return false;
            }
        } else
            return false;
    }

    private void setState(Object root, VerifierContext context) {
        // passed state
        this.context = context;
        // derived state
        this.metadataVerifier = model.getMetadataVerifier();
        this.parameterVerifier = model.getParameterVerifier();
        this.profileVerifier = model.getProfileVerifier();
        this.styleVerifier = model.getStyleVerifier();
        this.timingVerifier = model.getTimingVerifier();
    }

    public VerifierContext getContext() {
        return context;
    }

    protected Locator getLocator(Object content) {
        Locator locator = null;
        while (content != null) {
            if ((locator = Locators.getLocator(content)) != null)
                break;
            else
                content = getLocatableParent(content);
        }
        return locator;
    }

    private Object getLocatableParent(Object content) {
        if (content instanceof Element) {
            Node n = ((Element) content).getParentNode();
            return (n instanceof Element) ? n : null;
        } else if (content instanceof JAXBElement<?>) {
            return context.getBindingElementParent(((JAXBElement<?>)content).getValue());
        } else
            return context.getBindingElementParent(content);
    }

    protected boolean verifyTimedText(Object tt) {
        boolean failed = false;
        if (!verifyTimedTextParameterAttributes(tt))
            failed = true;
        if (!verifyStyleAttributes(tt))
            failed = true;
        if (!verifyTimingAttributes(tt))
            failed = true;
        if (!verifyOtherAttributes(tt))
            failed = true;
        Object head = getTimedTextHead(tt);
        if (head != null) {
            if (!verifyHead(head, tt))
                failed = true;
        }
        Object body = getTimedTextBody(tt);
        if (body != null) {
            if (!verifyBody(body))
                failed = true;
        }
        return !failed;
    }

    protected Object getTimedTextHead(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getHead();
    }

    protected Object getTimedTextBody(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getBody();
    }

    protected boolean verifyHead(Object head, Object tt) {
        boolean failed = false;
        if (!verifyOtherAttributes(head))
            failed = true;
        for (Object m : getHeadMetadata(head)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        Object styling  = getHeadStyling(head);
        if (styling != null) {
            if (!verifyStyling(styling))
                failed = true;
        }
        Object layout  = getHeadLayout(head);
        if (layout != null) {
            if (!verifyLayout(layout))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getHeadMetadata(Object head) {
        assert head instanceof Head;
        return ((Head) head).getMetadataClass();
    }

    protected Object getHeadStyling(Object head) {
        assert head instanceof Head;
        return ((Head) head).getStyling();
    }

    protected Object getHeadLayout(Object head) {
        assert head instanceof Head;
        return ((Head) head).getLayout();
    }

    protected boolean verifyStyling(Object styling) {
        boolean failed = false;
        if (!verifyOtherAttributes(styling))
            failed = true;
        for (Object m : getStylingMetadata(styling)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object s : getStylingStyles(styling)) {
            if (!verifyStyle(s))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getStylingMetadata(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getMetadataClass();
    }

    protected Collection<? extends Object> getStylingStyles(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getStyle();
    }

    protected boolean verifyStyle(Object style) {
        boolean failed = false;
        if (!verifyStyleAttributes(style))
            failed = true;
        if (!verifyOtherAttributes(style))
            failed = true;
        return !failed;
    }

    protected boolean verifyLayout(Object layout) {
        boolean failed = false;
        if (!verifyOtherAttributes(layout))
            failed = true;
        for (Object m : getLayoutMetadata(layout)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object r : getLayoutRegions(layout)) {
            if (!verifyRegion(r))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getLayoutMetadata(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getMetadataClass();
    }

    protected Collection<? extends Object> getLayoutRegions(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getRegion();
    }

    protected boolean verifyRegion(Object region) {
        boolean failed = false;
        if (!verifyStyleAttributes(region))
            failed = true;
        if (!verifyTimingAttributes(region))
            failed = true;
        if (!verifyOtherAttributes(region))
            failed = true;
        for (Object m : getRegionMetadata(region)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object a : getRegionAnimations(region)) {
            if (!verifyAnimation(a))
                failed = true;
        }
        for (Object s : getRegionStyles(region)) {
            if (!verifyStyle(s))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getRegionMetadata(Object region) {
        assert region instanceof Region;
        return ((Region) region).getMetadataClass();
    }

    protected Collection<? extends Object> getRegionAnimations(Object region) {
        assert region instanceof Region;
        return ((Region) region).getAnimationClass();
    }

    protected Collection<? extends Object> getRegionStyles(Object region) {
        assert region instanceof Region;
        return ((Region) region).getStyle();
    }

    protected boolean verifyBody(Object body) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, body))
            failed = true;
        if (!verifyStyleAttributes(body))
            failed = true;
        if (!verifyTimingAttributes(body))
            failed = true;
        if (!verifyOtherAttributes(body))
            failed = true;
        for (Object m : getBodyMetadata(body)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object a : getBodyAnimations(body)) {
            if (!verifyAnimation(a))
                failed = true;
        }
        for (Object d : getBodyDivisions(body)) {
            if (!verifyDivision(d))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getBodyMetadata(Object body) {
        assert body instanceof Body;
        return ((Body) body).getMetadataClass();
    }

    protected Collection<? extends Object> getBodyAnimations(Object body) {
        assert body instanceof Body;
        return ((Body) body).getAnimationClass();
    }

    protected Collection<? extends Object> getBodyDivisions(Object body) {
        assert body instanceof Body;
        return ((Body) body).getDiv();
    }

    protected boolean verifyDivision(Object division) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, division))
            failed = true;
        if (!verifyStyleAttributes(division))
            failed = true;
        if (!verifyTimingAttributes(division))
            failed = true;
        if (!verifyOtherAttributes(division))
            failed = true;
        for (Object m : getDivisionMetadata(division)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object a : getDivisionAnimations(division)) {
            if (!verifyAnimation(a))
                failed = true;
        }
        for (Object b : getDivisionBlocks(division)) {
            if (!verifyBlock(b))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getDivisionMetadata(Object division) {
        assert division instanceof Division;
        return ((Division) division).getMetadataClass();
    }

    protected Collection<? extends Object> getDivisionAnimations(Object division) {
        assert division instanceof Division;
        return ((Division) division).getAnimationClass();
    }

    protected Collection<? extends Object> getDivisionBlocks(Object division) {
        assert division instanceof Division;
        return ((Division) division).getBlockClass();
    }

    protected boolean verifyParagraph(Object paragraph) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, paragraph))
            failed = true;
        if (!verifyStyleAttributes(paragraph))
            failed = true;
        if (!verifyTimingAttributes(paragraph))
            failed = true;
        if (!verifyOtherAttributes(paragraph))
            failed = true;
        for (Serializable s : getParagraphContent(paragraph)) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    protected Collection<Serializable> getParagraphContent(Object paragraph) {
        assert paragraph instanceof Paragraph;
        return ((Paragraph) paragraph).getContent();
    }

    protected boolean verifySpan(Object span) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, span))
            failed = true;
        if (!verifyStyleAttributes(span))
            failed = true;
        if (!verifyTimingAttributes(span))
            failed = true;
        if (!verifyOtherAttributes(span))
            failed = true;
        for (Serializable s : getSpanContent(span)) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    protected Collection<Serializable> getSpanContent(Object span) {
        assert span instanceof Span;
        return ((Span) span).getContent();
    }

    protected boolean verifyBreak(Object br) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, br))
            failed = true;
        if (!verifyStyleAttributes(br))
            failed = true;
        if (!verifyOtherAttributes(br))
            failed = true;
        for (Object m : getBreakMetadata(br)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object a : getBreakAnimations(br)) {
            if (!verifyAnimation(a))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getBreakMetadata(Object br) {
        assert br instanceof Break;
        return ((Break) br).getMetadataClass();
    }

    protected Collection<? extends Object> getBreakAnimations(Object br) {
        assert br instanceof Break;
        return ((Break) br).getAnimationClass();
    }

    protected boolean verifyAnimation(Object animation) {
        if (animation instanceof Set)
            return verifySet(animation);
        else
            return unexpectedContent(animation);
    }

    protected boolean verifySet(Object set) {
        boolean failed = false;
        if (!verifyStyleAttributes(set))
            failed = true;
        if (!verifyTimingAttributes(set))
            failed = true;
        if (!verifyOtherAttributes(set))
            failed = true;
        for (Object m : getSetMetadata(set)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        if (!verifyStyledItem(set))
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getSetMetadata(Object set) {
        assert set instanceof Set;
        return ((Set) set).getMetadataClass();
    }

    protected boolean verifyProfile(Object profile) {
        boolean failed = false;
        if (!verifyParameterAttributes(profile))
            failed = true;
        if (!verifyOtherAttributes(profile))
            failed = true;
        for (Object m : getProfileMetadata(profile)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object features : getProfileFeatures(profile)) {
            if (!verifyFeatures(features))
                failed = true;
        }
        for (Object extensions : getProfileExtensions(profile)) {
            if (!verifyExtensions(extensions))
                failed = true;
        }
        if (!verifyProfileItem(profile))
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getProfileMetadata(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getMetadataClass();
    }

    protected Collection<? extends Object> getProfileFeatures(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getFeatures();
    }

    protected Collection<? extends Object> getProfileExtensions(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getExtensions();
    }

    protected boolean verifyFeatures(Object features) {
        boolean failed = false;
        if (!verifyParameterAttributes(features))
            failed = true;
        if (!verifyOtherAttributes(features))
            failed = true;
        for (Object m : getFeaturesMetadata(features)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object feature : getFeaturesFeatures(features)) {
            if (!verifyFeature(feature))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getFeaturesMetadata(Object features) {
        assert features instanceof Features;
        return ((Features) features).getMetadataClass();
    }

    protected Collection<? extends Object> getFeaturesFeatures(Object features) {
        assert features instanceof Features;
        return ((Features) features).getFeature();
    }

    protected boolean verifyFeature(Object feature) {
        boolean failed = false;
        if (!verifyOtherAttributes(feature))
            failed = true;
        if (!verifyProfileItem(feature))
            failed = true;
        return !failed;
    }

    protected boolean verifyExtensions(Object extensions) {
        boolean failed = false;
        if (!verifyParameterAttributes(extensions))
            failed = true;
        if (!verifyOtherAttributes(extensions))
            failed = true;
        for (Object m : getExtensionsMetadata(extensions)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object extension : getExtensionsExtensions(extensions)) {
            if (!verifyExtension(extension))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getExtensionsMetadata(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getMetadataClass();
    }

    protected Collection<? extends Object> getExtensionsExtensions(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getExtension();
    }

    protected boolean verifyExtension(Object extension) {
        boolean failed = false;
        if (!verifyOtherAttributes(extension))
            failed = true;
        if (!verifyProfileItem(extension))
            failed = true;
        return !failed;
    }

    private boolean verifyProfileItem(Object content) {
        return this.profileVerifier.verify(content, getLocator(content), this.context, ItemType.Element);
    }

    protected boolean verifyActor(Object actor) {
        boolean failed = false;
        if (!verifyOtherAttributes(actor))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, actor))
            failed = true;
        return !failed;
    }

    protected boolean verifyAgent(Object agent) {
        boolean failed = false;
        if (!verifyOtherAttributes(agent))
            failed = true;
        for (Object name : getAgentNames(agent)) {
            if (!verifyName(name))
                failed = true;
        }
        Object actor = getAgentActor(agent);
        if (actor != null ) {
            if (!verifyActor(actor))
                failed = true;
        }
        if (!verifyMetadataItem(metadataVerifier, agent))
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getAgentNames(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getName();
    }

    protected Object getAgentActor(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getActor();
    }

    protected boolean verifyCopyright(Object copyright) {
        boolean failed = false;
        if (!verifyOtherAttributes(copyright))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, copyright))
            failed = true;
        return !failed;
    }

    protected boolean verifyDescription(Object description) {
        boolean failed = false;
        if (!verifyOtherAttributes(description))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, description))
            failed = true;
        return !failed;
    }

    protected boolean verifyMetadata(Object metadata) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadataVerifier, metadata))
            failed = true;
        if (!verifyOtherAttributes(metadata))
            failed = true;
        for (Object content : getMetadataAny(metadata)) {
            if (isMetadataItem(content)) {
                if (!verifyMetadataItem(content))
                    failed = true;
            } else {
                if (content instanceof JAXBElement<?>)
                    content = ((JAXBElement<?>)content).getValue();
                failed = !verifyNonTTOtherElement(content, getLocator(content), this.context);
            }
        }
        if (!verifyMetadataItem(metadataVerifier, metadata))
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getMetadataAny(Object metadata) {
        assert metadata instanceof Metadata;
        return ((Metadata) metadata).getAny();
    }

    protected boolean verifyName(Object name) {
        boolean failed = false;
        if (!verifyOtherAttributes(name))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, name))
            failed = true;
        return !failed;
    }

    protected boolean verifyTitle(Object title) {
        boolean failed = false;
        if (!verifyOtherAttributes(title))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, title))
            failed = true;
        return !failed;
    }

    protected boolean verifyForeignMetadata(Element metadata) {
        boolean failed = false;
        return !failed;
    }

    protected boolean verifyMetadataItem(Object metadata) {
        if (metadata instanceof JAXBElement<?>)
            return verifyMetadataItem(((JAXBElement<?>)metadata).getValue());
        else if (metadata instanceof Actor)
            return verifyActor(metadata);
        else if (metadata instanceof Agent)
            return verifyAgent(metadata);
        else if (metadata instanceof Copyright)
            return verifyCopyright(metadata);
        else if (metadata instanceof Description)
            return verifyDescription(metadata);
        else if (metadata instanceof Metadata)
            return verifyMetadata(metadata);
        else if (metadata instanceof Name)
            return verifyName(metadata);
        else if (metadata instanceof Title)
            return verifyTitle(metadata);
        else if (metadata instanceof Element)
            return verifyForeignMetadata((Element)metadata);
        else
            return unexpectedContent(metadata);
    }

    protected boolean verifyBlock(Object block) {
        if (block instanceof Division)
            return verifyDivision(block);
        else if (block instanceof Paragraph)
            return verifyParagraph(block);
        else
            return unexpectedContent(block);
    }

    protected boolean verifyContent(Serializable content) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadataItem(element))
                return verifyMetadata(element);
            else if (element instanceof Set)
                return verifySet(element);
            else if (element instanceof Span)
                return verifySpan(element);
            else if (element instanceof Break)
                return verifyBreak(element);
            else
                return unexpectedContent(element);
        } else if (content instanceof String) {
            return true;
        } else
            return unexpectedContent(content);
    }

    protected boolean verifyMetadataAttributes(MetadataVerifier verifier, Object content) {
        return verifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    protected boolean verifyMetadataItem(MetadataVerifier verifier, Object content) {
        return verifier.verify(content, getLocator(content), this.context, ItemType.Element);
    }

    private boolean verifyTimedTextParameterAttributes(Object tt) {
        boolean failed = false;
        if (!this.parameterVerifier.verify(tt, getLocator(tt), this.context, ItemType.Attributes))
            failed = true;
        Object head = getTimedTextHead(tt);
        if (head != null) {
            if (!verifyHeadParameterAttributes(head))
                failed = true;
        }
        if (!failed)
            failed = !this.profileVerifier.verify(tt, getLocator(tt), this.context, ItemType.Element);
        return !failed;
    }

    private boolean verifyHeadParameterAttributes(Object head) {
        boolean failed = false;
        for (Object p : getHeadParameters(head)) {
            if (!verifyProfile(p))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getHeadParameters(Object head) {
        assert head instanceof Head;
        return ((Head) head).getParametersClass();
    }

    protected boolean verifyParameterAttributes(Object content) {
        return this.parameterVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    protected boolean verifyStyleAttributes(Object content) {
        return this.styleVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    protected boolean verifyStyledItem(Object content) {
        return this.styleVerifier.verify(content, getLocator(content), this.context, ItemType.Element);
    }

    protected boolean verifyTimingAttributes(Object content) {
        return this.timingVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    protected boolean verifyOtherAttributes(Object content) {
        boolean failed = false;
        if (!metadataVerifier.verify(content, getLocator(content), this.context, ItemType.Other))
            failed = true;
        if (!parameterVerifier.verify(content, getLocator(content), this.context, ItemType.Other))
            failed = true;
        if (!styleVerifier.verify(content, getLocator(content), this.context, ItemType.Other))
            failed = true;
        if (!verifyNonTTOtherAttributes(content, getLocator(content), this.context))
            failed = true;
        return !failed;
    }

    protected Object findTimedTextBindingElement(Object tt, Node node) {
        if (context.getXMLNode(tt) == node)
            return tt;
        else {
            Object head = getTimedTextHead(tt);
            if (head != null) {
                Object content = findHeadBindingElement(head, node);
                if (content != null)
                    return content;
            }
            Object body = getTimedTextBody(tt);
            if (body != null) {
                Object content = findBodyBindingElement(body, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findHeadBindingElement(Object head, Node node) {
        if (context.getXMLNode(head) == node)
            return head;
        else {
            for (Object m : getHeadMetadata(head)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            Object styling = getHeadStyling(head);
            if (styling != null) {
                Object content = findStylingBindingElement(styling, node);
                if (content != null)
                    return content;
            }
            Object layout = getHeadLayout(head);
            if (layout != null) {
                Object content = findLayoutBindingElement(layout, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findStylingBindingElement(Object styling, Node node) {
        if (context.getXMLNode(styling) == node)
            return styling;
        else {
            for (Object m : getStylingMetadata(styling)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object s : getStylingStyles(styling)) {
                Object content = findStyleBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findStyleBindingElement(Object style, Node node) {
        if (context.getXMLNode(style) == node)
            return style;
        else
            return null;
    }

    protected Object findLayoutBindingElement(Object layout, Node node) {
        if (context.getXMLNode(layout) == node)
            return layout;
        else {
            for (Object m : getLayoutMetadata(layout)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object r : getLayoutRegions(layout)) {
                Object content = findRegionBindingElement(r, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findRegionBindingElement(Object region, Node node) {
        if (context.getXMLNode(region) == node)
            return region;
        else {
            for (Object m : getRegionMetadata(region)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object a : getRegionAnimations(region)) {
                Object content = findAnimationBindingElement(a, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findBodyBindingElement(Object body, Node node) {
        if (context.getXMLNode(body) == node)
            return body;
        else {
            for (Object m : getBodyMetadata(body)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object a : getBodyAnimations(body)) {
                Object content = findAnimationBindingElement(a, node);
                if (content != null)
                    return content;
            }
            for (Object d : getBodyDivisions(body)) {
                Object content = findDivisionBindingElement(d, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findDivisionBindingElement(Object division, Node node) {
        if (context.getXMLNode(division) == node)
            return division;
        else {
            for (Object m : getDivisionMetadata(division)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object a : getDivisionAnimations(division)) {
                Object content = findAnimationBindingElement(a, node);
                if (content != null)
                    return content;
            }
            for (Object b : getDivisionBlocks(division)) {
                Object content = findBlockBindingElement(b, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findParagraphBindingElement(Object paragraph, Node node) {
        if (context.getXMLNode(paragraph) == node)
            return paragraph;
        else {
            for (Serializable s : getParagraphContent(paragraph)) {
                Object content = findContentBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findSpanBindingElement(Object span, Node node) {
        if (context.getXMLNode(span) == node)
            return span;
        else {
            for (Serializable s : getSpanContent(span)) {
                Object content = findContentBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findBreakBindingElement(Object br, Node node) {
        if (context.getXMLNode(br) == node)
            return br;
        else {
            for (Object m : getBreakMetadata(br)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object a : getBreakAnimations(br)) {
                Object content = findAnimationBindingElement(a, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findActorBindingElement(Object actor, Node node) {
        if (context.getXMLNode(actor) == node)
            return actor;
        else
            return null;
    }

    protected Object findAgentBindingElement(Object agent, Node node) {
        if (context.getXMLNode(agent) == node)
            return agent;
        else {
            for (Object name : getAgentNames(agent)) {
                Object content = findNameBindingElement(name, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findCopyrightBindingElement(Object copyright, Node node) {
        if (context.getXMLNode(copyright) == node)
            return copyright;
        else
            return null;
    }

    protected Object findDescriptionBindingElement(Object description, Node node) {
        if (context.getXMLNode(description) == node)
            return description;
        else
            return null;
    }

    protected Object findMetadataBindingElement(Object metadata, Node node) {
        if (context.getXMLNode(metadata) == node)
            return metadata;
        else {
            for (Object m : getMetadataAny(metadata)) {
                if (!isMetadataItem(m)) {
                    Object content = findMetadataItemBindingElement(m, node);
                    if (content != null)
                        return content;
                }
            }
            return null;
        }
    }

    protected Object findNameBindingElement(Object name, Node node) {
        if (context.getXMLNode(name) == node)
            return name;
        else
            return null;
    }

    protected Object findTitleBindingElement(Object title, Node node) {
        if (context.getXMLNode(title) == node)
            return title;
        else
            return null;
    }

    protected Object findMetadataItemBindingElement(Object metadata, Node node) {
        if (metadata instanceof JAXBElement<?>)
            return findMetadataItemBindingElement(((JAXBElement<?>)metadata).getValue(), node);
        else if (context.getXMLNode(metadata) == node)
            return metadata;
        else if (metadata instanceof Actor)
            return findActorBindingElement(metadata, node);
        else if (metadata instanceof Agent)
            return findAgentBindingElement(metadata, node);
        else if (metadata instanceof Copyright)
            return findCopyrightBindingElement(metadata, node);
        else if (metadata instanceof Description)
            return findDescriptionBindingElement(metadata, node);
        else if (metadata instanceof Metadata)
            return findMetadataBindingElement(metadata, node);
        else if (metadata instanceof Name)
            return findNameBindingElement(metadata, node);
        else if (metadata instanceof Title)
            return findTitleBindingElement(metadata, node);
        else if (metadata instanceof Element)
            return findForeignMetadataBindingElement((Element)metadata, node);
        else
            return null;
    }

    protected Object findForeignMetadataBindingElement(Element metadata, Node node) {
        if (context.getXMLNode(metadata) == node)
            return metadata;
        else
            return null;
    }

    protected Object findBlockBindingElement(Object block, Node node) {
        if (context.getXMLNode(block) == node)
            return block;
        else if (block instanceof Division)
            return findBindingElement((Division) block, node);
        else if (block instanceof Paragraph)
            return findBindingElement((Paragraph) block, node);
        else
            return null;
    }

    protected Object findContentBindingElement(Serializable content, Node node) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (context.getXMLNode(element) == node)
                return element;
            else if (isMetadataItem(element))
                return findMetadataItemBindingElement(element, node);
            else if (element instanceof Set)
                return findBindingElement((Set) element, node);
            else if (element instanceof Span)
                return findBindingElement((Span) element, node);
            else if (element instanceof Break)
                return findBindingElement((Break) element, node);
            else
                return null;
        } else
            return null;
    }

    protected Object findAnimationBindingElement(Object animation, Node node) {
        if (animation instanceof Set)
            return findSetBindingElement(animation, node);
        else
            return null;
    }

    protected Object findSetBindingElement(Object set, Node node) {
        if (context.getXMLNode(set) == node)
            return set;
        else {
            for (Object m : getSetMetadata(set)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findProfileBindingElement(Object profile, Node node) {
        if (context.getXMLNode(profile) == node)
            return profile;
        else {
            for (Object m : getProfileMetadata(profile)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object features : getProfileFeatures(profile)) {
                Object content = findFeaturesBindingElement(features, node);
                if (content != null)
                    return content;
            }
            for (Object extensions : getProfileExtensions(profile)) {
                Object content = findExtensionsBindingElement(extensions, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findFeaturesBindingElement(Object features, Node node) {
        if (context.getXMLNode(features) == node)
            return features;
        else {
            for (Object m : getFeaturesMetadata(features)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object feature : getFeaturesFeatures(features)) {
                Object content = findFeatureBindingElement(feature, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findFeatureBindingElement(Object feature, Node node) {
        if (context.getXMLNode(feature) == node)
            return feature;
        else
            return null;
    }

    protected Object findExtensionsBindingElement(Object extensions, Node node) {
        if (context.getXMLNode(extensions) == node)
            return extensions;
        else {
            for (Object m : getExtensionsMetadata(extensions)) {
                Object content = findMetadataItemBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Object extension : getExtensionsExtensions(extensions)) {
                Object content = findExtensionBindingElement(extension, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    protected Object findExtensionBindingElement(Object extension, Node node) {
        if (context.getXMLNode(extension) == node)
            return extension;
        else
            return null;
    }

    protected boolean unexpectedContent(Object content) throws IllegalStateException {
        throw new IllegalStateException("Unexpected JAXB content object of type '" + content.getClass().getName() +  "'.");
    }

    protected boolean isMetadataItem(Object element) {
        if (element instanceof Agent)
            return true;
        else if (element instanceof Copyright)
            return true;
        else if (element instanceof Description)
            return true;
        else if (element instanceof Metadata)
            return true;
        else if (element instanceof Title)
            return true;
        else
            return false;
    }

}
