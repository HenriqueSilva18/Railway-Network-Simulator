package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Map {
    private final String nameID;
    private final List<City> cities;
    private final List<Industry> industries;
    private final Size size;
    private final List<Position> occupiedPositions;
    private final Random random;

    private Map(String nameID, Size size) {
        this.nameID = nameID;
        this.size = size;
        this.cities = new ArrayList<>();
        this.industries = new ArrayList<>();
        this.occupiedPositions = new ArrayList<>();
        this.random = new Random();
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

    public List<City> getCities() {
        return new ArrayList<>(cities);
    }

    public List<Industry> getIndustries() {
        return new ArrayList<>(industries);
    }

    public Size getSize() {
        return size;
    }

    public boolean addCity(City city) {
        if (!isPositionAvailable(city.getPosition().getX(), city.getPosition().getY())) {
            return false;
        }
        cities.add(city);
        occupiedPositions.add(city.getPosition());
        return true;
    }

    public boolean addIndustry(Industry industry) {
        if (!isPositionAvailable(industry.getPosition().getX(), industry.getPosition().getY())) {
            return false;
        }
        industries.add(industry);
        occupiedPositions.add(industry.getPosition());
        return true;
    }

    public boolean isPositionAvailable(int x, int y) {
        if (x < 0 || x >= size.getWidth() || y < 0 || y >= size.getHeight()) {
            return false;
        }
        return !occupiedPositions.stream()
                .anyMatch(pos -> pos.getX() == x && pos.getY() == y);
    }

    public boolean isCellEmpty(int x, int y) {
        return isPositionAvailable(x, y);
    }

    public void markPositionOccupied(Position position) {
        if (!occupiedPositions.contains(position)) {
            occupiedPositions.add(position);
        }
    }

    public List<Position> getAvailablePositions(int numPositions) {
        List<Position> availablePositions = new ArrayList<>();
        int attempts = 0;
        int maxAttempts = numPositions * 10; // Limit attempts to avoid infinite loop

        while (availablePositions.size() < numPositions && attempts < maxAttempts) {
            int x = random.nextInt(size.getWidth());
            int y = random.nextInt(size.getHeight());
            Position position = new Position(x, y);

            if (isPositionAvailable(x, y)) {
                availablePositions.add(position);
                markPositionOccupied(position);
            }
            attempts++;
        }

        return availablePositions;
    }
} 