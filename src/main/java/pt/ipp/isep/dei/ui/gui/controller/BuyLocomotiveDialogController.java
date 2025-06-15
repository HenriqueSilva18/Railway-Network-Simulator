package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.BuyLocomotiveController;
import pt.ipp.isep.dei.controller.template.UserSession;
import pt.ipp.isep.dei.domain.template.*;

import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class BuyLocomotiveDialogController implements Initializable {
    @FXML
    private ListView<Locomotive> availableLocomotivesList;
    @FXML
    private ListView<Locomotive> ownedLocomotivesList;
    @FXML
    private Label locomotiveDetailsLabel;
    @FXML
    private Label priceLabel;
    @FXML
    private Label currentBudgetLabel;
    @FXML
    private Label remainingBudgetLabel;
    @FXML
    private Label errorMessageLabel;
    @FXML
    private Button purchaseButton;
    @FXML
    private VBox locomotiveDetailsBox;
    @FXML
    private TextArea locomotiveDetailsTextArea;

    private final BuyLocomotiveController controller;
    private Locomotive selectedLocomotive;

    public BuyLocomotiveDialogController() {
        this.controller = new BuyLocomotiveController();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        setupLocomotiveLists();
        setupOwnedLocomotivesListener();
        setupListeners();
        updateBudgetLabels();
        loadLocomotives();
        clearError();
        locomotiveDetailsBox.setVisible(false);
    }

    private void setupLocomotiveLists() {
        // Setup available locomotives list
        availableLocomotivesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Locomotive locomotive, boolean empty) {
                super.updateItem(locomotive, empty);
                if (empty || locomotive == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - $%.2f",
                            locomotive.getNameID(), locomotive.getPrice()));
                }
            }
        });

        // Setup owned locomotives list
        ownedLocomotivesList.setCellFactory(param -> new ListCell<>() {
            @Override
            protected void updateItem(Locomotive locomotive, boolean empty) {
                super.updateItem(locomotive, empty);
                if (empty || locomotive == null) {
                    setText(null);
                } else {
                    // Get associated train name if exists
                    Train associatedTrain = controller.getTrainForLocomotive(locomotive);
                    String trainInfo = associatedTrain != null ?
                            " (Train: " + associatedTrain.getNameID() + ")" : "";
                    setText(locomotive.getNameID() + trainInfo);
                }
            }
        });
    }

    private void setupListeners() {
        // Listener para comboios disponíveis
        availableLocomotivesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLocomotive = newVal;
                updateAvailableLocomotiveDetails();
                locomotiveDetailsBox.setVisible(true);

                // Limpa seleção na lista de comboios comprados
                ownedLocomotivesList.getSelectionModel().clearSelection();
            } else {
                // Se não houver seleção, esconde ou limpa detalhes
                locomotiveDetailsBox.setVisible(false);
                locomotiveDetailsTextArea.clear();
            }
        });

// Listener para comboios já comprados
        ownedLocomotivesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLocomotive = newVal;
                updateOwnerLocomotiveDetails();
                locomotiveDetailsBox.setVisible(true);

                // Limpa seleção na lista de comboios disponíveis
                availableLocomotivesList.getSelectionModel().clearSelection();
            } else {
                locomotiveDetailsBox.setVisible(false);
                locomotiveDetailsTextArea.clear();
            }
        });
    }

    private void setupOwnedLocomotivesListener() {
        // Listener para comboios disponíveis
        availableLocomotivesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLocomotive = newVal;
                updateAvailableLocomotiveDetails();
                locomotiveDetailsBox.setVisible(true);

                // Limpa seleção na lista de comboios comprados
                ownedLocomotivesList.getSelectionModel().clearSelection();
            } else {
                // Se não houver seleção, esconde ou limpa detalhes
                locomotiveDetailsBox.setVisible(false);
                locomotiveDetailsTextArea.clear();
            }
        });

// Listener para comboios já comprados
        ownedLocomotivesList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLocomotive = newVal;
                updateOwnerLocomotiveDetails();
                locomotiveDetailsBox.setVisible(true);

                // Limpa seleção na lista de comboios disponíveis
                availableLocomotivesList.getSelectionModel().clearSelection();
            } else {
                locomotiveDetailsBox.setVisible(false);
                locomotiveDetailsTextArea.clear();
            }
        });
    }

    private void loadLocomotives() {
        // Load available locomotives
        List<Locomotive> availableLocomotives = controller.getAvailableLocomotives();
        
        // Group locomotives by type
        Map<String, List<Locomotive>> locomotivesByType = availableLocomotives.stream()
                .collect(Collectors.groupingBy(Locomotive::getType));
        
        // Sort types alphabetically
        List<String> sortedTypes = new ArrayList<>(locomotivesByType.keySet());
        Collections.sort(sortedTypes);
        
        // Create a new sorted list
        List<Locomotive> sortedLocomotives = new ArrayList<>();
        
        // Add locomotives grouped by type and sorted by name
        for (String type : sortedTypes) {
            List<Locomotive> typeLocomotives = locomotivesByType.get(type);
            typeLocomotives.sort(Comparator.comparing(Locomotive::getNameID));
            sortedLocomotives.addAll(typeLocomotives);
        }
        
        availableLocomotivesList.getItems().clear();
        availableLocomotivesList.getItems().addAll(sortedLocomotives);

        // Load owned locomotives
        List<Locomotive> ownedLocomotives = controller.getPlayerLocomotives();
        
        // Group owned locomotives by type
        Map<String, List<Locomotive>> ownedByType = ownedLocomotives.stream()
                .collect(Collectors.groupingBy(Locomotive::getType));
        
        // Sort types alphabetically
        List<String> sortedOwnedTypes = new ArrayList<>(ownedByType.keySet());
        Collections.sort(sortedOwnedTypes);
        
        // Create a new sorted list for owned locomotives
        List<Locomotive> sortedOwnedLocomotives = new ArrayList<>();
        
        // Add owned locomotives grouped by type and sorted by name
        for (String type : sortedOwnedTypes) {
            List<Locomotive> typeLocomotives = ownedByType.get(type);
            typeLocomotives.sort(Comparator.comparing(Locomotive::getNameID));
            sortedOwnedLocomotives.addAll(typeLocomotives);
        }
        
        ownedLocomotivesList.getItems().clear();
        ownedLocomotivesList.getItems().addAll(sortedOwnedLocomotives);
    }

    private void updateAvailableLocomotiveDetails() {
        if (selectedLocomotive != null) {
            StringBuilder details = new StringBuilder();
            details.append("Name: ").append(selectedLocomotive.getNameID()).append("\n");
            details.append("Type: ").append(selectedLocomotive.getType() != null ?
                    selectedLocomotive.getType() : "Standard").append("\n");
            if (selectedLocomotive.getPower() > 0) {
                details.append("Power: ").append(selectedLocomotive.getPower()).append(" HP\n");
            }
            details.append("Top Speed: ").append(selectedLocomotive.getTopSpeed()).append(" km/h\n");
            details.append("Fuel Cost: $").append(selectedLocomotive.getFuelCost()).append("\n");
            details.append("Maintenance Cost: $").append(selectedLocomotive.getMaintenancePrice()).append("\n");
            details.append("Availability Year: ").append(selectedLocomotive.getAvailabilityYear()).append("\n");


            // Usa o TextArea para mostrar detalhes
            locomotiveDetailsTextArea.setText(details.toString());

            // Atualiza o label de preço normalmente
            priceLabel.setText(String.format("Price: $%.2f", selectedLocomotive.getPrice()));

            updateBudgetLabels();
        }
    }

    private void updateOwnerLocomotiveDetails() {
        if (selectedLocomotive != null) {
            StringBuilder details = new StringBuilder();
            details.append("Name: ").append(selectedLocomotive.getNameID()).append("\n");
            details.append("Type: ").append(selectedLocomotive.getType() != null ?
                    selectedLocomotive.getType() : "Standard").append("\n");
            if (selectedLocomotive.getPower() > 0) {
                details.append("Power: ").append(selectedLocomotive.getPower()).append(" HP\n");
            }
            details.append("Top Speed: ").append(selectedLocomotive.getTopSpeed()).append(" km/h\n");
            details.append("Fuel Cost: $").append(selectedLocomotive.getFuelCost()).append("\n");
            details.append("Maintenance Cost: $").append(selectedLocomotive.getMaintenancePrice()).append("\n");
            details.append("Availability Year: ").append(selectedLocomotive.getAvailabilityYear()).append("\n");
            details.append("Owner: ").append(selectedLocomotive.getOwner() != "" ?
                    selectedLocomotive.getOwner() : "None").append("\n");


            // Usa o TextArea para mostrar detalhes
            locomotiveDetailsTextArea.setText(details.toString());

            // Atualiza o label de preço normalmente
            priceLabel.setText("");

            updateBudgetLabels();
        }
    }

    private void updateBudgetLabels() {
        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer != null) {
            double currentBudget = currentPlayer.getCurrentBudget();
            currentBudgetLabel.setText(String.format("Current Budget: $%.2f", currentBudget));

            if (selectedLocomotive != null) {
                double remainingBudget = currentBudget - selectedLocomotive.getPrice();
                remainingBudgetLabel.setText(String.format("Remaining Budget After Purchase: $%.2f", remainingBudget));

                // Enable/disable purchase button based on budget
                boolean canAfford = remainingBudget >= 0;
                purchaseButton.setDisable(!canAfford);

                if (!canAfford) {
                    showError("Insufficient funds to purchase this locomotive.");
                } else {
                    clearError();
                }
            }
        }
    }

    @FXML
    private void handlePurchase() {
        clearError();

        if (selectedLocomotive == null) {
            showError("Please select a locomotive to purchase.");
            return;
        }

        Player currentPlayer = ApplicationSession.getInstance().getCurrentPlayer();
        if (currentPlayer == null) {
            showError("No player session found.");
            return;
        }

        // Check if player can afford the locomotive
        if (currentPlayer.getCurrentBudget() < selectedLocomotive.getPrice()) {
            showError("Insufficient funds to purchase this locomotive.");
            return;
        }

        // Check if locomotive is still available
        List<Locomotive> availableLocomotives = controller.getAvailableLocomotives();
        boolean stillAvailable = availableLocomotives.stream()
                .anyMatch(loc -> loc.getNameID().equals(selectedLocomotive.getNameID()));

        if (!stillAvailable) {
            showError("This locomotive is no longer available.");
            loadLocomotives(); // Refresh lists
            return;
        }

        // Attempt to purchase the locomotive with detailed debugging
        boolean purchaseResult = purchaseLocomotive(selectedLocomotive);

        if (purchaseResult) {
            showAlert("Success",
                    String.format("Successfully purchased locomotive '%s' for $%.2f!",
                            selectedLocomotive.getNameID(), selectedLocomotive.getPrice()));

            // Force refresh with delay to ensure data is persisted
            javafx.application.Platform.runLater(() -> {
                try {
                    Thread.sleep(100); // Small delay to ensure persistence
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }

                // Refresh the lists and budget
                loadLocomotives();
                updateBudgetLabels();

                // Also try direct method to get owned locomotives
                List<Locomotive> updatedOwnedLocomotives = controller.getPlayerLocomotives();
            });

            // Clear selection
            availableLocomotivesList.getSelectionModel().clearSelection();
            selectedLocomotive = null;
            locomotiveDetailsBox.setVisible(false);

        } else {
            // More detailed error message
            String detailedError = "Failed to purchase locomotive.";
            showError(detailedError);
        }
    }

    @FXML
    private void handleRefresh() {
        clearError();
        loadLocomotives();
        updateBudgetLabels();
        showAlert("Refreshed", "Locomotive lists have been refreshed.");
    }

    @FXML
    private void handleClose() {
        closeDialog();
    }

    private void showError(String message) {
        errorMessageLabel.setText(message);
    }

    private void clearError() {
        errorMessageLabel.setText("");
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeDialog() {
        ((Stage) purchaseButton.getScene().getWindow()).close();
    }

    /**
     * Debug version of purchase locomotive with detailed logging
     */
    private boolean purchaseLocomotive(Locomotive locomotive) {
        if (locomotive == null) {
            return false;
        }

        // Get player from session
        Player player = ApplicationSession.getInstance().getCurrentPlayer();
        if (player == null) {
            return false;
        }

        // Get locomotive price
        double locomotivePrice = locomotive.getPrice();

        // Check if player has enough money
        double playerBudget = player.getCurrentBudget();

        if (playerBudget < locomotivePrice) {
            showAlert("Insufficient Funds",
                    String.format("You do not have enough funds to purchase this locomotive. " +
                            "Current budget: $%.2f, Locomotive price: $%.2f", playerBudget, locomotivePrice));
            return false;
        }

        // Complete the purchase
        try {
            boolean success = controller.purchaseLocomotive(locomotive);
            return success;
        } catch (Exception e) {
            e.printStackTrace();
            showError("An error occurred while purchasing the locomotive: " + e.getMessage());
            return false;
        }
    }
}