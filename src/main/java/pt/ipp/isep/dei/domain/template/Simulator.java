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
    
    private String status;
    private int runTime;
    private String report;
    private Map map;
    private Scenario scenario;
    private final Date simulationStartTime;
    private Date lastCargoGeneration;
    private final java.util.Map<String, List<Cargo>> generatedCargoHistory;
    private final Random random;
    private int cargoCyclesCompleted;
    private final int CARGO_GENERATION_INTERVAL = 5; // in simulated days
    private double productionRate; // Production rate for cargo generation
    
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
        this.runTime = 0;
        this.report = "";
        this.simulationStartTime = new Date();
        this.lastCargoGeneration = null;
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
        lastCargoGeneration = new Date();
        generateInitialCargo();
        
        return true;
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
        return true;
    }
    
    /**
     * Stops the simulator and generates a report
     * @return The simulation report
     */
    public String stop() {
        if (STATUS_STOPPED.equals(status)) {
            return report; // Already stopped
        }
        
        status = STATUS_STOPPED;
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
        
        // Validate operation
        if (!validateOperation(operationType, parameters)) {
            return "Invalid operation or parameters";
        }
        
        // Perform operation
        return performOperation(operationType, parameters);
    }
    
    /**
     * Validates an operation before execution
     * @param operationType The type of operation to validate
     * @param parameters The parameters to validate
     * @return True if the operation is valid
     */
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
    
    /**
     * Performs an operation
     * @param operationType The type of operation to perform
     * @param parameters The parameters for the operation
     * @return The result of the operation
     */
    private Object performOperation(String operationType, java.util.Map<String, Object> parameters) {
        // Here we would actually call the appropriate controller to perform the operation
        // For now, we just return a success message
        return "Operation " + operationType + " executed successfully";
    }
    
    /**
     * Generates cargo at stations based on cities and industries
     */
    public void generateCargo() {
        if (!STATUS_RUNNING.equals(status)) {
            return;
        }
        
        // Update simulation time
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(lastCargoGeneration);
        calendar.add(Calendar.DAY_OF_MONTH, CARGO_GENERATION_INTERVAL);
        Date currentTime = calendar.getTime();
        lastCargoGeneration = currentTime;
        cargoCyclesCompleted++;
        
        // Get all stations
        List<Station> stations = map.getStations();
        
        for (Station station : stations) {
            // Get cities and industries served by this station
            List<City> cities = station.getServedCities();
            List<Industry> industries = getIndustriesServedByStation(station);
            
            // Generate cargo for each city
            for (City city : cities) {
                List<Cargo> generatedCargo = generateCargoForCity(city);
                for (Cargo cargo : generatedCargo) {
                    addCargoToStation(station, cargo);
                }
            }
            
            // Generate cargo for each industry
            for (Industry industry : industries) {
                List<Cargo> generatedCargo = generateCargoForIndustry(industry);
                for (Cargo cargo : generatedCargo) {
                    addCargoToStation(station, cargo);
                }
            }
        }
    }
    
    /**
     * Generates initial cargo at the start of simulation
     */
    private void generateInitialCargo() {
        generateCargo();
    }
    
    /**
     * Gets industries served by a station
     * @param station The station to check
     * @return List of industries served by the station
     */
    private List<Industry> getIndustriesServedByStation(Station station) {
        List<Industry> servedIndustries = new ArrayList<>();
        List<Industry> allIndustries = map.getIndustries();
        
        // Check which industries are within the station's economic radius
        for (Industry industry : allIndustries) {
            if (station.isWithinRadius(industry.getPosition())) {
                servedIndustries.add(industry);
            }
        }
        
        return servedIndustries;
    }
    
    /**
     * Generates cargo for a city
     * @param city The city to generate cargo for
     * @return List of generated cargo
     */
    private List<Cargo> generateCargoForCity(City city) {
        List<Cargo> generatedCargo = new ArrayList<>();
        
        // Generate passenger cargo
        int passengerAmount = random.nextInt(10) + 5; // 5-15 passengers
        Cargo passengers = new Cargo("Passengers from " + city.getNameID(), 
                                    passengerAmount,
                                    2, // Lifespan in days
                                    "passenger");
        generatedCargo.add(passengers);
        
        // Generate mail cargo
        int mailAmount = random.nextInt(5) + 1; // 1-5 mail bags
        Cargo mail = new Cargo("Mail from " + city.getNameID(),
                              mailAmount,
                              7, // Lifespan in days
                              "mail");
        generatedCargo.add(mail);
        
        // Record generated cargo in history
        String cityKey = "city_" + city.getNameID();
        generatedCargoHistory.putIfAbsent(cityKey, new ArrayList<>());
        generatedCargoHistory.get(cityKey).addAll(generatedCargo);
        
        return generatedCargo;
    }
    
    /**
     * Generates cargo for an industry
     * @param industry The industry to generate cargo for
     * @return List of generated cargo
     */
    private List<Cargo> generateCargoForIndustry(Industry industry) {
        List<Cargo> generatedCargo = new ArrayList<>();
        
        // Different types of industries generate different cargo
        String industryType = industry.getType();
        String industrySector = industry.getSector();
        
        // Get industry's production rate if available, otherwise use simulator's default
        double industryProductionRate = industry.getProductionRate() > 0 ? 
                                       industry.getProductionRate() : this.productionRate;
        
        // Generate cargo based on industry type
        if ("Mine".equals(industryType)) {
            // Generate raw materials
            int amount = (int) (10 * industryProductionRate * (random.nextDouble() + 0.5));
            String cargoType = industry.getNameID().contains("coal") ? "coal" : "ore";
            Cargo rawMaterial = new Cargo(cargoType + " from " + industry.getNameID(),
                                        amount,
                                        30, // Lifespan in days
                                        cargoType);
            generatedCargo.add(rawMaterial);
        } else if ("Farm".equals(industryType)) {
            // Generate agricultural products
            int amount = (int) (8 * industryProductionRate * (random.nextDouble() + 0.5));
            Cargo agriculturalProducts = new Cargo("Food from " + industry.getNameID(),
                                                amount,
                                                15, // Lifespan in days
                                                "food");
            generatedCargo.add(agriculturalProducts);
        } else if ("Steel Mill".equals(industryType)) {
            // Generate manufactured goods
            int amount = (int) (5 * industryProductionRate * (random.nextDouble() + 0.5));
            Cargo manufacturedGoods = new Cargo("Steel from " + industry.getNameID(),
                                              amount,
                                              60, // Lifespan in days
                                              "steel");
            generatedCargo.add(manufacturedGoods);
        } else if ("Port".equals(industryType)) {
            // Generate imports/exports
            int amount = (int) (15 * industryProductionRate * (random.nextDouble() + 0.5));
            Cargo imports = new Cargo("Imports from " + industry.getNameID(),
                                    amount,
                                    30, // Lifespan in days
                                    "goods");
            generatedCargo.add(imports);
        }
        
        // Record generated cargo in history
        String industryKey = "industry_" + industry.getNameID();
        generatedCargoHistory.putIfAbsent(industryKey, new ArrayList<>());
        generatedCargoHistory.get(industryKey).addAll(generatedCargo);
        
        return generatedCargo;
    }
    
    /**
     * Adds cargo to a station's available cargo
     * @param station The station to add cargo to
     * @param cargo The cargo to add
     */
    private void addCargoToStation(Station station, Cargo cargo) {
        // Check if the station already has this cargo type
        List<Cargo> availableCargo = station.getAvailableCargo();
        boolean cargoAdded = false;
        
        for (Cargo existingCargo : availableCargo) {
            if (existingCargo.getType().equals(cargo.getType())) {
                // Add to existing cargo
                existingCargo.setAmount(existingCargo.getAmount() + cargo.getAmount());
                cargoAdded = true;
                break;
            }
        }
        
        if (!cargoAdded) {
            // Add as new cargo
            availableCargo.add(cargo);
        }
    }
    
    /**
     * Generates a report of the simulation
     */
    private void generateReport() {
        StringBuilder reportBuilder = new StringBuilder();
        
        // Basic simulator information
        reportBuilder.append("Simulator Report\n");
        reportBuilder.append("===============\n\n");
        reportBuilder.append("Simulation Map: ").append(map.getNameID()).append("\n");
        reportBuilder.append("Simulation Scenario: ").append(scenario.getNameID()).append("\n");
        reportBuilder.append("Simulation Start Time: ").append(simulationStartTime).append("\n");
        reportBuilder.append("Simulation End Time: ").append(new Date()).append("\n");
        reportBuilder.append("Cargo Generation Cycles: ").append(cargoCyclesCompleted).append("\n\n");
        
        // Cargo generation statistics
        reportBuilder.append("Cargo Generation Statistics\n");
        reportBuilder.append("--------------------------\n\n");
        
        for (java.util.Map.Entry<String, List<Cargo>> entry : generatedCargoHistory.entrySet()) {
            String source = entry.getKey();
            List<Cargo> cargoList = entry.getValue();
            
            reportBuilder.append("Source: ").append(source).append("\n");
            reportBuilder.append("Generated Cargo Items: ").append(cargoList.size()).append("\n");
            
            // Count cargo by type
            java.util.Map<String, Integer> typeCount = new HashMap<>();
            for (Cargo cargo : cargoList) {
                typeCount.put(cargo.getType(), typeCount.getOrDefault(cargo.getType(), 0) + 1);
            }
            
            reportBuilder.append("Cargo types: ");
            for (java.util.Map.Entry<String, Integer> typeEntry : typeCount.entrySet()) {
                reportBuilder.append(typeEntry.getKey()).append(" (").append(typeEntry.getValue()).append(") ");
            }
            reportBuilder.append("\n\n");
        }
        
        // Final report
        report = reportBuilder.toString();
    }
    
    /**
     * Gets the current status of the simulator
     * @return The current status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Gets the map used by the simulator
     * @return The map
     */
    public Map getMap() {
        return map;
    }
    
    /**
     * Gets the scenario used by the simulator
     * @return The scenario
     */
    public Scenario getScenario() {
        return scenario;
    }
    
    /**
     * Sets the production rate for cargo generation
     * @param productionRate The production rate to set
     */
    public void setProductionRate(double productionRate) {
        if (productionRate > 0) {
            this.productionRate = productionRate;
        }
    }
    
    /**
     * Gets the production rate for cargo generation
     * @return The production rate
     */
    public double getProductionRate() {
        return productionRate;
    }
    
    /**
     * Gets cargo generation details for UI display
     * @return Map of station IDs to cargo lists
     */
    public java.util.Map<String, List<Cargo>> getCargoGenerationDetails() {
        java.util.Map<String, List<Cargo>> stationCargo = new HashMap<>();
        
        for (Station station : map.getStations()) {
            stationCargo.put(station.getNameID(), station.getAvailableCargo());
        }
        
        return stationCargo;
    }
} 