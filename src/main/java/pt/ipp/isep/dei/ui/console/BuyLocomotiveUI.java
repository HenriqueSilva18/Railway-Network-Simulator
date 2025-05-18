package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.BuyLocomotiveController;
import pt.ipp.isep.dei.domain.template.Locomotive;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class BuyLocomotiveUI implements Runnable {
    private final BuyLocomotiveController controller;

    public BuyLocomotiveUI() {
        this.controller = new BuyLocomotiveController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Buy a Locomotive ===\n");

        // Get available locomotives
        List<Locomotive> availableLocomotives = controller.getAvailableLocomotives();

        if (availableLocomotives.isEmpty()) {
            System.out.println("No locomotives are available for purchase. This could be because:");
            System.out.println("1. No map or scenario is selected");
            System.out.println("2. There are no locomotives available for the current scenario");
            System.out.println("3. No locomotives are available for the current date");
            return;
        }

        // Display available locomotives with price
        System.out.println("Available locomotives:");
        for (int i = 0; i < availableLocomotives.size(); i++) {
            Locomotive loco = availableLocomotives.get(i);
            System.out.printf("%d. %s - Type: %s - Price: %.2f - Max Speed: %.1f - Power: %.1f%n",
                    i + 1, loco.getNameID(), loco.getType(), loco.getPrice(), loco.getTopSpeed(), loco.getPower());
        }

        // Let user select a locomotive
        Locomotive selectedLocomotive = (Locomotive) Utils.selectsObject(availableLocomotives);
        if (selectedLocomotive == null) {
            System.out.println("Purchase cancelled.");
            return;
        }

        // Show details and confirm purchase
        System.out.println("\nLocomotive Details:");
        System.out.println("Name: " + selectedLocomotive.getNameID());
        System.out.println("Type: " + selectedLocomotive.getType());
        System.out.println("Power: " + selectedLocomotive.getPower());
        System.out.println("Max Speed: " + selectedLocomotive.getTopSpeed() + " km/h");
        System.out.println("Fuel Cost: " + selectedLocomotive.getFuelCost());
        System.out.println("Maintenance Cost: " + selectedLocomotive.getMaintenancePrice() + " per year");
        System.out.println("Price: " + selectedLocomotive.getPrice());

        // Ask for confirmation
        boolean confirm = Utils.confirm("Do you want to purchase this locomotive?");
        if (!confirm) {
            System.out.println("Purchase cancelled.");
            return;
        }

        // Complete purchase
        if (controller.purchaseLocomotive(selectedLocomotive)) {
            System.out.println("\nPurchase successful! The locomotive is now in your fleet.");
        } else {
            System.out.println("\nPurchase failed. Please check that you have enough funds.");
        }
    }
} 