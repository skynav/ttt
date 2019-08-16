/*
 * Copyright 2014-2019 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.converter;

import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.util.ExternalParameters;
import com.skynav.ttv.util.Reporter;

public interface ConverterContext {

    /**
     * Obtain initials from configuration.
     * @return list of initials
     */
    public List<Element> getConfigurationInitials();

    /**
     * Obtain region from configuration.
     * @param name of region
     * @return region
     */
    public Element getConfigurationRegion(String id);

    /**
     * Obtain external parameters.
     * @return external parameters object
     */
    public ExternalParameters getExternalParameters();

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

    /**
     * Obtain named option.
     * @param name of option
     * @return value of option
     */
    public String getOption(String name);

    /**
     * Obtain named boolean option.
     * @param name of option
     * @return value of option
     */
    public boolean getOptionBoolean(String name);

    /**
     * Obtain named integer option.
     * @param name of option
     * @return value of option
     */
    public int getOptionInteger(String name);

    /**
     * Obtain named object reference option.
     * @param name of option
     * @return value of option
     */
    public Object getOptionObject(String name);

    /**
     * Set named object reference option.
     * @param name of option
     * @param value of option
     */
    public void setOptionObject(String name, Object object);
    
    /**
     * Obtain known attribute specification map.
     * @return map of known attribute specifications
     */
    public Map<String, AttributeSpecification> getKnownAttributes();

}
