package org.yeah;

import org.yeah.graph.scc.TarjanSCC;
import org.yeah.model.Graph;
import org.yeah.model.Edge;
import org.yeah.utils.Metrics;
import org.junit.Test;
import java.util.List;
import static org.junit.Assert.*;

public class SCCTest {

    @Test
    public void testSCCSmallGraph() {
        Graph graph = new Graph();
        graph.n = 4;
        graph.edges.add(new Edge(0, 1, 1));
        graph.edges.add(new Edge(1, 2, 1));
        graph.edges.add(new Edge(2, 0, 1));
        graph.edges.add(new Edge(1, 3, 1));

        Metrics metrics = new Metrics();
        TarjanSCC tarjan = new TarjanSCC(graph, metrics);
        List<List<Integer>> sccs = tarjan.findSCCs();

        assertEquals(2, sccs.size());
        // Should have one SCC with 3 nodes and one with 1 node
        boolean foundSize3 = false, foundSize1 = false;
        for (List<Integer> scc : sccs) {
            if (scc.size() == 3) foundSize3 = true;
            if (scc.size() == 1) foundSize1 = true;
        }
        assertTrue(foundSize3 && foundSize1);
    }
}