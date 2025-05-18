package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.List;
import java.util.Optional;

public class MapController {
    private final MapRepository mapRepository;
    private final EditorRepository editorRepository;

    public MapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
        this.editorRepository = Repositories.getInstance().getEditorRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public List<String> getMapScenarios(String mapID) {
        Map map = mapRepository.getMap(mapID);
        return map != null ? map.getScenarios() : List.of();
    }

    public boolean loadMap(String mapID, String scenarioID) {
        Map map = mapRepository.getMap(mapID);
        if (map == null) {
            return false;
        }

        // Set current map in application session
        ApplicationSession.getInstance().setCurrentMap(map);
        
        // Find the scenario in the editor repository
        Optional<Scenario> scenario = editorRepository.getAllScenarios().stream()
                .filter(s -> s.getNameID().equals(scenarioID) && 
                           s.getMap() != null && 
                           s.getMap().getNameID().equals(mapID))
                .findFirst();

        if (scenario.isPresent()) {
            // Load cities and industries from the scenario
            map.getCities().clear();
            map.getIndustries().clear();
            
            map.getCities().addAll(scenario.get().getTweakedCityList());
            map.getIndustries().addAll(scenario.get().getAvailableIndustryList());
            
            return true;
        }
        
        // If no scenario found, try loading using the map's loadScenario method
        return map.loadScenario(scenarioID);
    }
} 