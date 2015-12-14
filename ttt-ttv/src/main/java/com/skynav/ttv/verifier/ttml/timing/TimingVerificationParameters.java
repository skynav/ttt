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
import java.math.BigInteger;
import java.math.RoundingMode;

import com.skynav.ttv.model.value.DropMode;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerificationParameters;
import com.skynav.ttv.verifier.VerifierContext;

public class TimingVerificationParameters implements VerificationParameters {

    public static final int frameRateMultiplierScale = 16;

    protected TimeBase timeBase;
    protected DropMode dropMode;
    protected boolean allowDuration;
    protected int frameRate;
    protected double frameRateMultiplier;
    protected int subFrameRate;
    protected double effectiveFrameRate;
    protected int tickRate;
    protected double externalDuration;

    protected TimingVerificationParameters(Object content, ExternalParameters externalParameters) {
        populate(content, externalParameters);
    }

    public static TimingVerificationParameters makeInstance(Object content, ExternalParameters externalParameters) {
        if (content instanceof com.skynav.ttv.model.ttml1.tt.TimedText)
            return new TimingVerificationParameters1(content, externalParameters);
        else if (content instanceof com.skynav.ttv.model.ttml2.tt.TimedText)
            return new TimingVerificationParameters2(content, externalParameters);
        else
            throw new IllegalStateException();
    }

    public TimeBase getTimeBase() {
        return timeBase;
    }

    public boolean allowsDuration() {
        return allowDuration;
    }

    public void badDuration(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(location.getLocator(),
            "*KEY*", "Duration not allowed when using ''smpte'' time base with ''discontinuous'' marker mode."));
    }

    public int getFrameRate() {
        return frameRate;
    }

    public double getFrameRateMultiplier() {
        return frameRateMultiplier;
    }

    public double getEffectiveFrameRate() {
        return effectiveFrameRate;
    }

    public int getSubFrameRate() {
        return subFrameRate;
    }

    private TimeParameters timeParameters;
    public TimeParameters getTimeParameters() {
        if (timeParameters == null)
            timeParameters = new TimeParameters(timeBase, dropMode, frameRate, subFrameRate, effectiveFrameRate, tickRate, externalDuration);
        return timeParameters;
    }

    protected void populate(Object content, ExternalParameters externalParameters) {
    }

    protected BigDecimal parseFrameRateMultiplier(String value) {
        String[] components = value.split("\\s+");
        if (components.length == 2) {
            BigDecimal n = new BigDecimal(new BigInteger(components[0]), frameRateMultiplierScale);
            BigDecimal d = new BigDecimal(new BigInteger(components[1]), frameRateMultiplierScale);
            if (d.signum() != 0)
                return n.divide(d, RoundingMode.HALF_EVEN);
            else
                return BigDecimal.ONE;
        } else
            return BigDecimal.ONE;
    }

}
