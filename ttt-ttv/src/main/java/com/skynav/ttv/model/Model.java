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

public interface Model {

    /**
     * Obtain model name.
     * @return model name
     */
    String getName();

    /**
     * Return TTML version number.
     */
    int getTTMLVersion();

    /**
     * Return true if this model is or is based upon the specified version of TTML.
     */
    boolean isTTMLVersion(int version);

    /**
     * Obtain TT schema resource names (paths), each of which should be
     * an acceptable argument to pass to ClassLoader.getResource().
     * @return schema resource names (paths)
     */
    String[] getTTSchemaResourceNames();

    /**
     * Obtain model's schema resource names (paths), each of which should be
     * an acceptable argument to pass to ClassLoader.getResource().
     * @return schema resource names (paths)
     */
    String[] getSchemaResourceNames();

    /**
     * Obtain TT schema (target) namespace URIs.
     * @return namespace URIs
     */
    URI[] getTTNamespaceURIs();

    /**
     * Obtain model's schema (target) namespace URIs.
     * @return namespace URIs
     */
    URI[] getNamespaceURIs();

    /**
     * Determine if a namespace URI matches one of this model's namespace URIs.
     * @return true if match
     */
    boolean isNamespace(String nsUri);

    /**
     * Determine if a name is bound to a global attribute type.
     * @param name of attribute
     * @return true if bound
     */
    boolean isGlobalAttribute(QName name);

    /**
     * Determine if a global attribute is permitted on element.
     * @param attributeName name of attribute
     * @param elementName name of attribute
     * @return true if bound
     */
    boolean isGlobalAttributePermitted(QName attributeName, QName elementName);

    /**
     * Determine if a name is bound to a element type.
     * @param name of element
     * @return true if bound
     */
    boolean isElement(QName name);

    /**
     * Obtain TT profile namespace URI.
     * @return TT profile namespace URI
     */
    URI getTTProfileNamespaceUri();

    /**
     * Obtain model's profile namespace URI.
     * @return profile namespace URI
     */
    URI getProfileNamespaceUri();

    /**
     * Obtain TT feature namespace URI.
     * @return feature namespace URI
     */
    URI getTTFeatureNamespaceUri();

    /**
     * Obtain model's feature namespace URI.
     * @return feature namespace URI
     */
    URI getFeatureNamespaceUri();

    /**
     * Obtain TT extension namespace URI.
     * @return extension namespace URI
     */
    URI getTTExtensionNamespaceUri();

    /**
     * Obtain model's extension namespace URI.
     * @return extension namespace URI
     */
    URI getExtensionNamespaceUri();

    /**
     * Obtain map of normalized namespace prefixes.
     * @return model's normalized prefixes
     */
    Map<String,String> getNormalizedPrefixes();

    /**
     * Obtain set of profiles designators.
     * @return model's profile designators
     */
    Set<URI> getProfileDesignators();

    /**
     * Obtain specifiction of a standard profile.
     * @param uri specifying profile designator
     * @return standard profile specification
     */
    Profile.Specification getProfileSpecification(URI uri);

    /**
     * Obtain model's standard designations.
     * @return standard designations
     */
    Profile.StandardDesignations getStandardDesignations();

    /**
     * Determine if feature designation is a standard designation.
     * @param uri specifying feature designation
     * @return true if uri is a standard feature designation
     */
    boolean isStandardFeatureDesignation(URI uri);

    /**
     * Determine if extension designation is a standard designation.
     * @param uri specifying extension designation
     * @return true if uri is a standard extension designation
     */
    boolean isStandardExtensionDesignation(URI uri);

    /**
     * Obtain JAXB context path that applies to model, which should
     * an acceptable argument to pass to JAXBContext.newInstance().
     * @return JAXB context path
     */
    String getJAXBContextPath();

    /**
     * Obtain ordered list of ID attribute qualified names. Elements
     * in document are assigned an ID based on the first match in this list.
     * @return list of ID attribute names
     */
    List<QName> getIdAttributes();

    /**
     * Obtain information about the root element content classes
     * for model, where this information takes the form of a Map
     * from content Class objects to method names, where the method
     * name is the method of the ObjectFactory associated with the
     * content class which is used to construct an instance of that
     * class.
     * @return map from content classes to method names
     */
    Map<Class<?>,String> getRootClasses();

    /**
     * Obtain qualified name of element type which IDREFs in the
     * specified attribute must reference.
     * @param attributeName name of referring attribute
     * @return qualified name or null if no constraint on target
     * element type
     */
    QName getIdReferenceTargetName(QName attributeName);

    /**
     * Obtain JAXB value class which IDREFs in the specified attribute
     * must reference.
     * @param attributeName name of referring attribute
     * @return JAXB value class or Object.class if no constraint on target
     * element type
     */
    Class<?> getIdReferenceTargetClass(QName attributeName);

    /**
     * Obtain list of lists of qualified names of permissible ancestors
     * of element type which IDREFs in the specified attribute must reference,
     * where the order of lists contained in the outer list is not significant, and the
     * order of qualified names contained in inner lists is from most immediate ancestor
     * to least immediate ancestor, where least immediate ancestor need not be the root.
     * @param attributeName name of referring attribute
     * @return a list of lists of qualified names or null if no constraint on target
     * ancestor element type
     */
    List<List<QName>> getIdReferencePermissibleAncestors(QName attributeName);

    /**
     * Obtain list of lists of qualified names of permissible ancestors
     * of specific element type, where the order of lists contained in the outer list
     * is not significant, and the order of qualified names contained in inner lists
     * is from most immediate ancestor to least immediate ancestor, where least immediate
     * ancestor need not be the root.
     * @param elementName name of element
     * @return a list of lists of qualified names or null if no constraint on target
     * ancestor element type
     */
    List<List<QName>> getElementPermissibleAncestors(QName elementName);

    /**
     * Obtain defined style names.
     * @return a collection of style names defined by this model.
     */
    Collection<QName> getDefinedStyleNames();

    /**
     * Obtain applicable style names for a specified named element type.
     * @param elementName name of element
     * @return a collection of style names that apply to named element
     */
    Collection<QName> getApplicableStyleNames(QName elementName);

    /**
     * Determine if named style is inheritable.
     * @param eltName name of element type
     * @param styleName name of style
     * @return true if named style is inheritable.
     */
    boolean isInheritableStyle(QName eltName, QName styleName);

    /**
     * Obtain initial value of named style.
     * @param eltName name of element type
     * @param styleName name of style
     * @return a initial value of named style or null if none defined or unknown named style
     */
    String getInitialStyleValue(QName eltName, QName styleName);

    /**
     * Determine if named style applies (semantically) to the named element type.
     * @param eltName name of element type
     * @param styleName name of style
     * @return true if named style apples to named element type
     */
    boolean doesStyleApply(QName eltName, QName styleName);

    /**
     * Determine if negative length expression is permitted or not.
     * @param eltName name of element type
     * @param styleName name of style
     * @return true if negative length expression is permitted
     */
    boolean isNegativeLengthPermitted(QName eltName, QName styleName);

    /**
     * Obtain semantics verifier for model.
     * @return semantics verifier instance
     */
    SemanticsVerifier getSemanticsVerifier();

    /**
     * Obtain parameter verifier for model.
     * @return parameter verifier instance
     */
    ParameterVerifier getParameterVerifier();

    /**
     * Obtain profile verifier for model.
     * @return profile verifier instance
     */
    ProfileVerifier getProfileVerifier();

    /**
     * Obtain style verifier for model.
     * @return style verifier instance
     */
    StyleVerifier getStyleVerifier();

    /**
     * Obtain timing verifier for model.
     * @return timing verifier instance
     */
    TimingVerifier getTimingVerifier();

    /**
     * Obtain metadata verifier for model.
     * @return metadata verifier instance
     */
    MetadataVerifier getMetadataVerifier();

    /**
     * Apply per-model reporter configuration.
     * @param reporter to configure
     */
    void configureReporter(Reporter reporter);

    /**
     * Make model specific resource state name.
     * @param name
     * @return model specific resource state name
     */
    String makeResourceStateName(String name);

    /**
     * Apply model specific resource state initializations.
     * @param uri URI of resource
     * @param state resource state object
     */
    void initializeResourceState(URI uri, Map<String,Object> state);
}
