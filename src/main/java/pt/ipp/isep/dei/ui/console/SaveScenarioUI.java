package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ScenarioController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Scanner;
import java.util.stream.Collectors;

public class SaveScenarioUI implements Runnable {
    private final MapController mapController;
    private final ScenarioController scenarioController;
    private final Scanner scanner;

    public SaveScenarioUI() {
        this.mapController = new MapController();
        this.scenarioController = new ScenarioController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        System.out.println("\n=== Save Scenario ===\n");

        // Get available maps
        List<Map> maps = mapController.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return;
        }

        // Show maps and let user select one
        System.out.println("Available maps:");
        for (int i = 0; i < maps.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, maps.get(i).getNameID());
        }
        System.out.println("0. Return to previous menu");

        System.out.print("\nType your option: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > maps.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Map selectedMap = maps.get(choice - 1);

        // Get scenarios for the selected map
        List<String> scenarioIds = mapController.getMapScenarios(selectedMap.getNameID());
        if (scenarioIds.isEmpty()) {
            System.out.println("No scenarios available for the selected map.");
            return;
        }

        // Filter out scenario3 and scenario4 for all maps
        scenarioIds = scenarioIds.stream()
            .filter(id -> !id.equals("scenario3") && !id.equals("scenario4"))
            .collect(Collectors.toList());

        // Create a mapping of display names to scenario IDs
        LinkedHashMap<String, String> scenarioDisplayMap = new LinkedHashMap<>();
        for (String id : scenarioIds) {
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

        // Get the selected scenario using MapController
        Scenario selectedScenario = mapController.getScenario(selectedScenarioId);
        if (selectedScenario == null) {
            System.out.println("Failed to load the selected scenario.");
            return;
        }

        // Create a filename that includes both map and scenario names
        String fileName = selectedMap.getNameID() + "_" + selectedScenarioId;
        selectedScenario.setNameID(fileName);

        // Save selected scenario
        if (scenarioController.saveScenario(selectedScenario)) {
            System.out.println("\nScenario saved successfully!");
            System.out.println("Saved as: " + fileName + ".scenario");
        } else {
            System.out.println("Failed to save scenario.");
        }
    }
} 