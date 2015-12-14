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

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Color;
import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.TextOutline;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Colors;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class Outline {

    public static boolean isOutline(String value, Location location, VerifierContext context, TextOutline[] outputOutline) {
        String [] components = value.split("[ \t\r\n]+");
        int componentIndex = 0;
        int numComponents = components.length;
        // none
        if ((numComponents == 1) && Keywords.isNone(components[0]))
            return true;
        // color
        String c = null;
        if (componentIndex < numComponents) {
            if (Colors.maybeColor(components[componentIndex]))
                c = components[componentIndex++];
        }
        Color[] color = new Color[1];
        if (c != null) {
            if (!Colors.isColor(c, location, context, color))
                return false;
        }
        // thickness
        String t = null;
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                t = components[componentIndex++];
        }
        Length[] thickness = new Length[1];
        if (t != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            if (!Lengths.isLength(t, location, context, treatments, thickness))
                return false;
        } else
            return false;
        // blur
        String b = null;
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                b = components[componentIndex++];
        }
        Length[] blur = new Length[1];
        if (b != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            if (!Lengths.isLength(b, location, context, treatments, blur))
                return false;
        }
        // unparsed components
        if (componentIndex < numComponents)
            return false;
        // output resultant outline
        if (outputOutline != null) {
            assert outputOutline.length >= 1;
            outputOutline[0] = new TextOutline(color[0], thickness[0], blur[0]);
        }
        return true;
    }

    public static void badOutline(String value, Location location, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        String [] components = value.split("[ \t\r\n]+");
        int componentIndex = 0;
        int numComponents = components.length;
        // none
        if ((numComponents == 1) && Keywords.isNone(components[0]))
            return;
        // color
        String c = null;
        if (componentIndex < numComponents) {
            if (Colors.maybeColor(components[componentIndex]))
                c = components[componentIndex++];
        }
        if (c != null) {
            if (!Colors.isColor(c, location, context, null)) {
                Colors.badColor(c, location, context);
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <color> expression in color component ''{0}''.", c));
            }
        }
        // thickness
        String t = null;
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                t = components[componentIndex++];
        }
        if (t != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            if (!Lengths.isLength(t, location, context, treatments, null)) {
                Lengths.badLength(t, location, context, treatments);
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <length> expression in thickness component ''{0}''.", t));
            }
        } else {
            reporter.logInfo(reporter.message(locator, "*KEY*", "Missing thickness component."));
        }
        // blur
        String b = null;
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                b = components[componentIndex++];
        }
        if (b != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            if (!Lengths.isLength(b, location, context, treatments, null)) {
                Lengths.badLength(b, location, context, treatments);
                reporter.logInfo(reporter.message(locator, "*KEY*", "Bad <length> expression in blur component ''{0}''.", b));
            }
        }
        // unparsed components
        String afterComponent;
        if (b != null)
            afterComponent = "blur";
        else if (t != null)
            afterComponent = "thickness";
        else if (c != null)
            afterComponent = "color";
        else
            afterComponent = null;
        while (componentIndex < numComponents) {
            String unparsedComponent = components[componentIndex++];
            if (afterComponent != null)
                reporter.logInfo(reporter.message(locator, "*KEY*", "Unparsed expression ''{0}'' after {1} component.", unparsedComponent, afterComponent));
            else
                reporter.logInfo(reporter.message(locator, "*KEY*", "Unparsed expression ''{0}''.", unparsedComponent));
        }
    }

}
