package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.domain.template.Editor;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

public class AuthenticationController {

    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    public static final String ROLE_EDITOR = "EDITOR";

    private final AuthenticationRepository authenticationRepository;

    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    public boolean doLogin(String email, String pwd) {
        try {
            boolean success = authenticationRepository.doLogin(email, pwd);
            if (success) {
                // Set the current session in ApplicationSession
                ApplicationSession.getInstance().setCurrentSession(new UserSession(email));
                
                // Check if user has EDITOR role and set current editor
                List<UserRoleDTO> roles = getUserRoles();
                if (roles != null) {
                    for (UserRoleDTO role : roles) {
                        if (role.getDescription().equals(ROLE_EDITOR)) {
                            Editor editor = new Editor(email, pwd);
                            ApplicationSession.getInstance().setCurrentEditor(editor);
                            break;
                        }
                    }
                }
            }
            return success;
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }

    public void doLogout() {
        authenticationRepository.doLogout();
        // Clear the current session and editor in ApplicationSession
        ApplicationSession.getInstance().setCurrentSession(null);
        ApplicationSession.getInstance().setCurrentEditor(null);
    }
}