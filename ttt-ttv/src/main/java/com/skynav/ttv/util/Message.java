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

import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    private String key;
    private String format;
    private Object[] arguments;
    public Message(String key, String format, Object... arguments) {
        this.key = key;
        this.format = format;
        this.arguments = arguments.clone();
    }
    public String getKey() {
        return key;
    }
    public String getFormat() {
        return format;
    }
    public Object[] getArguments() {
        return arguments;
    }
    public String toText() {
        return toText(null, false, false);
    }
    public String toText(ResourceBundle bundle) {
        return toText(bundle, false, false);
    }
    public String toText(ResourceBundle bundle, boolean hideLocation, boolean hidePath) {
        String format = ((bundle != null) && (this.key != null)) ? bundle.getString(this.key) : null;
        if (format == null)
            format = this.format;
        return MessageFormat.format(format, this.arguments);
    }
    public String toXML() {
        return toXML(null, false, false, false);
    }
    public String toXML(ResourceBundle bundle, boolean hideLocation, boolean hidePath, boolean showSource) {
        StringBuffer sb = new StringBuffer();
        sb.append("<message>\n");
        sb.append(toXMLKey());
        sb.append(toXMLText(bundle));
        sb.append("</message>\n");
        return sb.toString();
    }
    private static final String placeholderKey = "*KEY*";
    private static final String placeholderNoKey = "*NOKEY*";
    public String toXMLKey() {
        StringBuffer sb = new StringBuffer();
        String key = this.key;
        if ((key != null) && (key.length() > 0)) {
            if (!key.equals(placeholderKey) && !key.equals(placeholderNoKey)) {
                sb.append("<key>");
                sb.append(escapeText(key));
                sb.append("</key>\n");
            }
        }
        return sb.toString();
    }
    public String toXMLText(ResourceBundle bundle) {
        StringBuffer sb = new StringBuffer();
        String text = toText(bundle);
        if ((text != null) && (text.length() > 0)) {
            StringBuffer sbText = new StringBuffer(text);
            String reference = extractReference(sbText, true);
            sb.append("<text>");
            sb.append(escapeText(sbText.toString()));
            sb.append("</text>\n");
            if ((reference != null) && (reference.length() > 0)) {
                sb.append("<reference>");
                sb.append(escapeText(reference));
                sb.append("</reference>\n");
            }
        }
        return sb.toString();
    }
    private static final Pattern xsdPattern = Pattern.compile("(XSD\\([^\\)]*\\)): .*");
    private String extractReference(StringBuffer sb, boolean remove) {
        Matcher m = xsdPattern.matcher(sb.toString());
        if (m.matches()) {
            String reference = m.group(1);
            if ((reference != null) && (reference.length() > 0)) {
                if (remove)
                    sb.delete(m.start(1),m.end(1) + 2);
                return reference;
            } else
                return null;
        } else
            return null;
    }
    protected String escapeText(String text) {
        boolean doEscape = false;
        for (int i = 0, n = text.length(); !doEscape && (i < n); ++i) {
            char c = text.charAt(i);
            if (c == '&')
                doEscape = true;
            else if (c == '<')
                doEscape = true;
            else if (c == '>')
                doEscape = true;
        }
        if (!doEscape)
            return text;
        StringBuffer sb = new StringBuffer(text.length());
        for (int i = 0, n = text.length(); i < n; ++i) {
            char c = text.charAt(i);
            if (c == '&')
                sb.append("&amp;");
            else if (c == '<')
                sb.append("&gt;");
            else if (c == '>')
                sb.append("&gt;");
            else
                sb.append(c);
        }
        return sb.toString();
    }
}
