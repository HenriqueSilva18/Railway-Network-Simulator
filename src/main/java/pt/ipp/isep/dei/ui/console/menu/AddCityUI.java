package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.AddCityController;
import pt.ipp.isep.dei.controller.template.AddHouseBlocksController;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.HouseBlock;
import pt.ipp.isep.dei.domain.template.Position;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AddCityUI implements Runnable {
    private final AddCityController controller;
    private final AddHouseBlocksController houseBlocksController;
    private final Scanner scanner;

    public AddCityUI() {
        this.controller = new AddCityController();
        this.houseBlocksController = new AddHouseBlocksController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        try {
            System.out.println("\n=== Add City ===");
            
            // Get city name
            String nameID = readCityName();
            
            // Get coordinates
            Position position = readCoordinates();
            
            // Get number of blocks
            int numBlocks = readNumBlocks();
            
            // Ask for block placement preference
            System.out.print("\nDo you want to assign blocks manually? (y/n): ");
            boolean manualPlacement = scanner.nextLine().equalsIgnoreCase("y");
            
            // Assign blocks
            List<HouseBlock> houseBlocks;
            if (manualPlacement) {
                houseBlocks = assignManualBlocks(numBlocks);
            } else {
                houseBlocks = assignAutomaticBlocks(numBlocks);
            }
            
            // Show summary and confirm
            showCitySummary(nameID, position, houseBlocks);
            System.out.print("\nConfirm city creation? (y/n): ");
            if (scanner.nextLine().equalsIgnoreCase("y")) {
                City city = controller.saveCity(nameID, position, houseBlocks);
                if (city != null) {
                    System.out.println("\nCity created successfully!");
                    showCityDetails(city);
                } else {
                    System.out.println("\nFailed to create city.");
                }
            } else {
                System.out.println("\nOperation cancelled.");
            }
            
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private String readCityName() {
        while (true) {
            try {
                System.out.print("\nEnter city name: ");
                String name = scanner.nextLine();
                return controller.validateName(name);
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private Position readCoordinates() {
        while (true) {
            try {
                System.out.print("Enter X coordinate: ");
                int x = Integer.parseInt(scanner.nextLine());
                System.out.print("Enter Y coordinate: ");
                int y = Integer.parseInt(scanner.nextLine());
                return controller.validateCoordinates(x, y);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter valid numbers for coordinates.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private int readNumBlocks() {
        while (true) {
            try {
                System.out.print("Enter number of house blocks: ");
                int numBlocks = Integer.parseInt(scanner.nextLine());
                return controller.validateNumBlocks(numBlocks);
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private List<HouseBlock> assignManualBlocks(int numBlocks) {
        List<HouseBlock> blocks = new ArrayList<>();
        for (int i = 0; i < numBlocks; i++) {
            System.out.printf("\nAssigning block %d of %d%n", i + 1, numBlocks);
            Position position = readCoordinates();
            blocks.add(new HouseBlock(position, false));
        }
        return blocks;
    }

    private List<HouseBlock> assignAutomaticBlocks(int numBlocks) {
        return houseBlocksController.assignBlocks(numBlocks);
    }

    private void showCitySummary(String nameID, Position position, List<HouseBlock> houseBlocks) {
        System.out.println("\nCity Summary:");
        System.out.println("Name: " + nameID);
        System.out.println("Position: (" + position.getX() + "," + position.getY() + ")");
        System.out.println("Number of house blocks: " + houseBlocks.size());
    }

    private void showCityDetails(City city) {
        System.out.println("\nCity Details:");
        System.out.println("Name: " + city.getNameID());
        System.out.println("Position: (" + city.getPosition().getX() + "," + city.getPosition().getY() + ")");
        System.out.println("Number of house blocks: " + city.getHouseBlocks().size());
        System.out.println("Traffic rate: " + city.getTrafficRate());
    }
} 