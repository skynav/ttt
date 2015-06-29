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

package com.skynav.xml.helpers;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class Nodes {

    private Nodes() {
    }

    /**
     * Determine if node has any one of a specified list of immediate ancestor names
     * ordered from most immediate to least immediate.
     * @param node
     * @param ancestors list of lists of immediate ancestor names
     * @return true if ancestors of node match any list of immediate ancestor names
     */
    public static boolean hasAncestors(Node node, List<List<QName>> ancestors) {
        for (List<QName> ancestorNames : ancestors) {
            if (hasImmediateAncestors(node, ancestorNames))
                return true;
        }
        return false;
    }

    /**
     * Determine if node has immediate ancestors in a list of ancestor names
     * ordered from most immediate to least immediate.
     * @param node
     * @param ancestorNames list of ancestor names in most to least immediate order
     * @return true if ancestors of node match specified list of ancestor names
     */
    public static boolean hasImmediateAncestors(Node node, List<QName> ancestorNames) {
        for (QName name : ancestorNames) {
            if (node == null)
                return false;
            else {
                Node parentNode = node.getParentNode();
                if (parentNode == null)
                    return false;
                else if (!matchesName(parentNode, name))
                    return false;
                else
                    node = parentNode;
            }
        }
        return true;
    }

    /**
     * Determine if node has an any ancestor in set of ancestor names.
     * @param node
     * @param ancestorNames set of possible ancestors
     * @return true if any ancestor node matches one of the specified set of ancestor names
     */
    public static boolean hasAncestor(Node node, Set<QName> ancestorNames) {
        for (QName ancestorName : ancestorNames) {
            if (hasAncestor(node, ancestorName))
                return true;
        }
        return false;
    }

    /**
     * Determine if node has an ancestor of specified name.
     * @param node
     * @param ancestorName ancestor name
     * @return true if any ancestor node matches the specified name
     */
    public static boolean hasAncestor(Node node, QName ancestorName) {
        while ((node != null) && (node instanceof Element)) {
            if (matchesName(node, ancestorName))
                return true;
            node = node.getParentNode();
        }
        return false;
    }

    /**
     * Determine of node's qualified name matches specified name.
     * @param name
     * @return true if node's qualified name matches specified name.
     */
    public static boolean matchesName(Node node, QName name) {
        QName nodeName = new QName(node.getNamespaceURI(), node.getLocalName());
        return nodeName.equals(name);
    }

}
