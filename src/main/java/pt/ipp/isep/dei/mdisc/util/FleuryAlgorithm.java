package pt.ipp.isep.dei.mdisc.util;

import java.util.*;

public class FleuryAlgorithm {

    public static Map<String, Integer> calculateNodeDegrees(Map<String, List<Edge>> graph) {
        Map<String, Integer> degreeMap = new HashMap<>();

        for (String station : graph.keySet()) {
            List<Edge> edges = graph.get(station);
            int degree = (edges == null ? 0 : edges.size());
            degreeMap.put(station, degree);
        }
        return degreeMap;
    }

    public static int checkEulerian(Map<String, Integer> degreeMap, List<String> oddStations) {
        int count = 0;
        for (Map.Entry<String, Integer> entry : degreeMap.entrySet()) {
            String station = entry.getKey();
            int d = entry.getValue();
            if (d % 2 != 0) {
                oddStations.add(station);
                count = count + 1;
            }
        }
        return count;
    }

    private static void fleuryVisit(String v, Map<String, List<Edge>> gLocal, List<String> stations, List<String> path) {
        List<Edge> edges = gLocal.get(v);
        while (edges != null && edges.size() > 0) {
            Edge chosen = edges.get(0);
            String w = chosen.from.equals(v) ? chosen.to : chosen.from;
            if (edges.size() > 1) {
                for (int i = 0; i < edges.size(); i++) {
                    Edge cand = edges.get(i);
                    String u = cand.from.equals(v) ? cand.to : cand.from;
                    if (isValidNextEdge(v, u, gLocal, stations)) {
                        chosen = cand;
                        w = u;
                        break;
                    }
                }
            }
            removeEdge(v, w, gLocal);
            path.add(v);
            v = w;
            edges = gLocal.get(v);
        }
        path.add(v);
    }

    private static boolean isValidNextEdge(String u, String v, Map<String, List<Edge>> gLocal, List<String> stations) {
        List<Edge> edges = gLocal.get(u);
        if (edges.size() == 1) return true;

        removeEdge(u, v, gLocal);
        boolean[][] closure = MatrixUtils.computeTransitiveClosure(gLocal, stations);
        boolean stillConnected = closure[stations.indexOf(u)][stations.indexOf(v)];

        gLocal.get(u).add(new Edge(u, v, false, 0));
        gLocal.get(v).add(new Edge(u, v, false, 0));
        return stillConnected;
    }

    private static Map<String, List<Edge>> cloneGraph(Map<String, List<Edge>> graph) {
        Map<String, List<Edge>> copy = new HashMap<>();
        for (String u : graph.keySet()) {
            List<Edge> list = graph.get(u);
            List<Edge> newList = new ArrayList<>();
            for (Edge e : list) {
                newList.add(new Edge(e.from, e.to, e.electrified, e.distance));
            }
            copy.put(u, newList);
        }
        return copy;
    }

    private static void removeEdge(String u, String v, Map<String, List<Edge>> gLocal) {
        List<Edge> lu = gLocal.get(u);
        for (int i = 0; i < lu.size(); i++) {
            Edge e = lu.get(i);
            if ((e.from.equals(u) && e.to.equals(v)) || (e.from.equals(v) && e.to.equals(u))) {
                lu.remove(i);
                break;
            }
        }
        List<Edge> lv = gLocal.get(v);
        for (int i = 0; i < lv.size(); i++) {
            Edge e = lv.get(i);
            if ((e.from.equals(u) && e.to.equals(v)) || (e.from.equals(v) && e.to.equals(u))) {
                lv.remove(i);
                break;
            }
        }
    }

    public static List<String> computeEulerianPath(Map<String, List<Edge>> graph, List<String> stationOrder, Scanner sc) {
        Map<String, Integer> degreeMap = calculateNodeDegrees(graph);
        boolean[][] closure = MatrixUtils.computeTransitiveClosure(graph, stationOrder);

        String ref = null;
        for (String s : stationOrder) {
            if (degreeMap.getOrDefault(s, 0) > 0) {
                ref = s;
                break;
            }
        }
        if (ref == null) {
            return new ArrayList<>();
        }

        int refIdx = stationOrder.indexOf(ref);
        for (String s : stationOrder) {
            if (degreeMap.getOrDefault(s, 0) > 0) {
                int idx = stationOrder.indexOf(s);
                if (!closure[refIdx][idx]) {
                    System.out.println("Graph is disconnected cannot do maintenance route.");
                    return null;
                }
            }
        }

        List<String> oddStations = new ArrayList<>();
        int oddCount = checkEulerian(degreeMap, oddStations);
        if (oddCount != 0 && oddCount != 2) {
            System.out.println(
                    "Graph is not Eulerian nor semi-Eulerian (" + oddCount + " odd-degree vertices).");
            return null;
        }

        if (oddCount == 0) {
            System.out.println("Graph is Eulerian (all vertices have even degree).");
        } else {
            System.out.println("Graph is semi-Eulerian (has exactly 2 odd-degree vertices).");
        }

        String start = chooseStartStation(sc, degreeMap, stationOrder, oddCount, oddStations);
        System.out.println("Starting maintenance route at: " + start + "\n");

        Map<String, List<Edge>> gLocal = cloneGraph(graph);
        List<String> path = new ArrayList<>();
        fleuryVisit(start, gLocal, stationOrder, path);
        return path;
    }


    public static String chooseStartStation(Scanner sc, Map<String, Integer> degreeMap, List<String> stations, int oddCount, List<String> oddStations) {
        List<String> availableStations = new ArrayList<>();
        for (String station : stations) {
            if (degreeMap.getOrDefault(station, 0) > 0) {
                availableStations.add(station);
            }
        }

        List<String> stationsToShow = (oddCount == 2) ? oddStations : availableStations;

        String startStation = null;
        boolean validChoice = false;

        while (!validChoice) {
            if (oddCount == 2) {
                System.out.println("\nA semi-Eulerian graph should start at one odd-degree station:");
            } else {
                System.out.println("\nEnter the number of the station you want to start from:");
            }

            for (int i = 0; i < stationsToShow.size(); i++) {
                String station = stationsToShow.get(i);
                System.out.println("[" + (i + 1) + "]: " + station + " (degree: " + degreeMap.get(station) + ")");
            }

            try {
                int choice = sc.nextInt();
                if (choice >= 1 && choice <= stationsToShow.size()) {
                    startStation = stationsToShow.get(choice - 1);
                    validChoice = true;
                } else {
                    System.out.println("Invalid choice. Please enter a valid number.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number.");
                sc.next();
            }
        }

        return startStation;
    }

}
