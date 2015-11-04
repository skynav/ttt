/*
 * Copyright (c) 2015, Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 * Copyright 2015 Skynav, Inc.
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

package com.skynav.ttv.verifier.ebuttd;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.JAXBIntrospector;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml1.tt.Paragraph;
import com.skynav.ttv.model.ttml1.tt.Span;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1TimingVerifier;

/**
 *
 * @author Michal Samek, (Michal.Samek at at.redbullmediahouse.com)
 */
public class EBUTTDTimingVerifier extends TTML1TimingVerifier {

    public EBUTTDTimingVerifier(Model model) {
        super(model);
    }

    @Override
    public boolean verify(Object content, Locator locator, VerifierContext context, ItemType type) {
        if (type == ItemType.Attributes) {
            boolean failed = false;
            if (content instanceof Paragraph) {
                if (!verifyTimingSpecs((Paragraph) content, locator, context)) {
                    failed = true;
                }
            }
            return !failed && verifyAttributeItems(content, locator, context);
        } else {
            throw new IllegalArgumentException();
        }
    }

    protected boolean verifyTimingSpecs(Paragraph paragraph, Locator locator, VerifierContext context) {
        if (paragraph.getBegin() != null && !paragraph.getBegin().isEmpty()) {
            return verifyChildrenNotTimed(paragraph.getContent(), locator, context);
        } else {
            return verifyChildrenAreTimed(paragraph.getContent(), locator, context);
        }
    }

    /**
     * Verify all elements in the passed list are timed - have a timing information associated.
     *
     * @param children - list of elements to be checked
     * @param locator
     * @param context
     * @return
     */
    private boolean verifyChildrenAreTimed(List<Serializable> children, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        for (Serializable child : children) {
            if (JAXBIntrospector.getValue(child) instanceof Span) {
                Span span = (Span) JAXBIntrospector.getValue(child);
                if (span.getBegin() == null || span.getBegin().isEmpty()) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Timing information is missing for {0}", span));
                    failed = true;
                }

                if (!verifyChildrenNotTimed(span.getContent(), locator, context)) {
                    failed = true;
                }
            }

            if (child instanceof String) {
                reporter.logError(reporter.message(locator, "*KEY*", "Timing information is missing for content (\"{0}\")", child));
                failed = true;
            }
        }

        return !failed;
    }

    /**
     * Verify all elements in the passed list are not timed - don't have a timing information associated.
     *
     * @param children
     * @param locator
     * @param context
     * @return
     */
    private boolean verifyChildrenNotTimed(List<Serializable> children, Locator locator, VerifierContext context) {
        boolean failed = false;
        Reporter reporter = context.getReporter();
        for (Serializable child : children) {
            if (JAXBIntrospector.getValue(child) instanceof Span) {
                Span span = (Span) JAXBIntrospector.getValue(child);
                if (span.getBegin() != null && !span.getBegin().isEmpty()) {
                    reporter.logError(reporter.message(locator, "*KEY*", "Timing information not permitted on {0}", span));
                    failed = true;
                }

                if (!verifyChildrenNotTimed(span.getContent(), locator, context)) {
                    failed = true;
                }
            }
        }

        return !failed;
    }
}
