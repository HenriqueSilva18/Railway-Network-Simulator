package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.ArrayList;
import java.util.List;

public class CreateRouteController {
    private final RailwayLineRepository railwayLineRepository;
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;

    public CreateRouteController() {
        Repositories repositories = Repositories.getInstance();
        this.railwayLineRepository = repositories.getRailwayLineRepository();
        this.stationRepository = repositories.getStationRepository();
        this.routeRepository = repositories.getRouteRepository();
    }

    public List<Station> getAvailableStations() {
        // Get current map
        Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return new ArrayList<>();
        }

        // Get stations from the map
        List<Station> stations = currentMap.getStations();
        
        // Store stations in the repository for later retrieval
        for (Station station : stations) {
            stationRepository.add(station);
        }
        
        return stations;
    }

    public List<Station> getConnectedStations(Station station) {
        if (station == null) {
            return new ArrayList<>();
        }

        List<Station> connectedStations = new ArrayList<>();
        List<RailwayLine> railwayLines = railwayLineRepository.getAll();

        for (RailwayLine line : railwayLines) {
            if (line.getStartStation().equals(station)) {
                connectedStations.add(line.getEndStation());
            } else if (line.getEndStation().equals(station)) {
                connectedStations.add(line.getStartStation());
            }
        }

        return connectedStations;
    }

    public Route createRoute(String routeName, List<Station> stations) {
        if (routeName == null || stations == null || stations.size() < 2) {
            return null;
        }

        // Create new route
        Route route = new Route(routeName, stations);

        // Add railway lines to the route
        List<RailwayLine> allRailwayLines = railwayLineRepository.getAll();
        for (int i = 0; i < stations.size() - 1; i++) {
            Station current = stations.get(i);
            Station next = stations.get(i + 1);
            
            for (RailwayLine line : allRailwayLines) {
                if ((line.getStartStation().equals(current) && line.getEndStation().equals(next)) || 
                    (line.getStartStation().equals(next) && line.getEndStation().equals(current))) {
                    route.addRailwayLine(line);
                    break;
                }
            }
        }

        // Validate that all stations are connected
        if (!route.validateStations()) {
            return null;
        }

        // Save route
        if (!routeRepository.save(route)) {
            return null;
        }

        return route;
    }
} 