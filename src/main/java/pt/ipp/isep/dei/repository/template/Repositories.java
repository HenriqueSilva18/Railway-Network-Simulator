package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.repository.EditorRepository;
import pt.ipp.isep.dei.repository.MapRepository;
import pt.ipp.isep.dei.repository.template.AuthenticationRepository;
import pt.ipp.isep.dei.repository.template.OrganizationRepository;
import pt.ipp.isep.dei.repository.template.TaskCategoryRepository;

/**
 * Inspired on https://refactoring.guru/design-patterns/singleton/java/example
 *
 * The Repositories class works as a Singleton. It defines the getInstance method that serves as an alternative
 * to the constructor and lets client classes access the same instance of this class over and over.
 */
public class Repositories {
    private static Repositories instance;

    // Original template repositories
    private final OrganizationRepository organizationRepository;
    private final TaskCategoryRepository taskCategoryRepository;
    private final AuthenticationRepository authenticationRepository;

    // New railway simulation repositories
    private final EditorRepository editorRepository;
    private final MapRepository mapRepository;
    /* private final ScenarioRepository scenarioRepository; */

    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */
    private Repositories() {
        // Initialize template repositories
        this.organizationRepository = new OrganizationRepository();
        this.taskCategoryRepository = new TaskCategoryRepository();
        this.authenticationRepository = new AuthenticationRepository();

        // Initialize railway simulation repositories
        this.editorRepository = new EditorRepository();
        this.mapRepository = new MapRepository();
        /* this.scenarioRepository = new ScenarioRepository(); */
    }

    /**
     * This is the static method that controls the access to the singleton instance.
     * On the first run, it creates a singleton object and places it into the static attribute.
     * On subsequent runs, it returns the existing object stored in the static attribute.
     */
    public static synchronized Repositories getInstance() {
        if (instance == null) {
            instance = new Repositories();
        }
        return instance;
    }

    // Template repository accessors
    public OrganizationRepository getOrganizationRepository() {
        return organizationRepository;
    }

    public TaskCategoryRepository getTaskCategoryRepository() {
        return taskCategoryRepository;
    }

    public AuthenticationRepository getAuthenticationRepository() {
        return authenticationRepository;
    }

    // Railway simulation repository accessors
    public EditorRepository getEditorRepository() {
        return editorRepository;
    }

    public MapRepository getMapRepository() {
        return mapRepository;
    }

    /* public ScenarioRepository getScenarioRepository() {
        return scenarioRepository;
    } */
}