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
import java.util.Set;

import com.skynav.ttv.model.ttml.TTML1StandardDesignations;
import com.skynav.ttv.util.URIs;

import static com.skynav.ttv.model.smpte.ST20522010.Constants.NAMESPACE_2010_EXTENSION;

public class ST20522010StandardDesignations extends TTML1StandardDesignations {
    
    private static final String[] extensionDesignationStrings = new String[] {
        "#data",
        "#image",
        "#information"
    };

    private Set<URI> extensionDesignations;

    protected ST20522010StandardDesignations() {
        populateExtensionDesignations();
    }
    
    private void populateExtensionDesignations() {
        URI extensionNamespaceUri = URIs.makeURISafely(NAMESPACE_2010_EXTENSION);
        if (extensionNamespaceUri != null) {
            Set<URI> extensionDesignations = new java.util.HashSet<URI>(extensionDesignationStrings.length);
            for (String designation : extensionDesignationStrings) {
                extensionDesignations.add(extensionNamespaceUri.resolve(designation));
            }
            this.extensionDesignations = extensionDesignations;
        }
    }
    
    private static ST20522010StandardDesignations instance;
    public static ST20522010StandardDesignations getInstance() {
        if (instance == null)
            instance = new ST20522010StandardDesignations();
        return instance;
    }

    @Override
    public boolean isStandardExtensionDesignation(URI uri) {
        if (super.isStandardExtensionDesignation(uri))
            return true;
        else
            return (extensionDesignations != null) && extensionDesignations.contains(uri);
    }

}
