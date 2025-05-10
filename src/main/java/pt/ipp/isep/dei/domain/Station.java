package pt.ipp.isep.dei.domain;

public class Station {
    private String name;
    private int xCoordinate;
    private int yCoordinate;

    public Station(String name, int x, int y) {
        this.name = name;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    // Getters
    public String getName() { return name; }
    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }

    @Override
    public String toString() {
        return name + " (" + xCoordinate + "," + yCoordinate + ")";
    }
}