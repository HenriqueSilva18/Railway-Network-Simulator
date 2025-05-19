package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AssignTrainController {
    private final RouteRepository routeRepository;
    private final TrainRepository trainRepository;
    private final ApplicationSession applicationSession;
    private final PlayerRepository playerRepository;
    
    public AssignTrainController() {
        Repositories repositories = Repositories.getInstance();
        this.routeRepository = repositories.getRouteRepository();
        this.trainRepository = repositories.getTrainRepository();
        this.applicationSession = ApplicationSession.getInstance();
        this.playerRepository = repositories.getPlayerRepository();
    }
    
    public List<Route> getAvailableRoutes() {
        // Return all routes from the repository
        return routeRepository.getAll();
    }
    
    public RouteDetails getRouteDetails(Route selectedRoute) {
        if (selectedRoute == null) {
            return null;
        }
        return selectedRoute.getDetails();
    }
    
    public List<Train> getAvailableTrains() {
        // First, ensure trains exist for all locomotives
        ensureTrainsForLocomotives();
        
        return trainRepository.getAvailableTrains();
    }
    
    // Create trains for any locomotives that don't have associated trains
    public void ensureTrainsForLocomotives() {
        // Get the current player
        Player player = getPlayerFromSession();
        if (player == null) {
            return;
        }
        
        // Get the player's locomotives
        List<Locomotive> locomotives = player.getOwnedLocomotives();
        if (locomotives.isEmpty()) {
            return;
        }
        
        // For each locomotive, check if there's already a train using it
        for (Locomotive locomotive : locomotives) {
            boolean trainExists = false;
            
            // Check all trains to see if this locomotive is already used
            for (Train train : trainRepository.getAllTrains()) {
                if (train.getLocomotive().equals(locomotive)) {
                    trainExists = true;
                    break;
                }
            }
            
            // If no train exists for this locomotive, create one
            if (!trainExists) {
                String trainName = "Train_" + locomotive.getNameID() + "_" + System.currentTimeMillis() % 1000;
                Train train = new Train(trainName, locomotive);
                trainRepository.save(train);
            }
        }
    }
    
    private Player getPlayerFromSession() {
        UserSession currentSession = applicationSession.getCurrentSession();
        if (currentSession == null) {
            return null;
        }

        String username = currentSession.getUserEmail();
        return playerRepository.getPlayerByEmail(username);
    }
    
    public TrainDetails getTrainDetails(Train selectedTrain) {
        if (selectedTrain == null) {
            return null;
        }
        return selectedTrain.getDetails();
    }
    
    public boolean assignTrainToRoute(Route selectedRoute, Train selectedTrain) {
        if (selectedRoute == null || selectedTrain == null) {
            return false;
        }
        
        // Verify that the train is not already assigned to a route
        if (selectedTrain.getAssignedRoute() != null) {
            return false;
        }
        
        // Assign the train to the route
        boolean success = selectedTrain.assignToRoute(selectedRoute);
        
        if (success) {
            // Add the train to the route's list of trains
            selectedRoute.addTrain(selectedTrain);
            
            // Save changes
            routeRepository.save(selectedRoute);
            trainRepository.save(selectedTrain);
            
            // Set current train and route in the application session
            applicationSession.setCurrentTrain(selectedTrain);
            applicationSession.setCurrentRoute(selectedRoute);
        }
        
        return success;
    }
    
    public Map<Station, List<Cargo>> getCargoesToPickUp(Route route) {
        if (route == null) {
            return null;
        }
        return route.getStationCargo();
    }
} 