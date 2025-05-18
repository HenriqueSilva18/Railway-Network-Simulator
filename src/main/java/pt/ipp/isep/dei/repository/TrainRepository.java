package pt.ipp.isep.dei.repository;

import pt.ipp.isep.dei.domain.template.Train;
import pt.ipp.isep.dei.domain.template.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TrainRepository {
    private final Map<String, Train> trains;

    public TrainRepository() {
        this.trains = new ConcurrentHashMap<>();
    }

    public List<Train> getAvailableTrains() {
        List<Train> availableTrains = new ArrayList<>();
        for (Train train : trains.values()) {
            if (!train.isAssignedToRoute()) {
                availableTrains.add(train);
            }
        }
        return availableTrains;
    }

    public Train getTrain(String trainId) {
        return trains.get(trainId);
    }

    public boolean save(Train train) {
        if (train == null || train.getNameID() == null) return false;
        trains.put(train.getNameID(), train);
        return true;
    }

    public boolean delete(String trainId) {
        if (trainId == null) return false;
        return trains.remove(trainId) != null;
    }

    public List<Train> findByRoute(Route route) {
        List<Train> trainsOnRoute = new ArrayList<>();
        for (Train train : trains.values()) {
            if (train.getCurrentRoute() != null && train.getCurrentRoute().equals(route)) {
                trainsOnRoute.add(train);
            }
        }
        return trainsOnRoute;
    }
} 