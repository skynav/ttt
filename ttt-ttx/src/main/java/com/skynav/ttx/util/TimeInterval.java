/*
 * Copyright 2013-14 Skynav, Inc. All rights reserved.
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
 
package com.skynav.ttx.util;

public class TimeInterval implements Comparable<TimeInterval> {
    public static final TimeInterval UNRESOLVED = new TimeInterval();
    public static final TimeInterval EMPTY = new TimeInterval(TimeCoordinate.ZERO, TimeCoordinate.ZERO);
    public TimeCoordinate begin;
    public TimeCoordinate end;
    public TimeInterval() {
        this(TimeCoordinate.UNRESOLVED, TimeCoordinate.UNRESOLVED);
    }
    public TimeInterval(String begin, String end) {
        this(TimeCoordinate.parse(begin), TimeCoordinate.parse(end));
    }
    public TimeInterval(TimeCoordinate begin, TimeCoordinate end) {
        this.begin = begin;
        this.end = end;
    }
    public final TimeCoordinate getBegin() {
        return begin;
    }
    public final TimeCoordinate getEnd() {
        return end;
    }
    public TimeCoordinate getDuration() {
        return TimeCoordinate.sub(end, begin);
    }
    public boolean isEmpty() {
        return begin.compareTo(end) >= 0;
    }
    public boolean intersects(TimeInterval other) {
        if (end.compareTo(other.begin) <= 0)
            return false;
        else if (begin.compareTo(other.end) >= 0)
            return false;
        else
            return true;
    }
    @Override
    public int compareTo(TimeInterval other) {
        if (begin.compareTo(other.begin) < 0)
            return -1;
        else if (begin.compareTo(other.begin) > 0)
            return 1;
        else if (end.compareTo(other.end) < 0)
            return -1;
        else if (end.compareTo(other.end) > 0)
            return 1;
        else
            return 0;
    }
    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + begin.hashCode();
        hc = hc * 31 + end.hashCode();
        return hc;
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof TimeInterval) {
            TimeInterval other = (TimeInterval) o;
            if (!other.begin.equals(begin))
                return false;
            if (!other.end.equals(end))
                return false;
            else
                return true;
        } else
            return false;
    }
    @Override
    public String toString() {
        return "[" + getBegin() + "," + getEnd() + ")";
    }
}
