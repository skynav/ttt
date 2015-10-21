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
import com.skynav.ttv.model.value.DropMode;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.model.value.TimeParameters;

public class ClockTimeImpl implements ClockTime {
    public static final ClockTime ZERO = new ClockTimeImpl(0, 0, 0, 0, 0);
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
    public double getTime(TimeParameters parameters) {
        assert parameters != null;
        double t = 0;
        t += (double) getHours() * 3600;
        t += (double) getMinutes() * 60;
        t += (double) getSeconds() *  1;
        if (parameters.getTimeBase() == TimeBase.MEDIA) {
            double frames = 0;
            frames += (double) getFrames();
            frames += (double) getSubFrames() / (double) parameters.getSubFrameRate();
            t += frames / parameters.getEffectiveFrameRate();
        } else if (parameters.getTimeBase() == TimeBase.SMPTE) {
            double frames = t * (double) parameters.getFrameRate();
            frames += (double) getFrames();
            frames += (double) getSubFrames() / (double) parameters.getSubFrameRate();
            frames -= (double) getDroppedFrames(parameters.getDropMode());
            t  = frames / parameters.getEffectiveFrameRate();
        }
        return t;
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
    private long getDroppedFrames(DropMode dropMode) {
        long droppedFrames = 0;
        if (dropMode == DropMode.DROP_NTSC) {
            droppedFrames += (long) getHours() * 54;
            droppedFrames += getMinutes() - (long) Math.floor( (double) getMinutes() / 10 );
            droppedFrames *= 2;
        } else if (dropMode == DropMode.DROP_PAL) {
            droppedFrames += (long) getHours() * 27;
            droppedFrames += (long) Math.floor( (double) getMinutes() / 2 ) - (long) Math.floor( (double) getMinutes() / 20 );
            droppedFrames *= 4;
        }
        return droppedFrames;
    }
}

