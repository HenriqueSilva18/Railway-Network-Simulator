package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.template.Route;
import pt.ipp.isep.dei.domain.template.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RouteRepository {
    private final Map<String, Route> routes;

    public RouteRepository() {
        this.routes = new ConcurrentHashMap<>();
    }

    public List<Route> getAvailableRoutes() {
        List<Route> availableRoutes = new ArrayList<>();
        for (Route route : routes.values()) {
            if (route.validateStations()) {
                availableRoutes.add(route);
            }
        }
        return availableRoutes;
    }

    public Route getRoute(String routeId) {
        return routes.get(routeId);
    }

    public boolean save(Route route) {
        if (route == null || route.getNameID() == null) return false;
        routes.put(route.getNameID(), route);
        return true;
    }

    public boolean delete(String routeId) {
        if (routeId == null) return false;
        return routes.remove(routeId) != null;
    }

    public List<Route> findByStation(Station station) {
        List<Route> routesWithStation = new ArrayList<>();
        for (Route route : routes.values()) {
            if (route.getStationSequence().contains(station)) {
                routesWithStation.add(route);
            }
        }
        return routesWithStation;
    }
} 