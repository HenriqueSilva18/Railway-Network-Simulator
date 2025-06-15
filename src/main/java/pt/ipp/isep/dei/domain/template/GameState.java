package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Scenario scenario;
    private final Date currentDate;
    private final double currentBudget;
    private final List<Train> activeTrains;
    private final List<Station> stations;
    private final List<Route> routes;
    private final int currentYear;
    private final String mapId;
    private final String scenarioId;

    public GameState(Scenario scenario, Date currentDate, double currentBudget, 
                    List<Train> activeTrains, List<Station> stations, 
                    List<Route> routes, int currentYear, String mapId, String scenarioId) {
        this.scenario = scenario;
        this.currentDate = currentDate;
        this.currentBudget = currentBudget;
        this.activeTrains = new ArrayList<>(activeTrains);
        this.stations = new ArrayList<>(stations);
        this.routes = new ArrayList<>(routes);
        this.currentYear = currentYear;
        this.mapId = mapId;
        this.scenarioId = scenarioId;
    }

    // Getters
    public Scenario getScenario() { return scenario; }
    public Date getCurrentDate() { return currentDate; }
    public double getCurrentBudget() { return currentBudget; }
    public List<Train> getActiveTrains() { return new ArrayList<>(activeTrains); }
    public List<Station> getStations() { return new ArrayList<>(stations); }
    public List<Route> getRoutes() { return new ArrayList<>(routes); }
    public int getCurrentYear() { return currentYear; }
    public String getMapId() { return mapId; }
    public String getScenarioId() { return scenarioId; }
} 