/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.ttml2.ttd.InferProcessorProfileMethod;
import com.skynav.ttv.model.ttml2.ttd.InferProcessorProfileSource;
import com.skynav.ttv.model.ttml2.ttd.MarkerMode;
import com.skynav.ttv.model.ttml2.ttd.ProfileCombination;
import com.skynav.ttv.model.ttml2.ttd.TimeBase;
import com.skynav.ttv.model.ttml2.ttd.Validation;
import com.skynav.ttv.model.ttml2.ttd.ValidationAction;
import com.skynav.ttv.model.ttml2.ttp.Extensions;
import com.skynav.ttv.model.ttml2.ttp.Features;
import com.skynav.ttv.verifier.ttml.parameter.ClockModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.DisplayAspectRatioVerifier;
import com.skynav.ttv.verifier.ttml.parameter.DropModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.FeatureSemanticsVerifier;
import com.skynav.ttv.verifier.ttml.parameter.InferProcessorProfileMethodVerifier;
import com.skynav.ttv.verifier.ttml.parameter.InferProcessorProfileSourceVerifier;
import com.skynav.ttv.verifier.ttml.parameter.MarkerModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ProfileCombinationVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ProfilesVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TimeBaseVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ValidationActionVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ValidationVerifier;
import com.skynav.ttv.verifier.ttml.parameter.VersionVerifier;

import com.skynav.xml.helpers.XML;

public class TTML2ParameterVerifier extends TTML1ParameterVerifier {

    public static final QName contentProfileCombinationAttributeName    = new QName(NAMESPACE, "contentProfileCombination");
    public static final QName contentProfilesAttributeName              = new QName(NAMESPACE, "contentProfiles");
    public static final QName displayAspectRatioAttributeName           = new QName(NAMESPACE, "displayAspectRatio");
    public static final QName inferProcessorProfileMethodAttributeName  = new QName(NAMESPACE, "inferProcessorProfileMethod");
    public static final QName inferProcessorProfileSourceAttributeName  = new QName(NAMESPACE, "inferProcessorProfileSource");
    public static final QName mediaDurationAttributeName                = new QName(NAMESPACE, "mediaDuration");
    public static final QName mediaOffsetAttributeName                  = new QName(NAMESPACE, "mediaOffset");
    public static final QName permitFeatureNarrowingAttributeName       = new QName(NAMESPACE, "permitFeatureNarrowing");
    public static final QName permitFeatureWideningAttributeName        = new QName(NAMESPACE, "permitFeatureWidening");
    public static final QName processorProfileCombinationAttributeName  = new QName(NAMESPACE, "processorProfileCombination");
    public static final QName processorProfilesAttributeName            = new QName(NAMESPACE, "processorProfiles");
    public static final QName validationActionAttributeName             = new QName(NAMESPACE, "validationAction");
    public static final QName validationAttributeName                   = new QName(NAMESPACE, "validation");
    public static final QName versionAttributeName                      = new QName(NAMESPACE, "version");

    private static final Object[][] parameterAccessorMap                = new Object[][] {
        {
            clockModeAttributeName,
            "ClockMode",
            ClockMode.class,
            ClockModeVerifier.class,
            Boolean.FALSE,
            ClockMode.UTC,
        },
        {
            contentProfileCombinationAttributeName,
            "ContentProfileCombination",
            ProfileCombination.class,
            ProfileCombinationVerifier.class,
            Boolean.FALSE,
            ProfileCombination.REPLACE,
        },
        {
            contentProfilesAttributeName,
            "ContentProfiles",
            String.class,
            ProfilesVerifier.class,
            Boolean.FALSE,
            null
        },
        {
            displayAspectRatioAttributeName,
            "DisplayAspectRatio",
            String.class,
            DisplayAspectRatioVerifier.class,
            Boolean.FALSE,
            "auto",
        },
        {
            dropModeAttributeName,
            "DropMode",
            DropMode.class,
            DropModeVerifier.class,
            Boolean.FALSE,
            DropMode.NON_DROP,
        },
        {
            inferProcessorProfileMethodAttributeName,
            "InferProcessorProfileMethod",
            InferProcessorProfileMethod.class,
            InferProcessorProfileMethodVerifier.class,
            Boolean.FALSE,
            InferProcessorProfileMethod.LOOSE,
        },
        {
            inferProcessorProfileSourceAttributeName,
            "InferProcessorProfileSource",
            InferProcessorProfileSource.class,
            InferProcessorProfileSourceVerifier.class,
            Boolean.FALSE,
            InferProcessorProfileSource.COMBINED,
        },
        {
            markerModeAttributeName,
            "MarkerMode",
            MarkerMode.class,
            MarkerModeVerifier.class,
            Boolean.FALSE,
            MarkerMode.DISCONTINUOUS,
        },
        {
            permitFeatureNarrowingAttributeName,
            "PermitFeatureNarrowing",
            Boolean.class,
            FeatureSemanticsVerifier.class,
            Boolean.FALSE,
            Boolean.FALSE,
        },
        {
            permitFeatureWideningAttributeName,
            "PermitFeatureWidening",
            Boolean.class,
            FeatureSemanticsVerifier.class,
            Boolean.FALSE,
            Boolean.FALSE,
        },
        {
            processorProfileCombinationAttributeName,
            "ProcessorProfileCombination",
            ProfileCombination.class,
            ProfileCombinationVerifier.class,
            Boolean.FALSE,
            ProfileCombination.REPLACE,
        },
        {
            processorProfilesAttributeName,
            "ProcessorProfiles",
            String.class,
            ProfilesVerifier.class,
            Boolean.FALSE,
            null
        },
        {
            timeBaseAttributeName,
            "TimeBase",
            TimeBase.class,
            TimeBaseVerifier.class,
            Boolean.FALSE,
            TimeBase.MEDIA,
        },
        {
            validationAttributeName,
            "Validation",
            Validation.class,
            ValidationVerifier.class,
            Boolean.FALSE,
            Validation.OPTIONAL,
        },
        {
            validationActionAttributeName,
            "ValidationAction",
            ValidationAction.class,
            ValidationActionVerifier.class,
            Boolean.FALSE,
            ValidationAction.WARN,
        },
        // {
        //     versionAttributeName,
        //     "Version",
        //     BigInteger.class,
        //     VersionVerifier.class,
        //     Boolean.FALSE,
        //     BigInteger.valueOf(2),
        // },
    };

    public TTML2ParameterVerifier(Model model) {
        super(model);
    }

    @Override
    protected void populateAccessors(Map<QName, ParameterAccessor> accessors) {
        super.populateAccessors(accessors);
        populateAccessors(accessors, parameterAccessorMap);
    }

    @Override
    protected boolean permitsParameterAttribute(Object content, QName name) {
        if (content instanceof TimedText)
            return true;
        else
            return false;
    }

    @Override
    protected boolean isParameterAttribute(QName name) {
        if (super.isParameterAttribute(name))
            return true;
        else if (name.equals(mediaDurationAttributeName))       // handled by ttml2 timing verifier
            return true;
        else if (name.equals(mediaOffsetAttributeName))         // handled by ttml2 timing verifier
            return true;
        else
            return false;
    }

    @Override
    protected boolean setParameterDefaultValue(Object content, ParameterAccessor pa, Object defaultValue) {
        if (content instanceof TimedText) {
            if (defaultValue != null) {
                setParameterValue(content, pa.setterName, pa.valueClass, defaultValue);
                return true;
            }
        } else if ((content instanceof Features) || (content instanceof Extensions)) {
            if (pa.parameterName.equals(XML.getBaseAttributeName())) {
                Model model = getModel();
                if (content instanceof Features)
                    defaultValue = model.getFeatureNamespaceUri().toString();
                else if (content instanceof Extensions)
                    defaultValue = model.getExtensionNamespaceUri().toString();
                if (defaultValue != null) {
                    setParameterValue(content, pa.setterName, pa.valueClass, defaultValue);
                    return true;
                }
            }
        }
        return false;
    }

}
