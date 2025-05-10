package pt.ipp.isep.dei.domain;

import pt.isep.lei.esoft.auth.domain.model.Email;
import java.util.ArrayList;
import java.util.List;

public class Editor {
    private final Email email;
    private String username;
    private String password;
    private List<Map> createdMaps;
    /*private List<Scenario> createdScenarios;*/

    public Editor(Email email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
        this.createdMaps = new ArrayList<>();
        /*this.createdScenarios = new ArrayList<>();*/
    }

    public Map createMap(String name, int width, int height) {
        Map newMap = new Map(name, width, height);
        this.createdMaps.add(newMap);
        return newMap;
    }

    // Getters
    public Email getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public List<Map> getCreatedMaps() {
        return createdMaps;
    }
}