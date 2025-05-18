package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.CreateRouteController;
import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.Station;
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

        // Select first station
        Station startStation = selectStation(allStations, "Select the starting station");
        if (startStation == null) {
            System.out.println("Operation cancelled.");
            return;
        }
        routeStations.add(startStation);

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
        }

        // Get route name
        String routeName = Utils.readLineFromConsole("Enter route name");
        if (routeName == null || routeName.trim().isEmpty()) {
            System.out.println("Invalid route name. Operation cancelled.");
            return;
        }

        // Create the route
        Route route = controller.createRoute(routeName, routeStations);
        if (route != null) {
            System.out.println("\nRoute created successfully!");
            System.out.println(route);
        } else {
            System.out.println("\nFailed to create route.");
        }
    }
} 