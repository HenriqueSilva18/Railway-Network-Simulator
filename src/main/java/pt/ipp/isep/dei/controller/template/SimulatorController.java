package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;
import javafx.scene.control.Alert;
import pt.ipp.isep.dei.ui.gui.utils.SimulationNotificationHelper;

import java.util.*;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

/**
 * Controller for simulator operations
 */
public class SimulatorController {
    private final SimulatorRepository simulatorRepository;
    private final MapRepository mapRepository;
    private final ScenarioRepository scenarioRepository;
    private final EditorRepository editorRepository;
    private final TrainRepository trainRepository;
    private final StationRepository stationRepository;
    private final RouteRepository routeRepository;
    private final PlayerRepository playerRepository;
    
    // Background simulation support
    private Timer simulationTimer;
    private static final int SIMULATION_INTERVAL_MS = 10000; // 10 seconds between cargo generations
    private boolean simulationRunningInBackground = false;
    
    // Train movement constants
    private static final double SINGLE_TRACK_SPEED_FACTOR = 0.8; // 80% of max speed on single track
    private static final double CARRIAGE_SPEED_DEGRADATION = 0.05; // 5% speed reduction per carriage
    private static final double MAX_CARRIAGE_DEGRADATION = 0.30; // Maximum 30% speed reduction
    
    private final Set<Integer> shownBuildingNotifications = new HashSet<>(); // Track years where notifications were shown
    
    private static final String SAVED_GAMES_DIR = "saved_games";
    
    /**
     * Constructor for the simulator controller
     */
    public SimulatorController() {
        Repositories repositories = Repositories.getInstance();
        this.simulatorRepository = repositories.getSimulatorRepository();
        this.mapRepository = repositories.getMapRepository();
        this.scenarioRepository = repositories.getScenarioRepository();
        this.editorRepository = repositories.getEditorRepository();
        this.trainRepository = repositories.getTrainRepository();
        this.stationRepository = repositories.getStationRepository();
        this.routeRepository = repositories.getRouteRepository();
        this.playerRepository = repositories.getPlayerRepository();
        createSavedGamesDirectory();
    }

    private void createSavedGamesDirectory() {
        File directory = new File(SAVED_GAMES_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
    /**
     * Gets all available maps
     * @return List of available maps
     */
    public List<pt.ipp.isep.dei.domain.template.Map> getAvailableMaps() {
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
    public boolean startSimulation(pt.ipp.isep.dei.domain.template.Map selectedMap, Scenario selectedScenario) {
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

        simulatorRepository.setActiveSimulator(simulator.getStatus());
        
        // Start the simulator
        boolean started = simulator.start();
        
        if (started) {
            // Generate initial cargo
            simulator.generateCargo();
            
            // Start background simulation if not already running
            startBackgroundSimulation();
            
            // Check for locomotive availability
            checkLocomotiveAvailability();
        }
        
        return started;
    }
    
    /**
     * Start the background simulation timer to regularly generate cargo and update trains
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
                    // Generate cargo
                    simulator.generateCargo();
                    
                    // Update train positions and arrivals
                    updateTrainPositions();
                    
                    // Check for locomotive availability
                    checkLocomotiveAvailability();
                    
                    // Check for building availability
                    checkBuildingAvailability();
                    
                    // Update yearly demand if needed
                    updateYearlyDemand();
                    simulatorRepository.setActiveSimulator(simulator.getStatus());
                }
            }
        }, SIMULATION_INTERVAL_MS, SIMULATION_INTERVAL_MS);

        simulationRunningInBackground = true;
    }
    
    /**
     * Updates train positions and handles arrivals
     */
    private void updateTrainPositions() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) return;
        
        RailwayLineRepository railwayLineRepository = pt.ipp.isep.dei.repository.template.Repositories.getInstance().getRailwayLineRepository();
        RouteRepository routeRepository = pt.ipp.isep.dei.repository.template.Repositories.getInstance().getRouteRepository();
        List<RailwayLine> railwayLines = railwayLineRepository.getAll();
        List<Route> routes = routeRepository.getAll();
        
        for (Route route : routes) {
            for (Train train : route.getAssignedTrains()) {
                if (train.isInTransit() && train.getCurrentLine() != null) {
                    double baseSpeed = train.getLocomotive().getTopSpeed();
                    RailwayLine line = train.getCurrentLine();
                    double speedFactor = line.isDoubleTrack() ? 1.0 : SINGLE_TRACK_SPEED_FACTOR;
                    double carriageFactor = 1.0 - Math.min(
                        train.getCarriages().size() * CARRIAGE_SPEED_DEGRADATION,
                        MAX_CARRIAGE_DEGRADATION
                    );
                    double actualSpeed = baseSpeed * speedFactor * carriageFactor;
                    train.updatePosition(actualSpeed);
                    if (train.hasArrived()) {
                        handleTrainArrival(train);
                    }
                }
            }
        }
    }
    
    /**
     * Handles train arrival and calculates revenue
     */
    private void handleTrainArrival(Train train) {
        Station arrivalStation = train.getCurrentStation();
        if (arrivalStation == null) return;
        
        // Calculate revenue based on cargo, distance, and station buildings
        double baseRevenue = calculateBaseRevenue(train);
        double buildingMultiplier = calculateBuildingMultiplier(arrivalStation);
        double totalRevenue = baseRevenue * buildingMultiplier;
        
        // Update player's budget
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            currentPlayer.addToBudget(totalRevenue);
        }
        
        // Update station demand
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator != null) {
            int currentYear = simulator.getElapsedSimulatedDays() / 365;
            arrivalStation.updateDemand(currentYear);
        }
    }
    
    /**
     * Calculates base revenue for a train's cargo
     */
    private double calculateBaseRevenue(Train train) {
        double totalRevenue = 0;
        double distance = train.getCurrentLine().getLength();
        
        for (Cargo cargo : train.getCargo()) {
            // Base revenue depends on cargo type, amount, and distance
            double cargoValue = cargo.getAmount() * cargo.getBaseValue();
            totalRevenue += cargoValue * (1 + (distance / 1000)); // Distance bonus
        }
        
        return totalRevenue;
    }
    
    /**
     * Calculates revenue multiplier based on station buildings
     */
    private double calculateBuildingMultiplier(Station station) {
        double multiplier = 1.0;
        
        for (Building building : station.getBuildings()) {
            multiplier += building.getRevenueMultiplier();
        }
        
        return multiplier;
    }
    
    /**
     * Checks for locomotive availability based on service start year
     */
    private void checkLocomotiveAvailability() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) return;
        
        int currentYear = getCurrentYear();
        Scenario currentScenario = scenarioRepository.getCurrentScenario();
        if (currentScenario == null) return;
        
        // Get all locomotives from scenario
        List<Locomotive> allLocomotives = currentScenario.getAvailableLocomotives(simulator.getCurrentSimulatedDate());
        
        // Track which locomotives were newly available this year
        List<Locomotive> newlyAvailableLocomotives = new ArrayList<>();
        
        // Check each locomotive for availability
        for (Locomotive locomotive : allLocomotives) {
            if (locomotive.getAvailabilityYear() == currentYear && !locomotive.isAvailable()) {
                locomotive.setAvailable(true);
                newlyAvailableLocomotives.add(locomotive);
            }
        }
        
        // Show notification if any locomotives became available
        if (!newlyAvailableLocomotives.isEmpty()) {
            for (Locomotive locomotive : newlyAvailableLocomotives) {
                SimulationNotificationHelper.showLocomotiveAvailabilityNotification(currentYear, locomotive);
            }
        }
    }
    
    /**
     * Updates yearly demand for all stations
     */
    private void updateYearlyDemand() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) return;
        
        // Only update demand at the start of each year
        int currentYear = simulator.getElapsedSimulatedDays() / 365;
        if (simulator.getElapsedSimulatedDays() % 365 == 0) {
            pt.ipp.isep.dei.domain.template.Map map = simulator.getMap();
            for (Station station : map.getStations()) {
                station.updateDemand(currentYear);
            }
        }
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
        pt.ipp.isep.dei.domain.template.Map currentMap = currentSimulator.getMap();
        Scenario currentScenario = currentSimulator.getScenario();
        
        // Stop the current simulation
        stopBackgroundSimulation();
        currentSimulator.stop();
        
        // Create a new simulator with the same map and scenario
        Simulator newSimulator = simulatorRepository.createSimulator(currentMap, currentScenario);
        if (newSimulator == null) {
            return false;
        }
        
        // Reset the simulator's date to the scenario's start date
        newSimulator.resetDate(currentScenario.getStartDate());
        
        // Start the new simulator
        boolean started = newSimulator.start();
        
        if (started) {
            // Generate initial cargo
            newSimulator.generateCargo();
            
            // Start background simulation
            startBackgroundSimulation();
            
            // Check for locomotive availability
            checkLocomotiveAvailability();
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
        
        // Check if there are any trains assigned to routes
        boolean hasAssignedTrains = false;
        RouteRepository routeRepository = Repositories.getInstance().getRouteRepository();
        for (Route route : routeRepository.getAll()) {
            if (!route.getAssignedTrains().isEmpty()) {
                hasAssignedTrains = true;
                break;
            }
        }
        
        if (!hasAssignedTrains) {
            System.out.println("No trains assigned to routes. Cargo generation will start when trains are assigned.");
            return false;
        }
        
        // Generate cargo at cities and industries
        simulator.generateCargo();
        
        // Transfer cargo to stations within economic radius
        pt.ipp.isep.dei.domain.template.Map map = simulator.getMap();
        boolean anyCargoGenerated = false;
        
        for (City city : map.getCities()) {
            for (Station station : map.getStations()) {
                if (station.isWithinRadius(city.getPosition())) {
                    // Generate cargo for this city and add it to the station
                    List<Cargo> cityCargo = simulator.generateCargoForCity(city);
                    for (Cargo cargo : cityCargo) {
                        if (station.hasStorageCapacity(cargo.getAmount())) {
                            if (station.addCargo(cargo)) {
                                anyCargoGenerated = true;
                            }
                        } else {
                            System.out.printf("Station %s is at full capacity (%d/%d). Cannot add more cargo.\n", 
                                station.getNameID(), 
                                station.getStorageCapacity() - station.getAvailableStorage(),
                                station.getStorageCapacity());
                        }
                    }
                }
            }
        }
        
        for (Industry industry : map.getIndustries()) {
            for (Station station : map.getStations()) {
                if (station.isWithinRadius(industry.getPosition())) {
                    // Generate cargo for this industry and add it to the station
                    List<Cargo> industryCargo = simulator.generateCargoForIndustry(industry);
                    for (Cargo cargo : industryCargo) {
                        if (station.hasStorageCapacity(cargo.getAmount())) {
                            if (station.addCargo(cargo)) {
                                anyCargoGenerated = true;
                            }
                        } else {
                            System.out.printf("Station %s is at full capacity (%d/%d). Cannot add more cargo.\n", 
                                station.getNameID(), 
                                station.getStorageCapacity() - station.getAvailableStorage(),
                                station.getStorageCapacity());
                        }
                    }
                }
            }
        }
        
        if (!anyCargoGenerated) {
            System.out.println("No new cargo was generated. All stations are at full capacity.");
        }
        
        return anyCargoGenerated;
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
    
    /**
     * Handles train assignment to a route and triggers cargo generation
     * @param train The train being assigned
     * @param route The route the train is being assigned to
     * @return True if the assignment was successful
     */
    public boolean handleTrainAssignment(Train train, Route route) {
        if (train == null || route == null) {
            return false;
        }
        
        // Add train to route
        boolean assigned = route.addTrain(train);
        if (assigned) {
            // Trigger cargo generation since we now have an assigned train
            generateCargo();
        }
        return assigned;
    }

    public int getCurrentYear() {
        Simulator simulator = Repositories.getInstance().getSimulatorRepository().getActiveSimulator();

        if (simulator == null) {
            return 0; // Default to year 0 if no simulation is active
        }

        Date date = simulator.getCurrentSimulatedDate();
        int year = date.getYear() + 1900; // Date.getYear() returns years since 1900
        return year;
    }

    /**
     * Checks for newly available buildings and shows a notification
     */
    private void checkBuildingAvailability() {
        Simulator simulator = simulatorRepository.getActiveSimulator();
        if (simulator == null) return;
        
        int currentYear = getCurrentYear();
        BuildingRepository buildingRepository = Repositories.getInstance().getBuildingRepository();
        List<Building> allBuildings = buildingRepository.getNewBuildingOptions();
        
        List<Building> newlyAvailableBuildings = new ArrayList<>();
        for (Building building : allBuildings) {
            if (building.getAvailabilityYear() == currentYear) {
                newlyAvailableBuildings.add(building);
            }
        }
        
        if (!newlyAvailableBuildings.isEmpty()) {
            SimulationNotificationHelper.showBuildingAvailabilityNotification(currentYear, newlyAvailableBuildings);
        }
    }

    public boolean saveGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            return false;
        }

        try {
            // Get current game state
            Scenario currentScenario = scenarioRepository.getCurrentScenario();
            if (currentScenario == null) {
                return false;
            }

            // Create game state object
            GameState gameState = new GameState(
                currentScenario,
                scenarioRepository.getCurrentDate(),
                getCurrentPlayerBudget(),
                getActiveTrains(),
                getCurrentStations(),
                getCurrentRoutes(),
                getCurrentYear(),
                currentScenario.getNameID(),
                currentScenario.getNameID()
            );

            // Save to file
            String filePath = SAVED_GAMES_DIR + File.separator + saveName + ".sav";
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(gameState);
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean loadGame(String saveName) {
        if (saveName == null || saveName.trim().isEmpty()) {
            System.out.println("LoadGame: Invalid save name provided.");
            return false;
        }

        String filePath = SAVED_GAMES_DIR + File.separator + saveName + ".sav";
        File savFile = new File(filePath);
        if (!savFile.exists()) {
            filePath = SAVED_GAMES_DIR + File.separator + saveName + ".game";
            File gameFile = new File(filePath);
            if (!gameFile.exists()) {
                System.out.println("LoadGame: File not found at " + savFile.getAbsolutePath() + " or " + gameFile.getAbsolutePath());
                return false;
            }
        }

        System.out.println("LoadGame: Attempting to load from file: " + filePath);

        try (
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))
        ) {
            GameState gameState = (GameState) ois.readObject();
            System.out.println("LoadGame: GameState object successfully read from file.");

            // Stop current simulation if running
            if (isSimulationRunningInBackground()) {
                System.out.println("LoadGame: Stopping background simulation before loading.");
                stopSimulation();
            }

            // Restore game state
            System.out.println("LoadGame: Restoring scenario...");
            scenarioRepository.setCurrentScenario(gameState.getScenario());
            System.out.println("LoadGame: Scenario restored: " + (scenarioRepository.getCurrentScenario() != null ? scenarioRepository.getCurrentScenario().getNameID() : "null"));

            System.out.println("LoadGame: Restoring date...");
            scenarioRepository.setCurrentDate(gameState.getCurrentDate());
            System.out.println("LoadGame: Date restored: " + scenarioRepository.getCurrentDate());

            System.out.println("LoadGame: Restoring player budget...");
            restorePlayerBudget(gameState.getCurrentBudget());
            System.out.println("LoadGame: Player budget restored.");

            System.out.println("LoadGame: Restoring active trains...");
            restoreActiveTrains(gameState.getActiveTrains());
            System.out.println("LoadGame: Active trains restored.");

            System.out.println("LoadGame: Restoring stations...");
            restoreStations(gameState.getStations());
            System.out.println("LoadGame: Stations restored.");

            System.out.println("LoadGame: Restoring routes...");
            restoreRoutes(gameState.getRoutes());
            System.out.println("LoadGame: Routes restored.");
            
            System.out.println("LoadGame: Game loaded successfully!");
            return true;
        } catch (IOException e) {
            System.err.println("LoadGame: IOException during game load: " + e.getMessage());
            e.printStackTrace();
            return false;
        } catch (ClassNotFoundException e) {
            System.err.println("LoadGame: ClassNotFoundException during game load: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getSavedGames() {
        File savedGamesDir = new File(SAVED_GAMES_DIR);
        if (!savedGamesDir.exists() || !savedGamesDir.isDirectory()) {
            System.out.println("Saved games directory not found or is not a directory: " + savedGamesDir.getAbsolutePath());
            return new ArrayList<>();
        }

        File[] files = savedGamesDir.listFiles((dir, name) -> name.endsWith(".sav") || name.endsWith(".game"));
        if (files == null) {
            System.out.println("No files found in saved games directory: " + savedGamesDir.getAbsolutePath());
            return new ArrayList<>();
        }

        List<String> savedGames = new ArrayList<>();
        for (File file : files) {
            String fileName = file.getName();
            if (fileName.endsWith(".sav")) {
                savedGames.add(fileName.replace(".sav", ""));
            } else if (fileName.endsWith(".game")) {
                savedGames.add(fileName.replace(".game", ""));
            }
        }
        System.out.println("Found saved games: " + savedGames);
        return savedGames;
    }

    private double getCurrentPlayerBudget() {
        Player player = getCurrentPlayer();
        return player != null ? player.getCurrentBudget() : 0.0;
    }

    private void restorePlayerBudget(double budget) {
        Player player = getCurrentPlayer();
        if (player != null) {
            player.deductFromBudget(player.getCurrentBudget()); // Clear current budget
            player.addToBudget(budget); // Set new budget
        }
    }

    private List<Train> getActiveTrains() {
        // Implementation to get all active trains
        return trainRepository.getAllTrains();
    }

    private void restoreActiveTrains(List<Train> trains) {
        // Clear existing trains
        List<Train> existingTrains = trainRepository.getAllTrains();
        for (Train train : existingTrains) {
            trainRepository.delete(train.getNameID());
        }
        
        // Add saved trains
        for (Train train : trains) {
            trainRepository.save(train);
        }
    }

    private List<Station> getCurrentStations() {
        // Implementation to get all stations
        return stationRepository.getAllStations();
    }

    private void restoreStations(List<Station> stations) {
        // Clear existing stations
        List<Station> existingStations = stationRepository.getAllStations();
        for (Station station : existingStations) {
            stationRepository.remove(station.getNameID());
        }
        
        // Add saved stations
        for (Station station : stations) {
            stationRepository.save(station);
        }
    }

    private List<Route> getCurrentRoutes() {
        // Implementation to get all routes
        return routeRepository.getAll();
    }

    private void restoreRoutes(List<Route> routes) {
        // Clear existing routes
        List<Route> existingRoutes = routeRepository.getAll();
        for (Route route : existingRoutes) {
            routeRepository.delete(route.getNameID());
        }
        
        // Add saved routes
        for (Route route : routes) {
            routeRepository.save(route);
        }
    }

    private Player getCurrentPlayer() {
        UserSession currentSession = ApplicationSession.getInstance().getCurrentSession();
        if (currentSession == null) {
            return null;
        }
        return playerRepository.getPlayerByEmail(currentSession.getUserEmail());
    }

    public ScenarioRepository getScenarioRepository() {
        return scenarioRepository;
    }
} 