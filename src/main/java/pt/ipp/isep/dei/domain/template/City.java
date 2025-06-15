package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class City implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private static final int BASE_PASSENGER_PRODUCTION = 5;
    private static final int BASE_MAIL_PRODUCTION = 5;
    private final String nameID;
    private final Position position;
    private final List<HouseBlock> houseBlocks;
    private float trafficRate;
    private int suppliedCargo;
    private int demandedCargo;
    private double productionRate;
    private Date lastPassengerProductionDate;
    private Date lastMailProductionDate;

    public City(String nameID, Position position, List<HouseBlock> houseBlocks) {
        this.nameID = nameID;
        this.position = position;
        this.houseBlocks = new ArrayList<>(houseBlocks);
        this.trafficRate = 0.0f;
        this.suppliedCargo = 0;
        this.demandedCargo = 0;
        this.productionRate = 1.0;
        this.lastPassengerProductionDate = new Date();
        this.lastMailProductionDate = new Date();
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

    public double getProductionRate() {
        return productionRate;
    }

    public void setProductionRate(double productionRate) {
        this.productionRate = productionRate;
    }

    /**
     * Gets the passenger production amount based on production rate
     * @return The number of passengers produced
     */
    public int getPassengerProduction() {
        // Check if enough time has passed since last production (30 seconds)
        Date now = new Date();
        if (now.getTime() - lastPassengerProductionDate.getTime() < 30000) {
            return 0;
        }
        lastPassengerProductionDate = now;
        return (int) (BASE_PASSENGER_PRODUCTION * productionRate);
    }

    /**
     * Gets the mail production amount based on production rate
     * @return The number of mail units produced
     */
    public int getMailProduction() {
        // Check if enough time has passed since last production (30 seconds)
        Date now = new Date();
        if (now.getTime() - lastMailProductionDate.getTime() < 30000) {
            return 0;
        }
        lastMailProductionDate = now;
        return (int) (BASE_MAIL_PRODUCTION * productionRate);
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