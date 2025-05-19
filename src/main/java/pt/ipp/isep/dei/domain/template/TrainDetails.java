package pt.ipp.isep.dei.domain.template;

public class TrainDetails {
    private final String nameID;
    private final String locomotiveType;
    private final int power;
    private final int topSpeed;
    private final int numCarriages;
    private final String assignedRoute;

    public TrainDetails(String nameID, String locomotiveType, int power, int topSpeed, int numCarriages, String assignedRoute) {
        this.nameID = nameID;
        this.locomotiveType = locomotiveType;
        this.power = power;
        this.topSpeed = topSpeed;
        this.numCarriages = numCarriages;
        this.assignedRoute = assignedRoute;
    }

    public String getNameID() {
        return nameID;
    }

    public String getLocomotiveType() {
        return locomotiveType;
    }

    public int getPower() {
        return power;
    }

    public int getTopSpeed() {
        return topSpeed;
    }

    public int getNumCarriages() {
        return numCarriages;
    }

    public String getAssignedRoute() {
        return assignedRoute;
    }
    
    @Override
    public String toString() {
        StringBuilder details = new StringBuilder();
        details.append("Train: ").append(nameID).append("\n");
        details.append("Locomotive Type: ").append(locomotiveType).append("\n");
        details.append("Power: ").append(power).append("\n");
        details.append("Top Speed: ").append(topSpeed).append(" km/h\n");
        details.append("Carriages: ").append(numCarriages).append("\n");
        details.append("Assigned to route: ").append(assignedRoute);
        return details.toString();
    }
} 