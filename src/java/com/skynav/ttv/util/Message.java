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
 
package com.skynav.ttv.util;

import java.text.MessageFormat;

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
        return MessageFormat.format(this.format, this.arguments);
    }
    public String toText(boolean hideLocation, boolean hidePath) {
        return toText();
    }
    public String toXML() {
        StringBuffer sb = new StringBuffer();
        sb.append("<message>\n");
        sb.append("<text>");
        sb.append(escapeText(toText()));
        sb.append("</text>\n");
        sb.append("</message>\n");
        return sb.toString();
    }
    public String toXML(boolean hideLocation, boolean hidePath) {
        return toXML();
    }
    public String escapeText(String text) {
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
