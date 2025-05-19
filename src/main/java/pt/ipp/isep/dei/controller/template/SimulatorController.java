package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Cargo;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Simulator;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.SimulatorRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Controller for simulator operations
 */
public class SimulatorController {
    private final SimulatorRepository simulatorRepository;
    private final MapRepository mapRepository;
    private final ScenarioRepository scenarioRepository;
    private final EditorRepository editorRepository;
    
    // Background simulation support
    private Timer simulationTimer;
    private static final int SIMULATION_INTERVAL_MS = 10000; // 10 seconds between cargo generations
    private boolean simulationRunningInBackground = false;
    
    /**
     * Constructor for the simulator controller
     */
    public SimulatorController() {
        Repositories repositories = Repositories.getInstance();
        this.simulatorRepository = repositories.getSimulatorRepository();
        this.mapRepository = repositories.getMapRepository();
        this.scenarioRepository = repositories.getScenarioRepository();
        this.editorRepository = repositories.getEditorRepository();
    }
    
    /**
     * Gets all available maps
     * @return List of available maps
     */
    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }
    
    /**
     * Gets all available scenarios from both ScenarioRepository and EditorRepository
     * @return List of available scenarios
     */
    public List<Scenario> getAvailableScenarios() {
        List<Scenario> scenariosFromScenarioRepo = scenarioRepository.getAllScenarios();
        List<Scenario> scenariosFromEditorRepo = editorRepository.getAllScenarios();
        
        // Combine both lists while avoiding duplicates
        Set<String> scenarioIds = new HashSet<>();
        List<Scenario> allScenarios = new ArrayList<>();
        
        // Add from scenario repository
        for (Scenario scenario : scenariosFromScenarioRepo) {
            if (!scenarioIds.contains(scenario.getNameID())) {
                allScenarios.add(scenario);
                scenarioIds.add(scenario.getNameID());
            }
        }
        
        // Add from editor repository
        for (Scenario scenario : scenariosFromEditorRepo) {
            if (!scenarioIds.contains(scenario.getNameID())) {
                allScenarios.add(scenario);
                scenarioIds.add(scenario.getNameID());
            }
        }
        
        return allScenarios;
    }
    
    /**
     * Starts a new simulation with the given map and scenario
     * @param selectedMap The map to use for simulation
     * @param selectedScenario The scenario to use for simulation
     * @return True if the simulation was started successfully
     */
    public boolean startSimulation(Map selectedMap, Scenario selectedScenario) {
        if (selectedMap == null || selectedScenario == null) {
            return false;
        }
        
        // Make sure the scenario has a reference to the map
        if (selectedScenario.getMap() == null) {
            selectedScenario.setMap(selectedMap);
        }
        
        // Create a new simulator
        Simulator simulator = simulatorRepository.createSimulator(selectedMap, selectedScenario);
        if (simulator == null) {
            return false;
        }
        
        // Start the simulator
        boolean started = simulator.start();
        
        if (started) {
            // Generate initial cargo
            simulator.generateCargo();
            
            // Start background simulation if not already running
            startBackgroundSimulation();
        }
        
        return started;
    }
    
    /**
     * Start the background simulation timer to regularly generate cargo
     */
    private void startBackgroundSimulation() {
        if (simulationTimer != null) {
            simulationTimer.cancel();
        }
        
        simulationTimer = new Timer(true); // Create as daemon thread
        simulationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Simulator simulator = simulatorRepository.getActiveSimulator();
                if (simulator != null && Simulator.STATUS_RUNNING.equals(simulator.getStatus())) {
                    simulator.generateCargo();
                }
            }
        }, SIMULATION_INTERVAL_MS, SIMULATION_INTERVAL_MS);
        
        simulationRunningInBackground = true;
    }
    
    /**
     * Stops the background simulation timer
     */
    private void stopBackgroundSimulation() {
        if (simulationTimer != null) {
            simulationTimer.cancel();
            simulationTimer = null;
        }
        simulationRunningInBackground = false;
    }
    
    /**
     * Checks if simulation is running in background
     * @return True if simulation is running in background
     */
    public boolean isSimulationRunningInBackground() {
        return simulationRunningInBackground;
    }
    
    /**
     * Pauses the current simulation
     * @return True if the simulation was paused successfully
     */
    public boolean pauseSimulation() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return false;
        }
        
        boolean paused = simulator.pause();
        if (paused) {
            stopBackgroundSimulation();
        }
        return paused;
    }
    
    /**
     * Resumes the current simulation
     * @return True if the simulation was resumed successfully
     */
    public boolean resumeSimulation() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return false;
        }
        
        boolean resumed = simulator.resume();
        if (resumed) {
            startBackgroundSimulation();
        }
        return resumed;
    }
    
    /**
     * Stops the current simulation
     * @return The simulation report, or null if no simulation is active
     */
    public String stopSimulation() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return null;
        }
        
        stopBackgroundSimulation();
        return simulator.stop();
    }
    
    /**
     * Restarts the current simulation using the same map and scenario
     * @return True if the simulation was successfully restarted
     */
    public boolean restartSimulation() {
        Simulator currentSimulator = simulatorRepository.getActiveSimulator();
        if (currentSimulator == null) {
            return false;
        }
        
        // Get the current map and scenario
        Map currentMap = currentSimulator.getMap();
        Scenario currentScenario = currentSimulator.getScenario();
        
        // Stop the current simulation
        stopBackgroundSimulation();
        currentSimulator.stop();
        
        // Create a new simulator with the same map and scenario
        Simulator newSimulator = simulatorRepository.createSimulator(currentMap, currentScenario);
        if (newSimulator == null) {
            return false;
        }
        
        // Start the new simulator
        boolean started = newSimulator.start();
        
        if (started) {
            // Generate initial cargo
            newSimulator.generateCargo();
            
            // Start background simulation
            startBackgroundSimulation();
        }
        
        return started;
    }
    
    /**
     * Executes an operation in the simulator
     * @param operationType The type of operation to execute
     * @param parameters The parameters for the operation
     * @return The result of the operation, or null if no simulation is active
     */
    public Object executeOperation(String operationType, java.util.Map<String, Object> parameters) {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return null;
        }
        
        return simulator.executeOperation(operationType, parameters);
    }
    
    /**
     * Gets the details of an operation
     * @param operationType The type of operation to get details for
     * @return The operation details, or null if no simulation is active
     */
    public java.util.Map<String, Object> getOperationDetails(String operationType) {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null || operationType == null) {
            return null;
        }
        
        java.util.Map<String, Object> details = new HashMap<>();
        details.put("type", operationType);
        
        switch (operationType) {
            case "buy_locomotive":
                details.put("description", "Purchase a new locomotive for your railway");
                details.put("required_params", new String[] {"locomotive_id"});
                break;
            case "build_railway_line":
                details.put("description", "Build a new railway line between stations");
                details.put("required_params", new String[] {"start_station", "end_station"});
                break;
            case "assign_train":
                details.put("description", "Assign a train to a route");
                details.put("required_params", new String[] {"train_id", "route_id"});
                break;
            default:
                details.put("description", "Unknown operation");
                details.put("required_params", new String[] {});
        }
        
        return details;
    }
    
    /**
     * Gets the current status of the simulator
     * @return The current status, or null if no simulation is active
     */
    public String getSimulatorStatus() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return null;
        }
        
        return simulator.getStatus();
    }
    
    /**
     * Generates cargo at stations
     * @return True if cargo was generated successfully
     */
    public boolean generateCargo() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return false;
        }
        
        simulator.generateCargo();
        return true;
    }
    
    /**
     * Gets cargo generation details
     * @return Map of station IDs to cargo lists
     */
    public java.util.Map<String, List<Cargo>> getCargoGenerationDetails() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            return new HashMap<>();
        }
        
        return simulator.getCargoGenerationDetails();
    }
} 