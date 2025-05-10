package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.domain.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;

public class BuildStationUI implements Runnable {
    private final Map currentMap;

    public BuildStationUI(Map map) {
        this.currentMap = map;
    }

    @Override
    public void run() {
        System.out.println("\n--- BUILD STATION -----------------");
        System.out.println("Current Map: " + currentMap.getNameID());

        // Get station details from player
        String stationName = Utils.readLineFromConsole("Enter station name: ");
        int x = Utils.readIntegerFromConsole("Enter X coordinate: ");
        int y = Utils.readIntegerFromConsole("Enter Y coordinate: ");

        // Validate coordinates are within map bounds
        if (x < 0 || x >= currentMap.getSize().getWidth() ||
                y < 0 || y >= currentMap.getSize().getHeight()) {
            System.out.println("Error: Coordinates are outside map boundaries!");
            return;
        }

        // Create and add station
        Station newStation = new Station(stationName, x, y);
        currentMap.addStation(newStation);

        System.out.println("Station '" + stationName + "' built at (" + x + "," + y + ")");
    }
}