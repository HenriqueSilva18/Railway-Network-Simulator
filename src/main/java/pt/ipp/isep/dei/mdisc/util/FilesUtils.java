package pt.ipp.isep.dei.mdisc.util;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.awt.Desktop;

public class FilesUtils {

    public static void loadCSV(String csvPath, Map<String, Station> stations, Map<String, List<Edge>> graph,
                               List<String> stationOrder, int maintenanceType) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(csvPath));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            String from = parts[0].trim();
            String to = parts[1].trim();
            boolean electrified = parts[2].trim().equals("1");
            double distance = Double.parseDouble(parts[3].trim());

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
