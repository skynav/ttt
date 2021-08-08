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

import java.util.List;

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Combination;
import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.fonts.Orientation;
import com.skynav.ttpe.style.Decoration;
import com.skynav.ttpe.style.Image;
import com.skynav.ttpe.style.Visibility;

public class InlineImageArea extends LeafInlineArea {

    private List<Decoration> decorations;
    private Image image;

    public InlineImageArea(Element e, double ipd, double bpd, int level, List<Decoration> decorations, Image image) {
        super(e, ipd, bpd, level);
        this.decorations = decorations;
        this.image = image;
    }

    public Image getImage() {
        return image;
    }

    public Orientation getOrientation() {
        // FIXME - IMPLEMENT ME
        return null;
    }

    public boolean isRotatedOrientation() {
        Orientation orientation = getOrientation();
        return (orientation != null) && orientation.isRotated();
    }

    public Combination getCombination() {
        // FIXME - IMPLEMENT ME
        return null;
    }

    public boolean isCombined() {
        Combination combination = getCombination();
        return (combination != null) && !combination.isNone();
    }

    public List<Decoration> getDecorations() {
        return decorations;
    }

    @Override
    public Font getFont() {
        AreaNode p = getParent();
        if (p != null)
            return p.getFont();
        else
            return null;
    }

    @Override
    public Visibility getVisibility() {
        for (Decoration d : decorations) {
            if (d.getType() == Decoration.Type.VISIBILITY)
                return (Visibility) d.getValue();
        }
        return super.getVisibility();
    }

    @Override
    public double getOpacity() {
        for (Decoration d : decorations) {
            if (d.getType() == Decoration.Type.OPACITY)
                return ((Double) d.getValue()).doubleValue();
        }
        return super.getOpacity();
    }

}
