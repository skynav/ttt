/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Strings {

    public static boolean isDoubleQuotedString(String value, Location location, VerifierContext context, String[] outputString) {
        return isQuotedString(value, location, context, '\"', outputString);
    }

    public static boolean isSingleQuotedString(String value, Location location, VerifierContext context, String[] outputString) {
        return isQuotedString(value, location, context, '\'', outputString);
    }

    private static boolean isQuotedString(String value, Location location, VerifierContext context, char quote, String[] outputString) {
        if (value.length() < 2)
            return false;
        else if (value.charAt(0) != quote)
            return false;
        else {
            int valueIndex = 1;
            int valueLength = value.length();
            char c = 0;
            while (valueIndex < valueLength) {
                c = value.charAt(valueIndex++);
                if (c == '\\') {
                    if (valueIndex == valueLength)
                        return false;
                    else
                        ++valueIndex;
                } else if (c == quote)
                    break;
            }
            if ((c != quote) || (valueIndex < valueLength))
                return false;
            else if (value.length() < 3) {
                return false;
            } else {
                if (outputString != null)
                    outputString[0] = value;
                return true;
            }
        }
    }

    public static String unescape(String string) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = string.length(); i < n; ++i) {
            char c = string.charAt(i);
            if ((c == '\\') && (i < n - 1))
                c = string.charAt(++i);
            sb.append(c);
        }
        return sb.toString();
    }

    public static String unquote(String string) {
        if (string.length() >= 2) {
            char c = string.charAt(0);
            if ((c == '\"') || (c == '\'')) {
                if (string.charAt(string.length() - 1) == c)
                    return string.substring(1,string.length() - 1);
            }
        }
        return string;
    }

    public static String unescapeUnquoted(String string) {
        return unescape(unquote(string));
    }

    public static boolean containsXMLSpace(String value) {
        for (int i = 0, n = value.length(); i < n; ++i) {
            if (Characters.isXMLSpace(value.charAt(i)))
                return true;
        }
        return false;
    }

    public static boolean isAllXMLSpace(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!Characters.isXMLSpace(value.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isDigits(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!Characters.isDigit(value.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isHexDigits(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!Characters.isHexDigit(value.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean isLetters(String value) {
        int length = value.length();
        if (length == 0)
            return false;
        for (int i = 0; i < length; ++i) {
            if (!Characters.isLetter(value.charAt(i)))
                return false;
        }
        return true;
    }

    public static boolean containsDecimalSeparator(String number) {
        for (int i = 0, n = number.length(); i < n; ++i) {
            if (number.charAt(i) == '.')
                return true;
        }
        return false;
    }

}
