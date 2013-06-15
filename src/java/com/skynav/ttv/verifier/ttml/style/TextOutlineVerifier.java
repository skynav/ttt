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
 
package com.skynav.ttv.verifier.ttml.style;

import javax.xml.namespace.QName;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.StyleValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Colors;
import com.skynav.ttv.verifier.util.Lengths;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class TextOutlineVerifier implements StyleValueVerifier {

    public boolean verify(Model model, QName name, Object valueObject, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        boolean failed = false;
        assert valueObject instanceof String;
        String value = (String) valueObject;
        String [] components = value.split("[ \t\r\n]+");
        int componentIndex = 0;
        int numComponents = components.length;
        String color = null;
        String thickness = null;
        String blur = null;
        if (componentIndex < numComponents) {
            if (Colors.maybeColor(components[componentIndex]))
                color = components[componentIndex++];
        }
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                thickness = components[componentIndex++];
        }
        if (componentIndex < numComponents) {
            if (Lengths.maybeLength(components[componentIndex]))
                blur = components[componentIndex++];
        }
        if (color != null) {
            if (!Colors.isColor(color, locator, context, null)) {
                Colors.badColor(color, locator, context);
                reporter.logInfo(locator, "Bad <color> expression in color component '" + color + "'.");
                failed = true;
            } else if (thickness == null) {
                reporter.logInfo(locator, "Missing <length> expression after <color> expression '" + color + "'.");
                failed = true;
            }
        }
        if (thickness != null) {
            Object[] treatments = new Object[] { NegativeTreatment.Error };
            if (!Lengths.isLength(thickness, locator, context, treatments, null)) {
                Lengths.badLength(thickness, locator, context, treatments);
                reporter.logInfo(locator, "Bad <length> expression in thickness component '" + thickness + "'.");
                failed = true;
            } else if (blur != null) {
                if (!Lengths.isLength(blur, locator, context, treatments, null)) {
                    Lengths.badLength(blur, locator, context, treatments);
                    reporter.logInfo(locator, "Bad <length> expression in blur component '" + blur + "'.");
                    failed = true;
                }
            }
        }
        String afterComponent;
        if (blur != null)
            afterComponent = "blur";
        else if (thickness != null)
            afterComponent = "thickness";
        else if (color != null)
            afterComponent = "color";
        else
            afterComponent = null;
        while (componentIndex < numComponents) {
            String unparsedComponent = components[componentIndex++];
            reporter.logInfo(locator,
                "Unparsed expression '" + unparsedComponent + "'" +
                ((afterComponent != null) ? (" after " + afterComponent + " component") : "") + ".");
            failed = true;
        }
        return !failed;
    }

}
