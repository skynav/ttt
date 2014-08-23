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

import javax.xml.namespace.QName;

import org.w3c.dom.Node;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.Reporter;

public interface VerifierContext {

    /**
     * Obtain reference to reporter.
     * @return reporter instance
     */
    public Reporter getReporter();

    /**
     * Obtain reference to model.
     * @return model instance
     */
    public Model getModel();

    /**
     * Obtain XML element type name associated with a binding object,
     * which must either be an (outer) content element, i.e., JAXBElement<?>
     * instance, or an (inner) content value object.
     * @param value a binding object as described above
     * @return qualified name, which, if no name is available, then QName("","")
     * is returned
     */
    public QName getBindingElementName(Object value);

    /**
     * Obtain binding object associated with XML infoset parent of XML element
     * associated with specified binding object.
     * @param value a binding object associated with an XML infoset node
     * @return parent binding object or null if none
     */
    public Object getBindingElementParent(Object value);

    /**
     * Obtain binding content element or value instance associated
     * with infoset node. If an (outer) content element, i.e., JAXBElement<?>
     * is available, it will be returned; otherwise, an (inner) content value
     * object will be returned. Or if neither is available, null is returned.
     * @param an infoset node
     * @return an instance of JAXBElement<?> or a content object instance or null
     */
    public Object getBindingElement(Node node);

    /**
     * Obtain infoset node associated with a binding object, which must
     * be either a JAXBElement<?> instance or a content value object instance.
     * @param value a binding object as described above
     * @return an infoset node or null
     */
    public Node getXMLNode(Object value);

    /**
     * Establish or update a per-resource context state variable.
     * @param key name of state variable
     * @param value to set into state variable
     */
    public void setResourceState(String key, Object value);

    /**
     * Obtain value of per-resource context state variable.
     * @param key name of state variable
     * @return value of state variable or null if unknown key
     */
    public Object getResourceState(String key);

}
