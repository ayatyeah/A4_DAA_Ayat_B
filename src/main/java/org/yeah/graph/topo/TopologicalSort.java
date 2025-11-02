package org.yeah.graph.topo;

import org.yeah.model.Graph;
import org.yeah.model.Edge;
import org.yeah.utils.Metrics;
import java.util.*;

public class TopologicalSort {
    private Graph graph;
    private Metrics metrics;

    public TopologicalSort(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public List<Integer> topologicalOrderKahn() {
        int n = graph.n;
        int[] inDegree = new int[n];

        // Calculate in-degrees
        for (Edge edge : graph.edges) {
            inDegree[edge.v]++;
        }

        Queue<Integer> queue = new LinkedList<>();
        for (int i = 0; i < n; i++) {
            if (inDegree[i] == 0) {
                queue.offer(i);
                metrics.kahnPushes++;
            }
        }

        List<Integer> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            int node = queue.poll();
            metrics.kahnPops++;
            result.add(node);

            for (Edge edge : graph.getNeighbors(node)) {
                inDegree[edge.v]--;
                if (inDegree[edge.v] == 0) {
                    queue.offer(edge.v);
                    metrics.kahnPushes++;
                }
            }
        }

        return result;
    }

    public List<Integer> getOriginalTaskOrder(List<Integer> componentOrder,
                                              Map<Integer, Integer> nodeToComponent,
                                              List<List<Integer>> sccs) {
        List<Integer> taskOrder = new ArrayList<>();

        for (int comp : componentOrder) {
            taskOrder.addAll(sccs.get(comp));
        }

        return taskOrder;
    }
}