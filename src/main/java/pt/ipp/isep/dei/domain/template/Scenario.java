package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Scenario {
    private String nameID;
    private Editor editor;
    private Date startDate;
    private Date endDate;
    private List<City> tweakedCityList;
    private List<Industry> availableIndustryList;
    private List<Locomotive> availableLocomotives;
    private Map map;

    public Scenario(String nameID, Editor editor, Date startDate, Date endDate,
                   List<Industry> selectedIndustries, List<Locomotive> availableLocomotives,
                   List<City> mapCityList) {
        this.nameID = nameID;
        this.editor = editor;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableIndustryList = new ArrayList<>(selectedIndustries);
        this.availableLocomotives = new ArrayList<>(availableLocomotives);
        this.tweakedCityList = new ArrayList<>(mapCityList);
    }

    public boolean configurePort(Industry port, List<Cargo> portImports,
                               List<Cargo> portExports, List<Cargo> portProduces) {
        if (port == null || !availableIndustryList.contains(port)) {
            return false;
        }
        // Configure port's cargo operations
        port.setImportedCargo(portImports);
        port.setExportedCargo(portExports);
        port.setProducedCargo(portProduces);
        return true;
    }

    public boolean configureGeneratingIndustry(Industry industry, double genIndustryFactors) {
        if (industry == null || !availableIndustryList.contains(industry)) {
            return false;
        }
        // Configure industry's generation factors
        industry.setProductionRate(genIndustryFactors);
        return true;
    }

    public boolean configureCity(City city, double cityTrafficRates) {
        if (city == null || !tweakedCityList.contains(city)) {
            return false;
        }
        // Configure city's traffic rates
        city.setTrafficRate(cityTrafficRates);
        return true;
    }

    // Getters
    public String getNameID() {
        return nameID;
    }

    public Editor getEditor() {
        return editor;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public List<City> getTweakedCityList() {
        return new ArrayList<>(tweakedCityList);
    }

    public List<Industry> getAvailableIndustryList() {
        return new ArrayList<>(availableIndustryList);
    }

    public List<Locomotive> getAvailableLocomotives() {
        return new ArrayList<>(availableLocomotives);
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
    }
} 