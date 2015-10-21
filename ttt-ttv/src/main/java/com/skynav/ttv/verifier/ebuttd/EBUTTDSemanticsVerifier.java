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
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.ttml.TTML1SemanticsVerifier;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBElement;
import org.w3c.dom.Attr;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

/**
 *
 * @author msamek
 */
public class EBUTTDSemanticsVerifier extends TTML1SemanticsVerifier {

    public static class Limits {

        public static final int LINE_LENGTH_MIN = 1;
        public static final int LINE_LENGTH_MAX = 42;
        public static final int CAPTION_DURATION_MIN = 1;
        public static final int CAPTION_DURATION_MAX = 7;
        public static final TimeUnit CAPTION_DURATION_UNIT = TimeUnit.SECONDS;
    }

    public EBUTTDSemanticsVerifier(Model model) {
        super(model);
    }

    @Override
    protected boolean verifyParagraph(Object paragraph) {
        if (!verifyLengthOfLine(paragraph)) {
            return false;
        }
        return super.verifyParagraph(paragraph);
    }

    private boolean verifyLengthOfLine(Object element) {
        assert element instanceof Span || element instanceof Paragraph;
        Reporter reporter = getContext().getReporter();

        StringBuilder strings = new StringBuilder();
        getStringContent(element, strings);
        String stringContent = strings.toString();

        if (stringContent.trim().isEmpty()) {
            reporter.logWarning(
                    reporter.message(getLocator(element), "*KEY*",
                            "No textual payload for element {0}.", getContext().getBindingElementName(element)));
            return true;
        }

        String[] lines = stringContent.split("\n");
        for (String line : lines) {
            int len = line.trim().length();
            if (len < Limits.LINE_LENGTH_MIN || len > Limits.LINE_LENGTH_MAX) {
                reporter.logWarning(
                        reporter.message(getLocator(element), "*KEY*",
                                "Criteria for line length not met for {0}.", getContext().getBindingElementName(element)));
            }
        }

        return true;
    }

    private void getStringContent(Object object, StringBuilder strings) {
        List<Serializable> content;
        if (object instanceof JAXBElement) {
            JAXBElement jaxbElement = (JAXBElement) object;
            object = jaxbElement.getValue();
        }

        if (object instanceof String) {
            String str = (String) object;
            strings.append(str);
            return;
        } else if (object instanceof Break) {
            strings.append('\n');
            return;
        } else if (object instanceof Paragraph) {
            Paragraph par = (Paragraph) object;
            content = par.getContent();
        } else if (object instanceof Span) {
            Span span = (Span) object;
            content = span.getContent();
        } else {
            return;
        }

        for (Serializable s : content) {
            getStringContent(s, strings);
        }
    }

    @Override
    public boolean verifyNonTTOtherAttributes(Object content, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        
        Node element = context.getXMLNode(content);
        String elementNs = element.getNamespaceURI();
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0; i < attributes.getLength(); ++i) {
            Attr attrib = (Attr) attributes.item(i);
            String namespace = attrib.getNamespaceURI();
            if (namespace == null || namespace.isEmpty()) namespace = elementNs;
            if (!getModel().isNamespace(namespace)) {
                reporter.logWarning(reporter.message(locator, "*KEY*", "Unknown attribute found {1} on {0}", content, attrib.getName()));
            }
        }
        
        return true;
    }

    @Override
    public boolean verifyNonTTOtherElement(Object content, Locator locator, VerifierContext context) {
        Reporter reporter = context.getReporter();
        Node element = context.getXMLNode(content);
        String elementNs = element.getNamespaceURI();
        if (elementNs == null || !getModel().isNamespace(elementNs))
            reporter.logWarning(reporter.message(locator, "*KEY*", "Unknown element found: {0}", content));
        return true;
    }
    
    
}
