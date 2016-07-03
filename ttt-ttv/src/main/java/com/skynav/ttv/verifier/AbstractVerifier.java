/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier;

import java.net.URI;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Locators;

public abstract class AbstractVerifier {

    private Model model;
    private VerifierContext context;

    protected AbstractVerifier(Model model) {
        this.model = model;
    }

    public Model getModel() {
        return model;
    }

    protected void setState(Object root, VerifierContext context) {
        this.context = context;
    }

    public VerifierContext getContext() {
        return context;
    }

    protected Location getLocation(Object content) {
        return getLocation(content, null);
    }

    protected Location getLocation(Object content, QName attrName) {
        return new Location(content, getContext().getBindingElementName(content), attrName, getLocator(content));
    }

    protected Locator getLocator(Object content) {
        return getLocator(content, getSysidDefault());
    }

    private String getSysidDefault() {
        Object uri = getContext().getResourceState("sysid");
        if (uri != null)
            return ((URI)uri).toString();
        else
            return null;
    }

    private Locator getLocator(Object content, String sysidDefault) {
        Locator locator = null;
        while (content != null) {
            if ((locator = Locators.getLocator(content, sysidDefault)) != null)
                break;
            else
                content = getLocatableParent(content);
        }
        return locator;
    }

    private Object getLocatableParent(Object content) {
        if (content instanceof Element) {
            Node n = ((Element) content).getParentNode();
            return (n instanceof Element) ? n : null;
        } else if (context != null) {
            if (content instanceof JAXBElement<?>) {
                return context.getBindingElementParent(((JAXBElement<?>)content).getValue());
            } else
                return context.getBindingElementParent(content);
        } else
            return null;
    }

}
