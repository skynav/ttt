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

package com.skynav.cap2tt.converter.imsc;

import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.List;
import java.util.Map;

import com.skynav.cap2tt.converter.AbstractResourceConverterState;
import com.skynav.cap2tt.converter.Attribute;
import com.skynav.cap2tt.converter.ResourceConverter;
import com.skynav.cap2tt.converter.Screen;

import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.Layout;
import com.skynav.ttv.model.ttml2.tt.ObjectFactory;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.Styling;
import com.skynav.ttv.model.ttml2.ttd.AnnotationPosition;
import com.skynav.ttv.model.ttml2.ttd.TextAlign;

import static com.skynav.cap2tt.app.Converter.*;

public class IMSC11ResourceConverterState extends AbstractResourceConverterState {

    private ObjectFactory ttmlFactory;      // jaxb object factory
    private Division division;              // current division being constructed
    private Paragraph paragraph;            // current pargraph being constructed
    private Map<String,Region> regions;     // active regions

    public IMSC11ResourceConverterState(ResourceConverter converter, ObjectFactory ttmlFactory) {
        super(converter);
        this.ttmlFactory = ttmlFactory;
        this.division = ttmlFactory.createDivision();
        this.regions = new java.util.TreeMap<String,Region>();
    }

    public void populate(Head head) {
        // styling
        Styling styling = ttmlFactory.createStyling();
        head.setStyling(styling);
        // layout
        Layout layout = ttmlFactory.createLayout();
        for (Region r : regions.values())
            layout.getRegion().add(r);
        head.setLayout(layout);
    }

    public void populate(Body body, String defaultRegion) {
        if (hasParagraph()) {
            if (defaultRegion != null) {
                body.getOtherAttributes().put(regionAttrName, defaultRegion);
                maybeAddRegion(defaultRegion);
                this.defaultRegion = defaultRegion;
            }
            if (defaultWhitespace != null)
                body.getOtherAttributes().put(xmlSpaceAttrName, defaultWhitespace);
            body.getDiv().add(division);
        }
    }

    private boolean hasParagraph() {
        return !division.getBlockOrEmbeddedClass().isEmpty();
    }

    public void process(Screen s) {
        Paragraph p = this.paragraph;
        if (isNonContinuation(s)) {
            Paragraph pNew = populate(division, p);
            if (s != null) {
                String begin;
                String end;
                if (s.hasInOutCodes()) {
                    begin = s.getInTimeExpression();
                    end = s.getOutTimeExpression();
                } else {
                    begin = p.getBegin();
                    end = p.getEnd();
                }
                pNew.setBegin(begin);
                pNew.setEnd(end);
                resetScreenAttributes();
                populateStyles(pNew, mergeDefaults(s.getAttributes()), this.defaultRegion);
                populateText(pNew, s.getText(), false, getBlockDirection(s));
                updateScreenAttributes(s);
            }
            this.paragraph = pNew;
        } else {
            populateText(p, s.getText(), true, getBlockDirection(s));
        }
    }

    private Direction getBlockDirection(Screen s) {
        Direction d;
        String p = getPlacement(s, null);
        if (p == null)
            p = placement;
        if (p == null)
            p = globalPlacement;
        if (p.startsWith("縦"))
            d = Direction.RL;
        else
            d = Direction.TB;
        return d;
    }

    private Paragraph populate(Division d, Paragraph p) {
        if ((p != null) && (p.getContent().size() > 0)) {
            maybeWrapContentInSpan(p);
            maybeAddRegion(p.getOtherAttributes().get(regionAttrName));
            d.getBlockOrEmbeddedClass().add(p);
        }
        return ttmlFactory.createParagraph();
    }

    private void maybeAddRegion(String id) {
        if (id != null) {
            if (!regions.containsKey(id)) {
                Region r = ttmlFactory.createRegion();
                r.setId(id);
                populateStyles(r, id);
                regions.put(id, r);
            }
        }
    }

    private void populateText(Paragraph p, AttributedString as, boolean insertBreakBefore, Direction blockDirection) {
        if (as != null) {
            List<Serializable> content = p.getContent();
            if (insertBreakBefore)
                content.add(ttmlFactory.createBr(ttmlFactory.createBreak()));
            AttributedCharacterIterator aci = as.getIterator();
            aci.first();
            StringBuffer sb = new StringBuffer();
            while (aci.current() != CharacterIterator.DONE) {
                int i = aci.getRunStart();
                int e = aci.getRunLimit();
                Map<AttributedCharacterIterator.Attribute,Object> attributes = aci.getAttributes();
                while (i < e) {
                    sb.append(aci.setIndex(i++));
                }
                String text = sb.toString();
                if (!text.isEmpty()) {
                    if (!attributes.isEmpty()) {
                        populateAttributedText(content, text, attributes, blockDirection);
                    } else {
                        content.add(text);
                    }
                }
                sb.setLength(0);
                aci.setIndex(e);
            }
        }
    }

    private void populateAttributedText(List<Serializable> content, String text, Map<AttributedCharacterIterator.Attribute,Object> attributes, Direction blockDirection) {
        Span sEmphasis      = null;
        Span sRuby          = null;
        Span sCombine       = null;
        Span sOuter         = null;
        int numExclusive    = 0;
        for (AttributedCharacterIterator.Attribute k : attributes.keySet()) {
            Attribute a = (Attribute) attributes.get(k);
            if (a.isEmphasis()) {
                sEmphasis = createEmphasis(text, a, blockDirection);
                ++numExclusive;
            } else if (a.isRuby()) {
                sRuby = createRuby(text, a, blockDirection);
                ++numExclusive;
            } else if (a.isCombine()) {
                sCombine = createCombine(text, a, blockDirection);
                ++numExclusive;
            } else {
                if (sOuter == null)
                    sOuter = createStyledSpan(null, a);
                else
                    sOuter = augmentStyledSpan(sOuter, a);
            }
        }
        if (numExclusive > 1)
            throw new IllegalStateException();
        if (sOuter == null) {
            if (sEmphasis != null)
                sOuter = sEmphasis;
            else if (sRuby != null)
                sOuter = sRuby;
            else if (sCombine != null)
                sOuter = sCombine;
        } else {
            Span sInner = null;
            if (sEmphasis != null)
                sInner = sEmphasis;
            else if (sRuby != null)
                sInner = sRuby;
            else if (sCombine != null)
                sInner = sCombine;
            if (sInner != null) {
                sOuter.getContent().add(ttmlFactory.createSpan(sInner));
            } else {
                sOuter.getContent().add(text);
            }
        }
        if (sOuter != null)
            content.add(ttmlFactory.createSpan(sOuter));
    }

    private Span createEmphasis(String text, Attribute a, Direction blockDirection) {
        Span s = ttmlFactory.createSpan();
        StringBuffer sb = new StringBuffer();
        sb.append("dot");
        AnnotationPosition rp = a.getRubyPosition(blockDirection);
        if ((rp != null) && (rp != AnnotationPosition.OUTSIDE)) {
            sb.append(' ');
            sb.append(rp.name().toLowerCase());
        }
        s.getOtherAttributes().put(ttsTextEmphasisAttrName, sb.toString());
        s.getContent().add(text);
        return s;
    }

    private Span createRuby(String text, Attribute a, Direction blockDirection) {
        Span sBase = ttmlFactory.createSpan();
        sBase.getOtherAttributes().put(ttsRubyAttrName, "base");
        sBase.getContent().add(text);
        Span sText = ttmlFactory.createSpan();
        sText.getOtherAttributes().put(ttsRubyAttrName, "text");
        sText.getContent().add(a.getAnnotation());
        AnnotationPosition rp = a.getRubyPosition(blockDirection);
        if (rp != null)
            sText.setRubyPosition(rp);
        Span sCont = ttmlFactory.createSpan();
        sCont.getOtherAttributes().put(ttsRubyAttrName, "container");
        sCont.getContent().add(ttmlFactory.createSpan(sBase));
        sCont.getContent().add(ttmlFactory.createSpan(sText));
        return sCont;
    }

    private Span createCombine(String text, Attribute a, Direction blockDirection) {
        if (blockDirection != Direction.TB) {
            Span s = ttmlFactory.createSpan();
            s.getOtherAttributes().put(ttsTextCombineAttrName, "all");
            s.getContent().add(text);
            return s;
        } else
            return createStyledSpan(text, a);
    }

    private Span createStyledSpan(String text, Attribute a) {
        Span s = ttmlFactory.createSpan();
        populateStyles(s, a);
        if (text != null)
            s.getContent().add(text);
        return s;
    }

    private Span augmentStyledSpan(Span s, Attribute a) {
        populateStyles(s, a);
        return s;
    }

    private void populateStyles(Region r, String id) {
    }

    private void populateStyles(Paragraph p, List<Attribute> attributes, String defaultRegion) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                a.populate(p, styles, defaultRegion);
            }
        }
    }

    private void populateStyles(Span s, Attribute a) {
        a.populate(s, styles);
    }

    private void maybeWrapContentInSpan(Paragraph p) {
        String a = (alignment != null) ? alignment : globalAlignment;
        if (isMixedAlignment(a)) {
            TextAlign sa, pa;
            if (a.equals("中頭")) {
                pa = TextAlign.CENTER;
                sa = TextAlign.START;
            } else if (a.equals("中末")) {
                pa = TextAlign.CENTER;
                sa = TextAlign.END;
            } else {
                pa = TextAlign.CENTER;
                sa = null;
            }
            if (sa != null) {
                Span s = ttmlFactory.createSpan();
                s.getContent().addAll(p.getContent());
                s.setTextAlign(sa);
                p.getContent().clear();
                p.getContent().add(ttmlFactory.createSpan(s));
                p.setTextAlign(pa);
            }
        }
    }

}

// Local Variables:
// coding: utf-8-unix
// End:
