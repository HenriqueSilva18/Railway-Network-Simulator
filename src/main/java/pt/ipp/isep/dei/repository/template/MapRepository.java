package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapRepository {
    private final List<Map> maps;

    public MapRepository() {
        this.maps = new ArrayList<>();
    }

    public boolean save(Map map) {
        if (map == null) {
            return false;
        }
        return maps.add(map);
    }

    public Optional<Map> getMapByID(String nameID) {
        return maps.stream()
                .filter(map -> map.getNameID().equals(nameID))
                .findFirst();
    }

    public List<Map> getAllMaps() {
        return new ArrayList<>(maps);
    }
} 