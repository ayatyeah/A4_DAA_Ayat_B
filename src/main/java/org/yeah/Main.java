package org.yeah;

import org.yeah.graph.scc.TarjanSCC;
import org.yeah.graph.scc.CondensationGraph;
import org.yeah.graph.topo.TopologicalSort;
import org.yeah.graph.dagsp.DAGShortestPaths;
import org.yeah.model.Graph;
import org.yeah.utils.JSONReader;
import org.yeah.utils.Metrics;
import org.yeah.utils.GraphGenerator;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println("Starting Smart City Scheduling Analysis...");
            System.out.println();

            // Generate datasets if missing
            if (!datasetsExist()) {
                System.out.println("Generating test datasets...");
                GraphGenerator.generateAllDatasets();
                System.out.println("Datasets generated successfully.");
                System.out.println();
            }

            // Analyze all datasets
            String[] datasets = {"small1", "small2", "small3", "medium1", "medium2", "medium3", "large1", "large2", "large3"};

            for (String dataset : datasets) {
                System.out.println("==================================================");
                System.out.println("Analyzing dataset: " + dataset + ".json");
                System.out.println("==================================================");

                analyzeDataset(dataset);
                System.out.println();
            }

            System.out.println("All datasets analyzed successfully.");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean datasetsExist() {
        String[] datasets = {"small1", "small2", "small3", "medium1", "medium2", "medium3", "large1", "large2", "large3"};
        for (String dataset : datasets) {
            java.io.File file = new java.io.File("data/" + dataset + ".json");
            if (!file.exists()) {
                return false;
            }
        }
        return true;
    }

    private static void analyzeDataset(String datasetName) {
        try {
            // Read graph from file
            Graph graph = JSONReader.readGraph("data/" + datasetName + ".json");
            System.out.println("Graph: " + graph.n + " nodes, " + graph.edges.size() + " edges");

            Metrics metrics = new Metrics();

            // 1. Find SCCs
            metrics.startTimer();
            TarjanSCC tarjan = new TarjanSCC(graph, metrics);
            List<List<Integer>> sccs = tarjan.findSCCs();
            metrics.stopTimer();

            System.out.println("Strongly Connected Components:");
            System.out.println("  SCCs found: " + sccs.size());
            System.out.println("  SCC sizes: " + tarjan.getSccSizes());
            System.out.println("  Time: " + metrics.getElapsedTime() / 1000 + " microseconds");
            System.out.println("  DFS metrics: " + metrics.dfsVisits + " visits, " + metrics.dfsEdges + " edges");

            // 2. Build condensation graph
            CondensationGraph condensation = new CondensationGraph(graph, sccs);
            Graph condGraph = condensation.getCondensationGraph();

            System.out.println("Condensation Graph:");
            System.out.println("  Components: " + condGraph.n);
            System.out.println("  Edges between components: " + condGraph.edges.size());

            // 3. Topological sort
            metrics.reset();
            metrics.startTimer();
            TopologicalSort topo = new TopologicalSort(condGraph, metrics);
            List<Integer> componentOrder = topo.topologicalOrderKahn();
            List<Integer> taskOrder = topo.getOriginalTaskOrder(componentOrder,
                    condensation.getNodeToComponent(), sccs);
            metrics.stopTimer();

            System.out.println("Topological Order:");
            System.out.println("  Component order: " + componentOrder);
            System.out.println("  Task order (first 10): " +
                    (taskOrder.size() > 10 ? taskOrder.subList(0, 10) + "..." : taskOrder));
            System.out.println("  Time: " + metrics.getElapsedTime() / 1000 + " microseconds");
            System.out.println("  Kahn metrics: " + metrics.kahnPushes + " pushes, " + metrics.kahnPops + " pops");

            // 4. Shortest and longest paths - FIXED: choose better source
            if (condGraph.n > 0) {
                metrics.reset();
                metrics.startTimer();
                DAGShortestPaths dagSP = new DAGShortestPaths(condGraph, metrics);

                // Choose source component that has outgoing edges for better demonstration
                int sourceComponent = findGoodSourceComponent(condGraph, componentOrder);

                int[] shortestDist = dagSP.shortestPathsFromSource(sourceComponent, componentOrder);
                int[] longestDist = dagSP.longestPathsFromSource(sourceComponent, componentOrder);
                DAGShortestPaths.CriticalPathResult criticalPath =
                        dagSP.findCriticalPath(sourceComponent, componentOrder);
                metrics.stopTimer();

                System.out.println("Path Analysis (from component " + sourceComponent + "):");
                System.out.println("  Shortest distances: " + formatDistances(shortestDist));
                System.out.println("  Longest distances: " + formatDistances(longestDist));
                System.out.println("  Critical path: " + criticalPath.path);
                System.out.println("  Critical path length: " + criticalPath.length);
                System.out.println("  Time: " + metrics.getElapsedTime() / 1000 + " microseconds");
                System.out.println("  Relaxations: " + metrics.relaxations);
            }

            System.out.println("Analysis completed for " + datasetName);

        } catch (Exception e) {
            System.out.println("Error analyzing " + datasetName + ": " + e.getMessage());
        }
    }

    // Helper method to find a good source component with outgoing edges
    private static int findGoodSourceComponent(Graph condGraph, List<Integer> componentOrder) {
        // Try to find a component that has outgoing edges
        for (int comp : componentOrder) {
            if (!condGraph.getNeighbors(comp).isEmpty()) {
                return comp;
            }
        }
        // If no component has outgoing edges, use the first one
        return 0;
    }

    private static String formatDistances(int[] distances) {
        List<String> formatted = new ArrayList<>();
        for (int i = 0; i < distances.length; i++) {
            if (distances[i] == Integer.MAX_VALUE) {
                formatted.add("INF");
            } else if (distances[i] == Integer.MIN_VALUE) {
                formatted.add("-INF");
            } else {
                formatted.add(String.valueOf(distances[i]));
            }
        }
        return formatted.toString();
    }

    // Method to analyze specific file
    public static void analyzeSpecificFile(String filePath) {
        try {
            System.out.println("Analyzing: " + filePath);
            Graph graph = JSONReader.readGraph(filePath);

            Metrics metrics = new Metrics();

            // SCC
            metrics.startTimer();
            TarjanSCC tarjan = new TarjanSCC(graph, metrics);
            List<List<Integer>> sccs = tarjan.findSCCs();
            metrics.stopTimer();

            System.out.println("SCCs: " + sccs);
            System.out.println("Time: " + metrics.getElapsedTime() + " ns");

            // Condensation
            CondensationGraph condensation = new CondensationGraph(graph, sccs);
            Graph condGraph = condensation.getCondensationGraph();

            // Topological sort
            metrics.reset();
            metrics.startTimer();
            TopologicalSort topo = new TopologicalSort(condGraph, metrics);
            List<Integer> componentOrder = topo.topologicalOrderKahn();
            metrics.stopTimer();

            System.out.println("Topological order: " + componentOrder);

            // Paths
            metrics.reset();
            metrics.startTimer();
            DAGShortestPaths dagSP = new DAGShortestPaths(condGraph, metrics);
            DAGShortestPaths.CriticalPathResult criticalPath =
                    dagSP.findCriticalPath(0, componentOrder);
            metrics.stopTimer();

            System.out.println("Critical path: " + criticalPath.path);
            System.out.println("Critical length: " + criticalPath.length);

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}