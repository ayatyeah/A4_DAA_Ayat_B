# Assignment 4 Report - Smart City Scheduling

## About Test Data

We have 9 test graphs:

| Name | Nodes | Edges | Density | Type |
|------|-------|-------|---------|------|
| small1 | 6 | 8 | 26.7% | Cyclic |
| small2 | 8 | 28 | 50.0% | DAG |
| small3 | 10 | 15 | 16.7% | Cyclic |
| medium1 | 12 | 18 | 13.6% | Cyclic |
| medium2 | 12 | 66 | 50.0% | DAG |
| medium3 | 18 | 30 | 9.8% | Cyclic |
| large1 | 20 | 35 | 9.2% | Cyclic |
| large2 | 20 | 190 | 50.0% | DAG |
| large3 | 40 | 70 | 4.5% | Cyclic |

All graphs use edge weights from 1 to 5.

## Results

### SCC Algorithm (Tarjan)

| Dataset | Nodes | Time | DFS Visits | DFS Edges | SCCs |
|---------|-------|------|------------|-----------|------|
| small1 | 6 | 329 μs | 6 | 8 | 2 |
| small2 | 8 | 17 μs | 8 | 28 | 8 |
| small3 | 10 | 15 μs | 10 | 15 | 4 |
| medium1 | 12 | 21 μs | 12 | 18 | 9 |
| medium2 | 12 | 48 μs | 12 | 66 | 12 |
| medium3 | 18 | 31 μs | 18 | 30 | 9 |
| large1 | 20 | 27 μs | 20 | 35 | 10 |
| large2 | 20 | 65 μs | 20 | 190 | 20 |
| large3 | 40 | 69 μs | 40 | 70 | 30 |

### Topological Sort (Kahn)

| Dataset | Components | Time | Pushes | Pops |
|---------|------------|------|--------|------|
| small1 | 2 | 371 μs | 2 | 2 |
| small2 | 8 | 19 μs | 8 | 8 |
| small3 | 4 | 16 μs | 4 | 4 |
| medium1 | 9 | 17 μs | 9 | 9 |
| medium2 | 12 | 26 μs | 12 | 12 |
| medium3 | 9 | 20 μs | 9 | 9 |
| large1 | 10 | 23 μs | 10 | 10 |
| large2 | 20 | 52 μs | 20 | 20 |
| large3 | 30 | 53 μs | 30 | 30 |

### Path Algorithms

| Dataset | Components | Time | Relaxations | Longest Path |
|---------|------------|------|-------------|--------------|
| small1 | 2 | 511 μs | 2 | 4 |
| small2 | 8 | 19 μs | 56 | 17 |
| small3 | 4 | 9 μs | 2 | 3 |
| medium1 | 9 | 13 μs | 18 | 13 |
| medium2 | 12 | 29 μs | 132 | 24 |
| medium3 | 9 | 19 μs | 24 | 17 |
| large1 | 10 | 13 μs | 8 | 12 |
| large2 | 20 | 59 μs | 380 | 46 |
| large3 | 30 | 34 μs | 40 | 22 |

## What We See

### SCC Algorithm
- Work fast on all graphs
- More edges = more DFS work
- DAG graphs have many small SCCs (each node separate)
- Cyclic graphs have some big SCCs

### Topological Sort
- Very fast algorithm
- Time depend on number of components
- Work only on DAG graphs

### Path Algorithms
- Take more time on dense graphs
- Many relaxations on graphs with many edges
- Longest path bigger on dense graphs

## What We Learn

### When Use Each Algorithm

**SCC (Tarjan)**:
- Use when have cycles in graph
- Good for find connected parts
- Work on all graph types

**Topological Sort (Kahn)**:
- Use for put tasks in order
- Need DAG graph (no cycles)
- Very fast

**Path Algorithms**:
- Use for find important tasks
- Find shortest and longest paths
- Good for scheduling

### About Graph Structure

**Dense graphs** (many edges):
- More work for algorithms
- Longer paths possible
- More connections between nodes

**Sparse graphs** (few edges):
- Faster algorithms
- Some nodes not connected
- Shorter paths

**Cyclic graphs**:
- Need SCC first
- Have repeated dependencies
- Hard for scheduling

**DAG graphs** (no cycles):
- Can use topological sort directly
- Easy to schedule tasks
- Clear task order

## For Smart City

This algorithms can help for:
- Plan city services schedule
- Find which tasks most important
- Avoid task conflicts
- Make better time planning

## Final Thoughts

- Different graphs need different algorithms
- SCC good for find cycles
- Topological sort good for task order
- Path algorithms good for find critical tasks
- Graph density affect algorithm speed

---
*Report from testing on 9 different graphs*