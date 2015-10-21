/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.ttml;

import java.math.BigInteger;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.model.ttml2.ttd.ClockMode;
import com.skynav.ttv.model.ttml2.ttd.DropMode;
import com.skynav.ttv.model.ttml2.ttd.MarkerMode;
import com.skynav.ttv.model.ttml2.ttd.TimeBase;
import com.skynav.ttv.model.ttml2.ttp.Extensions;
import com.skynav.ttv.model.ttml2.ttp.Features;
import com.skynav.ttv.verifier.ttml.parameter.ClockModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.DropModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.MarkerModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TimeBaseVerifier;
import com.skynav.ttv.verifier.ttml.parameter.VersionVerifier;

import com.skynav.xml.helpers.XML;
import java.util.Arrays;


public class TTML2ParameterVerifier extends TTML1ParameterVerifier {

    private static final Object[][] parameterAccessorMap        = new Object[][] {
        {
            new QName(NAMESPACE,"clockMode"),
            "ClockMode",
            ClockMode.class,
            ClockModeVerifier.class,
            Boolean.FALSE,
            ClockMode.UTC,
        },
        {
            new QName(NAMESPACE,"dropMode"),
            "DropMode",
            DropMode.class,
            DropModeVerifier.class,
            Boolean.FALSE,
            DropMode.NON_DROP,
        },
        {
            new QName(NAMESPACE,"markerMode"),
            "MarkerMode",
            MarkerMode.class,
            MarkerModeVerifier.class,
            Boolean.FALSE,
            MarkerMode.DISCONTINUOUS,
        },
        {
            new QName(NAMESPACE,"timeBase"),
            "TimeBase",
            TimeBase.class,
            TimeBaseVerifier.class,
            Boolean.FALSE,
            TimeBase.MEDIA,
        },
        {
            new QName(NAMESPACE,"version"),
            "Version",
            BigInteger.class,
            VersionVerifier.class,
            Boolean.FALSE,
            BigInteger.valueOf(2),
        },
    };

    public TTML2ParameterVerifier(Model model) {
        super(model);
    }

    @Override
    protected void populateAccessors(Map<QName, ParameterAccessor> accessors) {
        super.populateAccessors(accessors);
        populateAccessors(accessors, Arrays.asList(parameterAccessorMap));
    }

    @Override
    protected boolean permitsParameterAttribute(Object content, QName name) {
        if (content instanceof TimedText)
            return true;
        else
            return false;
    }

    @Override
    protected void setParameterDefaultValue(Object content, ParameterAccessor pa, Object defaultValue) {
        if (content instanceof TimedText) {
            if (defaultValue != null)
                setParameterValue(content, pa.setterName, pa.valueClass, defaultValue);
        } else if ((content instanceof Features) || (content instanceof Extensions)) {
            if (pa.parameterName.equals(XML.getBaseAttributeName())) {
                Model model = getModel();
                if (content instanceof Features)
                    defaultValue = model.getFeatureNamespaceUri().toString();
                else if (content instanceof Extensions)
                    defaultValue = model.getExtensionNamespaceUri().toString();
                if (defaultValue != null)
                    setParameterValue(content, pa.setterName, pa.valueClass, defaultValue);
            }
        }
    }

}
