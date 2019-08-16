/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter;

import java.nio.charset.Charset;

import org.w3c.dom.Document;

import static com.skynav.cap2tt.app.Converter.*;

public class Results {
    private static final String NOURI = "*URI NOT AVAILABLE*";
    private static final String NOENCODING = "*ENCODING NOT AVAILABLE*";
    private String uriString;
    private boolean succeeded;
    private int code;
    private int flags;
    private int errorsExpected;
    private int errors;
    private int warningsExpected;
    private int warnings;
    private String encodingName;
    private Document document;
    public Results() {
        this.uriString = NOURI;
        this.succeeded = false;
        this.code = RV_USAGE;
        this.encodingName = NOENCODING;
    }
    public Results(String uriString, int rv, int errorsExpected, int errors, int warningsExpected, int warnings, Charset encoding, Document document) {
        this.uriString = uriString;
        this.succeeded = rvSucceeded(rv);
        this.code = rvCode(rv);
        this.flags = rvFlags(rv);
        this.errorsExpected = errorsExpected;
        this.errors = errors;
        this.warningsExpected = warningsExpected;
        this.warnings = warnings;
        if (encoding != null)
            this.encodingName = encoding.name();
        else
            this.encodingName = "unknown";
        this.document = document;
    }
    public String getURIString() {
        return uriString;
    }
    public boolean getSucceeded() {
        return succeeded;
    }
    public int getCode() {
        return code;
    }
    public int getFlags() {
        return flags;
    }
    public int getErrorsExpected() {
        return errorsExpected;
    }
    public int getErrors() {
        return errors;
    }
    public int getWarningsExpected() {
        return warningsExpected;
    }
    public int getWarnings() {
        return warnings;
    }
    public String getEncodingName() {
        return encodingName;
    }
    public Document getDocument() {
        return document;
    }
}

