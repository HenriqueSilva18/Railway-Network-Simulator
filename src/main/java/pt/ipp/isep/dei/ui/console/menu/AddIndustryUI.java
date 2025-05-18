package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.AddIndustryController;
import pt.ipp.isep.dei.controller.template.AuthenticationController;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Map;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;
import java.util.Scanner;

public class AddIndustryUI implements Runnable {
    private final AddIndustryController controller;
    private final AuthenticationController authController;
    private final Scanner scanner;

    public AddIndustryUI() {
        this.controller = new AddIndustryController();
        this.authController = new AuthenticationController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        if (!isUserEditor()) {
            System.out.println("\nError: Only users with Editor role can add industries.");
            return;
        }

        System.out.println("\n=== Add Industry ===");
        List<Industry> availableIndustries = controller.getAvailableIndustries();

        if (availableIndustries.isEmpty()) {
            System.out.println("No industries available to add.");
            return;
        }

        System.out.println("\nAvailable Industries:");
        for (int i = 0; i < availableIndustries.size(); i++) {
            Industry industry = availableIndustries.get(i);
            System.out.printf("%d. %s (%s)%n", i + 1, industry.getNameID(), industry.getType());
        }

        System.out.print("\nSelect an industry to add (number): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice < 1 || choice > availableIndustries.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Industry selectedIndustry = availableIndustries.get(choice - 1);
        System.out.println("\nEnter industry details:");
        
        System.out.print("Name ID: ");
        String nameID = scanner.nextLine();

        Map currentMap = controller.getCurrentMap();
        int maxX = currentMap.getSize().getWidth() - 1;
        int maxY = currentMap.getSize().getHeight() - 1;

        int x = -1;
        int y = -1;
        boolean validX = false;
        boolean validY = false;

        // Get X coordinate
        while (!validX) {
            try {
                System.out.printf("X coordinate (0-%d): ", maxX);
                x = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (x < 0 || x > maxX) {
                    System.out.println("\nError: X coordinate out of bounds!");
                    System.out.printf("X must be between 0 and %d%n", maxX);
                    continue;
                }
                validX = true;
            } catch (Exception e) {
                System.out.println("\nError: Please enter a valid number for X coordinate.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        // Get Y coordinate
        while (!validY) {
            try {
                System.out.printf("Y coordinate (0-%d): ", maxY);
                y = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (y < 0 || y > maxY) {
                    System.out.println("\nError: Y coordinate out of bounds!");
                    System.out.printf("Y must be between 0 and %d%n", maxY);
                    continue;
                }
                validY = true;
            } catch (Exception e) {
                System.out.println("\nError: Please enter a valid number for Y coordinate.");
                scanner.nextLine(); // Clear the invalid input
            }
        }

        // Check if position is occupied
        if (!currentMap.isCellEmpty(x, y)) {
            System.out.println("\nError: Position is already occupied!");
            System.out.println("Please choose different coordinates.");
            return;
        }

        if (controller.validateIndustry(nameID, x, y)) {
            System.out.println("\nConfirm industry details:");
            System.out.println("Name ID: " + nameID);
            System.out.println("Position: (" + x + "," + y + ")");
            System.out.print("\nConfirm? (y/n): ");
            
            String confirmation = scanner.nextLine();
            if (confirmation.equalsIgnoreCase("y")) {
                Industry createdIndustry = controller.createIndustry(nameID, x, y);
                if (createdIndustry != null) {
                    System.out.println("\nIndustry added successfully!");
                    showIndustryDetails(createdIndustry);
                } else {
                    System.out.println("\nFailed to add industry.");
                }
            } else {
                System.out.println("\nOperation cancelled.");
            }
        } else {
            System.out.println("\nInvalid industry data. Please check the coordinates and try again.");
        }
    }

    private boolean isUserEditor() {
        List<UserRoleDTO> userRoles = authController.getUserRoles();
        if (userRoles == null) {
            return false;
        }
        return userRoles.stream()
                .anyMatch(role -> role.getDescription().equals(AuthenticationController.ROLE_EDITOR));
    }

    private void showIndustryDetails(Industry industry) {
        System.out.println("\nIndustry Details:");
        System.out.println("Name ID: " + industry.getNameID());
        System.out.println("Type: " + industry.getType());
        System.out.println("Position: (" + industry.getPosition().getX() + "," + industry.getPosition().getY() + ")");
        System.out.println("Production Rate: " + industry.getProductionRate());
    }
} 