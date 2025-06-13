package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class LoadMapUI implements Runnable {
    private final MapController controller;

    public LoadMapUI() {
        this.controller = new MapController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Load Map ===\n");

        // Get available map files
        List<String> availableMaps = controller.getAvailableMapFiles();
        if (availableMaps.isEmpty()) {
            System.out.println("No saved maps available.");
            return;
        }

        // Display available maps
        System.out.println("Select a map to load:");
        for (String mapId : availableMaps) {
            System.out.println("- " + mapId);
        }

        // Select map
        String selectedMapId = (String) Utils.selectsObject(availableMaps);
        if (selectedMapId == null) {
            System.out.println("Map selection cancelled.");
            return;
        }

        // Load map
        Map loadedMap = controller.loadMap(selectedMapId);
        if (loadedMap != null) {
            controller.setCurrentMap(loadedMap);
            System.out.println("\nMap loaded successfully!");
            displayMap(loadedMap);
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