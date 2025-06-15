package pt.ipp.isep.dei.domain.template;

import java.io.Serializable;

public class PointOfRoute implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private final Station station;
    private CargoMode cargoMode;
    private final String pointId;

    public PointOfRoute(Station station, CargoMode cargoMode, String pointId) {
        if (station == null || cargoMode == null || pointId == null) {
            throw new IllegalArgumentException("Station, cargo mode, and point ID cannot be null");
        }
        this.station = station;
        this.cargoMode = cargoMode;
        this.pointId = pointId;
    }

    public Station getStation() {
        return station;
    }

    public CargoMode getCargoMode() {
        return cargoMode;
    }

    public void setCargoMode(CargoMode cargoMode) {
        if (cargoMode == null) {
            throw new IllegalArgumentException("Cargo mode cannot be null");
        }
        this.cargoMode = cargoMode;
    }

    public String getPointId() {
        return pointId;
    }
} 