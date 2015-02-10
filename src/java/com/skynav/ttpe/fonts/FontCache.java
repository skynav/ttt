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

import java.io.File;
import java.io.FileFilter;
import java.util.List;
import java.util.Map;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttv.util.Reporter;

public class FontCache {

    private File fontSpecificationDirectory;
    private List<File> fontSpecificationFiles;
    private boolean loaded;
    private List<FontSpecification> fontSpecifications;
    private Map<FontKey,Font> instances;
    private Reporter reporter;

    public FontCache(File fontSpecificationDirectory, List<File> fontSpecificationFiles, Reporter reporter) {
        this.fontSpecificationDirectory = fontSpecificationDirectory;
        if (fontSpecificationFiles != null)
            this.fontSpecificationFiles = new java.util.ArrayList<File>(fontSpecificationFiles);
        this.instances = new java.util.HashMap<FontKey,Font>();
        this.reporter = reporter;
    }
    
    public Font getDefaultFont(Axis axis, Extent size) {
        return get(new FontKey((axis == Axis.VERTICAL) ? FontKey.DEFAULT_VERTICAL : FontKey.DEFAULT_HORIZONTAL, size));
    }

    public Font mapFont(String family, FontStyle style, FontWeight weight, Axis axis, String language, Extent size) {
        return get(new FontKey(family, style, weight, axis, language != null ? language : "", size));
    }

    public Font get(FontKey key) {
        Font f = instances.get(key);
        if (f == null)
            put(f = create(key));
        return f;
    }

    private Font create(FontKey key) {
        FontSpecification fs = findSpecification(key);
        if (fs != null)
            return new Font(key, fs.source, reporter);
        else
            return null;
    }

    private FontSpecification findSpecification(FontKey key) {
        for (FontSpecification fs : fontSpecifications) {
            if (fs.matches(key))
                return fs;
        }
        return null;
    }

    public void put(Font f) {
        instances.put(f.getKey(), f);
    }

    public FontCache maybeLoad() {
        if (!loaded)
            load();
        return this;
    }

    public void clear() {
        instances.clear();
    }

    private void load() {
        if (fontSpecificationDirectory != null) {
            assert fontSpecificationDirectory.exists();
            assert fontSpecificationDirectory.isDirectory();
            FileFilter filter = new FileFilter() {
                public boolean accept(File f) {
                    return maybeFontSpecificationFile(f);
                }
            };
            for (File f : fontSpecificationDirectory.listFiles(filter)) {
                if (fontSpecificationFiles == null)
                    fontSpecificationFiles = new java.util.ArrayList<File>();
                fontSpecificationFiles.add(f);
            }
        }
        if (fontSpecificationFiles != null) {
            fontSpecifications = FontLoader.load(fontSpecificationFiles, reporter);
        }
        loaded = true;
    }

    private static boolean maybeFontSpecificationFile(File f) {
        assert f != null;
        String n = f.getName();
        return (n != null) && n.endsWith(".xml");
    }

}
