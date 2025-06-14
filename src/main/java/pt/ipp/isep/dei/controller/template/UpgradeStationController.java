package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Building;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.repository.template.BuildingRepository;
import pt.ipp.isep.dei.repository.template.PlayerRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.StationRepository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpgradeStationController {
    private final BuildingRepository buildingRepository;
    private final StationRepository stationRepository;
    private final PlayerRepository playerRepository;
    private final SimulatorController simulatorController;
    
    public UpgradeStationController() {
        this.buildingRepository = Repositories.getInstance().getBuildingRepository();
        this.stationRepository = Repositories.getInstance().getStationRepository();
        this.playerRepository = Repositories.getInstance().getPlayerRepository();
        this.simulatorController = new SimulatorController();
    }
    
    /**
     * Gets the current year from the scenario
     */
    public int getCurrentYear() {
        return simulatorController.getCurrentYear();
    }
    
    /**
     * Gets a list of new buildings available for installation
     */
    public List<Building> getAvailableNewBuildings() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return List.of();
        }
        
        // Use the filtered building options that exclude evolution targets
        List<Building> newBuildingOptions = buildingRepository.getNewBuildingOptions();
        return currentStation.getAvailableNewBuildings(newBuildingOptions, getCurrentYear());
    }
    
    /**
     * Gets a list of buildings that can be evolved
     */
    public List<Building.BuildingInfo> getEvolvableBuildings() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return List.of();
        }
        
        return currentStation.getEvolvableBuildings(getCurrentYear());
    }
    
    /**
     * Gets evolution options for a specific building
     */
    public List<Building> getEvolutionOptions(String buildingId) {
        return buildingRepository.getEvolutionOptions(buildingId, getCurrentYear());
    }
    
    /**
     * Gets information about a specific building
     */
    public Building.BuildingInfo getBuildingInfo(String buildingId) {
        Building building = buildingRepository.getBuilding(buildingId);
        if (building == null) {
            return null;
        }
        
        return building.getInfo();
    }
    
    /**
     * Installs a new building in the station
     */
    public boolean installNewBuilding(String buildingId) {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        Building building = buildingRepository.getBuilding(buildingId);
        
        if (currentStation == null || currentPlayer == null || building == null) {
            return false;
        }
        
        // Check if player has enough budget
        if (currentPlayer.getCurrentBudget() < building.getCost()) {
            return false;
        }
        
        // Attempt to install the building
        boolean success = currentStation.installNewBuilding(building, getCurrentYear());
        
        if (success) {
            // Deduct the cost from player's budget
            currentPlayer.deductFromBudget(building.getCost());
            
            // Save the updated station and player
            stationRepository.save(currentStation);
            playerRepository.save(currentPlayer);
        }
        
        return success;
    }
    
    /**
     * Evolves an existing building
     */
    public boolean evolveBuilding(String buildingId, String evolutionId) {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        Building building = buildingRepository.getBuilding(buildingId);
        Building evolution = buildingRepository.getBuilding(evolutionId);
        
        if (currentStation == null || currentPlayer == null || building == null || evolution == null) {
            return false;
        }
        
        // Check if player has enough budget
        if (currentPlayer.getCurrentBudget() < building.getEvolutionCost()) {
            return false;
        }
        
        // Use the evolveBuilding method directly in Station which properly handles the replacement
        boolean success = currentStation.evolveBuilding(buildingId, evolution, getCurrentYear());
        
        if (success) {
            // Deduct the evolution cost from player's budget
            currentPlayer.deductFromBudget(building.getEvolutionCost());
            
            // Save the updated station and player
            stationRepository.save(currentStation);
            playerRepository.save(currentPlayer);
            
            // Refresh the current station reference
            ApplicationSession.getInstance().setCurrentStation(currentStation);
        }
        
        return success;
    }
    
    /**
     * General method to upgrade a station with a building
     */
    public boolean upgradeStation(String buildingId) {
        Building building = buildingRepository.getBuilding(buildingId);
        if (building == null) {
            return false;
        }
        
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        
        if (currentStation == null || currentPlayer == null) {
            return false;
        }
        
        // First check if this is an evolution of an existing building
        for (Building existingBuilding : currentStation.getBuildings()) {
            if (existingBuilding.canEvolve() && 
                existingBuilding.getEvolvesInto() != null && 
                existingBuilding.getEvolvesInto().equals(buildingId)) {
                
                // Check if player has enough budget for evolution
                if (currentPlayer.getCurrentBudget() < existingBuilding.getEvolutionCost()) {
                    return false;
                }
                
                // Use the evolveBuilding method instead
                return evolveBuilding(existingBuilding.getNameID(), buildingId);
            }
        }
        
        // If not an evolution, try to install as a new building
        // Check if player has enough budget
        if (currentPlayer.getCurrentBudget() < building.getCost()) {
            return false;
        }
        
        // Attempt to install the new building
        boolean success = currentStation.installNewBuilding(building, getCurrentYear());
        
        if (success) {
            // Deduct the cost from player's budget
            currentPlayer.deductFromBudget(building.getCost());
            
            // Save the updated station and player
            stationRepository.save(currentStation);
            playerRepository.save(currentPlayer);
        }
        
        return success;
    }
    
    /**
     * Gets information about the current station
     */
    public Station.StationInfo getStationInfo() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return null;
        }
        
        return currentStation.getInfo();
    }
    
    /**
     * Gets the list of buildings currently in the station
     */
    public List<Building.BuildingInfo> getCurrentStationBuildings() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return List.of();
        }
        
        List<Building.BuildingInfo> buildingInfos = new ArrayList<>();
        for (Building building : currentStation.getBuildings()) {
            buildingInfos.add(building.getInfo());
        }
        
        return buildingInfos;
    }

    /**
     * Gets a building by its ID
     * @param buildingId The ID of the building to get
     * @return The building, or null if not found
     */
    public Building getBuilding(String buildingId) {
        return buildingRepository.getBuilding(buildingId);
    }

    /**
     * Removes a building from the current station
     */
    public boolean removeBuilding(String buildingId) {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return false;
        }
        
        // Get the building to remove
        Building building = currentStation.getBuilding(buildingId);
        if (building == null) {
            return false;
        }
        
        // Remove the building
        boolean success = currentStation.removeBuilding(buildingId);
        
        if (success) {
            // Save the updated station
            stationRepository.save(currentStation);
            
            // Refresh the current station reference
            ApplicationSession.getInstance().setCurrentStation(currentStation);
        }
        
        return success;
    }
} 