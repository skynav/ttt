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

import com.xfsi.xav.test.TestInfo;
import com.xfsi.xav.util.Error.ContentType;
import com.xfsi.xav.util.Error.TestType;
import com.xfsi.xav.validation.util.Util.AppType;
import com.xfsi.xav.validation.validator.AbstractValidator;

public abstract class AbstractLoggingValidator extends AbstractValidator {

    protected AbstractLoggingValidator(TestType testType, ContentType contentType) {
    }

    public AppType getAppType() {
        return null;
    }

    protected TestInfo getTestInfo() {
        return null;
    }

    protected boolean getErrorReported() {
        return false;
    }

    public void logResult(Enum<?> code, Object... args) {
    }

    public void logProgress(Enum<?> code, Object... args) {
    }

    public void logAll(Enum<?> code, Object... args) {
    }

    public String msgFormatterNV(String msg, Object...objects) {
        return super.msgFormatterNV(msg, objects);
    }

}
