/*
 * Copyright 2016-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.imsc.parameter;

import org.xml.sax.Locator;

import com.skynav.ttv.model.imsc.IMSC;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Integers;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.ZeroTreatment;

public class AspectRatioVerifier implements ParameterValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        assert value instanceof String;
        String s = (String) value;
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, ZeroTreatment.Error };
        if (Integers.isIntegers(s, location, context, minMax, treatments, null)) {
            int[] version = IMSC.getIMSCVersion(context.getModel());
            if ((version[0] >= 1) && (version[1] > 0)) {
                if (reporter.isWarningEnabled("deprecated")) {
                    Message message = reporter.message(locator, "*KEY*", "Uses deprecated attribute {0}.", location.getAttributeName());
                    if (reporter.logWarning(message))
                        failed = true;
                }
            }
        } else {
            Integers.badIntegers(s, location, context, minMax, treatments);
            failed = true;
        }
        return !failed;
    }

}
