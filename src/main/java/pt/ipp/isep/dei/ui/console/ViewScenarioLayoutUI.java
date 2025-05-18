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
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.LinkedHashMap;

public class ViewScenarioLayoutUI implements Runnable {

    private final MapController mapController;
    private final EditorRepository editorRepository;
    private final ViewScenarioLayoutController viewController;

    public ViewScenarioLayoutUI() {
        this.mapController = new MapController();
        this.editorRepository = Repositories.getInstance().getEditorRepository();
        this.viewController = new ViewScenarioLayoutController();
    }

    @Override
    public void run() {
        System.out.println("\n=== View Scenario Layout ===\n");

        // Get available maps
        List<Map> maps = mapController.getAvailableMaps();
        if (maps.isEmpty()) {
            System.out.println("No maps available.");
            return;
        }

        // Show available maps
        System.out.println("Available Maps:");
        Map selectedMap = (Map) Utils.showAndSelectOne(maps, "Select a map to view scenarios:");
        
        if (selectedMap == null) {
            return;
        }

        // Get scenarios for selected map
        List<String> scenarioIDs = mapController.getMapScenarios(selectedMap.getNameID());
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
                             selectedMap.getNameID().equals("north_america") ? "American Progressive Era" :
                             selectedMap.getNameID().equals("japan") ? "Japanese Meiji Era" :
                             selectedMap.getNameID().equals("scandinavia") ? "Nordic Industrial Revolution" :
                             selectedMap.getNameID().equals("central_europe") ? "Central European Industrial Age" : id;
            } else if (id.equals("scenario2")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Inter-War" :
                             selectedMap.getNameID().equals("france") ? "French Reconstruction" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Inter-War" :
                             selectedMap.getNameID().equals("british_isles") ? "British Inter-War" :
                             selectedMap.getNameID().equals("north_america") ? "American Roaring Twenties" :
                             selectedMap.getNameID().equals("japan") ? "Japanese Imperial Period" :
                             selectedMap.getNameID().equals("scandinavia") ? "Nordic Interwar Period" :
                             selectedMap.getNameID().equals("central_europe") ? "Central European Reconstruction" : id;
            } else if (id.equals("scenario3")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Economic Miracle" :
                             selectedMap.getNameID().equals("france") ? "French Les Trente Glorieuses" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Economic Miracle" :
                             selectedMap.getNameID().equals("british_isles") ? "British Nationalisation" :
                             selectedMap.getNameID().equals("north_america") ? "American Post-War Boom" :
                             selectedMap.getNameID().equals("japan") ? "Japanese Economic Miracle" :
                             selectedMap.getNameID().equals("scandinavia") ? "Nordic Welfare State" :
                             selectedMap.getNameID().equals("central_europe") ? "Central European Rebuilding" : id;
            } else if (id.equals("scenario4")) {
                displayName = selectedMap.getNameID().equals("italy") ? "Italian Modern Network" :
                             selectedMap.getNameID().equals("france") ? "French Modern Network" :
                             selectedMap.getNameID().equals("iberian_peninsula") ? "Iberian Modern Era" :
                             selectedMap.getNameID().equals("british_isles") ? "British Modernisation" :
                             selectedMap.getNameID().equals("north_america") ? "American Modern Era" :
                             selectedMap.getNameID().equals("japan") ? "Japanese Bullet Train Era" :
                             selectedMap.getNameID().equals("scandinavia") ? "Nordic Modern Networks" :
                             selectedMap.getNameID().equals("central_europe") ? "Central European Modern Network" : id;
            } else {
                displayName = id;
            }
            scenarioDisplayMap.put(displayName, id);
        }

        // Show available scenarios with their display names
        System.out.println("\nAvailable Scenarios for " + selectedMap.getNameID() + ":");
        List<String> displayNames = new ArrayList<>(scenarioDisplayMap.keySet());
        String selectedDisplayName = (String) Utils.showAndSelectOne(displayNames, "Select a scenario to view layout:");

        if (selectedDisplayName == null) {
            return;
        }

        // Get the actual scenario ID from the selected display name
        String selectedScenarioID = scenarioDisplayMap.get(selectedDisplayName);

        // Load the selected scenario
        if (!mapController.loadMap(selectedMap.getNameID(), selectedScenarioID)) {
            System.out.println("Failed to load scenario.");
            return;
        }

        // Get the loaded map from the session
        Map loadedMap = ApplicationSession.getInstance().getCurrentMap();
        if (loadedMap == null) {
            System.out.println("Error: Could not retrieve loaded map.");
            return;
        }

        // Display scenario layout
        displayScenarioLayout(loadedMap, selectedDisplayName);
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
            
            System.out.println("\nCities: " + currentScenario.getTweakedCityList().size());
            System.out.println("Industries: " + currentScenario.getAvailableIndustryList().size());
            System.out.println("Available Locomotive Types: " + currentScenario.getAvailableLocomotives().size());
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