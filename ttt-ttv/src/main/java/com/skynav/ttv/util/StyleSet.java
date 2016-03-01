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

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;

public class StyleSet extends AbstractMap<ComparableQName, StyleSpecification> implements Comparable<StyleSet> {

    public static final StyleSet EMPTY = new StyleSet();

    private int generation;
    private String id;
    private Map<ComparableQName, StyleSpecification> styles;
    private Condition condition;

    public StyleSet() {
        this(-1);
    }

    public StyleSet(int generation) {
        this(generation, "", null);
    }

    public StyleSet(int generation, StyleSet styles) {
        this(generation, "", styles);
    }

    public StyleSet(String id) {
        this(extractGeneration(id), id, null);
    }

    public StyleSet(String id, StyleSet styles) {
        this(extractGeneration(id), id, styles);
    }

    public StyleSet(int generation, String id, StyleSet styles) {
        this.generation = generation;
        assert id != null;
        this.id = id;
        if (styles == null)
            this.styles = new java.util.TreeMap<ComparableQName,StyleSpecification>();
        else
            this.styles = new java.util.TreeMap<ComparableQName,StyleSpecification>(styles);
    }

    public int getGeneration() {
        return generation;
    }

    public Map<ComparableQName, StyleSpecification> getStyles() {
        return styles;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    public Condition getCondition() {
        return condition;
    }

    public boolean isCondition(Condition.EvaluatorState state) {
        return (condition == null) || condition.evaluate(state);
    }

    public boolean isEmpty() {
        return styles.isEmpty();
    }

    public void merge(StyleSet styles, Condition.EvaluatorState state) {
        if (styles.isCondition(state))
            this.styles.putAll(styles);
    }

    public void merge(QName name, String value) {
        merge(new StyleSpecification(name, value));
    }

    public void merge(StyleSpecification style) {
        this.styles.put(style.getName(), style);
    }

    public void remove(ComparableQName name) {
        styles.remove(name);
    }

    public StyleSpecification get(QName qn) {
        return this.styles.get(qn instanceof ComparableQName ? qn : new ComparableQName(qn));
    }

    @Override
    public Set<Map.Entry<ComparableQName, StyleSpecification>> entrySet() {
        return styles.entrySet();
    }

    public int compareTo(StyleSet other) {
        int d = compare(styles.values(), other.styles.values());
        return (d != 0) ? d : id.compareTo(other.id);
    }

    private static final int compare(Collection<StyleSpecification> styles1, Collection<StyleSpecification> styles2) {
        List<StyleSpecification> sl1 = new java.util.ArrayList<StyleSpecification>(styles1);
        List<StyleSpecification> sl2 = new java.util.ArrayList<StyleSpecification>(styles2);
        int nsl1 = sl1.size();
        int nsl2 = sl2.size();
        for (int i = 0, n = Math.min(nsl1, nsl2); i < n; ++i) {
            StyleSpecification s1 = sl1.get(i);
            StyleSpecification s2 = sl2.get(i);
            int d = s1.compareTo(s2);
            if (d != 0)
                return d;
        }
        if (nsl1 < nsl2)
            return -1;
        else if (nsl1 > nsl2)
            return 1;
        else
            return 0;
    }

    @Override
    public int hashCode() {
        return styles.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof StyleSet)
            return styles.equals(((StyleSet) o).styles);
        else
            return false;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append('{');
        for (StyleSpecification ss : styles.values()) {
            if (sb.length() > 1)
                sb.append(',');
            sb.append(ss);
        }
        sb.append('}');
        return sb.toString();
    }

    public final static Comparator<StyleSet> GENERATION_COMPARATOR = new Comparator<StyleSet>() {
        public int compare(StyleSet ss1, StyleSet ss2) {
            int g1 = ss1.generation;
            int g2 = ss2.generation;
            if (g1 < g2)
                return -1;
            else if (g1 > g2)
                return 1;
            else
                return 0;
        }
    };

    private static int extractGeneration(String id) {
        for (int i = 0, n = id.length(); i < n; ++i) {
            try {
                return Integer.parseInt(id.substring(i));
            } catch (NumberFormatException e) {
            }
        }
        return -1;
    }

    public static Comparator<StyleSet> getGenerationComparator() {
        return GENERATION_COMPARATOR;
    }

    public final static Comparator<StyleSet> VALUES_COMPARATOR = new Comparator<StyleSet>() {
        public int compare(StyleSet ss1, StyleSet ss2) {
            return ss1.compareTo(ss2);
        }
    };

    public static Comparator<StyleSet> getValuesComparator() {
        return VALUES_COMPARATOR;
    }

}
