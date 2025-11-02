package org.yeah.utils;

import org.yeah.model.Graph;
import org.yeah.model.Edge;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class GraphGenerator {
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Random random = new Random();

    public static void main(String[] args) {
        generateAllDatasets();
        System.out.println("✅ All datasets generated successfully!");
    }

    public static void generateAllDatasets() {
        // Small graphs (6-10 nodes)
        generateDataset("small1", 6, 8, true);
        generateDataset("small2", 8, 12, false);
        generateDataset("small3", 10, 15, true);

        // Medium graphs (10-20 nodes)
        generateDataset("medium1", 12, 18, true);
        generateDataset("medium2", 15, 25, false);
        generateDataset("medium3", 18, 30, true);

        // Large graphs (20-50 nodes)
        generateDataset("large1", 20, 35, true);
        generateDataset("large2", 30, 50, false);
        generateDataset("large3", 40, 70, true);
    }

    public static void generateDataset(String name, int nodes, int edges, boolean includeCycles) {
        Graph graph = createBaseGraph(nodes);
        Set<String> existingEdges = new HashSet<>();

        // Ensure connectivity for DAG parts
        if (nodes > 1) {
            // Create a base DAG structure
            for (int i = 0; i < nodes - 1; i++) {
                int j = i + 1 + random.nextInt(nodes - i - 1);
                if (j < nodes) {
                    addEdge(graph, existingEdges, i, j, 1 + random.nextInt(5));
                }
            }
        }

        // Add cycles if requested
        if (includeCycles && nodes >= 3) {
            int cycles = 1 + random.nextInt(2);
            for (int c = 0; c < cycles; c++) {
                createCycle(graph, existingEdges, nodes);
            }
        }

        // Fill remaining edges randomly
        while (graph.edges.size() < edges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);

            // Avoid self-loops for simplicity
            if (u != v && random.nextDouble() > 0.1) {
                addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
            }
        }

        // Save to file
        saveGraphToFile(graph, name, nodes);
    }

    private static Graph createBaseGraph(int nodes) {
        Graph graph = new Graph();
        graph.n = nodes;
        // directed field removed - it's always true for our case
        graph.weightModel = "edge";
        graph.source = 0;
        graph.edges = new ArrayList<>();
        return graph;
    }

    private static void saveGraphToFile(Graph graph, String name, int nodes) {
        try {
            String filePath = "data/" + name + ".json";
            mapper.writerWithDefaultPrettyPrinter().writeValue(new File(filePath), graph);
            System.out.println("Generated: " + filePath + " (nodes: " + nodes + ", edges: " + graph.edges.size() + ")");
        } catch (IOException e) {
            System.err.println("Error saving " + name + ": " + e.getMessage());
        }
    }

    private static void addEdge(Graph graph, Set<String> existingEdges, int u, int v, int weight) {
        String edgeKey = u + "->" + v;
        if (!existingEdges.contains(edgeKey) && graph.edges.size() < graph.n * 2) {
            graph.edges.add(new Edge(u, v, weight));
            existingEdges.add(edgeKey);
        }
    }

    private static void createCycle(Graph graph, Set<String> existingEdges, int nodes) {
        if (nodes < 3) return;

        // Create a cycle of 3-4 nodes
        int cycleSize = 3 + random.nextInt(2);
        Set<Integer> selected = new HashSet<>();

        // Select random nodes for cycle
        while (selected.size() < cycleSize) {
            selected.add(random.nextInt(nodes));
        }

        List<Integer> cycleNodes = new ArrayList<>(selected); // Fixed: replaced addAll with constructor

        // Create cycle edges
        for (int i = 0; i < cycleSize; i++) {
            int u = cycleNodes.get(i);
            int v = cycleNodes.get((i + 1) % cycleSize);
            addEdge(graph, existingEdges, u, v, 1 + random.nextInt(3));
        }
    }

    // Method to generate specific types of graphs for testing
    public static Graph generatePureDAG(int nodes, int edges) {
        Graph graph = createBaseGraph(nodes);
        Set<String> existingEdges = new HashSet<>();

        // Create a topological order
        List<Integer> order = new ArrayList<>();
        for (int i = 0; i < nodes; i++) order.add(i);
        Collections.shuffle(order);

        // Add edges only from lower to higher indices in shuffled order
        for (int i = 0; i < edges; i++) {
            int uIdx = random.nextInt(nodes - 1);
            int vIdx = uIdx + 1 + random.nextInt(nodes - uIdx - 1);

            if (vIdx < nodes) {
                int u = order.get(uIdx);
                int v = order.get(vIdx);
                addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
            }
        }

        return graph;
    }

    public static Graph generateCyclicGraph(int nodes, int edges, int cycles) {
        Graph graph = createBaseGraph(nodes);
        Set<String> existingEdges = new HashSet<>();

        // Create specified number of cycles
        for (int c = 0; c < cycles; c++) {
            createCycle(graph, existingEdges, nodes);
        }

        // Add remaining edges randomly
        while (graph.edges.size() < edges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);
            if (u != v) {
                addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
            }
        }

        return graph;
    }
}