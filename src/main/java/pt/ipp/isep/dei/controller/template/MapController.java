package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;
import java.util.Calendar;

public class MapController {
    private final MapRepository mapRepository;
    private final EditorRepository editorRepository;
    private final ScenarioRepository scenarioRepository;

    public MapController() {
        Repositories repositories = Repositories.getInstance();
        this.mapRepository = repositories.getMapRepository();
        this.editorRepository = repositories.getEditorRepository();
        this.scenarioRepository = repositories.getScenarioRepository();
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

        // Set the current map in the application session
        ApplicationSession.getInstance().setCurrentMap(map);

        // Find the scenario by ID
        Optional<Scenario> scenarioOpt = editorRepository.findScenarioByNameID(scenarioID);

        if (scenarioOpt.isPresent()) {
            Scenario scenario = scenarioOpt.get();

            // Set the current scenario in the application session
            ApplicationSession.getInstance().setCurrentScenario(scenario);

            // Set the current scenario in the scenario repository
            scenarioRepository.setCurrentScenario(scenario);

            // Set a default current date halfway through the scenario period
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(scenario.getStartDate());
            int startYear = calendar.get(Calendar.YEAR);
            calendar.setTime(scenario.getEndDate());
            int endYear = calendar.get(Calendar.YEAR);
            int midYear = startYear + (endYear - startYear) / 2;

            calendar.set(midYear, Calendar.JANUARY, 1);
            scenarioRepository.setCurrentDate(calendar.getTime());

            return true;
        }

        return false;
    }

    public Scenario getScenario(String scenarioID) {
        Optional<Scenario> scenarioOpt = editorRepository.findScenarioByNameID(scenarioID);
        return scenarioOpt.orElse(null);
    }

    public String getMapLayout(String mapID, String scenarioID) {
        Map map = mapRepository.getMap(mapID);
        if (map == null) {
            return "Map not found";
        }

        // Simple ASCII representation of the map
        StringBuilder layout = new StringBuilder();
        layout.append("Map: ").append(map.getNameID()).append("\n");
        layout.append("Size: ").append(map.getSize().getWidth()).append("x").append(map.getSize().getHeight()).append("\n");
        layout.append("Cities: ").append(map.getCities().size()).append("\n");
        layout.append("Industries: ").append(map.getIndustries().size()).append("\n");

        return layout.toString();
    }
} 