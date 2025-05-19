package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private String nameID;
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private Route assignedRoute;

    public Train(String nameID, Locomotive locomotive) {
        this.nameID = nameID;
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>();
        this.assignedRoute = null;
    }

    public String getNameID() {
        return nameID;
    }

    public Locomotive getLocomotive() {
        return locomotive;
    }

    public List<Carriage> getCarriages() {
        return new ArrayList<>(carriages);
    }

    public Route getAssignedRoute() {
        return assignedRoute;
    }
    
    public Route getCurrentRoute() {
        return assignedRoute;
    }
    
    public boolean isAssignedToRoute() {
        return assignedRoute != null;
    }

    public boolean addCarriage(Carriage carriage) {
        if (carriage == null) {
            return false;
        }
        return carriages.add(carriage);
    }

    public boolean assignToRoute(Route route) {
        if (route == null) {
            return false;
        }
        
        // Verify if the route has valid stations
        if (!route.validateStations()) {
            return false;
        }
        
        // Assign route to train
        this.assignedRoute = route;
        
        return true;
    }

    public TrainDetails getDetails() {
        return new TrainDetails(
            this.nameID,
            this.locomotive.getType(),
            this.locomotive.getPower(),
            this.locomotive.getTopSpeed(),
            this.carriages.size(),
            this.assignedRoute != null ? this.assignedRoute.getNameID() : "None"
        );
    }

    @Override
    public String toString() {
        return nameID;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Train train = (Train) obj;
        return nameID != null && nameID.equals(train.nameID);
    }

    @Override
    public int hashCode() {
        return nameID != null ? nameID.hashCode() : 0;
    }
} 