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

import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.style.Visibility;

public class BoundedBlockArea extends BlockArea {

    private Extent extent;                                      // allocation rectangle extent

    public BoundedBlockArea(Element e, double width, double height, Visibility visibility) {
        super(e, visibility);
        this.extent = new Extent(width, height);
    }

    public Extent getExtent() {
        return extent;
    }

    public Extent getExtent(ReferenceRectangle rr) {
        if (rr == ReferenceRectangle.ALLOCATION) {
            return getExtent();
        } else if (rr == ReferenceRectangle.BORDER) {
            return getBorderExtent();
        } else if (rr == ReferenceRectangle.PADDING) {
            return getPaddingExtent();
        } else if (rr == ReferenceRectangle.CONTENT) {
            return getContentExtent();
        } else if (rr == ReferenceRectangle.CONTAINER) {
            throw new UnsupportedOperationException();          // [TBD] IMPLEMENT ME
        } else {
            throw new IllegalArgumentException();
        }
    }

    public Extent getBorderExtent() {
        return getExtent();
    }

    public Extent getPaddingExtent() {
        Extent borderExtent = getBorderExtent();
        if (hasBorder()) {
            double wBorder = getBorderWidth();
            double hBorder = getBorderHeight();
            return new Extent(borderExtent.getWidth() - wBorder, borderExtent.getHeight() - hBorder);
        } else
            return borderExtent;
    }

    public Extent getContentExtent() {
        Extent paddingExtent = getPaddingExtent();
        if (hasPadding()) {
            double wPadding = getPaddingWidth();
            double hPadding = getPaddingHeight();
            return new Extent(paddingExtent.getWidth() - wPadding, paddingExtent.getHeight() - hPadding);
        } else
            return paddingExtent;
    }

    public double getWidth() {
        return getExtent().getWidth();
    }

    public double getHeight() {
        return getExtent().getHeight();
    }

}
