package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class City {
    private String nameID;
    private double trafficRate;
    private List<Cargo> suppliedCargo;
    private List<Cargo> demandedCargo;
    private Position position;

    public City(String nameID, Position position) {
        this.nameID = nameID;
        this.position = position;
        this.trafficRate = 1.0; // Default traffic rate
        this.suppliedCargo = new ArrayList<>();
        this.demandedCargo = new ArrayList<>();
    }

    // Getters
    public String getNameID() {
        return nameID;
    }

    public double getTrafficRate() {
        return trafficRate;
    }

    public List<Cargo> getSuppliedCargo() {
        return new ArrayList<>(suppliedCargo);
    }

    public List<Cargo> getDemandedCargo() {
        return new ArrayList<>(demandedCargo);
    }

    public Position getPosition() {
        return position;
    }

    // Setters
    public void setTrafficRate(double trafficRate) {
        this.trafficRate = trafficRate;
    }
} 