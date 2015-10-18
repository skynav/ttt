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

public class StyleAttribute extends java.text.AttributedCharacterIterator.Attribute implements Comparable<StyleAttribute> {

    //                                                                                   ** name **                      ** value type **
    public static final StyleAttribute   ANNOTATIONS               = new StyleAttribute("ANNOTATIONS");               // com.skynav.ttpe.text.Phrase[]
    public static final StyleAttribute   ANNOTATION_ALIGNMENT      = new StyleAttribute("ANNOTATION_ALIGNMENT");      // com.skynav.ttpe.style.InlineAlignment
    public static final StyleAttribute   ANNOTATION_OFFSET         = new StyleAttribute("ANNOTATION_OFFSET");         // Double
    public static final StyleAttribute   ANNOTATION_OVERFLOW       = new StyleAttribute("ANNOTATION_OVERFLOW");       // com.skynav.ttpe.style.AnnotationOverflow
    public static final StyleAttribute   ANNOTATION_OVERHANG       = new StyleAttribute("ANNOTATION_OVERHANG");       // com.skynav.ttpe.style.AnnotationOverhang
    public static final StyleAttribute   ANNOTATION_OVERHANG_CLASS = new StyleAttribute("ANNOTATION_OVERHANG_CLASS"); // com.skynav.ttpe.style.AnnotationOverhangClass
    public static final StyleAttribute   ANNOTATION_POSITION       = new StyleAttribute("ANNOTATION_POSITION");       // com.skynav.ttpe.style.AnnotationPosition
    public static final StyleAttribute   ANNOTATION_RESERVE        = new StyleAttribute("ANNOTATION_RESERVE");        // com.skynav.ttpe.style.AnnotationReserve
    public static final StyleAttribute   BIDI                      = new StyleAttribute("BIDI");                      // Integer
    public static final StyleAttribute   BLOCK_ALIGNMENT           = new StyleAttribute("BLOCK_ALIGNMENT");           // com.skynav.ttpe.style.BlockAlignment
    public static final StyleAttribute   BPD                       = new StyleAttribute("BPD");                       // Double
    public static final StyleAttribute   COLOR                     = new StyleAttribute("COLOR");                     // com.skynav.ttpe.style.Color
    public static final StyleAttribute   COMBINATION               = new StyleAttribute("COMBINATION");               // com.skynav.ttpe.style.Combination
    public static final StyleAttribute   EMBEDDING                 = new StyleAttribute("EMBEDDING");                 // Object
    public static final StyleAttribute   EMPHASIS                  = new StyleAttribute("EMPHASIS");                  // com.skynav.ttpe.style.Emphasis
    public static final StyleAttribute   FONT                      = new StyleAttribute("FONT");                      // com.skynav.ttpe.fonts.Font
    public static final StyleAttribute   INLINE_ALIGNMENT          = new StyleAttribute("INLINE_ALIGNMENT");          // com.skynav.ttpe.style.InlineAlignment
    public static final StyleAttribute   IPD                       = new StyleAttribute("IPD");                       // Double
    public static final StyleAttribute   LANGUAGE                  = new StyleAttribute("LANGUAGE");                  // String
    public static final StyleAttribute   LETTER_SPACING            = new StyleAttribute("LETTER_SPACING");            // Double
    public static final StyleAttribute   LINE_HEIGHT               = new StyleAttribute("LINE_HEIGHT");               // Double
    public static final StyleAttribute   ORIENTATION               = new StyleAttribute("ORIENTATION");               // com.skynav.ttpe.style.Orientation
    public static final StyleAttribute   OUTLINE                   = new StyleAttribute("OUTLINE");                   // com.skynav.ttpe.style.Outline
    public static final StyleAttribute   SCRIPT                    = new StyleAttribute("SCRIPT");                    // String
    public static final StyleAttribute   WHITESPACE                = new StyleAttribute("WHITESPACE");                // com.skynav.ttpe.style.Whitespace
    public static final StyleAttribute   WRAP                      = new StyleAttribute("WRAP");                      // com.skynav.ttpe.style.Wrap

    private static final long serialVersionUID                  = -7114262035562833810L;

    private StyleAttribute(String name) {
        super(name);
    }

    public int compareTo(StyleAttribute other) {
        return getName().compareTo(other.getName());
    }

}
