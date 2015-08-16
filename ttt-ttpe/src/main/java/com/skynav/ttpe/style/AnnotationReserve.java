/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import java.text.MessageFormat;

public class AnnotationReserve {

    public static final AnnotationReserve NONE = new AnnotationReserve(Position.NONE.name(), 0);

    public enum Position {
        NONE,
        AUTO,
        BEFORE,
        AFTER,
        OUTSIDE;
    }

    private Position position;
    private double reserve;

    public AnnotationReserve(String position, double reserve) {
        this.position = Position.valueOf(position);
        this.reserve = reserve;
    }

    public boolean isNone() {
        return (this == NONE) || (getPosition() == Position.NONE);
    }

    public Position getPosition() {
        return position;
    }

    public Position resolvePosition(int numLines, boolean lastLine) {
        Position p = this.position;
        if (p == Position.AUTO)
            p = (numLines == 2) ? Position.OUTSIDE : Position.BEFORE;
        if (p == Position.OUTSIDE)
            p = lastLine ? Position.AFTER : Position.BEFORE;
        return p;
    }

    public double getReserve() {
        return reserve;
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + position.hashCode();
        hc = hc * 31 + Double.valueOf(reserve).hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationReserve) {
            AnnotationReserve other = (AnnotationReserve) o;
            if (other.position != position)
                return false;
            else if (other.reserve != reserve)
                return false;
            else
                return true;
        } else
            return false;
    }

    private static final MessageFormat doubleFormatter = new MessageFormat("{0,number,#.####}");

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(position);
        if (position != Position.NONE) {
            sb.append(',');
            sb.append(doubleFormatter.format(new Object[] {position}));
        }
        sb.append(']');
        return sb.toString();
    }

}
