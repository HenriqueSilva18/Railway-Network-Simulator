package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Industry {
    private String nameID;
    private String type;
    private int availabilityYear;
    private double productionRate;
    private List<Cargo> suppliedCargo;
    private List<Cargo> demandedCargo;
    private Position position;

    public Industry(String nameID, String type, int availabilityYear, double productionRate) {
        this.nameID = nameID;
        this.type = type;
        this.availabilityYear = availabilityYear;
        this.productionRate = productionRate;
        this.suppliedCargo = new ArrayList<>();
        this.demandedCargo = new ArrayList<>();
    }

    public static Industry create(String nameID, int x, int y) {
        Industry industry = new Industry(nameID, "Primary", 1900, 100.0);
        industry.setPosition(new Position(x, y));
        return industry;
    }

    public String getNameID() {
        return nameID;
    }

    public String getType() {
        return type;
    }

    public int getAvailabilityYear() {
        return availabilityYear;
    }

    public double getProductionRate() {
        return productionRate;
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

    public void setPosition(Position position) {
        this.position = position;
    }

    public void addSuppliedCargo(Cargo cargo) {
        if (cargo != null) {
            suppliedCargo.add(cargo);
        }
    }

    public void addDemandedCargo(Cargo cargo) {
        if (cargo != null) {
            demandedCargo.add(cargo);
        }
    }
} 