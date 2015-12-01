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

import com.skynav.ttv.model.value.Length;

public class LengthImpl implements Length {
    public static final Length PXL_0 = new LengthImpl(0, Length.Unit.Pixel);
    public static final Length PCT_0 = new LengthImpl(0, Length.Unit.Percentage);
    private double value;
    private Length.Unit units;
    public LengthImpl(double value, Length.Unit units) {
        this.value = value;
        this.units = units;
    }
    public LengthImpl(double value, String units) {
        this(value, Length.Unit.valueOf(units));
    }
    public double getValue() {
        return value;
    }
    public Length.Unit getUnits() {
        return units;
    }
    public Length negate() {
        return (value == 0) ? this : new LengthImpl(-value, units);
    }
    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + units.hashCode();
        hc = hc * 31 + Double.valueOf(value).hashCode();
        return hc;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof Length) {
            Length o = (Length) other;
            if (!o.getUnits().equals(getUnits()))
                return false;
            else if (!Double.valueOf(o.getValue()).equals(Double.valueOf(getValue())))
                return false;
            else
                return true;
        } else
            return false;
    }
    @Override
    public String toString() {
        return Double.toString(value) + units.shorthand();
    }
}

