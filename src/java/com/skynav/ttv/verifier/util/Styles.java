/*
 * Copyright 2013 Skynav, Inc. All rights reserved.
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

import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.xml.sax.Locator;

import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.xml.helpers.Nodes;

public class Styles {

    public static boolean isStyleReference(Node node, Object value, Locator locator, VerifierContext context, Class<?> targetClass, Set<QName> ancestorNames) {
        if (!targetClass.isInstance(value))
            return false;
        if (!isStylingDescendant(node, value, ancestorNames))
            return false;
        if (hasStylingChainLoop(node))
            return false;
        return true;
    }

    public static void badStyleReference(Node node, Object value, Locator locator, VerifierContext context, QName referencingAttribute,
        QName targetName, Class<?> targetClass, Set<QName> ancestorNames) {
        if (!targetClass.isInstance(value))
            IdReferences.badReference(value, locator, context, referencingAttribute, targetName);
        if (!isStylingDescendant(node, value, ancestorNames))
            badStylingDescendant(node, value, locator, context, referencingAttribute, targetName, ancestorNames);
        if (hasStylingChainLoop(node))
            badStylingChainLoop(node, value, locator, context);
    }

    private static boolean isStylingDescendant(Node node, Object value, Set<QName> ancestorNames) {
        return Nodes.hasAncestor(node, ancestorNames);
    }

    private static boolean hasStylingChainLoop(Node node) {
        assert node instanceof Element;
        return hasStylingChainLoop((Element) node, new java.util.HashSet<String>());
    }

    private static boolean hasStylingChainLoop(Element elt, Set<String> encounteredStyles) {
        Document document = elt.getOwnerDocument();
        String style = elt.getAttributeNS(null, "style");
        if (style.length() != 0) {
            String[] styleRefs = style.split("\\s+");
            for (String styleRef : styleRefs) {
                assert document.getElementById(styleRef) != null;
                if (encounteredStyles.contains(styleRef))
                    return true;
                else
                    encounteredStyles.add(styleRef);
                if (hasStylingChainLoop(document.getElementById(styleRef), encounteredStyles))
                    return true;
            }
        }
        return false;
    }

    private static void badStylingDescendant(Node node, Object value, Locator locator, VerifierContext context, QName referencingAttribute,
        QName targetName, Set<QName> ancestorNames) {
        context.getReporter().logInfo(locator,
            "Bad IDREF '" + IdReferences.getId(value) + "', must reference " + targetName + " that is a descendant of any of " + ancestorNames + ".");
    }

    private static void badStylingChainLoop(Node node, Object value, Locator locator, VerifierContext context) {
        context.getReporter().logInfo(locator,
            "Loop in styling chain from IDREF '" + IdReferences.getId(value) + "'.");
    }

}
