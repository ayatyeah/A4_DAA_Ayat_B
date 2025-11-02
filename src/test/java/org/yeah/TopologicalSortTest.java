package org.yeah;

import org.yeah.graph.topo.TopologicalSort;
import org.yeah.model.Graph;
import org.yeah.model.Edge;
import org.yeah.utils.Metrics;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class TopologicalSortTest {

    @Test
    public void testTopologicalSortDAG() {
        Graph graph = new Graph();
        graph.n = 4;
        graph.edges.add(new Edge(0, 1, 1));
        graph.edges.add(new Edge(0, 2, 1));
        graph.edges.add(new Edge(1, 3, 1));
        graph.edges.add(new Edge(2, 3, 1));
        graph.buildGraph();

        Metrics metrics = new Metrics();
        TopologicalSort topo = new TopologicalSort(graph, metrics);
        List<Integer> order = topo.topologicalOrderKahn();

        assertEquals(4, order.size());
        // Check that for every edge u->v, u comes before v in ordering
        assertTrue(order.indexOf(0) < order.indexOf(1));
        assertTrue(order.indexOf(0) < order.indexOf(2));
        assertTrue(order.indexOf(1) < order.indexOf(3));
        assertTrue(order.indexOf(2) < order.indexOf(3));
    }
}