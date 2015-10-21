/*
 * Copyright (c) 2015, msamek
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.skynav.ttv.verifier.ebuttd;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.ttd.ClockMode;
import com.skynav.ttv.model.ttml1.ttd.DropMode;
import com.skynav.ttv.model.ttml1.ttd.MarkerMode;
import com.skynav.ttv.model.ttml1.ttd.TimeBase;
import com.skynav.ttv.verifier.ttml.TTML1ParameterVerifier;
import static com.skynav.ttv.verifier.ttml.TTML1ParameterVerifier.NAMESPACE;
import com.skynav.ttv.verifier.ttml.parameter.BaseVerifier;
import com.skynav.ttv.verifier.ttml.parameter.CellResolutionVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ClockModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.DropModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.FrameRateMultiplierVerifier;
import com.skynav.ttv.verifier.ttml.parameter.FrameRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.MarkerModeVerifier;
import com.skynav.ttv.verifier.ttml.parameter.PixelAspectRatioVerifier;
import com.skynav.ttv.verifier.ttml.parameter.ProfileVerifier;
import com.skynav.ttv.verifier.ttml.parameter.SubFrameRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TickRateVerifier;
import com.skynav.ttv.verifier.ttml.parameter.TimeBaseVerifier;
import com.skynav.xml.helpers.XML;
import java.math.BigInteger;
import java.util.Arrays;
import javax.xml.namespace.QName;

/**
 *
 * @author msamek
 */
public class EBUTTDParameterVerifier extends TTML1ParameterVerifier {

    public EBUTTDParameterVerifier(Model model) {
        super(model);
    }

    static {
        parameterAccessorMap.clear();
        parameterAccessorMap.addAll(Arrays.asList(new Object[][]{
            {
                new QName(NAMESPACE, "cellResolution"), // attribute name
                "CellResolution", // accessor method name suffix
                String.class, // value type
                CellResolutionVerifier.class, // specialized verifier
                Boolean.FALSE, // padding permitted
                "40 24", // default value
            },
            {
                new QName(NAMESPACE, "clockMode"),
                "ClockMode",
                ClockMode.class,
                ClockModeVerifier.class,
                Boolean.FALSE,
                ClockMode.UTC,},
            {
                new QName(NAMESPACE, "dropMode"),
                "DropMode",
                DropMode.class,
                DropModeVerifier.class,
                Boolean.FALSE,
                DropMode.NON_DROP,},
            {
                new QName(NAMESPACE, "frameRate"),
                "FrameRate",
                BigInteger.class,
                FrameRateVerifier.class,
                Boolean.TRUE,
                BigInteger.valueOf(30),},
            {
                new QName(NAMESPACE, "frameRateMultiplier"),
                "FrameRateMultiplier",
                String.class,
                FrameRateMultiplierVerifier.class,
                Boolean.FALSE,
                "1 1",},
            {
                new QName(NAMESPACE, "markerMode"),
                "MarkerMode",
                MarkerMode.class,
                MarkerModeVerifier.class,
                Boolean.FALSE,
                MarkerMode.CONTINUOUS,},
            {
                new QName(NAMESPACE, "pixelAspectRatio"),
                "PixelAspectRatio",
                String.class,
                PixelAspectRatioVerifier.class,
                Boolean.FALSE,
                "1 1",},
            {
                new QName(NAMESPACE, "profile"),
                "Profile",
                String.class,
                ProfileVerifier.class,
                Boolean.FALSE,
                null,},
            {
                new QName(NAMESPACE, "subFrameRate"),
                "SubFrameRate",
                BigInteger.class,
                SubFrameRateVerifier.class,
                Boolean.FALSE,
                BigInteger.valueOf(1),},
            {
                new QName(NAMESPACE, "tickRate"),
                "TickRate",
                BigInteger.class,
                TickRateVerifier.class,
                Boolean.FALSE,
                BigInteger.valueOf(1),},
            {
                new QName(NAMESPACE, "timeBase"),
                "TimeBase",
                TimeBase.class,
                TimeBaseVerifier.class,
                Boolean.FALSE,
                TimeBase.MEDIA,},
            // 'use' attribute applies only to ttp:profile element
            {
                new QName("", "use"),
                "Use",
                String.class,
                ProfileVerifier.class,
                Boolean.FALSE,
                null,},
            // 'xml:base' attribute applies only to ttp:features and ttp:extensions elements
            {
                XML.getBaseAttributeName(),
                "Base",
                String.class,
                BaseVerifier.class,
                Boolean.FALSE,
                null,},}));
    }

}
