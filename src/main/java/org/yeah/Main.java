package org.yeah;

import org.yeah.graph.scc.TarjanSCC;
import org.yeah.graph.scc.CondensationGraph;
import org.yeah.graph.topo.TopologicalSort;
import org.yeah.graph.dagsp.DAGShortestPaths;
import org.yeah.model.Graph;
import org.yeah.utils.JSONReader;
import org.yeah.utils.Metrics;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            // Read input graph
            Graph graph = JSONReader.readGraph("data/tasks.json");

            Metrics metrics = new Metrics();

            // 1. Find SCCs
            metrics.startTimer();
            TarjanSCC tarjan = new TarjanSCC(graph, metrics);
            List<List<Integer>> sccs = tarjan.findSCCs();
            metrics.stopTimer();

            System.out.println("=== Strongly Connected Components ===");
            System.out.println("SCCs: " + sccs);
            System.out.println("SCC Sizes: " + tarjan.getSccSizes());
            System.out.println("Time: " + metrics.getElapsedTime() + " ns");
            System.out.println("DFS Visits: " + metrics.dfsVisits);
            System.out.println("DFS Edges: " + metrics.dfsEdges);
            System.out.println();

            // 2. Build condensation graph
            CondensationGraph condensation = new CondensationGraph(graph, sccs);
            Graph condGraph = condensation.getCondensationGraph();

            System.out.println("=== Condensation Graph ===");
            System.out.println("Number of components: " + condGraph.n);
            System.out.println("Edges between components: " + condGraph.edges.size());
            System.out.println();

            // 3. Topological sort
            metrics.reset();
            metrics.startTimer();
            TopologicalSort topo = new TopologicalSort(condGraph, metrics);
            List<Integer> componentOrder = topo.topologicalOrderKahn();
            List<Integer> taskOrder = topo.getOriginalTaskOrder(componentOrder,
                    condensation.getNodeToComponent(), sccs);
            metrics.stopTimer();

            System.out.println("=== Topological Order ===");
            System.out.println("Component Order: " + componentOrder);
            System.out.println("Task Order: " + taskOrder);
            System.out.println("Time: " + metrics.getElapsedTime() + " ns");
            System.out.println("Kahn Pushes: " + metrics.kahnPushes);
            System.out.println("Kahn Pops: " + metrics.kahnPops);
            System.out.println();

            // 4. Shortest and longest paths
            metrics.reset();
            metrics.startTimer();
            DAGShortestPaths dagSP = new DAGShortestPaths(condGraph, metrics);

            int[] shortestDist = dagSP.shortestPathsFromSource(0, componentOrder);
            int[] longestDist = dagSP.longestPathsFromSource(0, componentOrder);
            DAGShortestPaths.CriticalPathResult criticalPath =
                    dagSP.findCriticalPath(0, componentOrder);
            metrics.stopTimer();

            System.out.println("=== Shortest Paths ===");
            System.out.println("Shortest distances from source: " + Arrays.toString(shortestDist));
            System.out.println("Longest distances from source: " + Arrays.toString(longestDist));
            System.out.println("Critical Path: " + criticalPath.path);
            System.out.println("Critical Path Length: " + criticalPath.length);
            System.out.println("Time: " + metrics.getElapsedTime() + " ns");
            System.out.println("Relaxations: " + metrics.relaxations);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}