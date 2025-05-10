package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.domain.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import java.util.List;

public class BuildConnectionUI implements Runnable {
    private final Map map;

    public BuildConnectionUI(Map map) {
        this.map = map;
    }

    @Override
    public void run() {
        System.out.println("\n--- BUILD RAILWAY CONNECTION -----------------");
        List<Station> stations = map.getStations();

        // Select source station
        System.out.println("\nSelect SOURCE station:");
        int sourceIdx = Utils.showAndSelectIndex(stations, "Available stations:");
        if (sourceIdx == -1) return;

        // Select destination station
        System.out.println("\nSelect DESTINATION station:");
        int destIdx = Utils.showAndSelectIndex(stations, "Available stations:");
        if (destIdx == -1 || destIdx == sourceIdx) return;

        Station source = stations.get(sourceIdx);
        Station destination = stations.get(destIdx);

        // Get connection details
        boolean electrified = Utils.confirm("Should this connection be electrified? (y/n)");
        String name = Utils.readLineFromConsole("Enter connection name: ");

        // Confirm and build
        System.out.printf("\nAbout to build connection: %s (%s ↔ %s)%n",
                name, source.getName(), destination.getName());
        System.out.printf("Length: %.1f units | Electrified: %s%n",
                calculateDistance(source, destination),
                electrified ? "YES" : "NO");

        if (Utils.confirm("Confirm construction? (y/n)")) {
            try {
                map.addRailwayConnection(source, destination, name, electrified);
                System.out.println("Connection built successfully!");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private double calculateDistance(Station s1, Station s2) {
        int dx = s2.getXCoordinate() - s1.getXCoordinate();
        int dy = s2.getYCoordinate() - s1.getYCoordinate();
        return Math.sqrt(dx*dx + dy*dy);
    }
}