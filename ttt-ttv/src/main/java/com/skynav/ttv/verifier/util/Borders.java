/*
 * Copyright 2016-2018 Skynav, Inc. All rights reserved.
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

import java.util.List;

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
    private static final String         RADII_PREFIX    = "radii(";
    private static final String         RADII_SUFFIX    = ")";
    public static final Length[]        ZERO_RADII      = new Length[] { LengthImpl.PXL_0, LengthImpl.PXL_0 };

    public static boolean isBorder(String value, Location location, VerifierContext context, Border[] outputBorder) {
        String[]        components = getComponents(value);
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
            } else if (isRadiiFunction(c, location, context)) {
                if (br == null)
                    br = getLengthsFromRadiiFunction(c, location, context);
                else
                    return false;       // extra radii function
            } else {
                return false;           // other form of unknown expression or delimiter
            }
        }
        if ((bs == null) && (bt == null) && (bc == null) && (br == null))
            return false;               // must have at least one valid component
        else {
            if (bs == null)
                bs = Border.Style.NONE;
            if (bt == null)
                bt = LengthImpl.PXL_0;
            if (bc == null)
                bc = ColorImpl.CURRENT;
            if (br == null)
                br = ZERO_RADII;
        }
        if (outputBorder != null)
            outputBorder[0] = new BorderImpl(bs, bc, bt, br);
        return true;
    }

    private static String[] getComponents(String s) {
        assert s != null;
        String[]        ca = s.split("[ \t\r\n]+");
        int             nc = ca.length;
        int             ci = 0;
        List<String>    components = new java.util.ArrayList<String>();
        StringBuffer    rf = new StringBuffer();
        while (ci < nc) {
            String      c  = ca[ci++];
            if (c.startsWith(RADII_PREFIX)) {
                rf.setLength(0);
                rf.append(c);
                if (!c.endsWith(RADII_SUFFIX)) {
                    while (ci < nc) {
                        c = ca[ci++];
                        rf.append(c);
                        if (c.endsWith(RADII_SUFFIX))
                            break;
                    }
                }
                components.add(rf.toString());
            } else {
                components.add(c);
            }
        }
        return components.toArray(new String[components.size()]);
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

    private static boolean isRadiiFunction(String s, Location location, VerifierContext context) {
        if (!s.startsWith(RADII_PREFIX))
            return false;
        else if (!s.endsWith(RADII_SUFFIX))
            return false;
        else {
            String      al = s.substring(RADII_PREFIX.length(), s.length() - RADII_SUFFIX.length());
            String[]    aa = al.split(",");
            int         na = aa.length;
            if ((na < 1) || (na > 2)) {
                return false;
            } else {
                String  a1 = aa[0];
                if (!Lengths.isLength(a1, location, context, TREATMENTS, null))
                    return false;
                String  a2 = (na > 1) ? aa[1] : a1;
                if (!Lengths.isLength(a2, location, context, TREATMENTS, null))
                    return false;
            }
        }
        return true;
    }

    private static Length[] getLengthsFromRadiiFunction(String s, Location location, VerifierContext context) {
        assert s != null;
        assert s.startsWith(RADII_PREFIX);
        assert s.endsWith(RADII_SUFFIX);
        String          al = s.substring(RADII_PREFIX.length(), s.length() - RADII_SUFFIX.length());
        String[]        aa = al.split(",");
        int             na = aa.length;
        assert !((na < 1) || (na > 2));
        if ((na < 1) || (na > 2)) {
            return ZERO_RADII;
        } else {
            String      a1 = aa[0];
            Length[]    l1 = new Length[1];
            if (!Lengths.isLength(a1, location, context, TREATMENTS, l1)) {
                assert l1[0] != null;
                return ZERO_RADII;
            }
            String      a2 = (na > 1) ? aa[1] : a1;
            Length[]    l2 = new Length[1];
            if (!Lengths.isLength(a2, location, context, TREATMENTS, l2)) {
                assert l2[0] != null;
                return ZERO_RADII;
            }
            return new Length[] { l1[0], l2[0] };
        }
    }

}
