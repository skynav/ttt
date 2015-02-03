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

import org.apache.fontbox.ttf.CmapSubtable;
import org.apache.fontbox.ttf.CmapTable;
import org.apache.fontbox.ttf.OpenTypeFont;
import org.apache.fontbox.ttf.OTFParser;

import com.skynav.ttpe.util.Characters;

public class Font {

    private FontKey key;
    private String source;
    private boolean otfLoadFailed;
    private OpenTypeFont otf;
    private CmapSubtable cmapSubtable;
    
    public Font(FontKey key, String source) {
        this.key = key;
        this.source = source;
    }

    public FontKey getKey() {
        return key;
    }

    public String getSource() {
        return source;
    }

    public double getAdvance(String text) {
        return getAdvance(text, Characters.UC_REPLACEMENT);
    }

    public double getAdvance(String text, int substitution) {
        double advance = 0;
        try {
            if ((key.size > 0) && maybeLoad()) {
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
                advance = advance / (double) otf.getUnitsPerEm();
                advance *= key.size;
            }
        } catch (IOException e) {
        }
        return advance;
    }

    private boolean maybeLoad() {
        if ((otf == null) && !otfLoadFailed) {
            OpenTypeFont otf = null;
            CmapSubtable cmapSubtable = null;
            try {
                File f = new File(source);
                if (f.exists()) {
                    otf = new OTFParser(false, true).parse(f);
                    CmapTable cmap = otf.getCmap();
                    if (cmap != null)
                        cmapSubtable = cmap.getSubtable(CmapTable.PLATFORM_UNICODE, CmapTable.ENCODING_UNICODE_2_0_BMP);
                }
            } catch (IOException e) {
            }
            if (cmapSubtable != null) {
                this.otf = otf;
                this.cmapSubtable = cmapSubtable;
            } else
                otfLoadFailed = true;
        }
        return !otfLoadFailed;
    }

}
