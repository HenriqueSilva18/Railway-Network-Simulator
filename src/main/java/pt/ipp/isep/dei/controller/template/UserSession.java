package pt.ipp.isep.dei.controller.template;

public class UserSession {
    private String username;
    private boolean loggedIn;

    public UserSession(String username) {
        this.username = username;
        this.loggedIn = true;
    }

    public String getUsername() {
        return username;
    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }
} 