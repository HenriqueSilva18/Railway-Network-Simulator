package pt.ipp.isep.dei.domain.template;

import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.RailwayLineRepository;
import java.util.ArrayList;
import java.util.List;

public class Route {
    private final String nameID;
    private final List<Station> stationSequence;
    private final List<Cargo> cargoList;
    private Train assignedTrain;

    public Route(String nameID, List<Station> stationSequence) {
        if (nameID == null || stationSequence == null || stationSequence.size() < 2) {
            throw new IllegalArgumentException("Invalid route parameters");
        }
        
        this.nameID = nameID;
        this.stationSequence = new ArrayList<>(stationSequence);
        this.cargoList = new ArrayList<>();
        this.assignedTrain = null;
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

    public Train getAssignedTrain() {
        return assignedTrain;
    }

    public void setAssignedTrain(Train train) {
        this.assignedTrain = train;
    }

    public void addCargo(Cargo cargo) {
        if (cargo != null) {
            cargoList.add(cargo);
        }
    }

    public boolean validateStations() {
        if (stationSequence.size() < 2) {
            return false;
        }

        // Check if all consecutive stations are connected by railway lines
        for (int i = 0; i < stationSequence.size() - 1; i++) {
            Station current = stationSequence.get(i);
            Station next = stationSequence.get(i + 1);

            // Get railway lines repository
            RailwayLineRepository railwayLineRepository = 
                Repositories.getInstance().getRailwayLineRepository();

            // Check if there's a railway line between these stations
            if (!railwayLineRepository.exists(current, next)) {
                return false;
            }
        }

        return true;
    }

    public double calculateTotalLength() {
        double totalLength = 0;
        RailwayLineRepository railwayLineRepository = 
            Repositories.getInstance().getRailwayLineRepository();

        for (int i = 0; i < stationSequence.size() - 1; i++) {
            Station current = stationSequence.get(i);
            Station next = stationSequence.get(i + 1);

            // Find railway line between these stations
            List<RailwayLine> lines = railwayLineRepository.getAll();
            for (RailwayLine line : lines) {
                if ((line.getStartStation().equals(current) && line.getEndStation().equals(next)) ||
                    (line.getStartStation().equals(next) && line.getEndStation().equals(current))) {
                    totalLength += line.getLength();
                    break;
                }
            }
        }

        return totalLength;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Route: ").append(nameID).append("\n");
        sb.append("Stations: ");
        for (int i = 0; i < stationSequence.size(); i++) {
            sb.append(stationSequence.get(i).getNameID());
            if (i < stationSequence.size() - 1) {
                sb.append(" -> ");
            }
        }
        sb.append("\nTotal Length: ").append(String.format("%.2f", calculateTotalLength()));
        if (assignedTrain != null) {
            sb.append("\nAssigned Train: ").append(assignedTrain.getNameID());
        }
        return sb.toString();
    }
} 