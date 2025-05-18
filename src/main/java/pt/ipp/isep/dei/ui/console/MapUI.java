package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class MapUI implements Runnable {
    private final MapController controller;

    public MapUI() {
        this.controller = new MapController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Map Selection ===\n");

        // Get available maps
        List<Map> availableMaps = controller.getAvailableMaps();
        if (availableMaps.isEmpty()) {
            System.out.println("No maps available.");
            return;
        }

        // Display available maps
        System.out.println("Available maps:");
        for (Map map : availableMaps) {
            System.out.println("- " + map.getNameID());
        }

        // Select map
        Map selectedMap = (Map) Utils.selectsObject(availableMaps);
        if (selectedMap == null) {
            System.out.println("Map selection cancelled.");
            return;
        }

        // Get scenarios for selected map
        List<String> scenarios = controller.getMapScenarios(selectedMap.getNameID());
        if (scenarios.isEmpty()) {
            System.out.println("No scenarios available for this map.");
            return;
        }

        // Display available scenarios
        System.out.println("\nAvailable scenarios for " + selectedMap.getNameID() + ":");
        for (String scenario : scenarios) {
            System.out.println("- " + scenario);
        }

        // Select scenario
        String selectedScenario = (String) Utils.selectsObject(scenarios);
        if (selectedScenario == null) {
            System.out.println("Scenario selection cancelled.");
            return;
        }

        // Load map with selected scenario
        if (controller.loadMap(selectedMap.getNameID(), selectedScenario)) {
            System.out.println("\nMap loaded successfully!");
            displayMap(selectedMap);
        } else {
            System.out.println("Failed to load map.");
        }
    }

    private void displayMap(Map map) {
        System.out.println("\nMap: " + map.getNameID());
        System.out.println("Size: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
        System.out.println("Cities: " + map.getCities().size());
        System.out.println("Industries: " + map.getIndustries().size());
        System.out.println("Stations: " + map.getStations().size());
    }
} 