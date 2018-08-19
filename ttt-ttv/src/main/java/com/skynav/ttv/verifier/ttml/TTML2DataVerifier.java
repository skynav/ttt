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

package com.skynav.ttv.verifier.ttml;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.value.Data;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.AbstractVerifier;
import com.skynav.ttv.verifier.DataVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class TTML2DataVerifier extends AbstractVerifier implements DataVerifier {

    public TTML2DataVerifier(Model model) {
        super(model);
    }

    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        setState(content, context);
        if (type == ItemType.Other)
            return verifyOtherItem(content, locator, context);
        else
            throw new IllegalArgumentException();
    }

    protected boolean verifyOtherItem(Object content, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (content instanceof Data)
            failed = !verify((Data) content, locator, context);
        if (failed) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, "*KEY*", "Invalid data item."));
        }
        return !failed;
    }

    protected boolean verify(Data content, Locator locator, VerifierContext context) {
        boolean failed = false;
        return !failed;
    }

}
