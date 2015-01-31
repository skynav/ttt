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

import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.WritingMode;


import static com.skynav.ttpe.geometry.Axis.*;
import static com.skynav.ttpe.geometry.Dimension.*;

public abstract class AbstractArea implements Area {

    protected Element           e;
    protected WritingMode       wm;
    protected double            x;
    protected double            y;
    protected double            w;
    protected double            h;

    protected AbstractArea() {
        this(null);
    }

    protected AbstractArea(Element e) {
        this(e, WritingMode.LRTB);
    }

    protected AbstractArea(Element e, WritingMode wm) {
        this(e, wm, 0, 0, 0, 0);
    }

    protected AbstractArea(Element e, WritingMode wm, double x, double y, double w, double h) {
        this.e = e;
        this.wm = wm;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
    }

    public Element getElement() {
        return e;
    }

    public WritingMode getWritingMode() {
        return wm;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public double getIPD() {
        return (wm.getAxis(IPD) == HORIZONTAL) ? w : h;
    }

    public double getBPD() {
        return (wm.getAxis(BPD) == VERTICAL) ? h : w;
    }

    public double getAvailable(Dimension dimension) {
        if (dimension == IPD)
            return (wm.getAxis(IPD) == HORIZONTAL) ? w : h;
        else if (dimension == BPD)
            return (wm.getAxis(BPD) == VERTICAL) ? h : w;
        else
            throw new IllegalStateException();
    }

}