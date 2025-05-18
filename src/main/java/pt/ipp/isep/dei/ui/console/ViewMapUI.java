package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Station;

import java.util.Optional;

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
        // Add station names
        map.getStations().stream()
                .filter(station -> station.getPosition().getY() == y)
                .forEach(station -> layout.append("(Station at ").append(station.getPosition().getX())
                        .append(",").append(station.getPosition().getY()).append(") "));

        // Add city names
        map.getCities().stream()
                .filter(city -> city.getPosition().getY() == y)
                .forEach(city -> layout.append("(City: ").append(city.getNameID()).append(") "));

        // Add industry names
        map.getIndustries().stream()
                .filter(industry -> industry.getPosition().getY() == y)
                .forEach(industry -> layout.append("(Industry: ").append(industry.getNameID()).append(") "));
    }
} 