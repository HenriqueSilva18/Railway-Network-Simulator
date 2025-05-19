package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuyLocomotiveController;
import pt.ipp.isep.dei.domain.template.Locomotive;

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
            System.out.printf("%-4s | %-20s | %-8s | %-10s | %-10s | %-15s%n", 
                    "No.", "Name", "Power", "Max Speed", "Fuel Cost", "Maintenance");
            System.out.println("-".repeat(85));
            
            // Display locomotives of this type
            int i = 1;
            for (Locomotive loco : locos) {
                System.out.printf("%-4d | %-20s | %-8d | %-10d | %-10.2f | %-15.2f%n", 
                    i++, 
                    loco.getNameID(), 
                    loco.getPower(),
                    loco.getTopSpeed(),
                    loco.getFuelCost(),
                    loco.getMaintenancePrice());
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
} 