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

    public static boolean isNonSpacing(int c) {
        if ((c >= 0x0300) && (c <= 0x036F))
            return true;
        else if ((c >= 0x0590) && (c <= 0x05CF))
            return (c != 0x05C0) && (c != 0x05C6);
        else if (((c >= 0x0610) && (c <= 0x061A)) || (c == 0x061C))
            return true;
        else if ((c >= 0x064B) && (c <= 0x065F))
            return true;
        else if ((c >= 0x06D6) && (c <= 0x06ED))
            return (c != 0x06E5) && (c != 0x06E6) && (c != 0x06E9);
        else if ((c >= 0x1AB0) && (c <= 0x1AFF))
            return true;
        else if ((c >= 0x1DC0) && (c <= 0x1DFF))
            return true;
        else if ((c >= 0x2000) && (c <= 0x200F))
            return true;
        else if ((c >= 0x2028) && (c <= 0x202F))
            return true;
        else if ((c >= 0x205F) && (c <= 0x206F))
            return true;
        else
            return false;
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

    // UTR #50 - Unicode Vertical Text Layout

    public enum VerticalOrientation {
        U,              // displayed upright, same orientation as in code charts
        R,              // displayed sideways, rotated 90 from orientation in code charts
        Tu,             // mapped to vertical glyph variant, or if unavailable, same orientation as in code charts
        Tr;             // mapped to vertical glyph variant, or if unavailable, same rotated 90 from orientation in code charts
    };

    private static final VerticalOrientation[] verticalOrientations = new VerticalOrientation[] {
        VerticalOrientation.U,
        VerticalOrientation.R,
        VerticalOrientation.Tu,
        VerticalOrientation.Tr
    };

    private static final int[] voRanges1 = new int[] {
        0x0000, 0x00A6, // 0000..00A6 ; R
        0x00AA, 0x00AD, // 00AA..00AD ; R
        0x00AF, 0x00B0, // 00AF..00B0 ; R
        0x00B2, 0x00BB, // 00B2..00BB ; R
        0x00BC, 0x00BE, // 00BC..00BE ; U
        0x00BF, 0x00D6, // 00BF..00D6 ; R
        0x00D8, 0x00F6, // 00D8..00F6 ; R
        0x00F8, 0x02E9, // 00F8..02E9 ; R
        0x02EA, 0x02EB, // 02EA..02EB ; U
        0x02EC, 0x10FF, // 02EC..10FF ; R
        0x1100, 0x11FF, // 1100..11FF ; U
        0x1200, 0x1400, // 1200..1400 ; R
        0x1401, 0x167F, // 1401..167F ; U
        0x1680, 0x18AF, // 1680..18AF ; R
        0x18B0, 0x18FF, // 18B0..18FF ; U
        0x1900, 0x2015, // 1900..2015 ; R
        0x2017, 0x201F, // 2017..201F ; R
        0x2020, 0x2021, // 2020..2021 ; U
        0x2022, 0x2025, // 2022..2025 ; R       // temporarily subdivide 2022..202F to change 2026 to Tr
        0x2027, 0x202F, // 2027..202F ; R       // temporarily subdivide 2022..202F to change 2026 to Tr
        0x2030, 0x2031, // 2030..2031 ; U
        0x2032, 0x203A, // 2032..203A ; R
        0x203B, 0x203C, // 203B..203C ; U
        0x203D, 0x2041, // 203D..2041 ; R
        0x2043, 0x2046, // 2043..2046 ; R
        0x2047, 0x2049, // 2047..2049 ; U
        0x204A, 0x2050, // 204A..2050 ; R
        0x2052, 0x2064, // 2052..2064 ; R
        0x2066, 0x20DC, // 2066..20DC ; R
        0x20DD, 0x20E0, // 20DD..20E0 ; U
        0x20E2, 0x20E4, // 20E2..20E4 ; U
        0x20E5, 0x20FF, // 20E5..20FF ; R
        0x2100, 0x2101, // 2100..2101 ; U
        0x2103, 0x2109, // 2103..2109 ; U
        0x210A, 0x210E, // 210A..210E ; R
        0x2110, 0x2112, // 2110..2112 ; R
        0x2113, 0x2114, // 2113..2114 ; U
        0x2116, 0x2117, // 2116..2117 ; U
        0x2118, 0x211D, // 2118..211D ; R
        0x211E, 0x2123, // 211E..2123 ; U
        0x212A, 0x212D, // 212A..212D ; R
        0x212F, 0x2134, // 212F..2134 ; R
        0x2135, 0x213F, // 2135..213F ; U
        0x2140, 0x2144, // 2140..2144 ; R
        0x2145, 0x214A, // 2145..214A ; U
        0x214C, 0x214D, // 214C..214D ; U
        0x214F, 0x218F, // 214F..218F ; U
        0x2190, 0x221D, // 2190..221D ; R
        0x221F, 0x2233, // 221F..2233 ; R
        0x2234, 0x2235, // 2234..2235 ; U
        0x2236, 0x22FF, // 2236..22FF ; R
        0x2300, 0x2307, // 2300..2307 ; U
        0x230C, 0x231F, // 230C..231F ; U
        0x2320, 0x2323, // 2320..2323 ; R
        0x2324, 0x2328, // 2324..2328 ; U
        0x2329, 0x232A, // 2329..232A ; Tr
        0x232C, 0x237C, // 232C..237C ; R
        0x237D, 0x239A, // 237D..239A ; U
        0x239B, 0x23BD, // 239B..23BD ; R
        0x23BE, 0x23CD, // 23BE..23CD ; U
        0x23D1, 0x23DB, // 23D1..23DB ; U
        0x23DC, 0x23E1, // 23DC..23E1 ; R
        0x23E2, 0x2422, // 23E2..2422 ; U
        0x2424, 0x24FF, // 2424..24FF ; U
        0x2500, 0x259F, // 2500..259F ; R
        0x25A0, 0x2619, // 25A0..2619 ; U
        0x261A, 0x261F, // 261A..261F ; R
        0x2620, 0x2767, // 2620..2767 ; U
        0x2768, 0x2775, // 2768..2775 ; R
        0x2776, 0x2793, // 2776..2793 ; U
        0x2794, 0x2B11, // 2794..2B11 ; R
        0x2B12, 0x2B2F, // 2B12..2B2F ; U
        0x2B30, 0x2B4F, // 2B30..2B4F ; R
        0x2B50, 0x2B59, // 2B50..2B59 ; U
        0x2B5A, 0x2BB7, // 2B5A..2BB7 ; R
        0x2BB8, 0x2BFF, // 2BB8..2BFF ; U
        0x2C00, 0x2E7F, // 2C00..2E7F ; R
        0x2E80, 0x3000, // 2E80..3000 ; U
        0x3001, 0x3002, // 3001..3002 ; Tu
        0x3003, 0x3007, // 3003..3007 ; U
        0x3008, 0x3011, // 3008..3011 ; Tr
        0x3012, 0x3013, // 3012..3013 ; U
        0x3014, 0x301F, // 3014..301F ; Tr
        0x3020, 0x302F, // 3020..302F ; U
        0x3031, 0x3040, // 3031..3040 ; U
        0x304A, 0x3062, // 304A..3062 ; U
        0x3064, 0x3082, // 3064..3082 ; U
        0x3088, 0x308D, // 3088..308D ; U
        0x308F, 0x3094, // 308F..3094 ; U
        0x3095, 0x3096, // 3095..3096 ; Tu
        0x3097, 0x309A, // 3097..309A ; U
        0x309B, 0x309C, // 309B..309C ; Tu
        0x309D, 0x309F, // 309D..309F ; U
        0x30AA, 0x30C2, // 30AA..30C2 ; U
        0x30C4, 0x30E2, // 30C4..30E2 ; U
        0x30E8, 0x30ED, // 30E8..30ED ; U
        0x30EF, 0x30F4, // 30EF..30F4 ; U
        0x30F5, 0x30F6, // 30F5..30F6 ; Tu
        0x30F7, 0x30FB, // 30F7..30FB ; U
        0x30FD, 0x3126, // 30FD..3126 ; U
        0x3128, 0x31EF, // 3128..31EF ; U
        0x31F0, 0x31FF, // 31F0..31FF ; Tu
        0x3200, 0x32FF, // 3200..32FF ; U
        0x3300, 0x3357, // 3300..3357 ; Tu
        0x3358, 0x337A, // 3358..337A ; U
        0x337B, 0x337F, // 337B..337F ; Tu
        0x3380, 0xA4CF, // 3380..A4CF ; U
        0x3400, 0x4DBF, // 3400..4DBF ; U
        0xA4D0, 0xA95F, // A4D0..A95F ; R
        0xA960, 0xA97F, // A960..A97F ; U
        0xA980, 0xABFF, // A980..ABFF ; R
        0xAC00, 0xD7FF, // AC00..D7FF ; U
        0xD800, 0xDFFF, // D800..DFFF ; R
        0xE000, 0xFAFF, // E000..FAFF ; U
        0xFB00, 0xFE0F, // FB00..FE0F ; R
        0xFE10, 0xFE1F, // FE10..FE1F ; U
        0xFE20, 0xFE2F, // FE20..FE2F ; R
        0xFE30, 0xFE48, // FE30..FE48 ; U
        0xFE49, 0xFE4F, // FE49..FE4F ; R
        0xFE50, 0xFE52, // FE50..FE52 ; Tu
        0xFE53, 0xFE57, // FE53..FE57 ; U
        0xFE59, 0xFE5E, // FE59..FE5E ; Tr
        0xFE5F, 0xFE62, // FE5F..FE62 ; U
        0xFE63, 0xFE66, // FE63..FE66 ; R
        0xFE67, 0xFE6F, // FE67..FE6F ; U
        0xFE70, 0xFF00, // FE70..FF00 ; R
        0xFF02, 0xFF07, // FF02..FF07 ; U
        0xFF08, 0xFF09, // FF08..FF09 ; Tr
        0xFF0A, 0xFF0B, // FF0A..FF0B ; U
        0xFF0F, 0xFF19, // FF0F..FF19 ; U
        0xFF1A, 0xFF1B, // FF1A..FF1B ; Tr
        0xFF1C, 0xFF1E, // FF1C..FF1E ; R
        0xFF20, 0xFF3A, // FF20..FF3A ; U
        0xFF40, 0xFF5A, // FF40..FF5A ; U
        0xFF5B, 0xFF60, // FF5B..FF60 ; Tr
        0xFF61, 0xFFDF, // FF61..FFDF ; R
        0xFFE0, 0xFFE2, // FFE0..FFE2 ; U
        0xFFE4, 0xFFE7, // FFE4..FFE7 ; U
        0xFFE8, 0xFFEF, // FFE8..FFEF ; R
        0xFFF0, 0xFFF8, // FFF0..FFF8 ; U
        0xFFF9, 0xFFFB, // FFF9..FFFB ; R
        0xFFFC, 0xFFFD, // FFFC..FFFD ; U
        0xFFFE, 0xFFFF  // FFFE..FFFF ; R
    };

    private static final byte[] voRanges1Values = new byte[] {
        1,              // 0000..00A6 ; R
        1,              // 00AA..00AD ; R
        1,              // 00AF..00B0 ; R
        1,              // 00B2..00BB ; R
        0,              // 00BC..00BE ; U
        1,              // 00BF..00D6 ; R
        1,              // 00D8..00F6 ; R
        1,              // 00F8..02E9 ; R
        0,              // 02EA..02EB ; U
        1,              // 02EC..10FF ; R
        0,              // 1100..11FF ; U
        1,              // 1200..1400 ; R
        0,              // 1401..167F ; U
        1,              // 1680..18AF ; R
        0,              // 18B0..18FF ; U
        1,              // 1900..2015 ; R
        1,              // 2017..201F ; R
        0,              // 2020..2021 ; U
        1,              // 2022..2025 ; R
        1,              // 2027..202F ; R
        0,              // 2030..2031 ; U
        1,              // 2032..203A ; R
        0,              // 203B..203C ; U
        1,              // 203D..2041 ; R
        1,              // 2043..2046 ; R
        0,              // 2047..2049 ; U
        1,              // 204A..2050 ; R
        1,              // 2052..2064 ; R
        1,              // 2066..20DC ; R
        0,              // 20DD..20E0 ; U
        0,              // 20E2..20E4 ; U
        1,              // 20E5..20FF ; R
        0,              // 2100..2101 ; U
        0,              // 2103..2109 ; U
        1,              // 210A..210E ; R
        1,              // 2110..2112 ; R
        0,              // 2113..2114 ; U
        0,              // 2116..2117 ; U
        1,              // 2118..211D ; R
        0,              // 211E..2123 ; U
        1,              // 212A..212D ; R
        1,              // 212F..2134 ; R
        0,              // 2135..213F ; U
        1,              // 2140..2144 ; R
        0,              // 2145..214A ; U
        0,              // 214C..214D ; U
        0,              // 214F..218F ; U
        1,              // 2190..221D ; R
        1,              // 221F..2233 ; R
        0,              // 2234..2235 ; U
        1,              // 2236..22FF ; R
        0,              // 2300..2307 ; U
        0,              // 230C..231F ; U
        1,              // 2320..2323 ; R
        0,              // 2324..2328 ; U
        3,              // 2329..232A ; Tr
        1,              // 232C..237C ; R
        0,              // 237D..239A ; U
        1,              // 239B..23BD ; R
        0,              // 23BE..23CD ; U
        0,              // 23D1..23DB ; U
        1,              // 23DC..23E1 ; R
        0,              // 23E2..2422 ; U
        0,              // 2424..24FF ; U
        1,              // 2500..259F ; R
        0,              // 25A0..2619 ; U
        1,              // 261A..261F ; R
        0,              // 2620..2767 ; U
        1,              // 2768..2775 ; R
        0,              // 2776..2793 ; U
        1,              // 2794..2B11 ; R
        0,              // 2B12..2B2F ; U
        1,              // 2B30..2B4F ; R
        0,              // 2B50..2B59 ; U
        1,              // 2B5A..2BB7 ; R
        0,              // 2BB8..2BFF ; U
        1,              // 2C00..2E7F ; R
        0,              // 2E80..3000 ; U
        2,              // 3001..3002 ; Tu
        0,              // 3003..3007 ; U
        3,              // 3008..3011 ; Tr
        0,              // 3012..3013 ; U
        3,              // 3014..301F ; Tr
        0,              // 3020..302F ; U
        0,              // 3031..3040 ; U
        0,              // 304A..3062 ; U
        0,              // 3064..3082 ; U
        0,              // 3088..308D ; U
        0,              // 308F..3094 ; U
        2,              // 3095..3096 ; Tu
        0,              // 3097..309A ; U
        2,              // 309B..309C ; Tu
        0,              // 309D..309F ; U
        0,              // 30AA..30C2 ; U
        0,              // 30C4..30E2 ; U
        0,              // 30E8..30ED ; U
        0,              // 30EF..30F4 ; U
        2,              // 30F5..30F6 ; Tu
        0,              // 30F7..30FB ; U
        0,              // 30FD..3126 ; U
        0,              // 3128..31EF ; U
        2,              // 31F0..31FF ; Tu
        0,              // 3200..32FF ; U
        2,              // 3300..3357 ; Tu
        0,              // 3358..337A ; U
        2,              // 337B..337F ; Tu
        0,              // 3380..A4CF ; U
        0,              // 3400..4DBF ; U
        1,              // A4D0..A95F ; R
        0,              // A960..A97F ; U
        1,              // A980..ABFF ; R
        0,              // AC00..D7FF ; U
        1,              // D800..DFFF ; R
        0,              // E000..FAFF ; U
        1,              // FB00..FE0F ; R
        0,              // FE10..FE1F ; U
        1,              // FE20..FE2F ; R
        0,              // FE30..FE48 ; U
        1,              // FE49..FE4F ; R
        2,              // FE50..FE52 ; Tu
        0,              // FE53..FE57 ; U
        3,              // FE59..FE5E ; Tr
        0,              // FE5F..FE62 ; U
        1,              // FE63..FE66 ; R
        0,              // FE67..FE6F ; U
        1,              // FE70..FF00 ; R
        0,              // FF02..FF07 ; U
        3,              // FF08..FF09 ; Tr
        0,              // FF0A..FF0B ; U
        0,              // FF0F..FF19 ; U
        3,              // FF1A..FF1B ; Tr
        1,              // FF1C..FF1E ; R
        0,              // FF20..FF3A ; U
        0,              // FF40..FF5A ; U
        3,              // FF5B..FF60 ; Tr
        1,              // FF61..FFDF ; R
        0,              // FFE0..FFE2 ; U
        0,              // FFE4..FFE7 ; U
        1,              // FFE8..FFEF ; R
        0,              // FFF0..FFF8 ; U
        1,              // FFF9..FFFB ; R
        0,              // FFFC..FFFD ; U
        1               // FFFE..FFFF ; R
    };

    private static final int[] voSingles1 = new int[] {
        0x2016,         // 2016..2016 ; U
        0x2026,         // 2016..2016 ; Tr
        0x3030,         // 3030..3030 ; Tr
        0x3041,         // 3041..3041 ; Tu
        0x3042,         // 3042..3042 ; U
        0x3043,         // 3043..3043 ; Tu
        0x3044,         // 3044..3044 ; U
        0x3045,         // 3045..3045 ; Tu
        0x3046,         // 3046..3046 ; U
        0x3047,         // 3047..3047 ; Tu
        0x3048,         // 3048..3048 ; U
        0x3049,         // 3049..3049 ; Tu
        0x3063,         // 3063..3063 ; Tu
        0x3083,         // 3083..3083 ; Tu
        0x3084,         // 3084..3084 ; U
        0x3085,         // 3085..3085 ; Tu
        0x3086,         // 3086..3086 ; U
        0x3087,         // 3087..3087 ; Tu
        0x308E,         // 308E..308E ; Tu
        0x30A0,         // 30A0..30A0 ; Tr
        0x30A1,         // 30A1..30A1 ; Tu
        0x30A2,         // 30A2..30A2 ; U
        0x30A3,         // 30A3..30A3 ; Tu
        0x30A4,         // 30A4..30A4 ; U
        0x30A5,         // 30A5..30A5 ; Tu
        0x30A6,         // 30A6..30A6 ; U
        0x30A7,         // 30A7..30A7 ; Tu
        0x30A8,         // 30A8..30A8 ; U
        0x30A9,         // 30A9..30A9 ; Tu
        0x30C3,         // 30C3..30C3 ; Tu
        0x30E3,         // 30E3..30E3 ; Tu
        0x30E4,         // 30E4..30E4 ; U
        0x30E5,         // 30E5..30E5 ; Tu
        0x30E6,         // 30E6..30E6 ; U
        0x30E7,         // 30E7..30E7 ; Tu
        0x30EE,         // 30EE..30EE ; Tu
        0x30FC,         // 30FC..30FC ; Tr
        0x3127,         // 3127..3127 ; Tu
        0xFE58,         // FE58..FE58 ; R
        0xFF01,         // FF01..FF01 ; Tu
        0xFF0C,         // FF0C..FF0C ; Tu
        0xFF0D,         // FF0D..FF0D ; R
        0xFF0E,         // FF0E..FF0E ; Tu
        0xFF1F,         // FF1F..FF1F ; Tu
        0xFF3B,         // FF3B..FF3B ; Tr
        0xFF3C,         // FF3C..FF3C ; U
        0xFF3D,         // FF3D..FF3D ; Tr
        0xFF3E,         // FF3E..FF3E ; U
        0xFF3F,         // FF3F..FF3F ; Tr
        0xFFE3          // FFE3..FFE3 ; Tr
    };

    private static final byte[] voSingles1Values = new byte[] {
        3,              // 2016..2016 ; Tr
        3,              // 2026..2026 ; Tr
        3,              // 3030..3030 ; Tr
        2,              // 3041..3041 ; Tu
        0,              // 3042..3042 ; U
        2,              // 3043..3043 ; Tu
        0,              // 3044..3044 ; U
        2,              // 3045..3045 ; Tu
        0,              // 3046..3046 ; U
        2,              // 3047..3047 ; Tu
        0,              // 3048..3048 ; U
        2,              // 3049..3049 ; Tu
        2,              // 3063..3063 ; Tu
        2,              // 3083..3083 ; Tu
        0,              // 3084..3084 ; U
        2,              // 3085..3085 ; Tu
        0,              // 3086..3086 ; U
        2,              // 3087..3087 ; Tu
        2,              // 308E..308E ; Tu
        3,              // 30A0..30A0 ; Tr
        2,              // 30A1..30A1 ; Tu
        0,              // 30A2..30A2 ; U
        2,              // 30A3..30A3 ; Tu
        0,              // 30A4..30A4 ; U
        2,              // 30A5..30A5 ; Tu
        0,              // 30A6..30A6 ; U
        2,              // 30A7..30A7 ; Tu
        0,              // 30A8..30A8 ; U
        2,              // 30A9..30A9 ; Tu
        2,              // 30C3..30C3 ; Tu
        2,              // 30E3..30E3 ; Tu
        0,              // 30E4..30E4 ; U
        2,              // 30E5..30E5 ; Tu
        0,              // 30E6..30E6 ; U
        2,              // 30E7..30E7 ; Tu
        2,              // 30EE..30EE ; Tu
        3,              // 30FC..30FC ; Tr
        2,              // 3127..3127 ; Tu
        1,              // FE58..FE58 ; R
        2,              // FF01..FF01 ; Tu
        2,              // FF0C..FF0C ; Tu
        1,              // FF0D..FF0D ; R
        2,              // FF0E..FF0E ; Tu
        2,              // FF1F..FF1F ; Tu
        3,              // FF3B..FF3B ; Tr
        0,              // FF3C..FF3C ; U
        3,              // FF3D..FF3D ; Tr
        0,              // FF3E..FF3E ; U
        3,              // FF3F..FF3F ; Tr
        3               // FFE3..FFE3 ; Tr
    };

    public static VerticalOrientation getVerticalOrientation(int c) {
        VerticalOrientation vo;
        vo = lookupOrientationInRange0(c);
        if (vo != null)
            return vo;
        vo = lookupOrientationInRanges(c);
        if (vo != null)
            return vo;
        vo = lookupOrientationInSingles(c);
        if (vo != null)
            return vo;
        return VerticalOrientation.U;
    }

    private static VerticalOrientation lookupOrientationInRange0(int c) {
        assert voRanges1.length >= 2;
        assert voRanges1Values.length >= 1;
        int c0 = voRanges1[0];
        int c1 = voRanges1[1];
        return ((c >= c0) && (c <= c1)) ? verticalOrientations[voRanges1Values[0]] : null;
    }

    private static VerticalOrientation lookupOrientationInRanges(int c) {
        int n = voRanges1.length / 2;
        if (n > 0) {
            for (int f = 0, t = n; f <= t; ) {
                int m  = (f + t) / 2;
                int i  = 2 * m;
                int c0 = voRanges1[i + 0];
                int c1 = voRanges1[i + 1];
                if (c1 < c) {
                    if ((f = m + 1) > n)
                        break;
                } else if (c0 > c) {
                    if ((t = m - 1) < 0)
                        break;
                } else
                    return verticalOrientations[voRanges1Values[m]];
            }
        }
        return null;
    }

    private static VerticalOrientation lookupOrientationInSingles(int c) {
        int n = voSingles1.length;
        if (n > 0) {
            for (int f = 0, t = n;  f <= t; ) {
                int m  = (f + t) >>> 1;
                int c0 = voSingles1[m];
                if (c0 < c) {
                    if ((f = m + 1) > n)
                        break;
                } else if (c0 > c) {
                    if ((t = m - 1) < 0)
                        break;
                } else
                    return verticalOrientations[voSingles1Values[m]];
            }
        }
        return null;
    }

    public static boolean isUprightOrientation(int c) {
        VerticalOrientation vo = getVerticalOrientation(c);
        if (vo == VerticalOrientation.U)
            return true;
        else if (vo == VerticalOrientation.Tu)
            return true;
        else if ((vo == VerticalOrientation.Tr) && hasVertical(c))
            return true;
        else
            return false;
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

    private static final int[] h2vKey = new int[] {
        0x2026
    };

    private static final int[] h2vVal = new int[] {
        0xFE19,
    };

    public static boolean hasVertical(int c) {
        return Arrays.binarySearch(h2vKey, c) >= 0;
    }

    public static int toVertical(int c) {
        int k = Arrays.binarySearch(h2vKey, c);
        if (k >= 0)
            return h2vVal[k];
        else
            return c;
    }

}
