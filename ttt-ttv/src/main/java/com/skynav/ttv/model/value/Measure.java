/*
 * Copyright 2013-21 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.model.value;

public interface Measure extends Length {
    public enum Type {
        Auto            ("auto"),
        Available       ("available"),
        FitContent      ("fitContent"),
        Length          (null),
        MaxContent      ("maxContent"),
        MinContent      ("minContent");
        private String shorthand;
        Type(String shorthand) {
            this.shorthand = shorthand;
        }
        public String shorthand() {
            return shorthand;
        }
        public static Type valueOfShorthand(String value) {
            if (value == null)
                throw new IllegalArgumentException();
            for (Type v: values()) {
                String s = v.shorthand();
                if ((s != null) && value.equals(s))
                    return v;
            }
            throw new IllegalArgumentException();
        }
    };
    public interface Resolver {
        Length resolve(Measure m);
    };
    Type getType();
    boolean isLength();
    boolean isResolved();
    Length resolve(Resolver r);
}
