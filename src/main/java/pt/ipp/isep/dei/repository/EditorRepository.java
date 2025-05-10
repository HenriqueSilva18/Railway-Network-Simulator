package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.Editor;
import pt.isep.lei.esoft.auth.domain.model.Email;
import java.util.HashMap;
import java.util.Map;

public class EditorRepository {
    private final Map<Email, Editor> editors = new HashMap<>();

    public void addEditor(Editor editor) {
        editors.put(editor.getEmail(), editor);
    }

    public Editor getEditorByEmail(Email email) {
        return editors.get(email);
    }
}