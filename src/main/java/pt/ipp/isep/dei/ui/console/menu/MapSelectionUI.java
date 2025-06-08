package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.SimulatorController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Scanner;
import java.util.LinkedHashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class MapSelectionUI implements Runnable {
    private final MapController controller;
    private final SimulatorController simulatorController;
    private final Scanner scanner;

    public MapSelectionUI() {
        this.controller = new MapController();
        this.simulatorController = new SimulatorController();
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

        // Filter out scenario3 and scenario4 for all maps
        scenarioIDs = scenarioIDs.stream()
            .filter(id -> !id.equals("scenario3") && !id.equals("scenario4"))
            .collect(Collectors.toList());

        // Create a mapping of display names to scenario IDs
        LinkedHashMap<String, String> scenarioDisplayMap = new LinkedHashMap<>();
        for (String id : scenarioIDs) {
            String displayName;
            if (id.equals("scenario1")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Giolitti Era" :
                             selectedMap.getNameID().equals("france") ? "French Belle Époque" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Early Industrial" : id;
            } else if (id.equals("scenario2")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Inter-War" :
                             selectedMap.getNameID().equals("france") ? "French Reconstruction" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Inter-War" : id;
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
        String selectedScenarioId = scenarioDisplayMap.get(selectedDisplayName);

        // Load the selected map and scenario
        if (controller.loadMap(selectedMap.getNameID(), selectedScenarioId)) {
            // Initialize the budget for this scenario
            Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
            if (currentPlayer != null) {
                currentPlayer.initializeScenarioBudget(selectedScenarioId);
            }
            System.out.println("Map and scenario loaded successfully.");
            
            // Get the loaded scenario
            Scenario selectedScenario = ApplicationSession.getInstance().getCurrentScenario();
            if (selectedScenario != null) {
                // Start the simulator automatically
                if (simulatorController.startSimulation(selectedMap, selectedScenario)) {
                    System.out.println("\nSimulation started automatically!");
                    System.out.println("The simulation will run in the background while you continue playing.");
                    System.out.println("You can check the simulation status and control it through the main menu.");
                } else {
                    System.out.println("Failed to start simulation automatically.");
                }
            }
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