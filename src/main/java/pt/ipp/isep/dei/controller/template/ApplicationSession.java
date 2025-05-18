package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Editor;
import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Station;

public class ApplicationSession {
    private static ApplicationSession instance = null;
    private Editor currentEditor;
    private Map currentMap;
    private UserSession currentSession;
    private Player currentPlayer;
    private Scenario currentScenario;
    private Station currentStation;

    private ApplicationSession() {
        // Private constructor for singleton
    }

    public static synchronized ApplicationSession getInstance() {
        if (instance == null) {
            instance = new ApplicationSession();
        }
        return instance;
    }

    public Editor getCurrentEditor() {
        return currentEditor;
    }

    public void setCurrentEditor(Editor currentEditor) {
        this.currentEditor = currentEditor;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    public UserSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(UserSession session) {
        this.currentSession = session;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(Player player) {
        this.currentPlayer = player;
        if (this.currentSession != null) {
            // Update the player in the current session as well
            this.currentSession = new UserSession(player.getUsername());
        }
    }
    
    public Scenario getCurrentScenario() {
        return currentScenario;
    }
    
    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }
    
    public Station getCurrentStation() {
        return currentStation;
    }
    
    public void setCurrentStation(Station station) {
        this.currentStation = station;
    }
} 