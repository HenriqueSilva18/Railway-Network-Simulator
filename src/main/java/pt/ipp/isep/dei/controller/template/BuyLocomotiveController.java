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

public class BuyLocomotiveController {
    private final LocomotiveRepository locomotiveRepository;
    private final PlayerRepository playerRepository;
    private final ScenarioRepository scenarioRepository;
    private final TrainRepository trainRepository;

    public BuyLocomotiveController() {
        Repositories repositories = Repositories.getInstance();
        this.locomotiveRepository = repositories.getLocomotiveRepository();
        this.playerRepository = repositories.getPlayerRepository();
        this.scenarioRepository = repositories.getScenarioRepository();
        this.trainRepository = repositories.getTrainRepository();
    }

    public List<Locomotive> getAvailableLocomotives() {
        // Get current scenario and date
        Scenario currentScenario = getCurrentScenario();
        Date currentDate = getCurrentDate();

        if (currentScenario == null || currentDate == null) {
            return List.of(); // Empty list if no scenario or date
        }

        // Get available locomotives for the current scenario and date
        return currentScenario.getAvailableLocomotives(currentDate);
    }

    public List<Locomotive> getPlayerLocomotives() {
        Player player = getPlayerFromSession();

        if (player == null) {
            return new ArrayList<>();
        }

        List<Locomotive> owned = player.getOwnedLocomotives();

        for (Locomotive loc : owned) {
        }

        return owned;
    }


    public Locomotive getLocomotive(String locomotiveID) {
        return locomotiveRepository.getLocomotive(locomotiveID);
    }

    public boolean purchaseLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
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

        // Create a train with this locomotive
        String trainName = "Train_" + locomotive.getNameID() + "_" + System.currentTimeMillis() % 1000;
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
            if (train.getLocomotive().equals(locomotive) || 
                train.getLocomotive().getNameID().equals(locomotive.getNameID())) {
                return train;
            }
        }
        
        return null;
    }
} 