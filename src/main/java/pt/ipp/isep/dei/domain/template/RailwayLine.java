package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RailwayLine implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final String nameID;
    private final Station startStation;
    private final Station endStation;
    private final List<Position> path;
    private final boolean isDoubleTrack;
    private final boolean isElectrified;
    private final double length;
    private final double maintenanceCostPerKm;

    public RailwayLine(String nameID, Station startStation, Station endStation, List<Position> path, boolean isDoubleTrack, boolean isElectrified) {
        if (nameID == null || startStation == null || endStation == null || path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Invalid railway line parameters");
        }

        this.nameID = nameID;
        this.startStation = startStation;
        this.endStation = endStation;
        this.path = new ArrayList<>(path);
        this.isDoubleTrack = isDoubleTrack;
        this.isElectrified = isElectrified;
        this.length = calculateLength();
        this.maintenanceCostPerKm = 100.0; // Default maintenance cost
    }

    public String getNameID() {
        return nameID;
    }

    public Station getStartStation() {
        return startStation;
    }

    public Station getEndStation() {
        return endStation;
    }

    public List<Position> getPath() {
        return new ArrayList<>(path);
    }

    public boolean isDoubleTrack() {
        return isDoubleTrack;
    }

    public boolean isElectrified() {
        return isElectrified;
    }

    public double getLength() {
        return length;
    }

    public double getMaintenanceCostPerKm() {
        return maintenanceCostPerKm;
    }

    private double calculateLength() {
        double length = 0;
        for (int i = 0; i < path.size() - 1; i++) {
            Position current = path.get(i);
            Position next = path.get(i + 1);

            // Calculate distance between points
            int dx = next.getX() - current.getX();
            int dy = next.getY() - current.getY();

            // If diagonal movement, use diagonal distance (√2)
            if (dx != 0 && dy != 0) {
                length += Math.sqrt(2);
            } else {
                length += 1;
            }
        }
        return length;
    }

    public boolean containsPosition(Position position) {
        return path.contains(position);
    }

    public boolean containsPosition(int x, int y) {
        return path.stream().anyMatch(p -> p.getX() == x && p.getY() == y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        RailwayLine other = (RailwayLine) obj;
        return nameID != null && nameID.equals(other.nameID);
    }

    @Override
    public int hashCode() {
        return nameID != null ? nameID.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("%s: %s -> %s (%.2f units)",
                nameID, startStation.getNameID(), endStation.getNameID(), getLength());
    }
}