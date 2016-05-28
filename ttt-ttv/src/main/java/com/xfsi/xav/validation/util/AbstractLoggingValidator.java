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
import com.xfsi.xav.test.TestManager;
import com.xfsi.xav.util.Error;
import com.xfsi.xav.util.Error.ContentType;
import com.xfsi.xav.util.Error.TestType;
import com.xfsi.xav.util.Progress;
import com.xfsi.xav.util.property.PropertyMessageKey;
import com.xfsi.xav.validation.util.Util.AppType;
import com.xfsi.xav.validation.validator.AbstractValidator;

public abstract class AbstractLoggingValidator extends AbstractValidator {

    private final static Error.Reference[] reference =
    {
        Error.Reference.OTHER,
        Error.Reference.OCAP,
        Error.Reference.MHP,
        Error.Reference.GEM,
        Error.Reference.DAVIC,
        Error.Reference.GZIP,
        Error.Reference.JDK11,
        Error.Reference.JDK12,
        Error.Reference.JMF1,
        Error.Reference.JSSE1,
        Error.Reference.JTV1,
        Error.Reference.JVM12,
        Error.Reference.PJAE1,
        Error.Reference.ZIP,
        Error.Reference.OCSS,
        Error.Reference.JPEG,
        Error.Reference.JFIF,
        Error.Reference.AC3,
        Error.Reference.MP3,
        Error.Reference.IFRAME,
        Error.Reference.JSR242,
        Error.Reference.ACAP,
        Error.Reference.EBIF,
        Error.Reference.JDK122,
        Error.Reference.JVM11,
        Error.Reference.XAV10,
        Error.Reference.XML10,
    };

    private final static Error.Category[] category =
    {
        Error.Category.VALIDITY,
        Error.Category.INTEROPERABILITY,
        Error.Category.SECURITY,
        Error.Category.EFFICIENCY,
        Error.Category.OTHER,
    };

    private TestType testType;
    private ContentType contentType;
    private TestManager tm;
    private TestInfo ti;
    private boolean errorReported;

    protected AbstractLoggingValidator(TestType testType, ContentType contentType) {
        this.testType = testType;
        this.contentType = contentType;
    }

    @Override
    protected void initState(TestManager tm, TestInfo ti) throws Exception {
        super.initState(tm, ti);
        this.tm = tm;
        this.ti = ti;
    }

    public AppType getAppType() {
        return AppType.OTHER;
    }

    protected TestInfo getTestInfo() {
        return ti;
    }

    protected boolean getErrorReported() {
        return errorReported;
    }

    public void logResult(Enum<?> code, Object... args) {
        String key = code.toString();
        try {
            PropertyMessageKey parsedKey = parseKey(key);
            if (!isResultFiltered(tm, key, parsedKey.getSeverity())) {
                logResult(code, key, parsedKey, null, parsedKey.getSeverity(), getReference(code), getSection(key), args);
            } else {
                markErrorReported(parsedKey.getSeverity());
            }
        } catch (PropertyMessageKey.MalformedKeyException e) {
            logResult(code, key, null, e.toString(), Error.Severity.FATAL, getReference(code), getSection(key), args);
        }
    }

    private Error.Reference getReference(Enum<?> code) {
        String c = code.toString();
        String ref = this.properties.getProperty(String.format("ref.%1$s.%2$s", getAppType().toString().toLowerCase(), c));
        if (ref == null)
            ref = this.properties.getProperty(String.format("ref.%1$s", c));
        if (ref == null)
            return AbstractLoggingValidator.reference[0];
        for (Error.Reference er : AbstractLoggingValidator.reference)
            if (ref.equalsIgnoreCase(er.toString()))
                return er;
        return Error.Reference.OTHER;
    }

    private String getSection(String key) {
        String sec = this.properties.getProperty(String.format("sec.%1$s.%2$s", getAppType().toString().toLowerCase(), key));
        if (sec == null)
            sec = this.properties.getProperty(String.format("sec.%1$s", key));
        return sec;
    }

    private void logResult(Enum<?> code, String key, PropertyMessageKey parsedKey, String msg, Error.Severity severity,
        Error.Reference ref, String sec, Object... args) {
        Error.Category c = getCategory(code);
        Error e = null;
        if (parsedKey != null) {
            e = errorFormatterUnfiltered(this.tm, this.testType, c, this.contentType, ref, sec, key, parsedKey, args);
        } else {
            e = error(tm, testType, c, contentType, ref, sec, msg, key);
        }
        markErrorReported(severity);
        if (tm != null && ti != null)
            this.tm.reportError(this.ti, e);
        else {
            if (e != null)
                msg = e.getMessage();
            System.out.println(String.format("Result:   Key=%1$s, Msg=%2$s, Cat=%3$s, Ref=%4$s, Sec=%5$s",
                key, msg, c.toString(), getReference(code).toString(), sec));
        }
    }

    private Error.Category getCategory(Enum<?> code) {
        String c = code.toString();
        String cat = this.properties.getProperty("cat." + c);
        if (cat == null)
            return AbstractLoggingValidator.category[0];
        for (Error.Category ec : AbstractLoggingValidator.category)
            if (cat.equalsIgnoreCase(ec.toString()))
                return ec;
        return Error.Category.OTHER;
    }

    public void logProgress(Enum<?> code, Object... args) {
        String key = code.toString();
        Progress p = progressFormatter(tm, key, args);
        if (tm != null && ti != null)
            this.tm.reportProgress(this.ti, p);
        else {
            String msg = msgFormatterNV(key, args);
            System.out.println(String.format("Progress: Key=%1$s, Msg=%2$s", key, msg));
        }
    }

    public void logAll(Enum<?> code, Object... args) {
        logProgress(code, args);
        logResult(code, args);
    }

    public String msgFormatterNV(String msg, Object...objects) {
        return super.msgFormatterNV(msg, objects);
    }

    private void markErrorReported(Error.Severity severity) {
        switch (severity.getSeverity()) {
        case Error.Severity.ERROR_SEVERITY:
        case Error.Severity.FATAL_SEVERITY:
            errorReported = true;
            break;
        default:
            break;
        }
    }

}
