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

package com.skynav.ttpe.style;

import java.util.List;
import java.util.Set;

import com.skynav.ttpe.fonts.Combination;
import com.skynav.ttpe.fonts.FontFeature;
import com.skynav.ttpe.fonts.FontKerning;
import com.skynav.ttpe.fonts.FontKey;
import com.skynav.ttpe.fonts.FontStyle;
import com.skynav.ttpe.fonts.FontWeight;
import com.skynav.ttpe.fonts.Orientation;
import com.skynav.ttpe.geometry.Extent;
import com.skynav.ttpe.geometry.Overflow;
import com.skynav.ttpe.geometry.Point;
import com.skynav.ttpe.geometry.TransformMatrix;
import com.skynav.ttpe.geometry.WritingMode;

public class Defaults extends com.skynav.ttpe.parameter.Defaults {

    public static final Annotation defaultAnnotation                           = null;
    public static final InlineAlignment defaultAnnotationAlign                 = InlineAlignment.AUTO;
    public static final double defaultAnnotationOffset                         = 0;
    public static final AnnotationOverflow defaultAnnotationOverflow           = AnnotationOverflow.SHIFT_RUBY;
    public static final AnnotationOverhang defaultAnnotationOverhang           = AnnotationOverhang.ALLOW;
    public static final AnnotationOverhangClass defaultAnnotationOverhangClass = AnnotationOverhangClass.AUTO;
    public static final AnnotationPosition defaultAnnotationPosition           = AnnotationPosition.AUTO;
    public static final AnnotationReserve defaultAnnotationReserve             = AnnotationReserve.NONE;
    public static final Color defaultBackgroundColor                           = Color.TRANSPARENT;
    public static final Color defaultColor                                     = Color.YELLOW;
    public static final Combination defaultCombination                         = Combination.NONE;
    public static final BlockAlignment defaultDisplayAlign                     = BlockAlignment.BEFORE;
    public static final Emphasis defaultEmphasis                               = Emphasis.NONE;
    public static final Extent defaultExtent                                   = Extent.EMPTY;
    public static final Extent defaultExternalExtent                           = new Extent(1280, 720);
    public static final List<String> defaultFontFamilies                       = FontKey.DEFAULT_FAMILIES;
    public static final Set<FontFeature> defaultFontFeatures                   = FontKey.DEFAULT_FEATURES;
    public static final FontKerning defaultFontKerning                         = FontKerning.NORMAL;
    public static final double defaultFontShear                                = 0;
    public static final Extent defaultFontSize                                 = FontKey.DEFAULT_SIZE;
    public static final FontStyle defaultFontStyle                             = FontKey.DEFAULT_STYLE;
    public static final FontWeight defaultFontWeight                           = FontKey.DEFAULT_WEIGHT;
    public static final String defaultLanguage                                 = FontKey.DEFAULT_LANGUAGE;
    public static final double defaultLineHeight                               = defaultFontSize.getHeight() * 1.25;
    public static final String defaultPositionComponents                       = "center";
    public static final Point defaultOrigin                                    = Point.ZERO;
    public static final Orientation defaultOrientation                         = Orientation.ROTATE000;
    public static final Outline defaultOutline                                 = Outline.NONE;
    public static final Overflow defaultOverflow                               = Overflow.HIDDEN;
    public static final String defaultScript                                   = "auto";
    public static final Whitespace defaultWhitespace                           = Whitespace.DEFAULT;
    public static final InlineAlignment defaultTextAlign                       = InlineAlignment.START;
    public static final TransformMatrix defaultTransform                       = TransformMatrix.IDENTITY;
    public static final WritingMode defaultWritingMode                         = WritingMode.LRTB;
    public static final Wrap defaultWrap                                       = Wrap.WRAP;

    private Annotation annotation                                              = defaultAnnotation;
    private InlineAlignment annotationAlign                                    = defaultAnnotationAlign;
    private double annotationOffset                                            = defaultAnnotationOffset;
    private AnnotationOverflow annotationOverflow                              = defaultAnnotationOverflow;
    private AnnotationOverhang annotationOverhang                              = defaultAnnotationOverhang;
    private AnnotationOverhangClass annotationOverhangClass                    = defaultAnnotationOverhangClass;
    private AnnotationPosition annotationPosition                              = defaultAnnotationPosition;
    private AnnotationReserve annotationReserve                                = defaultAnnotationReserve;
    private Color backgroundColor                                              = defaultBackgroundColor;
    private Color color                                                        = defaultColor;
    private Combination combination                                            = defaultCombination;
    private BlockAlignment displayAlign                                        = defaultDisplayAlign;
    private Emphasis emphasis                                                  = defaultEmphasis;
    private Extent extent                                                      = defaultExtent;
    private Extent externalExtent                                              = defaultExternalExtent;
    private List<String> fontFamilies                                          = defaultFontFamilies;
    private Set<FontFeature> fontFeatures                                      = defaultFontFeatures;
    private FontKerning fontKerning                                            = defaultFontKerning;
    private double fontShear                                                   = defaultFontShear;
    private Extent fontSize                                                    = defaultFontSize;
    private FontStyle fontStyle                                                = defaultFontStyle;
    private FontWeight fontWeight                                              = defaultFontWeight;
    private String language                                                    = defaultLanguage;
    private double lineHeight                                                  = defaultLineHeight;
    private String positionComponents                                          = defaultPositionComponents;
    private Point origin                                                       = defaultOrigin;
    private Orientation orientation                                            = defaultOrientation;
    private Outline outline                                                    = defaultOutline;
    private Overflow overflow                                                  = defaultOverflow;
    private String script                                                      = defaultScript;
    private Whitespace whitespace                                              = defaultWhitespace;
    private InlineAlignment textAlign                                          = defaultTextAlign;
    private TransformMatrix transform                                          = defaultTransform;
    private WritingMode writingMode                                            = defaultWritingMode;
    private Wrap wrap                                                          = defaultWrap;

    public Defaults() {
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotationAlign(InlineAlignment annotationAlign) {
        this.annotationAlign = annotationAlign;
    }

    public InlineAlignment getAnnotationAlign() {
        return annotationAlign;
    }

    public void setAnnotationOffset(double annotationOffset) {
        this.annotationOffset = annotationOffset;
    }

    public double getAnnotationOffset() {
        return annotationOffset;
    }

    public void setAnnotationOverflow(AnnotationOverflow annotationOverflow) {
        this.annotationOverflow = annotationOverflow;
    }

    public AnnotationOverflow getAnnotationOverflow() {
        return annotationOverflow;
    }

    public void setAnnotationOverhang(AnnotationOverhang annotationOverhang) {
        this.annotationOverhang = annotationOverhang;
    }

    public AnnotationOverhang getAnnotationOverhang() {
        return annotationOverhang;
    }

    public void setAnnotationOverhangClass(AnnotationOverhangClass annotationOverhangClass) {
        this.annotationOverhangClass = annotationOverhangClass;
    }

    public AnnotationOverhangClass getAnnotationOverhangClass() {
        return annotationOverhangClass;
    }

    public void setAnnotationPosition(AnnotationPosition annotationPosition) {
        this.annotationPosition = annotationPosition;
    }

    public AnnotationPosition getAnnotationPosition() {
        return annotationPosition;
    }

    public void setAnnotationReserve(AnnotationReserve annotationReserve) {
        this.annotationReserve = annotationReserve;
    }

    public AnnotationReserve getAnnotationReserve() {
        return annotationReserve;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void setCombination(Combination combination) {
        this.combination = combination;
    }

    public Combination getCombination() {
        return combination;
    }

    public void setDisplayAlign(BlockAlignment displayAlign) {
        this.displayAlign = displayAlign;
    }

    public BlockAlignment getDisplayAlign() {
        return displayAlign;
    }

    public void setEmphasis(Emphasis emphasis) {
        this.emphasis = emphasis;
    }

    public Emphasis getEmphasis() {
        return emphasis;
    }

    public void setExtent(Extent extent) {
        this.extent = extent;
    }

    public Extent getExtent() {
        return extent;
    }

    public void setExternalExtent(Extent externalExtent) {
        this.externalExtent = externalExtent;
    }

    public Extent getExternalExtent() {
        return externalExtent;
    }

    public void setFontFamilies(List<String> fontFamilies) {
        this.fontFamilies = fontFamilies;
    }

    public List<String> getFontFamilies() {
        return fontFamilies;
    }

    public void setFontFeatures(Set<FontFeature> fontFeatures) {
        this.fontFeatures = fontFeatures;
    }

    public Set<FontFeature> getFontFeatures() {
        return fontFeatures;
    }

    public void setFontKerning(FontKerning fontKerning) {
        this.fontKerning = fontKerning;
    }

    public FontKerning getFontKerning() {
        return fontKerning;
    }

    public void setFontShear(double fontShear) {
        this.fontShear = fontShear;
    }

    public double getFontShear() {
        return fontShear;
    }

    public void setFontSize(Extent fontSize) {
        this.fontSize = fontSize;
    }

    public Extent getFontSize() {
        return fontSize;
    }

    public void setFontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
    }

    public FontStyle getFontStyle() {
        return fontStyle;
    }

    public void setFontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight;
    }

    public FontWeight getFontWeight() {
        return fontWeight;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }

    public void setLineHeight(double lineHeight) {
        this.lineHeight = lineHeight;
    }

    public double getLineHeight() {
        return lineHeight;
    }

    public void setPositionComponents(String positionComponents) {
        this.positionComponents = positionComponents;
    }

    public String getPositionComponents() {
        return positionComponents;
    }

    public void setOrigin(Point origin) {
        this.origin = origin;
    }

    public Point getOrigin() {
        return origin;
    }

    public void setOrientation(Orientation orientation) {
        this.orientation = orientation;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    public void setOutline(Outline outline) {
        this.outline = outline;
    }

    public Outline getOutline() {
        return outline;
    }

    public void setOverflow(Overflow overflow) {
        this.overflow = overflow;
    }

    public Overflow getOverflow() {
        return overflow;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setWhitespace(Whitespace whitespace) {
        this.whitespace = whitespace;
    }

    public Whitespace getWhitespace() {
        return whitespace;
    }

    public void setTextAlign(InlineAlignment textAlign) {
        this.textAlign = textAlign;
    }

    public InlineAlignment getTextAlign() {
        return textAlign;
    }

    public void setTransform(TransformMatrix transform) {
        this.transform = transform;
    }

    public TransformMatrix getTransform() {
        return transform;
    }

    public void setWritingMode(WritingMode writingMode) {
        this.writingMode = writingMode;
    }

    public WritingMode getWritingMode() {
        return writingMode;
    }

    public void setWrap(Wrap wrap) {
        this.wrap = wrap;
    }

    public Wrap getWrap() {
        return wrap;
    }

}
