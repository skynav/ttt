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

import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.WritingMode;

public interface AreaGeometry {

    public enum ReferenceRectangle {
        ALLOCATION,             // allocation rectangle of this area
        BORDER,                 // border rectangle of this area
        PADDING,                // padding rectangle of this area
        CONTENT,                // content rectangle of this area
        CONTAINER;              // content rectangle of container area
    };

    /**
     * Obtain writing mode of area.
     */
    WritingMode getWritingMode();

    /**
     * Determine if writing mode of area is vertical.
     */
    boolean isVertical();

    /**
     * Determine bidirectional level.
     */
    int getBidiLevel();

    /**
     * Set IPD of content rectangle of area.
     */
    void setIPD(double ipd);

    /**
     * Obtain IPD of content rectangle of area.
     */
    double getIPD();

    /**
     * Obtain IPD of allocation rectangle of area.
     */
    double getAllocationIPD();

    /**
     * Set BPD of content rectangle of area.
     */
    void setBPD(double bpd);

    /**
     * Obtain BPD of content rectangle of area.
     */
    double getBPD();

    /**
     * Obtain BPD of allocation rectangle of area.
     */
    double getAllocationBPD();

    /**
     * Obtain available measure of area in specified dimension.
     */
    double getAvailable(Dimension dimension);

}
