/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.ttml.style;

import java.util.List;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class FontShearVerifier implements StyleValueVerifier {

    private static Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };

    public boolean verify(Object value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        boolean failed = false;
        assert value instanceof String;
        String s = (String) value;
        Integer[] minMax = new Integer[] { 1, 1 };
        List<Length> outputLengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(s, location, context, minMax, treatments, outputLengths)) {
            assert outputLengths.size() == 1;
            Length length = outputLengths.get(0);
            if (length.getUnits() != Length.Unit.Percentage) {
                reporter.logInfo(reporter.message(location.getLocator(), "*KEY*", "Bad <percentage> expression ''{0}''.", s));
                failed = true;
            }
        } else {
            Lengths.badLengths(s, location, context, minMax, treatments);
            failed = true;
        }
        return !failed;
    }

}
