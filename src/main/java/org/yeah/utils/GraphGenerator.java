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
        System.out.println("All datasets generated successfully!");
    }

    public static void generateAllDatasets() {
        // Small graphs - mixed density and types
        generateDataset("small1", 6, 8, true);           // sparse cyclic
        generateDenseDataset("small2", 8, 20, false);    // DENSE acyclic
        generateDataset("small3", 10, 15, true);         // sparse cyclic

        // Medium graphs - mixed density and types
        generateDataset("medium1", 12, 18, true);        // sparse cyclic
        generateDenseDataset("medium2", 15, 45, false);  // DENSE acyclic
        generateDataset("medium3", 18, 30, true);        // sparse cyclic

        // Large graphs - mixed density and types
        generateDataset("large1", 20, 35, true);         // sparse cyclic
        generateDenseDataset("large2", 30, 120, false);  // DENSE acyclic
        generateDataset("large3", 40, 70, true);         // sparse cyclic
    }

    public static void generateDataset(String name, int nodes, int targetEdges, boolean includeCycles) {
        Graph graph = createBaseGraph(nodes);
        Set<String> existingEdges = new HashSet<>();

        System.out.println("Generating " + name + ": " + nodes + " nodes, target: " + targetEdges + " edges");

        // Phase 1: Create base structure
        if (nodes > 1) {
            // Create a connected base structure
            for (int i = 0; i < nodes - 1; i++) {
                int j = i + 1 + random.nextInt(nodes - i - 1);
                if (j < nodes) {
                    addEdge(graph, existingEdges, i, j, 1 + random.nextInt(5));
                }
            }
        }

        // Phase 2: Add cycles if requested
        if (includeCycles && nodes >= 3) {
            int cycles = 1 + random.nextInt(2);
            for (int c = 0; c < cycles; c++) {
                createCycle(graph, existingEdges, nodes);
            }
        }

        // Phase 3: Aggressive edge addition to reach target
        int maxPossibleEdges = nodes * (nodes - 1); // maximum possible edges in directed graph
        int actualTarget = Math.min(targetEdges, maxPossibleEdges);

        System.out.println("  Current edges: " + graph.edges.size() + ", target: " + actualTarget);

        // Add edges more aggressively
        int attempts = 0;
        while (graph.edges.size() < actualTarget && attempts < maxPossibleEdges * 3) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);

            if (u != v) {
                // For dense graphs, accept most edges
                if (random.nextDouble() < 0.8) {
                    addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
                }
            }
            attempts++;

            // Early exit if we're close to target
            if (graph.edges.size() >= actualTarget) break;
        }

        // Final attempt: if still not enough edges, try systematic approach
        if (graph.edges.size() < actualTarget) {
            for (int u = 0; u < nodes && graph.edges.size() < actualTarget; u++) {
                for (int v = 0; v < nodes && graph.edges.size() < actualTarget; v++) {
                    if (u != v) {
                        addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
                    }
                }
            }
        }

        System.out.println("  Final edges: " + graph.edges.size() + ", density: " +
                String.format("%.1f%%", (graph.edges.size() * 100.0) / maxPossibleEdges));

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
        if (!existingEdges.contains(edgeKey)) {
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

    public static void generateDenseDataset(String name, int nodes, int targetEdges, boolean includeCycles) {
        Graph graph = createBaseGraph(nodes);
        Set<String> existingEdges = new HashSet<>();

        System.out.println("Generating DENSE " + name + ": " + nodes + " nodes, target: " + targetEdges + " edges");

        // Phase 1: Create systematic edges to ensure density
        int edgesAdded = 0;
        for (int u = 0; u < nodes && edgesAdded < targetEdges; u++) {
            for (int v = 0; v < nodes && edgesAdded < targetEdges; v++) {
                if (u != v) {
                    // For dense graphs, add edge with high probability
                    if (random.nextDouble() < 0.6) { // 60% chance for each possible edge
                        addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
                        edgesAdded = graph.edges.size();
                    }
                }
            }
        }

        // Phase 2: If still not enough, add random edges aggressively
        while (graph.edges.size() < targetEdges) {
            int u = random.nextInt(nodes);
            int v = random.nextInt(nodes);
            if (u != v) {
                addEdge(graph, existingEdges, u, v, 1 + random.nextInt(5));
            }

            // Safety break
            if (graph.edges.size() >= nodes * (nodes - 1) * 0.8) break;
        }

        // Phase 3: Add cycles if requested
        if (includeCycles && nodes >= 3) {
            int cycles = 1 + random.nextInt(2);
            for (int c = 0; c < cycles; c++) {
                createCycle(graph, existingEdges, nodes);
            }
        }

        int maxPossible = nodes * (nodes - 1);
        double density = (graph.edges.size() * 100.0) / maxPossible;
        System.out.println("  Final: " + graph.edges.size() + " edges, density: " + String.format("%.1f%%", density));

        saveGraphToFile(graph, name, nodes);
    }
}