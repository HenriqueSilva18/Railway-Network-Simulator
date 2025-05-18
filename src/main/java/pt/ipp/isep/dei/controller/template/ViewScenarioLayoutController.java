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
                String symbol = getCellSymbol(map, x, y);
                layout.append(symbol).append(" ");
            }
            layout.append("  ");
            
            // Add labels for cities and industries on this row without duplication
            appendRowLabels(layout, map, y);
            layout.append("\n");
        }

        return layout.toString();
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
} 