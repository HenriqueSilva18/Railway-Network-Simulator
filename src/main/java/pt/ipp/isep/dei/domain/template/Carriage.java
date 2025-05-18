package pt.ipp.isep.dei.domain.template;

public class Carriage {
    private int storageCapacity;
    private Cargo cargo;

    public Carriage(int storageCapacity) {
        this.storageCapacity = storageCapacity;
        this.cargo = null;
    }

    public int getStorageCapacity() {
        return storageCapacity;
    }

    public Cargo getCargo() {
        return cargo;
    }

    public boolean loadCargo(Cargo cargo) {
        if (cargo == null) return false;
        if (this.cargo != null) return false;
        if (cargo.getAmount() > storageCapacity) return false;
        
        this.cargo = cargo;
        return true;
    }

    public Cargo unloadCargo() {
        Cargo unloadedCargo = this.cargo;
        this.cargo = null;
        return unloadedCargo;
    }

    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append(String.format("Storage Capacity: %d units\n", storageCapacity));
        if (cargo != null) {
            details.append("Loaded Cargo: ").append(cargo.getDetails());
        } else {
            details.append("Empty");
        }
        return details.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Carriage carriage = (Carriage) obj;
        return storageCapacity == carriage.storageCapacity;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(storageCapacity);
    }
} 