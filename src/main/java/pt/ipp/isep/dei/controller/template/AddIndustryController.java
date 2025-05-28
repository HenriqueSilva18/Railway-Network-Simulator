package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Station;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.IndustryRepository;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;
import java.util.Optional;

public class AddIndustryController {
    private final IndustryRepository industryRepository;
    private final MapRepository mapRepository;
    private final Map currentMap;

    public AddIndustryController() {
        this.industryRepository = Repositories.getInstance().getIndustryRepository();
        this.mapRepository = Repositories.getInstance().getMapRepository();
        this.currentMap = ApplicationSession.getInstance().getCurrentMap();
    }

    public List<Industry> getAvailableIndustries() {
        return industryRepository.getAvailableIndustries();
    }

    public String validateIndustryName(String nameID) {
        if (nameID == null || nameID.trim().isEmpty()) {
            throw new IllegalArgumentException("Industry name cannot be empty");
        }
        if (!nameID.matches("^[a-zA-Z0-9]+([_-][a-zA-Z0-9]+)*$")) {
            throw new IllegalArgumentException("Industry name contains invalid characters or format");
        }

        if (isNameIDTaken(nameID)) {
            throw new IllegalArgumentException("Industry name already exists");
        }

        return nameID;
    }

    public boolean validateIndustry(String nameID, int x, int y) {
        if (nameID == null || nameID.isEmpty()) {
            return false;
        }

        if (currentMap == null) {
            return false;
        }

        // We'll now allow validation even if cell is not empty to support replacement
        return true;
    }

    public Industry createIndustry(String nameID, int x, int y, Industry selectedIndustry) {
        if (!validateIndustry(nameID, x, y)) {
            return null;
        }

        // If position is not empty, we've confirmed replacement in the UI
        if (!currentMap.isCellEmpty(x, y)) {
            removeEntityAt(x, y);
        }

        Industry industry = new Industry(nameID, selectedIndustry.getType(), selectedIndustry.getSector(), 1900, new Position(x, y));
        if (currentMap.addIndustry(industry)) {
            mapRepository.save(currentMap);
            return industry;
        }

        return null;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public boolean isNameIDTaken(String nameID) {
        if (currentMap == null) {
            return false;
        }
        return currentMap.getIndustries().stream()
                .anyMatch(industry -> industry.getNameID().equals(nameID));
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
} 