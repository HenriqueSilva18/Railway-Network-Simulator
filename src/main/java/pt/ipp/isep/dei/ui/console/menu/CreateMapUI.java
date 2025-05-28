package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.CreateMapController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.utils.Utils;

public class CreateMapUI implements Runnable {
    private final CreateMapController controller;

    public CreateMapUI() {
        this.controller = new CreateMapController();
    }

    @Override
    public void run() {
        System.out.println("\n--- Create Map ---");
        
        String nameID;
        do {
            nameID = Utils.readLineFromConsole("Enter map name: ");
            if (!Map.validateMapName(nameID)) {
                System.out.println("\nError: Invalid map name. The name must:");
                System.out.println("- Not be empty");
                System.out.println("- Not have just an hyphen or underscore");
                System.out.println("- Not finish with a hyphen or underscore");
                System.out.println("- Contain only letters, numbers, underscores, and hyphens");
                System.out.println("Please try again.\n");
            }
        } while (!Map.validateMapName(nameID));

        int width = -1;
        do {
            String widthInput = Utils.readLineFromConsole("Enter map width: ");
            if (widthInput == null || widthInput.trim().isEmpty()) {
                System.out.println("\nError: Width cannot be empty.");
                System.out.println("Please try again.\n");
                continue;
            }
            try {
                width = Integer.parseInt(widthInput);
                if (width <= 0) {
                    System.out.println("\nError: Width must be a positive number.");
                    System.out.println("Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nError: Width must be a valid number.");
                System.out.println("Please try again.\n");
                width = -1; // Force loop to continue
            }
        } while (width <= 0);

        int height = -1;
        do {
            String heightInput = Utils.readLineFromConsole("Enter map height: ");
            if (heightInput == null || heightInput.trim().isEmpty()) {
                System.out.println("\nError: Height cannot be empty.");
                System.out.println("Please try again.\n");
                continue;
            }
            try {
                height = Integer.parseInt(heightInput);
                if (height <= 0) {
                    System.out.println("\nError: Height must be a positive number.");
                    System.out.println("Please try again.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("\nError: Height must be a valid number.");
                System.out.println("Please try again.\n");
                height = -1; // Force loop to continue
            }
        } while (height <= 0);

        System.out.println("\nMap details:");
        System.out.println("Name: " + nameID);
        System.out.println("Width: " + width);
        System.out.println("Height: " + height);

        if (Utils.confirm("Do you want to create this map? (y/n)")) {
            try {
                Map map = controller.createMap(nameID, width, height);
                System.out.println("\nMap created successfully!");
                System.out.println("Map details:");
                System.out.println("Name: " + map.getNameID());
                System.out.println("Size: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
            } catch (Exception e) {
                System.out.println("Error creating map: " + e.getMessage());
            }
        } else {
            System.out.println("Map creation cancelled.");
        }
    }
} 