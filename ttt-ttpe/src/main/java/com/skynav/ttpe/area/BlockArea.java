/*
 * Copyright 2014-21 Skynav, Inc. All rights reserved.
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

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Direction;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.Image;
import com.skynav.ttpe.style.Visibility;

public class BlockArea extends NonLeafAreaNode implements Block {

    private double bpd;                         // bpd of content rectangle
    private double ipd;                         // ipd of content rectangle
    private double[] border;                    // writing-mode relative border: { before, end, after, start }
    private double[] padding;                   // writing-mode relative padding: { before, end, after, start }
    private int level;
    private double shearAngle;
    private Visibility visibility;
    private double opacity;
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
        this(e, Double.NaN, Double.NaN, -1, 0, visibility, 1);
    }

    public BlockArea(Element e, double ipd, double bpd, int level, double shearAngle, Visibility visibility) {
        this(e, ipd, bpd, level, shearAngle, visibility, 1);
    }

    public BlockArea(Element e, double ipd, double bpd, int level, double shearAngle, Visibility visibility, double opacity) {
        super(e);
        this.ipd = ipd;
        this.bpd = bpd;
        this.level = level;
        this.shearAngle = shearAngle;
        this.visibility = visibility;
        this.opacity = opacity;
    }

    // AbstractArea overrides

    @Override
    public int getBidiLevel() {
        return level;
    }

    @Override
    public double getShearAngle() {
        return shearAngle;
    }

    @Override
    public Visibility getVisibility() {
        return visibility;
    }

    @Override
    public double getOpacity() {
        return opacity;
    }

    @Override
    public void setIPD(double ipd) {
        this.ipd = ipd;
    }

    @Override
    public double getAllocationIPD() {
        return ipd + getBorderAndPadding(Dimension.IPD);
    }

    @Override
    public void setBPD(double bpd) {
        this.bpd = bpd;
    }

    @Override
    public double getAllocationBPD() {
        return bpd + getBorderAndPadding(Dimension.BPD);
    }

    @Override
    public double getAvailable(Dimension dimension) {
        return (dimension == Dimension.IPD) ? ipd : bpd;
    }

    // AreaNode overrides

    @Override
    public void reverse() {
        ++reversals;
    }

    @Override
    public boolean needsReversing() {
        return (reversals & 1) == 1;
    }

    // NonLeafAreaNode overrides

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

    // Block methods

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

    public void setBorder(double[] border) {
        adjustContentRectangle(border, this.border);
        this.border = Arrays.copyOf(border, 4);
    }

    public boolean hasBorder() {
        return border != null;
    }

    public double[] getBorder() {
        if (border != null)
            return Arrays.copyOf(border, border.length);
        else
            return new double[4];
    }

    public double getBorder(Dimension dimension) {
        if (border != null) {
            if (dimension == Dimension.BPD)
                return border[0] + border[2];
            else if (dimension == Dimension.IPD)
                return border[1] + border[3];
        }
        return 0;
    }

    public double getBorderWidth() {
        if (getWritingMode().isVertical()) {
            return getBorder(Dimension.BPD);
        } else {
            return getBorder(Dimension.IPD);
        }
    }

    public double getBorderHeight() {
        if (getWritingMode().isVertical()) {
            return getBorder(Dimension.IPD);
        } else {
            return getBorder(Dimension.BPD);
        }
    }

    public double getBorderX(Dimension dimension) {
        if (border != null) {
            Direction direction = getWritingMode().getDirection(dimension);
            if (direction == Direction.LR)
                return border[1];
            else if (direction == Direction.RL)
                return border[3];
        }
        return 0;
    }

    public double getBorderX() {
        if (getWritingMode().isVertical())
            return getBorderX(Dimension.BPD);
        else
            return getBorderX(Dimension.IPD);
    }

    public double getBorderY(Dimension dimension) {
        if (border != null) {
            Direction direction = getWritingMode().getDirection(dimension);
            if (direction == Direction.TB)
                return border[0];
            else if (direction == Direction.BT)
                return border[2];
        }
        return 0;
    }

    public double getBorderY() {
        if (getWritingMode().isVertical())
            return getBorderY(Dimension.IPD);
        else
            return getBorderY(Dimension.BPD);
    }

    public void setPadding(double[] padding) {
        adjustContentRectangle(padding, this.padding);
        this.padding = Arrays.copyOf(padding, 4);
    }

    public boolean hasPadding() {
        return padding != null;
    }

    public double[] getPadding() {
        if (padding != null)
            return Arrays.copyOf(padding, padding.length);
        else
            return new double[4];
    }

    public double getPadding(Dimension dimension) {
        if (padding != null) {
            if (dimension == Dimension.BPD)
                return padding[0] + padding[2];
            else if (dimension == Dimension.IPD)
                return padding[1] + padding[3];
        }
        return 0;
    }

    public double getPaddingWidth() {
        if (getWritingMode().isVertical()) {
            return getPadding(Dimension.BPD);
        } else {
            return getPadding(Dimension.IPD);
        }
    }

    public double getPaddingHeight() {
        if (getWritingMode().isVertical()) {
            return getPadding(Dimension.IPD);
        } else {
            return getPadding(Dimension.BPD);
        }
    }

    public double getPaddingX(Dimension dimension) {
        if (padding != null) {
            Direction direction = getWritingMode().getDirection(dimension);
            if (direction == Direction.LR)
                return padding[1];
            else if (direction == Direction.RL)
                return padding[3];
        }
        return 0;
    }

    public double getPaddingX() {
        if (getWritingMode().isVertical())
            return getPaddingX(Dimension.BPD);
        else
            return getPaddingX(Dimension.IPD);
    }

    public double getPaddingY(Dimension dimension) {
        if (padding != null) {
            Direction direction = getWritingMode().getDirection(dimension);
            if (direction == Direction.TB)
                return padding[0];
            else if (direction == Direction.BT)
                return padding[2];
        }
        return 0;
    }

    public double getPaddingY() {
        if (getWritingMode().isVertical())
            return getPaddingY(Dimension.IPD);
        else
            return getPaddingY(Dimension.BPD);
    }

    public double getBorderAndPadding(Dimension dimension) {
        return getBorder(dimension) + getPadding(dimension);
    }

    private static final double EPSILON = 0.0000001;
    private void adjustContentRectangle(double[] insets, double[] oldInsets) {
        double ipdCurrent = getIPD();
        double ipdNew;
        double ipdInsetDiff = (insets[1] + insets[3]) - ((oldInsets != null) ? (oldInsets[1] + oldInsets[3]) : 0);
        if (!Double.isNaN(ipdCurrent)) {
            ipdNew = ipdCurrent - ipdInsetDiff;
        } else
            ipdNew = ipdCurrent;
        if (Math.abs(ipdNew - ipdCurrent) > EPSILON)
            setIPD(ipdNew);
        double bpdCurrent = getBPD();
        double bpdNew;
        double bpdInsetDiff = (insets[0] + insets[2]) - ((oldInsets != null) ? (oldInsets[0] + oldInsets[2]) : 0);
        if (!Double.isNaN(bpdCurrent)) {
            bpdNew = bpdCurrent - bpdInsetDiff;
        } else
            bpdNew = bpdCurrent;
        if (Math.abs(bpdNew - bpdCurrent) > EPSILON)
            setBPD(bpdNew);
    }

}
