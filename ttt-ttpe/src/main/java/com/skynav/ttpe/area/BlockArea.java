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

public class BlockArea extends NonLeafAreaNode implements Block {

    private double bpd;
    private double ipd;
    private int level;
    private double overflow;

    public BlockArea() {
        this(null);
    }

    public BlockArea(Element e) {
        this(e, Double.NaN, Double.NaN, -1);
    }

    public BlockArea(Element e, double ipd, double bpd, int level) {
        super(e);
        this.ipd = ipd;
        this.bpd = bpd;
        this.level = level;
    }

    @Override
    public int getBidiLevel() {
        return level;
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

    @Override
    public void expand(AreaNode a, Set<Expansion> expansions) {
        double ipd = a.getIPD();
        if (!Double.isNaN(ipd)) {
            double ipdCurrent = getIPD();
            if (Double.isNaN(ipdCurrent))
                ipdCurrent = 0;
            if (expansions.contains(Expansion.EXPAND_IPD))
                setIPD(ipdCurrent + ipd);
            else if (expansions.contains(Expansion.ENCLOSE_IPD)) {
                if (ipd > ipdCurrent)
                    setIPD(ipd);
            }
        }
        double bpd = a.getBPD();
        if (!Double.isNaN(bpd)) {
            double bpdCurrent = getBPD();
            if (Double.isNaN(bpdCurrent))
                bpdCurrent = 0;
            if (expansions.contains(Expansion.EXPAND_BPD))
                setBPD(bpdCurrent + bpd);
            else if (expansions.contains(Expansion.ENCLOSE_BPD)) {
                if (bpd > bpdCurrent)
                    setBPD(bpd);
            }
        }
    }

    @Override
    public double getAvailable(Dimension dimension) {
        return (dimension == Dimension.IPD) ? ipd : bpd;
    }

}
