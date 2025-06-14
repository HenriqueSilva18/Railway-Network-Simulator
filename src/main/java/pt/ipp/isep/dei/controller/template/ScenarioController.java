package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.domain.template.Map;

import java.util.List;
import java.util.Optional;

public class ScenarioController {
    private final ScenarioRepository scenarioRepository;
    private final EditorRepository editorRepository;

    public ScenarioController() {
        Repositories repositories = Repositories.getInstance();
        this.scenarioRepository = repositories.getScenarioRepository();
        this.editorRepository = repositories.getEditorRepository();
    }

    public List<Scenario> getAvailableScenarios() {
        return scenarioRepository.getAvailableScenarios();
    }

    public boolean saveScenario(Scenario scenario) {
        if (scenario == null) return false;
        return scenarioRepository.save(scenario);
    }

    public Scenario loadScenario(String scenarioId) {
        if (scenarioId == null) return null;

        // Get the current map
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            System.out.println("No map is currently loaded. Please load a map first.");
            return null;
        }

        // First try to load from file
        Scenario scenario = scenarioRepository.loadScenarioFromFile(scenarioId);
        
        // If not found in file, try to find in editor repository
        if (scenario == null) {
            Optional<Scenario> existingScenario = editorRepository.findScenarioByNameID(scenarioId);
            if (existingScenario.isPresent()) {
                scenario = existingScenario.get();
            } else {
                System.out.println("Failed to load scenario: Scenario not found.");
                return null;
            }
        }

        // Ensure the scenario is associated with the current map
        if (!scenario.getMap().getNameID().equals(currentMap.getNameID())) {
            System.out.println("Failed to load scenario: Scenario does not belong to the current map.");
            return null;
        }
        
        // Add to scenario repository
        scenarioRepository.save(scenario);
        
        // Add to editor repository
        editorRepository.addScenarioToEditor(
            ApplicationSession.getInstance().getCurrentEditor(), 
            scenario
        );
        
        // Set as current scenario in both repositories
        scenarioRepository.setCurrentScenario(scenario);
        ApplicationSession.getInstance().setCurrentScenario(scenario);

        return scenario;
    }

    public List<String> getAvailableScenarioFiles() {
        return scenarioRepository.getAvailableScenarioFiles();
    }

    public Scenario getCurrentScenario() {
        return ApplicationSession.getInstance().getCurrentScenario();
    }

    public boolean setCurrentScenario(Scenario scenario) {
        if (scenario == null) return false;
        ApplicationSession.getInstance().setCurrentScenario(scenario);
        scenarioRepository.setCurrentScenario(scenario);
        return true;
    }

    public Scenario getScenarioById(String scenarioId) {
        return scenarioRepository.getScenario(scenarioId);
    }
} 