package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.HouseBlock;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;

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
        if (currentMap.getCities().stream().anyMatch(city -> city.getNameID().equals(name))) {
            throw new IllegalArgumentException("City name already exists");
        }
        return name;
    }

    public Position validateCoordinates(int x, int y) {
        if (currentMap == null) {
            throw new IllegalStateException("No map selected");
        }
        if (!currentMap.isPositionAvailable(x, y)) {
            throw new IllegalArgumentException("Position is not available");
        }
        return new Position(x, y);
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

        City city = new City(nameID, position, houseBlocks);
        if (currentMap.addCity(city)) {
            mapRepository.save(currentMap);
            return city;
        }
        return null;
    }
} 