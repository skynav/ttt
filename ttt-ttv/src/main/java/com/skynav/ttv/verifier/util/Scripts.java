/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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
import java.util.Set;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Scripts {

    private static final String[] iso15924ScriptIdentifiers = new String[] {
        "adlm", // Adlam
        "afak", // Afaka
        "aghb", // Caucasian Albanian
        "ahom", // Ahom, Tai Ahom
        "arab", // Arabic
        "aran", // Arabic (Nastaliq variant)
        "armi", // Imperial Aramaic
        "armn", // Armenian
        "avst", // Avestan
        "bali", // Balinese
        "bamu", // Bamum
        "bass", // Bassa Vah
        "batk", // Batak
        "beng", // Bengali
        "bhks", // Bhaiksuki
        "blis", // Blissymbols
        "bopo", // Bopomofo
        "brah", // Brahmi
        "brai", // Braille
        "bugi", // Buginese
        "buhd", // Buhid
        "cakm", // Chakma
        "cans", // Unified Canadian Aboriginal Syllabics
        "cari", // Carian
        "cham", // Cham
        "cher", // Cherokee
        "cirt", // Cirth
        "copt", // Coptic
        "cprt", // Cypriot
        "cyrl", // Cyrillic
        "cyrs", // Cyrillic (Old Church Slavonic variant)
        "deva", // Devanagari (Nagari)
        "dsrt", // Deseret (Mormon)
        "dupl", // Duployan shorthand, Duployan stenography
        "egyd", // Egyptian demotic
        "egyh", // Egyptian hieratic
        "egyp", // Egyptian hieroglyphs
        "elba", // Elbasan
        "ethi", // Ethiopic (Geʻez)
        "geok", // Khutsuri (Asomtavruli and Nuskhuri)
        "geor", // Georgian (Mkhedruli)
        "glag", // Glagolitic
        "goth", // Gothic
        "gran", // Grantha
        "grek", // Greek
        "gujr", // Gujarati
        "guru", // Gurmukhi
        "hang", // Hangul (Hangŭl, Hangeul)
        "hani", // Han (Hanzi, Kanji, Hanja)
        "hano", // Hanunoo (Hanunóo)
        "hans", // Han (Simplified variant)
        "hant", // Han (Traditional variant)
        "hatr", // Hatran
        "hebr", // Hebrew
        "hira", // Hiragana
        "hluw", // Anatolian Hieroglyphs (Luwian Hieroglyphs, Hittite Hieroglyphs)
        "hmng", // Pahawh Hmong
        "hrkt", // Japanese syllabaries (alias for Hiragana + Katakana)
        "hung", // Old Hungarian (Hungarian Runic)
        "inds", // Indus (Harappan)
        "ital", // Old Italic (Etruscan, Oscan, etc.)
        "java", // Javanese
        "jpan", // Japanese (alias for Han + Hiragana + Katakana)
        "jurc", // Jurchen
        "kali", // Kayah Li
        "kana", // Katakana
        "khar", // Kharoshthi
        "khmr", // Khmer
        "khoj", // Khojki
        "kitl", // Khitan large script
        "kits", // Khitan small script
        "knda", // Kannada
        "kore", // Korean (alias for Hangul + Han)
        "kpel", // Kpelle
        "kthi", // Kaithi
        "lana", // Tai Tham (Lanna)
        "laoo", // Lao
        "latf", // Latin (Fraktur variant)
        "latg", // Latin (Gaelic variant)
        "latn", // Latin
        "leke", // Leke
        "lepc", // Lepcha (Róng)
        "limb", // Limbu
        "lina", // Linear A
        "linb", // Linear B
        "lisu", // Lisu (Fraser)
        "loma", // Loma
        "lyci", // Lycian
        "lydi", // Lydian
        "mahj", // Mahajani
        "mand", // Mandaic, Mandaean
        "mani", // Manichaean
        "marc", // Marchen
        "maya", // Mayan hieroglyphs
        "mend", // Mende Kikakui
        "merc", // Meroitic Cursive
        "mero", // Meroitic Hieroglyphs
        "mlym", // Malayalam
        "modi", // Modi, Moḍī
        "mong", // Mongolian
        "moon", // Moon (Moon code, Moon script, Moon type)
        "mroo", // Mro, Mru
        "mtei", // Meitei Mayek (Meithei, Meetei)
        "mult", // Multani
        "mymr", // Myanmar (Burmese)
        "narb", // Old North Arabian (Ancient North Arabian)
        "nbat", // Nabataean
        "nkgb", // Nakhi Geba ('Na-'Khi ²Ggŏ-¹baw, Naxi Geba)
        "nkoo", // N’Ko
        "nshu", // Nüshu
        "ogam", // Ogham
        "olck", // Ol Chiki (Ol Cemet’, Ol, Santali)
        "orkh", // Old Turkic, Orkhon Runic
        "orya", // Oriya
        "osge", // Osage
        "osma", // Osmanya
        "palm", // Palmyrene
        "pauc", // Pau Cin Hau
        "perm", // Old Permic
        "phag", // Phags-pa
        "phli", // Inscriptional Pahlavi
        "phlp", // Psalter Pahlavi
        "phlv", // Book Pahlavi
        "phnx", // Phoenician
        "plrd", // Miao (Pollard)
        "prti", // Inscriptional Parthian
        "qaaa", // Reserved for private use (start)
        "qabx", // Reserved for private use (end)
        "rjng", // Rejang (Redjang, Kaganga)
        "roro", // Rongorongo
        "runr", // Runic
        "samr", // Samaritan
        "sara", // Sarati
        "sarb", // Old South Arabian
        "saur", // Saurashtra
        "sgnw", // SignWriting
        "shaw", // Shavian (Shaw)
        "shrd", // Sharada, Śāradā
        "sidd", // Siddham, Siddhaṃ, Siddhamātṛkā
        "sind", // Khudawadi, Sindhi
        "sinh", // Sinhala
        "sora", // Sora Sompeng
        "sund", // Sundanese
        "sylo", // Syloti Nagri
        "syrc", // Syriac
        "syre", // Syriac (Estrangelo variant)
        "syrj", // Syriac (Western variant)
        "syrn", // Syriac (Eastern variant)
        "tagb", // Tagbanwa
        "takr", // Takri, Ṭākrī, Ṭāṅkrī
        "tale", // Tai Le
        "talu", // New Tai Lue
        "taml", // Tamil
        "tang", // Tangut
        "tavt", // Tai Viet
        "telu", // Telugu
        "teng", // Tengwar
        "tfng", // Tifinagh (Berber)
        "tglg", // Tagalog (Baybayin, Alibata)
        "thaa", // Thaana
        "thai", // Thai
        "tibt", // Tibetan
        "tirh", // Tirhuta
        "ugar", // Ugaritic
        "vaii", // Vai
        "visp", // Visible Speech
        "wara", // Warang Citi (Varang Kshiti)
        "wole", // Woleai
        "xpeo", // Old Persian
        "xsux", // Cuneiform, Sumero-Akkadian
        "yiii", // Yi
        "zinh", // Code for inherited script
        "zmth", // Mathematical notation
        "zsym", // Symbols
        "zxxx", // Code for unwritten documents
        "zyyy", // Code for undetermined script
        "zzzz"  // Code for uncoded script
    };

    private static final Set<String> scripts;
    static {
        Set<String> s = new java.util.HashSet<String>();
        for (String id : iso15924ScriptIdentifiers)
            s.add(id);
        scripts = Collections.unmodifiableSet(s);
    }

    public static boolean isScript(String value, Location location, VerifierContext context, String[] outputScript) {
        String script = value;
        do {
            if (Keywords.isNone(script))
                break;
            else if (Keywords.isAuto(script))
                break;
            else if (isScriptIdentifier(script))
                break;
            else
                return false;
        } while (false);
        if (outputScript != null)
            outputScript[0] = script;
        return true;
    }

    public static void badScript(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(location.getLocator(), "*KEY*", "Bad script expression ''{0}''.", value));
    }

    public static boolean isScriptIdentifier(String s) {
        return scripts.contains(s);
    }

}
