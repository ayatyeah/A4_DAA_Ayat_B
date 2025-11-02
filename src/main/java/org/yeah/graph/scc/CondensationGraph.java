package org.yeah.graph.scc;

import org.yeah.model.Graph;
import org.yeah.model.Edge;
import java.util.*;

public class CondensationGraph {
    private Graph originalGraph;
    private List<List<Integer>> sccs;
    private Map<Integer, Integer> nodeToComponent;
    private Graph condensationGraph;

    public CondensationGraph(Graph originalGraph, List<List<Integer>> sccs) {
        this.originalGraph = originalGraph;
        this.sccs = sccs;
        this.nodeToComponent = new HashMap<>();
        buildNodeToComponentMap();
        buildCondensationGraph();
    }

    private void buildNodeToComponentMap() {
        for (int i = 0; i < sccs.size(); i++) {
            for (int node : sccs.get(i)) {
                nodeToComponent.put(node, i);
            }
        }
    }

    private void buildCondensationGraph() {
        condensationGraph = new Graph();
        condensationGraph.n = sccs.size();
        condensationGraph.weightModel = "edge";

        Set<String> addedEdges = new HashSet<>();

        for (Edge edge : originalGraph.edges) {
            int compU = nodeToComponent.get(edge.u);
            int compV = nodeToComponent.get(edge.v);

            if (compU != compV) {
                String edgeKey = compU + "->" + compV;
                if (!addedEdges.contains(edgeKey)) {
                    condensationGraph.edges.add(new Edge(compU, compV, edge.w));
                    addedEdges.add(edgeKey);
                }
            }
        }

        condensationGraph.buildGraph();
    }

    public Graph getCondensationGraph() {
        return condensationGraph;
    }

    public Map<Integer, Integer> getNodeToComponent() {
        return nodeToComponent;
    }
}