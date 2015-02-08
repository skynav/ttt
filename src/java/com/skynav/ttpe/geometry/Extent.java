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

package com.skynav.ttpe.geometry;

import java.text.MessageFormat;

public class Extent {
    
    public static final Extent EMPTY = new Extent(0, 0);

    private double width;
    private double height;

    public Extent(double width, double height) {
        assert width >= 0;
        this.width = width;
        assert height >= 0;
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDimension(Axis axis) {
        if (axis == Axis.VERTICAL)
            return getHeight();
        else
            return getWidth();
    }

    public boolean isEmpty() {
        return (width == 0) || (height == 0);
    }

    private static final MessageFormat extentFormatter = new MessageFormat("[{0,number,#.####},{1,number,#.####}]");
    @Override
    public String toString() {
        return extentFormatter.format(new Object[] {width, height});
    }

    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + Double.valueOf(width).hashCode();
        hc = hc * 31 + Double.valueOf(height).hashCode();
        return hc;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Extent) {
            Extent other = (Extent) o;
            if (width != other.width)
                return false;
            else if (height != other.height)
                return false;
            else
                return true;
        } else
            return false;
    }

}
