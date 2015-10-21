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
import com.skynav.ttv.model.ttml1.tt.Break;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.ebuttd.EBUTTDSemanticsVerifier.Limits;
import com.skynav.ttv.verifier.TimingVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1TimingVerifier;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBElement;
import org.xml.sax.Locator;

/**
 *
 * @author msamek
 */
public class EBUTTDTimingVerifier extends TTML1TimingVerifier {

    protected TimingVerifier timingVerifier;

    public EBUTTDTimingVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (content instanceof Paragraph) {
            verifyParagraph(content, locator, context);
        } else if (content instanceof Span) {
            verifySpan(content, locator, context);
        }

        return super.verify(content, locator, context, type);
    }

    private boolean verifyParagraph(Object content, Locator locator, VerifierContext context) {
        assert content instanceof Paragraph;
        if (!verifyDuration(content, locator, context)) {
            return false;
        }
        return verifyTimingSpecified(content, locator, context);
    }

    private boolean verifySpan(Object content, Locator locator, VerifierContext context) {
        assert content instanceof Span;
        return verifyDuration(content, locator, context);
    }

    /**
     * Verifies if timing attributes are specified correctly. That is ... they
     * are specified either on paragraph element and on none of its children
     * elements, or they are specified on span element, but not on its parent
     * element.
     *
     * @param content
     * @param locator
     * @param context
     * @return
     */
    private boolean verifyTimingSpecified(Object content, Locator locator, VerifierContext context) {
        String begin;
        Reporter reporter = context.getReporter();
        // now content can be only Paragraph or Span
        assert content instanceof Paragraph || content instanceof Span;
        List<Serializable> children;
        if (content instanceof Paragraph) {
            Paragraph par = (Paragraph) content;
            begin = par.getBegin();
            children = par.getContent();
        } else {
            Span span = (Span) content;
            begin = span.getBegin();
            children = span.getContent();
        }

        if (begin == null && children.isEmpty()) {
            reporter.logError(reporter.message(locator, "*KEY*",
                    "Missing timing attributes on {0}.",
                    context.getBindingElementName(content)));
            return false;
        }

        for (Object child : children) {
            if (child instanceof JAXBElement) {
                JAXBElement jaxbel = (JAXBElement) child;
                child = jaxbel.getValue();
            }

            if (begin != null) {
                if (child instanceof String || child instanceof Break) {
                    continue;
                }
                if (!verifyTimingNotSpecified(child, locator, context)) {
                    reporter.logError(reporter.message(locator, "*KEY*",
                            "Timing attributes incorrectly specified on children of {0}.",
                            context.getBindingElementName(content)));
                    return false;
                }
            } else {
                if (child instanceof String || child instanceof Break) {
                    reporter.logError(reporter.message(locator, "*KEY*",
                            "Missing timing attributes on {0}.",
                            context.getBindingElementName(content)));
                    return false;
                }
                if (!verifyTimingSpecified(child, locator, context)) {
                    reporter.logError(reporter.message(locator, "*KEY*",
                            "Timing attributes not specified on {0} nor on its child node {1}.",
                            context.getBindingElementName(content),
                            context.getBindingElementName(child)));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean verifyTimingNotSpecified(Object content, Locator locator, VerifierContext context) {
        if (content instanceof JAXBElement) {
            JAXBElement jaxbEl = (JAXBElement) content;
            return verifyTimingNotSpecified(jaxbEl.getValue(), locator, context);
        } else if (content instanceof Break) {
            return true;
        }
        // now content can be only Span, since for Paragraphs cannot be childs of themselves or Spans
        assert content instanceof Span;
        Span span = (Span) content;

        if (span.getBegin() != null) {
            return false;
        }

        List<Serializable> children = span.getContent();
        for (Object child : children) {
            if (child instanceof JAXBElement) {
                JAXBElement jaxbel = (JAXBElement) child;
                child = jaxbel.getValue();
            }

            if (child instanceof String) {
                continue;
            }
            if (!verifyTimingNotSpecified(child, locator, context)) {
                return false;
            }
        }
        return true;
    }

    // For parsing "begin" and "end" attributes
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.SSS");

    /**
     * Checks for the conformance with criteria on the caption duration. The
     * strings of "begin" and "end" attributes are parsed using dateFormat
     * ("HH:mm:ss.SSS").
     *
     * @param content
     * @param locator
     * @param context
     * @see MINIMAL_DURATION
     * @see MAXIMAL_DURATION
     * @return
     */
    private boolean verifyDuration(Object content, Locator locator, VerifierContext context) {
        String begin, end;
        if (content instanceof Paragraph) {
            Paragraph p = (Paragraph) content;
            begin = p.getBegin();
            end = p.getEnd();
        } else {
            Span s = (Span) content;
            begin = s.getBegin();
            end = s.getEnd();
        }

        Reporter reporter = context.getReporter();
        if (begin == null && end == null) {
            return true;
        } else if (begin != null && end != null) {
            Date beginDate, endDate;
            try {
                beginDate = dateFormat.parse(begin);
                endDate = dateFormat.parse(end);
            } catch (ParseException ex) {
                reporter.logError(reporter.message(locator, "*KEY*",
                        "Cannot parse timing attributes on {0}.",
                        context.getBindingElementName(content)));
                return false;
            }
            Long duration_ms = endDate.getTime() - beginDate.getTime();
            if (duration_ms == 0) {
                reporter.logError(
                        reporter.message(locator, "*KEY*",
                                "Criteria for caption duration not met (begin equals end) for {0}.",
                                context.getBindingElementName(content)));
            } else if (duration_ms < 0) {
                reporter.logError(
                        reporter.message(locator, "*KEY*",
                                "Criteria for caption duration not met (negative duration) for {0}.",
                                context.getBindingElementName(content)));
            } else if (duration_ms < TimeUnit.MILLISECONDS.convert(Limits.CAPTION_DURATION_MIN, Limits.CAPTION_DURATION_UNIT)
                    || duration_ms > TimeUnit.MILLISECONDS.convert(Limits.CAPTION_DURATION_MAX, Limits.CAPTION_DURATION_UNIT)) {
                reporter.logWarning(
                        reporter.message(locator, "*KEY*",
                                "Criteria for caption duration not met (not inside limits) for {0}.",
                                context.getBindingElementName(content)));
                return true;
            }
            return true;
        } else if (begin == null && end != null) {
            reporter.logError(reporter.message(locator, "*KEY*",
                    "Attribute 'begin' is missing on {0}.",
                    context.getBindingElementName(content)));
            return false;
        } else {
            reporter.logError(reporter.message(locator, "*KEY*",
                    "Attribute 'end' is missing on {0}.",
                    context.getBindingElementName(content)));
            return false;
        }
    }
}
