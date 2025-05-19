package pt.ipp.isep.dei.repository.template;

import pt.ipp.isep.dei.domain.template.Cargo;
import java.util.ArrayList;
import java.util.List;

public class CargoRepository {
    private final List<Cargo> cargoList;

    public CargoRepository() {
        this.cargoList = new ArrayList<>();
        initializeCargoTypes();
    }

    private void initializeCargoTypes() {
        // Add basic cargo types - using the constructor Cargo(String name, int amount, int lifespan, String type)
        cargoList.add(new Cargo("Coal", 1000, 30, "Raw Material"));
        cargoList.add(new Cargo("Iron Ore", 1000, 30, "Raw Material"));
        cargoList.add(new Cargo("Steel", 500, 60, "Processed Material"));
        cargoList.add(new Cargo("Wood", 1000, 30, "Raw Material"));
        cargoList.add(new Cargo("Passengers", 100, 1, "People"));
        cargoList.add(new Cargo("Mail", 50, 1, "Communication"));
        cargoList.add(new Cargo("Food", 500, 7, "Consumable"));
        cargoList.add(new Cargo("Oil", 1000, 30, "Raw Material"));
        cargoList.add(new Cargo("Fuel", 500, 30, "Processed Material"));
    }

    public List<Cargo> getCargoList() {
        return new ArrayList<>(cargoList);
    }
} 