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

//import java.text.AttributedCharacterIterator;
//import java.text.AttributedCharacterIterator.Attribute;
//import java.text.AttributedString;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.xml.helpers.Documents;

public class ParagraphCollector {

    public static final String NAMESPACE_TT                     = TTML1.Constants.NAMESPACE_TT;

    public static char  UC_LINE_SEPARATOR                       = '\u2028';
    public static char  UC_PARA_SEPARATOR                       = '\u2029';

    public static QName ttSpanElementName                       = new QName(NAMESPACE_TT, "span");
    public static QName ttBreakElementName                      = new QName(NAMESPACE_TT, "br");

    private List<Paragraph> paragraphs;
    private StringBuffer text;

    public ParagraphCollector() {
    }

    public List<Paragraph> collect(Element e) {
        return collectParagraph(e);
    }

    private List<Paragraph> collectParagraph(Element e) {
        reset();
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                String t = ((Text) n).getWholeText();
                for (int i = t.indexOf(UC_PARA_SEPARATOR); (t != null) && (i >= 0);) {
                    text.append(t.substring(0, i));
                    emit();
                    if ((i + 1) < t.length())
                        t = t.substring(i + 1);
                    else
                        t = null;
                }
                if (t != null)
                    text.append(t);
            } else if (n instanceof Element) {
                if (Documents.isElement((Element) n, ttSpanElementName))
                    collectSpan((Element) n);
                else if (Documents.isElement((Element) n, ttBreakElementName))
                    collectBreak((Element) n);
            }
        }
        emit();
        return extract();
    }

    private void reset() {
        this.paragraphs = new java.util.ArrayList<Paragraph>();
        this.text = new StringBuffer();
    }

    private void emit() {
        if (text.length() > 0) {
            paragraphs.add(new Paragraph(text.toString()));
        }
        text.setLength(0);
    }

    private List<Paragraph> extract() {
        List<Paragraph> paragraphs = this.paragraphs;
        this.paragraphs = null;
        return paragraphs;
    }

    private void collectSpan(Element e) {
        for (Node n = e.getFirstChild(); n != null; n = n.getNextSibling()) {
            if (n instanceof Text) {
                String t = ((Text) n).getWholeText();
                for (int i = t.indexOf(UC_PARA_SEPARATOR); (t != null) && (i >= 0);) {
                    text.append(t.substring(0, i));
                    emit();
                    if ((i + 1) < t.length())
                        t = t.substring(i + 1);
                    else
                        t = null;
                }
                if (t != null)
                    text.append(t);
            } else if (n instanceof Element) {
                if (Documents.isElement((Element) n, ttSpanElementName))
                    collectSpan((Element) n);
                else if (Documents.isElement((Element) n, ttBreakElementName))
                    collectBreak((Element) n);
            }
        }
    }

    private void collectBreak(Element e) {
        text.append(UC_LINE_SEPARATOR);
    }

}
