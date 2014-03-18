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

import java.io.IOException;

public class XMLReporter extends TextReporter {

    public static final Reporter REPORTER = new XMLReporter();

    private boolean showSource;

    public static String getNamespace() {
        return "http://skynav.com/ns/ttv/report";
    }

    public XMLReporter() {
    }

    public String getName() {
        return "xml";
    }

    public void open(Object... arguments) throws IOException {
        super.open(arguments);
        String reporterOutputEncoding;
        if ((arguments.length > 1) && (arguments[2] instanceof String))
            reporterOutputEncoding = (String) arguments[2];
        else
            reporterOutputEncoding = DEFAULT_ENCODING;
        if ((arguments.length > 2) && (arguments[3] instanceof Boolean))
            showSource = (Boolean) arguments[3];
        out("<?xml version=\"1.0\" encoding=\"" + reporterOutputEncoding.toLowerCase() + "\"?>\n");
        out("<report xmlns=\"" + getNamespace() + "\">\n");
    }

    public void close() throws IOException {
        out("</report>\n");
        flush();
        super.close();
    }

    @Override
    protected void out(ReportType type, String message) {
        assert false;
    }

    @Override
    protected void out(ReportType reportType, Message message) {
        String type;
        if (reportType == ReportType.Error)
            type = "error";
        else if (reportType == ReportType.Warning)
            type = "warning";
        else if (reportType == ReportType.Info)
            type = "info";
        else if (reportType == ReportType.Debug)
            type = "debug";
        else
            type = "unknown";
        StringBuffer sb = new StringBuffer();
        sb.append('<');
        sb.append(type);
        sb.append('>');
        sb.append('\n');
        sb.append(message.toXML(isHidingLocation(), isHidingPath(), showSource));
        sb.append('<');
        sb.append('/');
        sb.append(type);
        sb.append('>');
        sb.append('\n');
        out(sb.toString());
    }

}
