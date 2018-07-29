/*
 * Copyright 2013-2018 Skynav, Inc. All rights reserved.
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
import com.skynav.ttv.model.value.Audio;
import com.skynav.ttv.model.value.impl.EmbeddedAudioImpl;
import com.skynav.ttv.model.value.impl.ExternalAudioImpl;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Message;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.util.URIs;
import com.skynav.ttv.verifier.ItemVerifier.ItemType;
import com.skynav.ttv.verifier.VerifierContext;

public class Audios {

    public static boolean isAudio(String value, Location location, VerifierContext context, Audio[] outputAudio) {
        return isAudio(value, location, context, true, outputAudio);
    }

    public static boolean isAudio(String value, Location location, VerifierContext context, boolean verifyContent, Audio[] outputAudio) {
        Audio audio;
        if ((outputAudio != null) && (outputAudio.length < 1))
            throw new IllegalArgumentException();
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
                            if (!isAudioElement(targetName))
                                return false;                                   // target must be an audio element
                            else {
                                URI base = location.getSystemIdAsURI();
                                URI uri = URIs.makeURISafely("#" + id, base);
                                audio = internalAudio(uri, target);             // good local reference (though not necessarily resolvable)
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
                audio = externalAudio(uri);                                     // good non-local reference (though not necessarily resolvable)
        } else if (value.length() > 0) {
            return false;                                                       // audio reference is all LWSP or has LWSP padding
        } else {
            return false;                                                       // audio reference is empty string
        }
        // record specified type and format
        audio.setSpecifiedType(null);                                           // [TBD] populate specified type from content
        audio.setSpecifiedFormat(null);                                         // [TBD] populate speicifed format from content
        // verify audio content
        assert audio != null;
        if (verifyContent && !context.getModel().getAudioVerifier().verify(audio, location.getLocator(), context, ItemType.Other))
            return false;
        // return audio instance if requested
        if (outputAudio != null) {
            assert outputAudio.length > 0;
            outputAudio[0] = audio;
        }
        return true;
    }

    private static Audio internalAudio(URI uri, Object target) {
        return new EmbeddedAudioImpl(uri, target);
    }

    private static Audio externalAudio(URI uri) {
        return new ExternalAudioImpl(uri);
    }

    public static void badAudio(String value, Location location, VerifierContext context) {
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
                            if (!isAudioElement(targetName)) {                  // target must be an audio element
                                m = reporter.message(locator, "*KEY*",
                                    "Bad <audio> expression, got ''{0}'', target must be an audio element.", value);
                            }
                        }
                    } else {                                                    // no target element for given fragment identifier
                        m = reporter.message(locator, "*KEY*",
                            "Bad <audio> expression, got ''{0}'', no target element for given fragment identifier.", value);
                    }
                }
            }
        } else if (value.trim().length() == value.length()) {
            URI base = location.getSystemIdAsURI();
            URI uri = URIs.makeURISafely(value, base);
            if (uri == null) {                                                  // bad syntax
                m = reporter.message(locator, "*KEY*",
                    "Bad <audio> expression, got ''{0}'', illegal syntax.", value);
            }
        } else if (value.length() > 0) {                                        // audio reference is all LWSP or has LWSP padding
            m = reporter.message(locator, "*KEY*",
                "Bad <audio> expression, got ''{0}'', all LWSP or has LWSP padding.", value);
        } else {                                                                // audio reference is empty string
            m = reporter.message(locator, "*KEY*",
                "Bad <audio> expression, got ''{0}'', empty string.", value);
        }
        if (m != null)
            reporter.logError(m);
    }

    private static final QName audioElementName = new QName(TTML.Constants.NAMESPACE_TT, "audio");
    private static boolean isAudioElement(QName name) {
        return name.equals(audioElementName);
    }

}
