package pt.ipp.isep.dei.repository.template;

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
    private final MapRepository mapRepository;
    private final IndustryRepository industryRepository;
    private final EditorRepository editorRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final CargoRepository cargoRepository;
    private final StationTypeRepository stationTypeRepository;
    private final PlayerRepository playerRepository;
    



    /**
     * The Singleton's constructor should always be private to prevent direct construction calls with the new operator.
     */
    private Repositories() {
        // Initialize template repositories
        this.organizationRepository = new OrganizationRepository();
        this.taskCategoryRepository = new TaskCategoryRepository();
        this.authenticationRepository = new AuthenticationRepository();
        this.mapRepository = new MapRepository();
        this.industryRepository = new IndustryRepository();
        this.editorRepository = new EditorRepository();
        this.locomotiveRepository = new LocomotiveRepository();
        this.cargoRepository = new CargoRepository();
        this.stationTypeRepository = new StationTypeRepository();
        this.playerRepository = new PlayerRepository();


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

    public MapRepository getMapRepository() {
        return mapRepository;
    }

    public IndustryRepository getIndustryRepository() {
        return industryRepository;
    }

    public EditorRepository getEditorRepository() {
        return editorRepository;
    }

    public LocomotiveRepository getLocomotiveRepository() {
        return locomotiveRepository;
    }

    public CargoRepository getCargoRepository() {
        return cargoRepository;
    }

    public StationTypeRepository getStationTypeRepository() {
        return stationTypeRepository;
    }

    public PlayerRepository getPlayerRepository() {
        return playerRepository;
    }
    


}