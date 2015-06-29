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

package com.skynav.ttpe.geometry;

import static com.skynav.ttpe.geometry.Axis.*;
import static com.skynav.ttpe.geometry.Dimension.*;
import static com.skynav.ttpe.geometry.Direction.*;

public enum WritingMode {

    LRTB,               // left to right inline, top to bottom lines
    RLTB,               // right to left inline, top to bottom lines
    TBRL,               // top to bottom inline, right to left lines
    TBLR;               // top to bottom inline, left to right lines

    public Axis getAxis(Dimension dimension) {
        if (isHorizontal()) {
            return dimension == IPD ? HORIZONTAL : VERTICAL;
        } else {
            return dimension == IPD ? VERTICAL : HORIZONTAL;
        }
    }

    public boolean isHorizontal() {
        return (this == LRTB) || (this == RLTB);
    }

    public boolean isVertical() {
        return !isHorizontal();
    }

    public Direction getDirection(Dimension dimension) {
        if (dimension == IPD) {
            if (this == LRTB)
                return LR;
            else if (this == RLTB)
                return RL;
            else
                return TB;
        } else if (dimension == BPD) {
            if (this == TBLR)
                return LR;
            else if (this == TBRL)
                return RL;
            else
                return TB;
        } else {
            throw new IllegalStateException();
        }
    }

}
