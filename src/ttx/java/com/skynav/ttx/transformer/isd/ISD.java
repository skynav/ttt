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
import java.util.List;
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
import com.skynav.ttv.model.ttml1.tt.ObjectFactory;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Set;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.Time;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;
import com.skynav.ttv.verifier.util.Timing;

public class ISD {

    public static final String TRANSFORMER_NAME = "isd";
    public static final Transformer TRANSFORMER = new ISDTransformer();

    public static class ISDTransformer extends AbstractTransformer {

        private Map<Object, Object> parents = new java.util.HashMap<Object, Object>();
        private Map<Object, TimingState> timingStates = new java.util.HashMap<Object, TimingState>();
        private int anonymousSpanIndex;

        protected ISDTransformer() {
        }

        public String getName() {
            return TRANSFORMER_NAME;
        }

        public void transform(Object root, ExternalParameters externalParameters, OutputStream out) {
            TimedText tt = (TimedText) root;
            generateAnonymousSpans(tt);
            recordParents(tt);
            resolveTiming(tt, externalParameters);
        }

        // generate anonymous spans in pre-traversal order  (albeit not sensitive to traversal order);
        // this step must be performed prior to parent recording since new span nodes will be grafted into
        // node hierarchy
        private void generateAnonymousSpans(TimedText tt) {
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (content instanceof String) {
                        List<Serializable> contentChildren;
                        if (parent instanceof Paragraph)
                            contentChildren = ((Paragraph) parent).getContent();
                        else if (parent instanceof Span) {
                            contentChildren = ((Span) parent).getContent();
                            if (contentChildren.size() == 1)
                                contentChildren = null;
                        } else
                            contentChildren = null;
                        if (contentChildren != null)
                            contentChildren.set(contentChildren.indexOf(content), wrapInAnonymousSpan(content));
                    }
                }
            });
        }

        private static final ObjectFactory spanFactory = new ObjectFactory();
        private JAXBElement<Span> wrapInAnonymousSpan(Object content) {
            assert content instanceof String;
            Span span = spanFactory.createSpan();
            span.setId(generateAnonymousSpanId());
            span.getContent().add((Serializable) content);
            return spanFactory.createSpan(span);
        }

        private String generateAnonymousSpanId() {
            return "ttxSpan" + pad(++anonymousSpanIndex, 6);
        }

        private static String digits = "0123456789";
        private static String pad(int value, int width) {
            assert value > 0;
            StringBuffer sb = new StringBuffer(width);
            while (value > 0) {
                sb.append(digits.charAt(value % 10));
                value /= 10;
            }
            while (sb.length() < width) {
                sb.append('0');
            }
            return sb.reverse().toString();
        }

        // record parents in pre-traversal order (albeit not sensitive to traversal order)
        private void recordParents(TimedText tt) {
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (!parents.containsKey(content))
                        parents.put(content, parent);
                }
            });
        }

        private Object getParent(Object content) {
            if (parents.containsKey(content))
                return parents.get(content);
            else
                return null;
        }

        private void resolveTiming(TimedText tt, ExternalParameters externalParameters) {
            // (1) resolve explicit timing
            resolveExplicit(tt, externalParameters);

            // (2) resolve implicit timing
            resolveImplicit(tt, externalParameters);
        }

        private void resolveExplicit(TimedText tt, ExternalParameters externalParameters) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, externalParameters).getTimeParameters();
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (isTimedElement(content)) {
                        TimingState ts = getTimingState(content, timeParameters);
                        ts.resolveExplicit(content);
                    }
                }
            });
        }

        private void resolveImplicit(TimedText tt, ExternalParameters externalParameters) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, externalParameters).getTimeParameters();
            traverse(tt, new PostVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (isTimedElement(content)) {
                        TimingState ts = getTimingState(content, timeParameters);
                        ts.resolveImplicit(content, order);
                    }
                }
            });
        }

        private boolean isTimedElement(Object content) {
            if (content instanceof Body)
                return true;
            else if (content instanceof Division)
                return true;
            else if (content instanceof Paragraph)
                return true;
            else if (content instanceof Span)
                return true;
            else if (content instanceof Region)
                return true;
            else if (content instanceof Set)
                return true;
            else
                return false;
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

        private static class TimeCoordinate implements Comparable<TimeCoordinate> {
            public enum Type {
                Unspecified,
                Invalid,
                Unresolved,
                Indefinite,
                Definite;
            }
            public static final TimeCoordinate UNSPECIFIED = new TimeCoordinate(Type.Unspecified);
            public static final TimeCoordinate INVALID = new TimeCoordinate(Type.Invalid);
            public static final TimeCoordinate UNRESOLVED = new TimeCoordinate(Type.Unresolved);
            public static final TimeCoordinate INDEFINITE = new TimeCoordinate(Type.Indefinite);
            public static final TimeCoordinate ZERO = new TimeCoordinate(0);
            private Type type = Type.Unresolved;
            private double value;
            private TimeCoordinate() {
                this(Type.Unresolved);
            }
            private TimeCoordinate(Type type) {
                this(type, 0);
            }
            private TimeCoordinate(double value) {
                this(Type.Definite, value);
            }
            private TimeCoordinate(Type type, double value) {
                assert (type == Type.Definite) | (value == 0);
                this.type = type;
                this.value = value;
            }
            public boolean isUnspecified() { return type == Type.Unspecified; }
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
                if (o instanceof TimeCoordinate) {
                    TimeCoordinate c = (TimeCoordinate) o;
                    return (c.getType() == getType()) && (c.getValue() == getValue());
                } else
                    return false;
            }
            public String toString() {
                return isDefinite() ? Double.toString(getValue()) : getType().name();
            }
            public int compareTo(TimeCoordinate c) {
                if (less(this, c))
                    return -1;
                else if (greater(this, c))
                    return 1;
                else if (equals(c))
                    return 0;
                else
                    return -1;
            }
            public static TimeCoordinate fromValue(double value) {
                if (value == 0)
                    return ZERO;
                else
                    return new TimeCoordinate(value);
            }
            public static TimeCoordinate min(TimeCoordinate c1, TimeCoordinate c2) {
                assert !c1.isInvalid();
                assert !c2.isInvalid();
                if (c1.isZero())
                    return c1;
                else if (c2.isZero())
                    return c2;
                else if (c1.isDefinite()) {
                    if (c2.isDefinite())
                        return (c1.getValue() < c2.getValue()) ? c1 : c2;
                    else
                        return c1;
                } else if (c1.isIndefinite()) {
                    if (c2.isDefinite())
                        return c2;
                    else
                        return c1;
                } else
                    return c2;
            }
            public static TimeCoordinate max(TimeCoordinate c1, TimeCoordinate c2) {
                assert !c1.isInvalid();
                assert !c2.isInvalid();
                if (c1.isUnresolved())
                    return c1;
                else if (c2.isUnresolved())
                    return c2;
                else if (c1.isDefinite()) {
                    if (c2.isDefinite())
                        return (c1.getValue() > c2.getValue()) ? c1 : c2;
                    else
                        return c2;
                } else
                    return c1;
            }
            public static boolean less(TimeCoordinate c1, TimeCoordinate c2) {
                return !c1.equals(c2) && min(c1,c2).equals(c1);
            }
            public static boolean greater(TimeCoordinate c1, TimeCoordinate c2) {
                return !c1.equals(c2) && max(c1,c2).equals(c1);
            }
            public TimeCoordinate neg() {
                if (isZero())
                    return this;
                else if (isDefinite())
                    return fromValue(-1 * getValue());
                else
                    return this;
            }
            public static TimeCoordinate add(TimeCoordinate d1, TimeCoordinate d2) {
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
            public static TimeCoordinate sub(TimeCoordinate d1, TimeCoordinate d2) {
                return add(d1, d2.neg());
            }
            public static TimeCoordinate mul(TimeCoordinate d1, TimeCoordinate d2) {
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
        }

        public class TimingState {
            // contextual timing parameters
            private TimeParameters timeParameters;
            // explicit timing state
            private TimeCoordinate durExplicit = TimeCoordinate.UNSPECIFIED;
            private TimeCoordinate beginExplicit = TimeCoordinate.UNSPECIFIED;
            private TimeCoordinate endExplicit = TimeCoordinate.UNSPECIFIED;
            // implicit timing state
            private TimeCoordinate durImplicit = TimeCoordinate.UNRESOLVED;
            TimingState(TimeParameters timeParameters) {
                this.timeParameters = timeParameters;
            }
            TimeCoordinate getSimpleDuration() {
                if (durExplicit.isUnspecified()) {
                    if (!endExplicit.isUnspecified())
                        return TimeCoordinate.INDEFINITE;
                    else if (durImplicit.isUnresolved())
                        return TimeCoordinate.UNRESOLVED;
                    else
                        return durImplicit;
                } else
                    return durExplicit;
            }
            TimeCoordinate getBegin() {
                if (!beginExplicit.isUnspecified())
                    return beginExplicit;
                else
                    return TimeCoordinate.ZERO;
            }
            TimeCoordinate getEnd() {
                return endExplicit;
            }
            TimeCoordinate getRepeatCount() {
                return TimeCoordinate.UNSPECIFIED;
            }
            TimeCoordinate getRepeatDuration() {
                return TimeCoordinate.UNSPECIFIED;
            }
            TimeCoordinate getMinimum() {
                return TimeCoordinate.ZERO;
            }
            TimeCoordinate getMaximum() {
                return TimeCoordinate.INDEFINITE;
            }
            TimeCoordinate computeIntermediateActiveDuration() {
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
            TimeCoordinate getActiveDuration() {
                TimeCoordinate b = getBegin();
                TimeCoordinate e = getEnd();
                TimeCoordinate pad;
                if (!endExplicit.isUnspecified() && durExplicit.isUnspecified()) {
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
            void resolveExplicit(Object content) {
                 resolveExplicitDuration(content);
                 resolveExplicitBegin(content);
                 resolveExplicitEnd(content);
                 // String cls = content.getClass().toString();
                 // cls = cls.substring(cls.lastIndexOf('.') + 1);
                 // System.out.println("[D]:RE: " + cls + this);
            }
            private void resolveExplicitDuration(Object content) {
                TimeCoordinate durExplicit = getDurationAttribute(content, timeParameters);
                if (durExplicit.isInvalid())
                    durExplicit = TimeCoordinate.UNSPECIFIED;
                this.durExplicit = durExplicit;
            }
            private void resolveExplicitBegin(Object content) {
                TimeCoordinate beginExplicit = getBeginAttribute(content, timeParameters);
                if (beginExplicit.isInvalid())
                    beginExplicit = TimeCoordinate.UNSPECIFIED;
                this.beginExplicit = beginExplicit;
            }
            private void resolveExplicitEnd(Object content) {
                TimeCoordinate endExplicit = getEndAttribute(content, timeParameters);
                if (endExplicit.isInvalid())
                    endExplicit = TimeCoordinate.UNSPECIFIED;
                this.endExplicit = endExplicit;
            }
            void resolveImplicit(Object content, Visitor.Order order) {
                resolveImplicitDuration(content, order);
                 String cls = content.getClass().toString();
                 cls = cls.substring(cls.lastIndexOf('.') + 1);
                 System.out.println("[D]:Resolved implicit: " + cls + this + ".");
            }
            private void resolveImplicitDuration(Object content, Visitor.Order order) {
                TimeCoordinate durImplicit = this.durImplicit;
                if (durImplicit == TimeCoordinate.UNRESOLVED) {
                    if (isAnonymousSpan(content)) {
                        Object parent = getParent(content);
                        if (isSequenceContainer(parent))
                            durImplicit = TimeCoordinate.ZERO;
                        else
                            durImplicit = TimeCoordinate.INDEFINITE;
                    } else if (isSequenceContainer(content)) {
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
                }
                if (!durExplicit.isUnspecified()) {
                    TimeCoordinate durSimple = getSimpleDuration();
                    if (durImplicit.compareTo(durSimple) != 0)
                        durImplicit = durSimple;
                } else if (content instanceof Body) {
                    double externalDuration = timeParameters.getExternalDuration();
                    if (!Double.isNaN(externalDuration))
                        durImplicit = new TimeCoordinate(externalDuration);
                }
                this.durImplicit = durImplicit;
            }
            private List<TimingState> getChildrenTiming(Object content, TimeParameters timeParameters) {
                @SuppressWarnings("rawtypes")
                List children = null;
                if (content instanceof Body)
                    children = ((Body) content).getDiv();
                else if (content instanceof Division)
                    children = ((Division) content).getBlockClass();
                else if (content instanceof Paragraph)
                    children = dereferenceAsContent(((Paragraph) content).getContent());
                else if (content instanceof Span)
                    children = dereferenceAsContent(((Span) content).getContent());
                List<TimingState> childrenTiming = new java.util.ArrayList<TimingState>();
                for (Object child: children) {
                    TimingState ts = getTimingState(child, timeParameters);
                    childrenTiming.add(ts);
                }
                return childrenTiming;
            }
            @Override
            public String toString() {
                return "[b(" + getBegin() + "),e(" + getEnd() + "),d(" + getSimpleDuration() + "},di(" + durImplicit + ")]";
            }
        }

        public static TimeCoordinate getTimeCoordinateAttribute(Object content, String name, TimeParameters timeParameters) {
            String value = getStringValuedAttribute(content, name);
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

        private static boolean isAnonymousSpan(Object content) {
            if (content instanceof Span) {
                String value = getStringValuedAttribute(content, "id");
                return (value != null) && (value.indexOf("ttxSpan") == 0);
            } else
                return false;
        }

        private static boolean isSequenceContainer(Object content) {
            String value = getStringValuedAttribute(content, "timeContainer");
            return (value != null) && value.equals("seq");
        }

        private static List<Object> dereferenceAsContent(List<Serializable> content) {
            List<Object> dereferencedContent = new java.util.ArrayList<Object>(content.size());
            for (Serializable s: content) {
                if (s instanceof JAXBElement<?>) {
                    Object element = ((JAXBElement<?>)s).getValue();
                    if (element instanceof Span)
                        dereferencedContent.add(element);
                    else if (element instanceof Break)
                        dereferencedContent.add(element);
                }
            }
            return dereferencedContent;
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
