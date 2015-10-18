/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.util;

import java.util.Collections;
import java.util.Map;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.CharacterClass;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Characters {

    private static final String[][] characterClassSpecifications = new String[][] {
        { "openBrackets",       // JLREQ cl-1
          "[" +
          "\\u0028" +           // LEFT PARENTHESIS
          "\\u005B" +           // LEFT SQUARE BRACKET
          "\\u007B" +           // LEFT CURLY BRACKET
          "\\u00AB" +           // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
          "\\u2018" +           // LEFT SINGLE QUOTATION MARK
          "\\u201C" +           // LEFT DOUBLE QUOTATION MARK
          "\\u2985" +           // LEFT WHITE PARENTHESIS
          "\\u3008" +           // LEFT ANGLE BRACKET
          "\\u300A" +           // LEFT DOUBLE ANGLE BRACKET
          "\\u300C" +           // LEFT CORNER BRACKET
          "\\u300E" +           // LEFT WHITE CORNER BRACKET
          "\\u3010" +           // LEFT BLACK LENTICULAR BRACKET
          "\\u3014" +           // LEFT TORTOISE SHELL BRACKET
          "\\u3016" +           // LEFT WHITE LENTICULAR BRACKET
          "\\u3018" +           // LEFT WHITE TORTOISE SHELL BRACKET
          "\\u301D" +           // REVERSED DOUBLE PRIME QUOTATION MARK
          "]"
        },
        { "closeBrackets",      // JLREQ cl-2
          "[" +
          "\\u0029" +           // RIGHT PARENTHESIS
          "\\u005D" +           // RIGHT SQUARE BRACKET
          "\\u007D" +           // RIGHT CURLY BRACKET
          "\\u00BB" +           // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
          "\\u2019" +           // RIGHT SINGLE QUOTATION MARK
          "\\u201D" +           // RIGHT DOUBLE QUOTATION MARK
          "\\u2986" +           // RIGHT WHITE PARENTHESIS
          "\\u3009" +           // RIGHT ANGLE BRACKET
          "\\u300B" +           // RIGHT DOUBLE ANGLE BRACKET
          "\\u300D" +           // RIGHT CORNER BRACKET
          "\\u300F" +           // RIGHT WHITE CORNER BRACKET
          "\\u3011" +           // RIGHT BLACK LENTICULAR BRACKET
          "\\u3015" +           // RIGHT TORTOISE SHELL BRACKET
          "\\u3017" +           // RIGHT WHITE LENTICULAR BRACKET
          "\\u3019" +           // RIGHT WHITE TORTOISE SHELL BRACKET
          "\\u301F" +           // LOW DOUBLE PRIME QUOTATION MARK
          "]"
        },
        { "hyphens",            // JLREQ cl-3
          "[" +
          "\\u2010" +           // HYPHEN
          "\\u2013" +           // EN DASH
          "\\u301C" +           // WAVE DASH
          "\\u30A0" +           // KATAKANA-HIRAGANA DOUBLE HYPHEN
          "]"
        },
        { "punctuationMarks",   // JLREQ cl-4
          "[" +
          "\\u0021" +           // EXCLAMATION MARK
          "\\u003F" +           // QUESTION MARK
          "\\u203C" +           // DOUBLE EXCLAMATION MARK
          "\\u2047" +           // DOUBLE QUESTION MARK
          "\\u2048" +           // QUESTION EXCLAMATION MARK
          "\\u2049" +           // EXCLAMATION QUESTION MARK
          "]"
        },
        { "middleDots",         // JLREQ cl-5
          "[" +
          "\\u003A" +           // COLON
          "\\u003B" +           // SEMICOLON
          "\\u30FB" +           // KATAKANA MIDDLE DOT
          "]"
        },
        { "fullStops",          // JLREQ cl-6
          "[" +
          "\\u002E" +           // FULL STOP
          "\\u3002" +           // IDEOGRAPHIC FULL STOP
          "]"
        },
        { "commas",             // JLREQ cl-7
          "[" +
          "\\u002C" +           // COMMA
          "\\u3001" +           // IDEOGRAPHIC COMMA
          "]"
        },
        { "inseparables",       // JLREQ cl-8
          "[" +
          "\\u2014" +           // EM DASH
          "\\u2025" +           // TWO DOT LEADER
          "\\u2026" +           // HORIZONTAL ELLIPSIS
          "\\u3033" +           // VERTICAL KANA REPEAT MARK UPPER HALF
          "\\u3034" +           // VERTICAL KANA REPEAT WITH VOICED SOUND MARK UPPER HALF
          "\\u3035" +           // VERTICAL KANA REPEAT MARK LOWER HALF
          "]"
        },
        { "iterationMarks",     // JLREQ cl-9
          "[" +
          "\\u3005" +           // IDEOGRAPHIC ITERATION MARK
          "\\u303B" +           // VERTICAL IDEOGRAPHIC ITERATION MARK
          "\\u309D" +           // HIRAGANA ITERATION MARK
          "\\u309E" +           // HIRAGANA VOICED ITERATION MARK
          "\\u30FD" +           // KATAKANA ITERATION MARK
          "\\u30FE" +           // KATAKANA VOICED ITERATION MARK
          "]"
        },
        { "soundMarks",         // JLREQ cl-10
          "[" +
          "\\u30FC" +           // KATAKANA-HIRAGANA PROLONGED SOUND MARK
          "]"
        },
        { "smallKana",          // JLREQ cl-11
          "[" +
          "\\u3041" +           // HIRAGANA LETTER SMALL A
          "\\u3043" +           // HIRAGANA LETTER SMALL I
          "\\u3045" +           // HIRAGANA LETTER SMALL U
          "\\u3047" +           // HIRAGANA LETTER SMALL E
          "\\u3049" +           // HIRAGANA LETTER SMALL O
          "\\u3063" +           // HIRAGANA LETTER SMALL TU
          "\\u3083" +           // HIRAGANA LETTER SMALL YA
          "\\u3085" +           // HIRAGANA LETTER SMALL YU
          "\\u3087" +           // HIRAGANA LETTER SMALL YO
          "\\u308E" +           // HIRAGANA LETTER SMALL WA
          "\\u3095" +           // HIRAGANA LETTER SMALL KA
          "\\u3096" +           // HIRAGANA LETTER SMALL KE
          "\\u30A1" +           // KATAKANA LETTER SMALL A
          "\\u30A3" +           // KATAKANA LETTER SMALL I
          "\\u30A5" +           // KATAKANA LETTER SMALL U
          "\\u30A7" +           // KATAKANA LETTER SMALL E
          "\\u30A9" +           // KATAKANA LETTER SMALL O
          "\\u30C3" +           // KATAKANA LETTER SMALL TU
          "\\u30E3" +           // KATAKANA LETTER SMALL YA
          "\\u30E5" +           // KATAKANA LETTER SMALL YU
          "\\u30E7" +           // KATAKANA LETTER SMALL YO
          "\\u30EE" +           // KATAKANA LETTER SMALL WA
          "\\u30F5" +           // KATAKANA LETTER SMALL KA
          "\\u30F6" +           // KATAKANA LETTER SMALL KE
          "\\u31F0" +           // KATAKANA LETTER SMALL KU
          "\\u31F1" +           // KATAKANA LETTER SMALL SI
          "\\u31F2" +           // KATAKANA LETTER SMALL SU
          "\\u31F3" +           // KATAKANA LETTER SMALL TO
          "\\u31F4" +           // KATAKANA LETTER SMALL NU
          "\\u31F5" +           // KATAKANA LETTER SMALL HA
          "\\u31F6" +           // KATAKANA LETTER SMALL HI
          "\\u31F7" +           // KATAKANA LETTER SMALL HU
          "\\u31F8" +           // KATAKANA LETTER SMALL HE
          "\\u31F9" +           // KATAKANA LETTER SMALL HO
          "\\u31FA" +           // KATAKANA LETTER SMALL MU
          "\\u31FB" +           // KATAKANA LETTER SMALL RA
          "\\u31FC" +           // KATAKANA LETTER SMALL RI
          "\\u31FD" +           // KATAKANA LETTER SMALL RU
          "\\u31FE" +           // KATAKANA LETTER SMALL RE
          "\\u31FF" +           // KATAKANA LETTER SMALL RO
          "]"
        },
        { "preAbbreviations",   // JLREQ cl-12
          "[" +
          "\\u0023" +           // NUMBER SIGN
          "\\u0024" +           // DOLLAR SIGN
          "\\u00A3" +           // POUND SIGN
          "\\u00A5" +           // YEN SIGN
          "\\u20AC" +           // EURO SIGN
          "\\u2116" +           // NUMERO SIGN
          "]"
        },
        { "postAbbreviations",  // JLREQ cl-13
          "[" +
          "\\u0025" +          // PERCENT SIGN
          "\\u00A2" +          // CENT SIGN
          "\\u00B0" +          // DEGREE SIGN
          "\\u2030" +          // PER MILLE SIGN
          "\\u2032" +          // PRIME
          "\\u2033" +          // DOUBLE PRIME
          "\\u2103" +          // DEGREE CELSIUS
          "\\u2113" +          // SCRIPT SMALL L
          "\\u3303" +          // SQUARE AARU
          "\\u330D" +          // SQUARE KARORII
          "\\u3314" +          // SQUARE KIRO
          "\\u3318" +          // SQUARE GURAMU
          "\\u3322" +          // SQUARE SENTI
          "\\u3323" +          // SQUARE SENTO
          "\\u3326" +          // SQUARE DORU
          "\\u3327" +          // SQUARE TON
          "\\u332B" +          // SQUARE PAASENTO
          "\\u3336" +          // SQUARE HEKUTAARU
          "\\u333B" +          // SQUARE PEEZI
          "\\u3349" +          // SQUARE MIRI
          "\\u334A" +          // SQUARE MIRIBAARU
          "\\u334D" +          // SQUARE MEETORU
          "\\u3351" +          // SQUARE RITTORU
          "\\u3357" +          // SQUARE WATTO
          "\\u338E" +          // SQUARE MG
          "\\u338F" +          // SQUARE KG
          "\\u339C" +          // SQUARE MM
          "\\u339D" +          // SQUARE CM
          "\\u339E" +          // SQUARE KM
          "\\u33A1" +          // SQUARE M SQUARED
          "\\u33C4" +          // SQUARE CC
          "\\u33CB" +          // SQUARE HP
          "]"
        },
        { "ideographicSpace",   // JLREQ cl-14
          "[" +
           "\\u3000" +          // IDEOGRAPHIC SPACE
          "]"
        },
        { "hiragana",           // JLREQ cl-15
          "[" +
          "\\u3042" +           // HIRAGANA LETTER A
          "\\u3044" +           // HIRAGANA LETTER I
          "\\u3046" +           // HIRAGANA LETTER U
          "\\u3048" +           // HIRAGANA LETTER E
          "\\u304A" +           // HIRAGANA LETTER O
          "\\u304B" +           // HIRAGANA LETTER KA
          "\\u304C" +           // HIRAGANA LETTER GA
          "\\u304D" +           // HIRAGANA LETTER KI
          "\\u304E" +           // HIRAGANA LETTER GI
          "\\u304F" +           // HIRAGANA LETTER KU
          "\\u3050" +           // HIRAGANA LETTER GU
          "\\u3051" +           // HIRAGANA LETTER KE
          "\\u3052" +           // HIRAGANA LETTER GE
          "\\u3053" +           // HIRAGANA LETTER KO
          "\\u3054" +           // HIRAGANA LETTER GO
          "\\u3055" +           // HIRAGANA LETTER SA
          "\\u3056" +           // HIRAGANA LETTER ZA
          "\\u3057" +           // HIRAGANA LETTER SI
          "\\u3058" +           // HIRAGANA LETTER ZI
          "\\u3059" +           // HIRAGANA LETTER SU
          "\\u305A" +           // HIRAGANA LETTER ZU
          "\\u305B" +           // HIRAGANA LETTER SE
          "\\u305C" +           // HIRAGANA LETTER ZE
          "\\u305D" +           // HIRAGANA LETTER SO
          "\\u305E" +           // HIRAGANA LETTER ZO
          "\\u305F" +           // HIRAGANA LETTER TA
          "\\u3060" +           // HIRAGANA LETTER DA
          "\\u3061" +           // HIRAGANA LETTER TI
          "\\u3062" +           // HIRAGANA LETTER DI
          "\\u3064" +           // HIRAGANA LETTER TU
          "\\u3065" +           // HIRAGANA LETTER DU
          "\\u3066" +           // HIRAGANA LETTER TE
          "\\u3067" +           // HIRAGANA LETTER DE
          "\\u3068" +           // HIRAGANA LETTER TO
          "\\u3069" +           // HIRAGANA LETTER DO
          "\\u306A" +           // HIRAGANA LETTER NA
          "\\u306B" +           // HIRAGANA LETTER NI
          "\\u306C" +           // HIRAGANA LETTER NU
          "\\u306D" +           // HIRAGANA LETTER NE
          "\\u306E" +           // HIRAGANA LETTER NO
          "\\u306F" +           // HIRAGANA LETTER HA
          "\\u3070" +           // HIRAGANA LETTER BA
          "\\u3071" +           // HIRAGANA LETTER PA
          "\\u3072" +           // HIRAGANA LETTER HI
          "\\u3073" +           // HIRAGANA LETTER BI
          "\\u3074" +           // HIRAGANA LETTER PI
          "\\u3075" +           // HIRAGANA LETTER HU
          "\\u3076" +           // HIRAGANA LETTER BU
          "\\u3077" +           // HIRAGANA LETTER PU
          "\\u3078" +           // HIRAGANA LETTER HE
          "\\u3079" +           // HIRAGANA LETTER BE
          "\\u307A" +           // HIRAGANA LETTER PE
          "\\u307B" +           // HIRAGANA LETTER HO
          "\\u307C" +           // HIRAGANA LETTER BO
          "\\u307D" +           // HIRAGANA LETTER PO
          "\\u307E" +           // HIRAGANA LETTER MA
          "\\u307F" +           // HIRAGANA LETTER MI
          "\\u3080" +           // HIRAGANA LETTER MU
          "\\u3081" +           // HIRAGANA LETTER ME
          "\\u3082" +           // HIRAGANA LETTER MO
          "\\u3084" +           // HIRAGANA LETTER YA
          "\\u3086" +           // HIRAGANA LETTER YU
          "\\u3088" +           // HIRAGANA LETTER YO
          "\\u3089" +           // HIRAGANA LETTER RA
          "\\u308A" +           // HIRAGANA LETTER RI
          "\\u308B" +           // HIRAGANA LETTER RU
          "\\u308C" +           // HIRAGANA LETTER RE
          "\\u308D" +           // HIRAGANA LETTER RO
          "\\u308F" +           // HIRAGANA LETTER WA
          "\\u3090" +           // HIRAGANA LETTER WI
          "\\u3091" +           // HIRAGANA LETTER WE
          "\\u3092" +           // HIRAGANA LETTER WO
          "\\u3093" +           // HIRAGANA LETTER N
          "\\u3094" +           // HIRAGANA LETTER VU
          "]"
        },
        { "katakana",           // JLREQ cl-16
          "[" +
          "\\u30A2" +           // KATAKANA LETTER A
          "\\u30A4" +           // KATAKANA LETTER I
          "\\u30A6" +           // KATAKANA LETTER U
          "\\u30A8" +           // KATAKANA LETTER E
          "\\u30AA" +           // KATAKANA LETTER O
          "\\u30AB" +           // KATAKANA LETTER KA
          "\\u30AC" +           // KATAKANA LETTER GA
          "\\u30AD" +           // KATAKANA LETTER KI
          "\\u30AE" +           // KATAKANA LETTER GI
          "\\u30AF" +           // KATAKANA LETTER KU
          "\\u30B0" +           // KATAKANA LETTER GU
          "\\u30B1" +           // KATAKANA LETTER KE
          "\\u30B2" +           // KATAKANA LETTER GE
          "\\u30B3" +           // KATAKANA LETTER KO
          "\\u30B4" +           // KATAKANA LETTER GO
          "\\u30B5" +           // KATAKANA LETTER SA
          "\\u30B6" +           // KATAKANA LETTER ZA
          "\\u30B7" +           // KATAKANA LETTER SI
          "\\u30B8" +           // KATAKANA LETTER ZI
          "\\u30B9" +           // KATAKANA LETTER SU
          "\\u30BA" +           // KATAKANA LETTER ZU
          "\\u30BB" +           // KATAKANA LETTER SE
          "\\u30BC" +           // KATAKANA LETTER ZE
          "\\u30BD" +           // KATAKANA LETTER SO
          "\\u30BE" +           // KATAKANA LETTER ZO
          "\\u30BF" +           // KATAKANA LETTER TA
          "\\u30C0" +           // KATAKANA LETTER DA
          "\\u30C1" +           // KATAKANA LETTER TI
          "\\u30C2" +           // KATAKANA LETTER DI
          "\\u30C4" +           // KATAKANA LETTER TU
          "\\u30C5" +           // KATAKANA LETTER DU
          "\\u30C6" +           // KATAKANA LETTER TE
          "\\u30C7" +           // KATAKANA LETTER DE
          "\\u30C8" +           // KATAKANA LETTER TO
          "\\u30C9" +           // KATAKANA LETTER DO
          "\\u30CA" +           // KATAKANA LETTER NA
          "\\u30CB" +           // KATAKANA LETTER NI
          "\\u30CC" +           // KATAKANA LETTER NU
          "\\u30CD" +           // KATAKANA LETTER NE
          "\\u30CE" +           // KATAKANA LETTER NO
          "\\u30CF" +           // KATAKANA LETTER HA
          "\\u30D0" +           // KATAKANA LETTER BA
          "\\u30D1" +           // KATAKANA LETTER PA
          "\\u30D2" +           // KATAKANA LETTER HI
          "\\u30D3" +           // KATAKANA LETTER BI
          "\\u30D4" +           // KATAKANA LETTER PI
          "\\u30D5" +           // KATAKANA LETTER HU
          "\\u30D6" +           // KATAKANA LETTER BU
          "\\u30D7" +           // KATAKANA LETTER PU
          "\\u30D8" +           // KATAKANA LETTER HE
          "\\u30D9" +           // KATAKANA LETTER BE
          "\\u30DA" +           // KATAKANA LETTER PE
          "\\u30DB" +           // KATAKANA LETTER HO
          "\\u30DC" +           // KATAKANA LETTER BO
          "\\u30DD" +           // KATAKANA LETTER PO
          "\\u30DE" +           // KATAKANA LETTER MA
          "\\u30DF" +           // KATAKANA LETTER MI
          "\\u30E0" +           // KATAKANA LETTER MU
          "\\u30E1" +           // KATAKANA LETTER ME
          "\\u30E2" +           // KATAKANA LETTER MO
          "\\u30E4" +           // KATAKANA LETTER YA
          "\\u30E6" +           // KATAKANA LETTER YU
          "\\u30E8" +           // KATAKANA LETTER YO
          "\\u30E9" +           // KATAKANA LETTER RA
          "\\u30EA" +           // KATAKANA LETTER RI
          "\\u30EB" +           // KATAKANA LETTER RU
          "\\u30EC" +           // KATAKANA LETTER RE
          "\\u30ED" +           // KATAKANA LETTER RO
          "\\u30EF" +           // KATAKANA LETTER WA
          "\\u30F0" +           // KATAKANA LETTER WI
          "\\u30F1" +           // KATAKANA LETTER WE
          "\\u30F2" +           // KATAKANA LETTER WO
          "\\u30F3" +           // KATAKANA LETTER N
          "\\u30F4" +           // KATAKANA LETTER VU
          "]"
        },
        { "mathSymbols",        // JLREQ cl-17
          "[" +
          "\\u003C" +           // LESS-THAN SIGN
          "\\u003D" +           // EQUALS SIGN
          "\\u003E" +           // GREATER-THAN SIGN
          "\\u2194" +           // LEFT RIGHT ARROW
          "\\u21D2" +           // RIGHTWARDS DOUBLE ARROW
          "\\u21D4" +           // LEFT RIGHT DOUBLE ARROW
          "\\u2208" +           // ELEMENT OF
          "\\u2209" +           // NOT AN ELEMENT OF
          "\\u220B" +           // CONTAINS AS MEMBER
          "\\u221D" +           // PROPORTIONAL TO
          "\\u2225" +           // PARALLEL TO
          "\\u2226" +           // NOT PARALLEL TO
          "\\u2227" +           // LOGICAL AND
          "\\u2228" +           // LOGICAL OR
          "\\u2229" +           // INTERSECTION
          "\\u222A" +           // UNION
          "\\u223D" +           // REVERSED TILDE (lazy S)
          "\\u2243" +           // ASYMPTOTICALLY EQUAL TO
          "\\u2245" +           // APPROXIMATELY EQUAL TO
          "\\u2248" +           // ALMOST EQUAL TO
          "\\u2252" +           // APPROXIMATELY EQUAL TO OR THE IMAGE OF
          "\\u2260" +           // NOT EQUAL TO
          "\\u2261" +           // IDENTICAL TO
          "\\u2262" +           // NOT IDENTICAL TO
          "\\u2266" +           // LESS-THAN OVER EQUAL TO
          "\\u2267" +           // GREATER-THAN OVER EQUAL TO
          "\\u226A" +           // MUCH LESS-THAN
          "\\u226B" +           // MUCH GREATER-THAN
          "\\u2276" +           // LESS-THAN OR GREATER-THAN
          "\\u2277" +           // GREATER-THAN OR LESS-THAN
          "\\u2282" +           // SUBSET OF
          "\\u2283" +           // SUPERSET OF
          "\\u2284" +           // NOT A SUBSET OF
          "\\u2285" +           // NOT A SUPERSET OF
          "\\u2286" +           // SUBSET OF OR EQUAL TO
          "\\u2287" +           // SUPERSET OF OR EQUAL TO
          "\\u228A" +           // SUBSET OF WITH NOT EQUAL TO
          "\\u228B" +           // SUPERSET OF WITH NOT EQUAL TO
          "\\u2295" +           // CIRCLED PLUS
          "\\u2297" +           // CIRCLED TIMES
          "\\u22A5" +           // UP TACK
          "\\u22DA" +           // LESS-THAN EQUAL TO OR GREATER-THAN
          "\\u22DB" +           // GREATER-THAN EQUAL TO OR LESS-THAN
          "\\u2305" +           // PROJECTIVE
          "\\u2306" +           // PERSPECTIVE
          "]"
        },
        { "mathOperators",      // JLREQ cl-18
          "[" +
          "\\u002B" +           // PLUS SIGN
          "\\u00B1" +           // PLUS-MINUS SIGN
          "\\u00D7" +           // MULTIPLICATION SIGN
          "\\u00F7" +           // DIVISION SIGN
          "\\u2212" +           // MINUS SIGN
          "\\u2213" +           // MINUS-OR-PLUS SIGN
          "]"
        },
        { "ideographic",        // JLREQ cl-19
          "[" +
          "\\u0026" +           // AMPERSAND
          "\\u002A" +           // ASTERISK
          "\\u002F" +           // SOLIDUS
          "\\u0030" +           // DIGIT ZERO
          "\\u0031" +           // DIGIT ONE
          "\\u0032" +           // DIGIT TWO
          "\\u0033" +           // DIGIT THREE
          "\\u0034" +           // DIGIT FOUR
          "\\u0035" +           // DIGIT FIVE
          "\\u0036" +           // DIGIT SIX
          "\\u0037" +           // DIGIT SEVEN
          "\\u0038" +           // DIGIT EIGHT
          "\\u0039" +           // DIGIT NINE
          "\\u0040" +           // COMMERCIAL AT
          "\\u0041" +           // LATIN CAPITAL LETTER A
          "\\u0042" +           // LATIN CAPITAL LETTER B
          "\\u0043" +           // LATIN CAPITAL LETTER C
          "\\u0044" +           // LATIN CAPITAL LETTER D
          "\\u0045" +           // LATIN CAPITAL LETTER E
          "\\u0046" +           // LATIN CAPITAL LETTER F
          "\\u0047" +           // LATIN CAPITAL LETTER G
          "\\u0048" +           // LATIN CAPITAL LETTER H
          "\\u0049" +           // LATIN CAPITAL LETTER I
          "\\u004A" +           // LATIN CAPITAL LETTER J
          "\\u004B" +           // LATIN CAPITAL LETTER K
          "\\u004C" +           // LATIN CAPITAL LETTER L
          "\\u004D" +           // LATIN CAPITAL LETTER M
          "\\u004E" +           // LATIN CAPITAL LETTER N
          "\\u004F" +           // LATIN CAPITAL LETTER O
          "\\u0050" +           // LATIN CAPITAL LETTER P
          "\\u0051" +           // LATIN CAPITAL LETTER Q
          "\\u0052" +           // LATIN CAPITAL LETTER R
          "\\u0053" +           // LATIN CAPITAL LETTER S
          "\\u0054" +           // LATIN CAPITAL LETTER T
          "\\u0055" +           // LATIN CAPITAL LETTER U
          "\\u0056" +           // LATIN CAPITAL LETTER V
          "\\u0057" +           // LATIN CAPITAL LETTER W
          "\\u0058" +           // LATIN CAPITAL LETTER X
          "\\u0059" +           // LATIN CAPITAL LETTER Y
          "\\u005A" +           // LATIN CAPITAL LETTER Z
          "\\u005C" +           // REVERSE SOLIDUS
          "\\u0061" +           // LATIN SMALL LETTER A
          "\\u0062" +           // LATIN SMALL LETTER B
          "\\u0063" +           // LATIN SMALL LETTER C
          "\\u0064" +           // LATIN SMALL LETTER D
          "\\u0065" +           // LATIN SMALL LETTER E
          "\\u0066" +           // LATIN SMALL LETTER F
          "\\u0067" +           // LATIN SMALL LETTER G
          "\\u0068" +           // LATIN SMALL LETTER H
          "\\u0069" +           // LATIN SMALL LETTER I
          "\\u006A" +           // LATIN SMALL LETTER J
          "\\u006B" +           // LATIN SMALL LETTER K
          "\\u006C" +           // LATIN SMALL LETTER L
          "\\u006D" +           // LATIN SMALL LETTER M
          "\\u006E" +           // LATIN SMALL LETTER N
          "\\u006F" +           // LATIN SMALL LETTER O
          "\\u0070" +           // LATIN SMALL LETTER P
          "\\u0071" +           // LATIN SMALL LETTER Q
          "\\u0072" +           // LATIN SMALL LETTER R
          "\\u0073" +           // LATIN SMALL LETTER S
          "\\u0074" +           // LATIN SMALL LETTER T
          "\\u0075" +           // LATIN SMALL LETTER U
          "\\u0076" +           // LATIN SMALL LETTER V
          "\\u0077" +           // LATIN SMALL LETTER W
          "\\u0078" +           // LATIN SMALL LETTER X
          "\\u0079" +           // LATIN SMALL LETTER Y
          "\\u007A" +           // LATIN SMALL LETTER Z
          "\\u007C" +           // VERTICAL LINE
          "\\u00A7" +           // SECTION SIGN
          "\\u00A9" +           // COPYRIGHT SIGN
          "\\u00AE" +           // REGISTERED SIGN
          "\\u00B6" +           // PILCROW SIGN
          "\\u00BC" +           // VULGAR FRACTION ONE QUARTER
          "\\u00BD" +           // VULGAR FRACTION ONE HALF
          "\\u00BE" +           // VULGAR FRACTION THREE QUARTERS
          "\\u0391" +           // GREEK CAPITAL LETTER ALPHA
          "\\u0392" +           // GREEK CAPITAL LETTER BETA
          "\\u0393" +           // GREEK CAPITAL LETTER GAMMA
          "\\u0394" +           // GREEK CAPITAL LETTER DELTA
          "\\u0395" +           // GREEK CAPITAL LETTER EPSILON
          "\\u0396" +           // GREEK CAPITAL LETTER ZETA
          "\\u0397" +           // GREEK CAPITAL LETTER ETA
          "\\u0398" +           // GREEK CAPITAL LETTER THETA
          "\\u0399" +           // GREEK CAPITAL LETTER IOTA
          "\\u039A" +           // GREEK CAPITAL LETTER KAPPA
          "\\u039B" +           // GREEK CAPITAL LETTER LAMDA
          "\\u039C" +           // GREEK CAPITAL LETTER MU
          "\\u039D" +           // GREEK CAPITAL LETTER NU
          "\\u039E" +           // GREEK CAPITAL LETTER XI
          "\\u039F" +           // GREEK CAPITAL LETTER OMICRON
          "\\u03A0" +           // GREEK CAPITAL LETTER PI
          "\\u03A1" +           // GREEK CAPITAL LETTER RHO
          "\\u03A3" +           // GREEK CAPITAL LETTER SIGMA
          "\\u03A4" +           // GREEK CAPITAL LETTER TAU
          "\\u03A5" +           // GREEK CAPITAL LETTER UPSILON
          "\\u03A6" +           // GREEK CAPITAL LETTER PHI
          "\\u03A7" +           // GREEK CAPITAL LETTER CHI
          "\\u03A8" +           // GREEK CAPITAL LETTER PSI
          "\\u03A9" +           // GREEK CAPITAL LETTER OMEGA
          "\\u03B1" +           // GREEK SMALL LETTER ALPHA
          "\\u03B2" +           // GREEK SMALL LETTER BETA
          "\\u03B3" +           // GREEK SMALL LETTER GAMMA
          "\\u03B4" +           // GREEK SMALL LETTER DELTA
          "\\u03B5" +           // GREEK SMALL LETTER EPSILON
          "\\u03B6" +           // GREEK SMALL LETTER ZETA
          "\\u03B7" +           // GREEK SMALL LETTER ETA
          "\\u03B8" +           // GREEK SMALL LETTER THETA
          "\\u03B9" +           // GREEK SMALL LETTER IOTA
          "\\u03BA" +           // GREEK SMALL LETTER KAPPA
          "\\u03BB" +           // GREEK SMALL LETTER LAMDA
          "\\u03BC" +           // GREEK SMALL LETTER MU
          "\\u03BD" +           // GREEK SMALL LETTER NU
          "\\u03BE" +           // GREEK SMALL LETTER XI
          "\\u03BF" +           // GREEK SMALL LETTER OMICRON
          "\\u03C0" +           // GREEK SMALL LETTER PI
          "\\u03C1" +           // GREEK SMALL LETTER RHO
          "\\u03C2" +           // GREEK SMALL LETTER FINAL SIGMA
          "\\u03C3" +           // GREEK SMALL LETTER SIGMA
          "\\u03C4" +           // GREEK SMALL LETTER TAU
          "\\u03C5" +           // GREEK SMALL LETTER UPSILON
          "\\u03C6" +           // GREEK SMALL LETTER PHI
          "\\u03C7" +           // GREEK SMALL LETTER CHI
          "\\u03C8" +           // GREEK SMALL LETTER PSI
          "\\u03C9" +           // GREEK SMALL LETTER OMEGA
          "\\u0401" +           // CYRILLIC CAPITAL LETTER IO
          "\\u0410" +           // CYRILLIC CAPITAL LETTER A
          "\\u0411" +           // CYRILLIC CAPITAL LETTER BE
          "\\u0412" +           // CYRILLIC CAPITAL LETTER VE
          "\\u0413" +           // CYRILLIC CAPITAL LETTER GHE
          "\\u0414" +           // CYRILLIC CAPITAL LETTER DE
          "\\u0415" +           // CYRILLIC CAPITAL LETTER IE
          "\\u0416" +           // CYRILLIC CAPITAL LETTER ZHE
          "\\u0417" +           // CYRILLIC CAPITAL LETTER ZE
          "\\u0418" +           // CYRILLIC CAPITAL LETTER I
          "\\u0419" +           // CYRILLIC CAPITAL LETTER SHORT I
          "\\u041A" +           // CYRILLIC CAPITAL LETTER KA
          "\\u041B" +           // CYRILLIC CAPITAL LETTER EL
          "\\u041C" +           // CYRILLIC CAPITAL LETTER EM
          "\\u041D" +           // CYRILLIC CAPITAL LETTER EN
          "\\u041E" +           // CYRILLIC CAPITAL LETTER O
          "\\u041F" +           // CYRILLIC CAPITAL LETTER PE
          "\\u0420" +           // CYRILLIC CAPITAL LETTER ER
          "\\u0421" +           // CYRILLIC CAPITAL LETTER ES
          "\\u0422" +           // CYRILLIC CAPITAL LETTER TE
          "\\u0423" +           // CYRILLIC CAPITAL LETTER U
          "\\u0424" +           // CYRILLIC CAPITAL LETTER EF
          "\\u0425" +           // CYRILLIC CAPITAL LETTER HA
          "\\u0426" +           // CYRILLIC CAPITAL LETTER TSE
          "\\u0427" +           // CYRILLIC CAPITAL LETTER CHE
          "\\u0428" +           // CYRILLIC CAPITAL LETTER SHA
          "\\u0429" +           // CYRILLIC CAPITAL LETTER SHCHA
          "\\u042A" +           // CYRILLIC CAPITAL LETTER HARD SIGN
          "\\u042B" +           // CYRILLIC CAPITAL LETTER YERU
          "\\u042C" +           // CYRILLIC CAPITAL LETTER SOFT SIGN
          "\\u042D" +           // CYRILLIC CAPITAL LETTER E
          "\\u042E" +           // CYRILLIC CAPITAL LETTER YU
          "\\u042F" +           // CYRILLIC CAPITAL LETTER YA
          "\\u0430" +           // CYRILLIC SMALL LETTER A
          "\\u0431" +           // CYRILLIC SMALL LETTER BE
          "\\u0432" +           // CYRILLIC SMALL LETTER VE
          "\\u0433" +           // CYRILLIC SMALL LETTER GHE
          "\\u0434" +           // CYRILLIC SMALL LETTER DE
          "\\u0435" +           // CYRILLIC SMALL LETTER IE
          "\\u0436" +           // CYRILLIC SMALL LETTER ZHE
          "\\u0437" +           // CYRILLIC SMALL LETTER ZE
          "\\u0438" +           // CYRILLIC SMALL LETTER I
          "\\u0439" +           // CYRILLIC SMALL LETTER SHORT I
          "\\u043A" +           // CYRILLIC SMALL LETTER KA
          "\\u043B" +           // CYRILLIC SMALL LETTER EL
          "\\u043C" +           // CYRILLIC SMALL LETTER EM
          "\\u043D" +           // CYRILLIC SMALL LETTER EN
          "\\u043E" +           // CYRILLIC SMALL LETTER O
          "\\u043F" +           // CYRILLIC SMALL LETTER PE
          "\\u0440" +           // CYRILLIC SMALL LETTER ER
          "\\u0441" +           // CYRILLIC SMALL LETTER ES
          "\\u0442" +           // CYRILLIC SMALL LETTER TE
          "\\u0443" +           // CYRILLIC SMALL LETTER U
          "\\u0444" +           // CYRILLIC SMALL LETTER EF
          "\\u0445" +           // CYRILLIC SMALL LETTER HA
          "\\u0446" +           // CYRILLIC SMALL LETTER TSE
          "\\u0447" +           // CYRILLIC SMALL LETTER CHE
          "\\u0448" +           // CYRILLIC SMALL LETTER SHA
          "\\u0449" +           // CYRILLIC SMALL LETTER SHCHA
          "\\u044A" +           // CYRILLIC SMALL LETTER HARD SIGN
          "\\u044B" +           // CYRILLIC SMALL LETTER YERU
          "\\u044C" +           // CYRILLIC SMALL LETTER SOFT SIGN
          "\\u044D" +           // CYRILLIC SMALL LETTER E
          "\\u044E" +           // CYRILLIC SMALL LETTER YU
          "\\u044F" +           // CYRILLIC SMALL LETTER YA
          "\\u0451" +           // CYRILLIC SMALL LETTER IO
          "\\u2016" +           // DOUBLE VERTICAL LINE
          "\\u2020" +           // DAGGER
          "\\u2021" +           // DOUBLE DAGGER
          "\\u2022" +           // BULLET
          "\\u203B" +           // REFERENCE MARK
          "\\u2042" +           // ASTERISM
          "\\u2051" +           // TWO ASTERISKS ALIGNED VERTICALLY
          "\\u2121" +           // TELEPHONE SIGN
          "\\u2153" +           // VULGAR FRACTION ONE THIRD
          "\\u2154" +           // VULGAR FRACTION TWO THIRDS
          "\\u2155" +           // VULGAR FRACTION ONE FIFTH
          "\\u2160" +           // ROMAN NUMERAL ONE
          "\\u2161" +           // ROMAN NUMERAL TWO
          "\\u2162" +           // ROMAN NUMERAL THREE
          "\\u2163" +           // ROMAN NUMERAL FOUR
          "\\u2164" +           // ROMAN NUMERAL FIVE
          "\\u2165" +           // ROMAN NUMERAL SIX
          "\\u2166" +           // ROMAN NUMERAL SEVEN
          "\\u2167" +           // ROMAN NUMERAL EIGHT
          "\\u2168" +           // ROMAN NUMERAL NINE
          "\\u2169" +           // ROMAN NUMERAL TEN
          "\\u216A" +           // ROMAN NUMERAL ELEVEN
          "\\u216B" +           // ROMAN NUMERAL TWELVE
          "\\u2170" +           // SMALL ROMAN NUMERAL ONE
          "\\u2171" +           // SMALL ROMAN NUMERAL TWO
          "\\u2172" +           // SMALL ROMAN NUMERAL THREE
          "\\u2173" +           // SMALL ROMAN NUMERAL FOUR
          "\\u2174" +           // SMALL ROMAN NUMERAL FIVE
          "\\u2175" +           // SMALL ROMAN NUMERAL SIX
          "\\u2176" +           // SMALL ROMAN NUMERAL SEVEN
          "\\u2177" +           // SMALL ROMAN NUMERAL EIGHT
          "\\u2178" +           // SMALL ROMAN NUMERAL NINE
          "\\u2179" +           // SMALL ROMAN NUMERAL TEN
          "\\u217A" +           // SMALL ROMAN NUMERAL ELEVEN
          "\\u217B" +           // SMALL ROMAN NUMERAL TWELVE
          "\\u2190" +           // LEFTWARDS ARROW
          "\\u2191" +           // UPWARDS ARROW
          "\\u2192" +           // RIGHTWARDS ARROW
          "\\u2193" +           // DOWNWARDS ARROW
          "\\u2194" +           // LEFT RIGHT ARROW
          "\\u2196" +           // NORTH WEST ARROW
          "\\u2197" +           // NORTH EAST ARROW
          "\\u2198" +           // SOUTH EAST ARROW
          "\\u2199" +           // SOUTH WEST ARROW
          "\\u21C4" +           // RIGHTWARDS ARROW OVER LEFTWARDS ARROW
          "\\u21E6" +           // LEFTWARDS WHITE ARROW
          "\\u21E7" +           // UPWARDS WHITE ARROW
          "\\u21E8" +           // RIGHTWARDS WHITE ARROW
          "\\u21E9" +           // DOWNWARDS WHITE ARROW
          "\\u221A" +           // SQUARE ROOT
          "\\u221E" +           // INFINITY
          "\\u221F" +           // RIGHT ANGLE
          "\\u222B" +           // INTEGRAL
          "\\u222C" +           // DOUBLE INTEGRAL
          "\\u2234" +           // THEREFORE
          "\\u2235" +           // BECAUSE
          "\\u2296" +           // CIRCLED MINUS
          "\\u22BF" +           // RIGHT TRIANGLE
          "\\u2318" +           // PLACE OF INTEREST SIGN
          "\\u23CE" +           // RETURN SYMBOL
          "\\u2423" +           // OPEN BOX
          "\\u2460" +           // CIRCLED DIGIT ONE
          "\\u2461" +           // CIRCLED DIGIT TWO
          "\\u2462" +           // CIRCLED DIGIT THREE
          "\\u2463" +           // CIRCLED DIGIT FOUR
          "\\u2464" +           // CIRCLED DIGIT FIVE
          "\\u2465" +           // CIRCLED DIGIT SIX
          "\\u2466" +           // CIRCLED DIGIT SEVEN
          "\\u2467" +           // CIRCLED DIGIT EIGHT
          "\\u2468" +           // CIRCLED DIGIT NINE
          "\\u2469" +           // CIRCLED NUMBER TEN
          "\\u246A" +           // CIRCLED NUMBER ELEVEN
          "\\u246B" +           // CIRCLED NUMBER TWELVE
          "\\u246C" +           // CIRCLED NUMBER THIRTEEN
          "\\u246D" +           // CIRCLED NUMBER FOURTEEN
          "\\u246E" +           // CIRCLED NUMBER FIFTEEN
          "\\u246F" +           // CIRCLED NUMBER SIXTEEN
          "\\u2470" +           // CIRCLED NUMBER SEVENTEEN
          "\\u2471" +           // CIRCLED NUMBER EIGHTEEN
          "\\u2472" +           // CIRCLED NUMBER NINETEEN
          "\\u2473" +           // CIRCLED NUMBER TWENTY
          "\\u24D0" +           // CIRCLED LATIN SMALL LETTER A
          "\\u24D1" +           // CIRCLED LATIN SMALL LETTER B
          "\\u24D2" +           // CIRCLED LATIN SMALL LETTER C
          "\\u24D3" +           // CIRCLED LATIN SMALL LETTER D
          "\\u24D4" +           // CIRCLED LATIN SMALL LETTER E
          "\\u24D5" +           // CIRCLED LATIN SMALL LETTER F
          "\\u24D6" +           // CIRCLED LATIN SMALL LETTER G
          "\\u24D7" +           // CIRCLED LATIN SMALL LETTER H
          "\\u24D8" +           // CIRCLED LATIN SMALL LETTER I
          "\\u24D9" +           // CIRCLED LATIN SMALL LETTER J
          "\\u24DA" +           // CIRCLED LATIN SMALL LETTER K
          "\\u24DB" +           // CIRCLED LATIN SMALL LETTER L
          "\\u24DC" +           // CIRCLED LATIN SMALL LETTER M
          "\\u24DD" +           // CIRCLED LATIN SMALL LETTER N
          "\\u24DE" +           // CIRCLED LATIN SMALL LETTER O
          "\\u24DF" +           // CIRCLED LATIN SMALL LETTER P
          "\\u24E0" +           // CIRCLED LATIN SMALL LETTER Q
          "\\u24E1" +           // CIRCLED LATIN SMALL LETTER R
          "\\u24E2" +           // CIRCLED LATIN SMALL LETTER S
          "\\u24E3" +           // CIRCLED LATIN SMALL LETTER T
          "\\u24E4" +           // CIRCLED LATIN SMALL LETTER U
          "\\u24E5" +           // CIRCLED LATIN SMALL LETTER V
          "\\u24E6" +           // CIRCLED LATIN SMALL LETTER W
          "\\u24E7" +           // CIRCLED LATIN SMALL LETTER X
          "\\u24E8" +           // CIRCLED LATIN SMALL LETTER Y
          "\\u24E9" +           // CIRCLED LATIN SMALL LETTER Z
          "\\u24EB" +           // NEGATIVE CIRCLED NUMBER ELEVEN
          "\\u24EC" +           // NEGATIVE CIRCLED NUMBER TWELVE
          "\\u24ED" +           // NEGATIVE CIRCLED NUMBER THIRTEEN
          "\\u24EE" +           // NEGATIVE CIRCLED NUMBER FOURTEEN
          "\\u24EF" +           // NEGATIVE CIRCLED NUMBER FIFTEEN
          "\\u24F0" +           // NEGATIVE CIRCLED NUMBER SIXTEEN
          "\\u24F1" +           // NEGATIVE CIRCLED NUMBER SEVENTEEN
          "\\u24F2" +           // NEGATIVE CIRCLED NUMBER EIGHTEEN
          "\\u24F3" +           // NEGATIVE CIRCLED NUMBER NINETEEN
          "\\u24F4" +           // NEGATIVE CIRCLED NUMBER TWENTY
          "\\u24F5" +           // DOUBLE CIRCLED DIGIT ONE
          "\\u24F6" +           // DOUBLE CIRCLED DIGIT TWO
          "\\u24F7" +           // DOUBLE CIRCLED DIGIT THREE
          "\\u24F8" +           // DOUBLE CIRCLED DIGIT FOUR
          "\\u24F9" +           // DOUBLE CIRCLED DIGIT FIVE
          "\\u24FA" +           // DOUBLE CIRCLED DIGIT SIX
          "\\u24FB" +           // DOUBLE CIRCLED DIGIT SEVEN
          "\\u24FC" +           // DOUBLE CIRCLED DIGIT EIGHT
          "\\u24FD" +           // DOUBLE CIRCLED DIGIT NINE
          "\\u24FE" +           // DOUBLE CIRCLED NUMBER TEN
          "\\u25A0" +           // BLACK SQUARE
          "\\u25A1" +           // WHITE SQUARE
          "\\u25B1" +           // WHITE PARALLELOGRAM
          "\\u25B2" +           // BLACK UP-POINTING TRIANGLE
          "\\u25B3" +           // WHITE UP-POINTING TRIANGLE
          "\\u25B6" +           // BLACK RIGHT-POINTING TRIANGLE
          "\\u25B7" +           // WHITE RIGHT-POINTING TRIANGLE
          "\\u25BC" +           // BLACK DOWN-POINTING TRIANGLE
          "\\u25BD" +           // WHITE DOWN-POINTING TRIANGLE
          "\\u25C0" +           // BLACK LEFT-POINTING TRIANGLE
          "\\u25C1" +           // WHITE LEFT-POINTING TRIANGLE
          "\\u25C6" +           // BLACK DIAMOND
          "\\u25C7" +           // WHITE DIAMOND
          "\\u25C9" +           // FISHEYE
          "\\u25CB" +           // WHITE CIRCLE
          "\\u25CE" +           // BULLSEYE
          "\\u25CF" +           // BLACK CIRCLE
          "\\u25D0" +           // CIRCLE WITH LEFT HALF BLACK
          "\\u25D1" +           // CIRCLE WITH RIGHT HALF BLACK
          "\\u25D2" +           // CIRCLE WITH LOWER HALF BLACK
          "\\u25D3" +           // CIRCLE WITH UPPER HALF BLACK
          "\\u25E6" +           // WHITE BULLET
          "\\u25EF" +           // LARGE CIRCLE
          "\\u2600" +           // BLACK SUN WITH RAYS
          "\\u2601" +           // CLOUD
          "\\u2602" +           // UMBRELLA
          "\\u2603" +           // SNOWMAN
          "\\u2605" +           // BLACK STAR
          "\\u2606" +           // WHITE STAR
          "\\u260E" +           // BLACK TELEPHONE
          "\\u2616" +           // WHITE SHOGI PIECE
          "\\u2617" +           // BLACK SHOGI PIECE
          "\\u261E" +           // WHITE RIGHT POINTING INDEX
          "\\u2640" +           // FEMALE SIGN
          "\\u2642" +           // MALE SIGN
          "\\u2660" +           // BLACK SPADE SUIT
          "\\u2661" +           // WHITE HEART SUIT
          "\\u2662" +           // WHITE DIAMOND SUIT
          "\\u2663" +           // BLACK CLUB SUIT
          "\\u2664" +           // WHITE SPADE SUIT
          "\\u2665" +           // BLACK HEART SUIT
          "\\u2666" +           // BLACK DIAMOND SUIT
          "\\u2667" +           // WHITE CLUB SUIT
          "\\u2668" +           // HOT SPRINGS
          "\\u2669" +           // QUARTER NOTE
          "\\u266A" +           // EIGHTH NOTE
          "\\u266B" +           // BEAMED EIGHTH NOTES
          "\\u266C" +           // BEAMED SIXTEENTH NOTES
          "\\u266D" +           // MUSIC FLAT SIGN
          "\\u266E" +           // MUSIC NATURAL SIGN
          "\\u266F" +           // MUSIC SHARP SIGN
          "\\u2713" +           // CHECK MARK
          "\\u2756" +           // BLACK DIAMOND MINUS WHITE X
          "\\u2776" +           // DINGBAT NEGATIVE CIRCLED DIGIT ONE
          "\\u2777" +           // DINGBAT NEGATIVE CIRCLED DIGIT TWO
          "\\u2778" +           // DINGBAT NEGATIVE CIRCLED DIGIT THREE
          "\\u2779" +           // DINGBAT NEGATIVE CIRCLED DIGIT FOUR
          "\\u277A" +           // DINGBAT NEGATIVE CIRCLED DIGIT FIVE
          "\\u277B" +           // DINGBAT NEGATIVE CIRCLED DIGIT SIX
          "\\u277C" +           // DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
          "\\u277D" +           // DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
          "\\u277E" +           // DINGBAT NEGATIVE CIRCLED DIGIT NINE
          "\\u277F" +           // DINGBAT NEGATIVE CIRCLED NUMBER TEN
          "\\u2934" +           // ARROW POINTING RIGHTWARDS THEN CURVING UPWARDS
          "\\u2935" +           // ARROW POINTING RIGHTWARDS THEN CURVING DOWNWARDS
          "\\u29BF" +           // CIRCLED BULLET
          "\\u29FA" +           // DOUBLE PLUS
          "\\u29FB" +           // TRIPLE PLUS
          "\\u3003" +           // DITTO MARK
          "\\u3006" +           // IDEOGRAPHIC CLOSING MARK
          "\\u3007" +           // IDEOGRAPHIC NUMBER ZERO
          "\\u3012" +           // POSTAL MARK
          "\\u3013" +           // GETA MARK
          "\\u3020" +           // POSTAL MARK FACE
          "\\u303C" +           // MASU MARK
          "\\u303D" +           // PART ALTERNATION MARK
          "\\u309F" +           // HIRAGANA DIGRAPH YORI
          "\\u30FF" +           // KATAKANA DIGRAPH KOTO
          "\\u3231" +           // PARENTHESIZED IDEOGRAPH STOCK
          "\\u3232" +           // PARENTHESIZED IDEOGRAPH HAVE
          "\\u3239" +           // PARENTHESIZED IDEOGRAPH REPRESENT
          "\\u3251" +           // CIRCLED NUMBER TWENTY ONE
          "\\u3252" +           // CIRCLED NUMBER TWENTY TWO
          "\\u3253" +           // CIRCLED NUMBER TWENTY THREE
          "\\u3254" +           // CIRCLED NUMBER TWENTY FOUR
          "\\u3255" +           // CIRCLED NUMBER TWENTY FIVE
          "\\u3256" +           // CIRCLED NUMBER TWENTY SIX
          "\\u3257" +           // CIRCLED NUMBER TWENTY SEVEN
          "\\u3258" +           // CIRCLED NUMBER TWENTY EIGHT
          "\\u3259" +           // CIRCLED NUMBER TWENTY NINE
          "\\u325A" +           // CIRCLED NUMBER THIRTY
          "\\u325B" +           // CIRCLED NUMBER THIRTY ONE
          "\\u325C" +           // CIRCLED NUMBER THIRTY TWO
          "\\u325D" +           // CIRCLED NUMBER THIRTY THREE
          "\\u325E" +           // CIRCLED NUMBER THIRTY FOUR
          "\\u325F" +           // CIRCLED NUMBER THIRTY FIVE
          "\\u32A4" +           // CIRCLED IDEOGRAPH HIGH
          "\\u32A5" +           // CIRCLED IDEOGRAPH CENTRE
          "\\u32A6" +           // CIRCLED IDEOGRAPH LOW
          "\\u32A7" +           // CIRCLED IDEOGRAPH LEFT
          "\\u32A8" +           // CIRCLED IDEOGRAPH RIGHT
          "\\u32B1" +           // CIRCLED NUMBER THIRTY SIX
          "\\u32B2" +           // CIRCLED NUMBER THIRTY SEVEN
          "\\u32B3" +           // CIRCLED NUMBER THIRTY EIGHT
          "\\u32B4" +           // CIRCLED NUMBER THIRTY NINE
          "\\u32B5" +           // CIRCLED NUMBER FORTY
          "\\u32B6" +           // CIRCLED NUMBER FORTY ONE
          "\\u32B7" +           // CIRCLED NUMBER FORTY TWO
          "\\u32B8" +           // CIRCLED NUMBER FORTY THREE
          "\\u32B9" +           // CIRCLED NUMBER FORTY FOUR
          "\\u32BA" +           // CIRCLED NUMBER FORTY FIVE
          "\\u32BB" +           // CIRCLED NUMBER FORTY SIX
          "\\u32BC" +           // CIRCLED NUMBER FORTY SEVEN
          "\\u32BD" +           // CIRCLED NUMBER FORTY EIGHT
          "\\u32BE" +           // CIRCLED NUMBER FORTY NINE
          "\\u32BF" +           // CIRCLED NUMBER FIFTY
          "\\u32D0" +           // CIRCLED KATAKANA A
          "\\u32D1" +           // CIRCLED KATAKANA I
          "\\u32D2" +           // CIRCLED KATAKANA U
          "\\u32D3" +           // CIRCLED KATAKANA E
          "\\u32D4" +           // CIRCLED KATAKANA O
          "\\u32D5" +           // CIRCLED KATAKANA KA
          "\\u32D6" +           // CIRCLED KATAKANA KI
          "\\u32D7" +           // CIRCLED KATAKANA KU
          "\\u32D8" +           // CIRCLED KATAKANA KE
          "\\u32D9" +           // CIRCLED KATAKANA KO
          "\\u32DA" +           // CIRCLED KATAKANA SA
          "\\u32DB" +           // CIRCLED KATAKANA SI
          "\\u32DC" +           // CIRCLED KATAKANA SU
          "\\u32DD" +           // CIRCLED KATAKANA SE
          "\\u32DE" +           // CIRCLED KATAKANA SO
          "\\u32DF" +           // CIRCLED KATAKANA TA
          "\\u32E0" +           // CIRCLED KATAKANA TI
          "\\u32E1" +           // CIRCLED KATAKANA TU
          "\\u32E2" +           // CIRCLED KATAKANA TE
          "\\u32E3" +           // CIRCLED KATAKANA TO
          "\\u32E5" +           // CIRCLED KATAKANA NI
          "\\u32E9" +           // CIRCLED KATAKANA HA
          "\\u32EC" +           // CIRCLED KATAKANA HE
          "\\u32ED" +           // CIRCLED KATAKANA HO
          "\\u32FA" +           // CIRCLED KATAKANA RO
          "\\u337B" +           // SQUARE ERA NAME HEISEI
          "\\u337C" +           // SQUARE ERA NAME SYOUWA
          "\\u337D" +           // SQUARE ERA NAME TAISYOU
          "\\u337E" +           // SQUARE ERA NAME MEIZI
          "\\u33CD" +           // SQUARE KK
          "\\u4EDD" +           // CJK UNIFIED IDEOGRAPH-4EDD
          "]"
        },
        { "groupedNumerals",    // JLREQ cl-24
          "[" +
          "\\u0020" +           // SPACE
          "\\u002C" +           // COMMA
          "\\u002E" +           // FULL STOP
          "\\u0030" +           // DIGIT ZERO
          "\\u0031" +           // DIGIT ONE
          "\\u0032" +           // DIGIT TWO
          "\\u0033" +           // DIGIT THREE
          "\\u0034" +           // DIGIT FOUR
          "\\u0035" +           // DIGIT FIVE
          "\\u0036" +           // DIGIT SIX
          "\\u0037" +           // DIGIT SEVEN
          "\\u0038" +           // DIGIT EIGHT
          "\\u0039" +           // DIGIT NINE
          "]"
        },
        { "unitSymbols",        // JLREQ cl-25
          "[" +
          "\\u0020" +           // SPACE
          "\\u0028" +           // LEFT PARENTHESIS
          "\\u0029" +           // RIGHT PARENTHESIS
          "\\u002F" +           // SOLIDUS
          "\\u0031" +           // DIGIT ONE
          "\\u0032" +           // DIGIT TWO
          "\\u0033" +           // DIGIT THREE
          "\\u0034" +           // DIGIT FOUR
          "\\u0041" +           // LATIN CAPITAL LETTER A
          "\\u0042" +           // LATIN CAPITAL LETTER B
          "\\u0043" +           // LATIN CAPITAL LETTER C
          "\\u0044" +           // LATIN CAPITAL LETTER D
          "\\u0045" +           // LATIN CAPITAL LETTER E
          "\\u0046" +           // LATIN CAPITAL LETTER F
          "\\u0047" +           // LATIN CAPITAL LETTER G
          "\\u0048" +           // LATIN CAPITAL LETTER H
          "\\u0049" +           // LATIN CAPITAL LETTER I
          "\\u004A" +           // LATIN CAPITAL LETTER J
          "\\u004B" +           // LATIN CAPITAL LETTER K
          "\\u004C" +           // LATIN CAPITAL LETTER L
          "\\u004D" +           // LATIN CAPITAL LETTER M
          "\\u004E" +           // LATIN CAPITAL LETTER N
          "\\u004F" +           // LATIN CAPITAL LETTER O
          "\\u0050" +           // LATIN CAPITAL LETTER P
          "\\u0051" +           // LATIN CAPITAL LETTER Q
          "\\u0052" +           // LATIN CAPITAL LETTER R
          "\\u0053" +           // LATIN CAPITAL LETTER S
          "\\u0054" +           // LATIN CAPITAL LETTER T
          "\\u0055" +           // LATIN CAPITAL LETTER U
          "\\u0056" +           // LATIN CAPITAL LETTER V
          "\\u0057" +           // LATIN CAPITAL LETTER W
          "\\u0058" +           // LATIN CAPITAL LETTER X
          "\\u0059" +           // LATIN CAPITAL LETTER Y
          "\\u005A" +           // LATIN CAPITAL LETTER Z
          "\\u0061" +           // LATIN SMALL LETTER A
          "\\u0062" +           // LATIN SMALL LETTER B
          "\\u0063" +           // LATIN SMALL LETTER C
          "\\u0064" +           // LATIN SMALL LETTER D
          "\\u0065" +           // LATIN SMALL LETTER E
          "\\u0066" +           // LATIN SMALL LETTER F
          "\\u0067" +           // LATIN SMALL LETTER G
          "\\u0068" +           // LATIN SMALL LETTER H
          "\\u0069" +           // LATIN SMALL LETTER I
          "\\u006A" +           // LATIN SMALL LETTER J
          "\\u006B" +           // LATIN SMALL LETTER K
          "\\u006C" +           // LATIN SMALL LETTER L
          "\\u006D" +           // LATIN SMALL LETTER M
          "\\u006E" +           // LATIN SMALL LETTER N
          "\\u006F" +           // LATIN SMALL LETTER O
          "\\u0070" +           // LATIN SMALL LETTER P
          "\\u0071" +           // LATIN SMALL LETTER Q
          "\\u0072" +           // LATIN SMALL LETTER R
          "\\u0073" +           // LATIN SMALL LETTER S
          "\\u0074" +           // LATIN SMALL LETTER T
          "\\u0075" +           // LATIN SMALL LETTER U
          "\\u0076" +           // LATIN SMALL LETTER V
          "\\u0077" +           // LATIN SMALL LETTER W
          "\\u0078" +           // LATIN SMALL LETTER X
          "\\u0079" +           // LATIN SMALL LETTER Y
          "\\u007A" +           // LATIN SMALL LETTER Z
          "\\u03A9" +           // GREEK CAPITAL LETTER OMEGA
          "\\u03BC" +           // GREEK SMALL LETTER MU
          "\\u2127" +           // INVERTED OHM SIGN
          "\\u212B" +           // ANGSTROM SIGN
          "\\u2212" +           // MINUS SIGN
          "\\u30FB" +           // KATAKANA MIDDLE DOT
          "]"
        },
        { "westernWordSpace",   // JLREQ cl-26
          "[" +
          "\\u0020" +           // SPACE
          "]"
        },
        { "western",            // JLREQ cl-27
          "[" +
          "\\u0021" +           // EXCLAMATION MARK
          "\\u0022" +           // QUOTATION MARK
          "\\u0023" +           // NUMBER SIGN
          "\\u0024" +           // DOLLAR SIGN
          "\\u0025" +           // PERCENT SIGN
          "\\u0026" +           // AMPERSAND
          "\\u0027" +           // APOSTROPHE
          "\\u0028" +           // LEFT PARENTHESIS
          "\\u0029" +           // RIGHT PARENTHESIS
          "\\u002A" +           // ASTERISK
          "\\u002B" +           // PLUS SIGN
          "\\u002C" +           // COMMA
          "\\u002D" +           // HYPHEN-MINUS
          "\\u002E" +           // FULL STOP
          "\\u002F" +           // SOLIDUS
          "\\u0030" +           // DIGIT ZERO
          "\\u0031" +           // DIGIT ONE
          "\\u0032" +           // DIGIT TWO
          "\\u0033" +           // DIGIT THREE
          "\\u0034" +           // DIGIT FOUR
          "\\u0035" +           // DIGIT FIVE
          "\\u0036" +           // DIGIT SIX
          "\\u0037" +           // DIGIT SEVEN
          "\\u0038" +           // DIGIT EIGHT
          "\\u0039" +           // DIGIT NINE
          "\\u003A" +           // COLON
          "\\u003B" +           // SEMICOLON
          "\\u003C" +           // LESS-THAN SIGN
          "\\u003D" +           // EQUALS SIGN
          "\\u003E" +           // GREATER-THAN SIGN
          "\\u003F" +           // QUESTION MARK
          "\\u0040" +           // COMMERCIAL AT
          "\\u0041" +           // LATIN CAPITAL LETTER A
          "\\u0042" +           // LATIN CAPITAL LETTER B
          "\\u0043" +           // LATIN CAPITAL LETTER C
          "\\u0044" +           // LATIN CAPITAL LETTER D
          "\\u0045" +           // LATIN CAPITAL LETTER E
          "\\u0046" +           // LATIN CAPITAL LETTER F
          "\\u0047" +           // LATIN CAPITAL LETTER G
          "\\u0048" +           // LATIN CAPITAL LETTER H
          "\\u0049" +           // LATIN CAPITAL LETTER I
          "\\u004A" +           // LATIN CAPITAL LETTER J
          "\\u004B" +           // LATIN CAPITAL LETTER K
          "\\u004C" +           // LATIN CAPITAL LETTER L
          "\\u004D" +           // LATIN CAPITAL LETTER M
          "\\u004E" +           // LATIN CAPITAL LETTER N
          "\\u004F" +           // LATIN CAPITAL LETTER O
          "\\u0050" +           // LATIN CAPITAL LETTER P
          "\\u0051" +           // LATIN CAPITAL LETTER Q
          "\\u0052" +           // LATIN CAPITAL LETTER R
          "\\u0053" +           // LATIN CAPITAL LETTER S
          "\\u0054" +           // LATIN CAPITAL LETTER T
          "\\u0055" +           // LATIN CAPITAL LETTER U
          "\\u0056" +           // LATIN CAPITAL LETTER V
          "\\u0057" +           // LATIN CAPITAL LETTER W
          "\\u0058" +           // LATIN CAPITAL LETTER X
          "\\u0059" +           // LATIN CAPITAL LETTER Y
          "\\u005A" +           // LATIN CAPITAL LETTER Z
          "\\u005B" +           // LEFT SQUARE BRACKET
          "\\u005C" +           // REVERSE SOLIDUS
          "\\u005D" +           // RIGHT SQUARE BRACKET
          "\\u005E" +           // CIRCUMFLEX ACCENT
          "\\u005F" +           // LOW LINE
          "\\u0060" +           // GRAVE ACCENT
          "\\u0061" +           // LATIN SMALL LETTER A
          "\\u0062" +           // LATIN SMALL LETTER B
          "\\u0063" +           // LATIN SMALL LETTER C
          "\\u0064" +           // LATIN SMALL LETTER D
          "\\u0065" +           // LATIN SMALL LETTER E
          "\\u0066" +           // LATIN SMALL LETTER F
          "\\u0067" +           // LATIN SMALL LETTER G
          "\\u0068" +           // LATIN SMALL LETTER H
          "\\u0069" +           // LATIN SMALL LETTER I
          "\\u006A" +           // LATIN SMALL LETTER J
          "\\u006B" +           // LATIN SMALL LETTER K
          "\\u006C" +           // LATIN SMALL LETTER L
          "\\u006D" +           // LATIN SMALL LETTER M
          "\\u006E" +           // LATIN SMALL LETTER N
          "\\u006F" +           // LATIN SMALL LETTER O
          "\\u0070" +           // LATIN SMALL LETTER P
          "\\u0071" +           // LATIN SMALL LETTER Q
          "\\u0072" +           // LATIN SMALL LETTER R
          "\\u0073" +           // LATIN SMALL LETTER S
          "\\u0074" +           // LATIN SMALL LETTER T
          "\\u0075" +           // LATIN SMALL LETTER U
          "\\u0076" +           // LATIN SMALL LETTER V
          "\\u0077" +           // LATIN SMALL LETTER W
          "\\u0078" +           // LATIN SMALL LETTER X
          "\\u0079" +           // LATIN SMALL LETTER Y
          "\\u007A" +           // LATIN SMALL LETTER Z
          "\\u007B" +           // LEFT CURLY BRACKET
          "\\u007C" +           // VERTICAL LINE
          "\\u007D" +           // RIGHT CURLY BRACKET
          "\\u007E" +           // TILDE
          "\\u00A0" +           // NO-BREAK SPACE
          "\\u00A1" +           // INVERTED EXCLAMATION MARK
          "\\u00A2" +           // CENT SIGN
          "\\u00A3" +           // POUND SIGN
          "\\u00A4" +           // CURRENCY SIGN
          "\\u00A5" +           // YEN SIGN
          "\\u00A6" +           // BROKEN BAR
          "\\u00A7" +           // SECTION SIGN
          "\\u00A8" +           // DIAERESIS
          "\\u00A9" +           // COPYRIGHT SIGN
          "\\u00AA" +           // FEMININE ORDINAL INDICATOR
          "\\u00AB" +           // LEFT-POINTING DOUBLE ANGLE QUOTATION MARK
          "\\u00AC" +           // NOT SIGN
          "\\u00AD" +           // SOFT HYPHEN
          "\\u00AE" +           // REGISTERED SIGN
          "\\u00AF" +           // MACRON
          "\\u00B0" +           // DEGREE SIGN
          "\\u00B1" +           // PLUS-MINUS SIGN
          "\\u00B2" +           // SUPERSCRIPT TWO
          "\\u00B3" +           // SUPERSCRIPT THREE
          "\\u00B4" +           // ACUTE ACCENT
          "\\u00B6" +           // PILCROW SIGN
          "\\u00B7" +           // MIDDLE DOT
          "\\u00B8" +           // CEDILLA
          "\\u00B9" +           // SUPERSCRIPT ONE
          "\\u00BA" +           // MASCULINE ORDINAL INDICATOR
          "\\u00BB" +           // RIGHT-POINTING DOUBLE ANGLE QUOTATION MARK
          "\\u00BC" +           // VULGAR FRACTION ONE QUARTER
          "\\u00BD" +           // VULGAR FRACTION ONE HALF
          "\\u00BE" +           // VULGAR FRACTION THREE QUARTERS
          "\\u00BF" +           // INVERTED QUESTION MARK
          "\\u00C0" +           // LATIN CAPITAL LETTER A WITH GRAVE
          "\\u00C1" +           // LATIN CAPITAL LETTER A WITH ACUTE
          "\\u00C2" +           // LATIN CAPITAL LETTER A WITH CIRCUMFLEX
          "\\u00C3" +           // LATIN CAPITAL LETTER A WITH TILDE
          "\\u00C4" +           // LATIN CAPITAL LETTER A WITH DIAERESIS
          "\\u00C5" +           // LATIN CAPITAL LETTER A WITH RING ABOVE
          "\\u00C6" +           // LATIN CAPITAL LETTER AE (ash)
          "\\u00C7" +           // LATIN CAPITAL LETTER C WITH CEDILLA
          "\\u00C8" +           // LATIN CAPITAL LETTER E WITH GRAVE
          "\\u00C9" +           // LATIN CAPITAL LETTER E WITH ACUTE
          "\\u00CA" +           // LATIN CAPITAL LETTER E WITH CIRCUMFLEX
          "\\u00CB" +           // LATIN CAPITAL LETTER E WITH DIAERESIS
          "\\u00CC" +           // LATIN CAPITAL LETTER I WITH GRAVE
          "\\u00CD" +           // LATIN CAPITAL LETTER I WITH ACUTE
          "\\u00CE" +           // LATIN CAPITAL LETTER I WITH CIRCUMFLEX
          "\\u00CF" +           // LATIN CAPITAL LETTER I WITH DIAERESIS
          "\\u00D0" +           // LATIN CAPITAL LETTER ETH (Icelandic)
          "\\u00D1" +           // LATIN CAPITAL LETTER N WITH TILDE
          "\\u00D2" +           // LATIN CAPITAL LETTER O WITH GRAVE
          "\\u00D3" +           // LATIN CAPITAL LETTER O WITH ACUTE
          "\\u00D4" +           // LATIN CAPITAL LETTER O WITH CIRCUMFLEX
          "\\u00D5" +           // LATIN CAPITAL LETTER O WITH TILDE
          "\\u00D6" +           // LATIN CAPITAL LETTER O WITH DIAERESIS
          "\\u00D7" +           // MULTIPLICATION SIGN
          "\\u00D8" +           // LATIN CAPITAL LETTER O WITH STROKE
          "\\u00D9" +           // LATIN CAPITAL LETTER U WITH GRAVE
          "\\u00DA" +           // LATIN CAPITAL LETTER U WITH ACUTE
          "\\u00DB" +           // LATIN CAPITAL LETTER U WITH CIRCUMFLEX
          "\\u00DC" +           // LATIN CAPITAL LETTER U WITH DIAERESIS
          "\\u00DD" +           // LATIN CAPITAL LETTER Y WITH ACUTE
          "\\u00DE" +           // LATIN CAPITAL LETTER THORN (Icelandic)
          "\\u00DF" +           // LATIN SMALL LETTER SHARP S (German)
          "\\u00E0" +           // LATIN SMALL LETTER A WITH GRAVE
          "\\u00E1" +           // LATIN SMALL LETTER A WITH ACUTE
          "\\u00E2" +           // LATIN SMALL LETTER A WITH CIRCUMFLEX
          "\\u00E3" +           // LATIN SMALL LETTER A WITH TILDE
          "\\u00E4" +           // LATIN SMALL LETTER A WITH DIAERESIS
          "\\u00E5" +           // LATIN SMALL LETTER A WITH RING ABOVE
          "\\u00E6" +           // LATIN SMALL LETTER AE (ash)
          "\\u00E7" +           // LATIN SMALL LETTER C WITH CEDILLA
          "\\u00E8" +           // LATIN SMALL LETTER E WITH GRAVE
          "\\u00E9" +           // LATIN SMALL LETTER E WITH ACUTE
          "\\u00EA" +           // LATIN SMALL LETTER E WITH CIRCUMFLEX
          "\\u00EB" +           // LATIN SMALL LETTER E WITH DIAERESIS
          "\\u00EC" +           // LATIN SMALL LETTER I WITH GRAVE
          "\\u00ED" +           // LATIN SMALL LETTER I WITH ACUTE
          "\\u00EE" +           // LATIN SMALL LETTER I WITH CIRCUMFLEX
          "\\u00EF" +           // LATIN SMALL LETTER I WITH DIAERESIS
          "\\u00F0" +           // LATIN SMALL LETTER ETH (Icelandic)
          "\\u00F1" +           // LATIN SMALL LETTER N WITH TILDE
          "\\u00F2" +           // LATIN SMALL LETTER O WITH GRAVE
          "\\u00F3" +           // LATIN SMALL LETTER O WITH ACUTE
          "\\u00F4" +           // LATIN SMALL LETTER O WITH CIRCUMFLEX
          "\\u00F5" +           // LATIN SMALL LETTER O WITH TILDE
          "\\u00F6" +           // LATIN SMALL LETTER O WITH DIAERESIS
          "\\u00F7" +           // DIVISION SIGN
          "\\u00F8" +           // LATIN SMALL LETTER O WITH STROKE
          "\\u00F9" +           // LATIN SMALL LETTER U WITH GRAVE
          "\\u00FA" +           // LATIN SMALL LETTER U WITH ACUTE
          "\\u00FB" +           // LATIN SMALL LETTER U WITH CIRCUMFLEX
          "\\u00FC" +           // LATIN SMALL LETTER U WITH DIAERESIS
          "\\u00FD" +           // LATIN SMALL LETTER Y WITH ACUTE
          "\\u00FE" +           // LATIN SMALL LETTER THORN (Icelandic)
          "\\u00FF" +           // LATIN SMALL LETTER Y WITH DIAERESIS
          "\\u0100" +           // LATIN CAPITAL LETTER A WITH MACRON
          "\\u0101" +           // LATIN SMALL LETTER A WITH MACRON
          "\\u0102" +           // LATIN CAPITAL LETTER A WITH BREVE
          "\\u0103" +           // LATIN SMALL LETTER A WITH BREVE
          "\\u0104" +           // LATIN CAPITAL LETTER A WITH OGONEK
          "\\u0105" +           // LATIN SMALL LETTER A WITH OGONEK
          "\\u0106" +           // LATIN CAPITAL LETTER C WITH ACUTE
          "\\u0107" +           // LATIN SMALL LETTER C WITH ACUTE
          "\\u0108" +           // LATIN CAPITAL LETTER C WITH CIRCUMFLEX
          "\\u0109" +           // LATIN SMALL LETTER C WITH CIRCUMFLEX
          "\\u010C" +           // LATIN CAPITAL LETTER C WITH CARON
          "\\u010D" +           // LATIN SMALL LETTER C WITH CARON
          "\\u010E" +           // LATIN CAPITAL LETTER D WITH CARON
          "\\u010F" +           // LATIN SMALL LETTER D WITH CARON
          "\\u0111" +           // LATIN SMALL LETTER D WITH STROKE
          "\\u0112" +           // LATIN CAPITAL LETTER E WITH MACRON
          "\\u0113" +           // LATIN SMALL LETTER E WITH MACRON
          "\\u0118" +           // LATIN CAPITAL LETTER E WITH OGONEK
          "\\u0119" +           // LATIN SMALL LETTER E WITH OGONEK
          "\\u011A" +           // LATIN CAPITAL LETTER E WITH CARON
          "\\u011B" +           // LATIN SMALL LETTER E WITH CARON
          "\\u011C" +           // LATIN CAPITAL LETTER G WITH CIRCUMFLEX
          "\\u011D" +           // LATIN SMALL LETTER G WITH CIRCUMFLEX
          "\\u0124" +           // LATIN CAPITAL LETTER H WITH CIRCUMFLEX
          "\\u0125" +           // LATIN SMALL LETTER H WITH CIRCUMFLEX
          "\\u0127" +           // LATIN SMALL LETTER H WITH STROKE
          "\\u012A" +           // LATIN CAPITAL LETTER I WITH MACRON
          "\\u012B" +           // LATIN SMALL LETTER I WITH MACRON
          "\\u0134" +           // LATIN CAPITAL LETTER J WITH CIRCUMFLEX
          "\\u0135" +           // LATIN SMALL LETTER J WITH CIRCUMFLEX
          "\\u0139" +           // LATIN CAPITAL LETTER L WITH ACUTE
          "\\u013A" +           // LATIN SMALL LETTER L WITH ACUTE
          "\\u013D" +           // LATIN CAPITAL LETTER L WITH CARON
          "\\u013E" +           // LATIN SMALL LETTER L WITH CARON
          "\\u0141" +           // LATIN CAPITAL LETTER L WITH STROKE
          "\\u0142" +           // LATIN SMALL LETTER L WITH STROKE
          "\\u0143" +           // LATIN CAPITAL LETTER N WITH ACUTE
          "\\u0144" +           // LATIN SMALL LETTER N WITH ACUTE
          "\\u0147" +           // LATIN CAPITAL LETTER N WITH CARON
          "\\u0148" +           // LATIN SMALL LETTER N WITH CARON
          "\\u014B" +           // LATIN SMALL LETTER ENG (Sami)
          "\\u014C" +           // LATIN CAPITAL LETTER O WITH MACRON
          "\\u014D" +           // LATIN SMALL LETTER O WITH MACRON
          "\\u0150" +           // LATIN CAPITAL LETTER O WITH DOUBLE ACUTE
          "\\u0151" +           // LATIN SMALL LETTER O WITH DOUBLE ACUTE
          "\\u0152" +           // LATIN CAPITAL LIGATURE OE
          "\\u0153" +           // LATIN SMALL LIGATURE OE
          "\\u0154" +           // LATIN CAPITAL LETTER R WITH ACUTE
          "\\u0155" +           // LATIN SMALL LETTER R WITH ACUTE
          "\\u0158" +           // LATIN CAPITAL LETTER R WITH CARON
          "\\u0159" +           // LATIN SMALL LETTER R WITH CARON
          "\\u015A" +           // LATIN CAPITAL LETTER S WITH ACUTE
          "\\u015B" +           // LATIN SMALL LETTER S WITH ACUTE
          "\\u015C" +           // LATIN CAPITAL LETTER S WITH CIRCUMFLEX
          "\\u015D" +           // LATIN SMALL LETTER S WITH CIRCUMFLEX
          "\\u015E" +           // LATIN CAPITAL LETTER S WITH CEDILLA
          "\\u015F" +           // LATIN SMALL LETTER S WITH CEDILLA
          "\\u0160" +           // LATIN CAPITAL LETTER S WITH CARON
          "\\u0161" +           // LATIN SMALL LETTER S WITH CARON
          "\\u0162" +           // LATIN CAPITAL LETTER T WITH CEDILLA
          "\\u0163" +           // LATIN SMALL LETTER T WITH CEDILLA
          "\\u0164" +           // LATIN CAPITAL LETTER T WITH CARON
          "\\u0165" +           // LATIN SMALL LETTER T WITH CARON
          "\\u016A" +           // LATIN CAPITAL LETTER U WITH MACRON
          "\\u016B" +           // LATIN SMALL LETTER U WITH MACRON
          "\\u016C" +           // LATIN CAPITAL LETTER U WITH BREVE
          "\\u016D" +           // LATIN SMALL LETTER U WITH BREVE
          "\\u016E" +           // LATIN CAPITAL LETTER U WITH RING ABOVE
          "\\u016F" +           // LATIN SMALL LETTER U WITH RING ABOVE
          "\\u0170" +           // LATIN CAPITAL LETTER U WITH DOUBLE ACUTE
          "\\u0171" +           // LATIN SMALL LETTER U WITH DOUBLE ACUTE
          "\\u0179" +           // LATIN CAPITAL LETTER Z WITH ACUTE
          "\\u017A" +           // LATIN SMALL LETTER Z WITH ACUTE
          "\\u017B" +           // LATIN CAPITAL LETTER Z WITH DOT ABOVE
          "\\u017C" +           // LATIN SMALL LETTER Z WITH DOT ABOVE
          "\\u017D" +           // LATIN CAPITAL LETTER Z WITH CARON
          "\\u017E" +           // LATIN SMALL LETTER Z WITH CARON
          "\\u0193" +           // LATIN CAPITAL LETTER G WITH HOOK
          "\\u01C2" +           // LATIN LETTER ALVEOLAR CLICK
          "\\u01CD" +           // LATIN CAPITAL LETTER A WITH CARON
          "\\u01CE" +           // LATIN SMALL LETTER A WITH CARON
          "\\u01D0" +           // LATIN SMALL LETTER I WITH CARON
          "\\u01D1" +           // LATIN CAPITAL LETTER O WITH CARON
          "\\u01D2" +           // LATIN SMALL LETTER O WITH CARON
          "\\u01D4" +           // LATIN SMALL LETTER U WITH CARON
          "\\u01D6" +           // LATIN SMALL LETTER U WITH DIAERESIS AND MACRON
          "\\u01D8" +           // LATIN SMALL LETTER U WITH DIAERESIS AND ACUTE
          "\\u01DA" +           // LATIN SMALL LETTER U WITH DIAERESIS AND CARON
          "\\u01DC" +           // LATIN SMALL LETTER U WITH DIAERESIS AND GRAVE
          "\\u01F8" +           // LATIN CAPITAL LETTER N WITH GRAVE
          "\\u01F9" +           // LATIN SMALL LETTER N WITH GRAVE
          "\\u01FD" +           // LATIN SMALL LETTER AE WITH ACUTE (ash)
          "\\u0250" +           // LATIN SMALL LETTER TURNED A
          "\\u0251" +           // LATIN SMALL LETTER ALPHA
          "\\u0252" +           // LATIN SMALL LETTER TURNED ALPHA
          "\\u0253" +           // LATIN SMALL LETTER B WITH HOOK
          "\\u0254" +           // LATIN SMALL LETTER OPEN O
          "\\u0255" +           // LATIN SMALL LETTER C WITH CURL
          "\\u0256" +           // LATIN SMALL LETTER D WITH TAIL
          "\\u0257" +           // LATIN SMALL LETTER D WITH HOOK
          "\\u0258" +           // LATIN SMALL LETTER REVERSED E
          "\\u0259" +           // LATIN SMALL LETTER SCHWA
          "\\u025A" +           // LATIN SMALL LETTER SCHWA WITH HOOK
          "\\u025C" +           // LATIN SMALL LETTER REVERSED OPEN E
          "\\u025E" +           // LATIN SMALL LETTER CLOSED REVERSED OPEN E
          "\\u025F" +           // LATIN SMALL LETTER DOTLESS J WITH STROKE
          "\\u0260" +           // LATIN SMALL LETTER G WITH HOOK
          "\\u0261" +           // LATIN SMALL LETTER SCRIPT G
          "\\u0264" +           // LATIN SMALL LETTER RAMS HORN
          "\\u0265" +           // LATIN SMALL LETTER TURNED H
          "\\u0266" +           // LATIN SMALL LETTER H WITH HOOK
          "\\u0267" +           // LATIN SMALL LETTER HENG WITH HOOK
          "\\u0268" +           // LATIN SMALL LETTER I WITH STROKE
          "\\u026C" +           // LATIN SMALL LETTER L WITH BELT
          "\\u026D" +           // LATIN SMALL LETTER L WITH RETROFLEX HOOK
          "\\u026E" +           // LATIN SMALL LETTER LEZH
          "\\u026F" +           // LATIN SMALL LETTER TURNED M
          "\\u0270" +           // LATIN SMALL LETTER TURNED M WITH LONG LEG
          "\\u0271" +           // LATIN SMALL LETTER M WITH HOOK
          "\\u0272" +           // LATIN SMALL LETTER N WITH LEFT HOOK
          "\\u0273" +           // LATIN SMALL LETTER N WITH RETROFLEX HOOK
          "\\u0275" +           // LATIN SMALL LETTER BARRED O
          "\\u0279" +           // LATIN SMALL LETTER TURNED R
          "\\u027A" +           // LATIN SMALL LETTER TURNED R WITH LONG LEG
          "\\u027B" +           // LATIN SMALL LETTER TURNED R WITH HOOK
          "\\u027D" +           // LATIN SMALL LETTER R WITH TAIL
          "\\u027E" +           // LATIN SMALL LETTER R WITH FISHHOOK
          "\\u0281" +           // LATIN LETTER SMALL CAPITAL INVERTED R
          "\\u0282" +           // LATIN SMALL LETTER S WITH HOOK
          "\\u0283" +           // LATIN SMALL LETTER ESH
          "\\u0284" +           // LATIN SMALL LETTER DOTLESS J WITH STROKE AND HOOK
          "\\u0288" +           // LATIN SMALL LETTER T WITH RETROFLEX HOOK
          "\\u0289" +           // LATIN SMALL LETTER U BAR
          "\\u028A" +           // LATIN SMALL LETTER UPSILON
          "\\u028B" +           // LATIN SMALL LETTER V WITH HOOK
          "\\u028C" +           // LATIN SMALL LETTER TURNED V
          "\\u028D" +           // LATIN SMALL LETTER TURNED W
          "\\u028E" +           // LATIN SMALL LETTER TURNED Y
          "\\u0290" +           // LATIN SMALL LETTER Z WITH RETROFLEX HOOK
          "\\u0291" +           // LATIN SMALL LETTER Z WITH CURL
          "\\u0292" +           // LATIN SMALL LETTER EZH
          "\\u0294" +           // LATIN LETTER GLOTTAL STOP
          "\\u0295" +           // LATIN LETTER PHARYNGEAL VOICED FRICATIVE
          "\\u0298" +           // LATIN LETTER BILABIAL CLICK
          "\\u029D" +           // LATIN SMALL LETTER J WITH CROSSED-TAIL
          "\\u02A1" +           // LATIN LETTER GLOTTAL STOP WITH STROKE
          "\\u02A2" +           // LATIN LETTER REVERSED GLOTTAL STOP WITH STROKE
          "\\u02C7" +           // CARON (Mandarin Chinese third tone)
          "\\u02C8" +           // MODIFIER LETTER VERTICAL LINE
          "\\u02CC" +           // MODIFIER LETTER LOW VERTICAL LINE
          "\\u02D0" +           // MODIFIER LETTER TRIANGULAR COLON
          "\\u02D1" +           // MODIFIER LETTER HALF TRIANGULAR COLON
          "\\u02D8" +           // BREVE
          "\\u02D9" +           // DOT ABOVE (Mandarin Chinese light tone)
          "\\u02DB" +           // OGONEK
          "\\u02DD" +           // DOUBLE ACUTE ACCENT
          "\\u02DE" +           // MODIFIER LETTER RHOTIC HOOK
          "\\u02E5" +           // MODIFIER LETTER EXTRA-HIGH TONE BAR
          "\\u02E6" +           // MODIFIER LETTER HIGH TONE BAR
          "\\u02E7" +           // MODIFIER LETTER MID TONE BAR
          "\\u02E8" +           // MODIFIER LETTER LOW TONE BAR
          "\\u02E9" +           // MODIFIER LETTER EXTRA-LOW TONE BAR
          "\\u0300" +           // COMBINING GRAVE ACCENT (Varia)
          "\\u0301" +           // COMBINING ACUTE ACCENT (Oxia, Tonos)
          "\\u0302" +           // COMBINING CIRCUMFLEX ACCENT
          "\\u0303" +           // COMBINING TILDE
          "\\u0304" +           // COMBINING MACRON
          "\\u0306" +           // COMBINING BREVE (Vrachy)
          "\\u0308" +           // COMBINING DIAERESIS (Dialytika)
          "\\u030B" +           // COMBINING DOUBLE ACUTE ACCENT
          "\\u030C" +           // COMBINING CARON
          "\\u030F" +           // COMBINING DOUBLE GRAVE ACCENT
          "\\u0318" +           // COMBINING LEFT TACK BELOW
          "\\u0319" +           // COMBINING RIGHT TACK BELOW
          "\\u031A" +           // COMBINING LEFT ANGLE ABOVE
          "\\u031C" +           // COMBINING LEFT HALF RING BELOW
          "\\u031D" +           // COMBINING UP TACK BELOW
          "\\u031E" +           // COMBINING DOWN TACK BELOW
          "\\u031F" +           // COMBINING PLUS SIGN BELOW
          "\\u0320" +           // COMBINING MINUS SIGN BELOW
          "\\u0324" +           // COMBINING DIAERESIS BELOW
          "\\u0325" +           // COMBINING RING BELOW
          "\\u0329" +           // COMBINING VERTICAL LINE BELOW
          "\\u032A" +           // COMBINING BRIDGE BELOW
          "\\u032C" +           // COMBINING CARON BELOW
          "\\u032F" +           // COMBINING INVERTED BREVE BELOW
          "\\u0330" +           // COMBINING TILDE BELOW
          "\\u0334" +           // COMBINING TILDE OVERLAY
          "\\u0339" +           // COMBINING RIGHT HALF RING BELOW
          "\\u033A" +           // COMBINING INVERTED BRIDGE BELOW
          "\\u033B" +           // COMBINING SQUARE BELOW
          "\\u033C" +           // COMBINING SEAGULL BELOW
          "\\u033D" +           // COMBINING X ABOVE
          "\\u0361" +           // COMBINING DOUBLE INVERTED BREVE
          "\\u0391" +           // GREEK CAPITAL LETTER ALPHA
          "\\u0392" +           // GREEK CAPITAL LETTER BETA
          "\\u0393" +           // GREEK CAPITAL LETTER GAMMA
          "\\u0394" +           // GREEK CAPITAL LETTER DELTA
          "\\u0395" +           // GREEK CAPITAL LETTER EPSILON
          "\\u0396" +           // GREEK CAPITAL LETTER ZETA
          "\\u0397" +           // GREEK CAPITAL LETTER ETA
          "\\u0398" +           // GREEK CAPITAL LETTER THETA
          "\\u0399" +           // GREEK CAPITAL LETTER IOTA
          "\\u039A" +           // GREEK CAPITAL LETTER KAPPA
          "\\u039B" +           // GREEK CAPITAL LETTER LAMDA
          "\\u039C" +           // GREEK CAPITAL LETTER MU
          "\\u039D" +           // GREEK CAPITAL LETTER NU
          "\\u039E" +           // GREEK CAPITAL LETTER XI
          "\\u039F" +           // GREEK CAPITAL LETTER OMICRON
          "\\u03A0" +           // GREEK CAPITAL LETTER PI
          "\\u03A1" +           // GREEK CAPITAL LETTER RHO
          "\\u03A3" +           // GREEK CAPITAL LETTER SIGMA
          "\\u03A4" +           // GREEK CAPITAL LETTER TAU
          "\\u03A5" +           // GREEK CAPITAL LETTER UPSILON
          "\\u03A6" +           // GREEK CAPITAL LETTER PHI
          "\\u03A7" +           // GREEK CAPITAL LETTER CHI
          "\\u03A8" +           // GREEK CAPITAL LETTER PSI
          "\\u03A9" +           // GREEK CAPITAL LETTER OMEGA
          "\\u03B1" +           // GREEK SMALL LETTER ALPHA
          "\\u03B2" +           // GREEK SMALL LETTER BETA
          "\\u03B3" +           // GREEK SMALL LETTER GAMMA
          "\\u03B4" +           // GREEK SMALL LETTER DELTA
          "\\u03B5" +           // GREEK SMALL LETTER EPSILON
          "\\u03B6" +           // GREEK SMALL LETTER ZETA
          "\\u03B7" +           // GREEK SMALL LETTER ETA
          "\\u03B8" +           // GREEK SMALL LETTER THETA
          "\\u03B9" +           // GREEK SMALL LETTER IOTA
          "\\u03BA" +           // GREEK SMALL LETTER KAPPA
          "\\u03BB" +           // GREEK SMALL LETTER LAMDA
          "\\u03BC" +           // GREEK SMALL LETTER MU
          "\\u03BD" +           // GREEK SMALL LETTER NU
          "\\u03BE" +           // GREEK SMALL LETTER XI
          "\\u03BF" +           // GREEK SMALL LETTER OMICRON
          "\\u03C0" +           // GREEK SMALL LETTER PI
          "\\u03C1" +           // GREEK SMALL LETTER RHO
          "\\u03C2" +           // GREEK SMALL LETTER FINAL SIGMA
          "\\u03C3" +           // GREEK SMALL LETTER SIGMA
          "\\u03C4" +           // GREEK SMALL LETTER TAU
          "\\u03C5" +           // GREEK SMALL LETTER UPSILON
          "\\u03C6" +           // GREEK SMALL LETTER PHI
          "\\u03C7" +           // GREEK SMALL LETTER CHI
          "\\u03C8" +           // GREEK SMALL LETTER PSI
          "\\u03C9" +           // GREEK SMALL LETTER OMEGA
          "\\u0401" +           // CYRILLIC CAPITAL LETTER IO
          "\\u0410" +           // CYRILLIC CAPITAL LETTER A
          "\\u0411" +           // CYRILLIC CAPITAL LETTER BE
          "\\u0412" +           // CYRILLIC CAPITAL LETTER VE
          "\\u0413" +           // CYRILLIC CAPITAL LETTER GHE
          "\\u0414" +           // CYRILLIC CAPITAL LETTER DE
          "\\u0415" +           // CYRILLIC CAPITAL LETTER IE
          "\\u0416" +           // CYRILLIC CAPITAL LETTER ZHE
          "\\u0417" +           // CYRILLIC CAPITAL LETTER ZE
          "\\u0418" +           // CYRILLIC CAPITAL LETTER I
          "\\u0419" +           // CYRILLIC CAPITAL LETTER SHORT I
          "\\u041A" +           // CYRILLIC CAPITAL LETTER KA
          "\\u041B" +           // CYRILLIC CAPITAL LETTER EL
          "\\u041C" +           // CYRILLIC CAPITAL LETTER EM
          "\\u041D" +           // CYRILLIC CAPITAL LETTER EN
          "\\u041E" +           // CYRILLIC CAPITAL LETTER O
          "\\u041F" +           // CYRILLIC CAPITAL LETTER PE
          "\\u0420" +           // CYRILLIC CAPITAL LETTER ER
          "\\u0421" +           // CYRILLIC CAPITAL LETTER ES
          "\\u0422" +           // CYRILLIC CAPITAL LETTER TE
          "\\u0423" +           // CYRILLIC CAPITAL LETTER U
          "\\u0424" +           // CYRILLIC CAPITAL LETTER EF
          "\\u0425" +           // CYRILLIC CAPITAL LETTER HA
          "\\u0426" +           // CYRILLIC CAPITAL LETTER TSE
          "\\u0427" +           // CYRILLIC CAPITAL LETTER CHE
          "\\u0428" +           // CYRILLIC CAPITAL LETTER SHA
          "\\u0429" +           // CYRILLIC CAPITAL LETTER SHCHA
          "\\u042A" +           // CYRILLIC CAPITAL LETTER HARD SIGN
          "\\u042B" +           // CYRILLIC CAPITAL LETTER YERU
          "\\u042C" +           // CYRILLIC CAPITAL LETTER SOFT SIGN
          "\\u042D" +           // CYRILLIC CAPITAL LETTER E
          "\\u042E" +           // CYRILLIC CAPITAL LETTER YU
          "\\u042F" +           // CYRILLIC CAPITAL LETTER YA
          "\\u0430" +           // CYRILLIC SMALL LETTER A
          "\\u0431" +           // CYRILLIC SMALL LETTER BE
          "\\u0432" +           // CYRILLIC SMALL LETTER VE
          "\\u0433" +           // CYRILLIC SMALL LETTER GHE
          "\\u0434" +           // CYRILLIC SMALL LETTER DE
          "\\u0435" +           // CYRILLIC SMALL LETTER IE
          "\\u0436" +           // CYRILLIC SMALL LETTER ZHE
          "\\u0437" +           // CYRILLIC SMALL LETTER ZE
          "\\u0438" +           // CYRILLIC SMALL LETTER I
          "\\u0439" +           // CYRILLIC SMALL LETTER SHORT I
          "\\u043A" +           // CYRILLIC SMALL LETTER KA
          "\\u043B" +           // CYRILLIC SMALL LETTER EL
          "\\u043C" +           // CYRILLIC SMALL LETTER EM
          "\\u043D" +           // CYRILLIC SMALL LETTER EN
          "\\u043E" +           // CYRILLIC SMALL LETTER O
          "\\u043F" +           // CYRILLIC SMALL LETTER PE
          "\\u0440" +           // CYRILLIC SMALL LETTER ER
          "\\u0441" +           // CYRILLIC SMALL LETTER ES
          "\\u0442" +           // CYRILLIC SMALL LETTER TE
          "\\u0443" +           // CYRILLIC SMALL LETTER U
          "\\u0444" +           // CYRILLIC SMALL LETTER EF
          "\\u0445" +           // CYRILLIC SMALL LETTER HA
          "\\u0446" +           // CYRILLIC SMALL LETTER TSE
          "\\u0447" +           // CYRILLIC SMALL LETTER CHE
          "\\u0448" +           // CYRILLIC SMALL LETTER SHA
          "\\u0449" +           // CYRILLIC SMALL LETTER SHCHA
          "\\u044A" +           // CYRILLIC SMALL LETTER HARD SIGN
          "\\u044B" +           // CYRILLIC SMALL LETTER YERU
          "\\u044C" +           // CYRILLIC SMALL LETTER SOFT SIGN
          "\\u044D" +           // CYRILLIC SMALL LETTER E
          "\\u044E" +           // CYRILLIC SMALL LETTER YU
          "\\u044F" +           // CYRILLIC SMALL LETTER YA
          "\\u0451" +           // CYRILLIC SMALL LETTER IO
          "\\u1E3E" +           // LATIN CAPITAL LETTER M WITH ACUTE
          "\\u1E3F" +           // LATIN SMALL LETTER M WITH ACUTE
          "\\u1F70" +           // GREEK SMALL LETTER ALPHA WITH VARIA
          "\\u1F71" +           // GREEK SMALL LETTER ALPHA WITH OXIA
          "\\u1F72" +           // GREEK SMALL LETTER EPSILON WITH VARIA
          "\\u1F73" +           // GREEK SMALL LETTER EPSILON WITH OXIA
          "\\u2010" +           // HYPHEN
          "\\u2013" +           // EN DASH
          "\\u2014" +           // EM DASH
          "\\u2016" +           // DOUBLE VERTICAL LINE
          "\\u2018" +           // LEFT SINGLE QUOTATION MARK
          "\\u2019" +           // RIGHT SINGLE QUOTATION MARK
          "\\u201C" +           // LEFT DOUBLE QUOTATION MARK
          "\\u201D" +           // RIGHT DOUBLE QUOTATION MARK
          "\\u2020" +           // DAGGER
          "\\u2021" +           // DOUBLE DAGGER
          "\\u2022" +           // BULLET
          "\\u2025" +           // TWO DOT LEADER
          "\\u2026" +           // HORIZONTAL ELLIPSIS
          "\\u2030" +           // PER MILLE SIGN
          "\\u2032" +           // PRIME
          "\\u2033" +           // DOUBLE PRIME
          "\\u203E" +           // OVERLINE
          "\\u203F" +           // UNDERTIE (Enotikon)
          "\\u2042" +           // ASTERISM
          "\\u2051" +           // TWO ASTERISKS ALIGNED VERTICALLY
          "\\u20AC" +           // EURO SIGN
          "\\u210F" +           // PLANCK CONSTANT OVER TWO PI
          "\\u2127" +           // INVERTED OHM SIGN
          "\\u212B" +           // ANGSTROM SIGN
          "\\u2135" +           // ALEF SYMBOL
          "\\u2153" +           // VULGAR FRACTION ONE THIRD
          "\\u2154" +           // VULGAR FRACTION TWO THIRDS
          "\\u2155" +           // VULGAR FRACTION ONE FIFTH
          "\\u2190" +           // LEFTWARDS ARROW
          "\\u2191" +           // UPWARDS ARROW
          "\\u2192" +           // RIGHTWARDS ARROW
          "\\u2193" +           // DOWNWARDS ARROW
          "\\u2194" +           // LEFT RIGHT ARROW
          "\\u2196" +           // NORTH WEST ARROW
          "\\u2197" +           // NORTH EAST ARROW
          "\\u2198" +           // SOUTH EAST ARROW
          "\\u2199" +           // SOUTH WEST ARROW
          "\\u21C4" +           // RIGHTWARDS ARROW OVER LEFTWARDS ARROW
          "\\u21D2" +           // RIGHTWARDS DOUBLE ARROW
          "\\u21D4" +           // LEFT RIGHT DOUBLE ARROW
          "\\u21E6" +           // LEFTWARDS WHITE ARROW
          "\\u21E7" +           // UPWARDS WHITE ARROW
          "\\u21E8" +           // RIGHTWARDS WHITE ARROW
          "\\u21E9" +           // DOWNWARDS WHITE ARROW
          "\\u2200" +           // FOR ALL
          "\\u2202" +           // PARTIAL DIFFERENTIAL
          "\\u2203" +           // THERE EXISTS
          "\\u2205" +           // EMPTY SET
          "\\u2207" +           // NABLA
          "\\u2208" +           // ELEMENT OF
          "\\u2209" +           // NOT AN ELEMENT OF
          "\\u220B" +           // CONTAINS AS MEMBER
          "\\u2212" +           // MINUS SIGN
          "\\u2213" +           // MINUS-OR-PLUS SIGN
          "\\u221A" +           // SQUARE ROOT
          "\\u221D" +           // TO
          "\\u221E" +           // INFINITY
          "\\u221F" +           // RIGHT ANGLE
          "\\u2220" +           // ANGLE
          "\\u2225" +           // PARALLEL TO
          "\\u2226" +           // NOT PARALLEL TO
          "\\u2227" +           // LOGICAL AND
          "\\u2228" +           // LOGICAL OR
          "\\u2229" +           // INTERSECTION
          "\\u222A" +           // UNION
          "\\u222B" +           // INTEGRAL
          "\\u222C" +           // DOUBLE INTEGRAL
          "\\u222E" +           // CONTOUR INTEGRAL
          "\\u2234" +           // THEREFORE
          "\\u2235" +           // BECAUSE
          "\\u223D" +           // REVERSED TILDE (lazy S)
          "\\u2243" +           // ASYMPTOTICALLY EQUAL TO
          "\\u2245" +           // APPROXIMATELY EQUAL TO
          "\\u2248" +           // ALMOST EQUAL TO
          "\\u2252" +           // APPROXIMATELY EQUAL TO OR THE IMAGE OF
          "\\u2260" +           // NOT EQUAL TO
          "\\u2261" +           // IDENTICAL TO
          "\\u2262" +           // NOT IDENTICAL TO
          "\\u2266" +           // LESS-THAN OVER EQUAL TO
          "\\u2267" +           // GREATER-THAN OVER EQUAL TO
          "\\u226A" +           // MUCH LESS-THAN
          "\\u226B" +           // MUCH GREATER-THAN
          "\\u2276" +           // LESS-THAN OR GREATER-THAN
          "\\u2277" +           // GREATER-THAN OR LESS-THAN
          "\\u2282" +           // SUBSET OF
          "\\u2283" +           // SUPERSET OF
          "\\u2284" +           // NOT A SUBSET OF
          "\\u2285" +           // NOT A SUPERSET OF
          "\\u2286" +           // SUBSET OF OR EQUAL TO
          "\\u2287" +           // SUPERSET OF OR EQUAL TO
          "\\u228A" +           // SUBSET OF WITH NOT EQUAL TO
          "\\u228B" +           // SUPERSET OF WITH NOT EQUAL TO
          "\\u2295" +           // CIRCLED PLUS
          "\\u2296" +           // CIRCLED MINUS
          "\\u2297" +           // CIRCLED TIMES
          "\\u22A5" +           // UP TACK
          "\\u22DA" +           // LESS-THAN EQUAL TO OR GREATER-THAN
          "\\u22DB" +           // GREATER-THAN EQUAL TO OR LESS-THAN
          "\\u2305" +           // PROJECTIVE
          "\\u2306" +           // PERSPECTIVE
          "\\u2312" +           // ARC
          "\\u2318" +           // PLACE OF INTEREST SIGN
          "\\u23CE" +           // RETURN SYMBOL
          "\\u2423" +           // OPEN BOX
          "\\u2460" +           // CIRCLED DIGIT ONE
          "\\u2461" +           // CIRCLED DIGIT TWO
          "\\u2462" +           // CIRCLED DIGIT THREE
          "\\u2463" +           // CIRCLED DIGIT FOUR
          "\\u2464" +           // CIRCLED DIGIT FIVE
          "\\u2465" +           // CIRCLED DIGIT SIX
          "\\u2466" +           // CIRCLED DIGIT SEVEN
          "\\u2467" +           // CIRCLED DIGIT EIGHT
          "\\u2468" +           // CIRCLED DIGIT NINE
          "\\u2469" +           // CIRCLED NUMBER TEN
          "\\u246A" +           // CIRCLED NUMBER ELEVEN
          "\\u246B" +           // CIRCLED NUMBER TWELVE
          "\\u246C" +           // CIRCLED NUMBER THIRTEEN
          "\\u246D" +           // CIRCLED NUMBER FOURTEEN
          "\\u246E" +           // CIRCLED NUMBER FIFTEEN
          "\\u246F" +           // CIRCLED NUMBER SIXTEEN
          "\\u2470" +           // CIRCLED NUMBER SEVENTEEN
          "\\u2471" +           // CIRCLED NUMBER EIGHTEEN
          "\\u2472" +           // CIRCLED NUMBER NINETEEN
          "\\u2473" +           // CIRCLED NUMBER TWENTY
          "\\u24D0" +           // CIRCLED LATIN SMALL LETTER A
          "\\u24D1" +           // CIRCLED LATIN SMALL LETTER B
          "\\u24D2" +           // CIRCLED LATIN SMALL LETTER C
          "\\u24D3" +           // CIRCLED LATIN SMALL LETTER D
          "\\u24D4" +           // CIRCLED LATIN SMALL LETTER E
          "\\u24D5" +           // CIRCLED LATIN SMALL LETTER F
          "\\u24D6" +           // CIRCLED LATIN SMALL LETTER G
          "\\u24D7" +           // CIRCLED LATIN SMALL LETTER H
          "\\u24D8" +           // CIRCLED LATIN SMALL LETTER I
          "\\u24D9" +           // CIRCLED LATIN SMALL LETTER J
          "\\u24DA" +           // CIRCLED LATIN SMALL LETTER K
          "\\u24DB" +           // CIRCLED LATIN SMALL LETTER L
          "\\u24DC" +           // CIRCLED LATIN SMALL LETTER M
          "\\u24DD" +           // CIRCLED LATIN SMALL LETTER N
          "\\u24DE" +           // CIRCLED LATIN SMALL LETTER O
          "\\u24DF" +           // CIRCLED LATIN SMALL LETTER P
          "\\u24E0" +           // CIRCLED LATIN SMALL LETTER Q
          "\\u24E1" +           // CIRCLED LATIN SMALL LETTER R
          "\\u24E2" +           // CIRCLED LATIN SMALL LETTER S
          "\\u24E3" +           // CIRCLED LATIN SMALL LETTER T
          "\\u24E4" +           // CIRCLED LATIN SMALL LETTER U
          "\\u24E5" +           // CIRCLED LATIN SMALL LETTER V
          "\\u24E6" +           // CIRCLED LATIN SMALL LETTER W
          "\\u24E7" +           // CIRCLED LATIN SMALL LETTER X
          "\\u24E8" +           // CIRCLED LATIN SMALL LETTER Y
          "\\u24E9" +           // CIRCLED LATIN SMALL LETTER Z
          "\\u24EB" +           // NEGATIVE CIRCLED NUMBER ELEVEN
          "\\u24EC" +           // NEGATIVE CIRCLED NUMBER TWELVE
          "\\u24ED" +           // NEGATIVE CIRCLED NUMBER THIRTEEN
          "\\u24EE" +           // NEGATIVE CIRCLED NUMBER FOURTEEN
          "\\u24EF" +           // NEGATIVE CIRCLED NUMBER FIFTEEN
          "\\u24F0" +           // NEGATIVE CIRCLED NUMBER SIXTEEN
          "\\u24F1" +           // NEGATIVE CIRCLED NUMBER SEVENTEEN
          "\\u24F2" +           // NEGATIVE CIRCLED NUMBER EIGHTEEN
          "\\u24F3" +           // NEGATIVE CIRCLED NUMBER NINETEEN
          "\\u24F4" +           // NEGATIVE CIRCLED NUMBER TWENTY
          "\\u24F5" +           // DOUBLE CIRCLED DIGIT ONE
          "\\u24F6" +           // DOUBLE CIRCLED DIGIT TWO
          "\\u24F7" +           // DOUBLE CIRCLED DIGIT THREE
          "\\u24F8" +           // DOUBLE CIRCLED DIGIT FOUR
          "\\u24F9" +           // DOUBLE CIRCLED DIGIT FIVE
          "\\u24FA" +           // DOUBLE CIRCLED DIGIT SIX
          "\\u24FB" +           // DOUBLE CIRCLED DIGIT SEVEN
          "\\u24FC" +           // DOUBLE CIRCLED DIGIT EIGHT
          "\\u24FD" +           // DOUBLE CIRCLED DIGIT NINE
          "\\u24FE" +           // DOUBLE CIRCLED NUMBER TEN
          "\\u25A0" +           // BLACK SQUARE
          "\\u25A1" +           // WHITE SQUARE
          "\\u25B1" +           // WHITE PARALLELOGRAM
          "\\u25B2" +           // BLACK UP-POINTING TRIANGLE
          "\\u25B3" +           // WHITE UP-POINTING TRIANGLE
          "\\u25B6" +           // BLACK RIGHT-POINTING TRIANGLE
          "\\u25B7" +           // WHITE RIGHT-POINTING TRIANGLE
          "\\u25BC" +           // BLACK DOWN-POINTING TRIANGLE
          "\\u25BD" +           // WHITE DOWN-POINTING TRIANGLE
          "\\u25C0" +           // BLACK LEFT-POINTING TRIANGLE
          "\\u25C1" +           // WHITE LEFT-POINTING TRIANGLE
          "\\u25C6" +           // BLACK DIAMOND
          "\\u25C7" +           // WHITE DIAMOND
          "\\u25CB" +           // WHITE CIRCLE
          "\\u25CE" +           // BULLSEYE
          "\\u25CF" +           // BLACK CIRCLE
          "\\u25D0" +           // CIRCLE WITH LEFT HALF BLACK
          "\\u25D1" +           // CIRCLE WITH RIGHT HALF BLACK
          "\\u25D2" +           // CIRCLE WITH LOWER HALF BLACK
          "\\u25D3" +           // CIRCLE WITH UPPER HALF BLACK
          "\\u25E6" +           // WHITE BULLET
          "\\u25EF" +           // LARGE CIRCLE
          "\\u2600" +           // BLACK SUN WITH RAYS
          "\\u2601" +           // CLOUD
          "\\u2602" +           // UMBRELLA
          "\\u2603" +           // SNOWMAN
          "\\u2605" +           // BLACK STAR
          "\\u2606" +           // WHITE STAR
          "\\u260E" +           // BLACK TELEPHONE
          "\\u261E" +           // WHITE RIGHT POINTING INDEX
          "\\u2640" +           // FEMALE SIGN
          "\\u2642" +           // MALE SIGN
          "\\u2660" +           // BLACK SPADE SUIT
          "\\u2661" +           // WHITE HEART SUIT
          "\\u2662" +           // WHITE DIAMOND SUIT
          "\\u2663" +           // BLACK CLUB SUIT
          "\\u2664" +           // WHITE SPADE SUIT
          "\\u2665" +           // BLACK HEART SUIT
          "\\u2666" +           // BLACK DIAMOND SUIT
          "\\u2667" +           // WHITE CLUB SUIT
          "\\u2669" +           // QUARTER NOTE
          "\\u266A" +           // EIGHTH NOTE
          "\\u266B" +           // BEAMED EIGHTH NOTES
          "\\u266C" +           // BEAMED SIXTEENTH NOTES
          "\\u266D" +           // MUSIC FLAT SIGN
          "\\u266E" +           // MUSIC NATURAL SIGN
          "\\u266F" +           // MUSIC SHARP SIGN
          "\\u2713" +           // CHECK MARK
          "\\u2756" +           // BLACK DIAMOND MINUS WHITE X
          "\\u2776" +           // DINGBAT NEGATIVE CIRCLED DIGIT ONE
          "\\u2777" +           // DINGBAT NEGATIVE CIRCLED DIGIT TWO
          "\\u2778" +           // DINGBAT NEGATIVE CIRCLED DIGIT THREE
          "\\u2779" +           // DINGBAT NEGATIVE CIRCLED DIGIT FOUR
          "\\u277A" +           // DINGBAT NEGATIVE CIRCLED DIGIT FIVE
          "\\u277B" +           // DINGBAT NEGATIVE CIRCLED DIGIT SIX
          "\\u277C" +           // DINGBAT NEGATIVE CIRCLED DIGIT SEVEN
          "\\u277D" +           // DINGBAT NEGATIVE CIRCLED DIGIT EIGHT
          "\\u277E" +           // DINGBAT NEGATIVE CIRCLED DIGIT NINE
          "\\u277F" +           // DINGBAT NEGATIVE CIRCLED NUMBER TEN
          "\\u2934" +           // ARROW POINTING RIGHTWARDS THEN CURVING UPWARDS
          "\\u2935" +           // ARROW POINTING RIGHTWARDS THEN CURVING DOWNWARDS
          "\\u29FA" +           // DOUBLE PLUS
          "\\u29FB" +           // TRIPLE PLUS
          "\\u3251" +           // CIRCLED NUMBER TWENTY ONE
          "\\u3252" +           // CIRCLED NUMBER TWENTY TWO
          "\\u3253" +           // CIRCLED NUMBER TWENTY THREE
          "\\u3254" +           // CIRCLED NUMBER TWENTY FOUR
          "\\u3255" +           // CIRCLED NUMBER TWENTY FIVE
          "\\u3256" +           // CIRCLED NUMBER TWENTY SIX
          "\\u3257" +           // CIRCLED NUMBER TWENTY SEVEN
          "\\u3258" +           // CIRCLED NUMBER TWENTY EIGHT
          "\\u3259" +           // CIRCLED NUMBER TWENTY NINE
          "\\u325A" +           // CIRCLED NUMBER THIRTY
          "\\u325B" +           // CIRCLED NUMBER THIRTY ONE
          "\\u325C" +           // CIRCLED NUMBER THIRTY TWO
          "\\u325D" +           // CIRCLED NUMBER THIRTY THREE
          "\\u325E" +           // CIRCLED NUMBER THIRTY FOUR
          "\\u325F" +           // CIRCLED NUMBER THIRTY FIVE
          "\\u32B1" +           // CIRCLED NUMBER THIRTY SIX
          "\\u32B2" +           // CIRCLED NUMBER THIRTY SEVEN
          "\\u32B3" +           // CIRCLED NUMBER THIRTY EIGHT
          "\\u32B4" +           // CIRCLED NUMBER THIRTY NINE
          "\\u32B5" +           // CIRCLED NUMBER FORTY
          "\\u32B6" +           // CIRCLED NUMBER FORTY ONE
          "\\u32B7" +           // CIRCLED NUMBER FORTY TWO
          "\\u32B8" +           // CIRCLED NUMBER FORTY THREE
          "\\u32B9" +           // CIRCLED NUMBER FORTY FOUR
          "\\u32BA" +           // CIRCLED NUMBER FORTY FIVE
          "\\u32BB" +           // CIRCLED NUMBER FORTY SIX
          "\\u32BC" +           // CIRCLED NUMBER FORTY SEVEN
          "\\u32BD" +           // CIRCLED NUMBER FORTY EIGHT
          "\\u32BE" +           // CIRCLED NUMBER FORTY NINE
          "\\u32BF" +           // CIRCLED NUMBER FIFTY
          "]"
        },
        { "openWarichu",        // JLREQ cl-28
          "[" +
          "\\u0028" +           // LEFT PARENTHESIS
          "\\u3014" +           // LEFT TORTOISE SHELL BRACKET
          "\\u005B" +           // LEFT SQUARE BRACKET
          "]"
        },
        { "closeWarichu",       // JLREQ cl-29
          "[" +
          "\\u0029" +           // RIGHT PARENTHESIS
          "\\u3015" +           // RIGHT TORTOISE SHELL BRACKET
          "\\u005D" +           // RIGHT SQUARE BRACKET
          "]"
        },
    };

    private static final Map<String,CharacterClass> characterClasses;
    private static final CharacterClass autoCharacterClass;
    static {
        // named classes
        Map<String,CharacterClass> m = new java.util.HashMap<String,CharacterClass>();
        for (String[] ccs : characterClassSpecifications) {
            assert ccs.length == 2;
            String n = ccs[0];
            String c = ccs[1];
            m.put(n,CharacterClass.parse(c));
        }
        characterClasses = Collections.unmodifiableMap(m);
        // auto class
        CharacterClass c = new CharacterClass(CharacterClass.EMPTY);
        c.add(m.get("hiragana"));       // JLREQ cl-15
        c.add(m.get("katakana"));       // JLREQ cl-16
        c.add(m.get("soundMarks"));     // JLREQ cl-10
        c.add(m.get("smallKana"));      // JLREQ cl-11
        autoCharacterClass = c;
    }

    public static CharacterClass getAutoCharacterClass() {
        return autoCharacterClass;
    }

    public static boolean isCharacterClass(String value, Locator locator, VerifierContext context, CharacterClass[] outputClass) {
        String[] components = splitComponents(value);
        int numComponents = components.length;
        if (numComponents < 1)
            return false;
        CharacterClass cc = (outputClass != null) ? new CharacterClass(CharacterClass.EMPTY) : null;
        if ((numComponents == 1) && Keywords.isAuto(components[0])) {
            cc.add(getAutoCharacterClass());
        } else {
            for (int i = 0, n = numComponents; i < n; ++i) {
                String c = components[i];
                if (isNamedCharacterClass(c)) {
                    if (cc != null)
                        cc.add(getNamedCharacterClass(c));
                    continue;
                } else if (isCharacterClass(c)) {
                    if (cc != null)
                        cc.add(CharacterClass.parse(c));
                    continue;
                } else
                    return false;
            }
        }
        if (outputClass != null)
            outputClass[0] = cc;
        return true;
    }

    private static String[] splitComponents(String value) {
        return value.split("[ \t\r\n]+");
    }

    private static boolean isNamedCharacterClass(String s) {
        return characterClasses.containsKey(s);
    }

    private static CharacterClass getNamedCharacterClass(String s) {
        return characterClasses.get(s);
    }

    private static boolean isCharacterClass(String s) {
        try {
            CharacterClass cc = CharacterClass.parse(s);
            return !cc.isEmpty();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public static void badCharacterClass(String value, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(locator, "*KEY*", "Bad character class expression ''{0}''.", value));
    }

    public static boolean isXMLSpace(char c) {
        return (c == ' ') || (c == '\t') || (c == '\n') || (c == '\r');
    }

    public static boolean isHexDigit(char c) {
        return ((c >= '0') && (c <= '9')) || ((c >= 'A') && (c <= 'F')) || ((c >= 'a') && (c <= 'f'));
    }

    public static boolean isDigit(char c) {
        return ((c >= '0') && (c <= '9'));
    }

    public static boolean isLetter(char c) {
        return ((c >= 'A') && (c <= 'Z')) || ((c >= 'a') && (c <= 'z'));
    }


    public static String charToNCRef(int c) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, nDigits = (c > 0xFFFF) ? 6 : 4; i < nDigits; i++, c >>= 4) {
            int d = c & 0xF;
            char hd;
            if (d < 10) {
                hd = (char) ((int) '0' + d);
            } else {
                hd = (char) ((int) 'A' + (d - 10));
            }
            sb.append(hd);
        }
        return "&#x" + sb.reverse() + ";";
    }

    public static String maybeEscapeAsNCRef(int c) {
        if ((c >= 32) && (c < 127))
            return Character.toString((char)c);
        else
            return charToNCRef(c);
    }

}
