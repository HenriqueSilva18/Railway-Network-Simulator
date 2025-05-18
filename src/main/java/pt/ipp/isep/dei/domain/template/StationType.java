package pt.ipp.isep.dei.domain.template;

public class StationType {
    private final String name;
    private final int economicRadius;
    private final double cost;
    private String centerPoint;

    public static final String DEPOT = "DEPOT";
    public static final String STATION = "STATION";
    public static final String TERMINAL = "TERMINAL";

    public StationType(String name) {
        this.name = name;
        switch (name) {
            case DEPOT:
                this.economicRadius = 3;
                this.cost = 50000;
                break;
            case STATION:
                this.economicRadius = 4;
                this.cost = 100000;
                break;
            case TERMINAL:
                this.economicRadius = 5;
                this.cost = 200000;
                break;
            default:
                throw new IllegalArgumentException("Invalid station type: " + name);
        }
    }

    public String getName() {
        return name;
    }

    public int getEconomicRadius() {
        return economicRadius;
    }

    public double getCost() {
        return cost;
    }

    public boolean requiresCenterPoint() {
        return STATION.equals(name);
    }

    public String getCenterPoint() {
        return centerPoint;
    }

    public void setCenterPoint(String centerPoint) {
        if (requiresCenterPoint()) {
            if (!isValidCenterPoint(centerPoint)) {
                throw new IllegalArgumentException("Invalid center point. Must be NE, SE, NW, or SW");
            }
            this.centerPoint = centerPoint;
        }
    }

    private boolean isValidCenterPoint(String centerPoint) {
        return centerPoint != null && (
            centerPoint.equals("NE") ||
            centerPoint.equals("SE") ||
            centerPoint.equals("NW") ||
            centerPoint.equals("SW")
        );
    }
} 