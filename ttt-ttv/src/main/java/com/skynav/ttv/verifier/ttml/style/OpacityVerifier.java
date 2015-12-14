/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;

public class OpacityVerifier implements StyleValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        assert value instanceof Float;
        Float f = (Float) value;
        float opacity = f.floatValue();
        if (reporter.isWarningEnabled("out-of-range-opacity")) {
            QName name = location.getAttributeName();
            if (f.isNaN()) {
                reporter.logWarning(reporter.message(locator,
                    "*KEY*", "Not a Number ''NaN'' should not be used with {0}; use ''0'' instead.", name));
            } else if (f.isInfinite()) {
                if (opacity < 0)
                    reporter.logWarning(reporter.message(locator,
                        "*KEY*", "Negative Infinity ''-INF'' should not be used with {0}; use ''0'' instead.", name));
                else
                    reporter.logWarning(reporter.message(locator,
                        "*KEY*", "Positive Infinity ''INF'' should not be used with {0}; use ''1'' instead.", name));
            } else if (opacity < 0) {
                reporter.logWarning(reporter.message(locator,
                    "*KEY*", "Negative values should not be used with {0}; use ''0'' instead.", name));
            } else if (opacity > 1) {
                reporter.logWarning(reporter.message(locator,
                    "*KEY*", "Positive values greater than 1 should not be used with {0}; use ''1'' instead.", name));
            }
        }
        return true;
    }

}
