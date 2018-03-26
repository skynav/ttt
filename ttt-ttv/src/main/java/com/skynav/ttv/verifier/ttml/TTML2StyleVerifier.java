/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.ttml2.ttd.AnnotationPosition;
import com.skynav.ttv.model.ttml2.ttd.AreaRectangle;
import com.skynav.ttv.model.ttml2.ttd.BackgroundRepeat;
import com.skynav.ttv.model.ttml2.ttd.Direction;
import com.skynav.ttv.model.ttml2.ttd.Display;
import com.skynav.ttv.model.ttml2.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml2.ttd.FontKerning;
import com.skynav.ttv.model.ttml2.ttd.FontStyle;
import com.skynav.ttv.model.ttml2.ttd.FontWeight;
import com.skynav.ttv.model.ttml2.ttd.Overflow;
import com.skynav.ttv.model.ttml2.ttd.Ruby;
import com.skynav.ttv.model.ttml2.ttd.RubyAlign;
// import com.skynav.ttv.model.ttml2.ttd.RubyOverflow;
// import com.skynav.ttv.model.ttml2.ttd.RubyOverhang;
import com.skynav.ttv.model.ttml2.ttd.ShowBackground;
import com.skynav.ttv.model.ttml2.ttd.TextAlign;
import com.skynav.ttv.model.ttml2.ttd.TextDecoration;
import com.skynav.ttv.model.ttml2.ttd.TextOrientation;
import com.skynav.ttv.model.ttml2.ttd.UnicodeBidi;
import com.skynav.ttv.model.ttml2.ttd.Visibility;
import com.skynav.ttv.model.ttml2.ttd.WrapOption;
import com.skynav.ttv.model.ttml2.ttd.WritingMode;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.style.AreaRectangleVerifier;
import com.skynav.ttv.verifier.ttml.style.BackgroundImageVerifier;
import com.skynav.ttv.verifier.ttml.style.BackgroundPositionVerifier;
import com.skynav.ttv.verifier.ttml.style.BackgroundRepeatVerifier;
import com.skynav.ttv.verifier.ttml.style.BorderVerifier;
import com.skynav.ttv.verifier.ttml.style.DirectionVerifier;
import com.skynav.ttv.verifier.ttml.style.DisparityVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayVerifier;
import com.skynav.ttv.verifier.ttml.style.ExtentVerifier;
import com.skynav.ttv.verifier.ttml.style.FontKerningVerifier;
//import com.skynav.ttv.verifier.ttml.style.FontSelectionStrategyVerifier;
import com.skynav.ttv.verifier.ttml.style.FontShearVerifier;
import com.skynav.ttv.verifier.ttml.style.FontStyleVerifier;
import com.skynav.ttv.verifier.ttml.style.FontVariantVerifier;
import com.skynav.ttv.verifier.ttml.style.FontWeightVerifier;
import com.skynav.ttv.verifier.ttml.style.LetterSpacingVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OriginVerifier;
import com.skynav.ttv.verifier.ttml.style.OverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.PaddingVerifier;
import com.skynav.ttv.verifier.ttml.style.PositionVerifier;
import com.skynav.ttv.verifier.ttml.style.ProgressionDimensionVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyOffsetVerifier;
// import com.skynav.ttv.verifier.ttml.style.RubyOverflowVerifier;
// import com.skynav.ttv.verifier.ttml.style.RubyOverhangClassVerifier;
// import com.skynav.ttv.verifier.ttml.style.RubyOverhangVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyPositionVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyReserveVerifier;
import com.skynav.ttv.verifier.ttml.style.RubyVerifier;
import com.skynav.ttv.verifier.ttml.style.ScriptVerifier;
import com.skynav.ttv.verifier.ttml.style.ShowBackgroundVerifier;
import com.skynav.ttv.verifier.ttml.style.TextAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.TextCombineVerifier;
import com.skynav.ttv.verifier.ttml.style.TextDecorationVerifier;
import com.skynav.ttv.verifier.ttml.style.TextEmphasisVerifier;
import com.skynav.ttv.verifier.ttml.style.TextOrientationVerifier;
import com.skynav.ttv.verifier.ttml.style.TextShadowVerifier;
import com.skynav.ttv.verifier.ttml.style.UnicodeBidiVerifier;
import com.skynav.ttv.verifier.ttml.style.VisibilityVerifier;
import com.skynav.ttv.verifier.ttml.style.WrapOptionVerifier;
import com.skynav.ttv.verifier.ttml.style.WritingModeVerifier;

public class TTML2StyleVerifier extends TTML1StyleVerifier {

    public static final QName backgroundClipAttributeName               = new QName(NAMESPACE,"backgroundClip");
    public static final QName backgroundExtentAttributeName             = new QName(NAMESPACE,"backgroundExtent");
    public static final QName backgroundImageAttributeName              = new QName(NAMESPACE,"backgroundImage");
    public static final QName backgroundOriginAttributeName             = new QName(NAMESPACE,"backgroundOrigin");
    public static final QName backgroundPositionAttributeName           = new QName(NAMESPACE,"backgroundPosition");
    public static final QName backgroundRepeatAttributeName             = new QName(NAMESPACE,"backgroundRepeat");
    public static final QName borderAttributeName                       = new QName(NAMESPACE,"border");
    public static final QName bpdAttributeName                          = new QName(NAMESPACE,"bpd");
    public static final QName disparityAttributeName                    = new QName(NAMESPACE,"disparity");
    public static final QName fontKerningAttributeName                  = new QName(NAMESPACE,"fontKerning");
    public static final QName fontSelectionStrategyAttributeName        = new QName(NAMESPACE,"fontSelectionStrategy");
    public static final QName fontShearAttributeName                    = new QName(NAMESPACE,"fontShear");
    public static final QName fontVariantAttributeName                  = new QName(NAMESPACE,"fontVariant");
    public static final QName ipdAttributeName                          = new QName(NAMESPACE,"ipd");
    public static final QName letterSpacingAttributeName                = new QName(NAMESPACE,"letterSpacing");
    public static final QName positionAttributeName                     = new QName(NAMESPACE,"position");
    public static final QName rubyAttributeName                         = new QName(NAMESPACE,"ruby");
    public static final QName rubyAlignAttributeName                    = new QName(NAMESPACE,"rubyAlign");
    public static final QName rubyOffsetAttributeName                   = new QName(NAMESPACE,"rubyOffset");
    // public static final QName rubyOverflowAttributeName                 = new QName(NAMESPACE,"rubyOverflow");
    // public static final QName rubyOverhangAttributeName                 = new QName(NAMESPACE,"rubyOverhang");
    // public static final QName rubyOverhangClassAttributeName            = new QName(NAMESPACE,"rubyOverhangClass");
    public static final QName rubyPositionAttributeName                 = new QName(NAMESPACE,"rubyPosition");
    public static final QName rubyReserveAttributeName                  = new QName(NAMESPACE,"rubyReserve");
    public static final QName scriptAttributeName                       = new QName(NAMESPACE,"script");
    public static final QName textCombineAttributeName                  = new QName(NAMESPACE,"textCombine");
    public static final QName textEmphasisAttributeName                 = new QName(NAMESPACE,"textEmphasis");
    public static final QName textOrientationAttributeName              = new QName(NAMESPACE,"textOrientation");
    public static final QName textShadowAttributeName                   = new QName(NAMESPACE,"textShadow");

    private static final Object[][] styleAccessorMap                    = new Object[][] {
        {
            backgroundClipAttributeName,
            "BackgroundClip",
            AreaRectangle.class,
            AreaRectangleVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            AreaRectangle.BORDER,
            AreaRectangle.BORDER.value(),
        },
        {
            backgroundImageAttributeName,
            "BackgroundImage",
            String.class,
            BackgroundImageVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "none",
            null,
        },
        {
            backgroundOriginAttributeName,
            "BackgroundOrigin",
            AreaRectangle.class,
            AreaRectangleVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            AreaRectangle.PADDING,
            AreaRectangle.PADDING.value(),
        },
        {
            backgroundPositionAttributeName,
            "BackgroundPosition",
            String.class,
            BackgroundPositionVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "0% 0%",
            null,
        },
        {
            backgroundRepeatAttributeName,
            "BackgroundRepeat",
            BackgroundRepeat.class,
            BackgroundRepeatVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            BackgroundRepeat.REPEAT,
            BackgroundRepeat.REPEAT.value(),
        },
        {
            backgroundExtentAttributeName,
            "BackgroundExtent",
            String.class,
            ExtentVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            borderAttributeName,
            "Border",
            String.class,
            BorderVerifier.class,
            Integer.valueOf(APPLIES_TO_CONTENT|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "none",
            null,
        },
        {
            bpdAttributeName,
            "Bpd",
            String.class,
            ProgressionDimensionVerifier.class,
            Integer.valueOf(APPLIES_TO_DIV|APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            directionAttributeName,
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
            displayAttributeName,
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
            disparityAttributeName,
            "Disparity",
            String.class,
            DisparityVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.FALSE,
            "0",
            null,
        },
        {
            displayAlignAttributeName,
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
            extentAttributeName,
            "Extent",
            String.class,
            ExtentVerifier.class,
            Integer.valueOf(APPLIES_TO_TT|APPLIES_TO_REGION|APPLIES_TO_DIV|APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            fontKerningAttributeName,
            "FontKerning",
            FontKerning.class,
            FontKerningVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontKerning.NORMAL,
            FontKerning.NORMAL.value(),
        },
        /*
        {
            fontSelectionStrategyAttributeName,
            "FontSelectionStrategy",
            FontSelectionStrategy.class,
            FontSelectionStrategyVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            FontSelectionStrategy.NORMAL,
            FontSelectionStrategy.NORMAL.value(),
        },
        */
        {
            fontShearAttributeName,
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
            fontStyleAttributeName,
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
            fontVariantAttributeName,
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
            fontWeightAttributeName,
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
            ipdAttributeName,
            "Ipd",
            String.class,
            ProgressionDimensionVerifier.class,
            Integer.valueOf(APPLIES_TO_DIV|APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            letterSpacingAttributeName,
            "LetterSpacing",
            String.class,
            LetterSpacingVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "normal",
            null,
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
            originAttributeName,
            "Origin",
            String.class,
            OriginVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION|APPLIES_TO_DIV|APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.FALSE,
            "auto",
            null,
        },
        {
            overflowAttributeName,
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
            paddingAttributeName,
            "Padding",
            String.class,
            PaddingVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION|APPLIES_TO_CONTENT),
            Boolean.FALSE,
            Boolean.FALSE,
            "0px",
            null,
        },
        {
            positionAttributeName,
            "Position",
            String.class,
            PositionVerifier.class,
            Integer.valueOf(APPLIES_TO_REGION|APPLIES_TO_DIV|APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.FALSE,
            "center",
            null,
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
            rubyAlignAttributeName,
            "RubyAlign",
            RubyAlign.class,
            RubyAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            RubyAlign.CENTER,
            RubyAlign.CENTER.value()
        },
        {
            rubyOffsetAttributeName,
            "RubyOffset",
            String.class,
            RubyOffsetVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "auto",
            null,
        },
        // {
        //     rubyOverflowAttributeName,
        //     "RubyOverflow",
        //     RubyOverflow.class,
        //     RubyOverflowVerifier.class,
        //     Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
        //     Boolean.FALSE,
        //     Boolean.TRUE,
        //     RubyOverflow.SHIFT_RUBY,
        //     RubyOverflow.SHIFT_RUBY.value()
        // },
        // {
        //     rubyOverhangAttributeName,
        //     "RubyOverhang",
        //     RubyOverhang.class,
        //     RubyOverhangVerifier.class,
        //     Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
        //     Boolean.FALSE,
        //     Boolean.TRUE,
        //     RubyOverhang.ALLOW,
        //     RubyOverhang.ALLOW.value()
        // },
        // {
        //     rubyOverhangClassAttributeName,
        //     "RubyOverhangClass",
        //     String.class,
        //     RubyOverhangClassVerifier.class,
        //     Integer.valueOf(APPLIES_TO_SPAN),
        //     Boolean.FALSE,
        //     Boolean.TRUE,
        //     "auto",
        //     null,
        // },
        {
            rubyPositionAttributeName,
            "RubyPosition",
            AnnotationPosition.class,
            RubyPositionVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            AnnotationPosition.OUTSIDE,
            AnnotationPosition.OUTSIDE.value()
        },
        {
            rubyReserveAttributeName,
            "RubyReserve",
            String.class,
            RubyReserveVerifier.class,
            Integer.valueOf(APPLIES_TO_P|APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null,
        },
        {
            scriptAttributeName,
            "Script",
            String.class,
            ScriptVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "auto",
            null,
        },
        {
            showBackgroundAttributeName,
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
            textCombineAttributeName,
            "TextCombine",
            String.class,
            TextCombineVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null,
        },
        {
            textDecorationAttributeName,
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
            textEmphasisAttributeName,
            "TextEmphasis",
            String.class,
            TextEmphasisVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null,
        },
        {
            textOrientationAttributeName,
            "TextOrientation",
            TextOrientation.class,
            TextOrientationVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            TextOrientation.MIXED,
            TextOrientation.MIXED.value(),
        },
        {
            textShadowAttributeName,
            "TextShadow",
            String.class,
            TextShadowVerifier.class,
            Integer.valueOf(APPLIES_TO_SPAN),
            Boolean.FALSE,
            Boolean.TRUE,
            "none",
            null,
        },
        {
            unicodeBidiAttributeName,
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
            visibilityAttributeName,
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
            wrapOptionAttributeName,
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
            writingModeAttributeName,
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
        populateAccessors(accessors, styleAccessorMap);
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
    public boolean isLengthUnitsRequired(QName eltName, QName styleName) {
        return false;
    }

    @Override
    protected boolean isAnimate(Object content) {
        return content instanceof Animate;
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
