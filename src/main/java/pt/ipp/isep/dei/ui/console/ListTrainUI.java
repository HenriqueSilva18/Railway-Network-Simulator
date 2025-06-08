package pt.ipp.isep.dei.ui.console;

import pt.ipp.isep.dei.controller.template.ListTrainController;
import pt.ipp.isep.dei.domain.template.*;

import java.util.*;
import java.util.Map;
import java.util.stream.Collectors;

public class ListTrainUI implements Runnable {
    private final ListTrainController controller;

    public ListTrainUI() {
        this.controller = new ListTrainController();
    }

    @Override
    public void run() {
        System.out.println("\n=== Your Train Fleet ===\n");

        List<Train> trains = controller.getAllTrains();

        if (trains.isEmpty()) {
            System.out.println("You don't own any trains yet.");
            System.out.println("Use the 'Buy Locomotive' option to purchase locomotives for your fleet.");
            return;
        }

        // Group by locomotive type, then sort train list alphabetically by nameID
        Map<String, List<Train>> trainsByType = trains.stream()
                .filter(train -> train.getLocomotive() != null)
                .sorted(Comparator.comparing(Train::getNameID))
                .collect(Collectors.groupingBy(t -> t.getLocomotive().getType(), TreeMap::new, Collectors.toList()));

        int totalTrains = 0;

        for (Map.Entry<String, List<Train>> entry : trainsByType.entrySet()) {
            String locoType = entry.getKey();
            List<Train> trainsOfType = entry.getValue();

            System.out.println("\n=== " + locoType + " Locomotives (" + trainsOfType.size() + ") ===");
            System.out.printf("%-4s | %-20s | %-20s | %-8s | %-10s | %-10s | %-15s%n",
                    "No.", "Train Name", "Locomotive", "Power", "Max Speed", "Fuel Cost", "Route");
            System.out.println("-".repeat(110));

            int i = 1;
            for (Train train : trainsOfType) {
                Locomotive loco = train.getLocomotive();
                String routeName = train.isAssignedToRoute() ? train.getAssignedRoute().getNameID() : "None";

                System.out.printf("%-4d | %-20s | %-20s | %-8d | %-10d | %-10.2f | %-15s%n",
                        i++,
                        train.getNameID(),
                        loco.getNameID(),
                        loco.getPower(),
                        loco.getTopSpeed(),
                        loco.getFuelCost(),
                        routeName);


                // Mostrar carruagens
                if (!train.getCarriages().isEmpty()) {
                    System.out.println("      Carriages:");
                    for (Carriage carriage : train.getCarriages()) {
                        System.out.println("        └── " + carriage.getClass().getSimpleName());
                    }
                } else {
                    System.out.println("      No carriages attached.");
                }
            }

            totalTrains += trainsOfType.size();
        }

        System.out.println("\nTotal trains: " + totalTrains);

        // Pause
        System.out.println("\nPress Enter to continue...");
        try {
            System.in.read();
        } catch (Exception ignored) {
        }
    }

    private void displayCargoInformation(Train train) {
        if (train == null || train.getAssignedRoute() == null) return;

        Route route = train.getAssignedRoute();
        Map<Station, List<Cargo>> stationCargo = route.getStationCargo();

        boolean hasCargo = stationCargo.values().stream().anyMatch(list -> !list.isEmpty());

        if (!hasCargo) {
            System.out.println("      No cargo available on this route.");
            return;
        }

        System.out.println("      Cargo on route " + route.getNameID() + ":");

        for (Map.Entry<Station, List<Cargo>> entry : stationCargo.entrySet()) {
            Station station = entry.getKey();
            List<Cargo> cargoList = entry.getValue();

            if (cargoList.isEmpty()) continue;

            System.out.println("      └── Station: " + station.getNameID());

            Map<String, Integer> cargoByType = new HashMap<>();
            for (Cargo cargo : cargoList) {
                cargoByType.put(cargo.getType(),
                        cargoByType.getOrDefault(cargo.getType(), 0) + cargo.getAmount());
            }

            for (Map.Entry<String, Integer> cargoEntry : cargoByType.entrySet()) {
                System.out.printf("          ├── %s: %d tons%n",
                        cargoEntry.getKey(), cargoEntry.getValue());
            }
        }
    }
}
