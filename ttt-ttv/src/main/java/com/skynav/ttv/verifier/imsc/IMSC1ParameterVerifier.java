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

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttd.TimeBase;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010ParameterVerifier;

public class IMSC1ParameterVerifier extends ST20522010ParameterVerifier {

    public IMSC1ParameterVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        boolean failed = false;
        if (!super.verify(content, locator, context, type)) {
            failed = true;
        } else if (type == ItemType.Attributes) {
            if (content instanceof TimedText) {
                if (!verifyParameters((TimedText) content, locator, context))
                    failed = true;
            }
        }
        return !failed;
    }

    protected boolean permitsParameterAttribute(Object content, QName name) {
        if (content instanceof TimedText) {
            String ln = name.getLocalPart();
            if (ln.equals("clockMode"))
                return false;
            else if (ln.equals("dropMode"))
                return false;
            else if (ln.equals("markerMode"))
                return false;
            else if (ln.equals("pixelAspectRatio"))
                return false;
            else if (ln.equals("subFrameRate"))
                return false;
        };
        return super.permitsParameterAttribute(content, name);
    }

    private boolean verifyParameters(TimedText tt, Locator locator, VerifierContext context) {
        boolean failed = false;
        if (!verifyTimeBase(tt, locator, context))
            failed = true;
        return !failed;
    }

    private boolean verifyTimeBase(TimedText tt, Locator locator, VerifierContext context) {
        boolean failed = false;
        TimeBase timeBase = tt.getTimeBase();
        String key;
        if (timeBase == TimeBase.CLOCK)
            key = "*KEY*";
        else if (timeBase == TimeBase.SMPTE)
            key = "*KEY*";
        else
            key = null;
        if (key != null) {
            Reporter reporter = context.getReporter();
            reporter.logError(reporter.message(locator, key, "Parameter value ''{0}'' is prohibited on ''{1}''.", timeBase.value(), timeBaseAttributeName));
            failed = true;
        }
        return !failed;
    }

}
