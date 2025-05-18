package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.EditMapController;
import pt.ipp.isep.dei.controller.template.AuthenticationController;
import pt.ipp.isep.dei.domain.template.Map;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;
import java.util.Scanner;

public class EditMapUI implements Runnable {
    private final EditMapController controller;
    private final AuthenticationController authController;
    private final Scanner scanner;

    public EditMapUI() {
        this.controller = new EditMapController();
        this.authController = new AuthenticationController();
        this.scanner = new Scanner(System.in);
    }

    @Override
    public void run() {
        if (!isUserEditor()) {
            System.out.println("\nError: Only users with Editor role can edit maps.");
            return;
        }

        System.out.println("\n=== Edit Map ===");
        List<Map> availableMaps = controller.getAvailableMaps();

        if (availableMaps.isEmpty()) {
            System.out.println("No maps available to edit.");
            return;
        }

        System.out.println("\nAvailable Maps:");
        for (int i = 0; i < availableMaps.size(); i++) {
            Map map = availableMaps.get(i);
            System.out.printf("%d. %s%n", i + 1, map.getNameID());
        }
        System.out.println("\n0. Return to previous menu");

        System.out.print("\nSelect a map to edit (number): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 0) {
            return;
        }

        if (choice < 1 || choice > availableMaps.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Map selectedMap = availableMaps.get(choice - 1);
        if (controller.loadMap(selectedMap)) {
            System.out.println("\nMap loaded successfully!");
            System.out.println(controller.getMapLayout());
            
            while (true) {
                System.out.println("\nEdit Map Options:");
                System.out.println("1. Add Industry");
                System.out.println("2. Add City");
                System.out.println("0. Return to Main Menu");

                System.out.print("\nSelect an option: ");
                int option = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (option) {
                    case 1:
                        new AddIndustryUI().run();
                        break;
                    case 2:
                        new AddCityUI().run();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid option.");
                }
            }
        } else {
            System.out.println("\nFailed to load map.");
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

    private void showMapDetails(Map map) {
        System.out.println("\nMap Details:");
        System.out.println("Name: " + map.getNameID());
        System.out.println("Size: " + map.getSize().getWidth() + "x" + map.getSize().getHeight());
        System.out.println("Number of Industries: " + map.getIndustries().size());
    }
} 