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

package com.skynav.ttv.model;

import java.net.URI;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.MetadataVerifier;
import com.skynav.ttv.verifier.ParameterVerifier;
import com.skynav.ttv.verifier.ProfileVerifier;
import com.skynav.ttv.verifier.SemanticsVerifier;
import com.skynav.ttv.verifier.StyleVerifier;
import com.skynav.ttv.verifier.TimingVerifier;

public class AbstractModel implements Model {

    public String getName() {
        throw new UnsupportedOperationException();
    }

    public int getTTMLVersion() {
        throw new UnsupportedOperationException();
    }

    public boolean isTTMLVersion(int version) {
        throw new UnsupportedOperationException();
    }

    public String[] getTTSchemaResourceNames() {
        throw new UnsupportedOperationException();
    }

    public String[] getSchemaResourceNames() {
        throw new UnsupportedOperationException();
    }

    public URI[] getTTNamespaceURIs() {
        throw new UnsupportedOperationException();
    }

    public URI[] getNamespaceURIs() {
        throw new UnsupportedOperationException();
    }

    public boolean isNamespace(String nsUri) {
        for (URI uri : getNamespaceURIs()) {
            if (uri.toString().equals(nsUri))
                return true;
        }
        return false;
    }

    public boolean isGlobalAttribute(QName name) {
        return false;
    }

    public boolean isGlobalAttributePermitted(QName attributeName, QName elementName) {
        return false;
    }

    public boolean isElement(QName name) {
        return false;
    }

    public URI getTTProfileNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public URI getProfileNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public URI getTTFeatureNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public URI getFeatureNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public URI getTTExtensionNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public URI getExtensionNamespaceUri() {
        throw new UnsupportedOperationException();
    }

    public Map<String,String> getNormalizedPrefixes() {
        throw new UnsupportedOperationException();
    }

    public Set<URI> getProfileDesignators() {
        throw new UnsupportedOperationException();
    }

    public Profile.Specification getProfileSpecification(URI uri) {
        throw new UnsupportedOperationException();
    }

    public Profile.StandardDesignations getStandardDesignations() {
        throw new UnsupportedOperationException();
    }

    public boolean isStandardFeatureDesignation(URI uri) {
        return false;
    }

    public boolean isStandardExtensionDesignation(URI uri) {
        return false;
    }

    public String getJAXBContextPath() {
        throw new UnsupportedOperationException();
    }

    public List<QName> getIdAttributes() {
        throw new UnsupportedOperationException();
    }

    public Map<Class<?>,String> getRootClasses() {
        throw new UnsupportedOperationException();
    }

    public QName getIdReferenceTargetName(QName attributeName) {
        throw new UnsupportedOperationException();
    }

    public Class<?> getIdReferenceTargetClass(QName attributeName) {
        throw new UnsupportedOperationException();
    }

    public List<List<QName>> getIdReferencePermissibleAncestors(QName attributeName) {
        return null;
    }

    public List<List<QName>> getElementPermissibleAncestors(QName attributeName) {
        return null;
    }

    public Collection<QName> getDefinedStyleNames() {
        return new java.util.ArrayList<QName>();
    }

    public Collection<QName> getApplicableStyleNames(QName eltName) {
        return new java.util.ArrayList<QName>();
    }

    public boolean isInheritableStyle(QName eltName, QName styleName) {
        return false;
    }

    public String getInitialStyleValue(QName eltName, QName styleName) {
        return null;
    }

    public boolean doesStyleApply(QName eltName, QName styleName) {
        return false;
    }

    public boolean isNegativeLengthPermitted(QName eltName, QName styleName) {
        return true;
    }

    public SemanticsVerifier getSemanticsVerifier() {
        throw new UnsupportedOperationException();
    }

    public ParameterVerifier getParameterVerifier() {
        throw new UnsupportedOperationException();
    }

    public ProfileVerifier getProfileVerifier() {
        throw new UnsupportedOperationException();
    }

    public StyleVerifier getStyleVerifier() {
        throw new UnsupportedOperationException();
    }

    public TimingVerifier getTimingVerifier() {
        throw new UnsupportedOperationException();
    }

    public MetadataVerifier getMetadataVerifier() {
        throw new UnsupportedOperationException();
    }

    public void configureReporter(Reporter reporter) {
    }

    public String makeResourceStateName(String name) {
        StringBuffer sb = new StringBuffer();
        sb.append(getName());
        sb.append('.');
        sb.append(name);
        return sb.toString();
    }

    public void initializeResourceState(URI uri, Map<String,Object> state) {
    }

}
