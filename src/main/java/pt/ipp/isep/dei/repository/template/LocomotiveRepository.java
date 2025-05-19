package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Locomotive;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LocomotiveRepository {
    private final List<Locomotive> locomotives;

    public LocomotiveRepository() {
        this.locomotives = new ArrayList<>();
        initializeLocomotiveTypes();
    }

    private void initializeLocomotiveTypes() {
        // Create locomotives with the constructor we have in Locomotive class
        locomotives.add(new Locomotive("Steam Engine", "Steam", 1000, 80, 1850, 10000.0));
        locomotives.add(new Locomotive("Diesel Engine", "Diesel", 2000, 120, 1920, 20000.0));
        locomotives.add(new Locomotive("Electric Engine", "Electric", 3000, 160, 1950, 30000.0));
    }

    public List<String> getLocomotiveTypes() {
        List<String> types = new ArrayList<>();
        for (Locomotive locomotive : locomotives) {
            if (!types.contains(locomotive.getType())) {
                types.add(locomotive.getType());
            }
        }
        return types;
    }

    public List<Locomotive> getAvailableLocomotives(List<String> selectedTypes, Date endDate) {
        List<Locomotive> availableLocomotives = new ArrayList<>();
        int endYear = endDate.getYear() + 1900; // Convert Date to year

        for (Locomotive locomotive : locomotives) {
            if (selectedTypes.contains(locomotive.getType()) &&
                    locomotive.getAvailabilityYear() <= endYear) {
                availableLocomotives.add(locomotive);
            }
        }
        return availableLocomotives;
    }

    public Locomotive getLocomotive(String locomotiveID) {
        for (Locomotive locomotive : locomotives) {
            if (locomotive.getNameID().equals(locomotiveID)) {
                return locomotive;
            }
        }
        return null;
    }

    public boolean saveLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
        }

        // Check if locomotive already exists
        for (int i = 0; i < locomotives.size(); i++) {
            if (locomotives.get(i).getNameID().equals(locomotive.getNameID())) {
                // Update existing locomotive
                locomotives.set(i, locomotive);
                return true;
            }
        }

        // Add new locomotive
        locomotives.add(locomotive);
        return true;
    }
} 