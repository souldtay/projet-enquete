// src/util/Graph.java
package util;

import java.util.*;

/**
 * Minimal undirected graph (node IDs are plain strings).
 */
public class Graph {

    private final Set<String> nodes = new HashSet<>();
    private final List<String[]> edges = new ArrayList<>();

    /* ---------- public API ---------- */

    public void addNode(String id)           { nodes.add(id); }

    public void addEdge(String a, String b)  { edges.add(new String[]{a, b}); }

    /** <- NEW : exposes nodes for read-only usage */
    public Set<String> nodes()               { return Collections.unmodifiableSet(nodes); }

    /** <- NEW : exposes edges for read-only usage */
    public List<String[]> edges()            { return Collections.unmodifiableList(edges); }

    /** Optional: quick DOT export for external visualisers */
    public String toDot() {
        StringBuilder sb = new StringBuilder("graph G {\n");
        nodes.forEach(n -> sb.append("  ").append(n).append(";\n"));
        edges.forEach(e -> sb.append("  ").append(e[0]).append(" -- ").append(e[1]).append(";\n"));
        sb.append('}');
        return sb.toString();
    }
}
