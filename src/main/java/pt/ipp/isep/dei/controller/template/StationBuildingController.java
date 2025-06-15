package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.List;
import java.util.Calendar;

public class StationBuildingController {
    private final MapRepository mapRepository;
    private final StationTypeRepository stationTypeRepository;

    public StationBuildingController() {
        Repositories repositories = Repositories.getInstance();
        this.mapRepository = repositories.getMapRepository();
        this.stationTypeRepository = repositories.getStationTypeRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public Map loadMap(String nameID) {
        return mapRepository.getMap(nameID);
    }

    public List<StationType> getStationTypes() {
        return stationTypeRepository.getStationTypes();
    }

    public boolean validatePosition(Position position) {
        Map currentMap = getCurrentMap();
        if (currentMap == null) {
            return false;
        }

        // Check if position is within map bounds and not occupied by another station
        if (!currentMap.validatePosition(position)) {
            return false;
        }

        // Check if position is occupied by a city
        for (City city : currentMap.getCities()) {
            if (city.getPosition().getX() == position.getX() && 
                city.getPosition().getY() == position.getY()) {
                return false;
            }
        }

        // Check if position is occupied by an industry
        for (Industry industry : currentMap.getIndustries()) {
            if (industry.getPosition().getX() == position.getX() && 
                industry.getPosition().getY() == position.getY()) {
                return false;
            }
        }

        return true;
    }

    public City getClosestCity(Position position) {
        Map currentMap = getCurrentMap();
        if (currentMap == null) {
            return null;
        }
        return currentMap.getClosestCity(position);
    }

    public boolean previewStationPlacement(StationType stationType, Position position, String centerPoint) {
        Map currentMap = getCurrentMap();
        if (currentMap == null) {
            return false;
        }
        
        if (stationType.requiresCenterPoint()) {
            stationType.setCenterPoint(centerPoint);
        }
        
        return currentMap.previewStationPlacement(stationType, position, centerPoint);
    }

    public boolean buildStation(String stationName, StationType stationType, Position position, String centerPoint, Building initialBuilding) {
        Map currentMap = getCurrentMap();
        if (currentMap == null) {
            return false;
        }

        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            return false;
        }

        // Check if player has enough budget for both station and building
        double totalCost = stationType.getCost() + initialBuilding.getCost();
        double currentBudget = currentPlayer.getCurrentBudget();
        if (currentBudget < totalCost) {
            return false;
        }

        // Get closest city for station naming
        City closestCity = currentMap.getClosestCity(position);
        if (closestCity == null) {
            return false;
        }

        // Set center point for station type if required
        if (stationType.requiresCenterPoint()) {
            stationType.setCenterPoint(centerPoint);
        }

        // Create station
        Station station = new Station(stationName, position, stationType, stationType.getStorageCapacity(), getCurrentMap());

        // Try to deduct total cost first
        if (!currentPlayer.deductFromBudget(totalCost)) {
            return false;
        }

        // Add station to map
        boolean success = currentMap.addStation(station);
        if (!success) {
            // If adding station fails, refund the budget
            currentPlayer.addToBudget(totalCost);
            return false;
        }

        // Add initial building to station
        if (!station.installNewBuilding(initialBuilding, getCurrentYear())) {
            // If adding building fails, remove station and refund budget
            currentMap.removeStation(station);
            currentPlayer.addToBudget(totalCost);
            return false;
        }

        // Add station to player's built stations
        currentPlayer.addStation(station);
        
        // Save map changes
        mapRepository.save(currentMap);
        return true;
    }

    // Keep the old method for backward compatibility
    public boolean buildStation(String stationName, StationType stationType, Position position, String centerPoint) {
        // Get a default building from the repository
        Building defaultBuilding = Repositories.getInstance().getBuildingRepository().getDefaultBuilding();
        if (defaultBuilding == null) {
            return false;
        }
        return buildStation(stationName, stationType, position, centerPoint, defaultBuilding);
    }

    public Map getCurrentMap() {
        return ApplicationSession.getInstance().getCurrentMap();
    }

    /**
     * Gets the current year from the scenario
     */
    private int getCurrentYear() {
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        if (currentScenario == null) {
            return Calendar.getInstance().get(Calendar.YEAR);
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentScenario.getStartDate());
        return calendar.get(Calendar.YEAR);
    }

    public boolean isStationNameTaken(String name) {
        Map currentMap = getCurrentMap();
        if (currentMap == null) {
            return false;
        }

        return currentMap.getStations().stream()
                .anyMatch(station -> station.getNameID().equalsIgnoreCase(name));
    }


    public String validateStationName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be empty");
        }

        if (isStationNameTaken(name)) {
            throw new IllegalArgumentException("Station name is already taken");
        }

        if (!name.matches("^[a-zA-Z0-9]+([ _-][a-zA-Z0-9]+)*$")) {
            throw new IllegalArgumentException("Station name contains invalid characters or format");
        }

        return name;
    }

} 