package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;

public class Carriage implements Serializable {
    private static final long serialVersionUID = 1L;
    
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
        if (cargo == null || cargo.getAmount() > storageCapacity) {
            return false;
        }
        
        this.cargo = cargo;
        return true;
    }

    public Cargo unloadCargo() {
        Cargo unloadedCargo = this.cargo;
        this.cargo = null;
        return unloadedCargo;
    }

    public boolean isLoaded() {
        return cargo != null;
    }

    public boolean hasCapacityFor(Cargo cargo) {
        return cargo != null && cargo.getAmount() <= storageCapacity;
    }

    @Override
    public String toString() {
        return "Carriage (capacity: " + storageCapacity + (isLoaded() ? ", loaded with: " + cargo.getName() : ", empty") + ")";
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