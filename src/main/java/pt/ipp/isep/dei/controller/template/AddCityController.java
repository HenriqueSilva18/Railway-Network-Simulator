package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.HouseBlock;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;
import java.util.Optional;

public class AddCityController {
    private final MapRepository mapRepository;
    private final Map currentMap;

    public AddCityController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
        this.currentMap = ApplicationSession.getInstance().getCurrentMap();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("City name cannot be empty");
        }

        // Regex: começa e termina com letra ou número, hífens/underscores só são permitidos entre letras/números
        if (!name.matches("^[a-zA-Z0-9]+([_-][a-zA-Z0-9]+)*$")) {
            throw new IllegalArgumentException("City name contains invalid characters or format");
        }

        if (currentMap.getCities().stream().anyMatch(city -> city.getNameID().equals(name))) {
            throw new IllegalArgumentException("City name already exists");
        }

        return name;
    }

    public Position validateCoordinates(int x, int y) {
        if (currentMap == null) {
            throw new IllegalStateException("No map selected");
        }
        
        // Now we return the position even if it's occupied, to allow replacement
        if (x < 0 || x >= currentMap.getSize().getWidth() || 
            y < 0 || y >= currentMap.getSize().getHeight()) {
            throw new IllegalArgumentException("Position (" + x + "," + y + ") is out of bounds");
        }
        
        return new Position(x, y);
    }

    public boolean isPositionOccupied(int x, int y) {
        return !currentMap.isCellEmpty(x, y);
    }
    
    /**
     * Gets information about what entity exists at the specified coordinates
     * @param x X coordinate
     * @param y Y coordinate
     * @return Description of the entity or null if no entity exists
     */
    public String getEntityInfoAt(int x, int y) {
        Position position = new Position(x, y);
        
        // Check industries
        Optional<Industry> industry = currentMap.getIndustries().stream()
                .filter(ind -> ind.getPosition().equals(position))
                .findFirst();
        if (industry.isPresent()) {
            return "Industry: " + industry.get().getNameID() + " (" + industry.get().getType() + ")";
        }
        
        // Check cities
        Optional<City> city = currentMap.getCities().stream()
                .filter(c -> c.getPosition().equals(position))
                .findFirst();
        if (city.isPresent()) {
            return "City: " + city.get().getNameID();
        }
        
        // Check stations
        Optional<Station> station = currentMap.getStations().stream()
                .filter(s -> s.getPosition().equals(position))
                .findFirst();
        if (station.isPresent()) {
            return "Station: " + station.get().getNameID();
        }
        
        return null;
    }
    
    /**
     * Removes any entity at the specified position
     * @param x X coordinate
     * @param y Y coordinate
     * @return true if an entity was removed, false otherwise
     */
    public boolean removeEntityAt(int x, int y) {
        return currentMap.removeEntityAt(x, y);
    }

    public int validateNumBlocks(int numBlocks) {
        if (numBlocks <= 0) {
            throw new IllegalArgumentException("Number of blocks must be positive");
        }
        return numBlocks;
    }

    public City saveCity(String nameID, Position position, List<HouseBlock> houseBlocks) {
        if (currentMap == null) {
            throw new IllegalStateException("No map selected");
        }

        // If position is not empty, we'll remove existing entity (already confirmed in UI)
        if (!currentMap.isCellEmpty(position.getX(), position.getY())) {
            removeEntityAt(position.getX(), position.getY());
        }

        City city = new City(nameID, position, houseBlocks);
        if (currentMap.addCity(city)) {
            mapRepository.save(currentMap);
            return city;
        }
        return null;
    }
} 