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
        if (resource != null && !resource.isEmpty()) {
            productionResources.add(resource);
        }
    }
} 