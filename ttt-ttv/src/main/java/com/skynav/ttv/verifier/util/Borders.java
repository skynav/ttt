/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.util;

import com.skynav.ttv.model.value.Border;
import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.impl.BorderImpl;
import com.skynav.ttv.model.value.impl.ColorImpl;
import com.skynav.ttv.model.value.impl.LengthImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Borders {

    private static final Object[]       TREATMENTS      = new Object[] { NegativeTreatment.Error };

    public static boolean isBorder(String[] components, Location location, VerifierContext context, Border[] outputBorder) {
        int             nc = components.length;
        Border.Style    bs = null;      // border style
        Color           bc = null;      // border color
        Length          bt = null;      // border thickness
        Length[]        br = null;      // border radii
        for (int i = 0; i < nc; ++i) {
            String c = components[i];
            if (isStyleKeyword(c)) {
                if (bs == null)
                    bs = Border.Style.valueOfShorthand(c);
                else
                    return false;       // extra style keyword
            } else if (isThicknessKeyword(c)) {
                if (bt == null)
                    bt = BorderImpl.getLengthFromThicknessKeyword(Border.Thickness.valueOfShorthand(c));
                else
                    return false;
            } else if (Lengths.maybeLength(c)) {
                if (bt == null) {
                    Length[] length = new Length[1];
                    if (Lengths.isLength(c, location, context, TREATMENTS, length))
                        bt = length[0];
                    else
                        return false;   // bad expression that may be length
                } else
                    return false;       // extra length expression
            } else if (Colors.maybeColor(c)) {
                if (bc == null) {
                    Color[] color = new Color[1];
                    if (Colors.isColor(c, location, context, color))
                        bc = color[0];
                    else
                        return false;   // bad expression that may be color
                } else
                    return false;       // extra color expression
            } else {
                return false;           // other form of unknown expression or delimiter
            }
        }
        if ((bs == null) && (bt == null) && (bc == null))
            return false;               // must have at least one valid component
        else {
            if (bs == null)
                bs = Border.Style.NONE;
            if (bt == null)
                bt = LengthImpl.PXL_0;
            if (bc == null)
                bc = ColorImpl.CURRENT;
        }
        if (outputBorder != null)
            outputBorder[0] = new BorderImpl(bs, bc, bt, br);
        return true;
    }

    private static boolean isStyleKeyword(String s) {
        try {
            Border.Style.valueOfShorthand(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean isThicknessKeyword(String s) {
        try {
            Border.Thickness.valueOfShorthand(s);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

}
