/*
 * Copyright 2013-2016 Skynav, Inc. All rights reserved.
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

package com.skynav.ttx.util;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public final class DirectedGraph<T> implements Iterable<T> {

    private Map<T, Set<T>> graph = new java.util.HashMap<T, Set<T>>();

    public boolean addNode(T node) {
        if (graph.containsKey(node))
            return false;
        else {
            graph.put(node, new java.util.HashSet<T>());
            return true;
        }
    }

    public void addEdge(T from, T to) {
        if (!graph.containsKey(from))
            throw new NoSuchElementException(from.toString());
        else if (!graph.containsKey(to))
            throw new NoSuchElementException(to.toString());
        else
            graph.get(from).add(to);
    }

    public void removeEdge(T from, T to) {
        if (!graph.containsKey(from))
            throw new NoSuchElementException(from.toString());
        else if (!graph.containsKey(to))
            throw new NoSuchElementException(to.toString());
        else
            graph.get(from).remove(to);
    }

    public boolean hasEdge(T from, T to) {
        if (!graph.containsKey(from))
            throw new NoSuchElementException(from.toString());
        else if (!graph.containsKey(to))
            throw new NoSuchElementException(to.toString());
        else
            return graph.get(from).contains(to);
    }

    public Set<T> edgesFrom(T node) {
        if (!graph.containsKey(node))
            throw new NoSuchElementException();
        else
            return Collections.unmodifiableSet(graph.get(node));
    }

    public Iterator<T> iterator() {
        return graph.keySet().iterator();
    }

    public int size() {
        return graph.size();
    }

    public boolean isEmpty() {
        return graph.isEmpty();
    }
}


