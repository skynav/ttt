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

package com.skynav.ttv.verifier;

import java.util.Collection;

import javax.xml.namespace.QName;

import com.skynav.ttv.model.value.Length;

public interface StyleVerifier extends ItemVerifier {

    /**
     * Add initial style overrides from styling element initial.
     * @param initial styling element
     * @param context verifier context instance
     */
    void addInitialOverrides(Object initial, VerifierContext context);

    /**
     * Get initial style override.
     * @param styleName style name
     * @param context verifier context instance
     * @return a style value object or null if no override
     */
    Object getInitialOverride(QName styleName, VerifierContext context);

    /**
     * Obtain qualified name for style attribute given a unique, unqualified style property name.
     * @param propertyName non-empty local name for style property
     * @return a qualified name or null if unknown style property
     */
    QName getStyleAttributeName(String propertyName);

    /**
     * Obtain defined style names.
     * @return a collection of style names defined by this model.
     */
    Collection<QName> getDefinedStyleNames();

    /**
     * Obtain applicable style names for a specified named element type.
     * @param elementName of element type
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
     * Determine if negative length is permitted.
     * @param eltName name of element type
     * @param styleName name of style
     * @return true if negative length is permitted
     */
    boolean isNegativeLengthPermitted(QName eltName, QName styleName);

    /**
     * Determine if length units is permitted.
     * @param eltName name of element type
     * @param styleName name of style
     * @param units length unit
     * @return true if length unit is permitted
     */
    boolean isLengthUnitsPermitted(QName eltName, QName styleName, Length.Unit units);

}
