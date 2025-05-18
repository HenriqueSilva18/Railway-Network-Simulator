package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.ArrayList;
import java.util.List;

public class ListStationController {

    /**
     * Gets the list of available maps from the repository
     * @return List of available maps
     */
    public List<Map> getAvailableMaps() {
        MapRepository mapRepository = Repositories.getInstance().getMapRepository();
        return mapRepository.getAvailableMaps();
    }

    /**
     * Loads a specific map by its ID
     * @param mapId The ID of the map to load
     * @return The loaded map or null if not found
     */
    public Map loadMap(String mapId) {
        MapRepository mapRepository = Repositories.getInstance().getMapRepository();
        Map map = mapRepository.getMap(mapId);
        if (map != null) {
            ApplicationSession.getInstance().setCurrentMap(map);
        }
        return map;
    }

    /**
     * Loads a map with a specific scenario
     * @param mapId The ID of the map to load
     * @param scenarioId The ID of the scenario to load
     * @return True if map and scenario were loaded successfully, false otherwise
     */
    public boolean loadMap(String mapId, String scenarioId) {
        Map map = loadMap(mapId);
        if (map != null) {
            return map.loadScenario(scenarioId);
        }
        return false;
    }

    /**
     * Gets the available scenarios for a specific map
     * @param mapId The ID of the map to get scenarios for
     * @return List of scenario IDs
     */
    public List<String> getMapScenarios(String mapId) {
        MapRepository mapRepository = Repositories.getInstance().getMapRepository();
        Map map = mapRepository.getMap(mapId);
        if (map != null) {
            return map.getScenarios();
        }
        return new ArrayList<>();
    }

    /**
     * Gets all stations from the currently loaded map
     * @return List of all stations
     */
    public List<Station> getStations() {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap != null) {
            return currentMap.getStations();
        }
        return new ArrayList<>();
    }

    /**
     * Gets details for a specific station by ID
     * @param stationId The ID of the station to get details for
     * @return The station or null if not found
     */
    public Station getStationDetails(String stationId) {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap != null) {
            List<Station> stations = currentMap.getStations();
            return stations.stream()
                    .filter(station -> station.getNameID().equals(stationId))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
} 