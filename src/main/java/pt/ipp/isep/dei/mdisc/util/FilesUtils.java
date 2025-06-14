package pt.ipp.isep.dei.mdisc.util;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
// import java.awt.Desktop;

public class FilesUtils {

    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile();
    }

    private static String normalizeStationName(String station) {
        // Remove all separators (_, ., -, space) and trim
        return station.replaceAll("[_.\\s-]", "").trim();
    }

    public static void loadCSV(String csvPath, Map<String, Station> stations, Map<String, List<Edge>> graph,
                               List<String> stationOrder, int maintenanceType) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csvPath));
        String line;
        while ((line = reader.readLine()) != null) {
            // First split by either semicolon or comma to separate the fields
            String[] fields = line.split("[;,]");
            if (fields.length < 4) {
                System.out.println("Warning: Skipping invalid line: " + line);
                continue;
            }

            // Process each field, handling potential empty strings
            List<String> validFields = new ArrayList<>();
            for (String field : fields) {
                String trimmed = field.trim();
                if (!trimmed.isEmpty()) {
                    validFields.add(trimmed);
                }
            }

            if (validFields.size() < 4) {
                System.out.println("Warning: Skipping invalid line (insufficient fields): " + line);
                continue;
            }

            String from = normalizeStationName(validFields.get(0));
            String to = normalizeStationName(validFields.get(1));
            boolean electrified = validFields.get(2).equals("1");
            double distance;
            try {
                distance = Double.parseDouble(validFields.get(3));
            } catch (NumberFormatException e) {
                System.out.println("Warning: Invalid distance value in line: " + line);
                continue;
            }

            if (maintenanceType == 2 && !electrified) {
                continue;
            }

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

    public static void exportToDOT(String dotPath, Map<String, List<Edge>> graph) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(dotPath));
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

    public static void dotToPNG(String dotPath, String pngPath) throws IOException {
        String dotSource = Files.readString(Paths.get(dotPath));
        Graphviz.fromString(dotSource)
                .render(Format.PNG)
                .toFile(new File(pngPath));
    }

    public static void openFile(String filePath) throws IOException {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File does not exist: " + filePath);
        }

        String os = System.getProperty("os.name").toLowerCase();
        String command = os.contains("win") ? "cmd /c start " : 
                        os.contains("mac") ? "open " : 
                        "xdg-open ";
        
        Runtime.getRuntime().exec(command + file.getAbsolutePath());
    }
}
