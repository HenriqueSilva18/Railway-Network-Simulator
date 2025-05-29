package pt.ipp.isep.dei.application.session;

import pt.isep.lei.esoft.auth.mappers.dto.UserRoleDTO;

import java.util.List;

/**
 * Represents a user session.
 */
public class UserSession {

    private pt.isep.lei.esoft.auth.UserSession userSession;

    /**
     * Initializes a new instance of UserSession.
     *
     * @param userSession The user session.
     */
    public UserSession(pt.isep.lei.esoft.auth.UserSession userSession) {
        this.userSession = userSession;
    }

    /**
     * Retrieves the email of the user.
     *
     * @return The email of the user.
     */
    public String getUserEmail() {
        return userSession.getUserId().getEmail();
    }

    /**
     * Retrieves the name of the user.
     *
     * @return The name of the user.
     */
    public String getUserName() {
        return this.userSession.getUserName();
    }

    /**
     * Retrieves the roles of the user.
     *
     * @return The roles of the user.
     */
    public List<UserRoleDTO> getUserRoles() {
        return this.userSession.getUserRoles();
    }

    /**
     * Logs the user out.
     */
    public void doLogout() {
        this.userSession.doLogout();
    }

    /**
     * Checks if the user is logged in.
     *
     * @return True if the user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return this.userSession.isLoggedIn();
    }

    /**
     * Checks if the user is logged in with a specific role.
     *
     * @param roleId The ID of the role to check.
     * @return True if the user is logged in with the specified role, false otherwise.
     */
    public boolean isLoggedInWithRole(String roleId) {
        return this.userSession.isLoggedInWithRole(roleId);
    }
}
