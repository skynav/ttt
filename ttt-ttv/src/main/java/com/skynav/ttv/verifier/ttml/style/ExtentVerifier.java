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

import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class ExtentVerifier implements StyleValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed;
        assert value instanceof String;
        String s = (String) value;
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
        if (Keywords.isAuto(s))
            failed = false;
        else if (Lengths.isLengths(s, location, context, minMax, treatments, null))
            failed = false;
        else {
            Lengths.badLengths(s, location, context, minMax, treatments);
            failed = true;
        }
        Object content = location.getContent();
        if (!failed && (content instanceof TimedText))
            failed = !verifyRootExtent(value, location, context);
        return !failed;
    }

    private boolean verifyRootExtent(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        assert value instanceof String;
        String s = (String) value;
        List<Length> lengths = new java.util.ArrayList<Length>();
        if (Lengths.isLengths(s, location, context, null, null, lengths)) {
            if (lengths.size() == 2) {
                Reporter reporter = context.getReporter();
                Length w = lengths.get(0);
                Length.Unit wUnits = w.getUnits();
                Length h = lengths.get(1);
                Length.Unit hUnits = h.getUnits();
                Length.Unit pxUnits = Length.Unit.Pixel;
                QName styleName = location.getAttributeName();
                Locator locator = location.getLocator();
                if (w.getUnits() != pxUnits) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad units on {0} width on root element, got ''{1}'', expected ''{2}''.",
                        styleName, wUnits.shorthand(), pxUnits.shorthand()));
                    failed = true;
                }
                if (h.getUnits() != pxUnits) {
                    reporter.logInfo(reporter.message(locator, "*KEY*",
                        "Bad units on {0} height on root element, got ''{1}'', expected ''{2}''.",
                        styleName, hUnits.shorthand(), pxUnits.shorthand()));
                    failed = true;
                }
            }
        }
        return !failed;
    }

}
