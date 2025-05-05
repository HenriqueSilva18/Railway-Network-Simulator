package pt.ipp.isep.dei.mdisc;

import pt.ipp.isep.dei.mdisc.util.Edge;
import pt.ipp.isep.dei.mdisc.util.MatrixUtils;
import pt.ipp.isep.dei.mdisc.util.Station;

import java.io.*;
import java.util.*;

public class US13_TrainConnectivityChecker {

    enum TrainType { STEAM, DIESEL, ELECTRIC }

    static Map<String, Station> stations = new HashMap<>();
    static Map<String, List<Edge>> graph = new HashMap<>();
    static List<String> stationOrder = new ArrayList<>();

    public static void loadCSV(String path) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(path));
        String line = reader.readLine(); // skip header
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String from = parts[0].trim();
            String to = parts[1].trim();
            boolean electrified = parts[2].trim().equals("1");
            double distance = Double.parseDouble(parts[3].trim());

            stations.putIfAbsent(from, new Station(from));
            stations.putIfAbsent(to, new Station(to));

            if (!stationOrder.contains(from)) stationOrder.add(from);
            if (!stationOrder.contains(to)) stationOrder.add(to);

            graph.putIfAbsent(from, new ArrayList<>());
            graph.putIfAbsent(to, new ArrayList<>());
            graph.get(from).add(new Edge(from, to, electrified, distance));
            graph.get(to).add(new Edge(to, from, electrified, distance));
        }
    }

    public static boolean canTravel(String start, String end, TrainType trainType, String requiredStationType) {
        Set<String> visited = new HashSet<>();
        return dfs(start, end, trainType, requiredStationType, visited);
    }

    private static boolean dfs(String current, String end, TrainType trainType, String stationType, Set<String> visited) {
        if (!stations.containsKey(current) || !stations.containsKey(end)) return false;

        // Skip station-type check if "any"
        if (!stationType.equals("any") && !stations.get(current).getType().equalsIgnoreCase(stationType)) return false;

        if (current.equals(end)) return true;
        visited.add(current);

        for (Edge edge : graph.getOrDefault(current, Collections.emptyList())) {
            if (!visited.contains(edge.to)) {
                if (trainType == TrainType.ELECTRIC && !edge.electrified) continue;

                // Skip station-type check if "any"
                if (!stationType.equals("any") && !stations.get(edge.to).getType().equalsIgnoreCase(stationType)) continue;

                if (dfs(edge.to, end, trainType, stationType, visited)) return true;
            }
        }
        return false;
    }


    public static void exportToDOT(String filename) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filename));
        writer.write("graph RailwayNetwork {\n");
        writer.write("  node [shape=circle];\n");
        Set<String> drawn = new HashSet<>();

        for (Map.Entry<String, List<Edge>> entry : graph.entrySet()) {
            for (Edge edge : entry.getValue()) {
                String id = edge.from + "--" + edge.to;
                String idRev = edge.to + "--" + edge.from;
                if (!drawn.contains(id) && !drawn.contains(idRev)) {
                    String color = edge.electrified ? "blue" : "black";
                    writer.write(String.format("  \"%s\" -- \"%s\" [label=\"%.1f km\", color=%s];\n",
                            edge.from, edge.to, edge.distance, color));
                    drawn.add(id);
                }
            }
        }
        writer.write("}\n");
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        Scanner sc = new Scanner(System.in);

        System.out.println("Loading railway data from data/porto_railways.csv...");
        loadCSV("docs/mdisc/us13/data/scenario4_lines.csv");

        System.out.println("Enter start station:");
        String start = sc.nextLine().trim();

        System.out.println("Enter end station:");
        String end = sc.nextLine().trim();

        System.out.println("Enter train type (STEAM, DIESEL, ELECTRIC):");
        TrainType trainType = TrainType.valueOf(sc.nextLine().trim().toUpperCase());

        System.out.println("Enter station type (depot, station, terminal):");
        String stationType = sc.nextLine().trim().toLowerCase();

        boolean result = canTravel(start, end, trainType, stationType);
        System.out.println(result ? "Train can travel!" : "No valid route found.");

        System.out.println("Exporting network to dot/network.dot");
        exportToDOT("docs/mdisc/us13/graphstream/dot/scenario4.dot");
        System.out.println("Export complete.");

        System.out.println("Enter path length to compute walk count (e.g., 4):");
        int length = sc.nextInt();
        int[][] walks = MatrixUtils.computeWalksMatrix(length, graph, stationOrder);

        int i = stationOrder.indexOf(start);
        int j = stationOrder.indexOf(end);
        System.out.println("Number of walks of length " + length + " from " + start + " to " + end + ": " + walks[i][j]);

        System.out.println("\nWalk count matrix M^" + length + ":");
        MatrixUtils.printMatrix(walks);

        System.out.println("\nComputing transitive closure (Boolean reachability)...");
        boolean[][] closure = MatrixUtils.computeTransitiveClosure(graph, stationOrder);
        MatrixUtils.printMatrix(closure);
    }
}
