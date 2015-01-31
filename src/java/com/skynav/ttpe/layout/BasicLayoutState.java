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

package com.skynav.ttpe.layout;

import java.util.Stack;

import org.w3c.dom.Element;

import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.BlockArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.text.LineBreakIterator;

import static com.skynav.ttpe.geometry.Dimension.*;

public class BasicLayoutState implements LayoutState {

    // initialized state
    private FontCache fontCache;
    private LineBreakIterator breakIterator;
    private Stack<NonLeafAreaNode> areas;

    public BasicLayoutState() {
    }

    public LayoutState initialize(FontCache fontCache, LineBreakIterator breakIterator) {
        this.fontCache = fontCache.maybeLoad();
        this.breakIterator = breakIterator;
        this.areas = new java.util.Stack<NonLeafAreaNode>();
        return this;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    public LineBreakIterator getBreakIterator() {
        return breakIterator;
    }

    public NonLeafAreaNode pushBlock(Element e) {
        return pushBlock(e, 0, 0, getAvailable(IPD), getAvailable(BPD));
    }

    public NonLeafAreaNode pushBlock(Element e, double x, double y, double w, double h) {
        return pushBlock(e, getWritingMode(), x, y, w, h);
    }

    public NonLeafAreaNode pushBlock(Element e, WritingMode wm, double x, double y, double w, double h) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        NonLeafAreaNode b = new BlockArea(e, wm, x, y, w, h);
        if (p != null)
            p.addChild(b);
        return areas.push(b);
    }

    public NonLeafAreaNode addLine(LineArea l) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        if (p != null)
            p.addChild(l);
        return l;
    }

    public NonLeafAreaNode pop() {
        return areas.pop();
    }

    public NonLeafAreaNode peek() {
        return areas.peek();
    }

    public WritingMode getWritingMode() {
        if (!areas.empty())
            return areas.peek().getWritingMode();
        else
            return WritingMode.LRTB;
    }

    public double getAvailable(Dimension dimension) {
        if (!areas.empty())
            return areas.peek().getAvailable(dimension);
        else
            return 0;
    }
}