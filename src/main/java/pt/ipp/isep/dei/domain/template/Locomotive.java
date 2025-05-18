package pt.ipp.isep.dei.domain.template;

public class Locomotive {
    private String nameID;
    private String owner;
    private String type;
    private double power;
    private double acceleration;
    private double topSpeed;
    private int startYear;
    private double fuelCost;
    private int availabilityYear;
    private double acquisitionPrice;
    private double maintenancePrice;

    public Locomotive(String nameID, String owner, String type, double power, double acceleration,
                     double topSpeed, int startYear, double fuelCost, int availabilityYear,
                     double acquisitionPrice, double maintenancePrice) {
        this.nameID = nameID;
        this.owner = owner;
        this.type = type;
        this.power = power;
        this.acceleration = acceleration;
        this.topSpeed = topSpeed;
        this.startYear = startYear;
        this.fuelCost = fuelCost;
        this.availabilityYear = availabilityYear;
        this.acquisitionPrice = acquisitionPrice;
        this.maintenancePrice = maintenancePrice;
    }

    // Getters
    public String getNameID() {
        return nameID;
    }

    public String getOwner() {
        return owner;
    }

    public String getType() {
        return type;
    }

    public double getPower() {
        return power;
    }

    public double getAcceleration() {
        return acceleration;
    }

    public double getTopSpeed() {
        return topSpeed;
    }

    public int getStartYear() {
        return startYear;
    }

    public double getFuelCost() {
        return fuelCost;
    }

    public int getAvailabilityYear() {
        return availabilityYear;
    }

    public double getAcquisitionPrice() {
        return acquisitionPrice;
    }

    public double getMaintenancePrice() {
        return maintenancePrice;
    }
} 