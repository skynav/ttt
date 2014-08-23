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
 
package com.skynav.ttv.model.smpte;

import java.net.URI;

import com.skynav.ttv.model.Profile.Specification;
import com.skynav.ttv.model.Profile.Usage;

import static com.skynav.ttv.model.ttml.TTML1.Constants.PROFILE_TT_FULL_ABSOLUTE;
import static com.skynav.ttv.model.smpte.ST20522010.Constants.NAMESPACE_2010_EXTENSION;

public class ST20522010FullProfileSpecification extends Specification {
    
    private static Object[][] extensionMapEntries = new Object[][] {
        { "#data", Usage.REQUIRED },
        { "#image", Usage.REQUIRED },
        { "#information", Usage.REQUIRED },
    };

    public ST20522010FullProfileSpecification(URI profileUri) {
        super(profileUri, PROFILE_TT_FULL_ABSOLUTE, null, extensionsMap(NAMESPACE_2010_EXTENSION, extensionMapEntries));
    }
}
