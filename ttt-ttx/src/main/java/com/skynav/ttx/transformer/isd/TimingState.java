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
 
package com.skynav.ttx.transformer.isd;

import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.util.Timing;

import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.util.TimeCoordinate;
import com.skynav.ttx.util.TimeInterval;

public class TimingState {

    public static final QName beginAttributeName = new QName(TTMLHelper.NAMESPACE_ISD,"begin");
    public static final QName endAttributeName = new QName(TTMLHelper.NAMESPACE_ISD,"end");

    // contextual state
    private TransformerContext context;                 // transformer context object
    private TTMLHelper helper;                          // ttml helper
    private Object content;                             // (jaxb) content object
    private TimeParameters timeParameters;              // timing parameters
    // explicit timing state
    private TimeCoordinate durExplicit = TimeCoordinate.UNSPECIFIED;
    private TimeCoordinate beginExplicit = TimeCoordinate.UNSPECIFIED;
    private TimeCoordinate endExplicit = TimeCoordinate.UNSPECIFIED;
    // implicit timing state
    private TimeCoordinate durImplicit = TimeCoordinate.UNRESOLVED;
    // active timing state
    private TimeCoordinate beginActive = TimeCoordinate.UNRESOLVED;
    private TimeCoordinate endActive = TimeCoordinate.UNRESOLVED;

    public TimingState(TransformerContext context, Object content, TimeParameters timeParameters) {
        this.context = context;
        this.helper = (TTMLHelper) context.getResourceState(ResourceState.isdHelper.name());
        this.content = content;
        this.timeParameters = timeParameters;
    }

    public TimeCoordinate getSimpleDuration() {
        // SMIL 3.0 Timing §5.4.5 "Defining the Simple Duration"
        if (durExplicit.isUnspecified()) {
            if (endExplicit.isSpecified())
                return TimeCoordinate.INDEFINITE;
            else if (durImplicit.isUnresolved())
                return TimeCoordinate.UNRESOLVED;
            else
                return durImplicit;
        } else if (durExplicit.isIndefinite()) {
            return TimeCoordinate.INDEFINITE;
        } else
            return durExplicit;
    }

    public TimeCoordinate getBegin() {
        if (beginExplicit.isSpecified())
            return beginExplicit;
        else
            return TimeCoordinate.ZERO;
    }

    public TimeCoordinate getEnd() {
        if (endExplicit.isSpecified())
            return endExplicit;
        else
            return TimeCoordinate.UNRESOLVED;
    }

    public TimeCoordinate getRepeatCount() {
        return TimeCoordinate.UNSPECIFIED;
    }

    public TimeCoordinate getRepeatDuration() {
        return TimeCoordinate.UNSPECIFIED;
    }

    public TimeCoordinate getMinimum() {
        return TimeCoordinate.ZERO;
    }

    public TimeCoordinate getMaximum() {
        return TimeCoordinate.INDEFINITE;
    }

    private TimeCoordinate computeIntermediateActiveDuration() {
        // SMIL 3.0 Timing §5.4.5 "Intermedia Active Duration Computation"
        TimeCoordinate p0 = getSimpleDuration();
        TimeCoordinate rc = getRepeatCount();
        TimeCoordinate rd = getRepeatDuration();
        TimeCoordinate p1 = rc.isUnspecified() ? TimeCoordinate.INDEFINITE : TimeCoordinate.mul(rc, rd);
        TimeCoordinate p2 = rd.isUnspecified() ? TimeCoordinate.INDEFINITE : rd;
        if (p0.isZero())
            return TimeCoordinate.ZERO;
        else if (rc.isUnspecified() && rd.isUnspecified())
            return p0;
        else
            return TimeCoordinate.min(p1, TimeCoordinate.min(p2, TimeCoordinate.INDEFINITE));
    }

    public TimeCoordinate getActiveDuration() {
        // SMIL 3.0 Timing §5.4.5 "Computing the Active Duration"
        TimeCoordinate b = getBegin();
        TimeCoordinate e = getEnd();
        TimeCoordinate pad;
        if (endExplicit.isSpecified() && durExplicit.isUnspecified()) {
            if (e.isDefinite())
                pad = TimeCoordinate.sub(e, b);
            else if (e.isIndefinite())
                pad = TimeCoordinate.INDEFINITE;
            else
                pad = TimeCoordinate.UNRESOLVED;
        } else {
            TimeCoordinate iad = computeIntermediateActiveDuration();
            if (endExplicit.isUnspecified() || endExplicit.isIndefinite()) {
                pad = iad;
            } else {
                pad = TimeCoordinate.min(iad, TimeCoordinate.sub(e, b));
            }
        }
        TimeCoordinate min = getMinimum();
        TimeCoordinate max = getMaximum();
        return TimeCoordinate.min(max, TimeCoordinate.max(min, pad));
    }

    public TimeCoordinate getActiveBegin() {
        return beginActive;
    }

    public TimeCoordinate getActiveEnd() {
        return endActive;
    }

    public void resolveExplicit() {
         resolveExplicitDuration();
         resolveExplicitBegin();
         resolveExplicitEnd();
    }

    private void debug(int level, String label, String details) {
        Reporter reporter = context.getReporter();
        if (reporter.getDebugLevel() >= level) {
            reporter.logDebug(reporter.message("*KEY*", "{0}: {1}[{2}].", label, helper.getClassString(content), details));
        }
    }

    private void resolveExplicitDuration() {
        TimeCoordinate durExplicit = getDurationAttribute(content, timeParameters);
        if (durExplicit.isInvalid())
            durExplicit = TimeCoordinate.UNSPECIFIED;
        this.durExplicit = durExplicit;
        if (!durExplicit.isUnspecified())
            debug(2, "Resolve explicit duration", durExplicit.toString());
    }

    private void resolveExplicitBegin() {
        TimeCoordinate beginExplicit = getBeginAttribute(content, timeParameters);
        if (beginExplicit.isInvalid())
            beginExplicit = TimeCoordinate.UNSPECIFIED;
        this.beginExplicit = beginExplicit;
        if (!beginExplicit.isUnspecified())
            debug(2, "Resolve explicit begin", beginExplicit.toString());
    }

    private void resolveExplicitEnd() {
        TimeCoordinate endExplicit = getEndAttribute(content, timeParameters);
        if (endExplicit.isInvalid())
            endExplicit = TimeCoordinate.UNSPECIFIED;
        this.endExplicit = endExplicit;
        if (!endExplicit.isUnspecified())
            debug(2, "Resolve explicit end", endExplicit.toString());
    }

    public void resolveImplicit() {
        resolveImplicitDuration();
    }

    private void resolveImplicitDuration() {
        TimeCoordinate durImplicit = this.durImplicit;
        if (helper.isAnonymousSpan(content)) {
            if (helper.isSequenceContainer(getParent(content)))
                durImplicit = TimeCoordinate.ZERO;
            else
                durImplicit = TimeCoordinate.INDEFINITE;
        } else if (helper.isSequenceContainer(content)) {
            TimeCoordinate sum = null;
            for (TimingState ts : getChildrenTiming(content, timeParameters)) {
                TimeCoordinate begin = ts.getBegin();
                TimeCoordinate activeDuration = ts.getActiveDuration();
                TimeCoordinate end = TimeCoordinate.add(begin, activeDuration);
                if (sum == null)
                    sum = end;
                else
                    sum = TimeCoordinate.add(sum, end);
            }
            if (sum != null)
                durImplicit = sum;
        } else {
            TimeCoordinate max = null;
            for (TimingState ts : getChildrenTiming(content, timeParameters)) {
                TimeCoordinate begin = ts.getBegin();
                TimeCoordinate activeDuration = ts.getActiveDuration();
                TimeCoordinate end = TimeCoordinate.add(begin, activeDuration);
                if (max == null)
                    max = end;
                else
                    max = TimeCoordinate.max(max, end);
            }
            if (max != null)
                durImplicit = max;
        }
        if (durExplicit.isSpecified()) {
            TimeCoordinate durSimple = getSimpleDuration();
            if (durImplicit.compareTo(durSimple) != 0)
                durImplicit = durSimple;
        } else if (helper.isTimedText(content)) {
            double externalDuration = timeParameters.getExternalDuration();
            if (!Double.isNaN(externalDuration))
                durImplicit = new TimeCoordinate(externalDuration);
        }
        this.durImplicit = durImplicit;
        debug(2, "Resolve implicit duration", durImplicit.toString());
    }

    public void resolveActive() {
        TimeCoordinate b = getBegin();
        TimeCoordinate e = getEnd();
        if (helper.isTimedText(content)) {
            beginActive = b;
            endActive = e;
            if (!endActive.isDefinite())
                endActive = TimeCoordinate.add(beginActive, getActiveDuration());
            if (!endActive.isDefinite())
                endActive = beginActive;
        } else {
            Object parent = getParent(content);
            TimingState tsParent = getTimingState(parent, timeParameters);
            TimeCoordinate bParent = (tsParent != null) ? tsParent.getActiveBegin() : TimeCoordinate.ZERO;
            TimeCoordinate reference;
            if (helper.isSequenceContainer(parent)) {
                TimingState tsElder = getTimingState(getPrevSibling(content, parent), timeParameters);
                reference = (tsElder != null) ? tsElder.getActiveEnd() : bParent;
            } else {
                reference = bParent;
            }
            // set active begin
            beginActive = TimeCoordinate.add(reference, b);
            // set active end
            endActive = TimeCoordinate.add(reference, e);
            if (!endActive.isDefinite()) {
                assert beginActive.isDefinite();
                endActive = TimeCoordinate.add(beginActive, getActiveDuration());
            }
            // clip active end to parent's active end
            TimeCoordinate eParent = (tsParent != null) ? tsParent.getActiveEnd() : TimeCoordinate.UNRESOLVED;
            if (!endActive.isDefinite() || endActive.compareTo(eParent) > 0)
                endActive = eParent;
            // pin active end to active begin if the former precedes the latter
            if (!endActive.isDefinite() || (endActive.compareTo(beginActive) < 0))
                endActive = beginActive;
        }
        debug(2, "Resolve active", getActiveBegin() + "," + getActiveEnd());
        // record active interval attributes
        Map<QName,String> otherAttributes = TTMLHelper.getOtherAttributes(content);
        if (otherAttributes != null) {
            otherAttributes.put(beginAttributeName, beginActive.toString().toLowerCase());
            otherAttributes.put(endAttributeName, endActive.toString().toLowerCase());
        }
    }

    public void extractActiveInterval(java.util.Set<TimeInterval> intervals) {
        intervals.add(new TimeInterval(beginActive, endActive));
    }

    private Object getParent(Object content) {
        Map<Object,Object> parents = ISD.getParents(context);
        if (parents.containsKey(content))
            return parents.get(content);
        else
            return null;
    }

    private Object getPrevSibling(Object content, Object parent) {
        if (parent == null)
            return null;
        Object prevSibling = null;
        for (Object sibling : helper.getChildren(parent)) {
            if (sibling == content)
                return prevSibling;
            prevSibling = sibling;
        }
        return null;
    }

    private TimingState getTimingState(Object content, TimeParameters timeParameters) {
        if (content == null)
            return null;
        Map<Object,TimingState> timingStates = ISD.getTimingStates(context);
        if (!timingStates.containsKey(content))
            timingStates.put(content, new TimingState(context, content, timeParameters));
        return timingStates.get(content);
    }

    private List<TimingState> getChildrenTiming(Object content, TimeParameters timeParameters) {
        List<TimingState> childrenTiming = new java.util.ArrayList<TimingState>();
        for (Object child: helper.getChildren(content)) {
            TimingState ts = getTimingState(child, timeParameters);
            childrenTiming.add(ts);
        }
        return childrenTiming;
    }

    @Override
    public String toString() {
        return "[b(" + getBegin() + "),e(" + getEnd() + "),d(" + getSimpleDuration() + "),di(" + durImplicit + ")]";
    }

    public static TimeCoordinate getTimeCoordinateAttribute(Object content, String name, TimeParameters timeParameters) {
        String value = TTMLHelper.getStringValuedAttribute(content, name);
        if (value != null) {
            Time[] times = new Time[1];
            if (Timing.isCoordinate(value, null, null, timeParameters, times)) {
                assert times.length > 0;
                return TimeCoordinate.fromValue(times[0].getTime(timeParameters));
            } else {
                return TimeCoordinate.INVALID;
            }
        } else
            return TimeCoordinate.UNSPECIFIED;
    }

    public static TimeCoordinate getDurationAttribute(Object content, TimeParameters timeParameters) {
        return getTimeCoordinateAttribute(content, "dur", timeParameters);
    }

    public static TimeCoordinate getBeginAttribute(Object content, TimeParameters timeParameters) {
        return getTimeCoordinateAttribute(content, "begin", timeParameters);
    }

    public static TimeCoordinate getEndAttribute(Object content, TimeParameters timeParameters) {
        return getTimeCoordinateAttribute(content, "end", timeParameters);
    }

}
