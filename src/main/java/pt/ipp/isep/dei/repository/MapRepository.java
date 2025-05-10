package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Map;

import java.util.ArrayList;
import java.util.List;

public class MapRepository {
    private List<Map> maps;

    public MapRepository() {
        this.maps = new ArrayList<>();
    }

    public boolean saveMap(Map map) {
        if (map == null || maps.contains(map)) {
            return false;
        }
        return maps.add(map);
    }

    public Map getMapByName(String name) {
        return maps.stream()
                .filter(m -> m.getNameID().equals(name))
                .findFirst()
                .orElse(null);
    }
}