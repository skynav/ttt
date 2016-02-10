/*
 * Copyright 2014-16 Skynav, Inc. All rights reserved.
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
import java.util.Locale;

public class Rectangle {

    public static final Rectangle EMPTY = new Rectangle();

    private Point origin;
    private Extent extent;

    public Rectangle() {
        this(Point.ZERO, Extent.EMPTY);
    }

    public Rectangle(double x, double y, double w, double h) {
        this(new Point(x, y), new Extent(w, h));
    }

    public Rectangle(Point origin, Extent extent) {
        assert origin != null;
        this.origin = new Point(origin);
        assert extent != null;
        this.extent = new Extent(extent);
    }

    public double getX() {
        return origin.getX();
    }

    public double getY() {
        return origin.getY();
    }

    public double getWidth() {
        return extent.getWidth();
    }

    public double getHeight() {
        return extent.getHeight();
    }

    public Point getOrigin() {
        return origin;
    }

    public Extent getExtent() {
        return extent;
    }

    public boolean isEmpty() {
        return (getWidth() == 0) || (getHeight() == 0);
    }

    public java.awt.geom.Rectangle2D getAWTRectangle() {
        return new java.awt.geom.Rectangle2D.Double(getX(), getY(), getWidth(), getHeight());
    }

    private static final MessageFormat rectangleFormatter =
        new MessageFormat("[{0,number,#.####},{1,number,#.####},{2,number,#.####},{3,number,#.####}]", Locale.US);

    @Override
    public String toString() {
        return rectangleFormatter.format(new Object[] {getX(), getY(), getWidth(), getHeight()});
    }
}
