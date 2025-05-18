package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Map;
import java.util.ArrayList;
import java.util.List;

public class MapRepository {
    private final List<Map> maps;

    public MapRepository() {
        this.maps = new ArrayList<>();
    }

    public List<Map> getAvailableMaps() {
        return new ArrayList<>(maps);
    }

    public boolean add(Map map) {
        if (map != null) {
            return maps.add(map);
        }
        return false;
    }

    public boolean save(Map map) {
        if (map == null) {
            return false;
        }

        int index = maps.indexOf(map);
        if (index >= 0) {
            maps.set(index, map);
            return true;
        }
        return false;
    }

    public Map getMap(String nameID) {
        for (Map map : maps) {
            if (map.getNameID().equals(nameID)) {
                return map;
            }
        }
        return null;
    }
} 