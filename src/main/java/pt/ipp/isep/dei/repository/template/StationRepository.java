package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StationRepository {

    private final Map<String, Station> stations = new HashMap<>();
    
    public Station getStation(String stationId) {
        return stations.get(stationId);
    }
    
    public List<Station> getAllStations() {
        return new ArrayList<>(stations.values());
    }

    public List<Station> getAll() {
        return getAllStations();  // Delegate to existing getAllStations() method
    }
    
    public boolean add(Station station) {
        if (station == null || stations.containsKey(station.getNameID())) {
            return false;
        }
        
        stations.put(station.getNameID(), station);
        return true;
    }
    
    public boolean save(Station station) {
        if (station == null) {
            return false;
        }
        
        stations.put(station.getNameID(), station);
        return true;
    }
    
    public boolean remove(String stationId) {
        if (stationId == null || !stations.containsKey(stationId)) {
            return false;
        }
        
        stations.remove(stationId);
        return true;
    }
    
    public int size() {
        return stations.size();
    }
} 