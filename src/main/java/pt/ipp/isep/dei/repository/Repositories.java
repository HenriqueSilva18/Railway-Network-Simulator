package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.repository.template.AuthenticationRepository;

public class Repositories {
    private static final Repositories instance = new Repositories();
    
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final MapRepository mapRepository;
    private final AuthenticationRepository authenticationRepository;

    private Repositories() {
        this.routeRepository = new RouteRepository();
        this.trainRepository = new TrainRepository();
        this.mapRepository = new MapRepository();
        this.authenticationRepository = new AuthenticationRepository();
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

    public AuthenticationRepository getAuthenticationRepository(){
        return authenticationRepository;
    }
} 