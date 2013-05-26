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

import java.util.Map;

import com.skynav.ttv.validator.SemanticsValidator;
import com.skynav.ttv.validator.StyleValidator;

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
     * Obtain semantics validator for model.
     * @return semantics validator instance
     */
    SemanticsValidator getSemanticsValidator();

    /**
     * Obtain style validator for model.
     * @return style validator instance
     */
    StyleValidator getStyleValidator();
}
