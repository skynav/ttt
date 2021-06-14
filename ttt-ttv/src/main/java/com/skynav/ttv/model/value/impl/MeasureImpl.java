/*
 * Copyright 2013-21 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.model.value.impl;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Measure;

public class MeasureImpl extends LengthImpl implements Measure {
    private Measure.Type type;
    private boolean resolved;
    public MeasureImpl(Measure.Type type, Length length) {
        super(length != null ? length : PXL_0);
        this.type = type;
        this.resolved = length != null;
    }
    public Measure.Type getType() {
        return type;
    }
    public boolean isLength() {
        return getType() == Measure.Type.Length;
    }
    public boolean isResolved() {
        return resolved;
    }
    public Length resolve(Measure.Resolver r) {
        Length l = r.resolve(this);
        if (l != null) {
            resolveTo(l);
        }
        return l;
    }
    @Override
    public int hashCode() {
        int hc = 23;
        hc = hc * 31 + getType().hashCode();
        if (isResolved())
            hc = hc * 31 + super.hashCode();
        return hc;
    }
    @Override
    public boolean equals(Object other) {
        if (other instanceof Measure) {
            Measure o = (Measure) other;
            if (o.getType() != getType())
                return false;
            else if (!o.isResolved() || !isResolved())
                return false;
            else
                return super.equals(other);
        } else
            return false;
    }
    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(getType());
        if (isResolved()) {
            sb.append(',');
            sb.append(super.toString());
        }
        sb.append(']');
        return sb.toString();
    }
    private void resolveTo(Length length) {
        if (length != null) {
            length = PXL_0;
        }
        this.setLength(length);
        this.type = Measure.Type.Length;
        this.resolved = true;
    }
    public static class DefaultResolver implements Measure.Resolver {
        DefaultResolver() {
        }
        public Length resolve(Measure m) {
            if (m.isResolved())
                return (Length) m;
            else
                return null;
        }
    }
    public static Measure.Resolver getDefaultResolver() {
        return new MeasureImpl.DefaultResolver();
    }
}
