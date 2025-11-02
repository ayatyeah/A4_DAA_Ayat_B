package org.yeah.utils;

public class Metrics {
    private long startTime;
    private long endTime;

    // Counters for different algorithms
    public int dfsVisits;
    public int dfsEdges;
    public int kahnPops;
    public int kahnPushes;
    public int relaxations;

    public void startTimer() {
        startTime = System.nanoTime();
    }

    public void stopTimer() {
        endTime = System.nanoTime();
    }

    public long getElapsedTime() {
        return endTime - startTime;
    }

    public void reset() {
        dfsVisits = 0;
        dfsEdges = 0;
        kahnPops = 0;
        kahnPushes = 0;
        relaxations = 0;
    }
}