/*
 * Copyright 2013-15 Skynav, Inc. All rights reserved.
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

import com.skynav.ttv.model.ttml.TTML;
import com.skynav.ttv.util.Visitor;
import com.skynav.ttx.transformer.TransformerContext;

public abstract class TTMLHelper {

    public static final String NAMESPACE_TT                     = TTML.Constants.NAMESPACE_TT;
    public static final String NAMESPACE_TT_METADATA            = TTML.Constants.NAMESPACE_TT_METADATA;
    public static final String NAMESPACE_TT_STYLE               = TTML.Constants.NAMESPACE_TT_STYLE;
    public static final String NAMESPACE_TT_PARAMETER           = TTML.Constants.NAMESPACE_TT_PARAMETER;
    public static final String NAMESPACE_ISD                    = TTML.Constants.NAMESPACE_TT_ISD;

    protected TTMLHelper() {
    }

    public static TTMLHelper makeInstance(int version) {
        if (version == 1)
            return new TTML1Helper();
        else if (version == 2)
            return new TTML2Helper();
        else
            return null;
    }

    public void traverse(Object tt, Visitor v) throws Exception {
        throw new UnsupportedOperationException();
    }

    public  void generateAnonymousSpans(Object root, final TransformerContext context) {
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    public boolean isBody(Object content) {
        throw new UnsupportedOperationException();
    }

    public boolean isAnonymousSpan(Object content) {
        throw new UnsupportedOperationException();
    }

    public boolean isTimedElement(Object content) {
        throw new UnsupportedOperationException();
    }

    public boolean isTimedContainerElement(Object content) {
        throw new UnsupportedOperationException();
    }

    public boolean isSequenceContainer(Object content) {
        throw new UnsupportedOperationException();
    }

    public String getClassString(Object content) {
        throw new UnsupportedOperationException();
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

    private static String digits = "0123456789";
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