package pt.ipp.isep.dei.ui.gui.utils;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import pt.ipp.isep.dei.domain.template.Building;
import pt.ipp.isep.dei.domain.template.Locomotive;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SimulationNotificationHelper {
    private static final Set<Integer> shownBuildingNotifications = new HashSet<>();

    public static void showBuildingAvailabilityNotification(int currentYear, List<Building> newlyAvailableBuildings) {
        // Skip if we've already shown the notification for this year
        if (shownBuildingNotifications.contains(currentYear)) {
            return;
        }

        if (!newlyAvailableBuildings.isEmpty()) {
            StringBuilder message = new StringBuilder("New buildings available in " + currentYear + ":\n\n");
            for (Building building : newlyAvailableBuildings) {
                message.append("• ").append(building.getNameID())
                      .append(" (").append(building.getType()).append(")\n")
                      .append("  Cost: ").append(String.format("%.2f", building.getCost()))
                      .append("\n  Effect: ").append(building.getEffect())
                      .append("\n\n");
            }

            // Show notification in JavaFX thread
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("New Buildings Available");
                alert.setHeaderText("New buildings available in " + currentYear);
                alert.setContentText(message.toString());
                alert.getDialogPane().setPrefWidth(700);
                alert.getDialogPane().setPrefHeight(500);
                alert.showAndWait();
            });

            // Mark this year's notification as shown
            shownBuildingNotifications.add(currentYear);
        }
    }

    public static void showLocomotiveAvailabilityNotification(int year, Locomotive locomotive) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("New Locomotive Available");
            alert.setHeaderText("New Locomotive Model Available in " + year);
            alert.setContentText(String.format("The %s locomotive model is now available for purchase!\n\n" +
                    "Type: %s\n" +
                    "Power: %d HP\n" +
                    "Top Speed: %d km/h\n" +
                    "Price: $%.2f",
                    locomotive.getNameID(),
                    locomotive.getType(),
                    locomotive.getPower(),
                    (int)locomotive.getTopSpeed(),
                    locomotive.getPrice()));
            
            // Add custom styling
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.getStyleClass().add("locomotive-notification");
            dialogPane.setStyle("-fx-background-color: #f0f8ff;");
            
            alert.show();
        });
    }
} 