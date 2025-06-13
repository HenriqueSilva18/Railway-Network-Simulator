package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Editor implements Serializable {
    private static final long serialVersionUID = 1L;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Editor editor = (Editor) o;
        return Objects.equals(username, editor.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(username);
    }

    @Override
    public String toString() {
        return username;
    }
} 