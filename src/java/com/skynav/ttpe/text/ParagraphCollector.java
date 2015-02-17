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

import com.skynav.ttpe.style.StyleCollector;
import com.skynav.ttpe.util.Characters;
import com.skynav.xml.helpers.Documents;

import static com.skynav.ttpe.text.Constants.*;

public class ParagraphCollector {

    private StyleCollector styleCollector;                      // style collector
    private List<Paragraph> paragraphs;                         // collected paragraphs
    private StringBuffer text;                                  // text content for paragraph being collected

    public ParagraphCollector(StyleCollector styleCollector) {
        assert styleCollector != null;
        this.styleCollector = styleCollector;
    }

    public List<Paragraph> collect(Element e) {
        return collectParagraph(e);
    }

    private List<Paragraph> collectParagraph(Element e) {
        clear();
        styleCollector.collectParagraphStyles(e);
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                String t = ((Text) n).getWholeText();
                for (int i = t.indexOf(Characters.UC_PARA_SEPARATOR); (t != null) && (i >= 0);) {
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
                if (Documents.isElement(c, ttSpanElementName))
                    collectSpan(c);
                else if (Documents.isElement(c, ttBreakElementName))
                    collectBreak(c);
            }
        }
        emit(e);
        return extract();
    }

    private void clear() {
        styleCollector.clear();
        this.paragraphs = null;
        this.text = new StringBuffer();
    }

    private void emit(Element e) {
        if (text.length() > 0) {
            if (paragraphs == null)
                paragraphs = new java.util.ArrayList<Paragraph>();
            paragraphs.add(new Paragraph(e, text.toString(), styleCollector.extract()));
        }
        text.setLength(0);
    }

    private List<Paragraph> extract() {
        List<Paragraph> paragraphs = this.paragraphs;
        this.paragraphs = null;
        if (paragraphs == null)
            paragraphs = new java.util.ArrayList<Paragraph>();
        return paragraphs;
    }

    private void collectSpan(Element e) {
        if (styleCollector.generatesInlineBlock(e)) {
            collectSpanAsParagraph(e);
        } else {
            int begin = text.length();
            for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Text) {
                    String t = ((Text) n).getWholeText();
                    for (int i = t.indexOf(Characters.UC_PARA_SEPARATOR); (t != null) && (i >= 0);) {
                        text.append(t.substring(0, i));
                        emit(getParagraph(e));
                        if ((i + 1) < t.length())
                            t = t.substring(i + 1);
                        else
                            t = null;
                    }
                    if (t != null)
                        text.append(t);
                } else if (n instanceof Element) {
                    Element c = (Element) n;
                    if (Documents.isElement(c, ttSpanElementName))
                        collectSpan(c);
                    else if (Documents.isElement(c, ttBreakElementName))
                        collectBreak(c);
                }
            }
            styleCollector.collectSpanStyles(e, begin, text.length());
        }
    }

    private Element getParagraph(Element e) {
        while (e != null) {
            if (Documents.isElement(e, ttParagraphElementName))
                return e;
            else
                e = (Element) e.getParentNode();
        }
        return null;
    }

    private void collectSpanAsParagraph(Element e) {
        for (Paragraph p : new ParagraphCollector(new StyleCollector(styleCollector)).collect(e)) {
            int begin = text.length();
            text.append((char) Characters.UC_OBJECT);
            styleCollector.addEmbedding(p, begin, text.length());
        }
    }

    private void collectBreak(Element e) {
        text.append((char) Characters.UC_LINE_SEPARATOR);
    }

}
