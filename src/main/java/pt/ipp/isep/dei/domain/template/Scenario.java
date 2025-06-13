package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

public class Scenario implements Serializable {
    private static final long serialVersionUID = 1L;
    private String nameID;
    private String displayName;
    private Editor editor;
    private Date startDate;
    private Date endDate;
    private List<City> tweakedCityList;
    private List<Industry> availableIndustryList;
    private List<Locomotive> availableLocomotives;
    private Map map;

    public Scenario(String nameID, String displayName, Editor editor, Date startDate, Date endDate,
                    List<Industry> selectedIndustries, List<Locomotive> availableLocomotives,
                    List<City> mapCityList) {
        this.nameID = nameID;
        this.displayName = displayName;
        this.editor = editor;
        this.startDate = startDate;
        this.endDate = endDate;
        this.availableIndustryList = new ArrayList<>(selectedIndustries);
        this.availableLocomotives = new ArrayList<>(availableLocomotives);
        this.tweakedCityList = new ArrayList<>(mapCityList);
    }

    public void setNameID(String nameID) {
        this.nameID = nameID;
    }

    public String getNameID() {
        return nameID;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public List<City> getTweakedCityList() {
        return new ArrayList<>(tweakedCityList);
    }

    public void setTweakedCityList(List<City> tweakedCityList) {
        this.tweakedCityList = new ArrayList<>(tweakedCityList);
    }

    public List<Industry> getAvailableIndustryList() {
        return new ArrayList<>(availableIndustryList);
    }

    public void setAvailableIndustryList(List<Industry> availableIndustryList) {
        this.availableIndustryList = new ArrayList<>(availableIndustryList);
    }

    public List<Locomotive> getAvailableLocomotives() {
        return new ArrayList<>(availableLocomotives);
    }

    public void setAvailableLocomotives(List<Locomotive> availableLocomotives) {
        this.availableLocomotives = new ArrayList<>(availableLocomotives);
    }

    public Map getMap() {
        return map;
    }

    public void setMap(Map map) {
        this.map = map;
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

    public boolean configureCity(City city, float cityTrafficRates) {
        if (city == null || !tweakedCityList.contains(city)) {
            return false;
        }
        // Configure city's traffic rates
        city.setTrafficRate(cityTrafficRates);
        return true;
    }

    public List<Locomotive> getAvailableLocomotives(Date currentDate) {
        if (currentDate == null) {
            return getAvailableLocomotives();
        }

        List<Locomotive> filteredLocomotives = new ArrayList<>();

        // Extract year from date
        Calendar cal = Calendar.getInstance();
        cal.setTime(currentDate);
        int currentYear = cal.get(Calendar.YEAR);

        // Filter locomotives by availability year
        for (Locomotive locomotive : availableLocomotives) {
            if (locomotive.getAvailabilityYear() <= currentYear) {
                filteredLocomotives.add(locomotive);
            }
        }

        return filteredLocomotives;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scenario scenario = (Scenario) o;
        return Objects.equals(nameID, scenario.nameID);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nameID);
    }

    @Override
    public String toString() {
        return displayName + " (" + nameID + ")";
    }
} 