package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Scenario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class ScenarioRepository {
    private final List<Scenario> scenarios;
    private Scenario currentScenario;
    private Date currentDate;

    public ScenarioRepository() {
        this.scenarios = new ArrayList<>();
        // Initialize current date to today
        this.currentDate = new Date();
    }

    public List<Scenario> getAllScenarios() {
        return new ArrayList<>(scenarios);
    }

    public void addScenario(Scenario scenario) {
        if (scenario != null && !scenarios.contains(scenario)) {
            scenarios.add(scenario);
        }
    }

    public Scenario getScenario(String scenarioID) {
        for (Scenario scenario : scenarios) {
            if (scenario.getNameID().equals(scenarioID)) {
                return scenario;
            }
        }
        return null;
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }

    public Date getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(Date date) {
        if (date != null) {
            this.currentDate = date;
        }
    }
} 