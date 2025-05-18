package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Station {
    private final String nameID;
    private final Position position;
    private final StationType stationType;
    private final int storageCapacity;
    private final int buildingSlots;
    private final List<Cargo> availableCargo;
    private final List<Cargo> requestedCargo;
    private final List<City> servedCities;
    private final List<Building> buildings;

    public Station(String nameID, Position position, StationType stationType) {
        if (nameID == null || position == null || stationType == null) {
            throw new IllegalArgumentException("Station parameters cannot be null");
        }
        
        this.nameID = nameID;
        this.position = position;
        this.stationType = stationType;
        
        // Initialize based on station type
        switch (stationType.getName()) {
            case StationType.DEPOT:
                this.storageCapacity = 100;
                this.buildingSlots = 2;
                break;
            case StationType.STATION:
                this.storageCapacity = 200;
                this.buildingSlots = 3;
                break;
            case StationType.TERMINAL:
                this.storageCapacity = 300;
                this.buildingSlots = 4;
                break;
            default:
                throw new IllegalArgumentException("Invalid station type");
        }
        
        this.availableCargo = new ArrayList<>();
        this.requestedCargo = new ArrayList<>();
        this.servedCities = new ArrayList<>();
        this.buildings = new ArrayList<>();
    }

    public String getNameID() {
        return nameID;
    }

    public Position getPosition() {
        return position;
    }

    public StationType getStationType() {
        return stationType;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public int getBuildingSlots() {
        return buildingSlots;
    }

    public List<Cargo> getAvailableCargo() {
        return new ArrayList<>(availableCargo);
    }

    public List<Cargo> getRequestedCargo() {
        return new ArrayList<>(requestedCargo);
    }

    public List<City> getServedCities() {
        return new ArrayList<>(servedCities);
    }

    public List<Building> getBuildings() {
        return new ArrayList<>(buildings);
    }
    
    /**
     * Directly add a building to the station (for internal use)
     */
    public void addBuilding(Building building) {
        if (building != null) {
            buildings.add(building);
        }
    }
    
    /**
     * Directly remove a building by ID (for internal use)
     */
    public boolean removeBuildingById(String buildingId) {
        if (buildingId == null) {
            return false;
        }
        return buildings.removeIf(building -> building.getNameID().equals(buildingId));
    }

    public void addServedCity(City city) {
        if (city == null) {
            return;
        }
        
        // Check if a city with the same nameID already exists in the list
        boolean cityAlreadyExists = servedCities.stream()
                .anyMatch(existingCity -> existingCity.getNameID().equals(city.getNameID()));
                
        if (!cityAlreadyExists) {
            servedCities.add(city);
        }
    }
    
    /**
     * Get a building by its nameID
     */
    public Building getBuilding(String buildingId) {
        for (Building building : buildings) {
            if (building.getNameID().equals(buildingId)) {
                return building;
            }
        }
        return null;
    }

    /**
     * Checks if a new building can be installed in this station
     */
    public boolean canInstallNewBuilding(Building building, int currentYear) {
        if (building == null) {
            return false;
        }
        
        // Check if the station has available building slots
        if (buildings.size() >= buildingSlots) {
            return false;
        }
        
        // Check if the building is available in the current year
        if (building.getAvailabilityYear() > currentYear) {
            return false;
        }

        // Check for replaced buildings
        if (building.getReplacesBuilding() != null) {
            // If this building replaces another, check if the replaced one exists
            boolean replacedBuildingExists = false;
            for (Building existingBuilding : buildings) {
                if (existingBuilding.getNameID().equals(building.getReplacesBuilding())) {
                    replacedBuildingExists = true;
                    break;
                }
            }
            if (!replacedBuildingExists) {
                // If the building it replaces doesn't exist, it cannot be built
                return false;
            }
        }

        // Check for mutually exclusive buildings
        if (building.isMutuallyExclusive()) {
            for (Building existingBuilding : buildings) {
                if (building.getMutuallyExclusiveWith() != null && 
                    existingBuilding.getType().equals(building.getMutuallyExclusiveWith())) {
                    return false;
                }
            }
        }

        // Check for duplicate buildings
        for (Building existingBuilding : buildings) {
            if (existingBuilding.getNameID().equals(building.getNameID())) {
                return false;
            }
        }

        return true;
    }
    
    /**
     * Checks if a building can be evolved to another building
     */
    public boolean canEvolveBuildingTo(String buildingId, String evolutionId, int currentYear) {
        // Get the current building
        Building currentBuilding = getBuilding(buildingId);
        if (currentBuilding == null || !currentBuilding.canEvolve()) {
            return false;
        }
        
        // Check if the evolution is the correct one
        if (!currentBuilding.getEvolvesInto().equals(evolutionId)) {
            return false;
        }
        
        // Check if the evolution is available in the current year
        Building evolution = null;
        for (Building building : buildings) {
            if (building.getNameID().equals(evolutionId)) {
                evolution = building;
                break;
            }
        }
        
        if (evolution != null && evolution.getAvailabilityYear() > currentYear) {
            return false;
        }
        
        return true;
    }

    /**
     * Gets a list of new buildings that can be installed in this station
     */
    public List<Building> getAvailableNewBuildings(List<Building> availableBuildings, int currentYear) {
        List<Building> validUpgrades = new ArrayList<>();
        
        for (Building building : availableBuildings) {
            if (canInstallNewBuilding(building, currentYear)) {
                validUpgrades.add(building);
            }
        }
        
        return validUpgrades;
    }
    
    /**
     * Gets a list of buildings that can be evolved in this station
     */
    public List<Building.BuildingInfo> getEvolvableBuildings(int currentYear) {
        List<Building.BuildingInfo> evolvableBuildings = new ArrayList<>();
        
        for (Building building : buildings) {
            if (building.canEvolve() && building.getEvolvesInto() != null) {
                evolvableBuildings.add(building.getInfo());
            }
        }
        
        return evolvableBuildings;
    }

    /**
     * Installs a new building in the station
     * Returns true if successful, false otherwise
     */
    public boolean installNewBuilding(Building building, int currentYear) {
        if (!canInstallNewBuilding(building, currentYear)) {
            return false;
        }

        // If the building replaces another, remove the replaced building
        if (building.getReplacesBuilding() != null) {
            buildings.removeIf(existingBuilding -> 
                existingBuilding.getNameID().equals(building.getReplacesBuilding()));
        }

        // Add the new building
        buildings.add(building);
        return true;
    }
    
    /**
     * Evolves an existing building to its next stage
     * Returns true if successful, false otherwise
     */
    public boolean evolveBuilding(String buildingId, Building evolution, int currentYear) {
        // Get the building to evolve
        Building currentBuilding = getBuilding(buildingId);
        if (currentBuilding == null || !currentBuilding.canEvolve()) {
            return false;
        }
        
        // Check if this is the correct evolution path
        if (!currentBuilding.getEvolvesInto().equals(evolution.getNameID())) {
            return false;
        }
        
        // Check if the evolution is available in the current year
        if (evolution.getAvailabilityYear() > currentYear) {
            return false;
        }
        
        // Remove the old building and add the evolved building
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getNameID().equals(buildingId)) {
                // Replace directly at the same position
                buildings.set(i, evolution);
                return true;
            }
        }
        
        return false;
    }

    /**
     * General method to upgrade station with a building (either new installation or evolution)
     */
    public boolean upgrade(Building building, int currentYear) {
        // First check if this is an evolution of an existing building
        for (Building existingBuilding : buildings) {
            if (existingBuilding.canEvolve() && 
                existingBuilding.getEvolvesInto() != null && 
                existingBuilding.getEvolvesInto().equals(building.getNameID())) {
                
                return evolveBuilding(existingBuilding.getNameID(), building, currentYear);
            }
        }
        
        // If not an evolution, try to install as a new building
        return installNewBuilding(building, currentYear);
    }

    public boolean isWithinRadius(Position otherPosition) {
        int radius = stationType.getEconomicRadius();
        int dx = Math.abs(position.getX() - otherPosition.getX());
        int dy = Math.abs(position.getY() - otherPosition.getY());
        return dx <= radius && dy <= radius;
    }
    
    /**
     * Returns a DTO with station information for display
     */
    public StationInfo getInfo() {
        return new StationInfo(nameID, stationType.getName(), position.getX(), position.getY(), 
                            storageCapacity, buildingSlots, buildings.size());
    }
    
    /**
     * DTO class to hold station information
     */
    public static class StationInfo {
        private final String nameID;
        private final String type;
        private final int posX;
        private final int posY;
        private final int storageCapacity;
        private final int totalBuildingSlots;
        private final int usedBuildingSlots;
        
        public StationInfo(String nameID, String type, int posX, int posY, 
                        int storageCapacity, int totalBuildingSlots, int usedBuildingSlots) {
            this.nameID = nameID;
            this.type = type;
            this.posX = posX;
            this.posY = posY;
            this.storageCapacity = storageCapacity;
            this.totalBuildingSlots = totalBuildingSlots;
            this.usedBuildingSlots = usedBuildingSlots;
        }
        
        public String getNameID() {
            return nameID;
        }
        
        public String getType() {
            return type;
        }
        
        public int getPosX() {
            return posX;
        }
        
        public int getPosY() {
            return posY;
        }
        
        public int getStorageCapacity() {
            return storageCapacity;
        }
        
        public int getTotalBuildingSlots() {
            return totalBuildingSlots;
        }
        
        public int getUsedBuildingSlots() {
            return usedBuildingSlots;
        }
        
        public int getAvailableBuildingSlots() {
            return totalBuildingSlots - usedBuildingSlots;
        }
    }
} 