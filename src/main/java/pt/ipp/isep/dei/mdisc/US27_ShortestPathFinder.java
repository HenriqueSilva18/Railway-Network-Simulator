package pt.ipp.isep.dei.mdisc;

import pt.ipp.isep.dei.mdisc.util.*;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;

public class US27_ShortestPathFinder {
    static String fileName = "test_scenario_lines";
    static String csvPath = "docs/mdisc/data/"+fileName+".csv";
    static String dotPath = "docs/mdisc/us27/dot/"+fileName+".dot";
    static String pngPath = "docs/mdisc/us27/png/"+fileName+".png";

    static Map<String, Station> stations = new HashMap<>();
    static Map<String, List<Edge>> fullGraph = new HashMap<>();
    static Map<String, List<Edge>> filteredGraph = new HashMap<>();
    static List<String> stationOrder = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        System.out.println("\nLoading railway scenario data from .csv file...");
        FilesUtils.loadCSV(csvPath, stations, fullGraph, stationOrder, 1);
        System.out.println("Loading complete\n");

        // First show the network visualization
        System.out.println("Exporting network to .dot file...");
        FilesUtils.exportToDOT(dotPath, fullGraph);
        System.out.println("Export network to .png file....");
        FilesUtils.dotToPNG(dotPath, pngPath);
        System.out.println("Export complete\n");
        System.out.println("Opening network visualization...");
        FilesUtils.openFile(pngPath);
        System.out.println("Please examine the network and press Enter to continue...");
        sc.nextLine();

        // Ask for line type preference
        int lineType = chooseLineType(sc);
        System.out.println("Selected line type: " +
                (lineType == 1 ? "All lines" : "Only electrified lines"));

        // Filter graph based on line type
        filteredGraph = new HashMap<>();
        for (String station : fullGraph.keySet()) {
            filteredGraph.put(station, new ArrayList<>());
            for (Edge edge : fullGraph.get(station)) {
                if (lineType == 1 || edge.electrified) {
                    filteredGraph.get(station).add(edge);
                }
            }
        }

        // Display available stations
        System.out.println("\nAvailable stations:");
        for (int i = 0; i < stationOrder.size(); i++) {
            System.out.printf("[%d] %s\n", i + 1, stationOrder.get(i));
        }

        // Get required stations from user
        List<String> requiredStations = getRequiredStations(sc);
        if (requiredStations == null) {
            return;
        }

        // Find shortest path through required stations
        List<String> path = DijkstraAlgorithm.findPathThroughStations(filteredGraph, requiredStations);
        if (path == null) {
            System.out.println("\nNo valid path found through the specified stations.");
            if (lineType == 2) {
                System.out.println("This might be because some stations are not connected by electrified lines.");
                System.out.println("Try using 'All lines' option instead.");
            }
            return;
        }

        // Calculate and display path details
        displayPathDetails(path);

        // Export and visualize the graph with the path highlighted
        System.out.println("\nExporting network to .dot file...");
        exportToDOTWithPath(dotPath, fullGraph, path);
        System.out.println("Export network to .png file....");
        FilesUtils.dotToPNG(dotPath, pngPath);
        System.out.println("Export complete\n");
        System.out.println("Opening visualization with shortest path highlighted in red...");
        FilesUtils.openFile(pngPath);
    }

    private static int chooseLineType(Scanner sc) {
        int lineType = 0;

        while (lineType != 1 && lineType != 2) {
            System.out.println("\nChoose the type of lines to use:" +
                    "\n[1]: All lines" +
                    "\n[2]: Only electrified lines\n");

            try {
                lineType = sc.nextInt();
                if (lineType != 1 && lineType != 2) {
                    System.out.println("Invalid option. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number (1 or 2).");
                sc.next();
            }
        }

        return lineType;
    }

    private static void displayPathDetails(List<String> path) {
        System.out.println("\nPath Details:");
        System.out.println("-------------");
        double totalDistance = 0.0;
        
        for (int i = 0; i < path.size() - 1; i++) {
            String current = path.get(i);
            String next = path.get(i + 1);
            double segmentDistance = getDistanceBetweenStations(current, next);
            totalDistance += segmentDistance;
            
            System.out.printf("%d. %s → %s: %.1f km\n", 
                i + 1, current, next, segmentDistance);
        }
        
        System.out.println("-------------");
        System.out.printf("Total distance: %.1f km\n", totalDistance);
    }

    private static double getDistanceBetweenStations(String from, String to) {
        for (Edge edge : filteredGraph.get(from)) {
            if (edge.to.equals(to)) {
                return edge.distance;
            }
        }
        return 0.0; // This should never happen for a valid path
    }

    private static List<String> getRequiredStations(Scanner sc) {
        List<String> stations = new ArrayList<>();
        
        // Get start station
        System.out.println("\nEnter the number of the starting station:");
        String startStation = getStationNumber(sc);
        if (startStation == null) return null;
        stations.add(startStation);

        // Get end station
        System.out.println("\nEnter the number of the destination station:");
        String endStation = getStationNumber(sc);
        if (endStation == null) return null;
        
        // Get intermediate stations
        System.out.println("\nDo you want to specify intermediate stations? (y/n)");
        String answer = sc.next().trim().toLowerCase();
        
        if (answer.equals("y")) {
            System.out.println("\nAvailable stations for intermediate stops:");
            for (int i = 0; i < stationOrder.size(); i++) {
                String station = stationOrder.get(i);
                if (!station.equals(startStation) && !station.equals(endStation)) {
                    System.out.printf("[%d] %s\n", i + 1, station);
                }
            }
            
            System.out.println("\nEnter the numbers of stations to visit in order (one per line).");
            System.out.println("Enter 0 when you're done selecting stations:");
            
            while (true) {
                System.out.print("\nEnter station number (or 0 to finish): ");
                try {
                    int stationNum = sc.nextInt();
                    if (stationNum == 0) {
                        break;
                    }
                    if (stationNum < 1 || stationNum > stationOrder.size()) {
                        System.out.println("Invalid station number: " + stationNum);
                        continue;
                    }
                    String selectedStation = stationOrder.get(stationNum - 1);
                    if (selectedStation.equals(startStation) || selectedStation.equals(endStation)) {
                        System.out.println("This station is already selected as start or end point.");
                        continue;
                    }
                    if (stations.contains(selectedStation)) {
                        System.out.println("This station is already selected.");
                        continue;
                    }
                    stations.add(selectedStation);
                    System.out.println("Added station: " + selectedStation);
                } catch (InputMismatchException e) {
                    System.out.println("Please enter a valid number.");
                    sc.next();
                }
            }
        }
        
        // Add end station last
        stations.add(endStation);
        return stations;
    }

    private static String getStationNumber(Scanner sc) {
        try {
            int stationNum = sc.nextInt();
            if (stationNum < 1 || stationNum > stationOrder.size()) {
                System.out.println("Invalid station number: " + stationNum);
                return null;
            }
            return stationOrder.get(stationNum - 1);
        } catch (InputMismatchException e) {
            System.out.println("Please enter a valid number.");
            sc.next();
            return null;
        }
    }

    private static void exportToDOTWithPath(String dotPath, Map<String, List<Edge>> graph, 
                                          List<String> path) throws IOException {
        Set<String> pathEdges = new HashSet<>();
        for (int i = 0; i < path.size() - 1; i++) {
            pathEdges.add(path.get(i) + "--" + path.get(i + 1));
            pathEdges.add(path.get(i + 1) + "--" + path.get(i));
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
        writer.write("graph RailwayNetwork {\n");
        writer.write("  node [shape=circle];\n");
        Set<String> drawn = new HashSet<>();

        for (Map.Entry<String, List<Edge>> entry : graph.entrySet()) {
            for (Edge edge : entry.getValue()) {
                String id = edge.from + "--" + edge.to;
                String idRev = edge.to + "--" + edge.from;
                if (!drawn.contains(id) && !drawn.contains(idRev)) {
                    String color = pathEdges.contains(id) ? "red" : 
                                 edge.electrified ? "blue" : "black";
                    String style = pathEdges.contains(id) ? "bold" : "normal";
                    writer.write(String.format("  \"%s\" -- \"%s\" [label=\"%.1f km\", color=%s, style=%s];\n",
                            edge.from, edge.to, edge.distance, color, style));
                    drawn.add(id);
                }
            }
        }
        writer.write("}\n");
        writer.close();
    }
}