package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Map {
    private String nameID;
    private Size size;
    private List<Industry> industries;
    private List<City> cities;
    private List<Position> positions;
    private List<Scenario> scenarios;

    private Map(String nameID, Size size) {
        this.nameID = nameID;
        this.size = size;
        this.industries = new ArrayList<>();
        this.cities = new ArrayList<>();
        this.positions = new ArrayList<>();
        this.scenarios = new ArrayList<>();
        initializePositions();
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

    private void initializePositions() {
        for (int x = 0; x < size.getWidth(); x++) {
            for (int y = 0; y < size.getHeight(); y++) {
                positions.add(new Position(x, y));
            }
        }
    }

    public String getNameID() {
        return nameID;
    }

    public Size getSize() {
        return size;
    }

    public List<Industry> getIndustries() {
        return new ArrayList<>(industries);
    }

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public List<Position> getPositions() {
        return new ArrayList<>(positions);
    }

    public List<Scenario> getScenarios() {
        return new ArrayList<>(scenarios);
    }

    public boolean isCellEmpty(int x, int y) {
        if (x < 0 || x >= size.getWidth() || y < 0 || y >= size.getHeight()) {
            return false;
        }

        Position position = getPosition(x, y);
        return position != null && !position.isOccupied();
    }

    public Position getPosition(int x, int y) {
        for (Position position : positions) {
            if (position.getX() == x && position.getY() == y) {
                return position;
            }
        }
        return null;
    }

    public boolean addIndustry(Industry industry) {
        if (industry == null || industry.getPosition() == null) {
            return false;
        }

        Position position = getPosition(industry.getPosition().getX(), industry.getPosition().getY());
        if (position == null || position.isOccupied()) {
            return false;
        }

        position.setOccupied(true);
        return industries.add(industry);
    }

    public boolean addCity(City city) {
        if (city == null || city.getPosition() == null) {
            return false;
        }

        Position position = getPosition(city.getPosition().getX(), city.getPosition().getY());
        if (position == null || position.isOccupied()) {
            return false;
        }

        position.setOccupied(true);
        return cities.add(city);
    }

    public boolean addScenario(Scenario scenario) {
        if (scenario == null) {
            return false;
        }
        return scenarios.add(scenario);
    }

    public String getMapLayout() {
        StringBuilder layout = new StringBuilder();
        layout.append("Map: ").append(nameID).append("\n");
        layout.append("Size: ").append(size.getWidth()).append("x").append(size.getHeight()).append("\n\n");

        for (int y = 0; y < size.getHeight(); y++) {
            for (int x = 0; x < size.getWidth(); x++) {
                Position position = getPosition(x, y);
                if (position != null && position.isOccupied()) {
                    layout.append("I "); // I for Industry
                } else {
                    layout.append(". "); // . for empty cell
                }
            }
            layout.append("\n");
        }

        return layout.toString();
    }
} 