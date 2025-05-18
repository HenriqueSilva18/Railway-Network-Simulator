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
        
        // Display current station information
        Station.StationInfo stationInfo = controller.getStationInfo();
        displayStationInfo(stationInfo);
        
        // Get current buildings in the station
        List<Building.BuildingInfo> currentBuildings = controller.getCurrentStationBuildings();
        if (!currentBuildings.isEmpty()) {
            displayCurrentBuildings(currentBuildings);
        }
        
        // Ask what type of upgrade the user wants to perform
        String[] upgradeOptions = {"Install New Building", "Evolve Existing Building"};
        int upgradeChoice = Utils.showAndSelectIndex(List.of(upgradeOptions), "\nWhat would you like to do?");
        
        if (upgradeChoice == 0) {
            // Install new building
            installNewBuilding();
        } else if (upgradeChoice == 1) {
            // Evolve existing building
            evolveExistingBuilding();
        } else {
            System.out.println("Operation cancelled.");
        }
    }
    
    private void installNewBuilding() {
        // Get available new buildings
        List<Building> availableBuildings = controller.getAvailableNewBuildings();
        
        if (availableBuildings.isEmpty()) {
            System.out.println("No available buildings for this station.");
            Utils.readLineFromConsole("Press Enter to continue...");
            return;
        }
        
        // Display available buildings
        displayAvailableBuildings(availableBuildings);
        
        // Get user selection
        int option = Utils.showAndSelectIndex(getBuildingOptions(availableBuildings), "\nSelect a building to install:");
        
        if (option < 0 || option >= availableBuildings.size()) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Get selected building
        Building selectedBuilding = availableBuildings.get(option);
        
        // Display building info
        Building.BuildingInfo buildingInfo = controller.getBuildingInfo(selectedBuilding.getNameID());
        displayBuildingInfo(buildingInfo);
        
        // Confirm installation
        boolean confirm = Utils.confirm("Do you want to install this building?");
        
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Perform installation
        boolean success = controller.installNewBuilding(selectedBuilding.getNameID());
        
        if (success) {
            System.out.println("\nBuilding successfully installed!");
            
            // Display updated station info
            Station.StationInfo stationInfo = controller.getStationInfo();
            displayStationInfo(stationInfo);
        } else {
            System.out.println("\nFailed to install building. You may not have enough budget or the building is not compatible.");
        }
        
        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
    
    private void evolveExistingBuilding() {
        // Get evolvable buildings
        List<Building.BuildingInfo> evolvableBuildings = controller.getEvolvableBuildings();
        
        if (evolvableBuildings.isEmpty()) {
            System.out.println("No buildings available for evolution.");
            Utils.readLineFromConsole("Press Enter to continue...");
            return;
        }
        
        // Display evolvable buildings
        displayEvolvableBuildings(evolvableBuildings);
        
        // Get user selection
        int option = Utils.showAndSelectIndex(getEvolvableBuildingOptions(evolvableBuildings), "\nSelect a building to evolve:");
        
        if (option < 0 || option >= evolvableBuildings.size()) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Get selected building
        Building.BuildingInfo selectedBuilding = evolvableBuildings.get(option);
        
        // Get evolution options
        List<Building> evolutionOptions = controller.getEvolutionOptions(selectedBuilding.getNameID());
        
        if (evolutionOptions.isEmpty()) {
            System.out.println("\nNo evolution options available for this building at this time.");
            Utils.readLineFromConsole("Press Enter to continue...");
            return;
        }
        
        // Display evolution options (usually just one)
        Building evolution = evolutionOptions.get(0); // There's usually only one evolution path
        Building.BuildingInfo evolutionInfo = controller.getBuildingInfo(evolution.getNameID());
        
        System.out.println("\n=== Evolution Information ===");
        System.out.println("Current Building: " + selectedBuilding.getNameID());
        System.out.println("Effect: " + selectedBuilding.getEffect());
        System.out.println("\nEvolves into: " + evolutionInfo.getNameID());
        System.out.println("Effect: " + evolutionInfo.getEffect());
        System.out.printf("Evolution Cost: %.2f\n", selectedBuilding.getEvolutionCost());
        
        // Confirm evolution
        boolean confirm = Utils.confirm("\nDo you want to evolve this building?");
        
        if (!confirm) {
            System.out.println("Operation cancelled.");
            return;
        }
        
        // Perform evolution
        boolean success = controller.evolveBuilding(selectedBuilding.getNameID(), evolution.getNameID());
        
        if (success) {
            System.out.println("\nBuilding successfully evolved!");
            
            // Display updated station info
            Station.StationInfo stationInfo = controller.getStationInfo();
            displayStationInfo(stationInfo);
        } else {
            System.out.println("\nFailed to evolve building. You may not have enough budget or the evolution is not available yet.");
        }
        
        Utils.readLineFromConsole("\nPress Enter to continue...");
    }
    
    private void displayCurrentBuildings(List<Building.BuildingInfo> buildings) {
        System.out.println("\nCurrent Buildings:");
        System.out.println("-----------------");
        
        for (Building.BuildingInfo building : buildings) {
            System.out.printf("- %s (Type: %s)\n", 
                building.getNameID(), 
                building.getType());
            System.out.printf("  Effect: %s\n", building.getEffect());
            
            if (building.canEvolve()) {
                System.out.printf("  Can evolve to: %s (Cost: %.2f)\n", 
                    building.getEvolvesInto(), 
                    building.getEvolutionCost());
            }
        }
    }
    
    private void displayAvailableBuildings(List<Building> buildings) {
        System.out.println("\nAvailable buildings for installation:");
        System.out.println("----------------------------------");
        
        for (int i = 0; i < buildings.size(); i++) {
            Building building = buildings.get(i);
            System.out.printf("%d. %s (Type: %s, Cost: %.2f)\n", 
                (i+1), 
                building.getNameID(), 
                building.getType(),
                building.getCost());
            
            if (building.getReplacesBuilding() != null) {
                System.out.printf("   Replaces: %s\n", building.getReplacesBuilding());
            }
        }
    }
    
    private void displayEvolvableBuildings(List<Building.BuildingInfo> buildings) {
        System.out.println("\nBuildings available for evolution:");
        System.out.println("-------------------------------");
        
        for (int i = 0; i < buildings.size(); i++) {
            Building.BuildingInfo building = buildings.get(i);
            System.out.printf("%d. %s (Type: %s)\n", 
                (i+1), 
                building.getNameID(), 
                building.getType());
            System.out.printf("   Evolves to: %s (Cost: %.2f)\n", 
                building.getEvolvesInto(), 
                building.getEvolutionCost());
        }
    }
    
    private List<String> getBuildingOptions(List<Building> buildings) {
        List<String> options = new ArrayList<>();
        for (Building building : buildings) {
            options.add(building.getNameID());
        }
        return options;
    }
    
    private List<String> getEvolvableBuildingOptions(List<Building.BuildingInfo> buildings) {
        List<String> options = new ArrayList<>();
        for (Building.BuildingInfo building : buildings) {
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
        
        if (info.canEvolve()) {
            System.out.printf("Can evolve to: %s (Cost: %.2f)\n", 
                info.getEvolvesInto(), 
                info.getEvolutionCost());
        }
    }
    
    private void displayStationInfo(Station.StationInfo info) {
        System.out.println("\n=== Station Information ===");
        System.out.printf("Name: %s\n", info.getNameID());
        System.out.printf("Type: %s\n", info.getType());
        System.out.printf("Position: (%d, %d)\n", info.getPosX(), info.getPosY());
        System.out.printf("Storage Capacity: %d\n", info.getStorageCapacity());
        System.out.printf("Building Slots: %d/%d\n", 
            info.getUsedBuildingSlots(), 
            info.getTotalBuildingSlots());
    }
} 