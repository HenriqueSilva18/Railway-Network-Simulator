package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.template.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MapRepository {
    private final java.util.Map<String, Map> maps;

    public MapRepository() {
        this.maps = new ConcurrentHashMap<>();
    }

    public List<Map> getAvailableMaps() {
        return new ArrayList<>(maps.values());
    }

    public Map getMap(String mapId) {
        return maps.get(mapId);
    }

    public boolean save(Map map) {
        if (map == null || map.getNameID() == null) return false;
        maps.put(map.getNameID(), map);
        return true;
    }

    public boolean delete(String mapId) {
        if (mapId == null) return false;
        return maps.remove(mapId) != null;
    }

    public List<String> getScenarios(String mapId) {
        Map map = maps.get(mapId);
        if (map == null) return new ArrayList<>();
        return map.getAvailableScenarios();
    }
} 