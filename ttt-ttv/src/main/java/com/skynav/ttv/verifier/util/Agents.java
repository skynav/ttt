/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.xml.helpers.Nodes;

public class Agents {

    public static boolean isAgentReference(Node node, Object value, Location location, VerifierContext context, Class<?> targetClass, List<List<QName>> ancestors) {
        if (!targetClass.isInstance(value))
            return false;
        if (!isSignificantAgent(node, value, ancestors))
            return false;
        return true;
    }

    public static void badAgentReference(Node node, Object value, Location location, VerifierContext context, QName referencingAttribute,
        QName targetName, Class<?> targetClass, List<List<QName>> ancestors) {
        if (!targetClass.isInstance(value))
            IdReferences.badReference(value, location, context, referencingAttribute, targetName);
        else if (!isSignificantAgent(node, value, ancestors))
            badAgentSignificance(node, value, location, context, referencingAttribute, targetName, ancestors);
    }

    private static boolean isSignificantAgent(Node node, Object value, List<List<QName>> ancestors) {
        return Nodes.hasAncestors(node, ancestors);
    }

    private static void badAgentSignificance(Node node, Object value, Location location, VerifierContext context, QName referencingAttribute,
        QName targetName, List<List<QName>> ancestors) {
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(location.getLocator(), "*KEY*",
            "Bad IDREF ''{0}'', must reference significant ''{1}'' which ancestors are one of {2}.",
            IdReferences.getId(value), targetName, ancestors));
    }

}
