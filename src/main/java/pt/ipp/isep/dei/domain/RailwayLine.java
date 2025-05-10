package pt.ipp.isep.dei.domain;

public class RailwayLine {
    private final Station source;
    private final Station destination;
    private final String name;
    private final double length;
    private final boolean electrified;

    public RailwayLine(Station source, Station destination, String name, boolean electrified) {
        this.source = source;
        this.destination = destination;
        this.name = name;
        this.electrified = electrified;
        this.length = calculateDistance();
    }

    private double calculateDistance() {
        int dx = destination.getXCoordinate() - source.getXCoordinate();
        int dy = destination.getYCoordinate() - source.getYCoordinate();
        return Math.sqrt(dx*dx + dy*dy);
    }

    // Getters
    public Station getSource() { return source; }
    public Station getDestination() { return destination; }
    public String getName() { return name; }
    public double getLength() { return length; }
    public boolean isElectrified() { return electrified; }

    @Override
    public String toString() {
        return String.format("%s (%s ↔ %s) %.1f units%s",
                name,
                source.getName(),
                destination.getName(),
                length,
                electrified ? " [electrified]" : "");
    }
}