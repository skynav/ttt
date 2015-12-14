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

package com.skynav.ttv.verifier.ttml.timing;

import java.math.BigDecimal;

import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.DropMode;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.util.ExternalParameters;

public class TimingVerificationParameters1 extends TimingVerificationParameters {

    public TimingVerificationParameters1(Object content, ExternalParameters externalParameters) {
        super(content, externalParameters);
    }

    @Override
    protected void populate(Object content, ExternalParameters externalParameters) {
        assert content instanceof TimedText;
        TimedText tt = (TimedText) content;
        this.timeBase = TimeBase.valueOf(tt.getTimeBase().name());
        this.dropMode = DropMode.valueOf(tt.getDropMode().name());
        this.allowDuration = (timeBase != TimeBase.SMPTE) || !tt.getMarkerMode().name().equals("DISCONTINUOUS");
        this.frameRate = tt.getFrameRate().intValue();
        this.subFrameRate = tt.getSubFrameRate().intValue();
        BigDecimal multiplier = parseFrameRateMultiplier(tt.getFrameRateMultiplier());
        this.frameRateMultiplier = multiplier.doubleValue();
        this.effectiveFrameRate = new BigDecimal(tt.getFrameRate()).multiply(multiplier).doubleValue();
        this.tickRate = tt.getTickRate().intValue();
        if (externalParameters != null) {
            Double externalDuration = (Double) externalParameters.getParameter("externalDuration");
            if (externalDuration != null)
                this.externalDuration = externalDuration.doubleValue();
            else
                this.externalDuration = Double.NaN;
        }
    }

}
