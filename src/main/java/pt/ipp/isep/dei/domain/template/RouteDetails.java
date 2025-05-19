package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteDetails {
    private final String nameID;
    private final List<Station> stations;
    private final List<Cargo> cargoes;
    private final Map<Station, List<Cargo>> stationCargo;
    private final int assignedTrainsCount;

    public RouteDetails(String nameID, List<Station> stations, List<Cargo> cargoes, 
                        Map<Station, List<Cargo>> stationCargo, int assignedTrainsCount) {
        this.nameID = nameID;
        this.stations = new ArrayList<>(stations);
        this.cargoes = new ArrayList<>(cargoes);
        
        // Make a deep copy of the station cargo map
        this.stationCargo = new HashMap<>();
        for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
            this.stationCargo.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        
        this.assignedTrainsCount = assignedTrainsCount;
    }

    public String getNameID() {
        return nameID;
    }

    public List<Station> getStations() {
        return new ArrayList<>(stations);
    }

    public List<Cargo> getCargoes() {
        return new ArrayList<>(cargoes);
    }

    public Map<Station, List<Cargo>> getStationCargo() {
        Map<Station, List<Cargo>> result = new HashMap<>();
        for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    public int getAssignedTrainsCount() {
        return assignedTrainsCount;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ").append(nameID).append("\n\n");
        
        sb.append("Stations: \n");
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            sb.append(i + 1).append(". ").append(station.getNameID()).append("\n");
            
            List<Cargo> cargoAtStation = stationCargo.get(station);
            if (cargoAtStation != null && !cargoAtStation.isEmpty()) {
                sb.append("   Cargo to pick up: \n");
                for (Cargo cargo : cargoAtStation) {
                    sb.append("   - ").append(cargo.toString()).append("\n");
                }
            } else {
                sb.append("   No cargo to pick up\n");
            }
        }
        
        sb.append("\nTotal stations: ").append(stations.size());
        sb.append("\nTotal cargo items: ").append(cargoes.size());
        sb.append("\nAssigned trains: ").append(assignedTrainsCount);
        
        return sb.toString();
    }
} 