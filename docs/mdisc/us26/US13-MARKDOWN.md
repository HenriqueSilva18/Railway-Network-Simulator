# User Story 26 – Algorithmic Complexity Analysis

## Objective

As a Product Owner, I want to conclude about the efficiency of the algorithms developed in **US13** and **US14** by analysing their **worst-case time complexity**.

---

## Acceptance Criteria

### AC01

> The analysis report of the worst-case time complexity of the algorithms should be documented/detailed in the repository documentation (using markdown format). All the algorithms should be presented in pseudocode, where the complexity analysis should be made.

### AC02

> The procedure for the **graphic visualization should be excluded** from this analysis.

---

## Algorithms from US13: Train Route Validation (Path Existence)

### Purpose

The purpose of US13 is to determine whether a **specific train type** can travel between two given stations. The implementation relies on **graph traversal**, specifically **Depth-First Search (DFS)**, to verify if a path exists while obeying the train type constraints.

We analyze the logic behind the algorithm, step-by-step, and provide a **trace table**, **pseudocode**, and **complexity analysis**.

---

## 1. `depthFirstSearch(graph, start, end, visited)`

### Pseudocode

```text
function DFS(graph, start, end, visited):
    if start == end:
        return true
    mark start as visited
    for each neighbor in graph[start]:
        if neighbor not in visited:
            if DFS(graph, neighbor, end, visited):
                return true
    return false
```

### Complexity Analysis

Let:

* $n$ be the number of nodes (stations)
* $m$ be the number of edges (railway lines)

**Worst-case Time Complexity**: $\mathcal{O}(n + m)$

> This is because in the worst case, the DFS explores every node and edge once.

---

## 2. `isReachableByTrainType(graph, start, end, trainType)`

### Pseudocode

```text
function isReachableByTrainType(graph, start, end, trainType):
    create filteredGraph based on trainType:
        - for electric, remove non-electrified edges
        - for diesel/steam, keep all edges
    visited := new empty set
    return DFS(filteredGraph, start, end, visited)
```

### Complexity Analysis

* Filtering edges takes $\mathcal{O}(m)$
* DFS takes $\mathcal{O}(n + m)$

**Total Complexity**: $\mathcal{O}(n + m)$

> Dominated by DFS; the filtering operation is linear in edge count and does not alter asymptotic behavior.

---

## 3. Trace Table (DFS Execution Example)

We simulate a DFS traversal for a train type that only allows electrified lines. Assume the graph and path are:

**Graph Example**:

* Nodes: {A, B, C, D}
* Edges: A-B, A-C (electrified), B-D (non-electrified), C-D (electrified)
* Train Type: Electric
* Start: A, End: D

### Step-by-Step DFS Table

| Step | Current Node | Visited Set | Next Nodes  | Action                    |
| ---- | ------------ | ----------- | ----------- | ------------------------- |
| 1    | A            | {A}         | \[B (X), C] | C is electrified → DFS(C) |
| 2    | C            | {A, C}      | \[A, D]     | D is electrified → DFS(D) |
| 3    | D            | {A, C, D}   | \[B (X), C] | Reached destination       |

**Conclusion**: A valid electrified path exists: A → C → D

**Note**: B is skipped since the edge B-D is non-electrified.

### Concepts 

* **DFS** is introduced as a recursive graph traversal
* Uses **adjacency lists** to process neighbors
* Time complexity: $\mathcal{O}(n + m)$
* Recursion depth at most $n$
* **Reachability** is one of its primary applications

---

## 4. Transitive Closure: `computeTransitiveClosure(graph)`

### Pseudocode

```text
function computeTransitiveClosure(matrix):
    for k from 0 to n-1:
        for i from 0 to n-1:
            for j from 0 to n-1:
                if matrix[i][k] and matrix[k][j]:
                    matrix[i][j] = true
```

### Complexity Analysis

* 3 nested loops over $n$ elements

**Time Complexity**: $\mathcal{O}(n^3)$

> Follows the **Warshall-Floyd** method from MDISC. This method computes reachability between all pairs and is typically only used in pre-analysis or validation.

---

## Summary Table for US13

| Algorithm                  | Description                    | Complexity           |
| -------------------------- | ------------------------------ | -------------------- |
| `DFS`                      | Check if destination reachable | $\mathcal{O}(n + m)$ |
| `isReachableByTrainType`   | Pre-filter + DFS traversal     | $\mathcal{O}(n + m)$ |
| `computeTransitiveClosure` | Full reachability matrix       | $\mathcal{O}(n^3)$   |

> Graphical rendering (e.g., GraphStream) is **excluded** from this analysis as per **AC02**.

---

### Conclusion

The algorithms implemented in US13 are efficient for sparse graphs and follow the standard DFS pattern taught in MDISC. Their worst-case complexities are linear for traversal and cubic for matrix-based methods, depending on usage context.
