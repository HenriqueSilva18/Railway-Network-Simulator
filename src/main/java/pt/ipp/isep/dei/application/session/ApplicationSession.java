package pt.ipp.isep.dei.application.session;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.application.session.emailService.EmailService;
import pt.ipp.isep.dei.application.session.emailService.adapters.DEIService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents the application session.
 */
public class ApplicationSession {
    private static ApplicationSession instance;
    private Map currentMap;
    private Scenario currentScenario;
    private final AuthenticationRepository authRepository;
    private final EmailService emailService;
    private static final String PROPERTIES_FILE = "application.properties";
    private static final String COMPANY_DESIGNATION = "Company.Designation";

    /**
     * Initializes a new instance of ApplicationSession.
     */
    private ApplicationSession() {
        Properties props = loadProperties();
        this.authRepository = Repositories.getInstance().getAuthenticationRepository();
        this.emailService = new DEIService();
    }

    /**
     * Retrieves the current user session.
     *
     * @return The current user session.
     */
    public UserSession getCurrentSession() {
        pt.isep.lei.esoft.auth.UserSession userSession = this.authRepository.getCurrentUserSession();
        return new UserSession(userSession);
    }

    /**
     * Retrieves the properties from the configuration file.
     *
     * @return The properties from the configuration file.
     */
    private Properties loadProperties() {
        Properties props = new Properties();
        try (InputStream in = new FileInputStream(PROPERTIES_FILE)) {
            props.load(in);
        } catch (IOException ex) {
            // If properties file doesn't exist, use default values
            props.setProperty(COMPANY_DESIGNATION, "Default Company");
        }
        return props;
    }

    /**
     * Retrieves the singleton instance of ApplicationSession.
     *
     * @return The singleton instance of ApplicationSession.
     */
    public static synchronized ApplicationSession getInstance() {
        if (instance == null) {
            instance = new ApplicationSession();
        }
        return instance;
    }

    public Map getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map map) {
        this.currentMap = map;
    }

    public Scenario getCurrentScenario() {
        return currentScenario;
    }

    public void setCurrentScenario(Scenario scenario) {
        this.currentScenario = scenario;
    }

    public AuthenticationRepository getAuthRepository() {
        return authRepository;
    }

    public EmailService getEmailService() {
        return emailService;
    }
}
