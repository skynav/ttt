/*
 * Copyright 2014-15 Skynav, Inc. All rights reserved.
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

package com.skynav.ttpe.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttv.model.ttml.TTML1;
import com.skynav.ttv.util.IOUtil;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public class Configuration {
    private Map<String,String> options;
    private List<Element> initials;
    private List<Element> regions;
    private Configuration() {
    }
    private Configuration(Document d) {
        populate(d);
    }
    private void populate(Document d) {
        populateOptions(d);
        populateInitials(d);
        populateRegions(d);
    }
    private static final QName ttpeOptionEltName = new QName(Namespace.NAMESPACE, "option");
    private void populateOptions(Document d) {
        Map<String,String> options = new java.util.HashMap<String,String>();
        ConfigurationDefaults.populateDefaults(options);
        for (Element e : Documents.findElementsByName(d, ttpeOptionEltName)) {
            if (e.hasAttribute("name")) {
                String n = e.getAttribute("name");
                String v = e.getTextContent();
                options.put(n, v);
            }
        }
        this.options = options;
    }
    public Map<String,String> getOptions() {
        return options;
    }
    public String getOption(String name) {
        return getOption(name, getOptionDefault(name));
    }
    public String getOption(String name, String optionDefault) {
        if (options.containsKey(name))
            return options.get(name);
        else
            return optionDefault;
    }
    public String getOptionDefault(String name) {
        return ConfigurationDefaults.getDefault(name);
    }
    public List<Element> getInitials() {
        return initials;
    }
    public List<Element> getRegions() {
        return regions;
    }
    public Element getRegion(String id) {
        for (Element r : regions) {
            String rId = r.getAttributeNS(XML.getNamespaceUri(), "id");
            if ((rId != null) && rId.equals(id))
                return r;
        }
        return null;
    }
    private static final QName ttInitialEltName = new QName(TTML1.Constants.NAMESPACE_TT, "initial");
    private void populateInitials(Document d) {
        this.initials = Documents.findElementsByName(d, ttInitialEltName);
    }
    private static final QName ttRegionEltName = new QName(TTML1.Constants.NAMESPACE_TT, "region");
    private void populateRegions(Document d) {
        this.regions = Documents.findElementsByName(d, ttRegionEltName);
    }
    public static final String defaultConfigurationName = "ttpe.xml";
    private static final Configuration nullConfiguration = new Configuration();
    public static Configuration fromDefault() throws IOException {
        URL urlConfig = Configuration.class.getResource(defaultConfigurationName);
        if (urlConfig != null)
            return fromStream(urlConfig.openStream());
        else
            return nullConfiguration;
    }
    public static Configuration fromFile(File f) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            return fromStream(is); 
        } catch (IOException e) {
            IOUtil.closeSafely(is);
            throw e;
        }
    }
    public static Configuration fromStream(InputStream is) throws IOException {
        try {
            SAXSource source = new SAXSource(new InputSource(is));
            DOMResult result = new DOMResult();
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            Document d = (Document) result.getNode();
            return new Configuration(d);
        } catch (TransformerFactoryConfigurationError e) {
            return nullConfiguration;
        } catch (TransformerException e) {
            return nullConfiguration;
        }
    }
}
