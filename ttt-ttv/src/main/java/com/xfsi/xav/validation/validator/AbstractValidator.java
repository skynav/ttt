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

package com.xfsi.xav.validation.validator;

import java.util.Properties;

import com.xfsi.xav.test.Test;
import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.test.TestManager;

import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Error.Category;
import com.xfsi.xav.util.Error.ContentType;
import com.xfsi.xav.util.Error.Reference;
import com.xfsi.xav.util.Error.TestType;
import com.xfsi.xav.util.property.PropertyMessageKey;

public abstract class AbstractValidator implements Test  {

    protected Properties properties;

    public AbstractValidator()  throws Exception {
    }

    public AbstractValidator(String propertyFileName) throws Exception {
        // super(propertyFileName);
    }

    public boolean isRunnable(TestManager tm, TestInfo ti) throws Exception {
        return true;
    }

    protected void initState(TestManager tm, TestInfo ti) throws Exception {
        loadProperties();
    }

    private void loadProperties() {
    }

    protected String msg(String key, Object... formatArguments) {
        return null;
    }

    protected String msgFormatterNV(String key, Object... formatArguments) {
        return null;
    }

    protected PropertyMessageKey parseKey(String key) throws PropertyMessageKey.MalformedKeyException {
        return null;
    }

    protected Error error(TestManager tm, TestType type, Category category, ContentType contentType, Reference reference, String subReference, String message, String messageKey) {
        return null;
    }

    protected boolean isResultFiltered(TestManager tm, String messageKey, Error.Severity severity) {
        return false;
    }

}
