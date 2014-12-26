/*
 * Copyright 2014 Skynav, Inc. All rights reserved.
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

package com.skynav.cap2tt.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.xml.sax.InputSource;

import org.w3c.dom.Document;

import com.skynav.ttv.util.IOUtil;

public class Configuration {
    public static final String defaultConfigurationName = "cap2tt.xml";
    private static final Configuration nullConfiguration = new Configuration();
    private Configuration() {
    }
    private Configuration(Document d) {
    }
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
