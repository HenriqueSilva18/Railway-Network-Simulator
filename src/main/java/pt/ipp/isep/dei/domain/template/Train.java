package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private String nameID;
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private Route assignedRoute;
    private boolean inTransit;
    private Station currentStation;
    private RailwayLine currentLine;
    private double position; // Position along the current line (0.0 to 1.0)
    private List<Cargo> cargo;

    public Train(String nameID, Locomotive locomotive) {
        this.nameID = nameID;
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>();
        this.assignedRoute = null;
        this.inTransit = false;
        this.currentStation = null;
        this.currentLine = null;
        this.position = 0.0;
        this.cargo = new ArrayList<>();
    }

    public String getNameID() {
        return nameID;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public List<Carriage> getCarriages() {
        return new ArrayList<>(carriages);
    }

    public Route getAssignedRoute() {
        return assignedRoute;
    }
    
    public Route getCurrentRoute() {
        return assignedRoute;
    }
    
    public boolean isAssignedToRoute() {
        return assignedRoute != null;
    }

    public boolean addCarriage(Carriage carriage) {
        if (carriage == null) {
            return false;
        }
        return carriages.add(carriage);
    }

    public boolean assignToRoute(Route route) {
        if (route == null) {
            return false;
        }
        
        // Verify if the route has valid stations
        if (!route.validateStations()) {
            return false;
        }
        
        // Assign route to train
        this.assignedRoute = route;
        
        // Set initial station
        if (!route.getStationSequence().isEmpty()) {
            this.currentStation = route.getStationSequence().get(0);
        }
        
        return true;
    }

    public TrainDetails getDetails() {
        return new TrainDetails(
            this.nameID,
            this.locomotive.getType(),
            this.locomotive.getPower(),
            this.locomotive.getTopSpeed(),
            this.carriages.size(),
            this.assignedRoute != null ? this.assignedRoute.getNameID() : "None"
        );
    }

    public boolean isInTransit() {
        return inTransit;
    }

    public Station getCurrentStation() {
        return currentStation;
    }

    public RailwayLine getCurrentLine() {
        return currentLine;
    }

    public double getPosition() {
        return position;
    }

    public List<Cargo> getCargo() {
        return new ArrayList<>(cargo);
    }

    public void updatePosition(double speed) {
        if (!inTransit || currentLine == null) {
            return;
        }

        // Calculate distance traveled based on speed
        double distanceTraveled = speed / currentLine.getLength();
        position += distanceTraveled;

        // Check if train has arrived at destination
        if (position >= 1.0) {
            position = 1.0;
            inTransit = false;
            currentStation = currentLine.getEndStation();
            currentLine = null;
        }
    }

    public boolean hasArrived() {
        return !inTransit && currentLine == null && currentStation != null;
    }

    public void startJourney(RailwayLine line) {
        if (line == null || currentStation == null) {
            return;
        }

        // Verify the line starts from current station
        if (!line.getStartStation().equals(currentStation)) {
            return;
        }

        currentLine = line;
        position = 0.0;
        inTransit = true;
    }

    public void addCargo(Cargo cargo) {
        if (cargo != null) {
            this.cargo.add(cargo);
        }
    }

    public void clearCargo() {
        this.cargo.clear();
    }

    @Override
    public String toString() {
        return nameID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Train train = (Train) obj;
        return nameID != null && nameID.equals(train.nameID);
    }

    @Override
    public int hashCode() {
        return nameID != null ? nameID.hashCode() : 0;
    }
} 