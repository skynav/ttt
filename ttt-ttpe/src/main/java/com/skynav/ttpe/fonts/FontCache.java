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
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttv.util.Reporter;

public class FontCache {

    private File fontSpecificationDirectory;
    private List<File> fontSpecificationFiles;
    private boolean loaded;
    private List<FontSpecification> fontSpecifications;
    private Map<FontKey,Font> instances;
    private Map<String,FontState> loadedState;
    private Reporter reporter;

    public FontCache(File fontSpecificationDirectory, List<File> fontSpecificationFiles, Reporter reporter) {
        this.fontSpecificationDirectory = fontSpecificationDirectory;
        if (fontSpecificationFiles != null)
            this.fontSpecificationFiles = new java.util.ArrayList<File>(fontSpecificationFiles);
        this.instances = new java.util.HashMap<FontKey,Font>();
        this.loadedState = new java.util.HashMap<String,FontState>();
        this.reporter = reporter;
    }

    public Font getDefaultFont(Axis axis, Extent size) {
        return get(new FontKey((axis == Axis.VERTICAL) ? FontKey.DEFAULT_VERTICAL : FontKey.DEFAULT_HORIZONTAL, size));
    }

    public Font getLastResortFont(Axis axis, Extent size) {
        return get(new FontKey((axis == Axis.VERTICAL) ? FontKey.LAST_RESORT_VERTICAL : FontKey.LAST_RESORT_HORIZONTAL, size));
    }

    public Font mapFont(List<String> families, FontStyle style, FontWeight weight, String language, Axis axis, Extent size, Set<FontFeature> features) {
        FontSpecification fs;
        if ((fs = findExactMatch(families, style, weight, language)) == null)
            fs = findBestMatch(families, style, weight, language, axis, size);
        if (fs != null)
            return get(new FontKey(fs.family, fs.style, fs.weight, fs.language, axis, size, features));
        return null;
    }

    public Font get(FontKey key) {
        Font f = instances.get(key);
        if (f == null) {
            f = create(key);
            if (f != null)
                put(f);
        }
        return f;
    }

    public Font getScaledFont(Font font, double scale) {
        return get(font.getKey().getScaled(scale));
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
        loadedState.clear();
    }

    public FontState getLoadedState(String source, BitSet forcePath, Reporter reporter) {
        FontState fs;
        if ((fs = loadedState.get(source)) == null)
            fs = createLoadedState(source, forcePath, reporter);
        return fs;
    }

    private FontSpecification findExactMatch(List<String> families, FontStyle style, FontWeight weight, String language) {
        for (String family : families) {
            if (fontSpecifications != null) {
                for (FontSpecification  fs : fontSpecifications) {
                    if (fs.family.compareToIgnoreCase(family) == 0) {
                        if (fs.style != style)
                            continue;
                        if (fs.weight != weight)
                            continue;
                        if ((language != null) && (fs.language != null) && !fs.language.equals(language))
                            continue;
                        return fs;
                    }
                }
            }
        }
        return null;
    }

    private FontSpecification findBestMatch(List<String> families, FontStyle style, FontWeight weight, String language, Axis axis, Extent size) {
        if (fontSpecifications != null) {
            List<FontSpecification> matchesFamily = new java.util.ArrayList<FontSpecification>();
            for (FontSpecification  fs : fontSpecifications) {
                for (String family : families) {
                    if (fs.family.compareToIgnoreCase(family) == 0)
                        matchesFamily.add(fs);
                }
            }
            List<FontSpecification> matchesLanguage = new java.util.ArrayList<FontSpecification>();
            for (FontSpecification  fs : matchesFamily) {
                if ((language == null) || language.isEmpty())
                    matchesLanguage.add(fs);
                else if (fs.language.compareToIgnoreCase(language) == 0)
                    matchesLanguage.add(fs);
            }
            List<FontSpecification> matchesStyle = new java.util.ArrayList<FontSpecification>();
            for (FontSpecification  fs : matchesLanguage) {
                if (style == null)
                    matchesStyle.add(fs);
                else if (fs.style == style)
                    matchesStyle.add(fs);
            }
            List<FontSpecification> matchesWeight = new java.util.ArrayList<FontSpecification>();
            for (FontSpecification  fs : matchesStyle) {
                if (weight == null)
                    matchesWeight.add(fs);
                else if (fs.weight == weight)
                    matchesWeight.add(fs);
            }
            if (!matchesWeight.isEmpty())
                return matchesWeight.get(0);
            if (!matchesStyle.isEmpty())
                return matchesStyle.get(0);
            if (!matchesLanguage.isEmpty())
                return matchesLanguage.get(0);
            if (!matchesFamily.isEmpty())
                return matchesFamily.get(0);
        }
        Font f = getDefaultFont(axis, size);
        return (f != null) ? f.getSpecification() : null;
    }

    private Font create(FontKey key) {
        FontSpecification fs = findSpecification(key);
        if (fs != null)
            return new Font(this, key, fs.source, fs.forcePath, reporter);
        else
            return null;
    }

    private FontSpecification findSpecification(FontKey key) {
        if (fontSpecifications != null) {
            for (FontSpecification fs : fontSpecifications) {
                if (fs.matches(key))
                    return fs;
            }
        }
        return null;
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
            File[] files = fontSpecificationDirectory.listFiles(filter);
            if (files != null) {
                for (File f : files) {
                    if (fontSpecificationFiles == null)
                        fontSpecificationFiles = new java.util.ArrayList<File>();
                    fontSpecificationFiles.add(f);
                }
            }
        }
        if (fontSpecificationFiles != null) {
            fontSpecifications = FontLoader.load(fontSpecificationFiles, reporter);
        }
        if ((fontSpecifications == null) || fontSpecifications.isEmpty())
            reporter.logWarning(reporter.message("*KEY*", "No font specifications! No text will be rendered."));
        loaded = true;
    }

    private static boolean maybeFontSpecificationFile(File f) {
        assert f != null;
        String n = f.getName();
        assert n != null;
        return n.endsWith(".xml");
    }

    public FontState createLoadedState(String source, BitSet forcePath, Reporter reporter) {
        FontState fs = new FontState(source, forcePath, reporter);
        assert loadedState != null;
        assert !loadedState.containsKey(source);
        loadedState.put(source, fs);
        return fs;
    }

}
