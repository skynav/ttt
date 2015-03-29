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

package com.skynav.ttpe.fonts;

import com.skynav.ttv.model.value.FontVariant;

public class FontFeature {
    private final String feature;
    private final Object[] arguments;
    public FontFeature(String feature) {
        this(feature, null);
    }
    public FontFeature(String feature, Object[] arguments) {
        this.feature = feature;
        this.arguments = arguments;
    }
    public String getFeature() {
        return feature;
    }
    public Object[] getArguments() {
        return arguments;
    }
    public Object getArgument(int index) {
        if ((arguments != null) && (arguments.length > index))
            return arguments[index];
        else
            return null;
    }
    public static FontFeature fromVariant(FontVariant variant) {
        String feature;
        if (variant == FontVariant.SUPER)
            feature = "sups";
        else if (variant == FontVariant.SUB)
            feature = "subs";
        else if (variant == FontVariant.HALF)
            feature = "hwid";
        else if (variant == FontVariant.FULL)
            feature = "fwid";
        else if (variant == FontVariant.RUBY)
            feature = "ruby";
        else
            feature = null;
        return (feature != null) ? new FontFeature(feature) : null;
    }
}
