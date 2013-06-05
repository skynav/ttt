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
 
package com.skynav.ttv.validator.ttml;

import java.io.Serializable;
import java.util.Map;

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
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.validator.SemanticsValidator;
import com.skynav.ttv.validator.StyleValidator;

public class TTML10SemanticsValidator implements SemanticsValidator {

    private Model model;
    private Map<Object,Locator> locators;
    private ErrorReporter errorReporter;
    private StyleValidator styleValidator;

    public TTML10SemanticsValidator(Model model) {
        this.model = model;
    }

    @Override
    public boolean validate(Object root, Map<Object,Locator> locators, ErrorReporter errorReporter) {
        setState(root, locators, errorReporter);
        if (root instanceof TimedText)
            return validate((TimedText)root);
        else if (root instanceof Profile)
            return validate((Profile)root);
        else
            throw new IllegalStateException("Unexpected JAXB content object of type '" + root.getClass().getName() +  "'.");
    }

    private void setState(Object root, Map<Object,Locator> locators, ErrorReporter errorReporter) {
        // passed state
        this.locators = locators;
        this.errorReporter = errorReporter;
        // derived state
        this.styleValidator = model.getStyleValidator();
    }

    private Locator getLocator(Object content) {
        return locators.get(content);
    }

    private boolean validate(TimedText tt) {
        boolean failed = false;
        if (!validateParameters(tt))
            failed = true;
        if (!validateStyles(tt))
            failed = true;
        Head head = tt.getHead();
        if (head != null) {
            if (!validate(head))
                failed = true;
        }
        Body body = tt.getBody();
        if (body != null) {
            if (!validate(body))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Head head) {
        boolean failed = false;
        for (Object m : head.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Profile p : head.getParametersClass()) {
            if (!validate(p))
                failed = true;
        }
        Styling styling  = head.getStyling();
        if (styling != null) {
            if (!validate(styling))
                failed = true;
        }
        Layout layout  = head.getLayout();
        if (layout != null) {
            if (!validate(layout))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Styling styling) {
        boolean failed = false;
        for (Object m : styling.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Style s : styling.getStyle()) {
            if (!validate(s))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Style style) {
        boolean failed = false;
        if (!validateStyles(style))
            failed = true;
        return !failed;
    }

    private boolean validate(Layout layout) {
        boolean failed = false;
        for (Object m : layout.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Region r : layout.getRegion()) {
            if (!validate(r))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Region region) {
        boolean failed = false;
        if (!validateStyles(region))
            failed = true;
        if (!validateTiming(region))
            failed = true;
        for (Object m : region.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Set s : region.getAnimationClass()) {
            if (!validate(s))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Body body) {
        boolean failed = false;
        if (!validateStyles(body))
            failed = true;
        if (!validateTiming(body))
            failed = true;
        for (Object m : body.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Set s : body.getAnimationClass()) {
            if (!validate(s))
                failed = true;
        }
        for (Division d : body.getDiv()) {
            if (!validate(d))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Division division) {
        boolean failed = false;
        if (!validateStyles(division))
            failed = true;
        if (!validateTiming(division))
            failed = true;
        for (Object m : division.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Set s : division.getAnimationClass()) {
            if (!validate(s))
                failed = true;
        }
        for (Object b : division.getBlockClass()) {
            if (!validateBlock(b))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Paragraph paragraph) {
        boolean failed = false;
        if (!validateStyles(paragraph))
            failed = true;
        if (!validateTiming(paragraph))
            failed = true;
        for (Serializable s : paragraph.getContent()) {
            if (!validateContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Span span) {
        boolean failed = false;
        if (!validateStyles(span))
            failed = true;
        if (!validateTiming(span))
            failed = true;
        for (Serializable s : span.getContent()) {
            if (!validateContent(s))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Break br) {
        boolean failed = false;
        if (!validateStyles(br))
            failed = true;
        if (!validateTiming(br))
            failed = true;
        for (Object m : br.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Set s : br.getAnimationClass()) {
            if (!validate(s))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Set set) {
        boolean failed = false;
        if (!validateStyles(set))
            failed = true;
        if (!validateTiming(set))
            failed = true;
        for (Object m : set.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Profile profile) {
        boolean failed = false;
        for (Object m : profile.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Features features : profile.getFeatures()) {
            if (!validate(features))
                failed = true;
        }
        for (Extensions extensions: profile.getExtensions()) {
            if (!validate(extensions))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Features features) {
        boolean failed = false;
        for (Object m : features.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Feature feature : features.getFeature()) {
            if (!validate(feature))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Feature feature) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Extensions extensions) {
        boolean failed = false;
        for (Object m : extensions.getMetadataClass()) {
            if (!validateMetadata(m))
                failed = true;
        }
        for (Extension extension : extensions.getExtension()) {
            if (!validate(extension))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Extension extension) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Actor actor) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Agent agent) {
        boolean failed = false;
        for (Name name : agent.getName()) {
            if (!validate(name))
                failed = true;
        }
        return !failed;
    }

    private boolean validate(Copyright copyright) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Description description) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Metadata metadata) {
        boolean failed = false;
        for (Object content : metadata.getAny()) {
            if (!isMetadata(content)) {
                if (!validateMetadata(content))
                    failed = true;
            }
        }
        return !failed;
    }

    private boolean validate(Name name) {
        boolean failed = false;
        return !failed;
    }

    private boolean validate(Title title) {
        boolean failed = false;
        return !failed;
    }

    private boolean validateForeignMetadata(Element metadata) {
        boolean failed = false;
        return !failed;
    }

    private boolean validateMetadata(Object metadata) {
        if (metadata instanceof JAXBElement<?>)
            return validateMetadata(((JAXBElement<?>)metadata).getValue());
        else if (metadata instanceof Actor)
            return validate((Actor)metadata);
        else if (metadata instanceof Agent)
            return validate((Agent)metadata);
        else if (metadata instanceof Copyright)
            return validate((Copyright)metadata);
        else if (metadata instanceof Description)
            return validate((Description)metadata);
        else if (metadata instanceof Metadata)
            return validate((Metadata)metadata);
        else if (metadata instanceof Name)
            return validate((Name)metadata);
        else if (metadata instanceof Title)
            return validate((Title)metadata);
        else if (metadata instanceof Element)
            return validateForeignMetadata((Element)metadata);
        else
            throw new IllegalStateException("Unexpected JAXB content object of type '" + metadata.getClass().getName() +  "'.");
    }

    private boolean validateBlock(Object block) {
        if (block instanceof Division)
            return validate((Division) block);
        else if (block instanceof Paragraph)
            return validate((Paragraph) block);
        else
            throw new IllegalStateException("Unexpected JAXB content object of type '" + block.getClass().getName() +  "'.");
    }

    private boolean validateContent(Serializable content) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadata(element))
                return validateMetadata(element);
            else if (element instanceof Set)
                return validate((Set) element);
            else if (element instanceof Span)
                return validate((Span) element);
            else if (element instanceof Break)
                return validate((Break) element);
            else
                throw new IllegalStateException("Unexpected JAXB content object of type '" + element.getClass().getName() +  "'.");
        } else if (content instanceof String) {
            return true;
        } else {
            throw new IllegalStateException("Unexpected JAXB object of type '" + content.getClass().getName() +  "'.");

        }
    }

    private boolean validateParameters(Object content) {
        boolean failed = false;
        return !failed;
    }

    private boolean validateStyles(Object content) {
        return this.styleValidator.validate(content, getLocator(content), this.errorReporter);
    }

    private boolean validateTiming(Object content) {
        boolean failed = false;
        return !failed;
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
