/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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
import java.math.BigInteger;

import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.ttml1.ttd.ClockMode;
import com.skynav.ttv.model.ttml1.ttd.DropMode;
import com.skynav.ttv.model.ttml1.ttd.MarkerMode;
import com.skynav.ttv.model.ttml1.ttd.TimeBase;
import com.skynav.ttv.util.ExternalParameters;

public class TimingVerificationParameters1 extends TimingVerificationParameters {

    public TimingVerificationParameters1(Object content, ExternalParameters externalParameters) {
        super(content, externalParameters);
    }

    @Override
    protected void populate(Object content, ExternalParameters externalParameters) {
        assert content instanceof TimedText;
        TimedText tt = (TimedText) content;
        TimeBase timeBase = tt.getTimeBase();
        if (timeBase != null)
            this.timeBase = com.skynav.ttv.model.value.TimeBase.valueOf(timeBase.name());
        ClockMode clockMode = tt.getClockMode();
        if (clockMode != null)
            this.clockMode = com.skynav.ttv.model.value.ClockMode.valueOf(clockMode.name());
        DropMode dropMode = tt.getDropMode();
        if (dropMode != null)
            this.dropMode = com.skynav.ttv.model.value.DropMode.valueOf(dropMode.name());
        MarkerMode markerMode = tt.getMarkerMode();
        if (markerMode == null)
            markerMode = MarkerMode.CONTINUOUS;
        this.allowDuration = (this.timeBase != com.skynav.ttv.model.value.TimeBase.SMPTE) || !markerMode.name().equals("DISCONTINUOUS");
        BigInteger frameRate = tt.getFrameRate();
        if (frameRate != null)
            this.frameRate = frameRate.intValue();
        BigInteger subFrameRate = tt.getSubFrameRate();
        if (subFrameRate != null)
            this.subFrameRate = tt.getSubFrameRate().intValue();
        String frameRateMultiplier = tt.getFrameRateMultiplier();
        if (frameRateMultiplier == null)
            frameRateMultiplier = "1 1";
        BigDecimal multiplier = parseFrameRateMultiplier(frameRateMultiplier);
        this.frameRateMultiplier = multiplier.doubleValue();
        this.effectiveFrameRate = new BigDecimal(BigInteger.valueOf(this.frameRate)).multiply(multiplier).doubleValue();
        BigInteger tickRate = tt.getTickRate();
        if (tickRate != null)
            this.tickRate = tickRate.intValue();
        if (externalParameters != null) {
            Double externalDuration = (Double) externalParameters.getParameter("externalDuration");
            if (externalDuration != null)
                this.externalDuration = externalDuration.doubleValue();
            else
                this.externalDuration = Double.NaN;
        }
    }

}
