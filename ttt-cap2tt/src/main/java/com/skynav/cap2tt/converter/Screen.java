/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter;

import java.text.AttributedString;
import java.util.List;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.ClockTime;

public class Screen {
    private Locator locator;
    private int number;
    private int lastNumber;
    private char letter;
    private ClockTime in;
    private ClockTime out;
    private List<Attribute> attributes;
    private AttributedString text;
    public Screen(Locator locator, int lastNumber) {
        this.locator = locator;
        this.lastNumber = lastNumber;
    }
    public Locator getLocator() {
        return locator;
    }
    public int getNumber() {
        return number;
    }
    public void setNumber(int number) {
        assert this.number == 0;
        this.number = number;
    }
    public int getLetter() {
        return letter;
    }
    public void setLetter(char letter) {
        assert this.letter == 0;
        this.letter = letter;
    }
    public ClockTime getInTime() {
        return in;
    }
    public void setInTime(ClockTime in) {
        assert this.in == null;
        this.in = in;
    }
    public ClockTime getOutTime() {
        return out;
    }
    public void setOutTime(ClockTime out) {
        assert this.out == null;
        this.out = out;
    }
    public AttributedString getText() {
        return text;
    }
    public void setText(AttributedString text) {
        assert this.text == null;
        this.text = text;
    }
    public boolean sameNumberAsLastScreen() {
        return (number == 0) || (number == lastNumber);
    }
    public boolean empty() {
        if (hasInOutCodes())
            return false;
        else if ((attributes != null) && (attributes.size() > 0))
            return false;
        else if (text != null)
            return false;
        else
            return true;
    }
    public boolean hasInOutCodes() {
        return (in != null) && (out != null);
    }
    public String getInTimeExpression() {
        return makeTimeExpression(in);
    }
    public String getOutTimeExpression() {
        return makeTimeExpression(out);
    }
    public List<Attribute> getAttributes() {
        return attributes;
    }
    public void addAttribute(Attribute a) {
        if (attributes == null)
            attributes = new java.util.ArrayList<Attribute>();
        attributes.add(a);
    }
    public String getPlacement(boolean[] retGlobal) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasPlacement()) {
                    return a.getPlacement(retGlobal);
                }
            }
        }
        return null;
    }
    public String getAlignment(boolean[] retGlobal) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasAlignment()) {
                    return a.getAlignment(retGlobal);
                }
            }
        }
        return null;
    }
    public String getShear(boolean[] retGlobal) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasShear()) {
                    return a.getShear(retGlobal);
                }
            }
        }
        return null;
    }
    public String getKerning(boolean[] retGlobal) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasKerning()) {
                    return a.getKerning(retGlobal);
                }
            }
        }
        return null;
    }
    public String getTypeface(boolean[] retGlobal) {
        if (attributes != null) {
            for (Attribute a : attributes) {
                if (a.hasTypeface()) {
                    return a.getTypeface(retGlobal);
                }
            }
        }
        return null;
    }
    private static String makeTimeExpression(ClockTime time) {
        return toString(time, ':');
    }
    private static String toString(ClockTime time, char sep) {
        StringBuffer sb = new StringBuffer();
        sb.append(pad(time.getHours(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad(time.getMinutes(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad((int) time.getSeconds(), 2, '0'));
        if (sep != 0)
            sb.append(sep);
        sb.append(pad((int) time.getFrames(), 2, '0'));
        return sb.toString();
    }
    private static String digits = "0123456789";
    private static String pad(int value, int width, char padding) {
        assert value >= 0;
        StringBuffer sb = new StringBuffer(width);
        while (value > 0) {
            sb.append(digits.charAt(value % 10));
            value /= 10;
        }
        while (sb.length() < width) {
            sb.append(padding);
        }
        return sb.reverse().toString();
    }
}
