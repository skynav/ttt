/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.style;

import com.skynav.ttv.model.value.CharacterClass;
import com.skynav.ttv.verifier.util.Characters;

public class AnnotationOverhangClass {
    public static final AnnotationOverhangClass EMPTY = new AnnotationOverhangClass(CharacterClass.EMPTY);
    public static final AnnotationOverhangClass AUTO = new AnnotationOverhangClass(Characters.getAutoCharacterClass());
    private CharacterClass characterClass;
    public AnnotationOverhangClass(CharacterClass characterClass) {
        assert characterClass != null;
        this.characterClass = characterClass;
    }
    public boolean inClass(int c) {
        return characterClass.inClass(c);
    }
    @Override
    public int hashCode() {
        return characterClass.hashCode();
    }
    @Override
    public boolean equals(Object o) {
        if (o instanceof AnnotationOverhangClass) {
            AnnotationOverhangClass other = (AnnotationOverhangClass) o;
            return other.characterClass.equals(characterClass);
        } else
            return false;
    }
}
