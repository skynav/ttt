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

import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;

import javax.xml.bind.JAXBElement;

import com.skynav.ttx.transformer.AbstractTransformer;
import com.skynav.ttx.transformer.Transformer;
import com.skynav.ttx.util.PostVisitor;
import com.skynav.ttx.util.PreVisitor;
import com.skynav.ttx.util.Visitor;

import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Set;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.util.Timing;

public class ISD {

    public static final String TRANSFORMER_NAME = "isd";
    public static final Transformer TRANSFORMER = new ISDTransformer();

    public static class ISDTransformer extends AbstractTransformer {

        private Map<Object, Object> parents = new java.util.HashMap<Object, Object>();
        private boolean recordedParents;
        private Map<Object, TimingState> timingStates = new java.util.HashMap<Object, TimingState>();

        protected ISDTransformer() {
        }

        public String getName() {
            return TRANSFORMER_NAME;
        }

        public void transform(Object root, OutputStream out) {
            TimedText tt = (TimedText) root;
            recordParents(tt);
            resolveTiming(tt);
        }

        private void recordParents(TimedText tt) {
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (!parents.containsKey(content))
                        parents.put(content, parent);
                }
            });
            recordedParents = true;
        }

        private Object getParent(Object content) {
            assert recordedParents;
            if (parents.containsKey(content))
                return parents.get(content);
            else
                return null;
        }

        private void resolveTiming(TimedText tt) {
            resolveDurations(tt);
        }

        private void resolveDurations(TimedText tt) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt).getTimeParameters();
            traverse(tt, new PostVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    TimingState ts = getTimingState(content, timeParameters);
                    ts.resolveBegin(content);
                    ts.resolveDuration(content);
                }
            });
        }

        private void traverse(TimedText tt, Visitor v) {
            Body body = tt.getBody();
            if (body != null) {
                v.preVisit(tt, null);
                traverse(body, tt, v);
                v.postVisit(tt, null);
            }
        }

        private void traverse(Body body, Object parent, Visitor v) {
            v.preVisit(body, parent);
            for (Set a : body.getAnimationClass())
                traverse(a, body, v);
            for (Division d : body.getDiv())
                traverse(d, body, v);
            v.postVisit(body, parent);
        }

        private void traverse(Division division, Object parent, Visitor v) {
            v.preVisit(division, parent);
            for (Set a : division.getAnimationClass())
                traverse(a, division, v);
            for (Object b : division.getBlockClass())
                traverseBlock(b, division, v);
            v.postVisit(division, parent);
        }

        private void traverse(Paragraph paragraph, Object parent, Visitor v) {
            v.preVisit(paragraph, parent);
            for (Serializable s : paragraph.getContent())
                traverseContent(s, paragraph, v);
            v.postVisit(paragraph, parent);
        }

        private void traverse(Span span, Object parent, Visitor v) {
            v.preVisit(span, parent);
            for (Serializable s : span.getContent())
                traverseContent(s, span, v);
            v.postVisit(span, parent);
        }

        private void traverse(Break br, Object parent, Visitor v) {
            v.preVisit(br, parent);
            for (Set a : br.getAnimationClass())
                traverse(a, br, v);
            v.postVisit(br, parent);
        }

        private void traverse(Set set, Object parent, Visitor v) {
            v.preVisit(set, parent);
            v.postVisit(set, parent);
        }

        private void traverse(String string, Object parent, Visitor v) {
            v.preVisit(string, parent);
            v.postVisit(string, parent);
        }

        private void traverseBlock(Object block, Object parent, Visitor v) {
            if (block instanceof Division)
                traverse((Division) block, parent, v);
            else if (block instanceof Paragraph)
                traverse((Paragraph) block, parent, v);
        }

        private void traverseContent(Serializable content, Object parent, Visitor v) {
            if (content instanceof JAXBElement<?>) {
                Object element = ((JAXBElement<?>)content).getValue();
                if (element instanceof Set)
                    traverse((Set) element, parent, v);
                else if (element instanceof Span)
                    traverse((Span) element, parent, v);
                else if (element instanceof Break)
                    traverse((Break) element, parent, v);
            } else if (content instanceof String) {
                traverse((String) content, parent, v);
            }
        }

        private TimingState getTimingState(Object content, TimeParameters timeParameters) {
            if (!timingStates.containsKey(content))
                timingStates.put(content, new TimingState(timeParameters));
            return timingStates.get(content);
        }

        private static class Duration implements Comparable<Duration> {
            public enum Type {
                Invalid,
                Unresolved,
                Indefinite,
                Definite;
            }
            public static final Duration INVALID = new Duration(Type.Invalid);
            public static final Duration UNRESOLVED = new Duration(Type.Unresolved);
            public static final Duration INDEFINITE = new Duration(Type.Indefinite);
            public static final Duration ZERO = new Duration(0);
            private Type type = Type.Unresolved;
            private double value;
            private Duration() {
                this(Type.Unresolved);
            }
            private Duration(Type type) {
                this(type, 0);
            }
            private Duration(double value) {
                this(Type.Definite, value);
            }
            private Duration(Type type, double value) {
                assert (type == Type.Definite) | (value == 0);
                this.type = type;
                this.value = value;
            }
            public boolean isInvalid() { return type == Type.Invalid; }
            public boolean isUnresolved() { return type == Type.Unresolved; }
            public boolean isIndefinite() { return type == Type.Indefinite; }
            public boolean isDefinite() { return type == Type.Definite; }
            public boolean isZero() { return isDefinite() && (value == 0); }
            public Type getType() { return type; }
            public double getValue() { return value; }
            public int hashCode() {
                return getType().ordinal() + Double.valueOf(getValue()).hashCode();
            }
            public boolean equals(Object o) {
                if (o instanceof Duration) {
                    Duration d = (Duration) o;
                    return (d.getType() == getType()) && (d.getValue() == getValue());
                } else
                    return false;
            }
            public int compareTo(Duration d) {
                if (less(this, d))
                    return -1;
                else if (greater(this, d))
                    return 1;
                else if (equals(d))
                    return 0;
                else
                    return -1;
            }
            public Duration neg() {
                if (isZero())
                    return this;
                else if (isDefinite())
                    return fromValue(-1 * getValue());
                else
                    return this;
            }
            public static Duration fromValue(double value) {
                if (value == 0)
                    return ZERO;
                else
                    return new Duration(value);
            }
            public static Duration add(Duration d1, Duration d2) {
                assert !d1.isInvalid();
                assert !d2.isInvalid();
                if (d1.isUnresolved())
                    return d1;
                else if (d2.isUnresolved())
                    return d2;
                else if (d1.isDefinite()) {
                    if (d2.isDefinite())
                        return fromValue(d1.getValue() + d2.getValue());
                    else
                        return d2;
                } else
                    return d1;
            }
            public static Duration mul(Duration d1, Duration d2) {
                assert !d1.isInvalid();
                assert !d2.isInvalid();
                if (d1.isZero())
                    return d1;
                else if (d2.isZero())
                    return d2;
                else if (d1.isUnresolved())
                    return d1;
                else if (d2.isUnresolved())
                    return d2;
                else if (d1.isDefinite()) {
                    if (d2.isDefinite())
                        return fromValue(d1.getValue() * d2.getValue());
                    else
                        return d2;
                } else
                    return d1;
            }
            public static Duration min(Duration d1, Duration d2) {
                assert !d1.isInvalid();
                assert !d2.isInvalid();
                if (d1.isZero())
                    return d1;
                else if (d2.isZero())
                    return d2;
                else if (d1.isDefinite()) {
                    if (d2.isDefinite())
                        return (d1.getValue() < d2.getValue()) ? d1 : d2;
                    else
                        return d1;
                } else if (d1.isIndefinite()) {
                    if (d2.isDefinite())
                        return d2;
                    else
                        return d1;
                } else
                    return d2;
            }
            public static Duration max(Duration d1, Duration d2) {
                assert !d1.isInvalid();
                assert !d2.isInvalid();
                if (d1.isUnresolved())
                    return d1;
                else if (d2.isUnresolved())
                    return d2;
                else if (d1.isDefinite()) {
                    if (d2.isDefinite())
                        return (d1.getValue() > d2.getValue()) ? d1 : d2;
                    else
                        return d2;
                } else
                    return d1;
            }
            public static boolean less(Duration d1, Duration d2) {
                return !d1.equals(d2) && min(d1,d2).equals(d1);
            }
            public static boolean greater(Duration d1, Duration d2) {
                return !d1.equals(d2) && max(d1,d2).equals(d1);
            }
        }

        public static class TimingState {
            private TimeParameters timeParameters;
            public Duration begin = Duration.UNRESOLVED;
            public Duration durImplicit = Duration.UNRESOLVED;
            public Duration durSimple = Duration.UNRESOLVED;
            public Duration durActive = Duration.UNRESOLVED;
            TimingState(TimeParameters timeParameters) {
                this.timeParameters = timeParameters;
            }
            void resolveBegin(Object content) {
                /*
                ** (1) Unless otherwise specified below, if there is any error in the argument value syntax for an attribute,
                **     the attribute will be ignored (as though it were not specified).
                ** (2) If no begin is specified, the default timing is dependent upon the time container.
                ** (3) A time value may conform to the defined syntax but still be invalid (e.g. if an unknown element is referenced
                **     by ID in a syncbase value). If there is such an evaluation error in an individual value in the list of begin or end values,
                **     the individual value will be will be treated as though "indefinite" were specified, and the rest of the list will be processed normally.
                **     If no legal value is specified for a begin or end attribute, the element assumes an "indefinite" begin or end time (respectively).
                ** (4) The element will actually begin at the time computed according to the following algorithm:
                **
                **     Let o be the offset value of a given begin value,
                **     d be the associated simple duration, 
                **     AD be the associated active duration.
                **     Let rAt be the time when the begin time becomes resolved.
                **     Let rTo be the resolved sync-base or event-base time without the offset
                **     Let rD be rTo - rAt.  If rD < 0 then rD is set to 0.
                **
                **     If AD is indefinite, it compares greater than any value of o or ABS(o).
                **     REM( x, y ) is defined as x - (y * floor( x/y )). 
                **     If y is indefinite or unresolved, REM( x, y ) is just x.
                **     
                **     Let mb = REM( ABS(o), d ) - rD
                **     If ABS(o) >= AD then the element does not begin.
                **     Else if mb >= 0 then the media begins at mb.
                **     Else the media begins at mb + d.
                */
                if (this.begin.isUnresolved()) {
                    Duration begin = getBegin(content, timeParameters);
                    if (begin.isInvalid())
                        begin = Duration.UNRESOLVED;
                    if (begin.isUnresolved())
                        begin = Duration.ZERO;
                    this.begin = begin;
                }
            }
            void resolveDuration(Object content) {
                /*
                ** IMPLICIT DURATION
                **
                ** (1) An element with the timeContainer attribute behaves the same as a media time container.
                ** (2) If a timeContainer attribute is not specified on an element that has time container semantics, then par time container semantics must apply.
                ** (3) Time container semantics applies only to the following element types: body, div, p, region, span.
                ** (4) The implicit duration of a par is controlled by endsync.
                ** (5) For the purpose of determining the [SMIL 2.1] endsync semantics of a par time container, a default value of all applies.
                ** (6) For endsync of 'all', the par, excl, or media element's implicit duration ends when all of the child elements have ended their
                **     respective active durations. Elements with indefinite or unresolved begin times will keep the simple duration of the time container from ending.
                **     When all elements have completed the active duration one or more times, the parent time container may end.
                ** (7) The implicit duration of a seq ends with the active end of the last child of the seq.
                ** (8) If any child of a seq has an indefinite active duration, the implicit duration of the seq is also indefinite.
                */

                /*
                ** SIMPLE DURATION
                **
                ** (1) If there is any error in the argument value syntax for dur, the attribute will be ignored (as though it were not specified).
                ** (2) If the element does not have a (valid) dur attribute, the simple duration for the element is defined to be the implicit duration of the element.
                ** (3) If the author specifies a value for dur that is shorter than the implicit duration for an element,
                **     the implicit duration will be cut short by the specified simple duration.
                ** (4) If the author specifies a simple duration that is longer than the implicit duration for an element,
                **     the implicit duration of the element is extended to the specified simple duration:
                **     a) For a discrete media element, the media will be shown for the specified simple duration.
                **     b) For a continuous media element, the ending state of the media (e.g. the last frame of video) will be shown
                **        from the end of the intrinsic media duration to the end of the specified simple duration. This only applies
                **        to visual media - aural media will simply stop playing (i.e. be silent).
                **     c) For a seq time container, the last child is frozen until the end of the simple duration of the seq
                **        if and only if its fill behavior is "freeze" or "hold" (otherwise the child just ends without freezing).
                **     d) Children of a par or excl are frozen until the end of the simple duration of the par or excl
                **        if and only if the children's fill behavior is "freeze" or "hold" (otherwise the children just ends without freezing).
                */
                if (this.durSimple.isUnresolved()) {
                    Duration durExplicit = getDur(content, timeParameters);
                    if (durExplicit.isInvalid())
                        durExplicit = Duration.UNRESOLVED;
                    Duration durImplicit = getDurImplicit(content, timeParameters);
                    Duration durSimple = durExplicit.isUnresolved() ? durImplicit : durExplicit;
                    switch (durSimple.compareTo(durImplicit)) {
                    case -1: // explicit duration is shorter than implicit duration
                        durImplicit = durExplicit;
                        break;
                    case 1: // explicit duration is longer than implicit duration
                        durImplicit = durExplicit;
                        break;
                    default:
                        break;
                    }
                    this.durImplicit = durImplicit;
                    this.durSimple = durSimple;
                }
            }
            void resolveEnd(Object content) {
                /*
                ** ACTIVE DURATION
                **
                ** (1) The end attribute allows the author to constrain the active duration ...
                ** (2) If the end value becomes resolved while the element is still active, and the resolved time is in the past,
                **     the element should end the active duration immediately.
                ** (3) The rules for combining the attributes to compute the active duration are presented in the section, Computing the active duration.
                */
                Duration end = getEnd(content, timeParameters);
            }
        }

        public static Duration getTimeAttribute(Object content, String name, TimeParameters timeParameters) {
            String value = getStringValuedAttribute(content, name);
            if (value != null) {
                Time[] times = new Time[1];
                if (Timing.isCoordinate(value, null, null, timeParameters, times)) {
                    assert times.length > 0;
                    return Duration.fromValue(times[0].getTime(timeParameters));
                } else {
                    return Duration.INVALID;
                }
            }
            return Duration.UNRESOLVED;
        }

        public static Duration getBegin(Object content, TimeParameters timeParameters) {
            return getTimeAttribute(content, "begin", timeParameters);
        }

        public static Duration getEnd(Object content, TimeParameters timeParameters) {
            return getTimeAttribute(content, "end", timeParameters);
        }

        public static Duration getDur(Object content, TimeParameters timeParameters) {
            return getTimeAttribute(content, "dur", timeParameters);
        }

        public static Duration getDurImplicit(Object content, TimeParameters timeParameters) {
            return Duration.UNRESOLVED;
        }

        private static String getStringValuedAttribute(Object content, String attributeName) {
            try {
                Class<?> contentClass = content.getClass();
                Method m = contentClass.getMethod(makeGetterName(attributeName), new Class<?>[]{});
                return (String) m.invoke(content, new Object[]{});
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                return null;
            } catch (SecurityException e) {
                throw new RuntimeException(e);
            }
        }

        private static String makeGetterName(String attributeName) {
            assert attributeName.length() > 0;
            StringBuffer sb = new StringBuffer();
            sb.append("get");
            sb.append(Character.toUpperCase(attributeName.charAt(0)));
            sb.append(attributeName.substring(1));
            return sb.toString();
        }
    }
}
