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

import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Image;
import com.skynav.ttpe.style.Visibility;

public class BlockArea extends NonLeafAreaNode implements Block {

    private double bpd;
    private double ipd;
    private int level;
    private Visibility visibility;
    private int reversals;
    private double overflow;
    private Color backgroundColor;
    private Image backgroundImage;

    public BlockArea() {
        this(null);
    }

    public BlockArea(Element e) {
        this(e, null);
    }

    public BlockArea(Element e, Visibility visibility) {
        this(e, Double.NaN, Double.NaN, -1, visibility);
    }

    public BlockArea(Element e, double ipd, double bpd, int level, Visibility visibility) {
        super(e);
        this.ipd = ipd;
        this.bpd = bpd;
        this.level = level;
        this.visibility = visibility;
    }

    @Override
    public int getBidiLevel() {
        return level;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public void reverse() {
        ++reversals;
    }

    @Override
    public boolean needsReversing() {
        return (reversals & 1) == 1;
    }

    @Override
    public void setIPD(double ipd) {
        this.ipd = ipd;
    }

    @Override
    public void setBPD(double bpd) {
        this.bpd = bpd;
    }

    public int getLineCount() {
        int numLines = 0;
        for (AreaNode a : getChildren()) {
            if (a instanceof LineArea)
                ++numLines;
        }
        return numLines;
    }

    public LineArea getFirstLine() {
        List<LineArea> lines = getLines();
        if (!lines.isEmpty())
            return lines.get(0);
        else
            return null;
    }

    public LineArea getLastLine() {
        List<LineArea> lines = getLines();
        if (!lines.isEmpty())
            return lines.get(lines.size() - 1);
        else
            return null;
    }

    public List<LineArea> getLines() {
        return getLines(true);
    }

    public List<LineArea> getLines(boolean includeDescendants) {
        List<LineArea> lines = new java.util.ArrayList<LineArea>();
        return getLines(lines, this, includeDescendants);
    }

    public List<LineArea> getLines(List<LineArea> lines, NonLeafAreaNode a, boolean includeDescendants) {
        for (AreaNode c : a.getChildren()) {
            if (c instanceof LineArea)
                lines.add((LineArea) c);
            if (includeDescendants && (c instanceof NonLeafAreaNode))
                getLines(lines, (NonLeafAreaNode) c, includeDescendants);
        }
        return lines;
    }

    public void setOverflow(double overflow) {
        this.overflow = overflow;
    }

    public double getOverflow() {
        return overflow;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    @Override
    public void expand(AreaNode a, Set<Expansion> expansions) {
        double ipd = a.getIPD();
        double ipdCurrent = getIPD();
        if (Double.isNaN(ipdCurrent))
            ipdCurrent = 0;
        double bpd = a.getBPD();
        double bpdCurrent = getBPD();
        if (Double.isNaN(bpdCurrent))
            bpdCurrent = 0;
        if (!Double.isNaN(ipd)) {
            if (expansions.contains(Expansion.EXPAND_IPD)) {
                if (expansions.contains(Expansion.CROSS))
                    setBPD(bpdCurrent + ipd);
                else
                    setIPD(ipdCurrent + ipd);
            } else if (expansions.contains(Expansion.ENCLOSE_IPD)) {
                if (expansions.contains(Expansion.CROSS)) {
                    if (ipd > bpdCurrent)
                        setBPD(ipd);
                } else {
                    if (ipd > ipdCurrent)
                        setIPD(ipd);
                }
            }
        }
        if (!Double.isNaN(bpd)) {
            if (expansions.contains(Expansion.EXPAND_BPD)) {
                if (expansions.contains(Expansion.CROSS))
                    setIPD(ipdCurrent + bpd);
                else
                    setBPD(bpdCurrent + bpd);
            } else if (expansions.contains(Expansion.ENCLOSE_BPD)) {
                if (expansions.contains(Expansion.CROSS)) {
                    if (bpd > ipdCurrent)
                        setIPD(bpd);
                } else {
                    if (bpd > bpdCurrent)
                        setBPD(bpd);
                }
            }
        }
    }

    @Override
    public double getAvailable(Dimension dimension) {
        return (dimension == Dimension.IPD) ? ipd : bpd;
    }

}
