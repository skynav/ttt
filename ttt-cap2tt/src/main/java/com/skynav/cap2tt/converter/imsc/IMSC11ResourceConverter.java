/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter.imsc;

import java.util.List;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import com.skynav.cap2tt.converter.AbstractResourceConverter;
import com.skynav.cap2tt.converter.ConverterContext;
import com.skynav.cap2tt.converter.Screen;

import com.skynav.ttv.model.ttml2.tt.Body;
import com.skynav.ttv.model.ttml2.tt.Head;
import com.skynav.ttv.model.ttml2.tt.ObjectFactory;
import com.skynav.ttv.model.ttml2.tt.TimedText;
import com.skynav.ttv.util.Reporter;

import static com.skynav.cap2tt.app.Converter.*;
import static com.skynav.ttv.model.imsc.IMSC11.Constants.*;

public class IMSC11ResourceConverter extends AbstractResourceConverter {

    public IMSC11ResourceConverter(ConverterContext context) {
        super(context);
    }

    protected static final ObjectFactory ttmlFactory = new ObjectFactory();

    @Override
    public boolean convert(List<Screen> screens) {
        boolean fail = !super.convert(screens);
        Reporter reporter = context.getReporter();
        if (!fail) {
            try {
                //  convert screens to a div of paragraphs
                IMSC11ResourceConverterState state = new IMSC11ResourceConverterState(this, ttmlFactory);
                state.process(screens);
                // populate body, extracting division from state object, must be performed prior to populating head
                Body body = ttmlFactory.createBody();
                state.populate(body, defaultRegion);
                // populate head
                Head head = ttmlFactory.createHead();
                state.populate(head);
                // populate root (tt)
                TimedText tt = ttmlFactory.createTimedText();
                tt.getOtherAttributes().put(ttvaModelAttrName, model.getName());
                if (defaultLanguage != null)
                    tt.setLang(defaultLanguage);
                if ((head.getStyling() != null) || (head.getLayout() != null))
                    tt.setHead(head);
                if (!body.getDivOrEmbeddedClass().isEmpty())
                    tt.setBody(body);
                // marshal and serialize
                if (!convertResource(ttmlFactory.createTt(tt)))
                    fail = true;
            } catch (Exception e) {
                reporter.logError(e);
            }
        }
        return !fail && (reporter.getResourceErrors() == 0);
    }

    protected boolean isMergedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        if ((ns != null) && ns.equals(NAMESPACE_EBUTT_STYLING))
            return true;
        else
            return super.isMergedStyle(a);
    }

    protected boolean isInitialStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        if ((ns != null) && ns.equals(NAMESPACE_EBUTT_STYLING))
            return true;
        else
            return super.isInitialStyle(a);
    }

    protected boolean isInlinedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        if ((ns != null) && ns.equals(NAMESPACE_EBUTT_STYLING))
            return true;
        else
            return super.isInlinedStyle(a);
    }

    protected boolean isPrunedStyle(Attr a) {
        assert a != null;
        String ns = a.getNamespaceURI();
        if ((ns != null) && ns.equals(NAMESPACE_EBUTT_STYLING))
            return true;
        else
            return super.isPrunedStyle(a);
    }

}
