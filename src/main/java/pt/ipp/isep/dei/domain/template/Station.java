package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Station implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String nameID;
    private final Position position;
    private final StationType stationType;
    private final int storageCapacity;
    private final int buildingSlots;
    private final List<Cargo> availableCargo;
    private final List<Cargo> requestedCargo;
    private final List<City> servedCities;
    private final List<Building> buildings;
    private int currentYear;
    private double demandMultiplier;
    private List<Cargo> demandedCargo;
    private transient final Map map; // Make map transient since it's a circular reference

    public Station(String nameID, Position position, StationType stationType, int storageCapacity, Map map) {
        if (nameID == null || position == null || stationType == null) {
            throw new IllegalArgumentException("Station parameters cannot be null");
        }
        
        this.nameID = nameID;
        this.position = position;
        this.stationType = stationType;
        this.currentYear = Calendar.getInstance().get(Calendar.YEAR);
        this.demandMultiplier = 1.0;
        
        // Initialize based on station type
        switch (stationType.getName()) {
            case StationType.DEPOT:
                this.buildingSlots = 2;
                break;
            case StationType.STATION:
                this.buildingSlots = 3;
                break;
            case StationType.TERMINAL:
                this.buildingSlots = 4;
                break;
            default:
                throw new IllegalArgumentException("Invalid station type");
        }
        
        this.storageCapacity = storageCapacity;
        this.availableCargo = new ArrayList<>();
        this.requestedCargo = new ArrayList<>();
        this.servedCities = new ArrayList<>();
        this.buildings = new ArrayList<>();
        this.demandedCargo = new ArrayList<>();
        this.map = map;
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
     * Checks if a building can be installed in this station
     * @param building The building to check
     * @return true if the building can be installed, false otherwise
     */
    public boolean canInstallBuilding(Building building) {
        if (building == null) return false;
        
        // Check if we already have a building of this type
        for (Building existingBuilding : buildings) {
            // Check for same type
            if (existingBuilding.getType().equals(building.getType())) {
                return false;
            }
            
            // Check for mutual exclusivity
            if (existingBuilding.isMutuallyExclusive() && 
                existingBuilding.getMutuallyExclusiveWith() != null &&
                existingBuilding.getMutuallyExclusiveWith().equals(building.getNameID())) {
                return false;
            }
            if (building.isMutuallyExclusive() && 
                building.getMutuallyExclusiveWith() != null &&
                building.getMutuallyExclusiveWith().equals(existingBuilding.getNameID())) {
                return false;
            }
            
            // Check if this building replaces an existing one
            if (building.getReplacesBuilding() != null && 
                building.getReplacesBuilding().equals(existingBuilding.getNameID())) {
                return false; // Can't install if the building to be replaced is still there
            }
        }
        
        return true;
    }

    /**
     * Gets a list of buildings that can be installed in this station
     * @param availableBuildings List of available buildings to check
     * @param currentYear Current year for availability check
     * @return List of buildings that can be installed
     */
    public List<Building> getAvailableNewBuildings(List<Building> availableBuildings, int currentYear) {
        List<Building> result = new ArrayList<>();
        
        for (Building building : availableBuildings) {
            // Check year availability
            if (building.getAvailabilityYear() > currentYear) {
                continue;
            }
            
            // Check if building can be installed
            if (canInstallBuilding(building)) {
                result.add(building);
            }
        }
        
        return result;
    }

    /**
     * Installs a new building in the station
     * @param building The building to install
     * @param currentYear Current year for availability check
     * @return true if the building was installed successfully, false otherwise
     */
    public boolean installNewBuilding(Building building, int currentYear) {
        if (building == null || building.getAvailabilityYear() > currentYear) {
            return false;
        }
        
        // Check if we can install this building
        if (!canInstallBuilding(building)) {
            return false;
        }
        
        // Check if we have enough building slots
        if (buildings.size() >= getTotalBuildingSlots()) {
            return false;
        }
        
        // Add the building
        return buildings.add(building);
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
     * Gets a list of buildings that can be evolved in this station
     */
    public List<Building.BuildingInfo> getEvolvableBuildings(int currentYear) {
        List<Building.BuildingInfo> evolvableBuildings = new ArrayList<>();

        for (Building building : buildings) {
            // Check if the building can evolve and is available in the current or past year
            if (building.canEvolve() && building.getEvolvesInto() != null && building.getAvailabilityYear() <= currentYear) {
                evolvableBuildings.add(building.getInfo());
            }
        }
        
        return evolvableBuildings;
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

    public boolean hasStorageCapacity(int amount) {
        if (amount <= 0) {
            return false;
        }
        
        int currentStorage = availableCargo.stream()
            .mapToInt(Cargo::getAmount)
            .sum();
            
        return currentStorage + amount <= storageCapacity;
    }
    
    public int getAvailableStorage() {
        int currentStorage = availableCargo.stream()
            .mapToInt(Cargo::getAmount)
            .sum();
        return storageCapacity - currentStorage;
    }
    
    public boolean addCargo(Cargo cargo) {
        if (cargo == null) {
            return false;
        }
        
        // Check if we have space
        if (!hasStorageCapacity(cargo.getAmount())) {
            return false;
        }
        
        // Check if we already have this type of cargo
        for (Cargo existingCargo : availableCargo) {
            if (existingCargo.getType().equals(cargo.getType())) {
                existingCargo.addAmount(cargo.getAmount());
                return true;
            }
        }
        
        // If we don't have this type of cargo, add it
        availableCargo.add(cargo);
        return true;
    }
    
    public void removeCargo(Cargo cargo) {
        if (cargo == null) {
            return;
        }
        
        availableCargo.removeIf(c -> c.getName().equals(cargo.getName()));
    }
    
    public void updateDemand(int currentYear) {
        // Clear previous demands
        demandedCargo.clear();
        
        // Get cities and industries in radius
        List<City> cities = getServedCities();
        List<Industry> industries = getIndustriesInRadius();
        
        // Add demands from cities
        for (City city : cities) {
            int cityDemand = city.getDemandedCargo();
            if (cityDemand > 0) {
                addDemandedCargo(new Cargo("Passengers from " + city.getNameID(), cityDemand, "passenger"));
                addDemandedCargo(new Cargo("Mail from " + city.getNameID(), cityDemand / 2, "mail"));
            }
        }
        
        // Add demands from industries
        for (Industry industry : industries) {
            List<Cargo> industryDemands = industry.getDemandedCargo();
            for (Cargo demand : industryDemands) {
                addDemandedCargo(demand);
            }
        }
    }
    
    private void addDemandedCargo(Cargo demand) {
        // Check if we already have this type of demand
        for (Cargo existing : demandedCargo) {
            if (existing.getType().equals(demand.getType())) {
                existing.setAmount(existing.getAmount() + demand.getAmount());
                return;
            }
        }
        // If not, add new demand
        demandedCargo.add(new Cargo(demand.getName(), demand.getAmount(), demand.getType()));
    }

    public List<Cargo> getDemandedCargo() {
        return new ArrayList<>(demandedCargo);
    }

    private List<Industry> getIndustriesInRadius() {
        List<Industry> industries = new ArrayList<>();
        for (Industry industry : map.getIndustries()) {
            if (isWithinRadius(industry.getPosition())) {
                industries.add(industry);
            }
        }
        return industries;
    }

    @Override
    public String toString() {
        // Retorna o nome da estação.
        // Se o método para obter o nome for diferente de "getNameID()", ajuste conforme necessário.
        return this.getNameID();
    }

    /**
     * Gets the total number of building slots available in this station
     * @return The total number of building slots
     */
    public int getTotalBuildingSlots() {
        return stationType.getBuildingSlots();
    }

    /**
     * Gets the number of used building slots
     * @return The number of buildings currently installed
     */
    public int getUsedBuildingSlots() {
        return buildings.size();
    }

    /**
     * Evolves an existing building to its next stage
     * @param buildingId The ID of the building to evolve
     * @param evolution The evolved building to install
     * @param currentYear The current year for availability check
     * @return true if the building was evolved successfully, false otherwise
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
            System.out.println("Evolution not available in the current year: " + currentYear + " only in: " + evolution.getAvailabilityYear());
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
     * Removes a building from the station
     * @param buildingId The ID of the building to remove
     * @return true if the building was removed, false otherwise
     */
    public boolean removeBuilding(String buildingId) {
        for (int i = 0; i < buildings.size(); i++) {
            if (buildings.get(i).getNameID().equals(buildingId)) {
                buildings.remove(i);
                return true;
            }
        }
        return false;
    }
} 