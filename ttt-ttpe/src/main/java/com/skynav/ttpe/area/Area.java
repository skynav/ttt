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

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.Visibility;
import com.skynav.ttpe.style.Whitespace;

public interface Area extends AreaGeometry {

    /**
     * Obtain element that generated area.
     */
    Element getElement();

    /**
     * Obtain (xml) whitespace treatment of area's content, or null if none defined.
     */
    Whitespace getWhitespace();

    /**
     * Obtain (xml) language of area's content, or null if none defined.
     */
    String getLanguage();

    /**
     * Obtain writing mode of area, or null if none defined.
     */
    WritingMode getWritingMode();

    /**
     * Determine if writing mode of area is vertical; if no writing mode defined, then false.
     */
    boolean isVertical();

    /**
     * Obtain visibility, or null if none defined.
     */
    Visibility getVisibility();

    /**
     * Determine if is (or should be) visible, based on visibility.
     */
    boolean isVisible();

    /**
     * Obtain font of area, or null if none defined.
     */
    Font getFont();

}
