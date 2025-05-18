package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Route;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class RouteRepository {
    private final java.util.Map<String, Route> routes;

    public RouteRepository() {
        this.routes = new ConcurrentHashMap<>();
    }

    public boolean save(Route route) {
        if (route == null || route.getNameID() == null) {
            return false;
        }
        routes.put(route.getNameID(), route);
        return true;
    }

    public Route getById(String id) {
        return routes.get(id);
    }

    public List<Route> getAll() {
        return new ArrayList<>(routes.values());
    }

    public boolean exists(String routeId) {
        return routes.containsKey(routeId);
    }

    public boolean delete(String routeId) {
        return routes.remove(routeId) != null;
    }
} 