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
        stationTypes.add(new StationType(StationType.DEPOT));
        stationTypes.add(new StationType(StationType.STATION));
        stationTypes.add(new StationType(StationType.TERMINAL));
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