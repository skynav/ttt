/*
 * Copyright 2013-14 Skynav, Inc. All rights reserved.
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

import java.util.List;
import java.util.Set;

public final class TopologicalSort {

    public static <T> List<T> sort(DirectedGraph<T> graph) {
        List<T> sort = new java.util.ArrayList<T>();
        Set<T> visited = new java.util.HashSet<T>();
        Set<T> expanded = new java.util.HashSet<T>();
        DirectedGraph<T> graphReversed = reverse(graph);
        for (T node : graphReversed) {
            visit(node, graphReversed, sort, visited, expanded);
        }
        return sort;
    }

    private static <T> DirectedGraph<T> reverse(DirectedGraph<T> graph) {
        DirectedGraph<T> graphReversed = new DirectedGraph<T>();
        for (T node : graph) {
            graphReversed.addNode(node);
        }
        for (T node : graph) {
            for (T to : graph.edgesFrom(node)) {
                graphReversed.addEdge(to, node);
            }
        }
        return graphReversed;
    }

    private static <T> void visit(T node, DirectedGraph<T> graph, List<T> sort, Set<T> visited, Set<T> expanded) {
        if (visited.contains(node)) {
            if (!expanded.contains(node))
                throw new IllegalArgumentException("graph contains a cycle");
            else
                return;
        } else {
            visited.add(node);
            for (T predecessor : graph.edgesFrom(node))
                visit(predecessor, graph, sort, visited, expanded);
            sort.add(node);
            expanded.add(node);
        }
    }

}


