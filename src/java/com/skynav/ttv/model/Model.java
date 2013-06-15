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

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

import com.skynav.ttv.verifier.ParameterVerifier;
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
     * Obtain schema resource name (path), which should be
     * an acceptable argument to pass to ClassLoader.getResource().
     * @return schema resource name (path)
     */
    String getSchemaResourceName();

    /**
     * Obtain primary schema (target) namespace URI string. 
     * @return namespace URI string
     */
    String getNamespaceUri();

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
     * Obtain qualified names of element type of ancestor of element
     * type which IDREFs in the specified attribute must reference.
     * @param attributeName name of referring attribute
     * @return a set of qualified names or null if no constraint on target
     * ancestor element type
     */
    Set<QName> getIdReferenceAncestorNames(QName attributeName);

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
     * Obtain style verifier for model.
     * @return style verifier instance
     */
    StyleVerifier getStyleVerifier();

    /**
     * Obtain timing verifier for model.
     * @return timing verifier instance
     */
    TimingVerifier getTimingVerifier();
}
