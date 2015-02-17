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

public class TextAttribute extends java.text.AttributedCharacterIterator.Attribute {

    //                               ** name **                                                                 ** value type ** 
    public static final TextAttribute   BLOCK_ALIGNMENT         = new TextAttribute("BLOCK_ALIGNMENT");         // com.skynav.ttpe.style.BlockAlignment
    public static final TextAttribute   BPD                     = new TextAttribute("BPD");                     // Double
    public static final TextAttribute   COLOR                   = new TextAttribute("COLOR");                   // com.skynav.ttpe.style.Color
    public static final TextAttribute   EMBEDDING               = new TextAttribute("EMBEDDING");               // Object
    public static final TextAttribute   FONT                    = new TextAttribute("FONT");                    // com.skynav.ttpe.fonts.Font
    public static final TextAttribute   FONT_FAMILY             = new TextAttribute("FONT_FAMILY");             // List<String>
    public static final TextAttribute   FONT_KERNING            = new TextAttribute("FONT_KERNING");            // Boolean
    public static final TextAttribute   FONT_SHEAR              = new TextAttribute("FONT_SHEAR");              // Double
    public static final TextAttribute   FONT_SIZE               = new TextAttribute("FONT_SIZE");               // com.skynav.ttpe.geometry.Extent
    public static final TextAttribute   FONT_STYLE              = new TextAttribute("FONT_STYLE");              // com.skynav.ttpe.fonts.FontStyle
    public static final TextAttribute   FONT_WEIGHT             = new TextAttribute("FONT_WEIGHT");             // com.skynav.ttpe.fonts.FontWeight
    public static final TextAttribute   INLINE_ALIGNMENT        = new TextAttribute("INLINE_ALIGNMENT");        // com.skynav.ttpe.style.BlockAlignment
    public static final TextAttribute   IPD                     = new TextAttribute("IPD");                     // Double
    public static final TextAttribute   LANGUAGE                = new TextAttribute("LANGUAGE");                // String
    public static final TextAttribute   LETTER_SPACING          = new TextAttribute("LETTER_SPACING");          // Double
    public static final TextAttribute   LINE_HEIGHT             = new TextAttribute("LINE_HEIGHT");             // Double
    public static final TextAttribute   WHITESPACE              = new TextAttribute("WHITESPACE");              // com.skynav.ttpe.style.Whitespace
    public static final TextAttribute   WRAP                    = new TextAttribute("WRAP");                    // com.skynav.ttpe.style.Wrap

    private static final long serialVersionUID                  = -7114262035562833810L;

    private TextAttribute(String name) {
        super(name);
    }

}
