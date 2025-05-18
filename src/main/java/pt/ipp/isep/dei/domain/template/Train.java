package pt.ipp.isep.dei.domain.template;

import java.util.ArrayList;
import java.util.List;

public class Train {
    private String nameID;
    private Locomotive locomotive;
    private List<Carriage> carriages;
    private Route currentRoute;

    public Train(String nameID, Locomotive locomotive) {
        this.nameID = nameID;
        this.locomotive = locomotive;
        this.carriages = new ArrayList<>();
        this.currentRoute = null;
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

    public Route getCurrentRoute() {
        return currentRoute;
    }

    public boolean isAssignedToRoute() {
        return currentRoute != null;
    }

    public boolean assignToRoute(Route route) {
        if (route == null) return false;
        if (isAssignedToRoute()) return false;
        
        this.currentRoute = route;
        return true;
    }

    public String getDetails() {
        StringBuilder details = new StringBuilder();
        details.append("Train: ").append(nameID).append("\n");
        details.append("Locomotive: ").append(locomotive.getDetails()).append("\n");
        details.append("Carriages:\n");
        for (int i = 0; i < carriages.size(); i++) {
            details.append(String.format("%d. %s\n", i + 1, carriages.get(i).getDetails()));
        }
        if (currentRoute != null) {
            details.append("\nAssigned to route: ").append(currentRoute.getNameID());
        }
        return details.toString();
    }

    public void addCarriage(Carriage carriage) {
        if (carriage != null) {
            carriages.add(carriage);
        }
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