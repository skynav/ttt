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
 
package com.skynav.ttv.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import org.xml.sax.Locator;
import org.xml.sax.helpers.LocatorImpl;

public class Locators {

    private static final QName locatorQName = new QName(Annotations.getNamespace(), "loc", Annotations.getNamespacePrefix());
    public static QName getLocatorAttributeQName() {
        return locatorQName;
    }

    public static Locator getLocator(Object content) {
        if (content instanceof JAXBElement<?>)
            return getLocator(((JAXBElement<?>)content).getValue());
        else
            return getLocatorAttributeAsLocator(content);
    }

    private static Pattern locPattern = Pattern.compile("\\{([^\\}]*)\\}:([-]?\\d+):([-]?\\d+)");
    private static Locator getLocatorAttributeAsLocator(Object content) {
        String locatorAttribute = getLocatorAttribute(content);
        if (locatorAttribute != null) {
            Matcher m = locPattern.matcher(locatorAttribute);
            if (m.matches()) {
                LocatorImpl locator = new LocatorImpl();
                assert m.groupCount() == 3;
                locator.setSystemId(m.group(1));
                locator.setLineNumber(Integer.parseInt(m.group(2)));
                locator.setColumnNumber(Integer.parseInt(m.group(3)));
                return locator;
            } else
                return null;
        } else
            return null;
    }

    private static String getLocatorAttribute(Object content) {
        Map<QName,String> attrs = getOtherAttributes(content);
        if ((attrs != null) && attrs.containsKey(getLocatorAttributeQName()))
            return attrs.get(getLocatorAttributeQName());
        else if (content instanceof Element)
            return getLocatorAttribute((Element) content);
        else
            return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<QName,String> getOtherAttributes(Object content) {
        try {
            Class<?> contentClass = content.getClass();
            Method m = contentClass.getMethod("getOtherAttributes", new Class<?>[]{});
            return (Map<QName,String>) m.invoke(content, new Object[]{});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            return null;
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    private static String getLocatorAttribute(Element element) {
        NamedNodeMap attributes = element.getAttributes();
        for (int i = 0, n = attributes.getLength(); i < n; ++i) {
            Node item = attributes.item(i);
            if (!(item instanceof Attr))
                continue;
            Attr attribute = (Attr) item;
            String nsUri = attribute.getNamespaceURI();
            String localName = attribute.getLocalName();
            if (localName == null)
                localName = attribute.getName();
            if (localName.indexOf("xmlns") == 0)
                continue;
            QName name = new QName(nsUri != null ? nsUri : "", localName);
            if (name.equals(getLocatorAttributeQName()))
                return attribute.getValue();
        }
        return null;
    }
    
}
