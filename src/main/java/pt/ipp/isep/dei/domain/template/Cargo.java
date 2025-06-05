package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Cargo {
    private String name;
    private int amount;
    private int lifespan;
    private String type;
    private List<String> productionResources;
    private double baseValue;
    private Date creationDate;

    public Cargo(String name, int amount, String type) {
        this.name = name;
        this.amount = amount;
        this.type = type;
        this.lifespan = 10; // Default lifespan in days
        this.productionResources = new ArrayList<>();
        this.baseValue = calculateBaseValue();
        this.creationDate = new Date(); // Set current date as creation date
    }

    public Cargo(String name, int amount, int lifespan, String type) {
        this.name = name;
        this.amount = amount;
        this.lifespan = lifespan;
        this.type = type;
        this.productionResources = new ArrayList<>();
        this.baseValue = calculateBaseValue();
        this.creationDate = new Date(); // Set current date as creation date
    }

    public Cargo(String name, int amount, int lifespan, String type, List<String> productionResources) {
        this.name = name;
        this.amount = amount;
        this.lifespan = lifespan;
        this.type = type;
        this.productionResources = new ArrayList<>(productionResources);
        this.baseValue = calculateBaseValue();
        this.creationDate = new Date(); // Set current date as creation date
    }

    public String getName() {
        return name;
    }

    public int getAmount() {
        return amount;
    }

    public int getLifespan() {
        return lifespan;
    }

    public String getType() {
        return type;
    }

    public List<String> getProductionResources() {
        return new ArrayList<>(productionResources);
    }

    public void setAmount(int amount) {
        if (amount >= 0) {
            this.amount = amount;
        }
    }

    public void addAmount(int amount) {
        if (amount > 0) {
            this.amount += amount;
        }
    }

    public boolean addProductionResource(String resource) {
        if (resource != null && !resource.isEmpty()) {
            return productionResources.add(resource);
        }
        return false;
    }

    public double getBaseValue() {
        return baseValue;
    }

    public Date getCreationDate() {
        return creationDate != null ? (Date) creationDate.clone() : null;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate != null ? (Date) creationDate.clone() : null;
    }

    private double calculateBaseValue() {
        // Base value calculation based on cargo type
        switch (type.toLowerCase()) {
            case "raw material":
                return 100.0;
            case "processed material":
                return 200.0;
            case "people":
                return 150.0;
            case "communication":
                return 120.0;
            case "consumable":
                return 80.0;
            default:
                return 50.0;
        }
    }

    @Override
    public String toString() {
        return name + " (" + amount + " tons, type: " + type + ")";
    }

    public String getDetails() {
        return String.format("%s (%s)\n" +
                "Amount: %d units\n" +
                "Lifespan: %d days\n" +
                "Type: %s\n" +
                "Base Value: %.2f\n" +
                "Creation Date: %s",
                name, type, amount, lifespan, type, baseValue, 
                creationDate != null ? creationDate.toString() : "Not set");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Cargo cargo = (Cargo) obj;
        return name != null && name.equals(cargo.name) && type != null && type.equals(cargo.type);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
} 