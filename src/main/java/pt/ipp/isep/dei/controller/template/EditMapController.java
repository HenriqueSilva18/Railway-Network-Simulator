package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;

public class EditMapController {
    private final MapRepository mapRepository;

    public EditMapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public boolean loadMap(Map selectedMap) {
        if (selectedMap == null) {
            return false;
        }

        ApplicationSession.getInstance().setCurrentMap(selectedMap);
        return true;
    }

    public Map getCurrentMap() {
        return ApplicationSession.getInstance().getCurrentMap();
    }
} 