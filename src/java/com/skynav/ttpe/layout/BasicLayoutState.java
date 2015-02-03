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
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.area.ViewportArea;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.text.LineBreakIterator;

public class BasicLayoutState implements LayoutState {

    // initialized state
    private FontCache fontCache;
    private LineBreakIterator breakIterator;
    private LineBreakIterator characterIterator;
    private Stack<NonLeafAreaNode> areas;

    public BasicLayoutState() {
    }

    public LayoutState initialize(FontCache fontCache, LineBreakIterator breakIterator, LineBreakIterator characterIterator) {
        this.fontCache = fontCache.maybeLoad();
        this.breakIterator = breakIterator;
        this.characterIterator = characterIterator;
        this.areas = new java.util.Stack<NonLeafAreaNode>();
        return this;
    }

    public FontCache getFontCache() {
        return fontCache;
    }

    public LineBreakIterator getBreakIterator() {
        return breakIterator;
    }

    public LineBreakIterator getCharacterIterator() {
        return characterIterator;
    }

    public NonLeafAreaNode pushViewport(Element e, double width, double height, boolean clip) {
        return push(new ViewportArea(e, width, height, clip));
    }

    public NonLeafAreaNode pushReference(Element e, double x, double y, double width, double height, WritingMode wm, TransformMatrix ctm) {
        return push(new ReferenceArea(e, x, y, width, height, wm, ctm));
    }

    public NonLeafAreaNode pushBlock(Element e) {
        ReferenceArea ra = getReferenceArea();
        if (ra != null)
            return push(new BlockArea(e, ra.getIPD(), ra.getBPD()));
        else
            throw new IllegalStateException();
    }

    public NonLeafAreaNode push(NonLeafAreaNode a) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        if (p != null)
            p.addChild(a);
        return (NonLeafAreaNode) areas.push(a);
    }

    public NonLeafAreaNode addLine(LineArea l) {
        NonLeafAreaNode p = !areas.empty() ? peek() : null;
        if ((p == null) || !(p instanceof BlockArea))
            throw new IllegalStateException();
        else
            p.addChild(l);
        return l;
    }

    public NonLeafAreaNode pop() {
        return areas.pop();
    }

    public NonLeafAreaNode peek() {
        return areas.peek();
    }

    public ReferenceArea getReferenceArea() {
        if (!areas.empty()) {
            for (int i = 0, n = areas.size(); i < n; ++i) {
                int k = n - i - 1;
                NonLeafAreaNode a = areas.get(k);
                if (a instanceof ReferenceArea)
                    return (ReferenceArea) a;
            }
        }
        return null;
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