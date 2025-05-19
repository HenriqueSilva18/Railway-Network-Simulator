package pt.ipp.isep.dei.domain.template;

import java.util.*;
import java.io.File;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.EditorRepository;

public class Map {
    private final String nameID;
    private final Size size;
    private final List<Position> positions;
    private final List<City> cities;
    private final List<Station> stations;
    private final List<Industry> industries;
    private final List<String> scenarios;
    private final Random random;

    private static final String SCENARIOS_PATH = "docs/mdisc/data";

    private Map(String nameID, Size size) {
        this.nameID = nameID;
        this.size = size;
        this.positions = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.stations = new ArrayList<>();
        this.industries = new ArrayList<>();
        this.scenarios = new ArrayList<>();
        this.random = new Random();
    }

    public static boolean validateMapName(String nameID) {
        if (nameID == null || nameID.trim().isEmpty()) {
            return false;
        }
        // Check if nameID is a valid file name
        return nameID.matches("^[a-zA-Z0-9_-]+$");
    }

    public static Map createMap(String nameID, Size size) {
        if (!validateMapName(nameID)) {
            throw new IllegalArgumentException("Invalid map name");
        }
        return new Map(nameID, size);
    }

    public String getNameID() {
        return nameID;
    }

    public Size getSize() {
        return size;
    }

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public List<Industry> getIndustries() {
        return new ArrayList<>(industries);
    }

    public List<String> getScenarios() {
        return new ArrayList<>(scenarios);
    }

    public List<String> getAvailableScenarios() {
        return getScenarios();  // Delegate to existing getScenarios() method
    }

    public boolean addScenario(String scenarioId) {
        if (scenarioId == null || scenarioId.trim().isEmpty()) {
            return false;
        }
        if (!scenarios.contains(scenarioId)) {
            return scenarios.add(scenarioId);
        }
        return false;
    }

    public boolean validatePosition(Position position) {
        if (position == null) {
            return false;
        }

        // Check if position is within map bounds
        if (position.getX() < 0 || position.getX() >= size.getWidth() ||
            position.getY() < 0 || position.getY() >= size.getHeight()) {
            return false;
        }

        // Check if position is already occupied
        return !isPositionOccupied(position);
    }

    private boolean isPositionOccupied(Position position) {
        // Check cities
        if (cities.stream().anyMatch(city -> city.getPosition().equals(position))) {
            return true;
        }

        // Check stations
        if (stations.stream().anyMatch(station -> station.getPosition().equals(position))) {
            return true;
        }

        // Check industries
        return industries.stream().anyMatch(industry -> industry.getPosition().equals(position));
    }

    public boolean isPositionAvailable(int x, int y) {
        if (x < 0 || x >= size.getWidth() || y < 0 || y >= size.getHeight()) {
            return false;
        }
        Position position = new Position(x, y);
        return !isPositionOccupied(position);
    }

    public boolean isCellEmpty(int x, int y) {
        return isPositionAvailable(x, y);
    }

    public City getClosestCity(Position position) {
        if (position == null || cities.isEmpty()) {
            return null;
        }

        return cities.stream()
                .min(Comparator.comparingDouble(city -> 
                    calculateDistance(city.getPosition(), position)))
                .orElse(null);
    }

    private double calculateDistance(Position p1, Position p2) {
        int dx = p1.getX() - p2.getX();
        int dy = p1.getY() - p2.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    public boolean previewStationPlacement(StationType stationType, Position position, String centerPoint) {
        if (!validatePosition(position)) {
            return false;
        }

        // For STATION type, validate center point
        if (StationType.STATION.equals(stationType.getName())) {
            if (centerPoint == null || !isValidCenterPoint(centerPoint)) {
                return false;
            }
        }

        return true;
    }

    private boolean isValidCenterPoint(String centerPoint) {
        return centerPoint.equals("NE") ||
               centerPoint.equals("SE") ||
               centerPoint.equals("NW") ||
               centerPoint.equals("SW");
    }

    public boolean addStation(Station station) {
        if (station == null || !validatePosition(station.getPosition())) {
            return false;
        }

        // Add station
        stations.add(station);

        // Mark position as occupied
        station.getPosition().setOccupied(true);

        // Find and add served cities within radius
        for (City city : cities) {
            if (station.isWithinRadius(city.getPosition())) {
                station.addServedCity(city);
            }
        }

        return true;
    }

    public boolean addCity(City city) {
        if (city == null || !validatePosition(city.getPosition())) {
            return false;
        }
        cities.add(city);
        city.getPosition().setOccupied(true);
        return true;
    }

    public boolean addIndustry(Industry industry) {
        if (industry == null || !validatePosition(industry.getPosition())) {
            return false;
        }
        industries.add(industry);
        industry.getPosition().setOccupied(true);
        return true;
    }

    public List<Position> getAvailablePositions(int numPositions) {
        List<Position> availablePositions = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = numPositions * 10; // Limit attempts to avoid infinite loop

        while (availablePositions.size() < numPositions && attempts < maxAttempts) {
            int x = random.nextInt(size.getWidth());
            int y = random.nextInt(size.getHeight());
            Position position = new Position(x, y);

            if (validatePosition(position)) {
                availablePositions.add(position);
                position.setOccupied(true);
            }
            attempts++;
        }

        return availablePositions;
    }

    public boolean removeEntityAt(int x, int y) {
        Position position = new Position(x, y);
        boolean removed = false;
        
        // Remove industry if present
        Optional<Industry> industry = industries.stream()
                .filter(ind -> ind.getPosition().equals(position))
                .findFirst();
        if (industry.isPresent()) {
            industries.remove(industry.get());
            removed = true;
        }
        
        // Remove city if present
        Optional<City> city = cities.stream()
                .filter(c -> c.getPosition().equals(position))
                .findFirst();
        if (city.isPresent()) {
            cities.remove(city.get());
            removed = true;
        }
        
        // Remove station if present
        Optional<Station> station = stations.stream()
                .filter(s -> s.getPosition().equals(position))
                .findFirst();
        if (station.isPresent()) {
            stations.remove(station.get());
            removed = true;
        }
        
        return removed;
    }

    public void markPositionOccupied(Position position) {
        if (position != null && validatePosition(position)) {
            position.setOccupied(true);
        }
    }

    public boolean loadScenario(String scenarioID) {
        if (scenarioID == null || !scenarios.contains(scenarioID)) {
            return false;
        }

        // Clear existing layout
        cities.clear();
        stations.clear();
        industries.clear();
        positions.clear();

        // Get the editor repository
        EditorRepository editorRepo = Repositories.getInstance().getEditorRepository();
        
        // Try to find the scenario by ID
        Optional<Scenario> scenario = editorRepo.getAllScenarios().stream()
                .filter(s -> s.getNameID().equals(scenarioID) && 
                           s.getMap() != null && 
                           s.getMap().getNameID().equals(this.nameID))
                .findFirst();

        if (!scenario.isPresent()) {
            // If not found by ID, try by display name
            scenario = editorRepo.getAllScenarios().stream()
                    .filter(s -> s.getDisplayName().contains(scenarioID) && 
                               s.getMap() != null && 
                               s.getMap().getNameID().equals(this.nameID))
                    .findFirst();
        }

        if (scenario.isPresent()) {
            // Load cities from the scenario
            for (City city : scenario.get().getTweakedCityList()) {
                City newCity = new City(city.getNameID(), city.getPosition(), city.getHouseBlocks());
                newCity.setTrafficRate(city.getTrafficRate());
                addCity(newCity);
            }

            // Load industries from the scenario
            for (Industry industry : scenario.get().getAvailableIndustryList()) {
                Industry newIndustry = new Industry(
                    industry.getNameID(),
                    industry.getType(),
                    industry.getSector(),
                    industry.getAvailabilityYear(),
                    industry.getPosition()
                );
                newIndustry.setProductionRate(industry.getProductionRate());
                newIndustry.setImportedCargo(industry.getImportedCargo());
                newIndustry.setExportedCargo(industry.getExportedCargo());
                newIndustry.setProducedCargo(industry.getProducedCargo());
                addIndustry(newIndustry);
            }

            return true;
        }

        // If no scenario found, use the predefined layout from Bootstrap
        return loadPredefinedLayout();
    }

    private boolean loadPredefinedLayout() {
        // The cities and industries added during bootstrap will remain
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Map map = (Map) o;
        return Objects.equals(nameID, map.nameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameID);
    }

    @Override
    public String toString() {
        return nameID;
    }
} 