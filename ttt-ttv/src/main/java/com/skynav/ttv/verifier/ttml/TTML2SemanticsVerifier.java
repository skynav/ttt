/*
 * Copyright 2015-2018 Skynav, Inc. All rights reserved.
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
import java.math.BigInteger;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML2;
import com.skynav.ttv.model.ttml2.isd.ISD;
import com.skynav.ttv.model.ttml2.isd.ISDSequence;
import com.skynav.ttv.model.ttml2.tt.Animate;
import com.skynav.ttv.model.ttml2.tt.Animation;
import com.skynav.ttv.model.ttml2.tt.Audio;
import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Break;
import com.skynav.ttv.model.ttml2.tt.Chunk;
import com.skynav.ttv.model.ttml2.tt.Data;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Font;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.Image;
import com.skynav.ttv.model.ttml2.tt.Initial;
import com.skynav.ttv.model.ttml2.tt.Layout;
import com.skynav.ttv.model.ttml2.tt.Metadata;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Resources;
import com.skynav.ttv.model.ttml2.tt.Set;
import com.skynav.ttv.model.ttml2.tt.Source;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.Styling;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.DataEncoding;
import com.skynav.ttv.model.ttml2.ttm.Actor;
import com.skynav.ttv.model.ttml2.ttm.Agent;
import com.skynav.ttv.model.ttml2.ttm.Copyright;
import com.skynav.ttv.model.ttml2.ttm.Description;
import com.skynav.ttv.model.ttml2.ttm.Item;
import com.skynav.ttv.model.ttml2.ttm.Name;
import com.skynav.ttv.model.ttml2.ttm.Title;
import com.skynav.ttv.model.ttml2.ttp.Extensions;
import com.skynav.ttv.model.ttml2.ttp.Features;
import com.skynav.ttv.model.ttml2.ttp.Profile;
import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Audios;
import com.skynav.ttv.verifier.util.Base64;
import com.skynav.ttv.verifier.util.Characters;
import com.skynav.ttv.verifier.util.Datas;
import com.skynav.ttv.verifier.util.Fonts;
import com.skynav.ttv.verifier.util.Images;
import com.skynav.ttv.verifier.util.QuotedGenericFontFamilyTreatment;
import com.skynav.ttv.verifier.util.RepeatCount;
import com.skynav.ttv.verifier.util.ResourceFormats;
import com.skynav.ttv.verifier.util.ResourceTypes;

public class TTML2SemanticsVerifier extends TTML1SemanticsVerifier {

    public static final String NAMESPACE                        = TTML2.Constants.NAMESPACE_TT;

    public static final QName animationFillAttributeName        = new QName("", "fill");
    public static final QName animationRepeatCountAttributeName = new QName("", "repeatCount");
    public static final QName fontFamilyAttributeName           = new QName("", "family");
    public static final QName fontRangeAttributeName            = new QName("", "range");
    public static final QName fontStyleAttributeName            = new QName("", "style");
    public static final QName fontWeightAttributeName           = new QName("", "weight");
    public static final QName resourceEncodingAttributeName     = new QName("", "encoding");
    public static final QName resourceFormatAttributeName       = new QName("", "format");
    public static final QName resourceLengthAttributeName       = new QName("", "length");
    public static final QName resourceSourceAttributeName       = new QName("", "src");
    public static final QName resourceTypeAttributeName         = new QName("", "type");

    public static final QName audioElementName                  = new QName(NAMESPACE, "audio");
    public static final QName imageElementName                  = new QName(NAMESPACE, "image");
    public static final QName sourceElementName                 = new QName(NAMESPACE, "source");

    public TTML2SemanticsVerifier(Model model) {
        super(model);
    }

    // verifier overrides

    @Override
    protected boolean verifyRoot(Object root) {
        if (root instanceof TimedText)
            return verifyTimedText(root);
        else if (root instanceof Profile)
            return verifyProfile(root);
        else if (root instanceof ISDSequence)
            return verifyISDSequence(root);
        else if (root instanceof ISD)
            return verifyISD(root);
        else
            return unexpectedContent(root);
    }

    @Override
    public boolean verifyOtherElement(Object content, Locator locator, VerifierContext context) {
        if (content instanceof Data)
            return verifyData(content);
        else
            return super.verifyOtherElement(content, locator, context);
    }

    @Override
    protected boolean verifyHead(Object head, Object tt) {
        boolean failed = false;
        if (!verifyParameterAttributes(head))
            failed = true;
        if (!verifyOtherAttributes(head))
            failed = true;
        for (Object m : getHeadMetadata(head)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        Object resources  = getHeadResources(head);
        if (resources != null) {
            if (!verifyResources(resources))
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
        Object animation  = getHeadAnimation(head);
        if (animation != null) {
            if (!verifyAnimations(animation))
                failed = true;
        }
        return !failed;
    }

    @Override
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
        for (Object nestedProfile : getProfileProfiles(profile)) {
            if (!verifyProfile(nestedProfile))
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

    protected boolean verifyISDSequence(Object isdSequence) {
        boolean failed = false;
        if (!verifyISDSequenceAttributes(isdSequence))
            failed = true;
        for (Object i : getISDs(isdSequence)) {
            if (!verifyISD(i))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getISDs(Object isdSequence) {
        assert isdSequence instanceof ISDSequence;
        return ((ISDSequence) isdSequence).getIsd();
    }

    protected boolean verifyISDSequenceAttributes(Object isdSequence) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(isdSequence);
        Locator locator = location.getLocator();
        BigInteger version = getISDSequenceVersionAttribute(isdSequence);
        if ((version != null) && (version.intValue() < 2)) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "Bad version attribute ''{0}'', must be greater than or equal to 2", version.intValue()));
            failed = true;
        }
        BigInteger size = getISDSequenceSizeAttribute(isdSequence);
        if ((size != null) && (size.intValue() < 0)) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "Bad size attribute ''{0}'', must be greater than or equal to 0", size.intValue()));
            failed = true;
        }
        // [TBD] - VERIFY SIZE is EQUAL TO NUMBER OF COMPONENT ISD INSTANCES
        return !failed;
    }
    
    protected BigInteger getISDSequenceVersionAttribute(Object isdSequence) {
        assert isdSequence instanceof ISDSequence;
        BigInteger version;
        try {
            version = new BigInteger(((ISDSequence) isdSequence).getVersion());
        } catch (RuntimeException e) {
            version = null;
        }
        return version;
    }

    protected BigInteger getISDSequenceSizeAttribute(Object isdSequence) {
        assert isdSequence instanceof ISDSequence;
        BigInteger size;
        try {
            size = new BigInteger(((ISDSequence) isdSequence).getSize());
        } catch (RuntimeException e) {
            size = null;
        }
        return size;
    }

    protected boolean verifyISD(Object isd) {
        boolean failed = false;
        if (!verifyISDAttributes(isd))
            failed = true;
        // [TBD] - IMPLEMENT REMAINDER
        return !failed;
    }

    protected boolean verifyISDAttributes(Object isd) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(isd);
        Locator locator = location.getLocator();
        BigInteger version = getISDVersionAttribute(isd);
        if (version != null) {
            Object parent = context.getBindingElementParent(isd);
            if (parent instanceof JAXBElement<?>)
                parent = ((JAXBElement<?>)parent).getValue();
            if ((parent != null) && (parent instanceof ISDSequence)) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "Bad version attribute, not permitted on nested ''{0}'' element.", context.getBindingElementName(isd)));
                failed = true;
            } else if (version.intValue() < 2) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "Bad version attribute, got {0}, expected {1} or greater.", version.intValue(), 2));
                failed = true;
            }
        }
        return !failed;
    }
    
    protected BigInteger getISDVersionAttribute(Object isd) {
        assert isd instanceof ISD;
        BigInteger version;
        try {
            version = new BigInteger(((ISD) isd).getVersion());
        } catch (RuntimeException e) {
            version = null;
        }
        return version;
    }

    @Override
    protected boolean verifyStyling(Object styling) {
        boolean failed = false;
        if (!verifyParameterAttributes(styling))
            failed = true;
        if (!verifyOtherAttributes(styling))
            failed = true;
        for (Object m : getStylingMetadata(styling)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Initial i : getStylingInitials(styling)) {
            if (!verifyInitial(i))
                failed = true;
            else
                addInitialOverrides(i);
        }
        for (Object s : getStylingStyles(styling)) {
            if (!verifyStyle(s))
                failed = true;
        }
        return !failed;
    }

    @Override
    protected boolean verifyAnimation(Object animation) {
        if (animation instanceof Animate)
            return verifyAnimate(animation);
        else if (animation instanceof Set)
            return verifySet(animation);
        else
            return unexpectedContent(animation);
    }

    @Override
    protected boolean verifySetAttributes(Object set) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(set);
        Locator locator = location.getLocator();
        // @fill        - schema validation only
        // @repeatCount
        String repeatCount = getSetRepeatCountAttribute(set);
        if (repeatCount != null) {
            if (!verifyNonEmptyOrPadded(set, animationRepeatCountAttributeName, repeatCount, locator, context))
                failed = true;
            else if (!RepeatCount.isRepeatCount(repeatCount, location, context, null)) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "Bad <repeat-count> expression ''{0}''.", repeatCount));
                failed = true;
            }
        }
        return !failed;
    }

    protected String getSetRepeatCountAttribute(Object set) {
        assert set instanceof Set;
        return ((Set) set).getRepeatCount();
    }

    @Override
    protected boolean verifyBody(Object body) {
        boolean failed = false;
        if (!verifyParameterAttributes(body))
            failed = true;
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
        for (Object d : getBodyDivisionsAndEmbeddings(body)) {
            if (!verifyDivisionOrEmbedding(d))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getBodyDivisionsAndEmbeddings(Object body) {
        assert body instanceof Body;
        return ((Body) body).getDivOrEmbeddedClass();
    }

    protected boolean verifyDivisionOrEmbedding(Object divisionOrEmbedding) {
        if (divisionOrEmbedding instanceof Division)
            return verifyDivision(divisionOrEmbedding);
        else
            return verifyEmbedding(divisionOrEmbedding);
    }

    @Override
    protected boolean verifyDivision(Object division) {
        boolean failed = false;
        if (!super.verifyDivision(division))
            failed = true;
        Region region = getDivisionRegion(division);
        if (region != null) {
            if (!verifyRegion(region))
                failed = true;
        }
        return !failed;
    }

    protected Region getDivisionRegion(Object division) {
        assert division instanceof Division;
        return ((Division) division).getLayoutClass();
    }

    protected boolean verifyEmbedding(Object embedding) {
        if (embedding instanceof Audio)
            return verifyAudio(embedding);
        else if (embedding instanceof Image)
            return verifyImage(embedding);
        else
            return unexpectedContent(embedding);
    }

    @Override
    protected boolean verifyBlock(Object block) {
        if (block instanceof Division)
            return verifyDivision(block);
        else if (block instanceof Paragraph)
            return verifyParagraph(block);
        else if (block instanceof Image)
            return verifyImage(block);
        else if (block instanceof Audio)
            return verifyAudio(block);
        else
            return unexpectedContent(block);
    }

    @Override
    protected boolean verifyContent(Serializable content) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadataItem(element))
                return verifyMetadata(element);
            else if (element instanceof Animate)
                return verifyAnimate(element);
            else if (element instanceof Set)
                return verifySet(element);
            else if (element instanceof Region)
                return verifyRegion(element);
            else if (element instanceof Span)
                return verifySpan(element);
            else if (element instanceof Break)
                return verifyBreak(element);
            else if (element instanceof Image)
                return verifyImage(element);
            else if (element instanceof Audio)
                return verifyAudio(element);
            else
                return unexpectedContent(element);
        } else if (content instanceof String) {
            return true;
        } else
            return unexpectedContent(content);
    }

    @Override
    protected Object findBlockBindingElement(Object block, Node node) {
        if (getContext().getXMLNode(block) == node)
            return block;
        else if (block instanceof Image)
            return findBindingElement((Image) block, node);
        else if (block instanceof Audio)
            return findBindingElement((Audio) block, node);
        else
            return super.findBlockBindingElement(block, node);
    }

    @Override
    protected Object findContentBindingElement(Serializable content, Node node) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (getContext().getXMLNode(element) == node)
                return element;
            else if (element instanceof Animate)
                return findBindingElement((Animate) element, node);
            else if (element instanceof Image)
                return findBindingElement((Image) element, node);
            else if (element instanceof Audio)
                return findBindingElement((Audio) element, node);
        }
        return super.findContentBindingElement(content, node);
    }

    // metadata overrides

    @Override
    protected boolean isMetadataItem(Object element) {
        if (element instanceof Agent)
            return true;
        else if (element instanceof Copyright)
            return true;
        else if (element instanceof Description)
            return true;
        else if (element instanceof Item)
            return true;
        else if (element instanceof Metadata)
            return true;
        else if (element instanceof Title)
            return true;
        else
            return false;
    }

    @Override
    protected boolean verifyMetadataItem(Object metadata) {
        if (metadata instanceof JAXBElement<?>)
            return verifyMetadata(((JAXBElement<?>)metadata).getValue());
        else if (metadata instanceof Actor)
            return verifyActor(metadata);
        else if (metadata instanceof Agent)
            return verifyAgent(metadata);
        else if (metadata instanceof Copyright)
            return verifyCopyright(metadata);
        else if (metadata instanceof Description)
            return verifyDescription(metadata);
        else if (metadata instanceof Item)
            return verifyItem(metadata);
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

    @Override
    protected Object findMetadataItemBindingElement(Object metadata, Node node) {
        if (metadata instanceof JAXBElement<?>)
            return findMetadataItemBindingElement(((JAXBElement<?>)metadata).getValue(), node);
        else if (getContext().getXMLNode(metadata) == node)
            return metadata;
        else if (metadata instanceof Actor)
            return findActorBindingElement(metadata, node);
        else if (metadata instanceof Agent)
            return findAgentBindingElement(metadata, node);
        else if (metadata instanceof Copyright)
            return findCopyrightBindingElement(metadata, node);
        else if (metadata instanceof Description)
            return findDescriptionBindingElement(metadata, node);
        else if (metadata instanceof Item)
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

    @Override
    protected Object findAnimationBindingElement(Object animation, Node node) {
        if (animation instanceof Animate)
            return findAnimateBindingElement(animation, node);
        else if (animation instanceof Set)
            return findSetBindingElement(animation, node);
        else
            return null;
    }

    // accessor overrides

    @Override
    protected Object getTimedTextHead(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getHead();
    }

    @Override
    protected Object getTimedTextBody(Object tt) {
        assert tt instanceof TimedText;
        return ((TimedText) tt).getBody();
    }

    @Override
    protected Collection<? extends Object> getHeadMetadata(Object head) {
        assert head instanceof Head;
        return ((Head) head).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getHeadParameters(Object head) {
        assert head instanceof Head;
        return ((Head) head).getParametersClass();
    }

    protected Object getHeadAnimation(Object head) {
        assert head instanceof Head;
        return ((Head) head).getAnimation();
    }

    protected Object getHeadResources(Object head) {
        assert head instanceof Head;
        return ((Head) head).getResources();
    }

    protected Object getHeadStyling(Object head) {
        assert head instanceof Head;
        return ((Head) head).getStyling();
    }

    @Override
    protected Object getHeadLayout(Object head) {
        assert head instanceof Head;
        return ((Head) head).getLayout();
    }

    @Override
    protected Collection<? extends Object> getStylingMetadata(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getStylingStyles(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getStyle();
    }

    @Override
    protected Collection<? extends Object> getLayoutMetadata(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getLayoutRegions(Object layout) {
        assert layout instanceof Layout;
        return ((Layout) layout).getRegion();
    }

    @Override
    protected Collection<? extends Object> getRegionMetadata(Object region) {
        assert region instanceof Region;
        return ((Region) region).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getRegionAnimations(Object region) {
        assert region instanceof Region;
        return ((Region) region).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getRegionStyles(Object region) {
        assert region instanceof Region;
        return ((Region) region).getStyle();
    }

    @Override
    protected Collection<? extends Object> getBodyMetadata(Object body) {
        assert body instanceof Body;
        return ((Body) body).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getBodyAnimations(Object body) {
        assert body instanceof Body;
        return ((Body) body).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getDivisionMetadata(Object division) {
        assert division instanceof Division;
        return ((Division) division).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getDivisionAnimations(Object division) {
        assert division instanceof Division;
        return ((Division) division).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getDivisionBlocks(Object division) {
        assert division instanceof Division;
        return ((Division) division).getBlockOrEmbeddedClass();
    }

    @Override
    protected Collection<Serializable> getParagraphContent(Object paragraph) {
        assert paragraph instanceof Paragraph;
        return ((Paragraph) paragraph).getContent();
    }

    @Override
    protected Collection<Serializable> getSpanContent(Object span) {
        assert span instanceof Span;
        return ((Span) span).getContent();
    }

    @Override
    protected Collection<? extends Object> getBreakMetadata(Object br) {
        assert br instanceof Break;
        return ((Break) br).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getBreakAnimations(Object br) {
        assert br instanceof Break;
        return ((Break) br).getAnimationClass();
    }

    @Override
    protected Collection<? extends Object> getSetMetadata(Object set) {
        assert set instanceof Set;
        return ((Set) set).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getProfileMetadata(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getMetadataClass();
    }

    protected Collection<? extends Object> getProfileProfiles(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getProfile();
    }

    @Override
    protected Collection<? extends Object> getProfileFeatures(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getFeatures();
    }

    @Override
    protected Collection<? extends Object> getProfileExtensions(Object profile) {
        assert profile instanceof Profile;
        return ((Profile) profile).getExtensions();
    }

    @Override
    protected Collection<? extends Object> getFeaturesMetadata(Object features) {
        assert features instanceof Features;
        return ((Features) features).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getFeaturesFeatures(Object features) {
        assert features instanceof Features;
        return ((Features) features).getFeature();
    }

    @Override
    protected Collection<? extends Object> getExtensionsMetadata(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getMetadataClass();
    }

    @Override
    protected Collection<? extends Object> getExtensionsExtensions(Object extensions) {
        assert extensions instanceof Extensions;
        return ((Extensions) extensions).getExtension();
    }

    @Override
    protected Collection<? extends Object> getAgentNames(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getName();
    }

    @Override
    protected Object getAgentActor(Object agent) {
        assert agent instanceof Agent;
        return ((Agent) agent).getActor();
    }

    @Override
    protected Collection<? extends Object> getMetadataAny(Object metadata) {
        assert metadata instanceof Metadata;
        return ((Metadata) metadata).getDataOrAny();
    }

    // new elements

    protected boolean verifyResources(Object resources) {
        boolean failed = false;
        if (!verifyParameterAttributes(resources))
            failed = true;
        if (!verifyOtherAttributes(resources))
            failed = true;
        for (Object m : getResourcesMetadata(resources)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object r : getResources(resources)) {
            if (!verifyResource(r))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getResourcesMetadata(Object resources) {
        assert resources instanceof Resources;
        return ((Resources) resources).getMetadataClass();
    }

    protected Collection<? extends Object> getResources(Object resources) {
        assert resources instanceof Resources;
        return ((Resources) resources).getResourceClass();
    }

    protected boolean verifyResource(Object resource) {
        if (resource instanceof Audio)
            return verifyAudio(resource);
        else if (resource instanceof Data)
            return verifyData(resource);
        else if (resource instanceof Font)
            return verifyFont(resource);
        else if (resource instanceof Image)
            return verifyImage(resource);
        else
            return unexpectedContent(resource);
    }

    protected List<Initial> getStylingInitials(Object styling) {
        assert styling instanceof Styling;
        return ((Styling) styling).getInitial();
    }

    protected boolean isInitial(Object element) {
        if (element instanceof Initial)
            return true;
        else
            return false;
    }

    protected boolean verifyInitial(Object initial) {
        boolean failed = false;
        if (!verifyParameterAttributes(initial))
            failed = true;
        if (!verifyStyleAttributes(initial))
            failed = true;
        if (!verifyOtherAttributes(initial))
            failed = true;
        return !failed;
    }

    private void addInitialOverrides(Initial initial) {
        this.styleVerifier.addInitialOverrides(initial, getContext());
    }

    protected boolean verifyAnimations(Object animation) {
        boolean failed = false;
        if (!verifyParameterAttributes(animation))
            failed = true;
        if (!verifyOtherAttributes(animation))
            failed = true;
        for (Object m : getAnimationMetadata(animation)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object a : getAnimations(animation)) {
            if (!verifyAnimation(a))
                failed = true;
        }
        return !failed;
    }

    protected Collection<? extends Object> getAnimationMetadata(Object animation) {
        assert animation instanceof Animation;
        return ((Animation) animation).getMetadataClass();
    }

    protected Collection<? extends Object> getAnimations(Object animation) {
        assert animation instanceof Animation;
        return ((Animation) animation).getAnimationClass();
    }

    protected boolean verifyAnimate(Object animate) {
        boolean failed = false;
        if (!verifyParameterAttributes(animate))
            failed = true;
        if (!verifyAnimateAttributes(animate))
            failed = true;
        if (!verifyStyleAttributes(animate))
            failed = true;
        if (!verifyTimingAttributes(animate))
            failed = true;
        if (!verifyOtherAttributes(animate))
            failed = true;
        for (Object m : getAnimateMetadata(animate)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        if (!verifyStyledItem(animate))
            failed = true;
        return !failed;
    }

    protected Collection<? extends Object> getAnimateMetadata(Object animate) {
        assert animate instanceof Animate;
        return ((Animate) animate).getMetadataClass();
    }

    protected Object findAnimateBindingElement(Object animate, Node node) {
        if (getContext().getXMLNode(animate) == node)
            return animate;
        else
            return findBindingElement((Animate) animate, node);
    }

    protected boolean verifyAnimateAttributes(Object animate) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(animate);
        Locator locator = location.getLocator();
        // @calcMode    - schema validation only
        // @fill        - schema validation only
        // @keySplines  - TBD
        // @keyTimes    - TBD
        // @repeatCount
        String repeatCount = getAnimateRepeatCountAttribute(animate);
        if (repeatCount != null) {
            if (!verifyNonEmptyOrPadded(animate, animationRepeatCountAttributeName, repeatCount, locator, context))
                failed = true;
            else if (!RepeatCount.isRepeatCount(repeatCount, location, context, null)) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "Bad <repeat-count> expression ''{0}''.", repeatCount));
                failed = true;
            }
        }
        return !failed;
    }

    protected String getAnimateRepeatCountAttribute(Object animate) {
        assert animate instanceof Animate;
        return ((Animate) animate).getRepeatCount();
    }

    protected boolean verifyAudio(Object audio) {
        boolean failed = false;
        if (!verifyParameterAttributes(audio))
            failed = true;
        if (!verifyAudioAttributes(audio))
            failed = true;
        if (!verifyStyleAttributes(audio))
            failed = true;
        if (!verifyOtherAttributes(audio))
            failed = true;
        for (Object m : getAudioMetadata(audio)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object s : getAudioSources(audio)) {
            if (!verifySource(s))
                failed = true;
        }
        if (!verifyStyledItem(audio))
            failed = true;
        if (!failed && !verifyAudioResources(audio))
            failed = true;
        return !failed;
    }

    protected boolean verifyAudioAttributes(Object audio) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(audio);
        Locator locator = location.getLocator();
        // @src
        String src = getAudioSourceAttribute(audio);
        int numSources = getAudioSources(audio).size();
        com.skynav.ttv.model.value.Audio[] outputAudio = new com.skynav.ttv.model.value.Audio[1];
        com.skynav.ttv.model.value.Audio a = null;
        if (src != null) {
            if (!Audios.isAudio(src, location, context, false, outputAudio)) {
                Audios.badAudio(src, location, context);
                failed = true;
            } else if (numSources > 0) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "No ''{0}'' child permitted when ''{1}'' attribute is specified.", sourceElementName, resourceSourceAttributeName));
                failed = true;
            } else {
                a = outputAudio[0];
            }
        } else if (numSources == 0) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "At least one ''{0}'' child is required when ''{1}'' attribute is not specified.", sourceElementName, resourceSourceAttributeName));
            failed = true;
        }
        // @type
        String type = getAudioTypeAttribute(audio);
        if (type != null) {
            if ((a == null) || !a.isExternal()) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute must not specified for internal audio sources.", resourceTypeAttributeName));
                failed = true;
            } else if (!ResourceTypes.isType(type, location, context, null)) {
                ResourceTypes.badType(type, location, context);
                failed = true;
            }
        } else if ((a != null) && a.isExternal()) {
            if (reporter.isWarningEnabled("missing-type-for-external-source")) {
                Message message = reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute should be specified for external audio sources.", resourceTypeAttributeName);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        // @format
        String format = getAudioFormatAttribute(audio);
        if (format != null) {
            if (!ResourceFormats.isFormat(format, location, context, null)) {
                ResourceFormats.badFormat(format, location, context);
                failed = true;
            }
        }
        return !failed;
    }

    protected String getAudioFormatAttribute(Object audio) {
        assert audio instanceof Audio;
        return null;
    }

    protected String getAudioSourceAttribute(Object audio) {
        assert audio instanceof Audio;
        return ((Audio) audio).getSrc();
    }

    protected String getAudioTypeAttribute(Object audio) {
        assert audio instanceof Audio;
        return ((Audio) audio).getDataType();
    }

    protected Collection<? extends Object> getAudioMetadata(Object audio) {
        assert audio instanceof Audio;
        return ((Audio) audio).getMetadataClass();
    }

    protected Collection<? extends Object> getAudioSources(Object audio) {
        assert audio instanceof Audio;
        return ((Audio) audio).getSource();
    }

    protected boolean verifyAudioResources(Object audio) {
        boolean failed = false;
        VerifierContext context = getContext();
        Location location = getLocation(audio);
        // @src
        String s = getAudioSourceAttribute(audio);
        if ((s != null) && !Audios.isAudio(s, location, context, true, null))
            failed = true;
        // [TBD] verify resources referenced by tt:source children
        return !failed;
    }

    protected boolean verifyChunk(Object chunk) {
        boolean failed = false;
        if (!verifyChunkAttributes(chunk))
            failed = true;
        if (!verifyOtherAttributes(chunk))
            failed = true;
        return !failed;
    }

    protected boolean verifyChunkAttributes(Object chunk) {
        // [TBD] - length
        // [TBD] - encoding
        return true;
    }

    protected boolean verifyData(Object data) {
        boolean failed = false;
        com.skynav.ttv.model.value.Data[] outputData = new com.skynav.ttv.model.value.Data[1];
        DataEncoding[] outputEncoding = new DataEncoding[] { DataEncoding.BASE_64 };
        Integer[] outputLength = new Integer[] { Integer.valueOf(0) };
        if (!verifyParameterAttributes(data))
            failed = true;
        if (!verifyDataAttributes(data, outputData, outputEncoding, outputLength))
            failed = true;
        if (!verifyOtherAttributes(data))
            failed = true;
        for (Serializable s : getDataContent(data)) {
            if (!verifyDataContent(s, data, outputData[0], outputEncoding[0], outputLength[0]))
                failed = true;
        }
        return !failed;
    }

    public enum DataEmbeddingType {
        Chunked,        // contains chunk child
        Reference,      // specifies @src attribute
        Simple,         // contains non-lwsp #PCDATA (text fragment) child
        Sourced;        // contains source child
    }

    private static final BigInteger maxLength = BigInteger.valueOf(Integer.MAX_VALUE);
    protected boolean verifyDataAttributes(Object data,
        com.skynav.ttv.model.value.Data[] outputData, DataEncoding[] outputEncoding, Integer[] outputLength) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(data);
        Locator locator = location.getLocator();
        DataEmbeddingType embeddingType = getDataEmbeddingType(data);

        // @src
        // simple embedding    - ignore with warning (not in spec!)
        // chunked embedding   - ignore with warning (not in spec!)
        // sourced embedding   - ignore with warning (not in spec!)
        // reference embedding - required
        String src = getDataSourceAttribute(data);
        com.skynav.ttv.model.value.Data d = null;
        if (src != null) {
            if (embeddingType != DataEmbeddingType.Reference) {
                if (reporter.isWarningEnabled("ignored-source-attribute")) {
                    Message message = reporter.message(locator, "*KEY*",
                      "Ignoring ''{0}'' attribute for non-reference {1} data embedding.", resourceSourceAttributeName, embeddingType);
                    if (reporter.logWarning(message)) {
                        reporter.logError(message);
                        failed = true;
                    }
                }
            } else {
                com.skynav.ttv.model.value.Data[] tempOutputData = new com.skynav.ttv.model.value.Data[1];
                if (!Datas.isData(src, location, context, false, tempOutputData)) {
                    Datas.badData(src, location, context);
                    failed = true;
                } else {
                    d = tempOutputData[0];
                }
            }
        }

        // @type
        // simple embedding    - required
        // chunked embedding   - required
        // sourced embedding   - use type of resolved source (what if present but different than type of resolved source?)
        // reference embedding - if external reference, then should be present; if internal reference, then must not be present (not in spec!)
        String type = getDataTypeAttribute(data);
        if (type != null) {
            if ((d != null) && !d.isExternal()) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute must not specified for internal {1} data embedding.", resourceTypeAttributeName, embeddingType));
                failed = true;
            } else if (!ResourceTypes.isType(type, location, context, null)) {
                ResourceTypes.badType(type, location, context);
                failed = true;
            }
        } else if ((embeddingType == DataEmbeddingType.Chunked) || (embeddingType == DataEmbeddingType.Simple)) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "A ''{0}'' attribute must be specified for {1} data embedding.", resourceTypeAttributeName, embeddingType));
            failed = true;
        } else if (embeddingType == DataEmbeddingType.Reference) {
            if ((d != null) && d.isExternal()) {
                if (reporter.isWarningEnabled("missing-type-for-external-source")) {
                    Message message = reporter.message(locator, "*KEY*",
                      "A ''{0}'' attribute should be specified for external {1} data embedding.", resourceTypeAttributeName, embeddingType);
                    if (reporter.logWarning(message)) {
                        reporter.logError(message);
                        failed = true;
                    }
                }
            }
        }

        // @format
        // simple embedding    - optional
        // chunked embedding   - optional
        // sourced embedding   - optional (what if present but different than format of resolved source?)
        // reference embedding - optional
        String format = getDataFormatAttribute(data);
        if (format != null) {
            if (!ResourceFormats.isFormat(format, location, context, null)) {
                ResourceFormats.badFormat(format, location, context);
                failed = true;
            }
        }

        // @encoding
        // simple embedding    - optional, base64 is default
        // chunked embedding   - not permitted
        // sourced embedding   - not permitted
        // reference embedding - not permitted
        DataEncoding encoding = getDataEncodingAttribute(data);
        if (encoding != null) {
            if (embeddingType != DataEmbeddingType.Simple) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "An ''{0}'' attribute must not specified for non-simple data embeddings.", resourceEncodingAttributeName));
                failed = true;
            }
        } else {
            if (embeddingType == DataEmbeddingType.Simple) {
                encoding = DataEncoding.BASE_64;
            }
        }

        // @length
        // simple embedding    - optional, actual number of decoded bytes is default
        // chunked embedding   - optional, actual number of decoded bytes of all chunks is default
        // sourced embedding   - not permitted
        // reference embedding - not permitted
        BigInteger length = getDataLengthAttribute(data);
        if (length != null) {
            if ((embeddingType == DataEmbeddingType.Sourced) || (embeddingType == DataEmbeddingType.Reference)) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute must not be specified for {1} data embedding.", resourceLengthAttributeName, embeddingType));
                failed = true;
            } else if (length.abs().compareTo(maxLength) > 0) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "Absolute values of the ''{0}'' attribute greater than {1} are not supported, got {2}.",
                    resourceLengthAttributeName, maxLength.toString(), length.toString()));
                failed = true;
            }
        } else {
            length = BigInteger.valueOf(-1);
        }

        // update output parameters
        if (!failed) {
            if (outputData != null) {
                assert outputData.length > 0;
                outputData[0] = d;
            }
            if (outputEncoding != null) {
                assert outputEncoding.length > 0;
                outputEncoding[0] = encoding;
            }
            if (outputLength != null) {
                assert outputLength.length > 0;
                outputLength[0] = Integer.valueOf(length.intValueExact());
            }
        }

        return !failed;
    }

    private DataEmbeddingType getDataEmbeddingType(Object data) {
        assert data instanceof Data;
        if (getDataChunks(data).size() > 0)
            return DataEmbeddingType.Chunked;
        else if (getDataSources(data).size() > 0)
            return DataEmbeddingType.Sourced;
        else {
            String text = getDataText(data).replaceAll("\\s", "");
            if (!text.isEmpty())
                return DataEmbeddingType.Simple;
            else {
                String src = getDataSourceAttribute(data);
                if (src != null)
                    return DataEmbeddingType.Reference;
                else
                    return DataEmbeddingType.Simple;
            }
        }
    }

    protected DataEncoding getDataEncodingAttribute(Object data) {
        assert data instanceof Data;
        return ((Data) data).getEncoding();
    }

    protected String getDataFormatAttribute(Object data) {
        assert data instanceof Data;
        return ((Data) data).getFormat();
    }

    protected BigInteger getDataLengthAttribute(Object data) {
        assert data instanceof Data;
        BigInteger dataLength;
        try {
            dataLength = new BigInteger(((Data) data).getLength());
        } catch (RuntimeException e) {
            dataLength = null;
        }
        return dataLength;
    }

    protected String getDataSourceAttribute(Object data) {
        assert data instanceof Data;
        return ((Data) data).getSrc();
    }

    protected String getDataTypeAttribute(Object data) {
        assert data instanceof Data;
        return ((Data) data).getDataType();
    }

    protected Collection<? extends Object> getDataChunks(Object data) {
        assert data instanceof Data;
        List<Chunk> chunks = new java.util.ArrayList<Chunk>();
        for (Serializable s : getDataContent(data)) {
            if (s instanceof JAXBElement<?>) {
                Object element = ((JAXBElement<?>)s).getValue();
                if (element instanceof Chunk)
                    chunks.add((Chunk) element);
            }
        }
        return chunks;
    }

    protected Collection<? extends Object> getDataSources(Object data) {
        assert data instanceof Data;
        List<Source> sources = new java.util.ArrayList<Source>();
        for (Serializable s : getDataContent(data)) {
            if (s instanceof JAXBElement<?>) {
                Object element = ((JAXBElement<?>)s).getValue();
                if (element instanceof Source)
                    sources.add((Source) element);
            }
        }
        return sources;
    }

    protected String getDataText(Object data) {
        assert data instanceof Data;
        StringBuffer sb = new StringBuffer();
        for (Serializable s : getDataContent(data)) {
            if (s instanceof String)
                sb.append(s);
        }
        return sb.toString();
    }

    protected Collection<Serializable> getDataContent(Object data) {
        assert data instanceof Data;
        return ((Data) data).getContent();
    }

    protected boolean verifyDataContent(Serializable content, Object parent, com.skynav.ttv.model.value.Data data, DataEncoding encoding, int length) {
        if (content instanceof JAXBElement<?>) {
            Object element = ((JAXBElement<?>)content).getValue();
            if (isMetadataItem(element))
                return verifyMetadata(element);
            else if (element instanceof Chunk)
                return verifyChunk(element);
            else if (element instanceof Source)
                return verifySource(element);
            else
                return unexpectedContent(element);
        } else if (content instanceof String) {
            boolean failed = false;
            VerifierContext context = getContext();
            Reporter reporter = context.getReporter();
            Message message = null;
            if (encoding == DataEncoding.BASE_64) {
                try {
                    Base64.decode((String) content);
                } catch (IllegalArgumentException e) {
                    Locator locator = getLocation(parent).getLocator();
                    message = reporter.message(locator, "*KEY*",
                        "Content of ''{0}'' element does not conform to Base64 encoding: {1}", context.getBindingElementName(parent), e.getMessage());
                    failed = true;
                }
            }
            if (message != null) {
                reporter.logError(message);
            }
            return !failed;
        } else
            return unexpectedContent(content);
    }

    protected boolean verifyFont(Object font) {
        boolean failed = false;
        if (!verifyParameterAttributes(font))
            failed = true;
        if (!verifyFontAttributes(font))
            failed = true;
        if (!verifyStyleAttributes(font))
            failed = true;
        if (!verifyOtherAttributes(font))
            failed = true;
        for (Object m : getFontMetadata(font)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object s : getFontSources(font)) {
            if (!verifySource(s))
                failed = true;
        }
        if (!verifyStyledItem(font))
            failed = true;
        if (!failed && !verifyFontResources(font))
            failed = true;
        return !failed;
    }

    protected boolean verifyFontAttributes(Object font) {
        boolean failed = false;
        VerifierContext context = getContext();
        Location location = getLocation(font);
        // @family
        String family = getFontFamilyAttribute(font);
        if (family != null) {
            if (!verifyFontFamilyAttribute(family, fontFamilyAttributeName, font, location, context))
                failed = true;
        }
        // @format
        String format = getFontFormatAttribute(font);
        if (format != null) {
            if (!verifyResourceFormatAttribute(format, resourceFormatAttributeName, font, location, context))
                failed = true;
        }
        // @range
        String range = getFontRangeAttribute(font);
        if (range != null) {
            if (!verifyFontRangeAttribute(range, fontRangeAttributeName, font, location, context))
                failed = true;
        }
        // @src         - schema validation only, @seealso #verifyFontResources
        // @style       - schema validation only, do not set default
        // @type        - @seealso #verifyFontResources
        String type = getFontTypeAttribute(font);
        if (type != null) {
            if (!verifyResourceTypeAttribute(type, resourceTypeAttributeName, font, location, context))
                failed = true;
        }
        // @weight      - schema validation only, do not set default
        return !failed;
    }

    private boolean verifyFontFamilyAttribute(String value, QName name, Object font, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        if (!verifyNonEmptyOrPadded(font, name, value, locator, context))
            failed = true;
        else {
            Object[] treatments = new Object[] { QuotedGenericFontFamilyTreatment.Allow };
            FontFamily[] outputFontFamily = new FontFamily[1];
            if (!Fonts.isFontFamily(value, location, context, treatments, null)) {
                Fonts.badFontFamily(value, location, context, treatments);
                failed = true;
            } else if (Fonts.isGenericFontFamily(value, location, context, treatments, null)) {
                // [TBD] - not yet in spec
                reporter.logInfo(reporter.message(locator, "*KEY*",
                    "Attribute {1} on {2} element does not permit generic font family ''{0}''.",
                    value, name, context.getBindingElementName(font)));
                failed = true;
            } else if (Fonts.isQuotedFontFamily(value, location, context, treatments, outputFontFamily)) {
                FontFamily fontFamily = outputFontFamily[0];
                if ((fontFamily != null) && (fontFamily.getType() == FontFamily.Type.Quoted)) {
                    String family = fontFamily.toString();
                    if (Fonts.isGenericFontFamily(family, location, context, treatments, null)) {
                        // [TBD] - not yet in spec
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Attribute {1} on {2} element does not permit quoted generic font family ''{0}''.",
                            family, name, context.getBindingElementName(font)));
                        failed = true;
                    }
                }
            }
        }
        if (failed)
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
        return !failed;
    }

    private boolean verifyFontRangeAttribute(String value, QName name, Object font, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        if (!verifyNonEmptyOrPadded(font, name, value, locator, context))
            failed = true;
        else if (!Characters.isCharacterRange(value, location, context, null)) {
            Characters.badCharacterRange(value, location, context);
            failed = true;
        }
        if (failed)
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
        return !failed;
    }

    private boolean verifyResourceFormatAttribute(String value, QName name, Object font, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        if (!verifyNonEmptyOrPadded(font, name, value, locator, context))
            failed = true;
        else if (!ResourceFormats.isFormat(value, location, context, null)) {
            ResourceFormats.badFormat(value, location, context);
            failed = true;
        }
        if (failed)
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
        return !failed;
    }

    private boolean verifyResourceTypeAttribute(String value, QName name, Object font, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        if (!verifyNonEmptyOrPadded(font, name, value, locator, context))
            failed = true;
        else if (!ResourceTypes.isType(value, location, context, null)) {
            ResourceTypes.badType(value, location, context);
            failed = true;
        }
        if (failed)
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid {0} value ''{1}''.", name, value));
        return !failed;
    }

    protected String getFontFamilyAttribute(Object font) {
        assert font instanceof Font;
        return ((Font) font).getFamily();
    }

    protected String getFontFormatAttribute(Object font) {
        assert font instanceof Font;
        return null;
    }

    protected String getFontRangeAttribute(Object font) {
        assert font instanceof Font;
        return ((Font) font).getRange();
    }

    protected String getFontSourceAttribute(Object font) {
        assert font instanceof Font;
        return ((Font) font).getSrc();
    }

    protected String getFontTypeAttribute(Object font) {
        assert font instanceof Font;
        return ((Font) font).getDataType();
    }

    protected Collection<? extends Object> getFontMetadata(Object font) {
        assert font instanceof Font;
        return ((Font) font).getMetadataClass();
    }

    protected Collection<? extends Object> getFontSources(Object font) {
        assert font instanceof Font;
        return ((Font) font).getSource();
    }

    protected boolean verifyFontResources(Object font) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(font);
        Locator locator = location.getLocator();
        // @src
        String src = getFontSourceAttribute(font);
        int numSources = getFontSources(font).size();
        com.skynav.ttv.model.value.Font[] outputFont = new com.skynav.ttv.model.value.Font[1];
        com.skynav.ttv.model.value.Font f = null;
        if (src != null) {
            if (!Fonts.isFont(src, location, context, false, outputFont)) {
                Fonts.badFont(src, location, context);
                failed = true;
            } else if (numSources > 0) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "No ''{0}'' child permitted when ''{1}'' attribute is specified.", sourceElementName, resourceSourceAttributeName));
                failed = true;
            } else {
                f = outputFont[0];
            }
        } else if (numSources == 0) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "At least one ''{0}'' child is required when ''{1}'' attribute is not specified.", sourceElementName, resourceSourceAttributeName));
            failed = true;
        }
        // @type
        String type = getFontTypeAttribute(font);
        if (type != null) {
            if ((f == null) || !f.isExternal()) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute must not specified for internal font sources.", resourceTypeAttributeName));
                failed = true;
            }
        } else if ((f != null) && f.isExternal()) {
            if (reporter.isWarningEnabled("missing-type-for-external-source")) {
                Message message = reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute should be specified for external font sources.", resourceTypeAttributeName);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        // [TBD] verify resources referenced by tt:source children
        return !failed;
    }

    protected boolean verifyImage(Object image) {
        boolean failed = false;
        if (!verifyParameterAttributes(image))
            failed = true;
        if (!verifyImageAttributes(image))
            failed = true;
        if (!verifyStyleAttributes(image))
            failed = true;
        if (!verifyOtherAttributes(image))
            failed = true;
        for (Object m : getImageMetadata(image)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        for (Object s : getImageSources(image)) {
            if (!verifySource(s))
                failed = true;
        }
        if (!verifyStyledItem(image))
            failed = true;
        if (!failed && !verifyImageResources(image))
            failed = true;
        return !failed;
    }

    protected boolean verifyImageAttributes(Object image) {
        boolean failed = false;
        VerifierContext context = getContext();
        Reporter reporter = context.getReporter();
        Location location = getLocation(image);
        Locator locator = location.getLocator();
        // @src
        String src = getImageSourceAttribute(image);
        int numSources = getImageSources(image).size();
        com.skynav.ttv.model.value.Image[] outputImage = new com.skynav.ttv.model.value.Image[1];
        com.skynav.ttv.model.value.Image i = null;
        if (src != null) {
            if (!Images.isImage(src, location, context, false, outputImage)) {
                Images.badImage(src, location, context);
                failed = true;
            } else if (numSources > 0) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "No ''{0}'' child permitted when ''{1}'' attribute is specified.", sourceElementName, resourceSourceAttributeName));
                failed = true;
            } else {
                i = outputImage[0];
            }
        } else if (numSources == 0) {
            reporter.logError(reporter.message(locator, "*KEY*",
                "At least one ''{0}'' child is required when ''{1}'' attribute is not specified.", sourceElementName, resourceSourceAttributeName));
            failed = true;
        }
        // @type
        String type = getImageTypeAttribute(image);
        if (type != null) {
            if ((i == null) || !i.isExternal()) {
                reporter.logError(reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute must not specified for internal image sources.", resourceTypeAttributeName));
                failed = true;
            } else if (!ResourceTypes.isType(type, location, context, null)) {
                ResourceTypes.badType(type, location, context);
                failed = true;
            }
        } else if ((i != null) && i.isExternal()) {
            if (reporter.isWarningEnabled("missing-type-for-external-source")) {
                Message message = reporter.message(locator, "*KEY*",
                    "A ''{0}'' attribute should be specified for external image sources.", resourceTypeAttributeName);
                if (reporter.logWarning(message)) {
                    reporter.logError(message);
                    failed = true;
                }
            }
        }
        // @format
        String format = getImageFormatAttribute(image);
        if (format != null) {
            if (!ResourceFormats.isFormat(format, location, context, null)) {
                ResourceFormats.badFormat(format, location, context);
                failed = true;
            }
        }
        return !failed;
    }

    protected String getImageFormatAttribute(Object image) {
        assert image instanceof Image;
        return null;
    }

    protected String getImageSourceAttribute(Object image) {
        assert image instanceof Image;
        return ((Image) image).getSrc();
    }

    protected String getImageTypeAttribute(Object image) {
        assert image instanceof Image;
        return ((Image) image).getDataType();
    }

    protected Collection<? extends Object> getImageMetadata(Object image) {
        assert image instanceof Image;
        return ((Image) image).getMetadataClass();
    }

    protected Collection<? extends Object> getImageSources(Object image) {
        assert image instanceof Image;
        return ((Image) image).getSource();
    }

    protected boolean verifyImageResources(Object image) {
        boolean failed = false;
        VerifierContext context = getContext();
        Location location = getLocation(image);
        // @src
        String s = getImageSourceAttribute(image);
        if ((s != null) && !Images.isImage(s, location, context, true, null))
            failed = true;
        // [TBD] verify resources referenced by tt:source children
        return !failed;
    }

    protected boolean verifySource(Object source) {
        boolean failed = false;
        if (!verifyParameterAttributes(source))
            failed = true;
        if (!verifySourceAttributes(source))
            failed = true;
        if (!verifyOtherAttributes(source))
            failed = true;
        for (Object m : getSourceMetadata(source)) {
            if (!verifyMetadataItem(m))
                failed = true;
        }
        Object data = getSourceData(source);
        if (data != null) {
            if (!verifyData(data))
                failed = true;
        }
        return !failed;
    }

    protected boolean verifySourceAttributes(Object source) {
        // [TBD] - src
        // [TBD] - type
        // [TBD] - format
        return true;
    }

    protected Collection<? extends Object> getSourceMetadata(Object source) {
        assert source instanceof Source;
        return ((Source) source).getMetadataClass();
    }

    protected Data getSourceData(Object source) {
        assert source instanceof Source;
        return ((Source) source).getData();
    }

    protected boolean verifyItem(Object item) {
        boolean failed = false;
        if (!verifyParameterAttributes(item))
            failed = true;
        if (!verifyOtherAttributes(item))
            failed = true;
        if (!verifyMetadataItem(metadataVerifier, item))
            failed = true;
        return !failed;
    }

}
