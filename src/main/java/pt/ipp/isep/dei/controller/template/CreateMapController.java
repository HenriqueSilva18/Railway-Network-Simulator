package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Size;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

public class CreateMapController {
    private final MapRepository mapRepository;

    public CreateMapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
    }

    public boolean validateMapData(String nameID, int width, int height) {
        return Map.validateMapName(nameID) && Size.validateSize(width, height);
    }

    public Map createMap(String nameID, int width, int height) {
        Size size = Size.createSize(width, height);
        Map map = Map.createMap(nameID, size);
        
        if (!mapRepository.save(map)) {
            throw new IllegalStateException("Failed to save map");
        }
        
        return map;
    }
} 