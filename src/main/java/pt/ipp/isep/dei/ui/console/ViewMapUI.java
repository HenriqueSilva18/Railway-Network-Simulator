package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Station;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ViewMapUI implements Runnable {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";

    @Override
    public void run() {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            System.out.println("No map selected. Please select a map and scenario first.");
            return;
        }

        displayMap(currentMap);
    }

    private void displayMap(Map map) {
        StringBuilder layout = new StringBuilder();
        layout.append("\nCurrent Map: ").append(map.getNameID()).append("\n");
        layout.append("Size: ").append(map.getSize().getWidth())
              .append("x").append(map.getSize().getHeight()).append("\n\n");

        // Add legend
        layout.append("Legend:\n");
        layout.append(RED + "C" + RESET + " - City\n");
        layout.append(BLUE + "I" + RESET + " - Industry\n");
        layout.append(GREEN + "S" + RESET + " - Station\n");
        layout.append(". - Empty cell\n\n");

        // Create the map layout
        for (int y = 0; y < map.getSize().getHeight(); y++) {
            for (int x = 0; x < map.getSize().getWidth(); x++) {
                String symbol = getCellSymbol(map, x, y);
                layout.append(symbol).append(" ");
            }
            layout.append("  ");
            
            // Add labels for cities, industries, and stations on this row
            appendRowLabels(layout, map, y);
            layout.append("\n");
        }

        System.out.println(layout.toString());
    }

    private String getCellSymbol(Map map, int x, int y) {
        // Check for station first (highest priority)
        Optional<Station> station = map.getStations().stream()
                .filter(s -> s.getPosition().getX() == x && s.getPosition().getY() == y)
                .findFirst();
        if (station.isPresent()) {
            return GREEN + "S" + RESET;
        }

        // Check for city
        Optional<City> city = map.getCities().stream()
                .filter(c -> c.getPosition().getX() == x && c.getPosition().getY() == y)
                .findFirst();
        if (city.isPresent()) {
            return RED + "C" + RESET;
        }

        // Check for industry
        Optional<Industry> industry = map.getIndustries().stream()
                .filter(i -> i.getPosition().getX() == x && i.getPosition().getY() == y)
                .findFirst();
        if (industry.isPresent()) {
            return BLUE + "I" + RESET;
        }

        // Empty cell
        return ".";
    }

    private void appendRowLabels(StringBuilder layout, Map map, int y) {
        // Use sets to track which names we've already added for this row
        Set<String> addedStationLabels = new HashSet<>();
        Set<String> addedCityLabels = new HashSet<>();
        Set<String> addedIndustryLabels = new HashSet<>();

        // Add station names (only once per name)
        map.getStations().stream()
                .filter(station -> station.getPosition().getY() == y)
                .forEach(station -> {
                    String label = "(Station at " + station.getPosition().getX() + "," + station.getPosition().getY() + ")";
                    if (addedStationLabels.add(label)) {
                        layout.append(label).append(" ");
                    }
                });

        // Add city names (only once per name)
        map.getCities().stream()
                .filter(city -> city.getPosition().getY() == y)
                .forEach(city -> {
                    String label = "(City: " + city.getNameID() + ")";
                    if (addedCityLabels.add(label)) {
                        layout.append(label).append(" ");
                    }
                });

        // Add industry names (only once per name)
        map.getIndustries().stream()
                .filter(industry -> industry.getPosition().getY() == y)
                .forEach(industry -> {
                    String label = "(Industry: " + industry.getNameID() + ")";
                    if (addedIndustryLabels.add(label)) {
                        layout.append(label).append(" ");
                    }
                });
    }
} 