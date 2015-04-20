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

import java.util.ResourceBundle;

import org.xml.sax.Locator;

import com.skynav.xml.helpers.XML;

public class LocatedMessage extends Message {
    public static final int LINE_CONTEXT_COUNT = 3;
    private String uri;
    private int row = -1;
    private int col = -1;
    private String[] lines;
    private int linesRowOffset = -1;
    public LocatedMessage(Locator locator, String key, String format, Object... arguments) {
        this(locator.getSystemId(), locator.getLineNumber(), locator.getColumnNumber(), null, key, format, arguments);
    }
    public LocatedMessage(String uri, int row, int col, String[] resourceLines, String key, String format, Object... arguments) {
        super(key, format, arguments);
        this.uri = uri;
        this.row = row;
        this.col = col;
        if ((resourceLines != null) && (row > 0))
            populateLines(resourceLines, row, LINE_CONTEXT_COUNT);
    }
    private void populateLines(String[] resourceLines, int row, int lineContextCount) {
        assert row > 0;
        int lineNumber = row - 1;
        if (lineNumber >= resourceLines.length)
            lineNumber = resourceLines.length - 1;
        int lineBeforeCount = (lineContextCount - 1) / 2;
        int lineAfterCount = (lineContextCount - 1) / 2;
        if (lineBeforeCount < 1)
            lineBeforeCount = 1;
        if (lineAfterCount < 1)
            lineAfterCount = 1;
        int lineBeforeCountAvailable = lineNumber;
        if (lineBeforeCountAvailable < lineBeforeCount)
            lineBeforeCount = lineBeforeCountAvailable;
        int lineAfterCountAvailable = resourceLines.length - (lineNumber + 1);
        if (lineAfterCountAvailable < lineAfterCount)
            lineAfterCount = lineAfterCountAvailable;
        int lineCount = lineBeforeCount + 1 + lineAfterCount;
        String[] lines = new String[lineCount];
        int firstLineOffset = lineNumber - lineBeforeCount;
        for (int i = firstLineOffset, n = i + lineCount; i < n; ++i) {
            lines[i - firstLineOffset] = resourceLines[i];
        }
        this.lines = lines;
        this.linesRowOffset = firstLineOffset + 1;
    }
    @Override
    public String toText(ResourceBundle bundle, boolean hideLocation, boolean hidePath) {
        String text = super.toText(bundle, false, false);
        if (uri != null) {
            StringBuffer sb = new StringBuffer();
            if (!hideLocation) {
                sb.append('{');
                String uriString = uri;
                if (hidePath)
                    uriString = hidePath(uriString);
                sb.append(uriString);
                sb.append('}');
            }
            if (row >= 0) {
                if (sb.length() > 0)
                    sb.append(':');
                sb.append('[');
                sb.append(row);
                if (col >= 0) {
                    sb.append(',');
                    sb.append(col);
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
    public String toXML(ResourceBundle bundle, boolean hideLocation, boolean hidePath, boolean showSource) {
        StringBuffer sb = new StringBuffer();
        sb.append("<message>\n");
        sb.append(toXMLLocation(hideLocation, hidePath));
        sb.append(toXMLKey());
        sb.append(toXMLText(bundle));
        if (showSource)
            sb.append(toXMLSource());
        sb.append("</message>\n");
        return sb.toString();
    }
    private String toXMLLocation(boolean hideLocation, boolean hidePath) {
        StringBuffer sb = new StringBuffer();
        if (uri != null) {
            sb.append("<location>\n");
            if (!hideLocation) {
                sb.append("<url>");
                String uriString = uri;
                if (hidePath)
                    uriString = hidePath(uriString);
                sb.append(escapeText(uriString));
                sb.append("</url>\n");
            }
            if (row >= 0) {
                sb.append("<row>");
                sb.append(row);
                sb.append("</row>\n");
                if (col >= 0) {
                    sb.append("<col>");
                    sb.append(col);
                    sb.append("</col>\n");
                }
            }
            sb.append("</location>\n");
        }
        return sb.toString();
    }
    private String toXMLSource() {
        StringBuffer sb = new StringBuffer();
        if (lines != null) {
            sb.append("<source>\n");
            int i = linesRowOffset;
            for (String line : lines) {
                int lineLength = line.length();
                sb.append("<line row=\"");
                sb.append(i);
                sb.append("\">");
                if (i == row) {
                    int j = col - 1;
                    if (j > lineLength - 1)
                        j = lineLength - 1;
                    String s1, s2, s3;
                    if (j > 0)
                        s1 = line.substring(0, j);
                    else
                        s1 = null;
                    if (j < line.length())
                        s2 = line.substring(j, j + 1);
                    else
                        s2 = null;
                    if (j + 1 < line.length())
                        s3 = line.substring(j + 1);
                    else
                        s3 = null;
                    if (s1 != null) {
                        sb.append("<unmarked>");
                        sb.append(XML.escapeMarkup(s1, true));
                        sb.append("</unmarked>");
                    }
                    if (s2 != null) {
                        sb.append("<marked>");
                        sb.append(XML.escapeMarkup(s2, true));
                        sb.append("</marked>");
                    }
                    if (s3 != null) {
                        sb.append("<unmarked>");
                        sb.append(XML.escapeMarkup(s3, true));
                        sb.append("</unmarked>");
                    }
                } else {
                    sb.append("<unmarked>");
                    sb.append(XML.escapeMarkup(line, true));
                    sb.append("</unmarked>");
                }
                sb.append("</line>\n");
                ++i;
            }
            sb.append("</source>\n");
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
