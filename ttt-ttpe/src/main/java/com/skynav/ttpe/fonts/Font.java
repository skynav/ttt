/*
 * Copyright 2014-21 Skynav, Inc. All rights reserved.
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

import java.util.BitSet;
import java.util.Collection;
import java.util.SortedSet;

import com.skynav.ttpe.geometry.Axis;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Shear;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttv.util.Reporter;

public class Font {

    private FontCache cache;
    private FontKey key;
    private String source;
    private FontState ls;

    public Font(FontCache cache, FontKey key, String source, BitSet forcePath, Reporter reporter) {
        this.cache = cache;
        this.key = key;
        this.source = source;
        this.ls = cache.getLoadedState(source, forcePath, reporter);
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
            t = applyShear(t, Shear.toAngle(getShear()), axis, rotatedOrientation);
        if (isAnamorphic())
            t = applyAnamorphic(t, getSize(), axis);
        return !t.isIdentity() ? t : null;
    }

    private TransformMatrix applyShear(TransformMatrix t0, double shearAngle, Axis axis, boolean rotatedOrientation) {
        TransformMatrix t = (TransformMatrix) t0.clone();
        double s = -Math.tan(Math.toRadians(shearAngle));
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

    public Font getScaledFont(double scale) {
        return cache.getScaledFont(this, scale);
    }

    public boolean hasGlyphMapping(String text, int prevIndex, int index, int nextIndex) {
        return ls.hasGlyphMapping(key, text, prevIndex, index, nextIndex);
    }

    public GlyphMapping getGlyphMapping(String text, SortedSet<FontFeature> features) {
        return ls.getGlyphMapping(key, text, features);
    }

    public GlyphMapping maybeReverse(GlyphMapping mapping, boolean mirror) {
        return ls.maybeReverse(key, mapping, mirror);
    }

    public int getAdvance(GlyphMapping gm) {
        return ls.getAdvance(key, gm);
    }

    public int[] getAdvances(GlyphMapping gm) {
        return ls.getAdvances(key, gm);
    }

    public double getScaledAdvance(GlyphMapping gm) {
        return ls.getScaledAdvance(key, gm);
    }

    public double[] getScaledAdvances(GlyphMapping gm) {
        return ls.getScaledAdvances(key, gm);
    }

    public double[][] getScaledAdjustments(GlyphMapping gm) {
        return ls.getScaledAdjustments(key, gm);
    }

    public boolean containsPUAMapping(String glyphsAsText) {
        return ls.containsPUAMapping(key, glyphsAsText);
    }

    public String getGlyphsPath(String glyphsAsText, Axis resolvedAxis, double[] advances) {
        return ls.getGlyphsPath(key, glyphsAsText, resolvedAxis, advances);
    }

}
