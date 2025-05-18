package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private final String nameID;
    private final Size size;
    private final List<City> cityList;
    private final List<Industry> industryList;

    private Map(String nameID, Size size) {
        this.nameID = nameID;
        this.size = size;
        this.cityList = new ArrayList<>();
        this.industryList = new ArrayList<>();
    }

    public static boolean validateMapName(String nameID) {
        if (nameID == null || nameID.trim().isEmpty()) {
            return false;
        }
        // Check if nameID is a valid file name
        return nameID.matches("^[a-zA-Z0-9_-]+$");
    }

    public static Map createMap(String nameID, Size size) {
        if (!validateMapName(nameID)) {
            throw new IllegalArgumentException("Invalid map name");
        }
        return new Map(nameID, size);
    }

    public String getNameID() {
        return nameID;
    }

    public Size getSize() {
        return size;
    }

    public List<City> getCityList() {
        return new ArrayList<>(cityList);
    }

    public List<Industry> getIndustryList() {
        return new ArrayList<>(industryList);
    }
} 