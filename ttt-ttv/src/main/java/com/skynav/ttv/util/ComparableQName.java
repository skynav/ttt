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
 
package com.skynav.ttv.util;

import javax.xml.namespace.QName;

public class ComparableQName extends QName implements Comparable<ComparableQName> {

    private static final long serialVersionUID = 6595331889303441857L;

    public ComparableQName(QName name) {
        this(name.getNamespaceURI(), name.getLocalPart());
    }

    public ComparableQName(String nsUri, String localPart) {
        super(nsUri, localPart);
    }

    public int compareTo(ComparableQName other) {
        String ns1 = getNamespaceURI();
        String ns2 = other.getNamespaceURI();
        if ((ns1 == null) && (ns2 != null))
            return -1;
        else if ((ns1 != null) && (ns2 == null))
            return 1;
        else if ((ns1 != null) && (ns2 != null)) {
            int d = ns1.compareTo(ns2);
            if (d != 0)
                return d;
        }
        String n1 = getLocalPart();
        String n2 = other.getLocalPart();
        return n1.compareTo(n2);
    }

}
