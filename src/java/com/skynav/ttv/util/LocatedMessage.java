/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttv.util;

import org.xml.sax.Locator;

public class LocatedMessage extends Message {
    private String uri;
    private int line = -1;
    private int column = -1;
    public LocatedMessage(Locator locator, String key, String format, Object... arguments) {
        this(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber(), key, format, arguments);
    }
    public LocatedMessage(String uri, int line, int column, String key, String format, Object... arguments) {
        super(key, format, arguments);
        this.uri = uri;
        this.line = line;
        this.column = column;
    }
    @Override
    public String toText(boolean hideLocation, boolean hidePath) {
        String text = super.toText();
        if (uri != null) {
            StringBuffer sb = new StringBuffer();
            if (!hideLocation) {
                sb.append('{');
                String uriString = uri.toString();
                if (hidePath)
                    uriString = hidePath(uriString);
                sb.append(uriString);
                sb.append('}');
            }
            if (line >= 0) {
                if (sb.length() > 0)
                    sb.append(':');
                sb.append('[');
                sb.append(line);
                if (column >= 0) {
                    sb.append(',');
                    sb.append(column);
                }
                sb.append(']');
            }
            if (sb.length() > 0)
                sb.append(':');
            sb.append(text);
            text = sb.toString();
        }
        return text;
    }
    public String toXML(boolean hideLocation, boolean hidePath) {
        StringBuffer sb = new StringBuffer();
        sb.append("<message>\n");
        sb.append(toXMLLocation(hideLocation, hidePath));
        sb.append(toXMLKey());
        sb.append(toXMLText());
        sb.append("</message>\n");
        return sb.toString();
    }
    private String toXMLLocation(boolean hideLocation, boolean hidePath) {
        StringBuffer sb = new StringBuffer();
        if (uri != null) {
            sb.append("<location>\n");
            if (!hideLocation) {
                sb.append("<url>");
                String uriString = uri.toString();
                if (hidePath)
                    uriString = hidePath(uriString);
                sb.append(escapeText(uriString));
                sb.append("</url>\n");
            }
            if (line >= 0) {
                sb.append("<line>");
                sb.append(line);
                sb.append("</line>\n");
                if (column >= 0) {
                    sb.append("<column>");
                    sb.append(column);
                    sb.append("</column>\n");
                }
            }
            sb.append("</location>\n");
        }
        return sb.toString();
    }
    private String hidePath(String uriString) {
        if (uriString != null) {
            int index = uriString.lastIndexOf("/");
            if (index >= 0)
                uriString = uriString.substring(index + 1);
        }
        return uriString;
    }
}
