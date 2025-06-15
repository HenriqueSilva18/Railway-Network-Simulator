package pt.ipp.isep.dei.domain.template;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.repository.template.Repositories;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Player {
    private final String username;
    private final double initialBudget;
    private final Map<String, Double> scenarioBudgets;
    private final List<Locomotive> ownedLocomotives;
    private final List<Station> builtStations;

    public Player(String username, double initialBudget) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        if (initialBudget < 0) {
            throw new IllegalArgumentException("Initial budget cannot be negative");
        }
        
        this.username = username;
        this.initialBudget = initialBudget;
        this.scenarioBudgets = new HashMap<>();
        this.ownedLocomotives = new ArrayList<>();
        this.builtStations = new ArrayList<>();
    }

    public String getUsername() {
        return username;
    }

    public double getInitialBudget() {
        return initialBudget;
    }

    public double getCurrentBudget() {
        // Get current map from ApplicationSession
        pt.ipp.isep.dei.domain.template.Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return initialBudget; // Return initial budget if no scenario is selected
        }
        
        // Get the current scenario from ApplicationSession
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        if (currentScenario == null) {
            return initialBudget;
        }
        
        String scenarioId = currentScenario.getNameID();

        // If this scenario hasn't been initialized yet, initialize it
        if (!scenarioBudgets.containsKey(scenarioId)) {
            initializeScenarioBudget(scenarioId);
        }

        return scenarioBudgets.get(scenarioId);
    }

    public List<Locomotive> getOwnedLocomotives() {
        return new ArrayList<>(ownedLocomotives);
    }

    public List<Station> getBuiltStations() {
        return new ArrayList<>(builtStations);
    }

    public boolean deductFromBudget(double amount) {
        if (amount < 0) {
            return false;
        }
        
        // Get current map from ApplicationSession
        pt.ipp.isep.dei.domain.template.Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return false;
        }
        
        // Get the current scenario from ApplicationSession
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        if (currentScenario == null) {
            return false;
        }
        
        String scenarioId = currentScenario.getNameID();
        
        // If this scenario hasn't been initialized yet, initialize it
        if (!scenarioBudgets.containsKey(scenarioId)) {
            initializeScenarioBudget(scenarioId);
        }
        
        double currentBudget = scenarioBudgets.get(scenarioId);
        
        if (amount > currentBudget) {
            return false;
        }
        
        // Update the budget for this scenario
        double newBudget = currentBudget - amount;
        scenarioBudgets.put(scenarioId, newBudget);
        return true;
    }

    public boolean addToBudget(double amount) {
        if (amount < 0) {
            return false;
        }
        
        // Get current map from ApplicationSession
        pt.ipp.isep.dei.domain.template.Map currentMap = ApplicationSession.getInstance().getCurrentMap();
        if (currentMap == null) {
            return false;
        }
        
        // Get the current scenario from ApplicationSession
        Scenario currentScenario = ApplicationSession.getInstance().getCurrentScenario();
        if (currentScenario == null) {
            return false;
        }
        
        String scenarioId = currentScenario.getNameID();
        
        // If this scenario hasn't been initialized yet, initialize it
        if (!scenarioBudgets.containsKey(scenarioId)) {
            initializeScenarioBudget(scenarioId);
        }
        
        double currentBudget = scenarioBudgets.get(scenarioId);
        double newBudget = currentBudget + amount;
        scenarioBudgets.put(scenarioId, newBudget);
        return true;
    }

    public boolean addStation(Station station) {
        if (station == null) {
            return false;
        }
        return builtStations.add(station);
    }

    public boolean addLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
        }
        return ownedLocomotives.add(locomotive);
    }

    public boolean removeLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
        }
        return ownedLocomotives.remove(locomotive);
    }

    // Method to initialize budget for a new scenario
    public void initializeScenarioBudget(String scenarioId) {
        scenarioBudgets.put(scenarioId, initialBudget);
    }
} 