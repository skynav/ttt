/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.app;

import java.net.URL;
import java.util.Collections;
import java.util.Map;

public class ConfigurationDefaults extends com.skynav.ttv.util.ConfigurationDefaults {
    private static final String[][] optionDefaultSpecifications = new String[][] {
        { "add-creation-metadata", "true" },
        { "merge-styles", "true" },
        { "style-id-pattern", "s{0}" },
        { "style-id-sequence-start", "1" },
    };
    private static final Map<String,String> optionDefaults;
    static {
        Map<String,String> m = new java.util.HashMap<String,String>();
        for (String[] optionDefault: optionDefaultSpecifications) {
            assert optionDefault.length >= 2;
            m.put(optionDefault[0], optionDefault[1]);
        }
        optionDefaults = Collections.unmodifiableMap(m);
    }
    public ConfigurationDefaults() {
        super();
    }
    public ConfigurationDefaults(URL locator) {
        super(locator);
    }
    public Map<String,String> getDefaults() {
        return optionDefaults;
    }
}
