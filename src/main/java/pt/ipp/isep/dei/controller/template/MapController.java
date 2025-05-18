package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Map;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.domain.template.Position;
import pt.ipp.isep.dei.domain.template.Industry;
import pt.ipp.isep.dei.domain.template.City;
import pt.ipp.isep.dei.repository.template.MapRepository;
import pt.ipp.isep.dei.repository.template.EditorRepository;
import pt.ipp.isep.dei.repository.template.Repositories;

import java.util.List;
import java.util.Optional;
import java.util.HashMap;

public class MapController {
    private final MapRepository mapRepository;
    private final EditorRepository editorRepository;

    public MapController() {
        this.mapRepository = Repositories.getInstance().getMapRepository();
        this.editorRepository = Repositories.getInstance().getEditorRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public List<String> getMapScenarios(String mapID) {
        Map map = mapRepository.getMap(mapID);
        return map != null ? map.getScenarios() : List.of();
    }

    public boolean loadMap(String mapID, String scenarioID) {
        Map map = mapRepository.getMap(mapID);
        if (map == null) {
            System.out.println("DEBUG: Map not found: " + mapID);
            return false;
        }

        System.out.println("DEBUG: Loading map: " + mapID + ", scenario: " + scenarioID);
        System.out.println("DEBUG: Initial map industries: " + map.getIndustries().size());
        for (Industry ind : map.getIndustries()) {
            System.out.println("DEBUG: Initial industry: " + ind.getNameID() + " at (" + 
                             ind.getPosition().getX() + "," + ind.getPosition().getY() + ")");
        }

        // Set current map in application session
        ApplicationSession.getInstance().setCurrentMap(map);
        
        // Print all available scenarios for debugging
        System.out.println("DEBUG: All scenarios in EditorRepository:");
        List<Scenario> allScenarios = editorRepository.getAllScenarios();
        for (Scenario s : allScenarios) {
            System.out.println("DEBUG: Found scenario: " + s.getNameID() + " for map: " + 
                              (s.getMap() != null ? s.getMap().getNameID() : "null"));
        }
        
        // Find the scenario by name using the new method
        Optional<Scenario> scenario = editorRepository.findScenarioByNameID(scenarioID);

        if (scenario.isPresent()) {
            // Found scenario by name, now load it
            Scenario foundScenario = scenario.get();
            System.out.println("DEBUG: Scenario found: " + foundScenario.getNameID());
            
            // Store the scenario in the application session
            ApplicationSession.getInstance().setCurrentScenario(foundScenario);
            
            // Get scenario data first before clearing the map
            List<City> cities = foundScenario.getTweakedCityList();
            List<Industry> industries = foundScenario.getAvailableIndustryList();
            
            System.out.println("DEBUG: Scenario industries: " + industries.size());
            for (Industry ind : industries) {
                System.out.println("DEBUG: Scenario industry: " + ind.getNameID() + " at (" + 
                                 ind.getPosition().getX() + "," + ind.getPosition().getY() + ")");
            }
            
            // Clear existing map contents - remove ALL industries and cities
            map.getCities().clear();
            map.getIndustries().clear();
            
            // Track which unique industries we've already added (by nameID and position)
            HashMap<String, Boolean> addedIndustries = new HashMap<>();
            
            // Add industries with direct position insertion - ONLY add each unique nameID+position once
            for (Industry industry : industries) {
                String key = industry.getNameID() + "@" + industry.getPosition().getX() + "," + industry.getPosition().getY();
                
                if (addedIndustries.containsKey(key)) {
                    System.out.println("DEBUG: Skipping duplicate industry: " + key);
                    continue;
                }
                
                // Create a clean industry object with same properties
                Industry newIndustry = new Industry(
                    industry.getNameID(),
                    industry.getType(),
                    industry.getSector(),
                    industry.getAvailabilityYear(),
                    new Position(industry.getPosition().getX(), industry.getPosition().getY())
                );
                
                // Set additional properties
                newIndustry.setProductionRate(industry.getProductionRate());
                newIndustry.setImportedCargo(industry.getImportedCargo());
                newIndustry.setExportedCargo(industry.getExportedCargo());
                newIndustry.setProducedCargo(industry.getProducedCargo());
                
                // Add to map
                map.addIndustry(newIndustry);
                addedIndustries.put(key, true);
                
                // Debug information
                System.out.println("DEBUG: Added industry: " + newIndustry.getNameID() + 
                                 " at position (" + newIndustry.getPosition().getX() + 
                                 "," + newIndustry.getPosition().getY() + ")");
            }
            
            // Track which unique cities we've already added (by nameID and position)
            HashMap<String, Boolean> addedCities = new HashMap<>();
            
            // Add cities with direct position insertion - ONLY add each unique nameID+position once
            for (City city : cities) {
                String key = city.getNameID() + "@" + city.getPosition().getX() + "," + city.getPosition().getY();
                
                if (addedCities.containsKey(key)) {
                    System.out.println("DEBUG: Skipping duplicate city: " + key);
                    continue;
                }
                
                City newCity = new City(
                    city.getNameID(), 
                    new Position(city.getPosition().getX(), city.getPosition().getY()), 
                    city.getHouseBlocks()
                );
                newCity.setTrafficRate(city.getTrafficRate());
                map.addCity(newCity);
                addedCities.put(key, true);
            }
            
            System.out.println("DEBUG: Final map industries: " + map.getIndustries().size());
            System.out.println("DEBUG: Final map cities: " + map.getCities().size());
            System.out.println("Map and scenario loaded successfully.");
            
            return true;
        } else {
            System.out.println("DEBUG: No scenario found with ID: " + scenarioID);
            System.out.println("DEBUG: Using map directly with existing elements");
        }
        
        // If no scenario found by name, try loading using the map's loadScenario method
        return map.loadScenario(scenarioID);
    }
} 