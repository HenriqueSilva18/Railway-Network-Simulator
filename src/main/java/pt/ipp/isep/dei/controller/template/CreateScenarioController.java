package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.*;
import pt.ipp.isep.dei.repository.template.*;

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

    public List<Locomotive> getAvailableLocomotives(List<String> selectedLocomotiveTypes, Date endDate) {
        return locomotiveRepository.getAvailableLocomotives(selectedLocomotiveTypes, endDate);
    }

    public Scenario createScenario(String nameID, Map selectedMap, Date startDate, Date endDate,
                                 List<Industry> selectedIndustries, List<Cargo> portImports,
                                 List<Cargo> portExports, List<Cargo> portProduces,
                                 double genIndustryFactors, List<String> selectedLocomotiveTypes,
                                 List<City> mapCityList, float cityTrafficRates) {
        Editor editor = getEditorFromSession();
        if (editor == null) {
            throw new IllegalStateException("No editor found in session");
        }

        List<Locomotive> availableLocomotives = getAvailableLocomotives(selectedLocomotiveTypes, endDate);
        Scenario scenario = new Scenario(nameID, editor, startDate, endDate,
                selectedIndustries, availableLocomotives, mapCityList);

        // Configure ports
        for (Industry industry : selectedIndustries) {
            if (industry.getType().equals("Port")) {
                scenario.configurePort(industry, portImports, portExports, portProduces);
            }
        }

        // Configure generating industries
        for (Industry industry : selectedIndustries) {
            if (industry.getType().equals("Primary")) {
                scenario.configureGeneratingIndustry(industry, genIndustryFactors);
            }
        }

        // Configure cities
        for (City city : mapCityList) {
            scenario.configureCity(city, cityTrafficRates);
        }

        scenario.setMap(selectedMap);
        editorRepository.addScenarioToEditor(editor, scenario);
        return scenario;
    }
} 