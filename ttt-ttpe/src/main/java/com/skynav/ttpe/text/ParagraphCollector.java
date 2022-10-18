/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.text;

import java.util.List;

import org.w3c.dom.Element;

import com.skynav.ttpe.style.AnnotationStyleCollector;
import com.skynav.ttpe.style.StyleCollector;

public class ParagraphCollector {

    private StyleCollector styleCollector;                      // style collector
    private boolean embedding;                                  // true if collecting an embedded paragraph, i.e., a span that generates an inline block
    private List<Paragraph> paragraphs;                         // collected paragraphs
    private List<Phrase> phrases;                               // phrases of paragraph being collected

    public ParagraphCollector(StyleCollector styleCollector) {
        this(styleCollector, false);
    }

    public ParagraphCollector(StyleCollector styleCollector, boolean embedding) {
        assert styleCollector != null;
        this.styleCollector = styleCollector;
        this.embedding = embedding;
    }

    public List<Paragraph> collect(Element e) {
        return collectParagraph(e);
    }

    private List<Paragraph> collectParagraph(Element e) {
        clear();
        styleCollector.maybeWrapWithBidiControls(e);
        styleCollector.collectParagraphStyles(e);
        for (Phrase p : new ParagraphPhraseCollector(new StyleCollector(styleCollector), embedding ? e : null).collect(e)) {
            if ((p instanceof BreakPhrase) && (((BreakPhrase) p).isParagraphBreak()))
                emit(e);
            else
                phrases.add(p);
        }
        emit(e);
        return extract();
    }

    private void clear() {
        styleCollector.clear();
        paragraphs = null;
        if (phrases == null)
            phrases = new java.util.ArrayList<Phrase>();
        else
            phrases.clear();
    }

    private void emit(Element e) {
        if (phrases.size() > 0) {
            if (paragraphs == null)
                paragraphs = new java.util.ArrayList<Paragraph>();
            paragraphs.add(new Paragraph(e, phrases, styleCollector.extract()));
        }
        phrases.clear();
    }

    private List<Paragraph> extract() {
        List<Paragraph> paragraphs = this.paragraphs;
        this.paragraphs = null;
        if (paragraphs == null)
            paragraphs = new java.util.ArrayList<Paragraph>();
        return paragraphs;
    }

    private static class ParagraphPhraseCollector extends PhraseCollector {

        private Element outer;

        public ParagraphPhraseCollector(StyleCollector styleCollector, Element outer) {
            super(styleCollector);
            this.outer = outer;
        }

        @Override
        protected void collectSpan(Element e) {
            if (styleCollector.generatesAnnotationBlock(e))
                collectAsAnnotation(e);
            else if ((outer == null) && styleCollector.generatesInlineBlock(e))
                collectAsParagraph(e);
            else if (e == outer)
                super.collectParagraph(e);
            else
                super.collectSpan(e);
        }

        private void collectAsAnnotation(Element e) {
            new AnnotatedPhraseCollector(new AnnotationStyleCollector(styleCollector, null), this).collectToDestination(e);
        }

        private void collectAsParagraph(Element e) {
            for (Paragraph p : new ParagraphCollector(new StyleCollector(styleCollector), true).collect(e))
                add(new EmbeddingPhrase(e, p, null));
        }
    }

}
