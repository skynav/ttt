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
import java.io.IOException;
import java.util.Set;

import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.NamingTable;
import org.apache.fontbox.ttf.OS2WindowsMetricsTable;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.OTFParser;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.util.Characters;
import com.skynav.ttv.util.Reporter;

public class Font {

    private FontKey key;
    private String source;
    private Reporter reporter;
    private boolean otfLoadFailed;
    private OpenTypeFont otf;
    private NamingTable nameTable;
    private OS2WindowsMetricsTable os2Table;
    private CmapSubtable cmapSubtable;
    
    public Font(FontKey key, String source, Reporter reporter) {
        this.key = key;
        this.source = source;
        this.reporter = reporter;
    }

    public FontKey getKey() {
        return key;
    }

    public FontSpecification getSpecification() {
        return key.getSpecification();
    }

    public String getSource() {
        return source;
    }

    public String getPreferredFamilyName() {
        if (maybeLoad()) {
            if (nameTable != null)
                return nameTable.getName(16, 1, 0, 0);
            else
                return key.family;
        } else
            return "unknown";
    }

    public FontStyle getStyle() {
        return key.style;
    }

    public FontWeight getWeight() {
        return key.weight;
    }

    private static final Set<FontFeature> emptyFeatures = new java.util.HashSet<FontFeature>();
    public Set<FontFeature> getFeatures() {
        if (key.features != null)
            return key.features;
        else
            return emptyFeatures;
    }

    public Axis getAxis() {
        return key.axis;
    }

    public Extent getSize() {
        return key.size;
    }

    public double getSize(Axis axis) {
        return key.size.getDimension(axis);
    }

    public Double getDefaultLineHeight() {
        return key.size.getDimension(key.axis) * 1.25;
    }

    public double getLeading() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoLineGap());
        else
            return 0;
    }

    public double getAscent() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoAscender());
        else
            return 0;
    }

    public double getDescent() {
        if (maybeLoad())
            return scaleFontUnits(os2Table.getTypoDescender());
        else
            return 0;
    }

    public double getAdvance(String text) {
        return getAdvance(text, Characters.UC_REPLACEMENT);
    }

    public double getAdvance(String text, int substitution) {
        double advance = 0;
        try {
            if (!key.size.isEmpty() && maybeLoad()) {
                for (int i = 0, n = text.length(); i < n; ++i) {
                    int c = (int) text.charAt(i);
                    if ((c >= 0xD800) && (c < 0xE000)) {
                        int s1 = c;
                        int s2 = ((i + 1) < n) ? (int) text.charAt(i + 1) : 0;
                        if (s1 < 0xDC00) {
                            if ((s2 >= 0xDC00) && (s2 < 0xE000)) {
                                c = ((s1 - 0xD800) << 10) + (s2 - 0xDC00) + 65536;
                                ++i;
                            } else {
                                c = substitution;
                            }
                        } else {
                            c = substitution;
                        }
                    }
                    int a = Characters.isZeroWidthWhitespace(c) ? 0 : otf.getAdvanceWidth(cmapSubtable.getGlyphId(c));
                    advance += (double) a;
                }
                advance = scaleFontUnits(advance);
            }
        } catch (IOException e) {
        }
        return advance;
    }

    private boolean maybeLoad() {
        if ((otf == null) && !otfLoadFailed) {
            OpenTypeFont otf = null;
            NamingTable nameTable = null;
            OS2WindowsMetricsTable os2Table = null;
            CmapSubtable cmapSubtable = null;
            try {
                File f = new File(source);
                if (f.exists()) {
                    otf = new OTFParser(false, true).parse(f);
                    nameTable = otf.getNaming();
                    os2Table = otf.getOS2Windows();
                    CmapTable cmap = otf.getCmap();
                    if (cmap != null)
                        cmapSubtable = cmap.getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_BMP);
                    reporter.logInfo(reporter.message("*KEY*", "Loaded font instance ''{0}''", f.getAbsolutePath()));
                }
            } catch (IOException e) {
            }
            if ((nameTable != null) && (os2Table != null) && (cmapSubtable != null)) {
                this.otf = otf;
                this.nameTable = nameTable;
                this.os2Table = os2Table;
                this.cmapSubtable = cmapSubtable;
            } else
                otfLoadFailed = true;
        }
        return !otfLoadFailed;
    }

    private double scaleFontUnits(double v) {
        try {
            return (v / (double) otf.getUnitsPerEm()) * key.size.getDimension(key.axis);
        } catch (Exception e) {
            return v;
        }
    }

}
