# Algorithm Analysis US14
In this section, we'll analyze the different methods of Fleury's algorithm developed in US14.

---

### Legend for Analysis Column
In the "ANALYSIS" column of the following tables, specific letters and variables are used to denote the type and scale of operations counted to determine the time complexity. These counts provide a detailed breakdown of the computational work.

* **$n$ (Number of Nodes/Vertices):**
  This variable represents the **total number of nodes** (often referred to as stations or vertices) present in the graph. Algorithms that process each station or perform operations whose cost depends directly on the number of stations will have a complexity related to $n$.

* **$m$ (Number of Edges):**
  This variable signifies the **total number of edges** (the connections between nodes) within the graph. Algorithms that iterate through each connection or involve operations related to the graph's overall connectivity will have a complexity related to $m$.

* **$A$ (Assignment/Arithmetic Operation):**
  This symbol represents a basic **assignment operation**, where a value is assigned to a variable (e.g., `variable := value`), or a simple **arithmetic operation** (e.g., addition, subtraction, increment, decrement). These are generally considered to take constant time.

* **$C$ (Comparison/Conditional Check):**
  This symbol denotes a **comparison operation** or a **conditional check** (e.g., `if (condition)`). It includes evaluating boolean expressions and loop conditions. For loops, an additional $C$ often appears, representing the final check that causes the loop to terminate.

* **$L$ (Lookup/Access Operation):**
  This symbol indicates an operation where a value is **looked up or accessed** from a data structure, such as retrieving an element from a map (`graph.get(key)`) or a list (`list.get(index)`). These are typically considered constant time operations for the types of data structures implied (e.g., hash maps, array-backed lists).

* **$Op$ (General Operation):**
  This is a general term for an **operation** that doesn't fit neatly into the more specific categories of Assignment, Comparison, or Lookup. It can represent a `break` statement within a loop, a display action, or other singular, constant-time actions.

* **$R$ (Return Statement):**
  This symbol specifically refers to the **return statement** of a function or procedure, marking the point where the function's execution concludes and a value is returned.

* **$k$ (Loop Iterations/User Input Attempts):**
  This variable is used to represent the **number of iterations** a specific loop executes, particularly when this number isn't directly tied to the overall graph size ($n$ or $m$). For example, in the `chooseStartStation` method, $k$ signifies the number of attempts a user makes to input a valid starting station.

* **$\text{grau}(v)$ or $\text{grau}(u)$ (Degree of a Vertex):**
  This notation represents the **degree of a vertex** $v$ (or $u$), which is the total number of edges connected to that vertex. Operations that iterate through the adjacency list of a specific vertex will have a complexity proportional to its degree.

---

### Method: calculateNodeDegrees
The `calculateNodeDegrees` method calculates the degree of each node (station) in a graph. It iterates over each node, gets the associated list of edges, and counts the number of edges to determine its degree.

| **CODE** | **ANALYSIS** |
|---|---|
| `function calculateNodeDegrees(graph)` | **$\mathcal{O}(n)$** |
| `    degreeMap := new empty Map` | $1A$ |
| `    for each station in graph.keySet()` | $(n+1)C$ |
| `        edges := graph.get(station)` | $nL$ |
| `        degree := 0` | $nA$ |
| `        if edges is not null` | $nC$ |
| `            degree := size of edges` | $\leq nA$ |
| `        degreeMap.put(station, degree)` | $nA$ |
| `    return degreeMap` | $1R$ |

(graph: Map\_String\_ListOfEdge)

*Table 1: Method calculateNodeDegrees*

The time complexity of the **`calculateNodeDegrees`** method is determined by the need to iterate through all the nodes (stations) of the graph. The main operation is the **for loop**, which is executed $n$ times, where $n$ is the number of nodes in the graph.

Inside the loop, operations such as getting the list of edges (`graph.get`), assigning a value to a variable, and adding a key-value pair to the `degreeMap` (`degreeMap.put`) are performed in **constant time**, or $\mathcal{O}(1)$.

Since the loop runs $n$ times and the internal operations are constant time, the total complexity of the method is dominated by the loop. Therefore, the overall time complexity of the **`calculateNodeDegrees`** method is **linear**, or $\mathcal{O}(n)$.

---

### Method: checkEulerian
The `checkEulerian` method checks a map of node degrees to count how many nodes have an odd degree, which is a key step in determining if a graph has an Eulerian path. It iterates through each node's degree, identifies the ones that are odd, and returns the total count of such nodes.

| **CODE** | **ANALYSIS** |
|---|---|
| `function checkEulerian(degreeMap, oddStations)` | **$\mathcal{O}(n)$** |
| `    count := 0` | $1A$ |
| `    for each entry (station, d) in degreeMap.entrySet()` | $(n+1)C$ |
| `        if d % 2 is not 0` | $nC$ |
| `            oddStations.add(station)` | $\leq nA$ |
| `            count := count + 1` | $\leq nA$ |
| `    return count` | $1R$ |

*Table 2: Method checkEulerian*

The time complexity of the **`checkEulerian`** method is determined by the need to iterate through all the entries in the `degreeMap`. The main operation is the **for loop**, which is executed $n$ times, where $n$ is the number of nodes in the graph.

Inside the loop, the operations are performed in **constant time**, or $\mathcal{O}(1)$. These include checking if a degree is odd using the modulo operator (`d % 2`), adding a station to the `oddStations` list, and incrementing the `count` variable.

Since the loop runs $n$ times and all internal operations are constant time, the total complexity is dominated by the loop itself. Therefore, the overall time complexity of the **`checkEulerian`** method is **linear**, or $\mathcal{O}(n)$.

---

### Method: chooseStartStation
The `chooseStartStation` method determines a set of valid starting stations and prompts the user to choose one. If the graph is semi-Eulerian (has exactly two odd-degree vertices), the options are limited to those two vertices. Otherwise, any vertex with a degree greater than zero is a valid option. The method ensures the user's choice is valid before returning it.

| **CODE** | **ANALYSIS** |
|---|---|
| `function chooseStartStation(degreeMap, stations, oddCount, oddStations)` | **$\mathcal{O}(n)$** |
| `    availableStations := new empty List` | $1A$ |
| `    for each station in stations` | $(n+1)C$ |
| `        if degreeMap.getOrDefault(station, 0) > 0` | $nL + nC$ |
| `            availableStations.add(station)` | $\leq nA$ |
| `    stationsToShow := availableStations` | $1A$ |
| `    if oddCount is 2` | $1C$ |
| `        stationsToShow := oddStations` | $\leq 1A$ |
| `    startStation := null` | $1A$ |
| `    validChoice := false` | $1A$ |
| `    while not validChoice` | $(k+1)C$ |
| `        display stationsToShow` | $k \cdot \mathcal{O}(n)$ |
| `        choice := read user input` | $kA$ |
| `    return startStation` | $1R$ |

*Table 3: Method chooseStartStation*

The time complexity of the **`chooseStartStation`** method is determined by the need to iterate through the list of stations. The main operations are two sequential **for loops**, each of which can run up to $n$ times, where $n$ is the number of stations in the graph.

The first loop filters the stations, and the second loop (inside the **while** block) displays the available options to the user. The operations inside these loops are performed in **constant time**, or $\mathcal{O}(1)$, per station. These include checking a station's degree and preparing it for display.

Since the dominant operations involve loops that are executed sequentially up to $n$ times (not nested), the total complexity is dominated by these linear steps. Therefore, the overall time complexity of the **`chooseStartStation`** method is **linear**, or $\mathcal{O}(n)$.

---

### Method: fleuryVisit
The `fleuryVisit` method implements the core traversal logic of Fleury's algorithm. Starting from a given vertex $v$, it iteratively chooses the next edge to traverse. The key rule is to select an edge that is not a "bridge", an edge whose removal would disconnect the remaining graph, unless there is no other option. This process is repeated until all edges have been traversed exactly once, constructing the Eulerian path.

| **CODE** | **ANALYSIS** |
|---|---|
| `procedure fleuryVisit(v, gLocal, stations, path)` | **$\mathcal{O}(m \cdot n^3)$** |
| `    edges := gLocal.get(v)` | $1L$ |
| `    while edges is not null and size of edges > 0` | $(m+1)C$ |
| `        chosen := edges.get(0)` | $mA$ |
| `        if chosen.from is equal to v` | $mC$ |
| `            w := chosen.to` | $\leq mA$ |
| `        else w := chosen.from` | $\leq mA$ |
| `        if size of edges > 1` | $mC$ |
| `            for i from 0 to size of edges - 1` | $\mathcal{O}(m \cdot \text{grau}(v))$ |
| `                cand := edges.get(i)` | $k \cdot \text{grau}(v) \cdot A$ |
| `                if cand.from is equal to v` | $k \cdot \text{grau}(v) \cdot C$ |
| `                    u := cand.to` | $\leq k \cdot \text{grau}(v) \cdot A$ |
| `                else u := cand.from` | $\leq k \cdot \text{grau}(v) \cdot A$ |
| `                if isValidNextEdge(v, u, gLocal, stations)` | $k \cdot \text{grau}(v) \cdot \mathcal{O}(n^3)$ |
| `                    chosen := cand` | $\leq mA$ |
| `                    w := u` | $\leq mA$ |
| `                    break` | $\leq m(\text{Op})$ |
| `        removeEdge(v, w, gLocal)` | $mA$ |
| `        path.add(v)` | $mA$ |
| `        v := w` | $mA$ |
| `        edges := gLocal.get(v)` | $mL$ |
| `    path.add(v)` | $1A$ |

(v: String, gLocal: Map\_String\_ListOfEdge, stations: List\_String, path: List\_String)

*Table 4: Method fleuryVisit*

The time complexity of the **`fleuryVisit`** method is determined by the nested loops and, most significantly, by the `isValidNextEdge` function call. This function checks if an edge is a "bridge".

The main **while loop** runs $m$ times, as it processes each edge in the graph exactly once. Inside this loop, a **for loop** iterates through the edges of the current vertex $v$. The crucial operation here is `isValidNextEdge`. This check is performed by calling `computeTransitiveClosure` (function on MatrixUtils file), which uses the Warshall-Floyd algorithm. This algorithm has a complexity of $\mathcal{O}(n^3)$.

Since this $\mathcal{O}(n^3)$ check is performed inside a **for loop** which, in turn, is inside a **while loop** that processes all $m$ edges, the total complexity is dominated by the repeated search for non-bridge edges. Therefore, the overall time complexity of this implementation of the **`fleuryVisit`** method is $\mathcal{O}(m \cdot n^3)$.

---

### Method: isValidNextEdge
The `isValidNextEdge` method is a helper function for Fleury's algorithm that determines if a given edge is a "bridge". An edge is a bridge if removing it would disconnect the graph. The method works by temporarily removing the edge in question and then computing the transitive closure of the remaining graph to check if the two vertices of the edge are still connected.

| **CODE** | **ANALYSIS** |
|---|---|
| `function isValidNextEdge(u, v, gLocal, stations)` | **$\mathcal{O}(n^3)$** |
| `    edges := gLocal.get(u)` | $1L$ |
| `    if size of edges is 1` | $1C$ |
| `        return true` | $1R$ |
| `    removeEdge(u, v, gLocal)` | $1A$ |
| `    closure := MatrixUtils.computeTransitiveClosure(gLocal, stations)` | $\mathcal{O}(n^3)$ |
| `    stillConnected := closure[stations.indexOf(u)][stations.indexOf(v)]` | $2L + 1A$ |
| `    gLocal.get(u).add(new Edge(u, v, false, 0))` | $1A$ |
| `    gLocal.get(v).add(new Edge(u, v, false, 0))` | $1A$ |
| `    return stillConnected` | $1R$ |

*Table 5: Method isValidNextEdge*

The time complexity of the **`isValidNextEdge`** method is determined by the call to `MatrixUtils.computeTransitiveClosure`.

The `computeTransitiveClosure` method, implemented in the `MatrixUtils.java` file, uses the Warshall-Floyd algorithm. This algorithm is characterized by three nested **for loops**, each iterating up to $n$ times, where $n$ is the number of vertices (stations) in the graph. The operations inside these loops are **constant time**, $\mathcal{O}(1)$.

Since the algorithm executes a structure of `for (i=0; i<n; i++) { for (j=0; j<n; j++) { for (k=0; k<n; k++) } }`, the total number of operations is proportional to $n \times n \times n$. Therefore, the complexity of `computeTransitiveClosure`, and consequently of **`isValidNextEdge`**, is **cubic**, or $\mathcal{O}(n^3)$.

---

### Method: cloneGraph
The `cloneGraph` method creates a copy of a graph. It iterates through every vertex in the original graph, and for each vertex, it creates a new list of its edges. This ensures that the new graph is a completely independent copy and not just a reference to the original.

| **CODE** | **ANALYSIS** |
|---|---|
| `function cloneGraph(graph)` | **$\mathcal{O}(n+m)$** |
| `    copy := new empty Map` | $1A$ |
| `    for each u_key in graph.keySet()` | $(n+1)C$ |
| `        list_val := graph.get(u_key)` | $nL$ |
| `        newList := new empty List` | $nA$ |
| `        for each e_item in list_val` | $(m+n)C$ |
| `            newList.add(new Edge(e_item.from, e_item.to, e_item.electrified, e_item.distance))` | $mA$ |
| `        copy.put(u_key, newList)` | $nA$ |
| `    return copy` | $1R$ |

*Table 6: Method cloneGraph*

The time complexity of the **`cloneGraph`** method is determined by the need to iterate through every vertex and every edge of the graph to create a copy. The main operations are two nested **for loops**.

The outer loop executes $n$ times, where $n$ is the number of vertices (nodes) in the graph. The inner loop's execution count depends on the number of edges connected to each vertex. When considering the entire execution, the inner loop's body will run a total of $m$ times, where $m$ is the total number of edges in the graph, because the sum of the degrees of all vertices is equal to twice the number of edges ($2m$).

Inside the loops, the operations are performed in **constant time**, or $\mathcal{O}(1)$. These include creating new lists, adding new edges to those lists, and putting the lists into the new map. Since the algorithm processes each of the $n$ vertices and each of the $m$ edges, the total complexity is the sum of these operations. Therefore, the overall time complexity of the **`cloneGraph`** method is $\mathcal{O}(n+m)$.

---

### Method: removeEdge
The `removeEdge` method is a utility function designed to remove an undirected edge between two vertices, $u$ and $v$, from a graph represented by an adjacency map. Since an edge $(u, v)$ exists in the adjacency list of both $u$ and $v$, the method must find and remove the corresponding entry from each list.

| **CODE** | **ANALYSIS** |
|---|---|
| `procedure removeEdge(u, v, gLocal)` | **$\mathcal{O}(\text{grau}(u)+\text{grau}(v))$** |
| `    lu := gLocal.get(u)` | $1L$ |
| `    for i from 0 to size of lu - 1` | $(\text{grau}(u)+1)C$ |
| `        e := lu.get(i)` | $\text{grau}(u) \cdot L$ |
| `        if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u)` | $\text{grau}(u) \cdot C$ |
| `            lu.remove(i)` | $\leq 1A$ |
| `            break` | $\leq 1(\text{Op})$ |
| `    lv := gLocal.get(v)` | $1L$ |
| `    for i from 0 to size of lv - 1` | $(\text{grau}(v)+1)C$ |
| `        e := lv.get(i)` | $\text{grau}(v) \cdot L$ |
| `        if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u)` | $\text{grau}(v) \cdot C$ |
| `            lv.remove(i)` | $\leq 1A$ |
| `            break` | $\leq 1(\text{Op})$ |

*Table 7: Method removeEdge*

The time complexity of the **`removeEdge`** method is determined by the need to search through the adjacency lists of both vertices $u$ and $v$ to find the edge that connects them. The method consists of two independent **for loops**.

The first loop iterates through the adjacency list of vertex $u$. In the worst-case scenario, it may need to scan all edges connected to $u$. The number of these edges is the degree of the vertex, $\text{grau}(u)$. Similarly, the second loop iterates through the adjacency list of vertex $v$, which has a worst-case proportional to $\text{grau}(v)$. The operations inside each loop, such as comparisons and removing an element from a list, are performed in **constant time**, $\mathcal{O}(1)$.

Since the two loops are executed sequentially (one after the other), we sum their complexities. The total complexity is therefore dominated by the combined size of the two adjacency lists. The overall time complexity of the **`removeEdge`** method is $\mathcal{O}(\text{grau}(u)+\text{grau}(v))$.

---

### Method: computeEulerianPath
The `computeEulerianPath` method arranges the entire process of finding an Eulerian path. It acts as the main controller, calling various helper methods to perform specific tasks in sequence: 1) it analyzes the graph's initial properties (node degrees, connectivity); 2) it validates if the graph is Eulerian or semi-Eulerian; 3) it prompts the user to choose a valid starting point; and finally, 4) it invokes the Fleury's algorithm to compute the actual path.

| **CODE** | **ANALYSIS** |
|---|---|
| `function computeEulerianPath(graph, stationOrder)` | **$\mathcal{O}(m \cdot n^3)$** |
| `    degreeMap := calculateNodeDegrees(graph)` | $\mathcal{O}(n)$ |
| `    closure := MatrixUtils.computeTransitiveClosure(graph, stationOrder)` | $\mathcal{O}(n^3)$ |
| `    ref := null` | $1A$ |
| `    for each s_item in stationOrder` | $(n+1)C$ |
| `        if grau(s_item) > 0` | $nL + nC$ |
| `            ref := s_item` | $\leq 1A$ |
| `            break` | $\leq 1(\text{Op})$ |
| `    if ref is null, return empty List` | $1C + 1R$ |
| `    refIdx := stationOrder.indexOf(ref)` | $\mathcal{O}(n)$ |
| `    for each s_item in stationOrder` | $(n+1)C$ |
| `        if grau(s_item) > 0` | $nL + nC$ |
| `            idx := stationOrder.indexOf(s_item)` | $n \cdot \mathcal{O}(n) = \mathcal{O}(n^2)$ |
| `            if not closure[refIdx][idx]` | $nC$ |
| `                return null` | $\leq 1R$ |
| `    oddStations := new empty List` | $1A$ |
| `    oddCount := checkEulerian(degreeMap, oddStations)` | $\mathcal{O}(n)$ |
| `    if oddCount is not 0 and not 2, return null` | $1C + 1R$ |
| `    display Eulerian properties` | $1(\text{Op})$ |
| `    startNode := chooseStartStation(degreeMap, stationOrder, oddCount, oddStations)` | $\mathcal{O}(n)$ |
| `    gLocal_map := cloneGraph(graph)` | $\mathcal{O}(n + m)$ |
| `    path_list := new empty List` | $1A$ |
| `    fleuryVisit(startNode, gLocal_map, stationOrder, path_list)` | $\mathcal{O}(m \cdot n^3)$ |
| `    return path_list` | $1R$ |

*Table 8: Method computeEulerianPath*

This complexity is determined by the step with the highest complexity in its sequence of operations. The dominant operation is the call to the **`fleuryVisit`** method. As previously analyzed, `fleuryVisit` has a complexity of $\mathcal{O}(m \cdot n^3)$ because, for many of the $m$ edges, it must check for bridges. In the implementation, this check invokes the `computeTransitiveClosure` function, which has a cubic cost of $\mathcal{O}(n^3)$.
All other sequential operations, such as the initial call to `computeTransitiveClosure` ($\mathcal{O}(n^3)$) and `cloneGraph` ($\mathcal{O}(n+m)$), are of a lower or equal order of complexity and are therefore absorbed by the dominant `fleuryVisit` step.

