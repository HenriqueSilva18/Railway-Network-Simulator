package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Building;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.repository.template.BuildingRepository;
import pt.ipp.isep.dei.repository.template.PlayerRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.StationRepository;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class UpgradeStationController {
    private final BuildingRepository buildingRepository;
    private final StationRepository stationRepository;
    private final PlayerRepository playerRepository;
    
    public UpgradeStationController() {
        this.buildingRepository = Repositories.getInstance().getBuildingRepository();
        this.stationRepository = Repositories.getInstance().getStationRepository();
        this.playerRepository = Repositories.getInstance().getPlayerRepository();
    }
    
    /**
     * Gets the current year from the scenario
     */
    public int getCurrentYear() {
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        if (currentScenario == null) {
            return 1900; // Default year if no scenario
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentScenario.getStartDate());
        return calendar.get(Calendar.YEAR);
    }
    
    /**
     * Gets a list of available upgrades for the current station
     */
    public List<Building> getAvailableUpgrades() {
        Station currentStation = ApplicationSession.getInstance().getCurrentStation();
        if (currentStation == null) {
            return List.of();
        }
        
        List<Building> allBuildings = buildingRepository.getAllBuildings();
        return currentStation.getAvailableUpgrades(allBuildings, getCurrentYear());
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
     * Upgrades the current station with the selected building
     */
    public boolean upgradeStation(String buildingId) {
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
        
        // Attempt to upgrade the station
        boolean success = currentStation.upgrade(building, getCurrentYear());
        
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
} 