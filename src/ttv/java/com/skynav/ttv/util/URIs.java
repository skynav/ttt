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
 
package com.skynav.ttv.util;

import java.net.URI;
import java.net.URISyntaxException;

public class URIs {

    public static URI makeURISafely(String uriString) {
        try {
            return new URI(uriString);
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public static boolean hasFragment(String uriString) {
        URI uri = makeURISafely(uriString);
        if (uri != null)
            return uri.getFragment() != null;
        else
            return false;
    }

    public static boolean isLocalFragment(String uriString) {
        URI uri = makeURISafely(uriString);
        if (uri != null) {
            String s = uri.getScheme();
            if ((s != null) && (s.length() > 0))
                return false;
            String ssp = uri.getSchemeSpecificPart();
            if ((ssp != null) && (ssp.length() > 0))
                return false;
            return uri.getFragment() != null;
        } else
            return false;
    }

    public static boolean isNonLocalFragment(String uriString) {
        URI uri = makeURISafely(uriString);
        if (uri != null) {
            String s = uri.getScheme();
            String ssp = uri.getSchemeSpecificPart();
            if (((s == null) || (s.length() ==0)) && ((ssp == null) || (ssp.length() ==0)))
                return false;
            return uri.getFragment() != null;
        } else
            return false;
    }

    public static String getFragment(String uriString) {
        URI uri = makeURISafely(uriString);
        if (uri != null)
            return uri.getFragment();
        else
            return null;
    }

}
