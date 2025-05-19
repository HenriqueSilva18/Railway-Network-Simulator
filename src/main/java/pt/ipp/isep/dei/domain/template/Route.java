package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Route {
    private String nameID;
    private List<Station> stationSequence;
    private List<Cargo> cargoList;
    private Map<Station, List<Cargo>> stationCargo;
    private List<RailwayLine> railwayLines;
    private List<Train> assignedTrains;

    public Route(String nameID) {
        this.nameID = nameID;
        this.stationSequence = new ArrayList<>();
        this.cargoList = new ArrayList<>();
        this.stationCargo = new HashMap<>();
        this.railwayLines = new ArrayList<>();
        this.assignedTrains = new ArrayList<>();
    }
    
    public Route(String nameID, List<Station> stationSequence) {
        if (nameID == null || stationSequence == null || stationSequence.size() < 2) {
            throw new IllegalArgumentException("Invalid route parameters");
        }
        
        this.nameID = nameID;
        this.stationSequence = new ArrayList<>(stationSequence);
        this.cargoList = new ArrayList<>();
        this.stationCargo = new HashMap<>();
        this.railwayLines = new ArrayList<>();
        this.assignedTrains = new ArrayList<>();
        
        // Initialize stationCargo map for each station
        for (Station station : stationSequence) {
            stationCargo.put(station, new ArrayList<>());
        }
    }

    public String getNameID() {
        return nameID;
    }

    public List<Station> getStationSequence() {
        return new ArrayList<>(stationSequence);
    }

    public List<Cargo> getCargoList() {
        return new ArrayList<>(cargoList);
    }

    public List<RailwayLine> getRailwayLines() {
        return new ArrayList<>(railwayLines);
    }

    public List<Train> getAssignedTrains() {
        return new ArrayList<>(assignedTrains);
    }

    public boolean addStation(Station station) {
        if (station == null) {
            return false;
        }
        
        // If this is not the first station, check if there is a railway line to the previous station
        if (!stationSequence.isEmpty()) {
            Station previousStation = stationSequence.get(stationSequence.size() - 1);
            boolean hasRailwayLine = false;
            
            for (RailwayLine line : railwayLines) {
                if ((line.getStartStation().equals(previousStation) && line.getEndStation().equals(station)) ||
                    (line.getStartStation().equals(station) && line.getEndStation().equals(previousStation))) {
                    hasRailwayLine = true;
                    break;
                }
            }
            
            if (!hasRailwayLine) {
                // No railway line between stations, but we'll still add it and validate later
                System.out.println("Warning: No railway line between stations " + previousStation.getNameID() + 
                                   " and " + station.getNameID());
            }
        }
        
        // Add the station to the sequence
        stationSequence.add(station);
        
        // Initialize cargo list for this station
        stationCargo.put(station, new ArrayList<>());
        
        return true;
    }

    public boolean addCargo(Cargo cargo, Station station) {
        if (cargo == null || station == null || !stationSequence.contains(station)) {
            return false;
        }
        
        // Add cargo to the general list
        cargoList.add(cargo);
        
        // Add cargo to the station's list
        List<Cargo> stationCargoList = stationCargo.get(station);
        if (stationCargoList == null) {
            stationCargoList = new ArrayList<>();
            stationCargo.put(station, stationCargoList);
        }
        stationCargoList.add(cargo);
        
        return true;
    }

    public boolean addRailwayLine(RailwayLine railwayLine) {
        if (railwayLine == null) {
            return false;
        }
        
        // Check if the railway line connects stations in the sequence
        Station start = railwayLine.getStartStation();
        Station end = railwayLine.getEndStation();
        
        if (!stationSequence.contains(start) || !stationSequence.contains(end)) {
            return false;
        }
        
        railwayLines.add(railwayLine);
        return true;
    }

    public boolean validateStations() {
        if (stationSequence.size() < 2) {
            return false; // Need at least 2 stations for a valid route
        }

        // Check if there are railway lines between consecutive stations
        for (int i = 0; i < stationSequence.size() - 1; i++) {
            Station current = stationSequence.get(i);
            Station next = stationSequence.get(i + 1);
            boolean hasConnection = false;

            for (RailwayLine line : railwayLines) {
                if ((line.getStartStation().equals(current) && line.getEndStation().equals(next)) ||
                    (line.getStartStation().equals(next) && line.getEndStation().equals(current))) {
                    hasConnection = true;
                    break;
                }
            }

            if (!hasConnection) {
                return false;
            }
        }

        return true;
    }

    public boolean addTrain(Train train) {
        if (train == null) {
            return false;
        }
        
        return assignedTrains.add(train);
    }

    public Map<Station, List<Cargo>> getStationCargo() {
        Map<Station, List<Cargo>> result = new HashMap<>();
        for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
            result.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        return result;
    }

    public RouteDetails getDetails() {
        return new RouteDetails(
            this.nameID,
            this.stationSequence,
            this.cargoList,
            this.stationCargo,
            this.assignedTrains.size()
        );
    }

    @Override
    public String toString() {
        return nameID;
    }
} 