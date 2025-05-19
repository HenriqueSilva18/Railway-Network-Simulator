package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Simulator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing simulators
 */
public class SimulatorRepository {
    private final java.util.Map<String, Simulator> simulators;
    private Simulator activeSimulator;

    public SimulatorRepository() {
        this.simulators = new HashMap<>();
        this.activeSimulator = null;
    }

    /**
     * Creates a new simulator with the given map and scenario
     * @param map The map to use for the simulator
     * @param scenario The scenario to use for the simulator
     * @return The created simulator
     */
    public Simulator createSimulator(Map map, Scenario scenario) {
        if (map == null || scenario == null) {
            return null;
        }
        
        // Create a unique ID for the simulator
        String simulatorId = "simulator_" + map.getNameID() + "_" + scenario.getNameID() + "_" + System.currentTimeMillis();
        
        // Create the simulator
        Simulator simulator = new Simulator(map, scenario);
        
        // Store the simulator
        simulators.put(simulatorId, simulator);
        
        // Set as active simulator
        activeSimulator = simulator;
        
        return simulator;
    }

    /**
     * Gets the active simulator
     * @return The active simulator, or null if no simulator is active
     */
    public Simulator getActiveSimulator() {
        return activeSimulator;
    }

    /**
     * Sets the active simulator by ID
     * @param simulatorId The ID of the simulator to set as active
     * @return True if the simulator was set as active, false otherwise
     */
    public boolean setActiveSimulator(String simulatorId) {
        if (simulatorId != null && simulators.containsKey(simulatorId)) {
            activeSimulator = simulators.get(simulatorId);
            return true;
        }
        return false;
    }

    /**
     * Gets all simulators
     * @return A list of all simulators
     */
    public List<Simulator> getAllSimulators() {
        return new ArrayList<>(simulators.values());
    }

    /**
     * Gets a simulator by ID
     * @param simulatorId The ID of the simulator to get
     * @return The simulator, or null if not found
     */
    public Simulator getSimulator(String simulatorId) {
        return simulators.get(simulatorId);
    }

    /**
     * Removes a simulator by ID
     * @param simulatorId The ID of the simulator to remove
     * @return True if the simulator was removed, false otherwise
     */
    public boolean removeSimulator(String simulatorId) {
        if (simulatorId == null || !simulators.containsKey(simulatorId)) {
            return false;
        }
        
        // If removing the active simulator, set active to null
        if (activeSimulator != null && activeSimulator.equals(simulators.get(simulatorId))) {
            activeSimulator = null;
        }
        
        simulators.remove(simulatorId);
        return true;
    }

    /**
     * Clears all simulators
     */
    public void clear() {
        simulators.clear();
        activeSimulator = null;
    }
} 