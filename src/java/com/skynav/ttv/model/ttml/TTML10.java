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
 
package com.skynav.ttv.model.ttml;

import java.util.Map;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.validator.SemanticsValidator;
import com.skynav.ttv.validator.StyleValidator;

public class TTML10 {
    public static final Model MODEL = new TTML10Model();
    private static class TTML10Model implements Model {
        public String getName() {
            return "ttml10";
        }
        public String getSchemaResourceName() {
            return "xsd/ttml10/ttaf1-dfxp.xsd";
        }
        public String getNamespaceUri() {
            return "http://www.w3.org/ns/ttml";
        }
        public String getJAXBContextPath() {
            return "com.skynav.ttv.model.ttml10.tt:com.skynav.ttv.model.ttml10.ttm:com.skynav.ttv.model.ttml10.ttp";
        }
        private static final Map<Class<?>,String> rootClasses;
        static {
            rootClasses = new java.util.HashMap<Class<?>,String>();
            rootClasses.put(com.skynav.ttv.model.ttml10.tt.TimedText.class, "createTt");
            rootClasses.put(com.skynav.ttv.model.ttml10.ttp.Profile.class, "createProfile");
        }
        public Map<Class<?>,String> getRootClasses() {
            return rootClasses;
        }
        private SemanticsValidator semanticsValidator;
        public SemanticsValidator getSemanticsValidator() {
            synchronized (this) {
                if (semanticsValidator == null) {
                    semanticsValidator = new com.skynav.ttv.validator.ttml.TTML10SemanticsValidator(this);
                }
            }
            return semanticsValidator;
        }
        private StyleValidator styleValidator;
        public StyleValidator getStyleValidator() {
            synchronized (this) {
                if (styleValidator == null) {
                    styleValidator = new com.skynav.ttv.validator.ttml.TTML10StyleValidator(this);
                }
            }
            return styleValidator;
        }
    }
}
