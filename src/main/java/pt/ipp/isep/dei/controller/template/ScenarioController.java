package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.List;
import java.util.Optional;

public class ScenarioController {
    private final ScenarioRepository scenarioRepository;

    public ScenarioController() {
        Repositories repositories = Repositories.getInstance();
        this.scenarioRepository = repositories.getScenarioRepository();
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
        Scenario scenario = scenarioRepository.loadScenarioFromFile(scenarioId);
        if (scenario != null) {
            scenarioRepository.save(scenario); // Add to in-memory repository
        }
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
        return true;
    }

    public Scenario getScenarioById(String scenarioId) {
        return scenarioRepository.getScenario(scenarioId);
    }
} 