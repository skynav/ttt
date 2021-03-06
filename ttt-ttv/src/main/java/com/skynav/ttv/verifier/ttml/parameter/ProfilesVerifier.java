/*
 * Copyright 2016 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.ttml.parameter;

import java.net.URI;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.Profile;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.verifier.ParameterValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Profiles;

public class ProfilesVerifier implements ParameterValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        assert value instanceof String;
        String s = (String) value;
        Model model = context.getModel();
        URI ttProfileNamespaceUri = model.getTTProfileNamespaceUri();
        Set<URI> designators = model.getProfileDesignators();
        Profile.Type profileType;
        QName parameterName = location.getAttributeName();
        if (parameterName.getLocalPart().equals("contentProfiles"))
            profileType = Profile.Type.CONTENT;
        else
            profileType = Profile.Type.PROCESSOR;
        if (Profiles.isProfileDesignators(s, location, context, ttProfileNamespaceUri, profileType, designators)) {
            return true;
        } else {
            Profiles.badProfileDesignators(s, location, context, ttProfileNamespaceUri, profileType, designators);
            return false;
        }
    }

}
