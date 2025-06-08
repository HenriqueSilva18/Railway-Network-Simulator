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
    private final String evolvesInto;
    private final double evolutionCost;
    private final boolean canEvolve;
    private final double revenueMultiplier;

    public Building(String nameID, String type, int availabilityYear, double cost, String effect) {
        this(nameID, type, availabilityYear, cost, effect, null, false, null, null, 0, false, 0.0);
    }

    public Building(String nameID, String type, int availabilityYear, double cost, String effect, 
                    String replacesBuilding, boolean isMutuallyExclusive, String mutuallyExclusiveWith,
                    String evolvesInto, double evolutionCost, boolean canEvolve) {
        this(nameID, type, availabilityYear, cost, effect, replacesBuilding, isMutuallyExclusive, 
             mutuallyExclusiveWith, evolvesInto, evolutionCost, canEvolve, 0.0);
    }

    public Building(String nameID, String type, int availabilityYear, double cost, String effect, 
                    String replacesBuilding, boolean isMutuallyExclusive, String mutuallyExclusiveWith,
                    String evolvesInto, double evolutionCost, boolean canEvolve, double revenueMultiplier) {
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
        this.evolvesInto = evolvesInto;
        this.evolutionCost = evolutionCost;
        this.canEvolve = canEvolve;
        this.revenueMultiplier = revenueMultiplier;
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
    
    public String getEvolvesInto() {
        return evolvesInto;
    }
    
    public double getEvolutionCost() {
        return evolutionCost;
    }
    
    public boolean canEvolve() {
        return canEvolve;
    }
    
    public boolean canEvolveTo(String buildingId) {
        return canEvolve && evolvesInto != null && evolvesInto.equals(buildingId);
    }

    public double getRevenueMultiplier() {
        return revenueMultiplier;
    }

    /**
     * Returns a data transfer object with the building information
     */
    public BuildingInfo getInfo() {
        return new BuildingInfo(nameID, type, availabilityYear, cost, effect, evolvesInto, evolutionCost, canEvolve, revenueMultiplier);
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
        private final String evolvesInto;
        private final double evolutionCost;
        private final boolean canEvolve;
        private final double revenueMultiplier;

        public BuildingInfo(String nameID, String type, int availabilityYear, double cost, String effect) {
            this(nameID, type, availabilityYear, cost, effect, null, 0, false, 0.0);
        }
        
        public BuildingInfo(String nameID, String type, int availabilityYear, double cost, String effect,
                          String evolvesInto, double evolutionCost, boolean canEvolve) {
            this(nameID, type, availabilityYear, cost, effect, evolvesInto, evolutionCost, canEvolve, 0.0);
        }
        
        public BuildingInfo(String nameID, String type, int availabilityYear, double cost, String effect,
                          String evolvesInto, double evolutionCost, boolean canEvolve, double revenueMultiplier) {
            this.nameID = nameID;
            this.type = type;
            this.availabilityYear = availabilityYear;
            this.cost = cost;
            this.effect = effect;
            this.evolvesInto = evolvesInto;
            this.evolutionCost = evolutionCost;
            this.canEvolve = canEvolve;
            this.revenueMultiplier = revenueMultiplier;
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
        
        public String getEvolvesInto() {
            return evolvesInto;
        }
        
        public double getEvolutionCost() {
            return evolutionCost;
        }
        
        public boolean canEvolve() {
            return canEvolve;
        }
        
        public double getRevenueMultiplier() {
            return revenueMultiplier;
        }
    }
} 