## Detailed Explanation of Algorithms for Train Connectivity (US13)

The US13 implementation focuses on train connectivity within a railway network, offering functionalities ranging from simple path existence checks to more complex network analysis. This is achieved through a combination of graph traversal and matrix-based algorithms, all grounded in the principles of Discrete Mathematics, particularly graph theory.

-----

- $n$ (Number of Nodes/Vertices): Represents the total number of nodes (stations) in the graph. Algorithms processing each station or operations dependent on the number of stations will relate to $n$.
- $m$ (Number of Edges): Signifies the total number of edges (connections) in the graph. Algorithms iterating through connections or related to overall connectivity will relate to $m$.
- $A$ (Assignment/Arithmetic Operation): A basic assignment (`:=`) or simple arithmetic operation (e.g., `+`, `-`). These are constant-time operations.
- $C$ (Comparison/Conditional Check): A comparison operation (e.g., `==`, `!=`, `&gt;`) or a conditional check (`if`, `while`). Loop conditions also fall here.
- $L$ (Lookup/Access Operation): Retrieving a value from a data structure (e.g., accessing an element in a list by index or a map by key). These are generally constant-time for efficient data structures like hash maps or arrays.
- $Op$ (General Operation): A general operation not fitting specific categories, but which is constant-time (e.g., a `return` statement, a `break` statement, or a display action if it's a fixed, small output).
- $R$ (Return Statement): Specifically refers to the return statement of a function or procedure.


### 1\. `DFS(graph, start, end, visited)`

This function represents the fundamental **Depth-First Search (DFS)** algorithm.

* **Purpose:** The primary purpose of `DFS` is to systematically explore a graph to determine if a path exists from a specified `start` node (station) to an `end` node (station). It's designed to go as "deep" as possible along each branch before backtracking.
* **Methodology (How it works):**
    1.  **Base Case (Line 1):** The algorithm first checks if the `current` node (`start` in the context of the pseudocode's parameters) is the `end` node. If they are the same, it means the destination has been reached, and a path exists, so it immediately returns `true`.
    2.  **Marking Visited (Line 2):** To prevent infinite loops in graphs with cycles and to ensure that each node is processed efficiently, the `current` node is marked as `visited`. This is typically done by adding the node to a `visited` set.
    3.  **Exploring Neighbors (Line 3):** The algorithm then iterates through every `neighbor` directly connected to the `current` node. For each `neighbor`, it checks if it has already been `visited` (Line 4).
    4.  **Call (Line 5):** If a `neighbor` has not been visited, the `DFS` function calls itself with that `neighbor` as the new `start` node. If the call finds the `end` node (i.e., returns `true`), that `true` value is propagated all the way up the call stack, indicating a path was found.
    5.  **Backtracking (Line 6):** If the loop completes and no unvisited neighbor leads to the `end` node, it means a path doesn't exist from the `current` node through its explored branches. The function then returns `false`, effectively "backtracking" to the previous node to try other paths.

**Pseudocode and Analysis Table:**

| **CODE** | **Number of Times Executed** |
|---|---|
| `function DFS(graph, start, end, visited)` |  |
| `     if start == end:` | $1C$ (per call) |
| `         return true` | $1R$ (if path found) |
| `     mark start as visited` | $1A$ (per call, if not already visited) |
| `     for each neighbor in graph[start]:` | Sum of degrees of all visited nodes (up to $2M$ for loop iterations) |
| `         if neighbor not in visited:` | $(1C + 1L)$ per iteration of neighbor loop |
| `             if DFS(graph, neighbor, end, visited):` | Recursive call (up to $N$ total calls) |
| `                 return true` | $1R$ (if path found) |
| `     return false` | $1R$ (if path not found from this branch) |
| **Total Operations** | $\sim N \cdot (1C + 1A) + 2M \cdot (1C + 1L) + N \cdot R$ (approx.) |

**Complexity Analysis Explanation:**

The `DFS` algorithm explores every vertex and edge in the connected component reachable from the `start` node.

* **Visiting Each Node:** Each node (station) is visited at most once across all recursive calls. For each node visited, a constant number of operations (comparison, marking as visited, initial setup) are performed. This contributes to the O(N) part of the complexity.
* **Traversing Each Edge:** The `for each neighbor in graph[start]` loop iterates through the adjacency list of the `current` node. In an adjacency list representation, each edge in the graph will be examined at most twice (once from each endpoint for an undirected edge, or once in its direction for a directed edge) across the entire DFS traversal. This accounts for the O(M) part of the complexity.
* **Total Sum (Detailed):** Summing the operations, we get approximately `N * (constant ops per node) + M * (constant ops per edge)`. For instance, `N` nodes each involve at least `1C` and `1A`. The total iterations of the neighbor loop across all `N` calls is `2M`. Each of these iterations involves `1C` and `1L`. Plus, at most `N` returns. This gives roughly `N*(1C+1A) + 2M*(1C+1L) + N*R`.
* **Big O Complexity:** Regardless of the exact constant factors, the dominant terms are N and M. Therefore, the worst-case time complexity for `DFS` is $**O(N + M)**$, which is a **linear (polynomial)** complexity, indicating efficient performance.

-----

### 2\. `isReachableByTrainType(graph, start, end, trainType)`

This function acts as a wrapper around the core `DFS` algorithm, adding crucial filtering logic based on specific train and station type constraints.

* **Purpose:** To determine if a train of a particular `trainType` (e.g., Electric, Diesel, Steam) can travel from a `start` station to an `end` station, respecting the infrastructure constraints of the railway network.
* **Methodology (How it works):**
    1.  **Graph Filtering (Line 1):** Before initiating the traversal, `isReachableByTrainType` first creates a `filteredGraph`. This is a critical step for enforcing constraints. For example:
        * If `trainType` is `ELECTRIC`, only electrified edges are included in `filteredGraph`. Non-electrified lines are effectively removed from consideration for this specific query.
        * For `DIESEL` or `STEAM` trains, all edges are typically kept, as they don't have electrification constraints.
        * Constraints related to `stationType` (depot, station, terminal) are handled within the `dfs` function's internal checks, or potentially by further refining the `filteredGraph` to only include relevant station types.
    2.  **DFS Invocation (Lines 2-3):** After the `filteredGraph` is prepared, an empty `visited` set is initialized, and the `DFS` function is called with this `filteredGraph`, the original `start` and `end` stations, and the `visited` set.

**Pseudocode and Analysis Table:**

| **CODE** | **Number of Times Executed** |
|---|---|
| `function isReachableByTrainType(graph, start, end, trainType)` |  |
| `     create filteredGraph based on trainType:` | $M \cdot (1C + 1A)$ for each edge (approx.) |
| `         - for electric, remove non-electrified edges` | (Implicit in above) |
| `         - for diesel/steam, keep all edges` | (Implicit in above) |
| `     visited := new empty set` | $1A$ |
| `     return DFS(filteredGraph, start, end, visited)` | $\mathcal{O}(N + M)$ (complexity of DFS on filtered graph) |
| **Total Operations** | $\sim M \cdot (1C + 1A) + 1A + \mathcal{O}(N + M) = \mathcal{O}(N + M)$ |

**Complexity Analysis Explanation:**

The complexity of `isReachableByTrainType` is determined by its sequential operations:

* **Graph Filtering:** Creating the `filteredGraph` involves iterating through all M edges of the original graph. Each edge is checked against the `trainType` constraint, and suitable edges are copied or referenced in the new graph structure. This step takes O(M) time, accounting for roughly `M * (1C + 1A)` operations.
* **DFS Traversal:** The subsequent call to the `DFS` function on the `filteredGraph` has a worst-case time complexity of O(N + M), as analyzed previously.

Since these two phases run sequentially, their complexities are added. The constant operations (like `1A` for `visited`) are negligible. The dominant term in $O(M) + O(N + M) = O(N + M)$
. Therefore, the total worst-case time complexity for `isReachableByTrainType` is **O(N + M)**. This means the addition of filtering logic does not change the asymptotic linear behavior of the pathfinding.

##### 2.1 Constraint-Based Graph Filtering
In `isReachableByTrainType`, the graph is dynamically filtered based on train-specific constraints (e.g., electrification). This represents a real-world application of graph theory where edge and node attributes influence traversal decisions.
Unlike pure DFS or Warshall’s algorithm/Boolean matrix powers, this approach models domain constraints more explicitly, offering a tailored subgraph for more meaningful analysis. This adds an applied layer to classical algorithms by bridging theoretical graph models with real-world decision logic.

---

### 3\. `computeTransitiveClosure(matrix)`

This algorithm shifts from path *existence* between two points to determining *all-pairs reachability* within the graph using matrix operations.

* **Purpose:** To generate a Boolean matrix that, for any pair of stations (i, j), indicates whether station j is reachable from station i (i.e., if a path of any length exists between them). It serves to verify if the entire network is "fully connected" (i.e., if there are paths between all relevant pairs of stations).
* **Methodology (How it works):** This implementation uses the **Warshall-Floyd algorithm/Boolean matrix powers** (often simply called Warshall's/transitive closure/Boolean matrix powers algorithm when dealing with boolean matrices for reachability).
    1.  **Initialization (Implicit):** It starts with an adjacency `matrix` where `matrix[i][j]` is `true` if there's a direct edge from `i` to `j`, and `false` otherwise. This initial matrix represents reachability for paths of length 1.
    2.  **Iterative Improvement (Lines 1-3):** The algorithm uses three nested loops.
        * The **outermost loop (indexed by `k`)** iterates through every possible intermediate node (from 0 to N-1). The core idea is that if you can go from `i` to `k`, AND from `k` to `j`, then you can go from `i` to `j` via `k`.
        * The **middle loop (indexed by `i`)** iterates through all possible source nodes.
        * The **innermost loop (indexed by `j`)** iterates through all possible destination nodes.
    3.  **Reachability Update (Line 4):** Inside the innermost loop, the crucial update happens: `if matrix[i][k] and matrix[k][j]: matrix[i][j] = true`. This means if a path exists from `i` to `k` AND a path exists from `k` to `j` (where `k` is the intermediate node considered in the outermost loop), then a path is established (or confirmed) between `i` and `j`. This uses Boolean logic (AND, OR) to combine reachability information.
* **Discrete Mathematics (Theory):**
    * **Transitive Closure:**  Transitive closure as the Boolean sum of matrix powers $F = M + M^2 + \dots + M^N,\ \text{where } + \text{ is Boolean OR}$
      and a graph is connected if its transitive closure has no zero entries (Corollary 4.5). (Warshall's algorithm/Boolean matrix powers is a known efficient method to compute this closure, offering a cubic time complexity.)
    * **Matrix Representation:** The use of an adjacency matrix to represent the graph and perform operations is a direct application of matrix theory in graph theory.

**Pseudocode and Analysis Table:**

| **CODE** | **Number of Times Executed / Complexity** |
|---|---|
| `function computeTransitiveClosure(matrix)` |  |
| `    for k from 0 to n-1:` | $(N+1)C$. Outer loop runs $N$ times (1 comparison per iteration) |
| `        for i from 0 to n-1:` | $N \cdot (N+1)C$. Middle loop runs $N$ times per $k$ |
| `            for j from 0 to n-1:` | $N^2 \cdot (N+1)C$. Inner loop runs $N$ times per $i$, $k$ |
| `                if matrix[i][k] and matrix[k][j]:` | $N^3 \cdot (3L + 1C)$. Logical checks and conditionals |
| `                    matrix[i][j] = true` | $N^3 \cdot 1A$. Single assignment per innermost iteration if condition true |
| **Total Operations** | $\sim N^3 \cdot (3L + 1C + 1A) = O(N^3)$ |


**Complexity Analysis Explanation:**

The `computeTransitiveClosure` function utilizes three nested loops, each iterating N times (from 0 to N-1).

* **Nested Loops:** The innermost operation (Line 4) is executed N \* N \* N = N^3 times.
* **Innermost Operations:** Inside the innermost loop, operations like accessing matrix elements (`matrix[i][k]`, `matrix[k][j]`), performing a boolean `AND`, and potentially an assignment (`matrix[i][j] = true`) are all considered constant time operations (3L + 1C for the `if` condition, and 1A for the assignment, for each of the N^3 iterations).

Therefore, the worst-case total number of operations is approximately `N^3 * (3L + 1C + 1A)`. The worst-case time complexity of `computeTransitiveClosure` is **O(N^3)**. This is a **cubic (polynomial)** complexity, which makes it suitable for pre-analysis or validation on moderately sized graphs but less ideal for real-time queries on very large networks due to its faster growth rate compared to linear algorithms.

-----

### 4\. `computeWalksMatrix(k)`

This algorithm uses matrix exponentiation to find the exact number of walks of a specific length.

* **Purpose:** To calculate an adjacency matrix where each entry (i, j) gives the precise number of walks of length `k` from station `i` to station `j`.
* **Methodology (How it works):**
    1.  **Adjacency Matrix (Line 1):** First, the standard adjacency matrix `M` is built from the graph. This matrix usually contains integers indicating the number of direct edges between nodes.
    2.  **Matrix Power (Line 2 & `matrixPower` function):** The `matrixPower` function employs **binary exponentiation** (also known as exponentiation by squaring).
        * **Identity Matrix (Line 1 of `matrixPower`):** A `result` matrix is initialized as an identity matrix.
        * **Loop (Line 2 of `matrixPower`):** The `while` loop runs log2(k) times. In each iteration, `k` is halved.
        * **Conditional Multiplication (Line 3 of `matrixPower`):** If `k` is odd, `result` is multiplied by the current `M`.
        * **Squaring `M` (Line 4 of `matrixPower`):** `M` is always multiplied by itself (`M = M * M`). This means `M` progressively becomes M^2, M^4, M^8, and so on, efficiently computing powers.
        * **Halving `k` (Line 5 of `matrixPower`):** The exponent `k` is integer-divided by 2.


**Pseudocode and Analysis Table:**

| **CODE** | **Number of Times Executed / Complexity** |
|---|---|
| `function computeWalksMatrix(k)` |  |
| `    M = adjacencyMatrix(graph)` | $O(N^2)$ operations (to build $N \times N$ matrix) |
| `    return matrixPower(M, k)` | Call to `matrixPower` |
|  |  |
| `function matrixPower(M, k)` |  |
| `    result = identity matrix` | $O(N^2)$ operations (initialize $N \times N$ matrix) |
| `    while k > 0:` | $\log_2(k)$ iterations |
| `        if k is odd:` | 1 conditional check per iteration |
| `            result = multiply(result, M)` | $O(N^3)$ operations (matrix multiplication) per iteration |
| `        M = multiply(M, M)` | $O(N^3)$ operations (matrix multiplication) per iteration |
| `        k = k // 2` | 1 assignment per iteration |
| `    return result` | 1 return operation |
| **Total Operations** | $\sim O(N^2) + \log_2(k) \cdot O(N^3) + O(N^2) = O(N^3 \log k)$ |


**Complexity Analysis Explanation:**

The complexity of `computeWalksMatrix` is dominated by the `matrixPower` function.

* **`adjacencyMatrix(graph)`:** Building the initial N x N adjacency matrix takes O(N^2) time.
* **`matrixPower(M, k)`:**
    * The `while` loop runs log2(k) times, as the exponent `k` is halved in each iteration.
    * Inside the loop, the most expensive operation is matrix multiplication (`multiply(matrix1, matrix2)`), which, for N x N matrices, takes O(N^3) using standard algorithms. There are at most two such multiplications per iteration.

Therefore, the overall worst-case total operations are approximately `O(N^2)` (for setup) + `log2(k) * O(N^3)` (for matrix multiplications) + `O(N^2)` (for setup). The dominant term is `N^3 log k`. So, the worst-case time complexity for `computeWalksMatrix` is **O(N^3 log k)**. This is a **polynomial** complexity, more specifically a cubic polynomial multiplied by a logarithmic factor. It is efficient for computing specific walk counts compared to linearly multiplying matrices `k` times.

-----

### Summary Table for US13

| Algorithm | Description | Complexity      |
| :------------------------- | :----------------------------- |:----------------|
| `DFS` | Recursive function to check if a destination is reachable within a given graph. | $O(N + M)$        |
| `isReachableByTrainType` | Orchestrates filtering the graph by `trainType` and then calling `DFS` on the resulting `filteredGraph`. | $O(N + M)$        |
| `computeTransitiveClosure` | Computes a matrix indicating reachability between all pairs of nodes using reachability . | $O(N^3)$          |
| `computeWalksMatrix` | Computes a matrix showing the number of walks of a specific length $k$ between all pairs of nodes. | $O(N^3 \log k)$ |

**Note**: Graphical rendering (e.g., GraphStream) is **excluded** from this analysis as per AC02.

-----

### Conclusion

The algorithms implemented in US13 leverage core graph theory principles from Discrete Mathematics to address various aspects of train connectivity analysis.

* The `DFS` and `isReachableByTrainType` algorithms are highly efficient for real-time queries, demonstrating **linear worst-case time complexity** $O(N + M)$. This makes them well-suited for interactive path existence checks in sparse graphs.
* The `computeTransitiveClosure` algorithm. This complexity is typical for all-pairs reachability problems and aligns with matrix-based graph analysis techniques. While more intensive than DFS, it's appropriate for global network validation.
* The `computeWalksMatrix` algorithm, utilizing binary exponentiation for matrix powers, shows a **polynomial complexity**. This is an efficient approach for counting walks of specific lengths, directly applying Theorem 4.4.

Overall, the algorithms chosen and their analyzed complexities align with the expected performance characteristics for graph problems, demonstrating a good application of the Discrete Mathematics.