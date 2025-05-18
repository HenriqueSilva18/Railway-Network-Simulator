package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuyLocomotiveController;
import pt.ipp.isep.dei.domain.template.Locomotive;

import java.util.List;

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

        // Display locomotives in a table format
        System.out.printf("%-4s | %-20s | %-10s | %-8s | %-10s | %-10s | %-15s%n", 
                "No.", "Name", "Type", "Power", "Max Speed", "Fuel Cost", "Maintenance");
        System.out.println("-".repeat(90));
        
        int i = 1;
        for (Locomotive loco : locomotives) {
            System.out.printf("%-4d | %-20s | %-10s | %-8.1f | %-10.1f | %-10.2f | %-15.2f%n", 
                i++, 
                loco.getNameID(), 
                loco.getType(), 
                loco.getPower(),
                loco.getTopSpeed(),
                loco.getFuelCost(),
                loco.getMaintenancePrice());
        }
        
        System.out.println("\nTotal locomotives: " + locomotives.size());
        
        // Wait for user input to continue
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();
        } catch (Exception e) {
            // Ignore exceptions
        }
    }
} 