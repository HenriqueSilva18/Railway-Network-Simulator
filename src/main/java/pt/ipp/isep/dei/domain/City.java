package pt.ipp.isep.dei.domain;

public class City {
    private String name;
    private int xCoordinate;
    private int yCoordinate;
    private int houseBlocks;

    public City(String name, int x, int y, int houseBlocks) {
        this.name = name;
        this.xCoordinate = x;
        this.yCoordinate = y;
        this.houseBlocks = houseBlocks;
    }

    // Getters
    public String getName() { return name; }
    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
    public int getHouseBlocks() { return houseBlocks; }
}