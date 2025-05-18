package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.UpgradeStationController;
import pt.ipp.isep.dei.domain.template.Building;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class UpgradeStationUI implements Runnable {
    private final UpgradeStationController controller;
    
    public UpgradeStationUI() {
        this.controller = new UpgradeStationController();
    }
    
    @Override
    public void run() {
        // Check if we have a current station
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            System.out.println("No station selected. Please select a station first.");
            return;
        }
        
        System.out.println("\n=== Upgrade Station: " + currentStation.getNameID() + " ===");
        
        // Get available upgrades
        List<Building> availableUpgrades = controller.getAvailableUpgrades();
        
        if (availableUpgrades.isEmpty()) {
            System.out.println("No available upgrades for this station.");
            Utils.readLineFromConsole("Press Enter to continue...");
            return;
        }
        
        // Display available upgrades
        displayAvailableUpgrades(availableUpgrades);
        
        // Get user selection
        int option = Utils.showAndSelectIndex(getBuildingOptions(availableUpgrades), "\nSelect a building to install:");
        
        if (option < 0 || option >= availableUpgrades.size()) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Get selected building
        Building selectedBuilding = availableUpgrades.get(option);
        
        // Display building info
        Building.BuildingInfo buildingInfo = controller.getBuildingInfo(selectedBuilding.getNameID());
        displayBuildingInfo(buildingInfo);
        
        // Confirm upgrade
        boolean confirm = Utils.confirm("Do you want to upgrade the station with this building?");
        
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Perform upgrade
        boolean success = controller.upgradeStation(selectedBuilding.getNameID());
        
        if (success) {
            System.out.println("\nStation successfully upgraded!");
            
            // Display updated station info
            Station.StationInfo stationInfo = controller.getStationInfo();
            displayStationInfo(stationInfo);
        } else {
            System.out.println("\nFailed to upgrade station. You may not have enough budget or the upgrade is not compatible.");
        }
        
        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
    
    private void displayAvailableUpgrades(List<Building> buildings) {
        System.out.println("\nAvailable buildings for upgrade:");
        System.out.println("-------------------------------");
        
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);
            System.out.printf("%d. %s (Type: %s, Cost: %.2f)\n", 
                (i+1), 
                building.getNameID(), 
                building.getType(),
                building.getCost());
        }
    }
    
    private List<String> getBuildingOptions(List<Building> buildings) {
        List<String> options = new ArrayList<>();
        for (Building building : buildings) {
            options.add(building.getNameID());
        }
        return options;
    }
    
    private void displayBuildingInfo(Building.BuildingInfo info) {
        System.out.println("\n=== Building Information ===");
        System.out.printf("Name: %s\n", info.getNameID());
        System.out.printf("Type: %s\n", info.getType());
        System.out.printf("Available from: %d\n", info.getAvailabilityYear());
        System.out.printf("Cost: %.2f\n", info.getCost());
        System.out.printf("Effect: %s\n", info.getEffect());
    }
    
    private void displayStationInfo(Station.StationInfo info) {
        System.out.println("\n=== Updated Station Information ===");
        System.out.printf("Name: %s\n", info.getNameID());
        System.out.printf("Type: %s\n", info.getType());
        System.out.printf("Position: (%d, %d)\n", info.getPosX(), info.getPosY());
        System.out.printf("Storage Capacity: %d\n", info.getStorageCapacity());
        System.out.printf("Building Slots: %d/%d\n", 
            info.getUsedBuildingSlots(), 
            info.getTotalBuildingSlots());
    }
} 