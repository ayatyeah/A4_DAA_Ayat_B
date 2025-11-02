package org.yeah.graph.scc;

import org.yeah.model.Graph;
import org.yeah.model.Edge;
import org.yeah.utils.Metrics;
import java.util.*;

public class TarjanSCC {
    private final Graph graph;
    private final Metrics metrics;
    private int index;
    private int[] indices;
    private int[] lowlinks;
    private boolean[] onStack;
    private Stack<Integer> stack;
    private List<List<Integer>> sccs;

    public TarjanSCC(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
        this.graph.buildGraph();
    }

    public List<List<Integer>> findSCCs() {
        int n = graph.n;
        indices = new int[n];
        lowlinks = new int[n];
        onStack = new boolean[n];
        stack = new Stack<>();
        sccs = new ArrayList<>();
        index = 0;

        Arrays.fill(indices, -1);

        for (int i = 0; i < n; i++) {
            if (indices[i] == -1) {
                strongConnect(i);
            }
        }

        return sccs;
    }

    private void strongConnect(int v) {
        metrics.dfsVisits++;

        indices[v] = index;
        lowlinks[v] = index;
        index++;
        stack.push(v);
        onStack[v] = true;

        for (Edge edge : graph.getNeighbors(v)) {
            metrics.dfsEdges++;
            int w = edge.v;

            if (indices[w] == -1) {
                strongConnect(w);
                lowlinks[v] = Math.min(lowlinks[v], lowlinks[w]);
            } else if (onStack[w]) {
                lowlinks[v] = Math.min(lowlinks[v], indices[w]);
            }
        }

        if (lowlinks[v] == indices[v]) {
            List<Integer> scc = new ArrayList<>();
            int w;
            do {
                w = stack.pop();
                onStack[w] = false;
                scc.add(w);
            } while (w != v);
            sccs.add(scc);
        }
    }

    public List<Integer> getSccSizes() {
        List<Integer> sizes = new ArrayList<>();
        for (List<Integer> scc : sccs) {
            sizes.add(scc.size());
        }
        return sizes;
    }
}