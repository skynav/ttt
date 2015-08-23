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

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Combination;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.GlyphMapping;
import com.skynav.ttpe.fonts.Orientation;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.util.Characters;

public class GlyphArea extends LeafInlineArea {

    private Font font;
    private GlyphMapping mapping;
    private List<Decoration> decorations;

    public GlyphArea(Element e, double ipd, double bpd, int level, Font font, GlyphMapping mapping, List<Decoration> decorations) {
        super(e, ipd, bpd, level);
        this.font = font;
        this.mapping = mapping;
        this.decorations = decorations;
    }

    public Font getFont() {
        return font;
    }

    public GlyphMapping getGlyphMapping() {
        return mapping;
    }

    public String getText() {
        return mapping.getText();
    }

    public String getScript() {
        return mapping.getScript();
    }

    public String getLanguage() {
        return mapping.getLanguage();
    }

    public Orientation getOrientation() {
        return mapping.getOrientation();
    }

    public boolean isRotatedOrientation() {
        Orientation orientation = getOrientation();
        return (orientation != null) && orientation.isRotated();
    }

    public Combination getCombination() {
        return mapping.getCombination();
    }

    public boolean isCombined() {
        Combination combination = getCombination();
        return (combination != null) && !combination.isNone();
    }

    public List<Decoration> getDecorations() {
        return decorations;
    }

    public BlockArea getContainingBlock() {
        return getLine().getContainingBlock();
    }

    public int getSpacingGlyphsCount() {
        String text = mapping.getGlyphsAsText();
        int ng = 0;
        for (int i = 0, n = text.length(); i < n; ++i) {
            int c = text.charAt(i);
            if (!Characters.isWhitespace(c) && !Characters.isNonSpacing(c))
                ng++;
        }
        return ng;
    }

    public void maybeReverseGlyphs(boolean mirror) {
        if (needsReversing())
            mapping = font.maybeReverse(mapping, needsMirroring(mirror));
    }

    private boolean needsMirroring(boolean mirror) {
        if (mirror) {
            int level = getBidiLevel();
            return (level >= 0) && ((level & 1) == 1);
        } else
            return false;
    }

}
