package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.controller.template.ViewScenarioLayoutController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Locomotive;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;
import java.util.HashSet;
import java.util.Set;

public class ViewScenarioLayoutUI implements Runnable {

    private final MapController mapController;
    private final EditorRepository editorRepository;
    private final ScenarioRepository scenarioRepository;
    private final ViewScenarioLayoutController viewController;

    public ViewScenarioLayoutUI() {
        this.mapController = new MapController();
        this.editorRepository = Repositories.getInstance().getEditorRepository();
        this.scenarioRepository = Repositories.getInstance().getScenarioRepository();
        this.viewController = new ViewScenarioLayoutController();
    }

    @Override
    public void run() {
        System.out.println("\n=== View Scenario Layout ===\n");

        // Check if a map is currently loaded
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            System.out.println("No map is currently loaded. Please load a map first.");
            return;
        }

        // Get all scenarios from both repositories
        List<Scenario> editorScenarios = editorRepository.getAllScenarios();
        List<Scenario> scenarioRepoScenarios = scenarioRepository.getAllScenarios();
        
        // Combine scenarios from both repositories while avoiding duplicates
        Set<String> scenarioIds = new HashSet<>();
        List<Scenario> allScenarios = new ArrayList<>();
        
        // Add from editor repository
        for (Scenario scenario : editorScenarios) {
            if (!scenarioIds.contains(scenario.getNameID())) {
                allScenarios.add(scenario);
                scenarioIds.add(scenario.getNameID());
            }
        }
        
        // Add from scenario repository
        for (Scenario scenario : scenarioRepoScenarios) {
            if (!scenarioIds.contains(scenario.getNameID())) {
                allScenarios.add(scenario);
                scenarioIds.add(scenario.getNameID());
            }
        }
        
        // Filter scenarios that belong to the current map and are properly loaded
        List<Scenario> availableScenarios = allScenarios.stream()
            .filter(s -> s != null && 
                        s.getMap() != null && 
                        s.getMap().getNameID().equals(currentMap.getNameID()) &&
                        !s.getNameID().equals("scenario3") && 
                        !s.getNameID().equals("scenario4"))
            .collect(Collectors.toList());

        if (availableScenarios.isEmpty()) {
            System.out.println("No scenarios available for the current map. Please load a scenario first.");
            return;
        }

        // Create a mapping of display names to scenarios
        LinkedHashMap<String, Scenario> scenarioDisplayMap = new LinkedHashMap<>();
        for (Scenario scenario : availableScenarios) {
            String displayName;
            if (scenario.getNameID().equals("scenario1")) {
                displayName = currentMap.getNameID().equals("italy") ? "Italian Giolitti Era" :
                             currentMap.getNameID().equals("france") ? "French Belle Époque" :
                             currentMap.getNameID().equals("iberian_peninsula") ? "Iberian Early Industrial" : scenario.getNameID();
            } else if (scenario.getNameID().equals("scenario2")) {
                displayName = currentMap.getNameID().equals("italy") ? "Italian Inter-War" :
                             currentMap.getNameID().equals("france") ? "French Reconstruction" :
                             currentMap.getNameID().equals("iberian_peninsula") ? "Iberian Inter-War" : scenario.getNameID();
            } else {
                displayName = scenario.getNameID();
            }
            scenarioDisplayMap.put(displayName, scenario);
        }

        // Display available scenarios
        System.out.println("Available scenarios for " + currentMap.getNameID() + ":");
        List<String> displayNames = new ArrayList<>(scenarioDisplayMap.keySet());
        for (int i = 0; i < displayNames.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, displayNames.get(i));
        }
        System.out.println("0. Return to previous menu");

        // Get user selection
        System.out.print("\nType your option: ");
        int choice = Utils.readIntegerFromConsole("Invalid option. Please try again: ");
        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > displayNames.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedDisplayName = displayNames.get(choice - 1);
        Scenario selectedScenario = scenarioDisplayMap.get(selectedDisplayName);

        // Set the selected scenario as current
        ApplicationSession.getInstance().setCurrentScenario(selectedScenario);

        // Display scenario layout
        displayScenarioLayout(currentMap, selectedDisplayName);
    }

    private void displayScenarioLayout(Map map, String displayName) {
        System.out.println("\n=== Scenario Layout: " + displayName + " ===");
        
        // Get the current scenario from ApplicationSession
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        
        if (currentScenario != null) {
            // Use the new controller to render the map with unique labels
            String layoutText = viewController.renderScenarioLayout(map, currentScenario);
            System.out.println(layoutText);
            
            // Display summary information about the scenario
            System.out.println("\nScenario Summary:");
            System.out.println("Date Range: " + formatYear(currentScenario.getStartDate()) + " to " + 
                             formatYear(currentScenario.getEndDate()));
            
            // Display cities with names
            List<City> cities = currentScenario.getTweakedCityList();
            System.out.println("\nCities (" + cities.size() + "):");
            for (City city : cities) {
                System.out.println("- " + city.getNameID());
            }
            
            // Display industries with names and types
            List<Industry> industries = currentScenario.getAvailableIndustryList();
            System.out.println("\nIndustries (" + industries.size() + "):");
            for (Industry industry : industries) {
                System.out.println("- " + industry.getNameID() + " (" + industry.getType() + ")");
            }
            
            // Display available locomotive types
            List<Locomotive> locomotives = currentScenario.getAvailableLocomotives();
            System.out.println("\nAvailable Locomotive Types (" + locomotives.size() + "):");
            for (Locomotive locomotive : locomotives) {
                System.out.println("- " + locomotive.getType());
            }
        } else {
            // Fall back to simple display if no scenario is in session
            System.out.println("Map: " + map.getNameID());
            System.out.println("Size: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
            
            System.out.println("\nCities:");
            if (map.getCities().isEmpty()) {
                System.out.println("No cities in this map.");
            } else {
                map.getCities().forEach(city -> 
                    System.out.println("- " + city.getNameID() + " at position (" + 
                        city.getPosition().getX() + "," + city.getPosition().getY() + ")"));
            }

            System.out.println("\nIndustries:");
            if (map.getIndustries().isEmpty()) {
                System.out.println("No industries in this map.");
            } else {
                map.getIndustries().forEach(industry -> 
                    System.out.println("- " + industry.getNameID() + " (" + industry.getType() + ") at position (" + 
                        industry.getPosition().getX() + "," + industry.getPosition().getY() + ")"));
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        Utils.readLineFromConsole("");
    }

    private String formatYear(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return String.valueOf(cal.get(Calendar.YEAR));
    }
} 