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

package com.skynav.ttpe.style;

import java.util.Arrays;
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

    private static final Annotation defaultAnnotation                           = null;
    private static final InlineAlignment defaultAnnotationAlign                 = InlineAlignment.AUTO;
    private static final double defaultAnnotationOffset                         = 0;
    private static final AnnotationOverflow defaultAnnotationOverflow           = AnnotationOverflow.SHIFT_RUBY;
    private static final AnnotationOverhang defaultAnnotationOverhang           = AnnotationOverhang.ALLOW;
    private static final AnnotationOverhangClass defaultAnnotationOverhangClass = AnnotationOverhangClass.AUTO;
    private static final AnnotationPosition defaultAnnotationPosition           = AnnotationPosition.AUTO;
    private static final AnnotationReserve defaultAnnotationReserve             = AnnotationReserve.NONE;
    private static final Image defaultBackgroundImage                           = Image.NONE;
    private static final Color defaultBackgroundColor                           = Color.TRANSPARENT;
    private static final double defaultBPD                                      = -1;
    private static final Color defaultColor                                     = Color.WHITE;
    private static final Combination defaultCombination                         = Combination.NONE;
    private static final Display defaultDisplay                                 = Display.AUTO;
    private static final BlockAlignment defaultDisplayAlign                     = BlockAlignment.BEFORE;
    private static final Emphasis defaultEmphasis                               = Emphasis.NONE;
    private static final Extent defaultExtent                                   = Extent.EMPTY;
    private static final Extent defaultExternalExtent                           = new Extent(1280, 720);
    private static final List<String> defaultFontFamilies                       = FontKey.DEFAULT_FAMILIES;
    private static final Set<FontFeature> defaultFontFeatures                   = FontKey.DEFAULT_FEATURES;
    private static final FontKerning defaultFontKerning                         = FontKerning.NORMAL;
    private static final double defaultFontShear                                = 0;
    private static final Extent defaultFontSize                                 = FontKey.DEFAULT_SIZE;
    private static final FontStyle defaultFontStyle                             = FontKey.DEFAULT_STYLE;
    private static final FontWeight defaultFontWeight                           = FontKey.DEFAULT_WEIGHT;
    private static final double defaultIPD                                      = -1;
    private static final String defaultLanguage                                 = FontKey.DEFAULT_LANGUAGE;
    private static final double defaultLineHeight                               = defaultFontSize.getHeight() * 1.25;
    private static final double defaultLineShear                                = 0;
    private static final double defaultOpacity                                  = 1;
    private static final Point defaultOrigin                                    = Point.ZERO;
    private static final Orientation defaultOrientation                         = Orientation.ROTATE000;
    private static final Outline defaultOutline                                 = Outline.NONE;
    private static final Overflow defaultOverflow                               = Overflow.HIDDEN;
    private static final double[] defaultPadding                                = new double[4];
    private static final String defaultPositionComponents                       = "center";
    private static final String defaultScript                                   = "auto";
    private static final double defaultShear                                    = 0;
    private static final Visibility defaultVisibility                           = Visibility.VISIBLE;
    private static final Whitespace defaultWhitespace                           = Whitespace.DEFAULT;
    private static final InlineAlignment defaultTextAlign                       = InlineAlignment.START;
    private static final TransformMatrix defaultTransform                       = TransformMatrix.IDENTITY;
    private static final WritingMode defaultWritingMode                         = WritingMode.LRTB;
    private static final Wrap defaultWrap                                       = Wrap.WRAP;

    private Annotation annotation                                               = defaultAnnotation;
    private InlineAlignment annotationAlign                                     = defaultAnnotationAlign;
    private double annotationOffset                                             = defaultAnnotationOffset;
    private AnnotationOverflow annotationOverflow                               = defaultAnnotationOverflow;
    private AnnotationOverhang annotationOverhang                               = defaultAnnotationOverhang;
    private AnnotationOverhangClass annotationOverhangClass                     = defaultAnnotationOverhangClass;
    private AnnotationPosition annotationPosition                               = defaultAnnotationPosition;
    private AnnotationReserve annotationReserve                                 = defaultAnnotationReserve;
    private Image backgroundImage                                               = defaultBackgroundImage;
    private Color backgroundColor                                               = defaultBackgroundColor;
    private double bpd                                                          = defaultBPD;
    private Color color                                                         = defaultColor;
    private Combination combination                                             = defaultCombination;
    private Display display                                                     = defaultDisplay;
    private BlockAlignment displayAlign                                         = defaultDisplayAlign;
    private Emphasis emphasis                                                   = defaultEmphasis;
    private Extent extent                                                       = defaultExtent;
    private Extent externalExtent                                               = defaultExternalExtent;
    private List<String> fontFamilies                                           = defaultFontFamilies;
    private Set<FontFeature> fontFeatures                                       = defaultFontFeatures;
    private FontKerning fontKerning                                             = defaultFontKerning;
    private double fontShear                                                    = defaultFontShear;
    private Extent fontSize                                                     = defaultFontSize;
    private FontStyle fontStyle                                                 = defaultFontStyle;
    private FontWeight fontWeight                                               = defaultFontWeight;
    private double ipd                                                          = defaultIPD;
    private String language                                                     = defaultLanguage;
    private double lineHeight                                                   = defaultLineHeight;
    private double lineShear                                                    = defaultLineShear;
    private double opacity                                                      = defaultOpacity;
    private Point origin                                                        = defaultOrigin;
    private Orientation orientation                                             = defaultOrientation;
    private Outline outline                                                     = defaultOutline;
    private Overflow overflow                                                   = defaultOverflow;
    private double[] padding                                                    = defaultPadding;
    private String positionComponents                                           = defaultPositionComponents;
    private String script                                                       = defaultScript;
    private double shear                                                        = defaultShear;
    private Whitespace whitespace                                               = defaultWhitespace;
    private InlineAlignment textAlign                                           = defaultTextAlign;
    private TransformMatrix transform                                           = defaultTransform;
    private Visibility visibility                                               = defaultVisibility;
    private WritingMode writingMode                                             = defaultWritingMode;
    private Wrap wrap                                                           = defaultWrap;

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

    public void setBPD(double bpd) {
        this.bpd = bpd;
    }

    public double getBPD() {
        return bpd;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public static Color getDefaultBackgroundColor() {
        return defaultBackgroundColor;
    }

    public void setBackgroundImage(Image backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    public Image getBackgroundImage() {
        return backgroundImage;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public static Color getDefaultColor() {
        return defaultColor;
    }

    public void setCombination(Combination combination) {
        this.combination = combination;
    }

    public Combination getCombination() {
        return combination;
    }

    public void setDisplay(Display display) {
        this.display = display;
    }

    public Display getDisplay() {
        return display;
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

    public static List<String> getDefaultFontFamilies() {
        return defaultFontFamilies;
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

    public void setIPD(double ipd) {
        this.ipd = ipd;
    }

    public double getIPD() {
        return ipd;
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

    public void setLineShear(double lineShear) {
        this.lineShear = lineShear;
    }

    public double getLineShear() {
        return lineShear;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public double getOpacity() {
        return opacity;
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

    public void setPadding(double[] padding) {
        this.padding = Arrays.copyOf(padding, padding.length);
    }

    public double[] getPadding() {
        return Arrays.copyOf(padding, padding.length);
    }

    public void setPositionComponents(String positionComponents) {
        this.positionComponents = positionComponents;
    }

    public String getPositionComponents() {
        return positionComponents;
    }

    public void setScript(String script) {
        this.script = script;
    }

    public String getScript() {
        return script;
    }

    public void setShear(double shear) {
        this.shear = shear;
    }

    public double getShear() {
        return shear;
    }

    public void setWhitespace(Whitespace whitespace) {
        this.whitespace = whitespace;
    }

    public Whitespace getWhitespace() {
        return whitespace;
    }

    public static Whitespace getDefaultWhitespace() {
        return defaultWhitespace;
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

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public Visibility getVisibility() {
        return visibility;
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
