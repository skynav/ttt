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

public class TimeCoordinate implements Comparable<TimeCoordinate> {
    public enum Type {
        Unspecified,
        Invalid,
        Unresolved,
        Indefinite,
        Definite;
    }
    public static final TimeCoordinate UNSPECIFIED = new TimeCoordinate(Type.Unspecified);
    public static final TimeCoordinate INVALID = new TimeCoordinate(Type.Invalid);
    public static final TimeCoordinate UNRESOLVED = new TimeCoordinate(Type.Unresolved);
    public static final TimeCoordinate INDEFINITE = new TimeCoordinate(Type.Indefinite);
    public static final TimeCoordinate ZERO = new TimeCoordinate(0);
    private Type type = Type.Unresolved;
    private double value;
    public TimeCoordinate() {
        this(Type.Unresolved);
    }
    public TimeCoordinate(Type type) {
        this(type, 0);
    }
    public TimeCoordinate(double value) {
        this(Type.Definite, value);
    }
    public TimeCoordinate(Type type, double value) {
        assert (type == Type.Definite) || (value == 0);
        this.type = type;
        this.value = value;
    }
    public boolean isUnspecified() { return type == Type.Unspecified; }
    public boolean isSpecified() { return !isUnspecified(); }
    public boolean isInvalid() { return type == Type.Invalid; }
    public boolean isUnresolved() { return type == Type.Unresolved; }
    public boolean isIndefinite() { return type == Type.Indefinite; }
    public boolean isDefinite() { return type == Type.Definite; }
    public boolean isZero() { return isDefinite() && (value == 0); }
    public Type getType() { return type; }
    public double getValue() { return value; }
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + getType().ordinal();
        hc = hc * 31 + Double.valueOf(getValue()).hashCode();
        return hc;
    }
    public boolean equals(Object o) {
        if (o instanceof TimeCoordinate) {
            TimeCoordinate c = (TimeCoordinate) o;
            return (c.getType() == getType()) && (c.getValue() == getValue());
        } else
            return false;
    }
    public String toString() {
        return isDefinite() ? Double.toString(getValue()) : getType().name();
    }
    public int compareTo(TimeCoordinate c) {
        if (less(this, c))
            return -1;
        else if (greater(this, c))
            return 1;
        else if (equals(c))
            return 0;
        else
            return -1;
    }
    public static TimeCoordinate fromValue(double value) {
        if (value == 0)
            return ZERO;
        else
            return new TimeCoordinate(value);
    }
    public static TimeCoordinate parse(String value) {
        if (value == null)
            return TimeCoordinate.UNSPECIFIED;
        else if (value.equals("indefinite"))
            return TimeCoordinate.INDEFINITE;
        else if (value.equals("unresolved"))
            return TimeCoordinate.UNRESOLVED;
        else {
            try {
                return fromValue(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                return TimeCoordinate.INVALID;
            }
        }
    }
    public static TimeCoordinate min(TimeCoordinate c1, TimeCoordinate c2) {
        assert !c1.isInvalid();
        assert !c2.isInvalid();
        if (c1.isZero())
            return c1;
        else if (c2.isZero())
            return c2;
        else if (c1.isDefinite()) {
            if (c2.isDefinite())
                return (c1.getValue() < c2.getValue()) ? c1 : c2;
            else
                return c1;
        } else if (c1.isIndefinite()) {
            if (c2.isDefinite())
                return c2;
            else
                return c1;
        } else
            return c2;
    }
    public static TimeCoordinate max(TimeCoordinate c1, TimeCoordinate c2) {
        assert !c1.isInvalid();
        assert !c2.isInvalid();
        if (c1.isUnresolved())
            return c1;
        else if (c2.isUnresolved())
            return c2;
        else if (c1.isDefinite()) {
            if (c2.isDefinite())
                return (c1.getValue() > c2.getValue()) ? c1 : c2;
            else
                return c2;
        } else
            return c1;
    }
    public static boolean less(TimeCoordinate c1, TimeCoordinate c2) {
        return !c1.equals(c2) && min(c1,c2).equals(c1);
    }
    public static boolean greater(TimeCoordinate c1, TimeCoordinate c2) {
        return !c1.equals(c2) && max(c1,c2).equals(c1);
    }
    public TimeCoordinate neg() {
        if (isZero())
            return this;
        else if (isDefinite())
            return fromValue(-1 * getValue());
        else
            return this;
    }
    public static TimeCoordinate add(TimeCoordinate d1, TimeCoordinate d2) {
        assert !d1.isInvalid();
        assert !d2.isInvalid();
        if (d1.isUnresolved())
            return d1;
        else if (d2.isUnresolved())
            return d2;
        else if (d1.isDefinite()) {
            if (d2.isDefinite())
                return fromValue(d1.getValue() + d2.getValue());
            else
                return d2;
        } else
            return d1;
    }
    public static TimeCoordinate sub(TimeCoordinate d1, TimeCoordinate d2) {
        return add(d1, d2.neg());
    }
    public static TimeCoordinate mul(TimeCoordinate d1, TimeCoordinate d2) {
        assert !d1.isInvalid();
        assert !d2.isInvalid();
        if (d1.isZero())
            return d1;
        else if (d2.isZero())
            return d2;
        else if (d1.isUnresolved())
            return d1;
        else if (d2.isUnresolved())
            return d2;
        else if (d1.isDefinite()) {
            if (d2.isDefinite())
                return fromValue(d1.getValue() * d2.getValue());
            else
                return d2;
        } else
            return d1;
    }
}
