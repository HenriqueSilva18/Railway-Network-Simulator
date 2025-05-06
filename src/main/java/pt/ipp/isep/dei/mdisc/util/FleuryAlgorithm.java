package pt.ipp.isep.dei.mdisc.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static List<String> findEulerianPath(Map<String, List<Edge>> graph, List<String> stationOrder, String startStation) {
        Map<String, List<Edge>> local = cloneGraph(graph);
        List<String> path = new ArrayList<>();
        fleuryVisit(startStation, local, stationOrder, path);
        return path;
    }

    private static void fleuryVisit(String v, Map<String, List<Edge>> local, List<String> order, List<String> path) {
        List<Edge> edges = local.get(v);
        while (edges != null && edges.size() > 0) {
            Edge chosen = edges.get(0);
            String w = chosen.from.equals(v) ? chosen.to : chosen.from;
            if (edges.size() > 1) {
                for (int i = 0; i < edges.size(); i++) {
                    Edge cand = edges.get(i);
                    String u = cand.from.equals(v) ? cand.to : cand.from;
                    if (isValidNextEdge(v, u, local, order)) {
                        chosen = cand;
                        w = u;
                        break;
                    }
                }
            }
            removeEdge(v, w, local);
            path.add(v);
            v = w;
            edges = local.get(v);
        }
        path.add(v);
    }

    private static boolean isValidNextEdge(String u, String v, Map<String, List<Edge>> local, List<String> order) {
        List<Edge> edges = local.get(u);
        if (edges.size() == 1) return true;

        removeEdge(u, v, local);
        boolean[][] closure = MatrixUtils.computeTransitiveClosure(local, order);
        boolean stillConnected = closure[order.indexOf(u)][order.indexOf(v)];

        local.get(u).add(new Edge(u, v, false, 0));
        local.get(v).add(new Edge(u, v, false, 0));
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

    private static void removeEdge(String u, String v, Map<String, List<Edge>> local) {
        List<Edge> lu = local.get(u);
        for (int i = 0; i < lu.size(); i++) {
            Edge e = lu.get(i);
            if ((e.from.equals(u) && e.to.equals(v)) || (e.from.equals(v) && e.to.equals(u))) {
                lu.remove(i);
                break;
            }
        }
        List<Edge> lv = local.get(v);
        for (int i = 0; i < lv.size(); i++) {
            Edge e = lv.get(i);
            if ((e.from.equals(u) && e.to.equals(v)) || (e.from.equals(v) && e.to.equals(u))) {
                lv.remove(i);
                break;
            }
        }
    }

}
