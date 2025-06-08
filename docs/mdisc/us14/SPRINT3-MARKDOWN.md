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
The fleuryVisit method implements the core traversal logic of Fleury's algorithm. Starting from a given vertex v, it
iteratively chooses the next edge to traverse. The key rule is to select an edge that is not a "bridge", an edge whose
removal would disconnect the remaining graph, unless there is no other option. This process is repeated until all edges
have been traversed exactly once, constructing the Eulerian path.

| **CODE**                                                               | **ANALYSIS**     |
|------------------------------------------------------------------------|------------------|
| function fleuryVisit(v, gLocal, stations, path)                        | **O(m⋅(n+m))**   |
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
| &emsp; &emsp; &emsp; &emsp; if isValidNextEdge(v, u, gLocal, stations) | k⋅grau(v)⋅O(n+m) |
| &emsp; &emsp; &emsp; &emsp; &emsp; chosen := cand                      | ≤ mA             |
| &emsp; &emsp; &emsp; &emsp; &emsp; w := u                              | ≤ mA             |
| &emsp; &emsp; &emsp; &emsp; &emsp; break                               | ≤ m(Op)          |
| &emsp; &emsp; removeEdge(v, w, gLocal)                                 | mA               |
| &emsp; &emsp; path.add(v)                                              | mA               |
| &emsp; &emsp; v := w                                                   | mA               |
| &emsp; &emsp; edges := gLocal.get(v)                                   | mL               |
| &emsp; path.add(v)                                                     | 1A               |
&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 4: Method fleuryVisit*

The time complexity of the fleuryVisit method is determined by the nested loops and, most significantly, by the
isValidNextEdge function call. This function checks if an edge is a "bridge", which is a computationally expensive
operation.

The main while loop runs m times, as it processes each edge in the graph exactly once. Inside this loop, a for loop
iterates through the edges of the current vertex v to find a valid next edge. The crucial operation here is
isValidNextEdge, which must check if removing a candidate edge would disconnect the graph. This check typically
requires a graph traversal (like BFS or DFS), which has a complexity of O(n+m).

Since this expensive O(n+m) check is performed inside a for loop which, in turn, is inside a while loop that processes
all m edges, the total complexity is dominated by the repeated search for non-bridge edges. Therefore, the overall
time complexity of the fleuryVisit method is O(m⋅(n+m)), which is often simplified to O(m2) for connected graphs.

---

### Method: computeEulerianPath


| **CODE** | **ANALYSIS** |
|----------|--------------|
|function computeEulerianPath(graph:Map_String_ListOfEdge, stationOrder:List_String, sc:Scanner)
|    degreeMap := calculateNodeDegrees(graph)
|    closure := MatrixUtils.computeTransitiveClosure(graph, stationOrder)
| ref := null         |              |
|  for each s_item in stationOrder        |              |
| if degreeMap.getOrDefault(s_item, 0) > 0         |              |
|          |              |

&emsp; &emsp; &emsp; &emsp; &emsp; &emsp; *Table 4: Method computeEulerianPath*


```
function computeEulerianPath(graph:Map_String_ListOfEdge, stationOrder:List_String, sc:Scanner)
    degreeMap := calculateNodeDegrees(graph)
    closure := MatrixUtils.computeTransitiveClosure(graph, stationOrder)

    ref := null
    for each s_item in stationOrder
        if degreeMap.getOrDefault(s_item, 0) > 0
            ref := s_item
            break
    if ref is null
        return new empty List_String
        
    refIdx := stationOrder.indexOf(ref)
    for each s_item in stationOrder
        if degreeMap.getOrDefault(s_item, 0) > 0
            idx := stationOrder.indexOf(s_item)
            if not closure[refIdx][idx]
                print "Graph is disconnected cannot do maintenance route."
                return null
                
    oddStations := new empty List_String
    oddCount := checkEulerian(degreeMap, oddStations)
    if oddCount is not 0 and oddCount is not 2
        print "Graph is not Eulerian nor semi-Eulerian (" + oddCount + " odd-degree vertices)."
        return null
        
    if oddCount is 0
        print "Graph is Eulerian (all vertices have even degree)."
    else
        print "Graph is semi-Eulerian (has exactly 2 odd-degree vertices)."

    startNode := chooseStartStation(sc, degreeMap, stationOrder, oddCount, oddStations)
    print "Starting maintenance route at: " + startNode

    gLocal_map := cloneGraph(graph)
    path_list := new empty List_String
    fleuryVisit(startNode, gLocal_map, stationOrder, path_list)
    return path_list
```
---
```
function isValidNextEdge(u:String, v:String, gLocal:Map_String_ListOfEdge, stations:List_String)
    edges := gLocal.get(u)
    if size of edges is 1
        return true
        
    removeEdge(u, v, gLocal)
    closure := MatrixUtils.computeTransitiveClosure(gLocal, stations)
    stillConnected := closure[stations.indexOf(u)][stations.indexOf(v)]
    
    gLocal.get(u).add(new Edge(u, v, false, 0))
    gLocal.get(v).add(new Edge(u, v, false, 0))
    return stillConnected
```
---
```
function cloneGraph(graph:Map_String_ListOfEdge)
    copy := new empty Map_String_ListOfEdge
    for each u_key in graph.keySet()
        list_val := graph.get(u_key)
        newList := new empty List_Edge
        for each e_item in list_val
            newList.add(new Edge(e_item.from, e_item.to, e_item.electrified, e_item.distance))
        copy.put(u_key, newList)
    return copy
```
---
```
procedure removeEdge(u:String, v:String, gLocal:Map_String_ListOfEdge)
    lu := gLocal.get(u)
    for i from 0 to size of lu - 1
        e := lu.get(i)
        if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u)
            lu.remove(i)
            break
            
    lv := gLocal.get(v)
    for i from 0 to size of lv - 1
        e := lv.get(i)
        if (e.from is equal to u and e.to is equal to v) or (e.from is equal to v and e.to is equal to u)
            lv.remove(i)
            break
```
---