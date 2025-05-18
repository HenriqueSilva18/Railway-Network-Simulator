package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.StationBuildingController;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.List;

public class StationBuildingUI implements Runnable {
    private final StationBuildingController controller;

    public StationBuildingUI() {
        this.controller = new StationBuildingController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Build Station ===\n");

        // Get station type
        List<StationType> stationTypes = controller.getStationTypes();
        if (stationTypes.isEmpty()) {
            System.out.println("No station types available.");
            return;
        }

        System.out.println("Available station types:");
        for (StationType type : stationTypes) {
            System.out.printf("%s (Cost: %.2f, Radius: %d)%n", 
                type.getName(), type.getCost(), type.getEconomicRadius());
        }

        StationType selectedType = (StationType) Utils.selectsObject(stationTypes);
        if (selectedType == null) {
            System.out.println("Station type selection cancelled.");
            return;
        }

        // Get position
        System.out.println("\nEnter station position:");
        int x = Utils.readIntegerFromConsole("X coordinate: ");
        int y = Utils.readIntegerFromConsole("Y coordinate: ");
        Position position = new Position(x, y);

        if (!controller.validatePosition(position)) {
            System.out.println("Invalid position. Position is either occupied by a city, industry, another station, or is out of bounds.");
            return;
        }

        // Get center point for STATION type
        String centerPoint = null;
        if (selectedType.requiresCenterPoint()) {
            System.out.println("\nSelect center point (NE, SE, NW, SW):");
            centerPoint = Utils.readLineFromConsole("Center point: ").toUpperCase();
            if (!isValidCenterPoint(centerPoint)) {
                System.out.println("Invalid center point.");
                return;
            }
        }

        // Preview placement
        if (!controller.previewStationPlacement(selectedType, position, centerPoint)) {
            System.out.println("Cannot place station at this location.");
            return;
        }

        // Show station details
        City closestCity = controller.getClosestCity(position);
        if (closestCity == null) {
            System.out.println("No nearby city found.");
            return;
        }

        String stationName = closestCity.getNameID() + " " + selectedType.getName();
        System.out.println("\nStation Details:");
        System.out.println("Name: " + stationName);
        System.out.println("Type: " + selectedType.getName());
        System.out.printf("Cost: %.2f%n", selectedType.getCost());
        System.out.println("Economic Radius: " + selectedType.getEconomicRadius());
        System.out.printf("Current Budget: %.2f%n", ApplicationSession.getInstance().getCurrentPlayer().getCurrentBudget());
        System.out.printf("Remaining Budget After Build: %.2f%n", 
            ApplicationSession.getInstance().getCurrentPlayer().getCurrentBudget() - selectedType.getCost());

        // Confirm placement
        boolean confirm = Utils.confirm("Do you want to build this station?");
        if (!confirm) {
            System.out.println("Station building cancelled.");
            return;
        }

        // Build station
        if (controller.buildStation(selectedType, position, centerPoint)) {
            System.out.println("Station built successfully!");
            System.out.printf("New Budget: %.2f%n", 
                ApplicationSession.getInstance().getCurrentPlayer().getCurrentBudget());
        } else {
            System.out.println("Failed to build station. Please check your budget and try again.");
        }
    }

    private boolean isValidCenterPoint(String centerPoint) {
        return centerPoint != null && (
            centerPoint.equals("NE") ||
            centerPoint.equals("SE") ||
            centerPoint.equals("NW") ||
            centerPoint.equals("SW")
        );
    }
} 