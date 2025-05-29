package pt.ipp.isep.dei.application.session;

import pt.ipp.isep.dei.application.session.emailService.EmailService;
import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.Repositories;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Represents the application session.
 */
public class ApplicationSession {
    private final AuthenticationRepository authenticationRepository;
    private static final String CONFIGURATION_FILENAME = "src/main/resources/config.properties";
    private static final String COMPANY_DESIGNATION = "Company.Designation";
    private EmailService emailService;

    /**
     * Initializes a new instance of ApplicationSession.
     */
    private ApplicationSession() {
        this.authenticationRepository = Repositories.getInstance().getAuthenticationRepository();
        Properties props = getProperties();
    }

    /**
     * Retrieves the current user session.
     *
     * @return The current user session.
     */
    public UserSession getCurrentSession() {
        pt.isep.lei.esoft.auth.UserSession userSession = this.authenticationRepository.getCurrentUserSession();
        return new UserSession(userSession);
    }

    /**
     * Retrieves the properties from the configuration file.
     *
     * @return The properties from the configuration file.
     */

    private Properties getProperties() {
        Properties props = new Properties();

        props.setProperty(COMPANY_DESIGNATION, "MusgoSublime");

        try {
            InputStream in = new FileInputStream(CONFIGURATION_FILENAME);
            props.load(in);
            in.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return props;
    }

    private static ApplicationSession singleton = null;

    /**
     * Retrieves the singleton instance of ApplicationSession.
     *
     * @return The singleton instance of ApplicationSession.
     */
    public static ApplicationSession getInstance() {
        if (singleton == null) {
            synchronized (ApplicationSession.class) {
                singleton = new ApplicationSession();
            }
        }
        return singleton;
    }

    public static EmailService getEmailService() {
        try {
            Properties props = new Properties();
            FileInputStream in=new FileInputStream(CONFIGURATION_FILENAME);
            props.load(in);
            in.close();

            String emailServiceClassName = props.getProperty("email.service");

            Class<?> emailServiceClass = Class.forName(emailServiceClassName);
            return (EmailService) emailServiceClass.getDeclaredConstructor().newInstance();
        } catch (Exception ex) {
            throw new RuntimeException("Failed to load email service", ex);
        }
    }



}
