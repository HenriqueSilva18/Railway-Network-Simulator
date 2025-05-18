package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;
import java.util.Optional;

public class EditMapController {
    // ANSI color codes
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String BLUE = "\u001B[34m";
    
    private final MapRepository mapRepository;
    private Map currentMap;

    public EditMapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public boolean loadMap(Map map) {
        if (map == null) {
            return false;
        }
        this.currentMap = map;
        ApplicationSession.getInstance().setCurrentMap(map);
        return true;
    }

    public String getMapLayout() {
        if (currentMap == null) {
            return "No map loaded";
        }

        StringBuilder layout = new StringBuilder();
        layout.append("Map: ").append(currentMap.getNameID()).append("\n");
        layout.append("Size: ").append(currentMap.getSize().getWidth())
              .append("x").append(currentMap.getSize().getHeight()).append("\n\n");

        // Add legend
        layout.append("Legend:\n");
        layout.append(RED + "C" + RESET + " - City\n");
        layout.append(BLUE + "I" + RESET + " - Industry\n");
        layout.append(GREEN + "S" + RESET + " - Station\n");
        layout.append(". - Empty cell\n\n");

        // Create the map layout
        for (int y = 0; y < currentMap.getSize().getHeight(); y++) {
            for (int x = 0; x < currentMap.getSize().getWidth(); x++) {
                String symbol = getCellSymbol(x, y);
                layout.append(symbol).append(" ");
            }
            layout.append("  ");
            
            // Add labels for cities and industries on this row
            appendRowLabels(layout, y);
            layout.append("\n");
        }

        return layout.toString();
    }

    private String getCellSymbol(int x, int y) {
        // Check for city
        Optional<City> city = currentMap.getCities().stream()
                .filter(c -> c.getPosition().getX() == x && c.getPosition().getY() == y)
                .findFirst();
        if (city.isPresent()) {
            return RED + "C" + RESET;
        }

        // Check for industry
        Optional<Industry> industry = currentMap.getIndustries().stream()
                .filter(i -> i.getPosition().getX() == x && i.getPosition().getY() == y)
                .findFirst();
        if (industry.isPresent()) {
            return BLUE + "I" + RESET;
        }

        // Empty cell
        return ".";
    }

    private void appendRowLabels(StringBuilder layout, int y) {
        // Add city names
        currentMap.getCities().stream()
                .filter(city -> city.getPosition().getY() == y)
                .forEach(city -> layout.append("(City: ").append(city.getNameID()).append(") "));

        // Add industry names
        currentMap.getIndustries().stream()
                .filter(industry -> industry.getPosition().getY() == y)
                .forEach(industry -> layout.append("(Industry: ").append(industry.getNameID()).append(") "));
    }

    public Map getCurrentMap() {
        return ApplicationSession.getInstance().getCurrentMap();
    }
} 