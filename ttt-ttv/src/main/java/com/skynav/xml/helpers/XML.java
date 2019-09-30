/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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

package com.skynav.xml.helpers;

import javax.xml.namespace.QName;

public class XML {

    public static final String xmlNamespace                     = "http://www.w3.org/XML/1998/namespace";
    public static final String xmlnsNamespace                   = "http://www.w3.org/2000/xmlns/";
    public static final String xsiNamespace                     = "http://www.w3.org/2001/XMLSchema-instance";
    public static final String xlinkNamespace                   = "http://www.w3.org/1999/xlink";

    public static final String xmlPrefix                        = "xml";
    public static final String xmlnsPrefix                      = "xmlns";
    public static final String xsiPrefix                        = "xsi";

    public static final QName xmlIdentifierAttributeName        = new QName(xmlNamespace, "id");

    private XML() {
    }

    public static String getNamespaceUri() {
        return xmlNamespace;
    }

    public static boolean isXMLNamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(getNamespaceUri());
    }

    private static final QName xmlBaseName = new QName(getNamespaceUri(), "base");
    public static QName getBaseAttributeName() {
        return xmlBaseName;
    }

    private static final QName xmlIdName = new QName(getNamespaceUri(), "id");
    public static QName getIdAttributeName() {
        return xmlIdName;
    }

    private static final QName xlinkHrefName = new QName(getXlinkNamespaceUri(), "href");
    public static QName getXlinkHrefAttributeName() {
        return xlinkHrefName;
    }

    public static String getXSINamespaceUri() {
        return xsiNamespace;
    }

    public static boolean isXSINamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(getXSINamespaceUri());
    }

    public static boolean isXMLNSNamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(xmlnsNamespace);
    }

    public static String getXlinkNamespaceUri() {
        return xlinkNamespace;
    }

    public static boolean isXlinkNamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(getXlinkNamespaceUri());
    }

    public static boolean inXlinkNamespace(QName name) {
        return isXlinkNamespace(name.getNamespaceURI());
    }

    public static boolean isAnyXMLNamespace(String nsUri) {
        if (isXMLNamespace(nsUri))
            return true;
        else if (isXMLNSNamespace(nsUri))
            return true;
        else
            return false;
    }

    private static final QName xsiSchemaLocationName = new QName(getXSINamespaceUri(), "schemaLocation");
    public static QName getSchemaLocationAttributeName() {
        return xsiSchemaLocationName;
    }

    public static String escapeMarkup(String s) {
        return escapeMarkup(s, false, false);
    }

    public static String escapeMarkup(String s, boolean escapeSpaceAsNBSP) {
        return escapeMarkup(s, false, escapeSpaceAsNBSP);
    }

    public static String escapeMarkup(String s, boolean escapeWhitespace, boolean escapeSpaceAsNBSP) {
        if (s == null)
            return null;
        else {
            StringBuffer sb = new StringBuffer(s.length());
            for (int i = 0, n = s.length(); i < n; ++i) {
                int c = s.codePointAt(i);
                if ((c > 65535)) {
                    appendNumericCharReference(sb, c);
                    ++i;
                } else if (Character.isISOControl(c) && (escapeWhitespace || !Character.isWhitespace(c))) {
                    appendNumericCharReference(sb, c);
                } else if (c == ' ') {
                    if (escapeWhitespace)
                        appendNumericCharReference(sb, ' ');
                    else if (escapeSpaceAsNBSP)
                        appendNumericCharReference(sb, 0x00A0);
                    else
                        sb.append((char) c);
                } else if (c == '<')
                    appendNamedCharReference(sb, "lt");
                else if (c == '>')
                    appendNamedCharReference(sb, "gt");
                else if (c == '&')
                    appendNamedCharReference(sb, "amp");
                else if (c == '"')
                    appendNamedCharReference(sb, "quot");
                else if (c == '\'')
                    appendNamedCharReference(sb, "apos");
                else {
                    assert c < 65536;
                    sb.append((char) c);
                }
            }
            return sb.toString();
        }
    }

    private static void appendNumericCharReference(StringBuffer sb, int codepoint) {
        sb.append("&#x");
        String s = Integer.toString(codepoint, 16).toUpperCase();
        for (int i = 0, n = (6 - s.length()) % 2; i < n; ++i)
            sb.append('0');
        sb.append(s);
        sb.append(';');
    }

    private static void appendNamedCharReference(StringBuffer sb, String name) {
        sb.append('&');
        sb.append(name);
        sb.append(';');
    }

    public static boolean isNCNameCharStart(char c) {
        if (c == '_')
            return true;
        else
            return isLetter(c);
    }

    public static boolean isNCNameCharPart(char c) {
        if (c == '_')
            return true;
        else if (c == '-')
            return true;
        else if (c == '.')
            return true;
        else if (isLetter(c))
            return true;
        else if (isDigit(c))
            return true;
        else if (isCombiningChar(c))
            return true;
        else if (isExtender(c))
            return true;
        else
            return false;
    }

    public static boolean isLetter(char c) {
        if (isBaseChar(c))
            return true;
        else if (isIdeographic(c))
            return true;
        else
            return false;
    }

    public static boolean isBaseChar(char c) {
        if ((c >= 0x0041) && (c <= 0x005A))
            return true;
        else if ((c >= 0x0061) && (c <= 0x007A))
            return true;
        else    // [TBD] include all base chars per XML 1.0
            return false;
    }

    public static boolean isIdeographic(char c) {
        if (c == 0x3007)
            return true;
        else if ((c >= 0x3021) && (c <= 0x3029))
            return true;
        else if ((c >= 0x4E00) && (c <= 0x9FA5))
            return true;
        else
            return false;
    }

    public static boolean isDigit(char c) {
        if ((c >= 0x0030) && (c <= 0x0039))
            return true;
        else    // [TBD] include all digit chars per XML 1.0
            return false;
    }

    public static boolean isCombiningChar(char c) {
        if ((c >= 0x0300) && (c <= 0x0345))
            return true;
        else if ((c >= 0x0360) && (c <= 0x0361))
            return true;
        else    // [TBD] include all combining chars per XML 1.0
            return false;
    }

    public static boolean isExtender(char c) {
        if (c == 0x00B7)
            return true;
        else if (c == 0x02D0)
            return true;
        else if (c == 0x02D1)
            return true;
        else if (c == 0x0387)
            return true;
        else if (c == 0x0640)
            return true;
        else if (c == 0x0E46)
            return true;
        else if (c == 0x0EC6)
            return true;
        else if (c == 0x3005)
            return true;
        else if ((c >= 0x3031) && (c <= 0x3035))
            return true;
        else if ((c >= 0x309D) && (c <= 0x309E))
            return true;
        else if ((c >= 0x30FC) && (c <= 0x30FE))
            return true;
        else
            return false;
    }

}
