package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class City {
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
} 