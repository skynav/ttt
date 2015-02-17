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

import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;

public class LineArea extends BlockArea {

    private InlineAlignment alignment;
    private Color color;
    private Font font;
    private double overflow;

    public LineArea(Element e, double ipd, double bpd, InlineAlignment alignment, Color color, Font font) {
        super(e, ipd, bpd);
        this.alignment = alignment;
        this.color = color;
        this.font = font;
    }

    @Override
    public void addChild(AreaNode c, boolean expand) {
        if (c instanceof Inline)
            super.addChild(c, expand);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void insertChild(AreaNode c, AreaNode cBefore, boolean expand) {
        if (c instanceof Inline)
            super.insertChild(c, cBefore, expand);
        else
            throw new IllegalArgumentException();
    }

    @Override
    public void expand(double ipd, double bpd) {
        // expand in Dimension.IPD to fit specified IPD
        if (!Double.isNaN(ipd)) {
            double ipdCurrent = getIPD();
            if (Double.isNaN(ipdCurrent) || (ipdCurrent < ipd))
                setIPD(ipd);
        }
        // expand in Dimension.BPD to fit specified BPD
        if (!Double.isNaN(bpd)) {
            double bpdCurrent = getBPD();
            if (Double.isNaN(bpdCurrent) || (bpdCurrent < bpd))
                setBPD(bpd);
        }
    }

    public InlineAlignment getAlignment() {
        return alignment;
    }

    public Color getColor() {
        return color;
    }

    public Font getFont() {
        return font;
    }

    public void setOverflow(double overflow) {
        this.overflow = overflow;
    }

    public double getOverflow() {
        return overflow;
    }

}