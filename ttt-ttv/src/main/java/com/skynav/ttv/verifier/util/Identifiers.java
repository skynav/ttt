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

import java.util.List;

import org.xml.sax.Locator;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class Identifiers {

    public static boolean isIdentifiers(String value, Location location, VerifierContext context, List<String> outputIdentifiers) {
        List<String> identifiers = new java.util.ArrayList<String>();
        int valueIndex = 0;
        int valueLength = value.length();
        while (valueIndex < valueLength) {
            int identStart;
            if ((identStart = startOfPossibleIdent(value, valueIndex)) >= 0) {
                int identEnd = endOfPossibleIdent(value, identStart);
                String[] identifier = new String[1];
                if (isIdentifier(value.substring(identStart, identEnd), location, context, identifier)) {
                    identifiers.add(identifier[0]);
                    valueIndex = identEnd;
                } else
                    return false;
            }
        }
        if (outputIdentifiers != null) {
            outputIdentifiers.clear();
            outputIdentifiers.addAll(identifiers);
        }
        return true;
    }

    public static void badIdentifiers(String value, Location location, VerifierContext context) {
        int valueIndex = 0;
        int valueLength = value.length();
        while (valueIndex < valueLength) {
            int identStart;
            if ((identStart = startOfPossibleIdent(value, valueIndex)) >= 0) {
                int identEnd = endOfPossibleIdent(value, identStart);
                if (!isIdentifier(value.substring(identStart, identEnd), location, context, null))
                    badIdentifier(value.substring(identStart, identEnd), location, context);
                valueIndex = identEnd;
            }
        }
    }

    private static int startOfPossibleIdent(String value, int start) {
        for (int i = start, n = value.length(); i < n; ++i) {
            char c = value.charAt(i);
            if (!Characters.isXMLSpace(c))
                return i;
        }
        return -1;
    }

    private static int endOfPossibleIdent(String value, int start) {
        for (int i = start, n = value.length(); i < n; ++i) {
            char c = value.charAt(i);
            if (c == '\\') {
                if (i < n - 1) {
                    ++i;
                    continue;
                }
            }
            if (Characters.isXMLSpace(c))
                return i;
        }
        return value.length();
    }

    private static boolean isIdentifier(String value, Location location, VerifierContext context, String[] outputIdentifier) {
        int valueIndex = 0;
        int valueLength = value.length();

        do {
            char c;

            // optional hyphen before ident-start
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '-')
                ++valueIndex;

            // ident-start
            if (valueIndex == valueLength)
                return false;
            c = value.charAt(valueIndex);
            if (c == '\\') {
                if (valueIndex + 1 == valueLength)
                    return false;
                else
                    valueIndex += 2;
            } else if (isIdentStart(c)) {
                ++valueIndex;
            } else
                return false;

            // ident-following*
            while (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (c == '\\') {
                    if (valueIndex + 1 == valueLength)
                        return false;
                    else
                        valueIndex += 2;
                } else if (isIdentFollowing(c)) {
                    ++valueIndex;
                } else
                    break;
            }

            // don't allow subsequent characters that are not ident-following
            if (valueIndex < valueLength)
                return false;

        } while (false);

        if (isReservedKeyword(value))
            return false;

        if (outputIdentifier != null)
            outputIdentifier[0] = value;
        return true;
    }

    public static void badIdentifier(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        int valueIndex = 0;
        int valueLength = value.length();

        do {
            char c;

            // optional hyphen before ident-start
            if (valueIndex == valueLength)
                break;
            c = value.charAt(valueIndex);
            if (c == '-')
                ++valueIndex;

            // ident-start
            if (valueIndex == valueLength) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad identifier in <familyName> expression, missing ident-start."));
                break;
            }
            c = value.charAt(valueIndex);
            if (c == '\\') {
                if (valueIndex + 1 == valueLength) {
                    reporter.logInfo(reporter.message(locator,
                        "*KEY*", "Bad identifier in <familyName> expression, incomplete escape in ident-start."));
                    valueIndex = valueLength;
                } else
                    valueIndex += 2;
            } else if (isIdentStart(c)) {
                ++valueIndex;
            } else {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad identifier in <familyName> expression, unexpected ident-start character ''{0}''.", c));
                valueIndex = valueLength;
            }

            // ident-following*
            while (valueIndex < valueLength) {
                c = value.charAt(valueIndex);
                if (c == '\\') {
                    if (valueIndex + 1 == valueLength) {
                        reporter.logInfo(reporter.message(locator,
                            "*KEY*", "Bad identifier in <familyName> expression, incomplete escape in ident-following."));
                        valueIndex = valueLength;
                    } else
                        valueIndex += 2;
                } else if (isIdentFollowing(c)) {
                    ++valueIndex;
                } else {
                    reporter.logInfo(reporter.message(locator,
                        "*KEY*", "Bad identifier in <familyName> expression, unexpected ident-following character ''{0}''.", c));
                    valueIndex = valueLength;
                }
            }

            // don't allow subsequent characters that are not ident-following
            if (valueIndex < valueLength)
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad identifier in <familyName> expression, unexpected character ''{0}'' following last ident-following character.", c));

        } while (false);

        if (isReservedKeyword(value)) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad identifier in <familyName> expression, reserved keyword ''{0}'' used.", value));
        }
    }

    private static boolean isReservedKeyword(String value) {
        if (value.equals("inherit"))
            return true;
        else if (value.equals("initial"))
            return true;
        else
            return false;
    }

    public static String joinIdentifiersUnescaping(List<String> identifiers) {
        StringBuffer sb = new StringBuffer();
        for (String identifier : identifiers) {
            if (sb.length() > 0)
                sb.append(' ');
            sb.append(Strings.unescape(identifier));
        }
        return sb.toString();
    }

    public static boolean isIdentStart(char c) {
        return Characters.isLetter(c) || (c == '_') || ((int) c > 0x9F);
    }

    public static boolean isIdentFollowing(char c) {
        return isIdentStart(c) || Characters.isDigit(c) || (c == '-');
    }

}
