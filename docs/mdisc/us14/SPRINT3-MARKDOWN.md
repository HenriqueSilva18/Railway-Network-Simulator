```
function calculateNodeDegrees(graph:Map_String_ListOfEdge)
    degreeMap := new empty Map_String_Integer
    
    for each station in graph.keySet()
        edges := graph.get(station)
        degree := 0
            if edges is not null
                degree := size of edges
        degreeMap.put(station, degree)
        
    return degreeMap
```
---
```
function checkEulerian(degreeMap:Map_String_Integer, oddStations:List_String)
    count := 0
    
    for each entry (station, d) in degreeMap.entrySet()
        if d % 2 is not 0
            oddStations.add(station)
            count := count + 1
    return count
```
---
```
procedure fleuryVisit(v:String, gLocal:Map_String_ListOfEdge, stations:List_String, path:List_String)
    edges := gLocal.get(v)
    
    while edges is not null and size of edges > 0
        chosen := edges.get(0)
        w := ""
            if chosen.from is equal to v
                w := chosen.to
            else
                w := chosen.from
        if size of edges > 1
            for i from 0 to size of edges - 1
                cand := edges.get(i)
                u := ""
                    if cand.from is equal to v
                        u := cand.to
                    else
                        u := cand.from
                if isValidNextEdge(v, u, gLocal, stations)
                    chosen := cand
                    w := u
                    break
        removeEdge(v, w, gLocal)
        path.add(v)
        v := w
        edges := gLocal.get(v)
    path.add(v)
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
function chooseStartStation(sc:Scanner, degreeMap:Map_String_Integer, stations:List_String, oddCount:Integer, oddStations:List_String)
    availableStations := new empty List_String
    for each station_item in stations
        if degreeMap.getOrDefault(station_item, 0) > 0
            availableStations.add(station_item)
            
    stationsToShow := availableStations
        if oddCount is 2
            stationsToShow := oddStations
    
    startStation := null
    validChoice := false
    
    while not validChoice
        if oddCount is 2
            print "A semi-Eulerian graph should start at one odd-degree station:"
        else
            print "Enter the number of the station you want to start from:"

        for i from 0 to size of stationsToShow - 1
            station_item := stationsToShow.get(i)
            print "[" + (i + 1) + "]: " + station_item + " (degree: " + degreeMap.get(station_item) + ")"
        try
            choice := sc.nextInt()
            if choice >= 1 and choice <= size of stationsToShow
                startStation := stationsToShow.get(choice - 1)
                validChoice := true
            else
                print "Invalid choice. Please enter a valid number."
            catch InputMismatchException
                print "Please enter a valid number."
                sc.next()
    return startStation