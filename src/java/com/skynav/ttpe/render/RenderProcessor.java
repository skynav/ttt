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

package com.skynav.ttpe.render;

import java.lang.reflect.Constructor;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skynav.ttpe.area.Area;
import com.skynav.ttpe.render.png.PNGRenderProcessor;
import com.skynav.ttpe.render.svg.SVGRenderProcessor;
import com.skynav.ttpe.render.xml.XMLRenderProcessor;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerOptions;

public abstract class RenderProcessor implements TransformerOptions, Render {

    protected TransformerContext context;

    protected RenderProcessor(TransformerContext context) {
        this.context = context;
    }

    public String getOutputPattern() {
        return null;
    }

    @Override
    public Collection<OptionSpecification> getShortOptionSpecs() {
        return new java.util.ArrayList<OptionSpecification>();
    }

    public Collection<OptionSpecification> getLongOptionSpecs() {
        return new java.util.ArrayList<OptionSpecification>();
    }

    public int parseLongOption(String args[], int index) {
        return index;
    }

    public int parseShortOption(String args[], int index) {
        return index;
    }

    public void processDerivedOptions() {
    }

    public abstract String getName();

    public abstract List<Frame> render(List<Area> areas);

    public void clear(boolean all) {
    }

    private static Map<String,Class<? extends RenderProcessor>> processorMap;
    static {
        processorMap = new java.util.TreeMap<String,Class<? extends RenderProcessor>>();
        processorMap.put(PNGRenderProcessor.NAME, PNGRenderProcessor.class);
        processorMap.put(SVGRenderProcessor.NAME, SVGRenderProcessor.class);
        processorMap.put(XMLRenderProcessor.NAME, XMLRenderProcessor.class);
    }

    public static Set<String> getProcessorNames() {
        return processorMap.keySet();
    }

    public static RenderProcessor getProcessor(String name, TransformerContext context) {
        Class<? extends RenderProcessor> cls = processorMap.get(name);
        if (cls != null) {
            try {
                Constructor<? extends RenderProcessor> constructor = cls.getDeclaredConstructor(new Class<?>[] { TransformerContext.class });
                return constructor.newInstance(new Object[] { context });
            } catch (Exception e) {
                return null;
            }
        } else
            return null;
    }

    public static String getDefaultName() {
        return XMLRenderProcessor.NAME;
    }

    public static RenderProcessor getDefaultProcessor(TransformerContext context) {
        return getProcessor(getDefaultName(), context);
    }

}