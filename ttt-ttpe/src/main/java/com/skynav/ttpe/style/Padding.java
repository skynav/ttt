/*
 * Copyright 2015-16 Skynav, Inc. All rights reserved.
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
import java.util.Arrays;
import java.util.Locale;

import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.geometry.WritingMode;

public class Padding {

    private static final double[] zeroPadding = new double[4];
    private static final MessageFormat doubleFormatter = new MessageFormat("{0,number,#.####}", Locale.US);

    public static final Padding NONE = new Padding(null);

    private double[] padding;

    public Padding(double[] padding) {
        this.padding = Arrays.copyOf((padding != null) ? padding : zeroPadding, 4);
    }

    public boolean isNone() {
        return (this == NONE) || Arrays.equals(padding, zeroPadding);
    }

    public double[] getPadding() {
        return Arrays.copyOf(padding, 4);
    }

    public double getBefore() {
        return padding[0];
    }

    public double getAfter() {
        return padding[2];
    }

    public double getStart() {
        return padding[3];
    }

    public double getEnd() {
        return padding[1];
    }

    public double getTop(WritingMode wm) {
        if (wm.isVertical()) {
            Direction d = wm.getDirection(Dimension.IPD);
            if (d == Direction.TB)
                return padding[3];
            else if (d == Direction.BT)
                return padding[1];
        } else {
            Direction d = wm.getDirection(Dimension.BPD);
            if (d == Direction.TB)
                return padding[0];
            else if (d == Direction.BT)
                return padding[2];
        }
        return 0;
    }

    public double getBottom(WritingMode wm) {
        if (wm.isVertical()) {
            Direction d = wm.getDirection(Dimension.IPD);
            if (d == Direction.TB)
                return padding[1];
            else if (d == Direction.BT)
                return padding[3];
        } else {
            Direction d = wm.getDirection(Dimension.BPD);
            if (d == Direction.TB)
                return padding[2];
            else if (d == Direction.BT)
                return padding[0];
        }
        return 0;
    }

    public double getLeft(WritingMode wm) {
        if (wm.isVertical()) {
            Direction d = wm.getDirection(Dimension.BPD);
            if (d == Direction.LR)
                return padding[0];
            else if (d == Direction.RL)
                return padding[2];
        } else {
            Direction d = wm.getDirection(Dimension.IPD);
            if (d == Direction.LR)
                return padding[3];
            else if (d == Direction.RL)
                return padding[1];
        }
        return 0;
    }

    public double getRight(WritingMode wm) {
        if (wm.isVertical()) {
            Direction d = wm.getDirection(Dimension.BPD);
            if (d == Direction.LR)
                return padding[2];
            else if (d == Direction.RL)
                return padding[0];
        } else {
            Direction d = wm.getDirection(Dimension.IPD);
            if (d == Direction.LR)
                return padding[1];
            else if (d == Direction.RL)
                return padding[3];
        }
        return 0;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(padding);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Padding) {
            Padding other = (Padding) o;
            return Arrays.equals(padding, other.padding);
        } else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(doubleFormatter.format(new Object[] {padding[0]}));
        sb.append("px,");
        sb.append(doubleFormatter.format(new Object[] {padding[1]}));
        sb.append("px,");
        sb.append(doubleFormatter.format(new Object[] {padding[2]}));
        sb.append("px,");
        sb.append(doubleFormatter.format(new Object[] {padding[3]}));
        sb.append("px");
        sb.append(']');
        return sb.toString();
    }

}
