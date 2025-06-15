package pt.ipp.isep.dei.controller.template;

import pt.ipp.isep.dei.domain.template.Player;
import pt.ipp.isep.dei.domain.template.Locomotive;
import pt.ipp.isep.dei.domain.template.Scenario;
import pt.ipp.isep.dei.repository.template.LocomotiveRepository;
import pt.ipp.isep.dei.repository.template.PlayerRepository;
import pt.ipp.isep.dei.repository.template.ScenarioRepository;
import pt.ipp.isep.dei.repository.template.Repositories;
import pt.ipp.isep.dei.repository.template.TrainRepository;
import pt.ipp.isep.dei.domain.template.Train;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class BuyLocomotiveController {
    private final LocomotiveRepository locomotiveRepository;
    private final PlayerRepository playerRepository;
    private final ScenarioRepository scenarioRepository;
    private final TrainRepository trainRepository;
    private final SimulatorController simulatorController;

    public BuyLocomotiveController() {
        Repositories repositories = Repositories.getInstance();
        this.locomotiveRepository = repositories.getLocomotiveRepository();
        this.playerRepository = repositories.getPlayerRepository();
        this.scenarioRepository = repositories.getScenarioRepository();
        this.trainRepository = repositories.getTrainRepository();
        this.simulatorController = new SimulatorController();
    }

    public int getCurrentYear() {
        return simulatorController.getCurrentYear();
    }

    public List<Locomotive> getAvailableLocomotives() {
        // Get current scenario and date
        Scenario currentScenario = getCurrentScenario();
        if (currentScenario == null) {
            return List.of(); // Empty list if no scenario
        }

        // Get all locomotives from scenario
        List<Locomotive> allLocomotives = currentScenario.getAvailableLocomotives(getCurrentDate());
        
        // Filter locomotives based on availability year
        int currentYear = getCurrentYear();
        return allLocomotives.stream()
                .filter(loc -> loc.getAvailabilityYear() <= currentYear)
                .collect(Collectors.toList());
    }

    public List<Locomotive> getPlayerLocomotives() {
        Player player = getPlayerFromSession();
        if (player == null) {
            return new ArrayList<>();
        }
        return player.getOwnedLocomotives();
    }

    public Locomotive getLocomotive(String locomotiveID) {
        return locomotiveRepository.getLocomotive(locomotiveID);
    }

    public boolean purchaseLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
        }

        // Get current year and check availability
        int currentYear = getCurrentYear();
        if (locomotive.getAvailabilityYear() > currentYear) {
            return false; // Cannot purchase locomotive that isn't available yet
        }

        // Get player from session
        Player player = getPlayerFromSession();
        if (player == null) {
            return false;
        }

        // Get locomotive price
        double locomotivePrice = locomotive.getPrice();

        // Check if player has enough money
        if (!checkBalance(locomotivePrice, player)) {
            return false;
        }

        // Complete the purchase
        if (!player.deductFromBudget(locomotivePrice)) {
            return false;
        }

        if (!player.addLocomotive(locomotive)) {
            // Refund the money if adding the locomotive fails
            player.addToBudget(locomotivePrice);
            return false;
        }

        // Set the player as the owner of the locomotive using the boolean method
        if (!locomotive.setOwner(player)) {
            // Refund if setting owner fails
            player.addToBudget(locomotivePrice);
            return false;
        }

        // Create a train with this locomotive adding a unique incremental name
        String trainName = "Train_" + locomotive.getNameID() + "_"+ (trainRepository.getAllTrains().size() + 1);

        Train train = new Train(trainName, locomotive);
        trainRepository.save(train);

        // Save updated locomotive and player
        locomotiveRepository.saveLocomotive(locomotive);
        playerRepository.save(player);

        return true;
    }

    private Scenario getCurrentScenario() {
        return scenarioRepository.getCurrentScenario();
    }

    private Date getCurrentDate() {
        return scenarioRepository.getCurrentDate();
    }

    private Player getPlayerFromSession() {
        UserSession currentSession = ApplicationSession.getInstance().getCurrentSession();
        if (currentSession == null) {
            return null;
        }

        String username = currentSession.getUserEmail();
        return playerRepository.getPlayerByEmail(username);
    }

    private boolean checkBalance(double price, Player player) {
        if (player == null) {
            return false;
        }

        return player.getCurrentBudget() >= price;
    }

    /**
     * Gets the train that uses the specified locomotive
     * @param locomotive The locomotive to find
     * @return The train using the locomotive, or null if not found
     */
    public Train getTrainForLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return null;
        }
        
        List<Train> allTrains = trainRepository.getAllTrains();
        for (Train train : allTrains) {
            if (train.getLocomotive() != null && train.getLocomotive().equals(locomotive)) {
                return train; // Found the train using this locomotive
            }
        }
        
        return null;
    }

    public boolean createTrain(Train train) {
        if (train == null || train.getLocomotive() == null || train.getNameID() == null) {
            return false;
        }

        // Check if the locomotive is already assigned to a train
        Train existingTrain = getTrainForLocomotive(train.getLocomotive());
        if (existingTrain != null) {
            return false; // Locomotive is already in use
        }

        // Save the new train
        return trainRepository.save(train);
    }
} 