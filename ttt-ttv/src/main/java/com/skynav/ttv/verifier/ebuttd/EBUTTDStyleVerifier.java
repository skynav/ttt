/*
 * Copyright (c) 2015, msamek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttv.verifier.ebuttd;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.ttd.Direction;
import com.skynav.ttv.model.ttml1.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml1.ttd.FontStyle;
import com.skynav.ttv.model.ttml1.ttd.FontWeight;
import com.skynav.ttv.model.ttml1.ttd.Overflow;
import com.skynav.ttv.model.ttml1.ttd.ShowBackground;
import com.skynav.ttv.model.ttml1.ttd.TextAlign;
import com.skynav.ttv.model.ttml1.ttd.TextDecoration;
import com.skynav.ttv.model.ttml1.ttd.UnicodeBidi;
import com.skynav.ttv.model.ttml1.ttd.WrapOption;
import com.skynav.ttv.model.ttml1.ttd.WritingMode;
import com.skynav.ttv.verifier.ttml.TTML1StyleVerifier;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.APPLIES_TO_CONTENT;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.APPLIES_TO_P;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.APPLIES_TO_REGION;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.APPLIES_TO_SPAN;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.APPLIES_TO_TT;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.NAMESPACE;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.extentAttributeName;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.fontSizeAttributeName;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.lineHeightAttributeName;
import static com.skynav.ttv.verifier.ttml.TTML1StyleVerifier.textAlignAttributeName;
import com.skynav.ttv.verifier.ttml.style.BackgroundColorVerifier;
import com.skynav.ttv.verifier.ttml.style.ColorVerifier;
import com.skynav.ttv.verifier.ttml.style.DirectionVerifier;
import com.skynav.ttv.verifier.ttml.style.DisplayAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.ExtentVerifier;
import com.skynav.ttv.verifier.ttml.style.FontFamilyVerifier;
import com.skynav.ttv.verifier.ttml.style.FontSizeVerifier;
import com.skynav.ttv.verifier.ttml.style.FontStyleVerifier;
import com.skynav.ttv.verifier.ttml.style.FontWeightVerifier;
import com.skynav.ttv.verifier.ttml.style.LineHeightVerifier;
import com.skynav.ttv.verifier.ttml.style.OriginVerifier;
import com.skynav.ttv.verifier.ttml.style.OverflowVerifier;
import com.skynav.ttv.verifier.ttml.style.PaddingVerifier;
import com.skynav.ttv.verifier.ttml.style.RegionAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.ShowBackgroundVerifier;
import com.skynav.ttv.verifier.ttml.style.StyleAttributeVerifier;
import com.skynav.ttv.verifier.ttml.style.TextAlignVerifier;
import com.skynav.ttv.verifier.ttml.style.TextDecorationVerifier;
import com.skynav.ttv.verifier.ttml.style.UnicodeBidiVerifier;
import com.skynav.ttv.verifier.ttml.style.WrapOptionVerifier;
import com.skynav.ttv.verifier.ttml.style.WritingModeVerifier;
import com.skynav.ttv.verifier.ttml.style.ZIndexVerifier;
import java.util.Arrays;
import java.util.List;
import javax.xml.namespace.QName;

/**
 *
 * @author msamek
 */
public class EBUTTDStyleVerifier extends TTML1StyleVerifier {

    public EBUTTDStyleVerifier(Model model) {
        super(model);
    }

    static {
        styleAccessorMap.clear();
        styleAccessorMap.addAll(Arrays.asList(new Object[][]{
            {
                new QName(NAMESPACE, "backgroundColor"), // attribute name
                "BackgroundColor", // accessor method name suffix
                String.class, // value type
                BackgroundColorVerifier.class, // specialized verifier
                Integer.valueOf(APPLIES_TO_CONTENT | APPLIES_TO_REGION), // applicability
                Boolean.FALSE, // padding permitted
                Boolean.FALSE, // inheritable
                "transparent", // initial value (as object suitable for setter)
                null, // initial value as string (or null if same as previous)
            },
            {
                new QName(NAMESPACE, "color"),
                "Color",
                String.class,
                ColorVerifier.class,
                Integer.valueOf(APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                "white",
                null,},
            {
                new QName(NAMESPACE, "direction"),
                "Direction",
                Direction.class,
                DirectionVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                Direction.LTR,
                Direction.LTR.value(),},
//            {
//                new QName(NAMESPACE, "display"),
//                "Display",
//                Display.class,
//                DisplayVerifier.class,
//                Integer.valueOf(APPLIES_TO_CONTENT | APPLIES_TO_REGION),
//                Boolean.FALSE,
//                Boolean.FALSE,
//                Display.AUTO,
//                Display.AUTO.value(),},
            {
                new QName(NAMESPACE, "displayAlign"),
                "DisplayAlign",
                DisplayAlign.class,
                DisplayAlignVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                DisplayAlign.AFTER,
                DisplayAlign.AFTER.value(),},
            {
                extentAttributeName,
                "Extent",
                String.class,
                ExtentVerifier.class,
                Integer.valueOf(APPLIES_TO_TT | APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                null,
                null,},
            {
                new QName(NAMESPACE, "fontFamily"),
                "FontFamily",
                String.class,
                FontFamilyVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.TRUE,
                Boolean.TRUE,
                "default",
                null,},
            {
                fontSizeAttributeName,
                "FontSize",
                String.class,
                FontSizeVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                "1c",
                null,},
            {
                new QName(NAMESPACE, "fontStyle"),
                "FontStyle",
                FontStyle.class,
                FontStyleVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                FontStyle.NORMAL,
                FontStyle.NORMAL.value(),},
            {
                new QName(NAMESPACE, "fontWeight"),
                "FontWeight",
                FontWeight.class,
                FontWeightVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                FontWeight.NORMAL,
                FontWeight.NORMAL.value(),},
            {
                lineHeightAttributeName,
                "LineHeight",
                String.class,
                LineHeightVerifier.class,
                Integer.valueOf(APPLIES_TO_P),
                Boolean.FALSE,
                Boolean.TRUE,
                "normal",
                null,},
//            {
//                new QName(NAMESPACE, "opacity"),
//                "Opacity",
//                Float.class,
//                OpacityVerifier.class,
//                Integer.valueOf(APPLIES_TO_REGION),
//                Boolean.FALSE,
//                Boolean.FALSE,
//                Float.valueOf(1.0F),
//                "1.0",},
            {
                new QName(NAMESPACE, "origin"),
                "Origin",
                String.class,
                OriginVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                null,
                null
            },
            {
                new QName(NAMESPACE, "overflow"),
                "Overflow",
                Overflow.class,
                OverflowVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                Overflow.VISIBLE,
                Overflow.VISIBLE.value(),},
            {
                new QName(NAMESPACE, "padding"),
                "Padding",
                String.class,
                PaddingVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                "0px",
                null,},
            {
                new QName("", "region"),
                "Region",
                Object.class,
                RegionAttributeVerifier.class,
                Integer.valueOf(APPLIES_TO_CONTENT),
                Boolean.FALSE,
                Boolean.FALSE,
                null,
                null,},
            {
                new QName(NAMESPACE, "showBackground"),
                "ShowBackground",
                ShowBackground.class,
                ShowBackgroundVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                ShowBackground.ALWAYS,
                ShowBackground.ALWAYS.value(),},
            {
                new QName("", "style"),
                "StyleAttribute",
                List.class,
                StyleAttributeVerifier.class,
                Integer.valueOf(APPLIES_TO_CONTENT),
                Boolean.FALSE,
                Boolean.FALSE,
                null,
                null,},
            {
                textAlignAttributeName,
                "TextAlign",
                TextAlign.class,
                TextAlignVerifier.class,
                Integer.valueOf(APPLIES_TO_P),
                Boolean.FALSE,
                Boolean.TRUE,
                TextAlign.CENTER,
                TextAlign.CENTER.value(),},
            {
                new QName(NAMESPACE, "textDecoration"),
                "TextDecoration",
                TextDecoration.class,
                TextDecorationVerifier.class,
                Integer.valueOf(APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                TextDecoration.NONE,
                TextDecoration.NONE.value(),},
//            {
//                new QName(NAMESPACE, "textOutline"),
//                "TextOutline",
//                String.class,
//                TextOutlineVerifier.class,
//                Integer.valueOf(APPLIES_TO_SPAN),
//                Boolean.FALSE,
//                Boolean.TRUE,
//                "none",
//                null,},
            {
                new QName(NAMESPACE, "unicodeBidi"),
                "UnicodeBidi",
                UnicodeBidi.class,
                UnicodeBidiVerifier.class,
                Integer.valueOf(APPLIES_TO_P | APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.FALSE,
                UnicodeBidi.NORMAL,
                UnicodeBidi.NORMAL.value(),},
//            {
//                new QName(NAMESPACE, "visibility"),
//                "Visibility",
//                Visibility.class,
//                VisibilityVerifier.class,
//                Integer.valueOf(APPLIES_TO_CONTENT | APPLIES_TO_REGION),
//                Boolean.FALSE,
//                Boolean.TRUE,
//                Visibility.VISIBLE,
//                Visibility.VISIBLE.value(),},
            {
                new QName(NAMESPACE, "wrapOption"),
                "WrapOption",
                WrapOption.class,
                WrapOptionVerifier.class,
                Integer.valueOf(APPLIES_TO_SPAN),
                Boolean.FALSE,
                Boolean.TRUE,
                null,
                null,},
            {
                new QName(NAMESPACE, "writingMode"),
                "WritingMode",
                WritingMode.class,
                WritingModeVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                WritingMode.LRTB,
                WritingMode.LRTB.value(),},
            {
                new QName(NAMESPACE, "zIndex"),
                "ZIndex",
                String.class,
                ZIndexVerifier.class,
                Integer.valueOf(APPLIES_TO_REGION),
                Boolean.FALSE,
                Boolean.FALSE,
                "auto",
                null,},}));
    }
}
