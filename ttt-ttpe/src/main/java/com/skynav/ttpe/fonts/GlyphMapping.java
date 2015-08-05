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

import java.nio.IntBuffer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.apache.fontbox.ttf.advanced.util.CharAssociation;
import org.apache.fontbox.ttf.advanced.util.GlyphSequence;

import com.skynav.ttpe.geometry.Axis;

@SuppressWarnings("rawtypes")
public class GlyphMapping {

    private Key key;                                // key (text + features)
    private String resolvedScript;                  // resolved script
    private String resolvedLanguage;                // resolved language
    private int[] glyphs;                           // mapped glyphs
    private String glyphsAsText;                    // mapped glyphs as text
    private List<CharAssociation> associations;     // associations between mapped glyphs and original text
    private int[] advances;                         // glyph advances (in IPD)
    private int[][] adjustments;                    // glyph position adjustments

    public GlyphMapping(Key key, GlyphSequence gs, int[] advances, int[][] adjustments) {
        this.key = key;
        this.glyphs = getGlyphs(gs);
        this.glyphsAsText = getText(gs);
        this.associations = getAssociations(gs);
        this.advances = advances;
        this.adjustments = adjustments;
    }

    public Key getKey() {
        return key;
    }

    public String getText() {
        return key.getText();
    }

    public Collection<FontFeature> getFeatures() {
        return key.getFeatures();
    }

    public String getScript() {
        return key.getScript();
    }

    public void setResolvedScript(String script) {
        resolvedScript = script;
    }

    public String getResolvedScript() {
        return resolvedScript;
    }

    public String getLanguage() {
        return key.getLanguage();
    }

    public String getResolvedLanguage() {
        return resolvedLanguage;
    }

    public void setResolvedLanguage(String language) {
        resolvedLanguage = language;
    }

    public Orientation getOrientation() {
        return key.getOrientation();
    }

    public Combination getCombination() {
        return key.getCombination();
    }

    public int[] getGlyphs() {
        return glyphs;
    }

    public String getGlyphsAsText() {
        return glyphsAsText;
    }

    public List getAssociations() {
        return associations;
    }

    public int[] getAdvances() {
        return advances;
    }

    public int[][] getAdjustments() {
        return adjustments;
    }

    private static String getText(GlyphSequence gs) {
        IntBuffer cb = gs.getCharacters();
        cb.rewind();
        StringBuffer sb = new StringBuffer(cb.limit() - cb.position());
        while (cb.position() < cb.limit()) {
            int c = cb.get();
            if (c < 0x10000)
                sb.append((char) c);
            else {
                c -= 0x10000;
                int sh = ((c >> 10) & 0x03FF) + 0xD800;
                int sl = ((c >>  0) & 0x03FF) + 0xDC00;
                sb.append((char) sh);
                sb.append((char) sl);
            }
        }
        return sb.toString();
    }

    private static int[] getGlyphs(GlyphSequence gs) {
        IntBuffer gb = gs.getGlyphs();
        gb.rewind();
        int[] glyphs = new int[gb.limit() - gb.position()];
        for (int i = 0; gb.position() < gb.limit(); ) {
            glyphs[i++] = gb.get();
        }
        return glyphs;
    }

    private static List<CharAssociation> getAssociations(GlyphSequence gs) {
        List associations = gs.getAssociations();
        if (associations != null) {
            List<CharAssociation> cal = new java.util.ArrayList<CharAssociation>(associations.size());
            for (Object a : associations) {
                if (a instanceof CharAssociation)
                    cal.add((CharAssociation) a);
                else
                    cal.add(null);
            }
            return cal;
        } else
            return null;
    }

    public static Key makeKey(String text, SortedSet<FontFeature> features) {
        return new Key(text, features);
    }

    static class Key {

        private String text;                                    // original text
        private Map<String,FontFeature> features;               // font feature and pseudo-feature parameters

        Key(String text, SortedSet<FontFeature> features) {
            this.text = text;
            this.features = new java.util.TreeMap<String,FontFeature>();
            populateFeatures(features);
        }

        @Override
        public int hashCode() {
            int hc = 23;
            hc = hc * 31 + text.hashCode();
            hc = hc * 31 + features.hashCode();
            return hc;
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof Key) {
                Key other = (Key) o;
                if (!text.equals(other.text))
                    return false;
                else if (!features.equals(other.features))
                    return false;
                else
                    return true;
            } else
                return false;
        }

        @Override
        public String toString() {
            StringBuffer sb = new StringBuffer();
            sb.append('[');
            sb.append('\'');
            sb.append(text);
            sb.append('\'');
            if (features != null) {
                sb.append(',');
                sb.append('[');
                boolean first = true;
                for (FontFeature f : features.values()) {
                    if (!first)
                        sb.append(',');
                    else
                        first = false;
                    sb.append(f);
                }
                sb.append(']');
            }
            sb.append(']');
            return sb.toString();
        }

        String getText() {
            return text;
        }

        Collection<FontFeature> getFeatures() {
            return features.values();
        }

        String getScript() {
            FontFeature f = getFeature(FontFeature.SCPT);
            if (f != null) {
                Object a0 = f.getArgument(0);
                if ((a0 != null) && (a0 instanceof String))
                    return (String) a0;
            }
            return null;
        }

        String getLanguage() {
            FontFeature f = getFeature(FontFeature.LANG);
            if (f != null) {
                Object a0 = f.getArgument(0);
                if ((a0 != null) && (a0 instanceof String))
                    return (String) a0;
            }
            return null;
        }

        boolean isKerningEnabled() {
            return getKerning();
        }

        Boolean getKerning() {
            FontFeature f = getFeature(FontFeature.KERN);
            if (f != null) {
                Object a0 = f.getArgument(0);
                if ((a0 != null) && (a0 instanceof Boolean))
                    return (Boolean) a0;
            }
            return Boolean.FALSE;
        }

        Orientation getOrientation() {
            FontFeature f = getFeature(FontFeature.ORNT);
            if (f != null) {
                Object a0 = f.getArgument(0);
                if ((a0 != null) && (a0 instanceof Orientation))
                    return (Orientation) a0;
            }
            return Orientation.ROTATE000;
        }

        Combination getCombination() {
            FontFeature f = getFeature(FontFeature.COMB);
            if (f != null) {
                Object a0 = f.getArgument(0);
                if ((a0 != null) && (a0 instanceof Combination))
                    return (Combination) a0;
            }
            return Combination.NONE;
        }

        Axis getAdvanceAxis(FontKey key) {
            return (key.axis.cross(!getCombination().isNone()).isVertical() && !getOrientation().isRotated()) ? Axis.VERTICAL : Axis.HORIZONTAL;
        }

        private void populateFeatures(SortedSet<FontFeature> features) {
            for (FontFeature f : features)
                putFeature(f);
        }

        private FontFeature getFeature(FontFeature f) {
            assert f != null;
            return this.features.get(f.getFeature());
        }

        private void putFeature(FontFeature f) {
            assert f != null;
            this.features.put(f.getFeature(), f);
        }

    }

}
