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

import java.util.List;

import com.skynav.ttpe.area.AnnotationArea;
import com.skynav.ttpe.area.LineArea;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.style.Color;
import com.skynav.ttpe.style.InlineAlignment;
import com.skynav.ttpe.style.Visibility;
import com.skynav.ttpe.text.Phrase;

public class AnnotationLayout extends LineLayout {

    public AnnotationLayout(Phrase content, LayoutState state) {
        super(content, state);
    }

    public List<AnnotationArea> layout() {
        List<AnnotationArea> areas = new java.util.ArrayList<AnnotationArea>();
        for (LineArea l : layout(Double.POSITIVE_INFINITY, Consume.FIT)) {
            if (l instanceof AnnotationArea)
                areas.add((AnnotationArea) l);
        }
        return areas;
    }

    @Override
    protected LineArea newLine(Phrase p, double ipd, double bpd, int level, Visibility visibility, InlineAlignment textAlign, Color color, Font font) {
        return new AnnotationArea(p.getElement(), ipd, bpd, level, visibility, textAlign, color, font, getNextAnnotationNumber());
    }

    private int getNextAnnotationNumber() {
        return getNextLineNumber();
    }
}
