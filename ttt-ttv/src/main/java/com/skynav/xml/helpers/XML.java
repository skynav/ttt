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
 
package com.skynav.xml.helpers;

import javax.xml.namespace.QName;

public class XML {

    public static final String xmlNamespace = "http://www.w3.org/XML/1998/namespace";
    public static final String xmlnsNamespace = "http://www.w3.org/2000/xmlns/";
    public static final String xsiNamespace = "http://www.w3.org/2001/XMLSchema-instance";

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

    public static String getXSINamespaceUri() {
        return xsiNamespace;
    }

    public static boolean isXSINamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(getXSINamespaceUri());
    }

    public static boolean isXMLNSNamespace(String nsUri) {
        return (nsUri != null) && nsUri.equals(xmlnsNamespace);
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
        return escapeMarkup(s, false);
    }

    public static String escapeMarkup(String s, boolean escapeSpaceAsNBSP) {
        if (s == null)
            return null;
        else {
            StringBuffer sb = new StringBuffer(s.length());
            for (int i = 0, n = s.length(); i < n; ++i) {
                int c = s.codePointAt(i);
                if ((c > 65535)) {
                    appendNumericCharReference(sb, c);
                    ++i;
                } else if (Character.isISOControl(c) && !Character.isWhitespace(c))
                    appendNumericCharReference(sb, c);
                else if (c == '<')
                    appendNamedCharReference(sb, "lt");
                else if (c == '>')
                    appendNamedCharReference(sb, "gt");
                else if (c == '&')
                    appendNamedCharReference(sb, "amp");
                else if (c == '"')
                    appendNamedCharReference(sb, "quot");
                else if (c == '\'')
                    appendNamedCharReference(sb, "apos");
                else if ((c == ' ') && escapeSpaceAsNBSP)
                    appendNumericCharReference(sb, 0x00A0);
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
        sb.append(Integer.toString(codepoint, 16));
        sb.append(';');
    }

    private static void appendNamedCharReference(StringBuffer sb, String name) {
        sb.append('&');
        sb.append(name);
        sb.append(';');
    }

}
