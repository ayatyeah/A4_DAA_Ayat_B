package org.yeah;

import org.yeah.graph.dagsp.DAGShortestPaths;
import org.yeah.model.Graph;
import org.yeah.model.Edge;
import org.yeah.utils.Metrics;
import org.junit.Test;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;

public class DAGShortestPathsTest {

    @Test
    public void testShortestPath() {
        Graph graph = new Graph();
        graph.n = 4;
        graph.edges.add(new Edge(0, 1, 1));
        graph.edges.add(new Edge(0, 2, 4));
        graph.edges.add(new Edge(1, 2, 2));
        graph.edges.add(new Edge(1, 3, 6));
        graph.edges.add(new Edge(2, 3, 3));
        graph.buildGraph();

        Metrics metrics = new Metrics();
        DAGShortestPaths dagSP = new DAGShortestPaths(graph, metrics);

        List<Integer> topoOrder = Arrays.asList(0, 1, 2, 3);
        int[] dist = dagSP.shortestPathsFromSource(0, topoOrder);

        assertEquals(0, dist[0]);
        assertEquals(1, dist[1]);
        assertEquals(3, dist[2]);
        assertEquals(6, dist[3]);
    }
}