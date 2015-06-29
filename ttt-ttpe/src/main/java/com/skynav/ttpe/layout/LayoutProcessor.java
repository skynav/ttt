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

package com.skynav.ttpe.layout;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.w3c.dom.Document;

import com.skynav.ttpe.area.Area;
import com.skynav.ttv.app.OptionSpecification;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.transformer.TransformerOptions;

public abstract class LayoutProcessor implements TransformerOptions, Layout {

    protected TransformerContext context;

    protected LayoutProcessor(TransformerContext context) {
        this.context = context;
    }

    public Collection<OptionSpecification> getShortOptionSpecs() {
        return new java.util.ArrayList<OptionSpecification>();
    }

    public Collection<OptionSpecification> getLongOptionSpecs() {
        return new java.util.ArrayList<OptionSpecification>();
    }

    public int parseLongOption(List<String> args, int index) {
        return index;
    }

    public int parseShortOption(List<String> args, int index) {
        return index;
    }

    public void processDerivedOptions() {
    }

    public abstract String getName();

    public abstract List<Area> layout(Document d);

    public abstract void clear(boolean all);

    private static Map<String,Class<? extends LayoutProcessor>> processorMap;

    static {
        processorMap = new java.util.TreeMap<String,Class<? extends LayoutProcessor>>();
        processorMap.put(BasicLayoutProcessor.NAME, BasicLayoutProcessor.class);
    }

    public static Set<String> getProcessorNames() {
        return processorMap.keySet();
    }

    public static LayoutProcessor getProcessor(String name, TransformerContext context) {
        Class<? extends LayoutProcessor> cls = processorMap.get(name);
        if (cls != null) {
            try {
                Constructor<? extends LayoutProcessor> constructor = cls.getDeclaredConstructor(new Class<?>[] { TransformerContext.class });
                return constructor.newInstance(new Object[] { context });
            } catch (NoSuchMethodException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            } catch (InstantiationException e) {
                return null;
            }
        } else
            return null;
    }

    public static String getDefaultName() {
        return BasicLayoutProcessor.NAME;
    }

    public static LayoutProcessor getDefaultProcessor(TransformerContext context) {
        return getProcessor(getDefaultName(), context);
    }

}
