# Algorithm Analysis US14
In this section, we will analyze the different methods of Fleury's algorithm developed in US14.

---

### Method: calculateNodeDegrees
The calculateNodeDegrees method calculates the degree of each node (station) in a graph. It iterates over each node,
gets the associated list of edges, and counts the number of edges to determine its degree.

| **CODE**                                            | **ANALYSIS** |
|-----------------------------------------------------|--------------|
| function calculateNodeDegrees(graph)                | **O(n)**     |
| &emsp; degreeMap := new empty Map                   | 1A           |
| &emsp; for each station in graph.keySet()           | (n+1)C       |
| &emsp; &emsp; edges := graph.get(station)           | nL           |
| &emsp; &emsp; degree := 0                           | nA           |
| &emsp; &emsp; &emsp;if edges is not null            | nC           |
| &emsp; &emsp; &emsp; &emsp;degree := size of edges  | ≤ nA         |
| &emsp; &emsp; &emsp; degreeMap.put(station, degree) | nA           |
| &emsp; return degreeMap                             | 1R           |

&emsp; &emsp; &emsp; &emsp; (graph: Map_String_ListOfEdge)

&emsp; &emsp; &emsp; *Table 1: Method calculateNodeDegrees*

The time complexity of the calculateNodeDegrees method is determined by the need to iterate through all the nodes
(stations) of the graph. The main operation is the for loop, which is executed n times, where n is the number of nodes
in the graph.

Inside the loop, operations such as getting the list of edges (graph.get), assigning a value to a variable, and adding
a key-value pair to the degreeMap (degreeMap.put) are performed in constant time, or O(1).

Since the loop runs n times and the internal operations are constant time, the total complexity of the method is
dominated by the loop. Therefore, the overall time complexity of the calculateNodeDegrees method is linear, or O(n).

---

### Method: checkEulerian
The checkEulerian method checks a map of node degrees to count how many nodes have an odd degree, which is a key step
in determining if a graph has an Eulerian path. It iterates through each node's degree, identifies the ones that are
odd, and returns the total count of such nodes.

| **CODE**                                                   | **ANALYSIS** |
|------------------------------------------------------------|--------------|
| function checkEulerian(degreeMap, oddStations)             | **O(n)**     |
| &emsp; count := 0                                          | 1A           |
| &emsp; for each entry (station, d) in degreeMap.entrySet() | (n+1)C       |
| &emsp; &emsp; if d % 2 is not 0                            | nC           |
| &emsp; &emsp; &emsp; oddStations.add(station)              | ≤ nA         |
| &emsp; &emsp; &emsp; count := count + 1                    | ≤ nA         |
| &emsp; return count                                        | 1R           |

&emsp; &emsp; (degreeMap: Map_String_Integer, oddStations: List_String)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 2: Method checkEulerian*

The time complexity of the checkEulerian method is determined by the need to iterate through all the entries in the
degreeMap. The main operation is the for loop, which is executed n times, where n is the number of nodes in the graph.

Inside the loop, the operations are performed in constant time, or O(1). These include checking if a degree is odd
using the modulo operator (d % 2), adding a station to the oddStations list, and incrementing the count variable.

Since the loop runs n times and all internal operations are constant time, the total complexity is dominated by the
loop itself. Therefore, the overall time complexity of the checkEulerian method is linear, or O(n).

---

### Method: chooseStartStation
The chooseStartStation method determines a set of valid starting stations and prompts the user to choose one. If the
graph is semi-Eulerian (has exactly two odd-degree vertices), the options are limited to those two vertices. Otherwise,
any vertex with a degree greater than zero is a valid option. The method ensures the user's choice is valid before
returning it.

| **CODE**                                                                | **ANALYSIS** |
|-------------------------------------------------------------------------|--------------|
| function chooseStartStation(degreeMap, stations, oddCount, oddStations) | **O(n)**     |
| &emsp; availableStations := new empty List                              | 1A           |
| &emsp; for each station in stations                                     | (n+1)C       |
| &emsp; &emsp; if degreeMap.getOrDefault(station, 0) > 0                 | nL + nC      |
| &emsp; &emsp; &emsp; availableStations.add(station)                     | ≤ nA         |
| &emsp; stationsToShow := availableStations                              | 1A           |
| &emsp; &emsp; if oddCount is 2                                          | 1C           |
| &emsp; &emsp; &emsp; stationsToShow := oddStations                      | ≤ 1A         |
| &emsp; startStation := null                                             | 1A           |
| &emsp; validChoice := false                                             | 1A           |
| &emsp; while not validChoice                                            | (k+1)C       |
| &emsp; &emsp; display stationsToShow                                    | k⋅O(n)       |
| &emsp; &emsp; choice := read user input                                 | kA           |
| &emsp; return startStation                                              | 1R           |

(degreeMap: Map_String_Integer, stations: List_String, oddCount: Integer, oddStations: List_String)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 3: Method chooseStartStation*

The time complexity of the chooseStartStation method is determined by the need to iterate through the list of stations.
The main operations are two sequential for loops, each of which can run up to n times, where n is the number of
stations in the graph.

The first loop filters the stations, and the second loop (inside the while block) displays the available options to
the user. The operations inside these loops are performed in constant time, or O(1), per station. These include
checking a station's degree and preparing it for display.

Since the dominant operations involve loops that are executed sequentially up to n times (not nested), the total
complexity is dominated by these linear steps. Therefore, the overall time complexity of the chooseStartStation method
is linear, or O(n).


---

### Method: fleuryVisit
The fleuryVisit method implements the core traversal logic of Fleury's algorithm. Starting from a given vertex v,
it iteratively chooses the next edge to traverse. The key rule is to select an edge that is not a "bridge", an edge
whose removal would disconnect the remaining graph, unless there is no other option. This process is repeated until
all edges have been traversed exactly once, constructing the Eulerian path.

| **CODE**                                                               | **ANALYSIS**     |
|------------------------------------------------------------------------|------------------|
| procedure fleuryVisit(v, gLocal, stations, path)                       | **O(m⋅n^3)**     |
| &emsp; edges := gLocal.get(v)                                          | 1L               |
| &emsp; while edges is not null and size of edges > 0                   | (m+1)C           |
| &emsp; &emsp; chosen := edges.get(0)                                   | mA               |
| &emsp; &emsp; &emsp; if chosen.from is equal to v                      | mC               |
| &emsp; &emsp; &emsp; &emsp; w := chosen.to                             | ≤ mA             |
| &emsp; &emsp; &emsp; else w := chosen.from                             | ≤ mA             |
| &emsp; &emsp; if size of edges > 1                                     | mC               |
| &emsp; &emsp; &emsp; for i from 0 to size of edges - 1                 | O(m⋅grau(v))     |
| &emsp; &emsp; &emsp; &emsp; cand := edges.get(i)                       | k⋅grau(v)⋅A      |
| &emsp; &emsp; &emsp; &emsp; &emsp; if cand.from is equal to v          | k⋅grau(v)⋅C      |
| &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; u := cand.to                 | ≤ k⋅grau(v)⋅A    |
| &emsp; &emsp; &emsp; &emsp; &emsp; else u := cand.from                 | ≤ k⋅grau(v)⋅A    |
| &emsp; &emsp; &emsp; &emsp; if isValidNextEdge(v, u, gLocal, stations) | k⋅grau(v)⋅O(n^3) |
| &emsp; &emsp; &emsp; &emsp; &emsp; chosen := cand                      | ≤ mA             |
| &emsp; &emsp; &emsp; &emsp; &emsp; w := u                              | ≤ mA             |
| &emsp; &emsp; &emsp; &emsp; &emsp; break                               | ≤ m(Op)          |
| &emsp; &emsp; removeEdge(v, w, gLocal)                                 | mA               |
| &emsp; &emsp; path.add(v)                                              | mA               |
| &emsp; &emsp; v := w                                                   | mA               |
| &emsp; &emsp; edges := gLocal.get(v)                                   | mL               |
| &emsp; path.add(v)                                                     | 1A               |

(v: String, gLocal: Map_String_ListOfEdge, stations: List_String, path: List_String)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 4: Method fleuryVisit*

The time complexity of the fleuryVisit method is determined by the nested loops and, most significantly, by the
isValidNextEdge function call. This function checks if an edge is a "bridge".

The main while loop runs m times, as it processes each edge in the graph exactly once. Inside this loop, a for loop
iterates through the edges of the current vertex v. The crucial operation here is isValidNextEdge. This check is 
performed by calling computeTransitiveClosure (function on MatrixUtils file), which uses the
Warshall-Floyd algorithm. This algorithm has a complexity of O(n^3).

Since this O(n^3) check is performed inside a for loop which, in turn, is inside a while loop that processes
all m edges, the total complexity is dominated by the repeated search for non-bridge edges. Therefore, the overall
time complexity of this implementation of the fleuryVisit method is O(m⋅n^3).

---

### Method: isValidNextEdge
The isValidNextEdge method is a helper function for Fleury's algorithm that determines if a given edge is a "bridge".
An edge is a bridge if removing it would disconnect the graph. The method works by temporarily removing the edge in
question and then computing the transitive closure of the remaining graph to check if the two vertices of the edge are
still connected.

| **CODE**                                                                   | **ANALYSIS** |
|----------------------------------------------------------------------------|--------------|
| function isValidNextEdge(u, v, gLocal, stations)                           | **O(n^3)**   |
| &emsp; edges := gLocal.get(u)                                              | 1L           |
| &emsp; if size of edges is 1                                               | 1C           |
| &emsp; &emsp; return true                                                  | 1R           |
| &emsp; removeEdge(u, v, gLocal)                                            | 1A           |
| &emsp; closure := MatrixUtils.computeTransitiveClosure(gLocal, stations)   | O(n^3)       |
| &emsp; stillConnected := closure[stations.indexOf(u)][stations.indexOf(v)] | 2L + 1A      |
| &emsp; gLocal.get(u).add(new Edge(u, v, false, 0))                         | 1A           |
| &emsp; gLocal.get(v).add(new Edge(u, v, false, 0))                         | 1A           |
| &emsp; return stillConnected                                               | 1R           |

&emsp; &emsp; (u: String, v: String, gLocal: Map_String_ListOfEdge, stations: List_String)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 5: Method isValidNextEdge*

The time complexity of the isValidNextEdge method is determined by the call to MatrixUtils.computeTransitiveClosure.

The computeTransitiveClosure method, implemented in the MatrixUtils.java file, uses the Warshall-Floyd algorithm.
This algorithm is characterized by three nested for loops, each iterating up to n times, where n is the
number of vertices (stations) in the graph. The operations inside these loops are constant time, O(1).

Since the algorithm executes a structure of for (i=0; i<n; i++) { for (j=0; j<n; j++) { for (k=0; k<n; k++) } },
the total number of operations is proportional to n×n×n. Therefore, the complexity of computeTransitiveClosure,
and consequently of isValidNextEdge, is cubic, or O(n^3).

---

### Method: cloneGraph
The cloneGraph method creates a copy of a graph. It iterates through every vertex in the original graph, and for
each vertex, it creates a new list of its edges. This ensures that the new graph is a completely independent copy and
not just a reference to the original.


| **CODE**                                                                                                | **ANALYSIS** |
|---------------------------------------------------------------------------------------------------------|--------------|
| function cloneGraph(graph)                                                                              | **O(n+m)**   |
| &emsp; copy := new empty Map                                                                            | 1A           |
| &emsp; for each u_key in graph.keySet()                                                                 | (n+1)C       |
| &emsp; &emsp; list_val := graph.get(u_key)                                                              | nL           |
| &emsp; &emsp; newList := new empty List                                                                 | nA           |
| &emsp; &emsp; for each e_item in list_val                                                               | (m+n)C       |
| &emsp; &emsp; &emsp; newList.add(new Edge(e_item.from, e_item.to, e_item.electrified, e_item.distance)) | mA           |
| &emsp; &emsp; copy.put(u_key, newList)                                                                  | nA           |
| &emsp; return copy                                                                                      | 1R           |

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; (graph: Map_String_ListOfEdge)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 6: Method cloneGraph*

The time complexity of the cloneGraph method is determined by the need to iterate through every vertex and every edge
of the graph to create a copy. The main operations are two nested for loops.

The outer loop executes n times, where n is the number of vertices (nodes) in the graph. The inner loop's execution
count depends on the number of edges connected to each vertex. When considering the entire execution, the inner loop's
body will run a total of m times, where m is the total number of edges in the graph, because the sum of the degrees of
all vertices is equal to twice the number of edges (2m).

Inside the loops, the operations are performed in constant time, or O(1). These include creating new lists, adding new
edges to those lists, and putting the lists into the new map. Since the algorithm processes each of the n vertices and
each of the m edges, the total complexity is the sum of these operations. Therefore, the overall time complexity of the
cloneGraph method is O(n+m).

---

### Method: removeEdge
The removeEdge method is a utility function designed to remove an undirected edge between two vertices, u and v, from a
graph represented by an adjacency map. Since an edge (u, v) exists in the adjacency list of both u and v, the method
must find and remove the corresponding entry from each list.

| **CODE**                                                                                                        | **ANALYSIS**           |
|-----------------------------------------------------------------------------------------------------------------|------------------------|
| procedure removeEdge(u, v, gLocal)                                                                               | **O(grau(u)+grau(v))** |
| &emsp; lu := gLocal.get(u)                                                                                      | 1L                     |
| &emsp; for i from 0 to size of lu - 1                                                                           | (grau(u)+1)C           |
| &emsp; &emsp; e := lu.get(i)                                                                                    | grau(u)·L              |
| &emsp; &emsp; if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u) | grau(u)·C              |
| &emsp; &emsp; &emsp; lu.remove(i)                                                                               | ≤ 1A                   |
| &emsp; &emsp; &emsp; break                                                                                      | ≤ 1(Op)                |
| &emsp; lv := gLocal.get(v)                                                                                      | 1L                     |
| &emsp; for i from 0 to size of lv - 1                                                                           | (grau(v)+1)C           |
| &emsp; &emsp; e := lv.get(i)                                                                                    | grau(v)·L              |
| &emsp; &emsp; if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u) | grau(v)·C              |
| &emsp; &emsp; &emsp; lv.remove(i)                                                                               | ≤ 1A                   |
| &emsp; &emsp; &emsp;  break                                                                                     | ≤ 1(Op)                |

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; (u: String, v: String, gLocal: Map_String_ListOfEdge)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 7: Method removeEdge*

The time complexity of the removeEdge method is determined by the need to search through the adjacency lists of both
vertices u and v to find the edge that connects them. The method consists of two independent for loops.

The first loop iterates through the adjacency list of vertex u. In the worst-case scenario, it may need to scan all
edges connected to u. The number of these edges is the degree of the vertex, grau(u). Similarly, the second loop
iterates through the adjacency list of vertex v, which has a worst-case proportional to grau(v). The operations
inside each loop, such as comparisons and removing an element from a list, are performed in constant time, O(1).

Since the two loops are executed sequentially (one after the other), we sum their complexities. The total complexity is
therefore dominated by the combined size of the two adjacency lists. The overall time complexity of the removeEdge
method is O(grau(u)+grau(v)).

---

### Method: computeEulerianPath
The computeEulerianPath method arrange the entire process of finding an Eulerian path. It acts as the main controller,
calling various helper methods to perform specific tasks in sequence: 1) it analyzes the graph's initial properties
(node degrees, connectivity); 2) it validates if the graph is Eulerian or semi-Eulerian; 3) it prompts the user to
choose a valid starting point; and finally, 4) it invokes the fleuryVisit algorithm to compute the actual path.


| **CODE**                                                                               | **ANALYSIS**    |
|----------------------------------------------------------------------------------------|-----------------|
| function computeEulerianPath(graph, stationOrder)                                      | **O(m⋅n^3)**    |
| &emsp; degreeMap := calculateNodeDegrees(graph)                                        | O(n)            |
| &emsp; closure := MatrixUtils.computeTransitiveClosure(graph, stationOrder)            | O(n^3)          |
| &emsp; ref := null                                                                     | 1A              |
| &emsp; for each s_item in stationOrder                                                 | (n+1)C          |
| &emsp; &emsp; if grau(s_item) > 0                                                      | nL + nC         |
| &emsp; &emsp; &emsp; ref := s_item                                                     | ≤ 1A            |
| &emsp; &emsp; &emsp; break                                                             | ≤ 1(Op)         |
| &emsp; if ref is null, return empty List                                               | 1C + 1R         |
| &emsp; refIdx := stationOrder.indexOf(ref)                                             | O(n)            |
| &emsp; for each s_item in stationOrder                                                 | (n+1)C          |
| &emsp; &emsp; if grau(s_item) > 0                                                      | nL + nC         |
| &emsp; &emsp; &emsp; idx := stationOrder.indexOf(s_item)                               | n⋅O(n) = O(n^2) |
| &emsp; &emsp; &emsp; if not closure[refIdx][idx]                                       | nC              |
| &emsp; &emsp; &emsp; &emsp; return null                                                | ≤ 1R            |
| &emsp; oddStations := new empty List                                                   | 1A              |
| &emsp; oddCount := checkEulerian(degreeMap, oddStations)                               | O(n)            |
| &emsp; if oddCount is not 0 and not 2, return null                                     | 1C + 1R         |
| &emsp; display Eulerian properties                                                     | 1(Op)           |
| &emsp; startNode := chooseStartStation(degreeMap, stationOrder, oddCount, oddStations) | O(n)            |
| &emsp; gLocal_map := cloneGraph(graph)                                                 | O(n + m)        |
| &emsp; path_list := new empty List                                                     | 1A              |
| &emsp; fleuryVisit(startNode, gLocal_map, stationOrder, path_list)                     | O(m⋅n^3)        |
| &emsp; return path_list                                                                | 1R              |

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; (graph: Map_String_ListOfEdge, stationOrder: List_String)

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 8: Method computeEulerianPath*


This complexity is determined by the step with the highest complexity in its sequence of operations. The dominant
operation is the call to the fleuryVisit method. As previously analyzed, fleuryVisit has a complexity of O(m⋅n^3)
because, for many of the m edges, it must check for bridges. In this implementation, this check invokes
the computeTransitiveClosure function, which has a cubic cost of O(n^3).

All other sequential operations, such as the initial call to computeTransitiveClosure (O(n^3)) and cloneGraph (O(n+m)),
are of a lower or equal order of complexity and are therefore absorbed by the dominant fleuryVisit step.

---