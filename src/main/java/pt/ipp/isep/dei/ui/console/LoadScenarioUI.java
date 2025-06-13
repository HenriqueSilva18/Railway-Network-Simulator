package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ScenarioController;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class LoadScenarioUI implements Runnable {
    private final ScenarioController scenarioController;
    private final MapController mapController;

    public LoadScenarioUI() {
        this.scenarioController = new ScenarioController();
        this.mapController = new MapController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Load Scenario ===\n");

        // Get available scenario files
        List<String> availableScenarios = scenarioController.getAvailableScenarioFiles();
        if (availableScenarios.isEmpty()) {
            System.out.println("No saved scenarios available.");
            return;
        }

        // Display available scenarios
        System.out.println("Select a scenario to load:");
        for (String scenarioId : availableScenarios) {
            System.out.println("- " + scenarioId);
        }

        // Select scenario
        String selectedScenarioId = (String) Utils.selectsObject(availableScenarios);
        if (selectedScenarioId == null) {
            System.out.println("Scenario selection cancelled.");
            return;
        }

        // Load scenario
        Scenario loadedScenario = scenarioController.loadScenario(selectedScenarioId);
        if (loadedScenario != null) {
            // Set the scenario in the scenario controller
            scenarioController.setCurrentScenario(loadedScenario);
            
            // Get the associated map
            Map associatedMap = loadedScenario.getMap();
            if (associatedMap != null) {
                // Load the map and set it as current
                Map loadedMap = mapController.loadMap(associatedMap.getNameID());
                if (loadedMap != null) {
                    mapController.setCurrentMap(loadedMap);
                    
                    // Add the scenario to the map's list of scenarios if not already present
                    if (!loadedMap.getScenarios().contains(selectedScenarioId)) {
                        loadedMap.addScenario(selectedScenarioId);
                        mapController.saveMap(loadedMap);
                    }
                    
                    // Set the scenario in the application session
                    ApplicationSession.getInstance().setCurrentScenario(loadedScenario);
                    
                    System.out.println("\nScenario loaded successfully!");
                    displayScenario(loadedScenario);
                } else {
                    System.out.println("Failed to load associated map.");
                }
            } else {
                System.out.println("Warning: Loaded scenario has no associated map.");
            }
        } else {
            System.out.println("Failed to load scenario.");
        }
    }

    private void displayScenario(Scenario scenario) {
        System.out.println("\nScenario: " + scenario.getNameID());
        System.out.println("Display Name: " + scenario.getDisplayName());
        System.out.println("Start Date: " + scenario.getStartDate());
        System.out.println("End Date: " + scenario.getEndDate());
        System.out.println("Map: " + (scenario.getMap() != null ? scenario.getMap().getNameID() : "Not set"));
        System.out.println("Cities: " + scenario.getTweakedCityList().size());
        System.out.println("Industries: " + scenario.getAvailableIndustryList().size());
        System.out.println("Locomotives: " + scenario.getAvailableLocomotives().size());
    }
} 