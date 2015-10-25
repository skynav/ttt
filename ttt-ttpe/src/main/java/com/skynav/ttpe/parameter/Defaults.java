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

package com.skynav.ttpe.parameter;

import com.skynav.ttpe.geometry.Extent;

public class Defaults {

    public static final Extent defaultCellResolution                           = new Extent(32, 15);
    public static final double defaultFrameRate                                = 30;
    public static final double defaultFrameRateMultiplier                      = 1;

    private Extent cellResolution                                              = defaultCellResolution;
    private double frameRate                                                   = defaultFrameRate;
    private double frameRateMultiplier                                         = defaultFrameRateMultiplier;

    public Defaults() {
    }

    public void setCellResolution(Extent cellResolution) {
        this.cellResolution = cellResolution;
    }

    public Extent getCellResolution() {
        return cellResolution;
    }

    public void setFrameRate(double frameRate) {
        this.frameRate = frameRate;
    }

    public double getFrameRate() {
        return frameRate;
    }

    public void setFrameRateMultiplier(double frameRateMultiplier) {
        this.frameRateMultiplier = frameRateMultiplier;
    }

    public double getFrameRateMultiplier() {
        return frameRateMultiplier;
    }

}
