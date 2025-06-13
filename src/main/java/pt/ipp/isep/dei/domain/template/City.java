package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String nameID;
    private final Position position;
    private final List<HouseBlock> houseBlocks;
    private float trafficRate;
    private int suppliedCargo;
    private int demandedCargo;

    public City(String nameID, Position position, List<HouseBlock> houseBlocks) {
        this.nameID = nameID;
        this.position = position;
        this.houseBlocks = new ArrayList<>(houseBlocks);
        this.trafficRate = 0.0f;
        this.suppliedCargo = 0;
        this.demandedCargo = 0;
    }

    public String getNameID() {
        return nameID;
    }

    public Position getPosition() {
        return position;
    }

    public List<HouseBlock> getHouseBlocks() {
        return new ArrayList<>(houseBlocks);
    }

    public float getTrafficRate() {
        return trafficRate;
    }

    public void setTrafficRate(float trafficRate) {
        this.trafficRate = trafficRate;
    }

    public int getSuppliedCargo() {
        return suppliedCargo;
    }

    public void setSuppliedCargo(int suppliedCargo) {
        this.suppliedCargo = suppliedCargo;
    }

    public int getDemandedCargo() {
        return demandedCargo;
    }

    public void setDemandedCargo(int demandedCargo) {
        this.demandedCargo = demandedCargo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        City city = (City) o;
        return Objects.equals(nameID, city.nameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameID);
    }

    @Override
    public String toString() {
        return nameID;
    }
} 