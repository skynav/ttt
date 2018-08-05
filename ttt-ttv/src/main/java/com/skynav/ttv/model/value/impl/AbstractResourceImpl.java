/*
 * Copyright 2016-2018 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.model.value.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import com.skynav.ttv.model.value.Resource;

public abstract class AbstractResourceImpl implements Resource {
    private URI uri;
    private String typeSpecified;
    private String typeVerified;
    private String formatSpecified;
    private String formatVerified;
    public AbstractResourceImpl(URI uri) {
        this.uri = uri;
    }
    public URI getURI() {
        return uri;
    }
    public InputStream openStream() throws IOException {
        if (uri != null)
            return uri.toURL().openStream();
        else
            return null;
    }
    public void setSpecifiedType(String type) {
        typeSpecified = type;
    }
    public String getSpecifiedType() {
        return typeSpecified;
    }
    public void setVerifiedType(String type) {
        typeVerified = type;
    }
    public String getVerifiedType() {
        return typeVerified;
    }
    public void setSpecifiedFormat(String format) {
        formatSpecified = format;
    }
    public String getSpecifiedFormat() {
        return formatSpecified;
    }
    public void setVerifiedFormat(String format) {
        formatVerified = format;
    }
    public String getVerifiedFormat() {
        return formatVerified;
    }
    public abstract boolean isExternal();
}
