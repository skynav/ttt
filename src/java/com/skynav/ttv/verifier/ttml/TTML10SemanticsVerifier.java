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

import javax.xml.bind.Binder;
import javax.xml.bind.JAXBElement;

import org.w3c.dom.Element;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml10.tt.Body;
import com.skynav.ttv.model.ttml10.tt.Break;
import com.skynav.ttv.model.ttml10.tt.Division;
import com.skynav.ttv.model.ttml10.tt.Head;
import com.skynav.ttv.model.ttml10.tt.Layout;
import com.skynav.ttv.model.ttml10.tt.Metadata;
import com.skynav.ttv.model.ttml10.tt.Paragraph;
import com.skynav.ttv.model.ttml10.tt.Region;
import com.skynav.ttv.model.ttml10.tt.Set;
import com.skynav.ttv.model.ttml10.tt.Span;
import com.skynav.ttv.model.ttml10.tt.Style;
import com.skynav.ttv.model.ttml10.tt.Styling;
import com.skynav.ttv.model.ttml10.tt.TimedText;
import com.skynav.ttv.model.ttml10.ttp.Extension;
import com.skynav.ttv.model.ttml10.ttp.Extensions;
import com.skynav.ttv.model.ttml10.ttp.Feature;
import com.skynav.ttv.model.ttml10.ttp.Features;
import com.skynav.ttv.model.ttml10.ttp.Profile;
import com.skynav.ttv.model.ttml10.ttm.Actor;
import com.skynav.ttv.model.ttml10.ttm.Agent;
import com.skynav.ttv.model.ttml10.ttm.Copyright;
import com.skynav.ttv.model.ttml10.ttm.Description;
import com.skynav.ttv.model.ttml10.ttm.Name;
import com.skynav.ttv.model.ttml10.ttm.Title;
import com.skynav.ttv.util.Locators;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;

public class TTML10SemanticsVerifier implements SemanticsVerifier {

    private Model model;
    private Binder<?> binder;
    private ErrorReporter errorReporter;
    private ParameterVerifier parameterVerifier;
    private StyleVerifier styleVerifier;
    private TimingVerifier timingVerifier;

    public TTML10SemanticsVerifier(Model model, Binder<?> binder) {
        this.model = model;
        this.binder = binder;
    }

    @Override
    public boolean verify(Object root, ErrorReporter errorReporter) {
        setState(root, errorReporter);
        if (root instanceof TimedText)
            return verify((TimedText)root);
        else if (root instanceof Profile)
            return verify((Profile)root);
        else
            return unexpectedContent(root);
    }

    private void setState(Object root, ErrorReporter errorReporter) {
        // passed state
        this.errorReporter = errorReporter;
        // derived state
        this.parameterVerifier = model.getParameterVerifier(binder);
        this.styleVerifier = model.getStyleVerifier(binder);
        this.timingVerifier = model.getTimingVerifier(binder);
    }

    private Locator getLocator(Object content) {
        return Locators.getLocator(content);
    }

    private boolean verify(TimedText tt) {
        boolean failed = false;
        if (!verifyParameters(tt))
            failed = true;
        if (!verifyStyles(tt))
            failed = true;
        Head head = tt.getHead();
        if (head != null) {
            if (!verify(head))
                failed = true;
        }
        Body body = tt.getBody();
        if (body != null) {
            if (!verify(body))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Head head) {
        boolean failed = false;
        for (Object m : head.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Profile p : head.getParametersClass()) {
            if (!verify(p))
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
        if (!verifyStyles(style))
            failed = true;
        return !failed;
    }

    private boolean verify(Layout layout) {
        boolean failed = false;
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
        if (!verifyStyles(region))
            failed = true;
        if (!verifyTiming(region))
            failed = true;
        for (Object m : region.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set s : region.getAnimationClass()) {
            if (!verify(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Body body) {
        boolean failed = false;
        if (!verifyStyles(body))
            failed = true;
        if (!verifyTiming(body))
            failed = true;
        for (Object m : body.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set s : body.getAnimationClass()) {
            if (!verify(s))
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
        if (!verifyStyles(division))
            failed = true;
        if (!verifyTiming(division))
            failed = true;
        for (Object m : division.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set s : division.getAnimationClass()) {
            if (!verify(s))
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
        if (!verifyStyles(paragraph))
            failed = true;
        if (!verifyTiming(paragraph))
            failed = true;
        for (Serializable s : paragraph.getContent()) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Span span) {
        boolean failed = false;
        if (!verifyStyles(span))
            failed = true;
        if (!verifyTiming(span))
            failed = true;
        for (Serializable s : span.getContent()) {
            if (!verifyContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Break br) {
        boolean failed = false;
        if (!verifyStyles(br))
            failed = true;
        for (Object m : br.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        for (Set s : br.getAnimationClass()) {
            if (!verify(s))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Set set) {
        boolean failed = false;
        if (!verifyStyles(set))
            failed = true;
        if (!verifyTiming(set))
            failed = true;
        for (Object m : set.getMetadataClass()) {
            if (!verifyMetadata(m))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Profile profile) {
        boolean failed = false;
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
        return !failed;
    }

    private boolean verify(Features features) {
        boolean failed = false;
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
        return !failed;
    }

    private boolean verify(Extensions extensions) {
        boolean failed = false;
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
        return !failed;
    }

    private boolean verify(Actor actor) {
        boolean failed = false;
        return !failed;
    }

    private boolean verify(Agent agent) {
        boolean failed = false;
        for (Name name : agent.getName()) {
            if (!verify(name))
                failed = true;
        }
        return !failed;
    }

    private boolean verify(Copyright copyright) {
        boolean failed = false;
        return !failed;
    }

    private boolean verify(Description description) {
        boolean failed = false;
        return !failed;
    }

    private boolean verify(Metadata metadata) {
        boolean failed = false;
        for (Object content : metadata.getAny()) {
            if (!isMetadata(content)) {
                if (!verifyMetadata(content))
                    failed = true;
            }
        }
        return !failed;
    }

    private boolean verify(Name name) {
        boolean failed = false;
        return !failed;
    }

    private boolean verify(Title title) {
        boolean failed = false;
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
            if (isMetadata(element))
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

    private boolean verifyParameters(Object content) {
        return this.parameterVerifier.verify(content, getLocator(content), this.errorReporter);
    }

    private boolean verifyStyles(Object content) {
        return this.styleVerifier.verify(content, getLocator(content), this.errorReporter);
    }

    private boolean verifyTiming(Object content) {
        return this.timingVerifier.verify(content, getLocator(content), this.errorReporter);
    }

    private boolean unexpectedContent(Object content) throws IllegalStateException {
        throw new IllegalStateException("Unexpected JAXB content object of type '" + content.getClass().getName() +  "'.");
    }

    private static boolean isMetadata(Object element) {
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
