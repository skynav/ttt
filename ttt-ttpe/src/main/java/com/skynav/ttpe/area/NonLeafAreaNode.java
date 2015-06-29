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

package com.skynav.ttpe.area;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Element;

public abstract class NonLeafAreaNode extends AreaNode {

    public enum Expansion {
        ENCLOSE_IPD,                                            // increase ipd if needed to enclose new child
        ENCLOSE_BPD,                                            // increase bpd if needed to enclose new child
        EXPAND_IPD,                                             // increase ipd by new child's ipd
        EXPAND_BPD,                                             // increase bpd by new child's bpd
        CROSS;                                                  // apply to cross (perpendicular) axis
    };

    public static final Set<Expansion> ENCLOSE_ALL;             // enclose both bpd and ipd
    public static final Set<Expansion> ENCLOSE_ALL_CROSS;       // enclose both bpd and ipd
    public static final Set<Expansion> EXPAND_ALL;              // expand both bpd and ipd
    public static final Set<Expansion> EXPAND_LINE;             // expand bpd, enclose ipd
    public static final Set<Expansion> EXPAND_IPD;              // expand ipd only
    public static final Set<Expansion> EXPAND_BPD;              // expand bpd only
    static {
        Set<Expansion> s;
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.ENCLOSE_IPD);
        s.add(Expansion.ENCLOSE_BPD);
        ENCLOSE_ALL = Collections.unmodifiableSet(s);
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.ENCLOSE_IPD);
        s.add(Expansion.ENCLOSE_BPD);
        s.add(Expansion.CROSS);
        ENCLOSE_ALL_CROSS = Collections.unmodifiableSet(s);
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.EXPAND_IPD);
        s.add(Expansion.EXPAND_BPD);
        EXPAND_ALL = Collections.unmodifiableSet(s);
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.ENCLOSE_IPD);
        s.add(Expansion.EXPAND_BPD);
        EXPAND_LINE = Collections.unmodifiableSet(s);
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.EXPAND_IPD);
        EXPAND_IPD = Collections.unmodifiableSet(s);
        s = new java.util.HashSet<Expansion>();
        s.add(Expansion.EXPAND_BPD);
        EXPAND_BPD = Collections.unmodifiableSet(s);
    }

    private List<AreaNode> children;

    public NonLeafAreaNode(Element e) {
        super(e);
    }

    public void expand(AreaNode a, Set<Expansion> expansions) {
    }

    public AreaNode firstChild() {
        if ((children == null) || children.isEmpty())
            return null;
        else
            return children.get(0);
    }

    public AreaNode lastChild() {
        if ((children == null) || children.isEmpty())
            return null;
        else
            return children.get(children.size() - 1);
    }

    public void addChildren(List<? extends AreaNode> children) {
        addChildren(children, null);
    }

    public void addChildren(List<? extends AreaNode> children, Set<Expansion> expansions) {
        for (AreaNode c : children)
            addChild(c, expansions);
    }

    public void addChild(AreaNode c) {
        addChild(c, null);
    }

    public void addChild(AreaNode c, Set<Expansion> expansions) {
        c.setParent(this);
        if (children == null)
            children = new java.util.ArrayList<AreaNode>();
        children.add(c);
        if (expansions != null)
            expand(c, expansions);
    }

    public void insertChild(AreaNode c, AreaNode cBefore) {
        insertChild(c, cBefore, null);
    }

    public void insertChild(AreaNode c, AreaNode cBefore, Set<Expansion> expansions) {
        c.setParent(this);
        if (children == null)
            children = new java.util.ArrayList<AreaNode>();
        if (cBefore == null)
            children.add(c);
        else {
            int i = children.indexOf(cBefore);
            if (i < 0)
                throw new IllegalArgumentException();
            else
                children.add(i, c);
        }
        if (expansions != null)
            expand(c, expansions);
    }

    public List<AreaNode> getChildren() {
        if (children == null)
            children = new java.util.ArrayList<AreaNode>();
        return children;
    }

    public int getChildCount() {
        return (children != null) ? children.size() : 0;
    }

}
