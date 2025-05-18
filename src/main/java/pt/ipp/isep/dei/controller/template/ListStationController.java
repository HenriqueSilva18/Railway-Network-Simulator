package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.StationRepository;

import java.util.ArrayList;
import java.util.List;

public class ListStationController {

    private final StationRepository stationRepository;

    public ListStationController() {
        this.stationRepository = Repositories.getInstance().getStationRepository();
    }

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
     * Gets all stations available on the current map
     */
    public List<Station> getStations() {
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return new ArrayList<>();
        }

        // Get stations from the map
        List<Station> stations = currentMap.getStations();
        
        // Store stations in the repository for later retrieval
        for (Station station : stations) {
            stationRepository.add(station);
        }
        
        return stations;
    }

    /**
     * Gets details for a specific station
     */
    public Station getStationDetails(String stationId) {
        return stationRepository.getStation(stationId);
    }

    /**
     * Get station by ID
     */
    public Station getStation(String stationId) {
        return stationRepository.getStation(stationId);
    }
} 