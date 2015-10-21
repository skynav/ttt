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

package com.skynav.ttv.verifier.ttml;

import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML2.TTML2Model;
import com.skynav.ttv.model.ttml2.tt.Animate;
import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Break;
import com.skynav.ttv.model.ttml2.tt.Division;
import com.skynav.ttv.model.ttml2.tt.Initial;
import com.skynav.ttv.model.ttml2.tt.Paragraph;
import com.skynav.ttv.model.ttml2.tt.Region;
import com.skynav.ttv.model.ttml2.tt.Set;
import com.skynav.ttv.model.ttml2.tt.Span;
import com.skynav.ttv.model.ttml2.tt.Style;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.Direction;
import com.skynav.ttv.model.ttml2.ttd.Display;
import com.skynav.ttv.model.ttml2.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml2.ttd.FontKerning;
import com.skynav.ttv.model.ttml2.ttd.FontStyle;
import com.skynav.ttv.model.ttml2.ttd.FontWeight;
import com.skynav.ttv.model.ttml2.ttd.Overflow;
import com.skynav.ttv.model.ttml2.ttd.Ruby;
import com.skynav.ttv.model.ttml2.ttd.RubyAlign;
import com.skynav.ttv.model.ttml2.ttd.RubyOverflow;
import com.skynav.ttv.model.ttml2.ttd.RubyOverhang;
import com.skynav.ttv.model.ttml2.ttd.RubyPosition;
import com.skynav.ttv.model.ttml2.ttd.ShowBackground;
import com.skynav.ttv.model.ttml2.ttd.TextAlign;
import com.skynav.ttv.model.ttml2.ttd.TextDecoration;
import com.skynav.ttv.model.ttml2.ttd.UnicodeBidi;
import com.skynav.ttv.model.ttml2.ttd.Visibility;
import com.skynav.ttv.model.ttml2.ttd.WrapOption;
import com.skynav.ttv.model.ttml2.ttd.WritingMode;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.style.DirectionVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayVerifier;
import com.skynav.ttv.verifier.ttml.style.FontKerningVerifier;
import com.skynav.ttv.verifier.ttml.style.FontShearVerifier;
import com.skynav.ttv.verifier.ttml.style.FontStyleVerifier;
import com.skynav.ttv.verifier.ttml.style.FontVariantVerifier;
import com.skynav.ttv.verifier.ttml.style.FontWeightVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.PositionVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyOffsetVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyOverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyOverhangClassVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyOverhangVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyPositionVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyReserveVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyVerifier;
import com.skynav.ttv.verifier.ttml.style.ScriptVerifier;
import com.skynav.ttv.verifier.ttml.style.ShowBackgroundVerifier;
import com.skynav.ttv.verifier.ttml.style.TextAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.TextCombineVerifier;
import com.skynav.ttv.verifier.ttml.style.TextDecorationVerifier;
import com.skynav.ttv.verifier.ttml.style.TextEmphasisVerifier;
import com.skynav.ttv.verifier.ttml.style.UnicodeBidiVerifier;
import com.skynav.ttv.verifier.ttml.style.VisibilityVerifier;
import com.skynav.ttv.verifier.ttml.style.WrapOptionVerifier;
import com.skynav.ttv.verifier.ttml.style.WritingModeVerifier;
import java.util.Arrays;

public class TTML2StyleVerifier extends TTML1StyleVerifier {

    public static final QName rubyAttributeName                 = new QName(NAMESPACE,"ruby");

    private static final Object[][] styleAccessorMap            = new Object[][] {
        {
            new QName(NAMESPACE,"direction"),
            "Direction",
            Direction.class,
            DirectionVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            Direction.LTR,
            Direction.LTR.value(),
        },
        {
            new QName(NAMESPACE,"display"),
            "Display",
            Display.class,
            DisplayVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            Display.AUTO,
            Display.AUTO.value(),
        },
        {
            new QName(NAMESPACE,"displayAlign"),
            "DisplayAlign",
            DisplayAlign.class,
            DisplayAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            DisplayAlign.BEFORE,
            DisplayAlign.BEFORE.value(),
        },
        {
            new QName(NAMESPACE,"fontKerning"),
            "FontKerning",
            FontKerning.class,
            FontKerningVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontKerning.NORMAL,
            FontKerning.NORMAL.value(),
        },
        {
            new QName(NAMESPACE,"fontShear"),
            "FontShear",
            String.class,
            FontShearVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "0%",
            null,
        },
        {
            new QName(NAMESPACE,"fontStyle"),
            "FontStyle",
            FontStyle.class,
            FontStyleVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontStyle.NORMAL,
            FontStyle.NORMAL.value(),
        },
        {
            new QName(NAMESPACE,"fontVariant"),
            "FontVariant",
            String.class,
            FontVariantVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "normal",
            null,
        },
        {
            new QName(NAMESPACE,"fontWeight"),
            "FontWeight",
            FontWeight.class,
            FontWeightVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontWeight.NORMAL,
            FontWeight.NORMAL.value(),
        },
        {
            lineHeightAttributeName,
            "LineHeight",
            String.class,
            LineHeightVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "normal",
            null,
        },
        {
            new QName(NAMESPACE,"overflow"),
            "Overflow",
            Overflow.class,
            OverflowVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            Overflow.HIDDEN,
            Overflow.HIDDEN.value(),
        },
        {
            new QName(NAMESPACE,"position"),
            "Position",
            String.class,
            PositionVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "center",
            null
        },
        {
            rubyAttributeName,
            "Ruby",
            Ruby.class,
            RubyVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.FALSE,
            Ruby.NONE,
            Ruby.NONE.value()
        },
        {
            new QName(NAMESPACE,"rubyAlign"),
            "RubyAlign",
            RubyAlign.class,
            RubyAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            RubyAlign.AUTO,
            RubyAlign.AUTO.value()
        },
        {
            new QName(NAMESPACE,"rubyOffset"),
            "RubyOffset",
            String.class,
            RubyOffsetVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "auto",
            null
        },
        {
            new QName(NAMESPACE,"rubyOverflow"),
            "RubyOverflow",
            RubyOverflow.class,
            RubyOverflowVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            RubyOverflow.SHIFT_RUBY,
            RubyOverflow.SHIFT_RUBY.value()
        },
        {
            new QName(NAMESPACE,"rubyOverhang"),
            "RubyOverhang",
            RubyOverhang.class,
            RubyOverhangVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            RubyOverhang.ALLOW,
            RubyOverhang.ALLOW.value()
        },
        {
            new QName(NAMESPACE,"rubyOverhangClass"),
            "RubyOverhangClass",
            String.class,
            RubyOverhangClassVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "auto",
            null
        },
        {
            new QName(NAMESPACE,"rubyPosition"),
            "RubyPosition",
            RubyPosition.class,
            RubyPositionVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            RubyPosition.AUTO,
            RubyPosition.AUTO.value()
        },
        {
            new QName(NAMESPACE,"rubyReserve"),
            "RubyReserve",
            String.class,
            RubyReserveVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null
        },
        {
            new QName(NAMESPACE,"script"),
            "Script",
            String.class,
            ScriptVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "auto",
            null,
        },
        {
            new QName(NAMESPACE,"showBackground"),
            "ShowBackground",
            ShowBackground.class,
            ShowBackgroundVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            ShowBackground.ALWAYS,
            ShowBackground.ALWAYS.value(),
        },
        {
            textAlignAttributeName,
            "TextAlign",
            TextAlign.class,
            TextAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            TextAlign.START,
            TextAlign.START.value(),
        },
        {
            new QName(NAMESPACE,"textCombine"),
            "TextCombine",
            String.class,
            TextCombineVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null
        },
        {
            new QName(NAMESPACE,"textDecoration"),
            "TextDecoration",
            TextDecoration.class,
            TextDecorationVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            TextDecoration.NONE,
            TextDecoration.NONE.value(),
        },
        {
            new QName(NAMESPACE,"textEmphasis"),
            "TextEmphasis",
            String.class,
            TextEmphasisVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null
        },
        {
            new QName(NAMESPACE,"unicodeBidi"),
            "UnicodeBidi",
            UnicodeBidi.class,
            UnicodeBidiVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.FALSE,
            UnicodeBidi.NORMAL,
            UnicodeBidi.NORMAL.value(),
        },
        {
            new QName(NAMESPACE,"visibility"),
            "Visibility",
            Visibility.class,
            VisibilityVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.TRUE,
            Visibility.VISIBLE,
            Visibility.VISIBLE.value(),
        },
        {
            new QName(NAMESPACE,"wrapOption"),
            "WrapOption",
            WrapOption.class,
            WrapOptionVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            WrapOption.WRAP,
            WrapOption.WRAP.value(),
        },
        {
            new QName(NAMESPACE,"writingMode"),
            "WritingMode",
            WritingMode.class,
            WritingModeVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            WritingMode.LRTB,
            WritingMode.LRTB.value(),
        },
    };

    public TTML2StyleVerifier(Model model) {
        super(model);
    }

    @Override
    protected void populateAccessors(Map<QName, StyleAccessor> accessors) {
        super.populateAccessors(accessors);
        populateAccessors(accessors, Arrays.asList(styleAccessorMap));
    }

    @Override
    @SuppressWarnings("unchecked")
    public void addInitialOverrides(Object initial, VerifierContext context) {
        Map<QName,Object> initials = (Map<QName,Object>) context.getResourceState("initials");
        if (initials == null) {
            initials = new java.util.HashMap<QName,Object>();
            context.setResourceState("initials", initials);
        }
        for (Map.Entry<QName,StyleAccessor> e : accessors.entrySet()) {
            QName name = e.getKey();
            if (isStyleAttribute(name)) {
                StyleAccessor sa = e.getValue();
                Object value = sa.getStyleValue(initial);
                if (value != null)
                    initials.put(name, value);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object getInitialOverride(QName styleName, VerifierContext context) {
        Map<QName,Object> initials = (Map<QName,Object>) context.getResourceState("initials");
        if (initials != null)
            return initials.get(styleName);
        else
            return null;
    }

    @Override
    public boolean isInheritableStyle(QName eltName, QName styleName) {
        if (eltName.equals(TTML2Model.spanElementName) && styleName.equals(textAlignAttributeName))
            return false;
        else
            return super.isInheritableStyle(eltName, styleName);
    }

    @Override
    public String getInitialStyleValue(QName eltName, QName styleName) {
        if ((eltName != null) && eltName.equals(TTML2Model.spanElementName) && styleName.equals(textAlignAttributeName))
            return null;
        else
            return super.getInitialStyleValue(eltName, styleName);
    }

    @Override
    protected boolean isInitial(Object content) {
        return content instanceof Initial;
    }

    @Override
    protected boolean isRegion(Object content) {
        return content instanceof Region;
    }

    @Override
    protected boolean isSet(Object content) {
        return content instanceof Set;
    }

    @Override
    protected boolean isTimedText(Object content) {
        return content instanceof TimedText;
    }

    @Override
    protected boolean permitsStyleAttribute(Object content, QName name) {
        if (content instanceof Animate)
            return true;
        else if (content instanceof Body)
            return true;
        else if (content instanceof Division)
            return true;
        else if (content instanceof Initial)
            return true;
        else if (content instanceof Paragraph)
            return true;
        else if (content instanceof Span)
            return true;
        else if (content instanceof Break)
            return true;
        else if (content instanceof Style)
            return true;
        else if (content instanceof Region)
            return true;
        else if (content instanceof Set)
            return true;
        else if (content instanceof TimedText)
            return true;
        else
            return false;
    }

}
