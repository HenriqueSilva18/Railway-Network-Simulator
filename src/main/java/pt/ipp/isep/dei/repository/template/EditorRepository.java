package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Editor;
import pt.ipp.isep.dei.domain.template.Scenario;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class EditorRepository {
    private final List<Editor> editors;
    private final Map<Editor, List<Scenario>> editorScenarios;

    public EditorRepository() {
        this.editors = new ArrayList<>();
        this.editorScenarios = new HashMap<>();
    }

    public Editor getEditorByUsername(String username) {
        for (Editor editor : editors) {
            if (editor.getUsername().equals(username)) {
                return editor;
            }
        }
        return null;
    }

    public boolean addEditor(Editor editor) {
        if (editor == null || editors.contains(editor)) {
            return false;
        }
        editors.add(editor);
        editorScenarios.put(editor, new ArrayList<>());
        return true;
    }

    public boolean addScenarioToEditor(Editor editor, Scenario scenario) {
        if (editor == null || scenario == null || !editors.contains(editor)) {
            return false;
        }
        List<Scenario> scenarios = editorScenarios.get(editor);
        if (scenarios == null) {
            scenarios = new ArrayList<>();
            editorScenarios.put(editor, scenarios);
        }
        return scenarios.add(scenario);
    }

    public List<Editor> getEditors() {
        return new ArrayList<>(editors);
    }

    public List<Scenario> getEditorScenarios(Editor editor) {
        List<Scenario> scenarios = editorScenarios.get(editor);
        return scenarios != null ? new ArrayList<>(scenarios) : new ArrayList<>();
    }

    public List<Scenario> getAllScenarios() {
        List<Scenario> allScenarios = new ArrayList<>();
        for (List<Scenario> scenarios : editorScenarios.values()) {
            allScenarios.addAll(scenarios);
        }
        return allScenarios;
    }
    
    /**
     * Find a scenario by its nameID, regardless of which editor created it.
     * This is useful when we only know the scenario name but not the editor.
     * 
     * @param nameID The ID of the scenario to find
     * @return Optional containing the scenario if found, empty otherwise
     */
    public Optional<Scenario> findScenarioByNameID(String nameID) {
        System.out.println("DEBUG: Searching for scenario with ID: " + nameID);
        System.out.println("DEBUG: Number of editors: " + editors.size());
        
        for (Editor editor : editors) {
            System.out.println("DEBUG: Checking editor: " + editor.getUsername());
            List<Scenario> scenarios = editorScenarios.get(editor);
            if (scenarios != null) {
                System.out.println("DEBUG: Editor has " + scenarios.size() + " scenarios");
                for (Scenario scenario : scenarios) {
                    System.out.println("DEBUG: Checking scenario: " + scenario.getNameID());
                    if (scenario.getNameID().equals(nameID)) {
                        System.out.println("DEBUG: Found scenario: " + nameID + " for editor " + editor.getUsername());
                        return Optional.of(scenario);
                    }
                }
            }
        }
        System.out.println("DEBUG: No scenario found with ID: " + nameID);
        return Optional.empty();
    }
} 