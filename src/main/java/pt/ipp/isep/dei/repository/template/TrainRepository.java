package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Train;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pt.ipp.isep.dei.domain.template.Route;

public class TrainRepository {
    private final Map<String, Train> trains;
    
    public TrainRepository() {
        this.trains = new HashMap<>();
    }
    
    public boolean save(Train train) {
        if (train == null || train.getNameID() == null) {
            return false;
        }
        
        trains.put(train.getNameID(), train);
        return true;
    }
    
    public Train getTrain(String trainId) {
        return trains.get(trainId);
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
    
    public List<Train> getAllTrains() {
        return new ArrayList<>(trains.values());
    }
    
    public boolean exists(String trainId) {
        return trains.containsKey(trainId);
    }

    public boolean delete(String trainId) {
        if (trainId == null) {
            return false;
        }
        return trains.remove(trainId) != null;
    }

    public void initialize() {
        // Add some default trains if needed
        // This can be implemented later when we have train creation functionality
    }
    
    public List<Train> findByRoute(Route route) {
        if (route == null) {
            return new ArrayList<>();
        }
        
        List<Train> trainsOnRoute = new ArrayList<>();
        for (Train train : trains.values()) {
            if (train.getAssignedRoute() != null && train.getAssignedRoute().equals(route)) {
                trainsOnRoute.add(train);
            }
        }
        return trainsOnRoute;
    }

    public void remove(Train train) {
        if (train != null) {
            trains.remove(train.getNameID());
        }
    }

    public boolean contains(Train train) {
        if (train == null || train.getNameID() == null) {
            return false;
        }
        return trains.containsKey(train.getNameID());
    }
} 