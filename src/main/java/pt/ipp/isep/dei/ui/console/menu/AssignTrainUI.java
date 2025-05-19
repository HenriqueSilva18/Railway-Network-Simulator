package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.AssignTrainController;
import pt.ipp.isep.dei.controller.template.MapController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;
import java.util.Map;

public class AssignTrainUI implements Runnable {
    private final AssignTrainController assignTrainController;
    private final MapController mapController;

    public AssignTrainUI() {
        this.assignTrainController = new AssignTrainController();
        this.mapController = new MapController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Assign Train to Route ===\n");

        // Get available routes
        List<Route> availableRoutes = assignTrainController.getAvailableRoutes();
        
        // If no routes, inform the user and return
        if (availableRoutes.isEmpty()) {
            System.out.println("No routes available. Please create a route first.");
            Utils.readLineFromConsole("\nPress Enter to continue...");
            return;
        }

        // Display routes and let the user select one
        System.out.println("Available Routes:");
        Route selectedRoute = (Route) Utils.showAndSelectOne(availableRoutes, "Select a route:");
        
        if (selectedRoute == null) {
            System.out.println("No route selected. Returning to menu.");
            return;
        }

        // Show route details
        RouteDetails routeDetails = assignTrainController.getRouteDetails(selectedRoute);
        System.out.println("\nRoute Details:");
        System.out.println(routeDetails);

        // Confirm route selection
        if (!Utils.confirm("\nConfirm route selection? (y/n)")) {
            System.out.println("Route selection cancelled. Returning to menu.");
            return;
        }

        // Get available trains
        List<Train> availableTrains = assignTrainController.getAvailableTrains();
        
        // If no trains, inform the user and return
        if (availableTrains.isEmpty()) {
            System.out.println("No trains available. Please purchase a locomotive first.");
            Utils.readLineFromConsole("\nPress Enter to continue...");
            return;
        }

        // Display trains and let the user select one
        System.out.println("\nAvailable Trains:");
        Train selectedTrain = (Train) Utils.showAndSelectOne(availableTrains, "Select a train:");
        
        if (selectedTrain == null) {
            System.out.println("No train selected. Returning to menu.");
            return;
        }

        // Show train details
        TrainDetails trainDetails = assignTrainController.getTrainDetails(selectedTrain);
        System.out.println("\nTrain Details:");
        System.out.println(trainDetails);

        // Confirm train selection
        if (!Utils.confirm("\nConfirm train selection? (y/n)")) {
            System.out.println("Train selection cancelled. Returning to menu.");
            return;
        }

        // Assign the train to the route
        if (assignTrainController.assignTrainToRoute(selectedRoute, selectedTrain)) {
            System.out.println("\nTrain successfully assigned to route!");
            
            // Display assigned train and route details
            System.out.println("\n=== Assignment Details ===");
            System.out.println("\nRoute: " + selectedRoute.getNameID());
            System.out.println("Train: " + selectedTrain.getNameID());
            
            // Show cargo to be picked up at each station
            Map<Station, List<Cargo>> stationCargo = assignTrainController.getCargoesToPickUp(selectedRoute);
            System.out.println("\nCargo to be picked up at stations:");
            
            if (stationCargo.isEmpty()) {
                System.out.println("No cargo to pick up on this route.");
            } else {
                for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
                    Station station = entry.getKey();
                    List<Cargo> cargoList = entry.getValue();
                    
                    System.out.println("\nStation: " + station.getNameID());
                    
                    if (cargoList.isEmpty()) {
                        System.out.println("  No cargo to pick up");
                    } else {
                        System.out.println("  Cargo to pick up:");
                        for (Cargo cargo : cargoList) {
                            System.out.println("  - " + cargo);
                        }
                    }
                }
            }
        } else {
            System.out.println("\nFailed to assign train to route. Please check that both are valid.");
        }

        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
} 