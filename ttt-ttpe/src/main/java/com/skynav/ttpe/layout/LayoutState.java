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

import java.util.Map;

import org.w3c.dom.Element;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.area.NonLeafAreaNode;
import com.skynav.ttpe.area.ReferenceArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.FontCache;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Overflow;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.BlockAlignment;
import com.skynav.ttpe.style.Defaults;
import com.skynav.ttpe.style.Whitespace;
import com.skynav.ttpe.text.LineBreakIterator;

import com.skynav.ttv.util.StyleSet;

public interface LayoutState {
    public enum Counter {
        REGIONS_IN_CANVAS,
        LINES_IN_CANVAS,
        LINES_IN_REGION,
        MAX_LINES_IN_REGION,
        CHARS_IN_CANVAS,
        CHARS_IN_REGION,
        MAX_CHARS_IN_REGION,
        CHARS_IN_LINE,
        MAX_CHARS_IN_LINE;
    };
    public enum CounterEvent {
        ADD_REGION,
        ADD_LINE,
        RESET;
    };
    // non-content derived state
    LayoutState initialize(FontCache fontCache, LineBreakIterator breakIterator, LineBreakIterator characterIterator, Defaults defaults);
    FontCache getFontCache();
    LineBreakIterator getBreakIterator();
    LineBreakIterator getCharacterIterator();
    Defaults getDefaults();
    // area stack
    NonLeafAreaNode pushCanvas(Element e, double begin, double end, Extent cellResolution);
    NonLeafAreaNode pushViewport(Element e, double width, double height, boolean clip);
    NonLeafAreaNode pushReference(Element e, double x, double y, double width, double height, WritingMode wm, TransformMatrix ctm);
    NonLeafAreaNode pushBlock(Element e);
    NonLeafAreaNode push(NonLeafAreaNode a);
    NonLeafAreaNode addLine(LineArea l);
    NonLeafAreaNode pop();
    NonLeafAreaNode peek();
    // area stack derived state
    ReferenceArea getReferenceArea();
    String getLanguage();
    Whitespace getWhitespace();
    WritingMode getWritingMode();
    int getBidiLevel();
    Font getFont();
    double getAvailable(Dimension dimension);
    Extent getCellResolution();
    Extent getReferenceExtent();
    BlockAlignment getReferenceAlignment();
    // external supplied styles
    Extent getExternalExtent();
    Point getExternalOrigin();
    Overflow getExternalOverflow();
    TransformMatrix getExternalTransform();
    WritingMode getExternalWritingMode();
    // element computed styles
    void saveStyles(Element e);
    Map<String,StyleSet> getStyles();
    StyleSet getStyles(Element e);
    BlockAlignment getDisplayAlign(Element e);
    Extent getExtent(Element e);
    Point getOrigin(Element e);
    Point getPosition(Element e, Extent extent);
    Overflow getOverflow(Element e);
    TransformMatrix getTransform(Element e);
    WritingMode getWritingMode(Element e);
    // statistics
    void incrementCounters(CounterEvent event, Area a);
    void finalizeCounters();
    int getCounter(Counter counter);
}
