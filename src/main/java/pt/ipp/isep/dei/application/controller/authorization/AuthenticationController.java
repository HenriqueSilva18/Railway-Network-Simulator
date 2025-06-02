package pt.ipp.isep.dei.application.controller.authorization;

import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

/**
 * Controller class responsible for managing user authentication.
 */
public class AuthenticationController {

    public static final String ROLE_ADMIN = "ADMINISTRATOR";
    public static final String ROLE_EDITOR = "EDITOR";
    public static final String ROLE_PLAYER = "PLAYER";

    //private final ApplicationSession applicationSession;
    private final AuthenticationRepository authenticationRepository;

    /**
     * Constructs an AuthenticationController.
     */
    public AuthenticationController() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
    }

    /**
     * Attempts to log in with the given credentials.
     *
     * @param email The user's email.
     * @param pwd   The user's password.
     * @return True if the login was successful, false otherwise.
     */
    public boolean doLogin(String email, String pwd) {
        try {
            return authenticationRepository.doLogin(email, pwd);
        } catch (IllegalArgumentException ex) {
            return false;
        }
    }

    /**
     * Retrieves the email of the currently logged-in user.
     *
     * @return The email of the currently logged-in user.
     */
    public String getCurrentUserEmail() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserEmail();
        } else {
            return "aa";
        }
    }



    /**
     * Logs out the current user.
     */
    public void doLogout() {
        authenticationRepository.doLogout();
    }

    public List<UserRoleDTO> getUserRoles() {
        if (authenticationRepository.getCurrentUserSession().isLoggedIn()) {
            return authenticationRepository.getCurrentUserSession().getUserRoles();
        }
        return null;
    }
}
