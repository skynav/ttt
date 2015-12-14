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

package com.skynav.ttv.verifier.ttml.metadata;

import java.util.List;
import java.util.Set;

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.MetadataValueVerifier;
import com.skynav.ttv.verifier.VerifierContext;
import com.skynav.ttv.verifier.util.Agents;
import com.skynav.ttv.verifier.util.IdReferences;

public class AgentAttributeVerifier implements MetadataValueVerifier {

    public boolean verify(Object value, Location location, VerifierContext context) {
        boolean failed = false;
        Model model = context.getModel();
        QName name = location.getAttributeName();
        QName targetName = model.getIdReferenceTargetName(name);
        Class<?> targetClass = model.getIdReferenceTargetClass(name);
        List<List<QName>> ancestors = model.getIdReferencePermissibleAncestors(name);
        assert value instanceof List<?>;
        List<?> agents = (List<?>) value;
        if (agents.size() > 0) {
            Set<String> agentIdentifiers = new java.util.HashSet<String>();
            for (Object agent : agents) {
                Node node = context.getXMLNode(agent);
                if (!Agents.isAgentReference(node, agent, location, context, targetClass, ancestors)) {
                    Agents.badAgentReference(node, agent, location, context, name, targetName, targetClass, ancestors);
                    failed = true;
                }
                String id = IdReferences.getId(agent);
                if (agentIdentifiers.contains(id)) {
                    Reporter reporter = context.getReporter();
                    if (reporter.isWarningEnabled("duplicate-idref-in-agent")) {
                        if (reporter.logWarning(reporter.message(location.getLocator(),
                            "*KEY*", "Duplicate IDREF ''{0}'' in ''{1}''.", IdReferences.getId(agent), name))) {
                            failed = true;
                        }
                    }
                } else
                    agentIdentifiers.add(id);
            }
        }
        return !failed;
    }

}
