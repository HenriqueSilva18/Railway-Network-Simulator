package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.domain.City;
import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.ui.console.BuildStationUI;
import pt.ipp.isep.dei.ui.console.DisplayMapUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import java.util.ArrayList;
import java.util.List;

public class PlayerUI implements Runnable {
    private final Repositories repositories = Repositories.getInstance();

    @Override
    public void run() {
        // Load all available maps
        List<Map> availableMaps = repositories.getMapRepository().getAllMaps();

        if (availableMaps.isEmpty()) {
            System.out.println("\nNo maps available. Please ask an editor to create one.");
            Utils.readLineFromConsole("Press Enter to return...");
            return;
        }

        // Main player loop
        while (true) {
            // Display map selection
            System.out.println("\n\n--- AVAILABLE MAPS -------------------");
            int mapChoice = Utils.showAndSelectIndex(availableMaps, "Select a map (or -1 to exit):");

            if (mapChoice == -1) break; // Exit player menu

            Map selectedMap = availableMaps.get(mapChoice);
            handleMapActions(selectedMap);
        }
    }

    private void handleMapActions(Map map) {
        // Player actions menu
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Build Station", new BuildStationUI(map)));
        options.add(new MenuItem("View Map Topology", new DisplayMapUI(map)));
        options.add(new MenuItem("Map Statistics", () -> displayMapStatistics(map)));
        options.add(new MenuItem("List All Stations", () -> listStations(map)));

        int actionChoice;
        do {
            System.out.println("\n=== Managing Map: " + map.getNameID() + " ===");
            actionChoice = Utils.showAndSelectIndex(options, "\n--- MAP ACTIONS -----------------------");
            if (actionChoice >= 0 && actionChoice < options.size()) {
                options.get(actionChoice).run();
            }
        } while (actionChoice != -1);
    }

    private void displayMapStatistics(Map map) {
        System.out.println("\n--- MAP STATISTICS -------------------");
        System.out.println("Name: " + map.getNameID());
        System.out.println("Dimensions: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
        System.out.println("Cities: " + map.getCities().size());
        System.out.println("Industries: " + map.getIndustries().size());
        System.out.println("Stations: " + map.getStations().size());

        if (!map.getStations().isEmpty()) {
            System.out.println("\nStation Coverage:");
            map.getCities().forEach(city ->
                    System.out.printf("- %s: %s\n", city.getName(),
                            hasNearbyStation(city, map) ? "Served" : "No station")
            );
        }

        Utils.readLineFromConsole("\nPress Enter to continue...");
    }

    private boolean hasNearbyStation(City city, Map map) {
        return map.getStations().stream()
                .anyMatch(station ->
                        Math.abs(station.getXCoordinate() - city.getXCoordinate()) < 50 &&
                                Math.abs(station.getYCoordinate() - city.getYCoordinate()) < 50
                );
    }

    private void listStations(Map map) {
        System.out.println("\n--- STATIONS LIST -------------------");
        if (map.getStations().isEmpty()) {
            System.out.println("No stations built yet.");
        } else {
            map.getStations().forEach(station ->
                    System.out.printf("- %s at (%d,%d)\n",
                            station.getName(),
                            station.getXCoordinate(),
                            station.getYCoordinate())
            );
        }
        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
}