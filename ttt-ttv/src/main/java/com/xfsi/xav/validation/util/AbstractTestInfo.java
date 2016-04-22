/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
 * Portions Copyright 2009 Extensible Formatting Systems, Inc (XFSI).
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

package com.xfsi.xav.validation.util;

import java.io.InputStream;

import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.util.MimeType;

public abstract class AbstractTestInfo implements TestInfo {

    private String name;
    private String description;
    private String[] arguments;
    private String resourceName;
    private InputStream resourceStream;
    private MimeType mimeType;

    protected AbstractTestInfo() {
        this("*no name*", "*no description*", null);
    }

    protected AbstractTestInfo(String name, String description, String[] arguments) {
        this.name = name;
        this.description = description;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String[] getArguments() {
        return arguments;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName( String name ) {
        this.resourceName = name;
    }

    public InputStream getResourceStream() {
        return resourceStream;
    }

    public void setResourceStream( InputStream stream ) {
        this.resourceStream = stream;
    }

    public MimeType getMimeType() {
        return mimeType;
    }

    public void setMimeType(MimeType type) {
        this.mimeType = type;
    }

    public boolean hasResourceHierarchy() {
        return false;
    }

    public String getResourceHierarchyPathname() {
        return null;
    }

    public String getResourceHierarchyAppPathname() {
        return null;
    }

    public String getDependsUpon() {
        return null;
    }

    public String getVersion() {
        return null;
    }

}
