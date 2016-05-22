/*
 * Copyright 2013-16 Skynav, Inc. All rights reserved.
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

package com.skynav.ttx.transformer.isd;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttv.model.Model;
import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.util.StyleSet;
import com.skynav.ttv.util.StyleSpecification;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttx.transformer.TransformerContext;
import com.skynav.xml.helpers.Documents;
import com.skynav.xml.helpers.XML;

public abstract class ISDHelper {

    public static final String NAMESPACE_ISD                    = TTML.Constants.NAMESPACE_TT_ISD;

    public static final QName isdElementName                    = new QName(NAMESPACE_ISD,"isd");
    public static final QName isdSequenceElementName            = new QName(NAMESPACE_ISD,"sequence");
    public static final QName isdStyleElementName               = new QName(NAMESPACE_ISD,"css");
    public static final QName isdRegionElementName              = new QName(NAMESPACE_ISD,"region");

    public static final QName isdStyleAttributeName             = new QName(NAMESPACE_ISD,"css");

    protected ISDHelper() {
    }

    public int getVersion() {
        throw new UnsupportedOperationException();
    }

    public void traverse(Object tt, Visitor v) throws Exception {
    }

    public void generateAnonymousSpans(Object root, final TransformerContext context) {
    }

    public int[] getGenerationIndices(TransformerContext context) {
        return (int[]) context.getResourceState(ResourceState.isdGenerationIndices.name());
    }

    public int generateStyleSetIndex(TransformerContext context) {
        int[] indices = getGenerationIndices(context);
        return indices[GenerationIndex.isdStyleSetIndex.ordinal()]++;
    }

    public String generateAnonymousSpanId(TransformerContext context) {
        int[] indices = getGenerationIndices(context);
        return "isdSpan" + pad(indices[GenerationIndex.isdAnonymousSpanIndex.ordinal()]++, 6);
    }

    public String generateAnonymousRegionId(TransformerContext context) {
        int[] indices = getGenerationIndices(context);
        return "isdRegion" + pad(indices[GenerationIndex.isdAnonymousRegionIndex.ordinal()]++, 6);
    }

    @SuppressWarnings("rawtypes")
    public List getChildren(Object content) {
        return new java.util.ArrayList<Object>();
    }

    public boolean isTimedText(Object content) {
        return false;
    }

    public boolean isAnonymousSpan(Object content) {
        return false;
    }

    public boolean isTimed(Object content) {
        return false;
    }

    public boolean isTimedContainer(Object content) {
        return false;
    }

    public boolean isSequenceContainer(Object content) {
        return false;
    }

    public String getClassString(Object content) {
        return content.getClass().toString();
    }

    public boolean isISDElement(Element elt) {
        return Documents.isElement(elt, isdElementName);
    }

    public boolean isISDSequenceElement(Element elt) {
        return Documents.isElement(elt, isdSequenceElementName);
    }

    public boolean isISDStyleElement(Element elt) {
        return Documents.isElement(elt, isdStyleElementName);
    }

    public boolean isISDRegionElement(Element elt) {
        return Documents.isElement(elt, isdRegionElementName);
    }

    public static void setXmlIdentifier(Element elt, String id) {
        Documents.setAttribute(elt, XML.xmlIdentifierAttributeName, id);
    }

    public static String getXmlIdentifier(Element elt) {
        return Documents.getAttribute(elt, XML.xmlIdentifierAttributeName, null);
    }

    public boolean specialStyleInheritance(Element elt, QName styleName, StyleSet sss, TransformerContext context) {
        return false;
    }

    public StyleSpecification getSpecialInheritedStyle(Element elt, QName styleName, StyleSet sss, Map<Element, StyleSet> specifiedStyleSets, TransformerContext context) {
        return null;
    }

    public void enactISDPostTransforms(Document document, TransformerContext context) {
    }

    public boolean hasUsableContent(Element elt) {
        return false;
    }

    public Element getISD(Element elt) {
        return Documents.findAncestorByName(elt, isdElementName);
    }

    public Element getFirstRegion(Element isd) {
        List<Element> regions = Documents.findElementsByName(isd, isdRegionElementName);
        return !regions.isEmpty() ? regions.get(0) : null;
    }

    public static ISDHelper makeInstance(Model model) {
        assert model != null;
        String modelName = model.getName();
        if (modelName.startsWith("imsc")) {
            return new IMSC1Helper();
        } else {
            int version = model.getTTMLVersion();
            if (version > 2)
                throw new UnsupportedOperationException();
            else if (version == 2)
                return new TTML2Helper();
            else
                return new TTML1Helper();
        }
    }

    public static String getStringValuedAttribute(Object content, String attributeName) {
        try {
            Class<?> contentClass = content.getClass();
            Method m = contentClass.getMethod(makeGetterName(attributeName), new Class<?>[]{});
            return (String) m.invoke(content, new Object[]{});
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

    private static String makeGetterName(String attributeName) {
        assert attributeName.length() > 0;
        StringBuffer sb = new StringBuffer();
        sb.append("get");
        sb.append(Character.toUpperCase(attributeName.charAt(0)));
        sb.append(attributeName.substring(1));
        return sb.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<QName,String> getOtherAttributes(Object content) {
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

    private static final String digits = "0123456789";
    public static String pad(int value, int width) {
        assert value >= 0;
        StringBuffer sb = new StringBuffer(width);
        while (value > 0) {
            sb.append(digits.charAt(value % 10));
            value /= 10;
        }
        while (sb.length() < width) {
            sb.append('0');
        }
        return sb.reverse().toString();
    }

    public static String escapeControls(String s) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = s.length(); i < n; ++i) {
            char c = s.charAt(i);
            if (c == '\n')
                sb.append("\\n");
            else if (c == '\r')
                sb.append("\\r");
            else if (c == '\t')
                sb.append("\\t");
            else if (c == '\f')
                sb.append("\\f");
            else
                sb.append(c);
        }
        return sb.toString();
    }

}
