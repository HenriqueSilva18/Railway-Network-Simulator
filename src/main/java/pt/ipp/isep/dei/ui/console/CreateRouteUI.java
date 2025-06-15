package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.CreateRouteController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class CreateRouteUI implements Runnable {
    private final CreateRouteController controller;

    public CreateRouteUI() {
        this.controller = new CreateRouteController();
    }

    private Station selectStation(List<Station> stations, String prompt) {
        // Display available stations
        System.out.println("\nAvailable stations:");
        for (int i = 0; i < stations.size(); i++) {
            System.out.printf("%d - %s\n", i + 1, stations.get(i).getNameID());
        }

        while (true) {
            try {
                System.out.println("\n" + prompt + " (1-" + stations.size() + ", 0 to cancel):");
                String input = Utils.readLineFromConsole("Enter station number");
                
                if (input == null || input.trim().isEmpty() || input.trim().equals("0")) {
                    return null;
                }

                int index = Integer.parseInt(input.trim()) - 1;
                if (index >= 0 && index < stations.size()) {
                    return stations.get(index);
                }
                System.out.println("Invalid station number. Please enter a number between 1 and " + stations.size());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }

    private CargoMode selectCargoMode(String stationName) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                    CARGO MODE SELECTION                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ Station: " + String.format("%-50s", stationName) + "║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 1. FULL                                                       ║");
        System.out.println("║    The train will wait at this station until all carriages     ║");
        System.out.println("║    are fully loaded before departing                           ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 2. HALF                                                        ║");
        System.out.println("║    The train will depart from this station as soon as at       ║");
        System.out.println("║    least 50% of its carriages are loaded                       ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        System.out.println("║ 3. AVAILABLE                                                   ║");
        System.out.println("║    The train will depart from this station with whatever       ║");
        System.out.println("║    cargo is currently available, regardless of capacity        ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");

        while (true) {
            try {
                String input = Utils.readLineFromConsole("\nEnter your choice (1-3)");
                if (input == null || input.trim().isEmpty()) {
                    continue;
                }

                int choice = Integer.parseInt(input.trim());
                switch (choice) {
                    case 1:
                        return CargoMode.FULL;
                    case 2:
                        return CargoMode.HALF;
                    case 3:
                        return CargoMode.AVAILABLE;
                    default:
                        System.out.println("\n❌ Invalid choice. Please enter a number between 1 and 3.");
                }
            } catch (NumberFormatException e) {
                System.out.println("\n❌ Please enter a valid number.");
            }
        }
    }

    @Override
    public void run() {
        System.out.println("\n=== Create New Route ===\n");

        // Get available stations
        List<Station> allStations = controller.getAvailableStations();
        if (allStations.size() < 2) {
            System.out.println("You need at least 2 stations to create a route.");
            return;
        }

        // Create list for route stations
        List<Station> routeStations = new ArrayList<>();
        List<CargoMode> cargoModes = new ArrayList<>();

        // Select first station
        Station startStation = selectStation(allStations, "Select the starting station");
        if (startStation == null) {
            System.out.println("Operation cancelled.");
            return;
        }
        routeStations.add(startStation);
        CargoMode startMode = selectCargoMode(startStation.getNameID());
        cargoModes.add(startMode);
        displayRouteStatus(routeStations, cargoModes);

        // Keep adding stations until user is done
        boolean addingStations = true;
        while (addingStations) {
            // Get remaining stations that are connected to the last added station
            List<Station> connectedStations = controller.getConnectedStations(routeStations.get(routeStations.size() - 1));
            connectedStations.removeAll(routeStations); // Remove already selected stations

            if (connectedStations.isEmpty()) {
                System.out.println("\nNo more connected stations available.");
                break;
            }

            // Select next station
            Station nextStation = selectStation(connectedStations, "Select the next station");
            if (nextStation == null) {
                if (routeStations.size() >= 2) {
                    addingStations = false;
                } else {
                    System.out.println("Route must have at least 2 stations. Please select another station.");
                }
                continue;
            }

            routeStations.add(nextStation);
            CargoMode nextMode = selectCargoMode(nextStation.getNameID());
            cargoModes.add(nextMode);
            displayRouteStatus(routeStations, cargoModes);
        }

        // Get route name
        String routeName = Utils.readLineFromConsole("Enter route name");
        if (routeName == null || routeName.trim().isEmpty()) {
            System.out.println("Invalid route name. Operation cancelled.");
            return;
        }

        // Create the route
        Route route = controller.createRoute(routeName, routeStations, cargoModes);
        if (route != null) {
            System.out.println("\nRoute created successfully!");
            System.out.println(route);
        } else {
            System.out.println("\nFailed to create route.");
        }
    }

    private void displayRouteStatus(List<Station> stations, List<CargoMode> modes) {
        System.out.println("\n╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║                      CURRENT ROUTE STATUS                      ║");
        System.out.println("╠════════════════════════════════════════════════════════════════╣");
        
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            CargoMode mode = modes.get(i);
            String stationInfo = String.format("║ %d. %-30s │ %-20s ║", 
                i + 1, 
                station.getNameID(),
                mode.toString());
            System.out.println(stationInfo);
        }
        
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
    }
} 