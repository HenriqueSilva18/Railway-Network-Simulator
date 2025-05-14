# US13 – Train Connectivity: Algorithm and Implementation Documentation

## Purpose

This module solves the problem of determining whether a train (steam, diesel, or electric) can travel between two stations in a railway network, based on connectivity and infrastructure constraints (station types, line electrification). It also allows checking the number of walks of a certain length between stations and computes the transitive closure to determine if the graph is connected.

---

## Functional Features

1. **Train route feasibility check**:

    * Uses recursive **Depth-First Search (DFS)**.
    * Restricts traversal based on:

        * **Train type**: Electric trains require electrified lines.
        * **Station type**: depot, station, terminal
        * OR: Accepts `"any"` to allow mixed station types (flexible traversal)
    * **NEW**: Accepts `"any"` for **start or end station**, allowing route checking from/to all stations

2. **Graph visualization** (via **GraphStream**):

    * Displays nodes (stations) with:

        * **Red** for depots
        * **Green** for terminals
        * **Blue** for stations
    * Node labels show station names
    * Edges are labeled with distances and indicate electrified lines visually
    * **NEW**: The full network is always displayed regardless of feasibility, to support connectivity analysis

3. **Matrix-based algorithms**:

    * Computes number of **walks of length *k*** via powers of the adjacency matrix `M^k`.
    * Computes **transitive closure** using Boolean matrix summation to verify full graph connectivity.

---

## Additional Features for Evaluation Requirements

* Program handles user input of `'any'` for:

    * Start station
    * End station
    * Station type

* Output lists all valid origin or destination matches based on selected filters

* Valid with all scenario `.csv` files using semicolon (;) separators

* Matrix and graph analysis performed regardless of input feasibility

---

## Algorithms

### 1. DFS for Route Checking

```java
private static boolean dfs(String current, String end, TrainType trainType, String stationType, Set<String> visited)
```

* Checks for a valid route:

    * Applies electrification constraint for electric trains.
    * Applies station-type constraint unless `"any"` is specified.
* Fully implemented using primitive recursion and no libraries.

---

### 2. Matrix Exponentiation for Walk Count

```java
public static int[][] computeWalksMatrix(int power)
```

* Builds adjacency matrix from the graph.
* Uses integer matrix multiplication to compute `M^k`.
* Entry `(i,j)` gives number of walks of length `k` from station `i` to `j`.

---

### 3. Boolean Transitive Closure

```java
public static boolean[][] computeTransitiveClosure()
```

* Combines all matrix powers using Boolean OR logic.
* Fully connected graph has **no zero entries** in result matrix (Corollary 4.5).

---

## Theoretical Foundations and Pseudocode

### DFS Traversal (Train Route Feasibility)

**Methodology:** Constrained DFS traversal from source to destination.

```pseudo
function canTravel(start, end, trainType, stationType):
    visited = empty set
    return dfs(start, end, trainType, stationType, visited)

function dfs(current, end, trainType, stationType, visited):
    if current or end not in station list:
        return false
    if stationType != "any" and current station type != stationType:
        return false
    if current == end:
        return true
    mark current as visited

    for neighbor in graph[current]:
        if neighbor not visited:
            if trainType == ELECTRIC and edge to neighbor is not electrified:
                continue
            if stationType != "any" and neighbor station type != stationType:
                continue
            if dfs(neighbor, end, trainType, stationType, visited):
                return true
    return false
```

---

### Matrix Walks – `M^k`

```pseudo
function computeWalksMatrix(k):
    M = adjacencyMatrix(graph)
    return matrixPower(M, k)

function matrixPower(M, k):
    result = identity matrix
    while k > 0:
        if k is odd:
            result = multiply(result, M)
        M = multiply(M, M)
        k = k // 2
    return result
```

---

### Transitive Closure

```pseudo
function computeTransitiveClosure():
    M = adjacencyMatrix(graph)
    F = zero matrix (boolean)
    for k = 1 to n:
        Mk = matrixPower(M, k)
        for all i, j:
            F[i][j] = F[i][j] OR (Mk[i][j] > 0)
    return F
```

---


# US13 – Test Cases (Scenario 1)

### Test Case 1 – Diesel Train from Any Station to Any Other

| Parameter       | Value                        |
|----------------|------------------------------|
| Start Station  | any                          |
| End Station    | any                          |
| Train Type     | DIESEL                       |
| Station Type   | any                          |


**Expected Output:**
```
- Valid paths shown between all station types
- Full network connectivity shown on graph
- Walk matrix and transitive closure confirm reachability
```

---

### Test Case 2 – Electric Train from Station or Terminal to Another Station or Terminal

| Parameter       | Value                        |
|----------------|------------------------------|
| Start Station  | any                          |
| End Station    | any                          |
| Train Type     | ELECTRIC                     |
| Station Type   | station+terminal             |


**Expected Output:**
```
- Shows only electric routes
- Respects station and terminal types
- Network of eligible connections displayed
```

---

### Test Case 3 – Electric Train from Terminal to Terminal

| Parameter       | Value                        |
|----------------|------------------------------|
| Start Station  | any                          |
| End Station    | T_Edinburgh                  |
| Train Type     | ELECTRIC                     |
| Station Type   | terminal                     |


**Expected Output:**
```
- Confirms all terminals that can reach T_Edinburgh
- Electrified connections visualized
- Transitive closure matrix verifies terminal connectivity
```

---

**Implemented by student**: Yasamin Ebrahimi (1232162)
