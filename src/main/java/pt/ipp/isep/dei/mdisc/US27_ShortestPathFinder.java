package pt.ipp.isep.dei.mdisc;

import pt.ipp.isep.dei.mdisc.util.*;
import java.io.IOException;
import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

public class US27_ShortestPathFinder {
    static Map<String, Station> stations = new HashMap<>();
    static Map<String, List<Edge>> fullGraph = new HashMap<>();
    static Map<String, List<Edge>> filteredGraph = new HashMap<>();
    static List<String> stationOrder = new ArrayList<>();

    public static void main(String[] args) throws IOException, InterruptedException {
        Scanner sc = new Scanner(System.in);

        // Get scenario file path from user
        System.out.println("\nPlease enter the path to your scenario CSV file:");
        String csvPath = sc.nextLine().trim();
        
        // Validate if the file exists
        if (!FilesUtils.fileExists(csvPath)) {
            System.out.println("Error: The specified scenario file does not exist.");
            return;
        }

        System.out.println("\nLoading railway scenario data from .csv file...");
        FilesUtils.loadCSV(csvPath, stations, fullGraph, stationOrder, 1);
        System.out.println("Loading complete\n");

        // Extract base filename without extension
        String baseFileName = new File(csvPath).getName().replaceFirst("[.][^.]+$", "");
        
        // Create output paths in the correct directories
        String dotPath = "docs/mdisc/us27/dot/" + baseFileName + ".dot";
        String pngPath = "docs/mdisc/us27/png/" + baseFileName + ".png";
        
        // Create directories if they don't exist
        new File("docs/mdisc/us27/dot").mkdirs();
        new File("docs/mdisc/us27/png").mkdirs();
        
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

        // Ask user how they want to input stations
        System.out.println("\nHow would you like to input the stations?");
        System.out.println("[1] Use a stop station file");
        System.out.println("[2] Enter stations manually");
        
        int inputChoice = 0;
        while (inputChoice != 1 && inputChoice != 2) {
            try {
                inputChoice = sc.nextInt();
                sc.nextLine(); // Consume newline
                if (inputChoice != 1 && inputChoice != 2) {
                    System.out.println("Invalid option. Please enter 1 or 2.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Please enter a valid number (1 or 2).");
                sc.next();
            }
        }

        List<String> requiredStations;
        if (inputChoice == 1) {
            // Get stop station file path from user
            System.out.println("\nPlease enter the path to your stop stations CSV file:");
            String stopStationPath = sc.nextLine().trim();
            
            // Validate if the file exists
            if (!FilesUtils.fileExists(stopStationPath)) {
                System.out.println("Error: The specified stop stations file does not exist.");
                return;
            }
            
            // Read the stop station file
            requiredStations = readStopStationFile(stopStationPath);
        } else {
            // Display available stations for manual input
            System.out.println("\nAvailable stations:");
            for (int i = 0; i < stationOrder.size(); i++) {
                System.out.printf("[%d] %s\n", i + 1, stationOrder.get(i));
            }
            // Use manual station input
            requiredStations = getRequiredStations(sc);
        }

        if (requiredStations == null) {
            System.out.println("Error with station selection.");
            return;
        }

        // Find shortest path through required stations
        System.out.println("\nRequired stations: " + requiredStations);
        System.out.println("Filtered graph stations: " + filteredGraph.keySet());
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
        
        // Display simple semicolon-separated path
        System.out.println("\nSimple path:");
        System.out.println("[" + String.join("; ", path) + "]");
    }

    private static double getDistanceBetweenStations(String from, String to) {
        for (Edge edge : filteredGraph.get(from)) {
            if (edge.to.equals(to)) {
                return edge.distance;
            }
        }
        return 0.0; // This should never happen for a valid path
    }

    private static List<String> readStopStationFile(String filePath) {
        List<String> stations = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line = reader.readLine();
            if (line != null) {
                String[] stationNames = line.split("[;,]");
                for (String station : stationNames) {
                    String normalizedStation = station.replaceAll("[_.\\s-]", "").trim();
                    if (!normalizedStation.isEmpty()) {
                        stations.add(normalizedStation);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading stop station file: " + e.getMessage());
            return null;
        }
        return stations;
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