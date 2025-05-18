package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Editor {
    private String username;
    private String password;
    private List<Map> createdMaps;
    private List<Scenario> createdScenarios;

    public Editor(String username, String password) {
        this.username = username;
        this.password = password;
        this.createdMaps = new ArrayList<>();
        this.createdScenarios = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Map> getCreatedMaps() {
        return new ArrayList<>(createdMaps);
    }

    public List<Scenario> getCreatedScenarios() {
        return new ArrayList<>(createdScenarios);
    }

    public void addMap(Map map) {
        if (map != null && !createdMaps.contains(map)) {
            createdMaps.add(map);
        }
    }

    public void addScenario(Scenario scenario) {
        if (scenario != null && !createdScenarios.contains(scenario)) {
            createdScenarios.add(scenario);
        }
    }
} 