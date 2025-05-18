package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Building;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuildingRepository {

    private final Map<String, Building> buildings = new HashMap<>();
    
    public Building getBuilding(String buildingId) {
        return buildings.get(buildingId);
    }
    
    public List<Building> getAllBuildings() {
        return new ArrayList<>(buildings.values());
    }
    
    public boolean add(Building building) {
        if (building == null || buildings.containsKey(building.getNameID())) {
            return false;
        }
        
        buildings.put(building.getNameID(), building);
        return true;
    }
    
    public boolean save(Building building) {
        if (building == null) {
            return false;
        }
        
        buildings.put(building.getNameID(), building);
        return true;
    }
    
    public boolean remove(String buildingId) {
        if (buildingId == null || !buildings.containsKey(buildingId)) {
            return false;
        }
        
        buildings.remove(buildingId);
        return true;
    }
    
    public int size() {
        return buildings.size();
    }
    
    public void initialize() {
        // Create telegraph (early communication)
        add(new Building("telegraph", "Communication", 1900, 5000, 
                "Improves train coordination by 10%"));
        
        // Create telephone (replaces telegraph)
        add(new Building("telephone", "Communication", 1920, 10000, 
                "Improves train coordination by 20%", "telegraph", false, null));
        
        // Create small cafe
        add(new Building("small_cafe", "Passenger Service", 1900, 3000, 
                "Increases passenger satisfaction by 5%", null, true, "large_cafe"));
        
        // Create large cafe
        add(new Building("large_cafe", "Passenger Service", 1910, 8000, 
                "Increases passenger satisfaction by 15%", null, true, "small_cafe"));
        
        // Create customs office
        add(new Building("customs", "Administrative", 1900, 12000, 
                "Enables international cargo handling"));
        
        // Create post office
        add(new Building("post_office", "Administrative", 1900, 7000, 
                "Generates additional mail cargo"));
        
        // Create small hotel
        add(new Building("small_hotel", "Passenger Service", 1910, 15000, 
                "Increases passenger traffic by 10%", null, true, "large_hotel"));
        
        // Create large hotel
        add(new Building("large_hotel", "Passenger Service", 1930, 30000, 
                "Increases passenger traffic by 25%", null, true, "small_hotel"));
        
        // Create silo (for grain storage)
        add(new Building("silo", "Cargo Storage", 1900, 20000, 
                "Extends grain cargo lifespan by 50%"));
        
        // Create liquid storage
        add(new Building("liquid_storage", "Cargo Storage", 1915, 25000, 
                "Enables liquid cargo storage"));
    }
} 