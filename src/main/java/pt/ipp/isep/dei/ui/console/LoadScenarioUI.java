package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ScenarioController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.util.ArrayList;

public class LoadScenarioUI implements Runnable {
    private final MapController mapController;
    private final ScenarioController scenarioController;
    private final Scanner scanner;
    private final ScenarioRepository scenarioRepository;
    private final EditorRepository editorRepository;

    public LoadScenarioUI() {
        this.mapController = new MapController();
        this.scenarioController = new ScenarioController();
        this.scanner = new Scanner(System.in);
        this.scenarioRepository = Repositories.getInstance().getScenarioRepository();
        this.editorRepository = Repositories.getInstance().getEditorRepository();
    }

    @Override
    public void run() {
        System.out.println("\n=== Load Scenario ===\n");

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

        // Create the full scenario file name
        String scenarioFileName = selectedMap.getNameID() + "_" + selectedScenarioId;

        // Load the selected scenario
        Scenario loadedScenario = scenarioController.loadScenario(scenarioFileName);
        if (loadedScenario != null) {
            // Ensure the map is set on the scenario
            loadedScenario.setMap(selectedMap);
            
            // Add to scenario repository
            scenarioRepository.save(loadedScenario);
            
            // Add to editor repository
            editorRepository.addScenarioToEditor(
                ApplicationSession.getInstance().getCurrentEditor(), 
                loadedScenario
            );
            
            // Set as current scenario in both repositories
            scenarioRepository.setCurrentScenario(loadedScenario);
            ApplicationSession.getInstance().setCurrentScenario(loadedScenario);

            System.out.println("\nScenario loaded successfully!");
            System.out.println("Scenario: " + loadedScenario.getDisplayName());
            System.out.println("Map: " + loadedScenario.getMap().getNameID());
            System.out.println("Time Period: " + loadedScenario.getStartDate() + " - " + loadedScenario.getEndDate());
        } else {
            System.out.println("Failed to load scenario.");
        }
    }
} 