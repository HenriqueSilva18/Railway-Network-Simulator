package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        return ApplicationSession.getInstance().getCurrentEditor();
    }

    private List<Locomotive> getAvailableLocomotives(List<String> selectedTypes, Date endDate) {
        List<Locomotive> availableLocomotives = new ArrayList<>();
        // Add locomotives based on selected types and availability date
        for (String type : selectedTypes) {
            // Default values for each locomotive type
            double power = 0;
            double acceleration = 0;
            double topSpeed = 0;
            double fuelCost = 0;
            double acquisitionPrice = 0;
            double maintenancePrice = 0;
            int startYear = 1900;
            
            // Set specific values based on locomotive type
            switch (type) {
                case "Steam":
                    power = 1000;
                    acceleration = 0.5;
                    topSpeed = 80;
                    fuelCost = 50;
                    acquisitionPrice = 10000;
                    maintenancePrice = 1000;
                    startYear = 1850;
                    break;
                case "Diesel":
                    power = 2000;
                    acceleration = 1.0;
                    topSpeed = 120;
                    fuelCost = 30;
                    acquisitionPrice = 20000;
                    maintenancePrice = 2000;
                    startYear = 1920;
                    break;
                case "Electric":
                    power = 3000;
                    acceleration = 1.5;
                    topSpeed = 160;
                    fuelCost = 20;
                    acquisitionPrice = 30000;
                    maintenancePrice = 3000;
                    startYear = 1950;
                    break;
            }
            
            // Create locomotive with the correct parameters
            Locomotive locomotive = new Locomotive(
                type + "_" + startYear,  // nameID
                "Railway Co.",           // owner
                type,                    // type
                power,                   // power
                acceleration,            // acceleration
                topSpeed,                // topSpeed
                startYear,               // startYear
                fuelCost,                // fuelCost
                startYear,               // availabilityYear
                acquisitionPrice,        // acquisitionPrice
                maintenancePrice         // maintenancePrice
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

        // Add the scenario to the editor's collection
        editorRepository.addScenarioToEditor(editor, scenario);

        return scenario;
    }
} 