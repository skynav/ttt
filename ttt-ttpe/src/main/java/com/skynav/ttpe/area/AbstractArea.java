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

import org.w3c.dom.Element;

import com.skynav.ttpe.fonts.Font;
import com.skynav.ttpe.geometry.Dimension;
import com.skynav.ttpe.geometry.WritingMode;
import com.skynav.ttpe.style.Visibility;
import com.skynav.ttpe.style.Whitespace;

import com.skynav.xml.helpers.XML;

public abstract class AbstractArea implements Area {

    protected Element element;

    protected AbstractArea() {
        this(null);
    }

    protected AbstractArea(Element element) {
        this.element = element;
    }

    // AreaGeometry interface

    public WritingMode getWritingMode() {
        return null;
    }

    public boolean isVertical() {
        WritingMode wm = getWritingMode();
        return (wm != null) && wm.isVertical();
    }

    public int getBidiLevel() {
        return -1;
    }

    public double getShearAngle() {
        return 0;
    }

    public void setIPD(double ipd) {
    }

    public double getIPD() {
        return getAvailable(Dimension.IPD);
    }

    public double getAllocationIPD() {
        return getIPD();
    }

    public void setBPD(double bpd) {
    }

    public double getBPD() {
        return getAvailable(Dimension.BPD);
    }

    public double getAllocationBPD() {
        return getBPD();
    }

    public double getAvailable(Dimension dimension) {
        return 0;
    }

    // Area interface

    public Element getElement() {
        return element;
    }

    public Whitespace getWhitespace() {
        Element e = getElement();
        if ((e != null) && e.hasAttributeNS(XML.xmlNamespace, "space"))
            return Whitespace.valueOf(e.getAttributeNS(XML.xmlNamespace, "space").toUpperCase());
        else
            return null;
    }

    public String getLanguage() {
        Element e = getElement();
        if ((e != null) && e.hasAttributeNS(XML.xmlNamespace, "lang"))
            return e.getAttributeNS(XML.xmlNamespace, "lang");
        else
            return null;
    }

    public Visibility getVisibility() {
        return null;
    }

    public boolean isVisible() {
        Visibility visibility = getVisibility();
        return (visibility == null) || (visibility != Visibility.HIDDEN);
    }

    public Font getFont() {
        return null;
    }

}
