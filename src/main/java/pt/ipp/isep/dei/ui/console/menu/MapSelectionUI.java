package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class MapSelectionUI implements Runnable {
    private final MapController controller;
    private final Scanner scanner;

    public MapSelectionUI() {
        this.controller = new MapController();
        this.scanner = new Scanner(System.in);
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

        // Display available maps with numbers
        System.out.println("Available maps:");
        for (int i = 0; i < availableMaps.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, availableMaps.get(i).getNameID());
        }
        System.out.println("0. Return to previous menu");

        // Get user selection
        System.out.print("\nType your option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > availableMaps.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Map selectedMap = availableMaps.get(choice - 1);

        // Get scenarios for selected map
        List<String> scenarioIDs = controller.getMapScenarios(selectedMap.getNameID());
        if (scenarioIDs.isEmpty()) {
            System.out.println("No scenarios available for this map.");
            return;
        }

        // Create a mapping of display names to scenario IDs
        LinkedHashMap<String, String> scenarioDisplayMap = new LinkedHashMap<>();
        for (String id : scenarioIDs) {
            String displayName;
            if (id.equals("scenario1")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Giolitti Era" :
                             selectedMap.getNameID().equals("france") ? "French Belle Époque" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Early Industrial" :
                             selectedMap.getNameID().equals("british_isles") ? "British Edwardian Era" :
                             selectedMap.getNameID().equals("north_america") ? "American Progressive Era" : id;
            } else if (id.equals("scenario2")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Inter-War" :
                             selectedMap.getNameID().equals("france") ? "French Reconstruction" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Inter-War" :
                             selectedMap.getNameID().equals("british_isles") ? "British Inter-War" :
                             selectedMap.getNameID().equals("north_america") ? "American Roaring Twenties" : id;
            } else if (id.equals("scenario3")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Economic Miracle" :
                             selectedMap.getNameID().equals("france") ? "French Les Trente Glorieuses" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Economic Miracle" :
                             selectedMap.getNameID().equals("british_isles") ? "British Nationalisation" :
                             selectedMap.getNameID().equals("north_america") ? "American Post-War Boom" : id;
            } else if (id.equals("scenario4")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Modern Network" :
                             selectedMap.getNameID().equals("france") ? "French Modern Network" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Modern Era" :
                             selectedMap.getNameID().equals("british_isles") ? "British Modernisation" :
                             selectedMap.getNameID().equals("north_america") ? "American Modern Era" : id;
            } else {
                displayName = id;
            }
            scenarioDisplayMap.put(displayName, id);
        }

        // Display available scenarios with numbers
        System.out.println("\nAvailable scenarios for " + selectedMap.getNameID() + ":");
        List<String> displayNames = new ArrayList<>(scenarioDisplayMap.keySet());
        for (int i = 0; i < displayNames.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, displayNames.get(i));
        }
        System.out.println("0. Return to previous menu");

        // Get user selection
        System.out.print("\nType your option: ");
        choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > displayNames.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedDisplayName = displayNames.get(choice - 1);
        String selectedScenario = scenarioDisplayMap.get(selectedDisplayName);

        // Load the selected map and scenario
        if (controller.loadMap(selectedMap.getNameID(), selectedScenario)) {
            // Initialize the budget for this scenario
            Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayer.initializeScenarioBudget(selectedScenario);
            }
            System.out.println("Map and scenario loaded successfully.");
        } else {
            System.out.println("Failed to load map and scenario.");
        }
    }

    private void displayMapSummary(Map map) {
        System.out.println("\nMap: " + map.getNameID());
        System.out.println("Size: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
        System.out.println("Cities: " + map.getCities().size());
        System.out.println("Industries: " + map.getIndustries().size());
        System.out.println("Stations: " + map.getStations().size());
    }
} 