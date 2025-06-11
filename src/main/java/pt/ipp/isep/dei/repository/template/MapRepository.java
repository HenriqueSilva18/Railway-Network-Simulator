package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Size;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MapRepository {
    private final List<Map> maps;
    private static final String SCENARIOS_PATH = "docs/mdisc/data";

    public MapRepository() {
        this.maps = new ArrayList<>();
        initializeMaps();
    }

    private void initializeMaps() {
        // Initialize predefined maps with their sizes
         Map iberianPeninsula = Map.createMap("iberian_peninsula", Size.createSize(12, 9), 1);
        Map france = Map.createMap("france", Size.createSize(10, 10), 1);
           Map italy = Map.createMap("italy", Size.createSize(15, 15), 1);


        // Add maps to repository
        maps.add(iberianPeninsula);
        maps.add(france);
        maps.add(italy);

        // Load scenarios for each map
        loadScenarios();
    }

    private void loadScenarios() {
        File scenariosDir = new File(SCENARIOS_PATH);
        if (!scenariosDir.exists() || !scenariosDir.isDirectory()) {
            return;
        }

        File[] files = scenariosDir.listFiles((dir, name) -> name.startsWith("scenario") && name.endsWith("_lines.csv"));
        if (files == null) {
            return;
        }

        for (File file : files) {
            String scenarioId = file.getName().replace("_lines.csv", "");
            for (Map map : maps) {
                map.addScenario(scenarioId);
            }
        }
    }

    public List<Map> getAvailableMaps() {
        return new ArrayList<>(maps);
    }

    public Map getMapByID(String nameID) {
        if (nameID == null) {
            return null;
        }
        return maps.stream()
                .filter(map -> nameID.equals(map.getNameID()))
                .findFirst()
                .orElse(null);
    }

    public boolean add(Map map) {
        if (map == null || maps.contains(map)) {
            return false;
        }
        return maps.add(map);
    }

    public boolean save(Map map) {
        if (map == null) {
            return false;
        }
        
        Optional<Map> existingMap = maps.stream()
                .filter(m -> m.getNameID().equals(map.getNameID()))
                .findFirst();
                
        if (existingMap.isPresent()) {
            maps.remove(existingMap.get());
            maps.add(map);
            return true;
        }
        
        return add(map);
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