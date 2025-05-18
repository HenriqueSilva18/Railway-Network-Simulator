package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
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

    public void addServedCity(City city) {
        if (city != null && !servedCities.contains(city)) {
            servedCities.add(city);
        }
    }

    public boolean isWithinRadius(Position otherPosition) {
        int radius = stationType.getEconomicRadius();
        int dx = Math.abs(position.getX() - otherPosition.getX());
        int dy = Math.abs(position.getY() - otherPosition.getY());
        return dx <= radius && dy <= radius;
    }
} 