package org.yeah.graph.dagsp;

import org.yeah.model.Graph;
import org.yeah.utils.Metrics;
import org.yeah.model.Edge;
import java.util.*;

public class DAGShortestPaths {
    private Graph graph;
    private Metrics metrics;

    public DAGShortestPaths(Graph graph, Metrics metrics) {
        this.graph = graph;
        this.metrics = metrics;
    }

    public int[] shortestPathsFromSource(int source, List<Integer> topologicalOrder) {
        int n = graph.n;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MAX_VALUE);
        dist[source] = 0;

        // Follow topological order
        for (int node : topologicalOrder) {
            if (dist[node] != Integer.MAX_VALUE) {
                for (Edge edge : graph.getNeighbors(node)) {
                    metrics.relaxations++;
                    if (dist[edge.v] > dist[node] + edge.w) {
                        dist[edge.v] = dist[node] + edge.w;
                    }
                }
            }
        }

        return dist;
    }

    public int[] longestPathsFromSource(int source, List<Integer> topologicalOrder) {
        int n = graph.n;
        int[] dist = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        dist[source] = 0;

        // For longest path, we can invert weights and find shortest path
        for (int node : topologicalOrder) {
            if (dist[node] != Integer.MIN_VALUE) {
                for (Edge edge : graph.getNeighbors(node)) {
                    metrics.relaxations++;
                    if (dist[edge.v] < dist[node] + edge.w) {
                        dist[edge.v] = dist[node] + edge.w;
                    }
                }
            }
        }

        return dist;
    }

    public CriticalPathResult findCriticalPath(int source, List<Integer> topologicalOrder) {
        int n = graph.n;
        int[] dist = new int[n];
        int[] prev = new int[n];
        Arrays.fill(dist, Integer.MIN_VALUE);
        Arrays.fill(prev, -1);
        dist[source] = 0;

        for (int node : topologicalOrder) {
            if (dist[node] != Integer.MIN_VALUE) {
                for (Edge edge : graph.getNeighbors(node)) {
                    if (dist[edge.v] < dist[node] + edge.w) {
                        dist[edge.v] = dist[node] + edge.w;
                        prev[edge.v] = node;
                    }
                }
            }
        }

        // Find node with maximum distance
        int maxDist = Integer.MIN_VALUE;
        int endNode = -1;
        for (int i = 0; i < n; i++) {
            if (dist[i] > maxDist) {
                maxDist = dist[i];
                endNode = i;
            }
        }

        // Reconstruct path
        List<Integer> path = reconstructPath(prev, endNode);

        return new CriticalPathResult(path, maxDist);
    }

    private List<Integer> reconstructPath(int[] prev, int endNode) {
        List<Integer> path = new ArrayList<>();
        for (int at = endNode; at != -1; at = prev[at]) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    public static class CriticalPathResult {
        public List<Integer> path;
        public int length;

        public CriticalPathResult(List<Integer> path, int length) {
            this.path = path;
            this.length = length;
        }
    }
}