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

package com.skynav.ttpe.util;

public class Characters {

    private Characters() {}

    public static int   UC_HT                                   = '\u0009';             // horizontal tab
    public static int   UC_LF                                   = '\n';                 // line feed
    public static int   UC_VT                                   = '\u000B';             // vertical tab
    public static int   UC_FF                                   = '\u000C';             // form feed
    public static int   UC_CR                                   = '\r';                 // carriage return
    public static int   UC_SPACE                                = '\u0020';             // space
    public static int   UC_NBSP                                 = '\u00A0';             // non-breaking space
    public static int   UC_SPACE_EN_QUAD                        = '\u2000';             // en quad
    public static int   UC_SPACE_EM_QUAD                        = '\u2001';             // em quad
    public static int   UC_SPACE_EN                             = '\u2002';             // en space
    public static int   UC_SPACE_EM                             = '\u2003';             // em space
    public static int   UC_SPACE_3_PER_EM                       = '\u2004';             // 3-per-em space
    public static int   UC_SPACE_4_PER_EM                       = '\u2005';             // 4-per-em space
    public static int   UC_SPACE_6_PER_EM                       = '\u2006';             // 6-per-em space
    public static int   UC_SPACE_FIGURE                         = '\u2007';             // figure space
    public static int   UC_SPACE_PUNCTUATION                    = '\u2008';             // punctuation space
    public static int   UC_SPACE_THIN                           = '\u2009';             // thin space
    public static int   UC_SPACE_HAIR                           = '\u200A';             // hair space
    public static int   UC_SPACE_ZWSP                           = '\u200B';             // zero-width space
    public static int   UC_LINE_SEPARATOR                       = '\u2028';             // line separator
    public static int   UC_PARA_SEPARATOR                       = '\u2029';             // paragraph separator
    public static int   UC_NNBSP                                = '\u202F';             // narrow non-breaking space
    public static int   UC_MMSP                                 = '\u205F';             // medium mathematical space
    public static int   UC_IDSP                                 = '\u3000';             // ideographic space
    public static int   UC_REPLACEMENT                          = '\uFFFD';             // replacement character

    public static boolean isBreakingWhitespace(int c) {
        if (c == UC_HT)
            return true;
        else if (c == UC_LF)
            return true;
        else if (c == UC_VT)
            return true;
        else if (c == UC_FF)
            return true;
        else if (c == UC_CR)
            return true;
        else if (c == UC_SPACE)
            return true;
        else if ((c >= UC_SPACE_EN_QUAD) && (c <= UC_SPACE_ZWSP))
            return true;
        else if (c == UC_LINE_SEPARATOR)
            return true;
        else if (c == UC_MMSP)
            return true;
        else
            return false;
    }

    public static boolean isNonBreakingWhitespace(int c) {
        if (c == UC_NBSP)
            return true;
        else if (c == UC_NNBSP)
            return true;
        else
            return false;
    }

    public static boolean isWhitespace(int c) {
        return isBreakingWhitespace(c) || isNonBreakingWhitespace(c);
    }

}
