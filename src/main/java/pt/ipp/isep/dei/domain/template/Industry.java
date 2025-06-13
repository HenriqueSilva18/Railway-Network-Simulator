package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Industry implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String nameID;
    private String type;
    private String sector;
    private int availabilityYear;
    private double productionRate;
    private List<Cargo> suppliedCargo;
    private List<Cargo> demandedCargo;
    private List<Cargo> importedCargo;
    private List<Cargo> exportedCargo;
    private List<Cargo> producedCargo;
    private Position position;

    public Industry(String nameID, String type, String sector, int availabilityYear, Position position) {
        this.nameID = nameID;
        this.type = type;
        this.sector = sector;
        this.availabilityYear = availabilityYear;
        this.position = position;
        this.suppliedCargo = new ArrayList<>();
        this.demandedCargo = new ArrayList<>();
        this.importedCargo = new ArrayList<>();
        this.exportedCargo = new ArrayList<>();
        this.producedCargo = new ArrayList<>();
    }

    public static Industry create(String nameID, int x, int y) {
        // Use nameID as the type and default to Primary sector for new industries
        return new Industry(nameID, nameID, "Primary", 1900, new Position(x, y));
    }

    public String getNameID() {
        return nameID;
    }

    public String getType() {
        return type;
    }

    public String getSector() {
        return sector;
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

    public List<Cargo> getImportedCargo() {
        return new ArrayList<>(importedCargo);
    }

    public List<Cargo> getExportedCargo() {
        return new ArrayList<>(exportedCargo);
    }

    public List<Cargo> getProducedCargo() {
        return new ArrayList<>(producedCargo);
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

    public void setProductionRate(double productionRate) {
        this.productionRate = productionRate;
    }

    public void setImportedCargo(List<Cargo> importedCargo) {
        this.importedCargo = new ArrayList<>(importedCargo);
    }

    public void setExportedCargo(List<Cargo> exportedCargo) {
        this.exportedCargo = new ArrayList<>(exportedCargo);
    }

    public void setProducedCargo(List<Cargo> producedCargo) {
        this.producedCargo = new ArrayList<>(producedCargo);
    }
} 