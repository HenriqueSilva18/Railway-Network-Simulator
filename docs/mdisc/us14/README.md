# US14 – Train Maintenance: Algorithm and Implementation Documentation

## Purpose

This module addresses the problem of determining a route that traverses 
each railway line exactly once for maintenance purposes. 
It allows users to choose between all lines or only electrified ones, 
and computes whether such a route exists. If possible, it also suggests valid 
starting stations and displays the network using GraphStream visualization.

---

## Functional Features

1. **Maintenance route calculation**:
   - Uses **Fleury Algorithm** to find:
      - If is Eulerian (all stations even degree)
      - If is semi-Eulerian (exactly two stations with odd degree)
   - Filters graph for:
      - **All lines**
      - **Only electrified lines**

2. **Route feasibility feedback**:
   - If no such path exists, a **warning message** is displayed.
   - If path exists, the **valid starting stations** are shown for user selection.

3. **Graph visualization**:
   - Displays network using **GraphStream**.
   - Electrified lines drawn in **blue**, others in **black**.
   - Shows station names and line connections.

---

## Algorithms

### 1. Fleury Algorithm

1. **Compute degrees of the vertices/nodes:**
   - The method **calculateNodeDegrees** is used, which
     counts the number of incident edges for each station.

   ```pseudo
   function calculateNodeDegrees(G):
       degreeMap ← empty map
       for each vertex v in G:
           degreeMap[v] ← size(G[v])
       return degreeMap
   ```

2. **Identify if its Eulerian or semi-Eulerian:**
   - The method **checkEulerian** is used, which
     finds and counts vertices whose degree is odd.

   ```pseudo
   function checkEulerian(degreeMap):
       oddVertices ← empty list
       for each (v, degree) in degreeMap:
           if degree mod 2 ≠ 0:
               oddVertices.append(v)
       oddCount ← size(oddVertices)
       return (oddCount, oddVertices)
   ```

3. **Choose start station:**
    - The method **chooseStartStation** is used, which
      displays valid options based on the graph and reads 
      the user input.

   ```pseudo
   function chooseStartStation(degreeMap, stations, oddCount, oddStations):
       determine availableStations based on degreeMap and oddCount
       prompt user with numbered list of stations
       read and validate choice
       return selected station
   ```

4. **Build the path:**
   - The method **fleuryVisit** is used, which
     while there are edges incident to v, choose one that is not a bridge 
     unless forced, remove it, append the next vertex, and continue.

   ```pseudo
   function fleuryVisit(v, G, stations, ε):
       while G[v] ≠ empty:
           E_v ← G[v]
           if |E_v| = 1:
               w ← otherEndpoint(E_v[0], v)
           else:
               for each edge e in E_v:
                   w ← otherEndpoint(e, v)
                   if isValidNextEdge(v, w, G, stations):
                       break
           removeEdge(v, w, G)
           ε.append(w)
           v ← w
   ```

   - The method **isValidNextEdge** is used, which
     decides whether a given edge can be taken next without 
     disconnecting the remaining graph.
   ```pseudo
   function isValidNextEdge(u, v, G, stations):
       if |G[u]| = 1:
           return true

       removeEdge(u, v, G)
       closure ← computeTransitiveClosure(G, stations)
       connected ← closure[indexOf(u)][indexOf(v)]

       addEdge(u, v, G)
       return connected
   ```

   - The method **removeEdge** is used, which
     removes an edge from both endpoints.
   ```pseudo
   function removeEdge(u, v, G):
       remove one edge (u, v) from G[u]
       remove one edge (u, v) from G[v]
   ```

**Additional methods used:**
   - The method **computeEulerianPath** is used, which
   is the main method to compute the path.
   ```pseudo
    function computeEulerianPath(G, stations, userInput):
        calculateNodeDegrees(G)
        computeTransitiveClosure(G, stationList)
        checkEulerian(degreeMap)
        chooseStartStation(degreeMap, stations, oddCount, oddStations)
        cloneGraph(G)
        fleuryVisit(v, Gcopy, stationList, path)
   ```

   - The method **cloneGraph** is used, which
   creates a copy of the graph so edges can be removed without affecting the original.
   ```pseudo
   function cloneGraph(G):
       Gcopy ← empty map
       for each vertex u in G:
           Gcopy[u] ← empty list
           for each edge e in G[u]:
               Gcopy[u].append(new Edge(e.from, e.to, e.electrified, e.distance))
       return Gcopy
   ```



