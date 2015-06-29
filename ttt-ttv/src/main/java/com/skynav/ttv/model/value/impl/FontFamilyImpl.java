/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import java.util.Map;

import com.skynav.ttv.model.value.FontFamily;
import com.skynav.ttv.model.value.GenericFontFamily;

public class FontFamilyImpl implements FontFamily {
    private Type type;
    private Object value;
    public FontFamilyImpl(Type type, Object value) {
        this.type = type;
        this.value = value;
    }
    public FontFamilyImpl(GenericFontFamily family) {
        this(Type.Generic, family);
    }
    public Type getType() {
        return type;
    }
    public Object getValue() {
        return value;
    }
    @Override
    public String toString() {
        if (type == Type.Generic)
            return ((GenericFontFamily) value).token();
        else if (type == Type.Unquoted)
            return (String) value;
        else if (type == Type.Quoted)
            return (String) value;
        else
            throw new IllegalStateException();
    }
    private static final Map<GenericFontFamily, FontFamily> genericFamilies;
    static {
        genericFamilies = new java.util.HashMap<GenericFontFamily, FontFamily>();
        for (GenericFontFamily f : GenericFontFamily.values()) {
            genericFamilies.put(f, new FontFamilyImpl(f));
        }
    }
    public static FontFamily getGenericFamily(String token) {
        try {
            return genericFamilies.get(GenericFontFamily.valueOfToken(token));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

}
