/*
 * Copyright 2015 Skynav, Inc. All rights reserved.
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

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.ttd.WritingMode;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.TextOutline;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.smpte.ST20522010StyleVerifier;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;
import com.skynav.ttv.verifier.util.Outline;

import static com.skynav.ttv.model.imsc.IMSC1.Constants.*;

public class IMSC1StyleVerifier extends ST20522010StyleVerifier {

    public static final QName forcedDisplayAttributeName        = new QName(NAMESPACE,"forcedDisplay");
    public static final QName linePaddingAttributeName          = new QName(NAMESPACE_EBUTT_STYLING,"linePadding");
    public static final QName multiRowAlignAttributeName        = new QName(NAMESPACE_EBUTT_STYLING,"multiRowAlign");

    public IMSC1StyleVerifier(Model model) {
        super(model);
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
                    if (name.equals(fontSizeAttributeName))
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
                        Length.Unit u = l.getUnits();
                        if ((u != Length.Unit.Pixel) && (u != Length.Unit.Percentage)) {
                            Reporter reporter = context.getReporter();
                            reporter.logError(reporter.message(locator,
                                "*KEY*", "Prohibited unit ''{0}'' used in length component ''{1}'' on {2}.", u, l, name));
                            failed = true;
                        }
                    }
                }
            }
        }
        return !failed;
    }

    private boolean verifyAttributePermittedInImageProfile(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        Object value = sa.getStyleValue(content);
        if (value != null) {
            QName name = sa.getStyleName();
            if (name.equals(colorAttributeName))
                return false;
            else if (name.equals(directionAttributeName))
                return false;
            else if (name.equals(displayAlignAttributeName))
                return false;
            else if (name.equals(fontFamilyAttributeName))
                return false;
            else if (name.equals(fontSizeAttributeName))
                return false;
            else if (name.equals(fontStyleAttributeName))
                return false;
            else if (name.equals(fontWeightAttributeName))
                return false;
            else if (name.equals(lineHeightAttributeName))
                return false;
            else if (name.equals(paddingAttributeName))
                return false;
            else if (name.equals(textAlignAttributeName))
                return false;
            else if (name.equals(textDecorationAttributeName))
                return false;
            else if (name.equals(textOutlineAttributeName))
                return false;
            else if (name.equals(unicodeBidiAttributeName))
                return false;
            else if (name.equals(wrapOptionAttributeName))
                return false;
        }
        return true;
    }

    private boolean verifyAttributePermittedInTextProfile(Object content, Locator locator, StyleAccessor sa, VerifierContext context) {
        return true;
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
                    Length w = lengths.get(0);
                    Length h = lengths.get(1);
                    if (!w.equals(h)) {
                        Reporter reporter = context.getReporter();
                        reporter.logError(reporter.message(locator, "*KEY*", "Anamorphic font size ''{0}'' prohibited.", s));
                        return false;
                    }
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
            if (Outline.isOutline(s, location, context, outline)) {
                assert outline.length > 0;
                assert outline[0] != null;
                Length b = outline[0].getBlur();
                if ((b != null) && (b.getValue() > 0)) {
                    Reporter reporter = context.getReporter();
                    reporter.logError(reporter.message(locator, "*Key*", "Non-zero blur ''{0}'' prohibited.", s));
                    return false;
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
        if (wm == WritingMode.LRTB)
            return true;
        else if (wm == WritingMode.RLTB)
            return true;
        else if (wm == WritingMode.TB)
            return true;
        else
            return false;
    }

}
