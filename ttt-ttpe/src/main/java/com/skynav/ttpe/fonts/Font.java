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

import java.util.Collection;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Rectangle;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttv.util.Reporter;

public class Font {

    private FontCache cache;
    private FontKey key;
    private String source;
    private FontState ls;

    public Font(FontCache cache, FontKey key, String source, Reporter reporter) {
        this.cache = cache;
        this.key = key;
        this.source = source;
        this.ls = cache.getLoadedState(source, reporter);
    }

    @Override
    public String toString() {
        if (key != null)
            return key.toString();
        else
            return super.toString();
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
        return ls.getPreferredFamilyName(key);
    }

    public FontStyle getStyle() {
        return key.style;
    }

    public FontWeight getWeight() {
        return key.weight;
    }

    public Collection<FontFeature> getFeatures() {
        return key.getFeatures();
    }

    public FontFeature getFeature(String feature) {
        return key.getFeature(feature);
    }

    public boolean isKerningEnabled() {
        return key.isKerningEnabled();
    }

    public boolean isSheared() {
        return key.isSheared();
    }

    public double getShear() {
        return key.getShear();
    }

    public Axis getAxis() {
        return key.axis;
    }

    public Extent getSize() {
        return key.size;
    }

    public boolean isVertical() {
        return getAxis() == Axis.VERTICAL;
    }

    public double getSize(Axis axis) {
        return key.size.getDimension(axis);
    }

    public double getWidth() {
        return getSize(Axis.HORIZONTAL);
    }

    public double getHeight() {
        return getSize(Axis.VERTICAL);
    }

    public boolean isAnamorphic() {
        return getSize(Axis.HORIZONTAL) != getSize(Axis.VERTICAL);
    }

    public Double getDefaultLineHeight() {
        return key.size.getDimension(key.axis) * 1.25;
    }

    public double getLeading() {
        return ls.getLeading(key);
    }

    public double getAscent() {
        return ls.getAscent(key);
    }

    public double getDescent() {
        return ls.getDescent(key);
    }

    public String getMappedText(String text) {
        return ls.getMappedText(key, text);
    }

    public double getAdvance(String text, String script, String language) {
        return getAdvance(text, script, language, isKerningEnabled(), false, false);
    }

    public double getRotatedAdvance(String text, String script, String language) {
        return getAdvance(text, script, language, isKerningEnabled(), true, false);
    }

    public double getAdvance(String text, String script, String language, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        return ls.getAdvance(key, text, script, language, adjustForKerning, rotatedOrientation, cross);
    }

    public double[] getAdvances(String text, String script, String language) {
        return getAdvances(text, script, language, isKerningEnabled(), false, false);
    }

    public double[] getRotatedAdvances(String text, String script, String language) {
        return getAdvances(text, script, language, isKerningEnabled(), true, false);
    }

    public double[] getAdvances(String text, String script, String language, boolean adjustForKerning, boolean rotatedOrientation, boolean cross) {
        return ls.getAdvances(key, text, script, language, adjustForKerning, rotatedOrientation, cross);
    }

    public double getKerningAdvance(String text, String script, String language) {
        return ls.getKerningAdvance(key, text, script, language);
    }

    public double[] getKerning(String text, String script, String language) {
        return ls.getKerning(key, text, script, language);
    }

    public double getShearAdvance(boolean rotatedOrientation, boolean cross) {
        return getShearAdvance(key.axis.cross(cross), rotatedOrientation);
    }

    public double getShearAdvance(Axis axis, boolean rotatedOrientation) {
        if (isSheared()) {
            TransformMatrix m = getTransform(axis, rotatedOrientation);
            if (m != null) {
                if (axis.isVertical())
                    return getWidth() * m.getShearY();
                else
                    return getHeight() * m.getShearX();
            }
        }
        return 0;
    }

    public TransformMatrix getTransform() {
        return getTransform(key.axis);
    }

    public TransformMatrix getTransform(Axis axis) {
        return getTransform(axis, false);
    }

    public TransformMatrix getTransform(Axis axis, boolean rotatedOrientation) {
        TransformMatrix t = TransformMatrix.IDENTITY;
        if (isSheared())
            t = applyShear(t, getShear(), axis, rotatedOrientation);
        if (isAnamorphic())
            t = applyAnamorphic(t, getSize(), axis);
        return !t.isIdentity() ? t : null;
    }

    private TransformMatrix applyShear(TransformMatrix t0, double shear, Axis axis, boolean rotatedOrientation) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double s = -Math.tan(Math.toRadians(shear * 90));
        double sx, sy;
        if (axis.isVertical()) {
            sx = 0;
            sy = s;
        } else {
            sx = rotatedOrientation ? -s : s;
            sy = 0;
        }
        t.shear(sx, sy);
        return t;
    }

    private TransformMatrix applyAnamorphic(TransformMatrix t0, Extent size, Axis axis) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double sx = size.getDimension(Axis.HORIZONTAL) / size.getDimension(Axis.VERTICAL);
        double sy = 1;
        t.scale(sx, sy);
        return t;
    }

    public Rectangle[] getGlyphBounds(String text, String script, String language) {
        return ls.getGlyphBounds(key, text, script, language);
    }

    public int[] getGlyphs(String text, String script, String language) {
        return ls.getGlyphs(key, text, script, language);
    }

    public Font getScaledFont(double scale) {
        return cache.getScaledFont(this, scale);
    }

}
