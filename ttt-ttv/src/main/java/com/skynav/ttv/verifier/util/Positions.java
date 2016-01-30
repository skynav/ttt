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

package com.skynav.ttv.verifier.util;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.impl.LengthImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.VerifierContext;

public class Positions {

    private static final Length         PCT_0           = new LengthImpl(0, Length.Unit.Percentage);
    private static final Length         PCT_50          = new LengthImpl(50, Length.Unit.Percentage);
    private static final Length         PCT_100         = new LengthImpl(100, Length.Unit.Percentage);

    private static final Object[]       TREATMENTS      = new Object[] { NegativeTreatment.Allow };

    public static boolean isPosition(String[] components, Location location, VerifierContext context, Length[] outputLengths) {
        if (is1ComponentPosition(components, location, context, outputLengths))
            return true;
        else if (is2ComponentPosition(components, location, context, outputLengths))
            return true;
        else if (is3ComponentPosition(components, location, context, outputLengths))
            return true;
        else if (is4ComponentPosition(components, location, context, outputLengths))
            return true;
        else
            return false;
    }

    private static boolean is1ComponentPosition(String[] components, Location location, VerifierContext context, Length[] outputLengths) {
        if (components.length == 1) {
            Length[] lengths = new Length[2];
            if (isOffsetPositionHorizontal(components, 0, location, context, lengths)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 2;
                    outputLengths[0] = lengths[0];
                    outputLengths[1] = PCT_50;
                }
                return true;
            } else if (isOffsetPositionVertical(components, 0, location, context, lengths)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 2;
                    outputLengths[0] = PCT_50;
                    outputLengths[1] = lengths[1];
                }
                return true;
            }
        }
        return false;
    }

    private static boolean is2ComponentPosition(String[] components, Location location, VerifierContext context, Length[] outputLengths) {
        if (components.length == 2) {
            Length[] lengths = new Length[4];
            if (isOffsetPositionHorizontal(components, 0, location, context, lengths) && isOffsetPositionVertical(components, 0, location, context, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
        }
        return false;
    }

    private static boolean is3ComponentPosition(String[] components, Location location, VerifierContext context, Length[] outputLengths) {
        if (components.length == 3) {
            Length[] lengths;
            lengths = new Length[4];
            if (isPositionKeywordHorizontal(components, 0, lengths) && isEdgeOffsetVertical(components, 1, location, context, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
            lengths = new Length[4];
            if (isPositionKeywordVertical(components, 0, lengths) && isEdgeOffsetHorizontal(components, 1, location, context, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
            lengths = new Length[4];
            if (isEdgeOffsetHorizontal(components, 0, location, context, lengths) && isPositionKeywordVertical(components, 2, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
            lengths = new Length[4];
            if (isEdgeOffsetVertical(components, 0, location, context, lengths) && isPositionKeywordHorizontal(components, 2, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
        }
        return false;
    }

    private static boolean is4ComponentPosition(String[] components, Location location, VerifierContext context, Length[] outputLengths) {
        if (components.length == 4) {
            Length[] lengths;
            lengths = new Length[4];
            if (isEdgeOffsetHorizontal(components, 0, location, context, lengths) && isEdgeOffsetVertical(components, 2, location, context, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
            lengths = new Length[4];
            if (isEdgeOffsetVertical(components, 0, location, context, lengths) && isEdgeOffsetHorizontal(components, 2, location, context, lengths))
                return componentPositionContinuation(lengths, outputLengths, true);
        }
        return false;
    }

    private static boolean componentPositionContinuation(Length[] lengths, Length[] outputLengths, boolean rv) {
        if (outputLengths != null) {
            assert lengths != null;
            assert lengths.length >= 4;
            assert outputLengths.length >= 4;
            for (int i = 0, n = 4; i < n; ++i)
                outputLengths[i] = lengths[i];
        }
        return rv;
    }

    private static boolean isOffsetPositionHorizontal(String[] components, int index, Location location, VerifierContext context, Length[] outputLengths) {
        return isOffsetPositionHorizontal(components[index], location, context, outputLengths);
    }

    private static boolean isOffsetPositionVertical(String[] components, int index, Location location, VerifierContext context, Length[] outputLengths) {
        return isOffsetPositionVertical(components[index], location, context, outputLengths);
    }

    private static boolean isEdgeOffsetHorizontal(String[] components, int index, Location location, VerifierContext context, Length[] outputLengths) {
        if ((index + 2) <= components.length)
            return isEdgeOffsetHorizontal(components[index + 0], components[index + 1], location, context, outputLengths);
        else
            return false;
    }

    private static boolean isEdgeOffsetVertical(String[] components, int index, Location location, VerifierContext context, Length[] outputLengths) {
        if ((index + 2) <= components.length)
            return isEdgeOffsetVertical(components[index + 0], components[index + 1], location, context, outputLengths);
        else
            return false;
    }

    private static boolean isPositionKeywordHorizontal(String[] components, int index, Length[] outputLengths) {
        return isPositionKeywordHorizontal(components[index], outputLengths);
    }

    private static boolean isPositionKeywordVertical(String[] components, int index, Length[] outputLengths) {
        return isPositionKeywordVertical(components[index], outputLengths);
    }

    private static boolean isOffsetPositionHorizontal(String component, Location location, VerifierContext context, Length[] outputLengths) {
        if (isPositionKeywordHorizontal(component, outputLengths))
            return true;
        else {
            Length[] length = new Length[1];
            if (Lengths.isLength(component, location, context, TREATMENTS, length)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 2;
                    outputLengths[0] = length[0];
                }
                return true;
            } else
                return false;
        }
    }

    private static boolean isPositionKeywordHorizontal(String component, Length[] outputLengths) {
        if (isCenterKeyword(component)) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[0] = PCT_50;
            }
            return true;
        } else if (isEdgeKeywordHorizontal(component, outputLengths))
            return true;
        else
            return false;
    }

    private static boolean isEdgeOffsetHorizontal(String c1, String c2, Location location, VerifierContext context, Length[] outputLengths) {
        Length[] lengths = new Length[2];
        if (isEdgeKeywordHorizontal(c1, lengths)) {
            Length[] offset = new Length[1];
            if (Lengths.isLength(c2, location, context, TREATMENTS, offset)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 4;
                    outputLengths[0] = lengths[0];
                    outputLengths[2] = c1.equals("left") ? offset[0].negate() : offset[0];
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isEdgeKeywordHorizontal(String component, Length[] outputLengths) {
        if (component.equals("left")) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[0] = PCT_0;
            }
            return true;
        } else if (component.equals("right")) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[0] = PCT_100;
            }
            return true;
        } else
            return false;
    }

    private static boolean isOffsetPositionVertical(String component, Location location, VerifierContext context, Length[] outputLengths) {
        if (isPositionKeywordVertical(component, outputLengths))
            return true;
        else {
            Length[] length = new Length[1];
            if (Lengths.isLength(component, location, context, TREATMENTS, length)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 2;
                    outputLengths[1] = length[0];
                }
                return true;
            } else
                return false;
        }
    }

    private static boolean isPositionKeywordVertical(String component, Length[] outputLengths) {
        if (isCenterKeyword(component)) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[1] = PCT_50;
            }
            return true;
        } else if (isEdgeKeywordVertical(component, outputLengths))
            return true;
        else
            return false;
    }

    private static boolean isEdgeOffsetVertical(String c1, String c2, Location location, VerifierContext context, Length[] outputLengths) {
        Length[] lengths = new Length[2];
        if (isEdgeKeywordVertical(c1, lengths)) {
            Length[] offset = new Length[1];
            if (Lengths.isLength(c2, location, context, TREATMENTS, offset)) {
                if (outputLengths != null) {
                    assert outputLengths.length >= 4;
                    outputLengths[1] = lengths[1];
                    outputLengths[3] = c1.equals("top") ? offset[0].negate() : offset[0];
                }
                return true;
            }
        }
        return false;
    }

    private static boolean isEdgeKeywordVertical(String component, Length[] outputLengths) {
        if (component.equals("top")) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[1] = PCT_0;
            }
            return true;
        } else if (component.equals("bottom")) {
            if (outputLengths != null) {
                assert outputLengths.length >= 2;
                outputLengths[1] = PCT_100;
            }
            return true;
        } else
            return false;
    }

    private static boolean isCenterKeyword(String component) {
        return component.equals("center");
    }

}
