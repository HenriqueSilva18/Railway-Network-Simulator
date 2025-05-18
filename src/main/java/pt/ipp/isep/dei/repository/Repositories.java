package pt.ipp.isep.dei.repository;

public class Repositories {
    private static final Repositories instance = new Repositories();
    
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final MapRepository mapRepository;

    private Repositories() {
        this.routeRepository = new RouteRepository();
        this.trainRepository = new TrainRepository();
        this.mapRepository = new MapRepository();
    }

    public static Repositories getInstance() {
        return instance;
    }

    public RouteRepository getRouteRepository() {
        return routeRepository;
    }

    public TrainRepository getTrainRepository() {
        return trainRepository;
    }

    public MapRepository getMapRepository() {
        return mapRepository;
    }
} 