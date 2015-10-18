/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import javax.xml.namespace.QName;

import static com.skynav.ttv.model.ttml.TTML.Constants.*;

public class Constants {

    // Namespaces
    public static final String ttsNamespace                             = NAMESPACE_TT_STYLE;

    // ISD Style Attribute Names
    public static final QName isdCSSAttrName                            = new QName(NAMESPACE_TT_ISD, "css");

    // TTML Style Attribute Names
    public static final QName ttsBackgroundColorAttrName                = new QName(NAMESPACE_TT_STYLE, "backgroundColor");
    public static final QName ttsBPDAttrName                            = new QName(NAMESPACE_TT_STYLE, "bpd");
    public static final QName ttsColorAttrName                          = new QName(NAMESPACE_TT_STYLE, "color");
    public static final QName ttsDirectionAttrName                      = new QName(NAMESPACE_TT_STYLE, "direction");
    public static final QName ttsDisplayAlignAttrName                   = new QName(NAMESPACE_TT_STYLE, "displayAlign");
    public static final QName ttsExtentAttrName                         = new QName(NAMESPACE_TT_STYLE, "extent");
    public static final QName ttsFontFamilyAttrName                     = new QName(NAMESPACE_TT_STYLE, "fontFamily");
    public static final QName ttsFontKerningAttrName                    = new QName(NAMESPACE_TT_STYLE, "fontKerning");
    public static final QName ttsFontShearAttrName                      = new QName(NAMESPACE_TT_STYLE, "fontShear");
    public static final QName ttsFontSizeAttrName                       = new QName(NAMESPACE_TT_STYLE, "fontSize");
    public static final QName ttsFontStyleAttrName                      = new QName(NAMESPACE_TT_STYLE, "fontStyle");
    public static final QName ttsFontVariantAttrName                    = new QName(NAMESPACE_TT_STYLE, "fontVariant");
    public static final QName ttsFontWeightAttrName                     = new QName(NAMESPACE_TT_STYLE, "fontWeight");
    public static final QName ttsIPDAttrName                            = new QName(NAMESPACE_TT_STYLE, "ipd");
    public static final QName ttsLineHeightAttrName                     = new QName(NAMESPACE_TT_STYLE, "lineHeight");
    public static final QName ttsOriginAttrName                         = new QName(NAMESPACE_TT_STYLE, "origin");
    public static final QName ttsOverflowAttrName                       = new QName(NAMESPACE_TT_STYLE, "overflow");
    public static final QName ttsPositionAttrName                       = new QName(NAMESPACE_TT_STYLE, "position");
    public static final QName ttsRubyAlignAttrName                      = new QName(NAMESPACE_TT_STYLE, "rubyAlign");
    public static final QName ttsRubyAttrName                           = new QName(NAMESPACE_TT_STYLE, "ruby");
    public static final QName ttsRubyOffsetAttrName                     = new QName(NAMESPACE_TT_STYLE, "rubyOffset");
    public static final QName ttsRubyOverflowAttrName                   = new QName(NAMESPACE_TT_STYLE, "rubyOverflow");
    public static final QName ttsRubyOverhangAttrName                   = new QName(NAMESPACE_TT_STYLE, "rubyOverhang");
    public static final QName ttsRubyOverhangClassAttrName              = new QName(NAMESPACE_TT_STYLE, "rubyOverhangClass");
    public static final QName ttsRubyPositionAttrName                   = new QName(NAMESPACE_TT_STYLE, "rubyPosition");
    public static final QName ttsRubyReserveAttrName                    = new QName(NAMESPACE_TT_STYLE, "rubyReserve");
    public static final QName ttsTextAlignAttrName                      = new QName(NAMESPACE_TT_STYLE, "textAlign");
    public static final QName ttsTextCombineAttrName                    = new QName(NAMESPACE_TT_STYLE, "textCombine");
    public static final QName ttsTextEmphasisAttrName                   = new QName(NAMESPACE_TT_STYLE, "textEmphasis");
    public static final QName ttsTextOutlineAttrName                    = new QName(NAMESPACE_TT_STYLE, "textOutline");
    public static final QName ttsUnicodeBidiAttrName                    = new QName(NAMESPACE_TT_STYLE, "unicodeBidi");
    public static final QName ttsWrapOptionAttrName                     = new QName(NAMESPACE_TT_STYLE, "wrapOption");
    public static final QName ttsWritingModeAttrName                    = new QName(NAMESPACE_TT_STYLE, "writingMode");

}
