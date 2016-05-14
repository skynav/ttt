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

package com.skynav.ttpe.area;

import org.w3c.dom.Element;

import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.style.Visibility;

public class PositionedBlockArea extends BoundedBlockArea {

    private Point origin;                                       // allocation rectangle origin

    public PositionedBlockArea(Element e, double x, double y, double width, double height, Visibility visibility) {
        super(e, width, height, visibility);
        this.origin = new Point(x, y);
    }

    public Point getOrigin() {
        return origin;
    }

    public Point getOrigin(ReferenceRectangle rr) {
        if (rr == ReferenceRectangle.ALLOCATION) {
            return getOrigin();
        } else if (rr == ReferenceRectangle.BORDER) {
            return getBorderOrigin();
        } else if (rr == ReferenceRectangle.PADDING) {
            return getPaddingOrigin();
        } else if (rr == ReferenceRectangle.CONTENT) {
            return getContentOrigin();
        } else if (rr == ReferenceRectangle.CONTAINER) {
            throw new UnsupportedOperationException();          // [TBD] IMPLEMENT ME
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Point getBorderOrigin() {                           // w.r.t. allocation rectangle origin
        return Point.ZERO;
    }

    public Point getPaddingOrigin() {                          // w.r.t. allocation rectangle origin
        Point borderOrigin = getBorderOrigin();
        if (hasBorder()) {
            double xBorder = getBorderX();
            double yBorder = getBorderY();
            return new Point(borderOrigin.getX() + xBorder, borderOrigin.getY() + yBorder);
        } else
            return borderOrigin;
    }

    public Point getContentOrigin() {                          // w.r.t. allocation rectangle origin
        Point paddingOrigin = getPaddingOrigin();
        if (hasPadding()) {
            double xPadding = getPaddingX();
            double yPadding = getPaddingY();
            return new Point(paddingOrigin.getX() + xPadding, paddingOrigin.getY() + yPadding);
        } else
            return paddingOrigin;
    }

    public double getX() {
        return getOrigin().getX();
    }

    public double getY() {
        return getOrigin().getY();
    }

}
