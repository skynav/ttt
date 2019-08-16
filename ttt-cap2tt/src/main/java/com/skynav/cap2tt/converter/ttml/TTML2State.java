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

package com.skynav.cap2tt.converter.ttml;

import java.io.Serializable;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.text.CharacterIterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.cap2tt.converter.Attribute;
import com.skynav.cap2tt.converter.AttributeSpecification;
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

public class TTML2State {
    private ObjectFactory ttmlFactory;      // jaxb object factory
    private ResourceConverter converter;    // converter
    private Division division;              // current division being constructed
    private Paragraph paragraph;            // current pargraph being constructed
    private String globalPlacement;         // global placement
    private String placement;               // current screen placement
    private String globalAlignment;         // global alignment
    private String alignment;               // current screen alignment
    private String globalShear;             // global italics
    private String shear;                   // current screen shear
    private String globalKerning;           // global kerning
    private String kerning;                 // current screen kerning
    private String globalTypeface;          // global typeface
    private String typeface;                // current screen kerning
    private String defaultRegion;           // default region
    private String defaultWhitespace;       // default whitespace
    private Map<String,Region> regions;     // active regions
    private Set<QName> styles;              // styles
    public TTML2State(ObjectFactory ttmlFactory, ResourceConverter converter) {
        this.ttmlFactory = ttmlFactory;
        this.converter = converter;
        this.division = ttmlFactory.createDivision();
        this.globalPlacement = converter.getOption("defaultPlacement");
        this.globalAlignment = converter.getOption("defaultAlignment");
        this.globalShear = converter.getOption("defaultShear");
        this.globalKerning = converter.getOption("defaultKerning");
        this.globalTypeface = converter.getOption("defaultTypeface");
        this.defaultRegion = converter.getOption("defaultRegion");
        this.defaultWhitespace = converter.getOption("defaultWhitespace");
        this.regions = new java.util.TreeMap<String,Region>();
        this.styles = new java.util.HashSet<QName>();
    }
    public void process(List<Screen> screens) {
        for (Screen s: screens)
            process(s);
        finish();
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
    private void finish() {
        process((Screen) null);
    }
    private boolean hasParagraph() {
        return !division.getBlockOrEmbeddedClass().isEmpty();
    }
    private void process(Screen s) {
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
    private void resetScreenAttributes() {
        placement = globalPlacement;
        shear = globalShear;
        kerning = globalKerning;
        typeface = globalTypeface;
    }
    private void updateScreenAttributes(Screen s) {
        boolean global[] = new boolean[1];
        placement = getPlacement(s, global);
        if (global[0])
            globalPlacement = placement;
        alignment = getAlignment(s, global);
        if (global[0])
            globalAlignment = alignment;
        shear = getShear(s, global);
        if (global[0])
            globalShear = shear;
        kerning = getKerning(s, global);
        if (global[0])
            globalKerning = kerning;
        typeface = getTypeface(s, global);
        if (global[0])
            globalTypeface = typeface;
    }
    private String getPlacement(Screen s, boolean[] retGlobal) {
        return s.getPlacement(retGlobal);
    }
    private String getAlignment(Screen s, boolean[] retGlobal) {
        return s.getAlignment(retGlobal);
    }
    private String getShear(Screen s, boolean[] retGlobal) {
        return s.getShear(retGlobal);
    }
    private String getKerning(Screen s, boolean[] retGlobal) {
        return s.getKerning(retGlobal);
    }
    private String getTypeface(Screen s, boolean[] retGlobal) {
        return s.getTypeface(retGlobal);
    }
    private boolean isNonContinuation(Screen s) {
        if (s == null)                                                              // special 'final' screen, never treat as continuation
            return true;
        else if (!s.sameNumberAsLastScreen())                                       // a screen with a different number is considered a non-continuation
            return true;
        else if (s.hasInOutCodes())                                                 // a screen with time codes is considered a non-continuation
            return true;
        else if (isNewPlacement(s))                                                 // a screen with new placement is considered a non-continuation
            return true;
        else if (isNewAlignment(s))                                                 // a screen with new alignment is considered a non-continuation
            return true;
        else if (isNewShear(s))                                                     // a screen with new shear is considered a non-continuation
            return true;
        else if (isNewKerning(s))                                                   // a screen with new kerning is considered a non-continuation
            return true;
        else if (isNewTypeface(s))                                                  // a screen with new typeface is considered a non-continuation
            return true;
        else
            return false;
    }
    private boolean isNewPlacement(Screen s) {
        String newPlacement = s.getPlacement(null);
        if (newPlacement != null) {
            if ((placement != null) || !newPlacement.equals(placement))
                return true;                                                        // new placement is different from current placement
            else
                return false;                                                       // new placement is same as current placement, treat as continuation
        } else {
            return false;                                                           // new placement not specified, treat as continuation
        }
    }
    private boolean isNewAlignment(Screen s) {
        String newAlignment = s.getAlignment(null);
        if (newAlignment != null) {
            if ((alignment != null) || !newAlignment.equals(alignment))
                return true;                                                        // new alignment is different from current alignment
            else
                return false;                                                       // new alignment is same as current alignment, treat as continuation
        } else {
            return false;                                                           // new alignment not specified, treat as continuation
        }
    }
    private boolean isNewShear(Screen s) {
        String newShear = s.getShear(null);
        if (newShear != null) {
            if ((shear != null) || !newShear.equals(shear))
                return true;                                                        // new shear is different from current shear
            else
                return false;                                                       // new shear is same as current shear, treat as continuation
        } else {
            return false;                                                           // new shear not specified, treat as continuation
        }
    }
    private boolean isNewKerning(Screen s) {
        String newKerning = s.getKerning(null);
        if (newKerning != null) {
            if ((kerning != null) || !newKerning.equals(kerning))
                return true;                                                        // new kerning is different from current kerning
            else
                return false;                                                       // new kerning is same as current kerning, treat as continuation
        } else {
            return false;                                                           // new kerning not specified, treat as continuation
        }
    }
    private boolean isNewTypeface(Screen s) {
        String newTypeface = s.getTypeface(null);
        if (newTypeface != null) {
            if ((typeface != null) || !newTypeface.equals(typeface))
                return true;                                                        // new typeface is different from current typeface
            else
                return false;                                                       // new typeface is same as current typeface, treat as continuation
        } else {
            return false;                                                           // new typeface not specified, treat as continuation
        }
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
    private List<Attribute> mergeDefaults(List<Attribute> attributes) {
        boolean hasAlignment = false;
        boolean hasKerning = false;
        boolean hasPlacement = false;
        boolean hasShear = false;
        boolean hasTypeface = false;
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasAlignment())
                    hasAlignment = true;
                if (a.hasKerning())
                    hasKerning = true;
                if (a.hasPlacement())
                    hasPlacement = true;
                if (a.hasShear())
                    hasShear = true;
                if (a.hasTypeface())
                    hasTypeface = true;
            }
        }
        if (hasAlignment && hasKerning && hasPlacement && hasShear && hasTypeface)
            return attributes;
        Map<String, AttributeSpecification> knownAttributes = converter.getKnownAttributes();
        List<Attribute> mergedAttributes = attributes != null ? new java.util.ArrayList<Attribute>(attributes) : new java.util.ArrayList<Attribute>();
        if (!hasAlignment) {
            String v = alignment;
            if (v == null)
                v = globalAlignment;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        if (!hasKerning) {
            String v = kerning;
            if (v == null)
                v = globalKerning;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get("詰");
                if (as != null) {
                    int count;
                    try {
                        count = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        count = -1;
                    }
                    mergedAttributes.add(new Attribute(as, count, false));
                }
            }
        }
        if (!hasPlacement) {
            String v = placement;
            if (v == null)
                v = globalPlacement;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        if (!hasShear) {
            String v = shear;
            if (v == null)
                v = globalShear;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get("斜");
                if (as != null) {
                    int count;
                    try {
                        count = Integer.parseInt(v);
                    } catch (NumberFormatException e) {
                        count = -1;
                    }
                    mergedAttributes.add(new Attribute(as, count, false));
                }
            }
        }
        if (!hasTypeface) {
            String v = typeface;
            if (v == null)
                v = globalTypeface;
            if (v != null) {
                AttributeSpecification as = knownAttributes.get(v);
                if (as != null)
                    mergedAttributes.add(new Attribute(as, -1, false));
            }
        }
        return mergedAttributes;
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
    private boolean isMixedAlignment(String alignment) {
        if (alignment == null)
            return false;
        else if (alignment.equals("中頭"))
            return true;
        else if (alignment.equals("中末"))
            return true;
        else
            return false;
    }
}

// Local Variables:
// coding: utf-8-unix
// End:
