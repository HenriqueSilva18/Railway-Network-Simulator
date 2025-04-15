# US13 – Train Connectivity: Algorithm and Implementation Documentation

## Purpose

This module solves the problem of determining whether a train (steam, diesel, or electric) can travel between two stations in a railway network, based on connectivity and infrastructure constraints (station types, line electrification). It also allows checking the number of walks of a certain length between stations and computes the transitive closure to determine if the graph is connected.

---

## Functional Features

1. **Train route feasibility check**:
    - Uses recursive **Depth-First Search (DFS)**.
    - Restricts traversal based on:
        - **Train type**: Electric trains require electrified lines.
        - **Station type**: depot, station, terminal
        - OR: Accepts `"any"` to allow mixed station types (flexible traversal)

2. **Graph visualization**:
    - Exports `.dot` file for use with Graphviz.
    - Electrified lines drawn in **blue**, non-electrified in **black**.
    - Station names and distances are labeled.

3. **Matrix-based algorithms**:
    - Computes number of **walks of length _k_** via powers of the adjacency matrix `M^k`.
    - Computes **transitive closure** using Boolean matrix summation to verify full graph connectivity.

---

## Algorithms

### 1. DFS for Route Checking

```java
private static boolean dfs(String current, String end, TrainType trainType, String stationType, Set<String> visited)
```

- Checks for a valid route:
    - Applies electrification constraint for electric trains.
    - Applies station-type constraint unless `"any"` is specified.
- Fully implemented using primitive recursion and no libraries.

---

### 2. Matrix Exponentiation for Walk Count

```java
public static int[][] computeWalksMatrix(int power)
```

- Builds adjacency matrix from the graph.
- Uses integer matrix multiplication to compute `M^k`.
- Entry `(i,j)` gives number of walks of length `k` from station `i` to `j`.

---

### 3. Boolean Transitive Closure

```java
public static boolean[][] computeTransitiveClosure()
```

- Combines all matrix powers using Boolean OR logic.
- Fully connected graph has **no zero entries** in result matrix.

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
#  US13 – Test Case: Electric Train Connectivity & Matrix Walk Count

## Test Objective

Verify whether an **electric train** can travel between two stations using only **electrified lines** and **stations of type `"station"`**, and compute:

- Number of walks of a specific length between those stations
- The full walk count matrix (`M^k`)
- Transitive closure of the graph

---

## Test Case

###  Input

| Parameter       | inputs                    |
|----------------|---------------------------|
| **Start**       | Campanha Station          |
| **End**         | Vila Nova de Gaia Station |
| **Train Type**  | ELECTRIC                  |
| **Station Type**| station                   |
| **Walk Length** | 4                         |

---

### Program Interaction (example)

```text
Loading railway data from data/porto_railways.csv...
Enter start station:
Campanha Station

Enter end station:
Vila Nova de Gaia Station

Enter train type (STEAM, DIESEL, ELECTRIC):
ELECTRIC

Enter station type (depot, station, terminal):
station

Train can travel!
Exporting network to dot/network.dot
Export complete.

Enter path length to compute walk count (e.g., 4):
4

Number of walks of length 4 from Campanha Station to Vila Nova de Gaia Station: 21

Walk count matrix M^4:
20 15 21 14 10 17 
15 20 21 17 10 14 
21 21 30 15 16 15 
14 17 15 19 5 17 
10 10 16 5 10 5 
17 14 15 17 5 19 

Computing transitive closure (Boolean reachability)...
1 1 1 1 1 1 
1 1 1 1 1 1 
1 1 1 1 1 1 
1 1 1 1 1 1 
1 1 1 1 1 1 
1 1 1 1 1 1 
```
---

##  US13 – Train Connectivity - (some notes)
See implementation under `src/com/mdisc/us13/`  
DOT file in `/dot/network.dot`, CSV in `/data/`, algorithm in `/docs/us13/`

**Implemented by student**: Yasamin Ebrahimi (1232162) 