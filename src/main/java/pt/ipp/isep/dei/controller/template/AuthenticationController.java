package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.domain.template.Editor;
import pt.ipp.isep.dei.domain.template.Player;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

public class AuthenticationController {

    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_PLAYER = "PLAYER";

    private final AuthenticationRepository authenticationRepository;

    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    public boolean doLogin(String email, String pwd) {
        try {
            boolean success = authenticationRepository.doLogin(email, pwd);
            if (success) {
                // Create and set the user session
                UserSession session = new UserSession(email);
                ApplicationSession.getInstance().setCurrentSession(session);
                
                // Check user roles and set appropriate session data
                List<UserRoleDTO> roles = getUserRoles();
                if (roles != null) {
                    for (UserRoleDTO role : roles) {
                        if (role.getDescription().equals(ROLE_EDITOR)) {
                            Editor editor = new Editor(email, pwd);
                            ApplicationSession.getInstance().setCurrentEditor(editor);
                        } else if (role.getDescription().equals(ROLE_PLAYER)) {
                            Player player = Repositories.getInstance().getPlayerRepository().getPlayerByEmail(email);
                            ApplicationSession.getInstance().setCurrentPlayer(player);
                        }
                    }
                }
                return true;
            }
            return false;
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
        UserSession currentSession = ApplicationSession.getInstance().getCurrentSession();
        if (currentSession != null) {
            currentSession.setLoggedIn(false);
        }
        authenticationRepository.doLogout();
        // Clear all session data
        ApplicationSession.getInstance().setCurrentSession(null);
        ApplicationSession.getInstance().setCurrentEditor(null);
        ApplicationSession.getInstance().setCurrentPlayer(null);
        ApplicationSession.getInstance().setCurrentMap(null);
        ApplicationSession.getInstance().setCurrentScenario(null);

    }
}