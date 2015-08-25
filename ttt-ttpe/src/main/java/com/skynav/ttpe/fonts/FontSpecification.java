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

package com.skynav.ttpe.fonts;

import java.io.File;
import java.util.BitSet;
import java.util.List;

import javax.xml.namespace.QName;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.skynav.ttpe.app.Namespace;
import com.skynav.xml.helpers.Documents;

public class FontSpecification {

    // specification state
    public String family;
    public FontStyle style;
    public FontWeight weight;
    public String language;
    public String source;
    public BitSet forcePath;

    public FontSpecification(String family, FontStyle style, FontWeight weight, String language, String source, BitSet forcePath) {
        this.family     = family;
        this.style      = style;
        this.weight     = weight;
        this.language   = language;
        this.source     = source;
        this.forcePath  = forcePath;
    }

    public boolean matches(FontKey key) {
        if (!key.family.equals(family))
            return false;
        else if (key.style != style)
            return false;
        else if (key.weight != weight)
            return false;
        else if (!key.language.isEmpty() && !key.language.equals(language))
            return false;
        else
            return true;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('[');
        sb.append(family.toUpperCase());
        sb.append(',');
        sb.append(style);
        sb.append(',');
        sb.append(weight);
        sb.append(',');
        sb.append(source);
        sb.append(']');
        return sb.toString();
    }

    private static final QName ttpeFontEltName  = new QName(Namespace.NAMESPACE, "font");
    private static final QName ttpeParamEltName = new QName(Namespace.NAMESPACE, "param");
    public static List<FontSpecification> fromDocument(Document d, String sourceBase) {
        List<FontSpecification> specs = new java.util.ArrayList<FontSpecification>();
        for (Element f : Documents.findElementsByName(d, ttpeFontEltName)) {
            String family = null;
            FontStyle style = FontKey.DEFAULT_STYLE;
            FontWeight weight = FontKey.DEFAULT_WEIGHT;
            String language = FontKey.DEFAULT_LANGUAGE;
            String source = null;
            BitSet forcePath = null;
            for (Element p : Documents.findElementsByName(f, ttpeParamEltName)) {
                if (p.hasAttribute("name")) {
                    String n = p.getAttribute("name");
                    String v = p.getTextContent();
                    if (n.equals("family"))
                        family = v.toLowerCase();
                    else if (n.equals("style"))
                        style = FontStyle.valueOf(v);
                    else if (n.equals("weight"))
                        weight = FontWeight.valueOf(v);
                    else if (n.equals("language"))
                        language = v.toLowerCase();
                    else if (n.equals("source")) {
                        source = v;
                        if (!source.startsWith(File.separator))
                            source = sourceBase + File.separator + source;
                    } else if (n.equals("forcePath"))
                        forcePath = parseIntegerRanges(v);
                }
            }
            if ((family == null) || family.isEmpty())
                continue;
            if ((source == null) || source.isEmpty())
                continue;
            specs.add(new FontSpecification(family, style, weight, language, source, forcePath));
        }
        return specs;
    }

    private static BitSet parseIntegerRanges(String s) {
        if (s.isEmpty())
            return null;
        else {
            List<Integer> ll = new java.util.ArrayList<Integer>();
            String[] ranges = s.split(",");
            int lMax = 0;
            for (String r : ranges) {
                String[] limits = r.split("-");
                int nl = limits.length;
                int l1, l2;
                try {
                    if (nl < 1) {
                        throw new IllegalArgumentException("invalid range syntax: too few limit values");
                    } else if (nl < 2) {
                        l1 = Integer.parseInt(limits[0]);
                        l2 = l1;
                    } else if (nl < 3) {
                        l1 = Integer.parseInt(limits[0]);
                        l2 = Integer.parseInt(limits[1]);
                    } else {
                        throw new IllegalArgumentException("invalid range syntax: too many limit values");
                    }
                    if (l1 > l2)
                        throw new IllegalArgumentException("invalid range syntax: lower limit must be less than or equal to upper limit");
                    if (l2 > lMax)
                        lMax = l2;
                    ll.add(Integer.valueOf(l1));
                    ll.add(Integer.valueOf(l2));
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("invalid range limit syntax: " + e.getMessage());
                }
            }
            BitSet bits = new BitSet(lMax + 1);
            for (int i = 0, n = ll.size(); i < n; i += 2) {
                int l1 = ll.get(i + 0);
                int l2 = ll.get(i + 1);
                for (int l = l1; l <= l2; ++l)
                    bits.set(l);
            }
            return bits;
        }
    }
}




