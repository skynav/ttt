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

import java.util.List;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.model.value.GenericFontFamily;
import com.skynav.ttv.model.value.impl.FontFamilyImpl;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.util.NullErrorReporter;

public class Fonts {

    public static boolean isFontFamily(String value, Locator locator, ErrorReporter errorReporter, FontFamily[] outputFamily) {
        if (isGenericFontFamily(value, locator, errorReporter, outputFamily))
            return true;
        else if (isUnquotedFontFamily(value, locator, errorReporter, outputFamily))
            return true;
        else if (isQuotedFontFamily(value, locator, errorReporter, outputFamily))
            return true;
        else
            return false;
    }

    private static boolean isGenericFontFamily(String value, Locator locator, ErrorReporter errorReporter, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        FontFamily family = FontFamilyImpl.getGenericFamily(trimmedValue);
        if (family != null) {
            if (outputFamily != null)
                outputFamily[0] = family;
            return true;
        } else
            return false;
    }

    private static boolean isUnquotedFontFamily(String value, Locator locator, ErrorReporter errorReporter, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0)
            return false;
        else {
            List<String> identifiers = new java.util.ArrayList<String>();
            if (Identifiers.isIdentifiers(trimmedValue, locator, errorReporter, identifiers)) {
                if (outputFamily != null)
                    outputFamily[0] = new FontFamilyImpl(FontFamily.Type.Unquoted, Identifiers.joinIdentifiersUnescaping(identifiers));
                return true;
            } else
                return false;
        }
    }

    private static boolean isQuotedFontFamily(String value, Locator locator, ErrorReporter errorReporter, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0)
            return false;
        else {
            String[] string = new String[1];
            if (Strings.isDoubleQuotedString(trimmedValue, locator, errorReporter, string) || Strings.isSingleQuotedString(trimmedValue, locator, errorReporter, string)) {
                String stringContent = Strings.unescapeUnquoted(string[0]);
                if (GenericFontFamily.isToken(stringContent)) {
                    errorReporter.logInfo(locator, "Quoted <familyName> expression is a generic font family, but will be treated as a non-generic, family name");
                }
                if (outputFamily != null)
                    outputFamily[0] = new FontFamilyImpl(FontFamily.Type.Quoted, stringContent);
                return true;
            } else
                return false;
        }
    }

    public static void badFontFamily(String value, Locator locator, ErrorReporter errorReporter) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0) {
            errorReporter.logInfo(locator, "Bad <familyName> of <genericFamilyName> expression, value is empty or only XML space characters.");
        } else {
            char c = trimmedValue.charAt(0);
            if ((c != '\"') && (c != '\''))
                badUnquotedFontFamily(trimmedValue, locator, errorReporter);
            else
                badQuotedFontFamily(trimmedValue, locator, errorReporter);
        }
    }

    public static void badUnquotedFontFamily(String value, Locator locator, ErrorReporter errorReporter) {
        assert value.length() > 0;
        Identifiers.badIdentifiers(value, locator, errorReporter);
    }

    public static void badQuotedFontFamily(String value, Locator locator, ErrorReporter errorReporter) {
        assert value.length() > 0;
        char quote = value.charAt(0);
        int valueIndex = 1;
        int valueLength = value.length();
        char c = 0;
        while (valueIndex < valueLength) {
            c = value.charAt(valueIndex++);
            if (c == '\\') {
                if (valueIndex == valueLength) {
                    errorReporter.logInfo(locator, "Bad quoted string in <familyName> expression, incomplete escape.");
                    return;
                } else
                    ++valueIndex;
            } else if (c == quote)
                break;
        }
        if (c != quote)
            errorReporter.logInfo(locator, "Bad quoted string in <familyName> expression, not terminated.");
        else if (valueIndex < valueLength) {
            errorReporter.logInfo(locator,
                "Bad quoted string in <familyName> expression, unrecognized characters following string, got '" + value.substring(valueIndex) + "'.");
        } else if (valueLength < 3) {
            errorReporter.logInfo(locator, "Bad quoted string in <familyName> expression, empty string.");
        }
    }

    public static boolean isFontFamilies(String value, Locator locator, ErrorReporter errorReporter, List<FontFamily> outputFamilies) {
        List<FontFamily> families = new java.util.ArrayList<FontFamily>();
        String [] familyItems = splitFontFamilies(value);
        for (String item : familyItems) {
            FontFamily[] family = new FontFamily[1];
            if (isFontFamily(item, locator, errorReporter, family))
                families.add(family[0]);
            else
                return false;
        }
        if (outputFamilies != null) {
            outputFamilies.clear();
            outputFamilies.addAll(families);
        }
        return true;
    }

    public static void badFontFamilies(String value, Locator locator, ErrorReporter errorReporter) {
        String [] familyItems = splitFontFamilies(value);
        for (String item : familyItems) {
            if (!isFontFamily(item, locator, NullErrorReporter.Reporter, null))
                badFontFamily(item, locator, errorReporter);
        }
    }

    private static String[] splitFontFamilies(String value) {
        List<String> items = new java.util.ArrayList<String>();
        int valueIndex = 0;
        int valueLength = value.length();
        int itemStart = valueIndex;
        while (true) {
            char c = 0;
            char quote = 0;
            while (valueIndex < valueLength) {
                c = value.charAt(valueIndex++);
                if (c == '\\') {
                    if (valueIndex == valueLength)
                        break;
                    else
                        valueIndex++;
                } else if ((c == '\'') || (c == '\"')) {
                    if (quote == 0)
                        quote = c;
                    else if (c == quote)
                        quote = 0;
                } else if ((c == ',') && (quote == 0)) {
                    break;
                }
            }
            if (valueIndex > itemStart)
                items.add(value.substring(itemStart, c == ',' ? valueIndex - 1 : valueIndex));
            itemStart = valueIndex;
            if (valueIndex >= valueLength) {
                if (c == ',')
                    items.add("");
                break;
            }
        }
        return items.toArray(new String[items.size()]);
    }

}
