package pt.ipp.isep.dei.domain;

public class Industry {
    private String name;
    private IndustryType type;
    private int xCoordinate;
    private int yCoordinate;

    public Industry(String name, IndustryType type, int x, int y) {
        this.name = name;
        this.type = type;
        this.xCoordinate = x;
        this.yCoordinate = y;
    }

    // Getters
    public String getName() { return name; }
    public IndustryType getType() { return type; }
    public int getXCoordinate() { return xCoordinate; }
    public int getYCoordinate() { return yCoordinate; }
}