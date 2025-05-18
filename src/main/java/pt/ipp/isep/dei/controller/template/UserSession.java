package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Player;

public class UserSession {
    private final String userEmail;
    private boolean loggedIn;
    private final Player player;

    public UserSession(String userEmail) {
        this.userEmail = userEmail;
        this.loggedIn = true;
        // Initialize player with default budget
        this.player = new Player(userEmail, 500000); // Starting with 500k budget
    }

    public String getUserEmail() {
        return userEmail;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public Player getUser() {
        return player;
    }
} 