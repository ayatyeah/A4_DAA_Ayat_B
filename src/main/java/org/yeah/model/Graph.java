package org.yeah.model;

import java.util.*;

public class Graph {
    public int n;
    public List<Edge> edges;
    public int source;
    public String weightModel;

    private List<List<Edge>> adjList;

    public Graph() {
        edges = new ArrayList<>();
    }

    public void buildGraph() {
        adjList = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            adjList.add(new ArrayList<>());
        }

        for (Edge edge : edges) {
            adjList.get(edge.u).add(edge);
        }
    }

    public List<Edge> getNeighbors(int node) {
        return adjList.get(node);
    }

    public List<List<Edge>> getAdjList() {
        return adjList;
    }
}