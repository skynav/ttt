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

package com.skynav.ttpe.util;

import java.text.AttributedCharacterIterator;
import java.text.AttributedString;

public class AttributedStrings {

    private AttributedStrings() {}

    public static AttributedString concat(AttributedCharacterIterator[] iterators, int[] contentIndices) {
        StringBuffer sb = new StringBuffer();
        int ci = 0;
        for (AttributedCharacterIterator aci : iterators) {
            if ((contentIndices != null) && (contentIndices.length > ci))
                contentIndices[ci++] = sb.length();
            int b = aci.getBeginIndex();
            int e = aci.getEndIndex();
            while (b < e)
                sb.append(aci.setIndex(b++));
        }
        if ((contentIndices != null) && (contentIndices.length > ci))
            contentIndices[ci] = sb.length();
        AttributedString as = new AttributedString(sb.toString());
        int offset = 0;
        for (AttributedCharacterIterator aci : iterators) {
            int b = aci.getBeginIndex();
            int e = aci.getEndIndex();
            for (AttributedCharacterIterator.Attribute a : aci.getAllAttributeKeys()) {
                aci.setIndex(0);
                while (aci.getIndex() < e) {
                    int s = aci.getRunStart(a);
                    int l = aci.getRunLimit(a);
                    Object v = aci.getAttribute(a);
                    if (v != null)
                        as.addAttribute(a, v, offset + s, offset + l);
                    aci.setIndex(l);
                }
            }
            offset += e - b;
        }
        return as;
    }

}
