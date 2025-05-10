package pt.ipp.isep.dei.controller;

import pt.ipp.isep.dei.domain.Editor;
import pt.ipp.isep.dei.domain.Map;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.isep.lei.esoft.auth.UserSession;
import pt.isep.lei.esoft.auth.domain.model.Email;

public class CreateMapController {
    private final Repositories repositories;

    public CreateMapController() {
        this.repositories = Repositories.getInstance();
    }

    public boolean validateMapName(String name) {
        return name != null && name.matches("^[a-zA-Z0-9 _-]+$");
    }

    public boolean validateMapDimensions(int width, int height) {
        return width > 0 && height > 0;
    }

    public Map createNewMap(String name, int width, int height) {
        // Get current session from authentication repository
        UserSession session = repositories.getAuthenticationRepository().getCurrentUserSession();

        if (!session.isLoggedIn()) {
            throw new IllegalStateException("No user logged in");
        }

        // Get current user email and retrieve Editor from repository
        Email userEmail = session.getUserId();
        Editor currentEditor = repositories.getEditorRepository().getEditorByEmail(userEmail);

        if (currentEditor == null) {
            throw new IllegalStateException("Current user is not an editor");
        }

        // Create and save map
        Map newMap = currentEditor.createMap(name, width, height);
        boolean saved = repositories.getMapRepository().saveMap(newMap);

        return saved ? newMap : null;
    }
}