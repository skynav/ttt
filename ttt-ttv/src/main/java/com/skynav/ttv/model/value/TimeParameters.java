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

package com.skynav.ttv.model.value;

public class TimeParameters {
    private TimeBase timeBase;
    private ClockMode clockMode;
    private DropMode dropMode;
    private int frameRate;
    private int subFrameRate;
    private double effectiveFrameRate;
    private int tickRate;
    private double externalDuration;
    public TimeParameters() {
        this(30.0);
    }
    public TimeParameters(double frameRate) {
        this(TimeBase.MEDIA, ClockMode.UTC, DropMode.NON_DROP, (int) frameRate, 1, frameRate, 1, Double.NaN);
    }
    public TimeParameters(TimeBase timeBase, ClockMode clockMode, DropMode dropMode, int frameRate, int subFrameRate, double effectiveFrameRate, int tickRate, double externalDuration) {
        this.timeBase = timeBase;
        this.clockMode = clockMode;
        this.dropMode = dropMode;
        this.frameRate = frameRate;
        this.subFrameRate = subFrameRate;
        this.effectiveFrameRate = effectiveFrameRate;
        this.tickRate = tickRate;
        this.externalDuration = externalDuration;
    }
    public TimeBase getTimeBase() { return this.timeBase; }
    public ClockMode getClockMode() { return clockMode; }
    public DropMode getDropMode() { return dropMode; }
    public int getFrameRate() { return this.frameRate; }
    public int getSubFrameRate() { return this.subFrameRate; }
    public double getEffectiveFrameRate() { return this.effectiveFrameRate; }
    public int getTickRate() { return this.tickRate; }
    public double getExternalDuration() { return this.externalDuration; }
}
