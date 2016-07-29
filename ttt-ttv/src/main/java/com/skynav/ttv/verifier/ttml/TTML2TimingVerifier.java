/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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

import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.parameter.MediaDurationVerifier;
import com.skynav.ttv.verifier.ttml.parameter.MediaOffsetVerifier;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters2;

public class TTML2TimingVerifier extends TTML1TimingVerifier {

    private static final Object[][] timingAccessorMap                   = new Object[][] {
        {
            TTML2ParameterVerifier.mediaDurationAttributeName,
            "MediaDuration",
            String.class,
            MediaDurationVerifier.class,
            Boolean.FALSE,
            null,
        },
        {
            TTML2ParameterVerifier.mediaOffsetAttributeName,
            "MediaOffset",
            String.class,
            MediaOffsetVerifier.class,
            Boolean.FALSE,
            null,
        },
    };

    public TTML2TimingVerifier(Model model) {
        super(model);
    }

    @Override
    protected void populateAccessors(Map<QName, TimingAccessor> accessors) {
        super.populateAccessors(accessors);
        populateAccessors(accessors, timingAccessorMap);
    }

    @Override
    protected TimingVerificationParameters makeTimingVerificationParameters(Object content, VerifierContext context) {
        return new TimingVerificationParameters2(content, context != null ? context.getExternalParameters() : null);
    }

    @Override
    protected boolean isTimedText(Object content) {
        return content instanceof TimedText;
    }

    @Override
    protected String getTimingValueAsString(Object content, QName timingName) {
        assert content instanceof TimedText;
        return ((TimedText)content).getOtherAttributes().get(timingName);
    }

}
