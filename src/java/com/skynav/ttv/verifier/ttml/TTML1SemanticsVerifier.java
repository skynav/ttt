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

import java.io.Serializable;

import javax.xml.bind.JAXBElement;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
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
import com.skynav.ttv.model.ttml1.tt.Style;
import com.skynav.ttv.model.ttml1.tt.Styling;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttp.Extension;
import com.skynav.ttv.model.ttml1.ttp.Extensions;
import com.skynav.ttv.model.ttml1.ttp.Feature;
import com.skynav.ttv.model.ttml1.ttp.Features;
import com.skynav.ttv.model.ttml1.ttp.Profile;
import com.skynav.ttv.model.ttml1.ttm.Actor;
import com.skynav.ttv.model.ttml1.ttm.Agent;
import com.skynav.ttv.model.ttml1.ttm.Copyright;
import com.skynav.ttv.model.ttml1.ttm.Description;
import com.skynav.ttv.model.ttml1.ttm.Name;
import com.skynav.ttv.model.ttml1.ttm.Title;
import com.skynav.ttv.util.Locators;
import com.skynav.ttv.verifier.ItemVerifier.ItemType;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class TTML1SemanticsVerifier implements SemanticsVerifier {

    private Model model;
    private VerifierContext context;
    private MetadataVerifier metadataVerifier;
    private ParameterVerifier parameterVerifier;
    private ProfileVerifier profileVerifier;
    private StyleVerifier styleVerifier;
    private TimingVerifier timingVerifier;

    public TTML1SemanticsVerifier(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    public Object findBindingElement(Object root, Node node) {
        if (root instanceof TimedText)
            return findBindingElement((TimedText)root, node);
        else if (root instanceof Profile)
            return findBindingElement((Profile)root, node);
        else
            return null;
    }

    public boolean verify(Object root, VerifierContext context) {
        setState(root, context);
        if (root instanceof TimedText)
            return verify((TimedText)root);
        else if (root instanceof Profile)
            return verify((Profile)root);
        else
            return unexpectedContent(root);
    }

    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        return true;
    }

    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        return true;
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

    private Locator getLocator(Object content) {
        return Locators.getLocator(content);
    }

    private boolean verify(TimedText tt) {
        boolean failed = false;
        if (!verifyParameterAttributes(tt))
            failed = true;
        if (!verifyStyleAttributes(tt))
            failed = true;
        if (!verifyTimingAttributes(tt))
            failed = true;
        if (!verifyOtherAttributes(tt))
            failed = true;
        Head head = tt.getHead();
        if (head != null) {
            if (!verify(head, tt))
                failed = true;
        }
        Body body = tt.getBody();
        if (body != null) {
            if (!verify(body))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Head head, TimedText tt) {
        boolean failed = false;
        if (!verifyOtherAttributes(head))
            failed = true;
        for (Object m : head.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        Styling styling  = head.getStyling();
        if (styling != null) {
            if (!verify(styling))
                failed = true;
        }
        Layout layout  = head.getLayout();
        if (layout != null) {
            if (!verify(layout))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Styling styling) {
        boolean failed = false;
        if (!verifyOtherAttributes(styling))
            failed = true;
        for (Object m : styling.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Style s : styling.getStyle()) {
            if (!verify(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Style style) {
        boolean failed = false;
        if (!verifyStyleAttributes(style))
            failed = true;
        if (!verifyOtherAttributes(style))
            failed = true;
        return !failed;
    }

    private boolean verify(Layout layout) {
        boolean failed = false;
        if (!verifyOtherAttributes(layout))
            failed = true;
        for (Object m : layout.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Region r : layout.getRegion()) {
            if (!verify(r))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Region region) {
        boolean failed = false;
        if (!verifyStyleAttributes(region))
            failed = true;
        if (!verifyTimingAttributes(region))
            failed = true;
        if (!verifyOtherAttributes(region))
            failed = true;
        for (Object m : region.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set a : region.getAnimationClass()) {
            if (!verify(a))
                failed = true;
        }
        for (Style s : region.getStyle()) {
            if (!verify(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Body body) {
        boolean failed = false;
        if (!verifyMetadataAttributes(body))
            failed = true;
        if (!verifyStyleAttributes(body))
            failed = true;
        if (!verifyTimingAttributes(body))
            failed = true;
        if (!verifyOtherAttributes(body))
            failed = true;
        for (Object m : body.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set a : body.getAnimationClass()) {
            if (!verify(a))
                failed = true;
        }
        for (Division d : body.getDiv()) {
            if (!verify(d))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Division division) {
        boolean failed = false;
        if (!verifyMetadataAttributes(division))
            failed = true;
        if (!verifyStyleAttributes(division))
            failed = true;
        if (!verifyTimingAttributes(division))
            failed = true;
        if (!verifyOtherAttributes(division))
            failed = true;
        for (Object m : division.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set a : division.getAnimationClass()) {
            if (!verify(a))
                failed = true;
        }
        for (Object b : division.getBlockClass()) {
            if (!verifyBlock(b))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Paragraph paragraph) {
        boolean failed = false;
        if (!verifyMetadataAttributes(paragraph))
            failed = true;
        if (!verifyStyleAttributes(paragraph))
            failed = true;
        if (!verifyTimingAttributes(paragraph))
            failed = true;
        if (!verifyOtherAttributes(paragraph))
            failed = true;
        for (Serializable s : paragraph.getContent()) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Span span) {
        boolean failed = false;
        if (!verifyMetadataAttributes(span))
            failed = true;
        if (!verifyStyleAttributes(span))
            failed = true;
        if (!verifyTimingAttributes(span))
            failed = true;
        if (!verifyOtherAttributes(span))
            failed = true;
        for (Serializable s : span.getContent()) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Break br) {
        boolean failed = false;
        if (!verifyMetadataAttributes(br))
            failed = true;
        if (!verifyStyleAttributes(br))
            failed = true;
        if (!verifyOtherAttributes(br))
            failed = true;
        for (Object m : br.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set a : br.getAnimationClass()) {
            if (!verify(a))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Set set) {
        boolean failed = false;
        if (!verifyStyleAttributes(set))
            failed = true;
        if (!verifyTimingAttributes(set))
            failed = true;
        if (!verifyOtherAttributes(set))
            failed = true;
        for (Object m : set.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        if (!verifyStyledItem(set))
            failed = true;
        return !failed;
    }

    private boolean verify(Profile profile) {
        boolean failed = false;
        if (!verifyParameterAttributes(profile))
            failed = true;
        if (!verifyOtherAttributes(profile))
            failed = true;
        for (Object m : profile.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Features features : profile.getFeatures()) {
            if (!verify(features))
                failed = true;
        }
        for (Extensions extensions: profile.getExtensions()) {
            if (!verify(extensions))
                failed = true;
        }
        if (!verifyProfileItem(profile))
            failed = true;
        return !failed;
    }

    private boolean verify(Features features) {
        boolean failed = false;
        if (!verifyParameterAttributes(features))
            failed = true;
        if (!verifyOtherAttributes(features))
            failed = true;
        for (Object m : features.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Feature feature : features.getFeature()) {
            if (!verify(feature))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Feature feature) {
        boolean failed = false;
        if (!verifyOtherAttributes(feature))
            failed = true;
        if (!verifyProfileItem(feature))
            failed = true;
        return !failed;
    }

    private boolean verify(Extensions extensions) {
        boolean failed = false;
        if (!verifyParameterAttributes(extensions))
            failed = true;
        if (!verifyOtherAttributes(extensions))
            failed = true;
        for (Object m : extensions.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Extension extension : extensions.getExtension()) {
            if (!verify(extension))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Extension extension) {
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

    private boolean verify(Actor actor) {
        boolean failed = false;
        if (!verifyOtherAttributes(actor))
            failed = true;
        if (!verifyMetadataItem(actor))
            failed = true;
        return !failed;
    }

    private boolean verify(Agent agent) {
        boolean failed = false;
        if (!verifyOtherAttributes(agent))
            failed = true;
        for (Name name : agent.getName()) {
            if (!verify(name))
                failed = true;
        }
        Actor actor = agent.getActor();
        if (actor != null ) {
            if (!verify(actor))
                failed = true;
        }
        if (!verifyMetadataItem(agent))
            failed = true;
        return !failed;
    }

    private boolean verify(Copyright copyright) {
        boolean failed = false;
        if (!verifyOtherAttributes(copyright))
            failed = true;
        if (!verifyMetadataItem(copyright))
            failed = true;
        return !failed;
    }

    private boolean verify(Description description) {
        boolean failed = false;
        if (!verifyOtherAttributes(description))
            failed = true;
        if (!verifyMetadataItem(description))
            failed = true;
        return !failed;
    }

    private boolean verify(Metadata metadata) {
        boolean failed = false;
        if (!verifyMetadataAttributes(metadata))
            failed = true;
        if (!verifyOtherAttributes(metadata))
            failed = true;
        for (Object content : metadata.getAny()) {
            if (isMetadataItem(content)) {
                if (!verifyMetadata(content))
                    failed = true;
            } else {
                if (content instanceof JAXBElement<?>)
                    content = ((JAXBElement<?>)content).getValue();
                failed = !verifyNonTTOtherElement(content, getLocator(content), this.context);
            }
        }
        if (!verifyMetadataItem(metadata))
            failed = true;
        return !failed;
    }

    private boolean verify(Name name) {
        boolean failed = false;
        if (!verifyOtherAttributes(name))
            failed = true;
        if (!verifyMetadataItem(name))
            failed = true;
        return !failed;
    }

    private boolean verify(Title title) {
        boolean failed = false;
        if (!verifyOtherAttributes(title))
            failed = true;
        if (!verifyMetadataItem(title))
            failed = true;
        return !failed;
    }

    private boolean verifyForeignMetadata(Element metadata) {
        boolean failed = false;
        return !failed;
    }

    private boolean verifyMetadata(Object metadata) {
        if (metadata instanceof JAXBElement<?>)
            return verifyMetadata(((JAXBElement<?>)metadata).getValue());
        else if (metadata instanceof Actor)
            return verify((Actor)metadata);
        else if (metadata instanceof Agent)
            return verify((Agent)metadata);
        else if (metadata instanceof Copyright)
            return verify((Copyright)metadata);
        else if (metadata instanceof Description)
            return verify((Description)metadata);
        else if (metadata instanceof Metadata)
            return verify((Metadata)metadata);
        else if (metadata instanceof Name)
            return verify((Name)metadata);
        else if (metadata instanceof Title)
            return verify((Title)metadata);
        else if (metadata instanceof Element)
            return verifyForeignMetadata((Element)metadata);
        else
            return unexpectedContent(metadata);
    }

    private boolean verifyBlock(Object block) {
        if (block instanceof Division)
            return verify((Division) block);
        else if (block instanceof Paragraph)
            return verify((Paragraph) block);
        else
            return unexpectedContent(block);
    }

    private boolean verifyContent(Serializable content) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadataItem(element))
                return verifyMetadata(element);
            else if (element instanceof Set)
                return verify((Set) element);
            else if (element instanceof Span)
                return verify((Span) element);
            else if (element instanceof Break)
                return verify((Break) element);
            else
                return unexpectedContent(element);
        } else if (content instanceof String) {
            return true;
        } else
            return unexpectedContent(content);
    }

    private boolean verifyMetadataAttributes(Object content) {
        return this.metadataVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    private boolean verifyMetadataItem(Object content) {
        return this.metadataVerifier.verify(content, getLocator(content), this.context, ItemType.Element);
    }

    private boolean verifyParameterAttributes(TimedText tt) {
        boolean failed = false;
        if (!this.parameterVerifier.verify(tt, getLocator(tt), this.context, ItemType.Attributes))
            failed = true;
        Head head = tt.getHead();
        if (head != null) {
            if (!verifyParameterAttributes(head))
                failed = true;
        }
        if (!failed)
            failed = !this.profileVerifier.verify(tt, getLocator(tt), this.context, ItemType.Element);
        return !failed;
    }

    private boolean verifyParameterAttributes(Head head) {
        boolean failed = false;
        for (Profile p : head.getParametersClass()) {
            if (!verify(p))
                failed = true;
        }
        return !failed;
    }

    private boolean verifyParameterAttributes(Object content) {
        return this.parameterVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    private boolean verifyStyleAttributes(TimedText tt) {
        boolean failed = false;
        if (!verifyStyleAttributes((Object) tt))
            failed = true;
        return !failed;
    }

    private boolean verifyStyleAttributes(Object content) {
        return this.styleVerifier.verify(content, getLocator(content), this.context, ItemType.Attributes);
    }

    private boolean verifyStyledItem(Object content) {
        return this.styleVerifier.verify(content, getLocator(content), this.context, ItemType.Element);
    }

    private boolean verifyTimingAttributes(Object content) {
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

    private Object findBindingElement(TimedText root, Node node) {
        if (context.getXMLNode(root) == node)
            return root;
        else {
            Head head = root.getHead();
            if (head != null) {
                Object content = findBindingElement(head, node);
                if (content != null)
                    return content;
            }
            Body body = root.getBody();
            if (body != null) {
                Object content = findBindingElement(body, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Head head, Node node) {
        if (context.getXMLNode(head) == node)
            return head;
        else {
            for (Object m : head.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            Styling styling = head.getStyling();
            if (styling != null) {
                Object content = findBindingElement(styling, node);
                if (content != null)
                    return content;
            }
            Layout layout = head.getLayout();
            if (layout != null) {
                Object content = findBindingElement(layout, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Styling styling, Node node) {
        if (context.getXMLNode(styling) == node)
            return styling;
        else {
            for (Object m : styling.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Style s : styling.getStyle()) {
                Object content = findBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Style style, Node node) {
        if (context.getXMLNode(style) == node)
            return style;
        else
            return null;
    }

    private Object findBindingElement(Layout layout, Node node) {
        if (context.getXMLNode(layout) == node)
            return layout;
        else {
            for (Object m : layout.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Region r : layout.getRegion()) {
                Object content = findBindingElement(r, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Region region, Node node) {
        if (context.getXMLNode(region) == node)
            return region;
        else {
            for (Object m : region.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Set s : region.getAnimationClass()) {
                Object content = findBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Body body, Node node) {
        if (context.getXMLNode(body) == node)
            return body;
        else {
            for (Object m : body.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Set s : body.getAnimationClass()) {
                Object content = findBindingElement(s, node);
                if (content != null)
                    return content;
            }
            for (Division d : body.getDiv()) {
                Object content = findBindingElement(d, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Division division, Node node) {
        if (context.getXMLNode(division) == node)
            return division;
        else {
            for (Object m : division.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Set s : division.getAnimationClass()) {
                Object content = findBindingElement(s, node);
                if (content != null)
                    return content;
            }
            for (Object b : division.getBlockClass()) {
                Object content = findBlockBindingElement(b, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Paragraph paragraph, Node node) {
        if (context.getXMLNode(paragraph) == node)
            return paragraph;
        else {
            for (Serializable s : paragraph.getContent()) {
                Object content = findContentBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Span span, Node node) {
        if (context.getXMLNode(span) == node)
            return span;
        else {
            for (Serializable s : span.getContent()) {
                Object content = findContentBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Break br, Node node) {
        if (context.getXMLNode(br) == node)
            return br;
        else {
            for (Object m : br.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Set s : br.getAnimationClass()) {
                Object content = findBindingElement(s, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Actor actor, Node node) {
        if (context.getXMLNode(actor) == node)
            return actor;
        else
            return null;
    }

    private Object findBindingElement(Agent agent, Node node) {
        if (context.getXMLNode(agent) == node)
            return agent;
        else {
            for (Name name : agent.getName()) {
                Object content = findBindingElement(name, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Copyright copyright, Node node) {
        if (context.getXMLNode(copyright) == node)
            return copyright;
        else
            return null;
    }

    private Object findBindingElement(Description description, Node node) {
        if (context.getXMLNode(description) == node)
            return description;
        else
            return null;
    }

    private Object findBindingElement(Metadata metadata, Node node) {
        if (context.getXMLNode(metadata) == node)
            return metadata;
        else {
            for (Object m : metadata.getAny()) {
                if (!isMetadataItem(m)) {
                    Object content = findMetadataBindingElement(m, node);
                    if (content != null)
                        return content;
                }
            }
            return null;
        }
    }

    private Object findBindingElement(Name name, Node node) {
        if (context.getXMLNode(name) == node)
            return name;
        else
            return null;
    }

    private Object findBindingElement(Title title, Node node) {
        if (context.getXMLNode(title) == node)
            return title;
        else
            return null;
    }

    private Object findMetadataBindingElement(Object metadata, Node node) {
        if (metadata instanceof JAXBElement<?>)
            return findMetadataBindingElement(((JAXBElement<?>)metadata).getValue(), node);
        else if (context.getXMLNode(metadata) == node)
            return metadata;
        else if (metadata instanceof Actor)
            return findBindingElement((Actor)metadata, node);
        else if (metadata instanceof Agent)
            return findBindingElement((Agent)metadata, node);
        else if (metadata instanceof Copyright)
            return findBindingElement((Copyright)metadata, node);
        else if (metadata instanceof Description)
            return findBindingElement((Description)metadata, node);
        else if (metadata instanceof Metadata)
            return findBindingElement((Metadata)metadata, node);
        else if (metadata instanceof Name)
            return findBindingElement((Name)metadata, node);
        else if (metadata instanceof Title)
            return findBindingElement((Title)metadata, node);
        else if (metadata instanceof Element)
            return findForeignMetadataBindingElement((Element)metadata, node);
        else
            return null;
    }

    private Object findForeignMetadataBindingElement(Element metadata, Node node) {
        if (context.getXMLNode(metadata) == node)
            return metadata;
        else
            return null;
    }

    private Object findBlockBindingElement(Object block, Node node) {
        if (context.getXMLNode(block) == node)
            return block;
        else if (block instanceof Division)
            return findBindingElement((Division) block, node);
        else if (block instanceof Paragraph)
            return findBindingElement((Paragraph) block, node);
        else
            return null;
    }

    private Object findContentBindingElement(Serializable content, Node node) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (context.getXMLNode(element) == node)
                return element;
            else if (isMetadataItem(element))
                return findMetadataBindingElement(element, node);
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

    private Object findBindingElement(Set set, Node node) {
        if (context.getXMLNode(set) == node)
            return set;
        else {
            for (Object m : set.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Profile profile, Node node) {
        if (context.getXMLNode(profile) == node)
            return profile;
        else {
            for (Object m : profile.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Features features : profile.getFeatures()) {
                Object content = findBindingElement(features, node);
                if (content != null)
                    return content;
            }
            for (Extensions extensions : profile.getExtensions()) {
                Object content = findBindingElement(extensions, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Features features, Node node) {
        if (context.getXMLNode(features) == node)
            return features;
        else {
            for (Object m : features.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Feature feature : features.getFeature()) {
                Object content = findBindingElement(feature, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Feature feature, Node node) {
        if (context.getXMLNode(feature) == node)
            return feature;
        else
            return null;
    }

    private Object findBindingElement(Extensions extensions, Node node) {
        if (context.getXMLNode(extensions) == node)
            return extensions;
        else {
            for (Object m : extensions.getMetadataClass()) {
                Object content = findMetadataBindingElement(m, node);
                if (content != null)
                    return content;
            }
            for (Extension extension : extensions.getExtension()) {
                Object content = findBindingElement(extension, node);
                if (content != null)
                    return content;
            }
            return null;
        }
    }

    private Object findBindingElement(Extension extension, Node node) {
        if (context.getXMLNode(extension) == node)
            return extension;
        else
            return null;
    }

    protected boolean unexpectedContent(Object content) throws IllegalStateException {
        throw new IllegalStateException("Unexpected JAXB content object of type '" + content.getClass().getName() +  "'.");
    }

    private static boolean isMetadataItem(Object element) {
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
