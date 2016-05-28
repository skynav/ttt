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

package com.skynav.ttv.verifier.util;

import java.util.List;
import java.util.Set;

import org.xml.sax.Locator;

import com.skynav.ttv.model.value.Length;
import com.skynav.ttv.model.value.Measure;
import com.skynav.ttv.model.value.impl.MeasureImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.MixedUnitsTreatment;
import com.skynav.ttv.verifier.util.NegativeTreatment;

public class Measures {

    public static boolean isMeasure(String value, Location location, VerifierContext context, Object[] treatments, Measure[] outputMeasure) {
        Measure m = null;
        if (isMeasureKeyword(value)) {
            try { 
                m = new MeasureImpl(Measure.Type.valueOfShorthand(value), (Length) null);
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException(e.getMessage());
            }
        } else {
            Object[] treatmentsInner = new Object[] {NegativeTreatment.Error, findTreatment(treatments, MixedUnitsTreatment.class)};
            Length[] length = new Length[1];
            if (Lengths.isLength(value, location, context, treatmentsInner, length))
                m = new MeasureImpl(Measure.Type.Length, length[0]);
        }
        if (m != null) {
            if (outputMeasure != null)
                outputMeasure[0] = m;
            return true;
        } else
            return false;
    }

    public static void badMeasure(String value, Location location, VerifierContext context, Object[] treatments) {
        if (!isMeasureKeyword(value)) {
            Object[] treatmentsInner = new Object[] {NegativeTreatment.Error, findTreatment(treatments, MixedUnitsTreatment.class)};
            Length[] length = new Length[1];
            if (!Lengths.isLength(value, location, context, treatmentsInner, length))
                Lengths.badLength(value, location, context, treatmentsInner);
        }
    }

    private static boolean isMeasureKeyword(String s) {
        if (s.equals("auto"))
            return true;
        else if (s.equals("available"))
            return true;
        else if (s.equals("fitContent"))
            return true;
        else if (s.equals("maxContent"))
            return true;
        else if (s.equals("minContent"))
            return true;
        else
            return false;
    }

    private static Object findTreatment(Object[] treatments, Class<?> treatmentClass) {
        if ((treatments != null) && (treatments.length > 0)) {
            for (Object t : treatments) {
                if (treatmentClass.isInstance(t))
                    return t;
            }
        }
        return null;
    }

    public static boolean isMeasures(String value, Location location, VerifierContext context, Integer[] minMax, Object[] treatments, List<Measure> outputMeasures) {
        Reporter reporter = (context != null) ? context.getReporter() : null;
        Locator locator = location.getLocator();
        List<Measure> measures = new java.util.ArrayList<Measure>();
        String [] measureComponents = value.split("[ \t\r\n]+");
        int numComponents = measureComponents.length;
        for (String component : measureComponents) {
            Measure[] measure = new Measure[1];
            if (isMeasure(component, location, context, treatments, measure))
                measures.add(measure[0]);
            else
                return false;
        }
        if (minMax != null) {
            if (numComponents < minMax[0])
                return false;
            else if (numComponents > minMax[1])
                return false;
        }
        if (treatments != null) {
            List<Length> lengths = new java.util.ArrayList<Length>();
            for (Measure m : measures) {
                if (m.isLength())
                    lengths.add(m);
            }
            if (!Units.sameUnits(lengths)) {
                assert treatments.length > 1;
                MixedUnitsTreatment mixedUnitsTreatment = (MixedUnitsTreatment) treatments[1];
                Set<Length.Unit> units = Units.units(lengths);
                if (mixedUnitsTreatment == MixedUnitsTreatment.Error)
                    return false;
                else if (reporter != null) {
                    if (mixedUnitsTreatment == MixedUnitsTreatment.Warning) {
                        if (reporter.logWarning(reporter.message(locator, "*KEY*",
                            "Mixed units {0} should not be used in <measure> expressions.", Length.Unit.shorthands(units)))) {
                            treatments[1] = MixedUnitsTreatment.Allow;                          // suppress second warning
                            return false;
                        }
                    } else if (mixedUnitsTreatment == MixedUnitsTreatment.Info) {
                        reporter.logInfo(reporter.message(locator, "*KEY*",
                            "Mixed units {0} used in <measure> expressions.", Length.Unit.shorthands(units)));
                    }
                }
            }
        }
        if (outputMeasures != null) {
            outputMeasures.clear();
            outputMeasures.addAll(measures);
        }
        return true;
    }

    public static void badMeasures(String value, Location location, VerifierContext context, Integer[] minMax, Object[] treatments) {
        Reporter reporter = context.getReporter();
        Locator locator = location.getLocator();
        String [] measureComponents = value.split("[ \t\r\n]+");
        int numComponents = measureComponents.length;
        Object[] treatmentsInner = (treatments != null) ? new Object[] { treatments[0], treatments[1] } : null;
        List<Length> lengths = new java.util.ArrayList<Length>();
        for (String component : measureComponents) {
            Measure[] measure = new Measure[1];
            if (isMeasure(component, location, context, treatmentsInner, measure)) {
                Measure m = measure[0];
                if (m.isLength())
                    lengths.add(m);
            } else
                badMeasure(component, location, context, treatmentsInner);
        }
        if (numComponents < minMax[0]) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Missing <measure> expression, got {0}, but expected at least {1} <measure> expressions.", numComponents, minMax[0]));
        } else if (numComponents > minMax[1]) {
            reporter.logInfo(reporter.message(locator, "*KEY*",
                "Extra <measure> expression, got {0}, but expected no more than {1} <measure> expressions.", numComponents, minMax[1]));
        }
        if (treatments != null) {
            MixedUnitsTreatment mixedUnitsTreatment = (MixedUnitsTreatment) treatments[1];
            if (!Units.sameUnits(lengths) && (mixedUnitsTreatment == MixedUnitsTreatment.Error)) {
                reporter.logInfo(reporter.message(locator, "*KEY*", "Mixed units {0} not permitted.", Length.Unit.shorthands(Units.units(lengths))));
            }
        }
    }

}
