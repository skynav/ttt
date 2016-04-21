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

package com.skynav.ttv.verifier.util;

import java.net.URI;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.Locator;

import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.model.value.Image;
import com.skynav.ttv.model.value.impl.EmbeddedImageImpl;
import com.skynav.ttv.model.value.impl.ExternalImageImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.URIs;
import com.skynav.ttv.verifier.VerifierContext;

public class Images {

    public static boolean isImage(String value, Location location, VerifierContext context, Image[] outputImage) {
        if (URIs.isLocalFragment(value)) {
            String id = URIs.getFragment(value);
            assert id != null;
            Object content = location.getContent();
            Node node = content != null ? context.getXMLNode(content) : null;
            if ((node == null) && (content instanceof Element))
                node = (Element) content;
            if (node != null) {
                Document document = node.getOwnerDocument();
                if (document != null) {
                    Element targetElement = document.getElementById(id);
                    if (targetElement != null) {
                        Object target = context.getBindingElement(targetElement);
                        if (target != null) {
                            QName targetName = context.getBindingElementName(target);
                            if (!isImageElement(targetName))
                                return false;                                   // target must be an image element
                            else {
                                URI base = location.getSystemIdAsURI();
                                URI uri = URIs.makeURISafely("#" + id, base);
                                return internalImage(uri, target, outputImage); // good local reference
                            }
                        } else
                            throw new IllegalStateException();                  // no binding for target element (illegal state)
                    } else
                        return false;                                           // no target element for given fragment identifier
                } else
                    throw new IllegalStateException();                          // no owner document (illegal state)
            } else
                throw new IllegalStateException();                              // no node (illegal state)
        } else if (value.trim().length() == value.length()) {
            URI base = location.getSystemIdAsURI();
            URI uri = URIs.makeURISafely(value, base);
            if (uri == null)
                return false;                                                   // bad syntax
            else
                return externalImage(uri, outputImage);                         // good non-local reference (though not necessarily resolvable)
        } else if (value.length() > 0) {
            return false;                                                       // image reference is all LWSP or has LWSP padding
        } else {
            return false;                                                       // image reference is empty string
        }
    }

    private static boolean internalImage(URI uri, Object target, Image[] outputImage) {
        if (outputImage != null) {
            if (outputImage.length < 1)
                throw new IllegalArgumentException();
            else
                outputImage[0] = new EmbeddedImageImpl(uri, target);
        }
        return true;
    }

    private static boolean externalImage(URI uri, Image[] outputImage) {
        if (outputImage != null) {
            if (outputImage.length < 1)
                throw new IllegalArgumentException();
            else
                outputImage[0] = new ExternalImageImpl(uri);
        }
        return true;
    }

    public static void badImage(String value, Location location, VerifierContext context) {

        Reporter reporter = context.getReporter();
        Message m = null;
        Locator locator = location.getLocator();

        if (URIs.isLocalFragment(value)) {
            String id = URIs.getFragment(value);
            assert id != null;
            Object content = location.getContent();
            Node node = content != null ? context.getXMLNode(content) : null;
            if ((node == null) && (content instanceof Element))
                node = (Element) content;
            if (node != null) {
                Document document = node.getOwnerDocument();
                if (document != null) {
                    Element targetElement = document.getElementById(id);
                    if (targetElement != null) {
                        Object target = context.getBindingElement(targetElement);
                        if (target != null) {
                            QName targetName = context.getBindingElementName(target);
                            if (!isImageElement(targetName)) {                  // target must be an image element
                                m = reporter.message(locator, "*KEY*",
                                    "Bad <image> expression, got ''{0}'', target must be an image element.", value);
                            }
                        }
                    } else {                                                    // no target element for given fragment identifier
                        m = reporter.message(locator, "*KEY*",
                            "Bad <image> expression, got ''{0}'', no target element for given fragment identifier.", value);
                    }
                }
            }
        } else if (value.trim().length() == value.length()) {
            URI base = location.getSystemIdAsURI();
            URI uri = URIs.makeURISafely(value, base);
            if (uri == null) {                                                  // bad syntax
                m = reporter.message(locator, "*KEY*",
                    "Bad <image> expression, got ''{0}'', illegal syntax.", value);
            }
        } else if (value.length() > 0) {                                        // image reference is all LWSP or has LWSP padding
            m = reporter.message(locator, "*KEY*",
                "Bad <image> expression, got ''{0}'', all LWSP or has LWSP padding.", value);
        } else {                                                                // image reference is empty string
            m = reporter.message(locator, "*KEY*",
                "Bad <image> expression, got ''{0}'', empty string.", value);
        }
        if (m != null)
            reporter.logError(m);
    }

    private static final QName imageElementName = new QName(TTML.Constants.NAMESPACE_TT, "image");
    private static boolean isImageElement(QName name) {
        return name.equals(imageElementName);
    }

}
