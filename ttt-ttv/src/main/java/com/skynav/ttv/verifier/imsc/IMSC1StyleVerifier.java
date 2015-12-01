/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.imsc;

import java.util.List;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010StyleVerifier;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class IMSC1StyleVerifier extends ST20522010StyleVerifier {

    public IMSC1StyleVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean isNegativeLengthPermitted(QName eltName, QName styleName) {
        return false;
    }

    @Override
    protected boolean verifyAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        if (!super.verifyAttributeItem(content, locator, sa, context))
            return false;
        else {
            boolean failed = false;
            QName sn = sa.getStyleName();
            if (sn.equals(extentAttributeName))
                failed = !verifyExtentAttributeItem(content, locator, sa, context);
            else if (sn.equals(fontSizeAttributeName))
                failed = !verifyFontSizeAttributeItem(content, locator, sa, context);
            else if (sn.equals(lineHeightAttributeName))
                failed = !verifyLineHeightAttributeItem(content, locator, sa, context);
            else if (sn.equals(originAttributeName))
                failed = !verifyOriginAttributeItem(content, locator, sa, context);
            else if (sn.equals(textOutlineAttributeName))
                failed = !verifyTextOutlineAttributeItem(content, locator, sa, context);
            return !failed;
        }
    }

    private boolean verifyExtentAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return true;
    }

    private boolean verifyFontSizeAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        if (value != null) {
            assert value instanceof String;
            Integer[] minMax = new Integer[] { 1, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths((String) value, locator, context, minMax, treatments, lengths)) {
                if (lengths.size() > 1) {
                    Length w = lengths.get(0);
                    Length h = lengths.get(1);
                    if (!w.equals(h)) {
                        Reporter reporter = context.getReporter();
                        reporter.logError(reporter.message(locator, "*KEY*", "Anamorphic font size ''{0}'' prohibited.", value));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean verifyLineHeightAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return true;
    }

    private boolean verifyOriginAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return true;
    }

    private boolean verifyTextOutlineAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return true;
    }

}
