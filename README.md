# SCC, Topological Order, and DAG Shortest/Longest Paths

**Student:** Ayat B. (SE-2401)  
**Course:** Design and Analysis of Algorithms

---

## Project Introduction

In this assignment I work with directed graphs for a “Smart City / Smart Campus Scheduling” scenario.  
Some parts of the graph are cyclic (tasks depend on each other), other parts are acyclic.  
The pipeline is:

1) find **Strongly Connected Components (SCC)**,
2) build the **condensation graph** (DAG of components),
3) compute **topological order** of this DAG,
4) run **shortest** and **longest (critical)** paths on the DAG.

I use **edge weights** as the weight model.

---

## Project Structure

A4_DAA_Ayat_B/
├── data/ # 9 generated datasets (small/medium/large)
│ ├── small1.json ... small3.json
│ ├── medium1.json ... medium3.json
│ └── large1.json ... large3.json
├── src/main/java/org/yeah/
│ ├── graph/
│ │ ├── scc/
│ │ │ ├── TarjanSCC.java
│ │ │ └── CondensationGraph.java
│ │ ├── topo/
│ │ │ └── TopologicalSort.java
│ │ └── dagsp/
│ │ └── DAGShortestPaths.java
│ ├── model/
│ │ ├── Graph.java
│ │ └── Edge.java
│ └── utils/
│ ├── Metrics.java
│ ├── JSONReader.java
│ └── GraphGenerator.java
├── src/test/java/org/yeah/
│ ├── SCCTest.java
│ ├── TopologicalSortTest.java
│ └── DAGShortestPathsTest.java
├── src/main/java/org/yeah/Main.java
├── src/main/java/org/yeah/GenerateData.java
└── pom.xml

yaml
Копировать код

---

## How to Run

```bash
# build
mvn clean compile

# run full analysis over all datasets
mvn exec:java -Dexec.mainClass=org.yeah.Main

# (optional) generate datasets again
mvn exec:java -Dexec.mainClass=org.yeah.GenerateData

# run tests
mvn test
Environment used: Java 11, Maven, Jackson for JSON, JUnit 4 for tests.
```

## Algorithms
1) Strongly Connected Components (Tarjan)
Classic recursive DFS with index, lowlink, a stack, and on-stack flags.

Produces list of SCCs and their sizes.

Metrics: dfsVisits, dfsEdges, and time.

2) Condensation Graph
Each SCC becomes a single node.

Parallel edges between same components are removed.

The result is always a DAG.

3) Topological Sort (Kahn)
Queue-based algorithm on the condensation DAG.

Metrics: kahnPushes, kahnPops, and time.

4) DAG Shortest and Longest Paths
Shortest: relax edges in topological order (single-source).

Longest: same idea but maximize instead of minimize.

Also returns critical path (one longest path) and its length.

Weight model: edge weights.

## Datasets
Nine datasets are generated automatically in /data/.
They cover different sizes and densities, both cyclic and acyclic cases.

Name	Nodes	Edges	Type
small1	6	8	cyclic
small2	8	12	DAG
small3	10	15	mixed
medium1	12	18	mixed
medium2	15	25	dense
medium3	18	30	cyclic
large1	20	35	mixed
large2	30	50	mixed
large3	40	70	mixed

## Example Results (from console summary)
These are short samples to demonstrate that pipeline works and metrics are recorded.
Full output in console includes SCC sizes, topo order, shortest distances, longest distances, relaxations, and timings.

### Sample: small1
SCCs found: 3 (sizes [4, 1, 1])

Condensation components: 3

Topological order example: [1, 2, 0]

Critical path example: [1, 0], length 3

### Sample: medium1
SCCs found: 6

Condensation components: 6

Topological order example: [3, 4, 5, 2, 1, 0]

Critical path example: [3, 2, 1, 0], length 9

### Sample: large3
SCCs found: 27

Condensation components: 27

Topological order example: long list (covers all 27)

Critical path example: [14, 13, 8, 7, 6, 5, 4, 2, 0], length 22

(Note: numbers depend on the generated graphs, but structure and steps are the same.)

## Metrics Collected
Stage	Metric names	Meaning
Tarjan (SCC)	dfsVisits, dfsEdges, time	DFS calls and traversed edges
Kahn (Topo)	kahnPushes, kahnPops, time	queue operations
DAG Paths	relaxations, time	number of relaxations on edges

Timing uses System.nanoTime().

## Discussion and Observations
Tarjan SCC runs fast on these sizes; most time is linear in V + E.

Condensation drastically reduces cycles and simplifies scheduling.

Topological sort on the DAG is straightforward and stable.

On the DAG, shortest paths and longest paths are efficient with one pass over topological order.

The longest path identifies a “critical route” in the schedule (bottleneck chain).

In my runs, metrics change with density: more edges → more relaxations and more DFS edges.

## Limitations and Notes
I use edge weight model (not node durations).

Datasets are random; results per file can change if data is regenerated.

In very dense graphs, number of edges increases metrics, but algorithms remain linear in DAG order.

## Testing
mvn test runs three unit tests:

SCCTest checks existence and sizes of SCC on a small graph.

TopologicalSortTest checks that order respects all edges of a small DAG.

DAGShortestPathsTest checks expected distances on a simple DAG.

All tests pass locally.

## Conclusion
This project shows a simple but complete pipeline:

detect SCC to manage cycles,

compress to DAG,

sort topologically,

compute shortest and longest paths.

This approach is practical for task scheduling in graphs with dependencies.
Critical path helps to estimate total duration and to see the main blocking chain.