package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

public class CreateScenarioController {
    private final MapRepository mapRepository;
    private final EditorRepository editorRepository;
    private final LocomotiveRepository locomotiveRepository;
    private final CargoRepository cargoRepository;

    public CreateScenarioController() {
        Repositories repositories = Repositories.getInstance();
        this.mapRepository = repositories.getMapRepository();
        this.editorRepository = repositories.getEditorRepository();
        this.locomotiveRepository = repositories.getLocomotiveRepository();
        this.cargoRepository = repositories.getCargoRepository();
    }

    public List<Map> getAvailableMaps() {
        return mapRepository.getAvailableMaps();
    }

    public Map getMapByID(String mapID) {
        return mapRepository.getMap(mapID);
    }

    public List<Industry> getMapIndustries(Map selectedMap) {
        return selectedMap.getIndustries();
    }

    public List<City> getMapCities(Map selectedMap) {
        return selectedMap.getCities();
    }

    public List<String> getLocomotiveTypes() {
        return locomotiveRepository.getLocomotiveTypes();
    }

    public List<Cargo> getCargoList() {
        return cargoRepository.getCargoList();
    }

    private Editor getEditorFromSession() {
        Editor editor = ApplicationSession.getInstance().getCurrentEditor();
        if (editor == null) {
            editor = new Editor("default_editor", "Default Editor");
            ApplicationSession.getInstance().setCurrentEditor(editor);
        }
        
        // Make sure the editor exists in the repository
        if (editorRepository.getEditorByUsername(editor.getUsername()) == null) {
            editorRepository.addEditor(editor);
        }
        
        return editor;
    }

    private List<Locomotive> getAvailableLocomotives(List<String> selectedTypes, Date endDate) {
        List<Locomotive> availableLocomotives = new ArrayList<>();
        
        // Base values for each type
        int power = 1000;  // Base power in horsepower
        int topSpeed = 60;  // Base top speed in km/h
        double acquisitionPrice = 10000;  // Base acquisition price
        
        // Get the end year for availability calculations
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(endDate);
        int endYear = calendar.get(Calendar.YEAR);
        
        for (String type : selectedTypes) {
            // Adjust values based on locomotive type
            int startYear;
            switch (type.toLowerCase()) {
                case "steam":
                    startYear = 1900;
                    power = 1000;
                    topSpeed = 60;
                    acquisitionPrice = 10000.0;
                    break;
                case "diesel":
                    startYear = 1925;
                    power = 1500;
                    topSpeed = 80;
                    acquisitionPrice = 20000.0;
                    break;
                case "electric":
                    startYear = 1950;
                    power = 2000;
                    topSpeed = 100;
                    acquisitionPrice = 30000.0;
                    break;
                default:
                    continue;
            }
            
            // Skip if the locomotive type is not available in the scenario's time period
            if (startYear > endYear) continue;
            
            // Use the constructor with correct parameters
            Locomotive locomotive = new Locomotive(
                type + "_" + startYear,  // nameID
                type,                    // type
                power,                   // power
                topSpeed,                // topSpeed
                startYear,               // availabilityYear
                acquisitionPrice         // acquisitionPrice
            );
            availableLocomotives.add(locomotive);
        }
        return availableLocomotives;
    }

    public Scenario createScenario(String nameID, String displayName, Map selectedMap, Date startDate, Date endDate,
                                 List<Industry> selectedIndustries, List<Cargo> portImports,
                                 List<Cargo> portExports, List<Cargo> portProduces,
                                 double genIndustryFactors, List<String> selectedLocomotiveTypes,
                                 List<City> mapCityList, float cityTrafficRates) {
        Editor editor = getEditorFromSession();
        if (editor == null) {
            throw new IllegalStateException("No editor found in session");
        }

        // Create deep copies of cities and industries to avoid modifying the originals
        List<City> scenarioCities = new ArrayList<>();
        for (City city : mapCityList) {
            City newCity = new City(city.getNameID(), new Position(city.getPosition().getX(), city.getPosition().getY()), 
                                  new ArrayList<>(city.getHouseBlocks()));
            newCity.setTrafficRate(cityTrafficRates);
            scenarioCities.add(newCity);
        }

        List<Industry> scenarioIndustries = new ArrayList<>();
        for (Industry industry : selectedIndustries) {
            Industry newIndustry = new Industry(
                industry.getNameID(),
                industry.getType(),
                industry.getSector(),
                industry.getAvailabilityYear(),
                new Position(industry.getPosition().getX(), industry.getPosition().getY())
            );
            newIndustry.setProductionRate(genIndustryFactors);
            
            if (industry.getType().equals("Port")) {
                newIndustry.setImportedCargo(new ArrayList<>(portImports));
                newIndustry.setExportedCargo(new ArrayList<>(portExports));
                newIndustry.setProducedCargo(new ArrayList<>(portProduces));
            }
            
            scenarioIndustries.add(newIndustry);
        }

        List<Locomotive> availableLocomotives = getAvailableLocomotives(selectedLocomotiveTypes, endDate);

        // Create the scenario with the copied objects
        Scenario scenario = new Scenario(nameID, displayName, editor, startDate, endDate,
                scenarioIndustries, availableLocomotives, scenarioCities);

        // Set the map reference
        scenario.setMap(selectedMap);
        
        // Set the current scenario in the application session
        ApplicationSession.getInstance().setCurrentScenario(scenario);

        // Add the scenario to the editor's collection
        editorRepository.addScenarioToEditor(editor, scenario);

        // Add the scenario to the map's list of scenarios
        selectedMap.addScenario(nameID);
        
        // Save the updated map to the repository
        mapRepository.save(selectedMap);

        return scenario;
    }
} 