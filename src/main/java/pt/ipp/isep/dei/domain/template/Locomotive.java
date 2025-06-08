package pt.ipp.isep.dei.domain.template;

public class Locomotive {
    private String nameID;
    private String owner;
    private String type;
    private int power;
    private double acceleration;
    private int topSpeed;
    private int startYear;
    private double fuelCost;
    private int availabilityYear;
    private double acquisitionPrice;
    private double maintenancePrice;
    private boolean available;
    
    public Locomotive(String nameID, String owner, String type, double power, double acceleration,
                      double topSpeed, int startYear, double fuelCost, int availabilityYear,
                      double acquisitionPrice, double maintenancePrice) {
        this.nameID = nameID;
        this.owner = owner;
        this.type = type;
        this.power = (int)power;
        this.acceleration = acceleration;
        this.topSpeed = (int)topSpeed;
        this.startYear = startYear;
        this.fuelCost = fuelCost;
        this.availabilityYear = availabilityYear;
        this.acquisitionPrice = acquisitionPrice;
        this.maintenancePrice = maintenancePrice;
        this.available = false;
    }
    
    public Locomotive(String nameID, String type, int power, int topSpeed, int availabilityYear, double acquisitionPrice) {
        this.nameID = nameID;
        this.type = type;
        this.power = power;
        this.topSpeed = topSpeed;
        this.availabilityYear = availabilityYear;
        this.acquisitionPrice = acquisitionPrice;
        
        // Default values
        this.owner = "";
        this.acceleration = 1.0;
        this.startYear = availabilityYear;
        this.fuelCost = 100.0; // Default fuel cost
        this.maintenancePrice = acquisitionPrice * 0.01; // 1% of acquisition price
        this.available = false;
    }
    
    public String getNameID() {
        return nameID;
    }
    
    public String getOwner() {
        return owner;
    }
    
    public String getType() {
        return type;
    }
    
    public int getPower() {
        return power;
    }
    
    public double getAcceleration() {
        return acceleration;
    }
    
    public int getTopSpeed() {
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

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
    
    // Methods for US09
    public double getPrice() {
        return acquisitionPrice;
    }
    
    public void setOwner(String owner) {
        this.owner = owner;
    }
    
    public boolean setOwner(Player player) {
        if (player == null) {
            return false;
        }
        this.owner = player.getUsername();
        return true;
    }
    
    public String getDetails() {
        return String.format("%s (%s) - Power: %d, Top Speed: %d km/h, Year: %d", 
            nameID, type, power, topSpeed, startYear);
    }
    
    @Override
    public String toString() {
        return nameID + " (" + type + ")";
    }
} 