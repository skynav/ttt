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

package com.skynav.ttv.util;

import java.util.Map;

public class MediaQuery {

    @SuppressWarnings("unused")
    private Query query;

    private MediaQuery(Query query) {
        assert query != null;
        this.query = query;
    }

    public boolean evaluate(Map<String,Object> parameters) {
        // [TBD] implement me
        return true;
    }

    public static MediaQuery valueOf(String query) throws ParserException {
        // [TBD] implement me
        return new MediaQuery(new Query());
    }

    public static void populateDefaultMediaParameters(Map<String,Object> parameters) {
        // current
        parameters.put("aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("color", Double.valueOf(8));
        parameters.put("color-index", Double.valueOf(0));
        parameters.put("device-aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("device-height", Double.valueOf(1280));
        parameters.put("device-width", Double.valueOf(720));
        parameters.put("grid", Boolean.FALSE);
        parameters.put("height", Double.valueOf(720));
        parameters.put("monochrome", Double.valueOf(0));
        parameters.put("orientation", "landscape");
        parameters.put("resolution", Double.valueOf(100));
        parameters.put("scan", "progressive");
        parameters.put("width", Double.valueOf(1280));
        // minimums
        parameters.put("min-aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("min-color", Double.valueOf(8));
        parameters.put("min-color-index", Double.valueOf(0));
        parameters.put("min-device-aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("min-device-height", Double.valueOf(1280));
        parameters.put("min-device-width", Double.valueOf(720));
        parameters.put("min-height", Double.valueOf(720));
        parameters.put("min-monochrome", Double.valueOf(0));
        parameters.put("min-resolution", Double.valueOf(100));
        parameters.put("min-width", Double.valueOf(1280));
        // maximums
        parameters.put("max-aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("max-color", Double.valueOf(8));
        parameters.put("max-color-index", Double.valueOf(0));
        parameters.put("max-device-aspect-ratio", Double.valueOf(16.0/9.0));
        parameters.put("max-device-height", Double.valueOf(1280));
        parameters.put("max-device-width", Double.valueOf(720));
        parameters.put("max-height", Double.valueOf(720));
        parameters.put("max-monochrome", Double.valueOf(0));
        parameters.put("max-resolution", Double.valueOf(100));
        parameters.put("max-width", Double.valueOf(1280));
    }

    public static class Query {
        public Query() {
        }
    }

    public static class ParserException extends RuntimeException {
        static final long serialVersionUID = 0;
        public ParserException(String message) {
            super(message);
        }
    }

    public static class EvaluatorException extends RuntimeException {
        static final long serialVersionUID = 0;
        public EvaluatorException(String message) {
            super(message);
        }
    }


}
