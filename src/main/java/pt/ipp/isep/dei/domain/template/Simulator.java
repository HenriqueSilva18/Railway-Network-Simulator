package pt.ipp.isep.dei.domain.template;

import java.util.*;

/**
 * This class represents a simulator that generates cargoes at stations considering cities and industries.
 */
public class Simulator {
    // Status constants
    public static final String STATUS_STOPPED = "STOPPED";
    public static final String STATUS_RUNNING = "RUNNING";
    public static final String STATUS_PAUSED = "PAUSED";

    private volatile String status; // Volatile for thread visibility
    private int elapsedSimulatedDays; // Replaces runTime, clearer semantic
    private String report;
    private Map map;
    private Scenario scenario;

    private final Date realWorldStartTime; // When the Simulator object was created/started
    private Date scenarioStartDate;      // Logical start date of the scenario
    private Date currentSimulatedDate;   // Current in-game date, advanced by the loop
    private Date lastCargoGenerationDate; // The simulated date when cargo was last generated

    private final java.util.Map<String, List<Cargo>> generatedCargoHistory;
    private final Random random;
    private int cargoCyclesCompleted;
    private final int CARGO_GENERATION_INTERVAL = 5; // in simulated days, e.g., generate cargo every 5 simulated days
    private double productionRate;

    // Threading members for Option A
    private transient Thread simulationThread; // transient: not part of object's persistent state if serialized
    private volatile boolean keepThreadRunning;


    /**
     * Constructor for the simulator
     * @param map The map to use for simulation
     * @param scenario The scenario to use for simulation
     */
    public Simulator(Map map, Scenario scenario) {
        if (map == null || scenario == null) {
            throw new IllegalArgumentException("Map and scenario cannot be null");
        }
        this.map = map;
        this.scenario = scenario;
        this.status = STATUS_STOPPED;
        this.elapsedSimulatedDays = 0;
        this.report = "";
        this.realWorldStartTime = new Date();

        // Assuming Scenario has a getStartDate() method
        // If not, you might need to default it or pass it in.
        // For this example, let's assume it exists or default it.
        this.scenarioStartDate = scenario.getStartDate() != null ? (Date) scenario.getStartDate().clone() : new Date();

        this.currentSimulatedDate = (Date) this.scenarioStartDate.clone();
        this.lastCargoGenerationDate = null; // Will be set on first generation

        this.generatedCargoHistory = new HashMap<>();
        this.random = new Random();
        this.cargoCyclesCompleted = 0;
        this.productionRate = 1.0; // Default production rate
    }

    /**
     * Starts the simulator
     * @return True if the simulator was started successfully
     */
    public boolean start() {
        if (!STATUS_STOPPED.equals(status)) {
            return false; // Can only start from stopped state
        }
        status = STATUS_RUNNING;
        this.elapsedSimulatedDays = 0; // Reset elapsed days
        this.currentSimulatedDate = (Date) this.scenarioStartDate.clone(); // Reset current sim date
        this.lastCargoGenerationDate = null; // Reset last generation date
        this.generatedCargoHistory.clear(); // Clear previous history
        this.cargoCyclesCompleted = 0;


        // Perform initial cargo generation for the starting date
        // This will also set lastCargoGenerationDate for the first time
        generateInitialCargo();

        keepThreadRunning = true;
        simulationThread = new Thread(() -> {
            while (keepThreadRunning) {
                if (STATUS_RUNNING.equals(status)) {
                    try {
                        // Advance simulation time by one day
                        advanceSimulationDay();
                        this.elapsedSimulatedDays++;

                        // Check and perform cargo generation
                        if (isTimeToGenerateNextCargo()) {
                            generateCargo();
                        }

                        // Placeholder for other simulation activities (e.g., train movements)
                        // logEvent("Simulated day " + elapsedSimulatedDays + " completed. Current date: " + currentSimulatedDate);

                        // Control simulation speed (e.g., 1 simulated day per 100ms real time)
                        Thread.sleep(100); // Adjust for desired speed
                    } catch (InterruptedException e) {
                        System.err.println("Simulation thread interrupted.");
                        keepThreadRunning = false; // Stop the thread if interrupted
                        Thread.currentThread().interrupt(); // Preserve interrupt status
                    } catch (Exception e) {
                        System.err.println("Exception in simulation loop: " + e.getMessage());
                        e.printStackTrace();
                        // Optionally, pause or stop simulation on unhandled errors
                        // status = STATUS_PAUSED;
                    }
                } else if (STATUS_PAUSED.equals(status)) {
                    // If paused, sleep for a bit to yield CPU
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        keepThreadRunning = false;
                        Thread.currentThread().interrupt();
                    }
                } else { // STATUS_STOPPED or unexpected
                    keepThreadRunning = false; // Ensure loop terminates if status is STOPPED by external means
                }
            }
            // System.out.println("Simulation thread finished.");
        });
        simulationThread.setName("SimulatorThread-" + System.currentTimeMillis());
        simulationThread.start();
        return true;
    }

    private void advanceSimulationDay() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(this.currentSimulatedDate);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        this.currentSimulatedDate = cal.getTime();
    }

    private boolean isTimeToGenerateNextCargo() {
        if (this.lastCargoGenerationDate == null) {
            // This case is typically handled by generateInitialCargo setting it.
            // If it's still null, it implies the first generation for the current start hasn't happened.
            // However, generateInitialCargo is called in start() before the loop,
            // so this check in the loop should always find lastCargoGenerationDate populated.
            // For safety, let's assume if it's null, it's time for the very first scheduled generation.
            return true;
        }

        Calendar nextGenerationTargetDate = Calendar.getInstance();
        nextGenerationTargetDate.setTime(this.lastCargoGenerationDate);
        nextGenerationTargetDate.add(Calendar.DAY_OF_MONTH, CARGO_GENERATION_INTERVAL);

        // True if currentSimulatedDate is on or after the nextGenerationTargetDate
        return !this.currentSimulatedDate.before(nextGenerationTargetDate.getTime());
    }


    /**
     * Pauses the simulator
     * @return True if the simulator was paused successfully
     */
    public boolean pause() {
        if (!STATUS_RUNNING.equals(status)) {
            return false; // Can only pause from running state
        }
        status = STATUS_PAUSED;
        // System.out.println("Simulator paused at day " + elapsedSimulatedDays);
        return true;
    }

    /**
     * Resumes the simulator
     * @return True if the simulator was resumed successfully
     */
    public boolean resume() {
        if (!STATUS_PAUSED.equals(status)) {
            return false; // Can only resume from paused state
        }
        status = STATUS_RUNNING;
        // System.out.println("Simulator resumed at day " + elapsedSimulatedDays);
        return true;
    }

    /**
     * Stops the simulator and generates a report
     * @return The simulation report
     */
    public String stop() {
        if (STATUS_STOPPED.equals(status) && !keepThreadRunning) { // Check if already fully stopped
            // System.out.println("Simulator was already stopped.");
            return report;
        }

        String previousStatus = this.status;
        status = STATUS_STOPPED; // Set status to stopped first
        keepThreadRunning = false; // Signal the simulation thread to terminate

        if (simulationThread != null && simulationThread.isAlive()) {
            // System.out.println("Stopping simulation thread...");
            try {
                simulationThread.interrupt(); // Interrupt if it's sleeping
                simulationThread.join(2000); // Wait for the thread to finish (e.g., 2 seconds timeout)
                if (simulationThread.isAlive()) {
                    System.err.println("Simulation thread did not terminate gracefully.");
                    // Consider more forceful ways if necessary, though usually not recommended
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted while waiting for simulation thread to stop.");
                Thread.currentThread().interrupt(); // Preserve interrupt status
            }
        }
        simulationThread = null; // Release the thread object

        // System.out.println("Simulator stopped. Final elapsed simulated days: " + elapsedSimulatedDays);
        generateReport();
        return report;
    }

    /**
     * Executes an operation in the simulator
     * @param operationType The type of operation to execute
     * @param parameters The parameters for the operation
     * @return The result of the operation
     */
    public Object executeOperation(String operationType, java.util.Map<String, Object> parameters) {
        if (!STATUS_RUNNING.equals(status) && !STATUS_PAUSED.equals(status)) {
            return "Simulator is not running or paused";
        }

        if (!validateOperation(operationType, parameters)) {
            return "Invalid operation or parameters";
        }
        return performOperation(operationType, parameters);
    }

    private boolean validateOperation(String operationType, java.util.Map<String, Object> parameters) {
        if (operationType == null || parameters == null) {
            return false;
        }
        switch (operationType) {
            case "buy_locomotive":
                return parameters.containsKey("locomotive_id");
            case "build_railway_line":
                return parameters.containsKey("start_station") && parameters.containsKey("end_station");
            case "assign_train":
                return parameters.containsKey("train_id") && parameters.containsKey("route_id");
            default:
                return false;
        }
    }

    private Object performOperation(String operationType, java.util.Map<String, Object> parameters) {
        // Actual implementation would delegate to other services/controllers
        // This should be thread-safe if it modifies shared simulation state
        // System.out.println("Performing operation: " + operationType + " at day " + elapsedSimulatedDays);
        return "Operation " + operationType + " executed successfully";
    }

    /**
     * Generates cargo at stations based on cities and industries for the currentSimulatedDate.
     * This method is called periodically by the simulation loop.
     */
    public void generateCargo() {
        // This method assumes currentSimulatedDate is the date for which cargo is being generated.
        List<Station> stations = map.getStations();
        if (stations == null || stations.isEmpty()) {
            // System.out.println("No stations found on map " + map.getNameID() + " to generate cargo.");
            return;
        }

        // System.out.println("Generating cargo for simulated date: " + this.currentSimulatedDate);

        for (Station station : stations) {
            List<City> cities = station.getServedCities(); // Assuming Station class has this method
            List<Industry> industries = getIndustriesServedByStation(station);

            for (City city : cities) {
                List<Cargo> cityCargo = generateCargoForCity(city);
                for (Cargo cargo : cityCargo) {
                    addCargoToStation(station, cargo);
                }
            }

            for (Industry industry : industries) {
                List<Cargo> industryCargo = generateCargoForIndustry(industry);
                for (Cargo cargo : industryCargo) {
                    addCargoToStation(station, cargo);
                }
            }
        }
        this.lastCargoGenerationDate = (Date) this.currentSimulatedDate.clone(); // Mark generation for this date
        this.cargoCyclesCompleted++;
        // System.out.println("Cargo generation cycle " + cargoCyclesCompleted + " completed for " + this.currentSimulatedDate);
    }

    /**
     * Generates initial cargo at the start of simulation for the scenarioStartDate.
     */
    private void generateInitialCargo() {
        // System.out.println("Generating initial cargo for scenario start date: " + this.scenarioStartDate);
        // Temporarily set currentSimulatedDate to scenarioStartDate for this initial generation
        Date actualCurrentSimDate = this.currentSimulatedDate;
        this.currentSimulatedDate = (Date) this.scenarioStartDate.clone();

        generateCargo(); // This will use currentSimulatedDate (now scenarioStartDate) and update lastCargoGenerationDate

        this.currentSimulatedDate = actualCurrentSimDate; // Restore actual current sim date
        // Note: generateCargo already updates lastCargoGenerationDate and cargoCyclesCompleted.
    }


    private List<Industry> getIndustriesServedByStation(Station station) {
        List<Industry> servedIndustries = new ArrayList<>();
        List<Industry> allIndustries = map.getIndustries(); // Assuming Map class has this
        if (allIndustries == null) return servedIndustries;

        for (Industry industry : allIndustries) {
            // Assuming Station has isWithinRadius and Industry has getPosition
            if (station.isWithinRadius(industry.getPosition())) {
                servedIndustries.add(industry);
            }
        }
        return servedIndustries;
    }

    private List<Cargo> generateCargoForCity(City city) {
        List<Cargo> generatedItems = new ArrayList<>();
        // Using currentSimulatedDate as the creation date for cargo for consistency
        String passengersName = "Passengers from " + city.getNameID() + " on " + this.currentSimulatedDate;
        int passengerAmount = random.nextInt(10) + 5;
        Cargo passengers = new Cargo(passengersName, passengerAmount, 2, "passenger");
        generatedItems.add(passengers);

        String mailName = "Mail from " + city.getNameID() + " on " + this.currentSimulatedDate;
        int mailAmount = random.nextInt(5) + 1;
        Cargo mail = new Cargo(mailName, mailAmount, 7, "mail");
        generatedItems.add(mail);

        String cityKey = "city_" + city.getNameID();
        generatedCargoHistory.computeIfAbsent(cityKey, k -> new ArrayList<>()).addAll(generatedItems);
        return generatedItems;
    }

    private List<Cargo> generateCargoForIndustry(Industry industry) {
        List<Cargo> generatedItems = new ArrayList<>();
        String industryType = industry.getType();
        double effectiveProductionRate = industry.getProductionRate() > 0 ? industry.getProductionRate() : this.productionRate;
        String cargoBaseName = " from " + industry.getNameID() + " on " + this.currentSimulatedDate;

        if ("Mine".equals(industryType)) {
            int amount = (int) (10 * effectiveProductionRate * (random.nextDouble() * 0.5 + 0.5)); // Random factor between 0.5 and 1.0
            String cargoType = industry.getNameID().toLowerCase().contains("coal") ? "coal" : "ore";
            generatedItems.add(new Cargo(cargoType + cargoBaseName, amount, 30, cargoType));
        } else if ("Farm".equals(industryType)) {
            int amount = (int) (8 * effectiveProductionRate * (random.nextDouble() * 0.5 + 0.5));
            generatedItems.add(new Cargo("Food" + cargoBaseName, amount, 15, "food"));
        } else if ("Steel Mill".equals(industryType)) {
            int amount = (int) (5 * effectiveProductionRate * (random.nextDouble() * 0.5 + 0.5));
            generatedItems.add(new Cargo("Steel" + cargoBaseName, amount, 60, "steel"));
        } else if ("Port".equals(industryType)) {
            int amount = (int) (15 * effectiveProductionRate * (random.nextDouble() * 0.5 + 0.5));
            generatedItems.add(new Cargo("Goods" + cargoBaseName, amount, 30, "goods")); // Generic goods for port
        }
        // Add other industry types as needed

        if (!generatedItems.isEmpty()) {
            String industryKey = "industry_" + industry.getNameID();
            generatedCargoHistory.computeIfAbsent(industryKey, k -> new ArrayList<>()).addAll(generatedItems);
        }
        return generatedItems;
    }

    private void addCargoToStation(Station station, Cargo cargo) {
        // Assuming Station class has getAvailableCargo() and setAmount() on Cargo
        // And that station.getAvailableCargo() returns a modifiable list.
        // This also needs to handle cargo limits per station/type if required by project specs.
        List<Cargo> availableCargo = station.getAvailableCargo();
        boolean found = false;
        for (Cargo existing : availableCargo) {
            if (existing.getType().equals(cargo.getType())) {
                existing.setAmount(existing.getAmount() + cargo.getAmount());
                found = true;
                break;
            }
        }
        if (!found) {
            availableCargo.add(cargo);
        }
    }

    private void generateReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("Simulator Report\n");
        sb.append("================\n\n");
        sb.append("Simulation Map: ").append(map.getNameID()).append("\n");
        sb.append("Scenario: ").append(scenario.getNameID()).append("\n");
        sb.append("Scenario Start Date: ").append(this.scenarioStartDate).append("\n");
        sb.append("Simulation Ended on (Simulated Date): ").append(this.currentSimulatedDate).append("\n");
        sb.append("Total Simulated Days Elapsed: ").append(this.elapsedSimulatedDays).append("\n");
        sb.append("Real-world Start Time: ").append(this.realWorldStartTime).append("\n");
        sb.append("Report Generated Time: ").append(new Date()).append("\n");
        sb.append("Cargo Generation Cycles Completed: ").append(this.cargoCyclesCompleted).append("\n\n");

        sb.append("Cargo Generation History Statistics\n");
        sb.append("----------------------------------\n");
        if (generatedCargoHistory.isEmpty()) {
            sb.append("No cargo was recorded in the generation history.\n");
        } else {
            for (java.util.Map.Entry<String, List<Cargo>> entry : generatedCargoHistory.entrySet()) {
                String sourceKey = entry.getKey(); // e.g., "city_Madrid" or "industry_CoalMine1"
                List<Cargo> cargoList = entry.getValue();
                sb.append("\nSource: ").append(sourceKey).append("\n");

                java.util.Map<String, Integer> totalAmountPerType = new HashMap<>();
                java.util.Map<String, Integer> itemsPerType = new HashMap<>();
                int totalItemsFromSource = 0;

                for (Cargo c : cargoList) {
                    totalAmountPerType.put(c.getType(), totalAmountPerType.getOrDefault(c.getType(), 0) + c.getAmount());
                    itemsPerType.put(c.getType(), itemsPerType.getOrDefault(c.getType(), 0) + 1);
                    totalItemsFromSource++;
                }
                sb.append("  Total Cargo Items Generated: ").append(totalItemsFromSource).append("\n");
                for (java.util.Map.Entry<String, Integer> typeEntry : totalAmountPerType.entrySet()) {
                    sb.append("  - Type '").append(typeEntry.getKey()).append("': ")
                            .append(typeEntry.getValue()).append(" units (from ")
                            .append(itemsPerType.get(typeEntry.getKey())).append(" generation events for this type)\n");
                }
            }
        }
        this.report = sb.toString();
    }

    // --- Getters and Setters ---
    public String getStatus() {
        return status;
    }

    public Map getMap() {
        return map;
    }

    public Scenario getScenario() {
        return scenario;
    }

    public void setProductionRate(double productionRate) {
        if (productionRate > 0) {
            this.productionRate = productionRate;
        }
    }

    public double getProductionRate() {
        return productionRate;
    }

    public java.util.Map<String, List<Cargo>> getCargoGenerationDetails() {
        // This should return a representation of cargo currently at stations
        // For thread safety, it's better to create a deep copy or make Station's cargo list immutable
        java.util.Map<String, List<Cargo>> stationCargoSnapshot = new HashMap<>();
        if (map != null && map.getStations() != null) {
            for (Station station : map.getStations()) {
                // Assuming getAvailableCargo returns a new list or is otherwise safe to iterate
                // Or deep copy here if necessary
                stationCargoSnapshot.put(station.getNameID(), new ArrayList<>(station.getAvailableCargo()));
            }
        }
        return stationCargoSnapshot;
    }

    public Date getCurrentSimulatedDate() {
        return (Date) currentSimulatedDate.clone(); // Return a copy for immutability
    }

    public int getElapsedSimulatedDays() {
        return elapsedSimulatedDays;
    }
}