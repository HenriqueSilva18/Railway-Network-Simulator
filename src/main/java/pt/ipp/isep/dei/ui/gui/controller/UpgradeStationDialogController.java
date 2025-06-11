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
    
    @FXML
    private Button removeBuildingButton;
    
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
        
        // Add selection listener to enable/disable remove button
        currentBuildingsListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> removeBuildingButton.setDisable(newVal == null)
        );
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
                Building building = controller.getBuilding(buildingName);
                buildingNameLabel.setText(info.getNameID());
                buildingTypeLabel.setText("Type: " + info.getType());
                buildingCostLabel.setText(String.format("Cost: $%.2f", info.getCost()));
                buildingEffectLabel.setText("Effect: " + info.getEffect());
                
                // Show compatibility information
                StringBuilder compatibilityInfo = new StringBuilder();
                Station currentStation = ApplicationSession.getInstance().getCurrentStation();
                
                if (currentStation != null) {
                    // Check for same type buildings
                    boolean hasSameType = currentStation.getBuildings().stream()
                        .anyMatch(b -> b.getType().equals(building.getType()));
                    if (hasSameType) {
                        compatibilityInfo.append("Cannot install: Station already has a building of this type.\n");
                    }
                    
                    // Check for mutually exclusive buildings
                    for (Building existingBuilding : currentStation.getBuildings()) {
                        if (existingBuilding.isMutuallyExclusive() && 
                            existingBuilding.getMutuallyExclusiveWith() != null &&
                            existingBuilding.getMutuallyExclusiveWith().equals(building.getNameID())) {
                            compatibilityInfo.append("Cannot install: Mutually exclusive with " + 
                                existingBuilding.getNameID() + ".\n");
                        }
                        if (building.isMutuallyExclusive() && 
                            building.getMutuallyExclusiveWith() != null &&
                            building.getMutuallyExclusiveWith().equals(existingBuilding.getNameID())) {
                            compatibilityInfo.append("Cannot install: Mutually exclusive with " + 
                                existingBuilding.getNameID() + ".\n");
                        }
                    }
                    
                    // Check for replacement buildings
                    if (building.getReplacesBuilding() != null) {
                        boolean hasBuildingToReplace = currentStation.getBuildings().stream()
                            .anyMatch(b -> b.getNameID().equals(building.getReplacesBuilding()));
                        if (!hasBuildingToReplace) {
                            compatibilityInfo.append("Cannot install: Requires " + 
                                building.getReplacesBuilding() + " to be present first.\n");
                        }
                    }
                }
                
                // Show evolution information if available
                if (info.canEvolve()) {
                    compatibilityInfo.append("\nCan evolve to: " + info.getEvolvesInto() + 
                        String.format(" (Cost: $%.2f)", info.getEvolutionCost()));
                }
                
                buildingEvolutionLabel.setText(compatibilityInfo.toString());
                
                // Disable upgrade button if there are compatibility issues
                upgradeButton.setDisable(!compatibilityInfo.toString().isEmpty() && 
                    !compatibilityInfo.toString().contains("Can evolve to:"));
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
        String errorMessage = "";
        
        if (installNewBuildingRadio.isSelected()) {
            Building building = controller.getBuilding(selectedBuilding);
            if (building != null) {
                Station currentStation = ApplicationSession.getInstance().getCurrentStation();
                if (currentStation != null && !currentStation.canInstallBuilding(building)) {
                    // Get specific reason why building can't be installed
                    if (currentStation.getBuildings().stream().anyMatch(b -> b.getType().equals(building.getType()))) {
                        errorMessage = "Cannot install: Station already has a building of this type.";
                    } else if (building.getReplacesBuilding() != null && 
                             !currentStation.getBuildings().stream().anyMatch(b -> 
                                 b.getNameID().equals(building.getReplacesBuilding()))) {
                        errorMessage = "Cannot install: Requires " + building.getReplacesBuilding() + " to be present first.";
                    } else {
                        errorMessage = "Cannot install: Building is not compatible with existing buildings.";
                    }
                }
            }
            
            if (errorMessage.isEmpty()) {
                success = controller.installNewBuilding(selectedBuilding);
            } else {
                success = false;
            }
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
                errorMessage.isEmpty() ? 
                "Failed to " + (installNewBuildingRadio.isSelected() ? "install" : "evolve") + 
                " building. You may not have enough budget." :
                errorMessage);
        }
    }
    
    @FXML
    private void handleRemoveBuilding() {
        String selectedBuilding = currentBuildingsListView.getSelectionModel().getSelectedItem();
        if (selectedBuilding == null) return;
        
        // Extract building name from the display text (format: "name (Type: type)")
        String buildingName = selectedBuilding.split(" ")[0];
        
        // Confirm removal
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Building Removal");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Are you sure you want to remove this building?");
        
        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            // Remove the building
            boolean success = controller.removeBuilding(buildingName);
            
            if (success) {
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
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to remove building.");
            }
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