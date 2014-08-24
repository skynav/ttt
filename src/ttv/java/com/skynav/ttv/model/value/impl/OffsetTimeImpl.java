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

import com.skynav.ttv.model.value.OffsetTime;
import com.skynav.ttv.model.value.TimeBase;
import com.skynav.ttv.model.value.TimeParameters;

public class OffsetTimeImpl implements OffsetTime {
    private double offset;
    private OffsetTime.Metric metric;
    public OffsetTimeImpl(double offset, OffsetTime.Metric metric) {
        this.offset = offset;
        this.metric = metric;
    }
    public OffsetTimeImpl(double offset, String metric) {
        this(offset, OffsetTime.Metric.valueOfShorthand(metric));
    }
    public OffsetTimeImpl(String offset, String metric) {
        this(Double.parseDouble(offset), metric);
    }
    public Type getType() {
        return Type.Offset;
    }
    public double getOffset() {
        return offset;
    }
    public double getTime(TimeParameters parameters) {
        assert parameters != null;
        double offset = getOffset();
        Metric m = getMetric();
        if (m == OffsetTime.Metric.Hours)
            return offset * 3600;
        else if (m == OffsetTime.Metric.Minutes)
            return offset * 60;
        else if (m == OffsetTime.Metric.Seconds)
            return offset * 1;
        else if (m == OffsetTime.Metric.Milliseconds)
            return offset / 1000;
        else if (m == OffsetTime.Metric.Frames) {
            if (parameters.getTimeBase() == TimeBase.MEDIA)
                return offset / parameters.getEffectiveFrameRate();
            else if (parameters.getTimeBase() == TimeBase.SMPTE)
                return offset;
            else
                return 0;
        } else if (m == OffsetTime.Metric.Ticks)
            return offset / parameters.getTickRate();
        else {
            assert false;
            return 0;
        }
    }
    public OffsetTime.Metric getMetric() {
        return metric;
    }
}

