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
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.JAXBElement;

import com.skynav.ttv.model.ttml1.tt.Body;
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Division;
import com.skynav.ttv.model.ttml1.tt.ObjectFactory;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Region;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.model.ttml1.tt.TimedText;
import com.skynav.ttv.model.value.TimeParameters;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ttml.timing.TimingVerificationParameters;

import com.skynav.ttx.transformer.AbstractTransformer;
import com.skynav.ttx.transformer.Transformer;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.ttx.util.PostVisitor;
import com.skynav.ttx.util.PreVisitor;
import com.skynav.ttx.util.TimeCoordinate;
import com.skynav.ttx.util.TimeInterval;
import com.skynav.ttx.util.Visitor;

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

        public void transform(Object root, TransformerContext context, OutputStream out) {
            assert root != null;
            assert context != null;
            Reporter reporter = context.getReporter();
            reporter.logInfo(reporter.message("*KEY*", "Transforming result using ''{0}'' transformer...", getName()));
            TimedText tt = (TimedText) root;
            // extract significant time intervals
            Set<TimeInterval> intervals = extractISDIntervals(tt, context);
            if (reporter.getDebugLevel() > 0) {
                StringBuffer sb = new StringBuffer();
                for (TimeInterval interval : intervals) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append(interval);
                }
                reporter.logDebug(reporter.message("*KEY*", "Resolved active intervals: {0}.", "{" + sb + "}"));
            }
        }

        private Set<TimeInterval> extractISDIntervals(TimedText tt, TransformerContext context) {
            Reporter reporter = context.getReporter();
            generateAnonymousSpans(tt);
            recordParents(tt);
            resolveTiming(tt, context);
            Set<TimeCoordinate> coordinates = new java.util.TreeSet<TimeCoordinate>();
            for (TimeInterval interval : extractActiveIntervals(tt, context)) {
                coordinates.add(interval.getBegin());
                coordinates.add(interval.getEnd());
            }
            if (reporter.getDebugLevel() > 0) {
                StringBuffer sb = new StringBuffer();
                for (TimeCoordinate coordinate : coordinates) {
                    if (sb.length() > 0)
                        sb.append(',');
                    sb.append(coordinate);
                }
                reporter.logDebug(reporter.message("*KEY*", "Resolved active coordinates: {0}.", "{" + sb + "}"));
            }
            Set<TimeInterval> intervals = new java.util.TreeSet<TimeInterval>();
            TimeCoordinate lastCoordinate = null;
            for (TimeCoordinate coordinate : coordinates) {
                if (lastCoordinate != null)
                    intervals.add(new TimeInterval(lastCoordinate, coordinate));
                lastCoordinate = coordinate;
            }
            return intervals;
        }

        private void generateAnonymousSpans(TimedText tt) {
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (content instanceof String) {
                        List<Serializable> contentChildren;
                        if (parent instanceof Paragraph)
                            contentChildren = ((Paragraph) parent).getContent();
                        else if (parent instanceof Span)
                            contentChildren = ((Span) parent).getContent();
                        else
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

        private void recordParents(TimedText tt) {
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (!parents.containsKey(content))
                        parents.put(content, parent);
                }
            });
        }

        private void resolveTiming(TimedText tt, TransformerContext context) {
            resolveExplicit(tt, context);
            resolveImplicit(tt, context);
            resolveActive(tt, context);
        }

        private void resolveExplicit(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveExplicit();
                    }
                }
            });
        }

        private void resolveImplicit(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PostVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveImplicit();
                    }
                }
            });
        }

        private void resolveActive(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content, context, timeParameters);
                        ts.resolveActive();
                    }
                }
            });
        }

        private java.util.Set<TimeInterval> extractActiveIntervals(TimedText tt, final TransformerContext context) {
            final TimeParameters timeParameters = new TimingVerificationParameters(tt, context.getExternalParameters()).getTimeParameters();
            final java.util.Set<TimeInterval> intervals = new java.util.TreeSet<TimeInterval>();
            traverse(tt, new PreVisitor() {
                public void visit(Object content, Object parent, Visitor.Order order) {
                    if (TimingState.isTimedElement(content)) {
                        TimingState ts = getTimingState(content,context, timeParameters);
                        ts.extractActiveInterval(intervals);
                    }
                }
            });
            return intervals;
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
            for (com.skynav.ttv.model.ttml1.tt.Set a : body.getAnimationClass())
                traverse(a, body, v);
            for (Division d : body.getDiv())
                traverse(d, body, v);
            v.postVisit(body, parent);
        }

        private void traverse(Division division, Object parent, Visitor v) {
            v.preVisit(division, parent);
            for (com.skynav.ttv.model.ttml1.tt.Set a : division.getAnimationClass())
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
            for (com.skynav.ttv.model.ttml1.tt.Set a : br.getAnimationClass())
                traverse(a, br, v);
            v.postVisit(br, parent);
        }

        private void traverse(com.skynav.ttv.model.ttml1.tt.Set set, Object parent, Visitor v) {
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
                if (element instanceof com.skynav.ttv.model.ttml1.tt.Set)
                    traverse((com.skynav.ttv.model.ttml1.tt.Set) element, parent, v);
                else if (element instanceof Span)
                    traverse((Span) element, parent, v);
                else if (element instanceof Break)
                    traverse((Break) element, parent, v);
            } else if (content instanceof String) {
                traverse((String) content, parent, v);
            }
        }

        private TimingState getTimingState(Object content, TransformerContext context, TimeParameters timeParameters) {
            if (content == null)
                return null;
            if (!timingStates.containsKey(content))
                timingStates.put(content, new TimingState(context, content, parents, timingStates, timeParameters));
            return timingStates.get(content);
        }

    }
}
