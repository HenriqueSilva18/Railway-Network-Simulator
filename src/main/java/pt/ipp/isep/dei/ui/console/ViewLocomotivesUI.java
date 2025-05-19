package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuyLocomotiveController;
import pt.ipp.isep.dei.domain.template.Locomotive;
import pt.ipp.isep.dei.domain.template.Train;
import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.domain.template.Cargo;

import java.util.*;
import java.util.stream.Collectors;

public class ViewLocomotivesUI implements Runnable {
    private final BuyLocomotiveController controller;

    public ViewLocomotivesUI() {
        this.controller = new BuyLocomotiveController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Your Locomotive Fleet ===\n");

        // Get player's locomotives
        List<Locomotive> locomotives = controller.getPlayerLocomotives();
        
        if (locomotives.isEmpty()) {
            System.out.println("You don't own any locomotives yet.");
            System.out.println("Use the 'Buy Locomotive' option to purchase locomotives for your fleet.");
            return;
        }

        // Group locomotives by type and sort by name within each type
        Map<String, List<Locomotive>> locomotivesByType = locomotives.stream()
                .collect(Collectors.groupingBy(
                    Locomotive::getType,
                    Collectors.toList()
                ));
        
        // Sort the map keys (locomotive types) for consistent display order
        List<String> sortedTypes = new ArrayList<>(locomotivesByType.keySet());
        Collections.sort(sortedTypes);
        
        int totalLocomotives = 0;
        
        // Display locomotives grouped by type
        for (String type : sortedTypes) {
            List<Locomotive> locos = locomotivesByType.get(type);
            
            // Sort locomotives of this type by name
            locos.sort(Comparator.comparing(Locomotive::getNameID));
            
            System.out.println("\n=== " + type + " Locomotives (" + locos.size() + ") ===");
            
            // Display header for this type
            System.out.printf("%-4s | %-20s | %-8s | %-10s | %-10s | %-15s | %-15s%n", 
                    "No.", "Name", "Power", "Max Speed", "Fuel Cost", "Maintenance", "Route");
            System.out.println("-".repeat(105));
            
            // Display locomotives of this type
            int i = 1;
            for (Locomotive loco : locos) {
                // Get the train that uses this locomotive
                Train train = controller.getTrainForLocomotive(loco);
                String routeName = "None";
                
                if (train != null && train.getAssignedRoute() != null) {
                    routeName = train.getAssignedRoute().getNameID();
                }
                
                System.out.printf("%-4d | %-20s | %-8d | %-10d | %-10.2f | %-15.2f | %-15s%n", 
                    i++, 
                    loco.getNameID(), 
                    loco.getPower(),
                    loco.getTopSpeed(),
                    loco.getFuelCost(),
                    loco.getMaintenancePrice(),
                    routeName);
                
                // If the locomotive is assigned to a route, display cargo information
                if (train != null && train.getAssignedRoute() != null) {
                    displayCargoInformation(train);
                }
            }
            
            totalLocomotives += locos.size();
        }
        
        System.out.println("\nTotal locomotives: " + totalLocomotives);
        
        // Wait for user input to continue
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
    
    /**
     * Displays cargo information for a train based on its assigned route
     */
    private void displayCargoInformation(Train train) {
        if (train == null || train.getAssignedRoute() == null) {
            return;
        }
        
        Route route = train.getAssignedRoute();
        
        // Get all cargo from the route
        Map<Station, List<Cargo>> stationCargo = route.getStationCargo();
        
        // Check if there's any cargo
        boolean hasCargo = false;
        for (List<Cargo> cargoList : stationCargo.values()) {
            if (!cargoList.isEmpty()) {
                hasCargo = true;
                break;
            }
        }
        
        if (!hasCargo) {
            System.out.println("      No cargo available on this route.");
            return;
        }
        
        System.out.println("      Cargo on route " + route.getNameID() + ":");
        
        // Display cargo information by station
        for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
            Station station = entry.getKey();
            List<Cargo> cargoList = entry.getValue();
            
            if (cargoList.isEmpty()) {
                continue;
            }
            
            System.out.println("      └── Station: " + station.getNameID());
            
            // Group cargo by type
            Map<String, Integer> cargoByType = new HashMap<>();
            for (Cargo cargo : cargoList) {
                cargoByType.put(cargo.getType(), 
                               cargoByType.getOrDefault(cargo.getType(), 0) + cargo.getAmount());
            }
            
            // Display cargo types and total amounts
            for (Map.Entry<String, Integer> cargoEntry : cargoByType.entrySet()) {
                System.out.printf("          ├── %s: %d tons%n", 
                                 cargoEntry.getKey(), cargoEntry.getValue());
            }
        }
    }
} 