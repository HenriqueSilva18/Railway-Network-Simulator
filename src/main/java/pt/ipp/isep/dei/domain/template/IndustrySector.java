package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class IndustrySector {
    private String name;
    private boolean importsExports;
    private List<Cargo> generatesCargo;
    private List<Cargo> consumesCargo;
    private String description;

    public IndustrySector(String name, boolean importsExports, String description) {
        this.name = name;
        this.importsExports = importsExports;
        this.description = description;
        this.generatesCargo = new ArrayList<>();
        this.consumesCargo = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public boolean isImportsExports() {
        return importsExports;
    }

    public List<Cargo> getGeneratesCargo() {
        return new ArrayList<>(generatesCargo);
    }

    public List<Cargo> getConsumesCargo() {
        return new ArrayList<>(consumesCargo);
    }

    public String getDescription() {
        return description;
    }

    public void addGeneratesCargo(Cargo cargo) {
        if (cargo != null) {
            generatesCargo.add(cargo);
        }
    }

    public void addConsumesCargo(Cargo cargo) {
        if (cargo != null) {
            consumesCargo.add(cargo);
        }
    }
} 