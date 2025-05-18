package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.MapRepository;

import java.util.List;

public class EditMapController {
    private final MapRepository mapRepository;
    private Map currentMap;

    public EditMapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public boolean loadMap(Map map) {
        if (map == null) {
            return false;
        }
        this.currentMap = map;
        ApplicationSession.getInstance().setCurrentMap(map);
        return true;
    }

    public String getMapLayout() {
        if (currentMap == null) {
            return "No map loaded";
        }

        StringBuilder layout = new StringBuilder();
        layout.append("Map: ").append(currentMap.getNameID()).append("\n");
        layout.append("Size: ").append(currentMap.getSize().getWidth())
              .append("x").append(currentMap.getSize().getHeight()).append("\n\n");

        for (int y = 0; y < currentMap.getSize().getHeight(); y++) {
            for (int x = 0; x < currentMap.getSize().getWidth(); x++) {
                if (!currentMap.isCellEmpty(x, y)) {
                    layout.append("X "); // X for occupied cell
                } else {
                    layout.append(". "); // . for empty cell
                }
            }
            layout.append("\n");
        }

        return layout.toString();
    }

    public Map getCurrentMap() {
        return ApplicationSession.getInstance().getCurrentMap();
    }
} 