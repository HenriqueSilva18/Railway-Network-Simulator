package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.RailwayLine;
import pt.ipp.isep.dei.domain.template.Station;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class RailwayLineRepository {
    private final java.util.Map<String, RailwayLine> railwayLines;

    public RailwayLineRepository() {
        this.railwayLines = new ConcurrentHashMap<>();
    }

    public boolean save(RailwayLine railwayLine) {
        if (railwayLine == null || railwayLine.getNameID() == null) {
            return false;
        }
        railwayLines.put(railwayLine.getNameID(), railwayLine);
        return true;
    }

    public RailwayLine getById(String id) {
        return railwayLines.get(id);
    }

    public List<RailwayLine> getAll() {
        return new ArrayList<>(railwayLines.values());
    }

    public List<RailwayLine> getConnectedLines(Station station) {
        return railwayLines.values().stream()
            .filter(line -> line.getStartStation().equals(station) || 
                          line.getEndStation().equals(station))
            .collect(Collectors.toList());
    }

    public boolean exists(Station station1, Station station2) {
        return railwayLines.values().stream()
            .anyMatch(line -> 
                (line.getStartStation().equals(station1) && line.getEndStation().equals(station2)) ||
                (line.getStartStation().equals(station2) && line.getEndStation().equals(station1))
            );
    }

    public boolean delete(String id) {
        return railwayLines.remove(id) != null;
    }
} 