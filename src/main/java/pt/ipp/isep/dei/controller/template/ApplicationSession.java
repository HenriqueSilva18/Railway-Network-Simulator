package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Editor;

public class ApplicationSession {
    private static ApplicationSession instance;
    private Editor currentEditor;
    private Map currentMap;
    private UserSession currentSession;

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

    public void setCurrentMap(Map currentMap) {
        this.currentMap = currentMap;
    }

    public UserSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(UserSession currentSession) {
        this.currentSession = currentSession;
    }
} 