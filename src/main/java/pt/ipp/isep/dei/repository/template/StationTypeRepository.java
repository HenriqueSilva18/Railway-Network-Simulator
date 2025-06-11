package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.StationType;
import java.util.ArrayList;
import java.util.List;

public class StationTypeRepository {
    private final List<StationType> stationTypes;

    public StationTypeRepository() {
        this.stationTypes = new ArrayList<>();
        initializeStationTypes();
    }

    private void initializeStationTypes() {
        createStationTypes();
    }

    private void createStationTypes() {
        stationTypes.add(new StationType(StationType.DEPOT, 3, 50000, 100, false, 2));
        stationTypes.add(new StationType(StationType.STATION, 4, 100000, 200, true, 3));
        stationTypes.add(new StationType(StationType.TERMINAL, 5, 200000, 300, false, 4));
    }

    public List<StationType> getStationTypes() {
        return new ArrayList<>(stationTypes);
    }

    public StationType getStationTypeByName(String name) {
        return stationTypes.stream()
                .filter(type -> type.getName().equals(name))
                .findFirst()
                .orElse(null);
    }
} 