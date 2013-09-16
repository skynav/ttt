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
 
package com.skynav.ttv.verifier.ttml.style;

import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Keywords;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class ExtentVerifier implements StyleValueVerifier {

    public boolean verify(Model model, Object content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        boolean failed;
        assert valueObject instanceof String;
        String value = (String) valueObject;
        Integer[] minMax = new Integer[] { 2, 2 };
        Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Allow };
        if (Keywords.isAuto(value))
            failed = false;
        else if (Lengths.isLengths(value, locator, context, minMax, treatments, null))
            failed = false;
        else {
            Lengths.badLengths(value, locator, context, minMax, treatments);
            failed = true;
        }
        if (!failed && (content instanceof TimedText))
            failed = !verify(model, (TimedText) content, name, valueObject, locator, context);
        return !failed;
    }

    private boolean verify(Model model, TimedText content, QName name, Object valueObject, Locator locator, VerifierContext context) {
        boolean failed = false;
        assert valueObject instanceof String;
        String value = (String) valueObject;
        if (value != null) {
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(value, locator, context, null, null, lengths)) {
                if (lengths.size() == 2) {
                    Reporter reporter = context.getReporter();
                    QName styleName = name;
                    Length w = lengths.get(0);
                    Length.Unit wUnits = w.getUnits();
                    Length h = lengths.get(1);
                    Length.Unit hUnits = h.getUnits();
                    Length.Unit pxUnits = Length.Unit.Pixel;
                    if (w.getUnits() != pxUnits) {
                        reporter.logInfo(locator,
                            "Bad units on " + styleName + " width on root element, got '" + wUnits.shorthand() + "', expected '" + pxUnits.shorthand() + "'.");
                        failed = true;
                    }
                    if (h.getUnits() != pxUnits) {
                        reporter.logInfo(locator,
                            "Bad units on " + styleName + " height on root element, got '" + hUnits.shorthand() + "', expected '" + pxUnits.shorthand() + "'.");
                        failed = true;
                    }
                }
            }
        }
        return !failed;
    }

}
