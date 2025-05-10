package pt.ipp.isep.dei.domain;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private String nameID;
    private Size size;
    private List<City> cities;
    private List<Industry> industries;

    public Map(String nameID, int width, int height) {
        if (!validateName(nameID)) {
            throw new IllegalArgumentException("Invalid map name");
        }
        this.nameID = nameID;
        this.size = new Size(width, height);
        this.cities = new ArrayList<>();
        this.industries = new ArrayList<>();
    }

    public boolean validateName(String name) {
        // Check if name is valid filename (no special chars)
        return name != null && name.matches("^[a-zA-Z0-9 _-]+$");
    }

    // Getters
    public String getNameID() {
        return nameID;
    }

    public Size getSize() {
        return size;
    }

    public List<City> getCities() {
        return new ArrayList<>(cities); // Return defensive copy
    }

    public List<Industry> getIndustries() {
        return new ArrayList<>(industries); // Return defensive copy
    }

    // Methods to add elements
    public void addCity(City city) {
        if (city != null) {
            cities.add(city);
        }
    }

    public void addIndustry(Industry industry) {
        if (industry != null) {
            industries.add(industry);
        }
    }
}