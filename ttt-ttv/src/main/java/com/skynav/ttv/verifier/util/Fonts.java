/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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

import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.model.value.Font;
import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.model.value.FontVariant;
import com.skynav.ttv.model.value.GenericFontFamily;
import com.skynav.ttv.model.value.impl.EmbeddedFontImpl;
import com.skynav.ttv.model.value.impl.ExternalFontImpl;
import com.skynav.ttv.model.value.impl.FontFamilyImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.URIs;
import com.skynav.ttv.verifier.ItemVerifier.ItemType;
import com.skynav.ttv.verifier.VerifierContext;

public class Fonts {

    public static boolean isFontFamily(String value, Location location, VerifierContext context, Object[] treatments, FontFamily[] outputFamily) {
        if (isGenericFontFamily(value, location, context, treatments, outputFamily))
            return true;
        else if (isUnquotedFontFamily(value, location, context, treatments, outputFamily))
            return true;
        else if (isQuotedFontFamily(value, location, context, treatments, outputFamily))
            return true;
        else
            return false;
    }

    public static boolean isGenericFontFamily(String value, Location location, VerifierContext context, Object[] treatments, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        FontFamily family = FontFamilyImpl.getGenericFamily(trimmedValue);
        if (family != null) {
            if (outputFamily != null)
                outputFamily[0] = family;
            return true;
        } else
            return false;
    }

    public static boolean isUnquotedFontFamily(String value, Location location, VerifierContext context, Object[] treatments, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0)
            return false;
        else {
            List<String> identifiers = new java.util.ArrayList<String>();
            if (Identifiers.isIdentifiers(trimmedValue, location, context, identifiers)) {
                if (outputFamily != null)
                    outputFamily[0] = new FontFamilyImpl(FontFamily.Type.Unquoted, Identifiers.joinIdentifiersUnescaping(identifiers));
                return true;
            } else
                return false;
        }
    }

    public static boolean isQuotedFontFamily(String value, Location location, VerifierContext context, Object[] treatments, FontFamily[] outputFamily) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0)
            return false;
        else {
            Reporter reporter = context.getReporter();
            String[] string = new String[1];
            if (Strings.isDoubleQuotedString(trimmedValue, location, context, string) || Strings.isSingleQuotedString(trimmedValue, location, context, string)) {
                String stringContent = Strings.unescapeUnquoted(string[0]);
                if (treatments != null) {
                    if (GenericFontFamily.isToken(stringContent)) {
                        QuotedGenericFontFamilyTreatment quotedGenericTreatment = (QuotedGenericFontFamilyTreatment) treatments[0];
                        Message message = reporter.message(location.getLocator(), "*KEY*",
                            "Quoted <familyName> expression is a generic font family, but will be treated as a non-generic family name.");
                        if (quotedGenericTreatment == QuotedGenericFontFamilyTreatment.Warning) {
                            if (reporter.logWarning(message)) {
                                treatments[0] = QuotedGenericFontFamilyTreatment.Allow;     // suppress second warning
                                return false;
                            }
                        } else if (quotedGenericTreatment == QuotedGenericFontFamilyTreatment.Info) {
                            reporter.logInfo(message);
                        }
                    }
                }
                if (outputFamily != null)
                    outputFamily[0] = new FontFamilyImpl(FontFamily.Type.Quoted, stringContent);
                return true;
            } else
                return false;
        }
    }

    public static void badFontFamily(String value, Location location, VerifierContext context, Object[] treatments) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0) {
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message(location.getLocator(),
                "*KEY*", "Bad <familyName> of <genericFamilyName> expression, value is empty or only XML space characters."));
        } else {
            char c = trimmedValue.charAt(0);
            if ((c != '\"') && (c != '\''))
                badUnquotedFontFamily(trimmedValue, location, context, treatments);
            else
                badQuotedFontFamily(trimmedValue, location, context, treatments);
        }
    }

    public static void badUnquotedFontFamily(String value, Location location, VerifierContext context, Object[] treatments) {
        assert value.length() > 0;
        Identifiers.badIdentifiers(value, location, context);
    }

    public static void badQuotedFontFamily(String value, Location location, VerifierContext context, Object[] treatments) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        assert value.length() > 0;
        char quote = value.charAt(0);
        int valueIndex = 1;
        int valueLength = value.length();
        char c = 0;
        while (valueIndex < valueLength) {
            c = value.charAt(valueIndex++);
            if (c == '\\') {
                if (valueIndex == valueLength) {
                    reporter.logInfo(reporter.message(locator,
                        "*KEY*", "Bad quoted string in <familyName> expression, incomplete escape."));
                    return;
                } else
                    ++valueIndex;
            } else if (c == quote)
                break;
        }
        if (c != quote) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad quoted string in <familyName> expression, not terminated."));
        } else if (valueIndex < valueLength) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad quoted string in <familyName> expression, unrecognized characters following string, got ''{0}''.",
                value.substring(valueIndex)));
        } else if (valueLength < 3) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad quoted string in <familyName> expression, empty string."));
        }
    }

    public static boolean isFontFamilies(String value, Location location, VerifierContext context, Object[] treatments, List<FontFamily> outputFamilies) {
        List<FontFamily> families = new java.util.ArrayList<FontFamily>();
        String [] familyItems = splitFontFamilies(value);
        for (String item : familyItems) {
            FontFamily[] family = new FontFamily[1];
            if (isFontFamily(item, location, context, treatments, family))
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

    public static void badFontFamilies(String value, Location location, VerifierContext context, Object[] treatments) {
        String [] familyItems = splitFontFamilies(value);
        Object[] treatmentsInner = (treatments != null) ? new Object[] { treatments[0] } : null;
        for (String item : familyItems) {
            if (!isFontFamily(item, location, context, treatmentsInner, null))
                badFontFamily(item, location, context, treatmentsInner);
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

    public static boolean isFontVariant(String value, Location location, VerifierContext context, FontVariant[] outputVariant) {
        String trimmedValue = value.trim();
        if (trimmedValue.length() == 0)
            return false;
        else {
            try {
                FontVariant fv = FontVariant.fromValue(trimmedValue);
                if (outputVariant != null)
                    outputVariant[0] = fv;
                return true;
            } catch (IllegalArgumentException e) {
                return false;
            }
        }
    }

    public static void badFontVariant(String value, Location location, VerifierContext context) {
        String trimmedValue = value.trim();
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        if (trimmedValue.length() == 0) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad token of <font-variant> expression, value is empty or only XML space characters."));
        } else {
            try {
                FontVariant.fromValue(trimmedValue);
            } catch (IllegalArgumentException e) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Bad token of <font-variant> expression, unknown token ''{0}''.", trimmedValue));
            }
        }
    }

    public static boolean isFontVariants(String value, Location location, VerifierContext context, Set<FontVariant> outputVariants) {
        Set<FontVariant> variants = new java.util.HashSet<FontVariant>();
        String [] variantTokens = splitFontVariants(value);
        for (String token : variantTokens) {
            FontVariant[] variant = new FontVariant[1];
            if (isFontVariant(token, location, context, variant)) {
                if (variants.contains(variant[0]))
                    return false;
                else
                    variants.add(variant[0]);
            }
            else
                return false;
        }
        if (variants.contains(FontVariant.NORMAL) && (variants.size() > 1)) {
            return false;                       // if includes normal, then cannot include another token
        } else if (variants.contains(FontVariant.SUPER) && variants.contains(FontVariant.SUB)) {
            return false;                       // cannot include both super and sub
        } else if (variants.contains(FontVariant.HALF) && variants.contains(FontVariant.FULL)) {
            return false;                       // cannot include both half and full
        }
        if (outputVariants != null) {
            outputVariants.clear();
            outputVariants.addAll(variants);
        }
        return true;
    }

    public static void badFontVariants(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        Set<FontVariant> variants = new java.util.HashSet<FontVariant>();
        String [] variantTokens = splitFontVariants(value);
        for (String token : variantTokens) {
            FontVariant[] variant = new FontVariant[1];
            if (!isFontVariant(token, location, context, variant)) {
                badFontVariant(token, location, context);
            } else if (variants.contains(variant[0])) {
                reporter.logInfo(reporter.message(locator,
                    "*KEY*", "Duplicate token of <font-variant> expression ''{0}''.", token.trim()));
            } else {
                variants.add(variant[0]);
            }
        }
        if (variants.contains(FontVariant.NORMAL) && (variants.size() > 1)) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad <font-variant> expression ''{0}'', no other token permitted when ''normal'' is present.", value));
        } else if (variants.contains(FontVariant.SUPER) && variants.contains(FontVariant.SUB)) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad <font-variant> expression ''{0}'', cannot include both ''super'' and ''sub''.", value));
        } else if (variants.contains(FontVariant.HALF) && variants.contains(FontVariant.FULL)) {
            reporter.logInfo(reporter.message(locator,
                "*KEY*", "Bad <font-variant> expression ''{0}'', cannot include both ''halfWidth'' and ''fullWidth''.", value));
        }
    }

    private static String[] splitFontVariants(String value) {
        return value.split("\\s+");
    }

    public static boolean isFont(String value, Location location, VerifierContext context, Font[] outputFont) {
        return isFont(value, location, context, true, outputFont);
    }

    public static boolean isFont(String value, Location location, VerifierContext context, boolean verifyContent, Font[] outputFont) {
        Font font;
        if ((outputFont != null) && (outputFont.length < 1))
            throw new IllegalArgumentException();
        if (URIs.isLocalFragment(value)) {
            String id = URIs.getFragment(value);
            assert id != null;
            Object content = location.getContent();
            Node node = content != null ? context.getXMLNode(content) : null;
            if ((node == null) && (content instanceof Element))
                node = (Element) content;
            if (node != null) {
                Document document = node.getOwnerDocument();
                if (document != null) {
                    Element targetElement = document.getElementById(id);
                    if (targetElement != null) {
                        Object target = context.getBindingElement(targetElement);
                        if (target != null) {
                            QName targetName = context.getBindingElementName(target);
                            if (!isFontElement(targetName))
                                return false;                                   // target must be an font element
                            else {
                                URI base = location.getSystemIdAsURI();
                                URI uri = URIs.makeURISafely("#" + id, base);
                                font = internalFont(uri, target);               // good local reference (though not necessarily resolvable)
                            }
                        } else
                            throw new IllegalStateException();                  // no binding for target element (illegal state)
                    } else
                        return false;                                           // no target element for given fragment identifier
                } else
                    throw new IllegalStateException();                          // no owner document (illegal state)
            } else
                throw new IllegalStateException();                              // no node (illegal state)
        } else if (value.trim().length() == value.length()) {
            URI base = location.getSystemIdAsURI();
            URI uri = URIs.makeURISafely(value, base);
            if (uri == null)
                return false;                                                   // bad syntax
            else
                font = externalFont(uri);                                       // good non-local reference (though not necessarily resolvable)
        } else if (value.length() > 0) {
            return false;                                                       // font reference is all LWSP or has LWSP padding
        } else {
            return false;                                                       // font reference is empty string
        }
        // record specified type and format
        font.setSpecifiedType(null);                                            // [TBD] populate specified type from content
        font.setSpecifiedFormat(null);                                          // [TBD] populate speicifed format from content
        // verify font content
        assert font != null;
        if (verifyContent && !context.getModel().getFontVerifier().verify(font, location.getLocator(), context, ItemType.Other))
            return false;
        // return font instance if requested
        if (outputFont != null) {
            assert outputFont.length > 0;
            outputFont[0] = font;
        }
        return true;
    }

    private static Font internalFont(URI uri, Object target) {
        return new EmbeddedFontImpl(uri, target);
    }

    private static Font externalFont(URI uri) {
        return new ExternalFontImpl(uri);
    }

    public static void badFont(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Message m = null;
        Locator locator = location.getLocator();
        if (URIs.isLocalFragment(value)) {
            String id = URIs.getFragment(value);
            assert id != null;
            Object content = location.getContent();
            Node node = content != null ? context.getXMLNode(content) : null;
            if ((node == null) && (content instanceof Element))
                node = (Element) content;
            if (node != null) {
                Document document = node.getOwnerDocument();
                if (document != null) {
                    Element targetElement = document.getElementById(id);
                    if (targetElement != null) {
                        Object target = context.getBindingElement(targetElement);
                        if (target != null) {
                            QName targetName = context.getBindingElementName(target);
                            if (!isFontElement(targetName)) {                   // target must be an font element
                                m = reporter.message(locator, "*KEY*",
                                    "Bad <font> expression, got ''{0}'', target must be an font element.", value);
                            }
                        }
                    } else {                                                    // no target element for given fragment identifier
                        m = reporter.message(locator, "*KEY*",
                            "Bad <font> expression, got ''{0}'', no target element for given fragment identifier.", value);
                    }
                }
            }
        } else if (value.trim().length() == value.length()) {
            URI base = location.getSystemIdAsURI();
            URI uri = URIs.makeURISafely(value, base);
            if (uri == null) {                                                  // bad syntax
                m = reporter.message(locator, "*KEY*",
                    "Bad <font> expression, got ''{0}'', illegal syntax.", value);
            }
        } else if (value.length() > 0) {                                        // font reference is all LWSP or has LWSP padding
            m = reporter.message(locator, "*KEY*",
                "Bad <font> expression, got ''{0}'', all LWSP or has LWSP padding.", value);
        } else {                                                                // font reference is empty string
            m = reporter.message(locator, "*KEY*",
                "Bad <font> expression, got ''{0}'', empty string.", value);
        }
        if (m != null)
            reporter.logError(m);
    }

    private static final QName fontElementName = new QName(TTML.Constants.NAMESPACE_TT, "font");
    private static boolean isFontElement(QName name) {
        return name.equals(fontElementName);
    }

}
