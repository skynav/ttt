/*
 * Copyright 2015-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.imsc;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.imsc11.ebuttd.MultiRowAlign;
import com.skynav.ttv.model.ttml2.ttd.DisplayAlign;
import com.skynav.ttv.model.ttml2.ttd.WritingMode;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.TextOutline;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.imsc.style.FillLineGapVerifier;
import com.skynav.ttv.verifier.imsc.style.ForcedDisplayVerifier;
import com.skynav.ttv.verifier.imsc.style.LinePaddingVerifier;
import com.skynav.ttv.verifier.imsc.style.MultiRowAlignVerifier;
import com.skynav.ttv.verifier.smpte.ST20522010TTML2StyleVerifier;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Outlines;

import static com.skynav.ttv.model.imsc.IMSC11.Constants.*;

public class IMSC11StyleVerifier extends ST20522010TTML2StyleVerifier {

    public static final QName fillLineGapAttributeName          = new QName(NAMESPACE_IMSC11_STYLING,ATTR_FILL_LINE_GAP);
    public static final QName forcedDisplayAttributeName        = new QName(NAMESPACE_IMSC11_STYLING,ATTR_FORCED_DISPLAY);
    public static final QName linePaddingAttributeName          = new QName(NAMESPACE_EBUTT_STYLING,ATTR_LINE_PADDING);
    public static final QName multiRowAlignAttributeName        = new QName(NAMESPACE_EBUTT_STYLING,ATTR_MULTI_ROW_ALIGN);

    private static final Object[][] styleAccessorMap            = new Object[][] {
        {
            fillLineGapAttributeName,
            "FillLineGap",
            Boolean.class,
            FillLineGapVerifier.class,
            Integer.valueOf(APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.FALSE.toString(),
            Animatability.Discrete,
        },
        {
            forcedDisplayAttributeName,
            "ForcedDisplay",
            Boolean.class,
            ForcedDisplayVerifier.class,
            Integer.valueOf(APPLIES_TO_BODY|APPLIES_TO_DIV|APPLIES_TO_P|APPLIES_TO_SPAN|APPLIES_TO_REGION),
            Boolean.FALSE,
            Boolean.TRUE,
            Boolean.FALSE,
            Boolean.FALSE.toString(),
            Animatability.Discrete,
        },
        {
            linePaddingAttributeName,
            "LinePadding",
            String.class,
            LinePaddingVerifier.class,
            Integer.valueOf(APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.TRUE,
            "0c",
            null,
            Animatability.Discrete,
        },
        {
            multiRowAlignAttributeName,
            "MultiRowAlign",
            MultiRowAlign.class,
            MultiRowAlignVerifier.class,
            Integer.valueOf(APPLIES_TO_P),
            Boolean.FALSE,
            Boolean.FALSE,
            MultiRowAlign.AUTO,
            MultiRowAlign.AUTO.value(),
            Animatability.Discrete,
        },
    };

    public IMSC11StyleVerifier(Model model) {
        super(model);
    }

    @Override
    protected void populateAccessors(Map<QName, StyleAccessor> accessors) {
        super.populateAccessors(accessors);
        populateAccessors(accessors, styleAccessorMap);
    }

    @Override
    public boolean isNegativeLengthPermitted(QName eltName, QName styleName) {
        return false;
    }

    @Override
    public boolean isLengthUnitsPermitted(QName eltName, QName styleName, Length.Unit units) {
        if (units == Length.Unit.Cell)
            return styleName.equals(linePaddingAttributeName);
        else
            return true;
    }

    @Override
    protected boolean verifyAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        if (!super.verifyAttributeItem(content, locator, sa, context))
            return false;
        else {
            boolean failed = false;
            String profile = (String) context.getResourceState(getModel().makeResourceStateName("profile"));
            if (profile == null)
                profile = PROFILE_TEXT_ABSOLUTE;
            QName name = sa.getStyleName();
            if (name.equals(extentAttributeName)) {
                failed = !verifyExtentOrOriginAttributeItem(content, locator, sa, context);
            } else if (name.equals(originAttributeName)) {
                failed = !verifyExtentOrOriginAttributeItem(content, locator, sa, context);
            } else if (profile.equals(PROFILE_TEXT_ABSOLUTE)) {
                if (!verifyAttributePermittedInTextProfile(content, locator, sa, context)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Attribute ''{0}'' prohibited on ''{1}'' in {2} text profile.",
                        name, context.getBindingElementName(content), getModel().getName()));
                    failed = true;
                } else {
                    if (name.equals(displayAlignAttributeName))
                        failed = !verifyDisplayAlignAttributeItem(content, locator, sa, context);
                    else if (name.equals(fontSizeAttributeName))
                        failed = !verifyFontSizeAttributeItem(content, locator, sa, context);
                    else if (name.equals(lineHeightAttributeName))
                        failed = !verifyLineHeightAttributeItem(content, locator, sa, context);
                    else if (name.equals(textOutlineAttributeName))
                        failed = !verifyTextOutlineAttributeItem(content, locator, sa, context);
                }
            } else if (profile.equals(PROFILE_IMAGE_ABSOLUTE)) {
                if (!verifyAttributePermittedInImageProfile(content, locator, sa, context)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Attribute ''{0}'' prohibited on ''{1}'' in {2} image profile.",
                        name, context.getBindingElementName(content), getModel().getName()));
                    failed = true;
                } else {
                    if (name.equals(writingModeAttributeName))
                        failed = !verifyWritingModeAttributeItem(content, locator, sa, context);
                }
            }
            return !failed;
        }
    }

    private boolean verifyExtentOrOriginAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        boolean failed = false;
        Object value = sa.getStyleValue(content);
        QName name = sa.getStyleName();
        if (value != null) {
            assert value instanceof String;
            String s = (String) value;
            Location location = new Location(content, context.getBindingElementName(content), name, locator);
            Integer[] minMax = new Integer[] { 2, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Allow, MixedUnitsTreatment.Allow };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(s, location, context, minMax, treatments, lengths)) {
                for (int i = 0, n = lengths.size(); i < n; ++i) {
                    Length l = lengths.get(i);
                    if (l != null) {
                        if (!isPermittedLengthUnit(content, name, l, context)) {
                            Reporter reporter = context.getReporter();
                            reporter.logError(reporter.message(locator,
                                "*KEY*", "Prohibited unit ''{0}'' used in length component ''{1}'' on {2}.", l.getUnits(), l, name));
                            failed = true;
                        }
                    }
                }
            }
        }
        return !failed;
    }

    private boolean isPermittedLengthUnit(Object content, QName styleName, Length length, VerifierContext context) {
        Length.Unit u = length.getUnits();
        if (u == Length.Unit.Pixel)
            return true;
        else if (u == Length.Unit.Percentage)
            return true;
        else if ((u == Length.Unit.ViewportWidth) || (u == Length.Unit.ViewportHeight)) {
            if (IMSC11SemanticsVerifier.isIMSCTextProfile(context)) {
                if (styleName.equals(extentAttributeName))
                    return true;
                else if (styleName.equals(originAttributeName))
                    return false;
                else if (styleName.equals(positionAttributeName))
                    return true;
                else
                    return true; // [TBD] - need to verify
            } else
                return false;
        } else {
            if (styleName.equals(extentAttributeName))
                return false;
            else if (styleName.equals(originAttributeName))
                return false;
            else if (styleName.equals(positionAttributeName))
                return false;
            else
                return true; // [TBD] - need to verify
        }
    }

    private boolean verifyAttributePermittedInImageProfile(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        if (value != null) {
            QName name = sa.getStyleName();
            // TT Style Attributes
            if (hasNonAudioStyleNamespace(name)) {
                if (name.equals(backgroundClipAttributeName))
                    return false;
                else if (name.equals(backgroundExtentAttributeName))
                    return false;
                else if (name.equals(backgroundImageAttributeName))
                    return false;
                else if (name.equals(backgroundOriginAttributeName))
                    return false;
                else if (name.equals(backgroundPositionAttributeName))
                    return false;
                else if (name.equals(backgroundRepeatAttributeName))
                    return false;
                else if (name.equals(borderAttributeName))
                    return false;
                else if (name.equals(bpdAttributeName))
                    return false;
                else if (name.equals(colorAttributeName))
                    return false;
                else if (name.equals(directionAttributeName))
                    return false;
                else if (name.equals(displayAlignAttributeName))
                    return false;
                else if (name.equals(fontFamilyAttributeName))
                    return false;
                else if (name.equals(fontKerningAttributeName))
                    return false;
                else if (name.equals(fontSelectionStrategyAttributeName))
                    return false;
                else if (name.equals(fontShearAttributeName))
                    return false;
                else if (name.equals(fontSizeAttributeName))
                    return false;
                else if (name.equals(fontStyleAttributeName))
                    return false;
                else if (name.equals(fontVariantAttributeName))
                    return false;
                else if (name.equals(fontWeightAttributeName))
                    return false;
                else if (name.equals(ipdAttributeName))
                    return false;
                else if (name.equals(letterSpacingAttributeName))
                    return false;
                else if (name.equals(lineHeightAttributeName))
                    return false;
                else if (name.equals(lineShearAttributeName))
                    return false;
                else if (name.equals(paddingAttributeName))
                    return false;
                else if (name.equals(positionAttributeName))
                    return false;
                else if (name.equals(rubyAttributeName))
                    return false;
                else if (name.equals(rubyAlignAttributeName))
                    return false;
                else if (name.equals(rubyPositionAttributeName))
                    return false;
                else if (name.equals(rubyReserveAttributeName))
                    return false;
                else if (name.equals(shearAttributeName))
                    return false;
                else if (name.equals(textAlignAttributeName))
                    return false;
                else if (name.equals(textCombineAttributeName))
                    return false;
                else if (name.equals(textDecorationAttributeName))
                    return false;
                else if (name.equals(textEmphasisAttributeName))
                    return false;
                else if (name.equals(textOutlineAttributeName))
                    return false;
                else if (name.equals(textOrientationAttributeName))
                    return false;
                else if (name.equals(textShadowAttributeName))
                    return false;
                else if (name.equals(unicodeBidiAttributeName))
                    return false;
                else if (name.equals(wrapOptionAttributeName))
                    return false;
            }
            // TT Audio Style Attributes
            if (hasAudioStyleNamespace(name)) {
                if (name.equals(gainAttributeName))
                    return false;
                else if (name.equals(panAttributeName))
                    return false;
                else if (name.equals(pitchAttributeName))
                    return false;
                else if (name.equals(speakAttributeName))
                    return false;
            }
        }
        return true;
    }

    private boolean verifyAttributePermittedInTextProfile(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        if (value != null) {
            QName name = sa.getStyleName();
            // TT Style Attributes
            if (hasNonAudioStyleNamespace(name)) {
                if (name.equals(backgroundClipAttributeName))
                    return false;
                else if (name.equals(backgroundExtentAttributeName))
                    return false;
                else if (name.equals(backgroundImageAttributeName))
                    return false;
                else if (name.equals(backgroundOriginAttributeName))
                    return false;
                else if (name.equals(backgroundPositionAttributeName))
                    return false;
                else if (name.equals(backgroundRepeatAttributeName))
                    return false;
                else if (name.equals(borderAttributeName))
                    return false;
                else if (name.equals(bpdAttributeName))
                    return false;
                else if (name.equals(fontKerningAttributeName))
                    return false;
                else if (name.equals(fontSelectionStrategyAttributeName))
                    return false;
                else if (name.equals(fontShearAttributeName))
                    return false;
                else if (name.equals(fontVariantAttributeName))
                    return false;
                else if (name.equals(ipdAttributeName))
                    return false;
                else if (name.equals(letterSpacingAttributeName))
                    return false;
                else if (name.equals(lineShearAttributeName))
                    return false;
                else if (name.equals(textOrientationAttributeName))
                    return false;
            }
            // TT Audio Style Attributes
            if (hasAudioStyleNamespace(name)) {
                if (name.equals(gainAttributeName))
                    return false;
                else if (name.equals(panAttributeName))
                    return false;
                else if (name.equals(pitchAttributeName))
                    return false;
                else if (name.equals(speakAttributeName))
                    return false;
            }
        }
        return true;
    }

    private boolean verifyDisplayAlignAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        boolean failed = false;
        String profile = (String) context.getResourceState(getModel().makeResourceStateName("profile"));
        if (profile == null)
            profile = PROFILE_TEXT_ABSOLUTE;
        QName name = sa.getStyleName();
        Object value = sa.getStyleValue(content);
        if (value != null) {
            assert value instanceof DisplayAlign;
            DisplayAlign align = (DisplayAlign) value;
            if (profile.equals(PROFILE_TEXT_ABSOLUTE)) {
                Reporter reporter = context.getReporter();
                if (isBlock(content, context)) {
                    reporter.logError(reporter.message(locator,
                        "*KEY*", "Attribute ''{0}'' prohibited on ''{1}'' in {2} text profile.",
                        name, context.getBindingElementName(content), getModel().getName()));
                    failed = true;
                } else if (align == DisplayAlign.JUSTIFY) {
                    reporter.logError(reporter.message(locator,
                        "*Key*", "Prohibited value ''{0}'' on ''{1}'' in {2} text profile.",
                        align.value(), name, getModel().getName()));
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private boolean verifyFontSizeAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        QName name = sa.getStyleName();
        if (value != null) {
            assert value instanceof String;
            String s = (String) value;
            Location location = new Location(content, context.getBindingElementName(content), name, locator);
            Integer[] minMax = new Integer[] { 1, 2 };
            Object[] treatments = new Object[] { NegativeTreatment.Error, MixedUnitsTreatment.Error };
            List<Length> lengths = new java.util.ArrayList<Length>();
            if (Lengths.isLengths(s, location, context, minMax, treatments, lengths)) {
                if (lengths.size() > 1) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator, "*KEY*", "Anamorphic font size ''{0}'' prohibited.", s));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyLineHeightAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        // use of 'normal' (either explicitly or by initial value assignment) cannot be performed here,
        // but must be performed post-ISD transformation; see TTXV for this test logic
        return true;
    }

    private boolean verifyTextOutlineAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        QName name = sa.getStyleName();
        if (value != null) {
            assert value instanceof String;
            String s = (String) value;
            Location location = new Location(content, context.getBindingElementName(content), name, locator);
            TextOutline[] outline = new TextOutline[1];
            if (Outlines.isOutline(s, location, context, outline)) {
                assert outline.length > 0;
                if (outline[0] != null) {
                    Length b = outline[0].getBlur();
                    if ((b != null) && (b.getValue() > 0)) {
                        Reporter reporter = context.getReporter();
                        reporter.logError(reporter.message(locator, "*Key*", "Non-zero blur ''{0}'' prohibited.", s));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean verifyWritingModeAttributeItem(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        boolean failed = false;
        String profile = (String) context.getResourceState(getModel().makeResourceStateName("profile"));
        if (profile == null)
            profile = PROFILE_TEXT_ABSOLUTE;
        Object value = sa.getStyleValue(content);
        QName name = sa.getStyleName();
        if (value != null) {
            assert value instanceof WritingMode;
            WritingMode wm = (WritingMode) value;
            if (profile.equals(PROFILE_IMAGE_ABSOLUTE)) {
                if (isVertical(wm)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator,
                        "*Key*", "Prohibited value ''{0}'' on ''{1}'' in {2} image profile.", wm.value(), name, getModel().getName()));
                    failed = true;
                }
            }
        }
        return !failed;
    }

    private static final boolean isVertical(WritingMode wm) {
        if (wm == WritingMode.TBLR)
            return true;
        else if (wm == WritingMode.TBRL)
            return true;
        else if (wm == WritingMode.TB)
            return true;
        else
            return false;
    }

}
