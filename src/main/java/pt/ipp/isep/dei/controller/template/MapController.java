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
            return false;
        }

        // Set current map in application session
        ApplicationSession.getInstance().setCurrentMap(map);
        
        // Find the scenario by name using the new method
        Optional<Scenario> scenario = editorRepository.findScenarioByNameID(scenarioID);

        if (scenario.isPresent()) {
            // Found scenario by name, now load it
            Scenario foundScenario = scenario.get();
            
            // Store the scenario in the application session
            ApplicationSession.getInstance().setCurrentScenario(foundScenario);
            
            // Get scenario data first before clearing the map
            List<City> cities = foundScenario.getTweakedCityList();
            List<Industry> industries = foundScenario.getAvailableIndustryList();
            
            // Clear existing map contents - remove ALL industries and cities
            map.getCities().clear();
            map.getIndustries().clear();
            
            // Track which unique industries we've already added (by nameID and position)
            HashMap<String, Boolean> addedIndustries = new HashMap<>();
            
            // Add industries with direct position insertion - ONLY add each unique nameID+position once
            for (Industry industry : industries) {
                String key = industry.getNameID() + "@" + industry.getPosition().getX() + "," + industry.getPosition().getY();
                
                if (addedIndustries.containsKey(key)) {
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
            }
            
            // Track which unique cities we've already added (by nameID and position)
            HashMap<String, Boolean> addedCities = new HashMap<>();
            
            // Add cities with direct position insertion - ONLY add each unique nameID+position once
            for (City city : cities) {
                String key = city.getNameID() + "@" + city.getPosition().getX() + "," + city.getPosition().getY();
                
                if (addedCities.containsKey(key)) {
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
            
            System.out.println("Map and scenario loaded successfully.");
            
            return true;
        }
        
        // If no scenario found by name, try loading using the map's loadScenario method
        return map.loadScenario(scenarioID);
    }
} 