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

package com.skynav.ttv.verifier.ttml.timing;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.verifier.TimingValueVerifier;
import com.skynav.ttv.verifier.VerificationParameters;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Timing;

public class TimeDurationVerifier implements TimingValueVerifier {

    public boolean verify(Model model, Object content, QName name, Object valueObject, Locator locator, VerifierContext context, VerificationParameters parameters) {
        String value = (String) valueObject;
        assert parameters instanceof TimingVerificationParameters;
        TimingVerificationParameters timingParameters = (TimingVerificationParameters) parameters;
        TimeParameters timeParameters = timingParameters.getTimeParameters();
        if (Timing.isDuration(value, locator, context, timeParameters, null)) {
            if (!timingParameters.allowsDuration()) {
                timingParameters.badDuration(name, value, locator, context);
                return false;
            } else
                return true;
        } else {
            Timing.badDuration(value, locator, context, timeParameters);
            return false;
        }
    }

}
