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

import java.util.Arrays;

public class Characters {

    private Characters() {}

    public static final int   UC_HT                             = '\u0009';             // horizontal tab
    public static final int   UC_LF                             = '\n';                 // line feed
    public static final int   UC_VT                             = '\u000B';             // vertical tab
    public static final int   UC_FF                             = '\u000C';             // form feed
    public static final int   UC_CR                             = '\r';                 // carriage return
    public static final int   UC_SPACE                          = '\u0020';             // space
    public static final int   UC_HYPHEN_MINUS                   = '\u002D';             // hyphen-minus
    public static final int   UC_NBSP                           = '\u00A0';             // non-breaking space
    public static final int   UC_SOFT_HYPHEN                    = '\u00AD';             // soft hyphen
    public static final int   UC_SPACE_EN_QUAD                  = '\u2000';             // en quad
    public static final int   UC_SPACE_EM_QUAD                  = '\u2001';             // em quad
    public static final int   UC_SPACE_EN                       = '\u2002';             // en space
    public static final int   UC_SPACE_EM                       = '\u2003';             // em space
    public static final int   UC_SPACE_3_PER_EM                 = '\u2004';             // 3-per-em space
    public static final int   UC_SPACE_4_PER_EM                 = '\u2005';             // 4-per-em space
    public static final int   UC_SPACE_6_PER_EM                 = '\u2006';             // 6-per-em space
    public static final int   UC_SPACE_FIGURE                   = '\u2007';             // figure space
    public static final int   UC_SPACE_PUNCTUATION              = '\u2008';             // punctuation space
    public static final int   UC_SPACE_THIN                     = '\u2009';             // thin space
    public static final int   UC_SPACE_HAIR                     = '\u200A';             // hair space
    public static final int   UC_SPACE_ZWSP                     = '\u200B';             // zero-width space
    public static final int   UC_ZWNJ                           = '\u200C';             // zero-width non-joiner
    public static final int   UC_ZWJ                            = '\u200D';             // zero-width joiner
    public static final int   UC_LRM                            = '\u200E';             // left-to-right mark
    public static final int   UC_RLM                            = '\u200F';             // right-to-left mark
    public static final int   UC_LINE_SEPARATOR                 = '\u2028';             // line separator
    public static final int   UC_PARA_SEPARATOR                 = '\u2029';             // paragraph separator
    public static final int   UC_LRE                            = '\u202A';             // left-to-right embedding
    public static final int   UC_RLE                            = '\u202B';             // right-to-left embedding
    public static final int   UC_PDF                            = '\u202C';             // pop directional formatting
    public static final int   UC_LRO                            = '\u202D';             // left-to-right override
    public static final int   UC_RLO                            = '\u202E';             // right-to-left override
    public static final int   UC_NNBSP                          = '\u202F';             // narrow non-breaking space
    public static final int   UC_MMSP                           = '\u205F';             // medium mathematical space
    public static final int   UC_IDSP                           = '\u3000';             // ideographic space
    public static final int   UC_CJK_SYMBOL_START               = '\u3000';             // cjk symbols - start
    public static final int   UC_CJK_SYMBOL_END                 = '\u303F';             // cjk symbols - end (inclusive)
    public static final int   UC_HIRAGANA_START                 = '\u3040';             // hiragana - start
    public static final int   UC_HIRAGANA_END                   = '\u309F';             // hiragana - end (inclusive)
    public static final int   UC_KATAKANA_START                 = '\u30A0';             // katakana - start
    public static final int   UC_KATAKANA_END                   = '\u30FF';             // katakana - end (inclusive)
    public static final int   UC_CJK_START                      = '\u4E00';             // unified cjk ideographs - start
    public static final int   UC_CJK_END                        = '\u9FCC';             // unified cjk ideographs - end (inclusive)
    public static final int   UC_CJK_VERTICAL_START             = '\uFE10';             // cjk vertical forms - start
    public static final int   UC_CJK_VERTICAL_END               = '\uFE1F';             // cjk vertical forms - end (inclusive)
    public static final int   UC_CJK_COMPAT_START               = '\uFE30';             // cjk compatibility forms - start
    public static final int   UC_CJK_COMPAT_END                 = '\uFE4F';             // cjk compatibility forms - end (inclusive)
    public static final int   UC_CJK_HALF_FULL_WIDTH_START      = '\uFF00';             // cjk half and full width forms - start
    public static final int   UC_CJK_HALF_FULL_WIDTH_END        = '\uFFEF';             // cjk half and full width forms - end (inclusive)
    public static final int   UC_OBJECT                         = '\uFFFC';             // object replacement character
    public static final int   UC_REPLACEMENT                    = '\uFFFD';             // replacement character
    public static final int   UC_NOT_A_CHARACTER                = '\uFFFF';             // not a character

    public static String formatCharacter(int c) {
        if (c < 65536)
            return String.format("'%c' (U+%04X)", (char) c, c);
        else if (c < 1114112)
            return String.format("'%c' (U+%06X)", (char) c, c);
        else
            return String.format("*illegal character* (%08X)", c);
    }

    public static boolean isLineSeparator(int c) {
        return c == UC_LINE_SEPARATOR;
    }

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

    public static boolean isZeroWidthWhitespace(int c) {
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
        else if (c == UC_SPACE_ZWSP)
            return true;
        else if (c == UC_LINE_SEPARATOR)
            return true;
        else if (c == UC_PARA_SEPARATOR)
            return true;
        else
            return false;
    }

    public static boolean isHyphenationPoint(int c) {
        return (c == UC_HYPHEN_MINUS) || (c == UC_SOFT_HYPHEN);
    }

    public static boolean isCJKSymbol(int c) {
        return (c >= UC_CJK_SYMBOL_START) && (c <= UC_CJK_SYMBOL_END);
    }

    public static boolean isCJKIdeograph(int c) {
        return (c >= UC_CJK_START) && (c <= UC_CJK_END);
    }

    public static boolean isCJKVertical(int c) {
        return (c >= UC_CJK_VERTICAL_START) && (c <= UC_CJK_VERTICAL_END);
    }

    public static boolean isCJKCompatibility(int c) {
        return (c >= UC_CJK_COMPAT_START) && (c <= UC_CJK_COMPAT_END);
    }

    public static boolean isCJKHalfFullWidth(int c) {
        return (c >= UC_CJK_HALF_FULL_WIDTH_START) && (c <= UC_CJK_HALF_FULL_WIDTH_END);
    }

    public static boolean isHiragana(int c) {
        return (c >= UC_HIRAGANA_START) && (c <= UC_HIRAGANA_END);
    }

    public static boolean isKatakana(int c) {
        return (c >= UC_KATAKANA_START) && (c <= UC_KATAKANA_END);
    }

    public static boolean isCJK(int c) {
        if (isCJKSymbol(c))
            return true;
        else if (isHiragana(c))
            return true;
        else if (isKatakana(c))
            return true;
        else if (isCJKIdeograph(c))
            return true;
        else if (isCJKVertical(c))
            return true;
        else if (isCJKCompatibility(c))
            return true;
        else if (isCJKHalfFullWidth(c))
            return true;
        else
            return false;
    }

    public static boolean isUprightOrientation(int c) {
        return isCJK(c);
    }

    public static boolean isMixedOrientation(int c) {
        return !isUprightOrientation(c);
    }

    private static final int[] h2fKey = new int[] {
        0x0021,
        0x0022,
        0x0023,
        0x0024,
        0x0025,
        0x0026,
        0x0027,
        0x0028,
        0x0029,
        0x002A,
        0x002B,
        0x002C,
        0x002D,
        0x002E,
        0x002F,
        0x0030,
        0x0031,
        0x0032,
        0x0033,
        0x0034,
        0x0035,
        0x0036,
        0x0037,
        0x0038,
        0x0039,
        0x003A,
        0x003B,
        0x003C,
        0x003D,
        0x003E,
        0x003F,
        0x0040,
        0x0041,
        0x0042,
        0x0043,
        0x0044,
        0x0045,
        0x0046,
        0x0047,
        0x0048,
        0x0049,
        0x004A,
        0x004B,
        0x004C,
        0x004D,
        0x004E,
        0x004F,
        0x0050,
        0x0051,
        0x0052,
        0x0053,
        0x0054,
        0x0055,
        0x0056,
        0x0057,
        0x0058,
        0x0059,
        0x005A,
        0x005B,
        0x005C,
        0x005D,
        0x005E,
        0x005F,
        0x0060,
        0x0061,
        0x0062,
        0x0063,
        0x0064,
        0x0065,
        0x0066,
        0x0067,
        0x0068,
        0x0069,
        0x006A,
        0x006B,
        0x006C,
        0x006D,
        0x006E,
        0x006F,
        0x0070,
        0x0071,
        0x0072,
        0x0073,
        0x0074,
        0x0075,
        0x0076,
        0x0077,
        0x0078,
        0x0079,
        0x007A,
        0x007B,
        0x007C,
        0x007D,
        0x007E,
        0x00A2,
        0x00A3,
        0x00A5,
        0x00A6,
        0x00AC,
        0x00AF,
        0x20A9,
        0x2985,
        0x2986,
        0x3001,
        0x3002,
        0x300C,
        0x300D,
        0xFF65,
        0xFF66,
        0xFF67,
        0xFF68,
        0xFF69,
        0xFF6A,
        0xFF6B,
        0xFF6C,
        0xFF6D,
        0xFF6E,
        0xFF6F,
        0xFF70,
        0xFF71,
        0xFF72,
        0xFF73,
        0xFF74,
        0xFF75,
        0xFF76,
        0xFF77,
        0xFF78,
        0xFF79,
        0xFF7A,
        0xFF7B,
        0xFF7C,
        0xFF7D,
        0xFF7E,
        0xFF7F,
        0xFF80,
        0xFF81,
        0xFF82,
        0xFF83,
        0xFF84,
        0xFF85,
        0xFF86,
        0xFF87,
        0xFF88,
        0xFF89,
        0xFF8A,
        0xFF8B,
        0xFF8C,
        0xFF8D,
        0xFF8E,
        0xFF8F,
        0xFF90,
        0xFF91,
        0xFF92,
        0xFF93,
        0xFF94,
        0xFF95,
        0xFF96,
        0xFF97,
        0xFF98,
        0xFF99,
        0xFF9A,
        0xFF9B,
        0xFF9C,
        0xFF9D,
        0xFF9E,
        0xFF9F,
        0xFFE8,
        0xFFE9,
        0xFFEA,
        0xFFEB,
        0xFFEC,
        0xFFED,
        0xFFEE
    };

    private static final int[] h2fVal = new int[] {
        0xFF01,
        0xFF02,
        0xFF03,
        0xFF04,
        0xFF05,
        0xFF06,
        0xFF07,
        0xFF08,
        0xFF09,
        0xFF0A,
        0xFF0B,
        0xFF0C,
        0xFF0D,
        0xFF0E,
        0xFF0F,
        0xFF10,
        0xFF11,
        0xFF12,
        0xFF13,
        0xFF14,
        0xFF15,
        0xFF16,
        0xFF17,
        0xFF18,
        0xFF19,
        0xFF1A,
        0xFF1B,
        0xFF1C,
        0xFF1D,
        0xFF1E,
        0xFF1F,
        0xFF20,
        0xFF21,
        0xFF22,
        0xFF23,
        0xFF24,
        0xFF25,
        0xFF26,
        0xFF27,
        0xFF28,
        0xFF29,
        0xFF2A,
        0xFF2B,
        0xFF2C,
        0xFF2D,
        0xFF2E,
        0xFF2F,
        0xFF30,
        0xFF31,
        0xFF32,
        0xFF33,
        0xFF34,
        0xFF35,
        0xFF36,
        0xFF37,
        0xFF38,
        0xFF39,
        0xFF3A,
        0xFF3B,
        0xFF3C,
        0xFF3D,
        0xFF3E,
        0xFF3F,
        0xFF40,
        0xFF41,
        0xFF42,
        0xFF43,
        0xFF44,
        0xFF45,
        0xFF46,
        0xFF47,
        0xFF48,
        0xFF49,
        0xFF4A,
        0xFF4B,
        0xFF4C,
        0xFF4D,
        0xFF4E,
        0xFF4F,
        0xFF50,
        0xFF51,
        0xFF52,
        0xFF53,
        0xFF54,
        0xFF55,
        0xFF56,
        0xFF57,
        0xFF58,
        0xFF59,
        0xFF5A,
        0xFF5B,
        0xFF5C,
        0xFF5D,
        0xFF5E,
        0xFFE0,
        0xFFE1,
        0xFFE5,
        0xFFE4,
        0xFFE2,
        0xFFE3,
        0xFFE6,
        0xFF5F,
        0xFF60,
        0xFF64,
        0xFF61,
        0xFF62,
        0xFF63,
        0x30FB,
        0x30F2,
        0x30A1,
        0x30A3,
        0x30A5,
        0x30A7,
        0x30A9,
        0x30E3,
        0x30E5,
        0x30E7,
        0x30C3,
        0x30FC,
        0x30A2,
        0x30A4,
        0x30A6,
        0x30A8,
        0x30AA,
        0x30AB,
        0x30AD,
        0x30AF,
        0x30B1,
        0x30B3,
        0x30B5,
        0x30B7,
        0x30B9,
        0x30BB,
        0x30BD,
        0x30BF,
        0x30C1,
        0x30C4,
        0x30C6,
        0x30C8,
        0x30CA,
        0x30CB,
        0x30CC,
        0x30CD,
        0x30CE,
        0x30CF,
        0x30D2,
        0x30D5,
        0x30D8,
        0x30DB,
        0x30DE,
        0x30DF,
        0x30E0,
        0x30E1,
        0x30E2,
        0x30E4,
        0x30E6,
        0x30E8,
        0x30E9,
        0x30EA,
        0x30EB,
        0x30EC,
        0x30ED,
        0x30EF,
        0x30F3,
        0x3099,
        0x309A,
        0x2502,
        0x2190,
        0x2191,
        0x2192,
        0x2193,
        0X25A0,
        0x25CB
    };

    private static final int[] f2hKey = new int[] {
        0x25A0,
        0x2190,
        0x2191,
        0x2192,
        0x2193,
        0x2502,
        0x25CB,
        0x3099,
        0x309A,
        0x30A1,
        0x30A2,
        0x30A3,
        0x30A4,
        0x30A5,
        0x30A6,
        0x30A7,
        0x30A8,
        0x30A9,
        0x30AA,
        0x30AB,
        0x30AD,
        0x30AF,
        0x30B1,
        0x30B3,
        0x30B5,
        0x30B7,
        0x30B9,
        0x30BB,
        0x30BD,
        0x30BF,
        0x30C1,
        0x30C3,
        0x30C4,
        0x30C6,
        0x30C8,
        0x30CA,
        0x30CB,
        0x30CC,
        0x30CD,
        0x30CE,
        0x30CF,
        0x30D2,
        0x30D5,
        0x30D8,
        0x30DB,
        0x30DE,
        0x30DF,
        0x30E0,
        0x30E1,
        0x30E2,
        0x30E3,
        0x30E4,
        0x30E5,
        0x30E6,
        0x30E7,
        0x30E8,
        0x30E9,
        0x30EA,
        0x30EB,
        0x30EC,
        0x30ED,
        0x30EF,
        0x30F2,
        0x30F3,
        0x30FB,
        0x30FC,
        0xFF01,
        0xFF02,
        0xFF03,
        0xFF04,
        0xFF05,
        0xFF06,
        0xFF07,
        0xFF08,
        0xFF09,
        0xFF0A,
        0xFF0B,
        0xFF0C,
        0xFF0D,
        0xFF0E,
        0xFF0F,
        0xFF10,
        0xFF11,
        0xFF12,
        0xFF13,
        0xFF14,
        0xFF15,
        0xFF16,
        0xFF17,
        0xFF18,
        0xFF19,
        0xFF1A,
        0xFF1B,
        0xFF1C,
        0xFF1D,
        0xFF1E,
        0xFF1F,
        0xFF20,
        0xFF21,
        0xFF22,
        0xFF23,
        0xFF24,
        0xFF25,
        0xFF26,
        0xFF27,
        0xFF28,
        0xFF29,
        0xFF2A,
        0xFF2B,
        0xFF2C,
        0xFF2D,
        0xFF2E,
        0xFF2F,
        0xFF30,
        0xFF31,
        0xFF32,
        0xFF33,
        0xFF34,
        0xFF35,
        0xFF36,
        0xFF37,
        0xFF38,
        0xFF39,
        0xFF3A,
        0xFF3B,
        0xFF3C,
        0xFF3D,
        0xFF3E,
        0xFF3F,
        0xFF40,
        0xFF41,
        0xFF42,
        0xFF43,
        0xFF44,
        0xFF45,
        0xFF46,
        0xFF47,
        0xFF48,
        0xFF49,
        0xFF4A,
        0xFF4B,
        0xFF4C,
        0xFF4D,
        0xFF4E,
        0xFF4F,
        0xFF50,
        0xFF51,
        0xFF52,
        0xFF53,
        0xFF54,
        0xFF55,
        0xFF56,
        0xFF57,
        0xFF58,
        0xFF59,
        0xFF5A,
        0xFF5B,
        0xFF5C,
        0xFF5D,
        0xFF5E,
        0xFF5F,
        0xFF60,
        0xFF61,
        0xFF62,
        0xFF63,
        0xFF64,
        0xFFE0,
        0xFFE1,
        0xFFE2,
        0xFFE3,
        0xFFE4,
        0xFFE5,
        0xFFE6
    };

    private static final int[] f2hVal = new int[] {
        0xFFED,
        0xFFE9,
        0xFFEA,
        0xFFEB,
        0xFFEC,
        0xFFE8,
        0xFFEE,
        0xFF9E,
        0xFF9F,
        0xFF67,
        0xFF71,
        0xFF68,
        0xFF72,
        0xFF69,
        0xFF73,
        0xFF6A,
        0xFF74,
        0xFF6B,
        0xFF75,
        0xFF76,
        0xFF77,
        0xFF78,
        0xFF79,
        0xFF7A,
        0xFF7B,
        0xFF7C,
        0xFF7D,
        0xFF7E,
        0xFF7F,
        0xFF80,
        0xFF81,
        0xFF6F,
        0xFF82,
        0xFF83,
        0xFF84,
        0xFF85,
        0xFF86,
        0xFF87,
        0xFF88,
        0xFF89,
        0xFF8A,
        0xFF8B,
        0xFF8C,
        0xFF8D,
        0xFF8E,
        0xFF8F,
        0xFF90,
        0xFF91,
        0xFF92,
        0xFF93,
        0xFF6C,
        0xFF94,
        0xFF6D,
        0xFF95,
        0xFF6E,
        0xFF96,
        0xFF97,
        0xFF98,
        0xFF99,
        0xFF9A,
        0xFF9B,
        0xFF9C,
        0xFF66,
        0xFF9D,
        0xFF65,
        0xFF70,
        0x0021,
        0x0022,
        0x0023,
        0x0024,
        0x0025,
        0x0026,
        0x0027,
        0x0028,
        0x0029,
        0x002A,
        0x002B,
        0x002C,
        0x002D,
        0x002E,
        0x002F,
        0x0030,
        0x0031,
        0x0032,
        0x0033,
        0x0034,
        0x0035,
        0x0036,
        0x0037,
        0x0038,
        0x0039,
        0x003A,
        0x003B,
        0x003C,
        0x003D,
        0x003E,
        0x003F,
        0x0040,
        0x0041,
        0x0042,
        0x0043,
        0x0044,
        0x0045,
        0x0046,
        0x0047,
        0x0048,
        0x0049,
        0x004A,
        0x004B,
        0x004C,
        0x004D,
        0x004E,
        0x004F,
        0x0050,
        0x0051,
        0x0052,
        0x0053,
        0x0054,
        0x0055,
        0x0056,
        0x0057,
        0x0058,
        0x0059,
        0x005A,
        0x005B,
        0x005C,
        0x005D,
        0x005E,
        0x005F,
        0x0060,
        0x0061,
        0x0062,
        0x0063,
        0x0064,
        0x0065,
        0x0066,
        0x0067,
        0x0068,
        0x0069,
        0x006A,
        0x006B,
        0x006C,
        0x006D,
        0x006E,
        0x006F,
        0x0070,
        0x0071,
        0x0072,
        0x0073,
        0x0074,
        0x0075,
        0x0076,
        0x0077,
        0x0078,
        0x0079,
        0x007A,
        0x007B,
        0x007C,
        0x007D,
        0x007E,
        0x2985,
        0x2986,
        0x3002,
        0x300C,
        0x300D,
        0x3001,
        0x00A2,
        0x00A3,
        0x00AC,
        0x00AF,
        0x00A6,
        0x00A5,
        0x20A9
    };

    public static boolean hasHalfWidth(int c) {
        return Arrays.binarySearch(f2hKey, c) >= 0;
    }

    public static int toHalfWidth(int c) {
        int k = Arrays.binarySearch(f2hKey, c);
        if (k >= 0)
            return f2hVal[k];
        else
            return c;
    }

    public static boolean hasFullWidth(int c) {
        return Arrays.binarySearch(h2fKey, c) >= 0;
    }

    public static int toFullWidth(int c) {
        int k = Arrays.binarySearch(h2fKey, c);
        if (k >= 0)
            return h2fVal[k];
        else
            return c;
    }

}
        
