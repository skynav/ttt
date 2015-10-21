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

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import com.skynav.ttv.model.imsc.IMSC1;
import com.skynav.ttv.model.netflix.NFLXTT;
import com.skynav.ttv.model.smpte.ST20522010;
import com.skynav.ttv.model.smpte.ST20522013;
import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.model.ttml.TTML2;
import com.skynav.ttv.model.ebuttd.EBUTTD;

public class Models {

    private static Map<String,Class<? extends Model>> modelMap;
    static {
        Map<String,Class<? extends Model>> m = new java.util.TreeMap<String,Class<? extends Model>>();
        m.put(TTML1.MODEL_NAME, TTML1.TTML1Model.class);
        m.put(TTML2.MODEL_NAME, TTML2.TTML2Model.class);
        m.put(ST20522010.MODEL_NAME, ST20522010.ST20522010Model.class);
        m.put(ST20522013.MODEL_NAME, ST20522013.ST20522013Model.class);
        m.put(NFLXTT.MODEL_NAME, NFLXTT.NFLXTTModel.class);
        m.put(EBUTTD.MODEL_NAME, EBUTTD.EBUTTDModel.class);
        m.put(IMSC1.MODEL_NAME, IMSC1.IMSC1Model.class);
        modelMap = Collections.unmodifiableMap(m);
    }

    public static Model getDefaultModel() {
        return getModel(getDefaultModelName());
    }

    public static String getDefaultModelName() {
        return TTML1.MODEL_NAME;
    }

    public static Set<String> getModelNames() {
        return modelMap.keySet();
    }

    public static Model getModel(String name) {
        Class<? extends Model> modelClass = modelMap.get(name);
        if (modelClass != null) {
            try {
                return modelClass.newInstance();
            } catch (IllegalAccessException e) {
                return null;
            } catch (InstantiationException e) {
                return null;
            }
        } else
            return null;
    }

}
