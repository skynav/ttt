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
 
package com.skynav.ttv.validator.ttml.style;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.ErrorReporter;
import com.skynav.ttv.validator.StyleValueValidator;

public class OpacityValidator implements StyleValueValidator {

    public boolean validate(Model model, String name, Object valueObject, Locator locator, ErrorReporter errorReporter) {
        Float value = (Float) valueObject;
        float opacity = value.floatValue();
        if (value.isNaN()) {
            errorReporter.logWarning(locator, "Not a Number 'NaN' should not be used with " + name + "; use '0' instead.");
        } else if (value.isInfinite()) {
            if (opacity < 0)
                errorReporter.logWarning(locator, "Negative Infinity '-INF' should not be used with " + name + "; use '0' instead.");
            else
                errorReporter.logWarning(locator, "Positive Infinity 'INF' should not be used with " + name + "; use '1' instead.");
        } else if (opacity < 0) {
            errorReporter.logWarning(locator, "Negative values should not be used with " + name + "; use '0' instead.");
        } else if (opacity > 1) {
            errorReporter.logWarning(locator, "Positive values greater than 1 should not be used with " + name + "; use '1' instead.");
        }
        return true;
    }

}
