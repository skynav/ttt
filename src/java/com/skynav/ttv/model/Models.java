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
 
package com.skynav.ttv.model;

import java.util.Map;
import java.util.Set;

import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.smpte.ST20522013;

public class Models {

    public static Model getDefaultModel() {
        return TTML1.MODEL;
    }

    private static Map<String,Model> modelMap;

    static {
        modelMap = new java.util.TreeMap<String,Model>();
        modelMap.put(TTML1.MODEL.getName(), TTML1.MODEL);
        modelMap.put(ST20522010.MODEL.getName(), ST20522010.MODEL);
        modelMap.put(ST20522013.MODEL.getName(), ST20522013.MODEL);
    }

    public static Set<String> getModelNames() {
        return modelMap.keySet();
    }

    public static Model getModel(String name) {
        return modelMap.get(name);
    }

}
