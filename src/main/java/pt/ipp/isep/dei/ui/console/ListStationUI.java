package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.ListStationController;
import pt.ipp.isep.dei.domain.template.Cargo;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.CargoMode;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.ui.console.menu.MenuItem;

import java.util.*;

public class ListStationUI implements Runnable {

    private final ListStationController controller;

    public ListStationUI() {
        this.controller = new ListStationController();
    }

    @Override
    public void run() {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        
        if (currentMap == null) {
            System.out.println("No map selected. Please select a map first.");
            return;
        }
        
        System.out.println("\n=== Station List ===");
        
        // Get all stations from the current map
        List<Station> stations = controller.getStations();
        
        if (stations.isEmpty()) {
            System.out.println("No stations found on the current map.");
            return;
        }
        
        // Display the list of stations with a summary
        displayStationList(stations);
        
        // Ask user to select a station
        int option = Utils.showAndSelectIndex(getStationOptions(stations), "\nSelect a station to view details:");
        
        if (option >= 0 && option < stations.size()) {
            // Selected station
            Station selectedStation = stations.get(option);
            
            // Set the selected station in the application session
            ApplicationSession.getInstance().setCurrentStation(selectedStation);
            
            // Display details for the selected station
            displayStationDetails(selectedStation);
            
            // After viewing details, show station action menu
            showStationActionMenu();
        }
    }
    
    private void showStationActionMenu() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return;
        }
        
        List<MenuItem> options = new ArrayList<>();
        options.add(new MenuItem("Upgrade Station", new UpgradeStationUI()));
        options.add(new MenuItem("Return to Station List", () -> {}));
        
        System.out.println("\n=== Station Actions for " + currentStation.getNameID() + " ===");
        int option = Utils.showAndSelectIndex(options, "\nChoose an action:");
        
        if (option >= 0 && option < options.size()) {
            options.get(option).run();
        }
    }
    
    private void displayStationList(List<Station> stations) {
        System.out.println("\nAvailable stations:");
        System.out.println("-------------------");
        
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            System.out.printf("%d. %s (Type: %s)\n", 
                (i+1), 
                station.getNameID(), 
                station.getStationType().getName());
            
            // Display cargo mode if the station is part of a route
            Route route = station.getAssignedRoute();
            if (route != null) {
                CargoMode mode = route.getCargoModeForStation(station);
                if (mode != null) {
                    System.out.printf("   Cargo Mode: %s\n", mode);
                }
            }
            
            // Display summary of available cargo
            List<Cargo> availableCargo = station.getAvailableCargo();
            System.out.print("   Supply: ");
            if (availableCargo.isEmpty()) {
                System.out.print("None");
            } else {
                System.out.print(summarizeCargo(availableCargo));
            }
            
            // Display summary of requested cargo
            List<Cargo> requestedCargo = station.getRequestedCargo();
            System.out.print("\n   Demand: ");
            if (requestedCargo.isEmpty()) {
                System.out.print("None");
            } else {
                System.out.print(summarizeCargo(requestedCargo));
            }
            System.out.println("\n");
        }
    }
    
    private String summarizeCargo(List<Cargo> cargoList) {
        StringBuilder summary = new StringBuilder();
        for (int i = 0; i < cargoList.size(); i++) {
            Cargo cargo = cargoList.get(i);
            summary.append(cargo.getName()).append(" (").append(cargo.getAmount()).append(")");
            
            if (i < cargoList.size() - 1) {
                summary.append(", ");
            }
        }
        return summary.toString();
    }
    
    private List<String> getStationOptions(List<Station> stations) {
        List<String> options = new ArrayList<>();
        for (Station station : stations) {
            options.add(station.getNameID());
        }
        return options;
    }
    
    private void displayStationDetails(Station station) {
        System.out.println("\n=== Station Details ===");
        System.out.printf("Name: %s\n", station.getNameID());
        System.out.printf("Type: %s\n", station.getStationType().getName());
        System.out.printf("Position: (%d, %d)\n", 
            station.getPosition().getX(), 
            station.getPosition().getY());
        
        // Calculate current storage usage
        int currentStorage = station.getAvailableCargo().stream()
            .mapToInt(Cargo::getAmount)
            .sum();
        System.out.printf("Storage Capacity: %d/%d units\n", 
            currentStorage,
            station.getStorageCapacity());
        System.out.printf("Building Slots: %d\n", station.getBuildingSlots());
        
        // Display served cities (prevent duplicates)
        System.out.println("\nServed Cities:");
        if (station.getServedCities().isEmpty()) {
            System.out.println("None");
        } else {
            // Use a set to track which city names have already been displayed
            Set<String> displayedCities = new HashSet<>();
            station.getServedCities().forEach(city -> {
                String cityName = city.getNameID();
                if (displayedCities.add(cityName)) {
                    System.out.printf("- %s\n", cityName);
                }
            });
        }
        
        // Display available cargo grouped by type
        System.out.println("\nAvailable Cargo (Supply):");
        List<Cargo> availableCargo = station.getAvailableCargo();
        if (availableCargo.isEmpty()) {
            System.out.println("None");
        } else {
            // Group cargo by type and calculate total amount for each type
            java.util.Map<String, Integer> cargoByType = new HashMap<>();
            for (Cargo cargo : availableCargo) {
                cargoByType.merge(cargo.getType(), cargo.getAmount(), Integer::sum);
            }
            
            // Display total amount for each cargo type
            cargoByType.forEach((type, totalAmount) -> 
                System.out.printf("- %s: %d units\n", type, totalAmount));
        }
        
        // Display requested cargo grouped by type
        System.out.println("\nRequested Cargo (Demand):");
        List<Cargo> requestedCargo = station.getRequestedCargo();
        if (requestedCargo.isEmpty()) {
            System.out.println("None");
        } else {
            // Group cargo by type and calculate total amount for each type
            java.util.Map<String, Integer> cargoByType = new HashMap<>();
            for (Cargo cargo : requestedCargo) {
                cargoByType.merge(cargo.getType(), cargo.getAmount(), Integer::sum);
            }
            
            // Display total amount for each cargo type
            cargoByType.forEach((type, totalAmount) -> 
                System.out.printf("- %s: %d units\n", type, totalAmount));
        }
    }
} 