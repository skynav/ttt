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
    public static final int   UC_LRI                            = '\u2066';             // left-to-right isolate
    public static final int   UC_RLI                            = '\u2067';             // right-to-left isolate
    public static final int   UC_PDI                            = '\u2069';             // pop directional isolate
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

    public static boolean isBidiControl(int c) {
        if (c == UC_LRE)
            return true;
        else if (c == UC_RLE)
            return true;
        else if (c == UC_LRO)
            return true;
        else if (c == UC_RLO)
            return true;
        else if (c == UC_PDF)
            return true;
        else if (c == UC_LRI)
            return true;
        else if (c == UC_RLI)
            return true;
        else if (c == UC_PDI)
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
        else if (vo == VerticalOrientation.Tr) {
            // TBD - should return false if font doesn't have a Tr mapping, but we don't have a font binding here; so just assume it does have a mapping;
            // if it doesn't have one, then will get an upright glyph when we want a rotated glyph
            return true;
        } else
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
        0xFE19
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

    private static final int[] mirKey = new int[] {
        0x0028,         // LEFT PARENTHESIS
        0x0029,         // RIGHT PARENTHESIS
        0x003C,         // LESS-THAN SIGN
        0x003E,         // GREATER-THAN SIGN
        0x005B,         // LEFT SQUARE BRACKET
        0x005D,         // RIGHT SQUARE BRACKET
        0x007B,         // LEFT CURLY BRACKET
        0x007D,         // RIGHT CURLY BRACKET
        0x00AB,         // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
        0x00BB,         // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
        0x0F3A,         // TIBETAN MARK GUG RTAGS GYON
        0x0F3B,         // TIBETAN MARK GUG RTAGS GYAS
        0x0F3C,         // TIBETAN MARK ANG KHANG GYON
        0x0F3D,         // TIBETAN MARK ANG KHANG GYAS
        0x169B,         // OGHAM FEATHER MARK
        0x169C,         // OGHAM REVERSED FEATHER MARK
        0x2039,         // SINGLE LEFT-POINTING ANGLE QUOTATION MARK
        0x203A,         // SINGLE RIGHT-POINTING ANGLE QUOTATION MARK
        0x2045,         // LEFT SQUARE BRACKET WITH QUILL
        0x2046,         // RIGHT SQUARE BRACKET WITH QUILL
        0x207D,         // SUPERSCRIPT LEFT PARENTHESIS
        0x207E,         // SUPERSCRIPT RIGHT PARENTHESIS
        0x208D,         // SUBSCRIPT LEFT PARENTHESIS
        0x208E,         // SUBSCRIPT RIGHT PARENTHESIS
        0x2208,         // ELEMENT OF
        0x2209,         // NOT AN ELEMENT OF
        0x220A,         // SMALL ELEMENT OF
        0x220B,         // CONTAINS AS MEMBER
        0x220C,         // DOES NOT CONTAIN AS MEMBER
        0x220D,         // SMALL CONTAINS AS MEMBER
        0x2215,         // DIVISION SLASH
        0x223C,         // TILDE OPERATOR
        0x223D,         // REVERSED TILDE
        0x2243,         // ASYMPTOTICALLY EQUAL TO
        0x2252,         // APPROXIMATELY EQUAL TO OR THE IMAGE OF
        0x2253,         // IMAGE OF OR APPROXIMATELY EQUAL TO
        0x2254,         // COLON EQUALS
        0x2255,         // EQUALS COLON
        0x2264,         // LESS-THAN OR EQUAL TO
        0x2265,         // GREATER-THAN OR EQUAL TO
        0x2266,         // LESS-THAN OVER EQUAL TO
        0x2267,         // GREATER-THAN OVER EQUAL TO
        0x2268,         // LESS-THAN BUT NOT EQUAL TO
        0x2269,         // GREATER-THAN BUT NOT EQUAL TO
        0x226A,         // MUCH LESS-THAN
        0x226B,         // MUCH GREATER-THAN
        0x226E,         // NOT LESS-THAN
        0x226F,         // NOT GREATER-THAN
        0x2270,         // NEITHER LESS-THAN NOR EQUAL TO
        0x2271,         // NEITHER GREATER-THAN NOR EQUAL TO
        0x2272,         // LESS-THAN OR EQUIVALENT TO
        0x2273,         // GREATER-THAN OR EQUIVALENT TO
        0x2274,         // NEITHER LESS-THAN NOR EQUIVALENT TO
        0x2275,         // NEITHER GREATER-THAN NOR EQUIVALENT TO
        0x2276,         // LESS-THAN OR GREATER-THAN
        0x2277,         // GREATER-THAN OR LESS-THAN
        0x2278,         // NEITHER LESS-THAN NOR GREATER-THAN
        0x2279,         // NEITHER GREATER-THAN NOR LESS-THAN
        0x227A,         // PRECEDES
        0x227B,         // SUCCEEDS
        0x227C,         // PRECEDES OR EQUAL TO
        0x227D,         // SUCCEEDS OR EQUAL TO
        0x227E,         // PRECEDES OR EQUIVALENT TO
        0x227F,         // SUCCEEDS OR EQUIVALENT TO
        0x2280,         // DOES NOT PRECEDE
        0x2281,         // DOES NOT SUCCEED
        0x2282,         // SUBSET OF
        0x2283,         // SUPERSET OF
        0x2284,         // NOT A SUBSET OF
        0x2285,         // NOT A SUPERSET OF
        0x2286,         // SUBSET OF OR EQUAL TO
        0x2287,         // SUPERSET OF OR EQUAL TO
        0x2288,         // NEITHER A SUBSET OF NOR EQUAL TO
        0x2289,         // NEITHER A SUPERSET OF NOR EQUAL TO
        0x228A,         // SUBSET OF WITH NOT EQUAL TO
        0x228B,         // SUPERSET OF WITH NOT EQUAL TO
        0x228F,         // SQUARE IMAGE OF
        0x2290,         // SQUARE ORIGINAL OF
        0x2291,         // SQUARE IMAGE OF OR EQUAL TO
        0x2292,         // SQUARE ORIGINAL OF OR EQUAL TO
        0x2298,         // CIRCLED DIVISION SLASH
        0x22A2,         // RIGHT TACK
        0x22A3,         // LEFT TACK
        0x22A6,         // ASSERTION
        0x22A8,         // TRUE
        0x22A9,         // FORCES
        0x22AB,         // DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE
        0x22B0,         // PRECEDES UNDER RELATION
        0x22B1,         // SUCCEEDS UNDER RELATION
        0x22B2,         // NORMAL SUBGROUP OF
        0x22B3,         // CONTAINS AS NORMAL SUBGROUP
        0x22B4,         // NORMAL SUBGROUP OF OR EQUAL TO
        0x22B5,         // CONTAINS AS NORMAL SUBGROUP OR EQUAL TO
        0x22B6,         // ORIGINAL OF
        0x22B7,         // IMAGE OF
        0x22C9,         // LEFT NORMAL FACTOR SEMIDIRECT PRODUCT
        0x22CA,         // RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT
        0x22CB,         // LEFT SEMIDIRECT PRODUCT
        0x22CC,         // RIGHT SEMIDIRECT PRODUCT
        0x22CD,         // REVERSED TILDE EQUALS
        0x22D0,         // DOUBLE SUBSET
        0x22D1,         // DOUBLE SUPERSET
        0x22D6,         // LESS-THAN WITH DOT
        0x22D7,         // GREATER-THAN WITH DOT
        0x22D8,         // VERY MUCH LESS-THAN
        0x22D9,         // VERY MUCH GREATER-THAN
        0x22DA,         // LESS-THAN EQUAL TO OR GREATER-THAN
        0x22DB,         // GREATER-THAN EQUAL TO OR LESS-THAN
        0x22DC,         // EQUAL TO OR LESS-THAN
        0x22DD,         // EQUAL TO OR GREATER-THAN
        0x22DE,         // EQUAL TO OR PRECEDES
        0x22DF,         // EQUAL TO OR SUCCEEDS
        0x22E0,         // DOES NOT PRECEDE OR EQUAL
        0x22E1,         // DOES NOT SUCCEED OR EQUAL
        0x22E2,         // NOT SQUARE IMAGE OF OR EQUAL TO
        0x22E3,         // NOT SQUARE ORIGINAL OF OR EQUAL TO
        0x22E4,         // SQUARE IMAGE OF OR NOT EQUAL TO
        0x22E5,         // SQUARE ORIGINAL OF OR NOT EQUAL TO
        0x22E6,         // LESS-THAN BUT NOT EQUIVALENT TO
        0x22E7,         // GREATER-THAN BUT NOT EQUIVALENT TO
        0x22E8,         // PRECEDES BUT NOT EQUIVALENT TO
        0x22E9,         // SUCCEEDS BUT NOT EQUIVALENT TO
        0x22EA,         // NOT NORMAL SUBGROUP OF
        0x22EB,         // DOES NOT CONTAIN AS NORMAL SUBGROUP
        0x22EC,         // NOT NORMAL SUBGROUP OF OR EQUAL TO
        0x22ED,         // DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL
        0x22F0,         // UP RIGHT DIAGONAL ELLIPSIS
        0x22F1,         // DOWN RIGHT DIAGONAL ELLIPSIS
        0x22F2,         // ELEMENT OF WITH LONG HORIZONTAL STROKE
        0x22F3,         // ELEMENT OF WITH VERTICAL BAR AT END OF HORIZONTAL STROKE
        0x22F4,         // SMALL ELEMENT OF WITH VERTICAL BAR AT END OF HORIZONTAL STROKE
        0x22F6,         // ELEMENT OF WITH OVERBAR
        0x22F7,         // SMALL ELEMENT OF WITH OVERBAR
        0x22FA,         // CONTAINS WITH LONG HORIZONTAL STROKE
        0x22FB,         // CONTAINS WITH VERTICAL BAR AT END OF HORIZONTAL STROKE
        0x22FC,         // SMALL CONTAINS WITH VERTICAL BAR AT END OF HORIZONTAL STROKE
        0x22FD,         // CONTAINS WITH OVERBAR
        0x22FE,         // SMALL CONTAINS WITH OVERBAR
        0x2308,         // LEFT CEILING
        0x2309,         // RIGHT CEILING
        0x230A,         // LEFT FLOOR
        0x230B,         // RIGHT FLOOR
        0x2329,         // LEFT-POINTING ANGLE BRACKET
        0x232A,         // RIGHT-POINTING ANGLE BRACKET
        0x2768,         // MEDIUM LEFT PARENTHESIS ORNAMENT
        0x2769,         // MEDIUM RIGHT PARENTHESIS ORNAMENT
        0x276A,         // MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT
        0x276B,         // MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT
        0x276C,         // MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT
        0x276D,         // MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT
        0x276E,         // HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT
        0x276F,         // HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT
        0x2770,         // HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT
        0x2771,         // HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT
        0x2772,         // LIGHT LEFT TORTOISE SHELL BRACKET
        0x2773,         // LIGHT RIGHT TORTOISE SHELL BRACKET
        0x2774,         // MEDIUM LEFT CURLY BRACKET ORNAMENT
        0x2775,         // MEDIUM RIGHT CURLY BRACKET ORNAMENT
        0x27C3,         // OPEN SUBSET
        0x27C4,         // OPEN SUPERSET
        0x27C5,         // LEFT S-SHAPED BAG DELIMITER
        0x27C6,         // RIGHT S-SHAPED BAG DELIMITER
        0x27C8,         // REVERSE SOLIDUS PRECEDING SUBSET
        0x27C9,         // SUPERSET PRECEDING SOLIDUS
        0x27D5,         // LEFT OUTER JOIN
        0x27D6,         // RIGHT OUTER JOIN
        0x27DD,         // LONG RIGHT TACK
        0x27DE,         // LONG LEFT TACK
        0x27E2,         // WHITE CONCAVE-SIDED DIAMOND WITH LEFTWARDS TICK
        0x27E3,         // WHITE CONCAVE-SIDED DIAMOND WITH RIGHTWARDS TICK
        0x27E4,         // WHITE SQUARE WITH LEFTWARDS TICK
        0x27E5,         // WHITE SQUARE WITH RIGHTWARDS TICK
        0x27E6,         // MATHEMATICAL LEFT WHITE SQUARE BRACKET
        0x27E7,         // MATHEMATICAL RIGHT WHITE SQUARE BRACKET
        0x27E8,         // MATHEMATICAL LEFT ANGLE BRACKET
        0x27E9,         // MATHEMATICAL RIGHT ANGLE BRACKET
        0x27EA,         // MATHEMATICAL LEFT DOUBLE ANGLE BRACKET
        0x27EB,         // MATHEMATICAL RIGHT DOUBLE ANGLE BRACKET
        0x27EC,         // MATHEMATICAL LEFT WHITE TORTOISE SHELL BRACKET
        0x27ED,         // MATHEMATICAL RIGHT WHITE TORTOISE SHELL BRACKET
        0x27EE,         // MATHEMATICAL LEFT FLATTENED PARENTHESIS
        0x27EF,         // MATHEMATICAL RIGHT FLATTENED PARENTHESIS
        0x2983,         // LEFT WHITE CURLY BRACKET
        0x2984,         // RIGHT WHITE CURLY BRACKET
        0x2985,         // LEFT WHITE PARENTHESIS
        0x2986,         // RIGHT WHITE PARENTHESIS
        0x2987,         // Z NOTATION LEFT IMAGE BRACKET
        0x2988,         // Z NOTATION RIGHT IMAGE BRACKET
        0x2989,         // Z NOTATION LEFT BINDING BRACKET
        0x298A,         // Z NOTATION RIGHT BINDING BRACKET
        0x298B,         // LEFT SQUARE BRACKET WITH UNDERBAR
        0x298C,         // RIGHT SQUARE BRACKET WITH UNDERBAR
        0x298D,         // LEFT SQUARE BRACKET WITH TICK IN TOP CORNER
        0x298E,         // RIGHT SQUARE BRACKET WITH TICK IN BOTTOM CORNER
        0x298F,         // LEFT SQUARE BRACKET WITH TICK IN BOTTOM CORNER
        0x2990,         // RIGHT SQUARE BRACKET WITH TICK IN TOP CORNER
        0x2991,         // LEFT ANGLE BRACKET WITH DOT
        0x2992,         // RIGHT ANGLE BRACKET WITH DOT
        0x2993,         // LEFT ARC LESS-THAN BRACKET
        0x2994,         // RIGHT ARC GREATER-THAN BRACKET
        0x2995,         // DOUBLE LEFT ARC GREATER-THAN BRACKET
        0x2996,         // DOUBLE RIGHT ARC LESS-THAN BRACKET
        0x2997,         // LEFT BLACK TORTOISE SHELL BRACKET
        0x2998,         // RIGHT BLACK TORTOISE SHELL BRACKET
        0x29B8,         // CIRCLED REVERSE SOLIDUS
        0x29C0,         // CIRCLED LESS-THAN
        0x29C1,         // CIRCLED GREATER-THAN
        0x29C4,         // SQUARED RISING DIAGONAL SLASH
        0x29C5,         // SQUARED FALLING DIAGONAL SLASH
        0x29CF,         // LEFT TRIANGLE BESIDE VERTICAL BAR
        0x29D0,         // VERTICAL BAR BESIDE RIGHT TRIANGLE
        0x29D1,         // BOWTIE WITH LEFT HALF BLACK
        0x29D2,         // BOWTIE WITH RIGHT HALF BLACK
        0x29D4,         // TIMES WITH LEFT HALF BLACK
        0x29D5,         // TIMES WITH RIGHT HALF BLACK
        0x29D8,         // LEFT WIGGLY FENCE
        0x29D9,         // RIGHT WIGGLY FENCE
        0x29DA,         // LEFT DOUBLE WIGGLY FENCE
        0x29DB,         // RIGHT DOUBLE WIGGLY FENCE
        0x29F5,         // REVERSE SOLIDUS OPERATOR
        0x29F8,         // BIG SOLIDUS
        0x29F9,         // BIG REVERSE SOLIDUS
        0x29FC,         // LEFT-POINTING CURVED ANGLE BRACKET
        0x29FD,         // RIGHT-POINTING CURVED ANGLE BRACKET
        0x2A2B,         // MINUS SIGN WITH FALLING DOTS
        0x2A2C,         // MINUS SIGN WITH RISING DOTS
        0x2A2D,         // PLUS SIGN IN LEFT HALF CIRCLE
        0x2A2E,         // PLUS SIGN IN RIGHT HALF CIRCLE
        0x2A34,         // MULTIPLICATION SIGN IN LEFT HALF CIRCLE
        0x2A35,         // MULTIPLICATION SIGN IN RIGHT HALF CIRCLE
        0x2A3C,         // INTERIOR PRODUCT
        0x2A3D,         // RIGHTHAND INTERIOR PRODUCT
        0x2A64,         // Z NOTATION DOMAIN ANTIRESTRICTION
        0x2A65,         // Z NOTATION RANGE ANTIRESTRICTION
        0x2A79,         // LESS-THAN WITH CIRCLE INSIDE
        0x2A7A,         // GREATER-THAN WITH CIRCLE INSIDE
        0x2A7D,         // LESS-THAN OR SLANTED EQUAL TO
        0x2A7E,         // GREATER-THAN OR SLANTED EQUAL TO
        0x2A7F,         // LESS-THAN OR SLANTED EQUAL TO WITH DOT INSIDE
        0x2A80,         // GREATER-THAN OR SLANTED EQUAL TO WITH DOT INSIDE
        0x2A81,         // LESS-THAN OR SLANTED EQUAL TO WITH DOT ABOVE
        0x2A82,         // GREATER-THAN OR SLANTED EQUAL TO WITH DOT ABOVE
        0x2A83,         // LESS-THAN OR SLANTED EQUAL TO WITH DOT ABOVE RIGHT
        0x2A84,         // GREATER-THAN OR SLANTED EQUAL TO WITH DOT ABOVE LEFT
        0x2A8B,         // LESS-THAN ABOVE DOUBLE-LINE EQUAL ABOVE GREATER-THAN
        0x2A8C,         // GREATER-THAN ABOVE DOUBLE-LINE EQUAL ABOVE LESS-THAN
        0x2A91,         // LESS-THAN ABOVE GREATER-THAN ABOVE DOUBLE-LINE EQUAL
        0x2A92,         // GREATER-THAN ABOVE LESS-THAN ABOVE DOUBLE-LINE EQUAL
        0x2A93,         // LESS-THAN ABOVE SLANTED EQUAL ABOVE GREATER-THAN ABOVE SLANTED EQUAL
        0x2A94,         // GREATER-THAN ABOVE SLANTED EQUAL ABOVE LESS-THAN ABOVE SLANTED EQUAL
        0x2A95,         // SLANTED EQUAL TO OR LESS-THAN
        0x2A96,         // SLANTED EQUAL TO OR GREATER-THAN
        0x2A97,         // SLANTED EQUAL TO OR LESS-THAN WITH DOT INSIDE
        0x2A98,         // SLANTED EQUAL TO OR GREATER-THAN WITH DOT INSIDE
        0x2A99,         // DOUBLE-LINE EQUAL TO OR LESS-THAN
        0x2A9A,         // DOUBLE-LINE EQUAL TO OR GREATER-THAN
        0x2A9B,         // DOUBLE-LINE SLANTED EQUAL TO OR LESS-THAN
        0x2A9C,         // DOUBLE-LINE SLANTED EQUAL TO OR GREATER-THAN
        0x2AA1,         // DOUBLE NESTED LESS-THAN
        0x2AA2,         // DOUBLE NESTED GREATER-THAN
        0x2AA6,         // LESS-THAN CLOSED BY CURVE
        0x2AA7,         // GREATER-THAN CLOSED BY CURVE
        0x2AA8,         // LESS-THAN CLOSED BY CURVE ABOVE SLANTED EQUAL
        0x2AA9,         // GREATER-THAN CLOSED BY CURVE ABOVE SLANTED EQUAL
        0x2AAA,         // SMALLER THAN
        0x2AAB,         // LARGER THAN
        0x2AAC,         // SMALLER THAN OR EQUAL TO
        0x2AAD,         // LARGER THAN OR EQUAL TO
        0x2AAF,         // PRECEDES ABOVE SINGLE-LINE EQUALS SIGN
        0x2AB0,         // SUCCEEDS ABOVE SINGLE-LINE EQUALS SIGN
        0x2AB3,         // PRECEDES ABOVE EQUALS SIGN
        0x2AB4,         // SUCCEEDS ABOVE EQUALS SIGN
        0x2ABB,         // DOUBLE PRECEDES
        0x2ABC,         // DOUBLE SUCCEEDS
        0x2ABD,         // SUBSET WITH DOT
        0x2ABE,         // SUPERSET WITH DOT
        0x2ABF,         // SUBSET WITH PLUS SIGN BELOW
        0x2AC0,         // SUPERSET WITH PLUS SIGN BELOW
        0x2AC1,         // SUBSET WITH MULTIPLICATION SIGN BELOW
        0x2AC2,         // SUPERSET WITH MULTIPLICATION SIGN BELOW
        0x2AC3,         // SUBSET OF OR EQUAL TO WITH DOT ABOVE
        0x2AC4,         // SUPERSET OF OR EQUAL TO WITH DOT ABOVE
        0x2AC5,         // SUBSET OF ABOVE EQUALS SIGN
        0x2AC6,         // SUPERSET OF ABOVE EQUALS SIGN
        0x2ACD,         // SQUARE LEFT OPEN BOX OPERATOR
        0x2ACE,         // SQUARE RIGHT OPEN BOX OPERATOR
        0x2ACF,         // CLOSED SUBSET
        0x2AD0,         // CLOSED SUPERSET
        0x2AD1,         // CLOSED SUBSET OR EQUAL TO
        0x2AD2,         // CLOSED SUPERSET OR EQUAL TO
        0x2AD3,         // SUBSET ABOVE SUPERSET
        0x2AD4,         // SUPERSET ABOVE SUBSET
        0x2AD5,         // SUBSET ABOVE SUBSET
        0x2AD6,         // SUPERSET ABOVE SUPERSET
        0x2ADE,         // SHORT LEFT TACK
        0x2AE3,         // DOUBLE VERTICAL BAR LEFT TURNSTILE
        0x2AE4,         // VERTICAL BAR DOUBLE LEFT TURNSTILE
        0x2AE5,         // DOUBLE VERTICAL BAR DOUBLE LEFT TURNSTILE
        0x2AEC,         // DOUBLE STROKE NOT SIGN
        0x2AED,         // REVERSED DOUBLE STROKE NOT SIGN
        0x2AF7,         // TRIPLE NESTED LESS-THAN
        0x2AF8,         // TRIPLE NESTED GREATER-THAN
        0x2AF9,         // DOUBLE-LINE SLANTED LESS-THAN OR EQUAL TO
        0x2AFA,         // DOUBLE-LINE SLANTED GREATER-THAN OR EQUAL TO
        0x2E02,         // LEFT SUBSTITUTION BRACKET
        0x2E03,         // RIGHT SUBSTITUTION BRACKET
        0x2E04,         // LEFT DOTTED SUBSTITUTION BRACKET
        0x2E05,         // RIGHT DOTTED SUBSTITUTION BRACKET
        0x2E09,         // LEFT TRANSPOSITION BRACKET
        0x2E0A,         // RIGHT TRANSPOSITION BRACKET
        0x2E0C,         // LEFT RAISED OMISSION BRACKET
        0x2E0D,         // RIGHT RAISED OMISSION BRACKET
        0x2E1C,         // LEFT LOW PARAPHRASE BRACKET
        0x2E1D,         // RIGHT LOW PARAPHRASE BRACKET
        0x2E20,         // LEFT VERTICAL BAR WITH QUILL
        0x2E21,         // RIGHT VERTICAL BAR WITH QUILL
        0x2E22,         // TOP LEFT HALF BRACKET
        0x2E23,         // TOP RIGHT HALF BRACKET
        0x2E24,         // BOTTOM LEFT HALF BRACKET
        0x2E25,         // BOTTOM RIGHT HALF BRACKET
        0x2E26,         // LEFT SIDEWAYS U BRACKET
        0x2E27,         // RIGHT SIDEWAYS U BRACKET
        0x2E28,         // LEFT DOUBLE PARENTHESIS
        0x2E29,         // RIGHT DOUBLE PARENTHESIS
        0x3008,         // LEFT ANGLE BRACKET
        0x3009,         // RIGHT ANGLE BRACKET
        0x300A,         // LEFT DOUBLE ANGLE BRACKET
        0x300B,         // RIGHT DOUBLE ANGLE BRACKET
        0x300C,         // LEFT CORNER BRACKET
        0x300D,         // RIGHT CORNER BRACKET
        0x300E,         // LEFT WHITE CORNER BRACKET
        0x300F,         // RIGHT WHITE CORNER BRACKET
        0x3010,         // LEFT BLACK LENTICULAR BRACKET
        0x3011,         // RIGHT BLACK LENTICULAR BRACKET
        0x3014,         // LEFT TORTOISE SHELL BRACKET
        0x3015,         // RIGHT TORTOISE SHELL BRACKET
        0x3016,         // LEFT WHITE LENTICULAR BRACKET
        0x3017,         // RIGHT WHITE LENTICULAR BRACKET
        0x3018,         // LEFT WHITE TORTOISE SHELL BRACKET
        0x3019,         // RIGHT WHITE TORTOISE SHELL BRACKET
        0x301A,         // LEFT WHITE SQUARE BRACKET
        0x301B,         // RIGHT WHITE SQUARE BRACKET
        0xFE59,         // SMALL LEFT PARENTHESIS
        0xFE5A,         // SMALL RIGHT PARENTHESIS
        0xFE5B,         // SMALL LEFT CURLY BRACKET
        0xFE5C,         // SMALL RIGHT CURLY BRACKET
        0xFE5D,         // SMALL LEFT TORTOISE SHELL BRACKET
        0xFE5E,         // SMALL RIGHT TORTOISE SHELL BRACKET
        0xFE64,         // SMALL LESS-THAN SIGN
        0xFE65,         // SMALL GREATER-THAN SIGN
        0xFF08,         // FULLWIDTH LEFT PARENTHESIS
        0xFF09,         // FULLWIDTH RIGHT PARENTHESIS
        0xFF1C,         // FULLWIDTH LESS-THAN SIGN
        0xFF1E,         // FULLWIDTH GREATER-THAN SIGN
        0xFF3B,         // FULLWIDTH LEFT SQUARE BRACKET
        0xFF3D,         // FULLWIDTH RIGHT SQUARE BRACKET
        0xFF5B,         // FULLWIDTH LEFT CURLY BRACKET
        0xFF5D,         // FULLWIDTH RIGHT CURLY BRACKET
        0xFF5F,         // FULLWIDTH LEFT WHITE PARENTHESIS
        0xFF60,         // FULLWIDTH RIGHT WHITE PARENTHESIS
        0xFF62,         // HALFWIDTH LEFT CORNER BRACKET
        0xFF63,         // HALFWIDTH RIGHT CORNER BRACKET
    };

    private static final int[] mirVal = new int[] {
        0x0029,         // MIRROR(LEFT PARENTHESIS)
        0x0028,         // MIRROR(RIGHT PARENTHESIS)
        0x003E,         // MIRROR(LESS-THAN SIGN)
        0x003C,         // MIRROR(GREATER-THAN SIGN)
        0x005D,         // MIRROR(LEFT SQUARE BRACKET)
        0x005B,         // MIRROR(RIGHT SQUARE BRACKET)
        0x007D,         // MIRROR(LEFT CURLY BRACKET)
        0x007B,         // MIRROR(RIGHT CURLY BRACKET)
        0x00BB,         // MIRROR(LEFT-POINTING DOUBLE ANGLE QUOTATION MARK)
        0x00AB,         // MIRROR(RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK)
        0x0F3B,         // MIRROR(TIBETAN MARK GUG RTAGS GYON)
        0x0F3A,         // MIRROR(TIBETAN MARK GUG RTAGS GYAS)
        0x0F3D,         // MIRROR(TIBETAN MARK ANG KHANG GYON)
        0x0F3C,         // MIRROR(TIBETAN MARK ANG KHANG GYAS)
        0x169C,         // MIRROR(OGHAM FEATHER MARK)
        0x169B,         // MIRROR(OGHAM REVERSED FEATHER MARK)
        0x203A,         // MIRROR(SINGLE LEFT-POINTING ANGLE QUOTATION MARK)
        0x2039,         // MIRROR(SINGLE RIGHT-POINTING ANGLE QUOTATION MARK)
        0x2046,         // MIRROR(LEFT SQUARE BRACKET WITH QUILL)
        0x2045,         // MIRROR(RIGHT SQUARE BRACKET WITH QUILL)
        0x207E,         // MIRROR(SUPERSCRIPT LEFT PARENTHESIS)
        0x207D,         // MIRROR(SUPERSCRIPT RIGHT PARENTHESIS)
        0x208E,         // MIRROR(SUBSCRIPT LEFT PARENTHESIS)
        0x208D,         // MIRROR(SUBSCRIPT RIGHT PARENTHESIS)
        0x220B,         // MIRROR(ELEMENT OF)
        0x220C,         // MIRROR(NOT AN ELEMENT OF)
        0x220D,         // MIRROR(SMALL ELEMENT OF)
        0x2208,         // MIRROR(CONTAINS AS MEMBER)
        0x2209,         // MIRROR(DOES NOT CONTAIN AS MEMBER)
        0x220A,         // MIRROR(SMALL CONTAINS AS MEMBER)
        0x29F5,         // MIRROR(DIVISION SLASH)
        0x223D,         // MIRROR(TILDE OPERATOR)
        0x223C,         // MIRROR(REVERSED TILDE)
        0x22CD,         // MIRROR(ASYMPTOTICALLY EQUAL TO)
        0x2253,         // MIRROR(APPROXIMATELY EQUAL TO OR THE IMAGE OF)
        0x2252,         // MIRROR(IMAGE OF OR APPROXIMATELY EQUAL TO)
        0x2255,         // MIRROR(COLON EQUALS)
        0x2254,         // MIRROR(EQUALS COLON)
        0x2265,         // MIRROR(LESS-THAN OR EQUAL TO)
        0x2264,         // MIRROR(GREATER-THAN OR EQUAL TO)
        0x2267,         // MIRROR(LESS-THAN OVER EQUAL TO)
        0x2266,         // MIRROR(GREATER-THAN OVER EQUAL TO)
        0x2269,         // MIRROR(LESS-THAN BUT NOT EQUAL TO)
        0x2268,         // MIRROR(GREATER-THAN BUT NOT EQUAL TO)
        0x226B,         // MIRROR(MUCH LESS-THAN)
        0x226A,         // MIRROR(MUCH GREATER-THAN)
        0x226F,         // MIRROR(NOT LESS-THAN)
        0x226E,         // MIRROR(NOT GREATER-THAN)
        0x2271,         // MIRROR(NEITHER LESS-THAN NOR EQUAL TO)
        0x2270,         // MIRROR(NEITHER GREATER-THAN NOR EQUAL TO)
        0x2273,         // MIRROR(LESS-THAN OR EQUIVALENT TO)
        0x2272,         // MIRROR(GREATER-THAN OR EQUIVALENT TO)
        0x2275,         // MIRROR(NEITHER LESS-THAN NOR EQUIVALENT TO)
        0x2274,         // MIRROR(NEITHER GREATER-THAN NOR EQUIVALENT TO)
        0x2277,         // MIRROR(LESS-THAN OR GREATER-THAN)
        0x2276,         // MIRROR(GREATER-THAN OR LESS-THAN)
        0x2279,         // MIRROR(NEITHER LESS-THAN NOR GREATER-THAN)
        0x2278,         // MIRROR(NEITHER GREATER-THAN NOR LESS-THAN)
        0x227B,         // MIRROR(PRECEDES)
        0x227A,         // MIRROR(SUCCEEDS)
        0x227D,         // MIRROR(PRECEDES OR EQUAL TO)
        0x227C,         // MIRROR(SUCCEEDS OR EQUAL TO)
        0x227F,         // MIRROR(PRECEDES OR EQUIVALENT TO)
        0x227E,         // MIRROR(SUCCEEDS OR EQUIVALENT TO)
        0x2281,         // MIRROR(DOES NOT PRECEDE)
        0x2280,         // MIRROR(DOES NOT SUCCEED)
        0x2283,         // MIRROR(SUBSET OF)
        0x2282,         // MIRROR(SUPERSET OF)
        0x2285,         // MIRROR(NOT A SUBSET OF)
        0x2284,         // MIRROR(NOT A SUPERSET OF)
        0x2287,         // MIRROR(SUBSET OF OR EQUAL TO)
        0x2286,         // MIRROR(SUPERSET OF OR EQUAL TO)
        0x2289,         // MIRROR(NEITHER A SUBSET OF NOR EQUAL TO)
        0x2288,         // MIRROR(NEITHER A SUPERSET OF NOR EQUAL TO)
        0x228B,         // MIRROR(SUBSET OF WITH NOT EQUAL TO)
        0x228A,         // MIRROR(SUPERSET OF WITH NOT EQUAL TO)
        0x2290,         // MIRROR(SQUARE IMAGE OF)
        0x228F,         // MIRROR(SQUARE ORIGINAL OF)
        0x2292,         // MIRROR(SQUARE IMAGE OF OR EQUAL TO)
        0x2291,         // MIRROR(SQUARE ORIGINAL OF OR EQUAL TO)
        0x29B8,         // MIRROR(CIRCLED DIVISION SLASH)
        0x22A3,         // MIRROR(RIGHT TACK)
        0x22A2,         // MIRROR(LEFT TACK)
        0x2ADE,         // MIRROR(ASSERTION)
        0x2AE4,         // MIRROR(TRUE)
        0x2AE3,         // MIRROR(FORCES)
        0x2AE5,         // MIRROR(DOUBLE VERTICAL BAR DOUBLE RIGHT TURNSTILE)
        0x22B1,         // MIRROR(PRECEDES UNDER RELATION)
        0x22B0,         // MIRROR(SUCCEEDS UNDER RELATION)
        0x22B3,         // MIRROR(NORMAL SUBGROUP OF)
        0x22B2,         // MIRROR(CONTAINS AS NORMAL SUBGROUP)
        0x22B5,         // MIRROR(NORMAL SUBGROUP OF OR EQUAL TO)
        0x22B4,         // MIRROR(CONTAINS AS NORMAL SUBGROUP OR EQUAL TO)
        0x22B7,         // MIRROR(ORIGINAL OF)
        0x22B6,         // MIRROR(IMAGE OF)
        0x22CA,         // MIRROR(LEFT NORMAL FACTOR SEMIDIRECT PRODUCT)
        0x22C9,         // MIRROR(RIGHT NORMAL FACTOR SEMIDIRECT PRODUCT)
        0x22CC,         // MIRROR(LEFT SEMIDIRECT PRODUCT)
        0x22CB,         // MIRROR(RIGHT SEMIDIRECT PRODUCT)
        0x2243,         // MIRROR(REVERSED TILDE EQUALS)
        0x22D1,         // MIRROR(DOUBLE SUBSET)
        0x22D0,         // MIRROR(DOUBLE SUPERSET)
        0x22D7,         // MIRROR(LESS-THAN WITH DOT)
        0x22D6,         // MIRROR(GREATER-THAN WITH DOT)
        0x22D9,         // MIRROR(VERY MUCH LESS-THAN)
        0x22D8,         // MIRROR(VERY MUCH GREATER-THAN)
        0x22DB,         // MIRROR(LESS-THAN EQUAL TO OR GREATER-THAN)
        0x22DA,         // MIRROR(GREATER-THAN EQUAL TO OR LESS-THAN)
        0x22DD,         // MIRROR(EQUAL TO OR LESS-THAN)
        0x22DC,         // MIRROR(EQUAL TO OR GREATER-THAN)
        0x22DF,         // MIRROR(EQUAL TO OR PRECEDES)
        0x22DE,         // MIRROR(EQUAL TO OR SUCCEEDS)
        0x22E1,         // MIRROR(DOES NOT PRECEDE OR EQUAL)
        0x22E0,         // MIRROR(DOES NOT SUCCEED OR EQUAL)
        0x22E3,         // MIRROR(NOT SQUARE IMAGE OF OR EQUAL TO)
        0x22E2,         // MIRROR(NOT SQUARE ORIGINAL OF OR EQUAL TO)
        0x22E5,         // MIRROR(SQUARE IMAGE OF OR NOT EQUAL TO)
        0x22E4,         // MIRROR(SQUARE ORIGINAL OF OR NOT EQUAL TO)
        0x22E7,         // MIRROR(LESS-THAN BUT NOT EQUIVALENT TO)
        0x22E6,         // MIRROR(GREATER-THAN BUT NOT EQUIVALENT TO)
        0x22E9,         // MIRROR(PRECEDES BUT NOT EQUIVALENT TO)
        0x22E8,         // MIRROR(SUCCEEDS BUT NOT EQUIVALENT TO)
        0x22EB,         // MIRROR(NOT NORMAL SUBGROUP OF)
        0x22EA,         // MIRROR(DOES NOT CONTAIN AS NORMAL SUBGROUP)
        0x22ED,         // MIRROR(NOT NORMAL SUBGROUP OF OR EQUAL TO)
        0x22EC,         // MIRROR(DOES NOT CONTAIN AS NORMAL SUBGROUP OR EQUAL)
        0x22F1,         // MIRROR(UP RIGHT DIAGONAL ELLIPSIS)
        0x22F0,         // MIRROR(DOWN RIGHT DIAGONAL ELLIPSIS)
        0x22FA,         // MIRROR(ELEMENT OF WITH LONG HORIZONTAL STROKE)
        0x22FB,         // MIRROR(ELEMENT OF WITH VERTICAL BAR AT END OF HORIZONTAL STROKE)
        0x22FC,         // MIRROR(SMALL ELEMENT OF WITH VERTICAL BAR AT END OF HORIZONTAL STROKE)
        0x22FD,         // MIRROR(ELEMENT OF WITH OVERBAR)
        0x22FE,         // MIRROR(SMALL ELEMENT OF WITH OVERBAR)
        0x22F2,         // MIRROR(CONTAINS WITH LONG HORIZONTAL STROKE)
        0x22F3,         // MIRROR(CONTAINS WITH VERTICAL BAR AT END OF HORIZONTAL STROKE)
        0x22F4,         // MIRROR(SMALL CONTAINS WITH VERTICAL BAR AT END OF HORIZONTAL STROKE)
        0x22F6,         // MIRROR(CONTAINS WITH OVERBAR)
        0x22F7,         // MIRROR(SMALL CONTAINS WITH OVERBAR)
        0x2309,         // MIRROR(LEFT CEILING)
        0x2308,         // MIRROR(RIGHT CEILING)
        0x230B,         // MIRROR(LEFT FLOOR)
        0x230A,         // MIRROR(RIGHT FLOOR)
        0x232A,         // MIRROR(LEFT-POINTING ANGLE BRACKET)
        0x2329,         // MIRROR(RIGHT-POINTING ANGLE BRACKET)
        0x2769,         // MIRROR(MEDIUM LEFT PARENTHESIS ORNAMENT)
        0x2768,         // MIRROR(MEDIUM RIGHT PARENTHESIS ORNAMENT)
        0x276B,         // MIRROR(MEDIUM FLATTENED LEFT PARENTHESIS ORNAMENT)
        0x276A,         // MIRROR(MEDIUM FLATTENED RIGHT PARENTHESIS ORNAMENT)
        0x276D,         // MIRROR(MEDIUM LEFT-POINTING ANGLE BRACKET ORNAMENT)
        0x276C,         // MIRROR(MEDIUM RIGHT-POINTING ANGLE BRACKET ORNAMENT)
        0x276F,         // MIRROR(HEAVY LEFT-POINTING ANGLE QUOTATION MARK ORNAMENT)
        0x276E,         // MIRROR(HEAVY RIGHT-POINTING ANGLE QUOTATION MARK ORNAMENT)
        0x2771,         // MIRROR(HEAVY LEFT-POINTING ANGLE BRACKET ORNAMENT)
        0x2770,         // MIRROR(HEAVY RIGHT-POINTING ANGLE BRACKET ORNAMENT)
        0x2773,         // MIRROR(LIGHT LEFT TORTOISE SHELL BRACKET)
        0x2772,         // MIRROR(LIGHT RIGHT TORTOISE SHELL BRACKET)
        0x2775,         // MIRROR(MEDIUM LEFT CURLY BRACKET ORNAMENT)
        0x2774,         // MIRROR(MEDIUM RIGHT CURLY BRACKET ORNAMENT)
        0x27C4,         // MIRROR(OPEN SUBSET)
        0x27C3,         // MIRROR(OPEN SUPERSET)
        0x27C6,         // MIRROR(LEFT S-SHAPED BAG DELIMITER)
        0x27C5,         // MIRROR(RIGHT S-SHAPED BAG DELIMITER)
        0x27C9,         // MIRROR(REVERSE SOLIDUS PRECEDING SUBSET)
        0x27C8,         // MIRROR(SUPERSET PRECEDING SOLIDUS)
        0x27D6,         // MIRROR(LEFT OUTER JOIN)
        0x27D5,         // MIRROR(RIGHT OUTER JOIN)
        0x27DE,         // MIRROR(LONG RIGHT TACK)
        0x27DD,         // MIRROR(LONG LEFT TACK)
        0x27E3,         // MIRROR(WHITE CONCAVE-SIDED DIAMOND WITH LEFTWARDS TICK)
        0x27E2,         // MIRROR(WHITE CONCAVE-SIDED DIAMOND WITH RIGHTWARDS TICK)
        0x27E5,         // MIRROR(WHITE SQUARE WITH LEFTWARDS TICK)
        0x27E4,         // MIRROR(WHITE SQUARE WITH RIGHTWARDS TICK)
        0x27E7,         // MIRROR(MATHEMATICAL LEFT WHITE SQUARE BRACKET)
        0x27E6,         // MIRROR(MATHEMATICAL RIGHT WHITE SQUARE BRACKET)
        0x27E9,         // MIRROR(MATHEMATICAL LEFT ANGLE BRACKET)
        0x27E8,         // MIRROR(MATHEMATICAL RIGHT ANGLE BRACKET)
        0x27EB,         // MIRROR(MATHEMATICAL LEFT DOUBLE ANGLE BRACKET)
        0x27EA,         // MIRROR(MATHEMATICAL RIGHT DOUBLE ANGLE BRACKET)
        0x27ED,         // MIRROR(MATHEMATICAL LEFT WHITE TORTOISE SHELL BRACKET)
        0x27EC,         // MIRROR(MATHEMATICAL RIGHT WHITE TORTOISE SHELL BRACKET)
        0x27EF,         // MIRROR(MATHEMATICAL LEFT FLATTENED PARENTHESIS)
        0x27EE,         // MIRROR(MATHEMATICAL RIGHT FLATTENED PARENTHESIS)
        0x2984,         // MIRROR(LEFT WHITE CURLY BRACKET)
        0x2983,         // MIRROR(RIGHT WHITE CURLY BRACKET)
        0x2986,         // MIRROR(LEFT WHITE PARENTHESIS)
        0x2985,         // MIRROR(RIGHT WHITE PARENTHESIS)
        0x2988,         // MIRROR(Z NOTATION LEFT IMAGE BRACKET)
        0x2987,         // MIRROR(Z NOTATION RIGHT IMAGE BRACKET)
        0x298A,         // MIRROR(Z NOTATION LEFT BINDING BRACKET)
        0x2989,         // MIRROR(Z NOTATION RIGHT BINDING BRACKET)
        0x298C,         // MIRROR(LEFT SQUARE BRACKET WITH UNDERBAR)
        0x298B,         // MIRROR(RIGHT SQUARE BRACKET WITH UNDERBAR)
        0x2990,         // MIRROR(LEFT SQUARE BRACKET WITH TICK IN TOP CORNER)
        0x298F,         // MIRROR(RIGHT SQUARE BRACKET WITH TICK IN BOTTOM CORNER)
        0x298E,         // MIRROR(LEFT SQUARE BRACKET WITH TICK IN BOTTOM CORNER)
        0x298D,         // MIRROR(RIGHT SQUARE BRACKET WITH TICK IN TOP CORNER)
        0x2992,         // MIRROR(LEFT ANGLE BRACKET WITH DOT)
        0x2991,         // MIRROR(RIGHT ANGLE BRACKET WITH DOT)
        0x2994,         // MIRROR(LEFT ARC LESS-THAN BRACKET)
        0x2993,         // MIRROR(RIGHT ARC GREATER-THAN BRACKET)
        0x2996,         // MIRROR(DOUBLE LEFT ARC GREATER-THAN BRACKET)
        0x2995,         // MIRROR(DOUBLE RIGHT ARC LESS-THAN BRACKET)
        0x2998,         // MIRROR(LEFT BLACK TORTOISE SHELL BRACKET)
        0x2997,         // MIRROR(RIGHT BLACK TORTOISE SHELL BRACKET)
        0x2298,         // MIRROR(CIRCLED REVERSE SOLIDUS)
        0x29C1,         // MIRROR(CIRCLED LESS-THAN)
        0x29C0,         // MIRROR(CIRCLED GREATER-THAN)
        0x29C5,         // MIRROR(SQUARED RISING DIAGONAL SLASH)
        0x29C4,         // MIRROR(SQUARED FALLING DIAGONAL SLASH)
        0x29D0,         // MIRROR(LEFT TRIANGLE BESIDE VERTICAL BAR)
        0x29CF,         // MIRROR(VERTICAL BAR BESIDE RIGHT TRIANGLE)
        0x29D2,         // MIRROR(BOWTIE WITH LEFT HALF BLACK)
        0x29D1,         // MIRROR(BOWTIE WITH RIGHT HALF BLACK)
        0x29D5,         // MIRROR(TIMES WITH LEFT HALF BLACK)
        0x29D4,         // MIRROR(TIMES WITH RIGHT HALF BLACK)
        0x29D9,         // MIRROR(LEFT WIGGLY FENCE)
        0x29D8,         // MIRROR(RIGHT WIGGLY FENCE)
        0x29DB,         // MIRROR(LEFT DOUBLE WIGGLY FENCE)
        0x29DA,         // MIRROR(RIGHT DOUBLE WIGGLY FENCE)
        0x2215,         // MIRROR(REVERSE SOLIDUS OPERATOR)
        0x29F9,         // MIRROR(BIG SOLIDUS)
        0x29F8,         // MIRROR(BIG REVERSE SOLIDUS)
        0x29FD,         // MIRROR(LEFT-POINTING CURVED ANGLE BRACKET)
        0x29FC,         // MIRROR(RIGHT-POINTING CURVED ANGLE BRACKET)
        0x2A2C,         // MIRROR(MINUS SIGN WITH FALLING DOTS)
        0x2A2B,         // MIRROR(MINUS SIGN WITH RISING DOTS)
        0x2A2E,         // MIRROR(PLUS SIGN IN LEFT HALF CIRCLE)
        0x2A2D,         // MIRROR(PLUS SIGN IN RIGHT HALF CIRCLE)
        0x2A35,         // MIRROR(MULTIPLICATION SIGN IN LEFT HALF CIRCLE)
        0x2A34,         // MIRROR(MULTIPLICATION SIGN IN RIGHT HALF CIRCLE)
        0x2A3D,         // MIRROR(INTERIOR PRODUCT)
        0x2A3C,         // MIRROR(RIGHTHAND INTERIOR PRODUCT)
        0x2A65,         // MIRROR(Z NOTATION DOMAIN ANTIRESTRICTION)
        0x2A64,         // MIRROR(Z NOTATION RANGE ANTIRESTRICTION)
        0x2A7A,         // MIRROR(LESS-THAN WITH CIRCLE INSIDE)
        0x2A79,         // MIRROR(GREATER-THAN WITH CIRCLE INSIDE)
        0x2A7E,         // MIRROR(LESS-THAN OR SLANTED EQUAL TO)
        0x2A7D,         // MIRROR(GREATER-THAN OR SLANTED EQUAL TO)
        0x2A80,         // MIRROR(LESS-THAN OR SLANTED EQUAL TO WITH DOT INSIDE)
        0x2A7F,         // MIRROR(GREATER-THAN OR SLANTED EQUAL TO WITH DOT INSIDE)
        0x2A82,         // MIRROR(LESS-THAN OR SLANTED EQUAL TO WITH DOT ABOVE)
        0x2A81,         // MIRROR(GREATER-THAN OR SLANTED EQUAL TO WITH DOT ABOVE)
        0x2A84,         // MIRROR(LESS-THAN OR SLANTED EQUAL TO WITH DOT ABOVE RIGHT)
        0x2A83,         // MIRROR(GREATER-THAN OR SLANTED EQUAL TO WITH DOT ABOVE LEFT)
        0x2A8C,         // MIRROR(LESS-THAN ABOVE DOUBLE-LINE EQUAL ABOVE GREATER-THAN)
        0x2A8B,         // MIRROR(GREATER-THAN ABOVE DOUBLE-LINE EQUAL ABOVE LESS-THAN)
        0x2A92,         // MIRROR(LESS-THAN ABOVE GREATER-THAN ABOVE DOUBLE-LINE EQUAL)
        0x2A91,         // MIRROR(GREATER-THAN ABOVE LESS-THAN ABOVE DOUBLE-LINE EQUAL)
        0x2A94,         // MIRROR(LESS-THAN ABOVE SLANTED EQUAL ABOVE GREATER-THAN ABOVE SLANTED EQUAL)
        0x2A93,         // MIRROR(GREATER-THAN ABOVE SLANTED EQUAL ABOVE LESS-THAN ABOVE SLANTED EQUAL)
        0x2A96,         // MIRROR(SLANTED EQUAL TO OR LESS-THAN)
        0x2A95,         // MIRROR(SLANTED EQUAL TO OR GREATER-THAN)
        0x2A98,         // MIRROR(SLANTED EQUAL TO OR LESS-THAN WITH DOT INSIDE)
        0x2A97,         // MIRROR(SLANTED EQUAL TO OR GREATER-THAN WITH DOT INSIDE)
        0x2A9A,         // MIRROR(DOUBLE-LINE EQUAL TO OR LESS-THAN)
        0x2A99,         // MIRROR(DOUBLE-LINE EQUAL TO OR GREATER-THAN)
        0x2A9C,         // MIRROR(DOUBLE-LINE SLANTED EQUAL TO OR LESS-THAN)
        0x2A9B,         // MIRROR(DOUBLE-LINE SLANTED EQUAL TO OR GREATER-THAN)
        0x2AA2,         // MIRROR(DOUBLE NESTED LESS-THAN)
        0x2AA1,         // MIRROR(DOUBLE NESTED GREATER-THAN)
        0x2AA7,         // MIRROR(LESS-THAN CLOSED BY CURVE)
        0x2AA6,         // MIRROR(GREATER-THAN CLOSED BY CURVE)
        0x2AA9,         // MIRROR(LESS-THAN CLOSED BY CURVE ABOVE SLANTED EQUAL)
        0x2AA8,         // MIRROR(GREATER-THAN CLOSED BY CURVE ABOVE SLANTED EQUAL)
        0x2AAB,         // MIRROR(SMALLER THAN)
        0x2AAA,         // MIRROR(LARGER THAN)
        0x2AAD,         // MIRROR(SMALLER THAN OR EQUAL TO)
        0x2AAC,         // MIRROR(LARGER THAN OR EQUAL TO)
        0x2AB0,         // MIRROR(PRECEDES ABOVE SINGLE-LINE EQUALS SIGN)
        0x2AAF,         // MIRROR(SUCCEEDS ABOVE SINGLE-LINE EQUALS SIGN)
        0x2AB4,         // MIRROR(PRECEDES ABOVE EQUALS SIGN)
        0x2AB3,         // MIRROR(SUCCEEDS ABOVE EQUALS SIGN)
        0x2ABC,         // MIRROR(DOUBLE PRECEDES)
        0x2ABB,         // MIRROR(DOUBLE SUCCEEDS)
        0x2ABE,         // MIRROR(SUBSET WITH DOT)
        0x2ABD,         // MIRROR(SUPERSET WITH DOT)
        0x2AC0,         // MIRROR(SUBSET WITH PLUS SIGN BELOW)
        0x2ABF,         // MIRROR(SUPERSET WITH PLUS SIGN BELOW)
        0x2AC2,         // MIRROR(SUBSET WITH MULTIPLICATION SIGN BELOW)
        0x2AC1,         // MIRROR(SUPERSET WITH MULTIPLICATION SIGN BELOW)
        0x2AC4,         // MIRROR(SUBSET OF OR EQUAL TO WITH DOT ABOVE)
        0x2AC3,         // MIRROR(SUPERSET OF OR EQUAL TO WITH DOT ABOVE)
        0x2AC6,         // MIRROR(SUBSET OF ABOVE EQUALS SIGN)
        0x2AC5,         // MIRROR(SUPERSET OF ABOVE EQUALS SIGN)
        0x2ACE,         // MIRROR(SQUARE LEFT OPEN BOX OPERATOR)
        0x2ACD,         // MIRROR(SQUARE RIGHT OPEN BOX OPERATOR)
        0x2AD0,         // MIRROR(CLOSED SUBSET)
        0x2ACF,         // MIRROR(CLOSED SUPERSET)
        0x2AD2,         // MIRROR(CLOSED SUBSET OR EQUAL TO)
        0x2AD1,         // MIRROR(CLOSED SUPERSET OR EQUAL TO)
        0x2AD4,         // MIRROR(SUBSET ABOVE SUPERSET)
        0x2AD3,         // MIRROR(SUPERSET ABOVE SUBSET)
        0x2AD6,         // MIRROR(SUBSET ABOVE SUBSET)
        0x2AD5,         // MIRROR(SUPERSET ABOVE SUPERSET)
        0x22A6,         // MIRROR(SHORT LEFT TACK)
        0x22A9,         // MIRROR(DOUBLE VERTICAL BAR LEFT TURNSTILE)
        0x22A8,         // MIRROR(VERTICAL BAR DOUBLE LEFT TURNSTILE)
        0x22AB,         // MIRROR(DOUBLE VERTICAL BAR DOUBLE LEFT TURNSTILE)
        0x2AED,         // MIRROR(DOUBLE STROKE NOT SIGN)
        0x2AEC,         // MIRROR(REVERSED DOUBLE STROKE NOT SIGN)
        0x2AF8,         // MIRROR(TRIPLE NESTED LESS-THAN)
        0x2AF7,         // MIRROR(TRIPLE NESTED GREATER-THAN)
        0x2AFA,         // MIRROR(DOUBLE-LINE SLANTED LESS-THAN OR EQUAL TO)
        0x2AF9,         // MIRROR(DOUBLE-LINE SLANTED GREATER-THAN OR EQUAL TO)
        0x2E03,         // MIRROR(LEFT SUBSTITUTION BRACKET)
        0x2E02,         // MIRROR(RIGHT SUBSTITUTION BRACKET)
        0x2E05,         // MIRROR(LEFT DOTTED SUBSTITUTION BRACKET)
        0x2E04,         // MIRROR(RIGHT DOTTED SUBSTITUTION BRACKET)
        0x2E0A,         // MIRROR(LEFT TRANSPOSITION BRACKET)
        0x2E09,         // MIRROR(RIGHT TRANSPOSITION BRACKET)
        0x2E0D,         // MIRROR(LEFT RAISED OMISSION BRACKET)
        0x2E0C,         // MIRROR(RIGHT RAISED OMISSION BRACKET)
        0x2E1D,         // MIRROR(LEFT LOW PARAPHRASE BRACKET)
        0x2E1C,         // MIRROR(RIGHT LOW PARAPHRASE BRACKET)
        0x2E21,         // MIRROR(LEFT VERTICAL BAR WITH QUILL)
        0x2E20,         // MIRROR(RIGHT VERTICAL BAR WITH QUILL)
        0x2E23,         // MIRROR(TOP LEFT HALF BRACKET)
        0x2E22,         // MIRROR(TOP RIGHT HALF BRACKET)
        0x2E25,         // MIRROR(BOTTOM LEFT HALF BRACKET)
        0x2E24,         // MIRROR(BOTTOM RIGHT HALF BRACKET)
        0x2E27,         // MIRROR(LEFT SIDEWAYS U BRACKET)
        0x2E26,         // MIRROR(RIGHT SIDEWAYS U BRACKET)
        0x2E29,         // MIRROR(LEFT DOUBLE PARENTHESIS)
        0x2E28,         // MIRROR(RIGHT DOUBLE PARENTHESIS)
        0x3009,         // MIRROR(LEFT ANGLE BRACKET)
        0x3008,         // MIRROR(RIGHT ANGLE BRACKET)
        0x300B,         // MIRROR(LEFT DOUBLE ANGLE BRACKET)
        0x300A,         // MIRROR(RIGHT DOUBLE ANGLE BRACKET)
        0x300D,         // MIRROR(LEFT CORNER BRACKET)
        0x300C,         // MIRROR(RIGHT CORNER BRACKET)
        0x300F,         // MIRROR(LEFT WHITE CORNER BRACKET)
        0x300E,         // MIRROR(RIGHT WHITE CORNER BRACKET)
        0x3011,         // MIRROR(LEFT BLACK LENTICULAR BRACKET)
        0x3010,         // MIRROR(RIGHT BLACK LENTICULAR BRACKET)
        0x3015,         // MIRROR(LEFT TORTOISE SHELL BRACKET)
        0x3014,         // MIRROR(RIGHT TORTOISE SHELL BRACKET)
        0x3017,         // MIRROR(LEFT WHITE LENTICULAR BRACKET)
        0x3016,         // MIRROR(RIGHT WHITE LENTICULAR BRACKET)
        0x3019,         // MIRROR(LEFT WHITE TORTOISE SHELL BRACKET)
        0x3018,         // MIRROR(RIGHT WHITE TORTOISE SHELL BRACKET)
        0x301B,         // MIRROR(LEFT WHITE SQUARE BRACKET)
        0x301A,         // MIRROR(RIGHT WHITE SQUARE BRACKET)
        0xFE5A,         // MIRROR(SMALL LEFT PARENTHESIS)
        0xFE59,         // MIRROR(SMALL RIGHT PARENTHESIS)
        0xFE5C,         // MIRROR(SMALL LEFT CURLY BRACKET)
        0xFE5B,         // MIRROR(SMALL RIGHT CURLY BRACKET)
        0xFE5E,         // MIRROR(SMALL LEFT TORTOISE SHELL BRACKET)
        0xFE5D,         // MIRROR(SMALL RIGHT TORTOISE SHELL BRACKET)
        0xFE65,         // MIRROR(SMALL LESS-THAN SIGN)
        0xFE64,         // MIRROR(SMALL GREATER-THAN SIGN)
        0xFF09,         // MIRROR(FULLWIDTH LEFT PARENTHESIS)
        0xFF08,         // MIRROR(FULLWIDTH RIGHT PARENTHESIS)
        0xFF1E,         // MIRROR(FULLWIDTH LESS-THAN SIGN)
        0xFF1C,         // MIRROR(FULLWIDTH GREATER-THAN SIGN)
        0xFF3D,         // MIRROR(FULLWIDTH LEFT SQUARE BRACKET)
        0xFF3B,         // MIRROR(FULLWIDTH RIGHT SQUARE BRACKET)
        0xFF5D,         // MIRROR(FULLWIDTH LEFT CURLY BRACKET)
        0xFF5B,         // MIRROR(FULLWIDTH RIGHT CURLY BRACKET)
        0xFF60,         // MIRROR(FULLWIDTH LEFT WHITE PARENTHESIS)
        0xFF5F,         // MIRROR(FULLWIDTH RIGHT WHITE PARENTHESIS)
        0xFF63,         // MIRROR(HALFWIDTH LEFT CORNER BRACKET)
        0xFF62,         // MIRROR(HALFWIDTH RIGHT CORNER BRACKET)
    };

    public static boolean hasMirror(int c) {
        return Arrays.binarySearch(mirKey, c) >= 0;
    }

    public static int toMirror(int c) {
        int k = Arrays.binarySearch(mirKey, c);
        if (k >= 0)
            return mirVal[k];
        else
            return c;
    }

}
