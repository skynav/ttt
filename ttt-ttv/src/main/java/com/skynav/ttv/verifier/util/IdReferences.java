/*
 * Copyright 2013-2015 Skynav, Inc. All rights reserved.
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

package com.skynav.ttv.verifier.util;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.xml.namespace.QName;

import com.skynav.ttv.util.Location;
import com.skynav.ttv.util.Reporter;
import com.skynav.ttv.verifier.VerifierContext;

public class IdReferences {

    public static void badReference(Object value, Location location, VerifierContext context, QName referencingAttribute, QName targetExpected) {
        QName targetActual = context.getBindingElementName(value);
        Reporter reporter = context.getReporter();
        reporter.logInfo(reporter.message(location.getLocator(), "*KEY*",
            "Bad IDREF ''{0}'', got reference to ''{1}'', expected reference to ''{2}''.",
            getId(value), targetActual, targetExpected));
    }

    public static String getIdReferences(Object value) {
        if (value instanceof List<?>) {
            StringBuffer sb = new StringBuffer();
            for (Object v : (List<?>) value) {
                String id = getId(v);
                if (id.length() > 0) {
                    if (sb.length() > 0)
                        sb.append(' ');
                    sb.append(id);
                }
            }
            return sb.toString();
        } else
            return getId(value);
    }

    public static String getIdReference(Object value) {
        return getId(value);
    }

    public static String getId(Object content) {
        try {
            Class<?> contentClass = content.getClass();
            Method m = contentClass.getMethod("getId", new Class<?>[]{});
            return (String) m.invoke(content, new Object[]{});
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (NoSuchMethodException e) {
            return "";
        } catch (SecurityException e) {
            throw new RuntimeException(e);
        }
    }

}
