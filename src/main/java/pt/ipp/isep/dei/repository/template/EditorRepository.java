package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Editor;
import pt.ipp.isep.dei.domain.template.Scenario;
import java.util.ArrayList;
import java.util.List;

public class EditorRepository {
    private final List<Editor> editors;

    public EditorRepository() {
        this.editors = new ArrayList<>();
    }

    public Editor getEditorByUsername(String username) {
        for (Editor editor : editors) {
            if (editor.getUsername().equals(username)) {
                return editor;
            }
        }
        return null;
    }

    public boolean addScenarioToEditor(Editor editor, Scenario scenario) {
        if (editor == null || scenario == null) {
            return false;
        }
        editor.addScenario(scenario);
        return true;
    }

    public void addEditor(Editor editor) {
        if (editor != null && !editors.contains(editor)) {
            editors.add(editor);
        }
    }
} 