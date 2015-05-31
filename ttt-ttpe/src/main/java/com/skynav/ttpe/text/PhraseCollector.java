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
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.skynav.ttpe.style.StyleAttributeInterval;
import com.skynav.ttpe.style.StyleCollector;
import com.skynav.ttpe.util.Characters;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.text.Constants.*;

public class PhraseCollector {

    protected StyleCollector styleCollector;                    // style collector
    protected List<Phrase> phrases;                             // collected phrases
    protected StringBuffer text;                                // text of phrase being collected

    public PhraseCollector(StyleCollector styleCollector) {
        assert styleCollector != null;
        this.styleCollector = styleCollector;
    }

    public List<Phrase> collect(Element e) {
        return collectPhrases(e);
    }

    protected List<Phrase> collectPhrases(Element e) {
        clear();
        if (Documents.isElement(e, ttParagraphElementName))
            collectParagraph(e);
        else if (Documents.isElement(e, ttSpanElementName))
            collectSpan(e);
        emit(e);
        return extract();
    }

    protected void collectParagraph(Element e) {
        collectChildren(e);
        emit(e);
    }

    protected void collectSpan(Element e) {
        int begin = text.length();
        styleCollector.maybeWrapWithBidiControls(e);
        collectChildren(e);
        styleCollector.collectSpanStyles(e, begin, text.length());
        emit(e);
    }

    protected void collectChildren(Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                String t = ((Text) n).getWholeText();
                for (int i = findPhraseBreak(t); (t != null) && (i >= 0);) {
                    text.append(t.substring(0, i));
                    emit(e);
                    if ((i + 1) < t.length())
                        t = t.substring(i + 1);
                    else
                        t = null;
                }
                if (t != null)
                    text.append(t);
            } else if (n instanceof Element) {
                Element c = (Element) n;
                if (breakPhrase(c))
                    break;
                else if (Documents.isElement(c, ttSpanElementName))
                    collectSpan(c);
                else if (Documents.isElement(c, ttBreakElementName))
                    collectBreak(c);
            }
        }
    }

    protected int findPhraseBreak(String text) {
        return text.indexOf(Characters.UC_PARA_SEPARATOR);
    }

    protected boolean breakPhrase(Element e) {
        return false;
    }

    protected void collectBreak(Element e) {
        text.append((char) Characters.UC_LINE_SEPARATOR);
        emit(e);
    }

    protected void clear() {
        styleCollector.clear();
        this.phrases = null;
        this.text = new StringBuffer();
    }

    protected void emit(Element e) {
        if (text.length() > 0) {
            String content = text.toString();
            styleCollector.collectContentStyles(content, 0, content.length());
            add(newPhrase(e, content, styleCollector.extract()));
        }
        text.setLength(0);
    }

    protected Phrase newPhrase(Element e, String text, List<StyleAttributeInterval> attributes) {
        return new Phrase(e, text, attributes);
    }

    protected void add(Phrase p) {
        if (phrases == null)
            phrases = new java.util.ArrayList<Phrase>();
        phrases.add(p);
    }

    protected List<Phrase> extract() {
        List<Phrase> phrases = this.phrases;
        this.phrases = null;
        if (phrases == null)
            phrases = new java.util.ArrayList<Phrase>();
        return phrases;
    }

}
