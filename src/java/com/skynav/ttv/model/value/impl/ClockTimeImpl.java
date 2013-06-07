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
 
package com.skynav.ttv.model.value.impl;

import com.skynav.ttv.model.value.ClockTime;

public class ClockTimeImpl implements ClockTime {
    private int hours;
    private int minutes;
    private double seconds;
    private double frames;
    private int subFrames;
    public ClockTimeImpl(int hours, int minutes, double seconds, double frames, int subFrames) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.frames = frames;
        this.subFrames = subFrames;
    }
    public ClockTimeImpl(String hours, String minutes, String seconds, String frames, String subFrames) {
        try {
            int hh = Integer.parseInt(hours);
            int mm = Integer.parseInt(minutes);
            double ss = Double.parseDouble(seconds);
            double ff = (frames != null) ? Double.parseDouble(frames) : 0;
            int sf = (subFrames != null) ? Integer.parseInt(subFrames) : 0;
            this.hours = hh;
            this.minutes = mm;
            this.seconds = ss;
            this.frames = ff;
            this.subFrames = sf;
        } catch (NumberFormatException e) {
        }
    }
    public Type getType() {
        return Type.Clock;
    }
    public int getHours() {
        return hours;
    }
    public int getMinutes() {
        return minutes;
    }
    public double getSeconds() {
        return seconds;
    }
    public double getFrames() {
        return frames;
    }
    public int getSubFrames() {
        return subFrames;
    }
}

