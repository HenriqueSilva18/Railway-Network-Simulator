package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.AuthenticationController;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.ui.console.ShowTextUI;
import pt.ipp.isep.dei.ui.console.StationBuildingUI;
import pt.ipp.isep.dei.ui.console.ViewMapUI;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PlayerUI implements Runnable {
    private final AuthenticationController authController;

    public PlayerUI() {
        this.authController = new AuthenticationController();
    }

    public void run() {
        // Verify if there's a player in the current session
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            System.out.println("Error: No player found in current session.");
            return;
        }

        int option = 0;
        do {
            List<MenuItem> options = new ArrayList<MenuItem>();
            
            // Always show the map selection option
            options.add(new MenuItem("Select Map and Scenario", new MapSelectionUI()));
            
            // Check if a map has been selected
            Map currentMap = ApplicationSession.getInstance().getCurrentMap();
            if (currentMap != null) {
                // Only show these options if a map is selected
                options.add(new MenuItem("Build Station", new StationBuildingUI()));
                options.add(new MenuItem("View Current Map", new ViewMapUI()));
                options.add(new MenuItem("View Budget", new ShowTextUI("Current budget: " + getCurrentPlayerBudget())));
                options.add(new MenuItem("View Stations", new ShowTextUI("Station list functionality will be implemented here.")));
                options.add(new MenuItem("View Network Statistics", new ShowTextUI("Network statistics will be implemented here.")));
            }
            
            // Always show the logout option
            options.add(new MenuItem("Logout", () -> {
                authController.doLogout();
                System.out.println("Logged out successfully.");
            }));

            System.out.println("\n=== Player Menu ===");
            // Only show budget if a map is selected
            if (currentMap != null) {
                System.out.println("Current Budget: " + getCurrentPlayerBudget());
            }
            System.out.println("==================\n");
            
            option = Utils.showAndSelectIndex(options, "\n\n--- PLAYER MENU -------------------------");

            if ((option >= 0) && (option < options.size())) {
                options.get(option).run();
            }
        } while (option != -1);
    }

    private String getCurrentPlayerBudget() {
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            return String.format("%.2f", currentPlayer.getCurrentBudget());
        }
        return "N/A";
    }
} 