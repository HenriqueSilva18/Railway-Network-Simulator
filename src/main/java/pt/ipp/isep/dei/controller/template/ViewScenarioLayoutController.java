package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Station;

import java.util.Optional;
import java.util.Set;
import java.util.HashSet;

public class ViewScenarioLayoutController {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";

    private String getCellSymbol(Map map, int x, int y, boolean useColors) {
        // Check for station first (highest priority)
        Optional<Station> station = map.getStations().stream()
                .filter(s -> s.getPosition().getX() == x && s.getPosition().getY() == y)
                .findFirst();
        if (station.isPresent()) {
            return useColors ? GREEN + "S" + RESET : "S";
        }

        // Check for city
        Optional<City> city = map.getCities().stream()
                .filter(c -> c.getPosition().getX() == x && c.getPosition().getY() == y)
                .findFirst();
        if (city.isPresent()) {
            return useColors ? RED + "C" + RESET : "C";
        }

        // Check for industry
        Optional<Industry> industry = map.getIndustries().stream()
                .filter(i -> i.getPosition().getX() == x && i.getPosition().getY() == y)
                .findFirst();
        if (industry.isPresent()) {
            return useColors ? BLUE + "I" + RESET : "I";
        }

        // Empty cell
        return ".";
    }

    public String renderScenarioLayout(Map map, Scenario scenario) {
        if (map == null) {
            return "No map loaded";
        }

        StringBuilder layout = new StringBuilder();
        layout.append("Map: ").append(map.getNameID()).append("\n");
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
                String symbol = getCellSymbol(map, x, y, true);
                layout.append(symbol).append(" ");
            }
            layout.append("  ");

            // Add labels for cities and industries on this row without duplication
            appendRowLabels(layout, map, y);
            layout.append("\n");
        }

        return layout.toString();
    }

    private void appendRowLabels(StringBuilder layout, Map map, int y) {
        // Create a set to track unique entity IDs that have been labeled
        Set<String> labeledEntities = new HashSet<>();

        // Add city names (no duplicates)
        for (City city : map.getCities()) {
            if (city.getPosition().getY() == y) {
                String cityId = city.getNameID();

                // Skip if we've already labeled this entity
                if (labeledEntities.contains(cityId)) {
                    continue;
                }

                layout.append("(City: ").append(cityId).append(") ");
                labeledEntities.add(cityId);
            }
        }

        // Add industry names (no duplicates)
        for (Industry industry : map.getIndustries()) {
            if (industry.getPosition().getY() == y) {
                String industryId = industry.getNameID();

                // Skip if we've already labeled this entity
                if (labeledEntities.contains(industryId)) {
                    continue;
                }

                layout.append("(Industry: ").append(industryId).append(") ");
                labeledEntities.add(industryId);
            }
        }
    }

    public String renderSimpleScenarioLayout(Map map, Scenario scenario) {
        if (map == null) {
            return "No map loaded";
        }

        StringBuilder layout = new StringBuilder();

        // Header
        layout.append("Map: ").append(map.getNameID()).append("\n");
        if (scenario != null) {
            layout.append("Scenario: ").append(scenario.getNameID()).append("\n");
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM yyyy");
            String startDate = dateFormat.format(scenario.getStartDate());
            String endDate = dateFormat.format(scenario.getEndDate());
            layout.append("Period: ").append(startDate).append(" - ").append(endDate).append("\n");
        }
        layout.append("Size: ").append(map.getSize().getWidth()).append("x").append(map.getSize().getHeight()).append("\n\n");

        // Legend
        layout.append("Legend:\n");
        layout.append("  C - City\n");
        layout.append("  I - Industry\n");
        layout.append("  S - Station\n");
        layout.append("  . - Empty cell\n\n");

        // Map content - build each row carefully for JavaFX parsing
        for (int y = 0; y < map.getSize().getHeight(); y++) {
            // Build the map row with proper spacing
            for (int x = 0; x < map.getSize().getWidth(); x++) {
                String symbol = getCellSymbol(map, x, y, false);
                layout.append(symbol);

                // Add space after each symbol except the last one in the row
                if (x < map.getSize().getWidth() - 1) {
                    layout.append(" ");
                }
            }

            // Add row information if available
            String info = getRowInfo(map, y, scenario);
            if (!info.isEmpty()) {
                layout.append("  | ").append(info);
            }
            layout.append("\n");
        }

        return layout.toString();
    }

    private String getRowInfo(Map map, int y, Scenario scenario) {
        StringBuilder info = new StringBuilder();
        Set<String> entitiesLabeled = new HashSet<>(); // To avoid duplicate labels for the same entity on a row

        // City information
        for (City city : map.getCities()) {
            if (city.getPosition().getY() == y) {
                String cityKey = "C_" + city.getNameID(); // Unique key for each city
                if (!entitiesLabeled.contains(cityKey)) {
                    if (info.length() > 0) {
                        info.append("; "); // Separator if other info already exists
                    }
                    info.append("City ").append(city.getNameID());
                    if (scenario != null) {
                        info.append(" (D:").append(city.getDemandedCargo()).append(", S:").append(city.getSuppliedCargo()).append(")");
                    }
                    entitiesLabeled.add(cityKey);
                }
            }
        }

        // Industry information
        for (Industry industry : map.getIndustries()) {
            if (industry.getPosition().getY() == y) {
                String industryKey = "I_" + industry.getNameID(); // Unique key for each industry
                if (!entitiesLabeled.contains(industryKey)) {
                    if (info.length() > 0) {
                        info.append("; "); // Separator if other info already exists
                    }
                    info.append("Industry ").append(industry.getNameID());
                    if (scenario != null) {
                        info.append(" (Type:").append(industry.getType()).append(")");
                    }
                    entitiesLabeled.add(industryKey);
                }
            }
        }
        return info.toString();
    }

    // Alternative method specifically designed for JavaFX with structured data
    public MapLayoutData getMapLayoutData(Map map, Scenario scenario) {
        if (map == null) {
            return null;
        }

        MapLayoutData data = new MapLayoutData();

        // Header information
        data.mapName = map.getNameID();
        data.width = map.getSize().getWidth();
        data.height = map.getSize().getHeight();

        if (scenario != null) {
            data.scenarioName = scenario.getNameID();
            java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("MMM yyyy");
            data.startDate = dateFormat.format(scenario.getStartDate());
            data.endDate = dateFormat.format(scenario.getEndDate());
        }

        // Build grid data
        data.grid = new CellData[data.height][data.width];
        for (int y = 0; y < data.height; y++) {
            final int rowY = y;  // Make y effectively final for lambda
            for (int x = 0; x < data.width; x++) {
                final int colX = x;  // Make x effectively final for lambda
                CellData cell = new CellData();

                // Check for station first (highest priority)
                Optional<Station> station = map.getStations().stream()
                        .filter(s -> s.getPosition().getX() == colX && s.getPosition().getY() == rowY)
                        .findFirst();
                if (station.isPresent()) {
                    cell.symbol = "S";
                    cell.type = CellType.STATION;
                    cell.entityName = station.get().getNameID();
                } else {
                    // Check for city
                    Optional<City> city = map.getCities().stream()
                            .filter(c -> c.getPosition().getX() == colX && c.getPosition().getY() == rowY)
                            .findFirst();
                    if (city.isPresent()) {
                        cell.symbol = "C";
                        cell.type = CellType.CITY;
                        cell.entityName = city.get().getNameID();
                        if (scenario != null) {
                            cell.demandedCargo = String.valueOf(city.get().getDemandedCargo());
                            cell.suppliedCargo = String.valueOf(city.get().getSuppliedCargo());
                        }
                    } else {
                        // Check for industry
                        Optional<Industry> industry = map.getIndustries().stream()
                                .filter(i -> i.getPosition().getX() == colX && i.getPosition().getY() == rowY)
                                .findFirst();
                        if (industry.isPresent()) {
                            cell.symbol = "I";
                            cell.type = CellType.INDUSTRY;
                            cell.entityName = industry.get().getNameID();
                            if (scenario != null) {
                                cell.industryType = industry.get().getType();
                            }
                        } else {
                            // Empty cell
                            cell.symbol = ".";
                            cell.type = CellType.EMPTY;
                        }
                    }
                }

                data.grid[rowY][colX] = cell;
            }
        }

        return data;
    }

    // Helper classes for structured data
    public static class MapLayoutData {
        public String mapName;
        public String scenarioName;
        public String startDate;
        public String endDate;
        public int width;
        public int height;
        public CellData[][] grid;
    }

    public static class CellData {
        public String symbol;
        public CellType type;
        public String entityName;
        public String demandedCargo;
        public String suppliedCargo;
        public String industryType;
    }

    public enum CellType {
        EMPTY, CITY, INDUSTRY, STATION
    }
}