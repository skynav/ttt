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

package com.skynav.ttpe.text;

import javax.xml.namespace.QName;

import com.skynav.xml.helpers.XML;

import static com.skynav.ttv.model.ttml.TTML.Constants.*;

public class Constants {

    // Namespaces
    public static final String ttNamespace                              = NAMESPACE_TT;
    public static final String isdNamespace                             = NAMESPACE_TT_ISD;

    // ISD Element Names
    public static final QName isdSequenceElementName                    = new QName(NAMESPACE_TT_ISD, "sequence");
    public static final QName isdInstanceElementName                    = new QName(NAMESPACE_TT_ISD, "isd");
    public static final QName isdComputedStyleSetElementName            = new QName(NAMESPACE_TT_ISD, "css");
    public static final QName isdRegionElementName                      = new QName(NAMESPACE_TT_ISD, "region");

    // TTML Element Names
    public static final QName ttBodyElementName                         = new QName(NAMESPACE_TT, "body");
    public static final QName ttDivisionElementName                     = new QName(NAMESPACE_TT, "div");
    public static final QName ttParagraphElementName                    = new QName(NAMESPACE_TT, "p");
    public static final QName ttSpanElementName                         = new QName(NAMESPACE_TT, "span");
    public static final QName ttBreakElementName                        = new QName(NAMESPACE_TT, "br");

    // TTML Attribute Names
    public static final QName conditionAttrName                         = new QName("", "condition");

    // XML Attribute Names
    public static final QName xmlIdAttrName                             = new QName(XML.xmlNamespace, "id");
    public static final QName xmlLanguageAttrName                       = new QName(XML.xmlNamespace, "lang");
    public static final QName xmlSpaceAttrName                          = new QName(XML.xmlNamespace, "space");

}
