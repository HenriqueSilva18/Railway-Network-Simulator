package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Cargo {
    private String name;
    private int amount;
    private int lifespan;
    private String type;
    private List<String> productionResources;

    public Cargo(String name, int amount, int lifespan, String type) {
        this.name = name;
        this.amount = amount;
        this.lifespan = lifespan;
        this.type = type;
        this.productionResources = new ArrayList<>();
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

    public void addProductionResource(String resource) {
        if (resource != null && !resource.trim().isEmpty()) {
            productionResources.add(resource);
        }
    }

    public String getDetails() {
        return String.format("%s (%s)\n" +
                "Amount: %d units\n" +
                "Lifespan: %d days\n" +
                "Type: %s",
                name, type, amount, lifespan, type);
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