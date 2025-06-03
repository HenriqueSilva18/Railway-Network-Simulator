package pt.ipp.isep.dei.mdisc.util;

import java.util.*;

public class DijkstraAlgorithm {
    private static class Node implements Comparable<Node> {
        String station;
        double distance;
        String predecessor;

        Node(String station, double distance, String predecessor) {
            this.station = station;
            this.distance = distance;
            this.predecessor = predecessor;
        }

        @Override
        public int compareTo(Node other) {
            return Double.compare(this.distance, other.distance);
        }
    }

    public static List<String> findShortestPath(Map<String, List<Edge>> graph, String start, String end) {
        // Validate that both stations exist in the graph
        if (!graph.containsKey(start) || !graph.containsKey(end)) {
            return null;
        }

        Map<String, Double> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        PriorityQueue<Node> queue = new PriorityQueue<>();

        // Initialize distances
        for (String station : graph.keySet()) {
            distances.put(station, Double.POSITIVE_INFINITY);
        }
        distances.put(start, 0.0);
        queue.add(new Node(start, 0.0, null));

        while (!queue.isEmpty()) {
            Node current = queue.poll();
            
            if (current.station.equals(end)) {
                break;
            }

            if (current.distance > distances.get(current.station)) {
                continue;
            }

            List<Edge> edges = graph.get(current.station);
            if (edges == null) continue;  // Skip if no edges exist

            for (Edge edge : edges) {
                String neighbor = edge.to;
                double newDistance = current.distance + edge.distance;

                if (newDistance < distances.get(neighbor)) {
                    distances.put(neighbor, newDistance);
                    predecessors.put(neighbor, current.station);
                    queue.add(new Node(neighbor, newDistance, current.station));
                }
            }
        }

        // Reconstruct path
        List<String> path = new ArrayList<>();
        String current = end;
        while (current != null) {
            path.add(0, current);
            current = predecessors.get(current);
        }

        return path.isEmpty() || !path.get(0).equals(start) ? null : path;
    }

    public static List<String> findPathThroughStations(Map<String, List<Edge>> graph, 
                                                     List<String> requiredStations) {
        if (requiredStations.size() < 2) {
            return null;
        }

        // Validate that all required stations exist in the graph
        for (String station : requiredStations) {
            if (!graph.containsKey(station)) {
                return null;
            }
        }

        List<String> fullPath = new ArrayList<>();
        for (int i = 0; i < requiredStations.size() - 1; i++) {
            List<String> segment = findShortestPath(graph, 
                                                  requiredStations.get(i), 
                                                  requiredStations.get(i + 1));
            if (segment == null) {
                return null;
            }
            
            // Add all stations except the last one (to avoid duplicates)
            if (i < requiredStations.size() - 2) {
                fullPath.addAll(segment.subList(0, segment.size() - 1));
            } else {
                fullPath.addAll(segment);
            }
        }
        
        return fullPath;
    }
} 