package pt.ipp.isep.dei.ui.console.menu;

import pt.ipp.isep.dei.controller.template.SimulatorController;
import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;
import pt.ipp.isep.dei.ui.console.utils.Utils;
import pt.ipp.isep.dei.controller.template.ApplicationSession;

import java.util.List;

/**
 * User interface for the simulator - acts as a control panel for the background simulation
 */
public class SimulatorUI implements Runnable {
    private final SimulatorController controller;
    private final SimulatorRepository simulatorRepository;
    
    public SimulatorUI() {
        this.controller = new SimulatorController();
        this.simulatorRepository = Repositories.getInstance().getSimulatorRepository();
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
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) {
            System.out.println("No active simulation.");
            return;
        }

        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            System.out.println("No active player.");
            return;
        }

        while (true) {
            System.out.println("\n=== Simulation Controls (Status: " + simulator.getStatus() + ") ===");
            System.out.printf("Current Budget: %.2f\n", currentPlayer.getCurrentBudget());
            System.out.printf("Current Date: %s\n", simulator.getCurrentSimulatedDate());
            System.out.println("1. View Cargo at Stations");
            System.out.println("2. Pause Simulation");
            System.out.println("3. Generate Cargo Now");
            System.out.println("4. Stop Simulation and View Report");
            System.out.println("5. Restart Simulation");
            System.out.println("0. Return to Main Menu (simulation continues in background)");

            int choice = Utils.readIntegerFromConsole("Select an option: ");
            switch (choice) {
                case 1:
                    displayCargoGeneration();
                    break;
                case 2:
                    if (simulator.getStatus().equals(Simulator.STATUS_RUNNING)) {
                        simulator.pause();
                        System.out.println("Simulation paused.");
                    } else {
                        System.out.println("Simulation is not running.");
                    }
                    break;
                case 3:
                    if (controller.generateCargo()) {
                        System.out.println("Cargo generated successfully.");
                    }
                    break;
                case 4:
                    if (Utils.confirm("Are you sure you want to stop the simulation? (y/n)")) {
                        simulator.stop();
                        displaySimulationReport();
                        return;
                    }
                    break;
                case 5:
                    if (Utils.confirm("Are you sure you want to restart the simulation? (y/n)")) {
                        controller.restartSimulation();
                        System.out.println("Simulation restarted.");
                    }
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
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
     * Displays the simulation report
     */
    private void displaySimulationReport() {
        String report = controller.stopSimulation();
        if (report == null) {
            System.out.println("Failed to stop simulation.");
            return;
        }
        
        System.out.println("\n=== Simulation Report ===");
        System.out.println(report);
        
        System.out.println("\nPress Enter to continue...");
        Utils.readLineFromConsole("");
    }
} 