/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import org.xml.sax.Locator;

import com.skynav.ttv.verifier.VerifierContext;

public class Positions {

    private static final Object[] treatments = new Object[] { NegativeTreatment.Allow };

    public static boolean isPosition(String[] components, Locator locator, VerifierContext context) {
        if (is1ComponentPosition(components, locator, context))
            return true;
        else if (is2ComponentPosition(components, locator, context))
            return true;
        else if (is3ComponentPosition(components, locator, context))
            return true;
        else if (is4ComponentPosition(components, locator, context))
            return true;
        else
            return false;
    }

    private static boolean is1ComponentPosition(String[] components, Locator locator, VerifierContext context) {
        return (components.length == 1) && (isOffsetPositionHorizontal(components, 0, locator, context) || isOffsetPositionVertical(components, 0, locator, context));
    }

    private static boolean is2ComponentPosition(String[] components, Locator locator, VerifierContext context) {
        return (components.length == 2) && isOffsetPositionHorizontal(components, 0, locator, context) && isOffsetPositionVertical(components, 1, locator, context);
    }

    private static boolean is3ComponentPosition(String[] components, Locator locator, VerifierContext context) {
        if (components.length == 3) {
            if (isPositionKeywordHorizontal(components, 0) && isEdgeOffsetVertical(components, 1, locator, context))
                return true;
            else if (isPositionKeywordVertical(components, 0) && isEdgeOffsetHorizontal(components, 1, locator, context))
                return true;
            else if (isEdgeOffsetHorizontal(components, 0, locator, context) && isPositionKeywordVertical(components, 2))
                return true;
            else if (isEdgeOffsetVertical(components, 0, locator, context) && isPositionKeywordHorizontal(components, 2))
                return true;
            else
                return false;
        } else
            return false;
    }

    private static boolean is4ComponentPosition(String[] components, Locator locator, VerifierContext context) {
        if (components.length == 4) {
            if (isEdgeOffsetHorizontal(components, 0, locator, context) && isEdgeOffsetVertical(components, 2, locator, context))
                return true;
            else if (isEdgeOffsetVertical(components, 0, locator, context) && isEdgeOffsetHorizontal(components, 2, locator, context))
                return true;
            else
                return false;
        } else
            return false;
    }

    private static boolean isOffsetPositionHorizontal(String[] components, int index, Locator locator, VerifierContext context) {
        return isOffsetPositionHorizontal(components[index], locator, context);
    }

    private static boolean isOffsetPositionVertical(String[] components, int index, Locator locator, VerifierContext context) {
        return isOffsetPositionVertical(components[index], locator, context);
    }

    private static boolean isEdgeOffsetHorizontal(String[] components, int index, Locator locator, VerifierContext context) {
        if ((index + 2) <= components.length)
            return isEdgeOffsetHorizontal(components[index + 0], components[index + 1], locator, context);
        else
            return false;
    }

    private static boolean isEdgeOffsetVertical(String[] components, int index, Locator locator, VerifierContext context) {
        if ((index + 2) <= components.length)
            return isEdgeOffsetVertical(components[index + 0], components[index + 1], locator, context);
        else
            return false;
    }

    private static boolean isPositionKeywordHorizontal(String[] components, int index) {
        return isPositionKeywordHorizontal(components[index]);
    }

    private static boolean isPositionKeywordVertical(String[] components, int index) {
        return isPositionKeywordVertical(components[index]);
    }

    private static boolean isOffsetPositionHorizontal(String component, Locator locator, VerifierContext context) {
        if (isPositionKeywordHorizontal(component))
            return true;
        else if (Lengths.isLength(component, locator, context, treatments, null))
            return true;
        else
            return false;
    }

    private static boolean isPositionKeywordHorizontal(String component) {
        if (isCenterKeyword(component))
            return true;
        else if (isEdgeKeywordHorizontal(component))
            return true;
        else
            return false;
    }

    private static boolean isEdgeOffsetHorizontal(String c1, String c2, Locator locator, VerifierContext context) {
        return isEdgeKeywordHorizontal(c1) && Lengths.isLength(c2, locator, context, treatments, null);
    }

    private static boolean isEdgeKeywordHorizontal(String component) {
        if (component.equals("left"))
            return true;
        else if (component.equals("right"))
            return true;
        else
            return false;
    }

    private static boolean isOffsetPositionVertical(String component, Locator locator, VerifierContext context) {
        if (isPositionKeywordVertical(component))
            return true;
        else if (Lengths.isLength(component, locator, context, treatments, null))
            return true;
        else
            return false;
    }

    private static boolean isPositionKeywordVertical(String component) {
        if (isCenterKeyword(component))
            return true;
        else if (isEdgeKeywordVertical(component))
            return true;
        else
            return false;
    }

    private static boolean isEdgeOffsetVertical(String c1, String c2, Locator locator, VerifierContext context) {
        return isEdgeKeywordVertical(c1) && Lengths.isLength(c2, locator, context, treatments, null);
    }

    private static boolean isEdgeKeywordVertical(String component) {
        if (component.equals("top"))
            return true;
        else if (component.equals("bottom"))
            return true;
        else
            return false;
    }

    private static boolean isCenterKeyword(String component) {
        return component.equals("center");
    }

}
