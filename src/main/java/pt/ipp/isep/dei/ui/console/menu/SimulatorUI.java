package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.SimulatorController;
import pt.ipp.isep.dei.domain.template.Cargo;
import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Simulator;
import pt.ipp.isep.dei.ui.console.utils.Utils;

import java.util.HashMap;
import java.util.List;

/**
 * User interface for the simulator - acts as a control panel for the background simulation
 */
public class SimulatorUI implements Runnable {
    
    private final SimulatorController controller;
    
    public SimulatorUI() {
        this.controller = new SimulatorController();
    }
    
    @Override
    public void run() {
        System.out.println("\n=== Railway Simulator Control Panel ===\n");
        
        // Check if simulator is already running
        String status = controller.getSimulatorStatus();
        boolean simulationActive = status != null;
        
        // If simulation is not active, start a new one
        if (!simulationActive) {
            startNewSimulation();
        } else {
            // Show current simulation status
            System.out.println("Simulation is currently " + status);
            showSimulationControls();
        }
    }
    
    /**
     * Start a new simulation by selecting map and scenario
     */
    private void startNewSimulation() {
        // Check if map and scenario are already selected
        Map selectedMap = ApplicationSession.getInstance().getCurrentMap();
        Scenario selectedScenario = ApplicationSession.getInstance().getCurrentScenario();
        
        // If map is not selected, prompt user to select one
        if (selectedMap == null) {
            // Get available maps
            List<Map> availableMaps = controller.getAvailableMaps();
            if (availableMaps.isEmpty()) {
                System.out.println("No maps available. Please create a map first.");
                return;
            }
            
            // Let user select a map
            System.out.println("Select a map for simulation:");
            selectedMap = (Map) Utils.showAndSelectOne(availableMaps, "Available Maps:");
            
            if (selectedMap == null) {
                System.out.println("No map selected. Exiting simulator.");
                return;
            }
        } else {
            System.out.println("Using currently selected map: " + selectedMap.getNameID());
        }
        
        // If scenario is not selected or doesn't match the map, prompt user to select one
        if (selectedScenario == null || selectedScenario.getMap() == null || 
                !selectedScenario.getMap().getNameID().equals(selectedMap.getNameID())) {
            
            // Get available scenarios
            List<Scenario> availableScenarios = controller.getAvailableScenarios();
            if (availableScenarios.isEmpty()) {
                System.out.println("No scenarios available. Please create a scenario first.");
                return;
            }
            
            // Filter scenarios - use map's scenario IDs
            List<Scenario> mapScenarios = new java.util.ArrayList<>();
            for (Scenario scenario : availableScenarios) {
                String scenarioId = scenario.getNameID();
                if (selectedMap.getScenarios().contains(scenarioId)) {
                    mapScenarios.add(scenario);
                    // Ensure the map is set properly on the scenario
                    if (scenario.getMap() == null) {
                        scenario.setMap(selectedMap);
                    }
                }
            }
            
            if (mapScenarios.isEmpty()) {
                System.out.println("\nNo scenarios available for the selected map. Please create a scenario first.");
                System.out.println("You can create scenarios through the Editor menu.");
                return;
            }
            
            // Let user select a scenario
            System.out.println("\nSelect a scenario for simulation:");
            selectedScenario = (Scenario) Utils.showAndSelectOne(mapScenarios, "Available Scenarios:");
            
            if (selectedScenario == null) {
                System.out.println("No scenario selected. Exiting simulator.");
                return;
            }
        } else {
            System.out.println("Using currently selected scenario: " + selectedScenario.getDisplayName());
        }
        
        // Show details and confirm
        System.out.println("\nMap: " + selectedMap.getNameID());
        System.out.println("Scenario: " + selectedScenario.getDisplayName());
        
        if (!Utils.confirm("Start simulation with these settings? (y/n)")) {
            System.out.println("Simulation cancelled.");
            return;
        }
        
        // Start simulation
        boolean simulationStarted = controller.startSimulation(selectedMap, selectedScenario);
        if (!simulationStarted) {
            System.out.println("Failed to start simulation. Please try again.");
            return;
        }
        
        System.out.println("\nSimulation started successfully!\n");
        System.out.println("The simulation will run in the background while you continue playing.");
        System.out.println("You can return to this control panel anytime to check status or stop the simulation.");
        
        // Show simulation controls
        showSimulationControls();
    }
    
    /**
     * Shows the simulation control options
     */
    private void showSimulationControls() {
        String status = controller.getSimulatorStatus();
        if (status == null) {
            System.out.println("No active simulation found.");
            return;
        }
        
        boolean exitControls = false;
        while (!exitControls) {
            System.out.println("\n=== Simulation Controls (Status: " + status + ") ===");
            System.out.println("1. View Cargo at Stations");
            
            // Toggle pause/resume option based on current status
            if (Simulator.STATUS_RUNNING.equals(status)) {
                System.out.println("2. Pause Simulation");
            } else if (Simulator.STATUS_PAUSED.equals(status)) {
                System.out.println("2. Resume Simulation");
            }
            
            System.out.println("3. Generate Cargo Now");
            System.out.println("4. Stop Simulation and View Report");
            System.out.println("5. Restart Simulation");
            System.out.println("0. Return to Main Menu (simulation continues in background)");
            
            int option = Utils.readIntegerFromConsole("Select an option: ");
            
            switch (option) {
                case 1: // View Cargo
                    displayCargoGeneration();
                    break;
                case 2: // Pause/Resume
                    togglePauseResume();
                    status = controller.getSimulatorStatus(); // Update status
                    break;
                case 3: // Generate Cargo
                    controller.generateCargo();
                    System.out.println("Cargo generated!");
                    break;
                case 4: // Stop Simulation
                    if (stopSimulation()) {
                        exitControls = true; // Exit after stopping
                    }
                    break;
                case 5: // Restart Simulation
                    if (restartSimulation()) {
                        status = controller.getSimulatorStatus(); // Update status
                    }
                    break;
                case 0: // Return to Main Menu
                    System.out.println("Returning to main menu. Simulation continues running in background.");
                    exitControls = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }
    
    /**
     * Displays the cargo generation at stations
     */
    private void displayCargoGeneration() {
        System.out.println("\n=== Cargo Generated at Stations ===");
        
        java.util.Map<String, List<Cargo>> stationCargo = controller.getCargoGenerationDetails();
        
        if (stationCargo.isEmpty()) {
            System.out.println("No cargo generated yet.");
            return;
        }
        
        for (java.util.Map.Entry<String, List<Cargo>> entry : stationCargo.entrySet()) {
            String stationId = entry.getKey();
            List<Cargo> cargoList = entry.getValue();
            
            System.out.println("\nStation: " + stationId);
            
            if (cargoList.isEmpty()) {
                System.out.println("  No cargo available");
            } else {
                System.out.println("  Available Cargo:");
                for (Cargo cargo : cargoList) {
                    System.out.println("  - " + cargo.toString());
                }
            }
        }
        
        System.out.println("\nPress Enter to continue...");
        Utils.readLineFromConsole("");
    }
    
    /**
     * Toggles between pause and resume
     */
    private void togglePauseResume() {
        String status = controller.getSimulatorStatus();
        
        if (Simulator.STATUS_RUNNING.equals(status)) {
            boolean paused = controller.pauseSimulation();
            if (paused) {
                System.out.println("Simulation paused.");
            } else {
                System.out.println("Failed to pause simulation.");
            }
        } else if (Simulator.STATUS_PAUSED.equals(status)) {
            boolean resumed = controller.resumeSimulation();
            if (resumed) {
                System.out.println("Simulation resumed.");
            } else {
                System.out.println("Failed to resume simulation.");
            }
        } else {
            System.out.println("Cannot pause/resume - simulator is not running or already paused.");
        }
    }
    
    /**
     * Stops the simulation and displays the report
     * @return True if simulation was successfully stopped
     */
    private boolean stopSimulation() {
        if (!Utils.confirm("Are you sure you want to stop the simulation? This cannot be undone. (y/n)")) {
            return false;
        }
        
        String report = controller.stopSimulation();
        if (report == null) {
            System.out.println("Failed to stop simulation.");
            return false;
        }
        
        // Display the report
        System.out.println("\n=== Simulation Report ===\n");
        System.out.println(report);
        
        System.out.println("\nPress Enter to continue...");
        Utils.readLineFromConsole("");
        
        return true;
    }
    
    /**
     * Restarts the simulation
     * @return True if simulation was successfully restarted
     */
    private boolean restartSimulation() {
        if (!Utils.confirm("Are you sure you want to restart the simulation? This cannot be undone. (y/n)")) {
            return false;
        }
        
        boolean restarted = controller.restartSimulation();
        if (restarted) {
            System.out.println("Simulation restarted successfully!");
            return true;
        } else {
            System.out.println("Failed to restart simulation.");
            return false;
        }
    }
} 