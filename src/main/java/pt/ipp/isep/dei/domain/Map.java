package pt.ipp.isep.dei.domain;

import guru.nidi.graphviz.engine.*;
import guru.nidi.graphviz.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static guru.nidi.graphviz.model.Factory.*;

public class Map {
    private String nameID;
    private Size size;
    private List<City> cities;
    private List<Industry> industries;
    private List<Station> stations;
    private List<RailwayLine> railwayLines;

    public Map(String nameID, int width, int height) {
        if (!validateName(nameID)) {
            throw new IllegalArgumentException("Invalid map name");
        }
        this.nameID = nameID;
        this.size = new Size(width, height);
        this.cities = new ArrayList<>();
        this.industries = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.railwayLines = new ArrayList<>();
    }

    // Validation and basic methods
    public boolean validateName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9 _-]+$");
    }

    // Getters (all return defensive copies)
    public String getNameID() { return nameID; }
    public Size getSize() { return size; }
    public List<City> getCities() { return new ArrayList<>(cities); }
    public List<Industry> getIndustries() { return new ArrayList<>(industries); }
    public List<Station> getStations() { return new ArrayList<>(stations); }
    public List<RailwayLine> getRailwayLines() { return new ArrayList<>(railwayLines); }

    // Add elements with validation
    public void addCity(City city) {
        if (city != null && !cities.contains(city)) {
            cities.add(city);
        }
    }

    public void addIndustry(Industry industry) {
        if (industry != null && !industries.contains(industry)) {
            industries.add(industry);
        }
    }

    public void addStation(Station station) {
        if (station != null && !stations.contains(station)) {
            if (isValidLocation(station.getXCoordinate(), station.getYCoordinate())) {
                stations.add(station);
            } else {
                throw new IllegalArgumentException("Station coordinates are outside map boundaries");
            }
        }
    }

    public void addRailwayConnection(Station source, Station destination, String name, boolean electrified) {
        if (source == null || destination == null) {
            throw new IllegalArgumentException("Stations cannot be null");
        }
        if (source.equals(destination)) {
            throw new IllegalArgumentException("Cannot connect station to itself");
        }
        if (connectionExists(source, destination)) {
            throw new IllegalArgumentException("Connection already exists");
        }

        RailwayLine connection = new RailwayLine(source, destination, name, electrified);
        railwayLines.add(connection);
    }

    // Display methods
    public String getMapSummary() {
        return String.format(
                "Map: %s (%dx%d)\nCities: %d | Industries: %d | Stations: %d | Lines: %d",
                nameID, size.getWidth(), size.getHeight(),
                cities.size(), industries.size(), stations.size(), railwayLines.size()
        );
    }

    public String getTopologicalView(boolean showPotentialConnections) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== RAILWAY NETWORK TOPOLOGY ===\n");
        sb.append(getMapSummary()).append("\n\n");

        sb.append("ACTUAL CONNECTIONS:\n");
        if (railwayLines.isEmpty()) {
            sb.append("No connections built yet\n");
        } else {
            railwayLines.forEach(line ->
                    sb.append(String.format("%s (%s ↔ %s) %.1f units%s\n",
                            line.getName(),
                            line.getSource().getName(),
                            line.getDestination().getName(),
                            line.getLength(),
                            line.isElectrified() ? " [electrified]" : ""))
            );
        }

        if (showPotentialConnections && stations.size() >= 2) {
            sb.append("\nPOTENTIAL CONNECTIONS:\n");
            List<String> potential = getPotentialConnections()
                    .stream()
                    .map(pair -> String.format("%s ↔ %s (%.1f units)",
                            pair.getLeft().getName(),
                            pair.getRight().getName(),
                            calculateDistance(pair.getLeft(), pair.getRight())))
                    .collect(Collectors.toList());

            if (potential.isEmpty()) {
                sb.append("All possible connections already exist\n");
            } else {
                potential.forEach(sb::append);
            }
        }

        return sb.toString();
    }

    // Visualization methods
    public String getASCIIArtView() {
        int displayWidth = Math.min(60, size.getWidth());
        int displayHeight = Math.min(20, size.getHeight());
        char[][] grid = new char[displayHeight][displayWidth];

        // Initialize grid
        for (int y = 0; y < displayHeight; y++) {
            for (int x = 0; x < displayWidth; x++) {
                grid[y][x] = '·';
            }
        }

        // Scale factor
        double xScale = (double)displayWidth/size.getWidth();
        double yScale = (double)displayHeight/size.getHeight();

        // Draw connections
        for (RailwayLine line : railwayLines) {
            drawConnection(grid, line, xScale, yScale);
        }

        // Draw stations
        for (Station station : stations) {
            int x = (int)(station.getXCoordinate() * xScale);
            int y = (int)(station.getYCoordinate() * yScale);
            if (x >= 0 && x < displayWidth && y >= 0 && y < displayHeight) {
                grid[y][x] = 'S';
            }
        }

        // Build output
        StringBuilder sb = new StringBuilder();
        for (char[] row : grid) {
            sb.append(new String(row)).append("\n");
        }
        return sb.toString();
    }

    public String generateGraphvizDiagram() {
        MutableGraph graph = mutGraph(nameID).setDirected(false);

        // Add stations as nodes
        stations.forEach(station ->
                graph.add(mutNode(station.getName())
                        .add("pos", String.format("\"%d,%d!\"",
                                station.getXCoordinate(),
                                station.getYCoordinate()))
                        .add("shape", "box"))
        );

        // Add connections
        railwayLines.forEach(line ->
                graph.add(mutNode(line.getSource().getName())
                        .addLink(mutNode(line.getDestination().getName())
                                .add("label", String.format("%.1f", line.getLength()))
                                .add("style", line.isElectrified() ? "bold" : "solid"))
                )
        );

        return Graphviz.fromGraph(graph)
                .engine(Engine.DOT)
                .width(1000)
                .render(Format.PNG)
                .toString();
    }

    // Helper methods
    private boolean isValidLocation(int x, int y) {
        return x >= 0 && x < size.getWidth() &&
                y >= 0 && y < size.getHeight();
    }

    private double calculateDistance(Station s1, Station s2) {
        int dx = s2.getXCoordinate() - s1.getXCoordinate();
        int dy = s2.getYCoordinate() - s1.getYCoordinate();
        return Math.sqrt(dx*dx + dy*dy);
    }

    private boolean connectionExists(Station s1, Station s2) {
        return railwayLines.stream()
                .anyMatch(line ->
                        (line.getSource().equals(s1) && line.getDestination().equals(s2)) ||
                                (line.getSource().equals(s2) && line.getDestination().equals(s1))
                );
    }

    private List<Pair<Station, Station>> getPotentialConnections() {
        List<Pair<Station, Station>> potential = new ArrayList<>();
        for (int i = 0; i < stations.size(); i++) {
            for (int j = i + 1; j < stations.size(); j++) {
                Station s1 = stations.get(i);
                Station s2 = stations.get(j);
                if (!connectionExists(s1, s2)) {
                    potential.add(new Pair<>(s1, s2));
                }
            }
        }
        return potential;
    }

    private void drawConnection(char[][] grid, RailwayLine line, double xScale, double yScale) {
        Station src = line.getSource();
        Station dest = line.getDestination();

        int x1 = (int)(src.getXCoordinate() * xScale);
        int y1 = (int)(src.getYCoordinate() * yScale);
        int x2 = (int)(dest.getXCoordinate() * xScale);
        int y2 = (int)(dest.getYCoordinate() * yScale);

        // Bresenham's line algorithm
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;

        while (true) {
            if (x1 >= 0 && x1 < grid[0].length && y1 >= 0 && y1 < grid.length) {
                if (grid[y1][x1] == '·') {
                    grid[y1][x1] = line.isElectrified() ? '=' : '-';
                }
            }

            if (x1 == x2 && y1 == y2) break;

            int e2 = 2 * err;
            if (e2 > -dy) {
                err -= dy;
                x1 += sx;
            }
            if (e2 < dx) {
                err += dx;
                y1 += sy;
            }
        }
    }

    // Helper class for station pairs
    private static class Pair<L, R> {
        final L left;
        final R right;

        Pair(L left, R right) {
            this.left = left;
            this.right = right;
        }

        L getLeft() { return left; }
        R getRight() { return right; }
    }
}