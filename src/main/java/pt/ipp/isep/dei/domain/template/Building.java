package pt.ipp.isep.dei.domain.template;

import java.util.Objects;

public class Building {
    private final String nameID;
    private final String type;
    private final int availabilityYear;
    private final double cost;
    private final String effect;
    private final String replacesBuilding;
    private final boolean isMutuallyExclusive;
    private final String mutuallyExclusiveWith;

    public Building(String nameID, String type, int availabilityYear, double cost, String effect) {
        this(nameID, type, availabilityYear, cost, effect, null, false, null);
    }

    public Building(String nameID, String type, int availabilityYear, double cost, String effect, 
                    String replacesBuilding, boolean isMutuallyExclusive, String mutuallyExclusiveWith) {
        if (nameID == null || type == null || effect == null) {
            throw new IllegalArgumentException("Building parameters cannot be null");
        }
        
        this.nameID = nameID;
        this.type = type;
        this.availabilityYear = availabilityYear;
        this.cost = cost;
        this.effect = effect;
        this.replacesBuilding = replacesBuilding;
        this.isMutuallyExclusive = isMutuallyExclusive;
        this.mutuallyExclusiveWith = mutuallyExclusiveWith;
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

    public double getCost() {
        return cost;
    }

    public String getEffect() {
        return effect;
    }

    public String getReplacesBuilding() {
        return replacesBuilding;
    }

    public boolean isMutuallyExclusive() {
        return isMutuallyExclusive;
    }

    public String getMutuallyExclusiveWith() {
        return mutuallyExclusiveWith;
    }

    /**
     * Returns a data transfer object with the building information
     */
    public BuildingInfo getInfo() {
        return new BuildingInfo(nameID, type, availabilityYear, cost, effect);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Building building = (Building) o;
        return Objects.equals(nameID, building.nameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameID);
    }

    /**
     * DTO class to hold building information
     */
    public static class BuildingInfo {
        private final String nameID;
        private final String type;
        private final int availabilityYear;
        private final double cost;
        private final String effect;

        public BuildingInfo(String nameID, String type, int availabilityYear, double cost, String effect) {
            this.nameID = nameID;
            this.type = type;
            this.availabilityYear = availabilityYear;
            this.cost = cost;
            this.effect = effect;
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

        public double getCost() {
            return cost;
        }

        public String getEffect() {
            return effect;
        }
    }
} 