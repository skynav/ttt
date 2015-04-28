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

package com.skynav.ttv.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.sax.SAXSource;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import org.xml.sax.InputSource;

import com.skynav.ttv.app.Namespace;
import com.skynav.ttv.util.IOUtil;
import com.skynav.xml.helpers.Documents;

public class Configuration {

    public static final String defaultFileName                  = "config.xml";
    private static final QName optionEltName                    = new QName(Namespace.NAMESPACE, "option");

    private ConfigurationDefaults defaults;
    private Map<String,String> options;

    public Configuration() {
        this(new ConfigurationDefaults());
    }

    public Configuration(ConfigurationDefaults defaults) {
        this(defaults, null);
    }

    public Configuration(ConfigurationDefaults defaults, Document d) {
        if (defaults == null)
            throw new IllegalArgumentException();
        this.defaults = defaults;
        populate(d);
    }

    protected void populate(Document d) {
        populateOptions(d);
    }

    private void populateOptions(Document d) {
        Map<String,String> options = new java.util.HashMap<String,String>();
        if (defaults != null)
            defaults.populateDefaults(options);
        if (d != null) {
            for (Element e : Documents.findElementsByName(d, optionEltName)) {
                if (e.hasAttribute("name")) {
                    String n = e.getAttribute("name");
                    String v = performVariableSubstitution(e.getTextContent());
                    options.put(n, v);
                }
            }
        }
        this.options = options;
    }

    private String performVariableSubstitution(String s) {
        if (maybeHasVariable(s)) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0, n = s.length(); i < n;) {
                int c0 = s.charAt(i++);
                if (c0 == '$') {
                    if (i < n) {
                        int c1 = s.charAt(i++);
                        if (c1 == '{') {
                            int j = s.indexOf('}', i);
                            if (j >= i) {
                                String name = s.substring(i, j);
                                String value = getVariable(name);
                                if (value != null)
                                    sb.append(value);
                                else {
                                    sb.append("${");
                                    sb.append(s.substring(i, j + 1));
                                }
                                i = j + 1;
                            } else {
                                sb.append("${");
                                sb.append(s.substring(i));
                                i = n;
                            }
                        } else {
                            sb.append((char) c0);
                            sb.append((char) c1);
                        }
                    }
                } else
                    sb.append((char) c0);
            }
            return sb.toString();
        } else
            return s;
    }

    private boolean maybeHasVariable(String s) {
        return s.indexOf("${") >= 0;
    }

    private String getVariable(String name) {
        if (name.equals("configdir")) {
            return defaults.getConfigurationDirectory();
        } else
            return null;
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
        return (defaults != null) ? defaults.getDefault(name) : null;
    }

    public static Configuration fromFile(String name, ConfigurationDefaults defaults, Class<? extends Configuration> cls) throws IOException {
        if (name == null)
            name = defaultFileName;
        URL urlConfig = cls.getResource(name);
        if (urlConfig != null)
            return fromStream(urlConfig.openStream(), defaults, cls);
        else
            return newInstance(cls, defaults, null);
    }

    public static Configuration fromFile(File f, ConfigurationDefaults defaults, Class<? extends Configuration> cls) throws IOException {
        InputStream is = null;
        try {
            is = new FileInputStream(f);
            return fromStream(is, defaults, cls); 
        } catch (IOException e) {
            IOUtil.closeSafely(is);
            throw e;
        }
    }

    public static Configuration fromStream(InputStream is, ConfigurationDefaults defaults, Class<? extends Configuration> cls) throws IOException {
        try {
            SAXSource source = new SAXSource(new InputSource(is));
            DOMResult result = new DOMResult();
            TransformerFactory.newInstance().newTransformer().transform(source, result);
            return newInstance(cls, defaults, (Document) result.getNode());
        } catch (TransformerFactoryConfigurationError e) {
            return null;
        } catch (TransformerException e) {
            return null;
        }
    }

    public static Configuration newInstance(Class<? extends Configuration> cls, ConfigurationDefaults defaults, Document d) {
        if (cls != null) {
            try {
                Constructor<? extends Configuration> constructor = cls.getDeclaredConstructor(new Class<?>[] { ConfigurationDefaults.class, Document.class });
                return constructor.newInstance(new Object[] { defaults, d });
            } catch (NoSuchMethodException e) {
                return null;
            } catch (IllegalAccessException e) {
                return null;
            } catch (InvocationTargetException e) {
                return null;
            } catch (InstantiationException e) {
                return null;
            }
        } else
            return null;
    }

    public static String getDefaultConfigurationPath(Class<?> cls, String name) {
        if (name == null)
            name = defaultFileName;
        URL url = cls.getResource(name);
        if (url != null) {
            try {
                URI uri = url.toURI();
                if (uri.getScheme().equals("file")) {
                    File f = new File(uri.getPath());
                    return f.getAbsolutePath();
                }
            } catch (URISyntaxException e) {
            }
        }
        return null;
    }

}
