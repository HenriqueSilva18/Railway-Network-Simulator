package pt.ipp.isep.dei.ui.gui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.value.ChangeListener;
import pt.ipp.isep.dei.controller.template.ApplicationSession;
import pt.ipp.isep.dei.controller.template.UpgradeStationController;
import pt.ipp.isep.dei.domain.template.Building;
import pt.ipp.isep.dei.domain.template.Station;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class UpgradeStationDialogController implements Initializable {
    @FXML
    private Label stationNameLabel;
    @FXML
    private Label stationTypeLabel;
    @FXML
    private Label stationPositionLabel;
    @FXML
    private Label storageCapacityLabel;
    @FXML
    private Label buildingSlotsLabel;
    
    @FXML
    private ListView<String> currentBuildingsListView;
    
    @FXML
    private RadioButton installNewBuildingRadio;
    @FXML
    private RadioButton evolveExistingBuildingRadio;
    
    @FXML
    private ListView<String> availableOptionsListView;
    
    @FXML
    private Label buildingNameLabel;
    @FXML
    private Label buildingTypeLabel;
    @FXML
    private Label buildingCostLabel;
    @FXML
    private Label buildingEffectLabel;
    @FXML
    private Label buildingEvolutionLabel;
    
    @FXML
    private Button upgradeButton;
    
    private final UpgradeStationController controller;
    private ToggleGroup upgradeType;
    
    public UpgradeStationDialogController() {
        this.controller = new UpgradeStationController();
        this.upgradeType = new ToggleGroup();
    }
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize toggle group
        installNewBuildingRadio.setToggleGroup(upgradeType);
        evolveExistingBuildingRadio.setToggleGroup(upgradeType);
        
        // Setup listeners
        setupListeners();
        
        // Load initial data
        loadStationInfo();
        loadCurrentBuildings();
        updateAvailableOptions();
    }
    
    private void setupListeners() {
        // Listen for upgrade type changes
        upgradeType.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateAvailableOptions();
        });
        
        // Listen for selection changes in available options
        availableOptionsListView.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> {
                if (newValue != null) {
                    updateBuildingDetails(newValue);
                    upgradeButton.setDisable(false);
                } else {
                    clearBuildingDetails();
                    upgradeButton.setDisable(true);
                }
            }
        );
    }
    
    private void loadStationInfo() {
        Station.StationInfo info = controller.getStationInfo();
        if (info != null) {
            stationNameLabel.setText(info.getNameID());
            stationTypeLabel.setText(info.getType());
            stationPositionLabel.setText(String.format("(%d, %d)", info.getPosX(), info.getPosY()));
            storageCapacityLabel.setText(String.valueOf(info.getStorageCapacity()));
            buildingSlotsLabel.setText(String.format("%d/%d", info.getUsedBuildingSlots(), info.getTotalBuildingSlots()));
        }
    }
    
    private void loadCurrentBuildings() {
        List<Building.BuildingInfo> currentBuildings = controller.getCurrentStationBuildings();
        ObservableList<String> items = FXCollections.observableArrayList();
        
        for (Building.BuildingInfo building : currentBuildings) {
            String displayText = String.format("%s (Type: %s)", building.getNameID(), building.getType());
            items.add(displayText);
        }
        
        currentBuildingsListView.setItems(items);
    }
    
    private void updateAvailableOptions() {
        ObservableList<String> items = FXCollections.observableArrayList();
        
        if (installNewBuildingRadio.isSelected()) {
            // Show available new buildings
            List<Building> availableBuildings = controller.getAvailableNewBuildings();
            for (Building building : availableBuildings) {
                items.add(building.getNameID());
            }
        } else {
            // Show evolvable buildings
            List<Building.BuildingInfo> evolvableBuildings = controller.getEvolvableBuildings();
            for (Building.BuildingInfo building : evolvableBuildings) {
                items.add(building.getNameID());
            }
        }
        
        availableOptionsListView.setItems(items);
        clearBuildingDetails();
        upgradeButton.setDisable(true);
    }
    
    private void updateBuildingDetails(String buildingName) {
        Building.BuildingInfo info;
        
        if (installNewBuildingRadio.isSelected()) {
            info = controller.getBuildingInfo(buildingName);
            if (info != null) {
                buildingNameLabel.setText(info.getNameID());
                buildingTypeLabel.setText("Type: " + info.getType());
                buildingCostLabel.setText(String.format("Cost: $%.2f", info.getCost()));
                buildingEffectLabel.setText("Effect: " + info.getEffect());
                buildingEvolutionLabel.setText(info.canEvolve() ? 
                    String.format("Can evolve to: %s (Cost: $%.2f)", info.getEvolvesInto(), info.getEvolutionCost()) : 
                    "No evolution available");
            }
        } else {
            // Handle evolution details
            List<Building.BuildingInfo> evolvableBuildings = controller.getEvolvableBuildings();
            for (Building.BuildingInfo building : evolvableBuildings) {
                if (building.getNameID().equals(buildingName)) {
                    buildingNameLabel.setText(building.getNameID());
                    buildingTypeLabel.setText("Type: " + building.getType());
                    buildingCostLabel.setText(String.format("Evolution Cost: $%.2f", building.getEvolutionCost()));
                    buildingEffectLabel.setText("Current Effect: " + building.getEffect());
                    buildingEvolutionLabel.setText("Evolves to: " + building.getEvolvesInto());
                    break;
                }
            }
        }
    }
    
    private void clearBuildingDetails() {
        buildingNameLabel.setText("");
        buildingTypeLabel.setText("");
        buildingCostLabel.setText("");
        buildingEffectLabel.setText("");
        buildingEvolutionLabel.setText("");
    }
    
    @FXML
    private void handleUpgrade() {
        String selectedBuilding = availableOptionsListView.getSelectionModel().getSelectedItem();
        if (selectedBuilding == null) return;
        
        boolean success;
        if (installNewBuildingRadio.isSelected()) {
            success = controller.installNewBuilding(selectedBuilding);
        } else {
            List<Building> evolutionOptions = controller.getEvolutionOptions(selectedBuilding);
            if (evolutionOptions.isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Error", "No evolution options available.");
                return;
            }
            success = controller.evolveBuilding(selectedBuilding, evolutionOptions.get(0).getNameID());
        }
        
        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "Success", 
                installNewBuildingRadio.isSelected() ? 
                "Building successfully installed!" : 
                "Building successfully evolved!");
            
            // Update displays
            loadStationInfo();
            loadCurrentBuildings();
            updateAvailableOptions();
            
            // Update the player's budget display in the main window
            if (getStage().getOwner() != null && getStage().getOwner().getScene().getRoot() instanceof BorderPane) {
                BorderPane mainPane = (BorderPane) getStage().getOwner().getScene().getRoot();
                if (mainPane.getTop() instanceof Label) {
                    Label budgetLabel = (Label) mainPane.getTop();
                    budgetLabel.setText(String.format("$%.2f", ApplicationSession.getInstance().getCurrentPlayer().getCurrentBudget()));
                }
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Error", 
                "Failed to " + (installNewBuildingRadio.isSelected() ? "install" : "evolve") + 
                " building. You may not have enough budget or the building is not compatible.");
        }
    }
    
    @FXML
    private void handleCancel() {
        getStage().close();
    }
    
    private Stage getStage() {
        return (Stage) upgradeButton.getScene().getWindow();
    }
    
    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 